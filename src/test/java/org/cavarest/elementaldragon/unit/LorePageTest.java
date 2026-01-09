package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.lore.LorePage;
import org.cavarest.elementaldragon.lore.LorePage.UnlockTrigger;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LorePage - chronicle page definitions and unlock conditions.
 */
class LorePageTest {

  @Test
  @DisplayName("LorePage enum has exactly 7 pages")
  void testLorePageCount() {
    assertEquals(7, LorePage.values().length, "Should have 7 lore pages");
  }

  @Test
  @DisplayName("All LorePage instances have valid page numbers")
  void testLorePageNumbersValid() {
    for (LorePage page : LorePage.values()) {
      assertTrue(page.getPageNumber() >= 1, "Page number should be >= 1");
      assertTrue(page.getPageNumber() <= 7, "Page number should be <= 7");
    }
  }

  @Test
  @DisplayName("All LorePage instances have non-empty titles")
  void testLorePageTitlesNotEmpty() {
    for (LorePage page : LorePage.values()) {
      assertNotNull(page.getTitle(), "Page title should not be null");
      assertFalse(page.getTitle().isEmpty(), "Page title should not be empty");
    }
  }

  @Test
  @DisplayName("All LorePage instances have non-empty content")
  void testLorePageContentNotEmpty() {
    for (LorePage page : LorePage.values()) {
      assertNotNull(page.getContent(), "Page content should not be null");
      assertFalse(page.getContent().isEmpty(), "Page content should not be empty");
    }
  }

  @Test
  @DisplayName("All LorePage instances have valid unlock triggers")
  void testLorePageTriggersValid() {
    for (LorePage page : LorePage.values()) {
      assertNotNull(page.getTrigger(), "Page trigger should not be null");
      assertNotNull(UnlockTrigger.valueOf(page.getTrigger().name()),
        "Page trigger should be a valid enum value");
    }
  }

  @Test
  @DisplayName("INTRODUCTION page has ALWAYS trigger")
  void testIntroductionTrigger() {
    LorePage introduction = LorePage.INTRODUCTION;
    assertEquals(UnlockTrigger.ALWAYS, introduction.getTrigger());
    assertNull(introduction.getFragmentType());
    assertEquals(0, introduction.getRequiredCount());
  }

  @Test
  @DisplayName("IGNIS page has ABILITY_USE trigger for BURNING fragment")
  void testIgnisTrigger() {
    LorePage ignis = LorePage.IGNIS;
    assertEquals(UnlockTrigger.ABILITY_USE, ignis.getTrigger());
    assertEquals(FragmentType.BURNING, ignis.getFragmentType());
    assertEquals(5, ignis.getRequiredCount());
  }

  @Test
  @DisplayName("ZEPHYR page has ABILITY_USE trigger for AGILITY fragment")
  void testZephyrTrigger() {
    LorePage zephyr = LorePage.ZEPHYR;
    assertEquals(UnlockTrigger.ABILITY_USE, zephyr.getTrigger());
    assertEquals(FragmentType.AGILITY, zephyr.getFragmentType());
    assertEquals(5, zephyr.getRequiredCount());
  }

  @Test
  @DisplayName("TERRA page has ABILITY_USE trigger for IMMORTAL fragment")
  void testTerraTrigger() {
    LorePage terra = LorePage.TERRA;
    assertEquals(UnlockTrigger.ABILITY_USE, terra.getTrigger());
    assertEquals(FragmentType.IMMORTAL, terra.getFragmentType());
    assertEquals(3, terra.getRequiredCount());
  }

  @Test
  @DisplayName("UMBRA page has ABILITY_USE trigger for CORRUPTED fragment")
  void testUmbraTrigger() {
    LorePage umbra = LorePage.UMBRA;
    assertEquals(UnlockTrigger.ABILITY_USE, umbra.getTrigger());
    assertEquals(FragmentType.CORRUPTED, umbra.getFragmentType());
    assertEquals(3, umbra.getRequiredCount());
  }

  @Test
  @DisplayName("THE_FALL page has EQUIP_ALL_FRAGMENTS trigger")
  void testTheFallTrigger() {
    LorePage theFall = LorePage.THE_FALL;
    assertEquals(UnlockTrigger.EQUIP_ALL_FRAGMENTS, theFall.getTrigger());
    assertNull(theFall.getFragmentType());
    assertEquals(0, theFall.getRequiredCount());
  }

