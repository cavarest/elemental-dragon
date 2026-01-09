package org.cavarest.elementaldragon.fragment;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.visual.ParticleFX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

/**
 * Agility Fragment implementation providing wind-based movement abilities.
 *
 * Active Abilities:
 * - Draconic Surge: Grants major speed boost, jump enhancement, and water walking
 * - Wing Burst: Launches player in facing direction with rapid ascent
 *
 * Passive Bonus: Permanent Speed I when fragment equipped
 */
public class AgilityFragment extends AbstractFragment implements Listener {

  // Draconic Surge constants
  private static final long DRACONIC_SURGE_COOLDOWN = 30000L; // 30 seconds
  private static final int DRACONIC_SURGE_DURATION = 200; // 10 seconds (200 ticks)
  private static final int DRACONIC_SURGE_SPEED_LEVEL = 1; // Speed II (amplifier 1)
  private static final int DRACONIC_SURGE_JUMP_LEVEL = 1; // Jump Boost II (amplifier 1)

  // Wing Burst constants
  private static final long WING_BURST_COOLDOWN = 45000L; // 45 seconds
  private static final double WING_BURST_VERTICAL_FORCE = 1.5; // Vertical launch strength
  private static final double WING_BURST_FORWARD_FORCE = 0.5; // Forward momentum
  private static final int WING_BURST_FALL_SLOW_DURATION = 60; // 3 seconds (60 ticks)
  private static final int WING_BURST_DURATION = 40; // Duration for levitation effect (40 ticks = 2 seconds)
  private static final int WING_BURST_AMPLIFIER = 1; // Levitation amplifier

  // Visual constants
  private static final Color TEAL_COLOR = Color.fromRGB(100, 255, 200);
  private static final Color WHITE_COLOR = Color.fromRGB(255, 255, 255);

  // Fragment metadata (Single Source of Truth)
  private static final Material MATERIAL = Material.FEATHER;
  private static final NamedTextColor THEME_COLOR = NamedTextColor.AQUA;
  private static final String ELEMENT_NAME = "WIND";

  // Ability definitions (Single Source of Truth)
  private final List<AbilityDefinition> abilities = List.of(
    new AbilityDefinition(1, "Draconic Surge", "speed boost",
      List.of("surge", "draconic-surge"),
      "Speed courses through your veins like the wind!"),
    new AbilityDefinition(2, "Wing Burst", "levitation jump",
      List.of("burst", "wing-burst"),
      "You leap into the air with the power of dragon wings!")
  );

  private final ElementalDragon plugin;

  /**
   * Create a new Agility Fragment.
   *
   * @param plugin The plugin instance
   */
  public AgilityFragment(ElementalDragon plugin) {
    super(
      plugin,
      FragmentType.AGILITY,
      DRACONIC_SURGE_COOLDOWN, // Use Draconic Surge cooldown as default
      Arrays.asList(
        "Ability 1: Draconic Surge - Speed II + Jump II (30s cooldown)",
        "Ability 2: Wing Burst - Vertical launch (45s cooldown)",
        "",
        "Passive: Permanent Speed I"
      )
    );
    this.plugin = plugin;
  }

  // ===== Single Source of Truth Methods =====

  @Override
  public Material getMaterial() {
    return MATERIAL;
  }

  @Override
  public NamedTextColor getThemeColor() {
    return THEME_COLOR;
  }

  @Override
  public List<AbilityDefinition> getAbilities() {
    return abilities;
  }

  @Override
  public String getElementName() {
    return ELEMENT_NAME;
  }

  // ===== Fragment Implementation =====

  @Override
  protected String getAbilitiesDescription() {
    return "Active Abilities:\n" +
      "1. Draconic Surge - Speed II + Jump II + water walking (30s cooldown)\n" +
      "2. Wing Burst - Vertical launch with fall protection (45s cooldown)\n" +
      "Passive: Permanent Speed I when equipped";
  }

