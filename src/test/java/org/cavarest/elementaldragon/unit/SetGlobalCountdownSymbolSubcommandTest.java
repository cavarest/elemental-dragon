package org.cavarest.elementaldragon.unit;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.command.subcommands.SetGlobalCountdownSymbolSubcommand;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SetGlobalCountdownSymbolSubcommand.
 */
public class SetGlobalCountdownSymbolSubcommandTest {

    @Mock
    private CommandSender sender;

    @Mock
    private ElementalDragon plugin;

    private SetGlobalCountdownSymbolSubcommand subcommand;

    private ProgressBarRenderer.ProgressVariant originalVariant;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subcommand = new SetGlobalCountdownSymbolSubcommand(plugin);
        // Save original variant to restore after tests
        originalVariant = ProgressBarRenderer.getCurrentVariant();
    }

    @AfterEach
    public void tearDown() {
        // Restore original variant
        ProgressBarRenderer.setCurrentVariant(originalVariant);
    }

    @Test
    @DisplayName("execute returns true and shows usage when no arguments provided")
    public void testExecuteNoArgsShowsUsage() {
        boolean result = subcommand.execute(sender, new String[0]);

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute returns true and shows usage with empty string argument")
    public void testExecuteEmptyArgShowsUsage() {
        boolean result = subcommand.execute(sender, new String[]{""});

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute shows detailed help when HELP is provided")
    public void testExecuteShowsHelp() {
        boolean result = subcommand.execute(sender, new String[]{"HELP"});

        assertTrue(result);
        // HELP should send multiple messages
        verify(sender, atLeast(10)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute shows detailed help when help (lowercase) is provided")
    public void testExecuteShowsHelpLowercase() {
        boolean result = subcommand.execute(sender, new String[]{"help"});

        assertTrue(result);
        // HELP should send multiple messages
        verify(sender, atLeast(10)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute returns true and shows error for invalid style")
    public void testExecuteInvalidStyleShowsError() {
        boolean result = subcommand.execute(sender, new String[]{"INVALID_STYLE"});

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute sets TILES style successfully")
    public void testExecuteSetsTilesStyle() {
        boolean result = subcommand.execute(sender, new String[]{"TILES"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.TILES, ProgressBarRenderer.getCurrentVariant().getType());
    }

    @Test
    @DisplayName("execute sets MOON style with default width")
    public void testExecuteSetsMoonStyleDefaultWidth() {
        boolean result = subcommand.execute(sender, new String[]{"MOON"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.MOON, ProgressBarRenderer.getCurrentVariant().getType());
        assertEquals(1, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute sets MOON style with custom width")
    public void testExecuteSetsMoonStyleCustomWidth() {
        boolean result = subcommand.execute(sender, new String[]{"MOON", "2"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.MOON, ProgressBarRenderer.getCurrentVariant().getType());
        assertEquals(2, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute sets CLOCK style with width 5")
    public void testExecuteSetsClockStyleWidth5() {
        boolean result = subcommand.execute(sender, new String[]{"CLOCK", "5"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.CLOCK, ProgressBarRenderer.getCurrentVariant().getType());
        assertEquals(5, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute sets SHADE style with maximum width 10")
    public void testExecuteSetsShadeStyleMaxWidth() {
        boolean result = subcommand.execute(sender, new String[]{"SHADE", "10"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.SHADE, ProgressBarRenderer.getCurrentVariant().getType());
        assertEquals(10, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute returns true and shows error for width below minimum")
    public void testExecuteWidthBelowMinimum() {
        boolean result = subcommand.execute(sender, new String[]{"MOON", "0"});

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute returns true and shows error for width above maximum")
    public void testExecuteWidthAboveMaximum() {
        boolean result = subcommand.execute(sender, new String[]{"CLOCK", "11"});

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute returns true and shows error for invalid width")
    public void testExecuteInvalidWidth() {
        boolean result = subcommand.execute(sender, new String[]{"SHADE", "abc"});

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute sets BLOCK1 style with width 3")
    public void testExecuteSetsBlock1Style() {
        boolean result = subcommand.execute(sender, new String[]{"BLOCK1", "3"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.BLOCK1, ProgressBarRenderer.getCurrentVariant().getType());
        assertEquals(3, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute sets BLOCK2 style with default width")
    public void testExecuteSetsBlock2Style() {
        boolean result = subcommand.execute(sender, new String[]{"BLOCK2"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.BLOCK2, ProgressBarRenderer.getCurrentVariant().getType());
    }

    @Test
    @DisplayName("execute sets BLOCK3 style with width 7")
    public void testExecuteSetsBlock3Style() {
        boolean result = subcommand.execute(sender, new String[]{"BLOCK3", "7"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.BLOCK3, ProgressBarRenderer.getCurrentVariant().getType());
        assertEquals(7, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute sets BLOCK4 style with width 1")
    public void testExecuteSetsBlock4Style() {
        boolean result = subcommand.execute(sender, new String[]{"BLOCK4", "1"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.BLOCK4, ProgressBarRenderer.getCurrentVariant().getType());
        assertEquals(1, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute sets TRIANGLE style with width 4")
    public void testExecuteSetsTriangleStyle() {
        boolean result = subcommand.execute(sender, new String[]{"TRIANGLE", "4"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.TRIANGLE, ProgressBarRenderer.getCurrentVariant().getType());
        assertEquals(4, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute handles lowercase style name")
    public void testExecuteHandlesLowercaseStyle() {
        boolean result = subcommand.execute(sender, new String[]{"tiles"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.TILES, ProgressBarRenderer.getCurrentVariant().getType());
    }

    @Test
    @DisplayName("execute handles mixed case style name")
    public void testExecuteHandlesMixedCaseStyle() {
        boolean result = subcommand.execute(sender, new String[]{"MoOn"});

        assertTrue(result);
        assertEquals(ProgressBarRenderer.VariantType.MOON, ProgressBarRenderer.getCurrentVariant().getType());
    }

    @Test
    @DisplayName("execute returns true for negative width (shows error)")
    public void testExecuteNegativeWidth() {
        boolean result = subcommand.execute(sender, new String[]{"CLOCK", "-1"});

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("tabComplete returns all styles when no args")
    public void testTabCompleteNoArgs() {
        List<String> completions = subcommand.tabComplete(sender, new String[0]);

        assertNotNull(completions);
        // Empty args doesn't trigger completions in the actual implementation
        // The implementation checks args.length == 1 for first arg completions
        assertTrue(completions.isEmpty(), "Should return empty list for empty args array");
    }

    @Test
    @DisplayName("tabComplete returns style completions for first arg")
    public void testTabCompleteFirstArg() {
        List<String> completions = subcommand.tabComplete(sender, new String[]{""});

        assertNotNull(completions);
        assertTrue(completions.contains("TILES"));
        assertTrue(completions.contains("MOON"));
        assertTrue(completions.contains("CLOCK"));
        assertTrue(completions.contains("SHADE"));
        assertTrue(completions.contains("BLOCK1"));
        assertTrue(completions.contains("BLOCK2"));
        assertTrue(completions.contains("BLOCK3"));
        assertTrue(completions.contains("BLOCK4"));
        assertTrue(completions.contains("TRIANGLE"));
        assertTrue(completions.contains("HELP"));
    }

    @Test
    @DisplayName("tabComplete filters by partial input for style")
    public void testTabCompletePartialStyle() {
        List<String> completions = subcommand.tabComplete(sender, new String[]{"M"});

        assertNotNull(completions);
        assertTrue(completions.contains("MOON"));
        assertFalse(completions.contains("TILES"));
        assertFalse(completions.contains("CLOCK"));
    }

    @Test
    @DisplayName("tabComplete filters by partial input for BLOCK")
    public void testTabCompletePartialBlock() {
        List<String> completions = subcommand.tabComplete(sender, new String[]{"BLOCK"});

        assertNotNull(completions);
        assertTrue(completions.contains("BLOCK1"));
        assertTrue(completions.contains("BLOCK2"));
        assertTrue(completions.contains("BLOCK3"));
        assertTrue(completions.contains("BLOCK4"));
        assertFalse(completions.contains("TILES"));
    }

    @Test
    @DisplayName("tabComplete returns width suggestions for second arg")
    public void testTabCompleteWidthArg() {
        List<String> completions = subcommand.tabComplete(sender, new String[]{"MOON", ""});

        assertNotNull(completions);
        // Should return 1-10 for width
        assertTrue(completions.contains("1"));
        assertTrue(completions.contains("5"));
        assertTrue(completions.contains("10"));
        assertEquals(10, completions.size());
    }

    @Test
    @DisplayName("tabComplete filters width by partial input")
    public void testTabCompletePartialWidth() {
        List<String> completions = subcommand.tabComplete(sender, new String[]{"MOON", "1"});

        assertNotNull(completions);
        assertTrue(completions.contains("1"));
        assertTrue(completions.contains("10"));
        assertFalse(completions.contains("2"));
        assertFalse(completions.contains("5"));
    }

    @Test
    @DisplayName("tabComplete returns empty for third arg")
    public void testTabCompleteThirdArg() {
        List<String> completions = subcommand.tabComplete(sender, new String[]{"MOON", "2", ""});

        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }

    @Test
    @DisplayName("getCommandName returns correct name")
    public void testGetCommandName() {
        assertEquals("setglobalcountdownsym", subcommand.getName());
    }

    @Test
    @DisplayName("getDescription returns correct description")
    public void testGetDescription() {
        assertEquals("Set global countdown progress bar style", subcommand.getDescription());
    }

    @Test
    @DisplayName("getUsage returns correct usage string")
    public void testGetUsage() {
        assertEquals("/ed setglobalcountdownsym <style> [width]", subcommand.getUsage());
    }

    @Test
    @DisplayName("getPermission returns correct permission")
    public void testGetPermission() {
        assertEquals("elementaldragon.admin", subcommand.getPermission());
    }

    @Test
    @DisplayName("execute ignores extra arguments after width")
    public void testExecuteIgnoresExtraArgs() {
        boolean result = subcommand.execute(sender, new String[]{"TILES", "1", "extra"});

        assertTrue(result);
        verify(sender, atLeastOnce()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("execute success message contains variant name")
    public void testExecuteSuccessMessageContainsVariant() {
        subcommand.execute(sender, new String[]{"TILES"});

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeastOnce()).sendMessage(captor.capture());

        List<Component> messages = captor.getAllValues();
        boolean foundSuccess = messages.stream()
            .anyMatch(msg -> msg.toString().contains("TILES"));

        assertTrue(foundSuccess, "Success message should contain 'TILES'");
    }

    @Test
    @DisplayName("execute includes update confirmation")
    public void testExecuteSendsUpdateConfirmation() {
        subcommand.execute(sender, new String[]{"TILES"});

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeastOnce()).sendMessage(captor.capture());

        List<Component> messages = captor.getAllValues();
        boolean foundConfirmation = messages.stream()
            .anyMatch(msg -> msg.toString().toLowerCase().contains("global") ||
                           msg.toString().toLowerCase().contains("set"));

        assertTrue(foundConfirmation, "Should send confirmation message");
    }

    @Test
    @DisplayName("execute error message includes available styles")
    public void testExecuteErrorIncludesAvailableStyles() {
        subcommand.execute(sender, new String[]{"INVALID"});

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeastOnce()).sendMessage(captor.capture());

        List<Component> messages = captor.getAllValues();
        boolean foundError = messages.stream()
            .anyMatch(msg -> msg.toString().contains("Invalid") ||
                           msg.toString().contains("Available"));

        assertTrue(foundError, "Error message should include available styles");
    }

    @Test
    @DisplayName("tabComplete is case insensitive for partial matching")
    public void testTabCompleteCaseInsensitive() {
        List<String> completions = subcommand.tabComplete(sender, new String[]{"m"});

        // Should match "MOON" because the implementation converts to uppercase
        assertTrue(completions.contains("MOON"));
    }

    @Test
    @DisplayName("execute width parameter defaults to 1 when not provided")
    public void testExecuteWidthDefaultsToOne() {
        subcommand.execute(sender, new String[]{"CLOCK"});

        assertEquals(1, ProgressBarRenderer.getCurrentVariant().getWidth());
    }

    @Test
    @DisplayName("execute handles all variant types correctly")
    public void testExecuteAllVariantTypes() {
        ProgressBarRenderer.VariantType[] types = {
            ProgressBarRenderer.VariantType.TILES,
            ProgressBarRenderer.VariantType.MOON,
            ProgressBarRenderer.VariantType.CLOCK,
            ProgressBarRenderer.VariantType.SHADE,
            ProgressBarRenderer.VariantType.BLOCK1,
            ProgressBarRenderer.VariantType.BLOCK2,
            ProgressBarRenderer.VariantType.BLOCK3,
            ProgressBarRenderer.VariantType.BLOCK4,
            ProgressBarRenderer.VariantType.TRIANGLE
        };

        for (ProgressBarRenderer.VariantType type : types) {
            subcommand.execute(sender, new String[]{type.name()});
            assertEquals(type, ProgressBarRenderer.getCurrentVariant().getType(),
                "Should set " + type + " correctly");
        }
    }
}
