package org.cavarest.elementaldragon.lore;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages the Chronicle of the Fallen Dragons lore book system.
 * Tracks player progress, ability usage, and unlocks pages based on triggers.
 */
public class ChronicleManager {

  private final ElementalDragon plugin;

  // Track discovered pages per player
  private final Map<UUID, Set<LorePage>> discoveredPages;

  // Track ability usage counts per player per fragment
  // Structure: UUID -> FragmentType -> AbilityNumber -> Count
  private final Map<UUID, Map<FragmentType, Map<Integer, Integer>>> abilityUsageCount;

  // Track which fragments have been equipped by each player
  private final Map<UUID, Set<FragmentType>> equippedFragments;

  /**
   * Create a new ChronicleManager.
   *
   * @param plugin The plugin instance
   */
  public ChronicleManager(ElementalDragon plugin) {
    this.plugin = plugin;
    this.discoveredPages = new HashMap<>();
    this.abilityUsageCount = new HashMap<>();
    this.equippedFragments = new HashMap<>();
  }

  /**
   * Register an ability use for a player.
   * This tracks usage for lore unlocking.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @param abilityNumber The ability number (1 or 2)
   */
  public void registerAbilityUse(Player player, FragmentType fragmentType, int abilityNumber) {
    if (player == null || fragmentType == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();

    // Initialize tracking if needed
    abilityUsageCount.putIfAbsent(playerUuid, new EnumMap<>(FragmentType.class));
    Map<FragmentType, Map<Integer, Integer>> playerUsage = abilityUsageCount.get(playerUuid);

    playerUsage.putIfAbsent(fragmentType, new HashMap<>());
    Map<Integer, Integer> fragmentUsage = playerUsage.get(fragmentType);

    // Increment count
    int currentCount = fragmentUsage.getOrDefault(abilityNumber, 0);
    fragmentUsage.put(abilityNumber, currentCount + 1);

    // Check for unlocks
    checkUnlocks(player);
  }

  /**
   * Register a fragment equip event for a player.
   *
   * @param player The player
   * @param fragmentType The fragment type being equipped
   */
  public void registerFragmentEquip(Player player, FragmentType fragmentType) {
    if (player == null || fragmentType == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();

    // Initialize tracking if needed
    equippedFragments.putIfAbsent(playerUuid, EnumSet.noneOf(FragmentType.class));

    // Add fragment to equipped set
    equippedFragments.get(playerUuid).add(fragmentType);

    // Check for unlocks
    checkUnlocks(player);
  }

  /**
   * Get all discovered pages for a player.
   *
   * @param player The player
   * @return Set of discovered pages
   */
  public Set<LorePage> getDiscoveredPages(Player player) {
    if (player == null) {
      return EnumSet.noneOf(LorePage.class);
    }

    UUID playerUuid = player.getUniqueId();
    discoveredPages.putIfAbsent(playerUuid, EnumSet.noneOf(LorePage.class));

    // Always ensure introduction page is discovered
    Set<LorePage> pages = discoveredPages.get(playerUuid);
    pages.add(LorePage.INTRODUCTION);

    return pages;
  }

  /**
   * Get progress for a specific page unlock condition.
   *
   * @param player The player
   * @param page The lore page
   * @return Progress string (e.g., "3/5")
   */
  public String getProgress(Player player, LorePage page) {
    if (player == null || page == null) {
      return "0/0";
    }

    UUID playerUuid = player.getUniqueId();

    switch (page.getTrigger()) {
      case ALWAYS:
        return "Unlocked";

      case ABILITY_USE:
        FragmentType fragmentType = page.getFragmentType();
        int requiredCount = page.getRequiredCount();

        if (fragmentType == null) {
          return "0/" + requiredCount;
        }

        // Get total usage for all abilities of this fragment
        int totalUsage = getTotalAbilityUsage(playerUuid, fragmentType);
        return totalUsage + "/" + requiredCount;

      case EQUIP_ALL_FRAGMENTS:
        Set<FragmentType> equipped = equippedFragments.getOrDefault(
          playerUuid,
          EnumSet.noneOf(FragmentType.class)
        );
        int equippedCount = equipped.size();
        int totalFragments = FragmentType.values().length;
        return equippedCount + "/" + totalFragments;

      case MASTER_ALL_ABILITIES:
        int masterCount = getMasteredAbilityCount(playerUuid);
        int totalAbilities = FragmentType.values().length * 2; // 2 abilities per fragment
        return masterCount + "/" + totalAbilities;

      default:
        return "Unknown";
    }
  }

  /**
   * Check and unlock new pages for a player based on their progress.
   *
   * @param player The player
   */
  public void checkUnlocks(Player player) {
    if (player == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();
    Set<LorePage> discovered = getDiscoveredPages(player);

    for (LorePage page : LorePage.values()) {
      // Skip already discovered pages
      if (discovered.contains(page)) {
        continue;
      }

      // Check if page should be unlocked
      if (shouldUnlockPage(player, page)) {
        unlockPage(player, page);
      }
    }
  }

  /**
   * Check if a page should be unlocked for a player.
   *
   * @param player The player
   * @param page The page to check
   * @return true if page should be unlocked
   */
  private boolean shouldUnlockPage(Player player, LorePage page) {
    UUID playerUuid = player.getUniqueId();

    switch (page.getTrigger()) {
      case ALWAYS:
        return true;

      case ABILITY_USE:
        FragmentType fragmentType = page.getFragmentType();
        if (fragmentType == null) {
          return false;
        }

        int totalUsage = getTotalAbilityUsage(playerUuid, fragmentType);
        return totalUsage >= page.getRequiredCount();

      case EQUIP_ALL_FRAGMENTS:
        Set<FragmentType> equipped = equippedFragments.getOrDefault(
          playerUuid,
          EnumSet.noneOf(FragmentType.class)
        );
        return equipped.size() >= FragmentType.values().length;

      case MASTER_ALL_ABILITIES:
        int masterCount = getMasteredAbilityCount(playerUuid);
        int totalAbilities = FragmentType.values().length * 2; // 2 abilities per fragment
        return masterCount >= totalAbilities;

      default:
        return false;
    }
  }

  /**
   * Unlock a page for a player and notify them.
   *
   * @param player The player
   * @param page The page to unlock
   */
  private void unlockPage(Player player, LorePage page) {
    UUID playerUuid = player.getUniqueId();

    discoveredPages.putIfAbsent(playerUuid, EnumSet.noneOf(LorePage.class));
    discoveredPages.get(playerUuid).add(page);

    // Notify player
    player.sendMessage(
      Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_PURPLE)
    );
    player.sendMessage(
      Component.text("📖 Chronicle Page Unlocked!", NamedTextColor.GOLD)
    );
    player.sendMessage(
      Component.text("Page " + page.getPageNumber() + ": " + page.getTitle(),
        NamedTextColor.YELLOW)
    );
    player.sendMessage(
      Component.text("Use /chronicle to read the newly discovered lore!",
        NamedTextColor.GRAY)
    );
    player.sendMessage(
      Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_PURPLE)
    );

    // Play sound
    player.playSound(
      player.getLocation(),
      org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
      1.0f,
      1.5f
    );
  }

  /**
   * Get total ability usage for a fragment type.
   *
   * @param playerUuid The player UUID
   * @param fragmentType The fragment type
   * @return Total usage count
   */
  private int getTotalAbilityUsage(UUID playerUuid, FragmentType fragmentType) {
    if (!abilityUsageCount.containsKey(playerUuid)) {
      return 0;
    }

    Map<FragmentType, Map<Integer, Integer>> playerUsage = abilityUsageCount.get(playerUuid);
    if (!playerUsage.containsKey(fragmentType)) {
      return 0;
    }

    Map<Integer, Integer> fragmentUsage = playerUsage.get(fragmentType);

    // Sum all ability usages for this fragment
    return fragmentUsage.values().stream().mapToInt(Integer::intValue).sum();
  }

  /**
   * Get count of mastered abilities (used 10+ times).
   *
   * @param playerUuid The player UUID
   * @return Count of mastered abilities
   */
  private int getMasteredAbilityCount(UUID playerUuid) {
    if (!abilityUsageCount.containsKey(playerUuid)) {
      return 0;
    }

    Map<FragmentType, Map<Integer, Integer>> playerUsage = abilityUsageCount.get(playerUuid);
    int masterCount = 0;

    for (Map<Integer, Integer> fragmentUsage : playerUsage.values()) {
      for (int count : fragmentUsage.values()) {
        if (count >= 10) {
          masterCount++;
        }
      }
    }

    return masterCount;
  }

  /**
   * Check if a player has discovered a specific page.
   *
   * @param player The player
   * @param page The page to check
   * @return true if page is discovered
   */
  public boolean hasDiscovered(Player player, LorePage page) {
    return getDiscoveredPages(player).contains(page);
  }

  /**
   * Get the total number of discovered pages for a player.
   *
   * @param player The player
   * @return Count of discovered pages
   */
  public int getDiscoveredCount(Player player) {
    return getDiscoveredPages(player).size();
  }

  /**
   * Get the total number of pages in the chronicle.
   *
   * @return Total page count
   */
  public int getTotalPageCount() {
    return LorePage.values().length;
  }

  /**
   * Get ability usage count for a specific fragment and ability.
   *
   * @param player The player
   * @param fragmentType The fragment type
   * @param abilityNumber The ability number
   * @return Usage count
   */
  public int getAbilityUsageCount(Player player, FragmentType fragmentType, int abilityNumber) {
    if (player == null || fragmentType == null) {
      return 0;
    }

    UUID playerUuid = player.getUniqueId();

    if (!abilityUsageCount.containsKey(playerUuid)) {
      return 0;
    }

    Map<FragmentType, Map<Integer, Integer>> playerUsage = abilityUsageCount.get(playerUuid);
    if (!playerUsage.containsKey(fragmentType)) {
      return 0;
    }

    Map<Integer, Integer> fragmentUsage = playerUsage.get(fragmentType);
    return fragmentUsage.getOrDefault(abilityNumber, 0);
  }

  /**
   * Reset all progress for a player (admin command).
   *
   * @param player The player
   */
  public void resetProgress(Player player) {
    if (player == null) {
      return;
    }

    UUID playerUuid = player.getUniqueId();

    discoveredPages.remove(playerUuid);
    abilityUsageCount.remove(playerUuid);
    equippedFragments.remove(playerUuid);

    player.sendMessage(
      Component.text("Chronicle progress has been reset.", NamedTextColor.YELLOW)
    );
  }
}