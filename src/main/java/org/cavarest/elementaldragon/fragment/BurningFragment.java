package org.cavarest.elementaldragon.fragment;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.ability.EntityTargeter;
import org.cavarest.elementaldragon.visual.ParticleFX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

/**
 * Burning Fragment implementation providing fire-based abilities.
 *
 * Active Abilities:
 * - Dragon's Wrath: Launch a powerful fireball that explodes on impact
 * - Infernal Dominion: Create a ring of fire around the player
 *
 * Passive Bonus: Fire Resistance when fragment equipped
 */
public class BurningFragment extends AbstractFragment implements Listener {

  // Dragon's Wrath constants (ORIGINAL SPECIFICATION)
  private static final long DRAGONS_WRATH_COOLDOWN = 120000L; // 2 minutes (original spec)
  private static final double DRAGONS_WRATH_DAMAGE = 6.0; // 3 hearts (original spec: armor-ignoring)
  private static final double DRAGONS_WRATH_AOE_RADIUS = 5.0; // 5 blocks (original spec: all players)
  private static final int DRAGONS_WRATH_HOMING_TICKS = 10; // 10 ticks = 0.5 seconds (original spec)
  private static final double DRAGONS_WRATH_TARGET_RANGE = 50.0; // Range to search for targets
  private static final double DRAGONS_WRATH_VELOCITY = 1.5;

  // Infernal Dominion constants (ORIGINAL SPECIFICATION)
  private static final long INFERNAL_DOMINION_COOLDOWN = 180000L; // 3 minutes (original spec)
  private static final double INFERNAL_DOMINION_RADIUS = 10.0; // 10 blocks (original spec)
  private static final int INFERNAL_DOMINION_DURATION = 200; // 10 seconds (original spec: 1 heart/sec for 10 hearts)
  private static final double INFERNAL_DOMINION_DAMAGE_PER_TICK = 1.0; // 0.5 hearts per 10-tick interval (1 heart/second total)

  // Visual constants
  private static final Color FIRE_COLOR = Color.fromRGB(255, 100, 0);
  private static final Color ORANGE_COLOR = Color.fromRGB(255, 165, 0);

  // Fragment metadata (Single Source of Truth)
  // Using BLAZE_POWDER instead of FIRE_CHARGE to avoid default right-click fire throwing behavior
  private static final Material MATERIAL = Material.BLAZE_POWDER;
  private static final NamedTextColor THEME_COLOR = NamedTextColor.RED;
  private static final String ELEMENT_NAME = "FIRE";

  // Ability definitions (Single Source of Truth)
  private final List<AbilityDefinition> abilities = List.of(
    new AbilityDefinition(1, "Dragon's Wrath", "fireball attack",
      List.of("wrath", "dragons-wrath"),
      "A fireball that dynamically chases the closest hostile in your crosshairs! 🔥🎯", "🎯"),
    new AbilityDefinition(2, "Infernal Dominion", "area fire",
      List.of("dominion", "infernal-dominion"),
      "A ring of fire erupts around you! 🔥⭕", "⭕")
  );

  private final ElementalDragon plugin;

