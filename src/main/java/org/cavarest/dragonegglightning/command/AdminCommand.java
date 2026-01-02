package org.cavarest.dragonegglightning.command;

import org.cavarest.dragonegglightning.DragonEggLightningPlugin;
import org.cavarest.dragonegglightning.ability.AbilityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Admin command for DragonEgg Lightning plugin.
 * Usage: /dragonlightning <subcommand> [args]
 *
 * Subcommands:
 *   setcooldown <player> <seconds>  - Set cooldown for a player
 *   clearcooldown <player>          - Clear cooldown for a player
 *   getcooldown <player>            - Get remaining cooldown for a player
 *   setglobalcooldown <seconds>     - Set global cooldown duration
 *   getglobalcooldown               - Get current global cooldown setting
 */
public class AdminCommand implements CommandExecutor, TabCompleter {

    private final DragonEggLightningPlugin plugin;
    private final AbilityManager abilityManager;

    public AdminCommand(DragonEggLightningPlugin plugin, AbilityManager abilityManager) {
        this.plugin = plugin;
        this.abilityManager = abilityManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("dragonlightning.admin")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "setcooldown":
                return handleSetCooldown(sender, args);
            case "clearcooldown":
                return handleClearCooldown(sender, args);
            case "getcooldown":
                return handleGetCooldown(sender, args);
            case "setglobalcooldown":
                return handleSetGlobalCooldown(sender, args);
            case "getglobalcooldown":
                return handleGetGlobalCooldown(sender);
            case "help":
            default:
                showHelp(sender);
                return true;
        }
    }

    private boolean handleSetCooldown(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /dragonlightning setcooldown <player> <seconds>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player '" + args[1] + "' not found!", NamedTextColor.RED));
            return true;
        }

        try {
            int seconds = Integer.parseInt(args[2]);
            abilityManager.setCooldown(target, seconds);
            sender.sendMessage(Component.text("Set cooldown for " + target.getName() + " to " + seconds + " seconds.", NamedTextColor.GREEN));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid number: " + args[2], NamedTextColor.RED));
        }
        return true;
    }

    private boolean handleClearCooldown(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /dragonlightning clearcooldown <player>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player '" + args[1] + "' not found!", NamedTextColor.RED));
            return true;
        }

        abilityManager.clearCooldown(target);
        sender.sendMessage(Component.text("Cleared cooldown for " + target.getName() + ".", NamedTextColor.GREEN));
        return true;
    }

    private boolean handleGetCooldown(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /dragonlightning getcooldown <player>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player '" + args[1] + "' not found!", NamedTextColor.RED));
            return true;
        }

        int remaining = abilityManager.getRemainingCooldown(target);
        if (remaining > 0) {
            sender.sendMessage(Component.text(target.getName() + " has " + remaining + " seconds cooldown remaining.", NamedTextColor.AQUA));
        } else {
            sender.sendMessage(Component.text(target.getName() + " has no cooldown.", NamedTextColor.GREEN));
        }
        return true;
    }

    private boolean handleSetGlobalCooldown(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /dragonlightning setglobalcooldown <seconds>", NamedTextColor.RED));
            return true;
        }

        try {
            int seconds = Integer.parseInt(args[1]);
            abilityManager.setGlobalCooldownDuration(seconds);
            sender.sendMessage(Component.text("Global cooldown set to " + seconds + " seconds.", NamedTextColor.GREEN));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid number: " + args[1], NamedTextColor.RED));
        }
        return true;
    }

    private boolean handleGetGlobalCooldown(CommandSender sender) {
        int seconds = abilityManager.getGlobalCooldownDuration();
        sender.sendMessage(Component.text("Global cooldown is " + seconds + " seconds.", NamedTextColor.AQUA));
        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== DragonEgg Lightning Admin Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/dragonlightning setcooldown <player> <seconds>", NamedTextColor.YELLOW)
            .append(Component.text(" - Set player cooldown", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/dragonlightning clearcooldown <player>", NamedTextColor.YELLOW)
            .append(Component.text(" - Clear player cooldown", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/dragonlightning getcooldown <player>", NamedTextColor.YELLOW)
            .append(Component.text(" - Get player cooldown", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/dragonlightning setglobalcooldown <seconds>", NamedTextColor.YELLOW)
            .append(Component.text(" - Set global cooldown", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/dragonlightning getglobalcooldown", NamedTextColor.YELLOW)
            .append(Component.text(" - Get global cooldown", NamedTextColor.GRAY)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("dragonlightning.admin")) {
            return completions;
        }

        if (args.length == 1) {
            completions.addAll(Arrays.asList(
                "setcooldown", "clearcooldown", "getcooldown",
                "setglobalcooldown", "getglobalcooldown", "help"
            ));
            String partial = args[0].toLowerCase();
            completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("setcooldown") || sub.equals("clearcooldown") || sub.equals("getcooldown")) {
                // Complete with online player names
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
                String partial = args[1].toLowerCase();
                completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
            } else if (sub.equals("setglobalcooldown")) {
                completions.addAll(Arrays.asList("5", "10", "30", "60", "120"));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setcooldown")) {
                completions.addAll(Arrays.asList("0", "5", "10", "30", "60"));
            }
        }

        return completions;
    }
}
