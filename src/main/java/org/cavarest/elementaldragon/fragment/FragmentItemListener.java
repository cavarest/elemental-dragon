package org.cavarest.elementaldragon.fragment;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.item.ElementalItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener for fragment item interactions.
 * - Right-click to equip fragment to offhand
 * - Prevents duplicate fragment pickup (player can only carry one of each type)
 * - Fragments can be stored in containers without restriction
 */
public class FragmentItemListener implements Listener {

  private final ElementalDragon plugin;
  private final FragmentManager fragmentManager;
  private final Map<UUID, Long> lastEquipClickTimes;
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  // Cooldown between equip attempts (500ms) to prevent spam clicking
  private static final long EQUIP_COOLDOWN_MS = 500;

  public FragmentItemListener(ElementalDragon plugin, FragmentManager fragmentManager) {
    this.plugin = plugin;
    this.fragmentManager = fragmentManager;
    this.lastEquipClickTimes = new HashMap<>();
  }

  /**
   * Handle fragment right-click to equip.
   * Uses HIGHEST priority to run before item's default behaviors (like FIRE_CHARGE throwing fire).
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerInteract(PlayerInteractEvent event) {
    // Only handle right-click actions
    if (event.getAction() != Action.RIGHT_CLICK_AIR &&
        event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();

    // Check cooldown to prevent spam clicking
    long lastClickTime = lastEquipClickTimes.getOrDefault(playerId, 0L);
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastClickTime < EQUIP_COOLDOWN_MS) {
      // Still in cooldown, just cancel the event but don't send any message
      event.setCancelled(true);
      return;
    }

    // Get item from hand (main hand or offhand)
    ItemStack item = getItemInHand(player);
    if (item == null || !item.hasItemMeta()) {
      return;
    }

    // Check if item is a fragment
    FragmentType fragmentType = getFragmentType(item);
    if (fragmentType == null) {
      return;
    }

    // Cancel the event to prevent default item behaviors (FIRE_CHARGE throwing fire, HEAVY_CORE placement)
    event.setCancelled(true);

    // Update last click time
    lastEquipClickTimes.put(playerId, currentTime);

    // Equip the fragment
    boolean success = fragmentManager.equipFragment(player, fragmentType);

    if (success) {
      // Check if already equipped message was shown by FragmentManager
      // If equipFragment returns true and FragmentManager sent "Already equipped!",
      // we should not send the full equip messages
      FragmentType currentlyEquipped = fragmentManager.getEquippedFragment(player);
      if (currentlyEquipped == fragmentType) {
        // FragmentManager handles "Already equipped!" message internally
        // No additional action needed
      } else {
        // Use MiniMessage for styled message with emoji
        player.sendMessage(miniMessage.deserialize(
          "<gold>Equipped <white>" + fragmentType.getDisplayName() + "</white>!</gold>\n" +
          "<gray>" + fragmentType.getPassiveBonus() + "</gray>"
        ));
      }
    }
  }

  /**
   * Get the item in the player's hand (checks both main hand and offhand).
   *
   * @param player The player
   * @return The item in hand, or null if none
   */
  private ItemStack getItemInHand(Player player) {
    // Check main hand first
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    if (mainHand != null && mainHand.hasItemMeta()) {
      return mainHand;
    }
    // Then check offhand
    return player.getInventory().getItemInOffHand();
  }

  /**
   * Prevent players from picking up a DUPLICATE of the same fragment type.
   * Players CAN have multiple different fragments (fire + agile + immortal + corrupt).
   * Players CANNOT have duplicates (two fire fragments, two agile fragments, etc.).
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityPickupItem(EntityPickupItemEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();
    ItemStack pickupItem = event.getItem().getItemStack();

    // Check if the item being picked up is a fragment
    FragmentType pickupType = getFragmentType(pickupItem);
    if (pickupType == null) {
      return;
    }

    // Count how many of this SAME fragment type the player already has
    int existingCount = countFragmentType(player.getInventory().getContents(), pickupType);
    if (existingCount > 0) {
      // Cancel pickup - player already has this fragment type
      event.setCancelled(true);
      player.sendMessage(miniMessage.deserialize(
        "<red>⚠️ You can only possess ONE <white>" + pickupType.getDisplayName() + "</white> at a time!</red>\n" +
        "<gray>Drop or store your existing <white>" + pickupType.getDisplayName() + "</white> before picking up another.</gray>"
      ));
    }
  }

  // Drop detection handled by listener.FragmentItemListener
  // Container duplicate restriction removed per user feedback Issue 7.
  // Fragments can now be stored freely in containers (chests, shulkers, etc.)

  /**
   * Handle fragment drops - automatically unequip if the dropped fragment was equipped.
   * This ensures passive effects are removed when the player loses the fragment.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    ItemStack droppedItem = event.getItemDrop().getItemStack();

    // Check if the dropped item is a fragment
    FragmentType droppedType = getFragmentType(droppedItem);
    if (droppedType == null) {
      return;
    }

    // Check if this fragment type is currently equipped
    FragmentType equippedType = fragmentManager.getEquippedFragment(player);
    if (equippedType == null) {
      return;
    }

    // If the dropped fragment matches the equipped fragment, unequip it silently
    // (we send our own contextual message below)
    if (droppedType == equippedType) {
      fragmentManager.unequipFragment(player, true);

      // Inform the player that abilities have been withdrawn
      player.sendMessage(miniMessage.deserialize(
        "<yellow>Your <white>" + droppedType.getDisplayName() + "</white> abilities have been withdrawn.</yellow>\n" +
        "<gray>The fragment item remains on the ground. Pick it up and re-equip to reactivate.</gray>"
      ));
    }
  }

  /**
   * Count how many items of a specific fragment type exist in an inventory.
   * Used to prevent duplicate fragment pickup.
   *
   * @param contents The inventory contents
   * @param targetType The fragment type to count
   * @return The count of matching fragments
   */
  private int countFragmentType(ItemStack[] contents, FragmentType targetType) {
    int count = 0;
    for (ItemStack item : contents) {
      FragmentType type = getFragmentType(item);
      if (type == targetType) {
        count += item.getAmount();
      }
    }
    return count;
  }

  /**
   * Determine fragment type from item.
   *
   * @param item The item to check
   * @return FragmentType or null if not a fragment
   */
  private FragmentType getFragmentType(ItemStack item) {
    if (item == null || !item.hasItemMeta()) {
      return null;
    }

    for (FragmentType type : FragmentType.values()) {
      if (ElementalItems.isFragment(item, type)) {
        return type;
      }
    }
    return null;
  }
}