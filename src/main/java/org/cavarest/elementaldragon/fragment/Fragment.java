package org.cavarest.elementaldragon.fragment;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Interface defining the contract for all elemental fragments.
 * Fragments provide unique abilities based on their elemental type.
 *
 * <p>This interface serves as the single source of truth for all fragment
 * metadata, including visual properties, ability definitions, and command
 * metadata. This eliminates duplication across commands and item classes.</p>
 */
public interface Fragment {

  /**
   * Get the display name of the fragment.
   *
   * @return The fragment name
   */
  String getName();

  /**
   * Get the fragment type.
   *
   * @return The FragmentType enum value
   */
  FragmentType getType();

  /**
   * Activate the fragment's passive effects for a player.
   *
   * @param player The player activating the fragment
   */
  void activate(Player player);

  /**
   * Activate a specific ability for the fragment.
   *
   * @param player The player using the ability
   * @param abilityNumber The ability number (1 or 2)
   */
  void activate(Player player, int abilityNumber);

  /**
   * Deactivate the fragment's passive effects for a player.
   *
   * @param player The player deactivating the fragment
   */
  void deactivate(Player player);

  /**
   * Get the lore description of the fragment.
   *
   * @return List of lore lines
   */
  List<String> getLore();

  /**
   * Get the cooldown time in milliseconds for the fragment's abilities.
   *
   * @return Cooldown time in milliseconds
   */
  long getCooldownMillis();

  /**
   * Get the fragment's description for display.
   *
   * @return Description text
   */
  String getDescription();

  // ===== Visual Properties (Single Source of Truth) =====

  /**
   * Get the Minecraft material used to represent this fragment in inventory.
   *
   * @return The Material for this fragment's item
   */
  Material getMaterial();

  /**
   * Get the theme color for this fragment's messages and displays.
   *
   * @return The NamedTextColor for this fragment
   */
  NamedTextColor getThemeColor();

  // ===== Ability Definitions (Single Source of Truth) =====

  /**
   * Get all ability definitions for this fragment.
   *
   * @return Immutable list of AbilityDefinition objects
   */
  List<AbilityDefinition> getAbilities();

  /**
   * Get a specific ability by its number.
   *
   * @param number The ability number (1 or 2)
   * @return The AbilityDefinition, or null if not found
   */
  AbilityDefinition getAbility(int number);

  /**
   * Get an ability by its command alias.
   *
   * @param alias The command alias to search for
   * @return The AbilityDefinition, or null if not found
   */
  AbilityDefinition getAbilityByAlias(String alias);

  // ===== Command Metadata (Single Source of Truth) =====

  /**
   * Get the command name for this fragment (e.g., "fire", "agility").
   *
   * @return The command name (lowercase)
   */
  String getCommandName();

  /**
   * Get the permission node for this fragment.
   *
   * @return The permission node string
   */
  String getPermissionNode();

  /**
   * Get the element name for this fragment (e.g., "FIRE", "WIND").
   *
   * @return The element name (uppercase)
   */
  String getElementName();

  /**
   * Get the passive bonus description for this fragment.
   *
   * @return Description of passive effects
   */
  String getPassiveBonus();
}
