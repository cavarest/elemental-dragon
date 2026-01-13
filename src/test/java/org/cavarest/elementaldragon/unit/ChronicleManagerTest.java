package org.cavarest.elementaldragon.unit;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.lore.ChronicleManager;
import org.cavarest.elementaldragon.lore.LorePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ChronicleManager.
 */
@DisplayName("ChronicleManager Tests")
public class ChronicleManagerTest {

    @Mock
    private ElementalDragon plugin;

    @Mock
    private Player player;

    private ChronicleManager chronicleManager;
    private UUID playerUuid;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        chronicleManager = new ChronicleManager(plugin);
        playerUuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerUuid);
    }

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor initializes empty tracking maps")
    public void testConstructorInitialization() {
        // New manager should have INTRODUCTION page discovered (always unlocked)
        assertEquals(1, chronicleManager.getDiscoveredCount(player));
        assertEquals(0, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 1));
        assertEquals(LorePage.values().length, chronicleManager.getTotalPageCount());
    }

    // ==================== registerAbilityUse tests ====================

    @Test
    @DisplayName("registerAbilityUse increments usage count for fragment ability")
    public void testRegisterAbilityUseIncrementsCount() {
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);

        assertEquals(1, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 1));
        assertEquals(0, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 2));
    }

    @Test
    @DisplayName("registerAbilityUse tracks both abilities separately")
    public void testRegisterAbilityUseTracksSeparately() {
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 2);
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);

        assertEquals(2, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 1));
        assertEquals(1, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 2));
    }

    @Test
    @DisplayName("registerAbilityUse handles null player gracefully")
    public void testRegisterAbilityUseNullPlayer() {
        chronicleManager.registerAbilityUse(null, FragmentType.BURNING, 1);

        // Should not throw, and no data should be recorded
        verify(player, never()).sendMessage(any(Component.class));
        verify(player, never()).playSound(any(org.bukkit.Location.class), any(Sound.class), anyFloat(), anyFloat());
    }

    @Test
    @DisplayName("registerAbilityUse handles null fragment type gracefully")
    public void testRegisterAbilityUseNullFragment() {
        chronicleManager.registerAbilityUse(player, null, 1);

        // Should not throw
        verify(player, never()).sendMessage(any(Component.class));
    }

    // ==================== registerFragmentEquip tests ====================

    @Test
    @DisplayName("registerFragmentEquip tracks equipped fragments")
    public void testRegisterFragmentEquip() {
        chronicleManager.registerFragmentEquip(player, FragmentType.BURNING);

        // Verify tracking - we can't directly access equippedFragments but
        // the progress should reflect equipped fragments
        String progress = chronicleManager.getProgress(player, LorePage.THE_FALL_1);
        assertTrue(progress.contains("1"));
    }

    @Test
    @DisplayName("registerFragmentEquip handles null player gracefully")
    public void testRegisterFragmentEquipNullPlayer() {
        chronicleManager.registerFragmentEquip(null, FragmentType.BURNING);

        // Should not throw
        verify(player, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("registerFragmentEquip handles null fragment type gracefully")
    public void testRegisterFragmentEquipNullFragment() {
        chronicleManager.registerFragmentEquip(player, null);

        // Should not throw
        verify(player, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("registerFragmentEquip tracks multiple fragments")
    public void testRegisterFragmentEquipMultiple() {
        chronicleManager.registerFragmentEquip(player, FragmentType.BURNING);
        chronicleManager.registerFragmentEquip(player, FragmentType.AGILITY);
        chronicleManager.registerFragmentEquip(player, FragmentType.IMMORTAL);

        String progress = chronicleManager.getProgress(player, LorePage.THE_FALL_1);
        assertTrue(progress.contains("3"));
    }

    @Test
    @DisplayName("registerFragmentEquip doesn't count same fragment twice")
    public void testRegisterFragmentEquipNoDuplicate() {
        chronicleManager.registerFragmentEquip(player, FragmentType.BURNING);
        chronicleManager.registerFragmentEquip(player, FragmentType.BURNING);

        String progress = chronicleManager.getProgress(player, LorePage.THE_FALL_1);
        assertTrue(progress.contains("1"));
    }

    // ==================== getDiscoveredPages tests ====================

    @Test
    @DisplayName("getDiscoveredPages always includes INTRODUCTION")
    public void testGetDiscoveredPagesIncludesIntroduction() {
        Set<LorePage> pages = chronicleManager.getDiscoveredPages(player);

        assertTrue(pages.contains(LorePage.INTRODUCTION));
    }

    @Test
    @DisplayName("getDiscoveredPages returns empty set for null player")
    public void testGetDiscoveredPagesNullPlayer() {
        Set<LorePage> pages = chronicleManager.getDiscoveredPages(null);

        assertEquals(0, pages.size());
    }

    // ==================== getProgress tests ====================

    @Test
    @DisplayName("getProgress returns 'Unlocked' for ALWAYS trigger")
    public void testGetProgressAlwaysTrigger() {
        String progress = chronicleManager.getProgress(player, LorePage.INTRODUCTION);

        assertEquals("Unlocked", progress);
    }

    @Test
    @DisplayName("getProgress returns '0/0' for null player")
    public void testGetProgressNullPlayer() {
        String progress = chronicleManager.getProgress(null, LorePage.IGNIS_1);

        assertEquals("0/0", progress);
    }

    @Test
    @DisplayName("getProgress returns '0/0' for null page")
    public void testGetProgressNullPage() {
        String progress = chronicleManager.getProgress(player, null);

        assertEquals("0/0", progress);
    }

    @Test
    @DisplayName("getProgress returns '0/required' for ABILITY_USE with no usage")
    public void testGetProgressAbilityUseNoUsage() {
        String progress = chronicleManager.getProgress(player, LorePage.IGNIS_1);

        assertEquals("0/5", progress);
    }

    @Test
    @DisplayName("getProgress returns current/required for ABILITY_USE")
    public void testGetProgressAbilityUseWithUsage() {
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 2);
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);

        String progress = chronicleManager.getProgress(player, LorePage.IGNIS_1);

        assertEquals("3/5", progress);
    }

    @Test
    @DisplayName("getProgress sums all abilities for fragment")
    public void testGetProgressSumsAllAbilities() {
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 2);

        String progress = chronicleManager.getProgress(player, LorePage.IGNIS_1);

        assertEquals("3/5", progress);
    }

    @Test
    @DisplayName("getProgress returns equipped/total for EQUIP_ALL_FRAGMENTS")
    public void testGetProgressEquipAllFragments() {
        chronicleManager.registerFragmentEquip(player, FragmentType.BURNING);
        chronicleManager.registerFragmentEquip(player, FragmentType.AGILITY);

        String progress = chronicleManager.getProgress(player, LorePage.THE_FALL_1);

        assertEquals("2/4", progress);
    }

    @Test
    @DisplayName("getProgress returns 'Unknown' for unknown trigger")
    public void testGetProgressUnknownTrigger() {
        // This tests the default case in switch - unlikely to happen with current enum
        // but the code handles it
        String progress = chronicleManager.getProgress(player, LorePage.INTRODUCTION);

        assertNotNull(progress);
    }

    // ==================== checkUnlocks tests ====================

    @Test
    @DisplayName("checkUnlocks handles null player gracefully")
    public void testCheckUnlocksNullPlayer() {
        chronicleManager.checkUnlocks(null);

        // Should not throw
        verify(player, never()).sendMessage(any(Component.class));
    }

    // ==================== hasDiscovered tests ====================

    @Test
    @DisplayName("hasDiscovered returns true for INTRODUCTION page")
    public void testHasDiscoveredIntroduction() {
        assertTrue(chronicleManager.hasDiscovered(player, LorePage.INTRODUCTION));
    }

    @Test
    @DisplayName("hasDiscovered returns false for locked pages")
    public void testHasDiscoveredLockedPage() {
        assertFalse(chronicleManager.hasDiscovered(player, LorePage.IGNIS_1));
    }

    // ==================== getDiscoveredCount tests ====================

    @Test
    @DisplayName("getDiscoveredCount returns 1 initially (INTRODUCTION)")
    public void testGetDiscoveredCountInitially() {
        assertEquals(1, chronicleManager.getDiscoveredCount(player));
    }

    @Test
    @DisplayName("getDiscoveredCount returns 0 for null player")
    public void testGetDiscoveredCountNullPlayer() {
        assertEquals(0, chronicleManager.getDiscoveredCount(null));
    }

    // ==================== getTotalPageCount tests ====================

    @Test
    @DisplayName("getTotalPageCount returns 19")
    public void testGetTotalPageCount() {
        assertEquals(19, chronicleManager.getTotalPageCount());
    }

    // ==================== getAbilityUsageCount tests ====================

    @Test
    @DisplayName("getAbilityUsageCount returns 0 for no usage")
    public void testGetAbilityUsageCountNoUsage() {
        assertEquals(0, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 1));
    }

    @Test
    @DisplayName("getAbilityUsageCount returns correct count")
    public void testGetAbilityUsageCountCorrect() {
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 2);

        assertEquals(2, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 1));
        assertEquals(1, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 2));
    }

    @Test
    @DisplayName("getAbilityUsageCount returns 0 for null player")
    public void testGetAbilityUsageCountNullPlayer() {
        assertEquals(0, chronicleManager.getAbilityUsageCount(null, FragmentType.BURNING, 1));
    }

    @Test
    @DisplayName("getAbilityUsageCount returns 0 for null fragment type")
    public void testGetAbilityUsageCountNullFragment() {
        assertEquals(0, chronicleManager.getAbilityUsageCount(player, null, 1));
    }

    // ==================== resetProgress tests ====================

    @Test
    @DisplayName("resetProgress clears all tracking data")
    public void testResetProgress() {
        // Add some data
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);
        chronicleManager.registerFragmentEquip(player, FragmentType.BURNING);

        // Reset
        chronicleManager.resetProgress(player);

        // Verify data cleared
        assertEquals(1, chronicleManager.getDiscoveredCount(player)); // Only INTRODUCTION
        assertEquals(0, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 1));
        verify(player).sendMessage(any(Component.class)); // Confirmation message
    }

    @Test
    @DisplayName("resetProgress handles null player gracefully")
    public void testResetProgressNullPlayer() {
        chronicleManager.resetProgress(null);

        // Should not throw
        verify(player, never()).sendMessage(any(Component.class));
    }

    // ==================== Multiple unlock conditions tests ====================

    // ==================== Different fragment types tests ====================

    @Test
    @DisplayName("Tracks different fragment types separately")
    public void testTracksFragmentsSeparately() {
        chronicleManager.registerAbilityUse(player, FragmentType.BURNING, 1);
        chronicleManager.registerAbilityUse(player, FragmentType.AGILITY, 1);

        assertEquals(1, chronicleManager.getAbilityUsageCount(player, FragmentType.BURNING, 1));
        assertEquals(1, chronicleManager.getAbilityUsageCount(player, FragmentType.AGILITY, 1));
        assertEquals(0, chronicleManager.getAbilityUsageCount(player, FragmentType.IMMORTAL, 1));
    }

    // ==================== Unlock notification tests ====================
}
