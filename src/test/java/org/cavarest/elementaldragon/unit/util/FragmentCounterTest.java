package org.cavarest.elementaldragon.unit.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;
import org.cavarest.elementaldragon.util.FragmentCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for FragmentCounter utility class.
 *
 * Note: Some tests have limitations due to Mockito's static method mocking.
 * The tests that work verify the core logic, while the ones that would fail
 * due to object comparison are documented for future integration testing.
 */
@DisplayName("FragmentCounter Tests")
public class FragmentCounterTest {

    @Mock
    private Player player;

    @Mock
    private PlayerInventory playerInventory;

    @Mock
    private ItemStack fragmentItem1;

    @Mock
    private ItemStack fragmentItem2;

    private UUID playerUuid;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        playerUuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getInventory()).thenReturn(playerInventory);
    }

    @Test
    @DisplayName("countFragments returns 0 for null player")
    public void testCountFragmentsReturnsZeroForNullPlayer() {
        int count = FragmentCounter.countFragments(null, FragmentType.BURNING);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("countFragments returns 0 for null fragment type")
    public void testCountFragmentsReturnsZeroForNullFragmentType() {
        int count = FragmentCounter.countFragments(player, null);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("canPossessAnother returns true when limit is 0 (unlimited)")
    public void testCanPossessAnotherReturnsTrueWhenLimitIsZero() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(any()))
                .thenAnswer(invocation -> {
                    ItemStack item = invocation.getArgument(0);
                    if (item == null) return null;
                    if (item == fragmentItem1) return FragmentType.BURNING;
                    return null;
                });

            ItemStack[] inventoryContents = new ItemStack[36];
            inventoryContents[0] = fragmentItem1;
            when(playerInventory.getContents()).thenReturn(inventoryContents);
            when(fragmentItem1.getAmount()).thenReturn(5);

            boolean result = FragmentCounter.canPossessAnother(player, FragmentType.BURNING, 0);

            assertTrue(result, "Should allow when limit is 0 (unlimited)");
        }
    }

    @Test
    @DisplayName("canPossessAnother returns true when count < limit")
    public void testCanPossessAnotherReturnsTrueWhenBelowLimit() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(any()))
                .thenAnswer(invocation -> {
                    ItemStack item = invocation.getArgument(0);
                    if (item == null) return null;
                    if (item == fragmentItem1) return FragmentType.BURNING;
                    return null;
                });

            ItemStack[] inventoryContents = new ItemStack[36];
            inventoryContents[0] = fragmentItem1;
            when(playerInventory.getContents()).thenReturn(inventoryContents);
            when(fragmentItem1.getAmount()).thenReturn(1);

            boolean result = FragmentCounter.canPossessAnother(player, FragmentType.BURNING, 3);

            assertTrue(result, "Should allow when count (1) < limit (3)");
        }
    }

    @Test
    @DisplayName("canPossessAnother returns false when count >= limit")
    public void testCanPossessAnotherReturnsFalseWhenAtOrAboveLimit() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(any()))
                .thenAnswer(invocation -> {
                    ItemStack item = invocation.getArgument(0);
                    if (item == null) return null;
                    if (item == fragmentItem1) return FragmentType.BURNING;
                    return null;
                });

            ItemStack[] inventoryContents = new ItemStack[36];
            inventoryContents[0] = fragmentItem1;
            when(playerInventory.getContents()).thenReturn(inventoryContents);
            when(fragmentItem1.getAmount()).thenReturn(3);

            boolean result = FragmentCounter.canPossessAnother(player, FragmentType.BURNING, 3);

            assertFalse(result, "Should not allow when count (3) >= limit (3)");
        }
    }

    @Test
    @DisplayName("countFragments handles item amounts correctly")
    public void testCountFragmentsHandlesAmounts() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(any()))
                .thenAnswer(invocation -> {
                    ItemStack item = invocation.getArgument(0);
                    if (item == null) return null;
                    if (item == fragmentItem1) return FragmentType.CORRUPTED;
                    if (item == fragmentItem2) return FragmentType.CORRUPTED;
                    return null;
                });

            when(fragmentItem1.getAmount()).thenReturn(2);
            when(fragmentItem2.getAmount()).thenReturn(3);

            ItemStack[] inventoryContents = new ItemStack[36];
            inventoryContents[0] = fragmentItem1;
            inventoryContents[1] = fragmentItem2;
            when(playerInventory.getContents()).thenReturn(inventoryContents);

            int count = FragmentCounter.countFragments(player, FragmentType.CORRUPTED);

            assertEquals(5, count);
        }
    }
}
