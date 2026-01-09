package org.cavarest.elementaldragon.command;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.ImmortalFragment;
import org.cavarest.elementaldragon.fragment.FragmentManager;

/**
 * Command handler for /immortal command - dedicated Immortal Fragment abilities.
 * Simplified to constructor-only by querying Fragment for all metadata.
 */
public class ImmortalCommand extends AbstractFragmentCommand {

  /**
   * Create a new ImmortalCommand.
   *
   * @param plugin The plugin instance
   * @param fragmentManager The fragment manager
   */
  public ImmortalCommand(ElementalDragon plugin, FragmentManager fragmentManager) {
    super(plugin, fragmentManager, new ImmortalFragment(plugin));
  }
}