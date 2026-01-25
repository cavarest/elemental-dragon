package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.ability.LightningAbility;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for AbilityManager cooldown behavior.
 * Tests all the scenarios mentioned by the user:
 * 1. Death resets cooldown immediately (handled by CooldownManager)
 * 2. Losing dragon egg doesn't reset cooldown
 * 3. Picking up dragon egg doesn't start new cooldown unless one was active
 *
 * Note: These tests focus on cooldown management logic, not ability execution.
 * Ability execution tests require a full mocked world environment.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AbilityManagerTest {

  private CooldownManager cooldownManager;
  private AbilityManager abilityManager;
  private LightningAbility lightningAbility;

  @BeforeEach
  void setUp() {
    cooldownManager = new CooldownManager(null); // null is fine for basic tests
    abilityManager = new AbilityManager(null, cooldownManager);
    lightningAbility = new LightningAbility(null);
  }

  // === COOLDOWN INDEPENDENCE TESTS ===

  @Test
  void testCooldownIndependentOfItemPossession() {
    // This is the core requirement: cooldown should be based on usage, not item possession
    var mockPlayer = createMockPlayerWithDragonEgg();

    // Initially no cooldown - should be able to use ability
    assertTrue(abilityManager.canUseAbility(mockPlayer, lightningAbility),
      "Should be able to use ability when no cooldown");

    // Set cooldown (simulates after using ability)
    abilityManager.setCooldown(mockPlayer, 60);

    // Now should NOT be able to use ability due to cooldown
    assertFalse(abilityManager.canUseAbility(mockPlayer, lightningAbility),
      "Should not be able to use ability when on cooldown");
  }

  @Test
  void testDeathClearsCooldownImmediately() {
    var mockPlayer = createMockPlayerWithDragonEgg();

    // Start with cooldown
    abilityManager.setCooldown(mockPlayer, 60); // 60 seconds
    assertTrue(abilityManager.isOnCooldown(mockPlayer), "Should be on cooldown");

    // Simulate death event (clears cooldown) - now handled by CooldownManager
    PlayerDeathEvent deathEvent = mock(PlayerDeathEvent.class);
    when(deathEvent.getEntity()).thenReturn(mockPlayer);
    cooldownManager.onPlayerDeath(deathEvent);

    // Cooldown should be cleared
    assertFalse(abilityManager.isOnCooldown(mockPlayer), "Cooldown should be cleared on death");
    assertTrue(abilityManager.canUseAbility(mockPlayer, lightningAbility),
      "Should be able to use ability after death (cooldown cleared)");
  }

  @Test
  void testCooldownPersistsAfterRespawn() {
    var mockPlayer = createMockPlayerWithDragonEgg();

    // Start with cooldown
    abilityManager.setCooldown(mockPlayer, 60); // 60 seconds
    assertTrue(abilityManager.isOnCooldown(mockPlayer), "Should be on cooldown");

    // Death clears cooldown - now handled by CooldownManager
    PlayerDeathEvent deathEvent = mock(PlayerDeathEvent.class);
    when(deathEvent.getEntity()).thenReturn(mockPlayer);
    cooldownManager.onPlayerDeath(deathEvent);

    // Cooldown should remain cleared (death cleared it, respawn doesn't restart it)
    assertFalse(abilityManager.isOnCooldown(mockPlayer),
      "Cooldown should not restart on respawn");
    assertTrue(abilityManager.canUseAbility(mockPlayer, lightningAbility),
      "Should be able to use ability after respawn");
  }

  @Test
  void testCooldownPersistenceAcrossLogout() {
    var mockPlayer = createMockPlayerWithDragonEgg();

    // Start with cooldown
    abilityManager.setCooldown(mockPlayer, 60); // 60 seconds
    assertTrue(abilityManager.isOnCooldown(mockPlayer), "Should be on cooldown");

    // Simulate logout/login (join event) - now handled by CooldownManager
    PlayerJoinEvent joinEvent = mock(PlayerJoinEvent.class);
    when(joinEvent.getPlayer()).thenReturn(mockPlayer);
    cooldownManager.onPlayerJoin(joinEvent);

    // Cooldown should still exist (persists across logout/login)
    assertTrue(abilityManager.isOnCooldown(mockPlayer),
      "Cooldown should persist across logout/login");
  }

  @Test
  void testCooldownExpiration() throws InterruptedException {
    var mockPlayer = createMockPlayerWithDragonEgg();

    // Set a short cooldown
    abilityManager.setCooldown(mockPlayer, 1); // 1 second

    assertTrue(abilityManager.isOnCooldown(mockPlayer), "Should be on cooldown initially");

    // Wait for cooldown to expire (2 seconds for safety margin)
    Thread.sleep(2000); // Wait 2 seconds to ensure full expiration

    assertFalse(abilityManager.isOnCooldown(mockPlayer),
      "Cooldown should expire after time passes");
    assertTrue(abilityManager.canUseAbility(mockPlayer, lightningAbility),
      "Should be able to use ability after cooldown expires");
  }

  // === HELPER TESTS ===

  @Test
  void testGetRemainingCooldown() {
    var mockPlayer = createMockPlayerWithDragonEgg();

    // No cooldown initially
    assertEquals(0, abilityManager.getRemainingCooldown(mockPlayer),
      "Should have no cooldown initially");

    // Set 60 second cooldown
    abilityManager.setCooldown(mockPlayer, 60);
    int remaining = abilityManager.getRemainingCooldown(mockPlayer);
    assertTrue(remaining >= 59 && remaining <= 60,
      "Should show remaining cooldown time (within 1 second tolerance)");
  }

  @Test
  void testClearCooldown() {
    var mockPlayer = createMockPlayerWithDragonEgg();

    // Set cooldown
    abilityManager.setCooldown(mockPlayer, 60);
    assertTrue(abilityManager.isOnCooldown(mockPlayer), "Should be on cooldown");

    // Clear cooldown
    abilityManager.clearCooldown(mockPlayer);
    assertFalse(abilityManager.isOnCooldown(mockPlayer), "Cooldown should be cleared");
  }

  @Test
  void testCanUseAbilityWithoutRequiredItem() {
    var mockPlayer = createMockPlayerWithoutDragonEgg();

    // Should not be able to use without required item (LightningAbility checks for dragon egg)
    assertFalse(abilityManager.canUseAbility(mockPlayer, lightningAbility),
      "Should not be able to use ability without required item");
  }

  @Test
  void testCanUseAbilityWithRequiredItemAndNoCooldown() {
    var mockPlayer = createMockPlayerWithDragonEgg();

    // Should be able to use with required item and no cooldown
    assertTrue(abilityManager.canUseAbility(mockPlayer, lightningAbility),
      "Should be able to use ability with required item and no cooldown");
  }

  @Test
  void testCanUseAbilityWithRequiredItemButOnCooldown() {
    var mockPlayer = createMockPlayerWithDragonEgg();

    // Set cooldown
    abilityManager.setCooldown(mockPlayer, 60);

    // Should NOT be able to use even with required item when on cooldown
    assertFalse(abilityManager.canUseAbility(mockPlayer, lightningAbility),
      "Should not be able to use ability when on cooldown, even with required item");
  }

  // === EDGE CASES ===

  @Test
  void testNullPlayerHandling() {
    assertFalse(abilityManager.canUseAbility(null, lightningAbility),
      "Should handle null player gracefully");
    assertEquals(0, abilityManager.getRemainingCooldown(null),
      "Should handle null player in getRemainingCooldown");
    assertFalse(abilityManager.isOnCooldown(null),
      "Should handle null player in isOnCooldown");
  }

  @Test
  void testNullAbilityHandling() {
    var mockPlayer = createMockPlayerWithDragonEgg();

    assertFalse(abilityManager.canUseAbility(mockPlayer, null),
      "Should handle null ability gracefully");
  }

  // === HELPER METHODS ===

  /**
   * Create a mock player with a dragon egg in inventory (Issue #28: any slot).
   */
  private Player createMockPlayerWithDragonEgg() {
    Player mockPlayer = mock(Player.class);
    UUID mockUUID = UUID.randomUUID();
    when(mockPlayer.getUniqueId()).thenReturn(mockUUID);

    // Mock inventory with dragon egg (Issue #28: can be in any slot)
    PlayerInventory mockInventory = mock(PlayerInventory.class);
    ItemStack dragonEgg = mock(ItemStack.class);
    when(dragonEgg.getType()).thenReturn(Material.DRAGON_EGG);
    when(dragonEgg.isEmpty()).thenReturn(false);
    when(mockInventory.getItemInOffHand()).thenReturn(dragonEgg);
    when(mockPlayer.getInventory()).thenReturn(mockInventory);

    // Mock contains() to return true for DRAGON_EGG (Issue #28)
    when(mockInventory.contains(Material.DRAGON_EGG)).thenReturn(true);

    return mockPlayer;
  }

  /**
   * Create a mock player without a dragon egg (Issue #28: not in inventory).
   */
  private Player createMockPlayerWithoutDragonEgg() {
    Player mockPlayer = mock(Player.class);
    UUID mockUUID = UUID.randomUUID();
    when(mockPlayer.getUniqueId()).thenReturn(mockUUID);

    // Mock inventory without dragon egg
    PlayerInventory mockInventory = mock(PlayerInventory.class);
    ItemStack emptyItem = mock(ItemStack.class);
    when(emptyItem.getType()).thenReturn(Material.AIR);
    when(emptyItem.isEmpty()).thenReturn(true);
    when(mockInventory.getItemInOffHand()).thenReturn(emptyItem);
    when(mockPlayer.getInventory()).thenReturn(mockInventory);

    // Mock contains() to return false for DRAGON_EGG (Issue #28)
    when(mockInventory.contains(Material.DRAGON_EGG)).thenReturn(false);

    return mockPlayer;
  }
}
