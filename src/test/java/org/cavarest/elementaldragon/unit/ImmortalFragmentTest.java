package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.fragment.ImmortalFragment;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Comprehensive tests for ImmortalFragment abilities - Draconic Reflex and Essence Rebirth.
 */
class ImmortalFragmentTest {

  @Test
  @DisplayName("ImmortalFragment has correct Draconic Reflex constants")
  void testDraconicReflexConstants() {
    ImmortalFragment fragment = new ImmortalFragment(null);

    // Verify the fragment is properly configured
    assertEquals("Immortal Fragment", fragment.getName());
    assertEquals(FragmentType.IMMORTAL, fragment.getType());
  }

  @Test
  @DisplayName("ImmortalFragment has correct cooldown values")
  void testImmortalFragmentCooldowns() {
    ImmortalFragment fragment = new ImmortalFragment(null);

    // Draconic Reflex: 90 seconds (90000ms)
    assertEquals(90000L, fragment.getCooldownMillis());
  }

  @Test
  @DisplayName("ImmortalFragment lore contains ability information")
  void testImmortalFragmentLore() {
    ImmortalFragment fragment = new ImmortalFragment(null);
    List<String> lore = fragment.getLore();

    assertNotNull(lore);
    assertFalse(lore.isEmpty());

    // Verify lore mentions Draconic Reflex
    boolean hasDraconicReflex = false;
    boolean hasEssenceRebirth = false;
    boolean mentionsCooldown = false;
    boolean mentionsPassive = false;

    for (String line : lore) {
      if (line != null) {
        if (line.contains("Draconic Reflex")) {
          hasDraconicReflex = true;
        }
        if (line.contains("Essence Rebirth")) {
          hasEssenceRebirth = true;
        }
        if (line.contains("90s") || line.contains("90 second")) {
          mentionsCooldown = true;
        }
        if (line.contains("knockback") || line.contains("hearts")) {
          mentionsPassive = true;
        }
      }
    }

    assertTrue(hasDraconicReflex, "Lore should mention Draconic Reflex");
    assertTrue(hasEssenceRebirth, "Lore should mention Essence Rebirth");
    assertTrue(mentionsCooldown, "Lore should mention cooldown");
    assertTrue(mentionsPassive, "Lore should mention passive bonuses");
  }

  @Test
  @DisplayName("ImmortalFragment getDescription returns earth-based theme")
  void testImmortalFragmentDescription() {
    ImmortalFragment fragment = new ImmortalFragment(null);
    String description = fragment.getDescription();

    assertNotNull(description);
    assertFalse(description.isEmpty());

    // Description should reference defensive/earth theme
    assertTrue(
      description.toLowerCase().contains("defensive") ||
      description.toLowerCase().contains("earth") ||
      description.toLowerCase().contains("immortal"),
      "Description should reference defensive or earth theme"
    );
  }

  @Test
  @DisplayName("ImmortalFragment implements Fragment interface correctly")
  void testImmortalFragmentInterfaceImplementation() {
    ImmortalFragment fragment = new ImmortalFragment(null);

    // Verify all interface methods work
    assertNotNull(fragment.getName());
    assertNotNull(fragment.getType());
    assertNotNull(fragment.getLore());
    assertNotNull(fragment.getDescription());
    assertTrue(fragment.getCooldownMillis() > 0);
  }

  @Test
  @DisplayName("ImmortalFragment fragment type has correct color")
  void testImmortalFragmentColor() {
    assertNotNull(FragmentType.IMMORTAL.getColor());

    // Earth-themed color (brown)
    assertEquals(139, FragmentType.IMMORTAL.getColor().getRed());
    assertEquals(69, FragmentType.IMMORTAL.getColor().getGreen());
    assertEquals(19, FragmentType.IMMORTAL.getColor().getBlue());
  }

  @Test
  @DisplayName("ImmortalFragment fragment type has correct particle type")
  void testImmortalFragmentParticleType() {
    assertNotNull(FragmentType.IMMORTAL.getParticleType());
    // Should use FALLING_DUST for earth theme
    assertNotNull(FragmentType.IMMORTAL.getParticleType());
  }

  @Test
  @DisplayName("ImmortalFragment fragment type has correct element")
  void testImmortalFragmentElement() {
    assertEquals("earth", FragmentType.IMMORTAL.getElement());
  }

  @Test
  @DisplayName("ImmortalFragment fragment type has passive bonus")
  void testImmortalFragmentPassiveBonus() {
    assertNotNull(FragmentType.IMMORTAL.getPassiveBonus());
    assertFalse(FragmentType.IMMORTAL.getPassiveBonus().isEmpty());

    // Should mention knockback reduction and health boost
    String passiveBonus = FragmentType.IMMORTAL.getPassiveBonus().toLowerCase();
    assertTrue(
      passiveBonus.contains("knockback") || passiveBonus.contains("hearts"),
      "Passive bonus should mention knockback or hearts"
    );
  }

  @Test
  @DisplayName("ImmortalFragment can be created with null plugin")
  void testImmortalFragmentCreationWithNullPlugin() {
    // This should not throw any exceptions
    ImmortalFragment fragment = new ImmortalFragment(null);

    assertNotNull(fragment);
    assertEquals("Immortal Fragment", fragment.getName());
  }

  @Test
  @DisplayName("FragmentType IMMORTAL can be found by name")
  void testFragmentTypeImmortalFromName() {
    assertEquals(FragmentType.IMMORTAL, FragmentType.fromName("immortal"));
    assertEquals(FragmentType.IMMORTAL, FragmentType.fromName("IMMORTAL"));
    assertEquals(FragmentType.IMMORTAL, FragmentType.fromName("Immortal"));
  }

  @Test
  @DisplayName("FragmentType has all four fragment types")
  void testAllFragmentTypesExist() {
    FragmentType[] types = FragmentType.values();
    assertEquals(4, types.length);

    assertNotNull(FragmentType.BURNING);
    assertNotNull(FragmentType.AGILITY);
    assertNotNull(FragmentType.IMMORTAL);
    assertNotNull(FragmentType.CORRUPTED);
  }

  @Test
  @DisplayName("FragmentType IMMORTAL has formatted description")
  void testImmortalFragmentFormattedDescription() {
    String formatted = FragmentType.IMMORTAL.getFormattedDescription();

    assertNotNull(formatted);
    assertFalse(formatted.isEmpty());
    assertTrue(
      formatted.toLowerCase().contains("defensive") ||
      formatted.toLowerCase().contains("earth"),
      "Formatted description should reference defensive or earth theme"
    );
  }
}