  @Test
  @DisplayName("RECOVERY page has MASTER_ALL_ABILITIES trigger")
  void testRecoveryTrigger() {
    LorePage recovery = LorePage.RECOVERY;
    assertEquals(UnlockTrigger.MASTER_ALL_ABILITIES, recovery.getTrigger());
    assertNull(recovery.getFragmentType());
    assertEquals(0, recovery.getRequiredCount());
  }

  @Test
  @DisplayName("fromPageNumber returns correct page for valid numbers")
  void testFromPageNumberValid() {
    assertEquals(LorePage.INTRODUCTION, LorePage.fromPageNumber(1));
    assertEquals(LorePage.IGNIS, LorePage.fromPageNumber(2));
    assertEquals(LorePage.ZEPHYR, LorePage.fromPageNumber(3));
    assertEquals(LorePage.TERRA, LorePage.fromPageNumber(4));
    assertEquals(LorePage.UMBRA, LorePage.fromPageNumber(5));
    assertEquals(LorePage.THE_FALL, LorePage.fromPageNumber(6));
    assertEquals(LorePage.RECOVERY, LorePage.fromPageNumber(7));
  }

  @Test
  @DisplayName("fromPageNumber returns null for invalid numbers")
  void testFromPageNumberInvalid() {
    assertNull(LorePage.fromPageNumber(0));
    assertNull(LorePage.fromPageNumber(8));
    assertNull(LorePage.fromPageNumber(-1));
    assertNull(LorePage.fromPageNumber(100));
  }

  @Test
  @DisplayName("UnlockTrigger enum has all expected values")
  void testUnlockTriggerValues() {
    UnlockTrigger[] triggers = UnlockTrigger.values();
    assertEquals(4, triggers.length, "Should have 4 unlock triggers");
    
    assertNotNull(UnlockTrigger.ALWAYS);
    assertNotNull(UnlockTrigger.ABILITY_USE);
    assertNotNull(UnlockTrigger.EQUIP_ALL_FRAGMENTS);
    assertNotNull(UnlockTrigger.MASTER_ALL_ABILITIES);
  }

  @Test
  @DisplayName("LorePage content mentions dragon names")
  void testLorePageContentMentionsDragons() {
    // IGNIS is the Fire Dragon
    assertTrue(LorePage.IGNIS.getContent().toLowerCase().contains("dragon") ||
               LorePage.IGNIS.getContent().toLowerCase().contains("fire"),
      "IGNIS page should mention dragon or fire");
    
    // ZEPHYR is the Wind Dragon
    assertTrue(LorePage.ZEPHYR.getContent().toLowerCase().contains("dragon") ||
               LorePage.ZEPHYR.getContent().toLowerCase().contains("wind"),
      "ZEPHYR page should mention dragon or wind");
    
    // TERRA is the Earth Dragon
    assertTrue(LorePage.TERRA.getContent().toLowerCase().contains("dragon") ||
               LorePage.TERRA.getContent().toLowerCase().contains("earth"),
      "TERRA page should mention dragon or earth");
    
    // UMBRA is the Void Dragon
    assertTrue(LorePage.UMBRA.getContent().toLowerCase().contains("dragon") ||
               LorePage.UMBRA.getContent().toLowerCase().contains("void"),
      "UMBRA page should mention dragon or void");
  }

  @Test
  @DisplayName("INTRODUCTION page content mentions Chronicle")
  void testIntroductionMentionsChronicle() {
    String content = LorePage.INTRODUCTION.getContent();
    assertTrue(content.toLowerCase().contains("chronicle"),
      "INTRODUCTION page should mention Chronicle");
  }

  @Test
  @DisplayName("THE_FALL page content mentions fragments")
  void testTheFallMentionsFragments() {
    String content = LorePage.THE_FALL.getContent();
    assertTrue(content.toLowerCase().contains("fragment"),
      "THE_FALL page should mention fragments");
  }

  @Test
  @DisplayName("RECOVERY page content mentions mastery")
  void testRecoveryMentionsMastery() {
    String content = LorePage.RECOVERY.getContent();
    assertTrue(content.toLowerCase().contains("master") ||
               content.toLowerCase().contains("ability"),
      "RECOVERY page should mention mastery or ability");
  }
}
