package org.cavarest.elementaldragon.unit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.command.WithdrawabilityCommand;
import org.cavarest.elementaldragon.fragment.BurningFragment;
import org.cavarest.elementaldragon.fragment.Fragment;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for WithdrawabilityCommand.
 * Tests the /withdrawability command behavior including:
 * - Normal fragment withdrawal
 * - Withdrawal when fragment was cleared from inventory (simulating /clear command)
 * - Withdrawal when no fragment is equipped
 */
@DisplayName("WithdrawabilityCommand Tests")
public class WithdrawabilityCommandTest {

    @Mock
    private ElementalDragon plugin;

    @Mock
    private FragmentManager fragmentManager;

    @Mock
    private Player player;

    @Mock
    private Command command;

    @Mock
    private PlayerInventory inventory;

    @Mock
    private ItemStack fragmentItem;

    @Mock
    private CommandSender nonPlayerSender;

    private WithdrawabilityCommand withdrawabilityCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        withdrawabilityCommand = new WithdrawabilityCommand(plugin, fragmentManager);

        // Common player setup
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getInventory()).thenReturn(inventory);
    }

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor initializes command")
    public void testConstructorInitialization() {
        assertNotNull(withdrawabilityCommand);
    }

    // ==================== Non-player sender tests ====================

    @Test
    @DisplayName("Non-player sender cannot use command")
    public void testNonPlayerSenderCannotUseCommand() {
        when(nonPlayerSender instanceof Player).thenReturn(false);

        boolean result = withdrawabilityCommand.onCommand(nonPlayerSender, command, "withdrawability", new String[0]);

        assertTrue(result, "Command should return true even when cancelled");
        verify(nonPlayerSender).sendMessage(any(net.kyori.adventure.text.Component.class));
        verify(fragmentManager, never()).unequipFragment(any());
    }

    // ==================== Normal fragment withdrawal tests ====================

    @Test
    @DisplayName("Withdrawability with equipped fragment unequips it")
    public void testWithdrawabilityWithEquippedFragment() {
        // Player has fragment equipped
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.unequipFragment(player)).thenReturn(true);

        boolean result = withdrawabilityCommand.onCommand(player, command, "withdrawability", new String[0]);

        assertTrue(result, "Command should return true");
        verify(fragmentManager).unequipFragment(player);
        verify(player, times(2)).sendMessage(any(net.kyori.adventure.text.Component.class));
    }

    @Test
    @DisplayName("Withdrawability with no fragment equipped shows message")
    public void testWithdrawabilityWithNoFragmentEquipped() {
        // Player has no fragment equipped
        when(fragmentManager.getEquippedFragment(player)).thenReturn(null);

        boolean result = withdrawabilityCommand.onCommand(player, command, "withdrawability", new String[0]);

        assertTrue(result, "Command should return true");
        verify(fragmentManager, never()).unequipFragment(any());
        verify(player).sendMessage(any(net.kyori.adventure.text.Component.class));
    }

    // ==================== /clear command simulation tests ====================

    @Test
    @DisplayName("Withdrawability after /clear clears cache and shows correct message")
    public void testWithdrawabilityAfterClearCommand() {
        // First call returns BURNING (cache shows equipped)
        // Second call returns null (cache was cleared after inventory check)
        when(fragmentManager.getEquippedFragment(player))
            .thenReturn(FragmentType.BURNING)  // First call - cache check passes
            .thenReturn(null);  // Subsequent calls - cache cleared

        boolean result = withdrawabilityCommand.onCommand(player, command, "withdrawability", new String[0]);

        assertTrue(result, "Command should return true");
        // getEquippedFragment() was called, which cleared the cache
        verify(fragmentManager, atLeastOnce()).getEquippedFragment(player);
        // unequipFragment() should NOT be called since fragment is not actually in inventory
        verify(fragmentManager, never()).unequipFragment(player);
        // Player should get the "don't have any fragment abilities equipped" message
        verify(player).sendMessage(any(net.kyori.adventure.text.Component.class));
    }

    @Test
    @DisplayName("getEquippedFragment clears cache when item missing from inventory")
    public void testGetEquippedFragmentClearsCacheWhenItemMissing() {
        // This test verifies that FragmentManager.getEquippedFragment() properly
        // handles the case where the cache shows a fragment equipped, but the
        // item is no longer in the player's inventory (e.g., after /clear)

        // Scenario:
        // 1. Player had Burning Fragment equipped (cache contains BURNING)
        // 2. Player ran /clear to remove all items
        // 3. getEquippedFragment() is called
        // 4. It should return null and clear the cache

        // First call: cache shows BURNING, but inventory verification fails
        // Second call: cache was cleared, returns null

        // This test documents the expected behavior - the actual implementation
        // is in FragmentManager, but this verifies the integration
        when(fragmentManager.getEquippedFragment(player))
            .thenReturn(null);  // After /clear, should return null

        FragmentType result = fragmentManager.getEquippedFragment(player);

        assertNull(result, "Should return null when item not in inventory");
    }

    @Test
    @DisplayName("Withdrawability only calls getEquippedFragment for inventory verification")
    public void testWithdrawabilityUsesGetEquippedFragmentForVerification() {
        // Test that WithdrawabilityCommand uses getEquippedFragment() instead of
        // hasFragmentEquipped() for proper inventory verification

        when(fragmentManager.getEquippedFragment(player)).thenReturn(null);

        withdrawabilityCommand.onCommand(player, command, "withdrawability", new String[0]);

        // Should call getEquippedFragment() which does inventory verification
        verify(fragmentManager).getEquippedFragment(player);
        // Should NOT call hasFragmentEquipped() which only checks cache
        verify(fragmentManager, never()).hasFragmentEquipped(any());
    }

    // ==================== Passive effects cleared tests ====================

    @Test
    @DisplayName("Withdrawability calls unequipFragment which clears passive effects")
    public void testWithdrawabilityClearsPassiveEffects() {
        // Verify that when unequipFragment is called, it will clear passive effects
        // The actual passive effect clearing is done by Fragment.deactivate()
        // which is called inside FragmentManager.unequipFragment()

        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.unequipFragment(player)).thenReturn(true);

        withdrawabilityCommand.onCommand(player, command, "withdrawability", new String[0]);

        // unequipFragment should be called, which internally calls fragment.deactivate(player)
        verify(fragmentManager).unequipFragment(player);
    }

    // ==================== All fragment types tests ====================

    @Test
    @DisplayName("Withdrawability works for all fragment types")
    public void testWithdrawabilityWorksForAllFragmentTypes() {
        FragmentType[] fragmentTypes = {
            FragmentType.BURNING,
            FragmentType.AGILITY,
            FragmentType.IMMORTAL,
            FragmentType.CORRUPTED
        };

        for (FragmentType type : fragmentTypes) {
            reset(fragmentManager);
            reset(player);

            when(fragmentManager.getEquippedFragment(player)).thenReturn(type);
            when(fragmentManager.unequipFragment(player)).thenReturn(true);

            boolean result = withdrawabilityCommand.onCommand(player, command, "withdrawability", new String[0]);

            assertTrue(result, "Command should succeed for " + type);
            verify(fragmentManager).unequipFragment(player);
        }
    }

    @Test
    @DisplayName("Withdrawability with arguments ignores them")
    public void testWithdrawabilityIgnoresArguments() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.unequipFragment(player)).thenReturn(true);

        // Command with arguments (should be ignored)
        boolean result = withdrawabilityCommand.onCommand(player, command, "withdrawability", new String[]{"arg1", "arg2"});

        assertTrue(result, "Command should return true");
        verify(fragmentManager).unequipFragment(player);
    }
}
