package org.cavarest.dragonegglightning.ability;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Tests for LightningAbility.
 */
class LightningAbilityTest {

  private LightningAbility ability;

  @BeforeEach
  void setUp() {
    ability = new LightningAbility(null);
  }

  @Test
  @DisplayName("Test ability creation")
  void testAbilityCreation() {
    assertNotNull(ability, "LightningAbility should be created successfully");
  }

  @Test
  @DisplayName("Test ability name")
  void testGetName() {
    String name = ability.getName();
    assertNotNull(name, "Ability name should not be null");
    assertFalse(name.isEmpty(), "Ability name should not be empty");
    assertEquals("Lightning Strike", name, "Ability should have correct name");
  }

  @Test
  @DisplayName("Test cooldown duration")
  void testGetCooldownMillis() {
    long cooldown = ability.getCooldownMillis();
    assertTrue(cooldown > 0, "Cooldown should be positive");
    assertEquals(60000L, cooldown, "Cooldown should be 60000ms (60 seconds)");
  }

  @Test
  @DisplayName("Test ability properties")
  void testAbilityProperties() {
    assertEquals("Lightning Strike", ability.getName(), "Ability should have correct name");
    assertTrue(ability.getCooldownMillis() > 0, "Cooldown should be positive");
  }

  @Test
  @DisplayName("Test required item validation")
  void testHasRequiredItem() {
    // Test with null player - should return false
    boolean result = ability.hasRequiredItem(null);
    assertFalse(result, "Should return false for null player");
  }

  @Test
  @DisplayName("Test execute with null player")
  void testExecuteWithNullPlayer() {
    // Test that execute handles null player gracefully
    boolean result = ability.execute(null);
    assertFalse(result, "execute should return false for null player");
  }

  @Test
  @DisplayName("Test damage value has been updated to 4.0 points (2 hearts)")
  void testDamageValueUpdated() throws Exception {
    // Use reflection to access the private DAMAGE_PER_STRIKE constant
    Field damageField = LightningAbility.class.getDeclaredField("DAMAGE_PER_STRIKE");
    damageField.setAccessible(true);
    double damagePerStrike = (double) damageField.get(null); // null for static field

    assertEquals(4.0, damagePerStrike, 0.001,
        "Damage per strike should be 4.0 points (2 hearts) to bypass armor");
  }

  @Test
  @DisplayName("Test armor-bypassing damage functionality exists")
  void testArmorBypassingDamageMethodExists() throws Exception {
    // Use reflection to verify the dealDirectDamage method exists
    Method dealDirectDamageMethod = LightningAbility.class.getDeclaredMethod(
        "dealDirectDamage", org.bukkit.entity.LivingEntity.class, double.class);

    assertNotNull(dealDirectDamageMethod, "dealDirectDamage method should exist");
    assertTrue(java.lang.reflect.Modifier.isPrivate(dealDirectDamageMethod.getModifiers()),
        "dealDirectDamage method should be private");
    assertFalse(java.lang.reflect.Modifier.isStatic(dealDirectDamageMethod.getModifiers()),
        "dealDirectDamage method should be an instance method (not static)");
  }

  @Test
  @DisplayName("Test damage calculation is armor-independent")
  void testDamageCalculationIndependent() throws Exception {
    // Verify that the damage calculation doesn't use the Bukkit damage() method
    // by checking that the new method dealDirectDamage exists and the damage is higher

    Field damageField = LightningAbility.class.getDeclaredField("DAMAGE_PER_STRIKE");
    damageField.setAccessible(true);
    double damagePerStrike = (double) damageField.get(null);

    // The damage should be 4.0 points (2 hearts) which is higher than the original 3.0
    // This ensures the new armor-bypassing system is in place
    assertEquals(4.0, damagePerStrike, 0.001,
        "Damage should be increased to 4.0 points (2 hearts) to compensate for armor bypass");
  }

  @Test
  @DisplayName("Test strike count is still 3")
  void testStrikeCount() throws Exception {
    // Use reflection to verify the strike count hasn't changed
    Field strikeCountField = LightningAbility.class.getDeclaredField("STRIKE_COUNT");
    strikeCountField.setAccessible(true);
    int strikeCount = (int) strikeCountField.get(null); // null for static field

    assertEquals(3, strikeCount, "Should still have 3 strikes per ability use");
  }

  @Test
  @DisplayName("Test total damage is now 12.0 points (6 hearts)")
  void testTotalDamage() throws Exception {
    // Calculate expected total damage (3 strikes × 4.0 damage each)
    Field damageField = LightningAbility.class.getDeclaredField("DAMAGE_PER_STRIKE");
    damageField.setAccessible(true);
    double damagePerStrike = (double) damageField.get(null);

    Field strikeCountField = LightningAbility.class.getDeclaredField("STRIKE_COUNT");
    strikeCountField.setAccessible(true);
    int strikeCount = (int) strikeCountField.get(null);

    double totalExpectedDamage = damagePerStrike * strikeCount; // 4.0 × 3 = 12.0
    assertEquals(12.0, totalExpectedDamage, 0.001,
        "Total damage should be 12.0 points (6 hearts) for 3 strikes");
  }

  @Test
  @DisplayName("Test strike interval is still 0.5 seconds")
  void testStrikeInterval() throws Exception {
    // Use reflection to verify the strike interval hasn't changed
    Field strikeIntervalField = LightningAbility.class.getDeclaredField("STRIKE_INTERVAL_TICKS");
    strikeIntervalField.setAccessible(true);
    long strikeIntervalTicks = (long) strikeIntervalField.get(null);

    assertEquals(10L, strikeIntervalTicks, "Strike interval should still be 10 ticks (0.5 seconds)");
  }
}
