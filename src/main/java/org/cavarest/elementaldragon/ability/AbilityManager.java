package org.cavarest.elementaldragon.ability;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player abilities.
 * Uses CooldownManager for unified cooldown tracking.
 */
public class AbilityManager implements Listener {

  private static final int DEFAULT_COOLDOWN_SECONDS = 60;

  private final ElementalDragon plugin;
  private final CooldownManager cooldownManager;
  private final Map<UUID, Ability> abilities;
  private int globalCooldownSeconds = DEFAULT_COOLDOWN_SECONDS;

  public AbilityManager(ElementalDragon plugin, CooldownManager cooldownManager) {
    this.plugin = plugin;
    this.cooldownManager = cooldownManager;
    this.abilities = new HashMap<>();

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

    return !cooldownManager.isOnCooldown(player, CooldownManager.LIGHTNING);
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
      // Check for global cooldown first, fall back to ability default
      int globalCooldown = cooldownManager.getGlobalCooldown(CooldownManager.LIGHTNING, 1);
      int cooldownSeconds;
      
      if (globalCooldown >= 0) {
        // Use global cooldown (including 0 for disabled)
        cooldownSeconds = globalCooldown;
      } else {
        // Fall back to ability's default cooldown
        cooldownSeconds = (int) (ability.getCooldownMillis() / 1000);
      }
      
      // Set cooldown (if 0, no cooldown will be set due to setCooldown logic)
      if (cooldownSeconds > 0) {
        cooldownManager.setCooldown(player, CooldownManager.LIGHTNING, 1, cooldownSeconds);
      }
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
    return cooldownManager.getRemainingCooldown(player, CooldownManager.LIGHTNING);
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
    return cooldownManager.isOnCooldown(player, CooldownManager.LIGHTNING);
  }

  /**
   * Clear cooldown for a player (for testing or special cases).
   *
   * @param player The player
   */
  public void clearCooldown(Player player) {
    if (player != null) {
      cooldownManager.clearCooldown(player, CooldownManager.LIGHTNING);
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
      cooldownManager.setCooldown(player, CooldownManager.LIGHTNING, cooldownSeconds);
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
}
