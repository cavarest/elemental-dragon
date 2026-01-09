package org.cavarest.elementaldragon.fragment;

import java.util.List;

/**
 * Immutable data class representing a fragment ability definition.
 * Contains all metadata about a single ability including name, description,
 * aliases, and success messages.
 *
 * <p>This class serves as the single source of truth for ability metadata,
 * eliminating duplication across commands and other components.</p>
 */
public class AbilityDefinition {
    private final int number;
    private final String name;
    private final String description;
    private final List<String> aliases;
    private final String successMessage;

    /**
     * Creates a new ability definition.
     *
     * @param number The ability number (1 or 2)
     * @param name The display name of the ability
     * @param description Brief description of what the ability does
     * @param aliases List of command aliases that can trigger this ability
     * @param successMessage Message shown when ability is successfully used
     */
    public AbilityDefinition(int number, String name, String description,
                            List<String> aliases, String successMessage) {
        this.number = number;
        this.name = name;
        this.description = description;
        this.aliases = List.copyOf(aliases); // Immutable copy
        this.successMessage = successMessage;
    }

    /**
     * Gets the ability number.
     * @return The ability number (1 or 2)
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the ability name.
     * @return The display name of the ability
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the ability description.
     * @return Brief description of what the ability does
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the list of command aliases.
     * @return Immutable list of aliases that can trigger this ability
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Gets the success message.
     * @return Message shown when ability is successfully used
     */
    public String getSuccessMessage() {
        return successMessage;
    }

    /**
     * Checks if the given alias matches this ability.
     *
     * @param alias The alias to check (case-insensitive)
     * @return true if the alias matches, false otherwise
     */
    public boolean matchesAlias(String alias) {
        return aliases.contains(alias.toLowerCase());
    }
}