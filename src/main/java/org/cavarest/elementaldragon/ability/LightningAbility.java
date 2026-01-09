package org.cavarest.elementaldragon.ability;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.visual.ParticleFX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Lightning ability that strikes targets with purple lightning.
 * Features intelligent target switching: if initial target dies, moves to next closest target.
 */
public class LightningAbility implements Ability {

  private static final int STRIKE_COUNT = 3;
  private static final long STRIKE_INTERVAL_TICKS = 10L; // 0.5 seconds
  private static final double DAMAGE_PER_STRIKE = 4.0; // 2.0 hearts (bypasses armor)
  private static final long COOLDOWN_MILLIS = 60000L; // 60 seconds
  private static final double MAX_RANGE = 50.0;
  private static final String ABILITY_NAME = "Lightning Strike";

  private final ElementalDragon plugin;

  public LightningAbility(ElementalDragon plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean execute(Player player) {
    if (player == null) {
      return false;
    }

    // Find initial target entity
    LivingEntity target = findTargetEntity(player);

    if (target == null) {
      player.sendMessage(
        Component.text("No valid target found!", NamedTextColor.RED)
      );
      return false;
    }

    // Check if player still has dragon egg in offhand
    if (!hasRequiredItem(player)) {
      player.sendMessage(
        Component.text(
          "You must hold a Dragon Egg in your offhand!",
          NamedTextColor.RED
        )
      );
      return false;
    }

    // Execute lightning strikes with intelligent target switching
    executeLightningStrikes(player, target);

    player.sendMessage(
      Component.text("Lightning ability activated!", NamedTextColor.LIGHT_PURPLE)
    );

    return true;
  }

  @Override
  public boolean hasRequiredItem(Player player) {
    if (player == null) {
      return false;
    }
    ItemStack offhand = player.getInventory().getItemInOffHand();
    return offhand != null && offhand.getType() == Material.DRAGON_EGG;
  }

  @Override
  public long getCooldownMillis() {
    return COOLDOWN_MILLIS;
  }

  @Override
  public String getName() {
    return ABILITY_NAME;
  }

  /**
   * Get a descriptive name for the target entity.
   *
   * @param entity The target entity
   * @return A descriptive name for the target
   */
  private String getTargetName(Entity entity) {
    if (entity instanceof Player) {
      return ((Player) entity).getName();
    }

    String entityType = entity.getType().name();
    // Convert enum name to more readable format
    entityType = entityType.replace('_', ' ');
    entityType = entityType.toLowerCase();
    entityType = entityType.substring(0, 1).toUpperCase() + entityType.substring(1);

    return entityType;
  }

  /**
   * Find the closest living entity in the direction the player is facing.
   *
   * @param player The player
   * @return The target entity or null if none found
   */
  private LivingEntity findTargetEntity(Player player) {
    if (player == null) {
      return null;
    }
    Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection();

    // Use ray tracing to find entities
    RayTraceResult result = player.getWorld().rayTraceEntities(
      eyeLocation,
      direction,
      MAX_RANGE,
      entity -> entity instanceof LivingEntity &&
        entity != player &&
        !entity.isDead()
    );

    if (result != null && result.getHitEntity() instanceof LivingEntity) {
      return (LivingEntity) result.getHitEntity();
    }

    // Fallback: find nearest entity in viewing cone
    return findNearestEntityInCone(player, eyeLocation, direction);
  }

  /**
   * Find nearest entity within a viewing cone.
   *
   * @param player The player
   * @param eyeLocation The player's eye location
   * @param direction The player's look direction
   * @return The nearest entity or null
   */
  private LivingEntity findNearestEntityInCone(
    Player player,
    Location eyeLocation,
    Vector direction
  ) {
    LivingEntity nearest = null;
    double nearestDistance = MAX_RANGE;
    double minDotProduct = 0.9; // Roughly 25 degree cone

    for (Entity entity : player.getWorld().getNearbyEntities(
      eyeLocation,
      MAX_RANGE,
      MAX_RANGE,
      MAX_RANGE
    )) {
      if (!(entity instanceof LivingEntity) ||
          entity == player ||
          entity.isDead()) {
        continue;
      }

      Vector toEntity = entity.getLocation()
        .subtract(eyeLocation)
        .toVector()
        .normalize();
      double dot = direction.dot(toEntity);

      if (dot >= minDotProduct) {
        double distance = eyeLocation.distance(entity.getLocation());
        if (distance < nearestDistance) {
          nearest = (LivingEntity) entity;
          nearestDistance = distance;
        }
      }
    }

    return nearest;
  }

  /**
   * Find next closest target excluding the current target.
   *
   * @param player The player
   * @param currentTarget The current target to exclude
   * @return The next closest target or null if none found
   */
  private LivingEntity findNextTarget(Player player, LivingEntity currentTarget) {
    if (player == null || currentTarget == null) {
      return null;
    }

    Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection();
    LivingEntity nextTarget = null;
    double nearestDistance = MAX_RANGE;
    double minDotProduct = 0.9; // Roughly 25 degree cone

    for (Entity entity : player.getWorld().getNearbyEntities(
      eyeLocation,
      MAX_RANGE,
      MAX_RANGE,
      MAX_RANGE
    )) {
      if (!(entity instanceof LivingEntity) ||
          entity == player ||
          entity == currentTarget || // Exclude current target
          entity.isDead()) {
        continue;
      }

      Vector toEntity = entity.getLocation()
        .subtract(eyeLocation)
        .toVector()
        .normalize();
      double dot = direction.dot(toEntity);

      if (dot >= minDotProduct) {
        double distance = eyeLocation.distance(entity.getLocation());
        if (distance < nearestDistance) {
          nextTarget = (LivingEntity) entity;
          nearestDistance = distance;
        }
      }
    }

    return nextTarget;
  }

  /**
   * Execute sequential lightning strikes with intelligent target switching.
   *
   * @param player The player casting the ability
   * @param initialTarget The initial target entity
   */
  private void executeLightningStrikes(Player player, LivingEntity initialTarget) {
    // Use AtomicReference to make variables effectively final for inner class
    final AtomicReference<LivingEntity> currentTargetRef = new AtomicReference<>(initialTarget);
    final AtomicReference<String> currentTargetNameRef = new AtomicReference<>(getTargetName(initialTarget));
    final AtomicReference<Integer> totalStrikesRef = new AtomicReference<>(0);
    final AtomicReference<Integer> strikesOnCurrentTargetRef = new AtomicReference<>(0);
    final Player finalPlayer = player;

    new BukkitRunnable() {
      @Override
      public void run() {
        // Check if player still has dragon egg (can be switched mid-cast)
        if (!hasRequiredItem(finalPlayer)) {
          finalPlayer.sendMessage(
            Component.text(
              "Ability cancelled! Dragon Egg removed from offhand.",
              NamedTextColor.RED
            )
          );
          cancel();
          return;
        }

        LivingEntity currentTarget = currentTargetRef.get();

        // If no valid target, try to find a new one
        if (currentTarget == null || currentTarget.isDead() || !currentTarget.isValid()) {
          LivingEntity newTarget = findNextTarget(finalPlayer, currentTarget);
          if (newTarget == null) {
            finalPlayer.sendMessage(
              Component.text("No more valid targets found!", NamedTextColor.RED)
            );
            cancel();
            return;
          } else {
            // Switched to new target
            currentTargetRef.set(newTarget);
            currentTargetNameRef.set(getTargetName(newTarget));
            strikesOnCurrentTargetRef.set(0);
            finalPlayer.sendMessage(
              Component.text("Lightning shifts to " + currentTargetNameRef.get() + "!", NamedTextColor.GOLD)
            );
          }
        }

        // Strike the current target
        strikeLightning(currentTargetRef.get(), finalPlayer, currentTargetNameRef.get());
        totalStrikesRef.set(totalStrikesRef.get() + 1);
        strikesOnCurrentTargetRef.set(strikesOnCurrentTargetRef.get() + 1);

        // Send strike message with target information
        finalPlayer.sendMessage(
          Component.text("Lightning strike " + totalStrikesRef.get() + "/" + STRIKE_COUNT +
                        " hit " + currentTargetNameRef.get() + "!",
                        NamedTextColor.LIGHT_PURPLE)
        );

        // Check if all strikes are done
        if (totalStrikesRef.get() >= STRIKE_COUNT) {
          cancel();
        }
      }
    }.runTaskTimer(plugin, 0L, STRIKE_INTERVAL_TICKS);
  }

  /**
   * Strike a single lightning bolt on the target.
   *
   * @param target The target entity
   * @param player The casting player (for feedback)
   * @param targetName The name of the target for messages
   */
  private void strikeLightning(LivingEntity target, Player player, String targetName) {
    Location targetLocation = target.getLocation();

    // Create actual lightning strike (use correct entity type)
    LightningStrike lightning = (LightningStrike) target.getWorld()
      .spawnEntity(targetLocation, EntityType.LIGHTNING_BOLT);

    // Make it visually purple with particles
    createPurpleLightningEffect(targetLocation);

    // Deal armor-bypassing damage directly to health
    dealDirectDamage(target, DAMAGE_PER_STRIKE);

    // Play proper thunder sound
    target.getWorld().playSound(
      targetLocation,
      Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
      3.0f,
      1.0f
    );

    // Play explosion sound for impact
    target.getWorld().playSound(
      targetLocation,
      Sound.ENTITY_DRAGON_FIREBALL_EXPLODE,
      2.0f,
      1.0f
    );
  }

  /**
   * Deal direct damage that bypasses armor and enchantments.
   * This ensures consistent damage regardless of target's armor.
   *
   * @param target The target entity
   * @param damage The amount of damage to deal
   */
  private void dealDirectDamage(LivingEntity target, double damage) {
    if (target == null || target.isDead()) {
      return;
    }

    double currentHealth = target.getHealth();
    double newHealth = Math.max(0, currentHealth - damage);
    target.setHealth(newHealth);
  }

  /**
   * Create purple lightning visual effect.
   *
   * @param location The location to spawn the effect
   */
  private void createPurpleLightningEffect(Location location) {
    ParticleFX.createPurpleLightningEffect(location);
  }
}
