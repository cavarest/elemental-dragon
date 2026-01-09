package org.cavarest.elementaldragon.unit;

import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CooldownManager.
 */
public class CooldownManagerTest {

    @Mock
    private ElementalDragon plugin;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    private CooldownManager cooldownManager;
    private UUID player1Uuid;
    private UUID player2Uuid;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        player1Uuid = UUID.randomUUID();
        player2Uuid = UUID.randomUUID();
        when(player1.getUniqueId()).thenReturn(player1Uuid);
        when(player2.getUniqueId()).thenReturn(player2Uuid);
        cooldownManager = new CooldownManager(null); // null plugin for unit tests
    }

    @Test
    @DisplayName("adjustActiveCooldowns caps cooldown when new max is lower")
    public void testAdjustActiveCooldownsCapToLower() {
        // Player has fire:1 cooldown with 50s remaining
        cooldownManager.setCooldown(player1, "fire", 1, 50);

        sleep(100);

        int beforeAdjust = cooldownManager.getRemainingCooldown(player1, "fire", 1);
        assertTrue(beforeAdjust >= 49 && beforeAdjust <= 50, "Should have ~50s remaining");

        // Admin changes global cooldown to 30s (lower than current)
        cooldownManager.adjustActiveCooldowns("fire", 1, 30);

        // Player's cooldown should be capped to min(~50, 30) = 30s
        int afterAdjust = cooldownManager.getRemainingCooldown(player1, "fire", 1);
        assertTrue(afterAdjust >= 29 && afterAdjust <= 30,
            "Cooldown should be capped to 30s, got: " + afterAdjust);
    }

    @Test
    @DisplayName("adjustActiveCooldowns keeps cooldown when new max is higher")
    public void testAdjustActiveCooldownsKeepWhenHigher() {
        // Player has fire:1 cooldown with 20s remaining
        cooldownManager.setCooldown(player1, "fire", 1, 20);

        sleep(100);

        int beforeAdjust = cooldownManager.getRemainingCooldown(player1, "fire", 1);
        assertTrue(beforeAdjust >= 19 && beforeAdjust <= 20, "Should have ~20s remaining");

        // Admin changes global cooldown to 60s (higher than current remaining)
        cooldownManager.adjustActiveCooldowns("fire", 1, 60);

        // Player's cooldown should stay at min(~20, 60) = ~20s
        int afterAdjust = cooldownManager.getRemainingCooldown(player1, "fire", 1);
        assertTrue(afterAdjust >= 19 && afterAdjust <= 20,
            "Cooldown should remain at ~20s, got: " + afterAdjust);
    }

    @Test
    @DisplayName("adjustActiveCooldowns clears all cooldowns when set to 0")
    public void testAdjustActiveCooldownsClearWhenZero() {
        // Player has fire:1 cooldown with 30s remaining
        cooldownManager.setCooldown(player1, "fire", 1, 30);

        sleep(100);

        // Verify cooldown exists
        assertTrue(cooldownManager.getRemainingCooldown(player1, "fire", 1) > 0);

        // Admin disables cooldown (sets to 0)
        cooldownManager.adjustActiveCooldowns("fire", 1, 0);

        // Cooldown should be completely cleared
        assertEquals(0, cooldownManager.getRemainingCooldown(player1, "fire", 1),
            "Cooldown should be cleared when disabled");
    }

    @Test
    @DisplayName("adjustActiveCooldowns affects multiple players correctly")
    public void testAdjustActiveCooldownsMultiplePlayers() {
        // Player 1 has 45s remaining (will be capped)
        cooldownManager.setCooldown(player1, "fire", 1, 45);
        // Player 2 has 20s remaining (will stay)
        cooldownManager.setCooldown(player2, "fire", 1, 20);

        sleep(100);

        // Admin changes global cooldown to 30s
        cooldownManager.adjustActiveCooldowns("fire", 1, 30);

        // Player 1: min(~45, 30) = 30s
        int player1Cooldown = cooldownManager.getRemainingCooldown(player1, "fire", 1);
        assertTrue(player1Cooldown >= 29 && player1Cooldown <= 30,
            "Player 1 should be capped to 30s, got: " + player1Cooldown);

        // Player 2: min(~20, 30) = ~20s
        int player2Cooldown = cooldownManager.getRemainingCooldown(player2, "fire", 1);
        assertTrue(player2Cooldown >= 19 && player2Cooldown <= 20,
            "Player 2 should keep ~20s, got: " + player2Cooldown);
    }

    @Test
    @DisplayName("adjustActiveCooldowns only affects specific element:ability")
    public void testAdjustActiveCooldownsSpecificAbility() {
        // Player has both fire abilities on cooldown
        cooldownManager.setCooldown(player1, "fire", 1, 50);
        cooldownManager.setCooldown(player1, "fire", 2, 40);

        sleep(100);

        // Adjust only fire:1 to 30s
        cooldownManager.adjustActiveCooldowns("fire", 1, 30);

        // fire:1 should be capped to 30s
        int fire1Cooldown = cooldownManager.getRemainingCooldown(player1, "fire", 1);
        assertTrue(fire1Cooldown >= 29 && fire1Cooldown <= 30,
            "fire:1 should be 30s, got: " + fire1Cooldown);

        // fire:2 should be unchanged
        int fire2Cooldown = cooldownManager.getRemainingCooldown(player1, "fire", 2);
        assertTrue(fire2Cooldown >= 39 && fire2Cooldown <= 40,
            "fire:2 should remain ~40s, got: " + fire2Cooldown);
    }

    @Test
    @DisplayName("adjustActiveCooldowns ignores expired cooldowns")
    public void testAdjustActiveCooldownsIgnoresExpired() {
        // Set a very short cooldown that will expire
        cooldownManager.setCooldown(player1, "fire", 1, 1);

        // Wait for it to expire
        sleep(1100);

        // Verify it's expired
        assertEquals(0, cooldownManager.getRemainingCooldown(player1, "fire", 1));

        // Try to adjust - should not crash or create new cooldown
        cooldownManager.adjustActiveCooldowns("fire", 1, 60);

        // Should still be 0 (no cooldown)
        assertEquals(0, cooldownManager.getRemainingCooldown(player1, "fire", 1));
    }

    @Test
    @DisplayName("adjustActiveCooldowns handles null element gracefully")
    public void testAdjustActiveCooldownsNullElement() {
        cooldownManager.setCooldown(player1, "fire", 1, 30);

        // Should not crash
        cooldownManager.adjustActiveCooldowns(null, 1, 60);

        // Cooldown should be unchanged
        int remaining = cooldownManager.getRemainingCooldown(player1, "fire", 1);
        assertTrue(remaining > 0, "Cooldown should still exist");
    }

    @Test
    @DisplayName("adjustActiveCooldowns with negative seconds does nothing")
    public void testAdjustActiveCooldownsNegativeSeconds() {
        cooldownManager.setCooldown(player1, "fire", 1, 30);

        sleep(100);
        int before = cooldownManager.getRemainingCooldown(player1, "fire", 1);

        // Calling with negative should not do anything
        cooldownManager.adjustActiveCooldowns("fire", 1, -10);

        sleep(100);
        int after = cooldownManager.getRemainingCooldown(player1, "fire", 1);

        // Cooldown should continue counting down naturally
        assertTrue(after <= before, "Cooldown should continue counting down naturally");
    }

    @Test
    @DisplayName("adjustActiveCooldowns works across different elements")
    public void testAdjustActiveCooldownsDifferentElements() {
        // Set cooldowns for different elements
        cooldownManager.setCooldown(player1, "fire", 1, 45);
        cooldownManager.setCooldown(player1, "agile", 1, 35);

        sleep(100);

        // Adjust only fire to 25s
        cooldownManager.adjustActiveCooldowns("fire", 1, 25);

        // Fire should be capped to 25s
        int fireCooldown = cooldownManager.getRemainingCooldown(player1, "fire", 1);
        assertTrue(fireCooldown >= 24 && fireCooldown <= 25, "fire:1 should be 25s");

        // Agile should be unchanged
        int agileCooldown = cooldownManager.getRemainingCooldown(player1, "agile", 1);
        assertTrue(agileCooldown >= 34 && agileCooldown <= 35, "agile:1 should be ~35s");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
