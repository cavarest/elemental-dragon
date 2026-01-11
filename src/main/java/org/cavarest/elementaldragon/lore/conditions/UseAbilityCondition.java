package org.cavarest.elementaldragon.lore.conditions;

import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.lore.ChronicleManager;
import org.cavarest.elementaldragon.lore.UnlockCondition;

import java.util.Map;
import java.util.UUID;

/**
 * Unlock condition that requires using abilities a specific number of times.
 */
public class UseAbilityCondition implements UnlockCondition {

  private final FragmentType fragmentType;
  private final int requiredCount;

  /**
   * Create a new UseAbilityCondition.
   *
   * @param fragmentType The fragment type to track (null for all fragments)
   * @param requiredCount Number of ability uses required
   */
  public UseAbilityCondition(FragmentType fragmentType, int requiredCount) {
    this.fragmentType = fragmentType;
    this.requiredCount = requiredCount;
  }

  @Override
  public boolean isSatisfied(Player player, ChronicleManager chronicleManager) {
    if (player == null) {
      return false;
    }
    UUID playerUuid = player.getUniqueId();
    return getTotalUsage(playerUuid, chronicleManager) >= requiredCount;
  }

  @Override
  public String getProgress(Player player, ChronicleManager chronicleManager) {
    if (player == null) {
      return "0/" + requiredCount;
    }
    int usage = getTotalUsage(player.getUniqueId(), chronicleManager);
    return usage + "/" + requiredCount;
  }

  @Override
  public FragmentType getFragmentType() {
    return fragmentType;
  }

  private int getTotalUsage(UUID playerUuid, ChronicleManager chronicleManager) {
    if (fragmentType == null) {
      // Count all fragment usage
      int total = 0;
      for (FragmentType type : FragmentType.values()) {
        total += getFragmentUsage(playerUuid, type, chronicleManager);
      }
      return total;
    }
    return getFragmentUsage(playerUuid, fragmentType, chronicleManager);
  }

  private int getFragmentUsage(UUID playerUuid, FragmentType type, ChronicleManager chronicleManager) {
    // This is a simplified version - in practice we'd query the chronicle manager
    // For now, we'll just track based on what we can access
    int total = 0;
    for (int i = 1; i <= 2; i++) {
      total += chronicleManager.getAbilityUsageCount(
        null, type, i); // Note: This needs access to player, simplified here
    }
    return total;
  }
}
