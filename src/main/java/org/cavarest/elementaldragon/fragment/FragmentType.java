package org.cavarest.elementaldragon.fragment;

import org.bukkit.Color;
import org.bukkit.Particle;

/**
 * Enum representing the different types of elemental fragments.
 * Each fragment type has unique properties and visual effects.
 */
public enum FragmentType {

  /**
   * Fire-based fragment with offensive abilities.
   */
  BURNING(
    "Burning Fragment",
    "fire",
    new String[]{"fire"},
    Color.fromRGB(255, 100, 0),
    Particle.DUST,
    "Grants fire-based abilities: Dragon's Wrath and Infernal Dominion.",
    "50% fire resistance passive bonus."
  ),

  /**
   * Wind-based fragment with movement abilities.
   */
  AGILITY(
    "Agility Fragment",
    "wind",
    new String[]{"wind"},
    Color.fromRGB(100, 255, 200),
    Particle.CLOUD,
    "Grants movement abilities: Draconic Surge and Wing Burst.",
    "Permanent Speed I and water walking passive bonus."
  ),

  /**
   * Earth-based fragment with defensive abilities.
   */
  IMMORTAL(
    "Immortal Fragment",
    "earth",
    new String[]{"immortal"},
    Color.fromRGB(139, 69, 19),
    Particle.FALLING_DUST,
    "Grants defensive abilities: Draconic Reflex and Essence Rebirth.",
    "25% knockback reduction and +2 hearts passive bonus."
  ),

  /**
   * Void-based fragment with dark abilities.
   */
  CORRUPTED(
    "Corrupted Core",
    "void",
    new String[]{"corrupt"},
    Color.fromRGB(75, 0, 130),
    Particle.REVERSE_PORTAL,
    "Grants dark abilities: Dread Gaze and Life Devourer.",
    "Night Vision and creeper avoidance passive bonus."
  );

  private final String displayName;
  private final String element;
  private final String[] aliases;
  private final Color color;
  private final Particle particleType;
  private final String description;
  private final String passiveBonus;

  FragmentType(
    String displayName,
    String element,
    Color color,
    Particle particleType,
    String description,
    String passiveBonus
  ) {
    this.displayName = displayName;
    this.element = element;
    this.aliases = new String[]{element};
    this.color = color;
    this.particleType = particleType;
    this.description = description;
    this.passiveBonus = passiveBonus;
  }

  FragmentType(
    String displayName,
    String element,
    String[] aliases,
    Color color,
    Particle particleType,
    String description,
    String passiveBonus
  ) {
    this.displayName = displayName;
    this.element = element;
    this.aliases = aliases;
    this.color = color;
    this.particleType = particleType;
    this.description = description;
    this.passiveBonus = passiveBonus;
  }

  /**
   * Get the display name of the fragment type.
   *
   * @return Display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Get the primary element name (for internal use).
   *
   * @return Element name
   */
  public String getElement() {
    return element;
  }

  /**
   * Get all valid aliases for this fragment type.
   *
   * @return Array of valid aliases
   */
  public String[] getAliases() {
    return aliases;
  }

  /**
   * Check if a string matches this fragment type (case-insensitive).
   *
   * @param name The name to check
   * @return true if it matches the element name or any alias
   */
  public boolean matches(String name) {
    if (name == null) return false;
    String lower = name.toLowerCase();
    if (element.equalsIgnoreCase(lower)) return true;
    for (String alias : aliases) {
      if (alias.equalsIgnoreCase(lower)) return true;
    }
    return false;
  }

  /**
   * Get the particle color for this fragment type.
   *
   * @return Particle color
   */
  public Color getColor() {
    return color;
  }

  /**
   * Get the particle type for visual effects.
   *
   * @return Particle type
   */
  public Particle getParticleType() {
    return particleType;
  }

  /**
   * Get the fragment description.
   *
   * @return Description text
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get the passive bonus description.
   *
   * @return Passive bonus text
   */
  public String getPassiveBonus() {
    return passiveBonus;
  }

