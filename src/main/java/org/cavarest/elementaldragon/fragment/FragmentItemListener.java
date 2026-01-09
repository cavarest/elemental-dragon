package org.cavarest.elementaldragon.fragment;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.item.ElementalItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener for fragment item interactions and inventory restrictions.
 * Enforces the rule: Players can only have ONE fragment item at a time.
 * This prevents duplicate fragments in inventory, chests, ender chests, shulkers, etc.
 */
public class FragmentItemListener implements Listener {

  private final ElementalDragon plugin;
  private final FragmentManager fragmentManager;

  public FragmentItemListener(ElementalDragon plugin, FragmentManager fragmentManager) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    // Only handle right-click actions
    if (event.getAction() != Action.RIGHT_CLICK_AIR &&
        event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Player player = event.getPlayer();
    ItemStack item = event.getItem();

    if (item == null || !item.hasItemMeta()) {
      return;
    }

    // Check if item is a fragment
    FragmentType fragmentType = getFragmentType(item);
    if (fragmentType == null) {
      return;
    }

    // Cancel the event to prevent other interactions
    event.setCancelled(true);

    // Equip the fragment
    boolean success = fragmentManager.equipFragment(player, fragmentType);

    if (success) {
      player.sendMessage(
        Component.text("Equipped " + fragmentType.getDisplayName() + "!",
          NamedTextColor.GOLD)
      );
      player.sendMessage(
        Component.text(fragmentType.getPassiveBonus(),
          NamedTextColor.GRAY)
      );
    }
  }

  /**
   * Prevent players from picking up a DUPLICATE of the same fragment type.
   * Players CAN have multiple different fragments (fire + agile + immortal + corrupt).
   * Players CANNOT have duplicates (two fire fragments, two agile fragments, etc.).
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

    // Count how many of this SAME fragment type the player already has
    int existingCount = countFragmentType(player.getInventory().getContents(), pickupType);
    if (existingCount > 0) {
      // Cancel pickup - player already has this fragment type
      event.setCancelled(true);
      player.sendMessage(
        Component.text("⚠️ You can only possess ONE " + pickupType.getDisplayName() + " at a time!",
          NamedTextColor.RED)
      );
      player.sendMessage(
        Component.text("Drop or store your existing " + pickupType.getDisplayName() + " before picking up another.",
          NamedTextColor.GRAY)
      );
    }
  }

  /**
   * Prevent players from storing DUPLICATES of the same fragment type in containers.
   * Players can store different fragment types in the same container.
   * Players cannot store multiple copies of the same fragment type.
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getWhoClicked();
    ItemStack clickedItem = event.getCurrentItem();
    ItemStack cursorItem = event.getCursor();

    // Check if player is trying to place a fragment into a container
    if (event.getInventory().getType() != InventoryType.PLAYER &&
        event.getInventory().getType() != InventoryType.CRAFTING) {

      // Check cursor (item being placed)
      FragmentType cursorType = getFragmentType(cursorItem);
      if (cursorType != null) {
        // Count how many of this SAME type are already in the container
        int containerCount = countFragmentType(event.getInventory().getContents(), cursorType);
        if (containerCount > 0) {
          event.setCancelled(true);
          player.sendMessage(
            Component.text("⚠️ This container already has a " + cursorType.getDisplayName() + "!",
              NamedTextColor.RED)
          );
          player.sendMessage(
            Component.text("Only ONE of each fragment type can be stored per container.",
              NamedTextColor.GRAY)
          );
        }
      }
    }
  }

  /**
   * Count how many items of a specific fragment type exist in an inventory.
   *
   * @param contents The inventory contents
   * @param targetType The fragment type to count
   * @return The count of matching fragments
   */
  private int countFragmentType(ItemStack[] contents, FragmentType targetType) {
    int count = 0;
    for (ItemStack item : contents) {
      FragmentType type = getFragmentType(item);
      if (type == targetType) {
        count += item.getAmount();  // Count stack size
      }
    }
    return count;
  }

  /**
   * Get the fragment type in a player's inventory (DEPRECATED - use countFragmentType).
   *
   * @param player The player
   * @return The fragment type in inventory, or null if none found
   */
  @Deprecated
  private FragmentType getFragmentInInventory(Player player) {
    for (ItemStack item : player.getInventory().getContents()) {
      FragmentType type = getFragmentType(item);
      if (type != null) {
        return type;
      }
    }
    return null;
  }

  /**
   * Get the fragment type in a container's contents (DEPRECATED - use countFragmentType).
   *
   * @param contents The container contents
   * @return The fragment type in container, or null if none found
   */
  @Deprecated
  private FragmentType getFragmentInContainer(ItemStack[] contents) {
    for (ItemStack item : contents) {
      FragmentType type = getFragmentType(item);
      if (type != null) {
        return type;
      }
    }
    return null;
  }

  /**
   * Determine fragment type from item.
   *
   * @param item The item to check
   * @return FragmentType or null if not a fragment
   */
  private FragmentType getFragmentType(ItemStack item) {
    if (item == null || !item.hasItemMeta()) {
      return null;
    }

    for (FragmentType type : FragmentType.values()) {
      if (ElementalItems.isFragment(item, type)) {
        return type;
      }
    }
    return null;
  }
}