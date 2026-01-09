package org.cavarest.elementaldragon.command;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Handles the /elementaldragon give subcommand.
 * Gives ingredients or equipment to players for elemental dragon crafting.
 * Supports player selectors: @p (nearest), @a (all), @s (self), or exact player name.
 */
public class AdminGiveCommand {

  private final ElementalDragon plugin;

  public AdminGiveCommand(ElementalDragon plugin) {
    this.plugin = plugin;
  }

  /**
   * Handle the give command.
   *
   * @param sender The command sender (must be operator)
   * @param args Command arguments: [player-ref] [ingredients|equipment] [element-kind]
   * @return true if the command was handled
   */
  public boolean handleGive(CommandSender sender, String[] args) {
    if (args.length < 3) {
      sender.sendMessage(Component.text(
        "Usage: /elementaldragon give <player-ref> <ingredients|equipment> <element-kind>",
        NamedTextColor.RED
      ));
      return true;
    }

    String playerRef = args[0];
    String giveType = args[1].toLowerCase();
    String elementKind = args[2].toLowerCase();

    // Resolve players from @p, @a, @s, or player name
    List<Player> targets = resolvePlayers(sender, playerRef);
    if (targets.isEmpty()) {
      return true; // Error message already sent
    }

    // Validate give type
    boolean giveIngredients;
    switch (giveType) {
      case "ingredients":
      case "ing":
        giveIngredients = true;
        break;
      case "equipment":
      case "equip":
      case "item":
        giveIngredients = false;
        break;
      default:
        sender.sendMessage(Component.text(
          "Invalid type: " + giveType + ". Use 'ingredients' or 'equipment'.",
          NamedTextColor.RED
        ));
        return true;
    }

    // Resolve element kind to FragmentType (CANONICAL NAMES ONLY)
    FragmentType fragmentType = FragmentType.fromCanonicalName(elementKind);
    if (fragmentType == null) {
      sender.sendMessage(Component.text(
        "Invalid fragment name: " + elementKind + ". Valid names: " + String.join(", ", FragmentType.getCanonicalNames()),
        NamedTextColor.RED
      ));
      return true;
    }

    // Give items to all resolved players
    giveToPlayers(sender, targets, giveIngredients, fragmentType);

    return true;
  }

  /**
   * Handle the ingredients subcommand - gives ingredients to player(s).
   *
   * @param sender The command sender
   * @param args Command arguments: [player-ref] [element-kind]
   * @return true if the command was handled
   */
  public boolean handleIngredients(CommandSender sender, String[] args) {
    if (args.length < 2) {
      sender.sendMessage(Component.text(
        "Usage: /elementaldragon ingredients <player-ref> <fragment-name>",
        NamedTextColor.RED
      ));
      return true;
    }

    String playerRef = args[0];
    String elementKind = args[1].toLowerCase();

    // Resolve players from @p, @a, @s, or player name
    List<Player> targets = resolvePlayers(sender, playerRef);
    if (targets.isEmpty()) {
      return true; // Error message already sent
    }

    // Resolve element kind to FragmentType (CANONICAL NAMES ONLY)
    FragmentType fragmentType = FragmentType.fromCanonicalName(elementKind);
    if (fragmentType == null) {
      sender.sendMessage(Component.text(
        "Invalid fragment name: " + elementKind + ". Valid names: " + String.join(", ", FragmentType.getCanonicalNames()),
        NamedTextColor.RED
      ));
      return true;
    }

    giveToPlayers(sender, targets, true, fragmentType);
    return true;
  }

  /**
   * Handle the equipment subcommand - gives equipment to player(s).
   *
   * @param sender The command sender
   * @param args Command arguments: [player-ref] [fragment-name]
   * @return true if the command was handled
   */
  public boolean handleEquipment(CommandSender sender, String[] args) {
    if (args.length < 2) {
      sender.sendMessage(Component.text(
        "Usage: /elementaldragon equipment <player-ref> <fragment-name>",
        NamedTextColor.RED
      ));
      return true;
    }

    String playerRef = args[0];
    String elementKind = args[1].toLowerCase();

    // Resolve players from @p, @a, @s, or player name
    List<Player> targets = resolvePlayers(sender, playerRef);
    if (targets.isEmpty()) {
      return true; // Error message already sent
    }

    // Resolve element kind to FragmentType (CANONICAL NAMES ONLY)
    FragmentType fragmentType = FragmentType.fromCanonicalName(elementKind);
    if (fragmentType == null) {
      sender.sendMessage(Component.text(
        "Invalid fragment name: " + elementKind + ". Valid names: " + String.join(", ", FragmentType.getCanonicalNames()),
        NamedTextColor.RED
      ));
      return true;
    }

    giveToPlayers(sender, targets, false, fragmentType);
    return true;
  }

