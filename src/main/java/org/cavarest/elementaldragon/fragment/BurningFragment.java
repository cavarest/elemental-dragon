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

  // Dragon's Wrath constants
  private static final long DRAGONS_WRATH_COOLDOWN = 40000L; // 40 seconds
  private static final double DRAGONS_WRATH_DAMAGE = 8.0; // 4 hearts
  private static final double DRAGONS_WRATH_EXPLOSION_POWER = 2.0F;
  private static final double DRAGONS_WRATH_VELOCITY = 1.5;

  // Infernal Dominion constants
  private static final long INFERNAL_DOMINION_COOLDOWN = 60000L; // 60 seconds
  private static final double INFERNAL_DOMINION_RADIUS = 8.0;
  private static final int INFERNAL_DOMINION_DURATION = 100; // ticks
  private static final int INFERNAL_DOMINION_DAMAGE_PER_TICK = 2;

  // Visual constants
  private static final Color FIRE_COLOR = Color.fromRGB(255, 100, 0);
  private static final Color ORANGE_COLOR = Color.fromRGB(255, 165, 0);

  // Fragment metadata (Single Source of Truth)
  private static final Material MATERIAL = Material.BLAZE_POWDER;
  private static final NamedTextColor THEME_COLOR = NamedTextColor.RED;
  private static final String ELEMENT_NAME = "FIRE";

  // Ability definitions (Single Source of Truth)
  private final List<AbilityDefinition> abilities = List.of(
    new AbilityDefinition(1, "Dragon's Wrath", "fireball attack",
      List.of("wrath", "dragons-wrath"),
      "The flames of the ancient dragon consume your foes!"),
    new AbilityDefinition(2, "Infernal Dominion", "area fire",
      List.of("dominion", "infernal-dominion"),
      "The flames of the ancient dragon consume your foes!")
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
   * Execute Dragon's Wrath - launch a powerful fireball.
   *
   * @param player The player executing the ability
   */
  private void executeDragonsWrath(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection().normalize();

    // Spawn fireball
    Fireball fireball = player.getWorld().spawn(
      eyeLocation.add(direction.multiply(1.5)),
      Fireball.class
    );

    fireball.setShooter(player);
    fireball.setDirection(direction);
    fireball.setYield((float) DRAGONS_WRATH_EXPLOSION_POWER);
    fireball.setIsIncendiary(true);
    fireball.setVelocity(direction.multiply(DRAGONS_WRATH_VELOCITY));

    // Store custom data for damage handling
    fireball.getPersistentDataContainer().set(
      new org.bukkit.NamespacedKey(plugin, "dragons_wrath_damage"),
      org.bukkit.persistence.PersistentDataType.DOUBLE,
      DRAGONS_WRATH_DAMAGE
    );

    // Play ability sound
    playAbilitySound(
      player.getLocation(),
      Sound.ENTITY_BLAZE_SHOOT,
      1.0f,
      1.0f
    );

    // Show particles
    showAbilityParticles(player.getLocation(), 15);

    // Cooldown is set by FragmentManager.useFragmentAbility()

    player.sendMessage(
      Component.text("Dragon's Wrath unleashed! Fireball launched!", NamedTextColor.GREEN)
    );
  }

  /**
   * Execute Infernal Dominion - create a ring of fire around the player.
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

    // Create fire ring effect and damage entities
    new BukkitRunnable() {
      private int ticks = 0;

      @Override
      public void run() {
        if (ticks >= INFERNAL_DOMINION_DURATION) {
          cancel();
          return;
        }

        // Damage entities in radius
        for (Entity entity : player.getWorld().getNearbyEntities(
          center, INFERNAL_DOMINION_RADIUS, INFERNAL_DOMINION_RADIUS, INFERNAL_DOMINION_RADIUS
        )) {
          if (entity instanceof LivingEntity && entity != player) {
            LivingEntity living = (LivingEntity) entity;
            living.setFireTicks(10); // 0.5 seconds of fire
            living.damage(INFERNAL_DOMINION_DAMAGE_PER_TICK, player);
          }
        }

        // Spawn fire ring particles
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
   * Apply passive Fire Resistance effect.
   *
   * @param player The player
   */
  @Override
  protected void applyPassiveEffects(Player player) {
    // Apply FIRE_RESISTANCE potion effect (amplifier 0 = Fire Resistance I)
    player.addPotionEffect(
      new PotionEffect(
        PotionEffectType.FIRE_RESISTANCE,
        Integer.MAX_VALUE, // Permanent while equipped
        0, // Amplifier 0 = Fire Resistance I
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
   * Event handler for fireball damage.
   */
  @EventHandler
  public void onFireballDamage(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Fireball)) {
      return;
    }

    Fireball fireball = (Fireball) event.getDamager();
    org.bukkit.NamespacedKey damageKey = new org.bukkit.NamespacedKey(plugin, "dragons_wrath_damage");

    if (fireball.getPersistentDataContainer().has(damageKey, org.bukkit.persistence.PersistentDataType.DOUBLE)) {
      double customDamage = fireball.getPersistentDataContainer().get(
        damageKey, org.bukkit.persistence.PersistentDataType.DOUBLE);
      event.setDamage(customDamage);
    }
  }

  /**
   * Event handler for fireball impact.
   */
  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof Fireball)) {
      return;
    }

    Fireball fireball = (Fireball) event.getEntity();
    org.bukkit.NamespacedKey damageKey = new org.bukkit.NamespacedKey(plugin, "dragons_wrath_damage");

    if (!fireball.getPersistentDataContainer().has(damageKey, org.bukkit.persistence.PersistentDataType.DOUBLE)) {
      return; // Not a custom fireball
    }

    // Apply fire effect on hit
    if (event.getHitEntity() instanceof LivingEntity) {
      LivingEntity hit = (LivingEntity) event.getHitEntity();
      hit.setFireTicks(40); // 2 seconds of fire
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
