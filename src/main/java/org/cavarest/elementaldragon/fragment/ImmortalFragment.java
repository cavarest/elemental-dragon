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
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Immortal Fragment implementation providing earth-based defensive abilities.
 *
 * Active Abilities:
 * - Draconic Reflex: Temporary damage reduction with melee attack reflection
 * - Essence Rebirth: Enhanced respawn with diamond armor and supplies
 *
 * Passive Bonus: Acts as permanent Totem of Undying when equipped
 */
public class ImmortalFragment extends AbstractFragment implements Listener {

  // Draconic Reflex constants (ORIGINAL SPECIFICATION)
  private static final long DRACONIC_REFLEX_COOLDOWN = 120000L; // 2 minutes (original spec)
  private static final int DRACONIC_REFLEX_DURATION = 300; // 15 seconds = 300 ticks (original spec)
  private static final double DRACONIC_REFLEX_DODGE_CHANCE = 0.2; // 20% (1/5 chance)

  // Essence Rebirth constants (ORIGINAL SPECIFICATION)
  private static final long ESSENCE_REBIRTH_COOLDOWN = 480000L; // 8 minutes (480,000ms) (original spec)
  private static final int ESSENCE_REBIRTH_DURATION = 600; // 30 seconds = 600 ticks (original spec)

  // Visual constants
  private static final Color BROWN_COLOR = Color.fromRGB(139, 69, 19);
  private static final Color GREEN_COLOR = Color.fromRGB(34, 139, 34);
  private static final Color GOLD_COLOR = Color.fromRGB(255, 215, 0);

  // Fragment metadata (Single Source of Truth)
  private static final Material MATERIAL = Material.DIAMOND;
  private static final NamedTextColor THEME_COLOR = NamedTextColor.GREEN;
  private static final String ELEMENT_NAME = "EARTH";

  // Ability definitions (Single Source of Truth)
  private final List<AbilityDefinition> abilities = List.of(
    new AbilityDefinition(1, "Draconic Reflex", "damage reduction",
      List.of("reflex", "draconic-reflex"),
      "A protective shield of ancient dragon energy surrounds you! 🔰✨", "✨"),
    new AbilityDefinition(2, "Essence Rebirth", "death protection",
      List.of("rebirth", "essence-rebirth"),
      "The dragon's essence will restore you upon death! 🔰🐉", "🐉")
  );

  // Metadata keys
  private static final String DRACONIC_REFLEX_ACTIVE_KEY = "immortal_draconic_reflex_active";
  private static final String DRACONIC_REFLEX_START_TIME_KEY = "immortal_draconic_reflex_start_time";
  private static final String ESSENCE_REBIRTH_ACTIVATED_KEY = "immortal_essence_rebirth_activated";
  private static final String ESSENCE_REBIRTH_START_TIME_KEY = "immortal_essence_rebirth_start_time";

  private final ElementalDragon plugin;
  private final Random random;

  /**
   * Create a new Immortal Fragment.
   *
   * @param plugin The plugin instance
   */
  public ImmortalFragment(ElementalDragon plugin) {
    super(
      plugin,
      FragmentType.IMMORTAL,
      DRACONIC_REFLEX_COOLDOWN, // Use Draconic Reflex cooldown as default
      Arrays.asList(
        "Ability 1: Draconic Reflex - 75% DMG reduction (90s cooldown)",
        "Ability 2: Essence Rebirth - Second life (8min cooldown)",
        "",
        "Passive: Acts as permanent Totem of Undying when equipped"
      )
    );
    this.plugin = plugin;
    this.random = new Random();
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
      "1. Draconic Reflex - 75% damage reduction, reflects melee (90s cooldown)\n" +
      "2. Essence Rebirth - Spawn with diamond armor, full hunger (5min cooldown)\n" +
      "Passive: Acts as permanent Totem of Undying when equipped";
  }

