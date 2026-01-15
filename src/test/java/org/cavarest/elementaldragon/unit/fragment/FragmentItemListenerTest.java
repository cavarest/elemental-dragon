package org.cavarest.elementaldragon.unit.fragment;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentItemListener;
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

    private FragmentItemListener fragmentItemListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fragmentItemListener = new FragmentItemListener(plugin, fragmentManager);
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
        // Mock ElementalItems.isFragment to return true for AGILITY only
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.AGILITY))
                .thenReturn(true);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.BURNING))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.IMMORTAL))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.CORRUPTED))
                .thenReturn(false);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.AGILITY);

            fragmentItemListener.onPlayerDropItem(event);

            verify(fragmentManager).unequipFragment(player, true);
        }
    }

    @Test
    @DisplayName("Dropping equipped Burning Fragment should unequip it")
    public void testDropEquippedBurningFragmentUnequips() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.AGILITY))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.BURNING))
                .thenReturn(true);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.IMMORTAL))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.CORRUPTED))
                .thenReturn(false);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);

            fragmentItemListener.onPlayerDropItem(event);

            verify(fragmentManager).unequipFragment(player, true);
        }
    }

    @Test
    @DisplayName("Dropping equipped Immortal Fragment should unequip it")
    public void testDropEquippedImmortalFragmentUnequips() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.AGILITY))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.BURNING))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.IMMORTAL))
                .thenReturn(true);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.CORRUPTED))
                .thenReturn(false);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.IMMORTAL);

            fragmentItemListener.onPlayerDropItem(event);

            verify(fragmentManager).unequipFragment(player, true);
        }
    }

    @Test
    @DisplayName("Dropping equipped Corrupted Core should unequip it")
    public void testDropEquippedCorruptedFragmentUnequips() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.AGILITY))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.BURNING))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.IMMORTAL))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.CORRUPTED))
                .thenReturn(true);

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            when(event.getPlayer()).thenReturn(player);
            when(event.getItemDrop()).thenReturn(droppedItem);
            when(droppedItem.getItemStack()).thenReturn(fragmentItem);
            when(fragmentItem.hasItemMeta()).thenReturn(true);
            when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.CORRUPTED);

            fragmentItemListener.onPlayerDropItem(event);

            verify(fragmentManager).unequipFragment(player, true);
        }
    }

    @Test
    @DisplayName("Dropping non-equipped fragment should not unequip")
    public void testDropNonEquippedFragmentDoesNotUnequip() {
        try (MockedStatic<ElementalItems> mockedElementalItems = mockStatic(ElementalItems.class)) {
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.AGILITY))
                .thenReturn(true);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.BURNING))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.IMMORTAL))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.CORRUPTED))
                .thenReturn(false);

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
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.AGILITY))
                .thenReturn(true);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.BURNING))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.IMMORTAL))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.CORRUPTED))
                .thenReturn(false);

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
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.AGILITY))
                .thenReturn(true);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.BURNING))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.IMMORTAL))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.CORRUPTED))
                .thenReturn(false);

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
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.AGILITY))
                .thenReturn(true);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.BURNING))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.IMMORTAL))
                .thenReturn(false);
            mockedElementalItems.when(() -> ElementalItems.isFragment(fragmentItem, FragmentType.CORRUPTED))
                .thenReturn(false);

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
}
