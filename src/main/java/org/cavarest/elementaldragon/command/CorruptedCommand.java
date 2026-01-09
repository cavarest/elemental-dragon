package org.cavarest.elementaldragon.command;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.CorruptedCoreFragment;
import org.cavarest.elementaldragon.fragment.FragmentManager;

/**
 * Command handler for /corrupt command - dedicated Corrupted Core abilities.
 * Simplified to constructor-only by querying Fragment for all metadata.
 */
public class CorruptedCommand extends AbstractFragmentCommand {

  /**
   * Create a new CorruptedCommand.
   *
   * @param plugin The plugin instance
   * @param fragmentManager The fragment manager
   */
  public CorruptedCommand(ElementalDragon plugin, FragmentManager fragmentManager) {
    super(plugin, fragmentManager, new CorruptedCoreFragment(plugin));
  }
}