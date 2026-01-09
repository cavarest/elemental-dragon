package org.cavarest.elementaldragon.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.AbilityDefinition;
import org.cavarest.elementaldragon.fragment.Fragment;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.lore.ChronicleManager;

import java.util.ArrayList;
import java.util.List;

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
   *
   * @param player The player
   * @return true if successful
   */
  protected final boolean handleEquip(Player player) {
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

    // Auto-generate ability commands from fragment
    for (AbilityDefinition ability : fragment.getAbilities()) {
      player.sendMessage(
        Component.text("/" + fragment.getCommandName() + " " + ability.getNumber(), NamedTextColor.YELLOW)
          .append(Component.text(" - " + ability.getName() + " (" + ability.getDescription() + ")", NamedTextColor.GRAY))
      );
    }

    player.sendMessage(
      Component.text("/" + fragment.getCommandName() + " status", NamedTextColor.YELLOW)
        .append(Component.text(" - Show fragment status", NamedTextColor.GRAY))
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
        "1", "2", "equip", "status", "help"
      ));

      // Auto-generate aliases from fragment
      for (AbilityDefinition ability : fragment.getAbilities()) {
        completions.addAll(ability.getAliases());
      }

      String partial = args[0].toLowerCase();
      completions.removeIf(comp -> !comp.toLowerCase().startsWith(partial));
    }

    return completions;
  }
}