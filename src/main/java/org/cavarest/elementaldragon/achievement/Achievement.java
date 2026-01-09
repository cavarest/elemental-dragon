package org.cavarest.elementaldragon.achievement;

import org.cavarest.elementaldragon.fragment.FragmentType;

/**
 * Enum representing all achievements in the Elemental Dragon plugin.
 * Each achievement has specific unlock criteria, descriptions, and progress tracking.
 * 
 * <p>Achievement Categories:</p>
 * <ul>
 *   <li>Fragment Discoveries - Unlock each fragment type</li>
 *   <li>Ability Mastery - Use each ability 10+ times</li>
 *   <li>Lore Hunter - Unlock chronicle pages</li>
 *   <li>Dragon Wielder - Complete mastery achievements</li>
 *   <li>Lightning Master - Lightning usage milestones</li>
 * </ul>
 */
public enum Achievement {

  /**
   * First Fragment - Equip your first fragment.
   * Category: Fragment Discoveries
   * Criteria: Equip any single fragment type
   */
  FIRST_FRAGMENT(
    "First Fragment",
    "Equip your first elemental fragment",
    AchievementCategory.FRAGMENT_DISCOVERY,
    AchievementCriteria.FRAGMENT_EQUIP,
    null,
    1
  ),

  /**
   * All Fragments - Equip all 4 fragment types.
   * Category: Fragment Discoveries
   * Criteria: Equip all 4 fragment types at least once
   */
  ALL_FRAGMENTS(
    "Dragon Collector",
    "Equip all 4 fragment types: Burning, Agility, Immortal, and Corrupted",
    AchievementCategory.FRAGMENT_DISCOVERY,
    AchievementCriteria.ALL_FRAGMENTS,
    null,
    4
  ),

  /**
   * Burning Mastery - Master both Burning Fragment abilities.
   * Category: Ability Mastery
   * Criteria: Use both Burning Fragment abilities 10 times each
   */
  BURNING_MASTERY(
    "Fire Tamer",
    "Master both Burning Fragment abilities (use each 10 times)",
    AchievementCategory.ABILITY_MASTERY,
    AchievementCriteria.FRAGMENT_MASTERY,
    FragmentType.BURNING,
    10
  ),

  /**
   * Agility Mastery - Master both Agility Fragment abilities.
   * Category: Ability Mastery
   * Criteria: Use both Agility Fragment abilities 10 times each
   */
  AGILITY_MASTERY(
    "Wind Walker",
    "Master both Agility Fragment abilities (use each 10 times)",
    AchievementCategory.ABILITY_MASTERY,
    AchievementCriteria.FRAGMENT_MASTERY,
    FragmentType.AGILITY,
    10
  ),

  /**
   * Immortal Mastery - Master both Immortal Fragment abilities.
   * Category: Ability Mastery
   * Criteria: Use both Immortal Fragment abilities 10 times each
   */
  IMMORTAL_MASTERY(
    "Stone Guardian",
    "Master both Immortal Fragment abilities (use each 10 times)",
    AchievementCategory.ABILITY_MASTERY,
    AchievementCriteria.FRAGMENT_MASTERY,
    FragmentType.IMMORTAL,
    10
  ),

  /**
   * Corrupted Mastery - Master both Corrupted Core abilities.
   * Category: Ability Mastery
   * Criteria: Use both Corrupted Core abilities 10 times each
   */
  CORRUPTED_MASTERY(
    "Void Walker",
    "Master both Corrupted Core abilities (use each 10 times)",
    AchievementCategory.ABILITY_MASTERY,
    AchievementCriteria.FRAGMENT_MASTERY,
    FragmentType.CORRUPTED,
    10
  ),

  /**
   * Lore Collector - Unlock 5 chronicle pages.
   * Category: Lore Hunter
   * Criteria: Discover 5 or more chronicle pages
   */
  LORE_COLLECTOR(
    "Lore Seeker",
    "Unlock 5 chronicle pages in the Chronicle of the Fallen Dragons",
    AchievementCategory.LORE_HUNTER,
    AchievementCriteria.LORE_UNLOCK,
    null,
    5
  ),

  /**
   * Lore Master - Unlock all 7 chronicle pages.
   * Category: Lore Hunter
   * Criteria: Discover all 7 chronicle pages
   */
  LORE_MASTER(
    "Chronicler",
    "Unlock all 7 chronicle pages",
    AchievementCategory.LORE_HUNTER,
    AchievementCriteria.LORE_UNLOCK,
    null,
    7
  ),

  /**
   * Lightning Adept - Use lightning 50 times.
   * Category: Lightning Master
   * Criteria: Use the lightning ability 50 times
   */
  LIGHTNING_ADEPT(
    "Storm Caller",
    "Use the lightning ability 50 times",
    AchievementCategory.LIGHTNING_MASTER,
    AchievementCriteria.LIGHTNING_USE,
    null,
    50
  ),

