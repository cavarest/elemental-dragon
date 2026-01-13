package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.command.util.ElementValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ElementValidator.
 */
public class ElementValidatorTest {

    private ElementValidator elementValidator;

    @BeforeEach
    public void setUp() {
        elementValidator = new ElementValidator();
    }

    // ==================== isValidElement tests ====================

    @Test
    @DisplayName("isValidElement returns true for lightning")
    public void testIsValidElementLightning() {
        assertTrue(elementValidator.isValidElement("lightning"));
    }

    @Test
    @DisplayName("isValidElement returns true for fire")
    public void testIsValidElementFire() {
        assertTrue(elementValidator.isValidElement("fire"));
    }

    @Test
    @DisplayName("isValidElement returns true for agile")
    public void testIsValidElementAgile() {
        assertTrue(elementValidator.isValidElement("agile"));
    }

    @Test
    @DisplayName("isValidElement returns true for immortal")
    public void testIsValidElementImmortal() {
        assertTrue(elementValidator.isValidElement("immortal"));
    }

    @Test
    @DisplayName("isValidElement returns true for corrupt")
    public void testIsValidElementCorrupt() {
        assertTrue(elementValidator.isValidElement("corrupt"));
    }

    @Test
    @DisplayName("isValidElement is case-insensitive")
    public void testIsValidElementCaseInsensitive() {
        assertTrue(elementValidator.isValidElement("LIGHTNING"));
        assertTrue(elementValidator.isValidElement("FIRE"));
        assertTrue(elementValidator.isValidElement("AGILE"));
        assertTrue(elementValidator.isValidElement("IMMORTAL"));
        assertTrue(elementValidator.isValidElement("CORRUPT"));
        assertTrue(elementValidator.isValidElement("Lightning"));
        assertTrue(elementValidator.isValidElement("FiRe"));
    }

    @Test
    @DisplayName("isValidElement returns false for invalid element")
    public void testIsValidElementInvalid() {
        assertFalse(elementValidator.isValidElement("invalid"));
        assertFalse(elementValidator.isValidElement("water"));
        assertFalse(elementValidator.isValidElement("earth"));
        assertFalse(elementValidator.isValidElement("air"));
    }

    @Test
    @DisplayName("isValidElement returns false for null")
    public void testIsValidElementNull() {
        assertFalse(elementValidator.isValidElement(null));
    }

    @Test
    @DisplayName("isValidElement returns false for empty string")
    public void testIsValidElementEmpty() {
        assertFalse(elementValidator.isValidElement(""));
    }

    // ==================== getValidElements tests ====================

    @Test
    @DisplayName("getValidElements returns all five elements")
    public void testGetValidElementsReturnsAll() {
        List<String> elements = elementValidator.getValidElements();

        assertEquals(5, elements.size());
        assertTrue(elements.contains("lightning"));
        assertTrue(elements.contains("fire"));
        assertTrue(elements.contains("agile"));
        assertTrue(elements.contains("immortal"));
        assertTrue(elements.contains("corrupt"));
    }

    @Test
    @DisplayName("getValidElements returns unmodifiable list")
    public void testGetValidElementsUnmodifiable() {
        List<String> elements = elementValidator.getValidElements();

        assertThrows(UnsupportedOperationException.class, () -> {
            elements.add("newelement");
        });
    }

    // ==================== getCanonicalName tests ====================

    @Test
    @DisplayName("getCanonicalName returns lowercase for valid elements")
    public void testGetCanonicalNameValid() {
        assertEquals("lightning", elementValidator.getCanonicalName("LIGHTNING"));
        assertEquals("fire", elementValidator.getCanonicalName("FIRE"));
        assertEquals("agile", elementValidator.getCanonicalName("Agile"));
        assertEquals("immortal", elementValidator.getCanonicalName("ImMoRtAl"));
        assertEquals("corrupt", elementValidator.getCanonicalName("CORRUPT"));
    }

    @Test
    @DisplayName("getCanonicalName returns lowercase for already lowercase valid elements")
    public void testGetCanonicalNameAlreadyLowercase() {
        assertEquals("lightning", elementValidator.getCanonicalName("lightning"));
        assertEquals("fire", elementValidator.getCanonicalName("fire"));
    }

