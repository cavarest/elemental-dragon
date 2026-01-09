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
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

/**
 * Corrupted Core fragment implementation providing void-based abilities.
 *
 * Active Abilities:
 * - Dread Gaze: Blindness effect on nearby enemies
 * - Life Devourer: Health steal from nearby enemies
 *
 * Passive Bonus: Night Vision and creeper avoidance
 */
public class CorruptedCoreFragment extends AbstractFragment implements Listener {

  // Dread Gaze constants
  private static final long DREAD_GAZE_COOLDOWN = 60000L; // 60 seconds
  private static final int DREAD_GAZE_DURATION = 100; // 5 seconds (100 ticks)
  private static final double DREAD_GAZE_RADIUS = 10.0; // 10 blocks
  private static final int BLINDNESS_AMPLIFIER = 1; // Blindness II

  // Life Devourer constants
  private static final long LIFE_DEVOURER_COOLDOWN = 90000L; // 90 seconds
  private static final int LIFE_DEVOURER_DURATION = 160; // 8 seconds (160 ticks)
  private static final double LIFE_DEVOURER_RANGE = 8.0; // 8 blocks
  private static final double LIFE_DEVOURER_DRAIN = 1.0; // 0.5 hearts per tick (1.0 damage)
  private static final double LIFE_DEVOURER_STEAL_PERCENT = 0.5; // 50% health steal

  // Fragment metadata (Single Source of Truth)
  private static final Material MATERIAL = Material.NETHER_STAR;
  private static final NamedTextColor THEME_COLOR = NamedTextColor.DARK_PURPLE;
  private static final String ELEMENT_NAME = "VOID";

  // Ability definitions (Single Source of Truth)
  private final List<AbilityDefinition> abilities = List.of(
    new AbilityDefinition(1, "Dread Gaze", "blindness & slow",
      List.of("gaze", "dread-gaze"),
      "Dark energy blinds and slows your foe!"),
    new AbilityDefinition(2, "Life Devourer", "health steal",
      List.of("devourer", "life-devourer"),
      "The void drains life force from your enemy!")
  );

  private final ElementalDragon plugin;

