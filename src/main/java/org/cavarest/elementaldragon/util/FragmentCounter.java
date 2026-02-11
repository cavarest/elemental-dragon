package org.cavarest.elementaldragon.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;

/**
 * Utility for counting fragments in player inventory.
 * Used for possession limit checking.
 *
 * <p>The possession limit is per fragment type, allowing players to have
 * all 4 different fragment types simultaneously while limiting duplicates
 * of the same type.</p>
 */
public class FragmentCounter {

  /**
   * Count how many fragments of a specific type the player has.
   * Checks main inventory, offhand, and cursor.
   *
   * @param player The player to check
   * @param fragmentType The fragment type to count
   * @return The count of fragments in inventory
   */
  public static int countFragments(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return 0;
    }

    int count = 0;

    // Check main inventory
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null) {
        FragmentType foundType = ElementalItems.getFragmentType(item);
        if (foundType == fragmentType) {
          count += item.getAmount();
        }
      }
    }

    // Check offhand
    ItemStack offhandItem = player.getInventory().getItemInOffHand();
    if (offhandItem != null) {
      FragmentType foundType = ElementalItems.getFragmentType(offhandItem);
      if (foundType == fragmentType) {
        count += offhandItem.getAmount();
      }
    }

    // Check cursor
    ItemStack cursorItem = player.getItemOnCursor();
    if (cursorItem != null) {
      FragmentType foundType = ElementalItems.getFragmentType(cursorItem);
      if (foundType == fragmentType) {
        count += cursorItem.getAmount();
      }
    }

    return count;
  }

  /**
   * Check if player can possess another fragment of the given type.
   *
   * @param player The player to check
   * @param fragmentType The fragment type
   * @param limit The possession limit (0 = unlimited)
   * @return true if player can have another fragment
   */
  public static boolean canPossessAnother(Player player, FragmentType fragmentType, int limit) {
    if (limit <= 0) {
      return true; // 0 = unlimited
    }
    int currentCount = countFragments(player, fragmentType);
    return currentCount < limit;
  }
}
