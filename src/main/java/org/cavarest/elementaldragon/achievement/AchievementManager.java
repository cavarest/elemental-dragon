package org.cavarest.elementaldragon.achievement;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.audio.SoundManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.lore.ChronicleManager;
import org.cavarest.elementaldragon.lore.LorePage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages player achievements for the Elemental Dragon plugin.
 * Tracks progress across fragment usage, ability mastery, lore discoveries,
 * and provides integration with other plugin systems.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Track unlocked achievements per player</li>
 *   <li>Track progress toward each achievement</li>
 *   <li>Integrate with ChronicleManager for lore-based achievements</li>
 *   <li>Integrate with FragmentManager for fragment-based achievements</li>
 *   <li>Integrate with AbilityManager for ability-based achievements</li>
 *   <li>Play sounds on achievement unlock</li>
 *   <li>Persistent storage of achievement state</li>
 * </ul>
 */
public class AchievementManager {

  private final ElementalDragon plugin;
  private final SoundManager soundManager;
  private final ChronicleManager chronicleManager;

  // Track unlocked achievements per player
  private final Map<UUID, Set<Achievement>> unlockedAchievements;

  // Track achievement progress per player
  // Structure: UUID -> Achievement -> Progress Count
  private final Map<UUID, Map<Achievement, Integer>> achievementProgress;

  // Track lightning usage count per player
  private final Map<UUID, Integer> lightningUsageCount;

  // Track fragment usage counts per player
  // Structure: UUID -> FragmentType -> AbilityNumber -> Count
  private final Map<UUID, Map<FragmentType, Map<Integer, Integer>>> fragmentAbilityUsage;

  // Track which fragments have been equipped by each player
  private final Map<UUID, Set<FragmentType>> equippedFragments;

  // Callback interface for ability usage tracking
  public interface AbilityUsageCallback {
    void onAbilityUsed(Player player, FragmentType fragmentType, int abilityNumber);
  }

  private AbilityUsageCallback abilityUsageCallback;

  /**
   * Create a new AchievementManager.
   *
   * @param plugin The plugin instance (can be null for testing)
   */
  public AchievementManager(ElementalDragon plugin) {
    this.plugin = plugin;
    this.soundManager = plugin != null ? new SoundManager(plugin) : null;
    this.chronicleManager = plugin != null ? plugin.getChronicleManager() : null;
    this.unlockedAchievements = new HashMap<>();
    this.achievementProgress = new HashMap<>();
    this.lightningUsageCount = new HashMap<>();
    this.fragmentAbilityUsage = new HashMap<>();
    this.equippedFragments = new HashMap<>();
  }

  /**
   * Set a callback for ability usage tracking.
   * This allows integration with FragmentManager.
   *
   * @param callback The callback to set
   */
  public void setAbilityUsageCallback(AbilityUsageCallback callback) {
    this.abilityUsageCallback = callback;
  }