  @Override
  public void activate(Player player) {
    if (player == null) {
      return;
    }

    // Check permission
    if (!player.hasPermission("elementaldragon.fragment.immortal")) {
      player.sendMessage(
        Component.text("You do not have permission to use the Immortal Fragment!",
          NamedTextColor.RED)
      );
      return;
    }

    // Apply passive effects (knockback reduction + health boost)
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
      Component.text("Passive: Totem of Undying protection granted!",
        NamedTextColor.GRAY)
    );
    player.sendMessage(
      Component.text("Use /immortal 1 or /immortal 2 to use abilities",
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
    if (!player.hasPermission("elementaldragon.fragment.immortal")) {
      player.sendMessage(
        Component.text("You do not have permission to use Immortal Fragment abilities!",
          NamedTextColor.RED)
      );
      return;
    }

    switch (abilityNumber) {
      case 1:
        executeDraconicReflex(player);
        break;
      case 2:
        executeEssenceRebirth(player);
        break;
      default:
        player.sendMessage(
          Component.text("Unknown ability number: " + abilityNumber,
            NamedTextColor.RED)
        );
        player.sendMessage(
          Component.text("Use /immortal 1 or /immortal 2", NamedTextColor.GRAY)
        );
    }
  }

  /**
   * Execute Draconic Reflex - grant 20% dodge chance for 15 seconds.
   * Original Specification:
   * - Provides 1/5 chance (20%) to avoid damage for 15 seconds after activation
   * - Plays anvil sound on miss (when dodge fails)
   * - Cooldown: 2 minutes
   *
   * @param player The player executing the ability
   */
  private void executeDraconicReflex(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    // Check if already active
    if (player.hasMetadata(DRACONIC_REFLEX_ACTIVE_KEY)) {
      player.sendMessage(
        Component.text("Draconic Reflex is already active!", NamedTextColor.RED)
      );
      return;
    }

    Location center = player.getLocation();

    // Mark player as having Draconic Reflex active (dodge chance enabled)
    player.setMetadata(
      DRACONIC_REFLEX_ACTIVE_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
    );

    // Store activation timestamp for HUD countdown
    player.setMetadata(
      DRACONIC_REFLEX_START_TIME_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis())
    );

    // Play activation sound
    playAbilitySound(center, Sound.BLOCK_ANVIL_LAND, 1.5f, 0.8f);
    playAbilitySound(center, Sound.ENTITY_GUARDIAN_HURT, 1.0f, 0.5f);

    // Show shield aura particles
    showShieldAuraParticles(player);

    player.sendMessage(
      Component.text("Draconic Reflex activated! 20% dodge chance for 15 seconds!",
        NamedTextColor.GREEN)
    );

    // Cooldown is set by FragmentManager.useFragmentAbility()

    // Schedule effect removal after duration
    new BukkitRunnable() {
      @Override
      public void run() {
        if (player.isDead() || !player.isValid()) {
          cancel();
          return;
        }

        // Remove the active metadata and start time
        if (player.hasMetadata(DRACONIC_REFLEX_ACTIVE_KEY)) {
          player.removeMetadata(DRACONIC_REFLEX_ACTIVE_KEY, plugin);
        }
        if (player.hasMetadata(DRACONIC_REFLEX_START_TIME_KEY)) {
          player.removeMetadata(DRACONIC_REFLEX_START_TIME_KEY, plugin);
        }

        // Play expiration sound
        playAbilitySound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f);

        player.sendMessage(
          Component.text("Draconic Reflex has ended.", NamedTextColor.GRAY)
        );
      }
    }.runTaskLater(plugin, DRACONIC_REFLEX_DURATION);
  }

  /**
   * Execute Essence Rebirth - grants second life for 30 seconds.
   * Original Specification:
   * - Grants wielder second life if reduced to 0 hearts
   * - Active for 30 seconds post-activation
   * - Retains all previous effects (fire resistance, speed, etc.)
   * - Cooldown: 8 minutes
   *
   * @param player The player executing the ability
   */
  private void executeEssenceRebirth(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    // Mark Essence Rebirth as activated (30-second protection window)
    player.setMetadata(
      ESSENCE_REBIRTH_ACTIVATED_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
    );

    // Store activation timestamp for HUD countdown
    player.setMetadata(
      ESSENCE_REBIRTH_START_TIME_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis())
    );

    // Play activation sound
    Location center = player.getLocation();
    playAbilitySound(center, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.5f, 1.2f);
    playAbilitySound(center, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

    // Show activation particles
    showEssenceRebirthParticles(center);

    // Cooldown is set by FragmentManager.useFragmentAbility()

    player.sendMessage(
      Component.text("Essence Rebirth activated! 30-second death protection active!",
        NamedTextColor.GREEN)
    );

    // Schedule protection window expiration after 30 seconds (600 ticks)
    new BukkitRunnable() {
      @Override
      public void run() {
        if (player.isDead() || !player.isValid()) {
          cancel();
          return;
        }

        // Remove protection and start time if still active (wasn't consumed by death prevention)
        if (player.hasMetadata(ESSENCE_REBIRTH_ACTIVATED_KEY)) {
          player.removeMetadata(ESSENCE_REBIRTH_ACTIVATED_KEY, plugin);
        }
        if (player.hasMetadata(ESSENCE_REBIRTH_START_TIME_KEY)) {
          player.removeMetadata(ESSENCE_REBIRTH_START_TIME_KEY, plugin);
        }
        player.sendMessage(
          Component.text("Essence Rebirth protection has expired.", NamedTextColor.GRAY)
        );
      }
    }.runTaskLater(plugin, ESSENCE_REBIRTH_DURATION);
  }

  /**
   * Show shield aura particles around the player during Draconic Reflex.
   *
   * @param player The player
   */
  private void showShieldAuraParticles(Player player) {
    new BukkitRunnable() {
      private int ticks = 0;

      @Override
      public void run() {
        if (ticks >= DRACONIC_REFLEX_DURATION ||
            player.isDead() || !player.isValid() ||
            !player.hasMetadata(DRACONIC_REFLEX_ACTIVE_KEY)) {
          cancel();
          return;
        }

        ParticleFX.spawnShieldAura(player.getLocation());

        ticks += 5;
      }
    }.runTaskTimer(plugin, 0L, 5L);
  }

  /**
   * Show Essence Rebirth activation particles.
   *
   * @param location The location
   */
  private void showEssenceRebirthParticles(Location location) {
    ParticleFX.spawnRebirthSparkles(location);
  }

  /**
   * Event handler for dodge chance during Draconic Reflex.
   * Original Specification:
   * - 20% chance (1/5) to avoid damage completely
   * - Plays anvil sound when dodge fails (80% of the time)
   * - Active for 15 seconds after Draconic Reflex activation
   *
   * @param event The entity damage event
   */
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();

    // Check if player has Draconic Reflex active
    if (!player.hasMetadata(DRACONIC_REFLEX_ACTIVE_KEY)) {
      return;
    }

    // Roll 20% dodge chance
    if (random.nextDouble() < DRACONIC_REFLEX_DODGE_CHANCE) {
      // Dodge successful - negate all damage
      event.setCancelled(true);

      // Show dodge success particles
      player.getWorld().spawnParticle(
        Particle.DUST,
        player.getLocation().add(0, 1, 0),
        15,
        0.5,
        0.5,
        0.5,
        0.05,
        new Particle.DustOptions(GOLD_COLOR, 1.5f)
      );

      // Play dodge success sound
      playAbilitySound(player.getLocation(), Sound.ENTITY_GUARDIAN_HURT, 1.0f, 1.2f);

      // Send dodge success message
      player.sendMessage(
        Component.text("Draconic Reflex: Dodge successful!", NamedTextColor.GOLD)
      );
    } else {
      // Dodge failed - play miss sound (anvil sound as specified)
      playAbilitySound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
    }
  }

  /**
   * Show particles when damage is reflected.
   *
   * @param location The reflection location
   */
  private void showReflectParticles(Location location) {
    location.getWorld().spawnParticle(
      Particle.DUST,
      location.add(0, 1, 0),
      8,
      0.5,
      0.5,
      0.5,
      0.05,
      new Particle.DustOptions(GOLD_COLOR, 1.5f)
    );

    location.getWorld().spawnParticle(
      Particle.SWEEP_ATTACK,
      location,
      3,
      0.3,
      0.3,
      0.3,
      0.02
    );
  }

  /**
   * Event handler for Essence Rebirth fatal damage prevention AND passive totem effect.
   * Original Specification:
   * - Grants second life if reduced to 0 hearts (active ability: 30-second window)
   * - Passive: Immortal Fragment acts as a permanent Totem of Undying when equipped
   * - Retains all active potion effects
   * - Plays totem animation/sound effects
   *
   * @param event The entity damage event
   */
  @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
  public void onEntityDamageForEssenceRebirth(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();

    // Check if player has Immortal Fragment equipped (passive totem effect)
    FragmentManager fragmentManager = plugin.getFragmentManager();
    boolean hasImmortalEquipped = fragmentManager.getEquippedFragment(player) == FragmentType.IMMORTAL;

    // Check if player has Essence Rebirth active (30-second protection window from active ability)
    boolean hasEssenceRebirthActive = player.hasMetadata(ESSENCE_REBIRTH_ACTIVATED_KEY);

    // Skip if neither passive nor active protection is available
    if (!hasImmortalEquipped && !hasEssenceRebirthActive) {
      return;
    }

    // Check if damage would be fatal
    double finalDamage = event.getFinalDamage();
    if (player.getHealth() - finalDamage <= 0) {
      // Cancel fatal damage
      event.setCancelled(true);

      // Restore to full health (20.0 = 10 hearts, or max health if boosted)
      double maxHealth = player.getAttribute(Attribute.MAX_HEALTH) != null
        ? player.getAttribute(Attribute.MAX_HEALTH).getValue()
        : 20.0;
      player.setHealth(maxHealth);

      // Notify player they were saved by the passive
      if (hasImmortalEquipped && !hasEssenceRebirthActive) {
        player.sendMessage(
          Component.text("Immortal Fragment saved you!", NamedTextColor.GOLD)
        );
      }

      // Remove active Essence Rebirth protection (if active ability was used)
      if (hasEssenceRebirthActive) {
        player.removeMetadata(ESSENCE_REBIRTH_ACTIVATED_KEY, plugin);
        player.sendMessage(
          Component.text("Essence Rebirth saved you from death!", NamedTextColor.GOLD)
        );
      }

      // Play totem sound effect (same as Totem of Undying)
      player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);

      // Show totem particle effect
      player.getWorld().spawnParticle(
        Particle.TOTEM_OF_UNDYING,
        player.getLocation().add(0, 1, 0),
        50,
        0.5,
        0.5,
        0.5,
        0.1
      );

      // Show golden rebirth particles (additional visual feedback)
      player.getWorld().spawnParticle(
        Particle.DUST,
        player.getLocation().add(0, 1, 0),
        50,
        1.0,
        1.0,
        1.0,
        0.1,
        new Particle.DustOptions(GOLD_COLOR, 2.0f)
      );

      // Note: All active potion effects are automatically retained (no action needed)
    }
  }

  /**
   * Apply passive effects (none, other than totem protection in event handler).
   *
   * @param player The player
   */
  @Override
  protected void applyPassiveEffects(Player player) {
    if (player == null) {
      return;
    }

    // No passive potion effects or stat boosts needed
    // The passive totem protection is handled in onEntityDamageForEssenceRebirth()

    // Show passive effect particles
    if (player.getWorld() != null) {
      // Brown dust particles for earth effect - use DUST instead of FALLING_DUST
      player.getWorld().spawnParticle(
        Particle.DUST,
        player.getLocation().add(0, 1, 0),
        5,
        0.3,
        0.3,
        0.3,
        0.02,
        new Particle.DustOptions(BROWN_COLOR, 1.0f)
      );
    }
  }

  /**
   * Remove passive effects (none, other than totem protection in event handler).
   *
   * @param player The player
   */
  @Override
  protected void removePassiveEffects(Player player) {
    if (player == null) {
      return;
    }

    // No passive potion effects or stat boosts to remove
    // The passive totem protection is handled in onEntityDamageForEssenceRebirth()
  }

  /**
   * Event handler for player respawn.
   * The passive totem protection works every time (no cooldown between uses).
   * Note: Diamond has no special vanilla death behavior, so no special handling needed.
   *
   * @param event The player respawn event
   */
  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    // No special handling needed - DIAMOND material has no vanilla death behavior
    // The fragment will simply be dropped on death like any other item
  }

  /**
   * Play activation sound for immortal fragment.
   *
   * @param player The player
   */
  @Override
  protected void playActivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.BLOCK_STONE_STEP,
      1.0f,
      0.8f
    );
    player.getWorld().playSound(
      location,
      Sound.BLOCK_STONE_PLACE,
      0.8f,
      1.0f
    );
  }

  /**
   * Play deactivation sound for immortal fragment.
   *
   * @param player The player
   */
  @Override
  protected void playDeactivationSound(Player player) {
    Location location = player.getLocation();
    player.getWorld().playSound(
      location,
      Sound.BLOCK_STONE_BREAK,
      0.8f,
      0.8f
    );
  }

  /**
   * Show activation particles for immortal fragment.
   *
   * @param player The player
   */
  @Override
  protected void showActivationParticles(Player player) {
    ParticleFX.spawnImmortalActivation(player.getLocation().add(0, 1, 0));
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