  /**
   * Create a new Corrupted Core fragment.
   *
   * @param plugin The plugin instance
   */
  public CorruptedCoreFragment(ElementalDragon plugin) {
    super(
      plugin,
      FragmentType.CORRUPTED,
      DREAD_GAZE_COOLDOWN, // Use Dread Gaze cooldown as default
      Arrays.asList(
        "Ability 1: Dread Gaze - Blind nearby enemies (60s cooldown)",
        "Ability 2: Life Devourer - Drain health from enemies (90s cooldown)",
        "",
        "Passive: Night Vision when equipped",
        "Passive: Invisible to creepers"
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
      "1. Dread Gaze - Blind nearby enemies for 5s (60s cooldown)\n" +
      "2. Life Devourer - Drain health from enemies, 50% stolen (90s cooldown)\n" +
      "Passive: Night Vision when equipped, invisible to creepers";
  }

  @Override
  public void activate(Player player) {
    if (player == null) {
      return;
    }

    // Check permission
    if (!player.hasPermission("elementaldragon.fragment.corrupted")) {
      player.sendMessage(
        Component.text("You do not have permission to use the Corrupted Core!",
          NamedTextColor.RED)
      );
      return;
    }

    // Apply passive effects (Night Vision)
    applyPassiveEffects(player);

    // Play activation sound
    playActivationSound(player);

    // Show activation particles
    showActivationParticles(player);

    // Send activation message
    player.sendMessage(
      Component.text(getName() + " activated!", NamedTextColor.DARK_PURPLE)
    );
    player.sendMessage(
      Component.text("Passive: Night Vision granted!", NamedTextColor.GRAY)
    );
    player.sendMessage(
      Component.text("Use /corrupt 1 or /corrupt 2 to use abilities",
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
    if (!player.hasPermission("elementaldragon.fragment.corrupted")) {
      player.sendMessage(
        Component.text("You do not have permission to use Corrupted Core abilities!",
          NamedTextColor.DARK_RED)
      );
      return;
    }

    switch (abilityNumber) {
      case 1:
        executeDreadGaze(player);
        break;
      case 2:
        executeLifeDevourer(player);
        break;
      default:
        player.sendMessage(
          Component.text("Unknown ability number: " + abilityNumber,
            NamedTextColor.RED)
        );
        player.sendMessage(
          Component.text("Use /corrupt 1 or /corrupt 2", NamedTextColor.GRAY)
        );
    }
  }

  /**
   * Execute Dread Gaze - blind and wither nearby enemies.
   *
   * @param player The player executing the ability
   * @return true if successful
   */
  private void executeDreadGaze(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location center = player.getLocation();

    // Find all hostile entities within range
    int affectedCount = 0;
    for (Entity entity : player.getWorld().getNearbyEntities(
      center,
      DREAD_GAZE_RADIUS,
      DREAD_GAZE_RADIUS,
      DREAD_GAZE_RADIUS
    )) {
      // Only affect hostile mobs (not players, not animals)
      if (!(entity instanceof LivingEntity)) {
        continue;
      }

      LivingEntity livingEntity = (LivingEntity) entity;

      // Skip if it's the player
      if (entity == player) {
        continue;
      }

      // Check if entity is hostile (has attack damage or is monster)
      if (isHostile(livingEntity)) {
        // Apply Blindness II effect
        livingEntity.addPotionEffect(
          new PotionEffect(
            PotionEffectType.BLINDNESS,
            DREAD_GAZE_DURATION,
            BLINDNESS_AMPLIFIER,
            true,
            true
          )
        );

        // Show void particles on target
        showVoidParticles(livingEntity.getLocation());

        // Play creepy sound
        playAbilitySound(
          livingEntity.getLocation(),
          Sound.ENTITY_ENDERMAN_SCREAM,
          0.5f,
          0.8f
        );

        affectedCount++;
      }
    }

    // Play activation sound
    playAbilitySound(center, Sound.ENTITY_PHANTOM_SWOOP, 1.0f, 0.8f);

    // Show void aura around player
    showVoidAura(player);

    // Cooldown is set by FragmentManager.useFragmentAbility()

    // Send feedback message
    player.sendMessage(
      Component.text("Dread Gaze activated! " + affectedCount + " enemies blinded!",
        NamedTextColor.DARK_PURPLE)
    );
  }

  /**
   * Execute Life Devourer - drain health from nearby enemies.
   *
   * @param player The player executing the ability
   * @return true if successful
   */
  private void executeLifeDevourer(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location center = player.getLocation();

    // Play activation sound
    playAbilitySound(center, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);

    player.sendMessage(
      Component.text("Life Devourer activated! Draining life force...",
        NamedTextColor.DARK_PURPLE)
    );

    // Create draining effect over time
    final double totalDrained = 0.0;

    new BukkitRunnable() {
      private int ticks = 0;

      @Override
      public void run() {
        if (ticks >= LIFE_DEVOURER_DURATION) {
          // Effect ended
          cancel();
          return;
        }

        if (player.isDead() || !player.isValid()) {
          cancel();
          return;
        }

        // Apply drain every 10 ticks (0.5 seconds)
        if (ticks % 10 == 0) {
          double stolen = applyLifeDrain(player);
          if (stolen > 0) {
            // Visual feedback for health gain
            showHealthStealParticles(player);
          }
        }

        // Show drain particles around player
        showDrainParticles(player.getLocation());

        ticks++;
      }
    }.runTaskTimer(plugin, 0L, 1L);

    // Cooldown is set by FragmentManager.useFragmentAbility()
  }

  /**
   * Apply life drain effect to nearby enemies.
   *
   * @param player The player who activated the ability
   * @return Total health stolen
   */
  private double applyLifeDrain(Player player) {
    Location center = player.getLocation();
    double totalStolen = 0.0;

    for (Entity entity : player.getWorld().getNearbyEntities(
      center,
      LIFE_DEVOURER_RANGE,
      LIFE_DEVOURER_RANGE,
      LIFE_DEVOURER_RANGE
    )) {
      // Only affect living entities that are not the player
      if (!(entity instanceof LivingEntity) || entity == player) {
        continue;
      }

      LivingEntity livingEntity = (LivingEntity) entity;

      // Only drain hostile mobs
      if (!isHostile(livingEntity)) {
        continue;
      }

      // Deal damage (0.5 hearts per tick)
      double currentHealth = livingEntity.getHealth();
      double damage = LIFE_DEVOURER_DRAIN;
      double newHealth = Math.max(0, currentHealth - damage);
      livingEntity.setHealth(newHealth);

      // Calculate stolen health (50%)
      double stolen = damage * LIFE_DEVOURER_STEAL_PERCENT;
      totalStolen += stolen;

      // Apply stolen health to player
      double playerHealth = player.getHealth();
      double maxHealth = player.getMaxHealth();
      double newPlayerHealth = Math.min(maxHealth, playerHealth + stolen);
      player.setHealth(newPlayerHealth);

      // Show drain particles
      showDrainTransferParticles(livingEntity.getLocation(), player.getLocation());

      // Play drain sound
      playAbilitySound(
        livingEntity.getLocation(),
        Sound.ENTITY_ENDERMAN_HURT,
        0.3f,
        0.5f
      );
    }

    return totalStolen;
  }

  /**
   * Check if an entity is hostile (should be affected by abilities).
   *
   * @param entity The entity to check
   * @return true if the entity is hostile
   */
  private boolean isHostile(LivingEntity entity) {
    // Check if it's a monster type
    if (entity instanceof org.bukkit.entity.Monster) {
      return true;
    }

    // Check for hostile mobs by name/type
    org.bukkit.entity.EntityType type = entity.getType();
    switch (type) {
      case ZOMBIE:
      case SKELETON:
      case CREEPER:
      case SPIDER:
      case CAVE_SPIDER:
      case ENDERMAN:
      case SILVERFISH:
      case BLAZE:
      case WITHER_SKELETON:
      case ZOMBIE_VILLAGER:
      case HUSK:
      case STRAY:
      case PHANTOM:
      case DROWNED:
      case PILLAGER:
      case VEX:
      case VINDICATOR:
      case EVOKER:
      case SHULKER:
      case GUARDIAN:
      case ELDER_GUARDIAN:
      case WITHER:
      case ENDER_DRAGON:
      case BOGGED:
      case ARMADILLO:
      case WARDEN:
        return true;
      default:
        return false;
    }
  }

  /**
   * Apply passive Night Vision effect.
   *
   * @param player The player
   */
  @Override
  protected void applyPassiveEffects(Player player) {
    if (player == null) {
      return;
    }

    // Apply NIGHT_VISION potion effect (infinite duration, no particles)
    player.addPotionEffect(
      new PotionEffect(
        PotionEffectType.NIGHT_VISION,
        Integer.MAX_VALUE, // Permanent while equipped
        0, // Amplifier 0 = normal night vision
        false, // Not ambient (no particles)
        false // Don't show icon
      )
    );

    // Show void particles around player
    player.getWorld().spawnParticle(
      Particle.REVERSE_PORTAL,
      player.getLocation().add(0, 1, 0),
      5,
      0.3,
      0.3,
      0.3,
      0.02
    );
  }

  /**
   * Remove passive Night Vision effect.
   *
   * @param player The player
   */
  @Override
  protected void removePassiveEffects(Player player) {
    if (player == null) {
      return;
    }
    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
  }

  /**
   * Event handler for creeper targeting - prevents creepers from targeting fragment-wearers.
   *
   * @param event The entity target event
   */
  @EventHandler
  public void onEntityTarget(EntityTargetEvent event) {
    // Check if the target is a player with this fragment equipped
    if (!(event.getTarget() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getTarget();

    // Check if player has Corrupted Core equipped
    FragmentManager fragmentManager = plugin.getFragmentManager();
    if (fragmentManager.getEquippedFragment(player) != FragmentType.CORRUPTED) {
      return;
    }

    // Check if the entity targeting is a creeper
    if (event.getEntity() instanceof Creeper) {
      // Cancel the targeting event - creeper won't target this player
      event.setCancelled(true);
    }
  }

  /**
   * Play activation sound for corrupted core.
   *
   * @param player The player
   */
  @Override
  protected void playActivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.ENTITY_PHANTOM_SWOOP,
      1.0f,
      0.8f
    );
    player.getWorld().playSound(
      location,
      Sound.AMBIENT_NETHER_WASTES_MOOD,
      0.5f,
      0.5f
    );
  }

  /**
   * Play deactivation sound for corrupted core.
   *
   * @param player The player
   */
  @Override
  protected void playDeactivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.ENTITY_ENDERMAN_TELEPORT,
      0.8f,
      0.6f
    );
  }

  /**
   * Show activation particles for corrupted core.
   *
   * @param player The player
   */
  @Override
  protected void showActivationParticles(Player player) {
    ParticleFX.spawnCorruptedActivation(player.getLocation().add(0, 1, 0));
  }

  /**
   * Show void particles at a location.
   *
   * @param location The location
   */
  private void showVoidParticles(Location location) {
    ParticleFX.spawnVoidParticles(location);
  }

  /**
   * Show void aura around player.
   *
   * @param player The player
   */
  private void showVoidAura(Player player) {
    ParticleFX.spawnVoidAura(player.getLocation());
  }

  /**
   * Show drain particles around a location.
   *
   * @param location The location
   */
  private void showDrainParticles(Location location) {
    ParticleFX.spawnDrainParticles(location);
  }

  /**
   * Show particles indicating health transfer.
   *
   * @param from Source location
   * @param to Target location
   */
  private void showDrainTransferParticles(Location from, Location to) {
    ParticleFX.spawnHealthTransfer(from, to);
  }

  /**
   * Show particles when health is stolen.
   *
   * @param player The player who gained health
   */
  private void showHealthStealParticles(Player player) {
    player.getWorld().spawnParticle(
      Particle.HEART,
      player.getLocation().add(0, 1.5, 0),
      2,
      0.3,
      0.3,
      0.3,
      0.1
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
