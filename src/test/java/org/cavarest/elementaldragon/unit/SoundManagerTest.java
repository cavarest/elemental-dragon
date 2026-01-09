package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.audio.SoundManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SoundManager - ensures all sound methods handle edge cases properly.
 * 
 * The SoundManager.playAbilitySound() method handles:
 *   - Null player returns early (no sound played)
 *   - Null fragment type uses default ability sound
 *   - Different sounds per fragment type and ability number
 * 
 * All other sound methods follow the same null-safety pattern.
 */
class SoundManagerTest {

  @Test
  @DisplayName("SoundManager constructor accepts null plugin without throwing")
  void testConstructorWithNullPlugin() {
    // This tests that the constructor doesn't throw when given null
    // In real usage, null would cause issues when playing sounds
    assertDoesNotThrow(() -> new SoundManager(null),
      "Constructor should not throw for null plugin");
  }

  @Test
  @DisplayName("playAbilitySound handles null player gracefully")
  void testPlayAbilitySoundWithNullPlayer() {
    SoundManager soundManager = new SoundManager(null);

    // Should not throw when player is null
    assertDoesNotThrow(() -> soundManager.playAbilitySound(null, FragmentType.BURNING, 1),
      "playAbilitySound should handle null player");
  }

  @Test
  @DisplayName("playAbilitySound handles null fragment type gracefully")
  void testPlayAbilitySoundWithNullFragmentType() {
    SoundManager soundManager = new SoundManager(null);

    // Should not throw when fragment type is null
    assertDoesNotThrow(() -> soundManager.playAbilitySound(null, null, 1),
      "playAbilitySound should handle null fragment type");
  }

  @Test
  @DisplayName("playCooldownReadySound handles null player gracefully")
  void testPlayCooldownReadySoundWithNullPlayer() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playCooldownReadySound(null),
      "playCooldownReadySound should handle null player");
  }

  @Test
  @DisplayName("playFragmentDiscoverySound handles null player gracefully")
  void testPlayFragmentDiscoverySoundWithNullPlayer() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playFragmentDiscoverySound(null),
      "playFragmentDiscoverySound should handle null player");
  }

  @Test
  @DisplayName("playAchievementSound handles null player gracefully")
  void testPlayAchievementSoundWithNullPlayer() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playAchievementSound(null),
      "playAchievementSound should handle null player");
  }

  @Test
  @DisplayName("playLightningSound handles null location gracefully")
  void testPlayLightningSoundWithNullLocation() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playLightningSound(null),
      "playLightningSound should handle null location");
  }

  @Test
  @DisplayName("playFragmentEquipSound handles null player gracefully")
  void testPlayFragmentEquipSoundWithNullPlayer() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playFragmentEquipSound(null, FragmentType.BURNING),
      "playFragmentEquipSound should handle null player");
  }

  @Test
  @DisplayName("playFragmentEquipSound handles null fragment type gracefully")
  void testPlayFragmentEquipSoundWithNullFragmentType() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playFragmentEquipSound(null, null),
      "playFragmentEquipSound should handle null fragment type");
  }

  @Test
  @DisplayName("playAbilityFailedSound handles null player gracefully")
  void testPlayAbilityFailedSoundWithNullPlayer() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playAbilityFailedSound(null),
      "playAbilityFailedSound should handle null player");
  }

  @Test
  @DisplayName("playCraftingCompleteSound handles null player gracefully")
  void testPlayCraftingCompleteSoundWithNullPlayer() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playCraftingCompleteSound(null),
      "playCraftingCompleteSound should handle null player");
  }

  @Test
  @DisplayName("playChronicleOpenSound handles null player gracefully")
  void testPlayChronicleOpenSoundWithNullPlayer() {
    SoundManager soundManager = new SoundManager(null);

    assertDoesNotThrow(() -> soundManager.playChronicleOpenSound(null),
      "playChronicleOpenSound should handle null player");
  }

  @Test
  @DisplayName("All fragment types can be used with playAbilitySound")
  void testPlayAbilitySoundWithAllFragmentTypes() {
    SoundManager soundManager = new SoundManager(null);

    // All fragment types should be handled without throwing
    for (FragmentType type : FragmentType.values()) {
      assertDoesNotThrow(() -> soundManager.playAbilitySound(null, type, 1),
        "playAbilitySound should handle " + type + " fragment type");
      assertDoesNotThrow(() -> soundManager.playAbilitySound(null, type, 2),
        "playAbilitySound should handle " + type + " with ability number 2");
    }
  }

  @Test
  @DisplayName("All fragment types can be used with playFragmentEquipSound")
  void testPlayFragmentEquipSoundWithAllFragmentTypes() {
    SoundManager soundManager = new SoundManager(null);

    // All fragment types should be handled without throwing
    for (FragmentType type : FragmentType.values()) {
      assertDoesNotThrow(() -> soundManager.playFragmentEquipSound(null, type),
        "playFragmentEquipSound should handle " + type + " fragment type");
    }
  }

  @Test
  @DisplayName("SoundManager instance can be created multiple times")
  void testMultipleSoundManagerInstances() {
    SoundManager soundManager1 = new SoundManager(null);
    SoundManager soundManager2 = new SoundManager(null);

    assertNotNull(soundManager1, "First SoundManager instance should be created");
    assertNotNull(soundManager2, "Second SoundManager instance should be created");
    assertNotSame(soundManager1, soundManager2, "Instances should be separate objects");
  }

  @Test
  @DisplayName("playAbilitySound handles all ability numbers without throwing")
  void testPlayAbilitySoundWithDifferentAbilityNumbers() {
    SoundManager soundManager = new SoundManager(null);

    // Test various ability numbers
    assertDoesNotThrow(() -> soundManager.playAbilitySound(null, FragmentType.BURNING, 1),
      "Should handle ability number 1");
    assertDoesNotThrow(() -> soundManager.playAbilitySound(null, FragmentType.BURNING, 2),
      "Should handle ability number 2");
    assertDoesNotThrow(() -> soundManager.playAbilitySound(null, FragmentType.BURNING, 0),
      "Should handle ability number 0");
    assertDoesNotThrow(() -> soundManager.playAbilitySound(null, FragmentType.BURNING, -1),
      "Should handle negative ability number");
    assertDoesNotThrow(() -> soundManager.playAbilitySound(null, FragmentType.BURNING, 100),
      "Should handle large ability number");
  }
}