    @Test
    @DisplayName("getCanonicalName returns input unchanged for invalid elements")
    public void testGetCanonicalNameInvalid() {
        assertEquals("water", elementValidator.getCanonicalName("water"));
        assertEquals("EARTH", elementValidator.getCanonicalName("EARTH"));
        assertEquals("Air", elementValidator.getCanonicalName("Air"));
    }

    @Test
    @DisplayName("getCanonicalName returns null for null input")
    public void testGetCanonicalNameNull() {
        assertNull(elementValidator.getCanonicalName(null));
    }

    // ==================== getValidElementsString tests ====================

    @Test
    @DisplayName("getValidElementsString returns comma-separated list")
    public void testGetValidElementsString() {
        String elementsString = elementValidator.getValidElementsString();

        assertNotNull(elementsString);
        assertTrue(elementsString.contains("lightning"));
        assertTrue(elementsString.contains("fire"));
        assertTrue(elementsString.contains("agile"));
        assertTrue(elementsString.contains("immortal"));
        assertTrue(elementsString.contains("corrupt"));
        assertTrue(elementsString.contains(","));
    }

    // ==================== isValidAbilityNumber tests ====================

    @Test
    @DisplayName("isValidAbilityNumber returns true for 1")
    public void testIsValidAbilityNumber1() {
        assertTrue(elementValidator.isValidAbilityNumber(1));
    }

    @Test
    @DisplayName("isValidAbilityNumber returns true for 2")
    public void testIsValidAbilityNumber2() {
        assertTrue(elementValidator.isValidAbilityNumber(2));
    }

    @Test
    @DisplayName("isValidAbilityNumber returns false for 0")
    public void testIsValidAbilityNumber0() {
        assertFalse(elementValidator.isValidAbilityNumber(0));
    }

    @Test
    @DisplayName("isValidAbilityNumber returns false for 3")
    public void testIsValidAbilityNumber3() {
        assertFalse(elementValidator.isValidAbilityNumber(3));
    }

    @Test
    @DisplayName("isValidAbilityNumber returns false for negative numbers")
    public void testIsValidAbilityNumberNegative() {
        assertFalse(elementValidator.isValidAbilityNumber(-1));
        assertFalse(elementValidator.isValidAbilityNumber(-10));
    }

    @Test
    @DisplayName("isValidAbilityNumber returns false for large numbers")
    public void testIsValidAbilityNumberLarge() {
        assertFalse(elementValidator.isValidAbilityNumber(100));
        assertFalse(elementValidator.isValidAbilityNumber(Integer.MAX_VALUE));
    }

    // ==================== hasOnlyAbility1 tests ====================

    @Test
    @DisplayName("hasOnlyAbility1 returns true for lightning")
    public void testHasOnlyAbility1Lightning() {
        assertTrue(elementValidator.hasOnlyAbility1("lightning"));
        assertTrue(elementValidator.hasOnlyAbility1("LIGHTNING"));
    }

    @Test
    @DisplayName("hasOnlyAbility1 returns false for fire")
    public void testHasOnlyAbility1Fire() {
        assertFalse(elementValidator.hasOnlyAbility1("fire"));
    }

    @Test
    @DisplayName("hasOnlyAbility1 returns false for agile")
    public void testHasOnlyAbility1Agile() {
        assertFalse(elementValidator.hasOnlyAbility1("agile"));
    }

    @Test
    @DisplayName("hasOnlyAbility1 returns false for immortal")
    public void testHasOnlyAbility1Immortal() {
        assertFalse(elementValidator.hasOnlyAbility1("immortal"));
    }

    @Test
    @DisplayName("hasOnlyAbility1 returns false for corrupt")
    public void testHasOnlyAbility1Corrupt() {
        assertFalse(elementValidator.hasOnlyAbility1("corrupt"));
    }

    @Test
    @DisplayName("hasOnlyAbility1 returns false for invalid elements")
    public void testHasOnlyAbility1Invalid() {
        assertFalse(elementValidator.hasOnlyAbility1("invalid"));
        assertFalse(elementValidator.hasOnlyAbility1("water"));
    }

    @Test
    @DisplayName("hasOnlyAbility1 returns false for null")
    public void testHasOnlyAbility1Null() {
        assertFalse(elementValidator.hasOnlyAbility1(null));
    }
}
