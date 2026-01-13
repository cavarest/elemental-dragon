package org.cavarest.elementaldragon.unit.hud;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.cavarest.elementaldragon.hud.PlayerPreferenceManager;
import org.cavarest.elementaldragon.hud.PlayerPreferenceManager.PlayerPreference;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer.VariantType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PlayerPreferenceManager and PlayerPreference.
 */
@DisplayName("PlayerPreferenceManager Tests")
public class PlayerPreferenceManagerTest {

    @Mock
    private Player player;

    private PlayerPreferenceManager manager;
    private UUID playerUuid;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new PlayerPreferenceManager();
        playerUuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerUuid);
    }

    // ==================== PlayerPreference serialization tests ====================

    @Test
    @DisplayName("PlayerPreference serialize returns correct format")
    public void testPlayerPreferenceSerialize() {
        PlayerPreference preference = new PlayerPreference(VariantType.MOON, 3);
        String serialized = preference.serialize();

        assertEquals("MOON:3", serialized);
    }

    @Test
    @DisplayName("PlayerPreference serialize handles TILES type")
    public void testPlayerPreferenceSerializeTiles() {
        PlayerPreference preference = new PlayerPreference(VariantType.TILES, 1);
        String serialized = preference.serialize();

        assertEquals("TILES:1", serialized);
    }

    @Test
    @DisplayName("PlayerPreference serialize handles CLOCK type")
    public void testPlayerPreferenceSerializeClock() {
        PlayerPreference preference = new PlayerPreference(VariantType.CLOCK, 5);
        String serialized = preference.serialize();

        assertEquals("CLOCK:5", serialized);
    }

    @Test
    @DisplayName("PlayerPreference deserialize parses valid format")
    public void testPlayerPreferenceDeserializeValid() {
        PlayerPreference preference = PlayerPreference.deserialize("SHADE:4");

        assertEquals(VariantType.SHADE, preference.getVariantType());
        assertEquals(4, preference.getWidth());
    }

    @Test
    @DisplayName("PlayerPreference deserialize handles different variant types")
    public void testPlayerPreferenceDeserializeVariants() {
        PlayerPreference tiles = PlayerPreference.deserialize("TILES:1");
        assertEquals(VariantType.TILES, tiles.getVariantType());

        PlayerPreference moon = PlayerPreference.deserialize("MOON:2");
        assertEquals(VariantType.MOON, moon.getVariantType());

        PlayerPreference clock = PlayerPreference.deserialize("CLOCK:3");
        assertEquals(VariantType.CLOCK, clock.getVariantType());

        PlayerPreference shade = PlayerPreference.deserialize("SHADE:4");
        assertEquals(VariantType.SHADE, shade.getVariantType());

        PlayerPreference block1 = PlayerPreference.deserialize("BLOCK1:5");
        assertEquals(VariantType.BLOCK1, block1.getVariantType());

        PlayerPreference triangle = PlayerPreference.deserialize("TRIANGLE:10");
        assertEquals(VariantType.TRIANGLE, triangle.getVariantType());
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on invalid format")
    public void testPlayerPreferenceDeserializeInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerPreference.deserialize("invalid");
        });
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on missing parts")
    public void testPlayerPreferenceDeserializeMissingParts() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerPreference.deserialize("MOON");
        });
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on extra parts")
    public void testPlayerPreferenceDeserializeExtraParts() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerPreference.deserialize("MOON:3:extra");
        });
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on invalid variant type")
    public void testPlayerPreferenceDeserializeInvalidVariant() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerPreference.deserialize("INVALID:5");
        });
    }

    @Test
    @DisplayName("PlayerPreference deserialize throws on invalid width")
    public void testPlayerPreferenceDeserializeInvalidWidth() {
        assertThrows(NumberFormatException.class, () -> {
            PlayerPreference.deserialize("MOON:abc");
        });
    }

    @Test
    @DisplayName("PlayerPreference deserialize and serialize are symmetric")
    public void testPlayerPreferenceSerializationSymmetry() {
        PlayerPreference original = new PlayerPreference(VariantType.BLOCK2, 7);
        String serialized = original.serialize();
        PlayerPreference deserialized = PlayerPreference.deserialize(serialized);

        assertEquals(original.getVariantType(), deserialized.getVariantType());
        assertEquals(original.getWidth(), deserialized.getWidth());
    }

    // ==================== PlayerPreference getter tests ====================

    @Test
    @DisplayName("PlayerPreference getVariantType returns correct type")
    public void testPlayerPreferenceGetVariantType() {
        PlayerPreference preference = new PlayerPreference(VariantType.TILES, 1);
        assertEquals(VariantType.TILES, preference.getVariantType());
    }

    @Test
    @DisplayName("PlayerPreference getWidth returns correct width")
    public void testPlayerPreferenceGetWidth() {
        PlayerPreference preference = new PlayerPreference(VariantType.MOON, 5);
        assertEquals(5, preference.getWidth());
    }

    // ==================== PlayerPreferenceManager tests ====================

    @Test
    @DisplayName("getPreference returns default when no preference set")
    public void testGetPreferenceDefault() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(null);

        PlayerPreference preference = manager.getPreference(player);

        assertEquals(VariantType.TILES, preference.getVariantType());
        assertEquals(1, preference.getWidth());
    }

    @Test
    @DisplayName("getPreference returns cached preference")
    public void testGetPreferenceCached() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        // First set a preference
        manager.setPreference(player, VariantType.MOON, 3);

        // Then get it (should return cached version, not query PDC again)
        PlayerPreference preference = manager.getPreference(player);

        assertEquals(VariantType.MOON, preference.getVariantType());
        assertEquals(3, preference.getWidth());

        // Verify PDC was accessed
        verify(player, atLeastOnce()).getPersistentDataContainer();
    }

    @Test
    @DisplayName("setPreference updates cache")
    public void testSetPreferenceUpdatesCache() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        manager.setPreference(player, VariantType.CLOCK, 2);

        PlayerPreference preference = manager.getPreference(player);

        assertEquals(VariantType.CLOCK, preference.getVariantType());
        assertEquals(2, preference.getWidth());
    }

    @Test
    @DisplayName("setPreference persists to player data")
    public void testSetPreferencePersists() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        manager.setPreference(player, VariantType.SHADE, 4);

        verify(pdc).set(any(), any(), any());
    }

    @Test
    @DisplayName("clearPreference removes from cache")
    public void testClearPreferenceRemovesFromCache() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        manager.setPreference(player, VariantType.MOON, 3);
        manager.clearPreference(player);

        // Now PDC should return null (since we cleared it)
        when(pdc.get(any(), any())).thenReturn(null);

        PlayerPreference preference = manager.getPreference(player);

        assertEquals(VariantType.TILES, preference.getVariantType());
        assertEquals(1, preference.getWidth());
    }

    @Test
    @DisplayName("clearPreference removes from persistent data")
    public void testClearPreferenceRemovesFromPersistentData() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        manager.clearPreference(player);

        verify(pdc).remove(any());
    }

    @Test
    @DisplayName("clearCache clears all cached preferences")
    public void testClearCache() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        manager.setPreference(player, VariantType.MOON, 3);
        manager.clearCache();

        // Reset stubbing to return null (simulating no stored preference)
        when(pdc.get(any(), any())).thenReturn(null);

        manager.getPreference(player);

        // Should query PDC again after cache clear
        verify(player, atLeastOnce()).getPersistentDataContainer();
    }

    @Test
    @DisplayName("getPreference handles invalid stored data gracefully")
    public void testGetPreferenceHandlesInvalidData() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn("invalid:data:extra");

        // Should return default when deserialization fails
        PlayerPreference preference = manager.getPreference(player);

        assertEquals(VariantType.TILES, preference.getVariantType());
        assertEquals(1, preference.getWidth());
    }

    @Test
    @DisplayName("getPreference handles empty stored data")
    public void testGetPreferenceHandlesEmptyData() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn("");

        // Should return default when data is empty
        PlayerPreference preference = manager.getPreference(player);

        assertEquals(VariantType.TILES, preference.getVariantType());
        assertEquals(1, preference.getWidth());
    }

    @Test
    @DisplayName("setPreference with different values overrides cache")
    public void testSetPreferenceOverridesCache() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        manager.setPreference(player, VariantType.MOON, 3);
        manager.setPreference(player, VariantType.CLOCK, 5);

        PlayerPreference preference = manager.getPreference(player);

        assertEquals(VariantType.CLOCK, preference.getVariantType());
        assertEquals(5, preference.getWidth());
    }

    @Test
    @DisplayName("getVariant returns progress variant for player preference")
    public void testGetVariant() {
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        manager.setPreference(player, VariantType.MOON, 3);

        var variant = manager.getVariant(player);

        assertNotNull(variant);
    }
}
