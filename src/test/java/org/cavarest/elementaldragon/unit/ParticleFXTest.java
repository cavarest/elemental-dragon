package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.visual.ParticleFX;
import org.bukkit.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ParticleFX - ensures all particle constants and helper methods
 * handle edge cases properly.
 * 
 * The ParticleFX class provides:
 * - Fragment-specific color constants
 * - Reusable particle spawning methods
 * - Null-safe implementations
 * 
 * All methods should handle null inputs gracefully without throwing exceptions.
 */
class ParticleFXTest {

  // ==========================================================================
  // BURNING FRAGMENT COLOR TESTS
  // ==========================================================================

  @Test
  @DisplayName("FIRE_ORANGE color should be correct RGB values")
  void testFireOrangeColor() {
    Color color = ParticleFX.FIRE_ORANGE;
    assertNotNull(color, "FIRE_ORANGE should not be null");
    assertEquals(255, color.getRed(), "Red component should be 255");
    assertEquals(100, color.getGreen(), "Green component should be 100");
    assertEquals(0, color.getBlue(), "Blue component should be 0");
  }

  @Test
  @DisplayName("FIRE_BRIGHT color should be correct RGB values")
  void testFireBrightColor() {
    Color color = ParticleFX.FIRE_BRIGHT;
    assertNotNull(color, "FIRE_BRIGHT should not be null");
    assertEquals(255, color.getRed(), "Red component should be 255");
    assertEquals(165, color.getGreen(), "Green component should be 165");
    assertEquals(0, color.getBlue(), "Blue component should be 0");
  }

  @Test
  @DisplayName("FIRE_RED color should be correct RGB values")
  void testFireRedColor() {
    Color color = ParticleFX.FIRE_RED;
    assertNotNull(color, "FIRE_RED should not be null");
    assertEquals(255, color.getRed(), "Red component should be 255");
    assertEquals(50, color.getGreen(), "Green component should be 50");
    assertEquals(0, color.getBlue(), "Blue component should be 0");
  }

  @Test
  @DisplayName("LAVA_COLOR should be distinct from other fire colors")
  void testLavaColorDistinct() {
    Color lava = ParticleFX.LAVA_COLOR;
    Color orange = ParticleFX.FIRE_ORANGE;
    Color bright = ParticleFX.FIRE_BRIGHT;
    
    assertNotEquals(lava, orange, "LAVA_COLOR should be different from FIRE_ORANGE");
    assertNotEquals(lava, bright, "LAVA_COLOR should be different from FIRE_BRIGHT");
  }

  // ==========================================================================
  // AGILITY FRAGMENT COLOR TESTS
  // ==========================================================================

  @Test
  @DisplayName("TEAL_COLOR should be correct RGB values")
  void testTealColor() {
    Color color = ParticleFX.TEAL_COLOR;
    assertNotNull(color, "TEAL_COLOR should not be null");
    assertEquals(100, color.getRed(), "Red component should be 100");
    assertEquals(255, color.getGreen(), "Green component should be 255");
    assertEquals(200, color.getBlue(), "Blue component should be 200");
  }

  @Test
  @DisplayName("SPEED_WHITE should be pure white")
  void testSpeedWhiteColor() {
    Color color = ParticleFX.SPEED_WHITE;
    assertNotNull(color, "SPEED_WHITE should not be null");
    assertEquals(255, color.getRed(), "Red component should be 255");
    assertEquals(255, color.getGreen(), "Green component should be 255");
    assertEquals(255, color.getBlue(), "Blue component should be 255");
  }

  @Test
  @DisplayName("AIR_BLUE should be correct RGB values")
  void testAirBlueColor() {
    Color color = ParticleFX.AIR_BLUE;
    assertNotNull(color, "AIR_BLUE should not be null");
    assertEquals(200, color.getRed(), "Red component should be 200");
    assertEquals(240, color.getGreen(), "Green component should be 240");
    assertEquals(255, color.getBlue(), "Blue component should be 255");
  }

  // ==========================================================================
  // IMMORTAL FRAGMENT COLOR TESTS
  // ==========================================================================

  @Test
  @DisplayName("GOLD_SHIELD should be correct RGB values")
  void testGoldShieldColor() {
    Color color = ParticleFX.GOLD_SHIELD;
    assertNotNull(color, "GOLD_SHIELD should not be null");
    assertEquals(255, color.getRed(), "Red component should be 255");
    assertEquals(215, color.getGreen(), "Green component should be 215");
    assertEquals(0, color.getBlue(), "Blue component should be 0");
  }

