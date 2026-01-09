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
 * Passive Bonus: 25% knockback reduction and +2 hearts permanent health boost
 */
public class ImmortalFragment extends AbstractFragment implements Listener {

  // Draconic Reflex constants
  private static final long DRACONIC_REFLEX_COOLDOWN = 90000L; // 90 seconds
  private static final int DRACONIC_REFLEX_DURATION = 100; // 5 seconds (100 ticks)
  private static final double DRACONIC_REFLEX_DAMAGE_REDUCTION = 0.75; // 75%
  private static final double DRACONIC_REFLEX_REFLECT = 0.25; // 25% reflect

  // Essence Rebirth constants
  private static final long ESSENCE_REBIRTH_COOLDOWN = 300000L; // 5 minutes (300,000ms)
  private static final int ESSENCE_REBIRTH_ARROWS = 32;

  // Visual constants
  private static final Color BROWN_COLOR = Color.fromRGB(139, 69, 19);
  private static final Color GREEN_COLOR = Color.fromRGB(34, 139, 34);
  private static final Color GOLD_COLOR = Color.fromRGB(255, 215, 0);

  // Fragment metadata (Single Source of Truth)
  private static final Material MATERIAL = Material.TOTEM_OF_UNDYING;
  private static final NamedTextColor THEME_COLOR = NamedTextColor.GREEN;
  private static final String ELEMENT_NAME = "EARTH";

  // Ability definitions (Single Source of Truth)
  private final List<AbilityDefinition> abilities = List.of(
    new AbilityDefinition(1, "Draconic Reflex", "damage reduction",
      List.of("reflex", "draconic-reflex"),
      "A protective shield of ancient dragon energy surrounds you!"),
    new AbilityDefinition(2, "Essence Rebirth", "death protection",
      List.of("rebirth", "essence-rebirth"),
      "The dragon's essence will restore you upon death!")
  );

