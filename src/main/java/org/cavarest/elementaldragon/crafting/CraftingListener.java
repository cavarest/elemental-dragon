package org.cavarest.elementaldragon.crafting;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;

/**
 * Listener for crafting events to validate Heavy Core usage in fragment recipes.
 * Since Heavy Core is a custom item (Netherite Ingot with display name),
 * we need to validate that the center ingredient is actually a Heavy Core
 * and not just a regular Netherite Ingot.
 */
public class CraftingListener implements Listener {

  private final ElementalDragon plugin;
  private final CraftingManager craftingManager;

  public CraftingListener(ElementalDragon plugin, CraftingManager craftingManager) {
    this.plugin = plugin;
    this.craftingManager = craftingManager;
  }

  /**
   * Validate Heavy Core in fragment crafting recipes.
   * Checks that the center ingredient is actually a Heavy Core (not just any Netherite Ingot).
   * Also prevents crafting duplicates of fragments the player already possesses.
   *
   * Note: Corrupted Core uses vanilla Heavy Core (Material.HEAVY_CORE), not custom Heavy Core,
   * so it doesn't need Heavy Core validation.
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPrepareItemCraft(PrepareItemCraftEvent event) {
    if (event.getRecipe() == null) {
      return;
    }

    CraftingInventory inventory = event.getInventory();
    ItemStack result = event.getInventory().getResult();

    if (result == null) {
      return;
    }

    // Check if this is a fragment recipe
    FragmentType resultFragmentType = getFragmentType(result);
    if (resultFragmentType == null) {
      return;
    }

    // Get the player crafting
    if (!(event.getView().getPlayer() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getView().getPlayer();

    // Check if player already has this fragment type
    if (hasFragmentInInventory(player, resultFragmentType)) {
      event.getInventory().setResult(null);
      player.sendMessage(Component.text(
        "⚠️ You already have a " + resultFragmentType.getDisplayName() + "!",
        NamedTextColor.RED
      ));
      player.sendMessage(Component.text(
        "You can only possess ONE of each fragment type.",
        NamedTextColor.GRAY
      ));
      return;
    }

    // Skip Heavy Core validation for Corrupted Core (uses vanilla Heavy Core, not custom)
    if (resultFragmentType == FragmentType.CORRUPTED) {
      return;
    }

    // Validate custom Heavy Core for Burning, Agility, and Immortal fragments
    ItemStack[] matrix = inventory.getMatrix();
    if (matrix.length < 9) {
      return;
    }

    ItemStack centerItem = matrix[4];  // Center slot

    // Validate center item is Heavy Core
    if (!ElementalItems.isHeavyCore(centerItem)) {
      // Center item is not a Heavy Core - cancel the craft
      event.getInventory().setResult(null);
      player.sendMessage(Component.text(
        "⚠️ Invalid recipe! Center item must be a Heavy Core.",
        NamedTextColor.RED
      ));
      player.sendMessage(Component.text(
        "Craft Heavy Core first or get one from an admin.",
        NamedTextColor.GRAY
      ));
    }
  }

  /**
   * Check if player has a specific fragment type in their inventory.
   *
   * @param player The player
   * @param fragmentType The fragment type to check
   * @return true if player has this fragment type
   */
  private boolean hasFragmentInInventory(Player player, FragmentType fragmentType) {
    for (ItemStack item : player.getInventory().getContents()) {
      FragmentType itemType = getFragmentType(item);
      if (itemType == fragmentType) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the fragment type from an item.
   *
   * @param item The item to check
   * @return FragmentType or null if not a fragment
   */
  private FragmentType getFragmentType(ItemStack item) {
    if (item == null || !item.hasItemMeta()) {
      return null;
    }

    ItemMeta meta = item.getItemMeta();
    if (meta.displayName() == null) {
      return null;
    }

    String displayName = meta.displayName().toString();

    if (displayName.contains("Burning Fragment")) {
      return FragmentType.BURNING;
    } else if (displayName.contains("Agility Fragment")) {
      return FragmentType.AGILITY;
    } else if (displayName.contains("Immortal Fragment")) {
      return FragmentType.IMMORTAL;
    } else if (displayName.contains("Corrupted Core")) {
      return FragmentType.CORRUPTED;
    }

    return null;
  }

  /**
   * Check if an item is a fragment item (DEPRECATED - use getFragmentType).
   *
   * @param item The item to check
   * @return true if the item is a fragment
   */
  @Deprecated
  private boolean isFragmentItem(ItemStack item) {
    return getFragmentType(item) != null;
  }
}