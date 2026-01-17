package org.cavarest.elementaldragon.command.display;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.tracking.ElementalPlayerTracker;

import java.util.Map;
import java.util.Set;

/**
 * Formats and displays player information for the info subcommand.
 * Provides both detailed single-player views and compact multi-player summaries.
 *
 * <p>This formatter handles:</p>
 * <ul>
 *   <li>Individual player status with fragment ownership, equipped fragment, and cooldowns</li>
 *   <li>Compact multi-player status for @a selector usage</li>
 *   <li>Table-formatted status lists for all tracked players</li>
 *   <li>Element distribution statistics</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class PlayerInfoFormatter {

    private final ElementalDragon plugin;
    private final AbilityManager abilityManager;

    /**
     * Creates a new player info formatter.
     *
     * @param plugin the plugin instance (for accessing tracker and fragment manager)
     * @param abilityManager the ability manager (for cooldown information)
     */
    public PlayerInfoFormatter(ElementalDragon plugin, AbilityManager abilityManager) {
        this.plugin = plugin;
        this.abilityManager = abilityManager;
    }

    /**
     * Displays detailed information about a player.
     *
     * @param sender the command sender to send the information to
     * @param target the player whose information to display
     * @param compact whether to use compact format (for multi-player display)
     */
    public void displayPlayerInfo(CommandSender sender, Player target, boolean compact) {
        // Get player status
        ElementalPlayerTracker tracker = plugin.getPlayerTracker();
        if (tracker == null) {
            sender.sendMessage(Component.text("Player tracker not available!", NamedTextColor.RED));
            return;
        }

        ElementalPlayerTracker.PlayerElementalStatus status = tracker.getPlayerStatus(target);
        tracker.refreshPlayerStatus(target);

        // Get cooldown info
        int cooldownRemaining = abilityManager.getRemainingCooldown(target);
        boolean onCooldown = cooldownRemaining > 0;

        // Get equipped fragment
        FragmentManager fragmentManager = plugin.getFragmentManager();
        FragmentType equippedFragment = fragmentManager != null ? fragmentManager.getEquippedFragment(target) : null;

        if (compact) {
            // Compact format for @a
            sender.sendMessage(Component.text(
                "📋 " + target.getName() + " | " +
                getStatusEmojis(status) + " | " +
                (equippedFragment != null ? "⚔ " + equippedFragment.getDisplayName() : "⚔ None") + " | " +
                (onCooldown ? "⏳ " + cooldownRemaining + "s" : "✅ Ready"),
                NamedTextColor.WHITE
            ));
        } else {
            // Detailed format for single player
            sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
            sender.sendMessage(Component.text("  👤 " + target.getName() + "'s Elemental Status", NamedTextColor.GOLD));
            sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));

            // Fragment Status
            sender.sendMessage(Component.text("  🔮 Fragments:", NamedTextColor.AQUA));
            sender.sendMessage(formatElementStatusEnhanced("  🔥 Fire", status.hasFire, "Burning Fragment"));
            sender.sendMessage(formatElementStatusEnhanced("  💨 Wind", status.hasWind, "Agility Fragment"));
            sender.sendMessage(formatElementStatusEnhanced("  🩸 Blood", status.hasBlood, "Immortal Fragment"));
            sender.sendMessage(formatElementStatusEnhanced("  🌑 Dark", status.hasDarkness, "Corrupted Core"));
            sender.sendMessage(formatElementStatusEnhanced("  ⚡ Light", status.hasLight, "Lightning (Dragon Egg)"));

            sender.sendMessage(Component.text("", NamedTextColor.WHITE));

            // Equipped Fragment
            sender.sendMessage(Component.text("  ⚔  Equipped: ", NamedTextColor.AQUA)
                .append(equippedFragment != null ?
                    Component.text(equippedFragment.getDisplayName(), NamedTextColor.GREEN) :
                    Component.text("None", NamedTextColor.GRAY)));

            // Cooldown Status
            sender.sendMessage(Component.text("  ⏱  Cooldown: ", NamedTextColor.AQUA)
                .append(onCooldown ?
                    Component.text(cooldownRemaining + " seconds remaining", NamedTextColor.RED) :
                    Component.text("Ready", NamedTextColor.GREEN)));

            // Quick Actions
            sender.sendMessage(Component.text("", NamedTextColor.WHITE));
            sender.sendMessage(Component.text("  💡 Quick: ", NamedTextColor.YELLOW)
                .append(Component.text("/ed give " + target.getName() + " equipment fire", NamedTextColor.GRAY)));
        }
    }

    /**
     * Displays a table-formatted list of all tracked players.
     *
     * @param sender the command sender to send the list to
     */
    public void displayPlayerList(CommandSender sender) {
        ElementalPlayerTracker tracker = plugin.getPlayerTracker();
        if (tracker == null) {
            sender.sendMessage(Component.text("Player tracker not available!", NamedTextColor.RED));
            return;
        }

        // Refresh all player statuses
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            tracker.refreshPlayerStatus(onlinePlayer);
        }

        // Get element counts
        Map<String, Integer> counts = tracker.getElementCounts();

        sender.sendMessage(Component.text("═══════════════════════════════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("     ⚔  ELEMENTAL DRAGON STATUS REPORT  ⚔", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("═══════════════════════════════════════════════════════════", NamedTextColor.GOLD));

        // Element summary with counts
        sender.sendMessage(Component.text("", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  📊 Element Distribution:", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("    🔥 Fire: " + padCount(counts.get("fire")) + "  " +
            "💨 Wind: " + padCount(counts.get("wind")) + "  " +
            "🩸 Blood: " + padCount(counts.get("blood")) + "  " +
            "🌑 Dark: " + padCount(counts.get("darkness")) + "  " +
            "⚡ Light: " + padCount(counts.get("light")),
            NamedTextColor.WHITE));

        sender.sendMessage(Component.text("", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  👥 Player Status Table:", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  ┌─────────────────────┬───────────┬─────────────────────┬──────────┐", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  │ Player              │ Fragments │ Equipped            │ Status   │", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("  ├─────────────────────┼───────────┼─────────────────────┼──────────┤", NamedTextColor.GRAY));

        // List all tracked players
        Set<ElementalPlayerTracker.PlayerElementalStatus> allStatuses = tracker.getAllPlayerStatuses();
        FragmentManager fragmentManager = plugin.getFragmentManager();

        for (ElementalPlayerTracker.PlayerElementalStatus status : allStatuses) {
            String playerName = status.playerName;
            if (playerName.length() > 19) playerName = playerName.substring(0, 18) + "~";

            String fragments = getStatusEmojis(status);
            if (fragments.equals("—")) fragments = "    none";

            FragmentType equipped = fragmentManager != null ?
                fragmentManager.getEquippedFragment(Bukkit.getPlayerExact(status.playerName)) : null;
            String equippedName = equipped != null ? equipped.getDisplayName() : "None";
            if (equippedName.length() > 19) equippedName = equippedName.substring(0, 18) + "~";

            // Get cooldown status
            Player player = Bukkit.getPlayerExact(status.playerName);
            String cooldownStatus;
            if (player != null) {
                int cd = abilityManager.getRemainingCooldown(player);
                cooldownStatus = cd > 0 ? cd + "s" : "Ready";
            } else {
                cooldownStatus = "—";
            }

            sender.sendMessage(Component.text(
                String.format("  │ %-19s │ %-8s │ %-19s │ %-8s │",
                    playerName, fragments, equippedName, cooldownStatus),
                NamedTextColor.WHITE
            ));
        }

        sender.sendMessage(Component.text("  └─────────────────────┴───────────┴─────────────────────┴──────────┘", NamedTextColor.GRAY));

        sender.sendMessage(Component.text("", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  📈 Total: " + allStatuses.size() + " players tracked", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  💡 Use /ed info player <name> for details", NamedTextColor.DARK_GRAY));
    }

    /**
     * Gets status emojis for a player's elemental status.
     *
     * @param status the player's elemental status
     * @return a string of emojis representing owned elements, or "—" if none
     */
    private String getStatusEmojis(ElementalPlayerTracker.PlayerElementalStatus status) {
        StringBuilder sb = new StringBuilder();
        if (status.hasFire) sb.append("🔥");
        if (status.hasWind) sb.append("💨");
        if (status.hasBlood) sb.append("🩸");
        if (status.hasDarkness) sb.append("🌑");
        if (status.hasLight) sb.append("⚡");
        return sb.length() > 0 ? sb.toString() : "—";
    }

    /**
     * Formats an element status line with unlock status.
     *
     * @param elementName the display name of the element (e.g., "  🔥 Fire")
     * @param hasElement whether the player has this element
     * @param fragmentName the name of the fragment (e.g., "Burning Fragment")
     * @return a formatted component showing the element's status
     */
    private Component formatElementStatusEnhanced(String elementName, boolean hasElement, String fragmentName) {
        if (hasElement) {
            return Component.text(elementName + " ✅ " + fragmentName, NamedTextColor.GREEN);
        } else {
            return Component.text(elementName + " ❌ (not unlocked)", NamedTextColor.GRAY);
        }
    }

    /**
     * Pads a count number for display alignment.
     *
     * @param count the count to pad
     * @return the padded count string
     */
    private String padCount(Integer count) {
        if (count == null) return "0  ";
        if (count < 10) return count + "   ";
        if (count < 100) return count + "  ";
        return count.toString();
    }
}