package org.cavarest.dragonegglightning.command;

import org.cavarest.dragonegglightning.DragonEggLightningPlugin;
import org.cavarest.dragonegglightning.ability.Ability;
import org.cavarest.dragonegglightning.ability.AbilityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Command handler for /ability command with tab completion support.
 */
public class AbilityCommand implements CommandExecutor, TabCompleter {

  private final DragonEggLightningPlugin plugin;
  private final AbilityManager abilityManager;

  public AbilityCommand(
    DragonEggLightningPlugin plugin,
    AbilityManager abilityManager
  ) {
    this.plugin = plugin;
    this.abilityManager = abilityManager;
  }

  @Override
  public boolean onCommand(
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

    // Handle about command
    if (args.length == 1 && args[0].equalsIgnoreCase("about")) {
      plugin.sendPluginInfo(player);
      return true;
    }

    // Handle help command
    if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
      player.sendMessage(
        Component.text("=== DragonEggLightning Help ===", NamedTextColor.GOLD)
      );
      player.sendMessage(
        Component.text("How to use the Dragon Egg Lightning ability:", NamedTextColor.WHITE)
      );
      player.sendMessage(
        Component.text("1. Hold a Dragon Egg in your offhand", NamedTextColor.GRAY)
      );
      player.sendMessage(
        Component.text("2. Use /ability 1 to cast lightning strike", NamedTextColor.GRAY)
      );
      player.sendMessage(
        Component.text("3. Lightning will strike your target 3 times", NamedTextColor.GRAY)
      );
      player.sendMessage(
        Component.text("4. Each strike deals 2 hearts of damage", NamedTextColor.GRAY)
      );
      player.sendMessage(
        Component.text("5. Damage bypasses armor protection", NamedTextColor.GRAY)
      );
      player.sendMessage(
        Component.text("6. 60 second cooldown between uses", NamedTextColor.GRAY)
      );
      return true;
    }

    // Handle version command
    if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
      plugin.sendPluginInfo(player);
      return true;
    }

    // Check arguments for ability use
    if (args.length != 1) {
      player.sendMessage(
        Component.text("Usage: /ability <number> | /ability version | /ability help", NamedTextColor.RED)
      );
      return true;
    }

    // Parse ability ID
    int abilityId;
    try {
      abilityId = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      player.sendMessage(
        Component.text("Invalid ability number! Use /ability help for usage.", NamedTextColor.RED)
      );
      return true;
    }

    // Get ability
    Ability ability = abilityManager.getAbility(abilityId);
    if (ability == null) {
      player.sendMessage(
        Component.text("Ability not found! Use /ability help for available abilities.", NamedTextColor.RED)
      );
      return true;
    }

    // Check if player has required item
    if (!ability.hasRequiredItem(player)) {
      player.sendMessage(
        Component.text(
          "You must hold a Dragon Egg in your offhand to use this ability!",
          NamedTextColor.RED
        )
      );
      return true;
    }

    // Check cooldown
    if (abilityManager.isOnCooldown(player)) {
      int remaining = abilityManager.getRemainingCooldown(player);
      player.sendMessage(
        Component.text(
          "Ability on cooldown! " + remaining + " seconds remaining.",
          NamedTextColor.RED
        )
      );
      return true;
    }

    // Use ability
    boolean success = abilityManager.useAbility(player, ability);
    if (!success) {
      player.sendMessage(
        Component.text(
          "Failed to use ability!",
          NamedTextColor.RED
        )
      );
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(
    CommandSender sender,
    Command command,
    String alias,
    String[] args
  ) {
    List<String> completions = new ArrayList<>();

    if (args.length == 1) {
      // Only show supported commands
      completions.add("1");      // Lightning ability
      completions.add("version"); // Version info
      completions.add("help");    // Help text

      // Filter completions based on what the player has typed
      String partial = args[0].toLowerCase();
      completions.removeIf(comp -> !comp.toLowerCase().startsWith(partial));
    }

    return completions;
  }
}
