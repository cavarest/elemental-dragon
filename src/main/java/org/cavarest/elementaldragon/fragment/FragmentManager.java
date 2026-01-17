package org.cavarest.elementaldragon.fragment;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.cavarest.elementaldragon.item.ElementalItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages fragment equipping, unequipping for players.
 * Uses CooldownManager for unified cooldown tracking.
 * Delegates to FragmentRegistry for fragment metadata and instances.
 */
public class FragmentManager implements Listener {

  private final ElementalDragon plugin;
  private final CooldownManager cooldownManager;
  private final FragmentRegistry fragmentRegistry;
  private final Map<UUID, FragmentType> equippedFragments;
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  /**
   * Create a new FragmentManager.
   *
   * @param plugin The plugin instance
   * @param cooldownManager The cooldown manager
   */
  public FragmentManager(ElementalDragon plugin, CooldownManager cooldownManager) {
    this.plugin = plugin;
    this.cooldownManager = cooldownManager;
    this.equippedFragments = new HashMap<>();

    // Initialize FragmentRegistry - handles all fragment registration
    this.fragmentRegistry = new FragmentRegistry(plugin);

    registerFragmentListeners();
  }

  /**
   * Register event listeners for fragment management.
   */
  private void registerFragmentListeners() {
    if (plugin != null && plugin.getServer() != null) {
      plugin.getServer().getPluginManager().registerEvents(this, plugin);

      // Schedule periodic inventory verification to catch /clear, drops, etc.
      // Runs every tick (20 times per second) to immediately detect removed fragments
      plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
        verifyAllEquippedFragments();
      }, 20L, 1L);  // Start after 1 second, then every tick
    }
  }

  /**
   * Verify all equipped fragments are still in player inventories.
   * This catches cases where fragments were removed via /clear, death, etc.
   * Called periodically by scheduled task.
   */
  private void verifyAllEquippedFragments() {
    if (equippedFragments.isEmpty()) {
      return;  // No equipped fragments to verify
    }

    // Create a copy of the keys to avoid ConcurrentModificationException
    java.util.Set<UUID> playerIds = new java.util.HashSet<>(equippedFragments.keySet());

    for (UUID playerId : playerIds) {
      Player player = org.bukkit.Bukkit.getPlayer(playerId);
      if (player != null && player.isOnline()) {
        // Call getEquippedFragment which verifies inventory and unequips if missing
        getEquippedFragment(player);
      }
    }
  }

  /**
   * Equip a fragment for a player.
   * If the player already has a fragment equipped, it will be unequipped first.
   *
   * @param player The player
   * @param fragmentType The fragment type to equip
   * @return true if the fragment was successfully equipped
   */
  public boolean equipFragment(Player player, FragmentType fragmentType) {
    boolean result = equipFragmentInternal(player, fragmentType);

    // Update HUD after equipping
    if (result && plugin != null && plugin.getHudManager() != null) {
      plugin.getHudManager().updatePlayerHud(player);
    }

    return result;
  }

  /**
   * Internal equip implementation.
   */
  private boolean equipFragmentInternal(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return false;
    }

    UUID playerUuid = player.getUniqueId();

    // Check if player has the fragment item in inventory (unless has admin permission)
    boolean isAdmin = player.hasPermission("elementaldragon.fragment.admin");
    if (!isAdmin) {
      boolean hasFragment = hasFragmentItem(player, fragmentType);
      if (!hasFragment) {
        String craftName = fragmentType.getCanonicalName(); // Use canonical name (fire, agile, immortal, corrupt)
        player.sendMessage(miniMessage.deserialize(
          "<red>⚠ You don't have the <white>" + fragmentType.getDisplayName() + "</white>!</red>\n" +
          "<gray>Craft it first using <white>/craft " + craftName + "</white> or ask an admin for help.</gray>"
        ));
        return false;
      }
    } else {
      // Admin bypassing fragment requirement
      boolean hasFragment = hasFragmentItem(player, fragmentType);
      if (!hasFragment) {
        player.sendMessage(miniMessage.deserialize(
          "<light_purple>✨ The special ones are automatically ascended to elemental dragon powers.</light_purple>"
        ));
      }
    }

    // If player already has a fragment equipped, unequip it first
    FragmentType existingFragment = equippedFragments.get(playerUuid);
    if (existingFragment != null) {
      // If same fragment type, just refresh and return success
      if (existingFragment == fragmentType) {
        player.sendMessage(miniMessage.deserialize("<yellow>Already equipped!</yellow>"));
        return true;
      }

      // Unequip with error handling - don't show errors to user for auto-unequip
      try {
        // Get the fragment instance from registry
        Fragment oldFragment = fragmentRegistry.getFragment(existingFragment);
        if (oldFragment != null) {
          oldFragment.deactivate(player);
        }
        // Remove from map
        equippedFragments.remove(playerUuid);
      } catch (Exception e) {
        plugin.getLogger().warning(
          "Error auto-unequipping fragment " + existingFragment +
          " for player " + player.getName() + ": " + e.getMessage()
        );
        // Force remove from map even if deactivate fails
        equippedFragments.remove(playerUuid);
        // Continue with equipping new fragment - don't fail the operation
      }
    }

    // Equip the new fragment
    equippedFragments.put(playerUuid, fragmentType);

    // Activate the fragment effects
    Fragment fragment = fragmentRegistry.getFragment(fragmentType);
    if (fragment != null) {
      try {
        fragment.activate(player);
      } catch (Exception e) {
        plugin.getLogger().warning(
          "Error activating fragment " + fragmentType +
          " for player " + player.getName() + ": " + e.getMessage()
        );
        // Even if activate fails, keep it equipped - passive effects may still work
      }
    } else {
      // Fallback for framework
      applyBasicActivation(player, fragmentType);
    }

    return true;
  }

  /**
   * Apply basic activation when no fragment implementation exists.
   *
   * @param player The player
   * @param fragmentType The fragment type
   */
  private void applyBasicActivation(Player player, FragmentType fragmentType) {
    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        "Equipped " + fragmentType.getDisplayName() + "!",
        net.kyori.adventure.text.format.NamedTextColor.GOLD
      )
    );
    player.sendMessage(
      net.kyori.adventure.text.Component.text(
        fragmentType.getPassiveBonus(),
        net.kyori.adventure.text.format.NamedTextColor.GRAY
      )
    );
  }

  /**
   * Unequip the current fragment from a player.
   *
   * @param player The player
   * @return true if a fragment was unequipped
   */
  public boolean unequipFragment(Player player) {
    return unequipFragment(player, false);
  }

  /**
   * Unequip the current fragment from a player.
   *
   * @param player The player
   * @param silent If true, suppress the unequip message (for drop handler)
   * @return true if a fragment was unequipped
   */
  public boolean unequipFragment(Player player, boolean silent) {
    if (player == null) {
      return false;
    }

    UUID playerUuid = player.getUniqueId();
    FragmentType equippedType = equippedFragments.remove(playerUuid);

    if (equippedType == null) {
      return false;
    }

    // Deactivate the fragment effects
    Fragment fragment = fragmentRegistry.getFragment(equippedType);
    if (fragment != null) {
      fragment.deactivate(player);
    }

    // Note: Cooldown persists even after unequipping (managed by CooldownManager)

    if (!silent) {
      player.sendMessage(miniMessage.deserialize(
        "<yellow>Unequipped <white>" + equippedType.getDisplayName() + "</white>.</yellow>"
      ));
    }

    // Update HUD after unequipping
    if (plugin != null && plugin.getHudManager() != null) {
      plugin.getHudManager().updatePlayerHud(player);
    }

    return true;
  }

  /**
   * Map FragmentType to canonical element name for CooldownManager.
   * Delegates to FragmentRegistry - Single Source of Truth.
   *
   * @param fragmentType The fragment type
   * @return The canonical element name
   */
  private String getElementName(FragmentType fragmentType) {
    return fragmentRegistry.getCanonicalName(fragmentType);
  }

  /**
   * Get the currently equipped fragment for a player.
   * Verifies the fragment item is still in the player's inventory (Issue 6 fix).
   * If the fragment is no longer in inventory, clears the equipped state.
   *
   * @param player The player
   * @return The equipped fragment type, or null if none equipped
   */
  public FragmentType getEquippedFragment(Player player) {
    if (player == null) {
      return null;
    }

    UUID playerId = player.getUniqueId();
    FragmentType cachedFragment = equippedFragments.get(playerId);

    // If no cached fragment, return null
    if (cachedFragment == null) {
      return null;
    }

    // Verify the fragment item is still in the player's inventory (Issue 6 fix)
    if (!hasFragmentItem(player, cachedFragment)) {
      // Fragment no longer in inventory - unequip to deactivate passive effects
      // This handles /clear command, dropping fragments, death, etc.
      // Show message so player knows abilities were removed
      unequipFragment(player, false);  // NOT silent - show unequip message
      return null;
    }

    // Item verified in inventory - return cached fragment type
    return cachedFragment;
  }

  /**
   * Check if a player has a fragment equipped.
   *
   * @param player The player
   * @return true if a fragment is equipped
   */
  public boolean hasFragmentEquipped(Player player) {
    if (player == null) {
      return false;
    }
    return equippedFragments.containsKey(player.getUniqueId());
  }

  /**
   * Use a fragment ability.
   * Checks cooldown and executes the ability if available.
   *
   * @param player The player
   * @param abilityNumber The ability number to use
   * @return true if the ability was successfully used
   */
  public boolean useFragmentAbility(Player player, int abilityNumber) {
    if (player == null) {
      return false;
    }

    FragmentType equipped = getEquippedFragment(player);
    if (equipped == null) {
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          "Equip a fragment first using /elementaldragon equipment <type>!",
          net.kyori.adventure.text.format.NamedTextColor.RED
        )
      );
      return false;
    }

    // Get element name for cooldown checking
    String elementName = getElementName(equipped);

    // Check cooldown for THIS SPECIFIC ABILITY (not all abilities)
    if (elementName != null && cooldownManager.isOnCooldown(player, elementName, abilityNumber)) {
      int remaining = cooldownManager.getRemainingCooldown(player, elementName, abilityNumber);
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          "Ability on cooldown! " + remaining + " seconds remaining.",
          net.kyori.adventure.text.format.NamedTextColor.RED
        )
      );
      return false;
    }

    // Get the fragment instance
    Fragment fragment = fragmentRegistry.getFragment(equipped);
    if (fragment == null) {
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          equipped.getDisplayName() + " abilities not yet implemented!",
          net.kyori.adventure.text.format.NamedTextColor.RED
        )
      );
      return false;
    }

    // Execute the ability with the ability number
    try {
      // Call the fragment's activate method with ability number
      java.lang.reflect.Method activateMethod = Fragment.class.getMethod(
        "activate", org.bukkit.entity.Player.class, int.class);
      activateMethod.invoke(fragment, player, abilityNumber);
    } catch (NoSuchMethodException e) {
      // Fragment doesn't support ability numbers, use basic activate
      fragment.activate(player);
    } catch (java.lang.reflect.InvocationTargetException e) {
      // Unwrap the actual exception
      Throwable cause = e.getCause();
      if (cause != null) {
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "Ability failed: " + cause.getClass().getSimpleName() + " - " + cause.getMessage(),
            net.kyori.adventure.text.format.NamedTextColor.RED
          )
        );
        // Log the full stack trace
        cause.printStackTrace();
      } else {
        player.sendMessage(
          net.kyori.adventure.text.Component.text(
            "Failed to use ability: " + e.getMessage(),
            net.kyori.adventure.text.format.NamedTextColor.RED
          )
        );
      }
      return false;
    } catch (Exception e) {
      player.sendMessage(
        net.kyori.adventure.text.Component.text(
          "Failed to use ability: " + e.getClass().getSimpleName() + " - " + e.getMessage(),
          net.kyori.adventure.text.format.NamedTextColor.RED
        )
      );
      e.printStackTrace();
      return false;
    }

    // Set cooldown - RESPECT GLOBAL COOLDOWN CONFIGURATION
    if (elementName != null) {
      // Check if there's a global cooldown configured for this ability
      int globalCooldown = cooldownManager.getGlobalCooldown(elementName, abilityNumber);

      int cooldown;
      if (globalCooldown == -1) {
        // Not set in global config - use fragment default
        cooldown = (int)(fragment.getCooldownMillis() / 1000);
      } else if (globalCooldown == 0) {
        // Explicitly disabled - no cooldown
        cooldown = 0;
      } else {
        // Use configured value
        cooldown = globalCooldown;
      }

      // Only set cooldown if > 0
      // If cooldown is 0, don't set - ability immediately ready again
      if (cooldown > 0) {
        cooldownManager.setCooldown(player, elementName, abilityNumber, cooldown);
      }
    }

    // Register ability usage for tracking (Achievements & Chronicle)
    if (plugin.getAchievementManager() != null) {
      plugin.getAchievementManager().registerAbilityUse(player, equipped, abilityNumber);
    }
    if (plugin.getChronicleManager() != null) {
      plugin.getChronicleManager().registerAbilityUse(player, equipped, abilityNumber);
    }

    return true;
  }

  /**
   * Check if player has the fragment item in inventory.
   * Uses ElementalItems.getFragmentType() - Single Source of Truth.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @return true if player has the fragment item
   */
  private boolean hasFragmentItem(Player player, FragmentType fragmentType) {
    if (player.getInventory() == null) {
      return false;
    }

    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && ElementalItems.getFragmentType(item) == fragmentType) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if a player is on fragment cooldown for their equipped fragment.
   *
   * @param player The player
   * @return true if on cooldown
   */
  public boolean isOnCooldown(Player player) {
    if (player == null) {
      return false;
    }
    return getRemainingCooldown(player) > 0;
  }

  /**
   * Get the remaining cooldown in seconds for a player's equipped fragment.
   *
   * @param player The player
   * @return Remaining cooldown in seconds, or 0 if none
   */
  public int getRemainingCooldown(Player player) {
    if (player == null) {
      return 0;
    }

    FragmentType equipped = getEquippedFragment(player);
    if (equipped == null) {
      return 0;
    }

    String elementName = getElementName(equipped);
    if (elementName == null) {
      return 0;
    }

    return cooldownManager.getRemainingCooldown(player, elementName);
  }

  /**
   * Clear cooldown for a player's equipped fragment.
   *
   * @param player The player
   */
  public void clearCooldown(Player player) {
    if (player == null) {
      return;
    }

    FragmentType equipped = getEquippedFragment(player);
    if (equipped != null) {
      String elementName = getElementName(equipped);
      if (elementName != null) {
        cooldownManager.clearCooldown(player, elementName);
      }
    }
  }

  /**
   * Set cooldown for a player's equipped fragment.
   *
   * @param player The player
   * @param cooldownSeconds Cooldown in seconds
   */
  public void setCooldown(Player player, int cooldownSeconds) {
    if (player == null) {
      return;
    }

    FragmentType equipped = getEquippedFragment(player);
    if (equipped != null) {
      String elementName = getElementName(equipped);
      if (elementName != null) {
        cooldownManager.setCooldown(player, elementName, cooldownSeconds);
      }
    }
  }

  /**
   * Get a fragment instance by type.
   * Delegates to FragmentRegistry - Single Source of Truth.
   *
   * @param fragmentType The fragment type
   * @return The fragment instance, or null if not found
   */
  public Fragment getFragment(FragmentType fragmentType) {
    return fragmentRegistry.getFragment(fragmentType);
  }

  /**
   * Get the number of fragments registered.
   * Delegates to FragmentRegistry.
   *
   * @return Number of registered fragments
   */
  public int getFragmentCount() {
    return fragmentRegistry.getFragmentCount();
  }

  /**
   * Get all available fragment types.
   *
   * @return Array of fragment types
   */
  public FragmentType[] getAvailableFragmentTypes() {
    return FragmentType.values();
  }

  /**
   * Event handler for player death - unequips fragment.
   *
   * @param event The player death event
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    if (player != null) {
      unequipFragment(player);
    }
  }

  // PlayerJoinEvent and PlayerQuitEvent handlers removed - CooldownManager now handles cooldown lifecycle
}
