package org.cavarest.elementaldragon.fragment;

import org.cavarest.elementaldragon.ElementalDragon;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class providing common functionality for all fragments.
 * Handles cooldown management, particle effects, and sound effects.
 *
 * <p>Provides default implementations for interface methods where possible,
 * allowing subclasses to define only fragment-specific metadata.</p>
 *
 * <p>Implements Listener so that fragment event handlers (@EventHandler methods)
 * are automatically registered by FragmentRegistry.</p>
 */
public abstract class AbstractFragment implements Fragment, Listener {

  protected final ElementalDragon plugin;
  private final FragmentType type;
  private final long cooldownMillis;
  private final List<String> lore;

  /**
   * Create a new abstract fragment.
   *
   * @param plugin The plugin instance
   * @param type The fragment type
   * @param cooldownMillis Cooldown time in milliseconds
   * @param loreLines Lore description lines
   */
  protected AbstractFragment(
    ElementalDragon plugin,
    FragmentType type,
    long cooldownMillis,
    List<String> loreLines
  ) {
    this.plugin = plugin;
    this.type = type;
    this.cooldownMillis = cooldownMillis;
    this.lore = loreLines;
  }

  @Override
  public String getName() {
    return type.getDisplayName();
  }

  @Override
  public FragmentType getType() {
    return type;
  }

  @Override
  public long getCooldownMillis() {
    return cooldownMillis;
  }

  @Override
  public List<String> getLore() {
    return lore;
  }

  @Override
  public String getDescription() {
    return type.getDescription();
  }

  // ===== Default Implementations for New Interface Methods =====

  @Override
  public AbilityDefinition getAbility(int number) {
    return getAbilities().stream()
        .filter(a -> a.getNumber() == number)
        .findFirst()
        .orElse(null);
  }

  @Override
  public AbilityDefinition getAbilityByAlias(String alias) {
    return getAbilities().stream()
        .filter(a -> a.matchesAlias(alias))
        .findFirst()
        .orElse(null);
  }

  @Override
  public String getCommandName() {
    return type.getCanonicalName();
  }

  @Override
  public String getPermissionNode() {
    // Permission nodes use enum names for backward compatibility with plugin.yml
    return "elementaldragon.fragment." + getType().name().toLowerCase();
  }

  @Override
  public String getPassiveBonus() {
    return type.getPassiveBonus();
  }

  // ===== Fragment Lifecycle Methods =====

  @Override
  public void activate(Player player) {
    if (player == null) {
      return;
    }

    // Apply passive effects
    applyPassiveEffects(player);

    // Play activation sound
    playActivationSound(player);

    // Show activation particles
    showActivationParticles(player);

    // Send activation message
    player.sendMessage(
      org.bukkit.ChatColor.GOLD + getName() + " activated!",
      org.bukkit.ChatColor.GRAY + type.getPassiveBonus()
    );
  }

  @Override
  public void activate(Player player, int abilityNumber) {
    if (player == null) {
      return;
    }

    // Execute the specific ability
    executeAbility(player, abilityNumber);
  }

  @Override
  public void deactivate(Player player) {
    if (player == null) {
      return;
    }

    // Remove passive effects
    removePassiveEffects(player);

    // Play deactivation sound
    playDeactivationSound(player);

    // Show deactivation particles
    showDeactivationParticles(player);
  }

  /**
   * Apply fragment-specific passive effects to the player.
   * Override in subclasses to add type-specific effects.
   *
   * @param player The player to apply effects to
   */
  protected void applyPassiveEffects(Player player) {
    // Default: no passive effects
    // Subclasses should override
  }

  /**
   * Remove fragment-specific passive effects from the player.
   * Override in subclasses to remove type-specific effects.
   *
   * @param player The player to remove effects from
   */
  protected void removePassiveEffects(Player player) {
    // Default: no passive effects to remove
    // Subclasses should override
  }

