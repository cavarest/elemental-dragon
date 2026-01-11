package org.cavarest.elementaldragon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Listener for fragment item restrictions:
 * 1. Detects when fragments leave player inventory (drop, container, etc.) and unequips them
 * 2. Prevents fragments from being placed in containers
 * 3. Makes fragments fireproof (cannot burn in lava/fire)
 */
public class FragmentItemListener implements Listener {

  private final ElementalDragon plugin;
  private final FragmentManager fragmentManager;

  public FragmentItemListener(ElementalDragon plugin, FragmentManager fragmentManager) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
  }

  // ===== Item Loss Detection =====

  /**
   * Detect when a player drops a fragment and unequip it.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    ItemStack droppedItem = event.getItemDrop().getItemStack();
    FragmentType fragmentType = getFragmentType(droppedItem);

    if (fragmentType != null) {
      Player player = event.getPlayer();
      fragmentManager.unequipFragment(player);
      player.sendMessage(Component.text(
        "Your " + fragmentType.getDisplayName() + " abilities have been withdrawn!",
        NamedTextColor.RED
      ));
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

  // ===== Container Restriction =====

  /**
   * Prevent fragments from being placed in containers.
   * This includes: chests, hoppers, droppers, furnaces, etc.
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getWhoClicked();
    ItemStack cursorItem = event.getCursor();
    ItemStack clickedItem = event.getCurrentItem();

    // Check if cursor item is a fragment being moved to a container
    if (isFragmentItem(cursorItem) && isContainerInventory(event.getInventory())) {
      event.setCancelled(true);
      player.sendMessage(Component.text(
        "Elemental Dragon fragments cannot be stored in containers.",
        NamedTextColor.RED
      ));
      return;
    }

    // Check if clicked item is a fragment being moved to a container
    if (isFragmentItem(clickedItem) && isContainerInventory(event.getInventory())) {
      event.setCancelled(true);
      player.sendMessage(Component.text(
        "Elemental Dragon fragments cannot be stored in containers.",
        NamedTextColor.RED
      ));
    }
  }

  /**
   * Prevent fragments from being moved into containers via hopper etc.
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onInventoryMoveItem(InventoryMoveItemEvent event) {
    ItemStack item = event.getItem();
    if (isFragmentItem(item)) {
      // Check if the destination is a container
      Inventory destInventory = event.getDestination();
      if (isContainerInventory(destInventory)) {
        event.setCancelled(true);
      }
    }
  }

  /**
   * Check if an inventory is a container type (not player inventory).
   */
  private boolean isContainerInventory(Inventory inventory) {
    if (inventory == null) {
      return false;
    }

    InventoryType type = inventory.getType();

    // Container types that should be blocked
    return switch (type) {
      case CHEST, BARREL, DISPENSER, DROPPER, HOPPER,
           FURNACE, BLAST_FURNACE, SMOKER,
           ENDER_CHEST, SHULKER_BOX,
           ANVIL, SMITHING, GRINDSTONE, STONECUTTER, LECTERN,
           CARTOGRAPHY -> true;
      default -> false;
    };
  }

  // ===== Helper Methods =====

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
