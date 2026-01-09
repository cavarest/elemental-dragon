package org.cavarest.elementaldragon.command;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.AgilityFragment;
import org.cavarest.elementaldragon.fragment.FragmentManager;

/**
 * Command handler for /agile command - dedicated Agility Fragment abilities.
 * Simplified to constructor-only by querying Fragment for all metadata.
 */
public class AgilityCommand extends AbstractFragmentCommand {

  /**
   * Create a new AgilityCommand.
   *
   * @param plugin The plugin instance
   * @param fragmentManager The fragment manager
   */
  public AgilityCommand(ElementalDragon plugin, FragmentManager fragmentManager) {
    super(plugin, fragmentManager, new AgilityFragment(plugin));
  }
}