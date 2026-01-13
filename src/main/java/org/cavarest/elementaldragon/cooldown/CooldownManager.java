package org.cavarest.elementaldragon.cooldown;

import org.cavarest.elementaldragon.ElementalDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Centralized cooldown management for ALL elemental abilities.
 * Single source of truth for cooldowns across lightning and all fragments.
 * Supports per-ability cooldowns (ability 1 and ability 2 for each element).
 */
public class CooldownManager implements Listener {

  // Element name constants (canonical names)
  public static final String LIGHTNING = "lightning";
  public static final String FIRE = "fire";
  public static final String AGILE = "agile";
  public static final String IMMORTAL = "immortal";
  public static final String CORRUPT = "corrupt";

  private final ElementalDragon plugin;

  // Player cooldowns: UUID -> (Element:Ability -> Cooldown End Time)
  // Key format: "element:ability" (e.g., "fire:1", "fire:2")
  private final Map<UUID, Map<String, Long>> cooldowns;

  // Global cooldown configuration: Element:Ability -> Default Duration (seconds)
  private final Map<String, Integer> globalCooldowns;

  /**
   * Create a new CooldownManager.
   *
   * @param plugin The plugin instance
   */
  public CooldownManager(ElementalDragon plugin) {
    this.plugin = plugin;
    this.cooldowns = new HashMap<>();
    this.globalCooldowns = new HashMap<>();
    initializeDefaultCooldowns();
    registerEventListeners();
  }

  /**
   * Initialize default global cooldowns for all elements and abilities.
   */
  private void initializeDefaultCooldowns() {
    // Lightning ability (only has ability 1)
    globalCooldowns.put(makeKey(LIGHTNING, 1), 60);

    // Fire fragment abilities (match BurningFragment constants)
    globalCooldowns.put(makeKey(FIRE, 1), 40);  // Dragon's Wrath (40s)
    globalCooldowns.put(makeKey(FIRE, 2), 60);  // Infernal Dominion (60s)

    // Agility fragment abilities (match AgilityFragment constants)
    globalCooldowns.put(makeKey(AGILE, 1), 30);  // Draconic Surge (30s)
    globalCooldowns.put(makeKey(AGILE, 2), 45);  // Wing Burst (45s)

    // Immortal fragment abilities (match ImmortalFragment constants)
    globalCooldowns.put(makeKey(IMMORTAL, 1), 90);   // Draconic Reflex (90s)
    globalCooldowns.put(makeKey(IMMORTAL, 2), 300);  // Essence Rebirth (5 min)

    // Corrupted Core abilities (match CorruptedCoreFragment constants)
    globalCooldowns.put(makeKey(CORRUPT, 1), 60);  // Dread Gaze (60s)
    globalCooldowns.put(makeKey(CORRUPT, 2), 90);  // Life Devourer (90s)
  }

  /**
   * Make a cooldown key from element and ability number.
   */
  private String makeKey(String element, int abilityNum) {
    return element.toLowerCase() + ":" + abilityNum;
  }

