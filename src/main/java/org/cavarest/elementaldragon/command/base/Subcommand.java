package org.cavarest.elementaldragon.command.base;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Interface for all subcommands in the Elemental Dragon plugin.
 * Implements the Command Pattern for clean separation of concerns and extensibility.
 *
 * <p>Each subcommand implementation should:</p>
 * <ul>
 *   <li>Handle a specific administrative function (e.g., give, info, cooldown management)</li>
 *   <li>Validate its own arguments and permissions</li>
 *   <li>Provide tab completion for its arguments</li>
 *   <li>Return clear success/failure status</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * public class InfoSubcommand extends AbstractSubcommand {
 *     public InfoSubcommand(...) {
 *         super("info", "Show player information", "/ed info &lt;player|list&gt;", "elementaldragon.admin.info");
 *     }
 *
 *     {@literal @}Override
 *     public boolean execute(CommandSender sender, String[] args) {
 *         // Implementation
 *         return true;
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;String&gt; tabComplete(CommandSender sender, String[] args) {
 *         // Tab completion logic
 *         return Arrays.asList("player", "list");
 *     }
 * }
 * </pre>
 *
 * @see AbstractSubcommand
 * @since 1.0.3
 */
public interface Subcommand {

    /**
     * Gets the name of this subcommand.
     * This is the identifier used in {@code /elementaldragon <name>}.
     *
     * @return the subcommand name (e.g., "give", "info", "setcooldown")
     */
    String getName();

    /**
     * Gets a brief description of what this subcommand does.
     * Used in help text and documentation.
     *
     * @return a human-readable description of the subcommand's purpose
     */
    String getDescription();

    /**
     * Gets the usage syntax for this subcommand.
     * Should include all required and optional parameters.
     *
     * @return the usage string (e.g., "/ed give &lt;player&gt; &lt;type&gt; &lt;element&gt;")
     */
    String getUsage();

    /**
     * Executes this subcommand with the given arguments.
     *
     * <p>Implementation notes:</p>
     * <ul>
     *   <li>Arguments do NOT include the subcommand name itself</li>
     *   <li>Validate all arguments before executing</li>
     *   <li>Send appropriate error messages for invalid input</li>
     *   <li>Return true if handled (even on error), false only if completely invalid</li>
     * </ul>
     *
     * @param sender the command sender (player or console)
     * @param args the arguments passed after the subcommand name (NOT including subcommand itself)
     * @return {@code true} if the command was handled, {@code false} otherwise
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Provides tab completion suggestions for this subcommand.
     *
     * <p>Implementation notes:</p>
     * <ul>
     *   <li>Arguments do NOT include the subcommand name itself</li>
     *   <li>Return suggestions based on the current argument position</li>
     *   <li>Filter suggestions based on partial input</li>
     *   <li>Return empty list if no suggestions available</li>
     * </ul>
     *
     * @param sender the command sender requesting completions
     * @param args the current arguments (NOT including subcommand itself)
     * @return a list of tab completion suggestions
     */
    List<String> tabComplete(CommandSender sender, String[] args);

    /**
     * Gets the permission required to execute this subcommand.
     * If {@code null}, no specific permission is required (falls back to parent command permission).
     *
     * @return the permission node, or {@code null} if no specific permission required
     */
    String getPermission();

    /**
     * Checks if a sender has permission to execute this subcommand.
     * Default implementation checks the permission returned by {@link #getPermission()}.
     *
     * @param sender the command sender to check
     * @return {@code true} if the sender has permission, {@code false} otherwise
     */
    default boolean hasPermission(CommandSender sender) {
        String permission = getPermission();
        return permission == null || sender.hasPermission(permission);
    }
}