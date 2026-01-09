package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.fragment.Fragment;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.fragment.BurningFragment;
import org.cavarest.elementaldragon.fragment.AgilityFragment;
import org.cavarest.elementaldragon.fragment.ImmortalFragment;
import org.cavarest.elementaldragon.fragment.CorruptedCoreFragment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for Fragment abilities - activation, cooldowns, and passive effects.
 */
class FragmentAbilityTest {

  @Test
  @DisplayName("All fragment types implement Fragment interface")
  void testFragmentInterfaceImplementation() {
    // Test that we can create fragment instances (with null plugin for basic tests)
    // The fragments will have null plugin, but we can test their properties
    assertTrue(createBurningFragment() instanceof Fragment);
    assertTrue(createAgilityFragment() instanceof Fragment);
    assertTrue(createImmortalFragment() instanceof Fragment);
    assertTrue(createCorruptedFragment() instanceof Fragment);
  }

  @Test
  @DisplayName("Fragment getName returns correct name")
  void testFragmentGetName() {
    assertEquals("Burning Fragment", createBurningFragment().getName());
    assertEquals("Agility Fragment", createAgilityFragment().getName());
    assertEquals("Immortal Fragment", createImmortalFragment().getName());
    assertEquals("Corrupted Core", createCorruptedFragment().getName());
  }

  @Test
  @DisplayName("Fragment getType returns correct FragmentType")
  void testFragmentGetType() {
    assertEquals(FragmentType.BURNING, createBurningFragment().getType());
    assertEquals(FragmentType.AGILITY, createAgilityFragment().getType());
    assertEquals(FragmentType.IMMORTAL, createImmortalFragment().getType());
    assertEquals(FragmentType.CORRUPTED, createCorruptedFragment().getType());
  }

  @Test
  @DisplayName("Fragment getCooldownMillis returns positive value")
  void testFragmentGetCooldownMillis() {
    assertTrue(createBurningFragment().getCooldownMillis() > 0,
      "Burning fragment cooldown should be positive");
    assertTrue(createAgilityFragment().getCooldownMillis() > 0,
      "Agility fragment cooldown should be positive");
    assertTrue(createImmortalFragment().getCooldownMillis() > 0,
      "Immortal fragment cooldown should be positive");
    assertTrue(createCorruptedFragment().getCooldownMillis() > 0,
      "Corrupted fragment cooldown should be positive");
  }

  @Test
  @DisplayName("Fragment getCooldownMillis returns reasonable values")
  void testFragmentCooldownValues() {
    // Burning: Dragon's Wrath is 40 seconds
    assertEquals(40000L, createBurningFragment().getCooldownMillis());

    // Agility: Draconic Surge is 30 seconds
    assertEquals(30000L, createAgilityFragment().getCooldownMillis());

    // Immortal: Draconic Reflex is 90 seconds
    assertEquals(90000L, createImmortalFragment().getCooldownMillis());

    // Corrupted: Dread Gaze is 60 seconds
    assertEquals(60000L, createCorruptedFragment().getCooldownMillis());
  }

  @Test
  @DisplayName("Fragment getLore returns non-empty list")
  void testFragmentGetLore() {
    assertNotNull(createBurningFragment().getLore());
    assertNotNull(createAgilityFragment().getLore());
    assertNotNull(createImmortalFragment().getLore());
    assertNotNull(createCorruptedFragment().getLore());

    // Verify lore lists are non-empty
    assertFalse(createBurningFragment().getLore().isEmpty());
    assertFalse(createAgilityFragment().getLore().isEmpty());
    assertFalse(createImmortalFragment().getLore().isEmpty());
    assertFalse(createCorruptedFragment().getLore().isEmpty());
  }

  @Test
  @DisplayName("Fragment getDescription returns non-empty string")
  void testFragmentGetDescription() {
    assertNotNull(createBurningFragment().getDescription());
    assertNotNull(createAgilityFragment().getDescription());
    assertNotNull(createImmortalFragment().getDescription());
    assertNotNull(createCorruptedFragment().getDescription());

    assertFalse(createBurningFragment().getDescription().isEmpty());
    assertFalse(createAgilityFragment().getDescription().isEmpty());
    assertFalse(createImmortalFragment().getDescription().isEmpty());
    assertFalse(createCorruptedFragment().getDescription().isEmpty());
  }

