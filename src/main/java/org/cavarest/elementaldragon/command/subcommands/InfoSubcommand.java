package org.cavarest.elementaldragon.command.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.command.base.AbstractSubcommand;
import org.cavarest.elementaldragon.command.display.PlayerInfoFormatter;
import org.cavarest.elementaldragon.command.util.PlayerResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Subcommand for displaying player information.
 * Handles both individual player info and server-wide status lists.
 *
 * <p>Subcommands:</p>
 * <ul>
 *   <li>{@code info player <player-ref>} - Show detailed info for a player</li>
 *   <li>{@code info list} - Show table of all tracked players</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class InfoSubcommand extends AbstractSubcommand {

    private final PlayerInfoFormatter formatter;
    private final PlayerResolver playerResolver;

    /**
     * Creates a new info subcommand.
     *
     * @param formatter the formatter for displaying player information
     * @param playerResolver the resolver for player references
     */
    public InfoSubcommand(PlayerInfoFormatter formatter, PlayerResolver playerResolver) {
        super(
            "info",
            "Show player information or list all players",
            "/ed info <player|list> [player-ref]",
            "elementaldragon.admin"
        );
        this.formatter = formatter;
        this.playerResolver = playerResolver;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!validateMinArgs(sender, args, 1)) {
            sendUsage(sender, "/ed info <player|list>");
            return true;
        }

        String infoType = args[0].toLowerCase();

        switch (infoType) {
            case "player":
            case "p":
                return handleInfoPlayer(sender, args);
            case "list":
            case "l":
                return handleInfoList(sender);
            default:
                sendError(sender, "Unknown info type: " + infoType + ". Use 'player' or 'list'.");
                return true;
        }
    }

    /**
     * Handles the info player subcommand.
     *
     * @param sender the command sender
     * @param args the command arguments (including "player" as args[0])
     * @return {@code true} if handled
     */
    private boolean handleInfoPlayer(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendUsage(sender, "/ed info player <player-ref>");
            return true;
        }

        String playerRef = args[1];
        List<Player> targets = playerResolver.resolvePlayers(sender, playerRef);
        if (targets.isEmpty()) {
            return true;
        }

        // For @a, show a summary; otherwise show detailed info
        boolean showAll = playerRef.equalsIgnoreCase("@a") && targets.size() > 1;

        for (Player target : targets) {
            formatter.displayPlayerInfo(sender, target, showAll);
        }

        return true;
    }

    /**
     * Handles the info list subcommand.
     *
     * @param sender the command sender
     * @return {@code true} if handled
     */
    private boolean handleInfoList(CommandSender sender) {
        formatter.displayPlayerList(sender);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: player or list
            completions.addAll(Arrays.asList("player", "list"));
            String partial = args[0].toLowerCase();
            completions.removeIf(c -> !c.startsWith(partial));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p"))) {
            // Second argument for "info player": player selectors and names
            completions.addAll(Arrays.asList("@p", "@s", "@a"));
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            String partial = args[1].toLowerCase();
            completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
        }

        return completions;
    }
}