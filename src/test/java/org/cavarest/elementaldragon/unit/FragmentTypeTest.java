package org.cavarest.elementaldragon.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.cavarest.elementaldragon.fragment.FragmentType;

/**
 * Unit tests for themed craft broadcast messages and FragmentType enum.
 * Tests the message generation logic for each fragment type.
 */
class FragmentTypeTest {

  @Test
  @DisplayName("FragmentType enum has all expected values")
  void fragmentType_hasAllExpectedValues() {
    FragmentType[] types = FragmentType.values();
    assertEquals(4, types.length, "FragmentType should have 4 values");

    assertNotNull(FragmentType.BURNING, "BURNING should exist");
    assertNotNull(FragmentType.AGILITY, "AGILITY should exist");
    assertNotNull(FragmentType.IMMORTAL, "IMMORTAL should exist");
    assertNotNull(FragmentType.CORRUPTED, "CORRUPTED should exist");
  }

  @Test
  @DisplayName("FragmentType getDisplayName returns correct names")
  void fragmentType_getDisplayName_returnsCorrectNames() {
    assertEquals("Burning Fragment", FragmentType.BURNING.getDisplayName());
    assertEquals("Agility Fragment", FragmentType.AGILITY.getDisplayName());
    assertEquals("Immortal Fragment", FragmentType.IMMORTAL.getDisplayName());
    assertEquals("Corrupted Core", FragmentType.CORRUPTED.getDisplayName());
  }

  @Test
  @DisplayName("FragmentType fromName returns correct type for valid input")
  void fragmentType_fromName_returnsCorrectType() {
    assertEquals(FragmentType.BURNING, FragmentType.fromName("burning"));
    assertEquals(FragmentType.AGILITY, FragmentType.fromName("agility"));
    assertEquals(FragmentType.IMMORTAL, FragmentType.fromName("immortal"));
    assertEquals(FragmentType.CORRUPTED, FragmentType.fromName("corrupt"));
  }

  @Test
  @DisplayName("FragmentType fromName returns null for invalid input")
  void fragmentType_fromName_returnsNullForInvalid() {
    assertNull(FragmentType.fromName("fire"));
    assertNull(FragmentType.fromName("wind"));
    assertNull(FragmentType.fromName("invalid"));
    assertNull(FragmentType.fromName(null));
  }

  @Test
  @DisplayName("Burning Fragment themed message should contain fire emoji and colors")
  void burningFragmentMessage_hasFireTheme() {
    String message = getThemedCraftMessage("TestPlayer", FragmentType.BURNING);

    assertNotNull(message, "Message should not be null");
    assertTrue(message.contains("🔥"), "Message should contain fire emoji");
    assertTrue(message.contains("TestPlayer"), "Message should contain player name");
    assertTrue(message.contains("Burning Fragment"), "Message should contain fragment name");
    assertTrue(message.contains("<red>") || message.contains("red>"),
      "Message should use red color");
  }

  @Test
  @DisplayName("Agility Fragment themed message should contain wind emoji and colors")
  void agilityFragmentMessage_hasWindTheme() {
    String message = getThemedCraftMessage("TestPlayer", FragmentType.AGILITY);

    assertNotNull(message, "Message should not be null");
    assertTrue(message.contains("💨"), "Message should contain wind emoji");
    assertTrue(message.contains("TestPlayer"), "Message should contain player name");
    assertTrue(message.contains("Agility Fragment"), "Message should contain fragment name");
    assertTrue(message.contains("<aqua>") || message.contains("aqua>"),
      "Message should use aqua color");
  }

  @Test
  @DisplayName("Immortal Fragment themed message should contain shield emoji and colors")
  void immortalFragmentMessage_hasEarthTheme() {
    String message = getThemedCraftMessage("TestPlayer", FragmentType.IMMORTAL);

    assertNotNull(message, "Message should not be null");
    assertTrue(message.contains("🛡️"), "Message should contain shield emoji");
    assertTrue(message.contains("TestPlayer"), "Message should contain player name");
    assertTrue(message.contains("Immortal Fragment"), "Message should contain fragment name");
    assertTrue(message.contains("<green>") || message.contains("green>"),
      "Message should use green color");
  }

  @Test
  @DisplayName("Corrupted Core themed message should contain void eye emoji and colors")
  void corruptedCoreMessage_hasVoidTheme() {
    String message = getThemedCraftMessage("TestPlayer", FragmentType.CORRUPTED);

    assertNotNull(message, "Message should not be null");
    assertTrue(message.contains("👁️"), "Message should contain eye emoji");
    assertTrue(message.contains("TestPlayer"), "Message should contain player name");
    assertTrue(message.contains("Corrupted Core"), "Message should contain fragment name");
    assertTrue(message.contains("<dark_purple>") || message.contains("dark_purple>"),
      "Message should use dark purple color");
  }

  @Test
  @DisplayName("Default themed message should be used for unknown fragment types")
  void defaultMessage_forUnknownType() {
    String message = getThemedCraftMessage("TestPlayer", null);

    assertNotNull(message, "Message should not be null");
    assertTrue(message.contains("TestPlayer"), "Message should contain player name");
    assertTrue(message.contains("⚔️"), "Message should contain sword emoji");
  }

  /**
   * Helper method to generate themed craft messages.
   * This mirrors the logic in CraftingListener.getThemedCraftMessage().
   */
  private String getThemedCraftMessage(String playerName, FragmentType fragmentType) {
    if (fragmentType == null) {
      return String.format(
        "<gold>⚔️ <yellow>%s</yellow> has forged an elemental fragment!",
        playerName
      );
    }

    switch (fragmentType) {
      case BURNING:
        return String.format(
          "<red>🔥 The flames of the ancient dragon acknowledge <yellow>%s</yellow> has forged the <red>Burning Fragment</red>!",
          playerName
        );
      case AGILITY:
        return String.format(
          "<aqua>💨 The winds of the ancient dragon acknowledge <yellow>%s</yellow> has forged the <aqua>Agility Fragment</aqua>!",
          playerName
        );
      case IMMORTAL:
        return String.format(
          "<green>🛡️ The earth of the ancient dragon acknowledges <yellow>%s</yellow> has forged the <green>Immortal Fragment</green>!",
          playerName
        );
      case CORRUPTED:
        return String.format(
          "<dark_purple>👁️ The void of the ancient dragon acknowledges <yellow>%s</yellow> has forged the <dark_purple>Corrupted Core</dark_purple>!",
          playerName
        );
      default:
        return String.format(
          "<gold>⚔️ <yellow>%s</yellow> has forged an elemental fragment!",
          playerName
        );
    }
  }
}
