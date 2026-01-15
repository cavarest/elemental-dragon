package org.cavarest.elementaldragon.unit.tracking;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.ability.Ability;
import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.tracking.ElementalPlayerTracker;
import org.cavarest.elementaldragon.tracking.ElementalPlayerTracker.PlayerElementalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ElementalPlayerTracker.
 */
@DisplayName("ElementalPlayerTracker Tests")
public class ElementalPlayerTrackerTest {

    @Mock
    private ElementalDragon plugin;

    @Mock
    private AbilityManager abilityManager;

    @Mock
    private FragmentManager fragmentManager;

    @Mock
    private Ability lightningAbility;

    private ElementalPlayerTracker tracker;

    private UUID playerUuid;
    private static final String PLAYER_NAME = "TestPlayer";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        playerUuid = UUID.randomUUID();

        // Setup plugin mocks
        when(plugin.getAbilityManager()).thenReturn(abilityManager);
        when(plugin.getFragmentManager()).thenReturn(fragmentManager);

        // Setup ability mocks
        when(abilityManager.getAbility(1)).thenReturn(lightningAbility);

        tracker = new ElementalPlayerTracker(plugin);
    }

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor initializes tracker")
    public void testConstructor() {
        assertNotNull(tracker);
    }

    @Test
    @DisplayName("Constructor initializes with empty player map")
    public void testConstructorInitializesEmptyMap() {
        Set<PlayerElementalStatus> statuses = tracker.getAllPlayerStatuses();
        assertNotNull(statuses);
        assertTrue(statuses.isEmpty());
    }

    // ==================== getPlayerStatus tests ====================

    @Test
    @DisplayName("getPlayerStatus returns null for null player")
    public void testGetPlayerStatusNullPlayer() {
        PlayerElementalStatus status = tracker.getPlayerStatus(null);
        assertNull(status);
    }

    @Test
    @DisplayName("getPlayerStatus creates new status for player")
    public void testGetPlayerStatusCreatesNew() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);

        assertNotNull(status);
        assertEquals(PLAYER_NAME, status.playerName);
    }

    @Test
    @DisplayName("getPlayerStatus returns existing status for same player")
    public void testGetPlayerStatusReturnsExisting() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        PlayerElementalStatus status1 = tracker.getPlayerStatus(player);
        PlayerElementalStatus status2 = tracker.getPlayerStatus(player);

        assertSame(status1, status2);
    }

    // ==================== refreshPlayerStatus tests ====================

    @Test
    @DisplayName("refreshPlayerStatus does nothing for null player")
    public void testRefreshPlayerStatusNullPlayer() {
        assertDoesNotThrow(() -> tracker.refreshPlayerStatus(null));
    }

    @Test
    @DisplayName("refreshPlayerStatus updates player name")
    public void testRefreshPlayerStatusUpdatesName() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertEquals(PLAYER_NAME, status.playerName);
    }

    @Test
    @DisplayName("refreshPlayerStatus sets hasFire when BURNING equipped")
    public void testRefreshPlayerStatusBurningEquipped() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertTrue(status.hasFire);
        assertFalse(status.hasWind);
        assertFalse(status.hasBlood);
        assertFalse(status.hasDarkness);
    }

    @Test
    @DisplayName("refreshPlayerStatus sets hasWind when AGILITY equipped")
    public void testRefreshPlayerStatusAgilityEquipped() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.AGILITY);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertFalse(status.hasFire);
        assertTrue(status.hasWind);
        assertFalse(status.hasBlood);
        assertFalse(status.hasDarkness);
    }

    @Test
    @DisplayName("refreshPlayerStatus sets hasBlood when IMMORTAL equipped")
    public void testRefreshPlayerStatusImmortalEquipped() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.IMMORTAL);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertFalse(status.hasFire);
        assertFalse(status.hasWind);
        assertTrue(status.hasBlood);
        assertFalse(status.hasDarkness);
    }

    @Test
    @DisplayName("refreshPlayerStatus sets hasDarkness when CORRUPTED equipped")
    public void testRefreshPlayerStatusCorruptedEquipped() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.CORRUPTED);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertFalse(status.hasFire);
        assertFalse(status.hasWind);
        assertFalse(status.hasBlood);
        assertTrue(status.hasDarkness);
    }

    @Test
    @DisplayName("refreshPlayerStatus sets hasLight when has dragon egg")
    public void testRefreshPlayerStatusHasLightning() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(fragmentManager.getEquippedFragment(player)).thenReturn(null);
        when(lightningAbility.hasRequiredItem(player)).thenReturn(true);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertFalse(status.hasFire);
        assertFalse(status.hasWind);
        assertFalse(status.hasBlood);
        assertFalse(status.hasDarkness);
        assertTrue(status.hasLight);
    }

    @Test
    @DisplayName("refreshPlayerStatus handles no fragments and no lightning")
    public void testRefreshPlayerStatusNoPowers() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(fragmentManager.getEquippedFragment(player)).thenReturn(null);
        when(lightningAbility.hasRequiredItem(player)).thenReturn(false);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertFalse(status.hasFire);
        assertFalse(status.hasWind);
        assertFalse(status.hasBlood);
        assertFalse(status.hasDarkness);
        assertFalse(status.hasLight);
    }

    @Test
    @DisplayName("refreshPlayerStatus handles null fragmentManager")
    public void testRefreshPlayerStatusNullFragmentManager() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(plugin.getFragmentManager()).thenReturn(null);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertFalse(status.hasFire);
        assertFalse(status.hasWind);
        assertFalse(status.hasBlood);
        assertFalse(status.hasDarkness);
    }

    @Test
    @DisplayName("refreshPlayerStatus handles null abilityManager")
    public void testRefreshPlayerStatusNullAbilityManager() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(plugin.getAbilityManager()).thenReturn(null);

        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertFalse(status.hasLight);
    }

    // ==================== getAllPlayerStatuses tests ====================

    @Test
    @DisplayName("getAllPlayerStatuses returns empty set initially")
    public void testGetAllPlayerStatusesEmpty() {
        Set<PlayerElementalStatus> statuses = tracker.getAllPlayerStatuses();
        assertNotNull(statuses);
        assertTrue(statuses.isEmpty());
    }

    @Test
    @DisplayName("getAllPlayerStatuses returns all tracked players")
    public void testGetAllPlayerStatusesReturnsAll() {
        var player1 = mock(org.bukkit.entity.Player.class);
        var player2 = mock(org.bukkit.entity.Player.class);
        UUID uuid2 = UUID.randomUUID();

        when(player1.getUniqueId()).thenReturn(playerUuid);
        when(player1.getName()).thenReturn("Player1");
        when(player2.getUniqueId()).thenReturn(uuid2);
        when(player2.getName()).thenReturn("Player2");

        tracker.getPlayerStatus(player1);
        tracker.getPlayerStatus(player2);

        Set<PlayerElementalStatus> statuses = tracker.getAllPlayerStatuses();
        assertEquals(2, statuses.size());
    }

    @Test
    @DisplayName("getAllPlayerStatuses returns a copy (not modifiable)")
    public void testGetAllPlayerStatusesReturnsCopy() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        tracker.getPlayerStatus(player);

        Set<PlayerElementalStatus> statuses1 = tracker.getAllPlayerStatuses();
        Set<PlayerElementalStatus> statuses2 = tracker.getAllPlayerStatuses();

        // Should be different objects (copies)
        assertNotSame(statuses1, statuses2);
        assertEquals(statuses1.size(), statuses2.size());
    }

    // ==================== getElementCounts tests ====================

    @Test
    @DisplayName("getElementCounts returns zero counts initially")
    public void testGetElementCountsInitially() {
        Map<String, Integer> counts = tracker.getElementCounts();

        assertNotNull(counts);
        assertEquals(5, counts.size());
        assertEquals(0, counts.get("fire"));
        assertEquals(0, counts.get("wind"));
        assertEquals(0, counts.get("blood"));
        assertEquals(0, counts.get("darkness"));
        assertEquals(0, counts.get("light"));
    }

    @Test
    @DisplayName("getElementCounts counts fire players")
    public void testGetElementCountsFire() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);

        tracker.refreshPlayerStatus(player);

        Map<String, Integer> counts = tracker.getElementCounts();
        assertEquals(1, counts.get("fire"));
        assertEquals(0, counts.get("wind"));
    }

    @Test
    @DisplayName("getElementCounts counts all element types")
    public void testGetElementCountsAllTypes() {
        var player1 = mock(org.bukkit.entity.Player.class);
        var player2 = mock(org.bukkit.entity.Player.class);
        UUID uuid2 = UUID.randomUUID();

        when(player1.getUniqueId()).thenReturn(playerUuid);
        when(player1.getName()).thenReturn("Player1");
        when(player2.getUniqueId()).thenReturn(uuid2);
        when(player2.getName()).thenReturn("Player2");

        // Player1 has fire and wind
        when(fragmentManager.getEquippedFragment(player1)).thenReturn(FragmentType.BURNING);
        // Player2 has blood
        when(fragmentManager.getEquippedFragment(player2)).thenReturn(FragmentType.IMMORTAL);

        tracker.refreshPlayerStatus(player1);
        tracker.refreshPlayerStatus(player2);

        Map<String, Integer> counts = tracker.getElementCounts();
        assertEquals(1, counts.get("fire"));
        assertEquals(0, counts.get("wind")); // Only player1 has BURNING, not AGILITY
        assertEquals(1, counts.get("blood"));
    }

    // ==================== PlayerElementalStatus tests ====================

    @Test
    @DisplayName("PlayerElementalStatus initializes with all false")
    public void testPlayerElementalStatusConstructor() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");

        assertEquals("TestPlayer", status.playerName);
        assertFalse(status.hasFire);
        assertFalse(status.hasWind);
        assertFalse(status.hasBlood);
        assertFalse(status.hasDarkness);
        assertFalse(status.hasLight);
    }

    @Test
    @DisplayName("getStatusEmojis returns dash when no powers")
    public void testGetStatusEmojisNoPowers() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");

        String emojis = status.getStatusEmojis();
        assertEquals("—", emojis);
    }

    @Test
    @DisplayName("getStatusEmojis returns fire emoji")
    public void testGetStatusEmojisFire() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");
        status.hasFire = true;

        String emojis = status.getStatusEmojis();
        assertEquals("🔥", emojis);
    }

    @Test
    @DisplayName("getStatusEmojis returns wind emoji")
    public void testGetStatusEmojisWind() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");
        status.hasWind = true;

        String emojis = status.getStatusEmojis();
        assertEquals("💨", emojis);
    }

    @Test
    @DisplayName("getStatusEmojis returns blood emoji")
    public void testGetStatusEmojisBlood() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");
        status.hasBlood = true;

        String emojis = status.getStatusEmojis();
        assertEquals("🩸", emojis);
    }

    @Test
    @DisplayName("getStatusEmojis returns darkness emoji")
    public void testGetStatusEmojisDarkness() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");
        status.hasDarkness = true;

        String emojis = status.getStatusEmojis();
        assertEquals("👁", emojis);
    }

    @Test
    @DisplayName("getStatusEmojis returns light emoji")
    public void testGetStatusEmojisLight() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");
        status.hasLight = true;

        String emojis = status.getStatusEmojis();
        assertEquals("⚡", emojis);
    }

    @Test
    @DisplayName("getStatusEmojis returns multiple emojis")
    public void testGetStatusEmojisMultiple() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");
        status.hasFire = true;
        status.hasWind = true;
        status.hasLight = true;

        String emojis = status.getStatusEmojis();
        assertTrue(emojis.contains("🔥"));
        assertTrue(emojis.contains("💨"));
        assertTrue(emojis.contains("⚡"));
    }

    @Test
    @DisplayName("getCompactStatus returns same as getStatusEmojis")
    public void testGetCompactStatus() {
        PlayerElementalStatus status = new PlayerElementalStatus("TestPlayer");
        status.hasFire = true;

        assertEquals(status.getStatusEmojis(), status.getCompactStatus());
    }

    // ==================== Multiple player tests ====================

    @Test
    @DisplayName("Multiple players can have different powers")
    public void testMultiplePlayersDifferentPowers() {
        var player1 = mock(org.bukkit.entity.Player.class);
        var player2 = mock(org.bukkit.entity.Player.class);
        UUID uuid2 = UUID.randomUUID();

        when(player1.getUniqueId()).thenReturn(playerUuid);
        when(player1.getName()).thenReturn("Player1");
        when(player2.getUniqueId()).thenReturn(uuid2);
        when(player2.getName()).thenReturn("Player2");

        // Player1 has fire
        when(fragmentManager.getEquippedFragment(player1)).thenReturn(FragmentType.BURNING);
        // Player2 has wind
        when(fragmentManager.getEquippedFragment(player2)).thenReturn(FragmentType.AGILITY);

        tracker.refreshPlayerStatus(player1);
        tracker.refreshPlayerStatus(player2);

        PlayerElementalStatus status1 = tracker.getPlayerStatus(player1);
        PlayerElementalStatus status2 = tracker.getPlayerStatus(player2);

        assertTrue(status1.hasFire);
        assertFalse(status1.hasWind);
        assertFalse(status2.hasFire);
        assertTrue(status2.hasWind);
    }

    @Test
    @DisplayName("Player status updates when fragment changes")
    public void testPlayerStatusUpdates() {
        var player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(PLAYER_NAME);

        // Initially has fire
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.BURNING);
        tracker.refreshPlayerStatus(player);

        PlayerElementalStatus status = tracker.getPlayerStatus(player);
        assertTrue(status.hasFire);

        // Change to wind
        when(fragmentManager.getEquippedFragment(player)).thenReturn(FragmentType.AGILITY);
        tracker.refreshPlayerStatus(player);

        status = tracker.getPlayerStatus(player);
        assertFalse(status.hasFire);
        assertTrue(status.hasWind);
    }
}
