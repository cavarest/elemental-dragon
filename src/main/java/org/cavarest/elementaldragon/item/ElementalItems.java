package org.cavarest.elementaldragon.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
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
}
