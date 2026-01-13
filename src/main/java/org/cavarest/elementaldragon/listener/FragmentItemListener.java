package org.cavarest.elementaldragon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Listener for fragment item restrictions:
 * 1. Detects when fragments leave player inventory (drop, despawn) and unequips them
 *
 * Note: Container restrictions removed per Issue 7 - fragments can now be stored freely.
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

  // Container restrictions removed per Issue 7 - fragments can be stored in containers.

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
