package org.cavarest.elementaldragon.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cavarest.elementaldragon.fragment.AgilityFragment;
import org.cavarest.elementaldragon.fragment.BurningFragment;
import org.cavarest.elementaldragon.fragment.CorruptedCoreFragment;
import org.cavarest.elementaldragon.fragment.Fragment;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.fragment.ImmortalFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for custom items used in the Elemental Dragon plugin.
 * Queries Fragment classes for all metadata - Single Source of Truth pattern.
 *
 * Note: Heavy Core is now a vanilla Minecraft item (Material.HEAVY_CORE).
 * Custom Heavy Core creation is deprecated - use vanilla item directly.
 */
public class ElementalItems {

  /**
   * Check if an ItemStack is a vanilla Heavy Core.
   * This is the validation method for fragment recipes.
   *
   * @param item The ItemStack to check
   * @return true if the item is a vanilla Heavy Core
   */
  public static boolean isHeavyCore(ItemStack item) {
    if (item == null) {
      return false;
    }
    // Check if it's the vanilla HEAVY_CORE material
    return item.getType() == Material.HEAVY_CORE;
  }

  /**
   * Create a Burning Fragment item - queries Fragment for ALL metadata.
   *
   * @return ItemStack representing a Burning Fragment
   */
  public static ItemStack createBurningFragment() {
    return createFragmentItem(new BurningFragment(null));
  }

  /**
   * Create an Agility Fragment item - queries Fragment for ALL metadata.
   *
   * @return ItemStack representing an Agility Fragment
   */
  public static ItemStack createAgilityFragment() {
    return createFragmentItem(new AgilityFragment(null));
  }

  /**
   * Create an Immortal Fragment item - queries Fragment for ALL metadata.
   *
   * @return ItemStack representing an Immortal Fragment
   */
  public static ItemStack createImmortalFragment() {
    return createFragmentItem(new ImmortalFragment(null));
  }

  /**
   * Create a Corrupted Core item - queries Fragment for ALL metadata.
   *
   * @return ItemStack representing a Corrupted Core
   */
  public static ItemStack createCorruptedCore() {
    return createFragmentItem(new CorruptedCoreFragment(null));
  }

