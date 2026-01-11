package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.fragment.AgilityFragment;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.ability.EntityTargeter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

/**
 * Tests for Agility Fragment fall damage protection and EntityTargeter.
 * Guards against bugs where:
 * 1. Fall damage was not prevented during Draconic Surge dash
 * 2. Entity targeting logic was duplicated across abilities
 */
class AgilityFragmentFallDamageTest {

  // ===== Agility Fragment Fall Damage Protection Tests =====

  @Test
  @DisplayName("Agility Fragment has dashingPlayers set for fall damage tracking")
  void testAgilityFragmentHasDashingPlayersTracking() {
    // This test guards against the bug where fall damage wasn't prevented during dash
    // The AgilityFragment must track dashing players to cancel fall damage

    AgilityFragment fragment = new AgilityFragment(null);

    // Use reflection to check if dashingPlayers field exists
    try {
      java.lang.reflect.Field dashingPlayersField = AgilityFragment.class.getDeclaredField("dashingPlayers");
      dashingPlayersField.setAccessible(true);
      Object fieldValue = dashingPlayersField.get(fragment);

      assertNotNull(fieldValue,
        "AgilityFragment must have dashingPlayers field for fall damage tracking");
      assertTrue(fieldValue instanceof java.util.Set,
        "dashingPlayers should be a Set for UUID tracking");

    } catch (NoSuchFieldException e) {
      fail("AgilityFragment must have 'dashingPlayers' field for fall damage tracking during Draconic Surge. " +
           "This field is required to track players who are dashing so their fall damage can be cancelled.");
    } catch (IllegalAccessException e) {
      fail("Should be able to access dashingPlayers field: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("AgilityFragment has fall damage handler method")
  void testAgilityFragmentHasFallDamageHandler() {
    // This test guards against the bug where fall damage wasn't cancelled
    // The AgilityFragment must have an EntityDamageEvent handler

    try {
      java.lang.reflect.Method damageHandler = AgilityFragment.class.getDeclaredMethod(
        "onEntityDamage", org.bukkit.event.entity.EntityDamageEvent.class);
      damageHandler.setAccessible(true);

      // Verify the method exists
      assertNotNull(damageHandler,
        "AgilityFragment must have onEntityDamage() method to handle fall damage cancellation");

      // Verify it has @EventHandler annotation
      assertTrue(damageHandler.isAnnotationPresent(org.bukkit.event.EventHandler.class),
        "onEntityDamage method must have @EventHandler annotation");

    } catch (NoSuchMethodException e) {
      fail("AgilityFragment must have 'onEntityDamage(EntityDamageEvent)' method. " +
           "This handler is required to cancel fall damage for dashing players (Draconic Surge) " +
           "and reduce fall damage for passively equipped fragments.");
    }
  }

  @Test
  @DisplayName("AgilityFragment has correct Draconic Surge distance constant (20 blocks)")
  void testDraconicSurgeDistanceConstant() {
    try {
      java.lang.reflect.Field distanceField = AgilityFragment.class.getDeclaredField("DRACONIC_SURGE_DISTANCE");
      distanceField.setAccessible(true);
      double distance = distanceField.getDouble(null);

      // Dash should be 20 blocks
      assertEquals(20.0, distance, 0.1,
        "Draconic Surge distance should be 20 blocks");

    } catch (NoSuchFieldException e) {
      fail("AgilityFragment must have DRACONIC_SURGE_DISTANCE constant");
    } catch (IllegalAccessException e) {
      fail("Should be able to access DRACONIC_SURGE_DISTANCE: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("AgilityFragment has landing protection duration constant")
  void testLandingProtectionDuration() {
    try {
      java.lang.reflect.Field protectionField = AgilityFragment.class.getDeclaredField("DRACONIC_SURGE_LANDING_PROTECTION");
      protectionField.setAccessible(true);
      long protectionTicks = protectionField.getLong(null);

      // Landing protection should be about 1 second (20 ticks)
      assertTrue(protectionTicks > 0,
        "Landing protection duration must be positive");
      assertTrue(protectionTicks <= 40,
        "Landing protection should be reasonable (within 2 seconds)");

    } catch (NoSuchFieldException e) {
      fail("AgilityFragment must have DRACONIC_SURGE_LANDING_PROTECTION constant for fall damage immunity after dash");
    } catch (IllegalAccessException e) {
      fail("Should be able to access DRACONIC_SURGE_LANDING_PROTECTION: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("Agility Fragment passive bonus mentions fall damage reduction")
  void testAgilityFragmentPassiveBonusMentionsFallDamage() {
    AgilityFragment fragment = new AgilityFragment(null);
    String passiveBonus = fragment.getPassiveBonus();

    assertNotNull(passiveBonus, "Passive bonus should not be null");
    assertTrue(passiveBonus.toLowerCase().contains("fall") ||
               passiveBonus.toLowerCase().contains("wind") ||
               passiveBonus.toLowerCase().contains("speed"),
      "Agility Fragment passive bonus should mention fall damage, wind, or speed benefits. " +
      "Actual: " + passiveBonus);
  }

  // ===== EntityTargeter Tests =====

  @Test
  @DisplayName("EntityTargeter class exists and provides targeting methods")
  void testEntityTargeterExists() {
    // This test guards against code duplication between Lightning and Fire abilities
    // EntityTargeter should provide centralized targeting logic

    assertNotNull(EntityTargeter.class,
      "EntityTargeter class should exist to consolidate targeting logic between abilities");

    // Verify key methods exist
    try {
      assertNotNull(EntityTargeter.class.getMethod("findInViewingCone",
        org.bukkit.entity.Player.class, double.class, double.class,
        java.util.function.Predicate.class, org.bukkit.entity.Entity.class),
        "EntityTargeter must have findInViewingCone method");

      assertNotNull(EntityTargeter.class.getMethod("findInViewingConeWithLineOfSight",
        org.bukkit.entity.Player.class, double.class, double.class,
        java.util.function.Predicate.class, org.bukkit.entity.Entity.class),
        "EntityTargeter must have findInViewingConeWithLineOfSight method");

      assertNotNull(EntityTargeter.class.getMethod("hasLineOfSight",
        org.bukkit.entity.Player.class, org.bukkit.entity.LivingEntity.class),
        "EntityTargeter must have hasLineOfSight method");

    } catch (NoSuchMethodException e) {
      fail("EntityTargeter must have all required targeting methods: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("EntityTargeter is a final utility class (no instantiation)")
  void testEntityTargeterIsUtilityClass() {
    assertTrue(java.lang.reflect.Modifier.isFinal(EntityTargeter.class.getModifiers()),
      "EntityTargeter should be a final class");

    // Check for private constructor
    try {
      java.lang.reflect.Constructor<?> constructor = EntityTargeter.class.getDeclaredConstructor();
      assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
        "EntityTargeter should have a private constructor to prevent instantiation");
    } catch (NoSuchMethodException e) {
      // Constructor might not exist - that's fine for utility class
    }
  }

  @Test
  @DisplayName("EntityTargeter has isHostileMob helper method")
  void testEntityTargeterHasIsHostileMob() {
    try {
      java.lang.reflect.Method isHostileMethod = EntityTargeter.class.getMethod(
        "isHostileMob", org.bukkit.entity.LivingEntity.class);
      isHostileMethod.setAccessible(true);

      assertNotNull(isHostileMethod,
        "EntityTargeter must have isHostileMob method for filtering hostile targets");

    } catch (NoSuchMethodException e) {
      fail("EntityTargeter must have isHostileMob(LivingEntity) method for ability targeting. " +
           "This consolidates hostile mob detection logic from BurningFragment.");
    }
  }
}