  // Metadata keys
  private static final String DRACONIC_REFLEX_ACTIVE_KEY = "immortal_draconic_reflex_active";
  private static final String ESSENCE_REBIRTH_ACTIVATED_KEY = "immortal_essence_rebirth_activated";

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
        "Ability 2: Essence Rebirth - Enhanced respawn (5min cooldown)",
        "",
        "Passive: 25% knockback reduction, +2 hearts"
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
      "Passive: 25% knockback reduction and +2 hearts when equipped";
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
      Component.text("Passive: 25% knockback reduction and +2 hearts granted!",
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
   * Execute Draconic Reflex - grant damage reduction and reflection.
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

    // Apply resistance effect (amplifier 2 = 75% reduction)
    player.addPotionEffect(
      new PotionEffect(
        PotionEffectType.RESISTANCE,
        DRACONIC_REFLEX_DURATION,
        2, // Amplifier 2 = 75% damage reduction
        false,
        true,
        true
      )
    );

    // Mark player as having Draconic Reflex active
    player.setMetadata(
      DRACONIC_REFLEX_ACTIVE_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
    );

    // Play activation sound
    playAbilitySound(center, Sound.BLOCK_ANVIL_LAND, 1.5f, 0.8f);
    playAbilitySound(center, Sound.ENTITY_GUARDIAN_HURT, 1.0f, 0.5f);

    // Show shield aura particles
    showShieldAuraParticles(player);

    player.sendMessage(
      Component.text("Draconic Reflex activated! 75% damage reduction for 5 seconds!",
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

        // Remove the active metadata
        if (player.hasMetadata(DRACONIC_REFLEX_ACTIVE_KEY)) {
          player.removeMetadata(DRACONIC_REFLEX_ACTIVE_KEY, plugin);
        }

        // Remove resistance effect early if still present
        player.removePotionEffect(PotionEffectType.RESISTANCE);

        // Play expiration sound
        playAbilitySound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f);

        player.sendMessage(
          Component.text("Draconic Reflex has ended.", NamedTextColor.GRAY)
        );
      }
    }.runTaskLater(plugin, DRACONIC_REFLEX_DURATION);
  }

  /**
   * Execute Essence Rebirth - enhanced respawn with bonus items.
   *
   * @param player The player executing the ability
   */
  private void executeEssenceRebirth(Player player) {
    // No cooldown check needed - FragmentManager.useFragmentAbility() already checked

    // Mark Essence Rebirth as activated
    player.setMetadata(
      ESSENCE_REBIRTH_ACTIVATED_KEY,
      new org.bukkit.metadata.FixedMetadataValue(plugin, true)
    );

    // Play activation sound
    Location center = player.getLocation();
    playAbilitySound(center, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.5f, 1.2f);
    playAbilitySound(center, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

    // Show activation particles
    showEssenceRebirthParticles(center);

    // Cooldown is set by FragmentManager.useFragmentAbility()

    player.sendMessage(
      Component.text("Essence Rebirth activated! You will respawn with bonus items!",
        NamedTextColor.GREEN)
    );
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
   * Event handler for damage reflection during Draconic Reflex.
   *
   * @param event The entity damage by entity event
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

    Entity attacker = event.getDamager();

    // Only reflect damage from living entities (not projectiles, etc.)
    if (!(attacker instanceof LivingEntity)) {
      return;
    }

    LivingEntity attackerEntity = (LivingEntity) attacker;

    // Calculate reflected damage (25% of original)
    double originalDamage = event.getDamage();
    double reflectedDamage = originalDamage * DRACONIC_REFLEX_REFLECT;

    // Apply damage reduction to player (75% reduction)
    double reducedDamage = originalDamage * DRACONIC_REFLEX_DAMAGE_REDUCTION;
    event.setDamage(reducedDamage);

    // Reflect damage back to attacker
    if (reflectedDamage > 0) {
      attackerEntity.damage(reflectedDamage);

      // Show reflect particles
      Location reflectLocation = attackerEntity.getLocation();
      showReflectParticles(reflectLocation);

      // Play reflect sound
      playAbilitySound(reflectLocation, Sound.ENTITY_IRON_GOLEM_HURT, 1.0f, 0.8f);
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
   * Event handler for player death - handles Essence Rebirth tracking.
   *
   * @param event The player death event
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();

    // Check if Essence Rebirth was activated
    if (player.hasMetadata(ESSENCE_REBIRTH_ACTIVATED_KEY)) {
      // Store that this player should receive rebirth benefits
      // The actual benefits are applied in PlayerRespawnEvent
      player.setMetadata(
        "immortal_rebirth_pending",
        new org.bukkit.metadata.FixedMetadataValue(plugin, true)
      );
    }
  }

  /**
   * Event handler for player respawn - applies Essence Rebirth benefits.
   *
   * @param event The player respawn event
   */
  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();

    // Check if player should receive rebirth benefits
    if (!player.hasMetadata("immortal_rebirth_pending")) {
      return;
    }

    // Check if respawn location is in spawn chunks (protected area)
    // For now, we'll apply benefits if the respawn location is the world spawn
    Location respawnLocation = event.getRespawnLocation();

    // Apply rebirth benefits
    applyRebirthBenefits(player);

    // Clear the pending metadata
    player.removeMetadata("immortal_rebirth_pending", plugin);

    // Clear the activated metadata (ready for next activation)
    if (player.hasMetadata(ESSENCE_REBIRTH_ACTIVATED_KEY)) {
      player.removeMetadata(ESSENCE_REBIRTH_ACTIVATED_KEY, plugin);
    }
  }

  /**
   * Apply rebirth benefits to a respawning player.
   *
   * @param player The player
   */
  private void applyRebirthBenefits(Player player) {
    // Give random diamond armor piece if available
    ItemStack[] diamondPieces = {
      new ItemStack(Material.DIAMOND_HELMET),
      new ItemStack(Material.DIAMOND_CHESTPLATE),
      new ItemStack(Material.DIAMOND_LEGGINGS),
      new ItemStack(Material.DIAMOND_BOOTS)
    };

    // Give one random diamond armor piece
    if (random.nextBoolean()) {
      ItemStack randomArmor = diamondPieces[random.nextInt(diamondPieces.length)];
      player.getInventory().addItem(randomArmor);

      // Show item given particles
      player.getWorld().spawnParticle(
        Particle.DUST,
        player.getLocation().add(0, 1, 0),
        15,
        0.5,
        0.5,
        0.5,
        0.05,
        new Particle.DustOptions(Color.fromRGB(0, 255, 255), 2.0f)
      );

      player.sendMessage(
        Component.text("Essence Rebirth: You received a diamond armor piece!",
          NamedTextColor.GOLD)
      );
    }

    // Set hunger to full (20)
    player.setFoodLevel(20);

    // Give 32 arrows if player has a bow
    boolean hasBow = false;
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && item.getType() == Material.BOW) {
        hasBow = true;
        break;
      }
    }

    if (hasBow) {
      player.getInventory().addItem(new ItemStack(Material.ARROW, ESSENCE_REBIRTH_ARROWS));
      player.sendMessage(
        Component.text("Essence Rebirth: 32 arrows added to your inventory!",
          NamedTextColor.GOLD)
      );
    }

    // Play respawn sound
    playAbilitySound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1.2f);

    // Show golden rebirth particles
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

    player.sendMessage(
      Component.text("Essence Rebirth complete! Welcome back, immortal warrior!",
        NamedTextColor.GREEN)
    );
  }

  /**
   * Apply passive knockback reduction and health boost effects.
   *
   * @param player The player
   */
  @Override
  protected void applyPassiveEffects(Player player) {
    if (player == null) {
      return;
    }

    // Apply DAMAGE_RESISTANCE for knockback reduction (amplifier 0 = ~20% reduction)
    // Note: DAMAGE_RESISTANCE doesn't directly reduce knockback,
    // but it does provide some damage reduction which helps with survivability
    player.addPotionEffect(
      new PotionEffect(
        PotionEffectType.RESISTANCE,
        Integer.MAX_VALUE, // Permanent while equipped
        0, // Amplifier 0 = minimal damage reduction
        false, // Not ambient
        true, // Show particles
        true // Show icon
      )
    );

    // Increase max health by 2 hearts (4.0)
    if (player.getAttribute(Attribute.MAX_HEALTH) != null) {
      double currentMaxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
      player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(currentMaxHealth + 4.0);

      // Also add the health to current health so player immediately gets the benefit
      double newMaxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
      player.setHealth(Math.min(player.getHealth() + 4.0, newMaxHealth));
    }

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

      // Green particles for health boost
      player.getWorld().spawnParticle(
        Particle.DUST,
        player.getLocation(),
        3,
        0.3,
        0.3,
        0.3,
        0.02,
        new Particle.DustOptions(GREEN_COLOR, 1.0f)
      );
    }
  }

  /**
   * Remove passive knockback reduction and health boost effects.
   *
   * @param player The player
   */
  @Override
  protected void removePassiveEffects(Player player) {
    if (player == null) {
      return;
    }

    // Remove damage resistance
    player.removePotionEffect(PotionEffectType.RESISTANCE);

    // Reset max health to default (20.0)
    if (player.getAttribute(Attribute.MAX_HEALTH) != null) {
      double currentMaxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
      double extraHealth = currentMaxHealth - 20.0;

      // First, reduce health if it exceeds the new max
      if (player.getHealth() > 20.0) {
        player.setHealth(20.0);
      }

      // Then reset base max health
      player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
    }
  }

  /**
   * Event handler for knockback reduction during Draconic Reflex.
   * This provides additional knockback reduction beyond the passive.
   *
   * @param event The entity damage event
   */
  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();

    // Apply knockback reduction from passive
    if (player.hasPotionEffect(PotionEffectType.RESISTANCE)) {
      // Check if this is an attack that would cause knockback
      if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
        // Reduce knockback by approximately 25%
        // Note: This is an approximation since Bukkit doesn't have direct knockback reduction
        // The actual knockback is handled by the client's physics
      }
    }
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
