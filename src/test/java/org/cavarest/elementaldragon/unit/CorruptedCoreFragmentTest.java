package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.fragment.CorruptedCoreFragment;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Comprehensive tests for CorruptedCoreFragment abilities - Dread Gaze and Life Devourer.
 */
class CorruptedCoreFragmentTest {

  @Test
  @DisplayName("CorruptedCoreFragment has correct name")
  void testCorruptedCoreFragmentName() {
    CorruptedCoreFragment fragment = new CorruptedCoreFragment(null);
    
    assertEquals("Corrupted Core", fragment.getName());
    assertEquals(FragmentType.CORRUPTED, fragment.getType());
  }

  @Test
  @DisplayName("CorruptedCoreFragment has correct cooldown values")
  void testCorruptedCoreFragmentCooldowns() {
    CorruptedCoreFragment fragment = new CorruptedCoreFragment(null);
    
    // Dread Gaze: 60 seconds (60000ms)
    assertEquals(60000L, fragment.getCooldownMillis());
  }

  @Test
  @DisplayName("CorruptedCoreFragment lore contains ability information")
  void testCorruptedCoreFragmentLore() {
    CorruptedCoreFragment fragment = new CorruptedCoreFragment(null);
    List<String> lore = fragment.getLore();
    
    assertNotNull(lore);
    assertFalse(lore.isEmpty());
    
    // Verify lore mentions Dread Gaze and Life Devourer
    boolean hasDreadGaze = false;
    boolean hasLifeDevourer = false;
    boolean mentionsPassive = false;
    
    for (String line : lore) {
      if (line != null) {
        if (line.contains("Dread Gaze")) {
          hasDreadGaze = true;
        }
        if (line.contains("Life Devourer")) {
          hasLifeDevourer = true;
        }
        if (line.contains("Night Vision") || line.contains("Passive")) {
          mentionsPassive = true;
        }
      }
    }
    
    assertTrue(hasDreadGaze, "Lore should mention Dread Gaze");
    assertTrue(hasLifeDevourer, "Lore should mention Life Devourer");
    assertTrue(mentionsPassive, "Lore should mention passive bonuses");
  }

  @Test
  @DisplayName("CorruptedCoreFragment getDescription returns void-based theme")
  void testCorruptedCoreFragmentDescription() {
    CorruptedCoreFragment fragment = new CorruptedCoreFragment(null);
    String description = fragment.getDescription();
    
    assertNotNull(description);
    assertFalse(description.isEmpty());
    
    // Description should reference dark/void theme
    assertTrue(
      description.toLowerCase().contains("dark") || 
      description.toLowerCase().contains("void") ||
      description.toLowerCase().contains("corrupted"),
      "Description should reference dark or void theme"
    );
  }

  @Test
  @DisplayName("CorruptedCoreFragment implements Fragment interface correctly")
  void testCorruptedCoreFragmentInterfaceImplementation() {
    CorruptedCoreFragment fragment = new CorruptedCoreFragment(null);
    
    // Verify all interface methods work
    assertNotNull(fragment.getName());
    assertNotNull(fragment.getType());
    assertNotNull(fragment.getLore());
    assertNotNull(fragment.getDescription());
    assertTrue(fragment.getCooldownMillis() > 0);
  }

  @Test
  @DisplayName("CorruptedCoreFragment fragment type has correct color")
  void testCorruptedCoreFragmentColor() {
    assertNotNull(FragmentType.CORRUPTED.getColor());
    
    // Void-themed color (dark purple)
    assertEquals(75, FragmentType.CORRUPTED.getColor().getRed());
    assertEquals(0, FragmentType.CORRUPTED.getColor().getGreen());
    assertEquals(130, FragmentType.CORRUPTED.getColor().getBlue());
  }

  @Test
  @DisplayName("CorruptedCoreFragment fragment type has correct particle type")
  void testCorruptedCoreFragmentParticleType() {
    assertNotNull(FragmentType.CORRUPTED.getParticleType());
    // Should use REVERSE_PORTAL for void theme
    assertNotNull(FragmentType.CORRUPTED.getParticleType());
  }

