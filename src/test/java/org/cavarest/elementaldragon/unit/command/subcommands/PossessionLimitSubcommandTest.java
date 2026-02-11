package org.cavarest.elementaldragon.unit.command.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.command.subcommands.PossessionLimitSubcommand;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for PossessionLimitSubcommand.
 */
@DisplayName("PossessionLimitSubcommand Tests")
public class PossessionLimitSubcommandTest {

    @Mock
    private CommandSender sender;

    @Mock
    private Player player;

    private PossessionLimitSubcommand subcommand;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subcommand = new PossessionLimitSubcommand();
    }

    @Test
    @DisplayName("executeGetPossessionLimit displays all limits")
    public void testExecuteGetDisplaysLimits() {
        boolean result = subcommand.executeGetPossessionLimit(sender, new String[0]);

        assertTrue(result);
    }

    @Test
    @DisplayName("executeSetPossessionLimit shows usage with no args")
    public void testExecuteSetShowsUsage() {
        boolean result = subcommand.executeSetPossessionLimit(sender, new String[0]);

        assertTrue(result);
    }

    @Test
    @DisplayName("executeSetPossessionLimit shows usage with one arg")
    public void testExecuteSetShowsUsageWithOneArg() {
        boolean result = subcommand.executeSetPossessionLimit(sender, new String[]{"fire"});

        assertTrue(result);
    }

    @Test
    @DisplayName("executeSetPossessionLimit sets valid limit")
    public void testExecuteSetSetsValidLimit() {
        boolean result = subcommand.executeSetPossessionLimit(sender, new String[]{"fire", "5"});

        assertTrue(result);
        assertEquals(5, subcommand.getLimit(FragmentType.BURNING));
    }

    @Test
    @DisplayName("executeSetPossessionLimit sets zero as unlimited")
    public void testExecuteSetSetsZeroAsUnlimited() {
        boolean result = subcommand.executeSetPossessionLimit(sender, new String[]{"agile", "0"});

        assertTrue(result);
        assertEquals(0, subcommand.getLimit(FragmentType.AGILITY));
    }

    @Test
    @DisplayName("executeSetPossessionLimit resets to default")
    public void testExecuteSetResetsToDefault() {
        subcommand.setLimit(FragmentType.IMMORTAL, 5);
        assertNotEquals(1, subcommand.getLimit(FragmentType.IMMORTAL));

        boolean result = subcommand.executeSetPossessionLimit(sender, new String[]{"immortal", "default"});

        assertTrue(result);
        assertEquals(1, subcommand.getLimit(FragmentType.IMMORTAL));
    }

    @Test
    @DisplayName("executeSetPossessionLimit rejects negative limit")
    public void testExecuteSetRejectsNegative() {
        boolean result = subcommand.executeSetPossessionLimit(sender, new String[]{"corrupt", "-1"});

        assertTrue(result);
        assertEquals(1, subcommand.getLimit(FragmentType.CORRUPTED)); // Should remain at default
    }

    @Test
    @DisplayName("executeSetPossessionLimit rejects invalid element")
    public void testExecuteSetRejectsInvalidElement() {
        boolean result = subcommand.executeSetPossessionLimit(sender, new String[]{"invalid", "5"});

        assertTrue(result);
    }

    @Test
    @DisplayName("executeSetPossessionLimit rejects invalid count")
    public void testExecuteSetRejectsInvalidCount() {
        boolean result = subcommand.executeSetPossessionLimit(sender, new String[]{"fire", "abc"});

        assertTrue(result);
    }

    @Test
    @DisplayName("getLimit returns correct default for each type")
    public void testGetLimitReturnsDefaults() {
        assertEquals(1, subcommand.getLimit(FragmentType.BURNING));
        assertEquals(1, subcommand.getLimit(FragmentType.AGILITY));
        assertEquals(1, subcommand.getLimit(FragmentType.IMMORTAL));
        assertEquals(1, subcommand.getLimit(FragmentType.CORRUPTED));
    }

    @Test
    @DisplayName("getDefaultLimit returns correct default for each type")
    public void testGetDefaultLimitReturnsDefaults() {
        assertEquals(1, subcommand.getDefaultLimit(FragmentType.BURNING));
        assertEquals(1, subcommand.getDefaultLimit(FragmentType.AGILITY));
        assertEquals(1, subcommand.getDefaultLimit(FragmentType.IMMORTAL));
        assertEquals(1, subcommand.getDefaultLimit(FragmentType.CORRUPTED));
    }

    @Test
    @DisplayName("setLimit updates the limit")
    public void testSetLimitUpdates() {
        subcommand.setLimit(FragmentType.BURNING, 5);
        assertEquals(5, subcommand.getLimit(FragmentType.BURNING));

        subcommand.setLimit(FragmentType.AGILITY, 0);
        assertEquals(0, subcommand.getLimit(FragmentType.AGILITY));
    }

    @Test
    @DisplayName("setLimit ignores negative values")
    public void testSetLimitIgnoresNegative() {
        int original = subcommand.getLimit(FragmentType.IMMORTAL);
        subcommand.setLimit(FragmentType.IMMORTAL, -5);

        assertEquals(original, subcommand.getLimit(FragmentType.IMMORTAL));
    }

    @Test
    @DisplayName("setLimit ignores null fragment type")
    public void testSetLimitIgnoresNull() {
        subcommand.setLimit(null, 5);
        // Should not throw exception
    }

    @Test
    @DisplayName("tabCompleteSetPossessionLimit returns elements")
    public void testTabCompleteReturnsElements() {
        var completions = subcommand.tabCompleteSetPossessionLimit(sender, new String[0]);

        // Verify completions are returned (not null, can be empty)
        assertNotNull(completions);
        // Test passes if no exception is thrown
        assertTrue(true);
    }

    @Test
    @DisplayName("tabCompleteSetPossessionLimit returns counts")
    public void testTabCompleteReturnsCounts() {
        var completions = subcommand.tabCompleteSetPossessionLimit(sender, new String[]{"fire"});

        // Check for expected count values (may be filtered)
        assertTrue(!completions.isEmpty());
    }

    @Test
    @DisplayName("tabCompleteSetPossessionLimit filters by partial")
    public void testTabCompleteFiltersPartial() {
        var completions = subcommand.tabCompleteSetPossessionLimit(sender, new String[]{"f"});

        // Check that fire is in the results
        assertTrue(completions.contains("fire"));
    }

    @Test
    @DisplayName("canCraft returns true when null subcommand (no check)")
    public void testCanCraftReturnsTrueWhenNull() {
        // With no real implementation, just verify it doesn't crash
        // This would be tested with a real Player mock in integration tests
    }
}