  /**
   * Get a formatted description with all details.
   *
   * @return Formatted description string
   */
  public String getFormattedDescription() {
    return String.format(
      "%s\n%s\n\nPassive: %s",
      description,
      getAbilitiesDescription(),
      passiveBonus
    );
  }

  /**
   * Get the abilities description for this fragment type.
   * To be overridden by specific implementations.
   *
   * @return Abilities description
   */
  protected String getAbilitiesDescription() {
    return "Active abilities available when equipped.";
  }

  /**
   * Get FragmentType from string name (case-insensitive).
   *
   * @param name The fragment type name
   * @return FragmentType enum value or null if not found
   */
  public static FragmentType fromName(String name) {
    if (name == null || name.trim().isEmpty()) {
      return null;
    }
    try {
      return FragmentType.valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      // Check for partial matches (e.g., "burning" matches BURNING)
      for (FragmentType type : values()) {
        if (type.name().startsWith(name.toUpperCase())) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * Get FragmentType from element kind string (e.g., "fire", "agile", "immortal").
   * Supports all aliases and element names.
   *
   * @param elementKind The element kind string
   * @return FragmentType enum value or null if not found
   */
  public static FragmentType fromElementKind(String elementKind) {
    if (elementKind == null || elementKind.trim().isEmpty()) {
      return null;
    }
    // Try exact match on enum name first
    try {
      return FragmentType.valueOf(elementKind.toUpperCase());
    } catch (IllegalArgumentException e) {
      // Check partial matches on enum name (e.g., "burning" matches BURNING)
      for (FragmentType type : values()) {
        if (type.name().startsWith(elementKind.toUpperCase())) {
          return type;
        }
      }
      // Check aliases and element names
      for (FragmentType type : values()) {
        if (type.matches(elementKind)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * Get all valid element kind names for tab completion.
   *
   * @return Array of valid element kind names
   */
  public static String[] getElementKindNames() {
    java.util.Set<String> names = new java.util.HashSet<>();
    for (FragmentType type : values()) {
      names.add(type.element);
      for (String alias : type.aliases) {
        names.add(alias);
      }
    }
    return names.toArray(new String[0]);
  }

  /**
   * Get all valid type names for tab completion.
   *
   * @return Array of valid type names
   */
  public static String[] getTypeNames() {
    String[] names = new String[values().length];
    for (int i = 0; i < values().length; i++) {
      names[i] = values()[i].name().toLowerCase();
    }
    return names;
  }

  /**
   * Get the canonical name for this fragment type.
   * Used for strict validation in admin commands.
   *
   * @return The canonical fragment name (fire, agile, immortal, corrupt)
   */
  public String getCanonicalName() {
    switch (this) {
      case BURNING: return "fire";
      case AGILITY: return "agile";
      case IMMORTAL: return "immortal";
      case CORRUPTED: return "corrupt";
      default: return this.name().toLowerCase();
    }
  }

  /**
   * Get only the canonical fragment names.
   * ONLY these 4 names should be accepted for /ed give equipment commands.
   *
   * @return Array of canonical names: fire, agile, immortal, corrupt
   */
  public static String[] getCanonicalNames() {
    return new String[]{"fire", "agile", "immortal", "corrupt"};
  }

  /**
   * Get FragmentType from canonical name only (strict validation).
   * Only accepts the 4 canonical names: fire, agile, immortal, corrupt.
   *
   * @param canonicalName The canonical name
   * @return FragmentType or null if not a canonical name
   */
  public static FragmentType fromCanonicalName(String canonicalName) {
    if (canonicalName == null || canonicalName.trim().isEmpty()) {
      return null;
    }
    String lower = canonicalName.toLowerCase();
    switch (lower) {
      case "fire": return BURNING;
      case "agile": return AGILITY;
      case "immortal": return IMMORTAL;
      case "corrupt": return CORRUPTED;
      default: return null;
    }
  }
}
