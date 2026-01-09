package org.cavarest.elementaldragon.audio;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentType;

/**
 * Manages all audio effects for the Elemental Dragon plugin.
 * Handles ability activation sounds, cooldown notifications, fragment discoveries,
 * achievements, and lightning strike effects.
 * 
 * <p>Sound Categories:</p>
 * <ul>
 *   <li>Ability Activation Sounds - Different sounds per fragment type</li>
 *   <li>Cooldown Ready Notification - Distinctive sound when cooldown expires</li>
 *   <li>Fragment Discovery - Sound when new lore is unlocked</li>
 *   <li>Achievement Unlock - Sound for achievements</li>
 *   <li>Lightning Strike - Purple thunder sound effect</li>
 * </ul>
 * 
 * <p>Fragment-Specific Sounds:</p>
 * <ul>
 *   <li>Burning Fragment - Fire-related sounds (fire ignition, explosion)</li>
 *   <li>Agility Fragment - Wind-related sounds (whoosh, flutter)</li>
 *   <li>Immortal Fragment - Earth-related sounds (rumble, crystal chime)</li>
 *   <li>Corrupted Core - Void-related sounds (whisper, darkness)</li>
 * </ul>
 */
public class SoundManager {

  private final ElementalDragon plugin;

  // Volume settings for different sound categories
  private static final float DEFAULT_VOLUME = 1.0f;
  private static final float ACHIEVEMENT_VOLUME = 1.5f;
  private static final float COOLDOWN_VOLUME = 0.8f;
  private static final float LIGHTNING_VOLUME = 2.0f;

  /**
   * Create a new SoundManager.
   *
   * @param plugin The plugin instance
   */
  public SoundManager(ElementalDragon plugin) {
    this.plugin = plugin;
  }

  /**
   * Play an ability activation sound for a player based on fragment type.
   *
   * @param player The player to play the sound for
   * @param fragmentType The fragment type being used
   * @param abilityNumber The ability number (1 or 2)
   */
  public void playAbilitySound(Player player, FragmentType fragmentType, int abilityNumber) {
    if (player == null) {
      return;
    }

    if (fragmentType == null) {
      // Default ability sound
      playDefaultAbilitySound(player);
      return;
    }

    Sound sound = getAbilitySoundForFragment(fragmentType, abilityNumber);
    float volume = getVolumeForFragment(fragmentType);
    float pitch = getPitchForAbility(abilityNumber);

    player.playSound(player.getLocation(), sound, volume, pitch);
  }

  /**
   * Play the cooldown ready notification sound for a player.
   * Distinctive sound that alerts the player their ability is ready.
   *
   * @param player The player to play the sound for
   */
  public void playCooldownReadySound(Player player) {
    if (player == null) {
      return;
    }

    // Play a distinctive two-tone notification
    player.playSound(
      player.getLocation(),
      Sound.BLOCK_NOTE_BLOCK_CHIME,
      COOLDOWN_VOLUME,
      1.2f
    );

    // Play second note after a short delay
    player.playSound(
      player.getLocation(),
      Sound.BLOCK_NOTE_BLOCK_BELL,
      COOLDOWN_VOLUME,
      1.5f
    );
  }

  /**
   * Play a fragment discovery sound for a player.
   * Played when the player unlocks new lore.
   *
   * @param player The player to play the sound for
   */
  public void playFragmentDiscoverySound(Player player) {
    if (player == null) {
      return;
    }

    // Magical chime sound for discovery
    player.playSound(
      player.getLocation(),
      Sound.ENTITY_PLAYER_LEVELUP,
      ACHIEVEMENT_VOLUME,
      1.2f
    );
  }

  /**
   * Play an achievement unlock sound for a player.
   *
   * @param player The player to play the sound for
   */
  public void playAchievementSound(Player player) {
    if (player == null) {
      return;
    }

    // Celebratory sound for achievements
    player.playSound(
      player.getLocation(),
      Sound.UI_TOAST_CHALLENGE_COMPLETE,
      ACHIEVEMENT_VOLUME,
      1.0f
    );

    // Additional triumphant flourish
    player.playSound(
      player.getLocation(),
      Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
      ACHIEVEMENT_VOLUME * 0.5f,
      1.3f
    );
  }

  /**
   * Play a lightning strike sound at a specific location.
   * Purple-themed thunder effect.
   *
   * @param location The location to play the sound at
   */
  public void playLightningSound(Location location) {
    if (location == null) {
      return;
    }

    // Main thunder sound
    location.getWorld().playSound(
      location,
      Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
      LIGHTNING_VOLUME,
      0.8f
    );

    // Additional impact sound
    location.getWorld().playSound(
      location,
      Sound.ENTITY_GENERIC_EXPLODE,
      LIGHTNING_VOLUME * 0.5f,
      1.0f
    );
  }

