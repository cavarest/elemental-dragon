package org.cavarest.elementaldragon.hud;

import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer.ProgressVariant;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer.VariantType;

import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-player preferences for the plugin.
 *
 * <p>Each player can set their own countdown symbol style using:
 * <pre>
 *   /fire setcountdownsym MOON 2
 *   /agile setcountdownsym CLOCK 1
 *   /immortal setcountdownsym SHADE 3
 *   /corrupt setcountdownsym TILES
 * </pre>
 *
 * <p>This class stores preferences in memory and persists them across
 * server restarts using the player's persistent data container.</p>
 *
 * <p>Designed to be extensible for future player preferences beyond countdown symbols.</p>
 *
 * @since 1.1.0
 */
public class PlayerPreferenceManager {

    /**
     * Metadata key for storing player countdown preferences.
     */
    private static final String PREFERENCE_KEY = "countdown_preference";

    /**
     * Default preference for players who haven't set one.
     */
    private static final PlayerPreference DEFAULT_PREFERENCE = new PlayerPreference(
        VariantType.TILES,
        1
    );

    /**
     * In-memory cache of player preferences for quick access.
     * Maps player UUID to their preference.
     */
    private final Map<UUID, PlayerPreference> preferenceCache = new ConcurrentHashMap<>();

    /**
     * Gets the countdown preference for a specific player.
     *
     * <p>If the player hasn't set a preference, returns the default (TILES).</p>
     *
     * @param player The player to get the preference for
     * @return The player's countdown preference
     */
    public PlayerPreference getPreference(Player player) {
        UUID uuid = player.getUniqueId();

        // Check cache first
        PlayerPreference cached = preferenceCache.get(uuid);
        if (cached != null) {
            return cached;
        }

        // Load from persistent data
        PlayerPreference preference = loadPreference(player);
        if (preference == null) {
            preference = DEFAULT_PREFERENCE;
        }

        // Cache it
        preferenceCache.put(uuid, preference);
        return preference;
    }

    /**
     * Sets the countdown preference for a specific player.
     *
     * @param player The player to set the preference for
     * @param variantType The variant type (TILES, MOON, CLOCK, etc.)
     * @param width The width parameter (1-10)
     */
    public void setPreference(Player player, VariantType variantType, int width) {
        UUID uuid = player.getUniqueId();
        PlayerPreference preference = new PlayerPreference(variantType, width);

        // Update cache
        preferenceCache.put(uuid, preference);

        // Persist to player data
        savePreference(player, preference);
    }

    /**
     * Clears the preference for a specific player, reverting them to default.
     *
     * @param player The player to clear the preference for
     */
    public void clearPreference(Player player) {
        UUID uuid = player.getUniqueId();

        // Remove from cache
        preferenceCache.remove(uuid);

        // Remove from persistent data
        player.getPersistentDataContainer().remove(
            org.bukkit.NamespacedKey.fromString("elementaldragon:" + PREFERENCE_KEY)
        );
    }

    /**
     * Gets the progress variant for a specific player.
     *
     * @param player The player to get the variant for
     * @return The progress variant to use for this player
     */
    public ProgressVariant getVariant(Player player) {
        PlayerPreference preference = getPreference(player);
        return getVariant(preference.variantType, preference.width);
    }

    /**
     * Gets a progress variant by type and width.
     *
     * @param type The variant type
     * @param width The width parameter
     * @return The corresponding progress variant
     */
    private ProgressVariant getVariant(VariantType type, int width) {
        switch (type) {
            case TILES:
                return ProgressBarRenderer.TILES;
            case MOON:
                return ProgressBarRenderer.MOON.withWidth(width);
            case CLOCK:
                return ProgressBarRenderer.CLOCK.withWidth(width);
            case SHADE:
                return ProgressBarRenderer.SHADE.withWidth(width);
            case BLOCK1:
                return ProgressBarRenderer.BLOCK1.withWidth(width);
            case BLOCK2:
                return ProgressBarRenderer.BLOCK2.withWidth(width);
            case BLOCK3:
                return ProgressBarRenderer.BLOCK3.withWidth(width);
            case BLOCK4:
                return ProgressBarRenderer.BLOCK4.withWidth(width);
            case TRIANGLE:
                return ProgressBarRenderer.TRIANGLE.withWidth(width);
            default:
                return ProgressBarRenderer.TILES;
        }
    }

    /**
     * Loads a player's preference from their persistent data container.
     *
     * @param player The player to load from
     * @return The loaded preference, or null if not set
     */
    private PlayerPreference loadPreference(Player player) {
        String data = player.getPersistentDataContainer().get(
            org.bukkit.NamespacedKey.fromString("elementaldragon:" + PREFERENCE_KEY),
            org.bukkit.persistence.PersistentDataType.STRING
        );

        if (data == null || data.isEmpty()) {
            return null;
        }

        try {
            return PlayerPreference.deserialize(data);
        } catch (Exception e) {
            // If deserialization fails, return null (will use default)
            return null;
        }
    }

    /**
     * Saves a player's preference to their persistent data container.
     *
     * @param player The player to save to
     * @param preference The preference to save
     */
    private void savePreference(Player player, PlayerPreference preference) {
        player.getPersistentDataContainer().set(
            org.bukkit.NamespacedKey.fromString("elementaldragon:" + PREFERENCE_KEY),
            org.bukkit.persistence.PersistentDataType.STRING,
            preference.serialize()
        );
    }

    /**
     * Clears the preference cache (called on plugin disable to free memory).
     */
    public void clearCache() {
        preferenceCache.clear();
    }

    /**
     * Represents a single player's countdown preference.
     */
    public static class PlayerPreference {
        private final VariantType variantType;
        private final int width;

        /**
         * Creates a new player preference.
         *
         * @param variantType The variant type
         * @param width The width parameter (1-10)
         */
        public PlayerPreference(VariantType variantType, int width) {
            this.variantType = variantType;
            this.width = width;
        }

        /**
         * Deserializes a player preference from a string.
         *
         * @param data The serialized data (format: "variantType:width")
         * @return The deserialized preference
         */
        public static PlayerPreference deserialize(String data) {
            String[] parts = data.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid preference format: " + data);
            }

            VariantType type = VariantType.valueOf(parts[0]);
            int width = Integer.parseInt(parts[1]);

            return new PlayerPreference(type, width);
        }

        /**
         * Serializes this preference to a string.
         *
         * @return The serialized data (format: "variantType:width")
         */
        public String serialize() {
            return variantType.name() + ":" + width;
        }

        /**
         * Gets the variant type.
         *
         * @return The variant type
         */
        public VariantType getVariantType() {
            return variantType;
        }

        /**
         * Gets the width parameter.
         *
         * @return The width (1-10)
         */
        public int getWidth() {
            return width;
        }
    }
}
