package org.cavarest.elementaldragon.hud;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.ability.Ability;
import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.Plugin;
import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import me.catcoder.sidebar.SidebarLine;

import java.util.*;

/**
 * Manages HUD display for ability cooldowns using ProtocolSidebar.
 * Each ability shown on a separate line with a vertical progress bar.
 *
 * FORMAT: [Icon] /command [#] [ProgressBar] Status
 * - Ready: ⚡ /lightning 1  █ Ready
 * - Cooldown: 🔥 /fire 1    █ In 1m 1s
 *
 * Displays:
 * - Lightning ability (if dragon egg in offhand)
 * - Fragment Ability 1 (if fragment equipped)
 * - Fragment Ability 2 (if fragment equipped)
 */
public class HudManager implements Listener {

  private final ElementalDragon plugin;
  private final AbilityManager abilityManager;
  private final FragmentManager fragmentManager;
  private final CooldownManager cooldownManager;

  // Track sidebars and their ability lines per player
  private final Map<UUID, Sidebar<Component>> playerSidebars = new HashMap<>();
  private final Map<UUID, List<SidebarLine<Component>>> playerLines = new HashMap<>();

  // MiniMessage instance for styled text
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  // Ability name mappings
  private static final Map<String, AbilityInfo> ABILITY_INFO = new HashMap<>();

  static {
    // Lightning
    ABILITY_INFO.put(CooldownManager.LIGHTNING + ":0", new AbilityInfo(
      "⚡", CooldownManager.LIGHTNING, 0, "/lightning 1", "Lightning Strike", NamedTextColor.LIGHT_PURPLE, "light_purple"
    ));

    // Burning Fragment
    ABILITY_INFO.put(CooldownManager.FIRE + ":1", new AbilityInfo(
      "🔥", CooldownManager.FIRE, 1, "/fire 1", "Dragon's Wrath", NamedTextColor.RED, "red"
    ));
    ABILITY_INFO.put(CooldownManager.FIRE + ":2", new AbilityInfo(
      "🔥", CooldownManager.FIRE, 2, "/fire 2", "Infernal Dominion", NamedTextColor.RED, "red"
    ));

    // Agility Fragment
    ABILITY_INFO.put(CooldownManager.AGILE + ":1", new AbilityInfo(
      "💨", CooldownManager.AGILE, 1, "/agile 1", "Draconic Surge", NamedTextColor.AQUA, "aqua"
    ));
    ABILITY_INFO.put(CooldownManager.AGILE + ":2", new AbilityInfo(
      "💨", CooldownManager.AGILE, 2, "/agile 2", "Wing Burst", NamedTextColor.AQUA, "aqua"
    ));

    // Immortal Fragment
    ABILITY_INFO.put(CooldownManager.IMMORTAL + ":1", new AbilityInfo(
      "🛡️", CooldownManager.IMMORTAL, 1, "/immortal 1", "Draconic Reflex", NamedTextColor.GREEN, "green"
    ));
    ABILITY_INFO.put(CooldownManager.IMMORTAL + ":2", new AbilityInfo(
      "🛡️", CooldownManager.IMMORTAL, 2, "/immortal 2", "Essence Rebirth", NamedTextColor.GREEN, "green"
    ));

    // Corrupted Core - using single-width eye emoji (without variation selector)
    ABILITY_INFO.put(CooldownManager.CORRUPT + ":1", new AbilityInfo(
      "👁", CooldownManager.CORRUPT, 1, "/corrupt 1", "Dread Gaze", NamedTextColor.DARK_PURPLE, "dark_purple"
    ));
    ABILITY_INFO.put(CooldownManager.CORRUPT + ":2", new AbilityInfo(
      "👁", CooldownManager.CORRUPT, 2, "/corrupt 2", "Life Devourer", NamedTextColor.DARK_PURPLE, "dark_purple"
    ));
  }

