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
 */
public class ElementalItems {

  // Material types for items
  private static final Material HEAVY_CORE_MATERIAL = Material.NETHERITE_INGOT;

  /**
   * Create a Heavy Core item with custom display name and lore.
   *
   * @return ItemStack representing a Heavy Core
   */
  public static ItemStack createHeavyCore() {
    ItemStack item = new ItemStack(HEAVY_CORE_MATERIAL);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(
        "Heavy Core",
        NamedTextColor.DARK_PURPLE
      ));

      List<Component> lore = new ArrayList<>();
      lore.add(Component.text(
        "A dense core forged from obsidian, iron, and dragon essence.",
        NamedTextColor.GRAY
      ));
      lore.add(Component.text(
        "Used to craft elemental fragments.",
        NamedTextColor.AQUA
      ));
      lore.add(Component.text(
        "",
        NamedTextColor.WHITE
      ));
      lore.add(Component.text(
        "Requires OP to craft",
        NamedTextColor.YELLOW
      ));

      meta.lore(lore);
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      item.setItemMeta(meta);
    }
    return item;
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
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      item.setItemMeta(meta);
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
   *
   * @return Heavy Core ItemStack
   */
  public static ItemStack getHeavyCore() {
    return createHeavyCore();
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
    if (item == null || item.getItemMeta() == null || item.getItemMeta().displayName() == null) {
      return false;
    }

    String displayName = item.getItemMeta().displayName().toString();
    return displayName.contains(fragmentType.getDisplayName());
  }

  /**
   * Check if an ItemStack is a Heavy Core.
   *
   * @param item The ItemStack to check
   * @return true if the item is a Heavy Core
   */
  public static boolean isHeavyCore(ItemStack item) {
    if (item == null || item.getItemMeta() == null || item.getItemMeta().displayName() == null) {
      return false;
    }
    return item.getItemMeta().displayName().toString().contains("Heavy Core");
  }
}
