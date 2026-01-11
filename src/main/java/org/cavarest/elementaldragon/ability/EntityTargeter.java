package org.cavarest.elementaldragon.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

/**
 * Utility class for entity targeting using viewing cone and optional line-of-sight checks.
 * Provides DRY targeting logic for abilities that need to find entities in front of the player.
 *
 * <p>This class supports:</p>
 * <ul>
 *   <li>Viewing cone detection using dot product threshold</li>
 *   <li>Configurable range and cone angle</li>
 *   <li>Custom entity filters (hostile mobs, exclude player, etc.)</li>
 *   <li>Optional line-of-sight checks using ray tracing</li>
 *   <li>Entity exclusion (useful for finding next target after current dies)</li>
 * </ul>
 */
public final class EntityTargeter {

  private static final double DEFAULT_RANGE = 50.0;
  private static final double DEFAULT_CONE_DOT_THRESHOLD = 0.9; // ~25 degree cone

  private EntityTargeter() {
    // Utility class - no instantiation
  }

  /**
   * Find the closest entity in the player's viewing cone.
   *
   * @param player The player
   * @param range Maximum search range
   * @param coneDotThreshold Dot product threshold (higher = narrower cone)
   *                         0.9 = ~25 degrees, 0.5 = ~60 degrees
   *                         Use -1.0 for no cone restriction (any direction)
   * @param entityFilter Filter predicate for valid targets (receives LivingEntity)
   * @param excludedEntity Entity to exclude from search (null for no exclusion)
   * @return The closest matching entity or null
   */
  public static LivingEntity findInViewingCone(
    Player player,
    double range,
    double coneDotThreshold,
    Predicate<LivingEntity> entityFilter,
    Entity excludedEntity
  ) {
    if (player == null) {
      return null;
    }

    Location eyeLocation = player.getEyeLocation();
    Vector lookDirection = eyeLocation.getDirection().normalize();

    LivingEntity closest = null;
    double closestDistance = range;

    for (Entity entity : player.getWorld().getNearbyEntities(
      eyeLocation, range, range, range
    )) {
      if (!processEntity(entity, player, excludedEntity, entityFilter)) {
        continue;
      }

      Vector toEntity = entity.getLocation()
        .toVector()
        .subtract(eyeLocation.toVector())
        .normalize();

      double dot = lookDirection.dot(toEntity);
      if (dot < coneDotThreshold) {
        continue;
      }

      double distance = eyeLocation.distance(entity.getLocation());
      if (distance < closestDistance) {
        closest = (LivingEntity) entity;
        closestDistance = distance;
      }
    }

    return closest;
  }

  /**
   * Find the closest entity in the player's viewing cone with line-of-sight check.
   *
   * @param player The player
   * @param range Maximum search range
   * @param coneAngleRadians Maximum angle from look direction (in radians)
   * @param entityFilter Filter predicate for valid targets (receives LivingEntity)
   * @param excludedEntity Entity to exclude from search (null for no exclusion)
   * @return The closest matching entity with line-of-sight or null
   */
  public static LivingEntity findInViewingConeWithLineOfSight(
    Player player,
    double range,
    double coneAngleRadians,
    Predicate<LivingEntity> entityFilter,
    Entity excludedEntity
  ) {
    if (player == null) {
      return null;
    }

    Location eyeLocation = player.getEyeLocation();
    Vector lookDirection = eyeLocation.getDirection().normalize();
    double coneDotThreshold = Math.cos(coneAngleRadians);

    LivingEntity closestInView = null;
    double closestDistance = range;
    double closestAngle = coneAngleRadians;

    for (Entity entity : player.getWorld().getNearbyEntities(
      eyeLocation, range, range, range
    )) {
      if (!processEntity(entity, player, excludedEntity, entityFilter)) {
        continue;
      }

      LivingEntity living = (LivingEntity) entity;

      // Check line of sight
      if (!hasLineOfSight(player, living)) {
        continue;
      }

      // Calculate angle from player's look direction
      Vector toEntity = entity.getLocation()
        .toVector()
        .subtract(eyeLocation.toVector())
        .normalize();

      double dotProduct = lookDirection.dot(toEntity);
      // Clamp to valid range for acos (-1 to 1)
      dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));
      double angle = Math.acos(dotProduct);

      if (angle >= closestAngle) {
        continue;
      }

      double distance = eyeLocation.distance(entity.getLocation());
      if (distance >= closestDistance) {
        continue;
      }

      // This entity is in view and closer than previous best
      closestInView = living;
      closestDistance = distance;
      closestAngle = angle;
    }

    return closestInView;
  }

  /**
   * Check if player has line of sight to a target entity.
   * Uses Bukkit's ray trace to check for opaque blocks.
   *
   * @param player The player
   * @param target The target entity
   * @return true if player has clear line of sight to target
   */
  public static boolean hasLineOfSight(Player player, LivingEntity target) {
    try {
      Location eyeLoc = player.getEyeLocation();
      Location targetEyeLoc = target.getEyeLocation();

      Vector direction = targetEyeLoc.toVector().subtract(eyeLoc.toVector()).normalize();
      double distance = eyeLoc.distance(targetEyeLoc);

      // Ray trace with small step size for accuracy
      RayTraceResult result = player.getWorld().rayTrace(
        eyeLoc,
        direction,
        distance,
        org.bukkit.FluidCollisionMode.NEVER,
        true, // ignore passable blocks
        0.1, // step size
        entity -> entity == target
      );

      return result != null && result.getHitEntity() == target;
    } catch (Exception e) {
      // Fallback: assume no line of sight on error
      return false;
    }
  }

  /**
   * Process entity for inclusion in targeting search.
   *
   * @param entity The entity to process
   * @param player The player (for exclusion check)
   * @param excludedEntity Entity to exclude (may be null)
   * @param entityFilter Custom filter predicate (may be null for no filter)
   * @return true if entity should be considered for targeting
   */
  private static boolean processEntity(
    Entity entity,
    Player player,
    Entity excludedEntity,
    Predicate<LivingEntity> entityFilter
  ) {
    // Must be living entity
    if (!(entity instanceof LivingEntity)) {
      return false;
    }

    LivingEntity living = (LivingEntity) entity;

    // Cannot target self
    if (entity == player) {
      return false;
    }

    // Cannot target dead entities
    if (entity.isDead()) {
      return false;
    }

    // Cannot target excluded entity
    if (entity == excludedEntity) {
      return false;
    }

    // Apply custom filter if provided (now with LivingEntity type)
    if (entityFilter != null && !entityFilter.test(living)) {
      return false;
    }

    return true;
  }

  /**
   * Check if an entity is hostile (attacks players or is dangerous).
   *
   * @param entity The entity to check
   * @return true if the entity is hostile
   */
  public static boolean isHostileMob(LivingEntity entity) {
    if (entity == null) {
      return false;
    }

    return switch (entity.getType()) {
      case ZOMBIE, SKELETON, CREEPER, SPIDER, CAVE_SPIDER, ENDERMAN, WITHER_SKELETON,
           STRAY, HUSK, ZOMBIE_VILLAGER, PHANTOM, BLAZE, GHAST, MAGMA_CUBE, WITCH,
           SHULKER, VEX, VINDICATOR, PILLAGER, RAVAGER, EVOKER, ILLUSIONER,
           DROWNED, ZOMBIFIED_PIGLIN, HOGLIN, PIGLIN, ZOGLIN, WARDEN -> true;
      case PLAYER -> true; // Players can be targeted in PvP
      default -> false;
    };
  }
}
