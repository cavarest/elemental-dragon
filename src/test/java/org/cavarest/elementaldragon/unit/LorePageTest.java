package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.lore.LorePage;
import org.cavarest.elementaldragon.lore.LorePage.UnlockTrigger;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LorePage - chronicle page definitions and unlock conditions.
 * Updated for expanded 19-page Chronicle Book.
 */
class LorePageTest {

  @Test
  @DisplayName("LorePage enum has exactly 19 pages")
  void testLorePageCount() {
    assertEquals(19, LorePage.values().length, "Should have 19 lore pages");
  }

  @Test
  @DisplayName("All LorePage instances have valid page numbers")
  void testLorePageNumbersValid() {
    for (LorePage page : LorePage.values()) {
      assertTrue(page.getPageNumber() >= 1, "Page number should be >= 1");
      assertTrue(page.getPageNumber() <= 19, "Page number should be <= 19");
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
    LorePage ignis = LorePage.IGNIS_1;
    assertEquals(UnlockTrigger.ABILITY_USE, ignis.getTrigger());
    assertEquals(FragmentType.BURNING, ignis.getFragmentType());
    assertEquals(5, ignis.getRequiredCount());
  }

  @Test
  @DisplayName("ZEPHYR page has ABILITY_USE trigger for AGILITY fragment")
  void testZephyrTrigger() {
    LorePage zephyr = LorePage.ZEPHYR_1;
    assertEquals(UnlockTrigger.ABILITY_USE, zephyr.getTrigger());
    assertEquals(FragmentType.AGILITY, zephyr.getFragmentType());
    assertEquals(5, zephyr.getRequiredCount());
  }

  @Test
  @DisplayName("TERRA page has ABILITY_USE trigger for IMMORTAL fragment")
  void testTerraTrigger() {
    LorePage terra = LorePage.TERRA_1;
    assertEquals(UnlockTrigger.ABILITY_USE, terra.getTrigger());
    assertEquals(FragmentType.IMMORTAL, terra.getFragmentType());
    assertEquals(3, terra.getRequiredCount());
  }

  @Test
  @DisplayName("UMBRA page has ABILITY_USE trigger for CORRUPTED fragment")
  void testUmbraTrigger() {
    LorePage umbra = LorePage.UMBRA_1;
    assertEquals(UnlockTrigger.ABILITY_USE, umbra.getTrigger());
    assertEquals(FragmentType.CORRUPTED, umbra.getFragmentType());
    assertEquals(3, umbra.getRequiredCount());
  }

  @Test
  @DisplayName("THE_FALL page has EQUIP_ALL_FRAGMENTS trigger")
  void testTheFallTrigger() {
    LorePage theFall = LorePage.THE_FALL_1;
    assertEquals(UnlockTrigger.EQUIP_ALL_FRAGMENTS, theFall.getTrigger());
    assertNull(theFall.getFragmentType());
    assertEquals(0, theFall.getRequiredCount());
  }

  @Test
  @DisplayName("RECOVERY page has MASTER_ALL_ABILITIES trigger")
  void testRecoveryTrigger() {
    LorePage recovery = LorePage.RECOVERY_1;
    assertEquals(UnlockTrigger.MASTER_ALL_ABILITIES, recovery.getTrigger());
    assertNull(recovery.getFragmentType());
    assertEquals(0, recovery.getRequiredCount());
  }

  @Test
  @DisplayName("fromPageNumber returns correct page for valid numbers")
  void testFromPageNumberValid() {
    assertEquals(LorePage.INTRODUCTION, LorePage.fromPageNumber(1));
    assertEquals(LorePage.IGNIS_1, LorePage.fromPageNumber(2));
    assertEquals(LorePage.IGNIS_2, LorePage.fromPageNumber(3));
    assertEquals(LorePage.IGNIS_3, LorePage.fromPageNumber(4));
    assertEquals(LorePage.ZEPHYR_1, LorePage.fromPageNumber(5));
    assertEquals(LorePage.ZEPHYR_2, LorePage.fromPageNumber(6));
    assertEquals(LorePage.ZEPHYR_3, LorePage.fromPageNumber(7));
    assertEquals(LorePage.TERRA_1, LorePage.fromPageNumber(8));
    assertEquals(LorePage.UMBRA_1, LorePage.fromPageNumber(11));
    assertEquals(LorePage.THE_FALL_1, LorePage.fromPageNumber(14));
    assertEquals(LorePage.RECOVERY_1, LorePage.fromPageNumber(17));
    assertEquals(LorePage.RECOVERY_3, LorePage.fromPageNumber(19));
  }

  @Test
  @DisplayName("fromPageNumber returns null for invalid numbers")
  void testFromPageNumberInvalid() {
    assertNull(LorePage.fromPageNumber(0));
    assertNull(LorePage.fromPageNumber(20));
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
    assertTrue(LorePage.IGNIS_1.getContent().toLowerCase().contains("ignis"),
      "IGNIS page should mention IGNIS");
    assertTrue(LorePage.ZEPHYR_1.getContent().toLowerCase().contains("zephyr"),
      "ZEPHYR page should mention ZEPHYR");
    assertTrue(LorePage.TERRA_1.getContent().toLowerCase().contains("terra"),
      "TERRA page should mention TERRA");
    assertTrue(LorePage.UMBRA_1.getContent().toLowerCase().contains("umbra"),
      "UMBRA page should mention UMBRA");
  }

  @Test
  @DisplayName("INTRODUCTION page content mentions Chronicle")
  void testIntroductionMentionsChronicle() {
    String content = LorePage.INTRODUCTION.getContent();
    assertTrue(content.toLowerCase().contains("chronicle"),
      "INTRODUCTION page should mention Chronicle");
  }

  @Test
  @DisplayName("THE_FALL pages content mentions dragons or fragments")
  void testTheFallMentionsRelevantContent() {
    String content1 = LorePage.THE_FALL_1.getContent();
    String content2 = LorePage.THE_FALL_2.getContent();
    String content3 = LorePage.THE_FALL_3.getContent();
    
    assertTrue(content1.toLowerCase().contains("dragon") || 
               content1.toLowerCase().contains("element"),
      "THE_FALL_1 should mention dragons or elements");
    assertTrue(content2.toLowerCase().contains("dragon") || 
               content2.toLowerCase().contains("sacrifice"),
      "THE_FALL_2 should mention dragons or sacrifice");
    assertTrue(content3.toLowerCase().contains("fragment") || 
               content3.toLowerCase().contains("essence"),
      "THE_FALL_3 should mention fragments or essence");
  }

  @Test
  @DisplayName("RECOVERY pages content mentions mastery or abilities")
  void testRecoveryMentionsRelevantContent() {
    String content1 = LorePage.RECOVERY_1.getContent();
    String content2 = LorePage.RECOVERY_2.getContent();
    String content3 = LorePage.RECOVERY_3.getContent();
    
    assertTrue(content1.toLowerCase().contains("worthy") ||
               content1.toLowerCase().contains("fragment"),
      "RECOVERY_1 should mention worthy or fragments");
    assertTrue(content2.toLowerCase().contains("master") || 
               content2.toLowerCase().contains("channel"),
      "RECOVERY_2 should mention mastery or channeling");
    assertTrue(content3.toLowerCase().contains("dragon") || 
               content3.toLowerCase().contains("legacy"),
      "RECOVERY_3 should mention dragons or legacy");
  }
  
  @Test
  @DisplayName("Multi-page chapters have progressive unlock requirements")
  void testProgressiveUnlockRequirements() {
    assertEquals(5, LorePage.IGNIS_1.getRequiredCount());
    assertEquals(10, LorePage.IGNIS_2.getRequiredCount());
    assertEquals(20, LorePage.IGNIS_3.getRequiredCount());
    
    assertEquals(5, LorePage.ZEPHYR_1.getRequiredCount());
    assertEquals(10, LorePage.ZEPHYR_2.getRequiredCount());
    assertEquals(20, LorePage.ZEPHYR_3.getRequiredCount());
    
    assertEquals(3, LorePage.TERRA_1.getRequiredCount());
    assertEquals(10, LorePage.TERRA_2.getRequiredCount());
    assertEquals(20, LorePage.TERRA_3.getRequiredCount());
    
    assertEquals(3, LorePage.UMBRA_1.getRequiredCount());
    assertEquals(10, LorePage.UMBRA_2.getRequiredCount());
    assertEquals(20, LorePage.UMBRA_3.getRequiredCount());
  }
  
  @Test
  @DisplayName("Fall chapter has different unlock requirements per page")
  void testFallChapterUnlockRequirements() {
    assertEquals(0, LorePage.THE_FALL_1.getRequiredCount());
    assertEquals(10, LorePage.THE_FALL_2.getRequiredCount());
    assertEquals(25, LorePage.THE_FALL_3.getRequiredCount());
  }
  
  @Test
  @DisplayName("Recovery chapter has progressive mastery requirements")
  void testRecoveryChapterRequirements() {
    assertEquals(0, LorePage.RECOVERY_1.getRequiredCount());
    assertEquals(25, LorePage.RECOVERY_2.getRequiredCount());
    assertEquals(50, LorePage.RECOVERY_3.getRequiredCount());
    
    assertEquals(UnlockTrigger.MASTER_ALL_ABILITIES, LorePage.RECOVERY_1.getTrigger());
    assertEquals(UnlockTrigger.MASTER_ALL_ABILITIES, LorePage.RECOVERY_2.getTrigger());
    assertEquals(UnlockTrigger.MASTER_ALL_ABILITIES, LorePage.RECOVERY_3.getTrigger());
  }
}
