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
import net.kyori.adventure.text.minimessage.MiniMessage;

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
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

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
        miniMessage.deserialize("<red>You do not have permission to use this command!</red>")
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
            miniMessage.deserialize("<red>Usage: /chronicle page &lt;1-7&gt;</red>")
          );
          return true;
        }
        try {
          int pageNumber = Integer.parseInt(args[1]);
          jumpToPage(player, pageNumber);
        } catch (NumberFormatException e) {
          player.sendMessage(
            miniMessage.deserialize("<red>Invalid page number! Use 1-7.</red>")
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
          miniMessage.deserialize("<red>Unknown subcommand. Use /chronicle help for usage.</red>")
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
      miniMessage.deserialize("<gray>Opening the Chronicle of the Fallen Dragons...</gray>")
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
        Component pageContent = miniMessage.deserialize(
          "<bold><dark_purple>" + page.getTitle() + "</dark_purple></bold><newline><newline>" +
          "<black>" + page.getContent() + "</black>"
        );

        meta.addPages(pageContent);
      } else {
        // Add locked page placeholder
        String progress = chronicleManager.getProgress(player, page);
        Component lockedContent = miniMessage.deserialize(
          "<bold><dark_gray>Page " + page.getPageNumber() + "</dark_gray></bold><newline><newline>" +
          "<dark_red>??? LOCKED ???</dark_red><newline><newline>" +
          "<gray>" + getUnlockHint(page) + "</gray><newline><newline>" +
          "<yellow>Progress: " + progress + "</yellow>"
        );

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
      miniMessage.deserialize("<dark_purple>═══════════════════════════════════</dark_purple>")
    );
    player.sendMessage(
      miniMessage.deserialize("<bold><gold>Chronicle of the Fallen Dragons</gold></bold>")
    );
    player.sendMessage(
      miniMessage.deserialize("<yellow>Discovery Progress</yellow>")
    );
    player.sendMessage(
      miniMessage.deserialize("<dark_purple>═══════════════════════════════════</dark_purple>")
    );

    int discovered = chronicleManager.getDiscoveredCount(player);
    int total = chronicleManager.getTotalPageCount();

    player.sendMessage(
      miniMessage.deserialize("<aqua>Pages Discovered: " + discovered + "/" + total + "</aqua>")
    );
    player.sendMessage(Component.empty());

    // Show each page status
    for (LorePage page : LorePage.values()) {
      boolean isDiscovered = chronicleManager.hasDiscovered(player, page);
      String progress = chronicleManager.getProgress(player, page);

      String statusIcon = isDiscovered ? "<green>✓</green>" : "<red>✗</red>";
      String titleColor = isDiscovered ? "yellow" : "dark_gray";

      player.sendMessage(
        miniMessage.deserialize(
          statusIcon + " <gray>Page " + page.getPageNumber() + ": </gray>" +
          "<" + titleColor + ">" + page.getTitle() + "</" + titleColor + ">"
        )
      );

      if (!isDiscovered) {
        player.sendMessage(
          miniMessage.deserialize("<dark_gray>  Progress: " + progress + "</dark_gray>")
        );
      }
    }

    player.sendMessage(
      miniMessage.deserialize("<dark_purple>═══════════════════════════════════</dark_purple>")
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
    player.sendMessage(Component.empty());
    player.sendMessage(
      miniMessage.deserialize("<gold>Ability Usage Statistics:</gold>")
    );

    for (FragmentType fragmentType : FragmentType.values()) {
      int ability1Count = chronicleManager.getAbilityUsageCount(player, fragmentType, 1);
      int ability2Count = chronicleManager.getAbilityUsageCount(player, fragmentType, 2);

      if (ability1Count > 0 || ability2Count > 0) {
        player.sendMessage(
          miniMessage.deserialize("<yellow>  " + fragmentType.getDisplayName() + ":</yellow>")
        );
        player.sendMessage(
          miniMessage.deserialize("<gray>    Ability 1: " + ability1Count + " uses</gray>")
        );
        player.sendMessage(
          miniMessage.deserialize("<gray>    Ability 2: " + ability2Count + " uses</gray>")
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
        miniMessage.deserialize("<red>Invalid page number! Use 1-7.</red>")
      );
      return;
    }

    if (!chronicleManager.hasDiscovered(player, page)) {
      player.sendMessage(
        miniMessage.deserialize("<red>You haven't discovered page " + pageNumber + " yet!</red>")
      );
      player.sendMessage(
        miniMessage.deserialize("<gray>" + getUnlockHint(page) + "</gray>")
      );
      player.sendMessage(
        miniMessage.deserialize("<yellow>Progress: " + chronicleManager.getProgress(player, page) + "</yellow>")
      );
      return;
    }

    // Create book and open to specific page
    ItemStack book = createChronicleBook(player);
    player.openBook(book);

    player.sendMessage(
      miniMessage.deserialize("<gray>Opening Chronicle to Page " + pageNumber + "...</gray>")
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
        miniMessage.deserialize("<red>You do not have permission to get a chronicle book!</red>")
      );
      return;
    }

    ItemStack book = createChronicleBook(player);
    player.getInventory().addItem(book);

    player.sendMessage(
      miniMessage.deserialize("<green>The Chronicle of the Fallen Dragons has been added to your inventory!</green>")
    );
  }

  /**
   * Show help message for the chronicle command.
   *
   * @param player The player
   */
  private void showHelp(Player player) {
    player.sendMessage(
      miniMessage.deserialize("<dark_purple>═══════════════════════════════════</dark_purple>")
    );
    player.sendMessage(
      miniMessage.deserialize("<bold><gold>Chronicle Command Help</gold></bold>")
    );
    player.sendMessage(
      miniMessage.deserialize("<dark_purple>═══════════════════════════════════</dark_purple>")
    );
    player.sendMessage(
      miniMessage.deserialize("<yellow>/chronicle</yellow><gray> - Open the chronicle book</gray>")
    );
    player.sendMessage(
      miniMessage.deserialize("<yellow>/chronicle status</yellow><gray> - Show discovery progress</gray>")
    );
    player.sendMessage(
      miniMessage.deserialize("<yellow>/chronicle page &lt;1-7&gt;</yellow><gray> - Jump to specific page</gray>")
    );
    player.sendMessage(
      miniMessage.deserialize("<yellow>/chronicle get</yellow><gray> - Get chronicle book item</gray>")
    );
    player.sendMessage(
      miniMessage.deserialize("<dark_purple>═══════════════════════════════════</dark_purple>")
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