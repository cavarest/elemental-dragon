package org.cavarest.elementaldragon.visual;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/**
 * Centralized particle effects helper class for the Elemental Dragon plugin.
 * Provides reusable particle effect methods for all fragments and abilities.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Fragment-specific color constants</li>
 *   <li>Reusable particle spawning methods</li>
 *   <li>Null-safe implementations</li>
 *   <li>Extensible design for future particle effects</li>
 * </ul>
 *
 * <p>Fragment Color Scheme:</p>
 * <ul>
 *   <li>Burning Fragment: Orange/Red (255, 100, 0)</li>
 *   <li>Agility Fragment: Teal/White (100, 255, 200)</li>
 *   <li>Immortal Fragment: Gold/Brown/Green (255, 215, 0)</li>
 *   <li>Corrupted Core: Purple/Dark (75, 0, 130)</li>
 *   <li>Lightning Ability: Purple (128, 0, 128)</li>
 * </ul>
 */
public final class ParticleFX {

  // ==========================================================================
  // BURNING FRAGMENT COLORS
  // ==========================================================================

  /** Fire orange color for flame particles */
  public static final Color FIRE_ORANGE = Color.fromRGB(255, 100, 0);

  /** Bright orange color for fireball trails */
  public static final Color FIRE_BRIGHT = Color.fromRGB(255, 165, 0);

  /** Red color for explosion effects */
  public static final Color FIRE_RED = Color.fromRGB(255, 50, 0);

  /** Lava pop color for infernal effects */
  public static final Color LAVA_COLOR = Color.fromRGB(200, 50, 0);

  // ==========================================================================
  // AGILITY FRAGMENT COLORS
  // ==========================================================================

  /** Teal color for wind effects */
  public static final Color TEAL_COLOR = Color.fromRGB(100, 255, 200);

  /** White color for feather/speed particles */
  public static final Color SPEED_WHITE = Color.fromRGB(255, 255, 255);

  /** Light blue for air currents */
  public static final Color AIR_BLUE = Color.fromRGB(200, 240, 255);

  // ==========================================================================
  // IMMORTAL FRAGMENT COLORS
  // ==========================================================================

  /** Gold color for shield/strength effects */
  public static final Color GOLD_SHIELD = Color.fromRGB(255, 215, 0);

  /** Brown color for earth/stone effects */
  public static final Color EARTH_BROWN = Color.fromRGB(139, 69, 19);

  /** Green color for life/health effects */
  public static final Color LIFE_GREEN = Color.fromRGB(34, 139, 34);

  /** Copper color for metallic sounds */
  public static final Color COPPER_COLOR = Color.fromRGB(184, 115, 51);

  // ==========================================================================
  // CORRUPTED CORE COLORS
  // ==========================================================================

  /** Dark purple for void effects */
  public static final Color VOID_PURPLE = Color.fromRGB(75, 0, 130);

  /** Deep purple for dark energy */
  public static final Color DARK_PURPLE = Color.fromRGB(128, 0, 128);

  /** Black/purple for void particles */
  public static final Color VOID_BLACK = Color.fromRGB(30, 0, 50);

  // ==========================================================================
  // LIGHTNING ABILITY COLORS
  // ==========================================================================

  /** Main purple lightning color - matches VOID_PURPLE for visual consistency */
  public static final Color LIGHTNING_PURPLE = Color.fromRGB(75, 0, 130);

  /** Bright purple for spark effects */
  public static final Color SPARK_PURPLE = Color.fromRGB(255, 0, 255);

  // ==========================================================================
  // PRIVATE CONSTRUCTOR - UTILITY CLASS
  // ==========================================================================

  private ParticleFX() {
    // Utility class - prevent instantiation
  }

  // ==========================================================================
  // BURNING FRAGMENT EFFECTS
  // ==========================================================================

  /**
   * Spawn fireball trail particles - orange/red flame trail behind fireballs.
   *
   * @param location The location to spawn particles at
   * @param count Number of particles per spawn
   */
  public static void spawnFireballTrail(Location location, int count) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Main flame particles
    world.spawnParticle(
      Particle.FLAME,
      location,
      count,
      0.2,
      0.2,
      0.2,
      0.05
    );

