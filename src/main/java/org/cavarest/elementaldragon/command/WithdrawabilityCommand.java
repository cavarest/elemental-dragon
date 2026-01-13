package org.cavarest.elementaldragon.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

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
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  public WithdrawabilityCommand(ElementalDragon plugin, FragmentManager fragmentManager) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // Only players can use this command
    if (!(sender instanceof Player)) {
      sender.sendMessage(miniMessage.deserialize(
        "<red>This command can only be used by players.</red>"
      ));
      return true;
    }

    Player player = (Player) sender;

    // Check if player has a fragment equipped
    if (!fragmentManager.hasFragmentEquipped(player)) {
      player.sendMessage(miniMessage.deserialize(
        "<gray>You don't have any fragment abilities equipped.</gray>"
      ));
      return true;
    }

    // Unequip the fragment (removes abilities, keeps item)
    fragmentManager.unequipFragment(player);

    player.sendMessage(miniMessage.deserialize(
      "<yellow>Your fragment abilities have been withdrawn.</yellow>"
    ));
    player.sendMessage(miniMessage.deserialize(
      "<gray>The fragment item remains in your inventory.</gray>"
    ));

    return true;
  }
}
