package org.cavarest.elementaldragon.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.item.ElementalItems;

/**
 * Manages all crafting recipes for the Elemental Dragon plugin.
 * Registers custom recipes for Heavy Core and all fragment types.
 */
public class CraftingManager {

  private final ElementalDragon plugin;
  private final NamespacedKey heavyCoreKey;
  private final NamespacedKey burningFragmentKey;
  private final NamespacedKey agilityFragmentKey;
  private final NamespacedKey immortalFragmentKey;
  private final NamespacedKey corruptedCoreKey;

  /**
   * Create a new CraftingManager.
   *
   * @param plugin The plugin instance
   */
  public CraftingManager(ElementalDragon plugin) {
    this.plugin = plugin;
    this.heavyCoreKey = new NamespacedKey(plugin, "heavy_core");
    this.burningFragmentKey = new NamespacedKey(plugin, "burning_fragment");
    this.agilityFragmentKey = new NamespacedKey(plugin, "agility_fragment");
    this.immortalFragmentKey = new NamespacedKey(plugin, "immortal_fragment");
    this.corruptedCoreKey = new NamespacedKey(plugin, "corrupted_core");

    registerRecipes();
  }

  /**
   * Register all custom recipes with the Bukkit recipe manager.
   * Note: Heavy Core is now a vanilla item, no custom recipe needed.
   */
  public void registerRecipes() {
    // Heavy Core is a vanilla item, no custom recipe needed

    try {
      registerBurningFragmentRecipe();
    } catch (Exception e) {
      plugin.getLogger().severe("Failed to register Burning Fragment recipe: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      registerAgilityFragmentRecipe();
    } catch (Exception e) {
      plugin.getLogger().severe("Failed to register Agility Fragment recipe: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      registerImmortalFragmentRecipe();
    } catch (Exception e) {
      plugin.getLogger().severe("Failed to register Immortal Fragment recipe: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      registerCorruptedCoreRecipe();
    } catch (Exception e) {
      plugin.getLogger().severe("Failed to register Corrupted Core recipe: " + e.getMessage());
      e.printStackTrace();
    }

    plugin.getLogger().info("All crafting recipes registered successfully!");
  }

  /**
   * Register the Burning Fragment recipe.
   * Pattern from original proposal:
   * nt	st	nt
   * ni	hc	ni
   * nt	rt	nt
   * Where:
   * nt = Netherite Upgrade Smithing Template
   * st = Stout Armor Trim (using BOLT_ARMOR_TRIM_SMITHING_TEMPLATE)
   * ni = Netherite Ingot
   * hc = Heavy Core (vanilla HEAVY_CORE item)
   * rt = Rib Armor Trim (using RIB_ARMOR_TRIM_SMITHING_TEMPLATE)
   */
  private void registerBurningFragmentRecipe() {
    ItemStack result = ElementalItems.createBurningFragment();
    ShapedRecipe recipe = new ShapedRecipe(burningFragmentKey, result);

    // Pattern matches proposal exactly
    recipe.shape(
      "NSN",  // N = Netherite Upgrade, S = Bolt/Stout Trim
      "IHI",  // I = Netherite Ingot, H = Heavy Core
      "NRN"   // N = Netherite Upgrade, R = Rib Trim
    );

    recipe.setIngredient('N', Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
    recipe.setIngredient('S', Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);  // Stout trim
    recipe.setIngredient('I', Material.NETHERITE_INGOT);  // Netherite Ingots
    recipe.setIngredient('R', Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);  // Rib trim

    // Using vanilla Heavy Core as per reworded feedback
    recipe.setIngredient('H', Material.HEAVY_CORE);

    Bukkit.addRecipe(recipe);
    plugin.getLogger().info("Burning Fragment recipe registered (uses vanilla HEAVY_CORE)");
  }

  /**
   * Register the Agility Fragment recipe.
   * Pattern from original proposal:
   * br	ft	ad
   * bd	hc	bd
   * ad	ft	br
   * Where:
   * br = Breeze Rod
   * ft = Flow Armor Trim
   * ad = Ancient Debris
   * bd = Block of Diamond
   * hc = Heavy Core (vanilla HEAVY_CORE item)
   */
  private void registerAgilityFragmentRecipe() {
    ItemStack result = ElementalItems.createAgilityFragment();
    ShapedRecipe recipe = new ShapedRecipe(agilityFragmentKey, result);

    recipe.shape(
      "BFA",  // B = Breeze Rod, F = Flow Armor Trim, A = Ancient Debris
      "DHD",  // D = Diamond Block, H = Heavy Core
      "AFB"   // A = Ancient Debris, F = Flow Armor Trim, B = Breeze Rod
    );

    recipe.setIngredient('B', Material.BREEZE_ROD);
    recipe.setIngredient('F', Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
    recipe.setIngredient('A', Material.ANCIENT_DEBRIS);
    recipe.setIngredient('D', Material.DIAMOND_BLOCK);

    // Using vanilla Heavy Core as per reworded feedback
    recipe.setIngredient('H', Material.HEAVY_CORE);

    Bukkit.addRecipe(recipe);
    plugin.getLogger().info("Agility Fragment recipe registered (uses vanilla HEAVY_CORE)");
  }

  /**
   * Register the Immortal Fragment recipe.
   * Pattern from original proposal:
   * tu	ad	tu
   * bd	hc	bd
   * tu	ad	tu
   * Where:
   * tu = Totem of Undying
   * ad = Ancient Debris
   * bd = Block of Diamond
   * hc = Heavy Core (vanilla HEAVY_CORE item)
   */
  private void registerImmortalFragmentRecipe() {
    ItemStack result = ElementalItems.createImmortalFragment();
    ShapedRecipe recipe = new ShapedRecipe(immortalFragmentKey, result);

    recipe.shape(
      "TAT",  // T = Totem of Undying, A = Ancient Debris
      "DHD",  // D = Diamond Block, H = Heavy Core
      "TAT"   // T = Totem of Undying, A = Ancient Debris
    );

    recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
    recipe.setIngredient('A', Material.ANCIENT_DEBRIS);
    recipe.setIngredient('D', Material.DIAMOND_BLOCK);

    // Using vanilla Heavy Core as per reworded feedback
    recipe.setIngredient('H', Material.HEAVY_CORE);

    Bukkit.addRecipe(recipe);
    plugin.getLogger().info("Immortal Fragment recipe registered (uses vanilla HEAVY_CORE)");
  }

  /**
   * Register the Corrupted Core recipe.
   * Pattern from original proposal:
   * wr	hc	ws
   * bn	ns	bn
   * ws	hc	wr
   * Where:
   * wr = Wither Rose
   * hc = Heavy Core
   * ws = Wither Skeleton Skull
   * bn = Block of Netherite
   * ns = Nether Star
   */
  private void registerCorruptedCoreRecipe() {
    ItemStack result = ElementalItems.createCorruptedCore();
    ShapedRecipe recipe = new ShapedRecipe(corruptedCoreKey, result);

    // Pattern matches proposal exactly
    recipe.shape(
      "RHK",  // R = Wither Rose, H = Heavy Core, K = Wither Skeleton Skull
      "NSN",  // N = Netherite Block, S = Nether Star
      "KHR"   // K = Wither Skeleton Skull, H = Heavy Core, R = Wither Rose
    );

    recipe.setIngredient('R', Material.WITHER_ROSE);
    recipe.setIngredient('H', Material.HEAVY_CORE);  // Using vanilla Heavy Core for ingredients
    recipe.setIngredient('K', Material.WITHER_SKELETON_SKULL);
    recipe.setIngredient('N', Material.NETHERITE_BLOCK);
    recipe.setIngredient('S', Material.NETHER_STAR);

    Bukkit.addRecipe(recipe);
    plugin.getLogger().info("Corrupted Core recipe registered (original proposal)");
  }

  /**
   * Get the NamespacedKey for Heavy Core recipe.
   *
   * @return NamespacedKey for Heavy Core
   */
  public NamespacedKey getHeavyCoreKey() {
    return heavyCoreKey;
  }

  /**
   * Get the NamespacedKey for Burning Fragment recipe.
   *
   * @return NamespacedKey for Burning Fragment
   */
  public NamespacedKey getBurningFragmentKey() {
    return burningFragmentKey;
  }

  /**
   * Get the NamespacedKey for Agility Fragment recipe.
   *
   * @return NamespacedKey for Agility Fragment
   */
  public NamespacedKey getAgilityFragmentKey() {
    return agilityFragmentKey;
  }

  /**
   * Get the NamespacedKey for Immortal Fragment recipe.
   *
   * @return NamespacedKey for Immortal Fragment
   */
  public NamespacedKey getImmortalFragmentKey() {
    return immortalFragmentKey;
  }

  /**
   * Get the NamespacedKey for Corrupted Core recipe.
   *
   * @return NamespacedKey for Corrupted Core
   */
  public NamespacedKey getCorruptedCoreKey() {
    return corruptedCoreKey;
  }

  /**
   * Unregister all recipes managed by this CraftingManager.
   * Useful for plugin reload functionality.
   * Note: Heavy Core is a vanilla item, no recipe to unregister.
   */
  public void unregisterRecipes() {
    Bukkit.removeRecipe(burningFragmentKey);
    Bukkit.removeRecipe(agilityFragmentKey);
    Bukkit.removeRecipe(immortalFragmentKey);
    Bukkit.removeRecipe(corruptedCoreKey);
    plugin.getLogger().info("All crafting recipes unregistered");
  }

  // ============ Recipe Introspection Methods ============
  // These methods expose recipe data for command display generation

  /**
   * Recipe data holder for exposing recipe information.
   */
  public static class RecipeData {
    private final String[] shape;
    private final java.util.Map<Character, Material> ingredients;
    private final String centerNote;

    public RecipeData(String[] shape, java.util.Map<Character, Material> ingredients, String centerNote) {
      this.shape = shape;
      this.ingredients = ingredients;
      this.centerNote = centerNote;
    }

    public String[] getShape() {
      return shape;
    }

    public java.util.Map<Character, Material> getIngredients() {
      return ingredients;
    }

    public String getCenterNote() {
      return centerNote;
    }
  }

  /**
   * Get Heavy Core recipe data.
   * Note: Heavy Core is now a vanilla Minecraft item, no crafting recipe needed.
   *
   * @return RecipeData for Heavy Core (null - vanilla item)
   */
  public RecipeData getHeavyCoreRecipe() {
    // Heavy Core is a vanilla Minecraft item, no crafting recipe needed
    return null;
  }

  /**
   * Get Burning Fragment recipe data.
   *
   * @return RecipeData for Burning Fragment
   */
  public RecipeData getBurningFragmentRecipe() {
    java.util.Map<Character, Material> ingredients = new java.util.HashMap<>();
    ingredients.put('N', Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
    ingredients.put('S', Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
    ingredients.put('I', Material.NETHERITE_INGOT);
    ingredients.put('H', Material.HEAVY_CORE); // Using vanilla Heavy Core
    ingredients.put('R', Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);

    return new RecipeData(
      new String[]{"NSN", "IHI", "NRN"},
      ingredients,
      "Requires 1 Heavy Core (vanilla) in center (H)!"
    );
  }

  /**
   * Get Agility Fragment recipe data.
   *
   * @return RecipeData for Agility Fragment
   */
  public RecipeData getAgilityFragmentRecipe() {
    java.util.Map<Character, Material> ingredients = new java.util.HashMap<>();
    ingredients.put('B', Material.BREEZE_ROD);
    ingredients.put('F', Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
    ingredients.put('A', Material.ANCIENT_DEBRIS);
    ingredients.put('D', Material.DIAMOND_BLOCK);
    ingredients.put('H', Material.HEAVY_CORE); // Using vanilla Heavy Core

    return new RecipeData(
      new String[]{"BFA", "DHD", "AFB"},
      ingredients,
      "Requires 1 Heavy Core (vanilla) in center (H)!"
    );
  }

  /**
   * Get Immortal Fragment recipe data.
   *
   * @return RecipeData for Immortal Fragment
   */
  public RecipeData getImmortalFragmentRecipe() {
    java.util.Map<Character, Material> ingredients = new java.util.HashMap<>();
    ingredients.put('T', Material.TOTEM_OF_UNDYING);
    ingredients.put('A', Material.ANCIENT_DEBRIS);
    ingredients.put('D', Material.DIAMOND_BLOCK);
    ingredients.put('H', Material.HEAVY_CORE); // Using vanilla Heavy Core

    return new RecipeData(
      new String[]{"TAT", "DHD", "TAT"},
      ingredients,
      "Requires 1 Heavy Core (vanilla) in center (H)!"
    );
  }

  /**
   * Get Corrupted Core recipe data.
   *
   * @return RecipeData for Corrupted Core
   */
  public RecipeData getCorruptedCoreRecipe() {
    java.util.Map<Character, Material> ingredients = new java.util.HashMap<>();
    ingredients.put('R', Material.WITHER_ROSE);
    ingredients.put('H', Material.HEAVY_CORE); // Using vanilla Heavy Core
    ingredients.put('K', Material.WITHER_SKELETON_SKULL);
    ingredients.put('N', Material.NETHERITE_BLOCK);
    ingredients.put('S', Material.NETHER_STAR);

    return new RecipeData(
      new String[]{"RHK", "NSN", "KHR"},
      ingredients,
      "Requires 2 Heavy Cores (vanilla) at positions shown!"
    );
  }
}