  /**
   * Resolve a single player from a player reference.
   * Supports @p (nearest), @s (self), @a (all - returns first), or exact player name.
   *
   * @param sender The command sender
   * @param playerRef The player reference
   * @return The resolved player, or null if not found
   */
  private Player resolvePlayer(CommandSender sender, String playerRef) {
    String ref = playerRef.toLowerCase();

    switch (ref) {
      case "@p":
        if (sender instanceof Player) {
          return (Player) sender;
        }
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (!players.isEmpty()) {
          return players.iterator().next();
        }
        sender.sendMessage(Component.text("No players online!", NamedTextColor.RED));
        return null;

      case "@s":
        if (sender instanceof Player) {
          return (Player) sender;
        }
        sender.sendMessage(Component.text("Cannot use @s from console.", NamedTextColor.RED));
        return null;

      case "@a":
        // @a returns all players, but resolvePlayer returns single - return first online
        Collection<? extends Player> allPlayers = Bukkit.getOnlinePlayers();
        if (!allPlayers.isEmpty()) {
          return allPlayers.iterator().next();
        }
        sender.sendMessage(Component.text("No players online!", NamedTextColor.RED));
        return null;

      default:
        Player target = Bukkit.getPlayer(playerRef);
        if (target == null) {
          sender.sendMessage(Component.text(
            "Player '" + playerRef + "' not found!",
            NamedTextColor.RED
          ));
          return null;
        }
        return target;
    }
  }

  /**
   * Resolve multiple players from a player reference.
   * Supports @p (nearest), @s (self), @a (all), or exact player name.
   *
   * @param sender The command sender
   * @param playerRef The player reference (@p, @a, @s, or player name)
   * @return List of players, empty list if no players found
   */
  public List<Player> resolvePlayers(CommandSender sender, String playerRef) {
    List<Player> players = new ArrayList<>();
    String ref = playerRef.toLowerCase();

    switch (ref) {
      case "@p":
        if (sender instanceof Player) {
          players.add((Player) sender);
        } else {
          Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
          if (!onlinePlayers.isEmpty()) {
            players.add(onlinePlayers.iterator().next());
          }
        }
        break;

      case "@s":
        if (sender instanceof Player) {
          players.add((Player) sender);
        } else {
          sender.sendMessage(Component.text("Cannot use @s from console.", NamedTextColor.RED));
        }
        break;

      case "@a":
        // Return all online players
        players.addAll(Bukkit.getOnlinePlayers());
        if (players.isEmpty()) {
          sender.sendMessage(Component.text("No players online!", NamedTextColor.RED));
        }
        break;

      default:
        // Try to find player by name
        Player target = Bukkit.getPlayer(playerRef);
        if (target != null) {
          players.add(target);
        } else {
          sender.sendMessage(Component.text(
            "Player '" + playerRef + "' not found!",
            NamedTextColor.RED
          ));
        }
        break;
    }

    return players;
  }

  /**
   * Give items to multiple players.
   *
   * @param sender The command sender
   * @param players List of players to give items to
   * @param giveIngredients True to give ingredients, false to give equipment
   * @param fragmentType The fragment type
   */
  private void giveToPlayers(CommandSender sender, List<Player> players, boolean giveIngredients, FragmentType fragmentType) {
    if (players.isEmpty()) {
      return;
    }

    int successCount = 0;
    String action = giveIngredients ? "ingredients" : "equipment";

    for (Player target : players) {
      boolean success;
      if (giveIngredients) {
        success = giveIngredientsToPlayer(target, fragmentType);
      } else {
        success = giveEquipmentToPlayer(target, fragmentType);
      }
      if (success) {
        successCount++;
      }
    }

    // Only show success message if at least one player received items
    if (successCount > 0) {
      if (successCount == 1) {
        sender.sendMessage(Component.text(
          "Gave " + fragmentType.getDisplayName() + " " + action + " to " + players.get(0).getName(),
          NamedTextColor.GREEN
        ));
      } else {
        sender.sendMessage(Component.text(
          "Gave " + fragmentType.getDisplayName() + " " + action + " to " + successCount + " players",
          NamedTextColor.GREEN
        ));
      }
    } else {
      sender.sendMessage(Component.text(
        "No items were given (all players already have this fragment type).",
        NamedTextColor.YELLOW
      ));
    }
  }

  /**
   * Give ingredients to a single player (internal helper).
   *
   * @return true if ingredients were given successfully
   */
  private boolean giveIngredientsToPlayer(Player target, FragmentType fragmentType) {
    List<ItemStack> ingredients = getIngredients(fragmentType);
    for (ItemStack item : ingredients) {
      target.getInventory().addItem(item);
    }
    target.sendMessage(Component.text(
      "You received " + fragmentType.getDisplayName() + " ingredients!",
      NamedTextColor.AQUA
    ));
    return true;
  }

