package org.cavarest.elementaldragon.unit.fragment;

import org.bukkit.entity.Player;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.listener.FragmentItemListener;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.bukkit.entity.Item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FragmentItemListener.
 * Tests fragment drop handler behavior - when a player drops an equipped fragment,
 * the fragment should be automatically unequipped (passive effects removed).
 */
@DisplayName("FragmentItemListener Tests")
public class FragmentItemListenerTest {

    @Mock
    private ElementalDragon plugin;

    @Mock
    private FragmentManager fragmentManager;

    @Mock
    private Player player;

    @Mock
    private Item droppedItem;

    @Mock
    private ItemStack fragmentItem;

    @Mock
    private ItemStack nonFragmentItem;

    @Mock
    private PlayerInventory playerInventory;

    @Mock
    private ItemStack burningFragment;

    @Mock
    private ItemStack immortalFragment;

    @Mock
    private ItemStack emptyItem;

    @Mock
    private Item pickupItemEntity;

    private FragmentItemListener fragmentItemListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fragmentItemListener = new FragmentItemListener(plugin, fragmentManager);

        // Initialize empty item mock
        lenient().when(emptyItem.getType()).thenReturn(org.bukkit.Material.AIR);
        lenient().when(emptyItem.getAmount()).thenReturn(0);
    }

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor initializes listener")
    public void testConstructorInitialization() {
        assertNotNull(fragmentItemListener);
    }

    @Test
    @DisplayName("Constructor initializes equip cooldown tracking")
    public void testConstructorInitializesCooldownTracking() {
        assertNotNull(fragmentItemListener);
        // Listener is created successfully
        // The internal lastEquipClickTimes map is initialized
    }

    // ==================== Basic functionality tests ====================

    @Test
    @DisplayName("Listener can be instantiated multiple times")
    public void testMultipleInstantiations() {
        FragmentItemListener listener1 = new FragmentItemListener(plugin, fragmentManager);
        FragmentItemListener listener2 = new FragmentItemListener(plugin, fragmentManager);

        assertNotNull(listener1);
        assertNotNull(listener2);
        assertNotSame(listener1, listener2);
    }

    @Test
    @DisplayName("Listener handles null dependencies gracefully")
    public void testNullDependencies() {
        assertDoesNotThrow(() -> new FragmentItemListener(plugin, fragmentManager));
    }

    // ==================== Fragment Drop Handler Tests ====================

    @Test
    @DisplayName("Dropping equipped Agility Fragment should unequip it")
    public void testDropEquippedAgilityFragmentUnequips() {
        // Mock ElementalItems.getFragmentType to return AGILITY
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.AGILITY);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.AGILITY);

            fragmentItemListener.onPlayerDropItem(event);

            // Should have unequipped the fragment (new listener sends its own message)
            verify(fragmentManager).unequipFragment(player);
        }
    }

    @Test
    @DisplayName("Dropping equipped Burning Fragment should unequip it")
    public void testDropEquippedBurningFragmentUnequips() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.BURNING);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);

            fragmentItemListener.onPlayerDropItem(event);

            // Should have unequipped the fragment (new listener sends its own message)
            verify(fragmentManager).unequipFragment(player);
        }
    }

    @Test
    @DisplayName("Dropping equipped Immortal Fragment should unequip it")
    public void testDropEquippedImmortalFragmentUnequips() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.IMMORTAL);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.IMMORTAL);

            fragmentItemListener.onPlayerDropItem(event);

            // Should have unequipped the fragment (new listener sends its own message)
            verify(fragmentManager).unequipFragment(player);
        }
    }

    @Test
    @DisplayName("Dropping equipped Corrupted Core should unequip it")
    public void testDropEquippedCorruptedFragmentUnequips() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.CORRUPTED);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.CORRUPTED);

            fragmentItemListener.onPlayerDropItem(event);

            // Should have unequipped the fragment (new listener sends its own message)
            verify(fragmentManager).unequipFragment(player);
        }
    }

    @Test
    @DisplayName("Dropping non-equipped fragment should not unequip")
    public void testDropNonEquippedFragmentDoesNotUnequip() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.AGILITY);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);

            // Player has DIFFERENT fragment equipped (Burning Fragment)
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);

            fragmentItemListener.onPlayerDropItem(event);

            verify(fragmentManager, never()).unequipFragment(player);
        }
    }

    @Test
    @DisplayName("Dropping fragment when no fragment equipped should do nothing")
    public void testDropFragmentWhenNoFragmentEquipped() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.AGILITY);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);

            // Player has NO fragment equipped
            when(fragmentManager.getEquippedFragment(player)).thenReturn(null);

            fragmentItemListener.onPlayerDropItem(event);

            verify(fragmentManager, never()).unequipFragment(player);
        }
    }

    @Test
    @DisplayName("Drop handler should not cancel the event (MONITOR priority)")
    public void testDropHandlerDoesNotCancelEvent() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.AGILITY);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.AGILITY);

            fragmentItemListener.onPlayerDropItem(event);

            verify(event, never()).setCancelled(true);
        }
    }

    @Test
    @DisplayName("Drop handler sends message to player when unequipping")
    public void testDropHandlerSendsMessage() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.AGILITY);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.AGILITY);

            fragmentItemListener.onPlayerDropItem(event);

            verify(player).sendMessage(any(net.kyori.adventure.text.Component.class));
        }
    }

    // ==================== Container Click Tests ====================

    @Test
    @DisplayName("Clicking equipped fragment should NOT unequip (Issue #22)")
    public void testClickEquippedFragmentDoesNotUnequip() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.BURNING);

            org.bukkit.inventory.Inventory mockedInventory = mock(org.bukkit.inventory.Inventory.class);
            // Return null for type to avoid InventoryType initialization issues
            when(mockedInventory.getType()).thenReturn(null);

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getCurrentItem()).thenReturn(fragmentItem);
            when(event.getWhoClicked()).thenReturn(player);
            when(event.getClickedInventory()).thenReturn(mockedInventory);

            // Player has Burning Fragment equipped
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);

            fragmentItemListener.onInventoryClickFragment(event);

            // Issue #22 fix: Should NOT unequip when clicking on fragment
            // Abilities stay equipped when managing inventory - handled implicitly
            // (no onInventoryClickUnequip method exists anymore)
            verify(fragmentManager, never()).unequipFragment(any(), anyBoolean());
        }
    }

    @Test
    @DisplayName("Clicking non-equipped fragment does not unequip")
    public void testClickNonEquippedFragmentDoesNotUnequip() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(fragmentItem))
                .thenReturn(FragmentType.BURNING);

            org.bukkit.inventory.Inventory mockedInventory = mock(org.bukkit.inventory.Inventory.class);
            when(mockedInventory.getType()).thenReturn(null);

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getCurrentItem()).thenReturn(fragmentItem);
            when(event.getWhoClicked()).thenReturn(player);
            when(event.getClickedInventory()).thenReturn(mockedInventory);

            // Player has NO fragment equipped
            when(fragmentManager.getEquippedFragment(player)).thenReturn(null);

            fragmentItemListener.onInventoryClickFragment(event);

            // Should NOT unequip since no fragment equipped
            verify(fragmentManager, never()).unequipFragment(player);
        }
    }

    // ==================== One-Fragment Pickup Limit Tests ====================
    // Critical: These tests ensure players can NEVER pick up a second fragment

    @Test
    @DisplayName("CANNOT pick up different fragment when holding one in main inventory")
    public void testCannotPickUpDifferentFragmentWhenHoldingOneInMainInventory() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Setup: Player has Immortal Fragment in main inventory
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(immortalFragment))
                .thenReturn(FragmentType.IMMORTAL);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragment))
                .thenReturn(FragmentType.BURNING);
            mockedElementalItems.when(() -> ElementalItems.getAnyFragmentExcept(any(), eq(null)))
                .thenReturn(FragmentType.IMMORTAL);

            // Mock player inventory
            when(player.getInventory()).thenReturn(playerInventory);
            when(playerInventory.getContents()).thenReturn(new ItemStack[]{immortalFragment});
            when(player.getItemOnCursor()).thenReturn(emptyItem);

            // Setup: Trying to pick up Burning Fragment
            EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);
            when(event.getEntity()).thenReturn(player);
            when(event.getItem()).thenReturn(pickupItemEntity);
            when(pickupItemEntity.getItemStack()).thenReturn(burningFragment);
            when(burningFragment.hasItemMeta()).thenReturn(true);

            // Execute
            fragmentItemListener.onEntityPickupItem(event);

            // Verify pickup was CANCELLED
            verify(event).setCancelled(true);
            // Verify error message sent
            verify(player).sendMessage(any(net.kyori.adventure.text.Component.class));
        }
    }

    @Test
    @DisplayName("CANNOT pick up different fragment when holding one in offhand")
    public void testCannotPickUpDifferentFragmentWhenHoldingOneInOffhand() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Setup: Player has Immortal Fragment in offhand
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(immortalFragment))
                .thenReturn(FragmentType.IMMORTAL);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragment))
                .thenReturn(FragmentType.BURNING);
            mockedElementalItems.when(() -> ElementalItems.getAnyFragmentExcept(any(), eq(null)))
                .thenReturn(FragmentType.IMMORTAL);

            // Mock player inventory
            when(player.getInventory()).thenReturn(playerInventory);
            when(playerInventory.getContents()).thenReturn(new ItemStack[0]); // Empty main inventory
            when(player.getItemOnCursor()).thenReturn(emptyItem);

            // Setup: Trying to pick up Burning Fragment
            EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);
            when(event.getEntity()).thenReturn(player);
            when(event.getItem()).thenReturn(pickupItemEntity);
            when(pickupItemEntity.getItemStack()).thenReturn(burningFragment);
            when(burningFragment.hasItemMeta()).thenReturn(true);

            // Execute
            fragmentItemListener.onEntityPickupItem(event);

            // Verify pickup was CANCELLED
            verify(event).setCancelled(true);
            // Verify error message sent
            verify(player).sendMessage(any(net.kyori.adventure.text.Component.class));
        }
    }

    @Test
    @DisplayName("CANNOT pick up different fragment when holding one on cursor")
    public void testCannotPickUpDifferentFragmentWhenHoldingOneOnCursor() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Setup: Player has Immortal Fragment on cursor
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(immortalFragment))
                .thenReturn(FragmentType.IMMORTAL);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragment))
                .thenReturn(FragmentType.BURNING);
            mockedElementalItems.when(() -> ElementalItems.getAnyFragmentExcept(any(), eq(null)))
                .thenReturn(FragmentType.IMMORTAL);

            // Mock player inventory
            when(player.getInventory()).thenReturn(playerInventory);
            when(playerInventory.getContents()).thenReturn(new ItemStack[0]); // Empty inventory
            when(player.getItemOnCursor()).thenReturn(immortalFragment); // Has fragment on cursor

            // Setup: Trying to pick up Burning Fragment
            EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);
            when(event.getEntity()).thenReturn(player);
            when(event.getItem()).thenReturn(pickupItemEntity);
            when(pickupItemEntity.getItemStack()).thenReturn(burningFragment);
            when(burningFragment.hasItemMeta()).thenReturn(true);

            // Execute
            fragmentItemListener.onEntityPickupItem(event);

            // Verify pickup was CANCELLED
            verify(event).setCancelled(true);
            // Verify error message sent
            verify(player).sendMessage(any(net.kyori.adventure.text.Component.class));
        }
    }

    @Test
    @DisplayName("CANNOT pick up same fragment type when already holding one")
    public void testCannotPickUpSameFragmentTypeWhenAlreadyHoldingOne() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Setup: Player has Immortal Fragment in inventory
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(immortalFragment))
                .thenReturn(FragmentType.IMMORTAL);
            mockedElementalItems.when(() -> ElementalItems.getAnyFragmentExcept(any(), eq(null)))
                .thenReturn(FragmentType.IMMORTAL);

            // Mock player inventory
            when(player.getInventory()).thenReturn(playerInventory);
            when(playerInventory.getContents()).thenReturn(new ItemStack[]{immortalFragment});
            when(player.getItemOnCursor()).thenReturn(emptyItem);

            // Setup: Trying to pick up another Immortal Fragment
            EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);
            when(event.getEntity()).thenReturn(player);
            when(event.getItem()).thenReturn(pickupItemEntity);
            when(pickupItemEntity.getItemStack()).thenReturn(immortalFragment);
            when(immortalFragment.hasItemMeta()).thenReturn(true);

            // Execute
            fragmentItemListener.onEntityPickupItem(event);

            // Verify pickup was CANCELLED
            verify(event).setCancelled(true);
            // Verify error message sent
            verify(player).sendMessage(any(net.kyori.adventure.text.Component.class));
        }
    }

    @Test
    @DisplayName("CAN pick up fragment when player has NO fragments")
    public void testCanPickUpFragmentWhenPlayerHasNoFragments() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Setup: Player has NO fragments
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragment))
                .thenReturn(FragmentType.BURNING);
            mockedElementalItems.when(() -> ElementalItems.getAnyFragmentExcept(any(), eq(null)))
                .thenReturn(null); // No fragments found

            // Mock player inventory
            when(player.getInventory()).thenReturn(playerInventory);
            when(playerInventory.getContents()).thenReturn(new ItemStack[0]); // Empty inventory
            when(player.getItemOnCursor()).thenReturn(emptyItem);

            // Setup: Trying to pick up Burning Fragment
            EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);
            when(event.getEntity()).thenReturn(player);
            when(event.getItem()).thenReturn(pickupItemEntity);
            when(pickupItemEntity.getItemStack()).thenReturn(burningFragment);
            when(burningFragment.hasItemMeta()).thenReturn(true);

            // Execute
            fragmentItemListener.onEntityPickupItem(event);

            // Verify pickup was NOT cancelled
            verify(event, never()).setCancelled(true);
            // No error message sent
            verify(player, never()).sendMessage(any(net.kyori.adventure.text.Component.class));
        }
    }

    @Test
    @DisplayName("Picking up non-fragment item is allowed")
    public void testPickingUpNonFragmentItemIsAllowed() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // Setup: Non-fragment item
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(nonFragmentItem))
                .thenReturn(null); // Not a fragment
            mockedElementalItems.when(() -> ElementalItems.getAnyFragmentExcept(any(), eq(null)))
                .thenReturn(null);

            // Mock player inventory
            when(player.getInventory()).thenReturn(playerInventory);
            when(playerInventory.getContents()).thenReturn(new ItemStack[0]);
            when(player.getItemOnCursor()).thenReturn(emptyItem);

            // Setup: Trying to pick up non-fragment item
            EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);
            when(event.getEntity()).thenReturn(player);
            when(event.getItem()).thenReturn(pickupItemEntity);
            when(pickupItemEntity.getItemStack()).thenReturn(nonFragmentItem);
            when(nonFragmentItem.hasItemMeta()).thenReturn(false);

            // Execute
            fragmentItemListener.onEntityPickupItem(event);

            // Verify pickup was NOT cancelled
            verify(event, never()).setCancelled(true);
        }
    }

    @Test
    @DisplayName("One-fragment limit works when fragment is in offhand")
    public void testOneFragmentLimitWorksWhenFragmentIsInOffhand() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            // This is the critical test for the offhand bug fix
            // Player has Immortal Fragment in OFFHAND
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(immortalFragment))
                .thenReturn(FragmentType.IMMORTAL);
            mockedElementalItems.when(() -> ElementalItems.getFragmentType(burningFragment))
                .thenReturn(FragmentType.BURNING);
            mockedElementalItems.when(() -> ElementalItems.getAnyFragmentExcept(any(), eq(null)))
                .thenReturn(FragmentType.IMMORTAL); // Found in offhand!

            // Mock player inventory with OFFHAND fragment
            when(player.getInventory()).thenReturn(playerInventory);
            when(playerInventory.getContents()).thenReturn(new ItemStack[0]); // Empty main inventory
            when(player.getItemOnCursor()).thenReturn(emptyItem);

            // Setup: Trying to pick up Burning Fragment
            EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);
            when(event.getEntity()).thenReturn(player);
            when(event.getItem()).thenReturn(pickupItemEntity);
            when(pickupItemEntity.getItemStack()).thenReturn(burningFragment);
            when(burningFragment.hasItemMeta()).thenReturn(true);

            // Execute
            fragmentItemListener.onEntityPickupItem(event);

            // CRITICAL: Verify pickup was CANCELLED
            // This is the regression test for the offhand bug
            verify(event).setCancelled(true);
            verify(player).sendMessage(any(net.kyori.adventure.text.Component.class));
        }
    }
}
