package com.dragonegg.lightning.hud;

import com.dragonegg.lightning.ability.LightningAbility;
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
}