  /**
   * Play activation sound for this fragment type.
   *
   * @param player The player
   */
  protected void playActivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.ENTITY_PLAYER_LEVELUP,
      1.0f,
      1.2f
    );
  }

  /**
   * Play deactivation sound for this fragment type.
   *
   * @param player The player
   */
  protected void playDeactivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.ENTITY_ITEM_BREAK,
      0.8f,
      0.8f
    );
  }

  /**
   * Show activation particle effects.
   *
   * @param player The player
   */
  protected void showActivationParticles(Player player) {
    Location location = player.getLocation().add(0, 1, 0);
    Color color = type.getColor();

    // Always use DUST particle when using DustOptions
    player.getWorld().spawnParticle(
      Particle.DUST,
      location,
      20,
      1.0,
      1.0,
      1.0,
      0.1,
      new Particle.DustOptions(color, 2.0f)
    );
  }

  /**
   * Show deactivation particle effects.
   *
   * @param player The player
   */
  protected void showDeactivationParticles(Player player) {
    Location location = player.getLocation().add(0, 1, 0);
    Color color = type.getColor();

    // Always use DUST particle when using DustOptions
    player.getWorld().spawnParticle(
      Particle.DUST,
      location,
      10,
      0.8,
      0.8,
      0.8,
      0.05,
      new Particle.DustOptions(color, 1.0f)
    );
  }

  /**
   * Show ability use particle effects.
   *
   * @param location The location to show effects at
   * @param count Number of particles
   */
  protected void showAbilityParticles(Location location, int count) {
    if (location == null) {
      return;
    }
    Color color = type.getColor();

    // Always use DUST particle when using DustOptions
    location.getWorld().spawnParticle(
      Particle.DUST,
      location,
      count,
      0.5,
      0.5,
      0.5,
      0.1,
      new Particle.DustOptions(color, 1.5f)
    );
  }

  /**
   * Play ability use sound.
   *
   * @param location The location to play sound at
   * @param sound The sound to play
   * @param volume Volume level
   * @param pitch Pitch level
   */
  protected void playAbilitySound(Location location, Sound sound, float volume, float pitch) {
    if (location == null || sound == null) {
      return;
    }
    location.getWorld().playSound(location, sound, volume, pitch);
  }

  /**
   * Apply a potion effect to the player.
   *
   * @param player The player
   * @param effectType The effect type
   * @param duration Duration in ticks
   * @param amplifier Effect amplifier
   * @param ambient Whether to use ambient particles
   */
  protected void applyPotionEffect(
    Player player,
    PotionEffectType effectType,
    int duration,
    int amplifier,
    boolean ambient
  ) {
    if (player == null || effectType == null) {
      return;
    }
    player.addPotionEffect(
      new PotionEffect(effectType, duration, amplifier, ambient)
    );
  }

  /**
   * Remove a specific potion effect from the player.
   *
   * @param player The player
   * @param effectType The effect type to remove
   */
  protected void removePotionEffect(Player player, PotionEffectType effectType) {
    if (player == null || effectType == null) {
      return;
    }
    player.removePotionEffect(effectType);
  }

  /**
   * Get the description for the abilities of this fragment type.
   * To be implemented by subclasses.
   *
   * @return Abilities description
   */
  protected abstract String getAbilitiesDescription();

  /**
   * Execute a specific ability for this fragment.
   * To be implemented by subclasses.
   *
   * @param player The player using the ability
   * @param abilityNumber The ability number (1 or 2)
   */
  protected abstract void executeAbility(Player player, int abilityNumber);

  /**
   * Create a standard lore list for fragments.
   *
   * @param abilitiesDescription Description of abilities
   * @return List of lore lines
   */
  protected static List<String> createStandardLore(String abilitiesDescription) {
    return Arrays.asList(
      abilitiesDescription,
      "",
      "Passive: See fragment details"
    );
  }
}
