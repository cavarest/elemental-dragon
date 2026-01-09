package org.cavarest.elementaldragon.command;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.BurningFragment;
import org.cavarest.elementaldragon.fragment.FragmentManager;

/**
 * Command handler for /fire command - dedicated Burning Fragment abilities.
 * Simplified to constructor-only by querying Fragment for all metadata.
 */
public class FireCommand extends AbstractFragmentCommand {

  /**
   * Create a new FireCommand.
   *
   * @param plugin The plugin instance
   * @param fragmentManager The fragment manager
   */
  public FireCommand(ElementalDragon plugin, FragmentManager fragmentManager) {
    super(plugin, fragmentManager, new BurningFragment(plugin));
  }
}