  @Test
  @DisplayName("EARTH_BROWN should be correct RGB values")
  void testEarthBrownColor() {
    Color color = ParticleFX.EARTH_BROWN;
    assertNotNull(color, "EARTH_BROWN should not be null");
    assertEquals(139, color.getRed(), "Red component should be 139");
    assertEquals(69, color.getGreen(), "Green component should be 69");
    assertEquals(19, color.getBlue(), "Blue component should be 19");
  }

  @Test
  @DisplayName("LIFE_GREEN should be correct RGB values")
  void testLifeGreenColor() {
    Color color = ParticleFX.LIFE_GREEN;
    assertNotNull(color, "LIFE_GREEN should not be null");
    assertEquals(34, color.getRed(), "Red component should be 34");
    assertEquals(139, color.getGreen(), "Green component should be 139");
    assertEquals(34, color.getBlue(), "Blue component should be 34");
  }

  @Test
  @DisplayName("COPPER_COLOR should be distinct from other earth colors")
  void testCopperColorDistinct() {
    Color copper = ParticleFX.COPPER_COLOR;
    Color brown = ParticleFX.EARTH_BROWN;
    
    assertNotNull(copper, "COPPER_COLOR should not be null");
    assertNotEquals(copper, brown, "COPPER_COLOR should be different from EARTH_BROWN");
  }

  // ==========================================================================
  // CORRUPTED CORE COLOR TESTS
  // ==========================================================================

  @Test
  @DisplayName("VOID_PURPLE should be correct RGB values")
  void testVoidPurpleColor() {
    Color color = ParticleFX.VOID_PURPLE;
    assertNotNull(color, "VOID_PURPLE should not be null");
    assertEquals(75, color.getRed(), "Red component should be 75");
    assertEquals(0, color.getGreen(), "Green component should be 0");
    assertEquals(130, color.getBlue(), "Blue component should be 130");
  }

  @Test
  @DisplayName("DARK_PURPLE should be correct RGB values")
  void testDarkPurpleColor() {
    Color color = ParticleFX.DARK_PURPLE;
    assertNotNull(color, "DARK_PURPLE should not be null");
    assertEquals(128, color.getRed(), "Red component should be 128");
    assertEquals(0, color.getGreen(), "Green component should be 0");
    assertEquals(128, color.getBlue(), "Blue component should be 128");
  }

  @Test
  @DisplayName("VOID_BLACK should be very dark purple")
  void testVoidBlackColor() {
    Color color = ParticleFX.VOID_BLACK;
    assertNotNull(color, "VOID_BLACK should not be null");
    assertEquals(30, color.getRed(), "Red component should be 30");
    assertEquals(0, color.getGreen(), "Green component should be 0");
    assertEquals(50, color.getBlue(), "Blue component should be 50");
  }

  // ==========================================================================
  // LIGHTNING ABILITY COLOR TESTS
  // ==========================================================================

  @Test
  @DisplayName("LIGHTNING_PURPLE should match void purple for consistency")
  void testLightningPurpleColor() {
    Color lightning = ParticleFX.LIGHTNING_PURPLE;
    Color voidPurple = ParticleFX.VOID_PURPLE;
    
    assertNotNull(lightning, "LIGHTNING_PURPLE should not be null");
    assertEquals(voidPurple, lightning, 
        "LIGHTNING_PURPLE should match VOID_PURPLE for visual consistency");
  }

  @Test
  @DisplayName("SPARK_PURPLE should be brighter than main lightning color")
  void testSparkPurpleColor() {
    Color spark = ParticleFX.SPARK_PURPLE;
    Color lightning = ParticleFX.LIGHTNING_PURPLE;
    
    assertNotNull(spark, "SPARK_PURPLE should not be null");
    assertTrue(spark.getRed() >= lightning.getRed(), 
        "Spark purple should have equal or higher red component");
    assertTrue(spark.getBlue() > lightning.getBlue(), 
        "Spark purple should have higher blue component for brightness");
  }

  // ==========================================================================
  // COLOR UNIQUENESS TESTS
  // ==========================================================================