    // Orange dust particles
    world.spawnParticle(
      Particle.DUST,
      location,
      count / 2,
      0.15,
      0.15,
      0.15,
      0.03,
      new Particle.DustOptions(FIRE_BRIGHT, 1.0f)
    );
  }

  /**
   * Spawn enhanced fire ring particles for Infernal Dominion.
   *
   * @param location The center location of the ring
   * @param radius The radius of the ring
   */
  public static void spawnFireRingParticles(Location location, double radius) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Create ring of flame particles
    for (int i = 0; i < 360; i += 15) {
      double angle = Math.toRadians(i);
      double x = location.getX() + (radius * Math.cos(angle));
      double z = location.getZ() + (radius * Math.sin(angle));
      Location particleLocation = new Location(world, x, location.getY() + 0.1, z);

      // Flame particles
      world.spawnParticle(
        Particle.FLAME,
        particleLocation,
        3,
        0.2,
        0.2,
        0.2,
        0.05
      );

      // Smoke rise effect
      world.spawnParticle(
        Particle.SMOKE,
        particleLocation.add(0, 0.5, 0),
        2,
        0.1,
        0.1,
        0.1,
        0.02
      );

      // Lava pop particles
      world.spawnParticle(
        Particle.DUST,
        particleLocation,
        2,
        0.15,
        0.15,
        0.15,
        0.02,
        new Particle.DustOptions(LAVA_COLOR, 1.0f)
      );
    }
  }

  /**
   * Spawn activation burst for Burning Fragment.
   *
   * @param location The location to spawn particles at
   */
  public static void spawnBurningActivation(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Fire particles burst
    world.spawnParticle(
      Particle.FLAME,
      location,
      20,
      1.0,
      1.0,
      1.0,
      0.1
    );

    // Orange dust particles
    world.spawnParticle(
      Particle.DUST,
      location,
      15,
      1.0,
      1.0,
      1.0,
      0.1,
      new Particle.DustOptions(FIRE_ORANGE, 2.0f)
    );

    // Smoke particles
    world.spawnParticle(
      Particle.SMOKE,
      location,
      10,
      0.8,
      0.8,
      0.8,
      0.05
    );
  }

  // ==========================================================================
  // AGILITY FRAGMENT EFFECTS
  // ==========================================================================

  /**
   * Spawn speed trail particles for Draconic Surge.
   *
   * @param location The location to spawn particles at
   */
  public static void spawnSpeedTrail(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Teal dust particles
    world.spawnParticle(
      Particle.DUST,
      location,
      3,
      0.5,
      0.5,
      0.5,
      0.1,
      new Particle.DustOptions(TEAL_COLOR, 1.5f)
    );

    // Cloud particles for wind effect
    world.spawnParticle(
      Particle.CLOUD,
      location,
      2,
      0.3,
      0.3,
      0.3,
      0.05
    );

    // White swirl particles
    world.spawnParticle(
      Particle.DUST,
      location,
      1,
      0.2,
      0.2,
      0.2,
      0.05,
      new Particle.DustOptions(SPEED_WHITE, 1.0f)
    );
  }

  /**
   * Spawn feather particles for Wing Burst.
   *
   * @param location The launch location
   */
  public static void spawnWingBurstParticles(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Burst of white particles
    world.spawnParticle(
      Particle.DUST,
      location.add(0, 1, 0),
      30,
      1.0,
      1.0,
      1.0,
      0.15,
      new Particle.DustOptions(SPEED_WHITE, 2.0f)
    );

    // Cloud particles spreading outward
    for (int i = 0; i < 360; i += 30) {
      double angle = Math.toRadians(i);
      double x = Math.cos(angle) * 1.5;
      double z = Math.sin(angle) * 1.5;

      Location particleLocation = location.clone().add(x, 0.5, z);
      world.spawnParticle(
        Particle.CLOUD,
        particleLocation,
        5,
        0.3,
        0.3,
        0.3,
        0.08
      );
    }

    // Teal ring particles
    world.spawnParticle(
      Particle.DUST,
      location.add(0, 0.5, 0),
      15,
      1.2,
      0.5,
      1.2,
      0.1,
      new Particle.DustOptions(TEAL_COLOR, 1.5f)
    );
  }

  /**
   * Spawn flight trail particles during Wing Burst.
   *
   * @param location The current player location
   * @param directionX Player direction X
   * @param directionZ Player direction Z
   */
  public static void spawnFlightTrail(Location location, double directionX, double directionZ) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // White trail particles
    world.spawnParticle(
      Particle.DUST,
      location.add(0, 0.5, 0),
      2,
      0.3,
      0.3,
      0.3,
      0.05,
      new Particle.DustOptions(SPEED_WHITE, 1.0f)
    );

    // Cloud particles behind player
    world.spawnParticle(
      Particle.CLOUD,
      location.add(-directionX * 0.5, 0, -directionZ * 0.5),
      3,
      0.2,
      0.2,
      0.2,
      0.03
    );
  }

  /**
   * Spawn activation burst for Agility Fragment.
   *
   * @param location The location to spawn particles at
   */
  public static void spawnAgilityActivation(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Teal dust particles burst
    world.spawnParticle(
      Particle.DUST,
      location,
      20,
      1.0,
      1.0,
      1.0,
      0.1,
      new Particle.DustOptions(TEAL_COLOR, 2.0f)
    );

    // White swirl particles
    world.spawnParticle(
      Particle.DUST,
      location,
      10,
      1.0,
      1.0,
      1.0,
      0.05,
      new Particle.DustOptions(SPEED_WHITE, 1.5f)
    );

    // Cloud particles
    world.spawnParticle(
      Particle.CLOUD,
      location,
      15,
      0.8,
      0.8,
      0.8,
      0.05
    );
  }

  // ==========================================================================
  // IMMORTAL FRAGMENT EFFECTS
  // ==========================================================================

  /**
   * Spawn shield aura particles for Draconic Reflex.
   *
   * @param location The player location
   */
  public static void spawnShieldAura(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Shield ring particles (gold/brown)
    world.spawnParticle(
      Particle.DUST,
      location.add(0, 1, 0),
      3,
      0.8,
      0.8,
      0.8,
      0.05,
      new Particle.DustOptions(GOLD_SHIELD, 1.5f)
    );

    // Brown dust particles for earth effect - use DUST instead of FALLING_DUST
    world.spawnParticle(
      Particle.DUST,
      location,
      2,
      0.6,
      0.6,
      0.6,
      0.02,
      new Particle.DustOptions(EARTH_BROWN, 1.0f)
    );

    // Green particles for roots
    world.spawnParticle(
      Particle.DUST,
      location,
      1,
      0.5,
      0.1,
      0.5,
      0.02,
      new Particle.DustOptions(LIFE_GREEN, 1.0f)
    );
  }

  /**
   * Spawn sparkle particles for Essence Rebirth.
   *
   * @param location The respawn location
   */
  public static void spawnRebirthSparkles(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Gold burst particles
    world.spawnParticle(
      Particle.DUST,
      location.add(0, 1, 0),
      30,
      1.0,
      1.0,
      1.0,
      0.1,
      new Particle.DustOptions(GOLD_SHIELD, 2.0f)
    );

    // Green particles in a ring (life/renewal)
    for (int i = 0; i < 360; i += 30) {
      double angle = Math.toRadians(i);
      double x = Math.cos(angle) * 1.5;
      double z = Math.sin(angle) * 1.5;

      Location particleLocation = location.clone().add(x, 0.5, z);
      world.spawnParticle(
        Particle.DUST,
        particleLocation,
        3,
        0.3,
        0.3,
        0.3,
        0.05,
        new Particle.DustOptions(LIFE_GREEN, 1.5f)
      );
    }

    // Sparkle particles
    world.spawnParticle(
      Particle.CRIT,
      location.add(0, 1, 0),
      10,
      0.5,
      0.5,
      0.5,
      0.1
    );
  }

  /**
   * Spawn activation burst for Immortal Fragment.
   *
   * @param location The location to spawn particles at
   */
  public static void spawnImmortalActivation(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Brown dust particles burst (earth) - use DUST instead of FALLING_DUST
    world.spawnParticle(
      Particle.DUST,
      location,
      20,
      1.0,
      1.0,
      1.0,
      0.1,
      new Particle.DustOptions(EARTH_BROWN, 1.5f)
    );

    // Gold dust particles (strength)
    world.spawnParticle(
      Particle.DUST,
      location,
      15,
      1.0,
      1.0,
      1.0,
      0.05,
      new Particle.DustOptions(GOLD_SHIELD, 2.0f)
    );

    // Green particles (life/health)
    world.spawnParticle(
      Particle.DUST,
      location,
      10,
      1.0,
      1.0,
      1.0,
      0.03,
      new Particle.DustOptions(LIFE_GREEN, 1.5f)
    );

    // Rooting particles
    for (int i = 0; i < 360; i += 45) {
      double angle = Math.toRadians(i);
      double x = Math.cos(angle) * 0.8;
      double z = Math.sin(angle) * 0.8;

      Location particleLocation = location.clone().add(x, -0.5, z);
      world.spawnParticle(
        Particle.DUST,
        particleLocation,
        2,
        0.2,
        0.2,
        0.2,
        0.02,
        new Particle.DustOptions(EARTH_BROWN, 1.0f)
      );
    }
  }

  // ==========================================================================
  // CORRUPTED CORE EFFECTS
  // ==========================================================================

  /**
   * Spawn void particles spreading outward for Dread Gaze.
   *
   * @param location The target location
   */
  public static void spawnVoidParticles(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Reverse portal particles
    world.spawnParticle(
      Particle.REVERSE_PORTAL,
      location,
      10,
      0.5,
      0.5,
      0.5,
      0.05
    );

    // Dark purple dust particles
    world.spawnParticle(
      Particle.DUST,
      location,
      5,
      0.3,
      0.3,
      0.3,
      0.02,
      new Particle.DustOptions(VOID_PURPLE, 1.5f)
    );
  }

  /**
   * Spawn dark energy drain particles for Life Devourer.
   *
   * @param location The drain location
   */
  public static void spawnDrainParticles(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Dark purple dust
    world.spawnParticle(
      Particle.DUST,
      location,
      3,
      0.5,
      0.5,
      0.5,
      0.02,
      new Particle.DustOptions(DARK_PURPLE, 1.0f)
    );

    // Soul particles
    world.spawnParticle(
      Particle.SOUL,
      location,
      2,
      0.3,
      0.3,
      0.3,
      0.01
    );
  }

  /**
   * Spawn health transfer particles between entities.
   *
   * @param from Source location
   * @param to Target location
   */
  public static void spawnHealthTransfer(Location from, Location to) {
    if (from == null || to == null || from.getWorld() == null) {
      return;
    }

    World world = from.getWorld();
    Location midPoint = from.clone().add(to).multiply(0.5);

    // Red particles from enemy
    world.spawnParticle(
      Particle.DUST,
      midPoint,
      5,
      0.3,
      0.3,
      0.3,
      0.02,
      new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f)
    );

    // Green particles to player
    world.spawnParticle(
      Particle.DUST,
      midPoint,
      5,
      0.3,
      0.3,
      0.3,
      0.02,
      new Particle.DustOptions(Color.fromRGB(0, 255, 0), 1.0f)
    );
  }

  /**
   * Spawn void aura around player for Dread Gaze activation.
   *
   * @param location The player location
   */
  public static void spawnVoidAura(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Burst of void particles in a circle
    for (int i = 0; i < 360; i += 30) {
      double angle = Math.toRadians(i);
      double x = Math.cos(angle) * 1.5;
      double z = Math.sin(angle) * 1.5;

      Location particleLocation = location.clone().add(x, 0.5, z);

      world.spawnParticle(
        Particle.REVERSE_PORTAL,
        particleLocation,
        3,
        0.2,
        0.2,
        0.2,
        0.01
      );
    }
  }

  /**
   * Spawn activation burst for Corrupted Core.
   *
   * @param location The location to spawn particles at
   */
  public static void spawnCorruptedActivation(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Purple/black void particles
    world.spawnParticle(
      Particle.REVERSE_PORTAL,
      location,
      25,
      1.0,
      1.0,
      1.0,
      0.1
    );

    // Dark purple dust particles
    world.spawnParticle(
      Particle.DUST,
      location,
      15,
      1.0,
      1.0,
      1.0,
      0.05,
      new Particle.DustOptions(VOID_PURPLE, 2.0f)
    );

    // Soul particles
    world.spawnParticle(
      Particle.SOUL,
      location,
      10,
      0.8,
      0.8,
      0.8,
      0.02
    );
  }

  // ==========================================================================
  // LIGHTNING ABILITY EFFECTS
  // ==========================================================================

  /**
   * Create purple lightning visual effect with enhanced spark particles.
   *
   * @param location The location to spawn the effect
   */
  public static void createPurpleLightningEffect(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();
    Location topLocation = location.clone().add(0, 10, 0);

    // Create vertical purple particle beam
    for (double y = 0; y <= 10; y += 0.3) {
      Location particleLocation = location.clone().add(0, y, 0);

      // Main purple beam
      world.spawnParticle(
        Particle.DUST,
        particleLocation,
        5,
        0.1,
        0.1,
        0.1,
        0,
        new Particle.DustOptions(LIGHTNING_PURPLE, 2.0f)
      );

      // Electric spark effect
      world.spawnParticle(
        Particle.ELECTRIC_SPARK,
        particleLocation,
        3,
        0.2,
        0.2,
        0.2,
        0.05
      );

      // Additional spark particles for enhanced effect
      world.spawnParticle(
        Particle.DUST,
        particleLocation,
        2,
        0.15,
        0.15,
        0.15,
        0.02,
        new Particle.DustOptions(SPARK_PURPLE, 1.0f)
      );
    }

    // Add explosion effect at impact
    world.spawnParticle(
      Particle.DUST,
      location,
      50,
      0.5,
      0.5,
      0.5,
      0,
      new Particle.DustOptions(SPARK_PURPLE, 1.5f)
    );

    // Add flash effect
    world.spawnParticle(
      Particle.FLASH,
      location,
      1,
      0,
      0,
      0,
      0
    );

    // Additional purple spark ring at impact
    for (int i = 0; i < 360; i += 30) {
      double angle = Math.toRadians(i);
      double x = Math.cos(angle) * 0.5;
      double z = Math.sin(angle) * 0.5;

      Location sparkLocation = location.clone().add(x, 0.5, z);
      world.spawnParticle(
        Particle.ELECTRIC_SPARK,
        sparkLocation,
        2,
        0.2,
        0.2,
        0.2,
        0.03
      );
    }
  }

  /**
   * Spawn lightning strike impact particles.
   *
   * @param location The impact location
   */
  public static void spawnLightningImpact(Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    World world = location.getWorld();

    // Purple explosion
    world.spawnParticle(
      Particle.DUST,
      location,
      30,
      0.8,
      0.8,
      0.8,
      0.1,
      new Particle.DustOptions(LIGHTNING_PURPLE, 2.0f)
    );

    // Electric sparks
    world.spawnParticle(
      Particle.ELECTRIC_SPARK,
      location,
      15,
      0.5,
      0.5,
      0.5,
      0.05
    );

    // Flash
    world.spawnParticle(
      Particle.FLASH,
      location,
      1,
      0,
      0,
      0,
      0
    );
  }

  // ==========================================================================
  // UTILITY METHODS
  // ==========================================================================

  /**
   * Spawn a burst of colored dust particles.
   *
   * @param location The location to spawn particles at
   * @param color The color of the particles
   * @param count Number of particles
   * @param radius The spread radius
   */
  public static void spawnDustBurst(Location location, Color color, int count, double radius) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    location.getWorld().spawnParticle(
      Particle.DUST,
      location,
      count,
      radius,
      radius,
      radius,
      0.1,
      new Particle.DustOptions(color, 1.5f)
    );
  }

  /**
   * Spawn a ring of particles around a center point.
   *
   * @param center The center location
   * @param particle The particle type
   * @param count Number of particles
   * @param radius The radius of the ring
   */
  public static void spawnParticleRing(
    Location center,
    Particle particle,
    int count,
    double radius
  ) {
    if (center == null || center.getWorld() == null) {
      return;
    }

    World world = center.getWorld();
    int angleStep = 360 / count;

    for (int i = 0; i < 360; i += angleStep) {
      double angle = Math.toRadians(i);
      double x = center.getX() + (radius * Math.cos(angle));
      double z = center.getZ() + (radius * Math.sin(angle));
      Location particleLocation = new Location(world, x, center.getY(), z);

      world.spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0);
    }
  }
}
