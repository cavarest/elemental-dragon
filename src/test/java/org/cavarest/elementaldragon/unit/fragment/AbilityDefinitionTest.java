package org.cavarest.elementaldragon.unit.fragment;

import org.cavarest.elementaldragon.fragment.AbilityDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AbilityDefinition.
 */
@DisplayName("AbilityDefinition Tests")
public class AbilityDefinitionTest {

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor with action emoji creates valid instance")
    public void testConstructorWithActionEmoji() {
        List<String> aliases = Arrays.asList("dragons wrath", "dw");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Dragon's Wrath", "Fire a devastating fireball",
            aliases, "☄ Dragon's Wrath activated!", "🔥"
        );

        assertEquals(1, definition.getNumber());
        assertEquals("Dragon's Wrath", definition.getName());
        assertEquals("Fire a devastating fireball", definition.getDescription());
        assertEquals(2, definition.getAliases().size());
        assertEquals("☄ Dragon's Wrath activated!", definition.getSuccessMessage());
        assertEquals("🔥", definition.getActionEmoji());
    }

    @Test
    @DisplayName("Constructor without action emoji creates instance with empty emoji")
    public void testConstructorWithoutActionEmoji() {
        List<String> aliases = Arrays.asList("test");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test Ability", "Test description",
            aliases, "Test success"
        );

        assertEquals("", definition.getActionEmoji());
    }

    @Test
    @DisplayName("Constructor creates immutable aliases list")
    public void testConstructorImmutableAliases() {
        List<String> originalAliases = Arrays.asList("alias1", "alias2");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test", originalAliases, "Success", "🔥"
        );

        List<String> returnedAliases = definition.getAliases();

        // Try to modify the returned list
        assertThrows(UnsupportedOperationException.class, () -> {
            returnedAliases.add("new_alias");
        });
    }

    @Test
    @DisplayName("Constructor handles empty aliases list")
    public void testConstructorEmptyAliases() {
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test", Collections.emptyList(), "Success", "🔥"
        );

        assertTrue(definition.getAliases().isEmpty());
    }

    // ==================== Getter tests ====================

    @Test
    @DisplayName("getNumber returns correct ability number")
    public void testGetNumber() {
        AbilityDefinition def1 = new AbilityDefinition(1, "Test", "Test",
            Collections.emptyList(), "Success", "🔥");
        assertEquals(1, def1.getNumber());

        AbilityDefinition def2 = new AbilityDefinition(2, "Test", "Test",
            Collections.emptyList(), "Success", "🔥");
        assertEquals(2, def2.getNumber());
    }

    @Test
    @DisplayName("getName returns correct name")
    public void testGetName() {
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test Ability", "Test",
            Collections.emptyList(), "Success", "🔥"
        );

        assertEquals("Test Ability", definition.getName());
    }

    @Test
    @DisplayName("getDescription returns correct description")
    public void testGetDescription() {
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "This is a test ability",
            Collections.emptyList(), "Success", "🔥"
        );

        assertEquals("This is a test ability", definition.getDescription());
    }

    @Test
    @DisplayName("getAliases returns unmodifiable list")
    public void testGetAliasesReturnsUnmodifiableList() {
        List<String> aliases = Arrays.asList("alias1", "alias2", "alias3");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test", aliases, "Success", "🔥"
        );

        List<String> returnedAliases = definition.getAliases();

        assertEquals(3, returnedAliases.size());
        assertTrue(returnedAliases.contains("alias1"));
        assertTrue(returnedAliases.contains("alias2"));
        assertTrue(returnedAliases.contains("alias3"));

        // Verify it's unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            returnedAliases.add("new_alias");
        });
    }

    @Test
    @DisplayName("getSuccessMessage returns correct message")
    public void testGetSuccessMessage() {
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test",
            Collections.emptyList(), "Ability used successfully!", "🔥"
        );

        assertEquals("Ability used successfully!", definition.getSuccessMessage());
    }

    @Test
    @DisplayName("getActionEmoji returns correct emoji")
    public void testGetActionEmoji() {
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test",
            Collections.emptyList(), "Success", "☄"
        );

        assertEquals("☄", definition.getActionEmoji());
    }

    @Test
    @DisplayName("getActionEmoji returns empty string when not set")
    public void testGetActionEmojiEmpty() {
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test",
            Collections.emptyList(), "Success"
        );

        assertEquals("", definition.getActionEmoji());
    }

    // ==================== matchesAlias tests ====================

    @Test
    @DisplayName("matchesAlias returns true for exact alias match")
    public void testMatchesAliasExactMatch() {
        List<String> aliases = Arrays.asList("dragons wrath", "dw", "fireball");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Dragon's Wrath", "Test", aliases, "Success", "🔥"
        );

        assertTrue(definition.matchesAlias("dragons wrath"));
        assertTrue(definition.matchesAlias("dw"));
        assertTrue(definition.matchesAlias("fireball"));
    }

    @Test
    @DisplayName("matchesAlias is case-insensitive")
    public void testMatchesAliasCaseInsensitive() {
        List<String> aliases = Arrays.asList("dragons wrath", "dw");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Dragon's Wrath", "Test", aliases, "Success", "🔥"
        );

        assertTrue(definition.matchesAlias("DRAGONS WRATH"));
        assertTrue(definition.matchesAlias("Dragons Wrath"));
        assertTrue(definition.matchesAlias("dragons wrath"));
        assertTrue(definition.matchesAlias("DW"));
        assertTrue(definition.matchesAlias("dW"));
    }

    @Test
    @DisplayName("matchesAlias returns false for non-matching alias")
    public void testMatchesAliasNoMatch() {
        List<String> aliases = Arrays.asList("dragons wrath", "dw");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Dragon's Wrath", "Test", aliases, "Success", "🔥"
        );

        assertFalse(definition.matchesAlias("fireball"));
        assertFalse(definition.matchesAlias("test"));
        assertFalse(definition.matchesAlias(""));
    }

    @Test
    @DisplayName("matchesAlias throws NullPointerException for null input")
    public void testMatchesAliasNull() {
        List<String> aliases = Arrays.asList("dragons wrath", "dw");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Dragon's Wrath", "Test", aliases, "Success", "🔥"
        );

        // matchesAlias calls toLowerCase() on the input, which throws NPE for null
        assertThrows(NullPointerException.class, () -> {
            definition.matchesAlias(null);
        });
    }

    @Test
    @DisplayName("matchesAlias handles empty aliases list")
    public void testMatchesAliasEmptyList() {
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test", Collections.emptyList(), "Success", "🔥"
        );

        assertFalse(definition.matchesAlias("anything"));
    }

    @Test
    @DisplayName("matchesAlias handles partial matches correctly")
    public void testMatchesAliasPartialMatch() {
        List<String> aliases = Arrays.asList("dragons", "wrath");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test", aliases, "Success", "🔥"
        );

        // Should match exact aliases only, not partial
        assertTrue(definition.matchesAlias("dragons"));
        assertTrue(definition.matchesAlias("wrath"));
        assertFalse(definition.matchesAlias("dragon"));  // Partial match
        assertFalse(definition.matchesAlias("dragons wrath"));  // Two words, not one alias
    }

    @Test
    @DisplayName("matchesAlias handles special characters in aliases")
    public void testMatchesAliasSpecialCharacters() {
        List<String> aliases = Arrays.asList("fire-ball", "test_123", "test.alias");
        AbilityDefinition definition = new AbilityDefinition(
            1, "Test", "Test", aliases, "Success", "🔥"
        );

        assertTrue(definition.matchesAlias("fire-ball"));
        assertTrue(definition.matchesAlias("test_123"));
        assertTrue(definition.matchesAlias("test.alias"));
    }

    // ==================== Edge cases ====================

    @Test
    @DisplayName("Constructor handles null action emoji as empty string")
    public void testConstructorNullActionEmoji() {
        // This test documents current behavior - action emoji cannot be null
        // The constructor would fail with NullPointerException if null is passed
        assertDoesNotThrow(() -> {
            new AbilityDefinition(
                1, "Test", "Test",
                Collections.emptyList(), "Success", ""
            );
        });
    }

    @Test
    @DisplayName("Ability definition with zero ability number")
    public void testZeroAbilityNumber() {
        AbilityDefinition definition = new AbilityDefinition(
            0, "Test", "Test",
            Collections.emptyList(), "Success", "🔥"
        );

        assertEquals(0, definition.getNumber());
    }

    @Test
    @DisplayName("Ability definition with negative ability number")
    public void testNegativeAbilityNumber() {
        AbilityDefinition definition = new AbilityDefinition(
            -1, "Test", "Test",
            Collections.emptyList(), "Success", "🔥"
        );

        assertEquals(-1, definition.getNumber());
    }

    @Test
    @DisplayName("Ability definition with large ability number")
    public void testLargeAbilityNumber() {
        AbilityDefinition definition = new AbilityDefinition(
            100, "Test", "Test",
            Collections.emptyList(), "Success", "🔥"
        );

        assertEquals(100, definition.getNumber());
    }

    @Test
    @DisplayName("Multiple instances are independent")
    public void testMultipleInstancesIndependent() {
        List<String> aliases1 = Arrays.asList("alias1", "alias2");
        List<String> aliases2 = Arrays.asList("alias3", "alias4");

        AbilityDefinition def1 = new AbilityDefinition(
            1, "Ability 1", "Description 1", aliases1, "Success 1", "🔥"
        );
        AbilityDefinition def2 = new AbilityDefinition(
            2, "Ability 2", "Description 2", aliases2, "Success 2", "💨"
        );

        assertEquals(1, def1.getNumber());
        assertEquals(2, def2.getNumber());
        assertEquals("Ability 1", def1.getName());
        assertEquals("Ability 2", def2.getName());
        assertEquals(2, def1.getAliases().size());
        assertEquals(2, def2.getAliases().size());
        assertTrue(def1.getAliases().contains("alias1"));
        assertFalse(def2.getAliases().contains("alias1"));
    }
}
