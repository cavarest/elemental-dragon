package org.cavarest.elementaldragon.hud;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.ability.Ability;
import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer.ProgressVariant;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
 * - Active: 👁 /corrupt 2  █ ACTIVE (18s)
 * - Cooldown: 🔥 /fire 1    █ In 1m 1s
 *
 * Displays:
 * - Lightning ability (if dragon egg in offhand)
 * - Fragment Ability 1 (if fragment equipped)
 * - Fragment Ability 2 (if fragment equipped)
 *
 * Active abilities with durations show countdown in the format "ACTIVE (Xs)".
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

  // Active ability duration mappings (ability key -> duration in seconds)
  private static final Map<String, ActiveAbilityInfo> ACTIVE_ABILITY_INFO = new HashMap<>();

  static {
    // Dread Gaze: 10 seconds
    ACTIVE_ABILITY_INFO.put("corrupt:1", new ActiveAbilityInfo(
      "corrupted_dread_gaze_active", "corrupted_dread_gaze_start_time", 10
    ));
    // Life Devourer: 20 seconds
    ACTIVE_ABILITY_INFO.put("corrupt:2", new ActiveAbilityInfo(
      "corrupted_life_devourer_active", "corrupted_life_devourer_start_time", 20
    ));
    // Draconic Reflex: 15 seconds
    ACTIVE_ABILITY_INFO.put("immortal:1", new ActiveAbilityInfo(
      "immortal_draconic_reflex_active", "immortal_draconic_reflex_start_time", 15
    ));
    // Essence Rebirth: 30 seconds
    ACTIVE_ABILITY_INFO.put("immortal:2", new ActiveAbilityInfo(
      "immortal_essence_rebirth_activated", "immortal_essence_rebirth_start_time", 30
    ));
  }

  // Ability name mappings
  private static final Map<String, AbilityInfo> ABILITY_INFO = new HashMap<>();

  static {
    // Lightning
    ABILITY_INFO.put(CooldownManager.LIGHTNING + ":0", new AbilityInfo(
      "⚡", CooldownManager.LIGHTNING, 0, "/lightning 1", "Lightning Strike", NamedTextColor.LIGHT_PURPLE, "light_purple", "instant"
    ));

    // Burning Fragment
    ABILITY_INFO.put(CooldownManager.FIRE + ":1", new AbilityInfo(
      "🔥", CooldownManager.FIRE, 1, "/fire 1", "Dragon's Wrath", NamedTextColor.RED, "red", "instant"
    ));
    ABILITY_INFO.put(CooldownManager.FIRE + ":2", new AbilityInfo(
      "🔥", CooldownManager.FIRE, 2, "/fire 2", "Infernal Dominion", NamedTextColor.RED, "red", "10s"
    ));

    // Agility Fragment
    ABILITY_INFO.put(CooldownManager.AGILE + ":1", new AbilityInfo(
      "💨", CooldownManager.AGILE, 1, "/agile 1", "Draconic Surge", NamedTextColor.AQUA, "aqua", "1s"
    ));
    ABILITY_INFO.put(CooldownManager.AGILE + ":2", new AbilityInfo(
      "💨", CooldownManager.AGILE, 2, "/agile 2", "Wing Burst", NamedTextColor.AQUA, "aqua", "10s"
    ));

    // Immortal Fragment
    ABILITY_INFO.put(CooldownManager.IMMORTAL + ":1", new AbilityInfo(
      "🔰", CooldownManager.IMMORTAL, 1, "/immortal 1", "Draconic Reflex", NamedTextColor.GREEN, "green", "15s"
    ));
    ABILITY_INFO.put(CooldownManager.IMMORTAL + ":2", new AbilityInfo(
      "🔰", CooldownManager.IMMORTAL, 2, "/immortal 2", "Essence Rebirth", NamedTextColor.GREEN, "green", "30s"
    ));

    // Corrupted Core - using single-width eye emoji (without variation selector)
    ABILITY_INFO.put(CooldownManager.CORRUPT + ":1", new AbilityInfo(
      "👁", CooldownManager.CORRUPT, 1, "/corrupt 1", "Dread Gaze", NamedTextColor.DARK_PURPLE, "dark_purple", "10s"
    ));
    ABILITY_INFO.put(CooldownManager.CORRUPT + ":2", new AbilityInfo(
      "👁", CooldownManager.CORRUPT, 2, "/corrupt 2", "Life Devourer", NamedTextColor.DARK_PURPLE, "dark_purple", "20s"
    ));
  }

  // Fragment display mappings (icon and color)
  private static final Map<FragmentType, FragmentDisplayInfo> FRAGMENT_DISPLAY_INFO = new HashMap<>();

  static {
    FRAGMENT_DISPLAY_INFO.put(FragmentType.BURNING, new FragmentDisplayInfo(
      "🔥", NamedTextColor.RED, "red"
    ));
    FRAGMENT_DISPLAY_INFO.put(FragmentType.AGILITY, new FragmentDisplayInfo(
      "💨", NamedTextColor.AQUA, "aqua"
    ));
    FRAGMENT_DISPLAY_INFO.put(FragmentType.IMMORTAL, new FragmentDisplayInfo(
      "🔰", NamedTextColor.GREEN, "green"
    ));
    FRAGMENT_DISPLAY_INFO.put(FragmentType.CORRUPTED, new FragmentDisplayInfo(
      "👁", NamedTextColor.DARK_PURPLE, "dark_purple"
    ));
  }

  // Debuff duration mappings (debuff key -> duration in seconds)
  private static final Map<String, DebuffInfo> DEBUFF_INFO = new HashMap<>();

  static {
    // Dread Gaze Freeze: 10 seconds
    DEBUFF_INFO.put("corrupted_dread_gaze_debuff", new DebuffInfo(
      "Dread Gaze Freeze", 10, "👁", NamedTextColor.DARK_PURPLE, "dark_purple"
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
   * Handle player death - clear their HUD.
   * When a player dies, they lose their items (dragon egg, fragments).
   * The HUD should be cleared to reflect the empty inventory.
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    clearPlayerSidebar(event.getEntity());
  }

  /**
   * Handle player respawn - update their HUD.
   * After respawning, check if player has items (dragon egg, fragments) in offhand.
   * This handles keepInventory game rule or if player picks up items after respawn.
   */
  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    // Update HUD after respawn to check if player has items
    scheduleHudUpdate(event.getPlayer());
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
   * Layout:
   * - ACTIVE abilities section (if any) with reverse progress bars
   * - Cooldown/Ready abilities section with normal progress bars
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

    // Separate into active, inactive abilities AND debuffs
    List<String> activeKeys = new ArrayList<>();
    List<String> inactiveKeys = new ArrayList<>();
    List<String> debuffKeys = getActiveDebuffs(player);

    for (String abilityKey : abilityKeys) {
      boolean isActive = isActiveAbility(player, abilityKey);
      if (isActive) {
        activeKeys.add(abilityKey);
      } else {
        inactiveKeys.add(abilityKey);
      }
    }

    // Update or create sidebar
    if (!abilityKeys.isEmpty() || !debuffKeys.isEmpty()) {
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

        // Add fragment display at the top (if a fragment is equipped)
        if (equippedFragment != null) {
          // Fragment name line
          FragmentDisplayInfo info = FRAGMENT_DISPLAY_INFO.get(equippedFragment);
          if (info != null) {
            String fragmentNameString = String.format(
              "<%s><shadow:#000000FF>%s</shadow></%s> <gold><bold>%s</bold></gold>",
              info.colorName,
              info.icon,
              info.colorName,
              equippedFragment.getDisplayName().toUpperCase()
            );
            Component fragmentNameComponent = miniMessage.deserialize(fragmentNameString);
            SidebarLine<Component> fragmentNameLine = sidebar.addLine(fragmentNameComponent);
            currentLines.add(fragmentNameLine);

            // Get active potion effects for this fragment
            List<String> activeBuffs = getActivePotionEffects(player, equippedFragment);

            // Passive description line (or active buffs)
            String passiveDescription = getWidthMatchedPassiveDescription(equippedFragment);
            String passiveLineString;
            if (activeBuffs.isEmpty()) {
              // No active buffs - show passive description
              passiveLineString = String.format("<gray>%s</gray>", passiveDescription);
            } else {
              // Show active buffs instead of passive description
              passiveLineString = String.format("<green>✦ %s</green>", String.join(" • ", activeBuffs));
            }
            Component passiveLineComponent = miniMessage.deserialize(passiveLineString);
            SidebarLine<Component> passiveLine = sidebar.addLine(passiveLineComponent);
            currentLines.add(passiveLine);
          }

          // Check for Dread Gaze "Foe Frozen" state (attacker has frozen someone)
          if (player.hasMetadata("corrupted_dread_gaze_foe_frozen")) {
            int remainingSeconds = getFoeFrozenRemainingDuration(player);
            if (remainingSeconds > 0) {
              String foeFrozenString = String.format(
                "<dark_purple><shadow:#000000FF>👁 Foe Frozen</shadow> (%ds)</dark_purple>",
                remainingSeconds
              );
              Component foeFrozenComponent = miniMessage.deserialize(foeFrozenString);
              SidebarLine<Component> foeFrozenLine = sidebar.addUpdatableLine(p -> {
                int updatedRemaining = getFoeFrozenRemainingDuration(p);
                if (updatedRemaining > 0) {
                  String updatedString = String.format(
                    "<dark_purple><shadow:#000000FF>👁 Foe Frozen</shadow> (%ds)</dark_purple>",
                    updatedRemaining
                  );
                  return miniMessage.deserialize(updatedString);
                }
                return Component.empty();
              });
              currentLines.add(foeFrozenLine);
            }
          }

          // No spacer after fragment display - AVAILABLE section will add one
        }

        // Add DEBUFFS section if player has active debuffs
        if (!debuffKeys.isEmpty()) {
          SidebarLine<Component> debuffHeader = sidebar.addLine(
            Component.text("═══ ⚠ DEBUFFS ⚠ ═══", NamedTextColor.RED)
          );
          currentLines.add(debuffHeader);

          SidebarLine<Component> debuffSpacer = sidebar.addLine(Component.empty());
          currentLines.add(debuffSpacer);

          // Add updatable lines for each debuff
          for (String debuffKey : debuffKeys) {
            SidebarLine<Component> line = sidebar.addUpdatableLine(p -> {
              Component debuffText = buildDebuffLine(p, debuffKey);
              return debuffText;
            });
            currentLines.add(line);
          }

          SidebarLine<Component> debuffDivider = sidebar.addLine(
            Component.text("─────────────────────────────", NamedTextColor.GRAY)
          );
          currentLines.add(debuffDivider);
        }

        // Add ACTIVE section header if there are active abilities
        if (!activeKeys.isEmpty()) {
          SidebarLine<Component> spacer1 = sidebar.addLine(Component.empty());
          currentLines.add(spacer1);

          SidebarLine<Component> activeHeader = sidebar.addLine(
            Component.text("═══ ✨ ACTIVE ABILITIES ✨ ═══", NamedTextColor.GOLD)
          );
          currentLines.add(activeHeader);

          SidebarLine<Component> spacer2 = sidebar.addLine(Component.empty());
          currentLines.add(spacer2);
        }

        // Add updatable lines for each ACTIVE ability
        for (String abilityKey : activeKeys) {
          SidebarLine<Component> line = sidebar.addUpdatableLine(p -> {
            Component abilityText = buildAbilityLineForUpdater(p, abilityKey);
            return abilityText;
          });
          currentLines.add(line);
        }

        // Add AVAILABLE section header if there are inactive abilities
        if (!inactiveKeys.isEmpty()) {
          // Add spacing before available section if there were active abilities
          if (!activeKeys.isEmpty()) {
            SidebarLine<Component> dividerLine = sidebar.addLine(
              Component.text("─────────────────────────────", NamedTextColor.GRAY)
            );
            currentLines.add(dividerLine);
          }

          SidebarLine<Component> spacer3 = sidebar.addLine(Component.empty());
          currentLines.add(spacer3);

          SidebarLine<Component> availableHeader = sidebar.addLine(
            Component.text("═══ ⚔ AVAILABLE ABILITIES ⚔ ═══", NamedTextColor.AQUA)
          );
          currentLines.add(availableHeader);

          SidebarLine<Component> spacer4 = sidebar.addLine(Component.empty());
          currentLines.add(spacer4);
        }

        // Add updatable lines for each AVAILABLE ability (cooldown or ready)
        for (String abilityKey : inactiveKeys) {
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
   * Get width-matched passive description for fragment type.
   * All descriptions are approximately 15-20 characters for better alignment.
   *
   * @param fragmentType The fragment type
   * @return Width-matched passive description
   */
  private String getWidthMatchedPassiveDescription(FragmentType fragmentType) {
    switch (fragmentType) {
      case BURNING:
        return "100% Fire Immunity"; // 20 chars
      case AGILITY:
        return "30% Speed Boost"; // 17 chars
      case IMMORTAL:
        return "Resists Death"; // 15 chars
      case CORRUPTED:
        return "See in Darkness"; // 18 chars
      default:
        return fragmentType.getPassiveBonus();
    }
  }

  /**
   * Build a single ability line with MiniMessage styling.
   * Format: [Icon] /command [#]  [ProgressBar] Status
   *
   * Active: 👁 /corrupt 2  █ ACTIVE (18s)
   * Ready:  ⚡ /lightning 1  █ Ready
   * Cooldown: 🔥 /fire 1    █ In 1m 1s
   *
   * Uses MiniMessage for styling:
   * - Command has shadow: <shadow:#000000FF>/command</shadow>
   * - Ready text has bold and shadow: <bold><shadow>Ready</shadow></bold>
   * - Active text shows remaining duration with glow effect
   * - Cooldown numbers are underlined: In <underlined>30</underlined>s
   * - Colors transition based on progress
   */
  private Component buildAbilityLine(Player player, String abilityKey) {
    AbilityInfo info = ABILITY_INFO.get(abilityKey);
    if (info == null) {
      return null;
    }

    // Get dynamic duration display from CooldownManager
    // This reflects the current global cooldown setting
    String durationText = getDynamicDurationDisplay(info.element, info.number);

    // Format ability name: hide "(instant)" for instant abilities
    String abilityNameDisplay;
    if ("instant".equals(durationText)) {
      abilityNameDisplay = info.abilityName; // No duration shown
    } else {
      abilityNameDisplay = String.format("%s(%s)", info.abilityName, durationText);
    }

    // Check if ability is currently active
    boolean isActive = isActiveAbility(player, abilityKey);

    // Special case for Dread Gaze: check if it's awaiting a hit
    boolean isAwaitingHit = false;
    if ("corrupt:1".equals(abilityKey)) {
      isAwaitingHit = player.hasMetadata("corrupted_dread_gaze_active") &&
                       !player.hasMetadata("corrupted_dread_gaze_active_start_time");
    }

    String miniMessageString;

    if (isAwaitingHit) {
      // READY TO STRIKE state - show awaiting hit message
      String barColor = "#FF00FF"; // Magenta for special state
      miniMessageString = String.format(
        "<%s><shadow:#000000FF>%s</shadow> <gray>%s</gray>  <%s><bold><dark_purple>READY TO STRIKE</dark_purple></bold></%s>",
        info.colorName,       // Icon color
        info.icon,            // Icon with shadow
        abilityNameDisplay,   // Ability name (with or without duration)
        barColor,            // State color
        barColor             // Overall color
      );
    } else if (isActive) {
      // ACTIVE state - show countdown with reverse progress bar
      int remainingSeconds = getActiveAbilityRemainingDuration(player, abilityKey);
      ActiveAbilityInfo activeInfo = ACTIVE_ABILITY_INFO.get(abilityKey);

      // Reverse progress: starts at 1.0, decreases to 0
      float reverseProgress = activeInfo != null && activeInfo.durationSeconds > 0
        ? (float) remainingSeconds / activeInfo.durationSeconds
        : 0.5f;

      // Clamp to valid range
      reverseProgress = Math.max(0.0f, Math.min(1.0f, reverseProgress));

      String progressBar = buildProgressBar(player, reverseProgress);
      String barColor = "#FF00FF"; // Magenta/pink for active abilities

      // Format: [Icon] Ability Name (Duration)  [ProgressBar] ACTIVE (Xs)
      miniMessageString = String.format(
        "<%s><shadow:#000000FF>%s</shadow> %s  <%s><bold>ACTIVE</bold> (%ds)</%s>",
        info.colorName,       // Icon color
        info.icon,            // Icon with shadow
        abilityNameDisplay,   // Ability name (with or without duration)
        barColor,            // Progress bar and Active color
        remainingSeconds,    // Remaining seconds
        barColor             // Overall color
      );
    } else {
      // Not active - check cooldown
      int cooldown = getCooldownForAbility(player, info.element, info.number);
      float progress = calculateProgress(info, cooldown);
      boolean isReady = cooldown <= 0;

      // Progress bar - single vertical block for 100%
      String progressBar = buildProgressBar(player, progress);
      String barColor = getProgressBarColorMiniMessage(progress, isReady);

      if (isReady) {
        // Ready state - green with bold and shadow
        miniMessageString = String.format(
          "<%s><shadow:#000000FF>%s</shadow> %s  <%s><bold><shadow:#000000FF>Ready</shadow></bold></%s>",
          info.colorName,    // Icon color
          info.icon,         // Icon with shadow
          abilityNameDisplay, // Ability name (with or without duration)
          barColor,         // Progress bar and Ready color
          barColor          // Ready text color
        );
      } else {
        // Cooldown state - "In" is white, countdown is colored based on progress
        String cooldownText = formatCooldownShort(cooldown);

        miniMessageString = String.format(
          "<%s><shadow:#000000FF>%s</shadow> %s %s <white>In</white> <%s>%s</%s>",
          info.colorName,    // Icon color
          info.icon,         // Icon with shadow
          abilityNameDisplay, // Ability name (with or without duration)
          progressBar,       // Progress bar (e.g., "█")
          barColor,         // Cooldown countdown color
          cooldownText,     // Countdown (e.g., "1m 10s")
          barColor          // Overall color
        );
      }
    }

    // Parse the MiniMessage string into a Component
    return miniMessage.deserialize(miniMessageString);
  }

  /**
   * Get the dynamic duration display for an ability.
   * Queries CooldownManager for the current global cooldown setting.
   *
   * @param element The element name
   * @param abilityNum The ability number
   * @return Formatted duration string (e.g., "60s", "instant")
   */
  private String getDynamicDurationDisplay(String element, int abilityNum) {
    if (cooldownManager == null) {
      return "unknown";
    }

    // Use CooldownManager to get the current effective cooldown display
    return cooldownManager.getCooldownDisplay(element, abilityNum);
  }

  /**
   * Build text-based progress bar using 2 vertical Unicode characters.
   * Uses "▒" for empty and "██" for filled portions.
   * Animation shows growth between 50% threshold.
   * - 0%: "▒▒" (empty)
   * - 50%: "▒▒" ↔ "██" (flashing at threshold)
   * - 100%: "██" (solid full)
   *
   * Uses player's preferred countdown style from PlayerPreferenceManager.
   *
   * @param player The player to get the preference for
   * @param progress Progress from 0.0 (empty) to 1.0 (full/ready)
   * @return Progress bar string (2 characters)
   */
  private String buildProgressBar(Player player, float progress) {
    // Get player's preferred countdown variant
    PlayerPreferenceManager preferenceManager = plugin.getPlayerPreferenceManager();
    ProgressVariant variant = preferenceManager.getVariant(player);

    return ProgressBarRenderer.render(progress, System.currentTimeMillis(), variant);
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

    // Check if player has dragon egg in offhand
    org.bukkit.inventory.ItemStack offhand = player.getInventory().getItemInOffHand();
    return offhand != null && offhand.getType() == org.bukkit.Material.DRAGON_EGG;
  }

  /**
   * Check if an ability is currently active for a player.
   * Checks metadata to determine if the ability is active.
   *
   * @param player The player
   * @param abilityKey The ability key (e.g., "corrupt:2")
   * @return true if the ability is active
   */
  private boolean isActiveAbility(Player player, String abilityKey) {
    ActiveAbilityInfo info = ACTIVE_ABILITY_INFO.get(abilityKey);
    if (info == null) {
      return false;
    }

    // Check if player has the active metadata set
    return player.hasMetadata(info.metadataKey);
  }

  /**
   * Get the remaining duration for an active ability.
   * Uses activation timestamp metadata if available for accurate countdown.
   *
   * @param player The player
   * @param abilityKey The ability key (e.g., "corrupt:2")
   * @return Remaining duration in seconds, or 0 if not active
   */
  private int getActiveAbilityRemainingDuration(Player player, String abilityKey) {
    ActiveAbilityInfo info = ACTIVE_ABILITY_INFO.get(abilityKey);
    if (info == null) {
      return 0;
    }

    // Check if player has the active metadata set
    if (!player.hasMetadata(info.metadataKey)) {
      return 0;
    }

    // Try to get activation timestamp from metadata
    if (info.startTimeMetadataKey != null && player.hasMetadata(info.startTimeMetadataKey)) {
      try {
        long startTime = player.getMetadata(info.startTimeMetadataKey).get(0).asLong();
        long elapsedMillis = System.currentTimeMillis() - startTime;
        int elapsedSeconds = (int) (elapsedMillis / 1000);
        int remaining = info.durationSeconds - elapsedSeconds;
        return Math.max(0, remaining);
      } catch (Exception e) {
        // If timestamp is invalid, return full duration
        plugin.getLogger().warning("[HUD] Invalid timestamp for " + abilityKey + ": " + e.getMessage());
        return info.durationSeconds;
      }
    }

    // No timestamp available - return full duration as fallback
    plugin.getLogger().warning("[HUD] No start time metadata found for " + abilityKey + ", using full duration");
    return info.durationSeconds;
  }

  /**
   * Get the remaining duration for the Dread Gaze "Foe Frozen" countdown.
   * This shows the attacker how much longer their target remains frozen.
   * Reads duration from metadata (single source of truth) set by CorruptedCoreFragment.
   *
   * @param player The attacker player
   * @return Remaining duration in seconds, or 0 if no foe is frozen
   */
  private int getFoeFrozenRemainingDuration(Player player) {
    String foeFrozenKey = "corrupted_dread_gaze_foe_frozen";
    String startTimeKey = "corrupted_dread_gaze_foe_frozen_start_time";
    String durationKey = "corrupted_dread_gaze_foe_frozen_duration";

    if (!player.hasMetadata(foeFrozenKey)) {
      return 0;
    }

    if (!player.hasMetadata(startTimeKey)) {
      return 0;
    }

    if (!player.hasMetadata(durationKey)) {
      plugin.getLogger().warning("[HUD] Foe frozen duration metadata not found, using default 10s");
      // Fallback to 10 seconds for backwards compatibility with old data
      try {
        long startTime = player.getMetadata(startTimeKey).get(0).asLong();
        long elapsedMillis = System.currentTimeMillis() - startTime;
        int elapsedSeconds = (int) (elapsedMillis / 1000);
        int remaining = 10 - elapsedSeconds;
        return Math.max(0, remaining);
      } catch (Exception e) {
        plugin.getLogger().warning("[HUD] Invalid foe frozen timestamp: " + e.getMessage());
        return 0;
      }
    }

    try {
      long startTime = player.getMetadata(startTimeKey).get(0).asLong();
      long elapsedMillis = System.currentTimeMillis() - startTime;
      int elapsedSeconds = (int) (elapsedMillis / 1000);

      // Read duration from metadata (single source of truth from CorruptedCoreFragment)
      int durationSeconds = player.getMetadata(durationKey).get(0).asInt();
      int remaining = durationSeconds - elapsedSeconds;
      return Math.max(0, remaining);
    } catch (Exception e) {
      plugin.getLogger().warning("[HUD] Invalid foe frozen metadata: " + e.getMessage());
      return 0;
    }
  }

  /**
   * Get list of active potion effects for a fragment.
   * Returns the names of active potion effects that the fragment provides.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @return List of active potion effect names
   */
  private List<String> getActivePotionEffects(Player player, FragmentType fragmentType) {
    List<String> buffs = new ArrayList<>();

    // Define potion effects for each fragment type
    switch (fragmentType) {
      case BURNING:
        // Fire Resistance (from Burning Fragment passive)
        if (player.hasPotionEffect(org.bukkit.potion.PotionEffectType.FIRE_RESISTANCE)) {
          buffs.add("Fire Immunity");
        }
        break;
      case AGILITY:
        // Speed (from Agility Fragment passive)
        if (player.hasPotionEffect(org.bukkit.potion.PotionEffectType.SPEED)) {
          buffs.add("Speed");
        }
        break;
      case IMMORTAL:
        // Resistance (from Immortal Fragment passive)
        if (player.hasPotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE)) {
          buffs.add("Resistance");
        }
        break;
      case CORRUPTED:
        // Night Vision (from Corrupted Core passive)
        if (player.hasPotionEffect(org.bukkit.potion.PotionEffectType.NIGHT_VISION)) {
          buffs.add("Night Vision");
        }
        break;
    }

    return buffs;
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
   * Get list of active debuffs for a player.
   * Checks metadata for debuff markers.
   *
   * @param player The player
   * @return List of active debuff keys
   */
  private List<String> getActiveDebuffs(Player player) {
    List<String> debuffs = new ArrayList<>();

    for (String debuffKey : DEBUFF_INFO.keySet()) {
      if (player.hasMetadata(debuffKey)) {
        debuffs.add(debuffKey);
      }
    }

    return debuffs;
  }

  /**
   * Build a debuff line showing remaining duration.
   * Format: [Icon] Debuff Name  [ProgressBar] (Xs)
   *
   * @param player The player
   * @param debuffKey The debuff metadata key
   * @return Component for debuff display
   */
  private Component buildDebuffLine(Player player, String debuffKey) {
    DebuffInfo info = DEBUFF_INFO.get(debuffKey);
    if (info == null) {
      return Component.empty();
    }

    // Get remaining duration
    int remainingSeconds = getDebuffRemainingDuration(player, debuffKey);
    if (remainingSeconds <= 0) {
      return Component.empty(); // Shouldn't happen if metadata is correct
    }

    // Reverse progress: starts at 1.0, decreases to 0
    float reverseProgress = (float) remainingSeconds / info.durationSeconds;
    reverseProgress = Math.max(0.0f, Math.min(1.0f, reverseProgress));

    String progressBar = buildProgressBar(player, reverseProgress);
    String barColor = "#FF00FF"; // Magenta for debuffs

    // Format: [Icon] Debuff Name  [ProgressBar] (Xs)
    String miniMessageString = String.format(
      "<%s><shadow:#000000FF>%s</shadow> <%s>%s</%s>  <%s>(%ds)</%s>",
      info.colorName,  // Icon color
      info.icon,          // Icon
      info.colorName,  // Name color
      info.name,          // Debuff name
      barColor,          // Progress bar color
      barColor,          // Countdown color
      remainingSeconds,  // Remaining seconds
      barColor           // Overall color
    );

    return miniMessage.deserialize(miniMessageString);
  }

  /**
   * Get remaining duration for a debuff.
   *
   * @param player The player
   * @param debuffKey The debuff metadata key
   * @return Remaining duration in seconds
   */
  private int getDebuffRemainingDuration(Player player, String debuffKey) {
    String startTimeKey = debuffKey + "_start_time";
    if (!player.hasMetadata(startTimeKey)) {
      return 0;
    }

    try {
      long startTime = player.getMetadata(startTimeKey).get(0).asLong();
      long elapsedMillis = System.currentTimeMillis() - startTime;
      int elapsedSeconds = (int) (elapsedMillis / 1000);

      DebuffInfo info = DEBUFF_INFO.get(debuffKey);
      int remaining = info.durationSeconds - elapsedSeconds;
      return Math.max(0, remaining);
    } catch (Exception e) {
      return 0;
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
   * Update HUDs for all online players.
   * Called when global settings change (cooldowns, symbols, etc.)
   */
  public void updateAllPlayerHuds() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      updatePlayerHud(player);
    }
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
    final String durationDisplay; // Duration text to display (e.g., "15s", "instant")

    AbilityInfo(String icon, String element, int number, String command,
                String abilityName, NamedTextColor color, String colorName, String durationDisplay) {
      this.icon = icon;
      this.element = element;
      this.number = number;
      this.command = command;
      this.abilityName = abilityName;
      this.color = color;
      this.colorName = colorName;
      this.durationDisplay = durationDisplay;
    }
  }

  /**
   * Active ability information holder.
   * Contains metadata key and duration for abilities with active states.
   */
  private static class ActiveAbilityInfo {
    final String metadataKey;           // Metadata key to check if ability is active
    final String startTimeMetadataKey;   // Metadata key for start time (for countdown)
    final int durationSeconds;           // Duration in seconds

    ActiveAbilityInfo(String metadataKey, String startTimeMetadataKey, int durationSeconds) {
      this.metadataKey = metadataKey;
      this.startTimeMetadataKey = startTimeMetadataKey;
      this.durationSeconds = durationSeconds;
    }
  }

  /**
   * Fragment display information holder.
   * Contains icon and color for displaying equipped fragment on HUD.
   */
  private static class FragmentDisplayInfo {
    final String icon;              // Icon emoji for the fragment
    final NamedTextColor color;     // Named text color
    final String colorName;         // MiniMessage color name

    FragmentDisplayInfo(String icon, NamedTextColor color, String colorName) {
      this.icon = icon;
      this.color = color;
      this.colorName = colorName;
    }
  }

  /**
   * Debuff information holder.
   * Contains name, duration, icon, and color for displaying debuffs on HUD.
   */
  private static class DebuffInfo {
    final String name;             // Debuff name (e.g., "Dread Gaze Freeze")
    final int durationSeconds;     // Duration in seconds
    final String icon;             // Icon emoji for the debuff
    final NamedTextColor color;    // Named text color
    final String colorName;        // MiniMessage color name

    DebuffInfo(String name, int durationSeconds, String icon, NamedTextColor color, String colorName) {
      this.name = name;
      this.durationSeconds = durationSeconds;
      this.icon = icon;
      this.color = color;
      this.colorName = colorName;
    }
  }
}
