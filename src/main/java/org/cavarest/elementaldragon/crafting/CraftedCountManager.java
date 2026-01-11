package org.cavarest.elementaldragon.crafting;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentType;

/**
 * Manages persistent tracking of crafted fragment quantities per player.
 *
 * ORIGINAL SPECIFICATION IMPLEMENTATION:
 * - Burning Fragment: Craftable Quantity = 2
 * - Agility Fragment: Craftable Quantity = 2
 * - Immortal Fragment: Craftable Quantity = 2
 * - Corrupted Core: Craftable Quantity = 1
 *
 * Uses Player PersistentDataContainer for storage (survives server restarts).
 */
public class CraftedCountManager {

  private final ElementalDragon plugin;

  // NamespacedKeys for storing crafted counts in player PDC
  private final NamespacedKey burningCountKey;
  private final NamespacedKey agilityCountKey;
  private final NamespacedKey immortalCountKey;
  private final NamespacedKey corruptedCountKey;

  // Maximum craftable quantities (Original Specification)
  private static final int BURNING_MAX_CRAFTABLE = 2;
  private static final int AGILITY_MAX_CRAFTABLE = 2;
  private static final int IMMORTAL_MAX_CRAFTABLE = 2;
  private static final int CORRUPTED_MAX_CRAFTABLE = 1;

  /**
   * Create a new CraftedCountManager.
   *
   * @param plugin The plugin instance
   */
  public CraftedCountManager(ElementalDragon plugin) {
    this.plugin = plugin;

    // Initialize NamespacedKeys for PDC storage
    this.burningCountKey = new NamespacedKey(plugin, "crafted_burning");
    this.agilityCountKey = new NamespacedKey(plugin, "crafted_agility");
    this.immortalCountKey = new NamespacedKey(plugin, "crafted_immortal");
    this.corruptedCountKey = new NamespacedKey(plugin, "crafted_corrupted");
  }

  /**
   * Get the number of fragments a player has crafted for a specific type.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @return The number crafted (0 if none)
   */
  public int getCraftedCount(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return 0;
    }

    PersistentDataContainer pdc = player.getPersistentDataContainer();
    NamespacedKey key = getKeyForFragment(fragmentType);

    if (key == null) {
      return 0;
    }

    return pdc.getOrDefault(key, PersistentDataType.INTEGER, 0);
  }

  /**
   * Increment the crafted count for a specific fragment type.
   *
   * @param player The player
   * @param fragmentType The fragment type
   */
  public void incrementCraftedCount(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return;
    }

    PersistentDataContainer pdc = player.getPersistentDataContainer();
    NamespacedKey key = getKeyForFragment(fragmentType);

    if (key == null) {
      return;
    }

    int currentCount = getCraftedCount(player, fragmentType);
    pdc.set(key, PersistentDataType.INTEGER, currentCount + 1);
  }

  /**
   * Get the maximum craftable count for a specific fragment type.
   *
   * @param fragmentType The fragment type
   * @return The maximum craftable count
   */
  public int getMaxCraftableCount(FragmentType fragmentType) {
    if (fragmentType == null) {
      return 0;
    }

    switch (fragmentType) {
      case BURNING:
        return BURNING_MAX_CRAFTABLE;
      case AGILITY:
        return AGILITY_MAX_CRAFTABLE;
      case IMMORTAL:
        return IMMORTAL_MAX_CRAFTABLE;
      case CORRUPTED:
        return CORRUPTED_MAX_CRAFTABLE;
      default:
        return 0;
    }
  }

  /**
   * Check if a player can craft another fragment of the specified type.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @return true if the player can craft another fragment, false otherwise
   */
  public boolean canCraft(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return false;
    }

    int currentCount = getCraftedCount(player, fragmentType);
    int maxCount = getMaxCraftableCount(fragmentType);

    return currentCount < maxCount;
  }

  /**
   * Get the appropriate NamespacedKey for a fragment type.
   *
   * @param fragmentType The fragment type
   * @return The corresponding NamespacedKey, or null if invalid
   */
  private NamespacedKey getKeyForFragment(FragmentType fragmentType) {
    if (fragmentType == null) {
      return null;
    }

    switch (fragmentType) {
      case BURNING:
        return burningCountKey;
      case AGILITY:
        return agilityCountKey;
      case IMMORTAL:
        return immortalCountKey;
      case CORRUPTED:
        return corruptedCountKey;
      default:
        return null;
    }
  }

  /**
   * Reset the crafted count for a specific fragment type (admin command use).
   *
   * @param player The player
   * @param fragmentType The fragment type
   */
  public void resetCraftedCount(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return;
    }

    PersistentDataContainer pdc = player.getPersistentDataContainer();
    NamespacedKey key = getKeyForFragment(fragmentType);

    if (key == null) {
      return;
    }

    pdc.set(key, PersistentDataType.INTEGER, 0);
  }

  /**
   * Reset all crafted counts for a player (admin command use).
   *
   * @param player The player
   */
  public void resetAllCraftedCounts(Player player) {
    if (player == null) {
      return;
    }

    resetCraftedCount(player, FragmentType.BURNING);
    resetCraftedCount(player, FragmentType.AGILITY);
    resetCraftedCount(player, FragmentType.IMMORTAL);
    resetCraftedCount(player, FragmentType.CORRUPTED);
  }
}
