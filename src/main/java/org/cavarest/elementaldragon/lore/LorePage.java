package org.cavarest.elementaldragon.lore;

import org.cavarest.elementaldragon.fragment.FragmentType;

/**
 * Enum representing pages in the Chronicle of the Fallen Dragons.
 * Each page has specific unlock conditions based on player actions.
 * 
 * Expanded to ~19 pages with rich lore across 7 chapters.
 */
public enum LorePage {

  // ==================== INTRODUCTION (1 page) ====================
  
  /**
   * Page 1 - Introduction to the Chronicle.
   * Always unlocked.
   */
  INTRODUCTION(
    1,
    "The Chronicle of the Fallen Dragons",
    "In ages past, four elemental dragons ruled the\n" +
    "realms of fire, wind, earth, and void. Their\n" +
    "power was absolute, their wisdom beyond mortal\n" +
    "comprehension. They shaped mountains, carved\n" +
    "oceans, and wove the very fabric of reality.\n\n" +
    "This chronicle tells their tale—the story of\n" +
    "cosmic beings who watched empires rise and fall\n" +
    "like sparks in the darkness. It speaks of their\n" +
    "power, scattered across the world in fragments,\n" +
    "waiting for those brave enough to claim them.\n\n" +
    "Turn these pages, and learn the truth of the\n" +
    "dragons who came before...",
    UnlockTrigger.ALWAYS,
    null,
    0
  ),

  // ==================== IGNIS - FIRE DRAGON (3 pages) ====================
  
  /**
   * Page 2 - IGNIS Part 1: The Eternal Flame
   * Unlocked by using any fire ability 5 times.
   */
  IGNIS_1(
    2,
    "IGNIS - The Dragon of Fire (Part I)",
    "IGNIS, the Dragon of Fire, burned with the\n" +
    "passion of a thousand suns. Before the world\n" +
    "knew dawn, IGNIS brought light. His scales\n" +
    "shimmered like molten gold, each one a furnace\n" +
    "of ancient power. His eyes held the birth of\n" +
    "stars, his breath the end of worlds.\n\n" +
    "In the Age of Creation, IGNIS forged the\n" +
    "first mountains from cooling magma. He danced\n" +
    "through volcanoes, his laughter the rumble of\n" +
    "eruptions. Mortals worshiped him as the\n" +
    "bringer of warmth, the guardian against the\n" +
    "cold void. Yet they also feared him, for his\n" +
    "wrath could turn paradise to ash in moments.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.BURNING,
    5
  ),

  /**
   * Page 3 - IGNIS Part 2: The Burning Passion
   * Unlocked by using any fire ability 10 times.
   */
  IGNIS_2(
    3,
    "IGNIS - The Dragon of Fire (Part II)",
    "The Burning Fragment carries IGNIS's essence—\n" +
    "passion incarnate, destruction and creation\n" +
    "intertwined. Those who wield it feel the\n" +
    "dragon's burning heart beating within their own\n" +
    "chest, urging them to act, to conquer, to burn\n" +
    "away all that stands in their path.\n\n" +
    "Dragon's Wrath channels raw fury into a\n" +
    "devastating fireball that consumes everything\n" +
    "it touches. Ancient texts speak of pyromancers\n" +
    "who leveled entire forests with a single cast,\n" +
    "their targets reduced to cinders before they\n" +
    "could scream. Yet this power demands respect—\n" +
    "misuse it, and you too shall be consumed.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.BURNING,
    10
  ),

  /**
   * Page 3 - IGNIS Part 3: Legacy of Ash
   * Unlocked by using any fire ability 20 times.
   */
  IGNIS_3(
    4,
    "IGNIS - The Dragon of Fire (Part III)",
    "Infernal Dominion, the fragment's ultimate\n" +
    "expression, creates a ring of flames that none\n" +
    "can cross. This was IGNIS's final gift to his\n" +
    "chosen champions—an impenetrable barrier that\n" +
    "turned battlefields into funeral pyres.\n\n" +
    "Legends tell of a warrior-queen who held back\n" +
    "an army of ten thousand with this power alone.\n" +
    "For three days and nights the flames burned,\n" +
    "until her enemies broke and fled. She stood\n" +
    "unharmed at the center, wreathed in fire,\n" +
    "as IGNIS himself once stood atop the world's\n" +
    "first volcano, surveying his domain of flame.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.BURNING,
    20
  ),