  public HudManager(
    ElementalDragon plugin,
    AbilityManager abilityManager,
    FragmentManager fragmentManager,
    CooldownManager cooldownManager
  ) {
    this.plugin = plugin;
    this.abilityManager = abilityManager;
    this.fragmentManager = fragmentManager;
    this.cooldownManager = cooldownManager;

    // Register event listeners for player state changes
    Bukkit.getPluginManager().registerEvents(this, plugin);

    // Initialize HUD for already online players
    Bukkit.getScheduler().runTask(plugin, () -> {
      for (Player player : Bukkit.getOnlinePlayers()) {
        updatePlayerHud(player);
      }
    });
  }

  /**
   * Handle player join - create/update their HUD.
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    updatePlayerHud(event.getPlayer());
  }

  /**
   * Handle player quit - remove their HUD.
   */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    clearPlayerSidebar(event.getPlayer());
  }

  /**
   * Handle item held changes - update HUD when player switches fragments or checks offhand.
   */
  @EventHandler
  public void onItemHeldChange(PlayerItemHeldEvent event) {
    Player player = event.getPlayer();
    // Small delay to allow inventory to update
    scheduleHudUpdate(player);
  }

  /**
   * Handle inventory clicks - update HUD when player puts dragon egg in offhand.
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getWhoClicked();

    // Check if the click is on the player's inventory (not a container)
    // and if it involves the offhand slot (slot 40)
    if (event.getClickedInventory() == player.getInventory() && event.getSlot() == 40) {
      // Offhand slot changed - update HUD
      scheduleHudUpdate(player);
    }
  }

  /**
   * Handle creative mode item placement - update HUD when player puts dragon egg in offhand.
   */
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    // Check if player is holding dragon egg and interacting with offhand
    Player player = event.getPlayer();
    if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR ||
        event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
      // Check if the item being placed is a dragon egg
      org.bukkit.inventory.ItemStack item = event.getItem();
      if (item != null && item.getType() == org.bukkit.Material.DRAGON_EGG) {
        scheduleHudUpdate(player);
      }
    }
  }

  /**
   * Handle drag operations - update HUD when player drags items to offhand.
   */
  @EventHandler
  public void onInventoryDrag(InventoryDragEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getWhoClicked();

    // Check if the drag involves the offhand slot (slot 40)
    if (event.getRawSlots().contains(40)) {
      scheduleHudUpdate(player);
    }
  }

  /**
   * Handle hand swap events (F key) - update HUD when player swaps items between hands.
   */
  @EventHandler
  public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
    Player player = event.getPlayer();
    scheduleHudUpdate(player);
  }

  /**
   * Schedule HUD update with small delay to allow inventory to settle.
   */
  private void scheduleHudUpdate(Player player) {
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      updatePlayerHud(player);
      // Force immediate sidebar refresh after update
      Sidebar<Component> sidebar = playerSidebars.get(player.getUniqueId());
      if (sidebar != null) {
        sidebar.updateAllLines();
      }
    }, 2L);
  }

  /**
   * Update HUD for a specific player.
   * Shows sidebar with text-based progress bars for each ability.
   * Uses ProtocolSidebar's updatable lines for dynamic content.
   *
   * @param player The player
   */
  public void updatePlayerHud(Player player) {
    UUID playerId = player.getUniqueId();

    // Check which abilities should be displayed
    boolean hasLightning = hasLightningAbility(player);
    FragmentType equippedFragment = fragmentManager != null ?
      fragmentManager.getEquippedFragment(player) : null;

    // Determine which abilities should be displayed
    List<String> abilityKeys = new ArrayList<>();

    // Add Lightning ability key
    if (hasLightning) {
      abilityKeys.add("lightning:0");
    }

    // Add Fragment ability keys
    if (equippedFragment != null) {
      String element = getElementName(equippedFragment);
      abilityKeys.add(element + ":1");
      abilityKeys.add(element + ":2");
    }

    // Update or create sidebar
    if (!abilityKeys.isEmpty()) {
      try {
        Sidebar<Component> sidebar = playerSidebars.get(playerId);
        List<SidebarLine<Component>> currentLines = playerLines.get(playerId);
        boolean isNewSidebar = false;

        if (sidebar == null) {
          sidebar = ProtocolSidebar.newAdventureSidebar(
            Component.text("Elemental Dragon", NamedTextColor.GOLD),
            plugin
          );
          // Remove score numbers - we show countdown in the line text instead
          sidebar.getObjective().scoreNumberFormatBlank();
          playerSidebars.put(playerId, sidebar);
          currentLines = new ArrayList<>();
          playerLines.put(playerId, currentLines);
          isNewSidebar = true;
        } else {
          // Remove old lines that are no longer needed
          if (currentLines != null) {
            for (SidebarLine<Component> oldLine : currentLines) {
              sidebar.removeLine(oldLine);
            }
            currentLines.clear();
          }
        }

        // Add updatable lines for each ability
        for (String abilityKey : abilityKeys) {
          SidebarLine<Component> line = sidebar.addUpdatableLine(p -> {
            Component abilityText = buildAbilityLineForUpdater(p, abilityKey);
            return abilityText;
          });
          currentLines.add(line);
        }

        // Set up periodic updates BEFORE adding viewer (as per ProtocolSidebar README)
        // This uses FoliaLib for smooth, non-flickering updates
        if (isNewSidebar) {
          sidebar.updateLinesPeriodically(0, 10);
        }

        // Show to the player (safe to call multiple times)
        sidebar.addViewer(player);

        // Force immediate update for first-time visibility
        // This ensures sidebar appears immediately when dragon egg is equipped
        sidebar.updateAllLines();
      } catch (Exception e) {
        plugin.getLogger().severe("[HUD ERROR] Exception managing sidebar: " + e.getMessage());
        e.printStackTrace();
      }
    } else {
      // No abilities - remove sidebar
      clearPlayerSidebar(player);
    }
  }

  /**
   * Build a single ability line with MiniMessage styling.
   * Format: [Icon] /command [#]  [ProgressBar] Status
   * Example: ⚡ /lightning 1  █ Ready
   *
   * Uses MiniMessage for styling:
   * - Command has shadow: <shadow:#000000FF>/command</shadow>
   * - Ready text has bold and shadow: <bold><shadow>Ready</shadow></bold>
   * - Cooldown numbers are underlined: In <underlined>30</underlined>s
   * - Colors transition based on progress
   */
  private Component buildAbilityLine(Player player, String abilityKey) {
    AbilityInfo info = ABILITY_INFO.get(abilityKey);
    if (info == null) {
      return null;
    }

    // Get cooldown and calculate progress
    int cooldown = getCooldownForAbility(player, info.element, info.number);
    float progress = calculateProgress(info, cooldown);
    boolean isReady = cooldown <= 0;

    // Progress bar - single vertical block for 100%
    String progressBar = buildProgressBar(progress);
    String barColor = getProgressBarColorMiniMessage(progress, isReady);

    // Build the line using MiniMessage
    // Format: [Icon] /command [#] [ProgressBar] Status
    // Example: ⚡ /lightning 1  █ Ready
    // Example: 🔥 /fire 1       █ In 1m 1s
    String miniMessageString;

    if (isReady) {
      // Ready state - green with bold and shadow
      miniMessageString = String.format(
        "<%s><shadow:#000000FF>%s</shadow> <shadow:#000000FF>%s</shadow>  <%s><bold><shadow:#000000FF>Ready</shadow></bold></%s>",
        info.colorName,    // Icon color
        info.icon,         // Icon with shadow
        info.command,      // Command with shadow
        barColor,         // Progress bar and Ready color
        barColor          // Ready text color
      );
    } else {
      // Cooldown state - "In" is white, countdown is colored based on progress
      String cooldownText = formatCooldownShort(cooldown);

      miniMessageString = String.format(
        "<%s><shadow:#000000FF>%s</shadow> <shadow:#000000FF>%s</shadow>  %s <white>In</white> <%s>%s</%s>",
        info.colorName,    // Icon color
        info.icon,         // Icon with shadow
        info.command,      // Command with shadow
        progressBar,       // Progress bar (e.g., "█")
        barColor,         // Cooldown countdown color
        cooldownText,     // Countdown (e.g., "1m 10s")
        barColor          // Overall color
      );
    }

    // Parse the MiniMessage string into a Component
    return miniMessage.deserialize(miniMessageString);
  }

  /**
   * Build text-based progress bar using 2 vertical Unicode characters.
   * Uses "▒" for empty and "██" for filled portions.
   * Animation shows growth between 50% threshold.
   * - 0%: "▒▒" (empty)
   * - 50%: "▒▒" ↔ "██" (flashing at threshold)
   * - 100%: "██" (solid full)
   *
   * @param progress Progress from 0.0 (empty) to 1.0 (full/ready)
   * @return Progress bar string (2 characters)
   */
  private String buildProgressBar(float progress) {
    return ProgressBarRenderer.render(progress, System.currentTimeMillis());
  }

  /**
   * Get progress bar color with gradient based on percentage for MiniMessage.
   * When ready: Green (#00FF00)
   * When on cooldown: Gradient from dark red -> red -> gold -> yellow -> green using hex codes
   *
   * @param progress Progress from 0.0 to 1.0
   * @param isReady True if ability is ready
   * @return Hex color code for MiniMessage (e.g., "#00FF00", "#FF0000")
   */
  private String getProgressBarColorMiniMessage(float progress, boolean isReady) {
    if (isReady) {
      return "#00FF00"; // Bright green when ready
    }

    // Smooth gradient based on progress percentage using hex codes
    if (progress >= 0.90f) return "#00FF00"; // Green at 90%+ (almost ready)
    if (progress >= 0.70f) return "#FFFF00"; // Yellow at 70-90%
    if (progress >= 0.50f) return "#FFAA00"; // Gold/Orange at 50-70%
    if (progress >= 0.30f) return "#FF0000"; // Red at 30-50%
    return "#AA0000"; // Dark red at 0-30% (just started cooldown)
  }

  /**
   * Get progress bar color with gradient based on percentage.
   * When ready: Green
   * When on cooldown: Gradient from dark red -> red -> gold -> yellow -> green
   *
   * @param progress Progress from 0.0 to 1.0
   * @param isReady True if ability is ready
   * @return Color for the progress bar
   */
  private NamedTextColor getProgressBarColor(float progress, boolean isReady) {
    if (isReady) {
      return NamedTextColor.GREEN;
    }

    // Gradient based on progress percentage
    if (progress >= 0.90f) return NamedTextColor.GREEN;
    if (progress >= 0.70f) return NamedTextColor.YELLOW;
    if (progress >= 0.50f) return NamedTextColor.GOLD;
    if (progress >= 0.30f) return NamedTextColor.RED;
    return NamedTextColor.DARK_RED;
  }

  /**
   * Extract numbers from cooldown text for underlined formatting.
   * Example: "In 1m 25s" -> "1m 25" (all numbers including units)
   *
   * @param text The cooldown text
   * @return The numeric portion
   */
  private String extractNumbers(String text) {
    StringBuilder numbers = new StringBuilder();
    for (char c : text.toCharArray()) {
      if (Character.isDigit(c) || c == 'm' || c == 's') {
        numbers.append(c);
      }
    }
    return numbers.toString();
  }

  /**
   * Calculate progress (0.0 to 1.0).
   * When ready: 1.0 (full bar)
   * When on cooldown: increases as time passes
   */
  private float calculateProgress(AbilityInfo info, int currentCooldown) {
    if (currentCooldown <= 0) {
      return 1.0f; // Full bar when ready
    }

    // Get maximum cooldown for this ability
    int maxCooldown = cooldownManager.getGlobalCooldown(info.element,
      info.number > 0 ? info.number : 1);

    if (maxCooldown <= 0) {
      maxCooldown = 60; // Fallback to 60 seconds
    }

    // Calculate progress (inverted: starts at 0, goes to 1 as cooldown decreases)
    float progress = 1.0f - ((float) currentCooldown / maxCooldown);

    // Clamp to valid range
    return Math.max(0.0f, Math.min(1.0f, progress));
  }

  /**
   * Format cooldown time in short form for compact display.
   * Examples: "35s", "1m 25s", "2m 30s"
   */
  private String formatCooldownShort(int totalSeconds) {
    if (totalSeconds <= 60) {
      return totalSeconds + "s";
    } else {
      int minutes = totalSeconds / 60;
      int seconds = totalSeconds % 60;
      if (seconds == 0) {
        return minutes + "m";
      }
      return minutes + "m " + seconds + "s";
    }
  }

  /**
   * Get cooldown for a specific ability.
   */
  private int getCooldownForAbility(Player player, String element, int number) {
    if (cooldownManager == null) {
      return 0;
    }

    if (number == 0) {
      // Lightning ability uses number 1 internally
      return cooldownManager.getRemainingCooldown(player, element, 1);
    }

    return cooldownManager.getRemainingCooldown(player, element, number);
  }

  /**
   * Check if player has lightning ability available (dragon egg in offhand).
   */
  private boolean hasLightningAbility(Player player) {
    if (abilityManager == null) {
      plugin.getLogger().info("[HUD DEBUG] hasLightningAbility: abilityManager is null");
      return false;
    }

    Ability lightning = abilityManager.getAbility(1);
    plugin.getLogger().info("[HUD DEBUG] hasLightningAbility: lightning=" + lightning);
    boolean result = lightning != null && lightning.hasRequiredItem(player);
    plugin.getLogger().info("[HUD DEBUG] hasLightningAbility: result=" + result);
    return result;
  }

  /**
   * Get element name from fragment type.
   */
  private String getElementName(FragmentType type) {
    switch (type) {
      case BURNING: return CooldownManager.FIRE;
      case AGILITY: return CooldownManager.AGILE;
      case IMMORTAL: return CooldownManager.IMMORTAL;
      case CORRUPTED: return CooldownManager.CORRUPT;
      default: return "unknown";
    }
  }

  /**
   * Clear sidebar for a player.
   */
  public void clearPlayerSidebar(Player player) {
    UUID playerId = player.getUniqueId();
    Sidebar<Component> sidebar = playerSidebars.remove(playerId);
    playerLines.remove(playerId);

    if (sidebar != null) {
      // Remove viewer to hide the sidebar
      sidebar.removeViewer(player);
    }
  }

  /**
   * Build ability line for updatable sidebar line.
   * This version is called by the updater function to get fresh cooldown data.
   */
  private Component buildAbilityLineForUpdater(Player player, String abilityKey) {
    return buildAbilityLine(player, abilityKey);
  }

  /**
   * Shutdown the HUD manager.
   */
  public void shutdown() {
    // Clear all sidebars
    for (Player player : Bukkit.getOnlinePlayers()) {
      clearPlayerSidebar(player);
    }
    playerSidebars.clear();
    playerLines.clear();
  }

  /**
   * Ability information holder.
   */
  private static class AbilityInfo {
    final String icon;
    final String element;
    final int number; // 0 for lightning, 1-2 for fragments
    final String command;
    final String abilityName;
    final NamedTextColor color;
    final String colorName; // MiniMessage color name (e.g., "light_purple", "red")

    AbilityInfo(String icon, String element, int number, String command,
                String abilityName, NamedTextColor color, String colorName) {
      this.icon = icon;
      this.element = element;
      this.number = number;
      this.command = command;
      this.abilityName = abilityName;
      this.color = color;
      this.colorName = colorName;
    }
  }
}
