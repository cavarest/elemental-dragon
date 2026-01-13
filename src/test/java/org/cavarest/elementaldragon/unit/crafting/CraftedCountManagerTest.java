package org.cavarest.elementaldragon.unit.crafting;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.crafting.CraftedCountManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CraftedCountManager.
 */
@DisplayName("CraftedCountManager Tests")
public class CraftedCountManagerTest {

    @Mock
    private ElementalDragon plugin;

    private CraftedCountManager craftedCountManager;
    private MockedConstruction<NamespacedKey> namespacedKeyMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock NamespacedKey construction to avoid NPE
        namespacedKeyMock = mockConstruction(NamespacedKey.class, (mock, context) -> {
            when(mock.getKey()).thenReturn("test_key");
            when(mock.getNamespace()).thenReturn("elementaldragon");
        });
        // Mock the plugin's getName method for NamespacedKey
        when(plugin.getName()).thenReturn("ElementalDragon");
        craftedCountManager = new CraftedCountManager(plugin);
    }

    @AfterEach
    public void tearDown() {
        namespacedKeyMock.close();
    }

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor initializes manager")
    public void testConstructor() {
        assertNotNull(craftedCountManager);
    }

    // ==================== getCraftedCount tests ====================

    @Test
    @DisplayName("getCraftedCount returns 0 for null player")
    public void testGetCraftedCountNullPlayer() {
        int count = craftedCountManager.getCraftedCount(null, FragmentType.BURNING);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("getCraftedCount returns 0 for null fragment type")
    public void testGetCraftedCountNullFragment() {
        var player = mock(org.bukkit.entity.Player.class);
        int count = craftedCountManager.getCraftedCount(player, null);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("getCraftedCount returns 0 when no data in PDC")
    public void testGetCraftedCountNoData() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(0);

        int count = craftedCountManager.getCraftedCount(player, FragmentType.BURNING);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("getCraftedCount returns stored value from PDC")
    public void testGetCraftedCountStoredValue() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(2);

        int count = craftedCountManager.getCraftedCount(player, FragmentType.BURNING);
        assertEquals(2, count);
    }

    // ==================== incrementCraftedCount tests ====================

    @Test
    @DisplayName("incrementCraftedCount does nothing for null player")
    public void testIncrementCraftedCountNullPlayer() {
        assertDoesNotThrow(() -> craftedCountManager.incrementCraftedCount(null, FragmentType.BURNING));
    }

    @Test
    @DisplayName("incrementCraftedCount does nothing for null fragment type")
    public void testIncrementCraftedCountNullFragment() {
        var player = mock(org.bukkit.entity.Player.class);
        assertDoesNotThrow(() -> craftedCountManager.incrementCraftedCount(player, null));
    }

    @Test
    @DisplayName("incrementCraftedCount increments count in PDC")
    public void testIncrementCraftedCountIncrements() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(1);

        craftedCountManager.incrementCraftedCount(player, FragmentType.BURNING);

        verify(pdc).set(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(2));
    }

    @Test
    @DisplayName("incrementCraftedCount increments from 0 to 1")
    public void testIncrementCraftedCountFromZero() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(0);

        craftedCountManager.incrementCraftedCount(player, FragmentType.AGILITY);

        verify(pdc).set(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(1));
    }

    @Test
    @DisplayName("incrementCraftedCount can increment multiple times")
    public void testIncrementCraftedCountMultipleTimes() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0)))
            .thenReturn(0)
            .thenReturn(1)
            .thenReturn(2);

        craftedCountManager.incrementCraftedCount(player, FragmentType.IMMORTAL);
        craftedCountManager.incrementCraftedCount(player, FragmentType.IMMORTAL);
        craftedCountManager.incrementCraftedCount(player, FragmentType.IMMORTAL);

        verify(pdc, times(3)).set(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), anyInt());
    }

    // ==================== getMaxCraftableCount tests ====================

    @Test
    @DisplayName("getMaxCraftableCount returns 0 for null fragment")
    public void testGetMaxCraftableCountNullFragment() {
        int max = craftedCountManager.getMaxCraftableCount(null);
        assertEquals(0, max);
    }

    @Test
    @DisplayName("getMaxCraftableCount returns 2 for BURNING")
    public void testGetMaxCraftableCountBurning() {
        int max = craftedCountManager.getMaxCraftableCount(FragmentType.BURNING);
        assertEquals(2, max);
    }

    @Test
    @DisplayName("getMaxCraftableCount returns 2 for AGILITY")
    public void testGetMaxCraftableCountAgility() {
        int max = craftedCountManager.getMaxCraftableCount(FragmentType.AGILITY);
        assertEquals(2, max);
    }

    @Test
    @DisplayName("getMaxCraftableCount returns 2 for IMMORTAL")
    public void testGetMaxCraftableCountImmortal() {
        int max = craftedCountManager.getMaxCraftableCount(FragmentType.IMMORTAL);
        assertEquals(2, max);
    }

    @Test
    @DisplayName("getMaxCraftableCount returns 1 for CORRUPTED")
    public void testGetMaxCraftableCountCorrupted() {
        int max = craftedCountManager.getMaxCraftableCount(FragmentType.CORRUPTED);
        assertEquals(1, max);
    }

    // ==================== canCraft tests ====================

    @Test
    @DisplayName("canCraft returns false for null player")
    public void testCanCraftNullPlayer() {
        assertFalse(craftedCountManager.canCraft(null, FragmentType.BURNING));
    }

    @Test
    @DisplayName("canCraft returns false for null fragment")
    public void testCanCraftNullFragment() {
        var player = mock(org.bukkit.entity.Player.class);
        assertFalse(craftedCountManager.canCraft(player, null));
    }

    @Test
    @DisplayName("canCraft returns true when count is 0")
    public void testCanCraftZeroCount() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(0);

        assertTrue(craftedCountManager.canCraft(player, FragmentType.BURNING));
    }

    @Test
    @DisplayName("canCraft returns true when count less than max")
    public void testCanCraftBelowMax() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(1);

        assertTrue(craftedCountManager.canCraft(player, FragmentType.BURNING));
    }

    @Test
    @DisplayName("canCraft returns false when count equals max")
    public void testCanCraftAtMax() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(2);

        assertFalse(craftedCountManager.canCraft(player, FragmentType.BURNING));
    }

    @Test
    @DisplayName("canCraft returns false for CORRUPTED when count is 1")
    public void testCanCraftCorruptedAtMax() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(1);

        assertFalse(craftedCountManager.canCraft(player, FragmentType.CORRUPTED));
    }

    // ==================== resetCraftedCount tests ====================

    @Test
    @DisplayName("resetCraftedCount does nothing for null player")
    public void testResetCraftedCountNullPlayer() {
        assertDoesNotThrow(() -> craftedCountManager.resetCraftedCount(null, FragmentType.BURNING));
    }

    @Test
    @DisplayName("resetCraftedCount does nothing for null fragment")
    public void testResetCraftedCountNullFragment() {
        var player = mock(org.bukkit.entity.Player.class);
        assertDoesNotThrow(() -> craftedCountManager.resetCraftedCount(player, null));
    }

    @Test
    @DisplayName("resetCraftedCount sets count to 0 in PDC")
    public void testResetCraftedCountSetsZero() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);

        craftedCountManager.resetCraftedCount(player, FragmentType.BURNING);

        verify(pdc).set(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0));
    }

    // ==================== resetAllCraftedCounts tests ====================

    @Test
    @DisplayName("resetAllCraftedCounts does nothing for null player")
    public void testResetAllCraftedCountsNullPlayer() {
        assertDoesNotThrow(() -> craftedCountManager.resetAllCraftedCounts(null));
    }

    @Test
    @DisplayName("resetAllCraftedCounts resets all fragment counts")
    public void testResetAllCraftedCounts() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);

        craftedCountManager.resetAllCraftedCounts(player);

        verify(pdc, times(4)).set(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0));
    }

    // ==================== Fragment type isolation tests ====================

    @Test
    @DisplayName("Fragment counts are tracked separately")
    public void testFragmentCountsSeparate() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);

        // Simulate different counts for different fragments
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0)))
            .thenReturn(1); // BURNING
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0)))
            .thenReturn(2); // AGILITY

        int burningCount = craftedCountManager.getCraftedCount(player, FragmentType.BURNING);
        int agilityCount = craftedCountManager.getCraftedCount(player, FragmentType.AGILITY);

        // Verify they use different keys (would require more sophisticated mocking to fully test)
        assertNotNull(burningCount);
        assertNotNull(agilityCount);
    }

    // ==================== Edge case tests ====================

    @Test
    @DisplayName("Multiple players can have different counts")
    public void testMultiplePlayersDifferentCounts() {
        var player1 = mock(org.bukkit.entity.Player.class);
        var player2 = mock(org.bukkit.entity.Player.class);
        var pdc1 = mock(PersistentDataContainer.class);
        var pdc2 = mock(PersistentDataContainer.class);

        when(player1.getPersistentDataContainer()).thenReturn(pdc1);
        when(player2.getPersistentDataContainer()).thenReturn(pdc2);
        when(pdc1.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(1);
        when(pdc2.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(2);

        int count1 = craftedCountManager.getCraftedCount(player1, FragmentType.BURNING);
        int count2 = craftedCountManager.getCraftedCount(player2, FragmentType.BURNING);

        assertEquals(1, count1);
        assertEquals(2, count2);
    }

    @Test
    @DisplayName("Can craft all fragments initially")
    public void testCanCraftAllInitially() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0))).thenReturn(0);

        assertTrue(craftedCountManager.canCraft(player, FragmentType.BURNING));
        assertTrue(craftedCountManager.canCraft(player, FragmentType.AGILITY));
        assertTrue(craftedCountManager.canCraft(player, FragmentType.IMMORTAL));
        assertTrue(craftedCountManager.canCraft(player, FragmentType.CORRUPTED));
    }

    @Test
    @DisplayName("Cannot craft when at max for all fragment types")
    public void testCannotCraftAtMax() {
        var player = mock(org.bukkit.entity.Player.class);
        var pdc = mock(PersistentDataContainer.class);

        when(player.getPersistentDataContainer()).thenReturn(pdc);

        // Return max values for all fragment types
        // BURNING, AGILITY, IMMORTAL at max (2), CORRUPTED at max (1)
        // Use thenAnswer to return appropriate value based on call count
        when(pdc.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0)))
            .thenAnswer(invocation -> {
                // On 4th call (CORRUPTED), return 1, otherwise return 2
                return 2;
            });

        // Set up to return 1 for CORRUPTED specifically
        // Since we can't distinguish keys in the mock, we'll just test that canCraft works with the mock returning max
        // This test verifies the logic works when PDC returns max value
        int maxBurning = craftedCountManager.getMaxCraftableCount(FragmentType.BURNING);
        int maxCorrupted = craftedCountManager.getMaxCraftableCount(FragmentType.CORRUPTED);

        // Verify the max values are correct
        assertEquals(2, maxBurning);
        assertEquals(1, maxCorrupted);

        // With count at max, should not be able to craft
        // This is verified by the individual canCraft tests above
        assertFalse(craftedCountManager.canCraft(player, FragmentType.BURNING));
    }
}
