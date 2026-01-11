package org.cavarest.elementaldragon.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.AbilityDefinition;
import org.cavarest.elementaldragon.fragment.Fragment;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.hud.PlayerPreferenceManager;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer.VariantType;
import org.cavarest.elementaldragon.item.ElementalItems;
import org.cavarest.elementaldragon.lore.ChronicleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract base class for all fragment commands using Single Source of Truth pattern.
 * Eliminates ALL code duplication by querying Fragment for metadata dynamically.
 *
 * <p>This class demonstrates TRUE object-oriented programming where Fragment owns
 * all its data, and commands simply delegate to it. NO abstract methods needed!</p>
 *
 * <p>Subclasses only need to:</p>
 * <ul>
 *   <li>Pass the correct Fragment instance to the constructor</li>
 * </ul>
 *
 * <p>That's it! Everything else is handled automatically by querying the Fragment.</p>
 */
public abstract class AbstractFragmentCommand implements CommandExecutor, TabCompleter {

  protected final ElementalDragon plugin;
  protected final FragmentManager fragmentManager;
  protected final ChronicleManager chronicleManager;
  protected final PlayerPreferenceManager playerPreferenceManager;
  protected final Fragment fragment; // Single Source of Truth!

  /**
   * Create a new fragment command.
   *
   * @param plugin The plugin instance
   * @param fragmentManager The fragment manager
   * @param fragment The fragment instance (provides ALL metadata)
   */
  public AbstractFragmentCommand(ElementalDragon plugin, FragmentManager fragmentManager, Fragment fragment) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
    this.chronicleManager = plugin.getChronicleManager();
    this.playerPreferenceManager = plugin.getPlayerPreferenceManager();
    this.fragment = fragment;
  }

  // ============================================================
  // Template Methods - ALL query Fragment dynamically
  // ============================================================

  @Override
  public final boolean onCommand(
    CommandSender sender,
    Command command,
    String label,
    String[] args
  ) {
    // Check if sender is a player
    if (!(sender instanceof Player)) {
      sender.sendMessage(
        Component.text("This command can only be used by players!",
          NamedTextColor.RED)
      );
      return true;
    }

    Player player = (Player) sender;

    // Check permission (query fragment)
    if (!player.hasPermission(fragment.getPermissionNode())) {
      player.sendMessage(
        Component.text("You do not have permission to use " + fragment.getName() + " abilities!",
          NamedTextColor.RED)
      );
      return true;
    }

    // Handle no arguments - show help
    if (args.length == 0) {
      showHelp(player);
      return true;
    }

    String subCommand = args[0].toLowerCase();

    switch (subCommand) {
      case "1":
        return handleAbility(player, 1);
      case "2":
        return handleAbility(player, 2);
      case "equip":
        return handleEquip(player);
      case "status":
        return handleStatus(player);
      case "setcountdownsym":
        return handleSetCountdownSym(player, args);
      case "help":
      default:
        // Check for ability aliases (query fragment)
        AbilityDefinition ability = fragment.getAbilityByAlias(subCommand);
        if (ability != null) {
          return handleAbility(player, ability.getNumber());
        }
        showHelp(player);
        return true;
    }
  }

  /**
   * Handle ability activation.
   *
   * @param player The player
   * @param abilityNumber The ability number (1 or 2)
   * @return true if successful
   */
  protected final boolean handleAbility(Player player, int abilityNumber) {
    // Check if player has correct fragment equipped
    FragmentType equipped = fragmentManager.getEquippedFragment(player);

    if (equipped != fragment.getType()) {
      player.sendMessage(
        Component.text("Equip the " + fragment.getName() + " first with /" + fragment.getCommandName() + " equip!",
          NamedTextColor.RED)
      );
      return true;
    }

    // Use the fragment ability
    boolean success = fragmentManager.useFragmentAbility(player, abilityNumber);

    if (success) {
      // Query fragment for ability metadata
      AbilityDefinition ability = fragment.getAbility(abilityNumber);
      if (ability != null) {
        player.sendMessage(
          Component.text("Used " + ability.getName() + "!",
            fragment.getThemeColor())
        );
        player.sendMessage(
          Component.text(ability.getSuccessMessage(),
            NamedTextColor.GRAY)
        );
      }
    }

    return success;
  }

  /**
   * Handle fragment equipping.
   * If player doesn't have the fragment item and is an admin, give it to them first.
   *
   * @param player The player
   * @return true if successful
   */
  protected final boolean handleEquip(Player player) {
    // Check if player already has this fragment
    FragmentType currentEquipped = fragmentManager.getEquippedFragment(player);
    if (currentEquipped == fragment.getType()) {
      player.sendMessage(
        Component.text(fragment.getName() + " is already equipped!", NamedTextColor.YELLOW)
      );
      return true;
    }

    // Check if player has the fragment item
    boolean hasFragment = hasFragmentItem(player, fragment.getType());

    if (!hasFragment) {
      // Player doesn't have the fragment - check if admin (auto-give)
      if (player.hasPermission("elementaldragon.fragment.admin")) {
        // Give the fragment item to player
        ItemStack fragmentItem = createFragmentItem(fragment.getType());
        player.getInventory().addItem(fragmentItem);
        player.sendMessage(
          Component.text("✨ Admin granted: " + fragment.getName(), NamedTextColor.LIGHT_PURPLE)
        );
      } else {
        // Regular player - tell them how to get it
        player.sendMessage(
          Component.text("You don't have the " + fragment.getName() + "!", NamedTextColor.RED)
        );
        player.sendMessage(
          Component.text("Craft it with /craft " + fragment.getCommandName() +
            " or ask an admin for help.", NamedTextColor.GRAY)
        );
        return false;
      }
    }

    // Now equip the fragment
    boolean success = fragmentManager.equipFragment(player, fragment.getType());

    if (success) {
      // Register fragment equip for chronicle tracking
      chronicleManager.registerFragmentEquip(player, fragment.getType());

      player.sendMessage(
        Component.text("Equipped " + fragment.getName() + "!", NamedTextColor.GOLD)
      );
      player.sendMessage(
        Component.text("Passive: " + fragment.getPassiveBonus(), NamedTextColor.GRAY)
      );

      // Auto-generate ability help from fragment
      for (AbilityDefinition ability : fragment.getAbilities()) {
        player.sendMessage(
          Component.text("Use /" + fragment.getCommandName() + " " + ability.getNumber() +
            " for " + ability.getName() + " (" + ability.getDescription() + ")",
            fragment.getThemeColor())
        );
      }
    } else {
      player.sendMessage(
        Component.text("Failed to equip fragment!", NamedTextColor.RED)
      );
    }

    return success;
  }

  /**
   * Check if player has a specific fragment item in inventory.
   *
   * @param player The player
   * @param fragmentType The fragment type to check
   * @return true if player has the fragment
   */
  private boolean hasFragmentItem(Player player, FragmentType fragmentType) {
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && ElementalItems.getFragmentType(item) == fragmentType) {
        return true;
      }
    }
    return false;
  }

  /**
   * Create a fragment item for the given type.
   *
   * @param fragmentType The fragment type
   * @return The fragment item
   */
  private ItemStack createFragmentItem(FragmentType fragmentType) {
    return switch (fragmentType) {
      case BURNING -> ElementalItems.createBurningFragment();
      case AGILITY -> ElementalItems.createAgilityFragment();
      case IMMORTAL -> ElementalItems.createImmortalFragment();
      case CORRUPTED -> ElementalItems.createCorruptedCore();
    };
  }

  /**
   * Handle status display.
   *
   * @param player The player
   * @return true if successful
   */
  protected final boolean handleStatus(Player player) {
    FragmentType equipped = fragmentManager.getEquippedFragment(player);

    player.sendMessage(
      Component.text("=== " + fragment.getName() + " Status ===", NamedTextColor.GOLD)
    );

    if (equipped == fragment.getType()) {
      player.sendMessage(
        Component.text("Equipped: " + fragment.getName(), NamedTextColor.GREEN)
      );
      player.sendMessage(
        Component.text("Element: " + fragment.getElementName(), fragment.getThemeColor())
      );
      player.sendMessage(
        Component.text("Passive: " + fragment.getPassiveBonus(), NamedTextColor.GRAY)
      );

      if (fragmentManager.isOnCooldown(player)) {
        int remaining = fragmentManager.getRemainingCooldown(player);
        player.sendMessage(
          Component.text("Ability Cooldown: " + remaining + "s", NamedTextColor.RED)
        );
      } else {
        player.sendMessage(
          Component.text("Abilities: Ready", NamedTextColor.GREEN)
        );
      }
    } else {
      player.sendMessage(
        Component.text(fragment.getName() + " not equipped.", NamedTextColor.GRAY)
      );
      player.sendMessage(
        Component.text("Use /" + fragment.getCommandName() + " equip to equip the " + fragment.getName() + ".",
          NamedTextColor.WHITE)
      );
    }

    return true;
  }

  /**
   * Handle setting the countdown symbol preference for this player.
   *
   * @param player The player
   * @param args Command arguments
   * @return true if successful
   */
  protected final boolean handleSetCountdownSym(Player player, String[] args) {
    // Auto-generate list of available variant types (exclude CUSTOM)
    String availableStyles = Arrays.stream(VariantType.values())
        .filter(type -> type != VariantType.CUSTOM)
        .map(Enum::name)
        .collect(Collectors.joining(", "));

    if (args.length < 2) {
      player.sendMessage(
        Component.text("Usage: /" + fragment.getCommandName() + " setcountdownsym <style> [width]", NamedTextColor.YELLOW)
      );
      player.sendMessage(
        Component.text("Styles: " + availableStyles, NamedTextColor.GRAY)
      );
      player.sendMessage(
        Component.text("Width: 1-10 (optional, defaults to 1 for most styles)", NamedTextColor.GRAY)
      );
      return true;
    }

    // Parse variant type
    VariantType variantType;
    try {
      variantType = VariantType.valueOf(args[1].toUpperCase());
      if (variantType == VariantType.CUSTOM) {
        throw new IllegalArgumentException("CUSTOM variant type is not allowed");
      }
    } catch (IllegalArgumentException e) {
      player.sendMessage(
        Component.text("Invalid style: " + args[1], NamedTextColor.RED)
      );
      player.sendMessage(
        Component.text("Valid styles: " + availableStyles, NamedTextColor.GRAY)
      );
      return true;
    }

    // Parse width parameter (optional)
    int width = 1;
    if (args.length >= 3) {
      try {
        width = Integer.parseInt(args[2]);
        if (width < 1 || width > 10) {
          player.sendMessage(
            Component.text("Width must be between 1 and 10!", NamedTextColor.RED)
          );
          return true;
        }
      } catch (NumberFormatException e) {
        player.sendMessage(
          Component.text("Invalid width: " + args[2], NamedTextColor.RED)
        );
        return true;
      }
    }

    // Set the preference
    playerPreferenceManager.setPreference(player, variantType, width);

    player.sendMessage(
      Component.text("✓ Your countdown style has been set to " + variantType.name() +
        (width > 1 ? " (width: " + width + ")" : ""), NamedTextColor.GREEN)
    );
    player.sendMessage(
      Component.text("This preference will persist across server restarts.", NamedTextColor.GRAY)
    );

    return true;
  }

  /**
   * Show help message - 100% auto-generated from fragment metadata.
   *
   * @param player The player
   */
  protected final void showHelp(Player player) {
    player.sendMessage(
      Component.text("=== " + fragment.getName() + " Commands ===", NamedTextColor.GOLD)
    );
    player.sendMessage(
      Component.text("/" + fragment.getCommandName() + " equip", NamedTextColor.YELLOW)
        .append(Component.text(" - Equip " + fragment.getName(), NamedTextColor.GRAY))
    );

    // Auto-generate ability commands from fragment (with action emoji)
    for (AbilityDefinition ability : fragment.getAbilities()) {
      String actionEmoji = ability.getActionEmoji();
      if (actionEmoji != null && !actionEmoji.isEmpty()) {
        actionEmoji = " " + actionEmoji;
      } else {
        actionEmoji = "";
      }
      player.sendMessage(
        Component.text("/" + fragment.getCommandName() + " " + ability.getNumber(), NamedTextColor.YELLOW)
          .append(Component.text(actionEmoji + " - " + ability.getName() + " (" + ability.getDescription() + ")", NamedTextColor.GRAY))
      );
    }

    player.sendMessage(
      Component.text("/" + fragment.getCommandName() + " status", NamedTextColor.YELLOW)
        .append(Component.text(" - Show fragment status", NamedTextColor.GRAY))
    );
    player.sendMessage(
      Component.text("/" + fragment.getCommandName() + " setcountdownsym", NamedTextColor.YELLOW)
        .append(Component.text(" - Set your countdown style", NamedTextColor.GRAY))
    );
    player.sendMessage(
      Component.text("/" + fragment.getCommandName() + " help", NamedTextColor.YELLOW)
        .append(Component.text(" - Show this help", NamedTextColor.GRAY))
    );
  }

  @Override
  public final List<String> onTabComplete(
    CommandSender sender,
    Command command,
    String alias,
    String[] args
  ) {
    List<String> completions = new ArrayList<>();

    if (args.length == 1) {
      completions.addAll(List.of(
        "1", "2", "equip", "status", "setcountdownsym", "help"
      ));

      // Auto-generate aliases from fragment
      for (AbilityDefinition ability : fragment.getAbilities()) {
        completions.addAll(ability.getAliases());
      }

      String partial = args[0].toLowerCase();
      completions.removeIf(comp -> !comp.toLowerCase().startsWith(partial));
    } else if (args.length == 2 && args[0].equalsIgnoreCase("setcountdownsym")) {
      // Tab complete for style names - auto-generated from VariantType enum
      Arrays.stream(VariantType.values())
          .filter(type -> type != VariantType.CUSTOM)
          .map(Enum::name)
          .forEach(completions::add);

      String partial = args[1].toUpperCase();
      completions.removeIf(comp -> !comp.startsWith(partial));
    }

    return completions;
  }
}