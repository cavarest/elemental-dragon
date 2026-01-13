package org.cavarest.elementaldragon.command.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.command.base.AbstractSubcommand;
import org.cavarest.elementaldragon.command.display.GlobalCooldownFormatter;
import org.cavarest.elementaldragon.command.util.ElementValidator;
import org.cavarest.elementaldragon.cooldown.CooldownManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Subcommand for managing global cooldown configuration.
 * Handles setting and viewing default cooldowns for all abilities.
 *
 * <p>Subcommands:</p>
 * <ul>
 *   <li>{@code setglobalcooldown <element> <ability> <seconds>} - Set default cooldown</li>
 *   <li>{@code getglobalcooldown} - View all default cooldowns</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class GlobalCooldownSubcommand extends AbstractSubcommand {

    private final CooldownManager cooldownManager;
    private final GlobalCooldownFormatter formatter;
    private final ElementValidator elementValidator;
    private final ElementalDragon plugin;

    /**
     * Creates a new global cooldown subcommand.
     *
     * @param cooldownManager the cooldown manager
     * @param formatter the formatter for displaying global cooldowns
     * @param elementValidator the validator for element names
     * @param plugin the plugin instance (for HUD updates)
     */
    public GlobalCooldownSubcommand(
        CooldownManager cooldownManager,
        GlobalCooldownFormatter formatter,
        ElementValidator elementValidator,
        ElementalDragon plugin
    ) {
        super(
            "globalcooldown",
            "Manage global cooldown configuration",
            "/ed <setglobalcooldown|getglobalcooldown> ...",
            "elementaldragon.admin"
        );
        this.cooldownManager = cooldownManager;
        this.formatter = formatter;
        this.elementValidator = elementValidator;
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // This subcommand is a parent for set/get global cooldown
        // It should never be called directly, but just in case:
        sendError(sender, "Use: /ed setglobalcooldown or /ed getglobalcooldown");
        return true;
    }

    /**
     * Handles the setglobalcooldown subcommand.
     *
     * @param sender the command sender
     * @param args the command arguments (NOT including "setglobalcooldown")
     * @return {@code true} if handled
     */
    public boolean executeSetGlobalCooldown(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender, "/ed setglobalcooldown <element> <ability-num> <seconds|0|default>");
            sendInfo(sender, "Elements: " + elementValidator.getValidElementsString());
            sendInfo(sender, "Ability: 1 or 2");
            sendInfo(sender, "Seconds: number, 0 (no cooldown), or 'default' (reset to fragment default)");
            return true;
        }

        String element = args[0].toLowerCase();
        String abilityArg = args[1];
        String cooldownArg = args[2];

        // Validate element name
        if (!elementValidator.isValidElement(element)) {
            sendError(sender, "Invalid element: " + element + ". Valid: " + elementValidator.getValidElementsString());
            return true;
        }

        // Parse ability number
        Integer abilityNum = parseInteger(sender, abilityArg, "ability number");
        if (abilityNum == null) {
            return true;
        }

        if (!elementValidator.isValidAbilityNumber(abilityNum)) {
            sendError(sender, "Ability number must be 1 or 2");
            return true;
        }

        // Handle special values: "default" or numeric (including 0)
        boolean isReset = cooldownArg.equalsIgnoreCase("default");
        Integer seconds = null;

        if (isReset) {
            // Reset to fragment default - remove from global config
            // This makes it fall back to fragment constant
            cooldownManager.removeGlobalCooldown(element, abilityNum);
            sendSuccess(sender, "Reset global " + element + " ability " + abilityNum +
                " to fragment default (e.g., 40s for fire:1).");
            return true;
        }

        // Parse numeric value (including 0)
        seconds = parseInteger(sender, cooldownArg, "seconds");
        if (seconds == null) {
            sendError(sender, "Use a number, 0 (no cooldown), or 'default' (reset)");
            return true;
        }

        if (seconds < 0) {
            sendError(sender, "Cooldown cannot be negative!");
            return true;
        }

        // Apply global cooldown
        cooldownManager.setGlobalCooldown(element, abilityNum, seconds);

        // Adjust active player cooldowns (including clearing when seconds=0)
        cooldownManager.adjustActiveCooldowns(element, abilityNum, seconds);

        // Update all player HUDs to show new duration display
        if (plugin.getHudManager() != null) {
            plugin.getHudManager().updateAllPlayerHuds();
        }

        if (seconds == 0) {
            sendSuccess(sender, "DISABLED global cooldown for " + element + " ability " +
                abilityNum + " - ALWAYS READY (no cooldown, can spam).");
            sendInfo(sender, "All active player cooldowns have been cleared.");
        } else {
            sendSuccess(sender, "Set global " + element + " ability " + abilityNum +
                " cooldown to " + seconds + "s.");
            sendInfo(sender, "Active player cooldowns have been adjusted accordingly.");
        }

        return true;
    }

    /**
     * Handles the getglobalcooldown subcommand.
     *
     * @param sender the command sender
     * @param args the command arguments (NOT including "getglobalcooldown")
     * @return {@code true} if handled
     */
    public boolean executeGetGlobalCooldown(CommandSender sender, String[] args) {
        formatter.displayGlobalCooldowns(sender);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        // This parent global cooldown subcommand won't be used for tab completion
        // Individual set/get will handle their own
        return new ArrayList<>();
    }

    /**
     * Tab completion for setglobalcooldown command.
     *
     * @param sender the command sender
     * @param args the current arguments (NOT including "setglobalcooldown")
     * @return list of completions
     */
    public List<String> tabCompleteSetGlobalCooldown(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First arg: element names
            completions.addAll(elementValidator.getValidElements());
        } else if (args.length == 2) {
            // Second arg: ability numbers
            completions.addAll(Arrays.asList("1", "2"));
        } else if (args.length == 3) {
            // Third arg: seconds suggestions
            completions.addAll(Arrays.asList("10", "25", "30", "45", "60", "90", "120", "300"));
        }

        // Filter by partial input
        if (args.length > 0) {
            String partial = args[args.length - 1].toLowerCase();
            completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
        }

        return completions;
    }

    /**
     * Tab completion for getglobalcooldown command.
     * No arguments needed for this command.
     *
     * @param sender the command sender
     * @param args the current arguments (NOT including "getglobalcooldown")
     * @return empty list (no completions)
     */
    public List<String> tabCompleteGetGlobalCooldown(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}