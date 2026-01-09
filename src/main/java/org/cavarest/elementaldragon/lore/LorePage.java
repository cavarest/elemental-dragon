package org.cavarest.elementaldragon.lore;

import org.cavarest.elementaldragon.fragment.FragmentType;

/**
 * Enum representing pages in the Chronicle of the Fallen Dragons.
 * Each page has specific unlock conditions based on player actions.
 */
public enum LorePage {

  /**
   * Page 1 - Introduction to the Chronicle.
   * Always unlocked.
   */
  INTRODUCTION(
    1,
    "The Chronicle of the Fallen Dragons",
    "In ages past, four elemental dragons\n" +
    "ruled the realms of fire, wind, earth,\n" +
    "and void. Their power was absolute,\n" +
    "their wisdom beyond mortal\n" +
    "comprehension.\n\n" +
    "This chronicle tells their tale,\n" +
    "and the story of how their power\n" +
    "was scattered across the world...",
    UnlockTrigger.ALWAYS,
    null,
    0
  ),

  /**
   * Page 2 - IGNIS, the Burning Dragon.
   * Unlocked by using Dragon's Wrath 5 times.
   */
  IGNIS(
    2,
    "IGNIS - The Dragon of Fire",
    "IGNIS, the Dragon of Fire, burned\n" +
    "with the passion of a thousand suns.\n" +
    "His scales shimmered like molten gold,\n" +
    "and his breath could melt mountains.\n\n" +
    "The Burning Fragment grants the power\n" +
    "to wield flames as a weapon, to create\n" +
    "infernos that consume all in their\n" +
    "path. Dragon's Wrath unleashes a\n" +
    "devastating fireball, while Infernal\n" +
    "Dominion creates a ring of flames\n" +
    "that none can cross.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.BURNING,
    5
  ),

  /**
   * Page 3 - ZEPHYR, the Wind Dragon.
   * Unlocked by using Draconic Surge 5 times.
   */
  ZEPHYR(
    3,
    "ZEPHYR - The Dragon of Wind",
    "ZEPHYR, the Dragon of Wind, moved\n" +
    "swift as thought itself. His wings\n" +
    "could summon hurricanes, and his\n" +
    "presence brought lightning and storm.\n\n" +
    "The Agility Fragment grants the\n" +
    "blessing of speed, the ability to\n" +
    "ride the winds and cross any terrain.\n" +
    "Draconic Surge channels the wind's\n" +
    "swiftness, while Wing Burst allows\n" +
    "one to soar like the dragon himself.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.AGILITY,
    5
  ),

  /**
   * Page 4 - TERRA, the Earth Dragon.
   * Unlocked by using Draconic Reflex 3 times.
   */
  TERRA(
    4,
    "TERRA - The Dragon of Earth",
    "TERRA, the Dragon of Earth, stood\n" +
    "as the unmovable object. Her scales\n" +
    "were as hard as diamond, her strength\n" +
    "unmatched in all the realms.\n\n" +
    "The Immortal Fragment grants the\n" +
    "resilience of mountains, the power\n" +
    "to withstand any attack. Draconic\n" +
    "Reflex channels her unbreakable\n" +
    "defense, while Essence Rebirth\n" +
    "embodies her eternal endurance.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.IMMORTAL,
    3
  ),

  /**
   * Page 5 - UMBRA, the Void Dragon.
   * Unlocked by using Dread Gaze 3 times.
   */
  UMBRA(
    5,
    "UMBRA - The Dragon of Void",
    "UMBRA, the Dragon of Void, emerged\n" +
    "from the spaces between stars. His\n" +
    "form flickered between existence and\n" +
    "nothingness, his eyes held infinity.\n\n" +
    "The Corrupted Core grants the power\n" +
    "of darkness, to drain life itself\n" +
    "and strike fear into the hearts of\n" +
    "enemies. Dread Gaze channels the\n" +
    "terror of the void, while Life\n" +
    "Devourer consumes the essence of\n" +
    "all living things.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.CORRUPTED,
    3
  ),

  /**
   * Page 6 - The Fall of the Dragons.
   * Unlocked by equipping all 4 fragments at least once.
   */
  THE_FALL(
    6,
    "The Fall",
    "But dragons are not immortal. In the\n" +
    "Age of Ending, the four elemental\n" +
    "dragons fell in a great cataclysm.\n" +
    "Their physical forms shattered,\n" +
    "their power scattered across the\n" +
    "world in the form of fragments.\n\n" +
    "Yet their essence endures. Those who\n" +
    "gather the fragments and prove their\n" +
    "worth may wield a fraction of the\n" +
    "dragons' ancient power.",
    UnlockTrigger.EQUIP_ALL_FRAGMENTS,
    null,
    0
  ),

  /**
   * Page 7 - Recovery and Mastery.
   * Unlocked by using all abilities 10 times each.
   */
  RECOVERY(
    7,
    "Recovery and Mastery",
    "Only those who prove themselves\n" +
    "worthy by mastering the lightning\n" +
    "of dragon eggs may hope to find and\n" +
    "wield the fragments of the fallen\n" +
    "dragons.\n\n" +
    "Through dedication and practice,\n" +
    "one can learn to channel the full\n" +
    "power of each fragment. Master all\n" +
    "abilities, and you shall become\n" +
    "a true Dragon Wielder, carrying\n" +
    "forward the legacy of the four\n" +
    "who came before.",
    UnlockTrigger.MASTER_ALL_ABILITIES,
    null,
    0
  );

  private final int pageNumber;
  private final String title;
  private final String content;
  private final UnlockTrigger trigger;
  private final FragmentType fragmentType;
  private final int requiredCount;

  LorePage(
    int pageNumber,
    String title,
    String content,
    UnlockTrigger trigger,
    FragmentType fragmentType,
    int requiredCount
  ) {
    this.pageNumber = pageNumber;
    this.title = title;
    this.content = content;
    this.trigger = trigger;
    this.fragmentType = fragmentType;
    this.requiredCount = requiredCount;
  }

  /**
   * Get the page number.
   *
   * @return Page number (1-7)
   */
  public int getPageNumber() {
    return pageNumber;
  }

  /**
   * Get the page title.
   *
   * @return Page title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get the page content.
   *
   * @return Page content text
   */
  public String getContent() {
    return content;
  }

  /**
   * Get the unlock trigger type.
   *
   * @return Unlock trigger
   */
  public UnlockTrigger getTrigger() {
    return trigger;
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
   * Get LorePage by page number.
   *
   * @param pageNumber The page number (1-7)
   * @return LorePage or null if not found
   */
  public static LorePage fromPageNumber(int pageNumber) {
    for (LorePage page : values()) {
      if (page.pageNumber == pageNumber) {
        return page;
      }
    }
    return null;
  }

  /**
   * Enum representing different unlock trigger types.
   */
  public enum UnlockTrigger {
    /**
     * Always unlocked (like the introduction page).
     */
    ALWAYS,

    /**
     * Unlocked by using a specific fragment ability a certain number of times.
     */
    ABILITY_USE,

    /**
     * Unlocked by equipping all fragments at least once.
     */
    EQUIP_ALL_FRAGMENTS,

    /**
     * Unlocked by mastering all abilities (using each 10 times).
     */
    MASTER_ALL_ABILITIES
  }
}