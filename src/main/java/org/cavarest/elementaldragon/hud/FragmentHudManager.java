package org.cavarest.elementaldragon.hud;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages HUD display for fragment cooldowns and status.
 * Shows equipped fragment type, active ability cooldowns, and visual indicators.
 */
public class FragmentHudManager {

  private final ElementalDragon plugin;
  private final FragmentManager fragmentManager;
  private BukkitTask updateTask;

  /**
   * Create a new FragmentHudManager.
   *
   * @param plugin The plugin instance
   * @param fragmentManager The fragment manager
   */
  public FragmentHudManager(ElementalDragon plugin, FragmentManager fragmentManager) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
    startUpdateTask();
  }

  /**
   * Start the HUD update task.
   */
  private void startUpdateTask() {
    // Update HUD every second (20 ticks)
    updateTask = Bukkit.getScheduler().runTaskTimer(
      plugin,
      this::updateAllPlayerHuds,
      0L,
      20L
    );
  }

  /**
   * Update HUD for all online players with fragments equipped.
   */
  private void updateAllPlayerHuds() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      updatePlayerHud(player);
    }
  }

  /**
   * Update HUD for a specific player.
   *
   * @param player The player
   */
  public void updatePlayerHud(Player player) {
    if (player == null) {
      return;
    }

    // Check if player has a fragment equipped
    FragmentType equippedFragment = fragmentManager.getEquippedFragment(player);
    if (equippedFragment == null) {
      return;
    }

    int cooldown = fragmentManager.getRemainingCooldown(player);

    // Build HUD text with color coding based on fragment type
    Component hudText = buildFragmentHudText(equippedFragment, cooldown);

    // Send action bar (appears above hotbar)
    player.sendActionBar(hudText);
  }

  /**
   * Build the fragment HUD text with color coding.
   *
   * @param fragmentType The equipped fragment type
   * @param cooldown The remaining cooldown in seconds
   * @return The formatted HUD component
   */
  private Component buildFragmentHudText(FragmentType fragmentType, int cooldown) {
    TextColor color = getFragmentColor(fragmentType);
    String fragmentSymbol = getFragmentSymbol(fragmentType);

    if (cooldown > 0) {
      // Show cooldown status
      return Component.text()
        .append(Component.text(fragmentSymbol + " ", color))
        .append(Component.text(fragmentType.getDisplayName() + " ", NamedTextColor.WHITE))
        .append(Component.text("[", NamedTextColor.GRAY))
        .append(Component.text(cooldown + "s", NamedTextColor.RED))
        .append(Component.text("]", NamedTextColor.GRAY))
        .build();
    } else {
      // Show ready status
      return Component.text()
        .append(Component.text(fragmentSymbol + " ", color))
        .append(Component.text(fragmentType.getDisplayName() + " ", NamedTextColor.WHITE))
        .append(Component.text("READY", NamedTextColor.GREEN))
        .build();
    }
  }

  /**
   * Get the color for a fragment type.
   *
   * @param fragmentType The fragment type
   * @return The text color
   */
  private TextColor getFragmentColor(FragmentType fragmentType) {
    if (fragmentType == null) {
      return NamedTextColor.WHITE;
    }

    switch (fragmentType) {
      case BURNING:
        return NamedTextColor.RED;
      case AGILITY:
        return NamedTextColor.AQUA;
      case IMMORTAL:
        return NamedTextColor.GOLD;
      case CORRUPTED:
        return NamedTextColor.DARK_PURPLE;
      default:
        return NamedTextColor.WHITE;
    }
  }

  /**
   * Get the symbol for a fragment type.
   *
   * @param fragmentType The fragment type
   * @return The symbol character
   */
  private String getFragmentSymbol(FragmentType fragmentType) {
    if (fragmentType == null) {
      return "?";
    }

    switch (fragmentType) {
      case BURNING:
        return "🔥";
      case AGILITY:
        return "💨";
      case IMMORTAL:
        return "🔰";
      case CORRUPTED:
        return "👁";
      default:
        return "◆";
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