  /**
   * Create a new Burning Fragment.
   *
   * @param plugin The plugin instance
   */
  public BurningFragment(ElementalDragon plugin) {
    super(
      plugin,
      FragmentType.BURNING,
      DRAGONS_WRATH_COOLDOWN,
      Arrays.asList(
        "Ability 1: Dragon's Wrath - Fireball attack (40s cooldown)",
        "Ability 2: Infernal Dominion - Fire ring (60s cooldown)",
        "",
        "Passive: Fire Resistance"
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
      "1. Dragon's Wrath - Launch explosive fireball (40s cooldown)\n" +
      "2. Infernal Dominion - Create fire ring around you (60s cooldown)\n" +
      "Passive: Fire Resistance when equipped";
  }

  @Override
  public void activate(Player player) {
    if (player == null) {
      return;
    }

    // Check permission
    if (!player.hasPermission("elementaldragon.fragment.burning")) {
      player.sendMessage(
        Component.text("You do not have permission to use the Burning Fragment!",
          NamedTextColor.RED)
      );
      return;
    }

    // Apply passive effects (Fire Resistance)
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
      Component.text("Passive: Fire Resistance granted!", NamedTextColor.GRAY)
    );
    player.sendMessage(
      Component.text("Use /fire 1 or /fire 2 to use abilities",
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
    if (!player.hasPermission("elementaldragon.fragment.burning")) {
      player.sendMessage(
        Component.text("You do not have permission to use Burning Fragment abilities!",
          NamedTextColor.RED)
      );
      return;
    }

    switch (abilityNumber) {
      case 1:
        executeDragonsWrath(player);
        break;
      case 2:
        executeInfernalDominion(player);
        break;
      default:
        player.sendMessage(
          Component.text("Unknown ability number: " + abilityNumber,
            NamedTextColor.RED)
        );
        player.sendMessage(
          Component.text("Use /fire 1 or /fire 2", NamedTextColor.GRAY)
        );
    }
  }

  /**
   * Execute Dragon's Wrath - launch a homing fireball targeting the closest hostile entity in view,
   * or if none, directed toward where the player is pointing.
   * Original Specification:
   * - Targets closest hostile entity within 50 blocks (must be in view/line of sight)
   * - If no hostile in view, fire toward cursor direction
   * - Homes/tracks for 10 ticks (0.5 seconds)
   * - Deals 6.0 damage (3 hearts) ignoring armor
   * - Affects all players in 5 block radius on impact
   *
   * @param player The player executing the ability
   */
  private void executeDragonsWrath(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location eyeLocation = player.getEyeLocation();
    Vector lookDirection = eyeLocation.getDirection().normalize();

    // Find closest hostile entity within range that is IN VIEW (line of sight)
    LivingEntity target = findClosestHostileInView(player, DRAGONS_WRATH_TARGET_RANGE);

    // Determine fireball direction
    Vector fireballDirection;
    boolean isTargetingEntity = false;

    if (target != null) {
      // Target hostile mob found - fire toward it
      Location targetLoc = target.getEyeLocation();
      fireballDirection = targetLoc.toVector().subtract(eyeLocation.toVector()).normalize();
      isTargetingEntity = true;
    } else {
      // No hostile in view - fire toward where player is pointing (cursor direction)
      fireballDirection = lookDirection;
      isTargetingEntity = false;
    }

    // Spawn fireball 1.5 blocks in front of player (outside hitbox)
    Location spawnLoc = eyeLocation.clone().add(fireballDirection.clone().multiply(1.5));
    Fireball fireball = player.getWorld().spawn(spawnLoc, Fireball.class);

    // Configure fireball
    fireball.setShooter(player);
    fireball.setDirection(fireballDirection);
    fireball.setYield(2.0f); // Allow block destruction
    fireball.setIsIncendiary(false); // No fire spread (damage handled by events)
    fireball.setVelocity(fireballDirection.clone().multiply(DRAGONS_WRATH_VELOCITY));

    // Store custom data for damage handling and AOE
    org.bukkit.NamespacedKey damageKey = new org.bukkit.NamespacedKey(plugin, "dragons_wrath_damage");
    org.bukkit.NamespacedKey aoeKey = new org.bukkit.NamespacedKey(plugin, "dragons_wrath_aoe");
    org.bukkit.NamespacedKey shooterKey = new org.bukkit.NamespacedKey(plugin, "dragons_wrath_shooter");

    fireball.getPersistentDataContainer().set(damageKey, org.bukkit.persistence.PersistentDataType.DOUBLE, DRAGONS_WRATH_DAMAGE);
    fireball.getPersistentDataContainer().set(aoeKey, org.bukkit.persistence.PersistentDataType.DOUBLE, DRAGONS_WRATH_AOE_RADIUS);
    fireball.getPersistentDataContainer().set(shooterKey, org.bukkit.persistence.PersistentDataType.STRING, player.getUniqueId().toString());

    // Implement homing mechanism (tracks target for 10 ticks if targeting entity)
    if (target != null) {
      new BukkitRunnable() {
        private int ticks = 0;

        @Override
        public void run() {
          // Stop tracking after 10 ticks or if fireball/target is invalid
          if (ticks >= DRAGONS_WRATH_HOMING_TICKS ||
              !fireball.isValid() ||
              fireball.isDead() ||
              !target.isValid() ||
              target.isDead()) {
            cancel();
            return;
          }

          // Calculate direction to target
          Location fireballLoc = fireball.getLocation();
          Location targetLoc = target.getEyeLocation();
          Vector toTarget = targetLoc.toVector().subtract(fireballLoc.toVector()).normalize();

          // Update fireball velocity to track target
          fireball.setVelocity(toTarget.multiply(DRAGONS_WRATH_VELOCITY));

          ticks++;
        }
      }.runTaskTimer(plugin, 0L, 1L);
    }

    // Play ability sound
    playAbilitySound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);

    // Show particles
    showAbilityParticles(player.getLocation(), 15);

    // Cooldown is set by FragmentManager.useFragmentAbility()

    // Send message with target info
    if (target != null) {
      player.sendMessage(
        Component.text("Dragon's Wrath pursues " + target.getName() + "! 🔥🎯", NamedTextColor.GREEN)
      );
    } else {
      player.sendMessage(
        Component.text("Dragon's Wrath fired toward your target! 🔥🎯", NamedTextColor.GREEN)
      );
    }
  }

  /**
   * Find the closest hostile entity within range that is IN VIEW (line of sight) of the player.
   * Uses ray-tracing to check if there's a clear line of sight to each hostile mob.
   *
   * @param player The player
   * @param range Maximum search range
   * @return The closest hostile entity with line of sight, or null if none found
   */
  private LivingEntity findClosestHostileInView(Player player, double range) {
    // Use EntityTargeter for DRY cone-based targeting with line-of-sight
    return EntityTargeter.findInViewingConeWithLineOfSight(
      player, range, Math.PI / 3,  // 60 degree cone
      EntityTargeter::isHostileMob,  // Only target hostile mobs
      null  // No exclusion
    );
  }

  /**
   * Find the closest hostile entity within range (legacy method, kept for compatibility).
   *
   * @param player The player
   * @param range Maximum search range
   * @return The closest hostile entity, or null if none found
   */
  private LivingEntity findClosestHostileEntity(Player player, double range) {
    // Use EntityTargeter for DRY targeting logic (no cone, no line-of-sight)
    return EntityTargeter.findInViewingCone(
      player, range, -1.0,  // -1.0 dot threshold = no cone restriction
      EntityTargeter::isHostileMob,
      null
    );
  }

  /**
   * Execute Infernal Dominion - create a ring of fire affecting all nearby players.
   * Original Specification:
   * - Sets all players (except wielder) in 10 block radius on fire
   * - Deals 1 heart per second for up to 10 hearts total
   * - Orange circle visible to all players (visual marker on ground)
   *
   * @param player The player executing the ability
   */
  private void executeInfernalDominion(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location center = player.getLocation();

    // Play ability sound
    playAbilitySound(
      center,
      Sound.BLOCK_FIRE_AMBIENT,
      2.0f,
      1.0f
    );
    playAbilitySound(
      center,
      Sound.ENTITY_BLAZE_AMBIENT,
      1.0f,
      1.2f
    );

    // Create fire ring effect and damage players (ORIGINAL SPEC: players only)
    new BukkitRunnable() {
      private int ticks = 0;

      @Override
      public void run() {
        if (ticks >= INFERNAL_DOMINION_DURATION) {
          cancel();
          return;
        }

        // Damage nearby living entities (hostile mobs, players, etc.)
        for (Entity entity : player.getWorld().getNearbyEntities(
          center, INFERNAL_DOMINION_RADIUS, INFERNAL_DOMINION_RADIUS, INFERNAL_DOMINION_RADIUS
        )) {
          // Affects all living entities (players, hostile mobs, animals)
          if (!(entity instanceof LivingEntity)) {
            continue;
          }

          LivingEntity target = (LivingEntity) entity;

          // Skip the wielder (wielder is not affected)
          if (target instanceof Player && target.getUniqueId().equals(player.getUniqueId())) {
            continue;
          }

          // Apply fire ticks (visual effect)
          target.setFireTicks(20); // 1 second of fire (20 ticks)

          // Create a custom damage event that bypasses armor
          // 1 heart per second = 2.0 damage per second
          // Running every 10 ticks (0.5 seconds) = 1.0 damage per interval
          @SuppressWarnings("removal")
          org.bukkit.event.entity.EntityDamageEvent damageEvent = new org.bukkit.event.entity.EntityDamageEvent(
            target,
            org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM,
            INFERNAL_DOMINION_DAMAGE_PER_TICK
          );

          // Set armor modifier to 0 to bypass armor
          if (damageEvent.getDamage(org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR) > 0) {
            damageEvent.setDamage(org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR, 0);
          }

          // Fire the event through the plugin manager so other abilities can react
          plugin.getServer().getPluginManager().callEvent(damageEvent);

          // If event wasn't cancelled, apply the damage
          if (!damageEvent.isCancelled()) {
            double finalDamage = damageEvent.getFinalDamage();
            double currentHealth = target.getHealth();
            double newHealth = Math.max(0, currentHealth - finalDamage);
            target.setHealth(newHealth);
          }

          // Visual feedback for affected entity
          target.getWorld().spawnParticle(
            Particle.FLAME,
            target.getLocation().add(0, 1, 0),
            10,
            0.3,
            0.5,
            0.3,
            0.05
          );
        }

        // Spawn orange particle circle on ground (ORIGINAL SPEC: visible marker)
        spawnOrangeCircleParticles(center, INFERNAL_DOMINION_RADIUS);

        // Spawn fire ring particles for visual effect
        spawnFireRingParticles(center, INFERNAL_DOMINION_RADIUS);

        ticks += 10;
      }
    }.runTaskTimer(plugin, 0L, 10L);

    // Cooldown is set by FragmentManager.useFragmentAbility()

    player.sendMessage(
      Component.text("Infernal Dominion activated! Fire ring surrounds you!", NamedTextColor.GREEN)
    );
  }

  /**
   * Spawn fire ring particles around a location.
   *
   * @param center The center location
   * @param radius The radius of the ring
   */
  private void spawnFireRingParticles(Location center, double radius) {
    ParticleFX.spawnFireRingParticles(center, radius);
  }

  /**
   * Spawn orange particle circle on ground at radius edge.
   * Creates a visible ground marker for Infernal Dominion AOE range.
   *
   * @param center The center location
   * @param radius The radius of the circle
   */
  private void spawnOrangeCircleParticles(Location center, double radius) {
    // Draw circle of orange particles on the ground
    int particleCount = 60; // Number of particles forming the circle
    double angleStep = (2 * Math.PI) / particleCount;

    for (int i = 0; i < particleCount; i++) {
      double angle = i * angleStep;
      double x = center.getX() + (radius * Math.cos(angle));
      double z = center.getZ() + (radius * Math.sin(angle));

      // Place particles at ground level (Y = center.getY())
      Location particleLoc = new Location(center.getWorld(), x, center.getY(), z);

      // Spawn orange DUST particles
      center.getWorld().spawnParticle(
        Particle.DUST,
        particleLoc,
        1, // Single particle per location
        0.0, // No X offset
        0.0, // No Y offset
        0.0, // No Z offset
        0.0, // No extra data
        new Particle.DustOptions(ORANGE_COLOR, 1.0f) // Orange color, size 1.0
      );
    }
  }

  /**
   * Apply passive Fire Resistance effect.
   * Grants complete immunity to fire and lava damage.
   *
   * @param player The player
   */
  @Override
  protected void applyPassiveEffects(Player player) {
    // Apply FIRE_RESISTANCE potion effect with amplifier 3 (Fire Resistance IV)
    // This provides complete immunity to fire, lava, and burning damage
    player.addPotionEffect(
      new PotionEffect(
        PotionEffectType.FIRE_RESISTANCE,
        Integer.MAX_VALUE, // Permanent while equipped
        3, // Amplifier 3 = Fire Resistance IV (complete immunity)
        false, // Not ambient
        true, // Show particles
        true // Show icon
      )
    );
  }

  /**
   * Remove passive Fire Resistance effect.
   *
   * @param player The player
   */
  @Override
  protected void removePassiveEffects(Player player) {
    player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
  }

  /**
   * Event handler for passive thorns effect - fire damage to melee attackers.
   * Passive Bonus: Dragon's Scales
   * - Applies 1 heart fire damage to melee attackers
   *
   * @param event The entity damage event
   */
  @EventHandler
  public void onMeleeAttackForThorns(EntityDamageByEntityEvent event) {
    // Check if the victim is a Player
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player victim = (Player) event.getEntity();

    // Check if player has Burning Fragment equipped
    FragmentManager fragmentManager = plugin.getFragmentManager();
    if (fragmentManager.getEquippedFragment(victim) != FragmentType.BURNING) {
      return;
    }

    // Check if this is melee damage from a living entity
    if (!(event.getDamager() instanceof LivingEntity)) {
      return;
    }

    LivingEntity attacker = (LivingEntity) event.getDamager();

    // Apply fire damage (1 heart = 2.0 damage)
    attacker.setFireTicks(60); // 3 seconds of fire
    attacker.damage(2.0, victim); // 1 heart damage

    // Visual feedback - fire particles on attacker
    attacker.getWorld().spawnParticle(
      Particle.FLAME,
      attacker.getLocation().add(0, 1, 0),
      15,
      0.3,
      0.5,
      0.3,
      0.05
    );

    // Sound feedback
    playAbilitySound(
      attacker.getLocation(),
      Sound.ENTITY_BLAZE_HURT,
      0.5f,
      1.2f
    );
  }

  /**
   * Play activation sound for burning fragment.
   *
   * @param player The player
   */
  @Override
  protected void playActivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.ENTITY_BLAZE_AMBIENT,
      1.0f,
      1.0f
    );
    player.getWorld().playSound(
      location,
      Sound.ITEM_FIRECHARGE_USE,
      0.8f,
      1.0f
    );
  }