  /**
   * Lightning Master - Use lightning 100 times.
   * Category: Lightning Master
   * Criteria: Use the lightning ability 100 times
   */
  LIGHTNING_MASTER(
    "Thunder Lord",
    "Use the lightning ability 100 times",
    AchievementCategory.LIGHTNING_MASTER,
    AchievementCriteria.LIGHTNING_USE,
    null,
    100
  ),

  /**
   * Dragon Wielder - Complete mastery of all abilities.
   * Category: Dragon Wielder
   * Criteria: Master all 8 fragment abilities (use each 10 times)
   */
  DRAGON_WIELDER(
    "Dragon Wielder",
    "Master all fragment abilities (use each 10 times)",
    AchievementCategory.DRAGON_WIELDER,
    AchievementCriteria.ALL_MASTERY,
    null,
    8
  );

  private final String displayName;
  private final String description;
  private final AchievementCategory category;
  private final AchievementCriteria criteria;
  private final FragmentType fragmentType;
  private final int requiredCount;

  Achievement(
    String displayName,
    String description,
    AchievementCategory category,
    AchievementCriteria criteria,
    FragmentType fragmentType,
    int requiredCount
  ) {
    this.displayName = displayName;
    this.description = description;
    this.category = category;
    this.criteria = criteria;
    this.fragmentType = fragmentType;
    this.requiredCount = requiredCount;
  }

  /**
   * Get the achievement display name.
   *
   * @return Display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Get the achievement description.
   *
   * @return Description text
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get the achievement category.
   *
   * @return Category
   */
  public AchievementCategory getCategory() {
    return category;
  }

  /**
   * Get the achievement criteria type.
   *
   * @return Criteria type
   */
  public AchievementCriteria getCriteria() {
    return criteria;
  }

  /**
   * Get the associated fragment type (if any).
   *
   * @return Fragment type or null
   */
  public FragmentType getFragmentType() {
    return fragmentType;
  }

  /**
   * Get the required count for unlocking.
   *
   * @return Required count
   */
  public int getRequiredCount() {
    return requiredCount;
  }

  /**
   * Get a formatted progress string for this achievement.
   *
   * @param currentProgress Current progress count
   * @return Formatted progress string (e.g., "3/10")
   */
  public String getProgressString(int currentProgress) {
    return currentProgress + "/" + requiredCount;
  }

  /**
   * Check if this achievement is complete based on progress.
   *
   * @param currentProgress Current progress count
   * @return true if achievement is complete
   */
  public boolean isComplete(int currentProgress) {
    return currentProgress >= requiredCount;
  }

  /**
   * Get Achievement by name (case-insensitive).
   *
   * @param name The achievement name
   * @return Achievement or null if not found
   */
  public static Achievement fromName(String name) {
    if (name == null || name.trim().isEmpty()) {
      return null;
    }
    try {
      return Achievement.valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      // Check for partial matches
      for (Achievement achievement : values()) {
        if (achievement.name().startsWith(name.toUpperCase())) {
          return achievement;
        }
      }
      return null;
    }
  }

  /**
   * Get all achievements in a specific category.
   *
   * @param category The category to filter by
   * @return Array of achievements in the category
   */
  public static Achievement[] getByCategory(AchievementCategory category) {
    if (category == null) {
      return new Achievement[0];
    }
    return java.util.Arrays.stream(values())
      .filter(a -> a.category == category)
      .toArray(Achievement[]::new);
  }

  /**
   * Get all achievements that require a specific fragment type.
   *
   * @param fragmentType The fragment type
   * @return Array of achievements for the fragment
   */
  public static Achievement[] getByFragmentType(FragmentType fragmentType) {
    if (fragmentType == null) {
      return new Achievement[0];
    }
    return java.util.Arrays.stream(values())
      .filter(a -> a.fragmentType == fragmentType)
      .toArray(Achievement[]::new);
  }

  /**
   * Enum representing achievement categories.
   */
  public enum AchievementCategory {
    /**
     * Discover and equip fragments.
     */
    FRAGMENT_DISCOVERY,

    /**
     * Master fragment abilities.
     */
    ABILITY_MASTERY,

    /**
     * Unlock chronicle lore pages.
     */
    LORE_HUNTER,

    /**
     * Lightning ability milestones.
     */
    LIGHTNING_MASTER,

    /**
     * Complete all mastery achievements.
     */
    DRAGON_WIELDER
  }

  /**
   * Enum representing achievement criteria types.
   */
  public enum AchievementCriteria {
    /**
     * Criteria based on equipping fragments.
     */
    FRAGMENT_EQUIP,

    /**
     * Criteria based on equipping all fragments.
     */
    ALL_FRAGMENTS,

    /**
     * Criteria based on mastering fragment abilities.
     */
    FRAGMENT_MASTERY,

    /**
     * Criteria based on unlocking chronicle pages.
     */
    LORE_UNLOCK,

    /**
     * Criteria based on lightning ability usage.
     */
    LIGHTNING_USE,

    /**
     * Criteria based on mastering all abilities.
     */
    ALL_MASTERY
  }
}
