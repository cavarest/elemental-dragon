package org.cavarest.elementaldragon.command;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.lore.ChronicleManager;
import org.cavarest.elementaldragon.lore.LorePage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Command handler for the Chronicle of the Fallen Dragons lore system.
 * Provides subcommands for viewing the chronicle book and checking progress.
 */
public class ChronicleCommand implements CommandExecutor, TabCompleter {

  private final ElementalDragon plugin;
  private final ChronicleManager chronicleManager;

  /**
   * Create a new ChronicleCommand.
   *
   * @param plugin The plugin instance
   */
  public ChronicleCommand(ElementalDragon plugin) {
    this.plugin = plugin;
    this.chronicleManager = plugin.getChronicleManager();
  }

  @Override
  public boolean onCommand(
    CommandSender sender,
    Command command,
    String label,
    String[] args
  ) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("This command can only be used by players.");
      return true;
    }

    Player player = (Player) sender;

    // Check permission
    if (!player.hasPermission("elementaldragon.chronicle")) {
      player.sendMessage(
        Component.text("You do not have permission to use this command!",
          NamedTextColor.RED)
      );
      return true;
    }

    // Handle subcommands
    if (args.length == 0) {
      // No arguments - open the chronicle book
      openChronicleBook(player);
      return true;
    }

    String subcommand = args[0].toLowerCase();

    switch (subcommand) {
      case "status":
      case "progress":
        showProgress(player);
        return true;

      case "page":
        if (args.length < 2) {
          player.sendMessage(
            Component.text("Usage: /chronicle page <1-7>", NamedTextColor.RED)
          );
          return true;
        }
        try {
          int pageNumber = Integer.parseInt(args[1]);
          jumpToPage(player, pageNumber);
        } catch (NumberFormatException e) {
          player.sendMessage(
            Component.text("Invalid page number! Use 1-7.", NamedTextColor.RED)
          );
        }
        return true;

      case "get":
        giveChronicleBook(player);
        return true;

      case "help":
        showHelp(player);
        return true;

      default:
        player.sendMessage(
          Component.text("Unknown subcommand. Use /chronicle help for usage.",
            NamedTextColor.RED)
        );
        return true;
    }
  }

  /**
   * Open the Chronicle book for a player.
   *
   * @param player The player
   */
  private void openChronicleBook(Player player) {
    ItemStack book = createChronicleBook(player);
    player.openBook(book);

    player.sendMessage(
      Component.text("Opening the Chronicle of the Fallen Dragons...",
        NamedTextColor.GRAY)
    );
  }

  /**
   * Create a written book with the chronicle content.
   * Only includes discovered pages.
   *
   * @param player The player
   * @return Written book ItemStack
   */
  private ItemStack createChronicleBook(Player player) {
    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta meta = (BookMeta) book.getItemMeta();

    if (meta == null) {
      return book;
    }

    // Set book metadata
    meta.setTitle("Chronicle of the Fallen Dragons");
    meta.setAuthor("The Ancient Scribes");

    // Get discovered pages
    Set<LorePage> discoveredPages = chronicleManager.getDiscoveredPages(player);

    // Add pages in order
    for (LorePage page : LorePage.values()) {
      if (discoveredPages.contains(page)) {
        // Add discovered page
        Component pageContent = Component.text()
          .append(Component.text(page.getTitle() + "\n\n",
            NamedTextColor.DARK_PURPLE,
            TextDecoration.BOLD))
          .append(Component.text(page.getContent(), NamedTextColor.BLACK))
          .build();

        meta.addPages(pageContent);
      } else {
        // Add locked page placeholder
        String progress = chronicleManager.getProgress(player, page);
        Component lockedContent = Component.text()
          .append(Component.text("Page " + page.getPageNumber() + "\n\n",
            NamedTextColor.DARK_GRAY,
            TextDecoration.BOLD))
          .append(Component.text("??? LOCKED ???\n\n", NamedTextColor.DARK_RED))
          .append(Component.text(getUnlockHint(page) + "\n\n", NamedTextColor.GRAY))
          .append(Component.text("Progress: " + progress, NamedTextColor.YELLOW))
          .build();

        meta.addPages(lockedContent);
      }
    }

    book.setItemMeta(meta);
    return book;
  }

  /**
   * Get unlock hint text for a locked page.
   *
   * @param page The locked page
   * @return Hint text
   */
  private String getUnlockHint(LorePage page) {
    switch (page.getTrigger()) {
      case ALWAYS:
        return "This page should be unlocked...";

      case ABILITY_USE:
        FragmentType fragmentType = page.getFragmentType();
        if (fragmentType != null) {
          return "Unlock by using " + fragmentType.getDisplayName() +
            " abilities " + page.getRequiredCount() + " times.";
        }
        return "Unlock by using fragment abilities.";

      case EQUIP_ALL_FRAGMENTS:
        return "Unlock by equipping all 4 fragment types at least once.";

      case MASTER_ALL_ABILITIES:
        return "Unlock by using each fragment ability 10 times.";

      default:
        return "Complete certain tasks to unlock.";
    }
  }

  /**
   * Show progress towards unlocking all pages.
   *
   * @param player The player
   */
  private void showProgress(Player player) {
    player.sendMessage(
      Component.text("═══════════════════════════════════", NamedTextColor.DARK_PURPLE)
    );
    player.sendMessage(
      Component.text("Chronicle of the Fallen Dragons", NamedTextColor.GOLD,
        TextDecoration.BOLD)
    );
    player.sendMessage(
      Component.text("Discovery Progress", NamedTextColor.YELLOW)
    );
    player.sendMessage(
      Component.text("═══════════════════════════════════", NamedTextColor.DARK_PURPLE)
    );

    int discovered = chronicleManager.getDiscoveredCount(player);
    int total = chronicleManager.getTotalPageCount();

    player.sendMessage(
      Component.text("Pages Discovered: " + discovered + "/" + total,
        NamedTextColor.AQUA)
    );
    player.sendMessage(Component.text(""));

    // Show each page status
    for (LorePage page : LorePage.values()) {
      boolean isDiscovered = chronicleManager.hasDiscovered(player, page);
      String progress = chronicleManager.getProgress(player, page);

      Component statusIcon = isDiscovered ?
        Component.text("✓", NamedTextColor.GREEN) :
        Component.text("✗", NamedTextColor.RED);

      Component pageLine = Component.text()
        .append(statusIcon)
        .append(Component.text(" Page " + page.getPageNumber() + ": ",
          NamedTextColor.GRAY))
        .append(Component.text(page.getTitle(),
          isDiscovered ? NamedTextColor.YELLOW : NamedTextColor.DARK_GRAY))
        .build();

      player.sendMessage(pageLine);

      if (!isDiscovered) {
        player.sendMessage(
          Component.text("  Progress: " + progress, NamedTextColor.DARK_GRAY)
        );
      }
    }

    player.sendMessage(
      Component.text("═══════════════════════════════════", NamedTextColor.DARK_PURPLE)
    );

    // Show ability usage stats
    showAbilityStats(player);
  }

  /**
   * Show ability usage statistics.
   *
   * @param player The player
   */
  private void showAbilityStats(Player player) {
    player.sendMessage(Component.text(""));
    player.sendMessage(
      Component.text("Ability Usage Statistics:", NamedTextColor.GOLD)
    );

    for (FragmentType fragmentType : FragmentType.values()) {
      int ability1Count = chronicleManager.getAbilityUsageCount(player, fragmentType, 1);
      int ability2Count = chronicleManager.getAbilityUsageCount(player, fragmentType, 2);

      if (ability1Count > 0 || ability2Count > 0) {
        player.sendMessage(
          Component.text("  " + fragmentType.getDisplayName() + ":",
            NamedTextColor.YELLOW)
        );
        player.sendMessage(
          Component.text("    Ability 1: " + ability1Count + " uses",
            NamedTextColor.GRAY)
        );
        player.sendMessage(
          Component.text("    Ability 2: " + ability2Count + " uses",
            NamedTextColor.GRAY)
        );
      }
    }
  }

  /**
   * Jump to a specific page in the chronicle.
   *
   * @param player The player
   * @param pageNumber The page number (1-7)
   */
  private void jumpToPage(Player player, int pageNumber) {
    LorePage page = LorePage.fromPageNumber(pageNumber);

    if (page == null) {
      player.sendMessage(
        Component.text("Invalid page number! Use 1-7.", NamedTextColor.RED)
      );
      return;
    }

    if (!chronicleManager.hasDiscovered(player, page)) {
      player.sendMessage(
        Component.text("You haven't discovered page " + pageNumber + " yet!",
          NamedTextColor.RED)
      );
      player.sendMessage(
        Component.text(getUnlockHint(page), NamedTextColor.GRAY)
      );
      player.sendMessage(
        Component.text("Progress: " + chronicleManager.getProgress(player, page),
          NamedTextColor.YELLOW)
      );
      return;
    }

    // Create book and open to specific page
    ItemStack book = createChronicleBook(player);
    player.openBook(book);

    player.sendMessage(
      Component.text("Opening Chronicle to Page " + pageNumber + "...",
        NamedTextColor.GRAY)
    );
  }

  /**
   * Give the player a physical copy of the chronicle book.
   *
   * @param player The player
   */
  private void giveChronicleBook(Player player) {
    if (!player.hasPermission("elementaldragon.chronicle.get")) {
      player.sendMessage(
        Component.text("You do not have permission to get a chronicle book!",
          NamedTextColor.RED)
      );
      return;
    }

    ItemStack book = createChronicleBook(player);
    player.getInventory().addItem(book);

    player.sendMessage(
      Component.text("The Chronicle of the Fallen Dragons has been added to your inventory!",
        NamedTextColor.GREEN)
    );
  }

  /**
   * Show help message for the chronicle command.
   *
   * @param player The player
   */
  private void showHelp(Player player) {
    player.sendMessage(
      Component.text("═══════════════════════════════════", NamedTextColor.DARK_PURPLE)
    );
    player.sendMessage(
      Component.text("Chronicle Command Help", NamedTextColor.GOLD,
        TextDecoration.BOLD)
    );
    player.sendMessage(
      Component.text("═══════════════════════════════════", NamedTextColor.DARK_PURPLE)
    );
    player.sendMessage(
      Component.text("/chronicle", NamedTextColor.YELLOW)
        .append(Component.text(" - Open the chronicle book", NamedTextColor.GRAY))
    );
    player.sendMessage(
      Component.text("/chronicle status", NamedTextColor.YELLOW)
        .append(Component.text(" - Show discovery progress", NamedTextColor.GRAY))
    );
    player.sendMessage(
      Component.text("/chronicle page <1-7>", NamedTextColor.YELLOW)
        .append(Component.text(" - Jump to specific page", NamedTextColor.GRAY))
    );
    player.sendMessage(
      Component.text("/chronicle get", NamedTextColor.YELLOW)
        .append(Component.text(" - Get chronicle book item", NamedTextColor.GRAY))
    );
    player.sendMessage(
      Component.text("═══════════════════════════════════", NamedTextColor.DARK_PURPLE)
    );
  }

  @Override
  public List<String> onTabComplete(
    CommandSender sender,
    Command command,
    String alias,
    String[] args
  ) {
    if (!(sender instanceof Player)) {
      return new ArrayList<>();
    }

    if (args.length == 1) {
      // Subcommand suggestions
      return Arrays.asList("status", "progress", "page", "get", "help")
        .stream()
        .filter(sub -> sub.startsWith(args[0].toLowerCase()))
        .collect(Collectors.toList());
    }

    if (args.length == 2 && args[0].equalsIgnoreCase("page")) {
      // Page number suggestions
      return Arrays.asList("1", "2", "3", "4", "5", "6", "7")
        .stream()
        .filter(num -> num.startsWith(args[1]))
        .collect(Collectors.toList());
    }

    return new ArrayList<>();
  }
}