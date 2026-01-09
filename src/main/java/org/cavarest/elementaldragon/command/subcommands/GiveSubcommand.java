package org.cavarest.elementaldragon.command.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.command.AdminGiveCommand;
import org.cavarest.elementaldragon.command.base.AbstractSubcommand;
import org.cavarest.elementaldragon.fragment.FragmentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Subcommand for giving items to players.
 * Delegates to {@link AdminGiveCommand} for backward compatibility.
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>{@code give <player-ref> <ingredients|equipment> <element-kind>} - Give items to player(s)</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class GiveSubcommand extends AbstractSubcommand {

    private final AdminGiveCommand giveCommand;

    /**
     * Creates a new give subcommand.
     *
     * @param giveCommand the admin give command to delegate to
     */
    public GiveSubcommand(AdminGiveCommand giveCommand) {
        super(
            "give",
            "Give items to players",
            "/ed give <player-ref> <ingredients|equipment> <element-kind>",
            "elementaldragon.admin"
        );
        this.giveCommand = giveCommand;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return giveCommand.handleGive(sender, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First arg: player selectors and names
            completions.addAll(Arrays.asList("@p", "@s", "@a"));
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 2) {
            // Second arg: ingredients or equipment
            completions.addAll(Arrays.asList("ingredients", "equipment"));
        } else if (args.length == 3) {
            // Third arg: fragment canonical names
            completions.addAll(Arrays.asList(FragmentType.getCanonicalNames()));
        }

        // Filter by partial input
        if (args.length > 0) {
            String partial = args[args.length - 1].toLowerCase();
            completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
        }

        return completions;
    }
}