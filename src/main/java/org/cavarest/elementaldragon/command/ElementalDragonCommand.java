package org.cavarest.elementaldragon.command;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.command.display.CooldownFormatter;
import org.cavarest.elementaldragon.command.display.GlobalCooldownFormatter;
import org.cavarest.elementaldragon.command.display.PlayerInfoFormatter;
import org.cavarest.elementaldragon.command.subcommands.CooldownSubcommand;
import org.cavarest.elementaldragon.command.subcommands.GiveSubcommand;
import org.cavarest.elementaldragon.command.subcommands.GlobalCooldownSubcommand;
import org.cavarest.elementaldragon.command.subcommands.InfoSubcommand;
import org.cavarest.elementaldragon.command.subcommands.SetGlobalCountdownSymbolSubcommand;
import org.cavarest.elementaldragon.command.util.ElementValidator;
import org.cavarest.elementaldragon.command.util.PlayerResolver;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Admin command for Elemental Dragon plugin.
 * Usage: /elementaldragon <subcommand> [args]
 * Alias: /ed
 *
 * <p>This command uses the Command Pattern with a subcommand registry for clean separation of concerns.
 * Each subcommand is responsible for its own execution, validation, and tab completion.</p>
 *
 * @since 1.0.0
 * @version 1.0.3 - Refactored to use Command Pattern
 */
public class ElementalDragonCommand implements CommandExecutor, TabCompleter {

    private final ElementalDragon plugin;
    private final AbilityManager abilityManager;
    private final CooldownManager cooldownManager;

    // Subcommands
    private final InfoSubcommand infoSubcommand;
    private final GiveSubcommand giveSubcommand;
    private final CooldownSubcommand cooldownSubcommand;
    private final GlobalCooldownSubcommand globalCooldownSubcommand;
    private final SetGlobalCountdownSymbolSubcommand setCountdownSymbolSubcommand;

    /**
     * Creates a new ElementalDragonCommand.
     *
     * @param plugin the plugin instance
     * @param abilityManager the ability manager
     */
    public ElementalDragonCommand(ElementalDragon plugin, AbilityManager abilityManager) {
        this.plugin = plugin;
        this.abilityManager = abilityManager;
        this.cooldownManager = plugin.getCooldownManager();

        // Initialize utilities
        PlayerResolver playerResolver = new PlayerResolver();
        ElementValidator elementValidator = new ElementValidator();

        // Initialize formatters
        PlayerInfoFormatter playerInfoFormatter = new PlayerInfoFormatter(plugin, abilityManager);
        CooldownFormatter cooldownFormatter = new CooldownFormatter(cooldownManager);
        GlobalCooldownFormatter globalCooldownFormatter = new GlobalCooldownFormatter(cooldownManager);

        // Initialize subcommands
        this.infoSubcommand = new InfoSubcommand(playerInfoFormatter, playerResolver);
        this.giveSubcommand = new GiveSubcommand(new AdminGiveCommand(plugin));
        this.cooldownSubcommand = new CooldownSubcommand(
            cooldownManager,
            cooldownFormatter,
            playerResolver,
            elementValidator
        );
        this.globalCooldownSubcommand = new GlobalCooldownSubcommand(
            cooldownManager,
            globalCooldownFormatter,
            elementValidator
        );
        this.setCountdownSymbolSubcommand = new SetGlobalCountdownSymbolSubcommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("elementaldragon.admin")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (subCommand) {
            case "give":
                return giveSubcommand.execute(sender, subArgs);

            case "info":
                return infoSubcommand.execute(sender, subArgs);

            case "setcooldown":
                return cooldownSubcommand.executeSetCooldown(sender, subArgs);

            case "clearcooldown":
                return cooldownSubcommand.executeClearCooldown(sender, subArgs);

            case "getcooldown":
                return cooldownSubcommand.executeGetCooldown(sender, subArgs);

            case "setglobalcooldown":
                return globalCooldownSubcommand.executeSetGlobalCooldown(sender, subArgs);

            case "getglobalcooldown":
                return globalCooldownSubcommand.executeGetGlobalCooldown(sender, subArgs);

            case "setcountdownsym":
                return setCountdownSymbolSubcommand.execute(sender, subArgs);

            case "help":
            default:
                showHelp(sender);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("elementaldragon.admin")) {
            return completions;
        }

        if (args.length == 1) {
            // First level: subcommand names
            completions.addAll(Arrays.asList(
                "give", "info", "setcooldown", "clearcooldown", "getcooldown",
                "setglobalcooldown", "getglobalcooldown", "setcountdownsym", "help"
            ));
            String partial = args[0].toLowerCase();
            completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
        } else if (args.length > 1) {
            // Delegate to subcommand
            String subCommand = args[0].toLowerCase();
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

            switch (subCommand) {
                case "give":
                    return giveSubcommand.tabComplete(sender, subArgs);

                case "info":
                    return infoSubcommand.tabComplete(sender, subArgs);

                case "setcooldown":
                    return cooldownSubcommand.tabCompleteSetCooldown(sender, subArgs);

                case "clearcooldown":
                    return cooldownSubcommand.tabCompleteClearCooldown(sender, subArgs);

                case "getcooldown":
                    return cooldownSubcommand.tabCompleteGetCooldown(sender, subArgs);

                case "setglobalcooldown":
                    return globalCooldownSubcommand.tabCompleteSetGlobalCooldown(sender, subArgs);

                case "getglobalcooldown":
                    return globalCooldownSubcommand.tabCompleteGetGlobalCooldown(sender, subArgs);

                case "setcountdownsym":
                    return setCountdownSymbolSubcommand.tabComplete(sender, subArgs);
            }
        }

        return completions;
    }

    /**
     * Shows the help message with all available commands.
     *
     * @param sender the command sender to show help to
     */
    private void showHelp(CommandSender sender) {
        sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("   Elemental Dragon Admin Commands", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("", NamedTextColor.WHITE));

        sender.sendMessage(Component.text("/ed give <player-ref> <ingredients|equipment> <element>", NamedTextColor.YELLOW)
            .append(Component.text(" - Give items to player(s)", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/ed info player <player-ref>", NamedTextColor.YELLOW)
            .append(Component.text(" - Show player's elemental status", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/ed info list", NamedTextColor.YELLOW)
            .append(Component.text(" - List all players' status", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/ed setcooldown <player> <element> <ability> <seconds>", NamedTextColor.YELLOW)
            .append(Component.text(" - Set cooldown", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/ed clearcooldown <player> [element]", NamedTextColor.YELLOW)
            .append(Component.text(" - Clear cooldown(s)", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/ed getcooldown <player>", NamedTextColor.YELLOW)
            .append(Component.text(" - Get cooldowns", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/ed setglobalcooldown <element> <ability> <seconds>", NamedTextColor.YELLOW)
            .append(Component.text(" - Set global cooldown", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/ed getglobalcooldown", NamedTextColor.YELLOW)
            .append(Component.text(" - Get global cooldowns", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/ed setcountdownsym <style> [width]", NamedTextColor.YELLOW)
            .append(Component.text(" - Set countdown progress bar style", NamedTextColor.GRAY)));

        sender.sendMessage(Component.text("", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("Player Selectors: @p (you), @a (all), @s (self), or player name", NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("Elements: lightning, fire, agile, immortal, corrupt", NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("Abilities: 1 or 2 (lightning only has 1)", NamedTextColor.DARK_GRAY));
    }
}
