package org.cavarest.elementaldragon;

import org.cavarest.elementaldragon.ability.AbilityManager;
import org.cavarest.elementaldragon.achievement.AchievementManager;
import org.cavarest.elementaldragon.command.ChronicleCommand;
import org.cavarest.elementaldragon.command.CraftCommand;
import org.cavarest.elementaldragon.command.AgilityCommand;
import org.cavarest.elementaldragon.command.CorruptedCommand;
import org.cavarest.elementaldragon.command.ElementalDragonCommand;
import org.cavarest.elementaldragon.command.FireCommand;
import org.cavarest.elementaldragon.command.ImmortalCommand;
import org.cavarest.elementaldragon.command.LightningCommand;
import org.cavarest.elementaldragon.command.WithdrawabilityCommand;
import org.cavarest.elementaldragon.cooldown.CooldownManager;
import org.cavarest.elementaldragon.crafting.CraftedCountManager;
import org.cavarest.elementaldragon.crafting.CraftingListener;
import org.cavarest.elementaldragon.crafting.CraftingManager;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.hud.PlayerPreferenceManager;
import org.cavarest.elementaldragon.hud.HudManager;
import org.cavarest.elementaldragon.lore.ChronicleManager;
import org.cavarest.elementaldragon.tracking.ElementalPlayerTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;

/**
 * Main plugin class for Elemental Dragon plugin.
 * Replaces the DragonEggLightning plugin with new branding.
 */
public class ElementalDragon extends JavaPlugin {

  private CooldownManager cooldownManager;
  private AbilityManager abilityManager;
  private FragmentManager fragmentManager;
  private HudManager hudManager;
  private CraftingManager craftingManager;
  private CraftedCountManager craftedCountManager;
  private ChronicleManager chronicleManager;
  private AchievementManager achievementManager;
  private ElementalPlayerTracker playerTracker;
  private WithdrawabilityCommand withdrawabilityCommand;
  private PlayerPreferenceManager playerPreferenceManager;

  @Override
  public void onEnable() {
    // Initialize CooldownManager FIRST - other managers depend on it
    this.cooldownManager = new CooldownManager(this);

    // Initialize managers with CooldownManager dependency
    this.abilityManager = new AbilityManager(this, cooldownManager);
    this.chronicleManager = new ChronicleManager(this);
    this.achievementManager = new AchievementManager(this);
    this.fragmentManager = new FragmentManager(this, cooldownManager);
    this.playerTracker = new ElementalPlayerTracker(this);
    this.hudManager = new HudManager(this, abilityManager, fragmentManager, cooldownManager);
    this.craftingManager = new CraftingManager(this);
    this.craftedCountManager = new CraftedCountManager(this);
    this.playerPreferenceManager = new PlayerPreferenceManager();

    registerCommands();
    registerListeners();

    getLogger().info("Elemental Dragon plugin enabled!");
    getLogger().info("Plugin version: " + getDescription().getVersion());
    getLogger().info("Fragment system available - use /fire, /agile, /immortal, or /corrupt for abilities");
    getLogger().info("Crafting system available - use /craft to view recipes");
    getLogger().info("Chronicle system available - use /chronicle to view lore");
  }

  @Override
  public void onDisable() {
    if (hudManager != null) {
      hudManager.shutdown();
    }
    if (playerPreferenceManager != null) {
      playerPreferenceManager.clearCache();
    }
    getLogger().info("Elemental Dragon plugin disabled!");
  }

