package org.cavarest.elementaldragon.command.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.command.base.AbstractSubcommand;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.util.FragmentCounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Subcommand for managing possession limit configuration.
 *
 * <p>The possession limit is per fragment type, allowing players to have
 * all 4 different fragment types simultaneously while limiting duplicates
 * of the same type. Default limit is 1 per elemental fragment type.</p>
 *
 * <p>Since crafting adds to inventory, possession limit IS the craft limit.</p>
 */
public class PossessionLimitSubcommand extends AbstractSubcommand {

  // Default limits: 1 per elemental fragment type
  private static final int DEFAULT_BURNING_LIMIT = 1;
  private static final int DEFAULT_AGILITY_LIMIT = 1;
  private static final int DEFAULT_IMMORTAL_LIMIT = 1;
  private static final int DEFAULT_CORRUPTED_LIMIT = 1;

  // Current limits (configurable)
  private int burningLimit = DEFAULT_BURNING_LIMIT;
  private int agilityLimit = DEFAULT_AGILITY_LIMIT;
  private int immortalLimit = DEFAULT_IMMORTAL_LIMIT;
  private int corruptedLimit = DEFAULT_CORRUPTED_LIMIT;

  public PossessionLimitSubcommand() {
    super(
      "possessionlimit",
      "Manage possession limit configuration",
      "/ed <setpossessionlimit|getpossessionlimit> ...",
      "elementaldragon.admin"
    );
  }

  /**
   * Execute the setpossessionlimit subcommand.
   */
  public boolean executeSetPossessionLimit(CommandSender sender, String[] args) {
    if (args.length < 2) {
      sendUsage(sender, "/ed setpossessionlimit <element> <count|default>");
      sendInfo(sender, "Elements: fire, agile, immortal, corrupt");
      sendInfo(sender, "Count: number (0 = unlimited), or 'default' to reset (default = 1)");
      return true;
    }

    String element = args[0].toLowerCase();
    String limitArg = args[1];

    FragmentType fragmentType = FragmentType.fromCanonicalName(element);
    if (fragmentType == null) {
      sendError(sender, "Invalid element: " + element);
      return true;
    }

    // Handle "default" keyword
    if (limitArg.equalsIgnoreCase("default")) {
      setLimit(fragmentType, getDefaultLimit(fragmentType));
      int defaultLimit = getLimit(fragmentType);
      sendSuccess(sender, "Reset " + element + " possession limit to default: " + defaultLimit);
      return true;
    }

    // Parse numeric value
    try {
      int limit = Integer.parseInt(limitArg);
      if (limit < 0) {
        sendError(sender, "Possession limit cannot be negative!");
        return true;
      }

      setLimit(fragmentType, limit);

      if (limit == 0) {
        sendSuccess(sender, "Set " + element + " possession limit to UNLIMITED.");
      } else {
        sendSuccess(sender, "Set " + element + " possession limit to " + limit + ".");
      }
    } catch (NumberFormatException e) {
      sendError(sender, "Invalid count: " + limitArg);
    }

    return true;
  }

  /**
   * Execute the getpossessionlimit subcommand.
   */
  public boolean executeGetPossessionLimit(CommandSender sender, String[] args) {
    sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
    sender.sendMessage(Component.text("   Possession Limits", NamedTextColor.GOLD));
    sender.sendMessage(Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
    sender.sendMessage(Component.text("", NamedTextColor.WHITE));

    displayLimit(sender, FragmentType.BURNING, burningLimit, DEFAULT_BURNING_LIMIT);
    displayLimit(sender, FragmentType.AGILITY, agilityLimit, DEFAULT_AGILITY_LIMIT);
    displayLimit(sender, FragmentType.IMMORTAL, immortalLimit, DEFAULT_IMMORTAL_LIMIT);
    displayLimit(sender, FragmentType.CORRUPTED, corruptedLimit, DEFAULT_CORRUPTED_LIMIT);

    sender.sendMessage(Component.text("", NamedTextColor.WHITE));
    sender.sendMessage(Component.text("Use /ed setpossessionlimit <element> <count> to change", NamedTextColor.DARK_GRAY));

    return true;
  }

  private void displayLimit(CommandSender sender, FragmentType type, int current, int defaultVal) {
    boolean isDefault = (current == defaultVal);
    String status = isDefault ? "(default)" : "(custom)";
    NamedTextColor color = isDefault ? NamedTextColor.GRAY : NamedTextColor.YELLOW;

    String display = current == 0 ? "UNLIMITED" : String.valueOf(current);
    sender.sendMessage(Component.text(
      String.format("  %s: %s %s", type.getDisplayName(), display, status),
      color
    ));
  }

  /**
   * Check if player can craft another fragment of the given type.
   * Uses actual inventory count, not PDC-stored craft count.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @return true if player can craft another
   */
  public boolean canCraft(Player player, FragmentType fragmentType) {
    int limit = getLimit(fragmentType);
    return FragmentCounter.canPossessAnother(player, fragmentType, limit);
  }

  /**
   * Check if player can equip another fragment of the given type.
   * Uses actual inventory count.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @return true if player can equip another
   */
  public boolean canEquip(Player player, FragmentType fragmentType) {
    int limit = getLimit(fragmentType);
    return FragmentCounter.canPossessAnother(player, fragmentType, limit);
  }

  public int getLimit(FragmentType fragmentType) {
    if (fragmentType == null) return 0;
    switch (fragmentType) {
      case BURNING: return burningLimit;
      case AGILITY: return agilityLimit;
      case IMMORTAL: return immortalLimit;
      case CORRUPTED: return corruptedLimit;
      default: return 0;
    }
  }

  public int getDefaultLimit(FragmentType fragmentType) {
    if (fragmentType == null) return 0;
    switch (fragmentType) {
      case BURNING: return DEFAULT_BURNING_LIMIT;
      case AGILITY: return DEFAULT_AGILITY_LIMIT;
      case IMMORTAL: return DEFAULT_IMMORTAL_LIMIT;
      case CORRUPTED: return DEFAULT_CORRUPTED_LIMIT;
      default: return 0;
    }
  }

  public void setLimit(FragmentType fragmentType, int limit) {
    if (fragmentType == null || limit < 0) return;
    switch (fragmentType) {
      case BURNING: burningLimit = limit; break;
      case AGILITY: agilityLimit = limit; break;
      case IMMORTAL: immortalLimit = limit; break;
      case CORRUPTED: corruptedLimit = limit; break;
    }
  }

  public List<String> tabCompleteSetPossessionLimit(CommandSender sender, String[] args) {
    List<String> completions = new ArrayList<>();

    if (args.length == 1) {
      completions.addAll(Arrays.asList("fire", "agile", "immortal", "corrupt"));
    } else if (args.length == 2) {
      completions.addAll(Arrays.asList("0", "1", "2", "3", "5", "10", "default"));
    }

    return filterCompletions(completions, args);
  }

  @Override
  public boolean execute(CommandSender sender, String[] args) {
    sendError(sender, "Use: /ed setpossessionlimit or /ed getpossessionlimit");
    return true;
  }

  @Override
  public List<String> tabComplete(CommandSender sender, String[] args) {
    return new ArrayList<>();
  }

  private List<String> filterCompletions(List<String> completions, String[] args) {
    if (args.length == 0) return completions;
    String partial = args[args.length - 1].toLowerCase();
    completions.removeIf(c -> !c.toLowerCase().startsWith(partial));
    return completions;
  }
}
