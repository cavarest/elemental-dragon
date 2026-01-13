package org.cavarest.elementaldragon.unit;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.command.display.CooldownFormatter;
import org.cavarest.elementaldragon.command.subcommands.CooldownSubcommand;
import org.cavarest.elementaldragon.command.util.ElementValidator;
import org.cavarest.elementaldragon.command.util.PlayerResolver;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CooldownSubcommand.
 */
public class CooldownSubcommandTest {

    @Mock
    private CommandSender sender;

    @Mock
    private Player targetPlayer;

    @Mock
    private CooldownManager cooldownManager;

    @Mock
    private CooldownFormatter formatter;

    @Mock
    private PlayerResolver playerResolver;

    @Mock
    private ElementValidator elementValidator;

    private CooldownSubcommand subcommand;
    private MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock Bukkit.getOnlinePlayers() to return empty list for tab completion tests
        mockedBukkit = mockStatic(Bukkit.class);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(new ArrayList<>());

        subcommand = new CooldownSubcommand(
            cooldownManager,
            formatter,
            playerResolver,
            elementValidator
        );
    }

    @AfterEach
    public void tearDown() {
        mockedBukkit.close();
    }

    // ==================== execute (parent) tests ====================

    @Test
    @DisplayName("execute returns true and shows usage error for parent command")
    public void testExecuteShowsUsageError() {
        boolean result = subcommand.execute(sender, new String[0]);

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    // ==================== executeSetCooldown tests ====================

    @Test
    @DisplayName("executeSetCooldown shows usage with insufficient arguments")
    public void testExecuteSetCooldownInsufficientArgs() {
        boolean result = subcommand.executeSetCooldown(sender, new String[]{"player"});

        assertTrue(result);
        verify(sender, atLeast(3)).sendMessage(any(Component.class)); // usage + info messages
    }

    @Test
    @DisplayName("executeSetCooldown returns true when player resolution fails")
    public void testExecuteSetCooldownPlayerResolutionFails() {
        when(playerResolver.resolvePlayer(sender, "unknown")).thenReturn(null);

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "unknown", "fire", "1", "10"
        });

        assertTrue(result);
        verify(cooldownManager, never()).setCooldown(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("executeSetCooldown shows error for invalid element")
    public void testExecuteSetCooldownInvalidElement() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("invalid")).thenReturn(false);
        when(elementValidator.getValidElementsString()).thenReturn("fire, agile, immortal, corrupt, lightning");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "invalid", "1", "10"
        });

        assertTrue(result);
        verify(cooldownManager, never()).setCooldown(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("executeSetCooldown shows error for invalid ability number")
    public void testExecuteSetCooldownInvalidAbilityNumber() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("fire")).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(3)).thenReturn(false);

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "fire", "3", "10"
        });

        assertTrue(result);
        verify(cooldownManager, never()).setCooldown(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("executeSetCooldown sets cooldown for ability 1 with valid arguments")
    public void testExecuteSetCooldownAbility1Success() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("fire")).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(1)).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "fire", "1", "30"
        });

        assertTrue(result);
        verify(cooldownManager).setCooldown(targetPlayer, "fire", 1, 30);
    }

    @Test
    @DisplayName("executeSetCooldown sets cooldown for ability 2 with valid arguments")
    public void testExecuteSetCooldownAbility2Success() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("agile")).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(2)).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "agile", "2", "20"
        });

        assertTrue(result);
        verify(cooldownManager).setCooldown(targetPlayer, "agile", 2, 20);
    }

    @Test
    @DisplayName("executeSetCooldown sets both abilities with 'all' keyword")
    public void testExecuteSetCooldownAllAbilitiesSuccess() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("immortal")).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "immortal", "all", "15"
        });

        assertTrue(result);
        verify(cooldownManager).setCooldown(targetPlayer, "immortal", 1, 15);
        verify(cooldownManager).setCooldown(targetPlayer, "immortal", 2, 15);
    }

    @Test
    @DisplayName("executeSetCooldown resets cooldown with 'default' keyword")
    public void testExecuteSetCooldownDefaultReset() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement(anyString())).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(anyInt())).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "lightning", "1", "default"
        });

        assertTrue(result);
        // Verify clearCooldown was called (the implementation may use a different overload)
        verify(cooldownManager, atLeastOnce()).clearCooldown(any(Player.class), anyString(), anyInt());
    }

    @Test
    @DisplayName("executeSetCooldown resets both abilities with 'all' and 'default'")
    public void testExecuteSetCooldownDefaultResetAll() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement(anyString())).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "corrupt", "all", "default"
        });

        assertTrue(result);
        verify(cooldownManager).clearCooldown(any(Player.class), eq("corrupt"), eq(1));
        verify(cooldownManager).clearCooldown(any(Player.class), eq("corrupt"), eq(2));
    }

    @Test
    @DisplayName("executeSetCooldown disables cooldown with 0 seconds")
    public void testExecuteSetCooldownZeroSeconds() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("fire")).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(1)).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "fire", "1", "0"
        });

        assertTrue(result);
        verify(cooldownManager).setCooldown(targetPlayer, "fire", 1, 0);
    }

    @Test
    @DisplayName("executeSetCooldown shows error for negative cooldown")
    public void testExecuteSetCooldownNegativeSeconds() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("fire")).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(1)).thenReturn(true);

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "fire", "1", "-5"
        });

        assertTrue(result);
        verify(cooldownManager, never()).setCooldown(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("executeSetCooldown shows error for invalid seconds value")
    public void testExecuteSetCooldownInvalidSeconds() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("fire")).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(1)).thenReturn(true);

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "fire", "1", "abc"
        });

        assertTrue(result);
        verify(cooldownManager, never()).setCooldown(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("executeSetCooldown is case-insensitive for 'all' ability")
    public void testExecuteSetCooldownAllCaseInsensitive() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("fire")).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "fire", "ALL", "10"
        });

        assertTrue(result);
        verify(cooldownManager).setCooldown(targetPlayer, "fire", 1, 10);
        verify(cooldownManager).setCooldown(targetPlayer, "fire", 2, 10);
    }

    @Test
    @DisplayName("executeSetCooldown is case-insensitive for 'default'")
    public void testExecuteSetCooldownDefaultCaseInsensitive() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement(anyString())).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(anyInt())).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "agile", "1", "DEFAULT"
        });

        assertTrue(result);
        // Verify clearCooldown was called
        verify(cooldownManager, atLeastOnce()).clearCooldown(any(Player.class), anyString(), anyInt());
    }

    @Test
    @DisplayName("executeSetCooldown converts element to lowercase")
    public void testExecuteSetCooldownElementToLowercase() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement(anyString())).thenReturn(true);
        when(elementValidator.isValidAbilityNumber(1)).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeSetCooldown(sender, new String[]{
            "player", "FIRE", "1", "10"
        });

        assertTrue(result);
        verify(cooldownManager).setCooldown(targetPlayer, "fire", 1, 10);
    }

    // ==================== executeClearCooldown tests ====================

    @Test
    @DisplayName("executeClearCooldown shows usage with no arguments")
    public void testExecuteClearCooldownNoArgs() {
        boolean result = subcommand.executeClearCooldown(sender, new String[0]);

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("executeClearCooldown returns true when player resolution fails")
    public void testExecuteClearCooldownPlayerResolutionFails() {
        when(playerResolver.resolvePlayer(sender, "unknown")).thenReturn(null);

        boolean result = subcommand.executeClearCooldown(sender, new String[]{"unknown"});

        assertTrue(result);
        verify(cooldownManager, never()).clearCooldown(any(), any());
    }

    @Test
    @DisplayName("executeClearCooldown clears specific element cooldown")
    public void testExecuteClearCooldownSpecificElement() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("fire")).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeClearCooldown(sender, new String[]{"player", "fire"});

        assertTrue(result);
        verify(cooldownManager).clearCooldown(targetPlayer, "fire");
    }

    @Test
    @DisplayName("executeClearCooldown clears all cooldowns when element omitted")
    public void testExecuteClearCooldownAllElements() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeClearCooldown(sender, new String[]{"player"});

        assertTrue(result);
        verify(cooldownManager).clearAllCooldowns(targetPlayer);
    }

    @Test
    @DisplayName("executeClearCooldown shows error for invalid element")
    public void testExecuteClearCooldownInvalidElement() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement("invalid")).thenReturn(false);
        when(elementValidator.getValidElementsString()).thenReturn("fire, agile, immortal, corrupt, lightning");

        boolean result = subcommand.executeClearCooldown(sender, new String[]{"player", "invalid"});

        assertTrue(result);
        verify(cooldownManager, never()).clearCooldown(any(), any());
    }

    @Test
    @DisplayName("executeClearCooldown converts element to lowercase")
    public void testExecuteClearCooldownElementToLowercase() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);
        when(elementValidator.isValidElement(anyString())).thenReturn(true);
        when(targetPlayer.getName()).thenReturn("TestPlayer");

        boolean result = subcommand.executeClearCooldown(sender, new String[]{"player", "FIRE"});

        assertTrue(result);
        verify(cooldownManager).clearCooldown(targetPlayer, "fire");
    }

    // ==================== executeGetCooldown tests ====================

    @Test
    @DisplayName("executeGetCooldown shows usage with no arguments")
    public void testExecuteGetCooldownNoArgs() {
        boolean result = subcommand.executeGetCooldown(sender, new String[0]);

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("executeGetCooldown returns true when player resolution fails")
    public void testExecuteGetCooldownPlayerResolutionFails() {
        when(playerResolver.resolvePlayer(sender, "unknown")).thenReturn(null);

        boolean result = subcommand.executeGetCooldown(sender, new String[]{"unknown"});

        assertTrue(result);
        verify(formatter, never()).displayPlayerCooldowns(any(), any());
    }

    @Test
    @DisplayName("executeGetCooldown displays cooldowns for valid player")
    public void testExecuteGetCooldownSuccess() {
        when(playerResolver.resolvePlayer(sender, "player")).thenReturn(targetPlayer);

        boolean result = subcommand.executeGetCooldown(sender, new String[]{"player"});

        assertTrue(result);
        verify(formatter).displayPlayerCooldowns(sender, targetPlayer);
    }

    // ==================== tabComplete tests ====================

    @Test
    @DisplayName("tabComplete returns empty list for parent command")
    public void testTabCompleteReturnsEmpty() {
        List<String> completions = subcommand.tabComplete(sender, new String[]{"arg"});
        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }

    // ==================== tabCompleteSetCooldown tests ====================

    @Test
    @DisplayName("tabCompleteSetCooldown returns player selectors for first arg")
    public void testTabCompleteSetCooldownFirstArg() {
        List<String> completions = subcommand.tabCompleteSetCooldown(sender, new String[]{""});

        assertNotNull(completions);
        assertTrue(completions.contains("@p"));
        assertTrue(completions.contains("@s"));
        assertTrue(completions.contains("@a"));
    }

    @Test
    @DisplayName("tabCompleteSetCooldown returns elements for second arg")
    public void testTabCompleteSetCooldownSecondArg() {
        when(elementValidator.getValidElements()).thenReturn(List.of("fire", "agile", "immortal", "corrupt", "lightning"));

        List<String> completions = subcommand.tabCompleteSetCooldown(sender, new String[]{"player", ""});

        assertNotNull(completions);
        assertTrue(completions.contains("fire"));
        assertTrue(completions.contains("agile"));
        assertTrue(completions.contains("immortal"));
        assertTrue(completions.contains("corrupt"));
        assertTrue(completions.contains("lightning"));
    }

    @Test
    @DisplayName("tabCompleteSetCooldown returns ability numbers for third arg")
    public void testTabCompleteSetCooldownThirdArg() {
        List<String> completions = subcommand.tabCompleteSetCooldown(sender, new String[]{"player", "fire", ""});

        assertNotNull(completions);
        assertTrue(completions.contains("1"));
        assertTrue(completions.contains("2"));
        assertTrue(completions.contains("all"));
    }

    @Test
    @DisplayName("tabCompleteSetCooldown returns cooldown values for fourth arg")
    public void testTabCompleteSetCooldownFourthArg() {
        List<String> completions = subcommand.tabCompleteSetCooldown(sender, new String[]{"player", "fire", "1", ""});

        assertNotNull(completions);
        assertTrue(completions.contains("0"));
        assertTrue(completions.contains("10"));
        assertTrue(completions.contains("30"));
        assertTrue(completions.contains("60"));
        assertTrue(completions.contains("300"));
    }

    @Test
    @DisplayName("tabCompleteSetCooldown filters by partial input")
    public void testTabCompleteSetCooldownPartialInput() {
        when(elementValidator.getValidElements()).thenReturn(List.of("fire", "agile", "immortal"));

        List<String> completions = subcommand.tabCompleteSetCooldown(sender, new String[]{"player", "f"});

        assertNotNull(completions);
        assertTrue(completions.contains("fire"));
        assertFalse(completions.contains("agile"));
        assertFalse(completions.contains("immortal"));
    }

    @Test
    @DisplayName("tabCompleteSetCooldown returns empty for fifth arg")
    public void testTabCompleteSetCooldownFifthArg() {
        List<String> completions = subcommand.tabCompleteSetCooldown(sender, new String[]{"player", "fire", "1", "10", ""});

        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }

    // ==================== tabCompleteClearCooldown tests ====================

    @Test
    @DisplayName("tabCompleteClearCooldown returns player selectors for first arg")
    public void testTabCompleteClearCooldownFirstArg() {
        List<String> completions = subcommand.tabCompleteClearCooldown(sender, new String[]{""});

        assertNotNull(completions);
        assertTrue(completions.contains("@p"));
        assertTrue(completions.contains("@s"));
        assertTrue(completions.contains("@a"));
    }

    @Test
    @DisplayName("tabCompleteClearCooldown returns elements for second arg")
    public void testTabCompleteClearCooldownSecondArg() {
        when(elementValidator.getValidElements()).thenReturn(List.of("fire", "agile", "immortal", "corrupt", "lightning"));

        List<String> completions = subcommand.tabCompleteClearCooldown(sender, new String[]{"player", ""});

        assertNotNull(completions);
        assertTrue(completions.contains("fire"));
        assertTrue(completions.contains("agile"));
    }

    @Test
    @DisplayName("tabCompleteClearCooldown filters by partial input")
    public void testTabCompleteClearCooldownPartialInput() {
        when(elementValidator.getValidElements()).thenReturn(List.of("fire", "agile"));

        List<String> completions = subcommand.tabCompleteClearCooldown(sender, new String[]{"p", "f"});

        assertNotNull(completions);
        assertTrue(completions.contains("fire"));
        assertFalse(completions.contains("agile"));
    }

    @Test
    @DisplayName("tabCompleteClearCooldown returns empty for third arg")
    public void testTabCompleteClearCooldownThirdArg() {
        List<String> completions = subcommand.tabCompleteClearCooldown(sender, new String[]{"player", "fire", ""});

        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }

    // ==================== tabCompleteGetCooldown tests ====================

    @Test
    @DisplayName("tabCompleteGetCooldown returns player selectors")
    public void testTabCompleteGetCooldownFirstArg() {
        List<String> completions = subcommand.tabCompleteGetCooldown(sender, new String[]{""});

        assertNotNull(completions);
        assertTrue(completions.contains("@p"));
        assertTrue(completions.contains("@s"));
        assertTrue(completions.contains("@a"));
    }

    @Test
    @DisplayName("tabCompleteGetCooldown filters by partial input")
    public void testTabCompleteGetCooldownPartialInput() {
        List<String> completions = subcommand.tabCompleteGetCooldown(sender, new String[]{"@"});

        assertNotNull(completions);
        assertTrue(completions.contains("@p"));
        assertTrue(completions.contains("@s"));
        assertTrue(completions.contains("@a"));
    }

    @Test
    @DisplayName("tabCompleteGetCooldown returns empty for second arg")
    public void testTabCompleteGetCooldownSecondArg() {
        List<String> completions = subcommand.tabCompleteGetCooldown(sender, new String[]{"player", ""});

        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }

    // ==================== Getter method tests ====================

    @Test
    @DisplayName("getName returns correct name")
    public void testGetName() {
        assertEquals("cooldown", subcommand.getName());
    }

    @Test
    @DisplayName("getDescription returns correct description")
    public void testGetDescription() {
        assertEquals("Manage player cooldowns", subcommand.getDescription());
    }

    @Test
    @DisplayName("getUsage returns correct usage")
    public void testGetUsage() {
        assertEquals("/ed <setcooldown|clearcooldown|getcooldown> ...", subcommand.getUsage());
    }

    @Test
    @DisplayName("getPermission returns correct permission")
    public void testGetPermission() {
        assertEquals("elementaldragon.admin", subcommand.getPermission());
    }
}
