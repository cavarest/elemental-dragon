package org.cavarest.elementaldragon.command.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.command.base.AbstractSubcommand;
import org.cavarest.elementaldragon.command.display.CooldownFormatter;
import org.cavarest.elementaldragon.command.util.ElementValidator;
import org.cavarest.elementaldragon.command.util.PlayerResolver;
import org.cavarest.elementaldragon.cooldown.CooldownManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Subcommand for managing player-specific cooldowns.
 * Handles setting, clearing, and viewing cooldowns for individual players.
 *
 * <p>Subcommands:</p>
 * <ul>
 *   <li>{@code setcooldown <player> <element> <ability> <seconds>} - Set a specific cooldown</li>
 *   <li>{@code clearcooldown <player> [element]} - Clear cooldown(s)</li>
 *   <li>{@code getcooldown <player>} - View all cooldowns for a player</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class CooldownSubcommand extends AbstractSubcommand {

    private final CooldownManager cooldownManager;
    private final CooldownFormatter formatter;
    private final PlayerResolver playerResolver;
    private final ElementValidator elementValidator;

    /**
     * Creates a new cooldown subcommand.
     *
     * @param cooldownManager the cooldown manager
     * @param formatter the formatter for displaying cooldowns
     * @param playerResolver the resolver for player references
     * @param elementValidator the validator for element names
     */
    public CooldownSubcommand(
        CooldownManager cooldownManager,
        CooldownFormatter formatter,
        PlayerResolver playerResolver,
        ElementValidator elementValidator
    ) {
        super(
            "cooldown",
            "Manage player cooldowns",
            "/ed <setcooldown|clearcooldown|getcooldown> ...",
            "elementaldragon.admin"
        );
        this.cooldownManager = cooldownManager;
        this.formatter = formatter;
        this.playerResolver = playerResolver;
        this.elementValidator = elementValidator;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // This subcommand is a parent for set/clear/get cooldown
        // It should never be called directly, but just in case:
        sendError(sender, "Use: /ed setcooldown, /ed clearcooldown, or /ed getcooldown");
        return true;
    }

    /**
     * Handles the setcooldown subcommand.
     *
     * @param sender the command sender
     * @param args the command arguments (NOT including "setcooldown")
     * @return {@code true} if handled
     */
    public boolean executeSetCooldown(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sendUsage(sender, "/ed setcooldown <player-ref> <element> <ability-num> <seconds|0|default>");
            sendInfo(sender, "Elements: " + elementValidator.getValidElementsString());
            sendInfo(sender, "Ability: 1 or 2 (use 'all' for both)");
            sendInfo(sender, "Seconds: number, 0 (no cooldown), or 'default' (reset to global)");
            return true;
        }

        String playerRef = args[0];
        String element = args[1].toLowerCase();
        String abilityArg = args[2];
        String cooldownArg = args[3];

        Player target = playerResolver.resolvePlayer(sender, playerRef);
        if (target == null) {
            return true;
        }

        // Validate element name
        if (!elementValidator.isValidElement(element)) {
            sendError(sender, "Invalid element: " + element + ". Valid: " + elementValidator.getValidElementsString());
            return true;
        }

        // Parse ability number or 'all'
        boolean setAll = abilityArg.equalsIgnoreCase("all");
        Integer abilityNum = null;

        if (!setAll) {
            abilityNum = parseInteger(sender, abilityArg, "ability number");
            if (abilityNum == null) {
                sendError(sender, "Use 1, 2, or 'all'");
                return true;
            }
            if (!elementValidator.isValidAbilityNumber(abilityNum)) {
                sendError(sender, "Ability number must be 1, 2, or 'all'");
                return true;
            }
        }

        // Handle special values: "default" or numeric (including 0)
        boolean isReset = cooldownArg.equalsIgnoreCase("default");
        Integer seconds = null;

        if (isReset) {
            // Reset to global default - clear the per-player override
            if (setAll) {
                cooldownManager.clearCooldown(target, element, 1);
                cooldownManager.clearCooldown(target, element, 2);
                sendSuccess(sender, "Reset " + element + " abilities 1 & 2 for " + target.getName() + " to global defaults.");
            } else {
                cooldownManager.clearCooldown(target, element, abilityNum);
                sendSuccess(sender, "Reset " + element + " ability " + abilityNum + " for " + target.getName() + " to global default.");
            }
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

        // Apply cooldown
        if (setAll) {
            cooldownManager.setCooldown(target, element, 1, seconds);
            cooldownManager.setCooldown(target, element, 2, seconds);
            if (seconds == 0) {
                sendSuccess(sender, "Disabled cooldown for " + element + " abilities 1 & 2 for " + target.getName() + ".");
            } else {
                sendSuccess(sender, "Set " + element + " abilities 1 & 2 cooldown for " + target.getName() + " to " + seconds + "s.");
            }
        } else {
            cooldownManager.setCooldown(target, element, abilityNum, seconds);
            if (seconds == 0) {
                sendSuccess(sender, "Disabled cooldown for " + element + " ability " + abilityNum + " for " + target.getName() + ".");
            } else {
                sendSuccess(sender, "Set " + element + " ability " + abilityNum + " cooldown for " + target.getName() + " to " + seconds + "s.");
            }
        }

        return true;
    }

    /**
     * Handles the clearcooldown subcommand.
     *
     * @param sender the command sender
     * @param args the command arguments (NOT including "clearcooldown")
     * @return {@code true} if handled
     */
    public boolean executeClearCooldown(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendUsage(sender, "/ed clearcooldown <player-ref> [element]");
            sendInfo(sender, "Elements: " + elementValidator.getValidElementsString() + " (omit for all)");
            return true;
        }

        String playerRef = args[0];
        Player target = playerResolver.resolvePlayer(sender, playerRef);
        if (target == null) {
            return true;
        }

        // Check if element is specified
        if (args.length >= 2) {
            String element = args[1].toLowerCase();

            // Validate element name
            if (!elementValidator.isValidElement(element)) {
                sendError(sender, "Invalid element: " + element + ". Valid: " + elementValidator.getValidElementsString());
                return true;
            }

            // Clear specific element
            cooldownManager.clearCooldown(target, element);
            sendSuccess(sender, "Cleared " + element + " cooldown for " + target.getName() + ".");
        } else {
            // Clear all cooldowns
            cooldownManager.clearAllCooldowns(target);
            sendSuccess(sender, "Cleared ALL cooldowns for " + target.getName() + ".");
        }

        return true;
    }

    /**
     * Handles the getcooldown subcommand.
     *
     * @param sender the command sender
     * @param args the command arguments (NOT including "getcooldown")
     * @return {@code true} if handled
     */
    public boolean executeGetCooldown(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendUsage(sender, "/ed getcooldown <player-ref>");
            return true;
        }

        String playerRef = args[0];
        Player target = playerResolver.resolvePlayer(sender, playerRef);
        if (target == null) {
            return true;
        }

        formatter.displayPlayerCooldowns(sender, target);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        // This parent cooldown subcommand won't be used for tab completion
        // Individual set/clear/get will handle their own
        return new ArrayList<>();
    }

    /**
     * Tab completion for setcooldown command.
     *
     * @param sender the command sender
     * @param args the current arguments (NOT including "setcooldown")
     * @return list of completions
     */
    public List<String> tabCompleteSetCooldown(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First arg: player selectors and names
            completions.addAll(Arrays.asList("@p", "@s", "@a"));
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 2) {
            // Second arg: element names
            completions.addAll(elementValidator.getValidElements());
        } else if (args.length == 3) {
            // Third arg: ability numbers
            completions.addAll(Arrays.asList("1", "2", "all"));
        } else if (args.length == 4) {
            // Fourth arg: seconds suggestions
            completions.addAll(Arrays.asList("0", "10", "30", "60", "90", "120", "300"));
        }

        // Filter by partial input
        if (args.length > 0) {
            String partial = args[args.length - 1].toLowerCase();
            completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
        }

        return completions;
    }

    /**
     * Tab completion for clearcooldown command.
     *
     * @param sender the command sender
     * @param args the current arguments (NOT including "clearcooldown")
     * @return list of completions
     */
    public List<String> tabCompleteClearCooldown(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First arg: player selectors and names
            completions.addAll(Arrays.asList("@p", "@s", "@a"));
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 2) {
            // Second arg: element names (optional)
            completions.addAll(elementValidator.getValidElements());
        }

        // Filter by partial input
        if (args.length > 0) {
            String partial = args[args.length - 1].toLowerCase();
            completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
        }

        return completions;
    }

    /**
     * Tab completion for getcooldown command.
     *
     * @param sender the command sender
     * @param args the current arguments (NOT including "getcooldown")
     * @return list of completions
     */
    public List<String> tabCompleteGetCooldown(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First arg: player selectors and names
            completions.addAll(Arrays.asList("@p", "@s", "@a"));
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }

            // Filter by partial input
            String partial = args[0].toLowerCase();
            completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
        }

        return completions;
    }
}