  // ==================== ZEPHYR - WIND DRAGON (3 pages) ====================
  
  /**
   * Page 5 - ZEPHYR Part 1: The Eternal Storm
   * Unlocked by using any agility ability 5 times.
   */
  ZEPHYR_1(
    5,
    "ZEPHYR - The Dragon of Wind (Part I)",
    "ZEPHYR, the Dragon of Wind, moved swift as\n" +
    "thought itself. Where IGNIS was passion, ZEPHYR\n" +
    "was freedom—the rush of wind through canyons,\n" +
    "the howl of hurricanes, the whisper of breeze\n" +
    "through ancient trees. His wings spanned the\n" +
    "horizon, each beat summoning tempests.\n\n" +
    "Before ZEPHYR, the world stood still. He taught\n" +
    "the air to move, the clouds to dance, the sea\n" +
    "to churn with waves. He painted the sky with\n" +
    "lightning, his roars the thunder that shook\n" +
    "mountains. Sailors prayed for his favor, for\n" +
    "ZEPHYR controlled the very breath of the world—\n" +
    "and breath, like freedom, could not be tamed.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.AGILITY,
    5
  ),

  /**
   * Page 6 - ZEPHYR Part 2: The Swift Current
   * Unlocked by using any agility ability 10 times.
   */
  ZEPHYR_2(
    6,
    "ZEPHYR - The Dragon of Wind (Part II)",
    "The Agility Fragment grants ZEPHYR's blessing—\n" +
    "the wind's favor, speed beyond mortal limits.\n" +
    "Those who master it become one with the storm,\n" +
    "their movements a blur, their strikes arriving\n" +
    "before enemies can react. Draconic Surge\n" +
    "channels the wind's swiftness directly.\n\n" +
    "Chronicles record scouts who crossed entire\n" +
    "continents in days, their feet barely touching\n" +
    "ground. They moved like ZEPHYR himself once\n" +
    "moved—a silver streak against blue sky, too\n" +
    "fast to follow, too free to catch. The wind\n" +
    "sang songs of their passage, and hostile\n" +
    "forces never saw them coming until too late.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.AGILITY,
    10
  ),

  /**
   * Page 7 - ZEPHYR Part 3: Wings of Freedom
   * Unlocked by using any agility ability 20 times.
   */
  ZEPHYR_3(
    7,
    "ZEPHYR - The Dragon of Wind (Part III)",
    "Wing Burst, the fragment's crowning gift,\n" +
    "allows one to soar like the dragon himself.\n" +
    "ZEPHYR granted this power sparingly, only to\n" +
    "those who proved they understood freedom's true\n" +
    "meaning—not escape, but the courage to face\n" +
    "any challenge from any direction.\n\n" +
    "Old texts speak of sky-warriors who rode the\n" +
    "wind like others rode horses. They struck from\n" +
    "above, vanished into clouds, and laughed at\n" +
    "walls and barriers. For them, the entire world\n" +
    "was open—no fortress could contain them, no\n" +
    "prison hold them. They lived as ZEPHYR lived:\n" +
    "absolutely, eternally, gloriously free.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.AGILITY,
    20
  ),

  // ==================== TERRA - EARTH DRAGON (3 pages) ====================
  
