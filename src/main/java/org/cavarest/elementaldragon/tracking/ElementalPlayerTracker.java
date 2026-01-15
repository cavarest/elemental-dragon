package org.cavarest.elementaldragon.tracking;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks which players have which elemental dragon powers.
 * Monitors player state to detect when they gain/lose elemental abilities.
 */
public class ElementalPlayerTracker implements Listener {

  private final ElementalDragon plugin;
  private final AbilityManager abilityManager;
  private final FragmentManager fragmentManager;

  // Map of player UUID to their elemental status
  private final Map<UUID, PlayerElementalStatus> playerStatus;

  public ElementalPlayerTracker(ElementalDragon plugin) {
    this.plugin = plugin;
    this.abilityManager = plugin.getAbilityManager();
    this.fragmentManager = plugin.getFragmentManager();
    this.playerStatus = new HashMap<>();
  }

  /**
   * Get the elemental status for a player.
   *
   * @param player The player
   * @return The player's elemental status
   */
  public PlayerElementalStatus getPlayerStatus(Player player) {
    if (player == null) {
      return null;
    }
    return playerStatus.computeIfAbsent(
      player.getUniqueId(),
      uuid -> new PlayerElementalStatus(player.getName())
    );
  }

  /**
   * Refresh the status for a player based on their current state.
   *
   * @param player The player
   */
  public void refreshPlayerStatus(Player player) {
    if (player == null) {
      return;
    }

    PlayerElementalStatus status = getPlayerStatus(player);
    status.playerName = player.getName();

    // Check Fire (Burning Fragment)
    status.hasFire = hasFragment(player, FragmentType.BURNING);

    // Check Wind (Agility Fragment)
    status.hasWind = hasFragment(player, FragmentType.AGILITY);

    // Check Blood (Immortal Fragment)
    status.hasBlood = hasFragment(player, FragmentType.IMMORTAL);

    // Check Darkness (Corrupted Core)
    status.hasDarkness = hasFragment(player, FragmentType.CORRUPTED);

    // Check Light (Lightning - Dragon Egg in offhand)
    status.hasLight = hasLightningAbility(player);
  }

  /**
   * Check if a player has a specific fragment equipped.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @return true if the player has the fragment equipped
   */
  private boolean hasFragment(Player player, FragmentType fragmentType) {
    if (fragmentManager == null) {
      return false;
    }
    FragmentType equipped = fragmentManager.getEquippedFragment(player);
    return equipped == fragmentType;
  }

  /**
   * Check if a player can use lightning ability (has Dragon Egg in offhand).
   *
   * @param player The player
   * @return true if the player can use lightning
   */
  private boolean hasLightningAbility(Player player) {
    if (abilityManager == null) {
      return false;
    }
    var ability = abilityManager.getAbility(1);
    return ability != null && ability.hasRequiredItem(player);
  }

  /**
   * Get all tracked players with their elemental status.
   *
   * @return Set of all player statuses
   */
  public Set<PlayerElementalStatus> getAllPlayerStatuses() {
    return new HashSet<>(playerStatus.values());
  }

  /**
   * Get count of players with each elemental type.
   *
   * @return Map of element name to count
   */
  public Map<String, Integer> getElementCounts() {
    Map<String, Integer> counts = new HashMap<>();
    counts.put("fire", 0);
    counts.put("wind", 0);
    counts.put("blood", 0);
    counts.put("darkness", 0);
    counts.put("light", 0);

    for (PlayerElementalStatus status : playerStatus.values()) {
      if (status.hasFire) counts.merge("fire", 1, Integer::sum);
      if (status.hasWind) counts.merge("wind", 1, Integer::sum);
      if (status.hasBlood) counts.merge("blood", 1, Integer::sum);
      if (status.hasDarkness) counts.merge("darkness", 1, Integer::sum);
      if (status.hasLight) counts.merge("light", 1, Integer::sum);
    }

    return counts;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    refreshPlayerStatus(player);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    // Keep the status in memory for reference
  }

  /**
   * Represents the elemental status of a player.
   */
  public static class PlayerElementalStatus {
    public String playerName;
    public boolean hasFire;      // Burning Fragment
    public boolean hasWind;      // Agility Fragment
    public boolean hasBlood;     // Immortal Fragment
    public boolean hasDarkness;  // Corrupted Core
    public boolean hasLight;     // Lightning (Dragon Egg)

    public PlayerElementalStatus(String playerName) {
      this.playerName = playerName;
      this.hasFire = false;
      this.hasWind = false;
      this.hasBlood = false;
      this.hasDarkness = false;
      this.hasLight = false;
    }

    /**
     * Get the status as emoji string.
     *
     * @return String with emojis representing active elements
     */
    public String getStatusEmojis() {
      StringBuilder sb = new StringBuilder();
      if (hasFire) sb.append("🔥");
      if (hasWind) sb.append("💨");
      if (hasBlood) sb.append("🩸");
      if (hasDarkness) sb.append("👁");
      if (hasLight) sb.append("⚡");
      return sb.length() > 0 ? sb.toString() : "—";
    }

    /**
     * Get a compact status string for table display.
     *
     * @return Status string like "🔥💨👁⚡" or "—"
     */
    public String getCompactStatus() {
      return getStatusEmojis();
    }
  }
}