  /**
   * Play deactivation sound for burning fragment.
   *
   * @param player The player
   */
  @Override
  protected void playDeactivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.BLOCK_FIRE_EXTINGUISH,
      0.6f,
      0.8f
    );
  }

  /**
   * Show activation particles for burning fragment.
   *
   * @param player The player
   */
  @Override
  protected void showActivationParticles(Player player) {
    ParticleFX.spawnBurningActivation(player.getLocation().add(0, 1, 0));
  }

  /**
   * Show ability particles.
   *
   * @param location The location
   * @param count Number of particles
   */
  @Override
  protected void showAbilityParticles(Location location, int count) {
    ParticleFX.spawnFireballTrail(location, count);
  }

  /**
   * Event handler for Dragon's Wrath fireball damage.
   * Applies TRUE damage that ignores armor but still fires damage events.
   *
   * Uses EntityDamageEvent with DamageModifier.ARMOR set to 0 to bypass armor
   * while still allowing other plugins/abilities to react to the damage.
   */
  @EventHandler
  public void onFireballDamage(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Fireball)) {
      return;
    }

    Fireball fireball = (Fireball) event.getDamager();
    org.bukkit.NamespacedKey damageKey = new org.bukkit.NamespacedKey(plugin, "dragons_wrath_damage");

    if (!fireball.getPersistentDataContainer().has(damageKey, org.bukkit.persistence.PersistentDataType.DOUBLE)) {
      return; // Not a Dragon's Wrath fireball
    }

    // Get custom damage value
    double customDamage = fireball.getPersistentDataContainer().get(
      damageKey, org.bukkit.persistence.PersistentDataType.DOUBLE);

    if (event.getEntity() instanceof LivingEntity) {
      LivingEntity target = (LivingEntity) event.getEntity();

      // Cancel the original fireball damage event
      event.setCancelled(true);

      // Create a custom damage event that bypasses armor
      @SuppressWarnings("removal")
      org.bukkit.event.entity.EntityDamageEvent damageEvent = new org.bukkit.event.entity.EntityDamageEvent(
        target,
        org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM,
        customDamage
      );

      // Set armor modifier to 0 to bypass armor
      if (damageEvent.getDamage(org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR) > 0) {
        damageEvent.setDamage(org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR, 0);
      }

      // Fire the event through the plugin manager so other abilities can react
      plugin.getServer().getPluginManager().callEvent(damageEvent);

      // If event wasn't cancelled, apply the damage
      if (!damageEvent.isCancelled()) {
        double finalDamage = damageEvent.getFinalDamage();
        double currentHealth = target.getHealth();
        double newHealth = Math.max(0, currentHealth - finalDamage);
        target.setHealth(newHealth);
      }

      // Apply fire ticks
      target.setFireTicks(40); // 2 seconds of fire
    }
  }

  /**
   * Event handler for Dragon's Wrath fireball impact.
   * Applies AOE damage to all players within 5 blocks of impact point.
   * Original Specification: Affects all players in 5 block radius with armor-ignoring damage.
   */
  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof Fireball)) {
      return;
    }

    Fireball fireball = (Fireball) event.getEntity();
    org.bukkit.NamespacedKey damageKey = new org.bukkit.NamespacedKey(plugin, "dragons_wrath_damage");
    org.bukkit.NamespacedKey aoeKey = new org.bukkit.NamespacedKey(plugin, "dragons_wrath_aoe");

    if (!fireball.getPersistentDataContainer().has(damageKey, org.bukkit.persistence.PersistentDataType.DOUBLE)) {
      return; // Not a Dragon's Wrath fireball
    }

    // Get impact location
    Location impactLoc = fireball.getLocation();

    // Get damage and AOE radius from persistent data
    double damage = fireball.getPersistentDataContainer().get(
      damageKey, org.bukkit.persistence.PersistentDataType.DOUBLE);

    double aoeRadius = fireball.getPersistentDataContainer().has(aoeKey, org.bukkit.persistence.PersistentDataType.DOUBLE)
      ? fireball.getPersistentDataContainer().get(aoeKey, org.bukkit.persistence.PersistentDataType.DOUBLE)
      : DRAGONS_WRATH_AOE_RADIUS;

    // Get the shooter to exclude them from damage
    Player shooter = null;
    if (fireball.getShooter() instanceof Player) {
      shooter = (Player) fireball.getShooter();
    }

    // Apply AOE damage to all living entities within radius
    for (Entity entity : fireball.getWorld().getNearbyEntities(
        impactLoc, aoeRadius, aoeRadius, aoeRadius)) {

      // Affects all living entities (players, hostile mobs, animals)
      if (!(entity instanceof LivingEntity)) {
        continue;
      }

      LivingEntity target = (LivingEntity) entity;

      // Skip the shooter (wielder is not affected)
      if (shooter != null && target instanceof Player && target.getUniqueId().equals(shooter.getUniqueId())) {
        continue;
      }

      // Create a custom damage event that bypasses armor
      @SuppressWarnings("removal")
      org.bukkit.event.entity.EntityDamageEvent damageEvent = new org.bukkit.event.entity.EntityDamageEvent(
        target,
        org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM,
        damage
      );

      // Set armor modifier to 0 to bypass armor
      if (damageEvent.getDamage(org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR) > 0) {
        damageEvent.setDamage(org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR, 0);
      }

      // Fire the event through the plugin manager so other abilities can react
      plugin.getServer().getPluginManager().callEvent(damageEvent);

      // If event wasn't cancelled, apply the damage
      if (!damageEvent.isCancelled()) {
        double finalDamage = damageEvent.getFinalDamage();
        double currentHealth = target.getHealth();
        double newHealth = Math.max(0, currentHealth - finalDamage);
        target.setHealth(newHealth);
      }

      // Apply fire ticks
      target.setFireTicks(40); // 2 seconds of fire

      // Visual feedback for AOE hit
      target.getWorld().spawnParticle(
        Particle.FLAME,
        target.getLocation().add(0, 1, 0),
        20,
        0.5,
        0.5,
        0.5,
        0.1
      );

      // Sound feedback (for players only)
      if (target instanceof Player) {
        Player targetPlayer = (Player) target;
        targetPlayer.playSound(
          targetPlayer.getLocation(),
          Sound.ENTITY_PLAYER_HURT_ON_FIRE,
          1.0f,
          1.0f
        );
      }
    }

    // Show impact explosion particles
    impactLoc.getWorld().spawnParticle(
      Particle.EXPLOSION_EMITTER,
      impactLoc,
      1
    );

    impactLoc.getWorld().spawnParticle(
      Particle.FLAME,
      impactLoc,
      50,
      2.0,
      2.0,
      2.0,
      0.2
    );

    // Play explosion sound
    impactLoc.getWorld().playSound(
      impactLoc,
      Sound.ENTITY_GENERIC_EXPLODE,
      2.0f,
      1.0f
    );
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
