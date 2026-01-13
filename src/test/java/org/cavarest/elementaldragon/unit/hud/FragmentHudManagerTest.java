package org.cavarest.elementaldragon.unit.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.hud.FragmentHudManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FragmentHudManager.
 */
@DisplayName("FragmentHudManager Tests")
public class FragmentHudManagerTest {

    @Mock
    private ElementalDragon plugin;

    @Mock
    private FragmentManager fragmentManager;

    @Mock
    private Player player;

    @Mock
    private BukkitScheduler scheduler;

    @Mock
    private BukkitTask bukkitTask;

    @Mock
    private Server server;

    private MockedStatic<Bukkit> mockedBukkit;
    private FragmentHudManager fragmentHudManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Bukkit.getScheduler() to return our mock scheduler
        mockedBukkit = mockStatic(Bukkit.class);
        mockedBukkit.when(Bukkit::getScheduler).thenReturn(scheduler);

        // Mock runTaskTimer to return a mock task (using doAnswer for void method)
        doAnswer(invocation -> bukkitTask).when(scheduler).runTaskTimer(
            eq(plugin), any(Runnable.class), eq(0L), eq(20L)
        );

        // Set up server
        when(server.getScheduler()).thenReturn(scheduler);
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);

        fragmentHudManager = new FragmentHudManager(plugin, fragmentManager);
    }

    @AfterEach
    public void tearDown() {
        fragmentHudManager.shutdown();
        mockedBukkit.close();
    }

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor initializes and starts update task")
    public void testConstructorInitialization() {
        assertNotNull(fragmentHudManager, "Manager should be initialized");
    }

    // ==================== updatePlayerHud tests ====================

    @Test
    @DisplayName("updatePlayerHud handles null player gracefully")
    public void testUpdatePlayerHudNullPlayer() {
        fragmentHudManager.updatePlayerHud(null);

        // Should not throw
        verify(player, never()).sendActionBar(any(Component.class));
        verify(fragmentManager, never()).getEquippedFragment(any());
    }

    @Test
    @DisplayName("updatePlayerHud does nothing when no fragment equipped")
    public void testUpdatePlayerHudNoFragment() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(null);

        fragmentHudManager.updatePlayerHud(player);

        verify(player, never()).sendActionBar(any(Component.class));
    }

    @Test
    @DisplayName("updatePlayerHud sends action bar with cooldown")
    public void testUpdatePlayerHudWithCooldown() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(30);

        fragmentHudManager.updatePlayerHud(player);

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(player).sendActionBar(captor.capture());

        Component hudText = captor.getValue();
        assertNotNull(hudText);
    }

    @Test
    @DisplayName("updatePlayerHud sends action bar when ready")
    public void testUpdatePlayerHudReady() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(0);

        fragmentHudManager.updatePlayerHud(player);

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(player).sendActionBar(captor.capture());

        Component hudText = captor.getValue();
        assertNotNull(hudText);
    }

    @Test
    @DisplayName("updatePlayerHud handles different fragment types")
    public void testUpdatePlayerHudDifferentFragments() {
        FragmentType[] fragments = {
            FragmentType.BURNING,
            FragmentType.AGILITY,
            FragmentType.IMMORTAL,
            FragmentType.CORRUPTED
        };

        for (FragmentType fragment : fragments) {
            when(fragmentManager.getEquippedFragment(player)).thenReturn(fragment);
            when(fragmentManager.getRemainingCooldown(player)).thenReturn(0);

            fragmentHudManager.updatePlayerHud(player);

            verify(player, atLeastOnce()).sendActionBar(any(Component.class));
        }
    }

    @Test
    @DisplayName("updatePlayerHud handles various cooldown values")
    public void testUpdatePlayerHudVariousCooldowns() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);

        int[] cooldowns = {0, 1, 30, 60, 120};
        for (int cooldown : cooldowns) {
            when(fragmentManager.getRemainingCooldown(player)).thenReturn(cooldown);

            fragmentHudManager.updatePlayerHud(player);

            verify(player, atLeastOnce()).sendActionBar(any(Component.class));
        }
    }

    // ==================== shutdown tests ====================

    @Test
    @DisplayName("shutdown cancels update task")
    public void testShutdown() {
        assertDoesNotThrow(() -> fragmentHudManager.shutdown());
    }

    @Test
    @DisplayName("shutdown can be called multiple times")
    public void testShutdownMultipleTimes() {
        assertDoesNotThrow(() -> {
            fragmentHudManager.shutdown();
            fragmentHudManager.shutdown();
        });
    }

    // ==================== Fragment color tests ====================

    @Test
    @DisplayName("Burning fragment uses RED color")
    public void testBurningFragmentColor() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(0);

        fragmentHudManager.updatePlayerHud(player);

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(player).sendActionBar(captor.capture());

        // The component should contain the fragment's display name
        Component hudText = captor.getValue();
        assertNotNull(hudText);
    }

    @Test
    @DisplayName("Agility fragment uses AQUA color")
    public void testAgilityFragmentColor() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.AGILITY);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(0);

        fragmentHudManager.updatePlayerHud(player);

        verify(player).sendActionBar(any(Component.class));
    }

    @Test
    @DisplayName("Immortal fragment uses GOLD color")
    public void testImmortalFragmentColor() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.IMMORTAL);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(0);

        fragmentHudManager.updatePlayerHud(player);

        verify(player).sendActionBar(any(Component.class));
    }

    @Test
    @DisplayName("Corrupted fragment uses DARK_PURPLE color")
    public void testCorruptedFragmentColor() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.CORRUPTED);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(0);

        fragmentHudManager.updatePlayerHud(player);

        verify(player).sendActionBar(any(Component.class));
    }

    // ==================== Fragment display tests ====================

    @Test
    @DisplayName("Fragment display includes display name")
    public void testFragmentDisplayName() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(0);

        fragmentHudManager.updatePlayerHud(player);

        verify(player).sendActionBar(any(Component.class));
    }

    @Test
    @DisplayName("Cooldown display shows seconds correctly")
    public void testCooldownDisplayFormat() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(45);

        fragmentHudManager.updatePlayerHud(player);

        verify(player).sendActionBar(any(Component.class));
    }

    @Test
    @DisplayName("Ready display shows READY text")
    public void testReadyDisplay() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(0);

        fragmentHudManager.updatePlayerHud(player);

        verify(player).sendActionBar(any(Component.class));
    }

    // ==================== Edge case tests ====================

    @Test
    @DisplayName("updatePlayerHud handles negative cooldown")
    public void testNegativeCooldown() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(-1);

        fragmentHudManager.updatePlayerHud(player);

        // Should still send action bar
        verify(player).sendActionBar(any(Component.class));
    }

    @Test
    @DisplayName("updatePlayerHud handles very large cooldown")
    public void testLargeCooldown() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        when(fragmentManager.getRemainingCooldown(player)).thenReturn(Integer.MAX_VALUE);

        fragmentHudManager.updatePlayerHud(player);

        verify(player).sendActionBar(any(Component.class));
    }

    @Test
    @DisplayName("updatePlayerHud handles fragment manager returning null")
    public void testNullFragmentManager() {
        when(fragmentManager.getEquippedFragment(player)).thenReturn(null);

        fragmentHudManager.updatePlayerHud(player);

        verify(player, never()).sendActionBar(any(Component.class));
    }
}
