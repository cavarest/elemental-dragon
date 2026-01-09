package org.cavarest.elementaldragon.hud;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.ability.Ability;
import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages HUD display for ability cooldowns.
 * Shows both lightning ability (dragon egg) and fragment ability status.
 * Uses CooldownManager for unified cooldown tracking across all elements.
 */
public class HudManager {

  private final ElementalDragon plugin;
  private final AbilityManager abilityManager;
  private final FragmentManager fragmentManager;
  private final CooldownManager cooldownManager;
  private BukkitTask updateTask;

  public HudManager(
    ElementalDragon plugin,
    AbilityManager abilityManager,
    FragmentManager fragmentManager,
    CooldownManager cooldownManager
  ) {
    this.plugin = plugin;
    this.abilityManager = abilityManager;
    this.fragmentManager = fragmentManager;
    this.cooldownManager = cooldownManager;
    startUpdateTask();
  }

  /**
   * Start the HUD update task.
   * Updates every second (20 ticks) for better performance.
   */
  private void startUpdateTask() {
    // Update HUD every second (once per second for better performance)
    updateTask = Bukkit.getScheduler().runTaskTimer(
      plugin,
      this::updateAllPlayerHuds,
      0L,
      20L  // Changed from 1L to 20L (every second instead of every tick)
    );
  }

  /**
   * Update HUD for all online players.
   */
  private void updateAllPlayerHuds() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      updatePlayerHud(player);
    }
  }

  /**
   * Update HUD for a specific player.
   * Shows both lightning and fragment status in a single action bar message.
   *
   * @param player The player
   */
  private void updatePlayerHud(Player player) {
    // Build combined HUD message
    Component hudText = buildCombinedHudMessage(player);

    if (hudText != null) {
      // Send action bar (appears above hotbar, middle-left area)
      player.sendActionBar(hudText);
    }
  }

  /**
   * Build a combined HUD message showing both lightning and fragment status.
   *
   * @param player The player
   * @return The combined HUD component, or null if no abilities available
   */
  private Component buildCombinedHudMessage(Player player) {
    // Check lightning ability (dragon egg in offhand)
    Ability lightningAbility = abilityManager.getAbility(1);
    boolean hasLightningItem = lightningAbility != null && lightningAbility.hasRequiredItem(player);

    // Check fragment ability (should show even without dragon egg)
    FragmentType equippedFragment = fragmentManager != null ?
      fragmentManager.getEquippedFragment(player) : null;

    // If neither ability is available, return null (no HUD)
    if (!hasLightningItem && equippedFragment == null) {
      return null;
    }

    Component result = null;

    // Lightning status (only if dragon egg in offhand)
    if (hasLightningItem) {
      int lightningCooldown = cooldownManager.getRemainingCooldown(player, CooldownManager.LIGHTNING);

      if (lightningCooldown > 0) {
        result = Component.text("⚡ ", NamedTextColor.DARK_PURPLE)
          .append(Component.text(lightningCooldown + "s", NamedTextColor.RED)
            .decoration(TextDecoration.BOLD, true));
      } else {
        result = Component.text("⚡ ", NamedTextColor.LIGHT_PURPLE)
          .append(Component.text("Lightning ready", NamedTextColor.GREEN)
            .decoration(TextDecoration.BOLD, false));
      }
    }

    // Fragment status (ALWAYS show if fragment equipped, regardless of dragon egg)
    if (equippedFragment != null) {
      // Add separator if both are shown
      if (result != null) {
        result = result.append(Component.text(" | ", NamedTextColor.GRAY));
      }

      int fragmentCooldown = fragmentManager.getRemainingCooldown(player);
      String fragmentEmoji = getFragmentEmoji(equippedFragment);
      String fragmentStatus = getFragmentStatus(equippedFragment);

      Component fragmentComponent;
      if (fragmentCooldown > 0) {
        fragmentComponent = Component.text(fragmentEmoji + " ", getFragmentColor(equippedFragment))
          .append(Component.text(fragmentCooldown + "s", NamedTextColor.RED)
            .decoration(TextDecoration.BOLD, true));
      } else {
        fragmentComponent = Component.text(fragmentEmoji + " ", getFragmentColor(equippedFragment))
          .append(Component.text(fragmentStatus + " ready", NamedTextColor.GREEN)
            .decoration(TextDecoration.BOLD, false));
      }

      // If result is null (no lightning), start with fragment
      result = (result == null) ? fragmentComponent : result.append(fragmentComponent);
    }

    return result;
  }

  /**
   * Get the emoji for a fragment type.
   *
   * @param type The fragment type
   * @return The emoji string
   */
  private String getFragmentEmoji(FragmentType type) {
    switch (type) {
      case BURNING:
        return "🔥";
      case AGILITY:
        return "💨";
      case IMMORTAL:
        return "🛡️";
      case CORRUPTED:
        return "👁️";
      default:
        return "✨";
    }
  }

  /**
   * Get the status text for a fragment type.
   *
   * @param type The fragment type
   * @return The status text
   */
  private String getFragmentStatus(FragmentType type) {
    switch (type) {
      case BURNING:
        return "Fire";
      case AGILITY:
        return "Dash";
      case IMMORTAL:
        return "Guard";
      case CORRUPTED:
        return "Void";
      default:
        return "Ready";
    }
  }

  /**
   * Get the color for a fragment type.
   *
   * @param type The fragment type
   * @return The NamedTextColor
   */
  private NamedTextColor getFragmentColor(FragmentType type) {
    switch (type) {
      case BURNING:
        return NamedTextColor.RED;
      case AGILITY:
        return NamedTextColor.AQUA;
      case IMMORTAL:
        return NamedTextColor.GREEN;
      case CORRUPTED:
        return NamedTextColor.DARK_PURPLE;
      default:
        return NamedTextColor.WHITE;
    }
  }

  /**
   * Shutdown the HUD manager.
   */
  public void shutdown() {
    if (updateTask != null) {
      updateTask.cancel();
    }
  }
}
