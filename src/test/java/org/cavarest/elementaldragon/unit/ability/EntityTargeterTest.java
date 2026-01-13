package org.cavarest.elementaldragon.unit.ability;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.cavarest.elementaldragon.ability.EntityTargeter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EntityTargeter.
 */
@DisplayName("EntityTargeter Tests")
public class EntityTargeterTest {

    @Mock
    private Player player;

    @Mock
    private World world;

    @Mock
    private Location eyeLocation;

    @Mock
    private LivingEntity targetEntity;

    @Mock
    private LivingEntity excludedEntity;

    @Mock
    private Zombie zombie;

    private static final double RANGE = 50.0;
    private static final double CONE_DOT_THRESHOLD = 0.9;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup common eyeLocation mocks
        when(eyeLocation.toVector()).thenReturn(new Vector(0, 0, 0));
        when(eyeLocation.getDirection()).thenReturn(new Vector(1, 0, 0)); // Looking along +X
    }

    // ==================== isHostileMob tests ====================

    @Test
    @DisplayName("isHostileMob returns false for null entity")
    public void testIsHostileMobNull() {
        assertFalse(EntityTargeter.isHostileMob(null));
    }

    @Test
    @DisplayName("isHostileMob returns true for Zombie")
    public void testIsHostileMobZombie() {
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);
        assertTrue(EntityTargeter.isHostileMob(zombie));
    }

    @Test
    @DisplayName("isHostileMob returns true for Skeleton")
    public void testIsHostileMobSkeleton() {
        LivingEntity skeleton = mock(LivingEntity.class);
        when(skeleton.getType()).thenReturn(EntityType.SKELETON);
        assertTrue(EntityTargeter.isHostileMob(skeleton));
    }

    @Test
    @DisplayName("isHostileMob returns true for Creeper")
    public void testIsHostileMobCreeper() {
        LivingEntity creeper = mock(LivingEntity.class);
        when(creeper.getType()).thenReturn(EntityType.CREEPER);
        assertTrue(EntityTargeter.isHostileMob(creeper));
    }

    @Test
    @DisplayName("isHostileMob returns true for Spider")
    public void testIsHostileMobSpider() {
        LivingEntity spider = mock(LivingEntity.class);
        when(spider.getType()).thenReturn(EntityType.SPIDER);
        assertTrue(EntityTargeter.isHostileMob(spider));
    }

    @Test
    @DisplayName("isHostileMob returns true for Enderman")
    public void testIsHostileMobEnderman() {
        LivingEntity enderman = mock(LivingEntity.class);
        when(enderman.getType()).thenReturn(EntityType.ENDERMAN);
        assertTrue(EntityTargeter.isHostileMob(enderman));
    }

    @Test
    @DisplayName("isHostileMob returns true for Wither Skeleton")
    public void testIsHostileMobWitherSkeleton() {
        LivingEntity witherSkeleton = mock(LivingEntity.class);
        when(witherSkeleton.getType()).thenReturn(EntityType.WITHER_SKELETON);
        assertTrue(EntityTargeter.isHostileMob(witherSkeleton));
    }

    @Test
    @DisplayName("isHostileMob returns true for Blaze")
    public void testIsHostileMobBlaze() {
        LivingEntity blaze = mock(LivingEntity.class);
        when(blaze.getType()).thenReturn(EntityType.BLAZE);
        assertTrue(EntityTargeter.isHostileMob(blaze));
    }

    @Test
    @DisplayName("isHostileMob returns true for Ghast")
    public void testIsHostileMobGhast() {
        LivingEntity ghast = mock(LivingEntity.class);
        when(ghast.getType()).thenReturn(EntityType.GHAST);
        assertTrue(EntityTargeter.isHostileMob(ghast));
    }

    @Test
    @DisplayName("isHostileMob returns true for Witch")
    public void testIsHostileMobWitch() {
        LivingEntity witch = mock(LivingEntity.class);
        when(witch.getType()).thenReturn(EntityType.WITCH);
        assertTrue(EntityTargeter.isHostileMob(witch));
    }

    @Test
    @DisplayName("isHostileMob returns true for Warden")
    public void testIsHostileMobWarden() {
        LivingEntity warden = mock(LivingEntity.class);
        when(warden.getType()).thenReturn(EntityType.WARDEN);
        assertTrue(EntityTargeter.isHostileMob(warden));
    }

    @Test
    @DisplayName("isHostileMob returns true for Phantom")
    public void testIsHostileMobPhantom() {
        LivingEntity phantom = mock(LivingEntity.class);
        when(phantom.getType()).thenReturn(EntityType.PHANTOM);
        assertTrue(EntityTargeter.isHostileMob(phantom));
    }

    @Test
    @DisplayName("isHostileMob returns true for Player (PvP)")
    public void testIsHostileMobPlayer() {
        Player targetPlayer = mock(Player.class);
        when(targetPlayer.getType()).thenReturn(EntityType.PLAYER);
        assertTrue(EntityTargeter.isHostileMob(targetPlayer));
    }

    @Test
    @DisplayName("isHostileMob returns false for passive animals")
    public void testIsHostileMobPassive() {
        LivingEntity cow = mock(LivingEntity.class);
        when(cow.getType()).thenReturn(EntityType.COW);
        assertFalse(EntityTargeter.isHostileMob(cow));

        LivingEntity pig = mock(LivingEntity.class);
        when(pig.getType()).thenReturn(EntityType.PIG);
        assertFalse(EntityTargeter.isHostileMob(pig));

        LivingEntity sheep = mock(LivingEntity.class);
        when(sheep.getType()).thenReturn(EntityType.SHEEP);
        assertFalse(EntityTargeter.isHostileMob(sheep));
    }

    @Test
    @DisplayName("isHostileMob returns false for villagers")
    public void testIsHostileMobVillager() {
        LivingEntity villager = mock(LivingEntity.class);
        when(villager.getType()).thenReturn(EntityType.VILLAGER);
        assertFalse(EntityTargeter.isHostileMob(villager));
    }

    @Test
    @DisplayName("isHostileMob returns false for neutral mobs")
    public void testIsHostileMobNeutral() {
        LivingEntity wolf = mock(LivingEntity.class);
        when(wolf.getType()).thenReturn(EntityType.WOLF);
        assertFalse(EntityTargeter.isHostileMob(wolf));

        LivingEntity ironGolem = mock(LivingEntity.class);
        when(ironGolem.getType()).thenReturn(EntityType.IRON_GOLEM);
        assertFalse(EntityTargeter.isHostileMob(ironGolem));
    }

    // ==================== hasLineOfSight tests ====================

    @Test
    @DisplayName("hasLineOfSight returns true when ray trace hits target")
    public void testHasLineOfSightTrue() {
        // Setup player eye location
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(eyeLocation.toVector()).thenReturn(new Vector(0, 0, 0));

        // Setup target eye location
        Location targetEyeLoc = mock(Location.class);
        when(targetEyeLoc.toVector()).thenReturn(new Vector(10, 0, 0));
        when(targetEntity.getEyeLocation()).thenReturn(targetEyeLoc);
        when(eyeLocation.distance(targetEyeLoc)).thenReturn(10.0);

        // Setup world and ray trace
        when(player.getWorld()).thenReturn(world);

        RayTraceResult result = mock(RayTraceResult.class);
        when(result.getHitEntity()).thenReturn(targetEntity);
        when(world.rayTrace(
            eq(eyeLocation),
            any(Vector.class),
            eq(10.0),
            eq(org.bukkit.FluidCollisionMode.NEVER),
            eq(true),
            eq(0.1),
            any()
        )).thenReturn(result);

        assertTrue(EntityTargeter.hasLineOfSight(player, targetEntity));
    }

    @Test
    @DisplayName("hasLineOfSight returns false when ray trace hits different entity")
    public void testHasLineOfSightWrongEntity() {
        // Setup player eye location
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(eyeLocation.toVector()).thenReturn(new Vector(0, 0, 0));

        // Setup target eye location
        Location targetEyeLoc = mock(Location.class);
        when(targetEyeLoc.toVector()).thenReturn(new Vector(10, 0, 0));
        when(targetEntity.getEyeLocation()).thenReturn(targetEyeLoc);
        when(eyeLocation.distance(targetEyeLoc)).thenReturn(10.0);

        // Setup world and ray trace
        when(player.getWorld()).thenReturn(world);

        LivingEntity otherEntity = mock(LivingEntity.class);
        RayTraceResult result = mock(RayTraceResult.class);
        when(result.getHitEntity()).thenReturn(otherEntity);
        when(world.rayTrace(
            eq(eyeLocation),
            any(Vector.class),
            eq(10.0),
            eq(org.bukkit.FluidCollisionMode.NEVER),
            eq(true),
            eq(0.1),
            any()
        )).thenReturn(result);

        assertFalse(EntityTargeter.hasLineOfSight(player, targetEntity));
    }

    @Test
    @DisplayName("hasLineOfSight returns false when ray trace returns null")
    public void testHasLineOfSightNullResult() {
        // Setup player eye location
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(eyeLocation.toVector()).thenReturn(new Vector(0, 0, 0));

        // Setup target eye location
        Location targetEyeLoc = mock(Location.class);
        when(targetEyeLoc.toVector()).thenReturn(new Vector(10, 0, 0));
        when(targetEntity.getEyeLocation()).thenReturn(targetEyeLoc);
        when(eyeLocation.distance(targetEyeLoc)).thenReturn(10.0);

        // Setup world and ray trace
        when(player.getWorld()).thenReturn(world);
        when(world.rayTrace(
            eq(eyeLocation),
            any(Vector.class),
            eq(10.0),
            eq(org.bukkit.FluidCollisionMode.NEVER),
            eq(true),
            eq(0.1),
            any()
        )).thenReturn(null);

        assertFalse(EntityTargeter.hasLineOfSight(player, targetEntity));
    }

    @Test
    @DisplayName("hasLineOfSight returns false on exception")
    public void testHasLineOfSightException() {
        when(player.getEyeLocation()).thenThrow(new RuntimeException("Test exception"));

        assertFalse(EntityTargeter.hasLineOfSight(player, targetEntity));
    }

    // ==================== findInViewingCone tests ====================

    @Test
    @DisplayName("findInViewingCone returns null for null player")
    public void testFindInViewingConeNullPlayer() {
        LivingEntity result = EntityTargeter.findInViewingCone(
            null, RANGE, CONE_DOT_THRESHOLD, null, null
        );

        assertNull(result);
    }

    @Test
    @DisplayName("findInViewingCone returns null when no entities nearby")
    public void testFindInViewingConeNoEntities() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);
        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of());

        LivingEntity result = EntityTargeter.findInViewingCone(
            player, RANGE, CONE_DOT_THRESHOLD, null, null
        );

        assertNull(result);
    }

    @Test
    @DisplayName("findInViewingCone excludes player from search")
    public void testFindInViewingConeExcludesPlayer() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);
        when(player.getLocation()).thenReturn(mock(Location.class)); // Needed for instanceof check
        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of(player));

        LivingEntity result = EntityTargeter.findInViewingCone(
            player, RANGE, CONE_DOT_THRESHOLD, null, null
        );

        assertNull(result);
    }

    @Test
    @DisplayName("findInViewingCone excludes dead entities")
    public void testFindInViewingConeExcludesDead() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);
        when(targetEntity.isDead()).thenReturn(true);
        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of(targetEntity));

        LivingEntity result = EntityTargeter.findInViewingCone(
            player, RANGE, CONE_DOT_THRESHOLD, null, null
        );

        assertNull(result);
    }

    @Test
    @DisplayName("findInViewingCone excludes excluded entity")
    public void testFindInViewingConeExcludesEntity() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);
        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of(targetEntity));

        LivingEntity result = EntityTargeter.findInViewingCone(
            player, RANGE, CONE_DOT_THRESHOLD, null, targetEntity
        );

        assertNull(result);
    }

    @Test
    @DisplayName("findInViewingCone respects entity filter")
    public void testFindInViewingConeWithFilter() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);

        LivingEntity hostile = mock(LivingEntity.class);
        LivingEntity passive = mock(LivingEntity.class);

        // Setup entity locations
        Location hostileLoc = mock(Location.class);
        Location passiveLoc = mock(Location.class);
        when(hostileLoc.toVector()).thenReturn(new Vector(10, 0, 0)); // In front of player
        when(passiveLoc.toVector()).thenReturn(new Vector(10, 0, 0));
        when(hostile.getLocation()).thenReturn(hostileLoc);
        when(passive.getLocation()).thenReturn(passiveLoc);

        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of(hostile, passive));

        // Filter that only accepts hostile mobs
        Predicate<LivingEntity> hostileFilter = EntityTargeter::isHostileMob;
        when(hostile.getType()).thenReturn(EntityType.ZOMBIE);
        when(passive.getType()).thenReturn(EntityType.COW);

        LivingEntity result = EntityTargeter.findInViewingCone(
            player, RANGE, -1.0, hostileFilter, null // -1.0 means no cone restriction
        );

        // Should return the hostile zombie
        assertEquals(hostile, result);
    }

    @Test
    @DisplayName("findInViewingCone handles non-LivingEntity in collection")
    public void testFindInViewingConeNonLivingEntity() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);

        Entity nonLiving = mock(Entity.class); // Not a LivingEntity
        Location nonLivingLoc = mock(Location.class);
        when(nonLiving.getLocation()).thenReturn(nonLivingLoc);
        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of(nonLiving));

        LivingEntity result = EntityTargeter.findInViewingCone(
            player, RANGE, CONE_DOT_THRESHOLD, null, null
        );

        assertNull(result);
    }

    // ==================== findInViewingConeWithLineOfSight tests ====================

    @Test
    @DisplayName("findInViewingConeWithLineOfSight returns null for null player")
    public void testFindInViewingConeWithLineOfSightNullPlayer() {
        LivingEntity result = EntityTargeter.findInViewingConeWithLineOfSight(
            null, RANGE, Math.toRadians(25), null, null
        );

        assertNull(result);
    }

    @Test
    @DisplayName("findInViewingConeWithLineOfSight returns null when no entities nearby")
    public void testFindInViewingConeWithLineOfSightNoEntities() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);
        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of());

        LivingEntity result = EntityTargeter.findInViewingConeWithLineOfSight(
            player, RANGE, Math.toRadians(25), null, null
        );

        assertNull(result);
    }

    @Test
    @DisplayName("findInViewingConeWithLineOfSight excludes entities without line of sight")
    public void testFindInViewingConeWithLineOfSightNoLos() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);
        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of(targetEntity));

        Location targetEyeLoc = mock(Location.class);
        when(targetEntity.getEyeLocation()).thenReturn(targetEyeLoc);
        when(eyeLocation.toVector()).thenReturn(new Vector(0, 0, 0));
        when(targetEyeLoc.toVector()).thenReturn(new Vector(10, 0, 0));
        when(eyeLocation.distance(targetEyeLoc)).thenReturn(10.0);

        // Ray trace returns null (no line of sight)
        when(world.rayTrace(
            eq(eyeLocation),
            any(Vector.class),
            eq(10.0),
            eq(org.bukkit.FluidCollisionMode.NEVER),
            eq(true),
            eq(0.1),
            any()
        )).thenReturn(null);

        LivingEntity result = EntityTargeter.findInViewingConeWithLineOfSight(
            player, RANGE, Math.toRadians(25), null, null
        );

        assertNull(result);
    }

    // ==================== Edge case tests ====================

    @Test
    @DisplayName("findInViewingCone works with -1.0 cone threshold (no cone restriction)")
    public void testFindInViewingConeNoConeRestriction() {
        when(player.getEyeLocation()).thenReturn(eyeLocation);
        when(player.getWorld()).thenReturn(world);

        // Setup target entity location
        Location entityLoc = mock(Location.class);
        when(entityLoc.toVector()).thenReturn(new Vector(10, 0, 0));
        when(targetEntity.getLocation()).thenReturn(entityLoc);

        when(world.getNearbyEntities(any(Location.class), eq(RANGE), eq(RANGE), eq(RANGE)))
            .thenReturn(List.of(targetEntity));

        LivingEntity result = EntityTargeter.findInViewingCone(
            player, RANGE, -1.0, null, null // -1.0 accepts any direction
        );

        // Should find the entity regardless of direction
        assertEquals(targetEntity, result);
    }

    @Test
    @DisplayName("isHostileMob handles all hostile mob types")
    public void testIsHostileMobComprehensive() {
        EntityType[] hostileTypes = {
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
            EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ENDERMAN,
            EntityType.WITHER_SKELETON, EntityType.STRAY, EntityType.HUSK,
            EntityType.ZOMBIE_VILLAGER, EntityType.PHANTOM, EntityType.BLAZE,
            EntityType.GHAST, EntityType.MAGMA_CUBE, EntityType.WITCH,
            EntityType.SHULKER, EntityType.VEX, EntityType.VINDICATOR,
            EntityType.PILLAGER, EntityType.RAVAGER, EntityType.EVOKER,
            EntityType.ILLUSIONER, EntityType.DROWNED, EntityType.ZOMBIFIED_PIGLIN,
            EntityType.HOGLIN, EntityType.PIGLIN, EntityType.ZOGLIN,
            EntityType.WARDEN, EntityType.PLAYER
        };

        for (EntityType type : hostileTypes) {
            LivingEntity entity = mock(LivingEntity.class);
            when(entity.getType()).thenReturn(type);
            assertTrue(EntityTargeter.isHostileMob(entity),
                "Expected " + type + " to be hostile");
        }
    }
}
