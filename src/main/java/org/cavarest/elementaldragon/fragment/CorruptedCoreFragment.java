package org.cavarest.elementaldragon.fragment;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
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

  // Dread Gaze constants (ORIGINAL SPECIFICATION)
  private static final long DREAD_GAZE_COOLDOWN = 180000L; // 3 minutes (original spec)
  private static final int DREAD_GAZE_DURATION = 80; // 4 seconds (80 ticks) - changed from 10s per Issue #20
  private static final int MAX_AMPLIFIER = 255; // Maximum effect level for complete freeze

  // Life Devourer constants (ORIGINAL SPECIFICATION)
  private static final long LIFE_DEVOURER_COOLDOWN = 120000L; // 2 minutes (120 seconds) (original spec)
  private static final int LIFE_DEVOURER_DURATION = 400; // 20 seconds (400 ticks) (original spec)
  private static final double LIFE_DEVOURER_STEAL_PERCENT = 0.25; // 25% health steal (Issue #28)

  // Metadata keys
  private static final String DREAD_GAZE_ACTIVE_KEY = "corrupted_dread_gaze_active";
  private static final String DREAD_GAZE_START_TIME_KEY = "corrupted_dread_gaze_start_time";
  private static final String LIFE_DEVOURER_ACTIVE_KEY = "corrupted_life_devourer_active";
  private static final String LIFE_DEVOURER_START_TIME_KEY = "corrupted_life_devourer_start_time";

  // Debuff metadata keys (set on VICTIMS when Dread Gaze hits them)
  private static final String DREAD_GAZE_DEBUFF_KEY = "corrupted_dread_gaze_debuff";
  private static final String DREAD_GAZE_DEBUFF_START_KEY = "corrupted_dread_gaze_debuff_start_time";
  private static final String DREAD_GAZE_FREEZE_LOCATION_KEY = "corrupted_dread_gaze_freeze_location";
  private static final String DREAD_GAZE_SATURATION_KEY = "corrupted_dread_gaze_saturation";

  // Attacker metadata keys (set on ATTACKER when they freeze someone with Dread Gaze)
  private static final String DREAD_GAZE_FOE_FROZEN_KEY = "corrupted_dread_gaze_foe_frozen";
  private static final String DREAD_GAZE_FOE_FROZEN_START_KEY = "corrupted_dread_gaze_foe_frozen_start_time";
  private static final String DREAD_GAZE_FOE_FROZEN_DURATION_KEY = "corrupted_dread_gaze_foe_frozen_duration";

  // PersistentDataContainer keys (for debuff persistence across rejoins - Issue #20)
  private final NamespacedKey DEBUFF_PERSIST_KEY;
  private final NamespacedKey DEBUFF_START_PERSIST_KEY;
  private final NamespacedKey FREEZE_LOCATION_PERSIST_KEY;
  // Metadata key for tracking Suspended Sustenance effect
  private static final String SUSPENDED_SUSTENANCE_KEY = "corrupted_suspended_sustenance_active";

  private final NamespacedKey SATURATION_PERSIST_KEY;

  // Fragment metadata (Single Source of Truth)
  // Using NETHER_STAR instead of HEAVY_CORE to avoid default right-click block placement behavior
  private static final Material MATERIAL = Material.NETHER_STAR;
  private static final NamedTextColor THEME_COLOR = NamedTextColor.DARK_PURPLE;
  private static final String ELEMENT_NAME = "VOID";

  // Ability definitions (Single Source of Truth)
  private final List<AbilityDefinition> abilities = List.of(
    new AbilityDefinition(1, "Dread Gaze", "blindness & slow",
      List.of("gaze", "dread-gaze"),
      "Dark energy blinds and slows your foe! 👁❄", "❄"),
    new AbilityDefinition(2, "Life Devourer", "health steal",
      List.of("devourer", "life-devourer"),
      "The void drains life force from your enemy! 👁🩸", "🩸")
  );

  private final ElementalDragon plugin;

  // Freeze task - processes frozen players every tick using teleportation
  // This prevents Paper anti-cheat from flagging as "flying" since we use server-initiated teleport
  private BukkitRunnable freezeTask;

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
        "Ability 1: Dread Gaze - Complete freeze on hit (3min cooldown)",
        "Ability 2: Life Devourer - Life steal from all damage (2min cooldown)",
        "",
        "Passive: Suspended Sustenance (no hunger) when equipped",
        "Passive: Invisible to creepers"
      )
    );
    this.plugin = plugin;

    // Initialize NamespacedKeys for persistent storage (only if plugin is real, not mocked for tests)
    // Mocked plugins have null getName() which causes NamespacedKey constructor to fail
    boolean isRealPlugin = plugin != null && plugin.getServer() != null;
    if (isRealPlugin) {
      this.DEBUFF_PERSIST_KEY = new NamespacedKey(plugin, "dread_gaze_debuff_persist");
      this.DEBUFF_START_PERSIST_KEY = new NamespacedKey(plugin, "dread_gaze_debuff_start_persist");
      this.FREEZE_LOCATION_PERSIST_KEY = new NamespacedKey(plugin, "dread_gaze_freeze_location_persist");
      this.SATURATION_PERSIST_KEY = new NamespacedKey(plugin, "dread_gaze_saturation_persist");
    } else {
      // For tests with null plugin or mocked plugin, use placeholder keys
      this.DEBUFF_PERSIST_KEY = null;
      this.DEBUFF_START_PERSIST_KEY = null;
      this.FREEZE_LOCATION_PERSIST_KEY = null;
      this.SATURATION_PERSIST_KEY = null;
    }

    // Start the freeze monitoring task (runs every tick)
    // Uses scheduler-based teleportation instead of PlayerMoveEvent cancellation
    // to prevent Paper anti-cheat false positives
    if (isRealPlugin) {
      startFreezeMonitoringTask();
    }
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
      "1. Dread Gaze - Blind nearby enemies for 10s (60s cooldown)\n" +
      "2. Life Devourer - Drain health from enemies, 25% stolen (90s cooldown)\n" +
      "Passive: Suspended Sustenance (no hunger) when equipped, invisible to creepers";
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
      Component.text("Passive: Suspended Sustenance granted!", NamedTextColor.GRAY)
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

    // Clear any active ability states (unstages READY TO STRIKE and other abilities)
    if (player.hasMetadata(DREAD_GAZE_ACTIVE_KEY)) {
      player.removeMetadata(DREAD_GAZE_ACTIVE_KEY, plugin);
    }
    if (player.hasMetadata(DREAD_GAZE_START_TIME_KEY)) {
      player.removeMetadata(DREAD_GAZE_START_TIME_KEY, plugin);
    }
    if (player.hasMetadata(DREAD_GAZE_FOE_FROZEN_KEY)) {
      player.removeMetadata(DREAD_GAZE_FOE_FROZEN_KEY, plugin);
    }
    if (player.hasMetadata(DREAD_GAZE_FOE_FROZEN_START_KEY)) {
      player.removeMetadata(DREAD_GAZE_FOE_FROZEN_START_KEY, plugin);
    }
    if (player.hasMetadata(DREAD_GAZE_DEBUFF_KEY)) {
      player.removeMetadata(DREAD_GAZE_DEBUFF_KEY, plugin);
    }
    if (player.hasMetadata(DREAD_GAZE_DEBUFF_START_KEY)) {
      player.removeMetadata(DREAD_GAZE_DEBUFF_START_KEY, plugin);
    }
    if (player.hasMetadata(DREAD_GAZE_FREEZE_LOCATION_KEY)) {
      player.removeMetadata(DREAD_GAZE_FREEZE_LOCATION_KEY, plugin);
    }
    if (player.hasMetadata(DREAD_GAZE_SATURATION_KEY)) {
      player.removeMetadata(DREAD_GAZE_SATURATION_KEY, plugin);
    }
    if (player.hasMetadata(LIFE_DEVOURER_ACTIVE_KEY)) {
      player.removeMetadata(LIFE_DEVOURER_ACTIVE_KEY, plugin);
    }
    if (player.hasMetadata(LIFE_DEVOURER_START_TIME_KEY)) {
      player.removeMetadata(LIFE_DEVOURER_START_TIME_KEY, plugin);
    }

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
   * Execute Dread Gaze - activate complete freeze on next hit.
   * Original Specification:
   * - Prevents victim from acting (moving, eating, attacking, etc.) upon being hit
   * - Single-target on-hit effect (activates on next melee attack)
   * - Cooldown: 3 minutes
   *
   * @param player The player executing the ability
   */
  private void executeDreadGaze(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    // Clear cooldown that FragmentManager set - we'll set it when target is hit instead
    CooldownManager cooldownManager = plugin.getCooldownManager();
    cooldownManager.setCooldown(player, FragmentType.CORRUPTED.getElement(), 1, 0);

    Location center = player.getLocation();

    // Mark player as having Dread Gaze active (but NOT start time yet)
    // Start time will be set when target is hit, enabling READY TO STRIKE display
    player.setMetadata(
      DREAD_GAZE_ACTIVE_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
    );

    // Play activation sound
    playAbilitySound(center, Sound.ENTITY_PHANTOM_SWOOP, 1.0f, 0.8f);
    playAbilitySound(center, Sound.ENTITY_ENDERMAN_AMBIENT, 0.5f, 0.5f);

    // Show void aura around player
    showVoidAura(player);

    // NOTE: Cooldown is NOT set here - it will be set when the target is hit
    // This allows "READY TO STRIKE" state to persist indefinitely until a hit occurs

    // Send feedback message
    player.sendMessage(
      Component.text("Dread Gaze activated! Your next hit will freeze the target completely!",
        NamedTextColor.DARK_PURPLE)
    );
  }

  /**
   * Execute Life Devourer - activate life steal for 20 seconds.
   * Original Specification:
   * - Applies "life steal": healing wielder half the damage dealt to any entity
   * - Active for 20 seconds
   * - Cooldown: 2 minutes
   *
   * @param player The player executing the ability
   */
  private void executeLifeDevourer(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    Location center = player.getLocation();

    // Mark player as having Life Devourer active (for event handler)
    player.setMetadata(
      LIFE_DEVOURER_ACTIVE_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
    );

    // Store activation timestamp for HUD countdown
    player.setMetadata(
      LIFE_DEVOURER_START_TIME_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis())
    );

    // Play activation sound
    playAbilitySound(center, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);

    player.sendMessage(
      Component.text("Life Devourer activated! Life steal active for 20 seconds!",
        NamedTextColor.DARK_PURPLE)
    );

    // Show activation particles
    showVoidAura(player);

    // Schedule effect removal after duration (400 ticks = 20 seconds)
    new BukkitRunnable() {
      @Override
      public void run() {
        if (player.isDead() || !player.isValid()) {
          cancel();
          return;
        }

        // IMPORTANT: Remove the active metadata FIRST (before sending message)
        // This ensures the HUD immediately transitions from ACTIVE to COOLDOWN state
        // when the ability expires, preventing display lag.
        if (player.hasMetadata(LIFE_DEVOURER_ACTIVE_KEY)) {
          player.removeMetadata(LIFE_DEVOURER_ACTIVE_KEY, plugin);
        }
        if (player.hasMetadata(LIFE_DEVOURER_START_TIME_KEY)) {
          player.removeMetadata(LIFE_DEVOURER_START_TIME_KEY, plugin);
        }

        // Now send the expiration message (after metadata is cleared)
        player.sendMessage(
          Component.text("Life Devourer has expired.", NamedTextColor.GRAY)
        );
      }
    }.runTaskLater(plugin, LIFE_DEVOURER_DURATION);

    // Cooldown is set by FragmentManager.useFragmentAbility()
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
   * Apply passive Suspended Sustenance effect (no hunger).
   *
   * @param player The player
   */
  @Override
  protected void applyPassiveEffects(Player player) {
    if (player == null) {
      return;
    }

    // Apply SATURATION potion effect (infinite duration, no particles)
    // Suspended Sustenance: hunger does not apply when fragment is equipped
    player.addPotionEffect(
      new PotionEffect(
        PotionEffectType.SATURATION,
        Integer.MAX_VALUE, // Permanent while equipped
        0, // Amplifier 0 = normal saturation
        false, // Not ambient (no particles)
        false // Don't show icon
      )
    );

    // Mark that we applied this effect (so we can properly remove it later)
    player.setMetadata(
      SUSPENDED_SUSTENANCE_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
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
   * Remove passive Suspended Sustenance effect.
   *
   * @param player The player
   */
  @Override
  protected void removePassiveEffects(Player player) {
    if (player == null) {
      return;
    }

    // Remove the SATURATION potion effect
    player.removePotionEffect(PotionEffectType.SATURATION);

    // Remove our tracking metadata
    player.removeMetadata(SUSPENDED_SUSTENANCE_KEY, plugin);

    // Force saturation to 0 to ensure hunger depletion resumes immediately
    // This is needed because the infinite SATURATION effect may have left
    // the saturation bar at a high value
    player.setSaturation(0);

    // Also reset food level to maximum if it was above normal
    // (SATURATION effect keeps food at 20 constantly)
    if (player.getFoodLevel() > 20) {
      player.setFoodLevel(20);
    }
  }

  /**
   * Event handler for creeper and enderman targeting prevention.
   * Passive Bonus: Void Shroud
   * - Creepers have reduced detection range (invisible to creepers)
   * - Endermen will not aggro when looked at
   *
   * @param event The entity target event
   */
  @EventHandler(priority = EventPriority.HIGH)
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
      // Cancel the targeting event - creeper won't target this player (invisible)
      event.setCancelled(true);

      // Visual feedback - void particles appear occasionally
      if (Math.random() < 0.05) { // 5% chance to avoid spam
        player.getWorld().spawnParticle(
          Particle.REVERSE_PORTAL,
          player.getLocation().add(0, 1, 0),
          3,
          0.3,
          0.3,
          0.3,
          0.02
        );
      }
    }

    // Check if the entity targeting is an enderman
    if (event.getEntity() instanceof org.bukkit.entity.Enderman) {
      // Cancel the targeting event - enderman won't aggro when looked at
      event.setCancelled(true);

      // Visual feedback - void particles appear occasionally
      if (Math.random() < 0.05) { // 5% chance to avoid spam
        player.getWorld().spawnParticle(
          Particle.REVERSE_PORTAL,
          player.getLocation().add(0, 1, 0),
          3,
          0.3,
          0.3,
          0.3,
          0.02
        );
      }
    }
  }

  /**
   * Event handler for Dread Gaze complete freeze on hit.
   * Original Specification:
   * - Applies complete freeze (prevents all actions) when player with active Dread Gaze hits a target
   * - Freeze effects: SLOW 255, MINING_FATIGUE 255, WEAKNESS 255, HUNGER 255
   * - Duration: 5 seconds (100 ticks)
   * - Single use (consumes Dread Gaze activation)
   *
   * @param event The entity damage event
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamageByEntityForDreadGaze(EntityDamageByEntityEvent event) {
    // Check if damager is a Player
    if (!(event.getDamager() instanceof Player)) {
      return;
    }

    Player attacker = (Player) event.getDamager();

    // Check if player has Corrupted Core equipped
    FragmentManager fragmentManager = plugin.getFragmentManager();
    if (fragmentManager.getEquippedFragment(attacker) != FragmentType.CORRUPTED) {
      return;
    }

    // Check if player has Dread Gaze active
    if (!attacker.hasMetadata(DREAD_GAZE_ACTIVE_KEY)) {
      return;
    }

    // Check if entity damaged is a LivingEntity
    if (!(event.getEntity() instanceof LivingEntity)) {
      return;
    }

    LivingEntity victim = (LivingEntity) event.getEntity();

    // Check if start time already set (prevents multiple hits from extending duration)
    if (attacker.hasMetadata(DREAD_GAZE_START_TIME_KEY)) {
      return;
    }

    // Set start time NOW (when target is hit) for countdown display
    // This transitions from READY TO STRIKE to ACTIVE state
    attacker.setMetadata(
      DREAD_GAZE_START_TIME_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis())
    );

    // Set cooldown NOW (when target is hit, not when activated)
    // This is a 3-minute cooldown for the Dread Gaze ability
    CooldownManager cooldownManager = plugin.getCooldownManager();
    cooldownManager.setCooldown(attacker, FragmentType.CORRUPTED.getElement(), 1, (int) DREAD_GAZE_COOLDOWN);

    // Remove the Dread Gaze active state (it's been consumed)
    attacker.removeMetadata(DREAD_GAZE_ACTIVE_KEY, plugin);

    // Apply complete freeze effects (all at max level for complete action prevention)
    victim.addPotionEffect(
      new PotionEffect(
        PotionEffectType.SLOWNESS,
        DREAD_GAZE_DURATION,
        MAX_AMPLIFIER,
        false, // Not ambient
        true,  // Show particles
        true   // Show icon
      )
    );
    victim.addPotionEffect(
      new PotionEffect(
        PotionEffectType.MINING_FATIGUE,
        DREAD_GAZE_DURATION,
        MAX_AMPLIFIER,
        false,
        true,
        true
      )
    );
    victim.addPotionEffect(
      new PotionEffect(
        PotionEffectType.WEAKNESS,
        DREAD_GAZE_DURATION,
        MAX_AMPLIFIER,
        false,
        true,
        true
      )
    );
    victim.addPotionEffect(
      new PotionEffect(
        PotionEffectType.HUNGER,
        DREAD_GAZE_DURATION,
        MAX_AMPLIFIER,
        false,
        true,
        true
      )
    );

    // Mark victim with debuff metadata for HUD display
    victim.setMetadata(
      DREAD_GAZE_DEBUFF_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
    );
    victim.setMetadata(
      DREAD_GAZE_DEBUFF_START_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis())
    );

    // Store freeze location (for scheduler-based position locking)
    // This prevents Paper anti-cheat from flagging as "flying" since we use teleport
    Location freezeLocation = victim.getLocation().clone();
    victim.setMetadata(
      DREAD_GAZE_FREEZE_LOCATION_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, freezeLocation)
    );

    // Store initial saturation value (for saturation freezing)
    // Only applies to players, not mobs
    float initialSaturation = 0.0f;
    if (victim instanceof Player) {
      initialSaturation = ((Player) victim).getSaturation();
      victim.setMetadata(
        DREAD_GAZE_SATURATION_KEY,
        new org.bukkit.metadata.FixedMetadataValue(plugin, initialSaturation)
      );
    }

    // PERSISTENCE: Save debuff data to PersistentDataContainer for players
    // This allows the freeze effect to persist across rejoins (Issue #20)
    if (victim instanceof Player && DEBUFF_PERSIST_KEY != null) {
      Player victimPlayer = (Player) victim;
      PersistentDataContainer pdc = victimPlayer.getPersistentDataContainer();
      long startTime = System.currentTimeMillis();

      pdc.set(DEBUFF_PERSIST_KEY, PersistentDataType.BYTE, (byte) 1);
      pdc.set(DEBUFF_START_PERSIST_KEY, PersistentDataType.LONG, startTime);
      pdc.set(FREEZE_LOCATION_PERSIST_KEY, PersistentDataType.STRING,
              serializeLocation(freezeLocation));
      pdc.set(SATURATION_PERSIST_KEY, PersistentDataType.FLOAT, initialSaturation);
    }

    // Mark attacker with foe frozen metadata for HUD countdown display
    // This allows the attacker to see how much longer their target remains frozen
    attacker.setMetadata(
      DREAD_GAZE_FOE_FROZEN_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
    );
    attacker.setMetadata(
      DREAD_GAZE_FOE_FROZEN_START_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis())
    );
    // Store duration in metadata as single source of truth for HUD display
    // DREAD_GAZE_DURATION is in ticks, convert to seconds for HUD
    attacker.setMetadata(
      DREAD_GAZE_FOE_FROZEN_DURATION_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, DREAD_GAZE_DURATION / 20) // ticks to seconds
    );

    // Update HUD to show the foe frozen countdown
    if (plugin.getHudManager() != null) {
      plugin.getHudManager().updatePlayerHud(attacker);
    }

    // Schedule debuff removal after duration
    new BukkitRunnable() {
      @Override
      public void run() {
        // Clean up victim's debuff metadata
        if (victim.isValid() && !victim.isDead()) {
          victim.removeMetadata(DREAD_GAZE_DEBUFF_KEY, plugin);
          victim.removeMetadata(DREAD_GAZE_DEBUFF_START_KEY, plugin);
          victim.removeMetadata(DREAD_GAZE_FREEZE_LOCATION_KEY, plugin);
          victim.removeMetadata(DREAD_GAZE_SATURATION_KEY, plugin);

          // PERSISTENCE: Remove debuff data from PersistentDataContainer
          if (victim instanceof Player && DEBUFF_PERSIST_KEY != null) {
            PersistentDataContainer pdc = ((Player) victim).getPersistentDataContainer();
            pdc.remove(DEBUFF_PERSIST_KEY);
            pdc.remove(DEBUFF_START_PERSIST_KEY);
            pdc.remove(FREEZE_LOCATION_PERSIST_KEY);
            pdc.remove(SATURATION_PERSIST_KEY);
          }
        }

        // Clean up attacker's metadata (both READY TO STRIKE and foe frozen)
        if (attacker.isValid() && !attacker.isDead()) {
          attacker.removeMetadata(DREAD_GAZE_START_TIME_KEY, plugin);
          attacker.removeMetadata(DREAD_GAZE_FOE_FROZEN_KEY, plugin);
          attacker.removeMetadata(DREAD_GAZE_FOE_FROZEN_START_KEY, plugin);
          attacker.removeMetadata(DREAD_GAZE_FOE_FROZEN_DURATION_KEY, plugin);

          // Update HUD to remove the foe frozen countdown
          if (plugin.getHudManager() != null) {
            plugin.getHudManager().updatePlayerHud(attacker);
          }
        }
      }
    }.runTaskLater(plugin, DREAD_GAZE_DURATION);

    // Show dark void particles around victim
    victim.getWorld().spawnParticle(
      Particle.REVERSE_PORTAL,
      victim.getLocation().add(0, 1, 0),
      30,
      0.5,
      1.0,
      0.5,
      0.1
    );

    // Play freeze sound
    victim.getWorld().playSound(
      victim.getLocation(),
      Sound.ENTITY_ENDERMAN_SCREAM,
      1.0f,
      0.5f
    );

    // Send feedback messages
    attacker.sendMessage(
      Component.text(victim.getName() + " is completely frozen now! 👁❄",
        NamedTextColor.DARK_PURPLE)
    );

    if (victim instanceof Player) {
      ((Player) victim).sendMessage(
        Component.text("You have been frozen by Dread Gaze!", NamedTextColor.RED)
      );
    }
  }

  /**
   * Event handler for Life Devourer life steal on damage.
   * Original Specification:
   * - Applies life steal: healing wielder half the damage dealt to any entity
   * - Active for 20 seconds after activation
   * - Triggers on ANY damage dealt by player (not limited by range or entity type)
   *
   * Uses HIGHEST priority to ensure we process the event before it might be cancelled.
   * Uses getDamage() instead of getFinalDamage() to get the original damage value.
   *
   * @param event The entity damage event
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDamageByEntityForLifeDevourer(EntityDamageByEntityEvent event) {
    // Check if damager is a Player
    if (!(event.getDamager() instanceof Player)) {
      return;
    }

    Player attacker = (Player) event.getDamager();

    // Check if player has Corrupted Core equipped
    FragmentManager fragmentManager = plugin.getFragmentManager();
    if (fragmentManager.getEquippedFragment(attacker) != FragmentType.CORRUPTED) {
      return;
    }

    // Check if player has Life Devourer active
    if (!attacker.hasMetadata(LIFE_DEVOURER_ACTIVE_KEY)) {
      return;
    }

    // Get the ORIGINAL damage (before event cancellation/modification)
    // Using getDamage() instead of getFinalDamage() ensures we still heal even if event is cancelled
    double damage = event.getDamage();

    // Skip if damage is 0 or less
    if (damage <= 0) {
      return;
    }

    // Calculate life steal (50% of damage)
    double healing = damage * LIFE_DEVOURER_STEAL_PERCENT;

    // Apply healing to player
    double currentHealth = attacker.getHealth();
    double maxHealth = attacker.getMaxHealth();
    double actualHealing = Math.min(healing, maxHealth - currentHealth);
    double newHealth = Math.min(maxHealth, currentHealth + healing);
    attacker.setHealth(newHealth);

    // Get victim name for the message
    String victimName = event.getEntity().getName();

    // Format healing amount to 1 decimal place
    String healingAmount = String.format("%.1f", actualHealing);

    // Send dragonic life transfer message with healing amount
    attacker.sendMessage(
      Component.text("The ancient dragon drains " + healingAmount + "❤ from " + victimName + " and bestows it upon you! 👁💫",
        NamedTextColor.DARK_PURPLE)
    );

    // Visual feedback - show health steal particles
    showHealthStealParticles(attacker);

    // Show drain transfer particles from victim to attacker
    if (event.getEntity() instanceof LivingEntity) {
      showDrainTransferParticles(event.getEntity().getLocation(), attacker.getLocation());
    }

    // Play drain sound
    playAbilitySound(
      attacker.getLocation(),
      Sound.ENTITY_ENDERMAN_HURT,
      0.3f,
      0.5f
    );
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
   * Start the freeze monitoring task.
   * Runs every tick to teleport frozen players back to their freeze location
   * and reset their saturation to the stored value.
   *
   * NATIVE APPROACH: Uses scheduler-based teleportation instead of
   * PlayerMoveEvent cancellation to prevent Paper anti-cheat false positives.
   * Since the movement is server-initiated (teleport), anti-cheat doesn't flag it.
   */
  private void startFreezeMonitoringTask() {
    // Guard against null plugin (for tests)
    if (plugin == null) {
      return;
    }

    freezeTask = new BukkitRunnable() {
      @Override
      public void run() {
        // Process all online players
        for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
          // Check if player is frozen by Dread Gaze
          if (!player.hasMetadata(DREAD_GAZE_DEBUFF_KEY)) {
            continue;
          }

          // Check if player has valid freeze location stored
          if (!player.hasMetadata(DREAD_GAZE_FREEZE_LOCATION_KEY)) {
            continue;
          }

          // Get stored freeze location
          Object locationObj = player.getMetadata(DREAD_GAZE_FREEZE_LOCATION_KEY).get(0).value();
          if (!(locationObj instanceof Location)) {
            continue;
          }

          Location freezeLocation = (Location) locationObj;

          // Teleport player back to freeze location if they moved
          // (allowing rotation/looking around, preventing position change)
          Location current = player.getLocation();
          if (current.getX() != freezeLocation.getX() ||
              current.getY() != freezeLocation.getY() ||
              current.getZ() != freezeLocation.getZ()) {
            // Teleport back to freeze location (preserves pitch/yaw for looking around)
            player.teleport(freezeLocation);
          }

          // Reset saturation to stored value (for players)
          if (player.hasMetadata(DREAD_GAZE_SATURATION_KEY)) {
            Object saturationObj = player.getMetadata(DREAD_GAZE_SATURATION_KEY).get(0).value();
            if (saturationObj instanceof Float) {
              float storedSaturation = (Float) saturationObj;
              player.setSaturation(storedSaturation);
            }
          }
        }
      }
    };

    // Run every tick (20 times per second)
    freezeTask.runTaskTimer(plugin, 0L, 1L);
  }

  /**
   * Get the plugin instance.
   *
   * @return The plugin
   */
  public ElementalDragon getPlugin() {
    return plugin;
  }

  // ===== Dread Gaze Freeze Event Handlers (Issue 3) =====

  /**
   * Prevent frozen players from placing blocks.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockPlaceWhileFrozen(org.bukkit.event.block.BlockPlaceEvent event) {
    Player player = event.getPlayer();

    // Check if player is frozen by Dread Gaze (checks VICTIM's debuff metadata)
    if (player.hasMetadata(DREAD_GAZE_DEBUFF_KEY)) {
      event.setCancelled(true);
      player.sendMessage(
        Component.text("⛶ You cannot place blocks while frozen by Dread Gaze!", NamedTextColor.DARK_PURPLE)
      );
    }
  }

  /**
   * Prevent frozen players from breaking blocks.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockBreakWhileFrozen(org.bukkit.event.block.BlockBreakEvent event) {
    Player player = event.getPlayer();

    // Check if player is frozen by Dread Gaze (checks VICTIM's debuff metadata)
    if (player.hasMetadata(DREAD_GAZE_DEBUFF_KEY)) {
      event.setCancelled(true);
      player.sendMessage(
        Component.text("⛶ You cannot break blocks while frozen by Dread Gaze!", NamedTextColor.DARK_PURPLE)
      );
    }
  }

  /**
   * Prevent frozen players from interacting with blocks/items.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerInteractWhileFrozen(org.bukkit.event.player.PlayerInteractEvent event) {
    Player player = event.getPlayer();

    // Check if player is frozen by Dread Gaze (checks VICTIM's debuff metadata)
    if (player.hasMetadata(DREAD_GAZE_DEBUFF_KEY)) {
      // Cancel all interactions (right-click actions)
      if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR ||
          event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK ||
          event.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_AIR ||
          event.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK) {
        event.setCancelled(true);
        player.sendMessage(
          Component.text("⛶ You cannot interact while frozen by Dread Gaze!", NamedTextColor.DARK_PURPLE)
        );
      }
    }
  }

  // ===== Debuff Persistence (Issue #20) =====

  /**
   * Restore freeze debuff when a player rejoins.
   * Checks PersistentDataContainer for active debuff and reapplies it.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoinRestoreDebuff(PlayerJoinEvent event) {
    // Skip if keys are null (tests with null plugin)
    if (DEBUFF_PERSIST_KEY == null) {
      return;
    }

    Player player = event.getPlayer();
    PersistentDataContainer pdc = player.getPersistentDataContainer();

    // Check if player has an active debuff stored
    if (!pdc.has(DEBUFF_PERSIST_KEY, PersistentDataType.BYTE)) {
      return; // No debuff to restore
    }

    Long startTime = pdc.get(DEBUFF_START_PERSIST_KEY, PersistentDataType.LONG);
    String locationStr = pdc.get(FREEZE_LOCATION_PERSIST_KEY, PersistentDataType.STRING);
    Float saturation = pdc.get(SATURATION_PERSIST_KEY, PersistentDataType.FLOAT);

    if (startTime == null) {
      return; // Invalid data, clear it
    }

    // Calculate remaining duration
    long elapsed = System.currentTimeMillis() - startTime;
    long remainingMillis = DREAD_GAZE_DURATION * 50L - elapsed; // ticks to ms

    if (remainingMillis <= 0) {
      // Debuff expired while player was offline, clear it
      pdc.remove(DEBUFF_PERSIST_KEY);
      pdc.remove(DEBUFF_START_PERSIST_KEY);
      pdc.remove(FREEZE_LOCATION_PERSIST_KEY);
      pdc.remove(SATURATION_PERSIST_KEY);
      return;
    }

    // Restore the debuff metadata
    player.setMetadata(DREAD_GAZE_DEBUFF_KEY, new org.bukkit.metadata.FixedMetadataValue(plugin, true));
    player.setMetadata(DREAD_GAZE_DEBUFF_START_KEY, new org.bukkit.metadata.FixedMetadataValue(plugin, startTime));

    // Restore freeze location
    if (locationStr != null) {
      Location freezeLocation = deserializeLocation(locationStr);
      if (freezeLocation != null) {
        player.setMetadata(DREAD_GAZE_FREEZE_LOCATION_KEY,
                         new org.bukkit.metadata.FixedMetadataValue(plugin, freezeLocation));
      }
    }

    // Restore saturation value
    if (saturation != null) {
      player.setMetadata(DREAD_GAZE_SATURATION_KEY,
                       new org.bukkit.metadata.FixedMetadataValue(plugin, saturation));
    }

    // Reapply potion effects
    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, (int) remainingMillis / 50,
                                           MAX_AMPLIFIER, false, true, true));
    player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, (int) remainingMillis / 50,
                                           MAX_AMPLIFIER, false, true, true));
    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (int) remainingMillis / 50,
                                           MAX_AMPLIFIER, false, true, true));
    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, (int) remainingMillis / 50,
                                           MAX_AMPLIFIER, false, true, true));

    // Schedule cleanup for remaining duration
    new BukkitRunnable() {
      @Override
      public void run() {
        if (!player.isOnline()) {
          return;
        }
        player.removeMetadata(DREAD_GAZE_DEBUFF_KEY, plugin);
        player.removeMetadata(DREAD_GAZE_DEBUFF_START_KEY, plugin);
        player.removeMetadata(DREAD_GAZE_FREEZE_LOCATION_KEY, plugin);
        player.removeMetadata(DREAD_GAZE_SATURATION_KEY, plugin);

        PersistentDataContainer pdc2 = player.getPersistentDataContainer();
        pdc2.remove(DEBUFF_PERSIST_KEY);
        pdc2.remove(DEBUFF_START_PERSIST_KEY);
        pdc2.remove(FREEZE_LOCATION_PERSIST_KEY);
        pdc2.remove(SATURATION_PERSIST_KEY);
      }
    }.runTaskLater(plugin, remainingMillis / 50L);

    player.sendMessage(
      Component.text("⚠ You are still frozen by Dread Gaze from before you disconnected!", NamedTextColor.DARK_PURPLE)
    );
  }

  /**
   * Serialize a Location to a String for storage in PersistentDataContainer.
   */
  private String serializeLocation(Location loc) {
    if (loc == null) {
      return "";
    }
    return String.format("%.2f,%.2f,%.2f,%s,%.2f,%.2f",
                       loc.getX(), loc.getY(), loc.getZ(),
                       loc.getWorld() != null ? loc.getWorld().getName() : "unknown",
                       loc.getYaw(), loc.getPitch());
  }

  /**
   * Deserialize a Location from a String stored in PersistentDataContainer.
   */
  private Location deserializeLocation(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    try {
      String[] parts = str.split(",");
      if (parts.length != 6) {
        return null;
      }
      double x = Double.parseDouble(parts[0]);
      double y = Double.parseDouble(parts[1]);
      double z = Double.parseDouble(parts[2]);
      String worldName = parts[3];
      float yaw = Float.parseFloat(parts[4]);
      float pitch = Float.parseFloat(parts[5]);

      org.bukkit.World world = plugin.getServer().getWorld(worldName);
      if (world == null) {
        return null;
      }

      return new Location(world, x, y, z, yaw, pitch);
    } catch (NumberFormatException e) {
      plugin.getLogger().warning("Failed to deserialize location: " + str);
      return null;
    }
  }
}
