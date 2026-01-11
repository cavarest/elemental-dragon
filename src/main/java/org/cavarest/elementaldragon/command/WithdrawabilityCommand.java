package org.cavarest.elementaldragon.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Command to withdraw/unequip the currently equipped fragment's abilities.
 * The fragment item remains in the player's inventory, but abilities are removed.
 *
 * Usage: /withdrawability
 *
 * This is a player command (not admin) that removes the currently equipped
 * fragment's abilities. Since only one fragment can be equipped at a time,
 * no arguments are needed.
 */
public class WithdrawabilityCommand implements CommandExecutor {

  private final ElementalDragon plugin;
  private final FragmentManager fragmentManager;

  public WithdrawabilityCommand(ElementalDragon plugin, FragmentManager fragmentManager) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // Only players can use this command
    if (!(sender instanceof Player)) {
      sender.sendMessage(Component.text(
        "This command can only be used by players.",
        NamedTextColor.RED
      ));
      return true;
    }

    Player player = (Player) sender;

    // Check if player has a fragment equipped
    if (!fragmentManager.hasFragmentEquipped(player)) {
      player.sendMessage(Component.text(
        "You don't have any fragment abilities equipped.",
        NamedTextColor.GRAY
      ));
      return true;
    }

    // Unequip the fragment (removes abilities, keeps item)
    fragmentManager.unequipFragment(player);

    player.sendMessage(Component.text(
      "Your fragment abilities have been withdrawn.",
      NamedTextColor.YELLOW
    ));
    player.sendMessage(Component.text(
      "The fragment item remains in your inventory.",
      NamedTextColor.GRAY
    ));

    return true;
  }
}
