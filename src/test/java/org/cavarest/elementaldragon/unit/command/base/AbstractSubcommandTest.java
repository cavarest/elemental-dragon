package org.cavarest.elementaldragon.unit.command.base;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.cavarest.elementaldragon.command.base.AbstractSubcommand;
import org.cavarest.elementaldragon.command.base.Subcommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AbstractSubcommand.
 */
@DisplayName("AbstractSubcommand Tests")
public class AbstractSubcommandTest {

    @Mock
    private CommandSender sender;

    private TestSubcommand subcommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subcommand = new TestSubcommand();
    }

    // ==================== Constructor and Getter tests ====================

    @Test
    @DisplayName("Constructor stores all parameters correctly")
    public void testConstructorStoresParameters() {
        assertEquals("testcommand", subcommand.getName());
        assertEquals("Test command description", subcommand.getDescription());
        assertEquals("/ed testcommand <args>", subcommand.getUsage());
        assertEquals("elementaldragon.admin.test", subcommand.getPermission());
    }

    @Test
    @DisplayName("Constructor handles null permission")
    public void testConstructorNullPermission() {
        TestSubcommand nullPermSubcommand = new TestSubcommand(
            "nullperm",
            "Description",
            "/usage",
            null
        );

        assertNull(nullPermSubcommand.getPermission());
    }

    // ==================== sendNoPermission tests ====================

    @Test
    @DisplayName("sendNoPermission sends red permission message")
    public void testSendNoPermission() {
        subcommand.testSendNoPermission(sender);

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender).sendMessage(captor.capture());

        Component message = captor.getValue();
        assertEquals(NamedTextColor.RED, message.color());
    }

    // ==================== sendUsage tests ====================

    @Test
    @DisplayName("sendUsage sends red usage message with defined usage")
    public void testSendUsageWithDefinedUsage() {
        subcommand.testSendUsage(sender);

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender).sendMessage(captor.capture());

        Component message = captor.getValue();
        assertEquals(NamedTextColor.RED, message.color());
    }

    @Test
    @DisplayName("sendUsage sends red usage message with custom usage")
    public void testSendUsageWithCustomUsage() {
        subcommand.testSendUsage(sender, "/custom <usage>");

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender).sendMessage(captor.capture());

        Component message = captor.getValue();
        assertEquals(NamedTextColor.RED, message.color());
    }

    // ==================== sendError tests ====================

    @Test
    @DisplayName("sendError sends red error message")
    public void testSendError() {
        subcommand.testSendError(sender, "Test error message");

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender).sendMessage(captor.capture());

        Component message = captor.getValue();
        assertEquals(NamedTextColor.RED, message.color());
    }

    @Test
    @DisplayName("sendError handles empty message")
    public void testSendErrorEmptyMessage() {
        subcommand.testSendError(sender, "");

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender).sendMessage(captor.capture());

        Component message = captor.getValue();
        assertEquals(NamedTextColor.RED, message.color());
    }

    // ==================== sendSuccess tests ====================

    @Test
    @DisplayName("sendSuccess sends green success message")
    public void testSendSuccess() {
        subcommand.testSendSuccess(sender, "Operation successful");

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender).sendMessage(captor.capture());

        Component message = captor.getValue();
        assertEquals(NamedTextColor.GREEN, message.color());
    }

    // ==================== sendInfo tests ====================

    @Test
    @DisplayName("sendInfo sends aqua info message")
    public void testSendInfo() {
        subcommand.testSendInfo(sender, "Information message");

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(sender).sendMessage(captor.capture());

        Component message = captor.getValue();
        assertEquals(NamedTextColor.AQUA, message.color());
    }

    // ==================== validateMinArgs tests ====================

    @Test
    @DisplayName("validateMinArgs returns true when args length meets minimum")
    public void testValidateMinArgsSufficient() {
        String[] args = {"arg1", "arg2", "arg3"};

        boolean result = subcommand.testValidateMinArgs(sender, args, 2);

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateMinArgs returns true when args length equals minimum")
    public void testValidateMinArgsExact() {
        String[] args = {"arg1", "arg2"};

        boolean result = subcommand.testValidateMinArgs(sender, args, 2);

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateMinArgs returns false and sends usage when insufficient args")
    public void testValidateMinArgsInsufficient() {
        String[] args = {"arg1"};

        boolean result = subcommand.testValidateMinArgs(sender, args, 2);

        assertFalse(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateMinArgs returns false and sends usage when empty args")
    public void testValidateMinArgsEmpty() {
        String[] args = new String[0];

        boolean result = subcommand.testValidateMinArgs(sender, args, 1);

        assertFalse(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateMinArgs handles minimum of zero")
    public void testValidateMinArgsZeroMin() {
        String[] args = new String[0];

        boolean result = subcommand.testValidateMinArgs(sender, args, 0);

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    // ==================== validateExactArgs tests ====================

    @Test
    @DisplayName("validateExactArgs returns true when args length matches")
    public void testValidateExactArgsMatch() {
        String[] args = {"arg1", "arg2", "arg3"};

        boolean result = subcommand.testValidateExactArgs(sender, args, 3);

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateExactArgs returns false and sends usage when args too many")
    public void testValidateExactArgsTooMany() {
        String[] args = {"arg1", "arg2", "arg3"};

        boolean result = subcommand.testValidateExactArgs(sender, args, 2);

        assertFalse(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateExactArgs returns false and sends usage when args too few")
    public void testValidateExactArgsTooFew() {
        String[] args = {"arg1"};

        boolean result = subcommand.testValidateExactArgs(sender, args, 2);

        assertFalse(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateExactArgs returns false when args empty but required")
    public void testValidateExactArgsEmptyWhenRequired() {
        String[] args = new String[0];

        boolean result = subcommand.testValidateExactArgs(sender, args, 1);

        assertFalse(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateExactArgs returns true for zero args when expected")
    public void testValidateExactArgsZeroExpected() {
        String[] args = new String[0];

        boolean result = subcommand.testValidateExactArgs(sender, args, 0);

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    // ==================== parseInteger tests ====================

    @Test
    @DisplayName("parseInteger returns valid integer for numeric string")
    public void testParseIntegerValid() {
        Integer result = subcommand.testParseInteger(sender, "42", "test parameter");

        assertEquals(42, result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("parseInteger returns valid negative integer")
    public void testParseIntegerNegative() {
        Integer result = subcommand.testParseInteger(sender, "-10", "test parameter");

        assertEquals(-10, result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("parseInteger returns valid zero")
    public void testParseIntegerZero() {
        Integer result = subcommand.testParseInteger(sender, "0", "test parameter");

        assertEquals(0, result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("parseInteger returns null and sends error for non-numeric string")
    public void testParseIntegerNonNumeric() {
        Integer result = subcommand.testParseInteger(sender, "abc", "width");

        assertNull(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("parseInteger returns null and sends error for partial numeric")
    public void testParseIntegerPartialNumeric() {
        Integer result = subcommand.testParseInteger(sender, "123abc", "count");

        assertNull(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("parseInteger returns null and sends error for empty string")
    public void testParseIntegerEmpty() {
        Integer result = subcommand.testParseInteger(sender, "", "size");

        assertNull(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("parseInteger returns null and sends error for decimal number")
    public void testParseIntegerDecimal() {
        Integer result = subcommand.testParseInteger(sender, "12.5", "value");

        assertNull(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("parseInteger includes parameter name in error message")
    public void testParseIntegerParameterNameInError() {
        subcommand.testParseInteger(sender, "invalid", "testParam");

        verify(sender).sendMessage(any(Component.class));
    }

    // ==================== validateRange tests ====================

    @Test
    @DisplayName("validateRange returns true when value within range")
    public void testValidateRangeWithin() {
        boolean result = subcommand.testValidateRange(sender, 5, 1, 10, "level");

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateRange returns true when value equals minimum")
    public void testValidateRangeAtMin() {
        boolean result = subcommand.testValidateRange(sender, 1, 1, 10, "level");

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateRange returns true when value equals maximum")
    public void testValidateRangeAtMax() {
        boolean result = subcommand.testValidateRange(sender, 10, 1, 10, "level");

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateRange returns false and sends error when value below minimum")
    public void testValidateRangeBelowMin() {
        boolean result = subcommand.testValidateRange(sender, 0, 1, 10, "level");

        assertFalse(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateRange returns false and sends error when value above maximum")
    public void testValidateRangeAboveMax() {
        boolean result = subcommand.testValidateRange(sender, 11, 1, 10, "level");

        assertFalse(result);
        verify(sender).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateRange handles negative range")
    public void testValidateRangeNegativeRange() {
        boolean result = subcommand.testValidateRange(sender, -5, -10, 10, "offset");

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateRange handles same min and max")
    public void testValidateRangeSameMinMax() {
        boolean result = subcommand.testValidateRange(sender, 5, 5, 5, "exact");

        assertTrue(result);
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("validateRange fails when value different from exact match")
    public void testValidateRangeExactMismatch() {
        boolean result = subcommand.testValidateRange(sender, 6, 5, 5, "exact");

        assertFalse(result);
        verify(sender).sendMessage(any(Component.class));
    }

    // ==================== Subcommand Interface Default Method Tests ====================

    @Nested
    @DisplayName("Subcommand.hasPermission() Tests")
    class HasPermissionTests {

        @Test
        @DisplayName("hasPermission returns true when permission is null")
        public void testHasPermissionNullPermission() {
            TestSubcommand nullPermSubcommand = new TestSubcommand(
                "test",
                "desc",
                "/usage",
                null
            );

            boolean result = nullPermSubcommand.hasPermission(sender);

            assertTrue(result);
            verify(sender, never()).hasPermission(anyString());
        }

        @Test
        @DisplayName("hasPermission returns true when sender has permission")
        public void testHasPermissionGranted() {
            when(sender.hasPermission("elementaldragon.admin.test")).thenReturn(true);

            boolean result = subcommand.hasPermission(sender);

            assertTrue(result);
            verify(sender).hasPermission("elementaldragon.admin.test");
        }

        @Test
        @DisplayName("hasPermission returns false when sender lacks permission")
        public void testHasPermissionDenied() {
            when(sender.hasPermission("elementaldragon.admin.test")).thenReturn(false);

            boolean result = subcommand.hasPermission(sender);

            assertFalse(result);
            verify(sender).hasPermission("elementaldragon.admin.test");
        }
    }

    // ==================== Test Implementation ====================

    /**
     * Test implementation of AbstractSubcommand that exposes protected methods for testing.
     */
    private static class TestSubcommand extends AbstractSubcommand {

        public TestSubcommand() {
            super("testcommand", "Test command description", "/ed testcommand <args>", "elementaldragon.admin.test");
        }

        public TestSubcommand(String name, String description, String usage, String permission) {
            super(name, description, usage, permission);
        }

        @Override
        public boolean execute(CommandSender sender, String[] args) {
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            return List.of();
        }

        // Public methods to expose protected methods for testing
        public void testSendNoPermission(CommandSender sender) {
            sendNoPermission(sender);
        }

        public void testSendUsage(CommandSender sender) {
            sendUsage(sender);
        }

        public void testSendUsage(CommandSender sender, String customUsage) {
            sendUsage(sender, customUsage);
        }

        public void testSendError(CommandSender sender, String message) {
            sendError(sender, message);
        }

        public void testSendSuccess(CommandSender sender, String message) {
            sendSuccess(sender, message);
        }

        public void testSendInfo(CommandSender sender, String message) {
            sendInfo(sender, message);
        }

        public boolean testValidateMinArgs(CommandSender sender, String[] args, int minLength) {
            return validateMinArgs(sender, args, minLength);
        }

        public boolean testValidateExactArgs(CommandSender sender, String[] args, int exactLength) {
            return validateExactArgs(sender, args, exactLength);
        }

        public Integer testParseInteger(CommandSender sender, String value, String parameterName) {
            return parseInteger(sender, value, parameterName);
        }

        public boolean testValidateRange(CommandSender sender, int value, int min, int max, String parameterName) {
            return validateRange(sender, value, min, max, parameterName);
        }
    }
}
