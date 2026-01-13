package org.cavarest.elementaldragon.unit.command.display;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.cavarest.elementaldragon.command.display.GlobalCooldownFormatter;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalCooldownFormatter.
 */
@DisplayName("GlobalCooldownFormatter Tests")
public class GlobalCooldownFormatterTest {

    @Mock
    private CooldownManager cooldownManager;

    @Mock
    private CommandSender sender;

    private GlobalCooldownFormatter formatter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        formatter = new GlobalCooldownFormatter(cooldownManager);
    }

    @AfterEach
    public void tearDown() {
        formatter = null;
    }

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor initializes with cooldown manager")
    public void testConstructor() {
        assertNotNull(formatter);
    }

    // ==================== displayGlobalCooldowns tests ====================

    @Test
    @DisplayName("displayGlobalCooldowns queries cooldown manager")
    public void testDisplayGlobalCooldownsQueriesManager() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        formatter.displayGlobalCooldowns(sender);

        verify(cooldownManager).getAllGlobalCooldowns();
    }

    @Test
    @DisplayName("displayGlobalCooldowns sends header messages")
    public void testDisplayGlobalCooldownsHeader() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        formatter.displayGlobalCooldowns(sender);

        // Should send at least header messages
        verify(sender, atLeast(3)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayGlobalCooldowns displays all 5 elements")
    public void testDisplayGlobalCooldownsAllElements() {
        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put("lightning:1", 10);
        cooldowns.put("fire:1", 20);
        cooldowns.put("fire:2", 30);
        cooldowns.put("agile:1", 15);
        cooldowns.put("agile:2", 25);
        cooldowns.put("immortal:1", 40);
        cooldowns.put("immortal:2", 50);
        cooldowns.put("corrupt:1", 35);
        cooldowns.put("corrupt:2", 45);

        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(cooldowns);

        formatter.displayGlobalCooldowns(sender);

        // Header + 5 elements + 9 abilities + footer
        verify(sender, atLeast(15)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayGlobalCooldowns shows not configured for null cooldowns")
    public void testDisplayGlobalCooldownsNotConfigured() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundNotConfigured = false;
        for (Component msg : messageCaptor.getAllValues()) {
            if (msg.toString().contains("not configured")) {
                foundNotConfigured = true;
                break;
            }
        }
        assertTrue(foundNotConfigured, "Should show 'not configured' for missing cooldowns");
    }

    @Test
    @DisplayName("displayGlobalCooldowns formats seconds correctly")
    public void testDisplayGlobalCooldownsSecondsFormat() {
        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put("lightning:1", 30);

        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(cooldowns);

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundSeconds = false;
        for (Component msg : messageCaptor.getAllValues()) {
            if (msg.toString().contains("30s")) {
                foundSeconds = true;
                break;
            }
        }
        assertTrue(foundSeconds, "Should format seconds as '30s'");
    }

    @Test
    @DisplayName("displayGlobalCooldowns formats minutes and seconds correctly")
    public void testDisplayGlobalCooldownsMinutesSecondsFormat() {
        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put("lightning:1", 90);  // 1m 30s

        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(cooldowns);

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundMinutes = false;
        for (Component msg : messageCaptor.getAllValues()) {
            if (msg.toString().contains("1m") && msg.toString().contains("30s")) {
                foundMinutes = true;
                break;
            }
        }
        assertTrue(foundMinutes, "Should format 90 seconds as '1m 30s'");
    }

    @Test
    @DisplayName("displayGlobalCooldowns shows only minutes for exact minute values")
    public void testDisplayGlobalCooldownsExactMinutes() {
        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put("lightning:1", 120);  // 2m exactly

        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(cooldowns);

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundExactMinutes = false;
        for (Component msg : messageCaptor.getAllValues()) {
            if (msg.toString().contains("2m") && !msg.toString().contains("2m 0s")) {
                foundExactMinutes = true;
                break;
            }
        }
        assertTrue(foundExactMinutes, "Should format 120 seconds as '2m' (without 0s)");
    }

    @Test
    @DisplayName("displayGlobalCooldowns includes usage hint")
    public void testDisplayGlobalCooldownsUsageHint() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundHint = false;
        for (Component msg : messageCaptor.getAllValues()) {
            if (msg.toString().contains("setglobalcooldown")) {
                foundHint = true;
                break;
            }
        }
        assertTrue(foundHint, "Should include usage hint");
    }

    @Test
    @DisplayName("displayGlobalCooldowns includes element headers with emojis")
    public void testDisplayGlobalCooldownsElementHeaders() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        String allMessages = "";
        for (Component msg : messageCaptor.getAllValues()) {
            allMessages += msg.toString();
        }

        // Check for element emojis/names
        assertTrue(allMessages.contains("Lightning") || allMessages.contains("⚡"),
            "Should include Lightning element");
        assertTrue(allMessages.contains("Fire") || allMessages.contains("🔥"),
            "Should include Fire element");
    }

    @Test
    @DisplayName("displayGlobalCooldowns handles empty map gracefully")
    public void testDisplayGlobalCooldownsEmptyMap() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        assertDoesNotThrow(() -> formatter.displayGlobalCooldowns(sender));
        verify(sender, atLeast(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayGlobalCooldowns handles very large cooldown values")
    public void testDisplayGlobalCooldownsLargeValues() {
        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put("lightning:1", 7200);  // 2 hours

        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(cooldowns);

        assertDoesNotThrow(() -> formatter.displayGlobalCooldowns(sender));
        verify(sender, atLeast(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayGlobalCooldowns handles zero cooldown values")
    public void testDisplayGlobalCooldownsZeroValue() {
        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put("lightning:1", 0);

        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(cooldowns);

        assertDoesNotThrow(() -> formatter.displayGlobalCooldowns(sender));
        verify(sender, atLeast(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayGlobalCooldowns sends messages to correct sender")
    public void testDisplayGlobalCooldownsCorrectSender() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        CommandSender differentSender = mock(CommandSender.class);
        formatter.displayGlobalCooldowns(differentSender);

        verify(differentSender, atLeast(1)).sendMessage(any(Component.class));
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayGlobalCooldowns formats under 60 seconds without minutes")
    public void testDisplayGlobalCooldownsUnderMinute() {
        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put("lightning:1", 45);

        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(cooldowns);

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundSecondsOnly = false;
        for (Component msg : messageCaptor.getAllValues()) {
            String content = msg.toString();
            if (content.contains("45s") && !content.contains("45m")) {
                // Should show "45s" not "45m" (but other messages may contain "m" from element names)
                foundSecondsOnly = true;
                break;
            }
        }
        assertTrue(foundSecondsOnly, "Should format 45 seconds as '45s' without minutes");
    }

    @Test
    @DisplayName("displayGlobalCooldowns shows all ability names")
    public void testDisplayGlobalCooldownsAbilityNames() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        String allMessages = "";
        for (Component msg : messageCaptor.getAllValues()) {
            allMessages += msg.toString();
        }

        // Check for ability names
        assertTrue(allMessages.contains("Ability 1"),
            "Should include Ability 1");
        assertTrue(allMessages.contains("Ability 2"),
            "Should include Ability 2");
    }

    @Test
    @DisplayName("displayGlobalCooldowns includes decorative border")
    public void testDisplayGlobalCooldownsBorder() {
        when(cooldownManager.getAllGlobalCooldowns()).thenReturn(new HashMap<>());

        formatter.displayGlobalCooldowns(sender);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundBorder = false;
        for (Component msg : messageCaptor.getAllValues()) {
            if (msg.toString().contains("═")) {
                foundBorder = true;
                break;
            }
        }
        assertTrue(foundBorder, "Should include decorative border");
    }
}
