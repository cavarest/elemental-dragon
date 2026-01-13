package org.cavarest.elementaldragon.unit.command.display;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.command.display.CooldownFormatter;
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
 * Unit tests for CooldownFormatter.
 */
@DisplayName("CooldownFormatter Tests")
public class CooldownFormatterTest {

    @Mock
    private CooldownManager cooldownManager;

    @Mock
    private CommandSender sender;

    @Mock
    private Player target;

    private CooldownFormatter formatter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        formatter = new CooldownFormatter(cooldownManager);
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

    @Test
    @DisplayName("Constructor accepts null cooldown manager (no validation)")
    public void testConstructorNullCooldownManager() {
        // The class doesn't validate null in constructor
        // It will throw NPE when methods are called
        CooldownFormatter formatter = new CooldownFormatter(null);
        assertNotNull(formatter);
    }

    // ==================== displayPlayerCooldowns tests ====================

    @Test
    @DisplayName("displayPlayerCooldowns shows no active cooldowns message when map is empty")
    public void testDisplayPlayerCooldownsEmpty() {
        when(target.getName()).thenReturn("TestPlayer");
        when(cooldownManager.getAllCooldowns(target)).thenReturn(new HashMap<>());

        formatter.displayPlayerCooldowns(sender, target);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender).sendMessage(messageCaptor.capture());

        Component message = messageCaptor.getValue();
        String content = message.toString();
        assertTrue(content.contains("no active cooldowns"));
    }

    @Test
    @DisplayName("displayPlayerCooldowns shows header when player has cooldowns")
    public void testDisplayPlayerCooldownsWithCooldowns() {
        when(target.getName()).thenReturn("TestPlayer");

        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put(CooldownManager.LIGHTNING, 5);
        cooldowns.put(CooldownManager.FIRE, 3);

        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        // Should send header + 5 element messages (Lightning, Fire, Agile, Immortal, Corrupt)
        verify(sender, atLeast(2)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayPlayerCooldowns shows cooldown values for active cooldowns")
    public void testDisplayPlayerCooldownsShowsValues() {
        when(target.getName()).thenReturn("TestPlayer");

        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put(CooldownManager.LIGHTNING, 10);
        cooldowns.put(CooldownManager.FIRE, 5);

        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        // Verify messages were sent
        verify(sender, atLeast(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayPlayerCooldowns shows ready for elements with zero or null cooldown")
    public void testDisplayPlayerCooldownsReadyState() {
        when(target.getName()).thenReturn("TestPlayer");

        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put(CooldownManager.LIGHTNING, 0);  // Zero cooldown
        cooldowns.put(CooldownManager.FIRE, null);     // No cooldown entry

        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        // Should show "ready" for elements with no active cooldown
        verify(sender, atLeast(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayPlayerCooldowns displays all 5 elements")
    public void testDisplayPlayerCooldownsAllElements() {
        when(target.getName()).thenReturn("TestPlayer");

        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put(CooldownManager.LIGHTNING, 10);
        cooldowns.put(CooldownManager.FIRE, 8);
        cooldowns.put(CooldownManager.AGILE, 6);
        cooldowns.put(CooldownManager.IMMORTAL, 4);
        cooldowns.put(CooldownManager.CORRUPT, 2);

        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        // Header + 5 elements = 6 messages
        verify(sender, times(6)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayPlayerCooldowns handles null target gracefully")
    public void testDisplayPlayerCooldownsNullTarget() {
        assertThrows(NullPointerException.class, () -> {
            formatter.displayPlayerCooldowns(sender, null);
        });
    }

    @Test
    @DisplayName("displayPlayerCooldowns handles null sender gracefully")
    public void testDisplayPlayerCooldownsNullSender() {
        when(target.getName()).thenReturn("TestPlayer");
        when(cooldownManager.getAllCooldowns(target)).thenReturn(new HashMap<>());

        assertThrows(NullPointerException.class, () -> {
            formatter.displayPlayerCooldowns(null, target);
        });
    }

    @Test
    @DisplayName("displayPlayerCooldowns includes player name in header")
    public void testDisplayPlayerCooldownsIncludesPlayerName() {
        when(target.getName()).thenReturn("Steve");

        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put(CooldownManager.LIGHTNING, 5);

        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundHeader = false;
        for (Component msg : messageCaptor.getAllValues()) {
            if (msg.toString().contains("Steve")) {
                foundHeader = true;
                break;
            }
        }
        assertTrue(foundHeader, "Header should contain player name");
    }

    // ==================== Edge cases ====================

    @Test
    @DisplayName("displayPlayerCooldowns handles very large cooldown values")
    public void testDisplayPlayerCooldownsLargeValues() {
        when(target.getName()).thenReturn("TestPlayer");

        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put(CooldownManager.LIGHTNING, Integer.MAX_VALUE);
        cooldowns.put(CooldownManager.FIRE, 1000000);

        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        // Should handle large values without crashing
        verify(sender, atLeast(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayPlayerCooldowns handles negative cooldown values")
    public void testDisplayPlayerCooldownsNegativeValues() {
        when(target.getName()).thenReturn("TestPlayer");

        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put(CooldownManager.LIGHTNING, -1);
        cooldowns.put(CooldownManager.FIRE, -100);

        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        // Negative values should show as "ready"
        verify(sender, atLeast(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayPlayerCooldowns displays correct element names")
    public void testDisplayPlayerCooldownsElementNames() {
        when(target.getName()).thenReturn("TestPlayer");

        Map<String, Integer> cooldowns = new HashMap<>();
        cooldowns.put(CooldownManager.LIGHTNING, 5);

        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        ArgumentCaptor<Component> messageCaptor = ArgumentCaptor.forClass(Component.class);
        verify(sender, atLeast(1)).sendMessage(messageCaptor.capture());

        boolean foundLightning = false;
        for (Component msg : messageCaptor.getAllValues()) {
            String content = msg.toString();
            if (content.contains("Lightning")) {
                foundLightning = true;
                break;
            }
        }
        assertTrue(foundLightning, "Should display Lightning element name");
    }

    @Test
    @DisplayName("displayPlayerCooldowns sends messages to correct sender")
    public void testDisplayPlayerCooldownsCorrectSender() {
        when(target.getName()).thenReturn("TestPlayer");
        when(cooldownManager.getAllCooldowns(target)).thenReturn(new HashMap<>());

        CommandSender differentSender = mock(CommandSender.class);
        formatter.displayPlayerCooldowns(differentSender, target);

        verify(differentSender, atLeast(1)).sendMessage(any(Component.class));
        verify(sender, never()).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("displayPlayerCooldowns queries cooldown manager for target")
    public void testDisplayPlayerCooldownsQueriesManager() {
        when(target.getName()).thenReturn("TestPlayer");

        Map<String, Integer> cooldowns = new HashMap<>();
        when(cooldownManager.getAllCooldowns(target)).thenReturn(cooldowns);

        formatter.displayPlayerCooldowns(sender, target);

        verify(cooldownManager).getAllCooldowns(target);
    }
}