  /**
   * Give equipment to a single player (internal helper).
   *
   * @return true if equipment was given successfully, false if player already has it
   */
  private boolean giveEquipmentToPlayer(Player target, FragmentType fragmentType) {
    // Check if player already has this fragment type
    if (hasFragmentInInventory(target, fragmentType)) {
      target.sendMessage(Component.text(
        "⚠️ You already have a " + fragmentType.getDisplayName() + "!",
        NamedTextColor.RED
      ));
      target.sendMessage(Component.text(
        "You can only possess ONE of each fragment type.",
        NamedTextColor.GRAY
      ));
      return false;
    }

    ItemStack equipment = getEquipment(fragmentType);
    target.getInventory().addItem(equipment);
    target.sendMessage(Component.text(
      "You received the " + fragmentType.getDisplayName() + "!",
      NamedTextColor.GOLD
    ));
    return true;
  }

  /**
   * Check if player has a specific fragment type in their inventory.
   *
   * @param player The player
   * @param fragmentType The fragment type to check
   * @return true if player has this fragment type
   */
  private boolean hasFragmentInInventory(Player player, FragmentType fragmentType) {
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && item.hasItemMeta()) {
        String displayName = item.getItemMeta().displayName() != null ?
          item.getItemMeta().displayName().toString() : "";

        if (displayName.contains(fragmentType.getDisplayName())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Get the ingredients needed for a fragment.
   */
  private List<ItemStack> getIngredients(FragmentType fragmentType) {
    switch (fragmentType) {
      case BURNING:
        // Recipe grid:
        // nt  st  nt
        // ni  hc  ni
        // nt  rt  nt
        return Arrays.asList(
          new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 4),  // 4 corners
          createTrimTemplate("STOUT"),    // 1 top center
          createTrimTemplate("RIB"),      // 1 bottom center
          new ItemStack(Material.NETHERITE_INGOT, 2),  // 2 sides
          ElementalItems.getHeavyCore()   // 1 center
        );

      case AGILITY:
        // Recipe grid:
        // br  ft  ad
        // bd  hc  bd
        // ad  ft  br
        return Arrays.asList(
          new ItemStack(Material.BREEZE_ROD, 2),
          new ItemStack(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, 2),
          new ItemStack(Material.ANCIENT_DEBRIS, 2),
          new ItemStack(Material.DIAMOND_BLOCK, 2),
          ElementalItems.getHeavyCore()
        );

      case IMMORTAL:
        // Recipe grid:
        // tu  ad  tu
        // bd  hc  bd
        // tu  ad  tu
        return Arrays.asList(
          new ItemStack(Material.TOTEM_OF_UNDYING, 4),  // 4 corners (was 2!)
          new ItemStack(Material.ANCIENT_DEBRIS, 2),
          new ItemStack(Material.DIAMOND_BLOCK, 2),
          ElementalItems.getHeavyCore()
        );

      case CORRUPTED:
        // Recipe grid:
        // wr  hc  ws
        // bn  ns  bn
        // ws  hc  wr
        return Arrays.asList(
          new ItemStack(Material.WITHER_ROSE, 2),
          new ItemStack(Material.HEAVY_CORE, 2),  // 2 heavy cores!
          new ItemStack(Material.WITHER_SKELETON_SKULL, 2),
          new ItemStack(Material.NETHERITE_BLOCK, 2),
          new ItemStack(Material.NETHER_STAR, 1)
          // Note: No ElementalItems.getHeavyCore() since we're giving 2 vanilla Heavy Cores as ingredients
        );

      default:
        return List.of();
    }
  }

  /**
   * Get the equipment (crafted item) for a fragment.
   */
  private ItemStack getEquipment(FragmentType fragmentType) {
    switch (fragmentType) {
      case BURNING:
        return ElementalItems.createBurningFragment();
      case AGILITY:
        return ElementalItems.createAgilityFragment();
      case IMMORTAL:
        return ElementalItems.createImmortalFragment();
      case CORRUPTED:
        return ElementalItems.createCorruptedCore();
      default:
        return new ItemStack(Material.AIR);
    }
  }

  /**
   * Create a smithing trim template item.
   * Returns the actual armor trim smithing template based on trim type.
   */
  private ItemStack createTrimTemplate(String trimType) {
    switch (trimType.toUpperCase()) {
      case "STOUT":
        return new ItemStack(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);  // Stout trim
      case "RIB":
        return new ItemStack(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);    // Rib trim
      case "FLOW":
        return new ItemStack(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);   // Flow trim (for agility)
      default:
        plugin.getLogger().warning("Unknown trim type: " + trimType + ", defaulting to Netherite Upgrade");
        return new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
    }
  }

  /**
   * Get tab completions for the give command.
   * Only shows canonical fragment names: fire, agile, immortal, corrupt
   */
  public List<String> getTabCompletions(String[] args) {
    if (args.length == 1) {
      // Player reference completions including @a
      return Arrays.asList("@p", "@s", "@a", "PlayerName");
    } else if (args.length == 2) {
      return Arrays.asList("ingredients", "equipment");
    } else if (args.length == 3) {
      // ONLY canonical names
      return Arrays.asList(FragmentType.getCanonicalNames());
    }
    return List.of();
  }
}