  /**
   * Register plugin commands.
   */
  private void registerCommands() {
    LightningCommand lightningCommand = new LightningCommand(this, abilityManager);
    getCommand("lightning").setExecutor(lightningCommand);
    getCommand("lightning").setTabCompleter(lightningCommand);

    ElementalDragonCommand elementalCommand = new ElementalDragonCommand(this, abilityManager);
    getCommand("elementaldragon").setExecutor(elementalCommand);
    getCommand("elementaldragon").setTabCompleter(elementalCommand);

    // Register dedicated fragment commands
    FireCommand fireCommand = new FireCommand(this, fragmentManager);
    getCommand("fire").setExecutor(fireCommand);
    getCommand("fire").setTabCompleter(fireCommand);

    AgilityCommand agilityCommand = new AgilityCommand(this, fragmentManager);
    getCommand("agile").setExecutor(agilityCommand);
    getCommand("agile").setTabCompleter(agilityCommand);

    ImmortalCommand immortalCommand = new ImmortalCommand(this, fragmentManager);
    getCommand("immortal").setExecutor(immortalCommand);
    getCommand("immortal").setTabCompleter(immortalCommand);

    CorruptedCommand corruptedCommand = new CorruptedCommand(this, fragmentManager);
    getCommand("corrupt").setExecutor(corruptedCommand);
    getCommand("corrupt").setTabCompleter(corruptedCommand);

    CraftCommand craftCommand = new CraftCommand(this, craftingManager, craftedCountManager);
    getCommand("craft").setExecutor(craftCommand);
    getCommand("craft").setTabCompleter(craftCommand);

    ChronicleCommand chronicleCommand = new ChronicleCommand(this);
    getCommand("chronicle").setExecutor(chronicleCommand);
    getCommand("chronicle").setTabCompleter(chronicleCommand);

    // Register withdraw WithdrawabilityCommandability command
    withdrawabilityCommand = new WithdrawabilityCommand(this, fragmentManager);
    getCommand("withdrawability").setExecutor(withdrawabilityCommand);
  }

  /**
   * Register event listeners.
   */
  private void registerListeners() {
    // Register player tracker
    if (playerTracker != null) {
      getServer().getPluginManager().registerEvents(playerTracker, this);
    }

    // Register unified fragment item listener (handles equip, drop, container restrictions, protection)
    if (fragmentManager != null) {
      org.cavarest.elementaldragon.listener.FragmentItemListener fragmentItemListener =
        new org.cavarest.elementaldragon.listener.FragmentItemListener(this, fragmentManager);
      getServer().getPluginManager().registerEvents(fragmentItemListener, this);
    }

    // Register crafting listener for Heavy Core validation in fragment recipes
    if (craftingManager != null) {
      CraftingListener craftingListener = new CraftingListener(this, craftingManager, craftedCountManager);
      getServer().getPluginManager().registerEvents(craftingListener, this);
    }
  }

  public CooldownManager getCooldownManager() {
    return cooldownManager;
  }

  public AbilityManager getAbilityManager() {
    return abilityManager;
  }

  public FragmentManager getFragmentManager() {
    return fragmentManager;
  }

  public HudManager getHudManager() {
    return hudManager;
  }

  public CraftingManager getCraftingManager() {
    return craftingManager;
  }

  public CraftedCountManager getCraftedCountManager() {
    return craftedCountManager;
  }

  public ChronicleManager getChronicleManager() {
    return chronicleManager;
  }

  public AchievementManager getAchievementManager() {
    return achievementManager;
  }

  public ElementalPlayerTracker getPlayerTracker() {
    return playerTracker;
  }

  public PlayerPreferenceManager getPlayerPreferenceManager() {
    return playerPreferenceManager;
  }

  /**
   * Send plugin info to player
   */
  @SuppressWarnings("deprecation")
  public void sendPluginInfo(Player player) {
    player.sendMessage(
      Component.text("=== Elemental Dragon Plugin Info ===", NamedTextColor.GOLD)
    );
    player.sendMessage(
      Component.text("Version: " + getDescription().getVersion(), NamedTextColor.WHITE)
    );
    player.sendMessage(
      Component.text("Plugin File: " + getFile().getName(), NamedTextColor.WHITE)
    );
    player.sendMessage(
      Component.text("Last Modified: " + new Date(getFile().lastModified()), NamedTextColor.WHITE)
    );
    player.sendMessage(
      Component.text("Author: " + String.join(", ", getDescription().getAuthors()), NamedTextColor.BLUE)
    );
    player.sendMessage(
      Component.text("Description: " + getDescription().getDescription(), NamedTextColor.GRAY)
    );
  }
}
