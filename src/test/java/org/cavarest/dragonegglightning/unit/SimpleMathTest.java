package org.cavarest.dragonegglightning.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify Gradle testing infrastructure works
 * without requiring external dependencies.
 */
class SimpleMathTest {

    @Test
    @DisplayName("Test basic addition")
    void testBasicAddition() {
        assertEquals(4, 2 + 2, "Basic addition should work");
    }

    @Test
    @DisplayName("Test basic subtraction")
    void testBasicSubtraction() {
        assertEquals(0, 5 - 5, "Basic subtraction should work");
    }

    @Test
    @DisplayName("Test basic multiplication")
    void testBasicMultiplication() {
        assertEquals(12, 3 * 4, "Basic multiplication should work");
    }

    @Test
    @DisplayName("Test basic division")
    void testBasicDivision() {
        assertEquals(2, 10 / 5, "Basic division should work");
    }

    @Test
    @DisplayName("Test string operations")
    void testStringOperations() {
        String test = "DragonEgg";
        assertTrue(test.startsWith("Dragon"), "String should start with Dragon");
        assertTrue(test.endsWith("Egg"), "String should end with Egg");
        assertEquals(9, test.length(), "String length should be 9");
    }
}
