package org.cavarest.elementaldragon.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.item.ElementalItems;

/**
 * Listener for crafting events to validate Heavy Core usage in fragment recipes.
 * Since Heavy Core is now a vanilla Minecraft item (Material.HEAVY_CORE),
 * we validate that the center ingredient is the vanilla Heavy Core.
 */
public class CraftingListener implements Listener {

  private final ElementalDragon plugin;
  private final CraftingManager craftingManager;
  private final CraftedCountManager craftedCountManager;
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  public CraftingListener(ElementalDragon plugin, CraftingManager craftingManager, CraftedCountManager craftedCountManager) {
    this.plugin = plugin;
    this.craftingManager = craftingManager;
    this.craftedCountManager = craftedCountManager;
  }

  /**
   * Track completed fragment crafts and increment crafted count.
   * This fires when the player actually takes the crafted fragment from the crafting table.
   * Sends a themed broadcast message to all players.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onCraftItem(CraftItemEvent event) {
    // Only track if event wasn't cancelled
    if (event.isCancelled()) {
      return;
    }

    ItemStack result = event.getRecipe().getResult();
    if (result == null) {
      return;
    }

    // Check if this is a fragment
    FragmentType fragmentType = getFragmentType(result);
    if (fragmentType == null) {
      return;
    }

    // Get the player crafting
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getWhoClicked();

    // Increment the crafted count for this fragment type
    craftedCountManager.incrementCraftedCount(player, fragmentType);

    // Send themed broadcast message to all players
    String themedMessage = getThemedCraftMessage(player.getName(), fragmentType);
    Bukkit.broadcast(miniMessage.deserialize(themedMessage), "elementaldragon.receive");
  }

  /**
   * Get the themed craft message for a fragment type.
   *
   * @param playerName The player who crafted the fragment
   * @param fragmentType The type of fragment crafted
   * @return Themed message string with MiniMessage formatting
   */
  private String getThemedCraftMessage(String playerName, FragmentType fragmentType) {
    switch (fragmentType) {
      case BURNING:
        return String.format(
          "<red>🔥 The flames of the ancient dragon acknowledge <yellow>%s</yellow> has forged the <red>Burning Fragment</red>!",
          playerName
        );
      case AGILITY:
        return String.format(
          "<aqua>💨 The winds of the ancient dragon acknowledge <yellow>%s</yellow> has forged the <aqua>Agility Fragment</aqua>!",
          playerName
        );
      case IMMORTAL:
        return String.format(
          "<green>🛡️ The earth of the ancient dragon acknowledges <yellow>%s</yellow> has forged the <green>Immortal Fragment</green>!",
          playerName
        );
      case CORRUPTED:
        return String.format(
          "<dark_purple>👁️ The void of the ancient dragon acknowledges <yellow>%s</yellow> has forged the <dark_purple>Corrupted Core</dark_purple>!",
          playerName
        );
      default:
        return String.format(
          "<gold>⚔️ <yellow>%s</yellow> has forged an elemental fragment!",
          playerName
        );
    }
  }

  /**
   * Validate Heavy Core in fragment crafting recipes.
   * Checks that the center ingredient is actually a vanilla Heavy Core.
   * Also validates crafting quantity limits per the original specification.
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

    // Check if player has reached the crafting limit for this fragment type (ORIGINAL SPECIFICATION)
    // Burning Fragment: 2 max, Agility Fragment: 2 max, Immortal Fragment: 2 max, Corrupted Core: 1 max
    if (!craftedCountManager.canCraft(player, resultFragmentType)) {
      event.getInventory().setResult(null);
      int current = craftedCountManager.getCraftedCount(player, resultFragmentType);
      int max = craftedCountManager.getMaxCraftableCount(resultFragmentType);
      player.sendMessage(Component.text(
        "⚠️ Crafting limit reached for " + resultFragmentType.getDisplayName() + "!",
        NamedTextColor.RED
      ));
      player.sendMessage(Component.text(
        "You have crafted " + current + "/" + max + " maximum.",
        NamedTextColor.GRAY
      ));
      return;
    }

    // Validate vanilla Heavy Core for all fragments (including Corrupted Core now)
    ItemStack[] matrix = inventory.getMatrix();
    if (matrix.length < 9) {
      return;
    }

    ItemStack centerItem = matrix[4];  // Center slot

    // Validate center item is vanilla Heavy Core
    if (!ElementalItems.isHeavyCore(centerItem)) {
      // Center item is not a Heavy Core - cancel the craft
      event.getInventory().setResult(null);
      player.sendMessage(Component.text(
        "⚠️ Invalid recipe! Center item must be a vanilla Heavy Core.",
        NamedTextColor.RED
      ));
      player.sendMessage(Component.text(
        "Heavy Core is a vanilla Minecraft item found in ancient cities.",
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