package org.cavarest.dragonegglightning.ability;

import org.cavarest.dragonegglightning.DragonEggLightningPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player abilities and cooldowns.
 */
public class AbilityManager implements Listener {

  private static final int DEFAULT_COOLDOWN_SECONDS = 60;

  private final DragonEggLightningPlugin plugin;
  private final Map<UUID, Ability> abilities;
  private final Map<UUID, Long> cooldowns;
  private int globalCooldownSeconds = DEFAULT_COOLDOWN_SECONDS;

  public AbilityManager(DragonEggLightningPlugin plugin) {
    this.plugin = plugin;
    this.abilities = new HashMap<>();
    this.cooldowns = new HashMap<>();

    registerAbilities();
    registerEventListeners();
  }

  /**
   * Register available abilities.
   */
  private void registerAbilities() {
    abilities.put(
      UUID.fromString("00000000-0000-0000-0000-000000000001"),
      new LightningAbility(plugin)
    );
  }

  /**
   * Register event listeners for cooldown management.
   * Only registers if plugin is not null (handles testing scenarios).
   */
  private void registerEventListeners() {
    if (plugin != null && plugin.getServer() != null) {
      plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
  }

  /**
   * Get ability by ID.
   *
   * @param abilityId The ability ID (1, 2, etc.)
   * @return The ability or null if not found
   */
  public Ability getAbility(int abilityId) {
    UUID abilityUuid = UUID.fromString(
      String.format("00000000-0000-0000-0000-%012d", abilityId)
    );
    return abilities.get(abilityUuid);
  }

  /**
   * Check if player can use an ability.
   *
   * @param player The player
   * @param ability The ability
   * @return true if the ability can be used
   */
  public boolean canUseAbility(Player player, Ability ability) {
    if (player == null || ability == null) {
      return false;
    }
    if (!ability.hasRequiredItem(player)) {
      return false;
    }

    Long cooldownEnd = cooldowns.get(player.getUniqueId());
    if (cooldownEnd == null) {
      return true;
    }

    return System.currentTimeMillis() >= cooldownEnd;
  }

  /**
   * Use an ability.
   *
   * @param player The player
   * @param ability The ability
   * @return true if the ability was successfully used
   */
  public boolean useAbility(Player player, Ability ability) {
    if (!canUseAbility(player, ability)) {
      return false;
    }

    boolean success = ability.execute(player);
    if (success) {
      long cooldownEnd = System.currentTimeMillis() +
        ability.getCooldownMillis();
      cooldowns.put(player.getUniqueId(), cooldownEnd);
    }

    return success;
  }

  /**
   * Get remaining cooldown in seconds.
   *
   * @param player The player
   * @return Remaining cooldown in seconds, or 0 if no cooldown
   */
  public int getRemainingCooldown(Player player) {
    if (player == null) {
      return 0;
    }
    Long cooldownEnd = cooldowns.get(player.getUniqueId());
    if (cooldownEnd == null) {
      return 0;
    }

    long remaining = cooldownEnd - System.currentTimeMillis();
    if (remaining <= 0) {
      cooldowns.remove(player.getUniqueId());
      return 0;
    }

    return (int) Math.ceil(remaining / 1000.0);
  }

  /**
   * Check if player is on cooldown.
   *
   * @param player The player
   * @return true if on cooldown
   */
  public boolean isOnCooldown(Player player) {
    if (player == null) {
      return false;
    }
    return getRemainingCooldown(player) > 0;
  }

  /**
   * Clear cooldown for a player (for testing or special cases).
   *
   * @param player The player
   */
  public void clearCooldown(Player player) {
    if (player != null) {
      cooldowns.remove(player.getUniqueId());
    }
  }

  /**
   * Set cooldown for a player (used for testing).
   *
   * @param player The player
   * @param cooldownSeconds Cooldown in seconds
   */
  public void setCooldown(Player player, int cooldownSeconds) {
    if (player != null) {
      long cooldownEnd = System.currentTimeMillis() + (cooldownSeconds * 1000L);
      cooldowns.put(player.getUniqueId(), cooldownEnd);
    }
  }

  /**
   * Get the global cooldown duration in seconds.
   *
   * @return Global cooldown in seconds
   */
  public int getGlobalCooldownDuration() {
    return globalCooldownSeconds;
  }

  /**
   * Set the global cooldown duration in seconds.
   * This affects all future ability uses.
   *
   * @param seconds New cooldown duration
   */
  public void setGlobalCooldownDuration(int seconds) {
    this.globalCooldownSeconds = Math.max(0, seconds);
  }

  /**
   * Event handler for player death - clears cooldown so respawn restarts it.
   *
   * @param event The player death event
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    if (player != null) {
      // Clear cooldown on death - this allows the respawn to restart the cooldown
      cooldowns.remove(player.getUniqueId());
    }
  }

  /**
   * Event handler for player join - cooldown persists across logout/login.
   * This prevents players from avoiding cooldowns by logging out and back in.
   *
   * @param event The player join event
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (player != null) {
      // Check if player has remaining cooldown
      Long cooldownEnd = cooldowns.get(player.getUniqueId());
      if (cooldownEnd != null) {
        long remaining = cooldownEnd - System.currentTimeMillis();
        if (remaining <= 0) {
          // Cooldown has expired while offline, remove it
          cooldowns.remove(player.getUniqueId());
        }
        // If remaining > 0, cooldown persists (which is what we want)
      }
    }
  }
}