  /**
   * Page 8 - TERRA Part 1: The Unbreakable
   * Unlocked by using any immortal ability 3 times.
   */
  TERRA_1(
    8,
    "TERRA - The Dragon of Earth (Part I)",
    "TERRA, the Dragon of Earth, stood as the\n" +
    "unmovable object against which all things\n" +
    "broke. While IGNIS burned and ZEPHYR soared,\n" +
    "TERRA endured. Her scales were harder than\n" +
    "diamond, her bones the foundations of\n" +
    "continents. She embodied permanence itself.\n\n" +
    "In the Age of Titans, when gods warred across\n" +
    "the heavens, TERRA alone remained unscathed.\n" +
    "Mountains crumbled, seas boiled away, but\n" +
    "TERRA stood firm. She was the world's anchor,\n" +
    "the final bastion against chaos. Warriors\n" +
    "prayed for her resilience, knowing that what\n" +
    "TERRA protected, nothing could destroy.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.IMMORTAL,
    3
  ),

  /**
   * Page 9 - TERRA Part 2: The Eternal Defense
   * Unlocked by using any immortal ability 10 times.
   */
  TERRA_2(
    9,
    "TERRA - The Dragon of Earth (Part II)",
    "The Immortal Fragment grants TERRA's gift—\n" +
    "resilience beyond mortal understanding, the\n" +
    "power to withstand any assault. Draconic\n" +
    "Reflex channels her unbreakable defense,\n" +
    "turning flesh momentarily hard as stone,\n" +
    "absorbing blows that would fell armies.\n\n" +
    "History remembers guardians who held narrow\n" +
    "passes against impossible odds, their bodies\n" +
    "covered in wounds but never falling. They drew\n" +
    "strength from TERRA herself, becoming living\n" +
    "walls. One such champion held a bridge for six\n" +
    "days while civilians fled, his armor shattered\n" +
    "but his resolve mirror-hard as dragon scales.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.IMMORTAL,
    10
  ),

  /**
   * Page 10 - TERRA Part 3: Essence of Eternity
   * Unlocked by using any immortal ability 20 times.
   */
  TERRA_3(
    10,
    "TERRA - The Dragon of Earth (Part III)",
    "Essence Rebirth embodies TERRA's ultimate\n" +
    "nature—eternal endurance, the refusal to\n" +
    "surrender even to death itself. This rare\n" +
    "gift allows the worthy to rise again, restored\n" +
    "by the earth's own vitality, as TERRA once\n" +
    "rose from beneath shattered continents.\n\n" +
    "Ancient chronicles tell of paladins who fell\n" +
    "protecting the innocent, only to stand again\n" +
    "moments later, wreathed in golden light. Their\n" +
    "enemies fled in terror, for they faced not\n" +
    "mortals but TERRA's own champions, granted her\n" +
    "greatest blessing: the strength to endure\n" +
    "anything, to protect anything, forever.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.IMMORTAL,
    20
  ),

  // ==================== UMBRA - VOID DRAGON (3 pages) ====================
  
  /**
   * Page 11 - UMBRA Part 1: The Void Walker
   * Unlocked by using any corrupted ability 3 times.
   */
  UMBRA_1(
    11,
    "UMBRA - The Dragon of Void (Part I)",
    "UMBRA, the Dragon of Void, emerged from the\n" +
    "spaces between stars, the darkness between\n" +
    "heartbeats. Where the other dragons were\n" +
    "creation, UMBRA was entropy—beautiful,\n" +
    "terrifying, inevitable. His form flickered\n" +
    "between existence and nothingness.\n\n" +
    "Before UMBRA, the universe knew no endings.\n" +
    "He taught the world that all things must pass,\n" +
    "that light must eventually fade, that stars\n" +
    "themselves would one day grow cold. Yet in\n" +
    "this dissolution he found not despair but\n" +
    "necessity—for without death, there could be no\n" +
    "transformation, no rebirth, no true meaning.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.CORRUPTED,
    3
  ),

  /**
   * Page 12 - UMBRA Part 2: The Dread Aspect
   * Unlocked by using any corrupted ability 10 times.
   */
  UMBRA_2(
    12,
    "UMBRA - The Dragon of Void (Part II)",
    "The Corrupted Core grants UMBRA's dark gift—\n" +
    "power over void itself, the ability to drain\n" +
    "life and strike primal fear into mortal hearts.\n" +
    "Dread Gaze channels the terror of infinite\n" +
    "darkness, the horror of staring into the abyss\n" +
    "and finding it staring back, hungering.\n\n" +
    "War-chronicles speak in hushed tones of shadow\n" +
    "operatives who wielded this power, breaking\n" +
    "armies without bloodshed. Enemies fled before\n" +
    "them, minds shattered by visions of their own\n" +
    "dissolution, their mortality made suddenly,\n" +
    "terribly real. They faced not warriors but\n" +
    "UMBRA's heralds, harbingers of inevitable end.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.CORRUPTED,
    10
  ),

