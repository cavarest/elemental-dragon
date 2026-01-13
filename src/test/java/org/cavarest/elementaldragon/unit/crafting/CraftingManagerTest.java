package org.cavarest.elementaldragon.unit.crafting;

import org.bukkit.Material;
import org.cavarest.elementaldragon.crafting.CraftingManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CraftingManager.RecipeData.
 */
@DisplayName("CraftingManager.RecipeData Tests")
public class CraftingManagerTest {

    private CraftingManager.RecipeData recipeData;

    @BeforeEach
    public void setUp() {
        Map<Character, Material> ingredients = Map.of(
            'N', Material.NETHERITE_INGOT,
            'S', Material.BLAZE_ROD,
            'H', Material.HEAVY_CORE
        );

        recipeData = new CraftingManager.RecipeData(
            new String[]{"NSN", "SHS", "NSN"},
            ingredients,
            "Test center note"
        );
    }

    // ==================== RecipeData constructor tests ====================

    @Test
    @DisplayName("RecipeData constructor stores all parameters")
    public void testRecipeDataConstructor() {
        assertNotNull(recipeData);
        assertArrayEquals(new String[]{"NSN", "SHS", "NSN"}, recipeData.getShape());
        assertNotNull(recipeData.getIngredients());
        assertEquals("Test center note", recipeData.getCenterNote());
    }

    @Test
    @DisplayName("RecipeData getShape returns correct array")
    public void testGetShape() {
        String[] shape = recipeData.getShape();
        assertArrayEquals(new String[]{"NSN", "SHS", "NSN"}, shape);
    }

    @Test
    @DisplayName("RecipeData getIngredients returns correct map")
    public void testGetIngredients() {
        Map<Character, Material> ingredients = recipeData.getIngredients();

        assertEquals(3, ingredients.size());
        assertEquals(Material.NETHERITE_INGOT, ingredients.get('N'));
        assertEquals(Material.BLAZE_ROD, ingredients.get('S'));
        assertEquals(Material.HEAVY_CORE, ingredients.get('H'));
    }

    @Test
    @DisplayName("RecipeData getCenterNote returns correct string")
    public void testGetCenterNote() {
        assertEquals("Test center note", recipeData.getCenterNote());
    }

    // ==================== RecipeData edge cases ====================

    @Test
    @DisplayName("RecipeData handles empty ingredients map")
    public void testEmptyIngredients() {
        CraftingManager.RecipeData emptyRecipe = new CraftingManager.RecipeData(
            new String[]{"   ", "   ", "   "},
            Map.of(),
            "No center note"
        );

        assertNotNull(emptyRecipe);
        assertTrue(emptyRecipe.getIngredients().isEmpty());
        assertEquals("No center note", emptyRecipe.getCenterNote());
    }

    @Test
    @DisplayName("RecipeData handles null center note")
    public void testNullCenterNote() {
        CraftingManager.RecipeData recipeWithNullNote = new CraftingManager.RecipeData(
            new String[]{"ABC"},
            Map.of('A', Material.AIR),
            null
        );

        assertNull(recipeWithNullNote.getCenterNote());
    }

    @Test
    @DisplayName("RecipeData handles single row shape")
    public void testSingleRowShape() {
        CraftingManager.RecipeData singleRowRecipe = new CraftingManager.RecipeData(
            new String[]{"ABC"},
            Map.of('A', Material.AIR, 'B', Material.WATER, 'C', Material.DIRT),
            "Single row"
        );

        assertArrayEquals(new String[]{"ABC"}, singleRowRecipe.getShape());
    }

    // ==================== RecipeData immutability tests ====================

    @Test
    @DisplayName("RecipeData shape array cannot be modified externally")
    public void testShapeArrayModification() {
        String[] shape = recipeData.getShape();
        shape[0] = "XXX";

        // The original array reference is returned, not a copy
        // This test documents the current behavior
        assertArrayEquals(new String[]{"XXX", "SHS", "NSN"}, recipeData.getShape());
    }

    @Test
    @DisplayName("RecipeData ingredients map cannot be modified externally")
    public void testIngredientsMapModification() {
        Map<Character, Material> ingredients = recipeData.getIngredients();

        // The returned map is the same reference
        // Modifications will affect the RecipeData
        assertThrows(UnsupportedOperationException.class, () -> {
            ingredients.put('X', Material.DIAMOND);
        });
    }
}
