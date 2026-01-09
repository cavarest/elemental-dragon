package org.cavarest.elementaldragon.ability;

import org.bukkit.entity.Player;

/**
 * Interface for all abilities.
 */
public interface Ability {

  /**
   * Execute the ability for a player.
   *
   * @param player The player using the ability
   * @return true if the ability was successfully executed
   */
  boolean execute(Player player);

  /**
   * Check if the player has the required item for this ability.
   *
   * @param player The player
   * @return true if the player has the required item
   */
  boolean hasRequiredItem(Player player);

  /**
   * Get the cooldown time in milliseconds.
   *
   * @return Cooldown time in milliseconds
   */
  long getCooldownMillis();

  /**
   * Get the ability name.
   *
   * @return The ability name
   */
  String getName();
}