  /**
   * Page 13 - UMBRA Part 3: Consumption Eternal
   * Unlocked by using any corrupted ability 20 times.
   */
  UMBRA_3(
    13,
    "UMBRA - The Dragon of Void (Part III)",
    "Life Devourer represents UMBRA's essence in\n" +
    "its purest form—the consumption of vitality\n" +
    "itself, transformation of life energy into\n" +
    "power. This forbidden art drains the living,\n" +
    "converting their essence into the wielder's\n" +
    "strength, as UMBRA once fed on dying stars.\n\n" +
    "Only a handful ever mastered this terrible\n" +
    "gift, and fewer still used it wisely. For it\n" +
    "is seductive, this power—the rush of stolen\n" +
    "life, the feeling of invincibility as enemies\n" +
    "wither. Yet UMBRA's final lesson remains:\n" +
    "everything ends, even those who feast on\n" +
    "endings. Even the void must one day meet void.",
    UnlockTrigger.ABILITY_USE,
    FragmentType.CORRUPTED,
    20
  ),

  // ==================== THE FALL (3 pages) ====================
  
  /**
   * Page 14 - The Fall Part 1: Twilight of Dragons
   * Unlocked by equipping all 4 fragments at least once.
   */
  THE_FALL_1(
    14,
    "The Fall (Part I) - Twilight of Dragons",
    "But dragons are not immortal. In the Age of\n" +
    "Ending, the four elemental dragons faced their\n" +
    "greatest challenge—not from gods or titans,\n" +
    "but from the universe itself. The cosmic\n" +
    "balance they maintained began to fail, reality\n" +
    "fraying at the edges like old cloth.\n\n" +
    "IGNIS's flames grew unstable, consuming even\n" +
    "the air itself. ZEPHYR's winds became violent\n" +
    "storms that tore the sky. TERRA's mountains\n" +
    "cracked and crumbled. UMBRA's void expanded,\n" +
    "threatening to consume everything. The dragons\n" +
    "realized the terrible truth: their very\n" +
    "existence was destroying the world they loved.",
    UnlockTrigger.EQUIP_ALL_FRAGMENTS,
    null,
    0
  ),

  /**
   * Page 15 - The Fall Part 2: The Great Sacrifice
   * Unlocked by equipping all 4 fragments and using 10 abilities total.
   */
  THE_FALL_2(
    15,
    "The Fall (Part II) - The Great Sacrifice",
    "Faced with this choice—leave and let the world\n" +
    "die, or stay and destroy it themselves—the\n" +
    "four dragons chose a third path. They would\n" +
    "give up their physical forms, scatter their\n" +
    "power across the world as fragments, becoming\n" +
    "part of creation rather than apart from it.\n\n" +
    "The ritual took seven days and seven nights.\n" +
    "The sky burned red as IGNIS dissolved. Thunder\n" +
    "shook the world as ZEPHYR dispersed. Mountains\n" +
    "trembled as TERRA fragmented. Stars dimmed as\n" +
    "UMBRA scattered into void. When it ended, the\n" +
    "four dragons were gone—but the world endured,\n" +
    "saved by those who had once ruled it.",
    UnlockTrigger.EQUIP_ALL_FRAGMENTS,
    null,
    10
  ),

