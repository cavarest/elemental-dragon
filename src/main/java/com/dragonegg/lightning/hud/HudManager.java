package com.dragonegg.lightning.hud;

import com.dragonegg.lightning.DragonEggLightningPlugin;
import com.dragonegg.lightning.ability.Ability;
import com.dragonegg.lightning.ability.AbilityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages HUD display for ability cooldowns.
 */
public class HudManager {

  private final DragonEggLightningPlugin plugin;
  private final AbilityManager abilityManager;
  private BukkitTask updateTask;

  public HudManager(
    DragonEggLightningPlugin plugin,
    AbilityManager abilityManager
  ) {
    this.plugin = plugin;
    this.abilityManager = abilityManager;
    startUpdateTask();
  }

  /**
   * Start the HUD update task.
   */
  private void startUpdateTask() {
    // Update HUD every tick (20 times per second)
    updateTask = Bukkit.getScheduler().runTaskTimer(
      plugin,
      this::updateAllPlayerHuds,
      0L,
      1L
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
   *
   * @param player The player
   */
  private void updatePlayerHud(Player player) {
    // Only show HUD if player has dragon egg in offhand
    Ability lightningAbility = abilityManager.getAbility(1);
    if (lightningAbility == null || !lightningAbility.hasRequiredItem(player)) {
      return;
    }

    int cooldown = abilityManager.getRemainingCooldown(player);

    Component hudText;
    if (cooldown > 0) {
      // Show cooldown time
      hudText = Component.text()
        .append(Component.text("⚡ ", NamedTextColor.DARK_PURPLE))
        .append(Component.text(cooldown + "s", NamedTextColor.RED))
        .decoration(TextDecoration.BOLD, true)
        .build();
    } else {
      // Show ready status
      hudText = Component.text()
        .append(Component.text("⚡ ", NamedTextColor.LIGHT_PURPLE))
        .append(Component.text("Lightning ready", NamedTextColor.GREEN))
        .decoration(TextDecoration.BOLD, false)
        .build();
    }

    // Send action bar (appears above hotbar, middle-left area)
    player.sendActionBar(hudText);
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