  @Test
  @DisplayName("Fragment lore contains ability descriptions")
  void testFragmentLoreContainsAbilities() {
    List<String> burningLore = createBurningFragment().getLore();
    assertTrue(containsAny(burningLore, "Dragon's Wrath", "Infernal Dominion"),
      "Burning fragment lore should mention abilities");

    List<String> agilityLore = createAgilityFragment().getLore();
    assertTrue(containsAny(agilityLore, "Draconic Surge", "Wing Burst"),
      "Agility fragment lore should mention abilities");

    List<String> immortalLore = createImmortalFragment().getLore();
    assertTrue(containsAny(immortalLore, "Draconic Reflex", "Essence Rebirth"),
      "Immortal fragment lore should mention abilities");

    List<String> corruptedLore = createCorruptedFragment().getLore();
    assertTrue(containsAny(corruptedLore, "Dread Gaze", "Life Devourer"),
      "Corrupted fragment lore should mention abilities");
  }

  @Test
  @DisplayName("Fragment lore mentions passive bonuses")
  void testFragmentLoreMentionsPassive() {
    List<String> burningLore = createBurningFragment().getLore();
    assertTrue(containsAny(burningLore, "fire resistance", "Passive"),
      "Burning fragment lore should mention passive");

    List<String> agilityLore = createAgilityFragment().getLore();
    assertTrue(containsAny(agilityLore, "Speed", "Passive"),
      "Agility fragment lore should mention passive");

    List<String> immortalLore = createImmortalFragment().getLore();
    assertTrue(containsAny(immortalLore, "knockback", "hearts", "Passive"),
      "Immortal fragment lore should mention passive");

    List<String> corruptedLore = createCorruptedFragment().getLore();
    assertTrue(containsAny(corruptedLore, "Night Vision", "Passive"),
      "Corrupted fragment lore should mention passive");
  }

  @Test
  @DisplayName("FragmentType has correct element names")
  void testFragmentTypeElementNames() {
    assertEquals("fire", FragmentType.BURNING.getElement());
    assertEquals("wind", FragmentType.AGILITY.getElement());
    assertEquals("earth", FragmentType.IMMORTAL.getElement());
    assertEquals("void", FragmentType.CORRUPTED.getElement());
  }

  @Test
  @DisplayName("FragmentType has non-null colors")
  void testFragmentTypeColors() {
    assertNotNull(FragmentType.BURNING.getColor());
    assertNotNull(FragmentType.AGILITY.getColor());
    assertNotNull(FragmentType.IMMORTAL.getColor());
    assertNotNull(FragmentType.CORRUPTED.getColor());
  }

  @Test
  @DisplayName("FragmentType has non-null particle types")
  void testFragmentTypeParticleTypes() {
    assertNotNull(FragmentType.BURNING.getParticleType());
    assertNotNull(FragmentType.AGILITY.getParticleType());
    assertNotNull(FragmentType.IMMORTAL.getParticleType());
    assertNotNull(FragmentType.CORRUPTED.getParticleType());
  }

  @Test
  @DisplayName("FragmentType has formatted description")
  void testFragmentTypeFormattedDescription() {
    String burningDesc = FragmentType.BURNING.getFormattedDescription();
    assertNotNull(burningDesc);
    assertFalse(burningDesc.isEmpty());

    String agilityDesc = FragmentType.AGILITY.getFormattedDescription();
    assertNotNull(agilityDesc);
    assertFalse(agilityDesc.isEmpty());
  }

  // Helper methods to create fragment instances with null plugin
  private BurningFragment createBurningFragment() {
    return new BurningFragment(null);
  }

  private AgilityFragment createAgilityFragment() {
    return new AgilityFragment(null);
  }

  private ImmortalFragment createImmortalFragment() {
    return new ImmortalFragment(null);
  }

  private CorruptedCoreFragment createCorruptedFragment() {
    return new CorruptedCoreFragment(null);
  }

  /**
   * Check if a list contains any of the specified strings.
   */
  private boolean containsAny(List<String> list, String... values) {
    for (String value : values) {
      for (String item : list) {
        if (item != null && item.contains(value)) {
          return true;
        }
      }
    }
    return false;
  }
}
