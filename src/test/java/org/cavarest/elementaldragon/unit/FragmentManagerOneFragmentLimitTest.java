package org.cavarest.elementaldragon.unit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Tests for FragmentManager one-fragment limit enforcement.
 * Tests that players cannot equip a fragment when they have a different fragment in inventory.
 */
@DisplayName("FragmentManager One-Fragment Limit Tests")
public class FragmentManagerOneFragmentLimitTest {

    @Mock
    private ElementalDragon plugin;

    @Mock
    private CooldownManager cooldownManager;

    @Mock
    private Player player;

    @Mock
    private PlayerInventory playerInventory;

    @Mock
    private ItemStack burningFragmentItem;

    @Mock
    private ItemStack corruptedCoreItem;

    @Mock
    private ItemStack nonFragmentItem;

    private FragmentManager fragmentManager;
    private UUID playerUuid;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        playerUuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getInventory()).thenReturn(playerInventory);
        fragmentManager = new FragmentManager(plugin, cooldownManager);
    }

    @Test
    @DisplayName("Equipping fragment when player has different fragment in inventory should fail")
    public void testEquipFragmentWhenPlayerHasDifferentFragmentInInventory() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Mock: Player has Burning Fragment in inventory
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragmentItem))
                .thenReturn(FragmentType.BURNING);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(corruptedCoreItem))
                .thenReturn(null); // Player doesn't have Corrupted Core
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(nonFragmentItem))
                .thenReturn(null);

            // Mock: getAnyFragmentExcept returns Burning Fragment when checking inventory
            // This simulates the inventory check that prevents having multiple fragments
            mockedElementalItems.when(() -> ElementalItems.getAnyFragmentExcept(eq(player), eq(FragmentType.CORRUPTED)))
                .thenReturn(FragmentType.BURNING);

            // Set up inventory to contain Burning Fragment
            ItemStack[] inventoryContents = new ItemStack[36];
            inventoryContents[0] = burningFragmentItem;
            when(playerInventory.getContents()).thenReturn(inventoryContents);

            // Give admin permission to auto-give items
            when(player.hasPermission("elementaldragon.fragment.admin")).thenReturn(true);

            // Try to equip Corrupted Core while having Burning Fragment in inventory
            boolean result = fragmentManager.equipFragment(player, FragmentType.CORRUPTED);

            // Should fail because player already has Burning Fragment in inventory
            assertFalse(result, "Should not equip Corrupted Core when player has Burning Fragment in inventory");
        }
    }

    @Test
    @DisplayName("Equipping fragment when player has same fragment type in inventory should succeed")
    public void testEquipFragmentWhenPlayerHasSameFragmentTypeInInventory() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Mock: Player has Corrupted Core in inventory
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(corruptedCoreItem))
                .thenReturn(FragmentType.CORRUPTED);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragmentItem))
                .thenReturn(null);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(nonFragmentItem))
                .thenReturn(null);

            // Set up inventory to contain Corrupted Core
            ItemStack[] inventoryContents = new ItemStack[36];
            inventoryContents[0] = corruptedCoreItem;
            when(playerInventory.getContents()).thenReturn(inventoryContents);

            // Give admin permission to auto-give items
            when(player.hasPermission("elementaldragon.fragment.admin")).thenReturn(true);

            // Try to equip Corrupted Core while having Corrupted Core in inventory
            boolean result = fragmentManager.equipFragment(player, FragmentType.CORRUPTED);

            // Should succeed because it's the same fragment type
            assertTrue(result, "Should equip Corrupted Core when player already has Corrupted Core in inventory");
        }
    }

    @Test
    @DisplayName("Equipping fragment when player has no fragments in inventory should succeed")
    public void testEquipFragmentWhenPlayerHasNoFragmentsInInventory() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Mock: No fragments in inventory
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragmentItem))
                .thenReturn(null);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(corruptedCoreItem))
                .thenReturn(null);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(nonFragmentItem))
                .thenReturn(null);

            // Set up empty inventory (or with non-fragment items)
            ItemStack[] inventoryContents = new ItemStack[36];
            inventoryContents[0] = nonFragmentItem; // Regular item
            when(playerInventory.getContents()).thenReturn(inventoryContents);

            // Give admin permission to auto-give items
            when(player.hasPermission("elementaldragon.fragment.admin")).thenReturn(true);

            // Try to equip Corrupted Core with empty inventory
            boolean result = fragmentManager.equipFragment(player, FragmentType.CORRUPTED);

            // Should succeed because no other fragments in inventory
            assertTrue(result, "Should equip Corrupted Core when player has no other fragments in inventory");
        }
    }

    @Test
    @DisplayName("Equipping fragment when different fragment is already equipped should fail")
    public void testEquipFragmentWhenDifferentFragmentEquipped() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Mock: No fragments in inventory
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragmentItem))
                .thenReturn(null);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(corruptedCoreItem))
                .thenReturn(null);

            // Set up empty inventory
            ItemStack[] inventoryContents = new ItemStack[36];
            when(playerInventory.getContents()).thenReturn(inventoryContents);

            // Give admin permission to auto-give items
            when(player.hasPermission("elementaldragon.fragment.admin")).thenReturn(true);

            // First equip Burning Fragment
            boolean firstEquip = fragmentManager.equipFragment(player, FragmentType.BURNING);
            assertTrue(firstEquip, "First equip should succeed");

            // Try to equip Corrupted Core while Burning Fragment is equipped
            boolean secondEquip = fragmentManager.equipFragment(player, FragmentType.CORRUPTED);

            // Should fail because different fragment is already equipped
            assertFalse(secondEquip, "Should not equip Corrupted Core when Burning Fragment is already equipped");
        }
    }

    @Test
    @DisplayName("Re-equipping same fragment type should succeed")
    public void testReequipSameFragmentType() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Mock: Player has Corrupted Core in inventory
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(corruptedCoreItem))
                .thenReturn(FragmentType.CORRUPTED);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragmentItem))
                .thenReturn(null);

            // Set up inventory to contain Corrupted Core
            ItemStack[] inventoryContents = new ItemStack[36];
            inventoryContents[0] = corruptedCoreItem;
            when(playerInventory.getContents()).thenReturn(inventoryContents);

            // Give admin permission to auto-give items
            when(player.hasPermission("elementaldragon.fragment.admin")).thenReturn(true);

            // First equip Corrupted Core
            boolean firstEquip = fragmentManager.equipFragment(player, FragmentType.CORRUPTED);
            assertTrue(firstEquip, "First equip should succeed");

            // Re-equip same fragment type
            boolean secondEquip = fragmentManager.equipFragment(player, FragmentType.CORRUPTED);

            // Should succeed - re-equipping same fragment is allowed (no-op)
            assertTrue(secondEquip, "Re-equipping same fragment type should succeed");
        }
    }
}
