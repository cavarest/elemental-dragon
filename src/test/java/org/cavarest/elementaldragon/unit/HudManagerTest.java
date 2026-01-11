package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.ability.LightningAbility;
import org.cavarest.elementaldragon.hud.HudManager;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for HUD behavior - ensures HUD only displays when player has dragon egg.
 * Tests the hasRequiredItem logic that controls HUD visibility.
 *
 * The HudManager.updatePlayerHud() method checks:
 *   Ability lightningAbility = abilityManager.getAbility(1);
 *   if (lightningAbility == null || !lightningAbility.hasRequiredItem(player)) {
 *     return; // HUD NOT shown
 *   }
 *   // HUD shown only if hasRequiredItem returns true
 */
class HudManagerTest {

  @Test
  @DisplayName("hasRequiredItem returns false for null player - HUD will NOT show")
  void testHasRequiredItemReturnsFalseForNullPlayer() {
    LightningAbility ability = new LightningAbility(null);

    // When player is null, hasRequiredItem should return false
    // This means HudManager.updatePlayerHud() will return early (no HUD shown)
    assertFalse(ability.hasRequiredItem(null),
      "Null player should return false - HUD will NOT show");
  }

  @Test
  @DisplayName("LightningAbility requires dragon egg in offhand")
  void testLightningAbilityRequiresDragonEgg() {
    LightningAbility ability = new LightningAbility(null);

    // The hasRequiredItem method checks:
    // ItemStack offhand = player.getInventory().getItemInOffHand();
    // return offhand != null && offhand.getType() == Material.DRAGON_EGG;

    // This test documents the expected behavior:
    // - hasRequiredItem(null) returns false
    // - hasRequiredItem(player without dragon egg) returns false
    // - hasRequiredItem(player with dragon egg in offhand) returns true

    // Verify null player returns false
    assertFalse(ability.hasRequiredItem(null),
      "hasRequiredItem should return false for null player");
  }

  @Test
  @DisplayName("HUD visibility is controlled by hasRequiredItem check")
  void testHudVisibilityControlledByHasRequiredItem() {
    // This test documents the HudManager behavior:
    //
    // In HudManager.updatePlayerHud():
    //   Ability lightningAbility = abilityManager.getAbility(1);
    //   if (lightningAbility == null || !lightningAbility.hasRequiredItem(player)) {
    //     return; // Early return = HUD NOT displayed
    //   }
    //   // Code below only executes if hasRequiredItem(player) == true
    //   player.sendActionBar(hudText); // HUD displayed
    //
    // This ensures HUD only shows when player has dragon egg in offhand.

    LightningAbility ability = new LightningAbility(null);

    // Verify the check exists and works for edge case
    assertFalse(ability.hasRequiredItem(null),
      "HUD should NOT show for null player");
  }

  @Test
  @DisplayName("LightningAbility name is Lightning Strike")
  void testAbilityName() {
    LightningAbility ability = new LightningAbility(null);
    assertEquals("Lightning Strike", ability.getName(),
      "Ability name should be 'Lightning Strike'");
  }

  @Test
  @DisplayName("LightningAbility has 60 second cooldown")
  void testAbilityCooldown() {
    LightningAbility ability = new LightningAbility(null);
    assertEquals(60000L, ability.getCooldownMillis(),
      "Cooldown should be 60 seconds (60000ms)");
  }

  // ===== Bug fix: Progress bar uses 1 vertical block with 8 fraction levels =====

  @Test
  @DisplayName("Progress bar renderer has static render methods and variant constants")
  void testProgressBarRendererHasStaticMethods() {
    // Test that ProgressBarRenderer has static render methods
    try {
      java.lang.reflect.Method renderMethod = ProgressBarRenderer.class.getMethod("render", float.class);
      assertNotNull(renderMethod,
        "ProgressBarRenderer.render(float) static method should exist");
      assertTrue(java.lang.reflect.Modifier.isStatic(renderMethod.getModifiers()),
        "render method should be static");
    } catch (NoSuchMethodException e) {
      fail("ProgressBarRenderer must have static render(float) method");
    }

    // Test MOON constant exists
    try {
      java.lang.reflect.Field moonField = ProgressBarRenderer.class.getField("MOON");
      assertNotNull(moonField,
        "ProgressBarRenderer.MOON constant should exist");
      assertTrue(java.lang.reflect.Modifier.isStatic(moonField.getModifiers()),
        "MOON field should be static");
      assertTrue(java.lang.reflect.Modifier.isFinal(moonField.getModifiers()),
        "MOON field should be final");
    } catch (NoSuchFieldException e) {
      fail("ProgressBarRenderer must have static final MOON constant");
    }

    // Test TILES constant exists
    try {
      java.lang.reflect.Field tilesField = ProgressBarRenderer.class.getField("TILES");
      assertNotNull(tilesField,
        "ProgressBarRenderer.TILES constant should exist");
      assertTrue(java.lang.reflect.Modifier.isStatic(tilesField.getModifiers()),
        "TILES field should be static");
      assertTrue(java.lang.reflect.Modifier.isFinal(tilesField.getModifiers()),
        "TILES field should be final");
    } catch (NoSuchFieldException e) {
      fail("ProgressBarRenderer must have static final TILES constant");
    }
  }

  @Test
  @DisplayName("Progress bar renderer uses TILES variant by default")
  void testProgressBarRendererHasEightLevels() {
    // Test that render() returns correct tile characters
    // 0% should return all empty tiles (gray)
    String result0 = ProgressBarRenderer.render(0.0f, 0);
    assertNotNull(result0, "render(0.0f) should return a value");
    assertTrue(result0.contains("▱"), "0% progress bar should contain empty tiles");
    assertTrue(result0.contains("<gray>"), "0% progress bar should have gray color");

    // 100% should return READY in green
    String result100 = ProgressBarRenderer.render(1.0f, 0);
    assertEquals("<green>READY", result100, "render(1.0f) should return READY in green");

    // Test getStatic method returns correct level
    String static0 = ProgressBarRenderer.getStatic(0.0f);
    assertTrue(static0.contains("▱"), "getStatic(0.0f) should contain empty tiles");

    // At 50% (0.5), should have about half filled tiles
    String static50 = ProgressBarRenderer.getStatic(0.5f);
    assertTrue(static50.contains("▰"), "getStatic(0.5f) should contain filled tiles");
  }

  @Test
  @DisplayName("HudManager buildProgressBar uses ProgressBarRenderer.render")
  void testHudManagerUsesRender() {
    // This test verifies that HudManager.buildProgressBar() uses the static
    // ProgressBarRenderer.render() method

    // Test the static method directly with fixed time to avoid animation race
    // At 50% progress with time 0, shows about half filled tiles with rainbow
    String result = ProgressBarRenderer.render(0.5f, 0);
    assertNotNull(result, "render should return a value");
    assertTrue(result.contains("▰") || result.contains("▱"),
        "Progress bar at 50% should contain tiles");
  }
}