  /**
   * Register event listeners for cooldown management.
   */
  private void registerEventListeners() {
    if (plugin != null && plugin.getServer() != null) {
      plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
  }

  /**
   * Set cooldown for a specific element and ability.
   *
   * @param player The player
   * @param element The element name
   * @param abilityNum The ability number (1 or 2)
   * @param seconds Cooldown duration in seconds
   */
  public void setCooldown(Player player, String element, int abilityNum, int seconds) {
    if (player == null || element == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();
    long cooldownEnd = System.currentTimeMillis() + (seconds * 1000L);

    Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(
      playerUuid,
      k -> new HashMap<>()
    );

    playerCooldowns.put(makeKey(element, abilityNum), cooldownEnd);
  }

  /**
   * Set cooldown for all abilities of an element.
   *
   * @param player The player
   * @param element The element name
   * @param seconds Cooldown duration in seconds
   */
  public void setCooldown(Player player, String element, int seconds) {
    // Set cooldown for both abilities
    setCooldown(player, element, 1, seconds);
    setCooldown(player, element, 2, seconds);
  }

  /**
   * Get remaining cooldown for a specific element and ability.
   *
   * @param player The player
   * @param element The element name
   * @param abilityNum The ability number
   * @return Remaining cooldown in seconds, or 0 if no cooldown
   */
  public int getRemainingCooldown(Player player, String element, int abilityNum) {
    if (player == null || element == null) {
      return 0;
    }

    Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
    if (playerCooldowns == null) {
      return 0;
    }

    String key = makeKey(element, abilityNum);
    Long cooldownEnd = playerCooldowns.get(key);
    if (cooldownEnd == null) {
      return 0;
    }

    long remaining = cooldownEnd - System.currentTimeMillis();
    if (remaining <= 0) {
      playerCooldowns.remove(key);
      if (playerCooldowns.isEmpty()) {
        cooldowns.remove(player.getUniqueId());
      }
      return 0;
    }

    return (int) Math.ceil(remaining / 1000.0);
  }

  /**
   * Get remaining cooldown for any ability of an element.
   * Returns the longest remaining cooldown.
   *
   * @param player The player
   * @param element The element name
   * @return Remaining cooldown in seconds, or 0 if no cooldown
   */
  public int getRemainingCooldown(Player player, String element) {
    int cd1 = getRemainingCooldown(player, element, 1);
    int cd2 = getRemainingCooldown(player, element, 2);
    return Math.max(cd1, cd2);
  }

  /**
   * Check if player is on cooldown for a specific ability.
   */
  public boolean isOnCooldown(Player player, String element, int abilityNum) {
    return getRemainingCooldown(player, element, abilityNum) > 0;
  }

  /**
   * Check if player is on cooldown for any ability of an element.
   */
  public boolean isOnCooldown(Player player, String element) {
    return getRemainingCooldown(player, element) > 0;
  }

  /**
   * Clear cooldown for a specific ability.
   */
  public void clearCooldown(Player player, String element, int abilityNum) {
    if (player == null || element == null) {
      return;
    }

    Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
    if (playerCooldowns != null) {
      playerCooldowns.remove(makeKey(element, abilityNum));
      if (playerCooldowns.isEmpty()) {
        cooldowns.remove(player.getUniqueId());
      }
    }
  }

  /**
   * Clear cooldown for all abilities of an element.
   */
  public void clearCooldown(Player player, String element) {
    clearCooldown(player, element, 1);
    clearCooldown(player, element, 2);
  }

  /**
   * Clear all cooldowns for a player.
   */
  public void clearAllCooldowns(Player player) {
    if (player != null) {
      cooldowns.remove(player.getUniqueId());
    }
  }

  /**
   * Get all cooldowns for a player.
   * Returns a map of element:ability -> remaining seconds.
   */
  public Map<String, Integer> getAllCooldowns(Player player) {
    Map<String, Integer> result = new HashMap<>();

    if (player == null) {
      return result;
    }

    Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
    if (playerCooldowns == null) {
      return result;
    }

    // Convert cooldown end times to remaining seconds
    for (Map.Entry<String, Long> entry : playerCooldowns.entrySet()) {
      long remaining = entry.getValue() - System.currentTimeMillis();
      if (remaining > 0) {
        result.put(entry.getKey(), (int) Math.ceil(remaining / 1000.0));
      }
    }

    return result;
  }

  /**
   * Set global cooldown duration for a specific element and ability.
   */
  public void setGlobalCooldown(String element, int abilityNum, int seconds) {
    if (element == null) {
      return;
    }
    globalCooldowns.put(makeKey(element, abilityNum), seconds);
  }

  /**
   * Get global cooldown duration for a specific element and ability.
   */
  public int getGlobalCooldown(String element, int abilityNum) {
    if (element == null) {
      return -1;
    }
    return globalCooldowns.getOrDefault(makeKey(element, abilityNum), -1);
  }

  /**
   * Remove global cooldown configuration for a specific element and ability.
   * After removal, the ability will fall back to the fragment's default cooldown.
   */
  public void removeGlobalCooldown(String element, int abilityNum) {
    if (element == null) {
      return;
    }
    globalCooldowns.remove(makeKey(element, abilityNum));
  }

  /**
   * Adjust active player cooldowns when global cooldown changes.
   * For players currently on cooldown for this ability:
   * - If newMaxSeconds is 0 (cooldown disabled), clears all active cooldowns
   * - Otherwise, caps cooldowns to min(currentRemaining, newMaxSeconds)
   *
   * @param element The element name
   * @param abilityNum The ability number
   * @param newMaxSeconds The new maximum cooldown in seconds (0 = disabled)
   */
  public void adjustActiveCooldowns(String element, int abilityNum, int newMaxSeconds) {
    if (element == null || newMaxSeconds < 0) {
      return;
    }

    String key = makeKey(element, abilityNum);
    int adjustedPlayers = 0;

    // If cooldown is disabled (0), clear all active cooldowns
    if (newMaxSeconds == 0) {
      for (Map.Entry<UUID, Map<String, Long>> entry : cooldowns.entrySet()) {
        Map<String, Long> playerCooldowns = entry.getValue();
        if (playerCooldowns.remove(key) != null) {
          adjustedPlayers++;
          if (plugin != null) {
            plugin.getLogger().info(
              "Cleared " + element + ":" + abilityNum + " cooldown for player UUID: " +
              entry.getKey() + " (cooldown disabled)"
            );
          }
        }
      }
      // Clean up empty maps
      cooldowns.entrySet().removeIf(e -> e.getValue().isEmpty());
      
      if (plugin != null && adjustedPlayers > 0) {
        plugin.getLogger().info(
          "Cleared active cooldowns for " + adjustedPlayers + " player(s) on " +
          element + ":" + abilityNum + " (cooldown disabled)"
        );
      }
      return;
    }

    // Otherwise, cap cooldowns to new maximum using min()
    for (Map.Entry<UUID, Map<String, Long>> entry : cooldowns.entrySet()) {
      Map<String, Long> playerCooldowns = entry.getValue();
      Long cooldownEnd = playerCooldowns.get(key);

      if (cooldownEnd != null) {
        // Calculate current remaining time
        long remaining = cooldownEnd - System.currentTimeMillis();
        if (remaining > 0) {
          int currentRemainingSeconds = (int) Math.ceil(remaining / 1000.0);

          // Apply min(currentRemaining, newMaxSeconds) to cap at new maximum
          int adjustedSeconds = Math.min(currentRemainingSeconds, newMaxSeconds);

          // Only update if there's an actual change
          if (adjustedSeconds != currentRemainingSeconds) {
            long newCooldownEnd = System.currentTimeMillis() + (adjustedSeconds * 1000L);
            playerCooldowns.put(key, newCooldownEnd);
            adjustedPlayers++;

            if (plugin != null) {
              plugin.getLogger().info(
                "Adjusted " + element + ":" + abilityNum + " cooldown from " +
                currentRemainingSeconds + "s to " + adjustedSeconds + "s for player UUID: " +
                entry.getKey() + " (capped to new max)"
              );
            }
          }
        }
      }
    }

    if (plugin != null && adjustedPlayers > 0) {
      plugin.getLogger().info(
        "Adjusted active cooldowns for " + adjustedPlayers + " player(s) on " +
        element + ":" + abilityNum + " to new maximum of " + newMaxSeconds + "s"
      );
    }
  }

  /**
   * Get the formatted cooldown display string for an ability.
   * Returns the effective cooldown duration in a human-readable format.
   * Used by HudManager to display dynamic duration information.
   *
   * @param element The element name
   * @param abilityNum The ability number
   * @return Formatted duration string (e.g., "60s", "instant", or the duration in seconds)
   */
  public String getCooldownDisplay(String element, int abilityNum) {
    if (element == null) {
      return "unknown";
    }

    int cooldown = globalCooldowns.getOrDefault(makeKey(element, abilityNum), -1);

    // Handle special cases
    if (cooldown <= 0) {
      return "instant"; // 0 seconds or negative means no cooldown/instant
    }

    return cooldown + "s"; // Format: "60s", "30s", etc.
  }

  /**
   * Get all global cooldown configurations.
   * Returns a map of element:ability -> default duration (seconds).
   */
  public Map<String, Integer> getAllGlobalCooldowns() {
    return new HashMap<>(globalCooldowns);
  }

  /**
   * Event handler for player death - clears all cooldowns.
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    if (player != null) {
      clearAllCooldowns(player);
    }
  }

  /**
   * Event handler for player join - cleans up expired cooldowns.
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (player == null) {
      return;
    }

    Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
    if (playerCooldowns == null) {
      return;
    }

    // Remove expired cooldowns
    playerCooldowns.entrySet().removeIf(entry -> {
      long remaining = entry.getValue() - System.currentTimeMillis();
      return remaining <= 0;
    });

    // Clean up empty map
    if (playerCooldowns.isEmpty()) {
      cooldowns.remove(player.getUniqueId());
    }
  }
}