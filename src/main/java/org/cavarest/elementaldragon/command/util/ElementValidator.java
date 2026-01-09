package org.cavarest.elementaldragon.command.util;

import org.cavarest.elementaldragon.cooldown.CooldownManager;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for validating and managing element names.
 * Provides canonical element names and validation for command arguments.
 *
 * <p>Supported elements:</p>
 * <ul>
 *   <li>{@code lightning} - Lightning ability (dragon egg)</li>
 *   <li>{@code fire} - Fire Fragment (Burning Fragment)</li>
 *   <li>{@code agile} - Agility Fragment</li>
 *   <li>{@code immortal} - Immortal Fragment</li>
 *   <li>{@code corrupt} - Corrupted Core Fragment</li>
 * </ul>
 *
 * @since 1.0.3
 */
public class ElementValidator {

    /**
     * List of all valid element names in canonical form.
     */
    private static final List<String> VALID_ELEMENTS = Arrays.asList(
        CooldownManager.LIGHTNING,
        CooldownManager.FIRE,
        CooldownManager.AGILE,
        CooldownManager.IMMORTAL,
        CooldownManager.CORRUPT
    );

    /**
     * Checks if the given element name is valid.
     *
     * @param elementName the element name to validate (case-insensitive)
     * @return {@code true} if valid, {@code false} otherwise
     */
    public boolean isValidElement(String elementName) {
        return elementName != null && VALID_ELEMENTS.contains(elementName.toLowerCase());
    }

    /**
     * Gets the list of all valid element names.
     *
     * @return an immutable list of valid element names
     */
    public List<String> getValidElements() {
        return VALID_ELEMENTS;
    }

    /**
     * Gets the canonical form of an element name.
     * If the element is invalid, returns the input unchanged.
     *
     * @param elementName the element name to canonicalize
     * @return the canonical element name (lowercase)
     */
    public String getCanonicalName(String elementName) {
        if (elementName == null) {
            return null;
        }
        String lower = elementName.toLowerCase();
        return VALID_ELEMENTS.contains(lower) ? lower : elementName;
    }

    /**
     * Gets a formatted string of all valid elements for error messages.
     *
     * @return a comma-separated string of valid element names
     */
    public String getValidElementsString() {
        return String.join(", ", VALID_ELEMENTS);
    }

    /**
     * Validates an ability number for elements.
     * Valid ability numbers are 1 or 2.
     * Lightning only has ability 1, fragments have both 1 and 2.
     *
     * @param abilityNum the ability number to validate
     * @return {@code true} if valid (1 or 2), {@code false} otherwise
     */
    public boolean isValidAbilityNumber(int abilityNum) {
        return abilityNum == 1 || abilityNum == 2;
    }

    /**
     * Checks if the given element only has ability 1 (no ability 2).
     * Currently, only lightning falls into this category.
     *
     * @param elementName the element name to check
     * @return {@code true} if the element only has ability 1
     */
    public boolean hasOnlyAbility1(String elementName) {
        return CooldownManager.LIGHTNING.equalsIgnoreCase(elementName);
    }
}