package org.cavarest.elementaldragon.command.display;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.cooldown.CooldownManager;

import java.util.Map;

/**
 * Formats and displays cooldown information for players.
 * Provides formatted output for player-specific cooldowns.
 *
 * <p>This formatter handles:</p>
 * <ul>
 *   <li>Display of all active cooldowns for a player</li>
 *   <li>Element-specific cooldown status with color coding</li>
 *   <li>Ready state indication when no cooldown is active</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class CooldownFormatter {

    private final CooldownManager cooldownManager;

    /**
     * Creates a new cooldown formatter.
     *
     * @param cooldownManager the cooldown manager to get cooldown data from
     */
    public CooldownFormatter(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    /**
     * Displays all cooldowns for a player.
     *
     * @param sender the command sender to send the information to
     * @param target the player whose cooldowns to display
     */
    public void displayPlayerCooldowns(CommandSender sender, Player target) {
        // Get all cooldowns
        Map<String, Integer> allCooldowns = cooldownManager.getAllCooldowns(target);

        if (allCooldowns.isEmpty()) {
            sender.sendMessage(Component.text(
                target.getName() + " has no active cooldowns.",
                NamedTextColor.GREEN
            ));
        } else {
            sender.sendMessage(Component.text(
                "Cooldowns for " + target.getName() + ":",
                NamedTextColor.AQUA
            ));

            // Display each element's cooldown
            displayElementCooldown(sender, "Lightning", allCooldowns.get(CooldownManager.LIGHTNING));
            displayElementCooldown(sender, "Fire", allCooldowns.get(CooldownManager.FIRE));
            displayElementCooldown(sender, "Agile", allCooldowns.get(CooldownManager.AGILE));
            displayElementCooldown(sender, "Immortal", allCooldowns.get(CooldownManager.IMMORTAL));
            displayElementCooldown(sender, "Corrupt", allCooldowns.get(CooldownManager.CORRUPT));
        }
    }

    /**
     * Displays a single element's cooldown status.
     * Shows the element name with either remaining time (if on cooldown) or ready state.
     *
     * @param sender the command sender to send the message to
     * @param elementName the display name of the element (e.g., "Lightning", "Fire")
     * @param seconds the remaining cooldown in seconds, or {@code null} if no cooldown
     */
    private void displayElementCooldown(CommandSender sender, String elementName, Integer seconds) {
        if (seconds != null && seconds > 0) {
            sender.sendMessage(Component.text(
                "  " + elementName + ": " + seconds + "s",
                NamedTextColor.RED
            ));
        } else {
            sender.sendMessage(Component.text(
                "  " + elementName + ": ready",
                NamedTextColor.GREEN
            ));
        }
    }
}