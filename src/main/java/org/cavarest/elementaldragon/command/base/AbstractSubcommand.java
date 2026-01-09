package org.cavarest.elementaldragon.command.base;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

/**
 * Abstract base class for subcommands providing common functionality.
 * Implements the Template Method pattern for consistent subcommand behavior.
 *
 * <p>This class provides:</p>
 * <ul>
 *   <li>Automatic permission checking with customizable messages</li>
 *   <li>Standard error message formatting</li>
 *   <li>Common utility methods for argument validation</li>
 *   <li>Consistent message styling across all subcommands</li>
 * </ul>
 *
 * <p>Subclasses should:</p>
 * <ul>
 *   <li>Call super constructor with name, description, usage, and permission</li>
 *   <li>Implement {@link #execute(CommandSender, String[])} with specific logic</li>
 *   <li>Implement {@link #tabComplete(CommandSender, String[])} for tab completion</li>
 *   <li>Use provided utility methods for consistent error handling</li>
 * </ul>
 *
 * @see Subcommand
 * @since 1.0.3
 */
public abstract class AbstractSubcommand implements Subcommand {

    private final String name;
    private final String description;
    private final String usage;
    private final String permission;

    /**
     * Creates a new abstract subcommand.
     *
     * @param name the subcommand name (e.g., "give", "info")
     * @param description brief description of what this subcommand does
     * @param usage the usage syntax (e.g., "/ed give &lt;player&gt; &lt;type&gt; &lt;element&gt;")
     * @param permission the permission node required, or {@code null} for no specific permission
     */
    protected AbstractSubcommand(String name, String description, String usage, String permission) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.permission = permission;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    /**
     * Sends a permission denied message to the sender.
     *
     * @param sender the command sender to notify
     */
    protected void sendNoPermission(CommandSender sender) {
        sender.sendMessage(Component.text(
            "You don't have permission to use this command!",
            NamedTextColor.RED
        ));
    }

    /**
     * Sends a usage message to the sender.
     * Uses the subcommand's defined usage string.
     *
     * @param sender the command sender to notify
     */
    protected void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("Usage: " + usage, NamedTextColor.RED));
    }

    /**
     * Sends a custom usage message to the sender.
     *
     * @param sender the command sender to notify
     * @param customUsage the custom usage string to display
     */
    protected void sendUsage(CommandSender sender, String customUsage) {
        sender.sendMessage(Component.text("Usage: " + customUsage, NamedTextColor.RED));
    }

    /**
     * Sends an error message to the sender.
     *
     * @param sender the command sender to notify
     * @param message the error message to display
     */
    protected void sendError(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.RED));
    }

    /**
     * Sends a success message to the sender.
     *
     * @param sender the command sender to notify
     * @param message the success message to display
     */
    protected void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.GREEN));
    }

    /**
     * Sends an info message to the sender.
     *
     * @param sender the command sender to notify
     * @param message the info message to display
     */
    protected void sendInfo(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.AQUA));
    }

    /**
     * Checks if the provided arguments meet the minimum length requirement.
     *
     * @param sender the command sender (for error messaging)
     * @param args the arguments to check
     * @param minLength the minimum required length
     * @return {@code true} if valid, {@code false} if insufficient arguments
     */
    protected boolean validateMinArgs(CommandSender sender, String[] args, int minLength) {
        if (args.length < minLength) {
            sendUsage(sender);
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided arguments are exactly the required length.
     *
     * @param sender the command sender (for error messaging)
     * @param args the arguments to check
     * @param exactLength the exact required length
     * @return {@code true} if valid, {@code false} if wrong number of arguments
     */
    protected boolean validateExactArgs(CommandSender sender, String[] args, int exactLength) {
        if (args.length != exactLength) {
            sendUsage(sender);
            return false;
        }
        return true;
    }

    /**
     * Attempts to parse an integer from a string.
     *
     * @param sender the command sender (for error messaging)
     * @param value the string to parse
     * @param parameterName the name of the parameter (for error messages)
     * @return the parsed integer, or {@code null} if invalid
     */
    protected Integer parseInteger(CommandSender sender, String value, String parameterName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            sendError(sender, "Invalid " + parameterName + ": " + value + " (must be a number)");
            return null;
        }
    }

    /**
     * Validates that an integer is within a specified range.
     *
     * @param sender the command sender (for error messaging)
     * @param value the value to check
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @param parameterName the name of the parameter (for error messages)
     * @return {@code true} if valid, {@code false} if out of range
     */
    protected boolean validateRange(CommandSender sender, int value, int min, int max, String parameterName) {
        if (value < min || value > max) {
            sendError(sender, parameterName + " must be between " + min + " and " + max);
            return false;
        }
        return true;
    }
}