  @Override
  public void activate(Player player) {
    if (player == null) {
      return;
    }

    // Check permission
    if (!player.hasPermission("elementaldragon.fragment.agility")) {
      player.sendMessage(
        Component.text("You do not have permission to use the Agility Fragment!",
          NamedTextColor.RED)
      );
      return;
    }

    // Apply passive effects (permanent Speed I)
    applyPassiveEffects(player);

    // Play activation sound
    playActivationSound(player);

    // Show activation particles
    showActivationParticles(player);

    // Send activation message
    player.sendMessage(
      Component.text(getName() + " activated!", NamedTextColor.GOLD)
    );
    player.sendMessage(
      Component.text("Passive: Speed I granted!", NamedTextColor.GRAY)
    );
    player.sendMessage(
      Component.text("Use /agile 1 or /agile 2 to use abilities",
        NamedTextColor.AQUA)
    );
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
   * Execute a specific ability.
   *
   * @param player The player executing the ability
   * @param abilityNumber The ability number (1 or 2)
   */
  @Override
  protected void executeAbility(Player player, int abilityNumber) {
    if (player == null) {
      return;
    }

    // Check permission
    if (!player.hasPermission("elementaldragon.fragment.agility")) {
      player.sendMessage(
        Component.text("You do not have permission to use Agility Fragment abilities!",
          NamedTextColor.RED)
      );
      return;
    }

    switch (abilityNumber) {
      case 1:
        executeDraconicSurge(player);
        break;
      case 2:
        executeWingBurst(player);
        break;
      default:
        player.sendMessage(
          Component.text("Unknown ability number: " + abilityNumber,
            NamedTextColor.RED)
        );
        player.sendMessage(
          Component.text("Use /agile 1 or /agile 2", NamedTextColor.GRAY)
        );
    }
  }

  /**
   * Execute Draconic Surge - grants speed boost and jump enhancement.
   *
   * @param player The player executing the ability
   */
  private void executeDraconicSurge(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location center = player.getLocation();

    // Apply speed and jump boost
    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
      (int) (DRACONIC_SURGE_DURATION / 50), // Convert ticks to duration
      DRACONIC_SURGE_SPEED_LEVEL,
      false, // No particles
      true   // Show ambient particles
    ));

    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,
      (int) (DRACONIC_SURGE_DURATION / 50),
      DRACONIC_SURGE_JUMP_LEVEL,
      false,
      true
    ));

    // Cooldown is set by FragmentManager.useFragmentAbility()

    player.sendMessage(
      Component.text("Draconic Surge activated! Speed and jump boost granted!",
        NamedTextColor.GREEN)
    );

    // Play ability sound
    playAbilitySound(
      player.getLocation(),
      Sound.ENTITY_PHANTOM_FLAP,
      1.0f,
      1.0f
    );

    // Show particles - use CLOUD directly (doesn't require DustOptions)
    player.getWorld().spawnParticle(
      Particle.CLOUD,
      player.getLocation().add(0, 1, 0),
      15,
      0.5,
      0.5,
      0.5,
      0.05
    );

    // Visual indicator - spawn temporary colored particles
    spawnSurgeParticles(player);
  }

  /**
   * Execute Wing Burst - launch player upward in direction they are facing.
   *
   * @param player The player executing the ability
   */
  private void executeWingBurst(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location playerLocation = player.getLocation();

    // Play launch sound
    playAbilitySound(playerLocation, Sound.ENTITY_PHANTOM_FLAP, 2.0f, 1.5f);

    // Calculate launch velocity
    Vector direction = player.getLocation().getDirection().normalize();
    Vector velocity = new Vector(
      direction.getX() * WING_BURST_FORWARD_FORCE,
      WING_BURST_VERTICAL_FORCE,
      direction.getZ() * WING_BURST_FORWARD_FORCE
    );

    // Apply velocity to player
    player.setVelocity(velocity);

    // Apply levitation effect for upward movement
    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,
      (int) (WING_BURST_DURATION / 50),
      WING_BURST_AMPLIFIER,
      false,
      true
    ));

    // Show wing burst particles
    showWingBurstParticles(playerLocation);

    // Show trail particles during flight
    showFlightTrailParticles(player);

    // Schedule fall slow after 3 seconds
    new BukkitRunnable() {
      @Override
      public void run() {
        if (player.isDead() || !player.isValid()) {
          cancel();
          return;
        }

        // Apply slow fall to prevent fall damage
        player.addPotionEffect(
          new PotionEffect(
            PotionEffectType.SLOW_FALLING,
            WING_BURST_FALL_SLOW_DURATION,
            0,
            false,
            false,
            false
          )
        );

        // Play landing preparation sound
        playAbilitySound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.8f, 1.2f);
      }
    }.runTaskLater(plugin, 60L); // 3 seconds (60 ticks) after launch

    // Cooldown is set by FragmentManager.useFragmentAbility()

    player.sendMessage(
      Component.text("Wing Burst activated! You soar into the air!", NamedTextColor.GREEN)
    );

    // Play ability sound
    playAbilitySound(
      player.getLocation(),
      Sound.ENTITY_PARROT_FLY,
      1.0f,
      1.2f
    );

    // Visual effect - wing particles (use CLOUD directly)
    player.getWorld().spawnParticle(
      Particle.CLOUD,
      player.getLocation().add(0, 1, 0),
      15,
      0.5,
      0.5,
      0.5,
      0.05
    );
  }

  /**
   * Show wind trail particles during Draconic Surge.
   *
   * @param player The player
   */
  private void showWindTrailParticles(Player player) {
    new BukkitRunnable() {
      private int ticks = 0;

      @Override
      public void run() {
        if (ticks >= DRACONIC_SURGE_DURATION) {
          cancel();
          return;
        }

        if (player.isDead() || !player.isValid()) {
          cancel();
          return;
        }

        ParticleFX.spawnSpeedTrail(player.getLocation().add(0, 0.5, 0));

        ticks += 5;
      }
    }.runTaskTimer(plugin, 0L, 5L);
  }

  /**
   * Show wing burst particles at launch location.
   *
   * @param location The launch location
   */
  private void showWingBurstParticles(Location location) {
    ParticleFX.spawnWingBurstParticles(location);
  }

  /**
   * Show trail particles during flight.
   *
   * @param player The player
   */
  private void showFlightTrailParticles(Player player) {
    new BukkitRunnable() {
      private int ticks = 0;
      private final int maxTicks = 40; // Track for ~2 seconds

      @Override
      public void run() {
        if (ticks >= maxTicks || player.isDead() || !player.isValid()) {
          cancel();
          return;
        }

        Location location = player.getLocation();
        ParticleFX.spawnFlightTrail(
          location,
          player.getLocation().getDirection().getX(),
          player.getLocation().getDirection().getZ()
        );

        ticks++;
      }
    }.runTaskTimer(plugin, 0L, 1L);
  }

  /**
   * Apply passive Speed I effect.
   *
   * @param player The player
   */
  @Override
  protected void applyPassiveEffects(Player player) {
    // Apply SPEED potion effect (amplifier 0 = Speed I)
    player.addPotionEffect(
      new PotionEffect(
        PotionEffectType.SPEED,
        Integer.MAX_VALUE, // Permanent while equipped
        0, // Amplifier 0 = Speed I
        false, // Not ambient
        true, // Show particles
        true // Show icon
      )
    );

    // Show passive effect particles
    player.getWorld().spawnParticle(
      Particle.CLOUD,
      player.getLocation().add(0, 1, 0),
      5,
      0.3,
      0.3,
      0.3,
      0.02
    );
  }

  /**
   * Remove passive Speed I effect only.
   * Note: We only remove Speed effect, not the surge effects (those expire naturally).
   *
   * @param player The player
   */
  @Override
  protected void removePassiveEffects(Player player) {
    player.removePotionEffect(PotionEffectType.SPEED);
  }

  /**
   * Play activation sound for agility fragment.
   *
   * @param player The player
   */
  @Override
  protected void playActivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.ENTITY_PHANTOM_FLAP,
      1.0f,
      1.2f
    );
    player.getWorld().playSound(
      location,
      Sound.ENTITY_BAT_TAKEOFF,
      0.8f,
      1.4f
    );
  }

  /**
   * Play deactivation sound for agility fragment.
   *
   * @param player The player
   */
  @Override
  protected void playDeactivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.ENTITY_BAT_DEATH,
      0.6f,
      0.8f
    );
  }

  /**
   * Show activation particles for agility fragment.
   *
   * @param player The player
   */
  @Override
  protected void showActivationParticles(Player player) {
    ParticleFX.spawnAgilityActivation(player.getLocation().add(0, 1, 0));
  }

  /**
   * Event handler for fall damage reduction during Wing Burst.
   * This provides additional fall protection alongside the slow falling effect.
   */
  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();

    // Check if this is fall damage
    if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
      // Check if player has slow falling (from Wing Burst)
      if (player.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
        // Reduce fall damage significantly
        event.setDamage(event.getDamage() * 0.2);
      }
    }
  }

  /**
   * Spawn surge particles around the player.
   *
   * @param player The player
   */
  private void spawnSurgeParticles(Player player) {
    Location location = player.getLocation();
    Color color = Color.fromRGB(0, 255, 255); // Cyan for agility

    // Create spiral of particles
    for (int i = 0; i < 20; i++) {
      double angle = (i / 20.0) * 2 * Math.PI;
      double radius = 0.5 + (i * 0.05);
      double x = location.getX() + Math.cos(angle) * radius;
      double z = location.getZ() + Math.sin(angle) * radius;
      double y = location.getY() + 1 + (i * 0.05);

      Location particleLoc = new Location(location.getWorld(), x, y, z);
      location.getWorld().spawnParticle(
        Particle.DUST,
        particleLoc,
        3,
        0.1,
        0.1,
        0.1,
        0.1,
        new Particle.DustOptions(color, 1.0f)
      );
    }
  }

  /**
   * Get the plugin instance.
   *
   * @return The plugin
   */
  public ElementalDragon getPlugin() {
    return plugin;
  }
}
