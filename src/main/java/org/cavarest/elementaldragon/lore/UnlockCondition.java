package org.cavarest.elementaldragon.lore;

import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.fragment.FragmentType;

/**
 * Interface for Chronicle page unlock conditions.
 * Implement this interface to create custom unlock conditions for lore pages.
 *
 * This allows new fragments to define their own unlock conditions without
 * modifying ChronicleManager.
 *
 * Example implementations:
 * - UseAbilityCondition: Unlock after using an ability N times
 * - EquipFragmentCondition: Unlock after equipping a fragment
 * - MasterAbilityCondition: Unlock after mastering an ability (10+ uses)
 * - UnlockAllCondition: Unlock after collecting all fragments
 */
public interface UnlockCondition {

  /**
   * Check if the unlock condition is satisfied for a player.
   *
   * @param player The player to check
   * @param chronicleManager The chronicle manager (for querying state)
   * @return true if the condition is satisfied
   */
  boolean isSatisfied(Player player, ChronicleManager chronicleManager);

  /**
   * Get a human-readable description of the progress toward this condition.
   *
   * @param player The player
   * @param chronicleManager The chronicle manager
   * @return Progress string (e.g., "3/5", "Unlocked", "50%")
   */
  String getProgress(Player player, ChronicleManager chronicleManager);

  /**
   * Get the fragment type associated with this condition, if any.
   *
   * @return The fragment type, or null if not fragment-specific
   */
  default FragmentType getFragmentType() {
    return null;
  }
}