  /**
   * Page 16 - The Fall Part 3: Fragments Eternal
   * Unlocked by equipping all 4 fragments and using 25 abilities total.
   */
  THE_FALL_3(
    16,
    "The Fall (Part III) - Fragments Eternal",
    "Yet their essence endures. Fragments of their\n" +
    "power lie scattered across the world, waiting\n" +
    "for those worthy to claim them. Each fragment\n" +
    "carries not just power but memory—echoes of\n" +
    "what the dragons were, lessons they learned\n" +
    "across eons, wisdom born of cosmic perspective.\n\n" +
    "Those who gather these fragments and prove\n" +
    "their worth inherit more than abilities. They\n" +
    "become inheritors of a legacy spanning the\n" +
    "birth of stars to the heat death of galaxies.\n" +
    "They carry forward the will of beings who\n" +
    "loved this world enough to give everything\n" +
    "so it might continue. This is their gift.",
    UnlockTrigger.EQUIP_ALL_FRAGMENTS,
    null,
    25
  ),

  // ==================== RECOVERY/EPILOGUE (3 pages) ====================
  
  /**
   * Page 17 - Recovery Part 1: The Path Forward
   * Unlocked by using each ability type 10 times.
   */
  RECOVERY_1(
    17,
    "Recovery (Part I) - The Path Forward",
    "Only those who prove themselves worthy may\n" +
    "hope to wield the fragments of fallen dragons.\n" +
    "This worthiness is not measured in strength\n" +
    "alone, but in dedication, wisdom, and the\n" +
    "willingness to shoulder great responsibility.\n\n" +
    "The path begins with lightning—primal power\n" +
    "channeled through dragon eggs, teaching control\n" +
    "and precision. From there, one may seek the\n" +
    "fragments: fire's passion, wind's freedom,\n" +
    "earth's endurance, void's mystery. Each\n" +
    "fragment tests the wielder differently, each\n" +
    "demands different strengths. Together, they\n" +
    "form a complete understanding of power itself.",
    UnlockTrigger.MASTER_ALL_ABILITIES,
    null,
    0
  ),

  /**
   * Page 18 - Recovery Part 2: Masters of Dragons
   * Unlocked by using each ability type 25 times.
   */
  RECOVERY_2(
    18,
    "Recovery (Part II) - Masters of Dragons",
    "Through dedication and practice, one learns to\n" +
    "channel each fragment's full potential. This\n" +
    "mastery is not mere mechanical skill but deep\n" +
    "understanding—knowing when passion serves and\n" +
    "when it consumes, when freedom liberates and\n" +
    "when it abandons, when endurance preserves and\n" +
    "when it merely prolongs suffering.\n\n" +
    "True masters hear the dragons' whispers in\n" +
    "their fragments. IGNIS speaks of purpose, of\n" +
    "burning away what no longer serves. ZEPHYR\n" +
    "sings of possibilities, of paths not yet taken.\n" +
    "TERRA reminds of foundations, of what must be\n" +
    "protected. UMBRA counsels acceptance of endings,\n" +
    "teaching that all things transform in time.",
    UnlockTrigger.MASTER_ALL_ABILITIES,
    null,
    25
  ),

  /**
   * Page 19 - Recovery Part 3: Legacy Continued
   * Unlocked by using each ability type 50 times each.
   */
  RECOVERY_3(
    19,
    "Recovery (Part III) - Legacy Continued",
    "Master all abilities, balance all elements, and\n" +
    "you become more than a Dragon Wielder—you\n" +
    "become a Dragon Inheritor, carrying forward a\n" +
    "legacy that predates recorded history. You\n" +
    "hold in your hands the distilled essence of\n" +
    "four cosmic beings who shaped reality itself.\n\n" +
    "The responsibility is immense. The dragons gave\n" +
    "everything so the world might endure. Now you\n" +
    "carry their power, their wisdom, their eternal\n" +
    "question: What will you do with such gifts?\n" +
    "How will you honor their sacrifice? What mark\n" +
    "will you leave upon this world they loved\n" +
    "enough to die for? Choose wisely, Dragon's Heir.",
    UnlockTrigger.MASTER_ALL_ABILITIES,
    null,
    50
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
   * @return Page number (1-19)
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
   * @param pageNumber The page number (1-19)
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
     * Unlocked by mastering all abilities (using each 10+ times).
     */
    MASTER_ALL_ABILITIES
  }
}
