package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FragmentManager - fragment equipping, unequipping, and cooldown management.
 */
class FragmentManagerTest {

  @Test
  @DisplayName("FragmentType enum has all expected values")
  void testFragmentTypeEnumValues() {
    FragmentType[] types = FragmentType.values();
    assertEquals(4, types.length, "Should have 4 fragment types");

    // Verify all fragment types exist
    assertNotNull(FragmentType.BURNING, "BURNING fragment type should exist");
    assertNotNull(FragmentType.AGILITY, "AGILITY fragment type should exist");
    assertNotNull(FragmentType.IMMORTAL, "IMMORTAL fragment type should exist");
    assertNotNull(FragmentType.CORRUPTED, "CORRUPTED fragment type should exist");
  }

  @Test
  @DisplayName("FragmentType fromName returns correct type for valid input")
  void testFragmentTypeFromNameValid() {
    assertEquals(FragmentType.BURNING, FragmentType.fromName("burning"));
    assertEquals(FragmentType.BURNING, FragmentType.fromName("BURNING"));
    assertEquals(FragmentType.AGILITY, FragmentType.fromName("agility"));
    assertEquals(FragmentType.IMMORTAL, FragmentType.fromName("immortal"));
    assertEquals(FragmentType.CORRUPTED, FragmentType.fromName("corrupted"));
  }

  @Test
  @DisplayName("FragmentType fromName returns null for invalid input")
  void testFragmentTypeFromNameInvalid() {
    assertNull(FragmentType.fromName("invalid"));
    assertNull(FragmentType.fromName(""));
    assertNull(FragmentType.fromName(null));
  }

  @Test
  @DisplayName("FragmentType has correct display names")
  void testFragmentTypeDisplayNames() {
    assertEquals("Burning Fragment", FragmentType.BURNING.getDisplayName());
    assertEquals("Agility Fragment", FragmentType.AGILITY.getDisplayName());
    assertEquals("Immortal Fragment", FragmentType.IMMORTAL.getDisplayName());
    assertEquals("Corrupted Core", FragmentType.CORRUPTED.getDisplayName());
  }

  @Test
  @DisplayName("FragmentType has passive bonuses")
  void testFragmentTypePassiveBonuses() {
    assertNotNull(FragmentType.BURNING.getPassiveBonus());
    assertNotNull(FragmentType.AGILITY.getPassiveBonus());
    assertNotNull(FragmentType.IMMORTAL.getPassiveBonus());
    assertNotNull(FragmentType.CORRUPTED.getPassiveBonus());

    // Verify passive bonuses are non-empty
    assertFalse(FragmentType.BURNING.getPassiveBonus().isEmpty());
    assertFalse(FragmentType.AGILITY.getPassiveBonus().isEmpty());
    assertFalse(FragmentType.IMMORTAL.getPassiveBonus().isEmpty());
    assertFalse(FragmentType.CORRUPTED.getPassiveBonus().isEmpty());
  }

  @Test
  @DisplayName("FragmentType has descriptions")
  void testFragmentTypeDescriptions() {
    assertNotNull(FragmentType.BURNING.getDescription());
    assertNotNull(FragmentType.AGILITY.getDescription());
    assertNotNull(FragmentType.IMMORTAL.getDescription());
    assertNotNull(FragmentType.CORRUPTED.getDescription());

    // Verify descriptions are non-empty
    assertFalse(FragmentType.BURNING.getDescription().isEmpty());
    assertFalse(FragmentType.AGILITY.getDescription().isEmpty());
    assertFalse(FragmentType.IMMORTAL.getDescription().isEmpty());
    assertFalse(FragmentType.CORRUPTED.getDescription().isEmpty());
  }

  @Test
  @DisplayName("FragmentType getTypeNames returns all type names")
  void testFragmentTypeGetTypeNames() {
    String[] names = FragmentType.getTypeNames();
    assertEquals(4, names.length, "Should return 4 type names");

    // Verify all names are lowercase
    for (String name : names) {
      assertEquals(name.toLowerCase(), name, "Type names should be lowercase");
    }
  }
}