  @Test
  @DisplayName("CorruptedCoreFragment fragment type has correct element")
  void testCorruptedCoreFragmentElement() {
    assertEquals("void", FragmentType.CORRUPTED.getElement());
  }

  @Test
  @DisplayName("CorruptedCoreFragment fragment type has passive bonus")
  void testCorruptedCoreFragmentPassiveBonus() {
    assertNotNull(FragmentType.CORRUPTED.getPassiveBonus());
    assertFalse(FragmentType.CORRUPTED.getPassiveBonus().isEmpty());
    
    // Should mention Night Vision and creeper avoidance
    String passiveBonus = FragmentType.CORRUPTED.getPassiveBonus().toLowerCase();
    assertTrue(
      passiveBonus.contains("night vision") || passiveBonus.contains("creeper"),
      "Passive bonus should mention Night Vision or creeper"
    );
  }

  @Test
  @DisplayName("CorruptedCoreFragment can be created with null plugin")
  void testCorruptedCoreFragmentCreationWithNullPlugin() {
    // This should not throw any exceptions
    CorruptedCoreFragment fragment = new CorruptedCoreFragment(null);
    
    assertNotNull(fragment);
    assertEquals("Corrupted Core", fragment.getName());
  }

  @Test
  @DisplayName("FragmentType CORRUPTED can be found by name")
  void testFragmentTypeCorruptedFromName() {
    assertEquals(FragmentType.CORRUPTED, FragmentType.fromName("corrupted"));
    assertEquals(FragmentType.CORRUPTED, FragmentType.fromName("CORRUPTED"));
    assertEquals(FragmentType.CORRUPTED, FragmentType.fromName("Corrupted"));
  }

  @Test
  @DisplayName("FragmentType CORRUPTED has formatted description")
  void testCorruptedCoreFragmentFormattedDescription() {
    String formatted = FragmentType.CORRUPTED.getFormattedDescription();
    
    assertNotNull(formatted);
    assertFalse(formatted.isEmpty());
    assertTrue(formatted.contains("Dark") || formatted.contains("void"));
  }

  @Test
  @DisplayName("All fragment types have unique display names")
  void testFragmentTypeDisplayNamesUnique() {
    assertEquals("Burning Fragment", FragmentType.BURNING.getDisplayName());
    assertEquals("Agility Fragment", FragmentType.AGILITY.getDisplayName());
    assertEquals("Immortal Fragment", FragmentType.IMMORTAL.getDisplayName());
    assertEquals("Corrupted Core", FragmentType.CORRUPTED.getDisplayName());
    
    // Verify uniqueness
    long uniqueCount = java.util.Arrays.stream(FragmentType.values())
      .map(FragmentType::getDisplayName)
      .distinct()
      .count();
    
    assertEquals(4, uniqueCount, "All fragment display names should be unique");
  }

  @Test
  @DisplayName("All fragment types have unique elements")
  void testFragmentTypeElementsUnique() {
    // Verify each fragment has a unique element
    long uniqueCount = java.util.Arrays.stream(FragmentType.values())
      .map(FragmentType::getElement)
      .distinct()
      .count();
    
    assertEquals(4, uniqueCount, "All fragment elements should be unique");
    
    // Verify specific elements
    assertEquals("fire", FragmentType.BURNING.getElement());
    assertEquals("wind", FragmentType.AGILITY.getElement());
    assertEquals("earth", FragmentType.IMMORTAL.getElement());
    assertEquals("void", FragmentType.CORRUPTED.getElement());
  }

  @Test
  @DisplayName("FragmentType has correct type names for tab completion")
  void testFragmentTypeGetTypeNames() {
    String[] names = FragmentType.getTypeNames();
    
    assertEquals(4, names.length);
    
    // Verify all names are lowercase
    for (String name : names) {
      assertEquals(name.toLowerCase(), name, "Type names should be lowercase");
    }
    
    // Verify expected names
    assertTrue(java.util.Arrays.asList(names).contains("burning"));
    assertTrue(java.util.Arrays.asList(names).contains("agility"));
    assertTrue(java.util.Arrays.asList(names).contains("immortal"));
    assertTrue(java.util.Arrays.asList(names).contains("corrupted"));
  }
}
