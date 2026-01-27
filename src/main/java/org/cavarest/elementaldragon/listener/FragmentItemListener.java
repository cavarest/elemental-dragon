package org.cavarest.elementaldragon.listener;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Unified listener for all fragment item interactions.
 *
 * <p>Handles:</p>
 * <ul>
 *   <li>Right-click to equip fragment</li>
 *   <li>One-fragment limit enforcement (can only carry one fragment type at a time)</li>
 *   <li>Duplicate fragment pickup prevention</li>
 *   <li>Container storage restrictions</li>
 *   <li>Automatic unequip on drop</li>
 *   <li>Fragment protection (indestructible when dropped)</li>
 * </ul>
 */
public class FragmentItemListener implements Listener {

  private final ElementalDragon plugin;
  private final FragmentManager fragmentManager;
  private final Map<UUID, Long> lastEquipClickTimes;

  // Cooldown between equip attempts (500ms) to prevent spam clicking
  private static final long EQUIP_COOLDOWN_MS = 500;

  public FragmentItemListener(ElementalDragon plugin, FragmentManager fragmentManager) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
    this.lastEquipClickTimes = new HashMap<>();
  }

  // ===== Right-Click Equip =====

  /**
   * Handle fragment right-click to equip.
   * Uses HIGHEST priority to run before item's default behaviors (like FIRE_CHARGE throwing fire).
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerInteract(PlayerInteractEvent event) {
    // Only handle right-click actions
    if (event.getAction() != Action.RIGHT_CLICK_AIR &&
        event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();

    // Check cooldown to prevent spam clicking
    long lastClickTime = lastEquipClickTimes.getOrDefault(playerId, 0L);
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastClickTime < EQUIP_COOLDOWN_MS) {
      // Still in cooldown, just cancel the event but don't send any message
      event.setCancelled(true);
      return;
    }

    // Get item from hand (main hand or offhand)
    ItemStack item = getItemInHand(player);
    if (item == null || !item.hasItemMeta()) {
      return;
    }

    // Check if item is a fragment
    FragmentType fragmentType = getFragmentType(item);
    if (fragmentType == null) {
      return;
    }

    // Cancel the event to prevent default item behaviors (FIRE_CHARGE throwing fire, HEAVY_CORE placement)
    event.setCancelled(true);

    // Update last click time
    lastEquipClickTimes.put(playerId, currentTime);

    // Equip the fragment
    boolean success = fragmentManager.equipFragment(player, fragmentType);

    if (success) {
      // Check if already equipped message was shown by FragmentManager
      FragmentType currentlyEquipped = fragmentManager.getEquippedFragment(player);
      if (currentlyEquipped == fragmentType) {
        // FragmentManager handles "Already equipped!" message internally
        // No additional action needed
      } else {
        player.sendMessage(net.kyori.adventure.text.Component.text()
            .append(net.kyori.adventure.text.Component.text("Equipped ", net.kyori.adventure.text.format.NamedTextColor.GOLD))
            .append(net.kyori.adventure.text.Component.text(fragmentType.getDisplayName(), net.kyori.adventure.text.format.NamedTextColor.WHITE))
            .append(net.kyori.adventure.text.Component.text("!", net.kyori.adventure.text.format.NamedTextColor.GOLD))
            .append(net.kyori.adventure.text.Component.newline())
            .append(net.kyori.adventure.text.Component.text(fragmentType.getPassiveBonus(), net.kyori.adventure.text.format.NamedTextColor.GRAY))
            .build()
        );
      }
    }
  }

  /**
   * Get the item in the player's hand (checks both main hand and offhand).
   */
  private ItemStack getItemInHand(Player player) {
    // Check main hand first
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    if (mainHand != null && mainHand.hasItemMeta()) {
      return mainHand;
    }
    // Then check offhand
    return player.getInventory().getItemInOffHand();
  }

  // ===== Pickup Restriction =====

  /**
   * Enforce one-fragment limit - prevent pickup of ANY fragment when player already has one.
   * Players can only carry ONE fragment at a time (regardless of type).
   * Uses modern EntityPickupItemEvent instead of deprecated PlayerPickupItemEvent.
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityPickupItem(EntityPickupItemEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();
    ItemStack pickupItem = event.getItem().getItemStack();

    // Check if the item being picked up is a fragment
    FragmentType pickupType = getFragmentType(pickupItem);
    if (pickupType == null) {
      return;
    }

    // Check if player has ANY fragment in their inventory (main + offhand + cursor)
    // Use DRY helper from ElementalItems
    FragmentType existingFragment = ElementalItems.getAnyFragmentExcept(player, null);

    if (existingFragment != null) {
      // Player already has a fragment - cancel pickup
      event.setCancelled(true);

      // Different message depending on whether it's the same or different fragment
      if (existingFragment == pickupType) {
        player.sendMessage(net.kyori.adventure.text.Component.text()
            .append(net.kyori.adventure.text.Component.text("⚠ You can only possess ONE ", net.kyori.adventure.text.format.NamedTextColor.RED))
            .append(net.kyori.adventure.text.Component.text(pickupType.getDisplayName(), net.kyori.adventure.text.format.NamedTextColor.WHITE))
            .append(net.kyori.adventure.text.Component.text(" at a time!", net.kyori.adventure.text.format.NamedTextColor.RED))
            .append(net.kyori.adventure.text.Component.newline())
            .append(net.kyori.adventure.text.Component.text("Drop your existing ", net.kyori.adventure.text.format.NamedTextColor.GRAY))
            .append(net.kyori.adventure.text.Component.text(pickupType.getDisplayName(), net.kyori.adventure.text.format.NamedTextColor.WHITE))
            .append(net.kyori.adventure.text.Component.text(" before picking up another.", net.kyori.adventure.text.format.NamedTextColor.GRAY))
            .build()
        );
      } else {
        player.sendMessage(net.kyori.adventure.text.Component.text()
            .append(net.kyori.adventure.text.Component.text("⚠ You can only carry ONE fragment at a time!", net.kyori.adventure.text.format.NamedTextColor.RED))
            .append(net.kyori.adventure.text.Component.newline())
            .append(net.kyori.adventure.text.Component.text("You already have: ", net.kyori.adventure.text.format.NamedTextColor.GRAY))
            .append(net.kyori.adventure.text.Component.text(existingFragment.getDisplayName(), net.kyori.adventure.text.format.NamedTextColor.WHITE))
            .append(net.kyori.adventure.text.Component.text(". Drop it before picking up: ", net.kyori.adventure.text.format.NamedTextColor.GRAY))
            .append(net.kyori.adventure.text.Component.text(pickupType.getDisplayName(), net.kyori.adventure.text.format.NamedTextColor.WHITE))
            .append(net.kyori.adventure.text.Component.text(".", net.kyori.adventure.text.format.NamedTextColor.GRAY))
            .build()
        );
      }
    }
  }

  /**
   * Count how many items of a specific fragment type exist in an inventory.
   */
  private int countFragmentType(ItemStack[] contents, FragmentType targetType) {
    int count = 0;
    for (ItemStack item : contents) {
      FragmentType type = getFragmentType(item);
      if (type == targetType) {
        count += item.getAmount();
      }
    }
    return count;
  }

  // ===== Drop Detection =====

  /**
   * Detect when a player drops a fragment and unequip it ONLY if it's the currently equipped fragment.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    ItemStack droppedItem = event.getItemDrop().getItemStack();

    // Check if the dropped item is a fragment
    FragmentType droppedType = getFragmentType(droppedItem);
    if (droppedType == null) {
      return;
    }

    // Check if this fragment type is currently equipped
    FragmentType equippedType = fragmentManager.getEquippedFragment(player);
    if (equippedType == null) {
      return;
    }

    // If the dropped fragment matches the equipped fragment, unequip it
    if (droppedType == equippedType) {
      fragmentManager.unequipFragment(player);

      // Inform the player that abilities have been withdrawn
      player.sendMessage(net.kyori.adventure.text.Component.text()
          .append(net.kyori.adventure.text.Component.text("Your ", net.kyori.adventure.text.format.NamedTextColor.YELLOW))
          .append(net.kyori.adventure.text.Component.text(droppedType.getDisplayName(), net.kyori.adventure.text.format.NamedTextColor.WHITE))
          .append(net.kyori.adventure.text.Component.text(" abilities have been withdrawn.", net.kyori.adventure.text.format.NamedTextColor.YELLOW))
          .append(net.kyori.adventure.text.Component.newline())
          .append(net.kyori.adventure.text.Component.text("The fragment item remains on the ground. Pick it up and re-equip to reactivate.", net.kyori.adventure.text.format.NamedTextColor.GRAY))
          .build()
      );
    }
  }

  // ===== Container Restrictions =====

  /**
   * Prevent storage of fragments in containers (chests, hoppers, etc.).
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

    // Check if the target inventory is a container (not player inventory)
    InventoryType clickedInventoryType = event.getClickedInventory() != null
        ? event.getClickedInventory().getType()
        : null;

    // Prevent moving fragments into containers
    if (clickedInventoryType != null && clickedInventoryType != InventoryType.PLAYER) {
      event.setCancelled(true);
      event.getWhoClicked().sendMessage(net.kyori.adventure.text.Component.text()
          .append(net.kyori.adventure.text.Component.text("⚠ Fragments cannot be stored in containers!", net.kyori.adventure.text.format.NamedTextColor.RED))
          .append(net.kyori.adventure.text.Component.newline())
          .append(net.kyori.adventure.text.Component.text("They must remain in your personal inventory.", net.kyori.adventure.text.format.NamedTextColor.GRAY))
          .build()
      );
      return;
    }

    // Also check if the action is moving to a different inventory (like shift-clicking)
    InventoryAction action = event.getAction();
    if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
      // Check if there's a top inventory (container) and we're in the player inventory
      if (event.getView().getTopInventory() != null &&
          event.getView().getTopInventory().getType() != InventoryType.PLAYER) {
        event.setCancelled(true);
        event.getWhoClicked().sendMessage(net.kyori.adventure.text.Component.text()
            .append(net.kyori.adventure.text.Component.text("⚠ Fragments cannot be stored in containers!", net.kyori.adventure.text.format.NamedTextColor.RED))
            .append(net.kyori.adventure.text.Component.newline())
            .append(net.kyori.adventure.text.Component.text("They must remain in your personal inventory.", net.kyori.adventure.text.format.NamedTextColor.GRAY))
            .build()
        );
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
          event.getWhoClicked().sendMessage(net.kyori.adventure.text.Component.text()
              .append(net.kyori.adventure.text.Component.text("⚠ Fragments cannot be stored in containers!", net.kyori.adventure.text.format.NamedTextColor.RED))
              .append(net.kyori.adventure.text.Component.newline())
              .append(net.kyori.adventure.text.Component.text("They must remain in your personal inventory.", net.kyori.adventure.text.format.NamedTextColor.GRAY))
              .build()
          );
        }
      }
    }
  }

  /**
   * Prevent storage of fragments in containers via drag operation.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInventoryDragFragment(InventoryDragEvent event) {
    // Check if any of the dragged items are fragments
    for (ItemStack item : event.getNewItems().values()) {
      if (item != null && isFragmentItem(item)) {
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
            event.getWhoClicked().sendMessage(net.kyori.adventure.text.Component.text()
                .append(net.kyori.adventure.text.Component.text("⚠ Fragments cannot be stored in containers!", net.kyori.adventure.text.format.NamedTextColor.RED))
                .append(net.kyori.adventure.text.Component.newline())
                .append(net.kyori.adventure.text.Component.text("They must remain in your personal inventory.", net.kyori.adventure.text.format.NamedTextColor.GRAY))
                .build()
            );
            return;
          }
        }
      }
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
   * Detect when a fragment despawns and log it.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onItemDespawn(ItemDespawnEvent event) {
    ItemStack despawnedItem = event.getEntity().getItemStack();
    FragmentType fragmentType = getFragmentType(despawnedItem);

    if (fragmentType != null) {
      // Log the despawn (we can't easily track who owned the item)
      plugin.getLogger().info("A " + fragmentType.getDisplayName() + " has despawned");
    }
  }

  // ===== Helper Methods =====

  /**
   * Get the fragment type from an item.
   * Delegates to ElementalItems.getFragmentType() - Single Source of Truth.
   */
  private FragmentType getFragmentType(ItemStack item) {
    return ElementalItems.getFragmentType(item);
  }

  /**
   * Check if an ItemStack is a fragment item.
   */
  private boolean isFragmentItem(ItemStack item) {
    return ElementalItems.getFragmentType(item) != null;
  }
}
