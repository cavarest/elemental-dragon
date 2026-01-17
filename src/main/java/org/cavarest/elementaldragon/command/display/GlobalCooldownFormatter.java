package org.cavarest.elementaldragon.command.display;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.cavarest.elementaldragon.cooldown.CooldownManager;

import java.util.Map;

/**
 * Formats and displays global cooldown configuration.
 * Provides formatted output for system-wide cooldown settings.
 *
 * <p>This formatter handles:</p>
 * <ul>
 *   <li>Display of all global cooldown configurations</li>
 *   <li>Formatted output with emojis and ability names</li>
 *   <li>Time conversion (seconds to minutes/seconds)</li>
 *   <li>Organized display by element type</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class GlobalCooldownFormatter {

    private final CooldownManager cooldownManager;

    /**
     * Creates a new global cooldown formatter.
     *
     * @param cooldownManager the cooldown manager to get global cooldown data from
     */
    public GlobalCooldownFormatter(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    /**
     * Displays all global cooldown configurations.
     *
     * @param sender the command sender to send the information to
     */
    public void displayGlobalCooldowns(CommandSender sender) {
        Map<String, Integer> globalCooldowns = cooldownManager.getAllGlobalCooldowns();

        sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("    ⚙  GLOBAL COOLDOWN CONFIGURATION  ⚙", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("", NamedTextColor.WHITE));

        // Lightning (only has ability 1)
        sender.sendMessage(Component.text("  ⚡ Lightning:", NamedTextColor.LIGHT_PURPLE));
        displayGlobalCooldownLine(sender, "    Ability 1", globalCooldowns.get("lightning:1"));
        sender.sendMessage(Component.text("", NamedTextColor.WHITE));

        // Fire Fragment
        sender.sendMessage(Component.text("  🔥 Fire Fragment:", NamedTextColor.RED));
        displayGlobalCooldownLine(sender, "    Ability 1 (Dragon's Wrath)", globalCooldowns.get("fire:1"));
        displayGlobalCooldownLine(sender, "    Ability 2 (Infernal Dominion)", globalCooldowns.get("fire:2"));
        sender.sendMessage(Component.text("", NamedTextColor.WHITE));

        // Agility Fragment
        sender.sendMessage(Component.text("  💨 Agility Fragment:", NamedTextColor.AQUA));
        displayGlobalCooldownLine(sender, "    Ability 1 (Draconic Surge)", globalCooldowns.get("agile:1"));
        displayGlobalCooldownLine(sender, "    Ability 2 (Wing Burst)", globalCooldowns.get("agile:2"));
        sender.sendMessage(Component.text("", NamedTextColor.WHITE));

        // Immortal Fragment
        sender.sendMessage(Component.text("  🩸 Immortal Fragment:", NamedTextColor.GOLD));
        displayGlobalCooldownLine(sender, "    Ability 1 (Draconic Reflex)", globalCooldowns.get("immortal:1"));
        displayGlobalCooldownLine(sender, "    Ability 2 (Essence Rebirth)", globalCooldowns.get("immortal:2"));
        sender.sendMessage(Component.text("", NamedTextColor.WHITE));

        // Corrupted Core
        sender.sendMessage(Component.text("  🌑 Corrupted Core:", NamedTextColor.DARK_PURPLE));
        displayGlobalCooldownLine(sender, "    Ability 1 (Dread Gaze)", globalCooldowns.get("corrupt:1"));
        displayGlobalCooldownLine(sender, "    Ability 2 (Life Devourer)", globalCooldowns.get("corrupt:2"));

        sender.sendMessage(Component.text("", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  💡 Use /ed setglobalcooldown <element> <ability> <seconds> to change", NamedTextColor.DARK_GRAY));
    }

    /**
     * Displays a single global cooldown line with formatting.
     * Converts seconds to human-readable format (minutes and seconds).
     *
     * @param sender the command sender to send the message to
     * @param abilityName the display name of the ability (e.g., "    Ability 1 (Dragon's Wrath)")
     * @param seconds the cooldown duration in seconds, or {@code null} if not configured
     */
    private void displayGlobalCooldownLine(CommandSender sender, String abilityName, Integer seconds) {
        if (seconds == null) {
            sender.sendMessage(Component.text(
                abilityName + ": not configured",
                NamedTextColor.GRAY
            ));
        } else {
            String timeStr;
            if (seconds >= 60) {
                int minutes = seconds / 60;
                int secs = seconds % 60;
                timeStr = minutes + "m" + (secs > 0 ? " " + secs + "s" : "");
            } else {
                timeStr = seconds + "s";
            }
            sender.sendMessage(Component.text(
                abilityName + ": " + timeStr,
                NamedTextColor.WHITE
            ));
        }
    }
}