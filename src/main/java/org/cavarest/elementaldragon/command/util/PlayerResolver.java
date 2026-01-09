package org.cavarest.elementaldragon.command.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for resolving player references to actual Player objects.
 * Supports Minecraft player selectors (@p, @s, @a) and player names.
 *
 * <p>Selector support:</p>
 * <ul>
 *   <li>{@code @p} - Nearest player (self if sender is player, otherwise first online)</li>
 *   <li>{@code @s} - Self (sender must be a player)</li>
 *   <li>{@code @a} - All online players</li>
 *   <li>Player name - Exact player name match</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class PlayerResolver {

    /**
     * Resolves a single player from a player reference.
     * For {@code @a}, returns the first online player.
     *
     * @param sender the command sender (for context and error messaging)
     * @param playerRef the player reference (@p, @s, @a, or player name)
     * @return the resolved player, or {@code null} if not found
     */
    public Player resolvePlayer(CommandSender sender, String playerRef) {
        String ref = playerRef.toLowerCase();

        switch (ref) {
            case "@p":
                if (sender instanceof Player) {
                    return (Player) sender;
                }
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                if (!players.isEmpty()) {
                    return players.iterator().next();
                }
                sender.sendMessage(Component.text("No players online!", NamedTextColor.RED));
                return null;

            case "@s":
                if (sender instanceof Player) {
                    return (Player) sender;
                }
                sender.sendMessage(Component.text("Cannot use @s from console.", NamedTextColor.RED));
                return null;

            case "@a":
                // @a returns all players, but resolvePlayer returns single - return first online
                Collection<? extends Player> allPlayers = Bukkit.getOnlinePlayers();
                if (!allPlayers.isEmpty()) {
                    return allPlayers.iterator().next();
                }
                sender.sendMessage(Component.text("No players online!", NamedTextColor.RED));
                return null;

            default:
                Player target = Bukkit.getPlayer(playerRef);
                if (target == null) {
                    sender.sendMessage(Component.text(
                        "Player '" + playerRef + "' not found!",
                        NamedTextColor.RED
                    ));
                    return null;
                }
                return target;
        }
    }

    /**
     * Resolves multiple players from a player reference.
     * Supports @p (nearest), @s (self), @a (all), or exact player name.
     *
     * @param sender the command sender (for context and error messaging)
     * @param playerRef the player reference (@p, @s, @a, or player name)
     * @return a list of resolved players (empty list if no players found)
     */
    public List<Player> resolvePlayers(CommandSender sender, String playerRef) {
        List<Player> players = new ArrayList<>();
        String ref = playerRef.toLowerCase();

        switch (ref) {
            case "@p":
                if (sender instanceof Player) {
                    players.add((Player) sender);
                } else {
                    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                    if (!onlinePlayers.isEmpty()) {
                        players.add(onlinePlayers.iterator().next());
                    }
                }
                break;

            case "@s":
                if (sender instanceof Player) {
                    players.add((Player) sender);
                } else {
                    sender.sendMessage(Component.text("Cannot use @s from console.", NamedTextColor.RED));
                }
                break;

            case "@a":
                // Return all online players
                players.addAll(Bukkit.getOnlinePlayers());
                if (players.isEmpty()) {
                    sender.sendMessage(Component.text("No players online!", NamedTextColor.RED));
                }
                break;

            default:
                // Try to find player by name
                Player target = Bukkit.getPlayer(playerRef);
                if (target != null) {
                    players.add(target);
                } else {
                    sender.sendMessage(Component.text(
                        "Player '" + playerRef + "' not found!",
                        NamedTextColor.RED
                    ));
                }
                break;
        }

        return players;
    }
}