  /**
   * Play a sound when a fragment is equipped.
   *
   * @param player The player equipping the fragment
   * @param fragmentType The fragment type being equipped
   */
  public void playFragmentEquipSound(Player player, FragmentType fragmentType) {
    if (player == null) {
      return;
    }

    Sound equipSound = getEquipSoundForFragment(fragmentType);
    player.playSound(player.getLocation(), equipSound, DEFAULT_VOLUME, 1.0f);
  }

  /**
   * Play a sound when an ability fails (e.g., no targets).
   *
   * @param player The player attempting the ability
   */
  public void playAbilityFailedSound(Player player) {
    if (player == null) {
      return;
    }

    player.playSound(
      player.getLocation(),
      Sound.ENTITY_VILLAGER_NO,
      DEFAULT_VOLUME,
      0.8f
    );
  }

  /**
   * Get the appropriate ability sound for a fragment type and ability number.
   *
   * @param fragmentType The fragment type
   * @param abilityNumber The ability number (1 or 2)
   * @return The sound to play
   */
  private Sound getAbilitySoundForFragment(FragmentType fragmentType, int abilityNumber) {
    switch (fragmentType) {
      case BURNING:
        // Fire ignition sound for ability 1, explosion for ability 2
        return abilityNumber == 1 ? Sound.ITEM_FIRECHARGE_USE : Sound.ENTITY_GENERIC_EXPLODE;

      case AGILITY:
        // Wind whoosh for ability 1, flutter for ability 2
        return abilityNumber == 1 ? Sound.ENTITY_PHANTOM_FLAP : Sound.ENTITY_BAT_TAKEOFF;

      case IMMORTAL:
        // Earth rumble for ability 1, crystal chime for ability 2
        return abilityNumber == 1 ? Sound.BLOCK_ANCIENT_DEBRIS_PLACE : Sound.BLOCK_AMETHYST_CLUSTER_PLACE;

      case CORRUPTED:
        // Void whisper for ability 1, darkness sound for ability 2
        return abilityNumber == 1 ? Sound.ENTITY_ENDERMAN_AMBIENT : Sound.ENTITY_PHANTOM_AMBIENT;

      default:
        return Sound.ENTITY_PLAYER_ATTACK_SWEEP;
    }
  }

  /**
   * Get the volume setting for a fragment type.
   *
   * @param fragmentType The fragment type
   * @return The volume level
   */
  private float getVolumeForFragment(FragmentType fragmentType) {
    switch (fragmentType) {
      case BURNING:
        return 1.2f; // Louder fire/explosion sounds
      case CORRUPTED:
        return 0.7f; // Softer, mysterious sounds
      default:
        return DEFAULT_VOLUME;
    }
  }

  /**
   * Get the pitch for an ability number.
   * Different abilities have slightly different pitches for variety.
   *
   * @param abilityNumber The ability number (1 or 2)
   * @return The pitch value
   */
  private float getPitchForAbility(int abilityNumber) {
    // Ability 1 has slightly lower pitch, ability 2 has higher pitch
    return abilityNumber == 1 ? 0.9f : 1.1f;
  }

  /**
   * Get the equip sound for a fragment type.
   *
   * @param fragmentType The fragment type
   * @return The sound to play on equip
   */
  private Sound getEquipSoundForFragment(FragmentType fragmentType) {
    switch (fragmentType) {
      case BURNING:
        return Sound.ITEM_FIRECHARGE_USE;
      case AGILITY:
        return Sound.ENTITY_BAT_TAKEOFF;
      case IMMORTAL:
        return Sound.BLOCK_STONE_PLACE;
      case CORRUPTED:
        return Sound.ENTITY_ENDERMAN_TELEPORT;
      default:
        return Sound.ITEM_ARMOR_EQUIP_GENERIC;
    }
  }

  /**
   * Play a default ability sound when no fragment is specified.
   *
   * @param player The player
   */
  private void playDefaultAbilitySound(Player player) {
    player.playSound(
      player.getLocation(),
      Sound.ENTITY_PLAYER_ATTACK_SWEEP,
      DEFAULT_VOLUME,
      1.0f
    );
  }

  /**
   * Play a sound when crafting is complete.
   *
   * @param player The player completing crafting
   */
  public void playCraftingCompleteSound(Player player) {
    if (player == null) {
      return;
    }

    player.playSound(
      player.getLocation(),
      Sound.BLOCK_ANVIL_USE,
      DEFAULT_VOLUME,
      1.2f
    );
  }

  /**
   * Play a sound when the chronicle is opened.
   *
   * @param player The player opening the chronicle
   */
  public void playChronicleOpenSound(Player player) {
    if (player == null) {
      return;
    }

    player.playSound(
      player.getLocation(),
      Sound.ITEM_BOOK_PAGE_TURN,
      DEFAULT_VOLUME,
      1.0f
    );
  }
}