  /**
   * Register an ability use for a player.
   * This tracks usage for achievement unlocking.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @param abilityNumber The ability number (1 or 2)
   */
  public void registerAbilityUse(Player player, FragmentType fragmentType, int abilityNumber) {
    if (player == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();

    // Initialize tracking if needed
    fragmentAbilityUsage.putIfAbsent(playerUuid, new EnumMap<>(FragmentType.class));
    Map<FragmentType, Map<Integer, Integer>> playerUsage = fragmentAbilityUsage.get(playerUuid);

    playerUsage.putIfAbsent(fragmentType, new HashMap<>());
    Map<Integer, Integer> fragmentUsage = playerUsage.get(fragmentType);

    // Increment count
    int currentCount = fragmentUsage.getOrDefault(abilityNumber, 0);
    fragmentUsage.put(abilityNumber, currentCount + 1);

    // Trigger callback if set
    if (abilityUsageCallback != null) {
      abilityUsageCallback.onAbilityUsed(player, fragmentType, abilityNumber);
    }

    // Check for fragment mastery achievements
    checkFragmentMasteryAchievements(player, fragmentType);

    // Check for all mastery achievement
    checkAllMasteryAchievement(player);

    // Sync with ChronicleManager
    if (chronicleManager != null) {
      chronicleManager.registerAbilityUse(player, fragmentType, abilityNumber);
    }
  }

  /**
   * Register a lightning ability use for a player.
   *
   * @param player The player
   */
  public void registerLightningUse(Player player) {
    if (player == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();
    int currentCount = lightningUsageCount.getOrDefault(playerUuid, 0);
    lightningUsageCount.put(playerUuid, currentCount + 1);

    // Check lightning achievements
    checkLightningAchievements(player);
  }

  /**
   * Register a fragment equip event for a player.
   *
   * @param player The player
   * @param fragmentType The fragment type being equipped
   */
  public void registerFragmentEquip(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();

    // Initialize tracking if needed
    equippedFragments.putIfAbsent(playerUuid, EnumSet.noneOf(FragmentType.class));

    // Add fragment to equipped set
    Set<FragmentType> equipped = equippedFragments.get(playerUuid);
    boolean wasEmpty = equipped.isEmpty();
    equipped.add(fragmentType);

    // Check first fragment achievement
    if (wasEmpty && !hasAchievement(player, Achievement.FIRST_FRAGMENT)) {
      unlockAchievement(player, Achievement.FIRST_FRAGMENT);
    }

    // Check all fragments achievement
    if (equipped.size() >= FragmentType.values().length) {
      if (!hasAchievement(player, Achievement.ALL_FRAGMENTS)) {
        unlockAchievement(player, Achievement.ALL_FRAGMENTS);
      }
    }

    // Sync with ChronicleManager
    if (chronicleManager != null) {
      chronicleManager.registerFragmentEquip(player, fragmentType);
    }
  }

  /**
   * Register a lore page unlock for a player.
   *
   * @param player The player
   * @param page The lore page that was unlocked
   */
  public void registerLoreUnlock(Player player, LorePage page) {
    if (player == null || page == null) {
      return;
    }

    // Check lore achievements based on discovered page count
    if (chronicleManager != null) {
      int discoveredCount = chronicleManager.getDiscoveredCount(player);

      if (discoveredCount >= 5 && !hasAchievement(player, Achievement.LORE_COLLECTOR)) {
        unlockAchievement(player, Achievement.LORE_COLLECTOR);
      }

      if (discoveredCount >= 7 && !hasAchievement(player, Achievement.LORE_MASTER)) {
        unlockAchievement(player, Achievement.LORE_MASTER);
      }
    }
  }

  /**
   * Check fragment mastery achievements for a player.
   *
   * @param player The player
   * @param fragmentType The fragment type
   */
  private void checkFragmentMasteryAchievements(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();
    if (!fragmentAbilityUsage.containsKey(playerUuid)) {
      return;
    }

    Map<FragmentType, Map<Integer, Integer>> playerUsage = fragmentAbilityUsage.get(playerUuid);
    if (!playerUsage.containsKey(fragmentType)) {
      return;
    }

    Map<Integer, Integer> fragmentUsage = playerUsage.get(fragmentType);

    // Check if both abilities have been used 10+ times
    int ability1Count = fragmentUsage.getOrDefault(1, 0);
    int ability2Count = fragmentUsage.getOrDefault(2, 0);

    if (ability1Count >= 10 && ability2Count >= 10) {
      Achievement masteryAchievement = getMasteryAchievementForFragment(fragmentType);
      if (masteryAchievement != null && !hasAchievement(player, masteryAchievement)) {
        unlockAchievement(player, masteryAchievement);
      }
    }
  }

  /**
   * Get the mastery achievement for a fragment type.
   *
   * @param fragmentType The fragment type
   * @return The mastery achievement or null if not found
   */
  private Achievement getMasteryAchievementForFragment(FragmentType fragmentType) {
    if (fragmentType == null) {
      return null;
    }

    switch (fragmentType) {
      case BURNING:
        return Achievement.BURNING_MASTERY;
      case AGILITY:
        return Achievement.AGILITY_MASTERY;
      case IMMORTAL:
        return Achievement.IMMORTAL_MASTERY;
      case CORRUPTED:
        return Achievement.CORRUPTED_MASTERY;
      default:
        return null;
    }
  }

  /**
   * Check lightning achievements for a player.
   *
   * @param player The player
   */
  private void checkLightningAchievements(Player player) {
    if (player == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();
    int usageCount = lightningUsageCount.getOrDefault(playerUuid, 0);

    if (usageCount >= 50 && !hasAchievement(player, Achievement.LIGHTNING_ADEPT)) {
      unlockAchievement(player, Achievement.LIGHTNING_ADEPT);
    }

    if (usageCount >= 100 && !hasAchievement(player, Achievement.LIGHTNING_MASTER)) {
      unlockAchievement(player, Achievement.LIGHTNING_MASTER);
    }
  }

  /**
   * Check if all abilities have been mastered.
   *
   * @param player The player
   */
  private void checkAllMasteryAchievement(Player player) {
    if (player == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();

    // Check if all 8 abilities have been mastered (used 10+ times each)
    int masteredCount = 0;

    if (fragmentAbilityUsage.containsKey(playerUuid)) {
      Map<FragmentType, Map<Integer, Integer>> playerUsage = fragmentAbilityUsage.get(playerUuid);

      for (Map<Integer, Integer> fragmentUsage : playerUsage.values()) {
        for (int count : fragmentUsage.values()) {
          if (count >= 10) {
            masteredCount++;
          }
        }
      }
    }

    if (masteredCount >= 8 && !hasAchievement(player, Achievement.DRAGON_WIELDER)) {
      unlockAchievement(player, Achievement.DRAGON_WIELDER);
    }
  }

  /**
   * Unlock an achievement for a player.
   *
   * @param player The player
   * @param achievement The achievement to unlock
   */
  public void unlockAchievement(Player player, Achievement achievement) {
    if (player == null || achievement == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();

    // Initialize tracking if needed
    unlockedAchievements.putIfAbsent(playerUuid, EnumSet.noneOf(Achievement.class));

    // Check if already unlocked
    if (unlockedAchievements.get(playerUuid).contains(achievement)) {
      return;
    }

    // Unlock the achievement
    unlockedAchievements.get(playerUuid).add(achievement);

    // Initialize progress tracking if needed
    achievementProgress.putIfAbsent(playerUuid, new EnumMap<>(Achievement.class));

    // Mark as complete
    achievementProgress.get(playerUuid).put(achievement, achievement.getRequiredCount());

    // Play achievement sound (only if soundManager is available)
    if (soundManager != null) {
      soundManager.playAchievementSound(player);
    }

    // Notify player
    notifyAchievementUnlock(player, achievement);
  }

  /**
   * Notify player of achievement unlock.
   *
   * @param player The player
   * @param achievement The achievement that was unlocked
   */
  private void notifyAchievementUnlock(Player player, Achievement achievement) {
    player.sendMessage(
      Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GOLD)
    );
    player.sendMessage(
      Component.text("🏆 Achievement Unlocked!", NamedTextColor.YELLOW)
        .decoration(TextDecoration.BOLD, true)
    );
    player.sendMessage(
      Component.text(achievement.getDisplayName(), NamedTextColor.GOLD)
    );
    player.sendMessage(
      Component.text(achievement.getDescription(), NamedTextColor.WHITE)
    );
    player.sendMessage(
      Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GOLD)
    );
  }

  /**
   * Check if a player has an achievement.
   *
   * @param player The player
   * @param achievement The achievement to check
   * @return true if the player has the achievement
   */
  public boolean hasAchievement(Player player, Achievement achievement) {
    if (player == null || achievement == null) {
      return false;
    }

    UUID playerUuid = player.getUniqueId();
    if (!unlockedAchievements.containsKey(playerUuid)) {
      return false;
    }

    return unlockedAchievements.get(playerUuid).contains(achievement);
  }

  /**
   * Get all unlocked achievements for a player.
   *
   * @param player The player
   * @return Set of unlocked achievements
   */
  public Set<Achievement> getUnlockedAchievements(Player player) {
    if (player == null) {
      return EnumSet.noneOf(Achievement.class);
    }

    UUID playerUuid = player.getUniqueId();
    unlockedAchievements.putIfAbsent(playerUuid, EnumSet.noneOf(Achievement.class));

    return EnumSet.copyOf(unlockedAchievements.get(playerUuid));
  }

  /**
   * Get achievement progress for a player.
   *
   * @param player The player
   * @param achievement The achievement
   * @return Progress count
   */
  public int getAchievementProgress(Player player, Achievement achievement) {
    if (player == null || achievement == null) {
      return 0;
    }

    UUID playerUuid = player.getUniqueId();

    if (!achievementProgress.containsKey(playerUuid)) {
      return 0;
    }

    Map<Achievement, Integer> progress = achievementProgress.get(playerUuid);
    if (!progress.containsKey(achievement)) {
      return 0;
    }

    return progress.get(achievement);
  }

  /**
   * Get lightning usage count for a player.
   *
   * @param player The player
   * @return Lightning usage count
   */
  public int getLightningUsageCount(Player player) {
    if (player == null) {
      return 0;
    }

    return lightningUsageCount.getOrDefault(player.getUniqueId(), 0);
  }

  /**
   * Get fragment ability usage count for a player.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @param abilityNumber The ability number
   * @return Usage count
   */
  public int getFragmentAbilityUsageCount(Player player, FragmentType fragmentType, int abilityNumber) {
    if (player == null || fragmentType == null) {
      return 0;
    }

    UUID playerUuid = player.getUniqueId();

    if (!fragmentAbilityUsage.containsKey(playerUuid)) {
      return 0;
    }

    Map<FragmentType, Map<Integer, Integer>> playerUsage = fragmentAbilityUsage.get(playerUuid);
    if (!playerUsage.containsKey(fragmentType)) {
      return 0;
    }

    Map<Integer, Integer> fragmentUsage = playerUsage.get(fragmentType);
    return fragmentUsage.getOrDefault(abilityNumber, 0);
  }

  /**
   * Get equipped fragments for a player.
   *
   * @param player The player
   * @return Set of equipped fragment types
   */
  public Set<FragmentType> getEquippedFragments(Player player) {
    if (player == null) {
      return EnumSet.noneOf(FragmentType.class);
    }

    UUID playerUuid = player.getUniqueId();
    equippedFragments.putIfAbsent(playerUuid, EnumSet.noneOf(FragmentType.class));

    return EnumSet.copyOf(equippedFragments.get(playerUuid));
  }

  /**
   * Get the total number of unlocked achievements for a player.
   *
   * @param player The player
   * @return Count of unlocked achievements
   */
  public int getUnlockedCount(Player player) {
    return getUnlockedAchievements(player).size();
  }

  /**
   * Get the total number of available achievements.
   *
   * @return Total achievement count
   */
  public int getTotalAchievementCount() {
    return Achievement.values().length;
  }

  /**
   * Get a formatted progress string for an achievement.
   *
   * @param player The player
   * @param achievement The achievement
   * @return Progress string (e.g., "3/10")
   */
  public String getProgressString(Player player, Achievement achievement) {
    if (player == null || achievement == null) {
      return "0/0";
    }

    int progress = getAchievementProgress(player, achievement);
    return achievement.getProgressString(progress);
  }

  /**
   * Reset all progress for a player (admin command).
   *
   * @param player The player
   */
  public void resetProgress(Player player) {
    if (player == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();

    unlockedAchievements.remove(playerUuid);
    achievementProgress.remove(playerUuid);
    lightningUsageCount.remove(playerUuid);
    fragmentAbilityUsage.remove(playerUuid);
    equippedFragments.remove(playerUuid);

    player.sendMessage(
      Component.text("Achievement progress has been reset.", NamedTextColor.YELLOW)
    );
  }

  /**
   * Get achievement by name (case-insensitive).
   *
   * @param name The achievement name
   * @return Achievement or null if not found
   */
  public Achievement getAchievementByName(String name) {
    return Achievement.fromName(name);
  }

  /**
   * Get all achievements in a category.
   *
   * @param category The category
   * @return Array of achievements
   */
  public Achievement[] getAchievementsByCategory(Achievement.AchievementCategory category) {
    return Achievement.getByCategory(category);
  }

  /**
   * Get achievement categories.
   *
   * @return Array of all categories
   */
  public Achievement.AchievementCategory[] getCategories() {
    return Achievement.AchievementCategory.values();
  }
}