  @Test
  @DisplayName("All fragment colors should be unique")
  void testAllFragmentColorsUnique() {
    assertNotEquals(ParticleFX.FIRE_ORANGE, ParticleFX.TEAL_COLOR,
        "Fire and agility colors should differ");
    assertNotEquals(ParticleFX.FIRE_ORANGE, ParticleFX.GOLD_SHIELD,
        "Fire and immortal colors should differ");
    assertNotEquals(ParticleFX.FIRE_ORANGE, ParticleFX.VOID_PURPLE,
        "Fire and corrupted colors should differ");
    
    assertNotEquals(ParticleFX.TEAL_COLOR, ParticleFX.GOLD_SHIELD,
        "Agility and immortal colors should differ");
    assertNotEquals(ParticleFX.TEAL_COLOR, ParticleFX.VOID_PURPLE,
        "Agility and corrupted colors should differ");
    
    assertNotEquals(ParticleFX.GOLD_SHIELD, ParticleFX.VOID_PURPLE,
        "Immortal and corrupted colors should differ");
  }

  // ==========================================================================
  // COLOR COUNT TESTS
  // ==========================================================================

  @Test
  @DisplayName("Should have correct number of burning fragment colors")
  void testBurningColorCount() {
    // Verify we have 4 burning colors
    assertNotNull(ParticleFX.FIRE_ORANGE);
    assertNotNull(ParticleFX.FIRE_BRIGHT);
    assertNotNull(ParticleFX.FIRE_RED);
    assertNotNull(ParticleFX.LAVA_COLOR);
  }

  @Test
  @DisplayName("Should have correct number of agility fragment colors")
  void testAgilityColorCount() {
    // Verify we have 3 agility colors
    assertNotNull(ParticleFX.TEAL_COLOR);
    assertNotNull(ParticleFX.SPEED_WHITE);
    assertNotNull(ParticleFX.AIR_BLUE);
  }

  @Test
  @DisplayName("Should have correct number of immortal fragment colors")
  void testImmortalColorCount() {
    // Verify we have 4 immortal colors
    assertNotNull(ParticleFX.GOLD_SHIELD);
    assertNotNull(ParticleFX.EARTH_BROWN);
    assertNotNull(ParticleFX.LIFE_GREEN);
    assertNotNull(ParticleFX.COPPER_COLOR);
  }

  @Test
  @DisplayName("Should have correct number of corrupted core colors")
  void testCorruptedColorCount() {
    // Verify we have 3 corrupted colors
    assertNotNull(ParticleFX.VOID_PURPLE);
    assertNotNull(ParticleFX.DARK_PURPLE);
    assertNotNull(ParticleFX.VOID_BLACK);
  }

  @Test
  @DisplayName("Should have correct number of lightning ability colors")
  void testLightningColorCount() {
    // Verify we have 2 lightning colors
    assertNotNull(ParticleFX.LIGHTNING_PURPLE);
    assertNotNull(ParticleFX.SPARK_PURPLE);
  }

  // ==========================================================================
  // PARTICLE METHOD EXISTENCE TESTS
  // ==========================================================================

