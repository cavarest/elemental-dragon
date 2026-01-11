package org.cavarest.elementaldragon.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.crafting.CraftedCountManager;
import org.cavarest.elementaldragon.crafting.CraftingManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Command handler for the /craft command.
 * Allows players to craft Heavy Core and elemental fragments.
 * Generates recipe displays from CraftingManager's actual recipes (DRY principle).
 */
public class CraftCommand implements CommandExecutor, TabCompleter {

  private final ElementalDragon plugin;
  private final CraftingManager craftingManager;
  private final CraftedCountManager craftedCountManager;

  // Valid crafting types
  private static final String[] CRAFT_TYPES = {
    "heavy_core",
    "fire",
    "agile",
    "immortal",
    "corrupt"
  };

  /**
   * Find a Dragon Egg in player's inventory.
   *
   * @param player The player
   * @return ItemStack of Dragon Egg or null if not found
   */
  private ItemStack findDragonEgg(Player player) {
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && item.getType() == Material.DRAGON_EGG) {
        return item;
      }
    }
    return null;
  }

  /**
   * Create a new CraftCommand.
   *
   * @param plugin The plugin instance
   * @param craftingManager The crafting manager for recipe access
   * @param craftedCountManager The crafted count manager for tracking quantities
   */
  public CraftCommand(ElementalDragon plugin, CraftingManager craftingManager, CraftedCountManager craftedCountManager) {
    this.plugin = plugin;
    this.craftingManager = craftingManager;
    this.craftedCountManager = craftedCountManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
        net.kyori.adventure.text.Component.text(
          "This command can only be used by players!",
          net.kyori.adventure.text.format.NamedTextColor.RED
        )
      );
      return true;
    }

    Player player = (Player) sender;

    // Check permission
    if (!player.hasPermission("elementaldragon.craft")) {
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          "You don't have permission to use this command!",
          net.kyori.adventure.text.format.NamedTextColor.RED
        )
      );
      return true;
    }

    // No arguments - show help
    if (args.length == 0) {
      showCraftingHelp(player);
      return true;
    }

    String craftType = args[0].toLowerCase();

    switch (craftType) {
      case "heavy_core":
        showRecipe(player, "Heavy Core", craftingManager.getHeavyCoreRecipe(), true, null);
        break;
      case "fire":
        showRecipe(player, "Burning Fragment", craftingManager.getBurningFragmentRecipe(), false, FragmentType.BURNING);
        break;
      case "agile":
        showRecipe(player, "Agility Fragment", craftingManager.getAgilityFragmentRecipe(), false, FragmentType.AGILITY);
        break;
      case "immortal":
        showRecipe(player, "Immortal Fragment", craftingManager.getImmortalFragmentRecipe(), false, FragmentType.IMMORTAL);
        break;
      case "corrupt":
        showRecipe(player, "Corrupted Core", craftingManager.getCorruptedCoreRecipe(), false, FragmentType.CORRUPTED);
        break;
      case "help":
      case "?":
        showCraftingHelp(player);
        break;
      default:
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "Unknown craft type: " + args[0],
            net.kyori.adventure.text.format.NamedTextColor.RED
          )
        );
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "Use /craft help for available recipes",
            net.kyori.adventure.text.format.NamedTextColor.YELLOW
          )
        );
        break;
    }

    return true;
  }

  /**
   * Display a recipe to the player using actual recipe data from CraftingManager.
   *
   * @param player The player to display to
   * @param displayName The display name of the item
   * @param recipeData The recipe data from CraftingManager
   * @param requiresOp Whether this recipe requires OP status
   * @param fragmentType The fragment type (null for Heavy Core)
   */
  private void showRecipe(Player player, String displayName, CraftingManager.RecipeData recipeData, boolean requiresOp, FragmentType fragmentType) {
    // Check OP requirement for Heavy Core
    if (requiresOp && !player.isOp()) {
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          displayName + " crafting requires OP status!",
          net.kyori.adventure.text.format.NamedTextColor.RED
        )
      );
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          "Contact an administrator to craft this item.",
          net.kyori.adventure.text.format.NamedTextColor.GRAY
        )
      );
      return;
    }

    // Check for Dragon Egg if this is Heavy Core
    if (requiresOp) {
      ItemStack dragonEgg = findDragonEgg(player);
      if (dragonEgg == null) {
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "You need a Dragon Egg to craft a " + displayName + "!",
            net.kyori.adventure.text.format.NamedTextColor.RED
          )
        );
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "Place the Dragon Egg in your inventory.",
            net.kyori.adventure.text.format.NamedTextColor.GRAY
          )
        );
        return;
      }
    }

    // Display recipe header
    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "To craft " + displayName + ", use a 3x3 crafting table:",
        net.kyori.adventure.text.format.NamedTextColor.GREEN
      )
    );

    // Generate and display recipe grid
    displayRecipeGrid(player, recipeData);

    // Display center note (e.g., "Requires Heavy Core", "Dragon Egg will be consumed")
    if (recipeData.getCenterNote() != null && !recipeData.getCenterNote().isEmpty()) {
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          recipeData.getCenterNote(),
          net.kyori.adventure.text.format.NamedTextColor.YELLOW
        )
      );
    }

    // Display crafted quantity status for fragments (ORIGINAL SPECIFICATION)
    if (fragmentType != null && craftedCountManager != null) {
      int current = craftedCountManager.getCraftedCount(player, fragmentType);
      int max = craftedCountManager.getMaxCraftableCount(fragmentType);
      boolean canCraft = craftedCountManager.canCraft(player, fragmentType);

      // Show status message
      if (canCraft) {
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "Crafting Status: " + current + "/" + max + " crafted (You can still craft " + (max - current) + " more)",
            net.kyori.adventure.text.format.NamedTextColor.GREEN
          )
        );
      } else {
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "Crafting Status: " + current + "/" + max + " crafted (LIMIT REACHED)",
            net.kyori.adventure.text.format.NamedTextColor.RED
          )
        );
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "You cannot craft any more of this fragment type.",
            net.kyori.adventure.text.format.NamedTextColor.GRAY
          )
        );
      }
    }
  }

  /**
   * Display a 3x3 recipe grid from RecipeData.
   * Converts material names to human-readable format.
   *
   * @param player The player to display to
   * @param recipeData The recipe data to display
   */
  private void displayRecipeGrid(Player player, CraftingManager.RecipeData recipeData) {
    String[] shape = recipeData.getShape();
    Map<Character, Material> ingredients = recipeData.getIngredients();

    // Display each row of the recipe
    for (String row : shape) {
      StringBuilder displayRow = new StringBuilder();
      for (int i = 0; i < row.length(); i++) {
        char symbol = row.charAt(i);
        Material material = ingredients.get(symbol);

        if (material != null) {
          // Convert material name to readable format
          String materialName = formatMaterialName(material);
          displayRow.append(materialName);
        } else {
          // Empty slot
          displayRow.append("   ");
        }

        // Add spacing between columns (except last)
        if (i < row.length() - 1) {
          displayRow.append(" ");
        }
      }

      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          displayRow.toString(),
          net.kyori.adventure.text.format.NamedTextColor.WHITE
        )
      );
    }
  }

  /**
   * Format a Material name into human-readable text.
   * Converts SCREAMING_SNAKE_CASE to Title Case.
   *
   * @param material The material to format
   * @return Human-readable material name
   */
  private String formatMaterialName(Material material) {
    String name = material.name();
    String[] parts = name.split("_");
    StringBuilder formatted = new StringBuilder();

    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];
      // Capitalize first letter, lowercase rest
      formatted.append(part.substring(0, 1).toUpperCase());
      formatted.append(part.substring(1).toLowerCase());

      // Add space between words (except last)
      if (i < parts.length - 1) {
        formatted.append(" ");
      }
    }

    return formatted.toString();
  }

  /**
   * Show crafting help to the player.
   *
   * @param player The player to show help to
   */
  private void showCraftingHelp(Player player) {
    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "=== Elemental Dragon Crafting ===",
        net.kyori.adventure.text.format.NamedTextColor.GOLD
      )
    );

    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "/craft heavy_core - Craft a Heavy Core (OP only, consumes Dragon Egg)",
        net.kyori.adventure.text.format.NamedTextColor.WHITE
      )
    );

    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "/craft fire - Burning Fragment recipe",
        net.kyori.adventure.text.format.NamedTextColor.RED
      )
    );

    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "/craft agile - Agility Fragment recipe",
        net.kyori.adventure.text.format.NamedTextColor.GREEN
      )
    );

    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "/craft immortal - Immortal Fragment recipe",
        net.kyori.adventure.text.format.NamedTextColor.GOLD
      )
    );

    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "/craft corrupt - Corrupted Core recipe",
        net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE
      )
    );

    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "/craft help - Show this help message",
        net.kyori.adventure.text.format.NamedTextColor.GRAY
      )
    );

    if (!player.hasPermission("elementaldragon.craft")) {
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          "You don't have permission to craft items!",
          net.kyori.adventure.text.format.NamedTextColor.RED
        )
      );
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    if (!(sender instanceof Player)) {
      return new ArrayList<>();
    }

    if (args.length == 1) {
      List<String> completions = new ArrayList<>();
      String partial = args[0].toLowerCase();

      for (String type : CRAFT_TYPES) {
        if (type.startsWith(partial)) {
          completions.add(type);
        }
      }

      return completions;
    }

    return new ArrayList<>();
  }
}
