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
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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

  // Draconic Surge constants (ORIGINAL SPECIFICATION)
  private static final long DRACONIC_SURGE_COOLDOWN = 45000L; // 45 seconds (original spec)
  private static final double DRACONIC_SURGE_DISTANCE = 20.0; // 20 blocks (original spec)
  private static final int DRACONIC_SURGE_DURATION = 20; // 1 second = 20 ticks (user spec)
  private static final double DRACONIC_SURGE_VELOCITY = DRACONIC_SURGE_DISTANCE / DRACONIC_SURGE_DURATION; // 1.0 blocks/tick
  private static final int DRACONIC_SURGE_FALL_PROTECTION = 200; // 10 seconds (200 ticks) of fall damage protection

  // Metadata keys for tracking Draconic Surge state
  private static final String DRACONIC_SURGE_ACTIVE_KEY = "agile_draconic_surge_active";
  private static final String DRACONIC_SURGE_START_TIME_KEY = "agile_draconic_surge_start_time";

  // Wing Burst constants (ORIGINAL SPECIFICATION)
  private static final long WING_BURST_COOLDOWN = 120000L; // 2 minutes (original spec)
  private static final double WING_BURST_RADIUS = 8.0; // 8 blocks (original spec)
  private static final double WING_BURST_DISTANCE = 20.0; // 20 blocks (original spec)
  private static final int WING_BURST_PUSH_DURATION = 40; // 2 seconds = 40 ticks (original spec)
  // Velocity multiplier: higher value needed to compensate for friction (2.0 instead of 0.5)
  private static final double WING_BURST_VELOCITY = (WING_BURST_DISTANCE / WING_BURST_PUSH_DURATION) * 4.0; // 2.0 blocks/tick (4x to overcome friction)
  private static final int WING_BURST_FALL_SLOW_DURATION = 200; // 10 seconds (200 ticks)

  // Visual constants
  private static final Color TEAL_COLOR = Color.fromRGB(100, 255, 200);
  private static final Color WHITE_COLOR = Color.fromRGB(255, 255, 255);

  // Fragment metadata (Single Source of Truth)
  // Using PHANTOM_MEMBRANE instead of WIND_CHARGE to avoid default right-click wind throwing behavior
  private static final Material MATERIAL = Material.PHANTOM_MEMBRANE;
  private static final NamedTextColor THEME_COLOR = NamedTextColor.AQUA;
  private static final String ELEMENT_NAME = "WIND";

  // Ability definitions (Single Source of Truth)
  private final List<AbilityDefinition> abilities = List.of(
    new AbilityDefinition(1, "Draconic Surge", "speed boost",
      List.of("surge", "draconic-surge"),
      "Speed courses through your veins like the wind! 💨⚡", "⚡"),
    new AbilityDefinition(2, "Wing Gust", "repulsion wave",
      List.of("burst", "wing-burst", "gust"),
      "A powerful gust pushes all nearby foes 20 blocks away! 💨🌊", "🌊")
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
   * Execute Draconic Surge - dash player 20 blocks in direction they are looking.
   * Original Specification:
   * - Dashes player 20 blocks in direction they are looking
   * - Completes within 1 second (20 ticks)
   * - Negates damage while flying and during landing
   * - Cooldown: 45 seconds
   *
   * @param player The player executing the ability
   */
  private void executeDraconicSurge(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location playerLocation = player.getLocation();

    // Calculate dash direction (3D - dashes in the direction player is looking)
    Vector direction = playerLocation.getDirection().normalize();
    Vector dashVelocity = direction.multiply(DRACONIC_SURGE_VELOCITY);

    // Mark player as having fall damage protection (10 seconds)
    // This prevents fall damage even if player disconnects and reconnects
    player.setMetadata(DRACONIC_SURGE_ACTIVE_KEY, new org.bukkit.metadata.FixedMetadataValue(plugin, true));
    player.setMetadata(DRACONIC_SURGE_START_TIME_KEY, new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis()));

    // Play activation sound
    playAbilitySound(
      playerLocation,
      Sound.ENTITY_PHANTOM_FLAP,
      2.0f,
      1.5f
    );

    // Apply continuous velocity over DRACONIC_SURGE_DURATION ticks (1 second)
    new BukkitRunnable() {
      private int ticks = 0;

      @Override
      public void run() {
        if (ticks >= DRACONIC_SURGE_DURATION) {
          // Dash complete - fall damage protection continues separately
          cancel();
          return;
        }

        if (player.isDead() || !player.isValid()) {
          cancel();
          return;
        }

        // Apply velocity each tick for smooth dash
        player.setVelocity(dashVelocity);

        // Show wind trail particles
        player.getWorld().spawnParticle(
          Particle.CLOUD,
          player.getLocation().add(0, 0.5, 0),
          3,
          0.2,
          0.2,
          0.2,
          0.02
        );

        ticks++;
      }
    }.runTaskTimer(plugin, 0L, 1L);

    // Schedule removal of fall damage protection after 10 seconds
    new BukkitRunnable() {
      @Override
      public void run() {
        if (player.hasMetadata(DRACONIC_SURGE_ACTIVE_KEY)) {
          player.removeMetadata(DRACONIC_SURGE_ACTIVE_KEY, plugin);
        }
        if (player.hasMetadata(DRACONIC_SURGE_START_TIME_KEY)) {
          player.removeMetadata(DRACONIC_SURGE_START_TIME_KEY, plugin);
        }
      }
    }.runTaskLater(plugin, DRACONIC_SURGE_FALL_PROTECTION);

    // Cooldown is set by FragmentManager.useFragmentAbility()

    player.sendMessage(
      Component.text("Draconic Surge activated! Fall damage protected for 10 seconds!",
        NamedTextColor.GREEN)
    );

    // Show initial burst particles
    player.getWorld().spawnParticle(
      Particle.CLOUD,
      playerLocation.add(0, 1, 0),
      20,
      0.5,
      0.5,
      0.5,
      0.1
    );
  }

  /**
   * Execute Wing Burst - push all nearby players away with knockback.
   * Original Specification:
   * - Pushes all players in 8 block radius 20 blocks away from wielder
   * - Push completes within 2 seconds (40 ticks)
   * - Applies slow falling for 10 seconds
   * - Cooldown: 2 minutes
   *
   * @param player The player executing the ability
   */
  private void executeWingBurst(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location center = player.getLocation();

    // Play activation sound
    playAbilitySound(center, Sound.ENTITY_PHANTOM_FLAP, 2.0f, 1.5f);

    // Find all living entities in radius and calculate their knockback vectors
    java.util.Map<java.util.UUID, Vector> affectedEntities = new java.util.HashMap<>();

    for (Entity entity : player.getWorld().getNearbyEntities(
        center, WING_BURST_RADIUS, WING_BURST_RADIUS, WING_BURST_RADIUS)) {

      // Affects all living entities (players, hostile mobs, animals)
      if (!(entity instanceof LivingEntity)) {
        continue;
      }

      LivingEntity target = (LivingEntity) entity;

      // Skip the wielder (wielder is not affected)
      if (target instanceof Player && target.getUniqueId().equals(player.getUniqueId())) {
        continue;
      }

      // Calculate knockback direction (away from wielder, horizontal only)
      Location targetLoc = target.getLocation();
      Vector knockbackDirection = targetLoc.toVector()
        .subtract(center.toVector())
        .setY(0) // Horizontal only (no vertical component)
        .normalize();

      // Calculate velocity for 20 block push over 40 ticks
      Vector velocity = knockbackDirection.multiply(WING_BURST_VELOCITY);

      // Store for continuous application
      affectedEntities.put(target.getUniqueId(), velocity);

      // Apply slow falling to players only (200 ticks = 10 seconds)
      if (target instanceof Player) {
        ((Player) target).addPotionEffect(
          new PotionEffect(
            PotionEffectType.SLOW_FALLING,
            WING_BURST_FALL_SLOW_DURATION,
            0,
            false,
            false,
            false
          )
        );
      }

      // Visual feedback for affected entity
      target.getWorld().spawnParticle(
        Particle.CLOUD,
        target.getLocation().add(0, 1, 0),
        10,
        0.3,
        0.5,
        0.3,
        0.05
      );
    }

    // Show wind burst particles at origin
    showWingBurstParticles(center);

    // Apply continuous knockback velocity over 40 ticks (2 seconds)
    new BukkitRunnable() {
      private int ticks = 0;

      @Override
      public void run() {
        if (ticks >= WING_BURST_PUSH_DURATION) {
          cancel();
          return;
        }

        // Apply velocity to all affected entities
        for (java.util.UUID uuid : affectedEntities.keySet()) {
          // Find the living entity by UUID in the player's world
          LivingEntity target = null;
          for (Entity entity : player.getWorld().getEntities()) {
            if (entity.getUniqueId().equals(uuid) && entity instanceof LivingEntity) {
              target = (LivingEntity) entity;
              break;
            }
          }

          // Skip if entity is no longer valid or dead
          if (target == null || target.isDead() || !target.isValid()) {
            continue;
          }

          // Apply knockback velocity - ADD to current velocity for cumulative effect
          // Preserve Y component (gravity/falling) while adding horizontal push
          Vector additionalVelocity = affectedEntities.get(uuid);
          Vector currentVelocity = target.getVelocity();
          target.setVelocity(new Vector(
            currentVelocity.getX() + additionalVelocity.getX(),
            currentVelocity.getY(),
            currentVelocity.getZ() + additionalVelocity.getZ()
          ));

          // Show wind trail particles
          target.getWorld().spawnParticle(
            Particle.CLOUD,
            target.getLocation().add(0, 0.5, 0),
            2,
            0.2,
            0.2,
            0.2,
            0.02
          );
        }

        ticks++;
      }
    }.runTaskTimer(plugin, 0L, 1L);

    // Cooldown is set by FragmentManager.useFragmentAbility()

    player.sendMessage(
      Component.text("Wing Burst activated! " + affectedEntities.size() + " entities knocked back!",
        NamedTextColor.GREEN)
    );

    // Play ability sound
    playAbilitySound(
      player.getLocation(),
      Sound.ENTITY_PARROT_FLY,
      1.0f,
      1.2f
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
   * Event handler for damage negation and reduction.
   * - Negates ALL fall damage during Draconic Surge (10 seconds protection)
   * - Reduces fall damage during Wing Burst (slow falling effect)
   * - Passive: 50% fall damage reduction when Agility Fragment equipped
   */
  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();

    // Check if player has Draconic Surge fall damage protection active
    if (player.hasMetadata(DRACONIC_SURGE_ACTIVE_KEY)) {
      // Check if the protection is still within the 10 second window
      if (player.hasMetadata(DRACONIC_SURGE_START_TIME_KEY)) {
        long startTime = player.getMetadata(DRACONIC_SURGE_START_TIME_KEY).get(0).asLong();
        long elapsed = (System.currentTimeMillis() - startTime) / 50; // Convert to ticks
        if (elapsed < DRACONIC_SURGE_FALL_PROTECTION) {
          // Negate ALL fall damage while protection is active
          if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
          }
        }
      }
    }

    // Check if this is fall damage
    if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
      // Check if player has slow falling (from Wing Burst)
      if (player.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
        // Reduce fall damage significantly
        event.setDamage(event.getDamage() * 0.2);
        return;
      }

      // Passive Bonus: Wind Walker - 50% fall damage reduction
      FragmentManager fragmentManager = plugin.getFragmentManager();
      if (fragmentManager.getEquippedFragment(player) == FragmentType.AGILITY) {
        // Reduce fall damage by 50%
        event.setDamage(event.getDamage() * 0.5);

        // Visual feedback - cloud particles on landing
        if (event.getDamage() > 0) {
          player.getWorld().spawnParticle(
            Particle.CLOUD,
            player.getLocation(),
            5,
            0.3,
            0.1,
            0.3,
            0.02
          );
        }
      }
    }
  }

  /**
   * Event handler for soul sand/honey block slowdown prevention.
   * Passive Bonus: Wind Walker - No slow-down in soul sand/honey blocks
   *
   * @param event The player move event
   */
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();

    // Check if player has Agility Fragment equipped
    FragmentManager fragmentManager = plugin.getFragmentManager();
    if (fragmentManager.getEquippedFragment(player) != FragmentType.AGILITY) {
      return;
    }

    // Check if player is on soul sand or in honey block
    Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();
    Block blockAt = player.getLocation().getBlock();

    if (blockBelow.getType() == Material.SOUL_SAND ||
        blockBelow.getType() == Material.SOUL_SOIL ||
        blockAt.getType() == Material.HONEY_BLOCK) {

      // Remove slowness effect caused by these blocks
      player.removePotionEffect(PotionEffectType.SLOWNESS);

      // Visual feedback - small cloud particles at feet
      if (Math.random() < 0.1) { // 10% chance per move to avoid spam
        player.getWorld().spawnParticle(
          Particle.CLOUD,
          player.getLocation(),
          2,
          0.2,
          0.0,
          0.2,
          0.01
        );
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
