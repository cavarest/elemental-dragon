package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.achievement.Achievement;
import org.cavarest.elementaldragon.achievement.AchievementManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for Achievement system.
 * Tests Achievement enum and AchievementManager functionality.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AchievementTest {

  private AchievementManager achievementManager;

  @BeforeEach
  void setUp() {
    achievementManager = new AchievementManager(null); // null is fine for basic tests
  }

  // === ACHIEVEMENT ENUM TESTS ===

  @Test
  void testAchievementCount() {
    // Should have exactly 11 achievements
    assertEquals(11, Achievement.values().length,
      "Should have 11 achievements defined");
  }

  @Test
  void testAchievementDisplayNames() {
    assertEquals("First Fragment", Achievement.FIRST_FRAGMENT.getDisplayName());
    assertEquals("Dragon Collector", Achievement.ALL_FRAGMENTS.getDisplayName());
    assertEquals("Fire Tamer", Achievement.BURNING_MASTERY.getDisplayName());
    assertEquals("Wind Walker", Achievement.AGILITY_MASTERY.getDisplayName());
    assertEquals("Stone Guardian", Achievement.IMMORTAL_MASTERY.getDisplayName());
    assertEquals("Void Walker", Achievement.CORRUPTED_MASTERY.getDisplayName());
    assertEquals("Lore Seeker", Achievement.LORE_COLLECTOR.getDisplayName());
    assertEquals("Chronicler", Achievement.LORE_MASTER.getDisplayName());
    assertEquals("Storm Caller", Achievement.LIGHTNING_ADEPT.getDisplayName());
    assertEquals("Thunder Lord", Achievement.LIGHTNING_MASTER.getDisplayName());
    assertEquals("Dragon Wielder", Achievement.DRAGON_WIELDER.getDisplayName());
  }

  @Test
  void testAchievementDescriptions() {
    assertNotNull(Achievement.FIRST_FRAGMENT.getDescription());
    assertFalse(Achievement.FIRST_FRAGMENT.getDescription().isEmpty());
    
    assertNotNull(Achievement.DRAGON_WIELDER.getDescription());
    assertFalse(Achievement.DRAGON_WIELDER.getDescription().isEmpty());
  }

  @Test
  void testAchievementCategories() {
    // Test fragment discovery category
    Achievement[] fragmentAchievements = Achievement.getByCategory(
      Achievement.AchievementCategory.FRAGMENT_DISCOVERY);
    assertEquals(2, fragmentAchievements.length);
    assertTrue(containsAchievement(fragmentAchievements, Achievement.FIRST_FRAGMENT));
    assertTrue(containsAchievement(fragmentAchievements, Achievement.ALL_FRAGMENTS));

    // Test ability mastery category
    Achievement[] masteryAchievements = Achievement.getByCategory(
      Achievement.AchievementCategory.ABILITY_MASTERY);
    assertEquals(4, masteryAchievements.length);
    assertTrue(containsAchievement(masteryAchievements, Achievement.BURNING_MASTERY));
    assertTrue(containsAchievement(masteryAchievements, Achievement.AGILITY_MASTERY));
    assertTrue(containsAchievement(masteryAchievements, Achievement.IMMORTAL_MASTERY));
    assertTrue(containsAchievement(masteryAchievements, Achievement.CORRUPTED_MASTERY));

    // Test lore hunter category
    Achievement[] loreAchievements = Achievement.getByCategory(
      Achievement.AchievementCategory.LORE_HUNTER);
    assertEquals(2, loreAchievements.length);
    assertTrue(containsAchievement(loreAchievements, Achievement.LORE_COLLECTOR));
    assertTrue(containsAchievement(loreAchievements, Achievement.LORE_MASTER));

    // Test lightning master category
    Achievement[] lightningAchievements = Achievement.getByCategory(
      Achievement.AchievementCategory.LIGHTNING_MASTER);
    assertEquals(2, lightningAchievements.length);
    assertTrue(containsAchievement(lightningAchievements, Achievement.LIGHTNING_ADEPT));
    assertTrue(containsAchievement(lightningAchievements, Achievement.LIGHTNING_MASTER));

    // Test dragon wielder category
    Achievement[] dragonAchievements = Achievement.getByCategory(
      Achievement.AchievementCategory.DRAGON_WIELDER);
    assertEquals(1, dragonAchievements.length);
    assertEquals(Achievement.DRAGON_WIELDER, dragonAchievements[0]);
  }

  @Test
  void testFragmentTypeAchievements() {
    // Test Burning Fragment achievements
    Achievement[] burningAchievements = Achievement.getByFragmentType(FragmentType.BURNING);
    assertEquals(1, burningAchievements.length);
    assertEquals(Achievement.BURNING_MASTERY, burningAchievements[0]);

    // Test Agility Fragment achievements
    Achievement[] agilityAchievements = Achievement.getByFragmentType(FragmentType.AGILITY);
    assertEquals(1, agilityAchievements.length);
    assertEquals(Achievement.AGILITY_MASTERY, agilityAchievements[0]);

    // Test Immortal Fragment achievements
    Achievement[] immortalAchievements = Achievement.getByFragmentType(FragmentType.IMMORTAL);
    assertEquals(1, immortalAchievements.length);
    assertEquals(Achievement.IMMORTAL_MASTERY, immortalAchievements[0]);

    // Test Corrupted Fragment achievements
    Achievement[] corruptedAchievements = Achievement.getByFragmentType(FragmentType.CORRUPTED);
    assertEquals(1, corruptedAchievements.length);
    assertEquals(Achievement.CORRUPTED_MASTERY, corruptedAchievements[0]);

    // Test null fragment type
    Achievement[] nullAchievements = Achievement.getByFragmentType(null);
    assertEquals(0, nullAchievements.length);
  }

  @Test
  void testAchievementCriteria() {
    assertEquals(Achievement.AchievementCriteria.FRAGMENT_EQUIP,
      Achievement.FIRST_FRAGMENT.getCriteria());
    assertEquals(Achievement.AchievementCriteria.ALL_FRAGMENTS,
      Achievement.ALL_FRAGMENTS.getCriteria());
    assertEquals(Achievement.AchievementCriteria.FRAGMENT_MASTERY,
      Achievement.BURNING_MASTERY.getCriteria());
    assertEquals(Achievement.AchievementCriteria.LORE_UNLOCK,
      Achievement.LORE_COLLECTOR.getCriteria());
    assertEquals(Achievement.AchievementCriteria.LIGHTNING_USE,
      Achievement.LIGHTNING_ADEPT.getCriteria());
    assertEquals(Achievement.AchievementCriteria.ALL_MASTERY,
      Achievement.DRAGON_WIELDER.getCriteria());
  }

  @Test
  void testRequiredCounts() {
    assertEquals(1, Achievement.FIRST_FRAGMENT.getRequiredCount());
    assertEquals(4, Achievement.ALL_FRAGMENTS.getRequiredCount());
    assertEquals(10, Achievement.BURNING_MASTERY.getRequiredCount());
    assertEquals(5, Achievement.LORE_COLLECTOR.getRequiredCount());
    assertEquals(50, Achievement.LIGHTNING_ADEPT.getRequiredCount());
    assertEquals(100, Achievement.LIGHTNING_MASTER.getRequiredCount());
    assertEquals(8, Achievement.DRAGON_WIELDER.getRequiredCount());
  }

  @Test
  void testProgressString() {
    assertEquals("0/10", Achievement.BURNING_MASTERY.getProgressString(0));
    assertEquals("5/10", Achievement.BURNING_MASTERY.getProgressString(5));
    assertEquals("10/10", Achievement.BURNING_MASTERY.getProgressString(10));
    assertEquals("15/10", Achievement.BURNING_MASTERY.getProgressString(15));
  }

  @Test
  void testIsComplete() {
    assertFalse(Achievement.LIGHTNING_ADEPT.isComplete(49));
    assertTrue(Achievement.LIGHTNING_ADEPT.isComplete(50));
    assertTrue(Achievement.LIGHTNING_ADEPT.isComplete(100));
  }

  @Test
  void testFromName() {
    assertEquals(Achievement.FIRST_FRAGMENT, Achievement.fromName("first_fragment"));
    assertEquals(Achievement.FIRST_FRAGMENT, Achievement.fromName("FIRST_FRAGMENT"));
    assertEquals(Achievement.FIRST_FRAGMENT, Achievement.fromName("first"));
    assertEquals(Achievement.LIGHTNING_MASTER, Achievement.fromName("lightning_master"));
    assertNull(Achievement.fromName("nonexistent"));
    assertNull(Achievement.fromName(null));
    assertNull(Achievement.fromName(""));
  }

  @Test
  void testGetByCategoryNull() {
    Achievement[] achievements = Achievement.getByCategory(null);
    assertEquals(0, achievements.length);
  }

  // === ACHIEVEMENT MANAGER TESTS ===

  @Test
  void testInitialState() {
    Player mockPlayer = createMockPlayer();

    // Should have no achievements initially
    Set<Achievement> unlocked = achievementManager.getUnlockedAchievements(mockPlayer);
    assertTrue(unlocked.isEmpty(), "Should have no unlocked achievements initially");

    // Should have 0 unlocked count
    assertEquals(0, achievementManager.getUnlockedCount(mockPlayer));

    // Should have 11 total achievements
    assertEquals(11, achievementManager.getTotalAchievementCount());
  }

  @Test
  void testUnlockAchievement() {
    Player mockPlayer = createMockPlayer();

    // Unlock an achievement
    achievementManager.unlockAchievement(mockPlayer, Achievement.FIRST_FRAGMENT);

    // Should now have the achievement
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.FIRST_FRAGMENT));
    assertEquals(1, achievementManager.getUnlockedCount(mockPlayer));

    // Progress should be at required count
    assertEquals(1, achievementManager.getAchievementProgress(mockPlayer, Achievement.FIRST_FRAGMENT));
  }

  @Test
  void testUnlockMultipleAchievements() {
    Player mockPlayer = createMockPlayer();

    // Unlock multiple achievements
    achievementManager.unlockAchievement(mockPlayer, Achievement.FIRST_FRAGMENT);
    achievementManager.unlockAchievement(mockPlayer, Achievement.LIGHTNING_ADEPT);
    achievementManager.unlockAchievement(mockPlayer, Achievement.LORE_COLLECTOR);

    // Should have all three achievements
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.FIRST_FRAGMENT));
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.LIGHTNING_ADEPT));
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.LORE_COLLECTOR));
    assertEquals(3, achievementManager.getUnlockedCount(mockPlayer));
  }

  @Test
  void testDuplicateUnlockIgnored() {
    Player mockPlayer = createMockPlayer();

    // Unlock same achievement twice
    achievementManager.unlockAchievement(mockPlayer, Achievement.FIRST_FRAGMENT);
    achievementManager.unlockAchievement(mockPlayer, Achievement.FIRST_FRAGMENT);

    // Should still only have one
    assertEquals(1, achievementManager.getUnlockedCount(mockPlayer));
  }

  @Test
  void testUnlockNullAchievement() {
    Player mockPlayer = createMockPlayer();

    // Should not throw exception
    assertDoesNotThrow(() ->
      achievementManager.unlockAchievement(mockPlayer, null)
    );

    // Should not have any achievements
    assertEquals(0, achievementManager.getUnlockedCount(mockPlayer));
  }

  @Test
  void testUnlockForNullPlayer() {
    // Should not throw exception
    assertDoesNotThrow(() ->
      achievementManager.unlockAchievement(null, Achievement.FIRST_FRAGMENT)
    );
  }

  @Test
  void testHasAchievementNullPlayer() {
    assertFalse(achievementManager.hasAchievement(null, Achievement.FIRST_FRAGMENT));
    assertFalse(achievementManager.hasAchievement(createMockPlayer(), null));
    assertFalse(achievementManager.hasAchievement(null, null));
  }

  @Test
  void testGetProgressNullHandling() {
    Player mockPlayer = createMockPlayer();

    assertEquals(0, achievementManager.getAchievementProgress(null, Achievement.FIRST_FRAGMENT));
    assertEquals(0, achievementManager.getAchievementProgress(mockPlayer, null));
    assertEquals(0, achievementManager.getAchievementProgress(null, null));
  }

  @Test
  void testGetProgressString() {
    Player mockPlayer = createMockPlayer();

    // Before unlocking
    assertEquals("0/50", achievementManager.getProgressString(mockPlayer, Achievement.LIGHTNING_ADEPT));

    // Unlock partially
    achievementManager.unlockAchievement(mockPlayer, Achievement.LIGHTNING_ADEPT);
    assertEquals("50/50", achievementManager.getProgressString(mockPlayer, Achievement.LIGHTNING_ADEPT));
  }

  @Test
  void testLightningUsageTracking() {
    Player mockPlayer = createMockPlayer();

    assertEquals(0, achievementManager.getLightningUsageCount(mockPlayer));

    // Register lightning uses
    achievementManager.registerLightningUse(mockPlayer);
    achievementManager.registerLightningUse(mockPlayer);
    achievementManager.registerLightningUse(mockPlayer);

    assertEquals(3, achievementManager.getLightningUsageCount(mockPlayer));
  }

  @Test
  void testLightningUsageAchievements() {
    Player mockPlayer = createMockPlayer();

    // Use lightning 50 times (should unlock LIGHTNING_ADEPT)
    for (int i = 0; i < 50; i++) {
      achievementManager.registerLightningUse(mockPlayer);
    }

    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.LIGHTNING_ADEPT));
    assertFalse(achievementManager.hasAchievement(mockPlayer, Achievement.LIGHTNING_MASTER));
  }

  @Test
  void testLightningMasterAchievement() {
    Player mockPlayer = createMockPlayer();

    // Use lightning 100 times (should unlock both achievements)
    for (int i = 0; i < 100; i++) {
      achievementManager.registerLightningUse(mockPlayer);
    }

    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.LIGHTNING_ADEPT));
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.LIGHTNING_MASTER));
  }

  @Test
  void testFragmentEquipTracking() {
    Player mockPlayer = createMockPlayer();

    // Initially no fragments equipped
    Set<FragmentType> equipped = achievementManager.getEquippedFragments(mockPlayer);
    assertTrue(equipped.isEmpty());

    // Equip Burning Fragment
    achievementManager.registerFragmentEquip(mockPlayer, FragmentType.BURNING);
    equipped = achievementManager.getEquippedFragments(mockPlayer);
    assertEquals(1, equipped.size());
    assertTrue(equipped.contains(FragmentType.BURNING));

    // Should unlock FIRST_FRAGMENT achievement
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.FIRST_FRAGMENT));
  }

  @Test
  void testAllFragmentsAchievement() {
    Player mockPlayer = createMockPlayer();

    // Equip all 4 fragments
    achievementManager.registerFragmentEquip(mockPlayer, FragmentType.BURNING);
    achievementManager.registerFragmentEquip(mockPlayer, FragmentType.AGILITY);
    achievementManager.registerFragmentEquip(mockPlayer, FragmentType.IMMORTAL);
    achievementManager.registerFragmentEquip(mockPlayer, FragmentType.CORRUPTED);

    // Should unlock ALL_FRAGMENTS achievement
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.ALL_FRAGMENTS));
  }

  @Test
  void testFragmentAbilityUsageTracking() {
    Player mockPlayer = createMockPlayer();

    assertEquals(0, achievementManager.getFragmentAbilityUsageCount(
      mockPlayer, FragmentType.BURNING, 1));
    assertEquals(0, achievementManager.getFragmentAbilityUsageCount(
      mockPlayer, FragmentType.BURNING, 2));

    // Use abilities
    achievementManager.registerAbilityUse(mockPlayer, FragmentType.BURNING, 1);
    achievementManager.registerAbilityUse(mockPlayer, FragmentType.BURNING, 1);
    achievementManager.registerAbilityUse(mockPlayer, FragmentType.BURNING, 2);

    assertEquals(2, achievementManager.getFragmentAbilityUsageCount(
      mockPlayer, FragmentType.BURNING, 1));
    assertEquals(1, achievementManager.getFragmentAbilityUsageCount(
      mockPlayer, FragmentType.BURNING, 2));
  }

  @Test
  void testFragmentMasteryAchievement() {
    Player mockPlayer = createMockPlayer();

    // Use Burning Fragment abilities 10 times each
    for (int i = 0; i < 10; i++) {
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.BURNING, 1);
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.BURNING, 2);
    }

    // Should unlock BURNING_MASTERY
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.BURNING_MASTERY));
  }

  @Test
  void testAllMasteryAchievement() {
    Player mockPlayer = createMockPlayer();

    // Master all 8 abilities (2 per fragment * 4 fragments)
    for (int i = 0; i < 10; i++) {
      // Burning
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.BURNING, 1);
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.BURNING, 2);
      // Agility
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.AGILITY, 1);
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.AGILITY, 2);
      // Immortal
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.IMMORTAL, 1);
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.IMMORTAL, 2);
      // Corrupted
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.CORRUPTED, 1);
      achievementManager.registerAbilityUse(mockPlayer, FragmentType.CORRUPTED, 2);
    }

    // Should unlock DRAGON_WIELDER
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.DRAGON_WIELDER));
  }

  @Test
  void testResetProgress() {
    Player mockPlayer = createMockPlayer();

    // Unlock some achievements
    achievementManager.unlockAchievement(mockPlayer, Achievement.FIRST_FRAGMENT);
    achievementManager.unlockAchievement(mockPlayer, Achievement.LIGHTNING_ADEPT);
    achievementManager.registerLightningUse(mockPlayer);
    achievementManager.registerFragmentEquip(mockPlayer, FragmentType.BURNING);

    // Should have achievements
    assertEquals(2, achievementManager.getUnlockedCount(mockPlayer));
    assertEquals(1, achievementManager.getLightningUsageCount(mockPlayer));

    // Reset progress
    achievementManager.resetProgress(mockPlayer);

    // Should be cleared
    assertEquals(0, achievementManager.getUnlockedCount(mockPlayer));
    assertEquals(0, achievementManager.getLightningUsageCount(mockPlayer));
  }

  @Test
  void testResetNullPlayer() {
    // Should not throw exception
    assertDoesNotThrow(() ->
      achievementManager.resetProgress(null)
    );
  }

  @Test
  void testGetAchievementByName() {
    assertEquals(Achievement.FIRST_FRAGMENT,
      achievementManager.getAchievementByName("first_fragment"));
    assertEquals(Achievement.LIGHTNING_MASTER,
      achievementManager.getAchievementByName("lightning_master"));
    assertNull(achievementManager.getAchievementByName("nonexistent"));
  }

  @Test
  void testGetAchievementsByCategory() {
    Achievement[] fragmentAchievements = achievementManager.getAchievementsByCategory(
      Achievement.AchievementCategory.FRAGMENT_DISCOVERY);
    assertEquals(2, fragmentAchievements.length);
  }

  @Test
  void testGetCategories() {
    Achievement.AchievementCategory[] categories = achievementManager.getCategories();
    assertEquals(5, categories.length);
  }

  @Test
  void testGetUnlockedAchievementsImmutable() {
    Player mockPlayer = createMockPlayer();

    achievementManager.unlockAchievement(mockPlayer, Achievement.FIRST_FRAGMENT);

    Set<Achievement> unlocked = achievementManager.getUnlockedAchievements(mockPlayer);

    // Attempting to modify should not affect internal state
    unlocked.clear();
    assertTrue(achievementManager.hasAchievement(mockPlayer, Achievement.FIRST_FRAGMENT));
  }

  @Test
  void testIsolatedPlayerData() {
    Player mockPlayer1 = createMockPlayer();
    Player mockPlayer2 = createMockPlayer();

    // Unlock achievement for player 1
    achievementManager.unlockAchievement(mockPlayer1, Achievement.FIRST_FRAGMENT);

    // Player 2 should not have any achievements
    assertFalse(achievementManager.hasAchievement(mockPlayer2, Achievement.FIRST_FRAGMENT));
    assertEquals(0, achievementManager.getUnlockedCount(mockPlayer2));

    // Player 1 should have the achievement
    assertTrue(achievementManager.hasAchievement(mockPlayer1, Achievement.FIRST_FRAGMENT));
    assertEquals(1, achievementManager.getUnlockedCount(mockPlayer1));
  }

  // === HELPER METHODS ===

  /**
   * Create a mock player with a unique UUID.
   */
  private Player createMockPlayer() {
    Player mockPlayer = mock(Player.class);
    UUID mockUUID = UUID.randomUUID();
    when(mockPlayer.getUniqueId()).thenReturn(mockUUID);
    return mockPlayer;
  }

  /**
   * Check if an array contains a specific achievement.
   */
  private boolean containsAchievement(Achievement[] array, Achievement achievement) {
    for (Achievement a : array) {
      if (a == achievement) {
        return true;
      }
    }
    return false;
  }
}