  /**
   * Create a fragment item from a Fragment instance.
   * Queries Fragment for material, name, color, description, passive bonus.
   * This is the ONLY place fragment items are created - Single Source of Truth!
   *
   * @param fragment The fragment to create an item for
   * @return ItemStack representing the fragment
   */
  private static ItemStack createFragmentItem(Fragment fragment) {
    // Query fragment for its material
    ItemStack item = new ItemStack(fragment.getMaterial());
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      // Query fragment for its name and color
      meta.displayName(Component.text(
        fragment.getName(),
        fragment.getThemeColor()
      ));

      List<Component> lore = new ArrayList<>();

      // Query fragment for description and passive bonus
      lore.add(Component.text(
        fragment.getDescription(),
        NamedTextColor.GRAY
      ));
      lore.add(Component.text(
        fragment.getPassiveBonus(),
        NamedTextColor.AQUA
      ));
      lore.add(Component.text("", NamedTextColor.WHITE));
      lore.add(Component.text(
        "Right-click while holding to equip",
        NamedTextColor.YELLOW
      ));

      meta.lore(lore);
      // Add item flags for fireproof appearance and enchanted glint
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Shows enchanted glint effect
      item.setItemMeta(meta);

      // Make item glow by adding a fake enchantment (using a dummy enchant)
      // This gives the visual appearance without actual enchantment effects
      item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.MENDING, 1);
    }

    return item;
  }

  /**
   * Get an ItemStack for a fragment type by name.
   *
   * @param name The fragment type name (burning, agility, immortal, corrupted)
   * @return ItemStack representing the fragment, or null if not found
   */
  public static ItemStack getFragmentByName(String name) {
    if (name == null) {
      return null;
    }

    String upperName = name.toUpperCase();
    switch (upperName) {
      case "BURNING":
        return createBurningFragment();
      case "AGILITY":
        return createAgilityFragment();
      case "IMMORTAL":
        return createImmortalFragment();
      case "CORRUPTED":
        return createCorruptedCore();
      default:
        return null;
    }
  }

  /**
   * Get the Heavy Core ItemStack.
   * Now returns the vanilla Minecraft Heavy Core item.
   *
   * @return Heavy Core ItemStack (vanilla)
   */
  public static ItemStack getHeavyCore() {
    return new ItemStack(Material.HEAVY_CORE);
  }

  /**
   * Get all available fragment ItemStacks.
   *
   * @return Array of fragment ItemStacks
   */
  public static ItemStack[] getAllFragments() {
    return new ItemStack[]{
      createBurningFragment(),
      createAgilityFragment(),
      createImmortalFragment(),
      createCorruptedCore()
    };
  }

  /**
   * Check if an ItemStack matches a specific fragment type.
   *
   * @param item The ItemStack to check
   * @param fragmentType The fragment type to match
   * @return true if the item matches the fragment type
   */
  public static boolean isFragment(ItemStack item, FragmentType fragmentType) {
    return getFragmentType(item) == fragmentType;
  }

  /**
   * Get the FragmentType from an ItemStack.
   * Returns null if the item is not a fragment.
   * This is the SINGLE SOURCE OF TRUTH for fragment type detection.
   *
   * @param item The ItemStack to check
   * @return The FragmentType, or null if not a fragment
   */
  public static FragmentType getFragmentType(ItemStack item) {
    if (item == null || !item.hasItemMeta()) {
      return null;
    }

    try {
      ItemMeta meta = item.getItemMeta();
      if (meta == null || meta.displayName() == null) {
        return null;
      }

      String displayName = meta.displayName().toString();

      // Single source of truth: query displayName for fragment identification
      // This matches the display names set in createFragmentItem()
      if (displayName.contains("Burning Fragment")) {
        return FragmentType.BURNING;
      } else if (displayName.contains("Agility Fragment")) {
        return FragmentType.AGILITY;
      } else if (displayName.contains("Immortal Fragment")) {
        return FragmentType.IMMORTAL;
      } else if (displayName.contains("Corrupted Core")) {
        return FragmentType.CORRUPTED;
      }
    } catch (Exception e) {
      // Handle any unexpected errors gracefully
    }

    return null;
  }

  // ==================== DRY Inventory Checking Helpers ====================

  /**
   * Check if player has a specific fragment type in their inventory OR offhand.
   * This is the DRY helper for all inventory+offhand checking.
   * Bukkit's getContents() does NOT include offhand, so we check both.
   *
   * @param player The player to check
   * @param fragmentType The fragment type to look for
   * @return true if the fragment is found in main inventory or offhand
   */
  public static boolean hasFragmentInInventory(Player player, FragmentType fragmentType) {
    if (player == null || player.getInventory() == null) {
      return false;
    }

    // Check main inventory
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && getFragmentType(item) == fragmentType) {
        return true;
      }
    }

    // Check offhand explicitly (getContents() doesn't include offhand!)
    ItemStack offhandItem = player.getInventory().getItemInOffHand();
    if (offhandItem != null && getFragmentType(offhandItem) == fragmentType) {
      return true;
    }

    return false;
  }

  /**
   * Find ANY fragment in player's inventory (excluding a specific type).
   * Used for one-fragment limit enforcement.
   * Checks main inventory, offhand, AND cursor (for inventory operations).
   *
   * @param player The player to check
   * @param excludeType Fragment type to exclude (null to check for any fragment)
   * @return The fragment type found, or null if none
   */
  public static FragmentType getAnyFragmentExcept(Player player, FragmentType excludeType) {
    if (player == null || player.getInventory() == null) {
      return null;
    }

    // Check main inventory
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null) {
        FragmentType fragmentType = getFragmentType(item);
        if (fragmentType != null && fragmentType != excludeType) {
          return fragmentType;
        }
      }
    }

    // Check offhand explicitly (getContents() doesn't include offhand!)
    ItemStack offhandItem = player.getInventory().getItemInOffHand();
    if (offhandItem != null) {
      FragmentType fragmentType = getFragmentType(offhandItem);
      if (fragmentType != null && fragmentType != excludeType) {
        return fragmentType;
      }
    }

    // Check cursor item (when player is holding an item during inventory operations)
    ItemStack cursorItem = player.getItemOnCursor();
    if (cursorItem != null) {
      FragmentType fragmentType = getFragmentType(cursorItem);
      if (fragmentType != null && fragmentType != excludeType) {
        return fragmentType;
      }
    }

    return null;
  }

  /**
   * Check if player's inventory contains a specific material (including offhand).
   * DRY helper for inventory-wide material checking.
   *
   * @param player The player to check
   * @param material The material to look for
   * @return true if the material is found in main inventory or offhand
   */
  public static boolean hasMaterialInInventory(Player player, Material material) {
    if (player == null || player.getInventory() == null) {
      return false;
    }

    // Check main inventory (contains() doesn't include offhand!)
    if (player.getInventory().contains(material)) {
      return true;
    }

    // Check offhand explicitly
    ItemStack offhandItem = player.getInventory().getItemInOffHand();
    if (offhandItem != null && offhandItem.getType() == material) {
      return true;
    }

    return false;
  }
}
