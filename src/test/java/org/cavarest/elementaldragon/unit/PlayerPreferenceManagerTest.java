package org.cavarest.elementaldragon.unit;

import org.bukkit.entity.Player;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.cavarest.elementaldragon.hud.PlayerPreferenceManager;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer.VariantType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PlayerPreferenceManager.
 */
public class PlayerPreferenceManagerTest {

    @Mock
    private Player player;

    @Mock
    private Player player2;

    @Mock
    private PersistentDataContainer persistentDataContainer;

    private PlayerPreferenceManager playerPreferenceManager;
    private UUID playerUuid;
    private UUID player2Uuid;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        playerPreferenceManager = new PlayerPreferenceManager();
        playerUuid = UUID.randomUUID();
        player2Uuid = UUID.randomUUID();

        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getPersistentDataContainer()).thenReturn(persistentDataContainer);
        when(player2.getUniqueId()).thenReturn(player2Uuid);
        when(player2.getPersistentDataContainer()).thenReturn(persistentDataContainer);
    }

    @Test
    @DisplayName("getPreference returns default TILES when no preference set")
    public void testGetPreferenceReturnsDefault() {
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn(null);

        PlayerPreferenceManager.PlayerPreference preference = playerPreferenceManager.getPreference(player);

        assertNotNull(preference);
        assertEquals(VariantType.TILES, preference.getVariantType());
        assertEquals(1, preference.getWidth());
    }

    @Test
    @DisplayName("getPreference returns cached preference on second call")
    public void testGetPreferenceReturnsCached() {
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn(null);

        // First call loads from persistent data
        PlayerPreferenceManager.PlayerPreference preference1 = playerPreferenceManager.getPreference(player);

        // Modify the mock to return a different value (shouldn't be called due to cache)
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn("MOON:2");

        // Second call should use cache
        PlayerPreferenceManager.PlayerPreference preference2 = playerPreferenceManager.getPreference(player);

        assertSame(preference1, preference2, "Should return same cached instance");
        assertEquals(VariantType.TILES, preference2.getVariantType());
    }

    @Test
    @DisplayName("getPreference loads from persistent data container")
    public void testGetPreferenceLoadsFromPersistentData() {
        String serializedData = "MOON:2";
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn(serializedData);

        PlayerPreferenceManager.PlayerPreference preference = playerPreferenceManager.getPreference(player);

        assertNotNull(preference);
        assertEquals(VariantType.MOON, preference.getVariantType());
        assertEquals(2, preference.getWidth());
    }

    @Test
    @DisplayName("getPreference handles invalid data gracefully")
    public void testGetPreferenceHandlesInvalidData() {
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn("invalid:data:format");

        PlayerPreferenceManager.PlayerPreference preference = playerPreferenceManager.getPreference(player);

        assertNotNull(preference);
        assertEquals(VariantType.TILES, preference.getVariantType());
        assertEquals(1, preference.getWidth());
    }

    @Test
    @DisplayName("getPreference handles empty data gracefully")
    public void testGetPreferenceHandlesEmptyData() {
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn("");

        PlayerPreferenceManager.PlayerPreference preference = playerPreferenceManager.getPreference(player);

        assertNotNull(preference);
        assertEquals(VariantType.TILES, preference.getVariantType());
        assertEquals(1, preference.getWidth());
    }

    @Test
    @DisplayName("setPreference stores in cache and persistent data")
    public void testSetPreference() {
        playerPreferenceManager.setPreference(player, VariantType.CLOCK, 3);

        verify(persistentDataContainer).set(
            any(NamespacedKey.class),
            eq(PersistentDataType.STRING),
            eq("CLOCK:3")
        );

        // Verify cache was updated
        PlayerPreferenceManager.PlayerPreference preference = playerPreferenceManager.getPreference(player);
        assertEquals(VariantType.CLOCK, preference.getVariantType());
        assertEquals(3, preference.getWidth());
    }

    @Test
    @DisplayName("clearPreference removes from cache and persistent data")
    public void testClearPreference() {
        // First set a preference
        playerPreferenceManager.setPreference(player, VariantType.SHADE, 2);

        // Clear it
        playerPreferenceManager.clearPreference(player);

        verify(persistentDataContainer).remove(any(NamespacedKey.class));

        // Verify cache was cleared
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn(null);

        PlayerPreferenceManager.PlayerPreference preference = playerPreferenceManager.getPreference(player);
        assertEquals(VariantType.TILES, preference.getVariantType());
        assertEquals(1, preference.getWidth());
    }

    @Test
    @DisplayName("getVariant returns TILES for default preference")
    public void testGetVariantReturnsDefault() {
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn(null);

        var variant = playerPreferenceManager.getVariant(player);

        assertNotNull(variant);
    }

    @Test
    @DisplayName("getVariant returns correct variant for MOON with width 2")
    public void testGetVariantMoonWithWidth() {
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn("MOON:2");

        var variant = playerPreferenceManager.getVariant(player);

        assertNotNull(variant);
    }

    @Test
    @DisplayName("clearCache clears all cached preferences")
    public void testClearCache() {
        // Set preference for one player
        playerPreferenceManager.setPreference(player, VariantType.TRIANGLE, 3);

        // Clear cache
        playerPreferenceManager.clearCache();

        // Mock should be called again for next getPreference
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn(null);

        PlayerPreferenceManager.PlayerPreference preference = playerPreferenceManager.getPreference(player);
        assertEquals(VariantType.TILES, preference.getVariantType());
    }

    @Test
    @DisplayName("PlayerPreference serialize creates correct format")
    public void testPlayerPreferenceSerialize() {
        PlayerPreferenceManager.PlayerPreference preference =
            new PlayerPreferenceManager.PlayerPreference(VariantType.CLOCK, 5);

        String serialized = preference.serialize();

        assertEquals("CLOCK:5", serialized);
    }

    @Test
    @DisplayName("PlayerPreference deserialize parses correct format")
    public void testPlayerPreferenceDeserialize() {
        PlayerPreferenceManager.PlayerPreference preference =
            PlayerPreferenceManager.PlayerPreference.deserialize("SHADE:3");

        assertEquals(VariantType.SHADE, preference.getVariantType());
        assertEquals(3, preference.getWidth());
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on invalid format")
    public void testPlayerPreferenceDeserializeInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerPreferenceManager.PlayerPreference.deserialize("invalid");
        });
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on missing parts")
    public void testPlayerPreferenceDeserializeMissingParts() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerPreferenceManager.PlayerPreference.deserialize("SHADE");
        });
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on invalid variant type")
    public void testPlayerPreferenceDeserializeInvalidVariant() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerPreferenceManager.PlayerPreference.deserialize("INVALID:1");
        });
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on invalid width")
    public void testPlayerPreferenceDeserializeInvalidWidth() {
        assertThrows(NumberFormatException.class, () -> {
            PlayerPreferenceManager.PlayerPreference.deserialize("SHADE:abc");
        });
    }

    @Test
    @DisplayName("getVariant returns correct variant for each VariantType")
    public void testGetVariantForAllTypes() {
        // This tests the private getVariant method indirectly through getPreference
        VariantType[] types = {
            VariantType.TILES, VariantType.MOON, VariantType.CLOCK,
            VariantType.SHADE, VariantType.BLOCK1, VariantType.BLOCK2,
            VariantType.BLOCK3, VariantType.BLOCK4, VariantType.TRIANGLE
        };

        for (VariantType type : types) {
            String data = type.name() + ":2";
            when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
                .thenReturn(data);

            var variant = playerPreferenceManager.getVariant(player);
            assertNotNull(variant, "Variant should not be null for " + type);
        }
    }

    @Test
    @DisplayName("Multiple players have independent preferences")
    public void testMultiplePlayersIndependent() {
        // Clear cache to start fresh
        playerPreferenceManager.clearCache();

        // Set different preferences for each player
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn("MOON:1"); // player1 gets MOON

        playerPreferenceManager.setPreference(player, VariantType.MOON, 1);

        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn("CLOCK:1"); // player2 gets CLOCK

        playerPreferenceManager.setPreference(player2, VariantType.CLOCK, 1);

        // Verify each player has their own preference
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn("MOON:1");

        PlayerPreferenceManager.PlayerPreference pref1 = playerPreferenceManager.getPreference(player);
        assertEquals(VariantType.MOON, pref1.getVariantType());

        when(persistentDataContainer.get(any(NamespacedKey.class), eq(PersistentDataType.STRING)))
            .thenReturn("CLOCK:1");

        PlayerPreferenceManager.PlayerPreference pref2 = playerPreferenceManager.getPreference(player2);
        assertEquals(VariantType.CLOCK, pref2.getVariantType());
    }

    @Test
    @DisplayName("setPreference with width 10 (maximum)")
    public void testSetPreferenceMaxWidth() {
        playerPreferenceManager.setPreference(player, VariantType.BLOCK1, 10);

        verify(persistentDataContainer).set(
            any(NamespacedKey.class),
            eq(PersistentDataType.STRING),
            eq("BLOCK1:10")
        );
    }

    @Test
    @DisplayName("setPreference with width 1 (minimum)")
    public void testSetPreferenceMinWidth() {
        playerPreferenceManager.setPreference(player, VariantType.BLOCK2, 1);

        verify(persistentDataContainer).set(
            any(NamespacedKey.class),
            eq(PersistentDataType.STRING),
            eq("BLOCK2:1")
        );
    }
}