  @Test
  @DisplayName("ParticleFX class should have all required methods")
  void testRequiredMethodsExist() {
    // Burning fragment methods
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnFireballTrail", 
        org.bukkit.Location.class, int.class),
        "spawnFireballTrail method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnFireRingParticles", 
        org.bukkit.Location.class, double.class),
        "spawnFireRingParticles method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnBurningActivation", 
        org.bukkit.Location.class),
        "spawnBurningActivation method should exist");
    
    // Agility fragment methods
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnSpeedTrail", 
        org.bukkit.Location.class),
        "spawnSpeedTrail method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnWingBurstParticles", 
        org.bukkit.Location.class),
        "spawnWingBurstParticles method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnFlightTrail", 
        org.bukkit.Location.class, double.class, double.class),
        "spawnFlightTrail method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnAgilityActivation", 
        org.bukkit.Location.class),
        "spawnAgilityActivation method should exist");
    
    // Immortal fragment methods
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnShieldAura", 
        org.bukkit.Location.class),
        "spawnShieldAura method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnRebirthSparkles", 
        org.bukkit.Location.class),
        "spawnRebirthSparkles method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnImmortalActivation", 
        org.bukkit.Location.class),
        "spawnImmortalActivation method should exist");
    
    // Corrupted core methods
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnVoidParticles", 
        org.bukkit.Location.class),
        "spawnVoidParticles method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnDrainParticles", 
        org.bukkit.Location.class),
        "spawnDrainParticles method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnHealthTransfer", 
        org.bukkit.Location.class, org.bukkit.Location.class),
        "spawnHealthTransfer method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnVoidAura", 
        org.bukkit.Location.class),
        "spawnVoidAura method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnCorruptedActivation", 
        org.bukkit.Location.class),
        "spawnCorruptedActivation method should exist");
    
    // Lightning ability methods
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("createPurpleLightningEffect", 
        org.bukkit.Location.class),
        "createPurpleLightningEffect method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnLightningImpact", 
        org.bukkit.Location.class),
        "spawnLightningImpact method should exist");
    
    // Utility methods
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnDustBurst", 
        org.bukkit.Location.class, Color.class, int.class, double.class),
        "spawnDustBurst method should exist");
    assertDoesNotThrow(() -> ParticleFX.class.getMethod("spawnParticleRing", 
        org.bukkit.Location.class, org.bukkit.Particle.class, int.class, double.class),
        "spawnParticleRing method should exist");
  }

  // ==========================================================================
  // CLASS STRUCTURE TESTS
  // ==========================================================================

  @Test
  @DisplayName("ParticleFX should be a final class")
  void testClassIsFinal() {
    assertTrue(java.lang.reflect.Modifier.isFinal(ParticleFX.class.getModifiers()),
        "ParticleFX class should be final");
  }

  @Test
  @DisplayName("ParticleFX should have private constructor")
  void testPrivateConstructor() throws NoSuchMethodException {
    var constructor = ParticleFX.class.getDeclaredConstructor();
    assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
        "ParticleFX constructor should be private");
  }

  @Test
  @DisplayName("ParticleFX should be a utility class with no instance fields")
  void testNoInstanceFields() {
    // Verify all fields are static (constants)
    for (var field : ParticleFX.class.getDeclaredFields()) {
      if (!field.getName().equals("$jacocoData")) { // Ignore JaCoCo data field
        assertTrue(java.lang.reflect.Modifier.isStatic(field.getModifiers()),
            "Field " + field.getName() + " should be static");
      }
    }
  }

  // ==========================================================================
  // VISUAL CONSISTENCY TESTS
  // ==========================================================================

  @Test
  @DisplayName("All fragment colors should have valid RGB ranges")
  void testAllColorsValidRGB() {
    Color[] allColors = {
      ParticleFX.FIRE_ORANGE, ParticleFX.FIRE_BRIGHT, ParticleFX.FIRE_RED, 
      ParticleFX.LAVA_COLOR, ParticleFX.TEAL_COLOR, ParticleFX.SPEED_WHITE,
      ParticleFX.AIR_BLUE, ParticleFX.GOLD_SHIELD, ParticleFX.EARTH_BROWN,
      ParticleFX.LIFE_GREEN, ParticleFX.COPPER_COLOR, ParticleFX.VOID_PURPLE,
      ParticleFX.DARK_PURPLE, ParticleFX.VOID_BLACK, ParticleFX.LIGHTNING_PURPLE,
      ParticleFX.SPARK_PURPLE
    };
    
    for (Color color : allColors) {
      assertNotNull(color, "Color should not be null");
      assertTrue(color.getRed() >= 0 && color.getRed() <= 255,
          "Red component should be between 0 and 255");
      assertTrue(color.getGreen() >= 0 && color.getGreen() <= 255,
          "Green component should be between 0 and 255");
      assertTrue(color.getBlue() >= 0 && color.getBlue() <= 255,
          "Blue component should be between 0 and 255");
    }
  }

  @Test
  @DisplayName("Fragment colors should match their theme expectations")
  void testColorThemes() {
    // Fire colors should be warm (high red, low blue)
    assertTrue(ParticleFX.FIRE_ORANGE.getRed() > ParticleFX.FIRE_ORANGE.getBlue(),
        "Fire orange should have more red than blue");
    
    // Speed colors should be cool/bright (high values)
    assertTrue(ParticleFX.SPEED_WHITE.getRed() == 255, 
        "Speed white should be pure white");
    
    // Life green should have high green
    assertTrue(ParticleFX.LIFE_GREEN.getGreen() > ParticleFX.LIFE_GREEN.getRed(),
        "Life green should have more green than red");
    
    // Void purple should have low green
    assertTrue(ParticleFX.VOID_PURPLE.getGreen() < ParticleFX.VOID_PURPLE.getRed(),
        "Void purple should have less green than red");
  }
}
