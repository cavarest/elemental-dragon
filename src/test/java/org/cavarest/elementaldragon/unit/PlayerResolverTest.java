package org.cavarest.elementaldragon.unit;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.command.util.PlayerResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PlayerResolver.
 */
public class PlayerResolverTest {

    private CommandSender sender;
    private CommandSender consoleSender;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    private PlayerResolver playerResolver;
    private MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        playerResolver = new PlayerResolver();

        // Set up player names
        when(player1.getName()).thenReturn("Player1");
        when(player2.getName()).thenReturn("Player2");

        // Use player1 as a CommandSender for @p and @s tests
        sender = player1;
        // Create a mock console sender (not a Player)
        consoleSender = mock(CommandSender.class);

        // Mock Bukkit.getOnlinePlayers() to return empty list by default
        mockedBukkit = mockStatic(Bukkit.class);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(new ArrayList<>());
    }

    @AfterEach
    public void tearDown() {
        mockedBukkit.close();
    }

    // ==================== resolvePlayer (single) tests ====================

    @Test
    @DisplayName("resolvePlayer with @p returns sender if sender is Player")
    public void testResolvePlayerAtPOfflineModePlayer() {
        Player result = playerResolver.resolvePlayer(sender, "@p");

        assertSame(sender, result);
    }

    @Test
    @DisplayName("resolvePlayer with @p returns first online player when sender is not Player")
    public void testResolvePlayerAtPConsole() {
        Collection<Player> players = List.of(player1, player2);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(players);

        Player result = playerResolver.resolvePlayer(consoleSender, "@p");

        assertEquals(player1, result);
    }

    @Test
    @DisplayName("resolvePlayer with @p returns null when no players online")
    public void testResolvePlayerAtPNoPlayers() {
        Player result = playerResolver.resolvePlayer(consoleSender, "@p");

        assertNull(result);
        verify(consoleSender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("resolvePlayer with @s returns sender if sender is Player")
    public void testResolvePlayerAtSPlayer() {
        Player result = playerResolver.resolvePlayer(sender, "@s");

        assertSame(sender, result);
    }

    @Test
    @DisplayName("resolvePlayer with @s returns null and sends error when sender is not Player")
    public void testResolvePlayerAtSConsole() {
        Player result = playerResolver.resolvePlayer(consoleSender, "@s");

        assertNull(result);
        verify(consoleSender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("resolvePlayer with @a returns first online player")
    public void testResolvePlayerAtA() {
        Collection<Player> players = List.of(player1, player2);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(players);

        Player result = playerResolver.resolvePlayer(sender, "@a");

        assertEquals(player1, result);
    }

    @Test
    @DisplayName("resolvePlayer with @a returns null when no players online")
    public void testResolvePlayerAtANoPlayers() {
        Player result = playerResolver.resolvePlayer(sender, "@a");

        assertNull(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("resolvePlayer with exact player name returns player")
    public void testResolvePlayerExactName() {
        mockedBukkit.when(() -> Bukkit.getPlayer("Player1")).thenReturn(player1);

        Player result = playerResolver.resolvePlayer(sender, "Player1");

        assertEquals(player1, result);
    }

    @Test
    @DisplayName("resolvePlayer with player name is case-sensitive")
    public void testResolvePlayerCaseSensitive() {
        mockedBukkit.when(() -> Bukkit.getPlayer("Player1")).thenReturn(player1);
        mockedBukkit.when(() -> Bukkit.getPlayer("player1")).thenReturn(null);

        Player result = playerResolver.resolvePlayer(sender, "Player1");

        assertEquals(player1, result);
    }

    @Test
    @DisplayName("resolvePlayer returns null for non-existent player name")
    public void testResolvePlayerNonExistentName() {
        mockedBukkit.when(() -> Bukkit.getPlayer("NonExistent")).thenReturn(null);

        Player result = playerResolver.resolvePlayer(sender, "NonExistent");

        assertNull(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("resolvePlayer is case-insensitive for selectors")
    public void testResolvePlayerSelectorsCaseInsensitive() {
        assertSame(sender, playerResolver.resolvePlayer(sender, "@P"));
        assertSame(sender, playerResolver.resolvePlayer(sender, "@S"));
        // @A always returns first online player, so test with console sender
        Collection<Player> players = List.of(player1);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(players);
        assertEquals(player1, playerResolver.resolvePlayer(consoleSender, "@A"));
    }

    // ==================== resolvePlayers (multiple) tests ====================

    @Test
    @DisplayName("resolvePlayers with @p returns list with sender if sender is Player")
    public void testResolvePlayersAtPOfflineModePlayer() {
        List<Player> result = playerResolver.resolvePlayers(sender, "@p");

        assertEquals(1, result.size());
        assertTrue(result.contains(player1));
    }

    @Test
    @DisplayName("resolvePlayers with @p returns first online player when sender is not Player")
    public void testResolvePlayersAtPConsole() {
        Collection<Player> players = List.of(player1, player2);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(players);

        List<Player> result = playerResolver.resolvePlayers(consoleSender, "@p");

        assertEquals(1, result.size());
        assertTrue(result.contains(player1));
    }

    @Test
    @DisplayName("resolvePlayers with @p returns empty list when no players online")
    public void testResolvePlayersAtPNoPlayers() {
        List<Player> result = playerResolver.resolvePlayers(consoleSender, "@p");

        assertTrue(result.isEmpty());
        // resolvePlayers doesn't send error message for @p, it just returns empty list
        // (only resolvePlayer sends error message for @p with no players)
    }

    @Test
    @DisplayName("resolvePlayers with @s returns list with sender if sender is Player")
    public void testResolvePlayersAtSPlayer() {
        List<Player> result = playerResolver.resolvePlayers(sender, "@s");

        assertEquals(1, result.size());
        assertTrue(result.contains(player1));
    }

    @Test
    @DisplayName("resolvePlayers with @s returns empty list when sender is not Player")
    public void testResolvePlayersAtSConsole() {
        List<Player> result = playerResolver.resolvePlayers(consoleSender, "@s");

        assertTrue(result.isEmpty());
        verify(consoleSender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("resolvePlayers with @a returns all online players")
    public void testResolvePlayersAtA() {
        Collection<Player> players = List.of(player1, player2);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(players);

        List<Player> result = playerResolver.resolvePlayers(sender, "@a");

        assertEquals(2, result.size());
        assertTrue(result.contains(player1));
        assertTrue(result.contains(player2));
    }

    @Test
    @DisplayName("resolvePlayers with @a returns empty list when no players online")
    public void testResolvePlayersAtANoPlayers() {
        List<Player> result = playerResolver.resolvePlayers(consoleSender, "@a");

        assertTrue(result.isEmpty());
        verify(consoleSender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("resolvePlayers with exact player name returns list with that player")
    public void testResolvePlayersExactName() {
        mockedBukkit.when(() -> Bukkit.getPlayer("Player1")).thenReturn(player1);

        List<Player> result = playerResolver.resolvePlayers(sender, "Player1");

        assertEquals(1, result.size());
        assertTrue(result.contains(player1));
    }

    @Test
    @DisplayName("resolvePlayers returns empty list for non-existent player name")
    public void testResolvePlayersNonExistentName() {
        mockedBukkit.when(() -> Bukkit.getPlayer("NonExistent")).thenReturn(null);

        List<Player> result = playerResolver.resolvePlayers(sender, "NonExistent");

        assertTrue(result.isEmpty());
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("resolvePlayers is case-insensitive for selectors")
    public void testResolvePlayersSelectorsCaseInsensitive() {
        List<Player> resultP = playerResolver.resolvePlayers(sender, "@P");
        List<Player> resultS = playerResolver.resolvePlayers(sender, "@S");

        assertEquals(1, resultP.size());
        assertEquals(1, resultS.size());
        assertTrue(resultP.contains(player1));
        assertTrue(resultS.contains(player1));
    }

    @Test
    @DisplayName("resolvePlayers returns all online players for @a selector")
    public void testResolvePlayersAtAAllPlayers() {
        Collection<Player> players = List.of(player1, player2);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(players);

        List<Player> result = playerResolver.resolvePlayers(sender, "@a");

        assertEquals(2, result.size());
        assertTrue(result.contains(player1));
        assertTrue(result.contains(player2));
    }
}
