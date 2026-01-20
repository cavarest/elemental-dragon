package org.cavarest.elementaldragon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Item;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Listener for fragment item restrictions:
 * 1. Enforces one-fragment limit (Issue #20)
 * 2. Prevents storage in containers (Issue #20)
 * 3. Detects when fragments leave player inventory (drop, despawn) and unequips them
 * 4. Unequips on inventory drag/sort operations (Issue #20)
 * 5. Protects fragments from destruction (fire, lava, cactus, etc.)
 */
public class FragmentItemListener implements Listener {

  private final ElementalDragon plugin;
  private final FragmentManager fragmentManager;
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  public FragmentItemListener(ElementalDragon plugin, FragmentManager fragmentManager) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
  }

  // ===== Item Loss Detection =====

  /**
   * Detect when a player drops a fragment and unequip it ONLY if it's the currently equipped fragment.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    ItemStack droppedItem = event.getItemDrop().getItemStack();
    FragmentType droppedFragmentType = getFragmentType(droppedItem);

    if (droppedFragmentType != null) {
      Player player = event.getPlayer();

      // Only unequip if the dropped fragment matches the currently equipped one
      FragmentType equippedFragmentType = fragmentManager.getEquippedFragment(player);
      if (equippedFragmentType == droppedFragmentType) {
        fragmentManager.unequipFragment(player);
        player.sendMessage(miniMessage.deserialize(
          "<red>Your " + droppedFragmentType.getDisplayName() + " abilities have been withdrawn!</red>"
        ));
      }
    }
  }

  /**
   * Enforce one-fragment limit - prevent pickup of additional fragments (Issue #20).
   * Players can only carry one fragment type at a time.
   * This check considers: inventory, equipped fragment, AND cursor (item being held).
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerPickupFragment(PlayerPickupItemEvent event) {
    ItemStack pickedUpItem = event.getItem().getItemStack();
    FragmentType pickedUpFragmentType = getFragmentType(pickedUpItem);

    if (pickedUpFragmentType == null) {
      return; // Not a fragment
    }

    Player player = event.getPlayer();

    // Check if player already has a fragment in their inventory
    FragmentType existingFragmentType = null;
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null) {
        FragmentType fragmentType = getFragmentType(item);
        if (fragmentType != null) {
          existingFragmentType = fragmentType;
          break;
        }
      }
    }

    // Also check if player has a different fragment EQUIPPED (even if not in inventory)
    FragmentType equippedFragmentType = fragmentManager.getEquippedFragment(player);
    if (equippedFragmentType != null && equippedFragmentType != pickedUpFragmentType) {
      // Player has a different fragment equipped - prevent pickup
      existingFragmentType = equippedFragmentType;
    }

    // IMPORTANT: Also check if player is HOLDING a fragment on their cursor
    // This prevents pickup when clicking on an equipped fragment (which unequips it,
    // then the item goes to cursor, then pickup sees no fragment equipped)
    ItemStack cursorItem = player.getItemOnCursor();
    FragmentType cursorFragmentType = getFragmentType(cursorItem);
    if (cursorFragmentType != null && cursorFragmentType != pickedUpFragmentType) {
      // Player is holding a different fragment on cursor - prevent pickup
      existingFragmentType = cursorFragmentType;
    }

    // If player already has a fragment (any type), prevent pickup of another
    if (existingFragmentType != null && existingFragmentType != pickedUpFragmentType) {
      event.setCancelled(true);
      player.sendMessage(miniMessage.deserialize(
        "<red>⚠ You can only carry one fragment at a time!</red>\n" +
        "<gray>You already have the <white>" + existingFragmentType.getDisplayName() +
        "</white> equipped.</gray>"
      ));
    }
  }

  /**
   * Prevent storage of fragments in containers (chests, hoppers, etc.) - Issue #20.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInventoryClickFragment(InventoryClickEvent event) {
    // Check both the clicked slot AND the cursor (item being held)
    ItemStack clickedItem = event.getCurrentItem();
    ItemStack cursorItem = event.getCursor();

    // Determine if we're dealing with a fragment in either slot or cursor
    FragmentType fragmentTypeInSlot = getFragmentType(clickedItem);
    FragmentType fragmentTypeOnCursor = getFragmentType(cursorItem);
    FragmentType clickedFragmentType = fragmentTypeInSlot != null ? fragmentTypeInSlot : fragmentTypeOnCursor;

    // If neither the clicked slot nor the cursor has a fragment, nothing to do
    if (clickedFragmentType == null) {
      return;
    }

    Player player = (Player) event.getWhoClicked();

    // Check if this fragment is currently equipped - if so, unequip it first
    // This handles the case where player tries to move equipped fragment to a container
    FragmentType equippedFragmentType = fragmentManager.getEquippedFragment(player);
    if (equippedFragmentType != null && equippedFragmentType == clickedFragmentType) {
      fragmentManager.unequipFragment(player, true); // silent unequip
    }

    // Check if the target inventory is a container (not player inventory)
    InventoryType clickedInventoryType = event.getClickedInventory() != null
      ? event.getClickedInventory().getType()
      : null;

    // Prevent moving fragments into containers
    if (clickedInventoryType != null && clickedInventoryType != InventoryType.PLAYER) {
      event.setCancelled(true);
      event.getWhoClicked().sendMessage(miniMessage.deserialize(
        "<red>⚠ Fragments cannot be stored in containers!</red>\n" +
        "<gray>They must remain in your personal inventory.</gray>"
      ));
      return;
    }

    // Also check if the action is moving to a different inventory (like shift-clicking)
    InventoryAction action = event.getAction();
    if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
      // Check if there's a top inventory (container) and we're in the player inventory
      if (event.getView().getTopInventory() != null &&
          event.getView().getTopInventory().getType() != InventoryType.PLAYER) {
        event.setCancelled(true);
        event.getWhoClicked().sendMessage(miniMessage.deserialize(
          "<red>⚠ Fragments cannot be stored in containers!</red>\n" +
          "<gray>They must remain in your personal inventory.</gray>"
        ));
      }
    }

    // Check if placing cursor item into container (PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PLACE)
    if (fragmentTypeOnCursor != null) {
      // These actions mean the player is trying to place the cursor item somewhere
      if (action == InventoryAction.PLACE_ALL ||
          action == InventoryAction.PLACE_SOME ||
          action == InventoryAction.PLACE_ONE) {
        // Check if clicking into a container's inventory
        if (event.getClickedInventory() != null &&
            event.getClickedInventory().getType() != InventoryType.PLAYER) {
          event.setCancelled(true);
          event.getWhoClicked().sendMessage(miniMessage.deserialize(
            "<red>⚠ Fragments cannot be stored in containers!</red>\n" +
            "<gray>They must remain in your personal inventory.</gray>"
          ));
        }
      }
    }
  }

  /**
   * Prevent storage of fragments in containers via drag operation - Issue #20.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInventoryDragFragment(InventoryDragEvent event) {
    // Check if any of the dragged items are fragments
    for (ItemStack item : event.getNewItems().values()) {
      if (item != null && isFragmentItem(item)) {
        Player player = (Player) event.getWhoClicked();
        FragmentType draggedFragmentType = getFragmentType(item);

        // Check if this fragment is currently equipped - if so, unequip it first
        // This handles the case where player tries to drag equipped fragment to a container
        FragmentType equippedFragmentType = fragmentManager.getEquippedFragment(player);
        if (equippedFragmentType != null && equippedFragmentType == draggedFragmentType) {
          fragmentManager.unequipFragment(player, true); // silent unequip
        }

        // Check if drag targets a container inventory
        InventoryType topInventoryType = event.getView().getTopInventory() != null
          ? event.getView().getTopInventory().getType()
          : null;

        // If dragging into a container (top inventory exists and is not player inventory)
        if (topInventoryType != null && topInventoryType != InventoryType.PLAYER) {
          // Check if any slot is in the container
          for (Integer slot : event.getInventorySlots()) {
            if (slot >= event.getView().getTopInventory().getSize()) {
              continue; // This slot is in player inventory
            }
            // This slot is in the container - cancel
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(miniMessage.deserialize(
              "<red>⚠ Fragments cannot be stored in containers!</red>\n" +
              "<gray>They must remain in your personal inventory.</gray>"
            ));
            return;
          }
        }
      }
    }
  }

  /**
   * Detect inventory drag/sort operations and unequip if equipped fragment is moved - Issue #20.
   * This handles cases where players drag/sort their inventory, which should unequip the fragment.
   */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryClickUnequip(InventoryClickEvent event) {
    // Only care about clicks in player inventory
    if (event.getClickedInventory() == null ||
        event.getClickedInventory().getType() != InventoryType.PLAYER) {
      return;
    }

    ItemStack clickedItem = event.getCurrentItem();
    if (clickedItem == null || !isFragmentItem(clickedItem)) {
      return;
    }

    Player player = (Player) event.getWhoClicked();
    FragmentType clickedFragmentType = getFragmentType(clickedItem);

    // Check if this fragment is currently equipped
    FragmentType equippedFragmentType = fragmentManager.getEquippedFragment(player);
    if (equippedFragmentType == clickedFragmentType) {
      // Fragment is equipped - unequip it when it's moved in inventory
      // This prevents players from having abilities active while moving the item
      fragmentManager.unequipFragment(player, true); // silent unequip
      // We don't show a message here since the user is just organizing their inventory
    }
  }

  /**
   * Detect when a fragment despawns and unequip if the owner had it equipped.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onItemDespawn(ItemDespawnEvent event) {
    ItemStack despawnedItem = event.getEntity().getItemStack();
    FragmentType fragmentType = getFragmentType(despawnedItem);

    if (fragmentType != null) {
      // Find the owner and unequip
      // Note: We can't easily track who owned the item, so we just log it
      plugin.getLogger().info("A " + fragmentType.getDisplayName() + " has despawned");
    }
  }

  // ===== Fragment Protection =====

  /**
   * Protect fragments from destruction by fire, lava, cactus, and other damage sources.
   * Fragments are indestructible when dropped in the world.
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onItemDamage(EntityDamageEvent event) {
    // Only check dropped items
    if (!(event.getEntity() instanceof Item itemEntity)) {
      return;
    }

    ItemStack item = itemEntity.getItemStack();
    FragmentType fragmentType = getFragmentType(item);

    if (fragmentType != null) {
      // Cancel any damage to fragment items (fire, lava, cactus, etc.)
      event.setCancelled(true);
    }
  }

  /**
   * Check if an ItemStack is a fragment item.
   */
  private boolean isFragmentItem(ItemStack item) {
    return ElementalItems.getFragmentType(item) != null;
  }

  /**
   * Get the fragment type from an item.
   * Delegates to ElementalItems.getFragmentType() - Single Source of Truth.
   */
  private FragmentType getFragmentType(ItemStack item) {
    return ElementalItems.getFragmentType(item);
  }
}
