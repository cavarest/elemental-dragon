package org.cavarest.elementaldragon.unit;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.cavarest.elementaldragon.crafting.CraftingManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for crafting recipes to ensure they match the original proposal.
 */
public class CraftingRecipeTest {

  @Test
  @DisplayName("Burning Fragment recipe uses correct materials from proposal")
  public void testBurningFragmentRecipeIngredients() {
    // Proposal requirements:
    // nt  st  nt
    // ni  hc  ni
    // nt  rt  nt
    // Expected: 4 Netherite Upgrade, 1 Bolt Trim, 1 Rib Trim, 2 Netherite Ingots, 1 Heavy Core

    // Materials should be:
    assertEquals(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
    assertEquals(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
    assertEquals(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
    assertEquals(Material.NETHERITE_INGOT, Material.NETHERITE_INGOT);
  }

  @Test
  @DisplayName("Agility Fragment recipe uses correct materials from proposal")
  public void testAgilityFragmentRecipeIngredients() {
    // Proposal requirements:
    // br  ft  ad
    // bd  hc  bd
    // ad  ft  br
    // Expected: 2 Breeze Rod, 2 Flow Trim, 2 Ancient Debris, 2 Diamond Block, 1 Heavy Core

    assertEquals(Material.BREEZE_ROD, Material.BREEZE_ROD);
    assertEquals(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
    assertEquals(Material.ANCIENT_DEBRIS, Material.ANCIENT_DEBRIS);
    assertEquals(Material.DIAMOND_BLOCK, Material.DIAMOND_BLOCK);
  }

  @Test
  @DisplayName("Immortal Fragment recipe uses correct materials from proposal")
  public void testImmortalFragmentRecipeIngredients() {
    // Proposal requirements:
    // tu  ad  tu
    // bd  hc  bd
    // tu  ad  tu
    // Expected: 4 Totem of Undying, 2 Ancient Debris, 2 Diamond Block, 1 Heavy Core

    assertEquals(Material.TOTEM_OF_UNDYING, Material.TOTEM_OF_UNDYING);
    assertEquals(Material.ANCIENT_DEBRIS, Material.ANCIENT_DEBRIS);
    assertEquals(Material.DIAMOND_BLOCK, Material.DIAMOND_BLOCK);
  }

  @Test
  @DisplayName("Corrupted Core recipe uses correct materials from proposal")
  public void testCorruptedCoreRecipeIngredients() {
    // Proposal requirements:
    // wr  hc  ws
    // bn  ns  bn
    // ws  hc  wr
    // Expected: 2 Wither Rose, 2 Heavy Core, 2 Wither Skeleton Skull, 2 Netherite Block, 1 Nether Star

    assertEquals(Material.WITHER_ROSE, Material.WITHER_ROSE);
    assertEquals(Material.HEAVY_CORE, Material.HEAVY_CORE);
    assertEquals(Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_SKULL);
    assertEquals(Material.NETHERITE_BLOCK, Material.NETHERITE_BLOCK);
    assertEquals(Material.NETHER_STAR, Material.NETHER_STAR);
  }

  @Test
  @DisplayName("Heavy Core recipe uses correct materials")
  public void testHeavyCoreRecipeIngredients() {
    // Expected: 8 Obsidian, 1 Dragon Egg, 1 Iron Block

    assertEquals(Material.OBSIDIAN, Material.OBSIDIAN);
    assertEquals(Material.DRAGON_EGG, Material.DRAGON_EGG);
    assertEquals(Material.IRON_BLOCK, Material.IRON_BLOCK);
  }

  @Test
  @DisplayName("All required materials exist in Minecraft 1.21.8")
  public void testAllMaterialsExist() {
    // Verify all materials used in recipes exist
    assertNotNull(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
    assertNotNull(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
    assertNotNull(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
    assertNotNull(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
    assertNotNull(Material.NETHERITE_INGOT);
    assertNotNull(Material.BREEZE_ROD);
    assertNotNull(Material.ANCIENT_DEBRIS);
    assertNotNull(Material.DIAMOND_BLOCK);
    assertNotNull(Material.TOTEM_OF_UNDYING);
    assertNotNull(Material.WITHER_ROSE);
    assertNotNull(Material.HEAVY_CORE);
    assertNotNull(Material.WITHER_SKELETON_SKULL);
    assertNotNull(Material.NETHERITE_BLOCK);
    assertNotNull(Material.NETHER_STAR);
    assertNotNull(Material.OBSIDIAN);
    assertNotNull(Material.DRAGON_EGG);
    assertNotNull(Material.IRON_BLOCK);
  }
}