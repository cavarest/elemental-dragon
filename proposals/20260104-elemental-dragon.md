# Elemental Dragon - Complete Implementation Proposal

## Executive Summary

This document outlines the complete transformation of the "Dragon Egg Lightning" plugin into "Elemental Dragon," a comprehensive DLC-style experience that tells the story of the ancient Draconis Aeterna dragon race and their scattered powers. This proposal includes all features, changes, lore integration, testing requirements, and implementation details needed for a seamless player experience.

---

## 1. Plugin Rebranding & Core Changes

### 1.1 Plugin Metadata Updates

**File: `plugin.yml`**

```yaml
name: ElementalDragon
version: 0.2.0
main: [your.package.path].ElementalDragon
api-version: '1.21'
description: Reclaim the power of the ancient Draconis Aeterna. Craft fragments of their shattered dominion and wield Storm, Flame, Wind, Immortality, and Corruption.
author: [Your Name]
website: [Your Website]
prefix: ED

commands:
  elementaldragon:
    description: Main command for Elemental Dragon
    aliases: [ed, edragon]
    usage: /<command> [reload|give|lore|help]
    permission: elementaldragon.admin

  edlore:
    description: Receive the Chronicle of the Fallen Dragons
    usage: /<command>
    permission: elementaldragon.lore

permissions:
  elementaldragon.admin:
    description: Access to all admin commands
    default: op

  elementaldragon.lore:
    description: Ability to receive the lore book
    default: true

  elementaldragon.use.lightning:
    description: Use Dragon Egg Lightning ability
    default: true

  elementaldragon.use.burning:
    description: Use Burning Fragment abilities
    default: true

  elementaldragon.use.agility:
    description: Use Agility Fragment abilities
    default: true

  elementaldragon.use.immortal:
    description: Use Immortal Fragment abilities
    default: true

  elementaldragon.use.corrupted:
    description: Use Corrupted Core abilities
    default: true

  elementaldragon.craft.*:
    description: Craft all fragments
    default: true
    children:
      elementaldragon.craft.burning: true
      elementaldragon.craft.agility: true
      elementaldragon.craft.immortal: true
      elementaldragon.craft.corrupted: true
```

### 1.2 Configuration File Updates

**File: `config.yml`**

```yaml
# Elemental Dragon Configuration
# Version 0.2.0

# Plugin Settings
plugin:
  prefix: '§5[Elemental Dragon]§r'
  language: 'en_US'
  debug: false

# Lore & Story Settings
lore:
  enable-discovery-messages: true
  enable-achievement-system: true
  enable-lore-book: true
  lore-book-craftable: true
  show-prophecy-on-all-fragments: true

# Dragon Egg Lightning (Original Feature)
dragon-egg:
  enabled: true
  offhand-only: true
  ability-name: '§eHeaven's Light'
  cooldown: 5 # seconds
  damage: 8.0
  range: 50
  particle-effects: true
  sound-effects: true
  permission-required: true

  messages:
    first-use: '§7The Dragon Egg pulses with ancient power...'
    ability-name-display: '§6⚡ §eHeaven's Light §6⚡'

# Burning Fragment Settings
burning-fragment:
  enabled: true
  offhand-only: true

  # Ability 1: Dragon's Wrath (Fireball)
  dragons-wrath:
    enabled: true
    name: "§6Dragon's Wrath"
    cooldown: 8
    damage: 10.0
    explosion-power: 2.0
    fire-duration: 100 # ticks (5 seconds)
    range: 60
    velocity: 2.0

  # Ability 2: Infernal Dominion (Area Burn)
  infernal-dominion:
    enabled: true
    name: '§cInfernal Dominion'
    cooldown: 15
    radius: 5.0
    duration: 200 # ticks (10 seconds)
    damage-per-tick: 1.0
    tick-interval: 20 # ticks (1 second)

  lore:
    - '§6Burning Fragment'
    - '§7Harness the power of flame'
    - '§8The dragon''s breath, crystallized'
    - '§8Relic of the Draconis Aeterna'
    - ''
    - '§7Right-Click: §6Dragon''s Wrath'
    - '§7Shift + Right-Click: §cInfernal Dominion'

# Agility Fragment Settings
agility-fragment:
  enabled: true
  offhand-only: true

  # Ability 1: Draconic Surge (Dash)
  draconic-surge:
    enabled: true
    name: '§bDraconic Surge'
    cooldown: 5
    distance: 10.0
    invulnerability-duration: 10 # ticks (0.5 seconds)
    particle-trail: true

  # Ability 2: Wing Burst (Knockback)
  wing-burst:
    enabled: true
    name: '§3Wing Burst'
    cooldown: 12
    radius: 8.0
    knockback-strength: 3.0
    damage: 6.0

  lore:
    - '§bAgility Fragment'
    - '§7Master speed and mobility'
    - '§8Wings of the void-born'
    - '§8Relic of the Draconis Aeterna'
    - ''
    - '§7Right-Click: §bDraconic Surge'
    - '§7Shift + Right-Click: §3Wing Burst'

# Immortal Fragment Settings
immortal-fragment:
  enabled: true
  offhand-only: true

  # Ability 1: Draconic Reflex (Dodge)
  draconic-reflex:
    enabled: true
    name: '§eDraconic Reflex'
    cooldown: 20
    duration: 60 # ticks (3 seconds)
    dodge-chance: 0.75 # 75%

  # Ability 2: Essence Rebirth (Second Life)
  essence-rebirth:
    enabled: true
    name: '§6Essence Rebirth'
    cooldown: 300 # 5 minutes
    heal-amount: 20.0 # full health
    saturation: 20.0
    effect-duration: 100 # ticks (5 seconds)

  lore:
    - '§eImmortal Fragment'
    - '§7Defy death itself'
    - '§8Death holds no dominion'
    - '§8Relic of the Draconis Aeterna'
    - ''
    - '§7Right-Click: §eDraconic Reflex'
    - '§7Passive: §6Essence Rebirth'

# Corrupted Core Settings
corrupted-core:
  enabled: true
  offhand-only: true

  # Ability 1: Dread Gaze (Freeze)
  dread-gaze:
    enabled: true
    name: '§5Dread Gaze'
    cooldown: 15
    duration: 80 # ticks (4 seconds)
    range: 15.0
    max-targets: 5

  # Ability 2: Life Devourer (Health Steal)
  life-devourer:
    enabled: true
    name: '§4Life Devourer'
    cooldown: 20
    damage: 8.0
    heal-percentage: 1.0 # 100% of damage dealt
    range: 20.0

  lore:
    - '§5Corrupted Core'
    - '§7Embrace the consuming darkness'
    - '§8Power beyond mortal comprehension'
    - '§8Relic of the Draconis Aeterna'
    - ''
    - '§7Right-Click: §5Dread Gaze'
    - '§7Shift + Right-Click: §4Life Devourer'

# Crafting System
crafting:
  enabled: true
  require-heavy-core: true
  shaped-recipes: true

  # Heavy Core recipe (existing)
  heavy-core:
    enabled: true
    ingredients:
      center: NETHERITE_BLOCK
      corners: NETHERITE_INGOT
      edges: CRYING_OBSIDIAN

  # Fragment recipes
  burning-fragment:
    enabled: true
    shape:
      - 'FBF'
      - 'BHB'
      - 'FBF'
    ingredients:
      F: FIRE_CHARGE
      B: BLAZE_ROD
      H: HEAVY_CORE

  agility-fragment:
    enabled: true
    shape:
      - 'PEP'
      - 'EHE'
      - 'PEP'
    ingredients:
      P: PHANTOM_MEMBRANE
      E: ELYTRA
      H: HEAVY_CORE

  immortal-fragment:
    enabled: true
    shape:
      - 'TCT'
      - 'CHC'
      - 'TCT'
    ingredients:
      T: TOTEM_OF_UNDYING
      C: ENCHANTED_GOLDEN_APPLE
      H: HEAVY_CORE

  corrupted-core:
    enabled: true
    shape:
      - 'WSW'
      - 'SHS'
      - 'WSW'
    ingredients:
      W: WITHER_SKELETON_SKULL
      S: NETHER_STAR
      H: HEAVY_CORE

# Particle Effects
particles:
  enabled: true
  density: NORMAL # LOW, NORMAL, HIGH

  lightning-strike:
    type: ELECTRIC_SPARK
    count: 30

  fireball:
    type: FLAME
    count: 20

  area-burn:
    type: FLAME
    count: 10
    interval: 5 # ticks

  dash:
    type: CLOUD
    count: 15

  knockback:
    type: EXPLOSION_NORMAL
    count: 25

  dodge:
    type: ENCHANTMENT_TABLE
    count: 5
    interval: 3

  resurrection:
    type: TOTEM
    count: 50

  freeze:
    type: SNOWFLAKE
    count: 20

  life-steal:
    type: DAMAGE_INDICATOR
    count: 10

  all-fragments-achievement:
    type: DRAGON_BREATH
    count: 100
    duration: 100 # ticks

# Sound Effects
sounds:
  enabled: true
  volume: 1.0

  lightning-strike: ENTITY_LIGHTNING_BOLT_THUNDER
  fireball: ENTITY_BLAZE_SHOOT
  area-burn: BLOCK_FIRE_AMBIENT
  dash: ENTITY_ENDER_DRAGON_FLAP
  knockback: ENTITY_GENERIC_EXPLODE
  dodge: BLOCK_ENCHANTMENT_TABLE_USE
  resurrection: ITEM_TOTEM_USE
  freeze: BLOCK_GLASS_BREAK
  life-steal: ENTITY_PLAYER_HURT

  fragment-discovery: ENTITY_PLAYER_LEVELUP
  all-fragments: ENTITY_ENDER_DRAGON_GROWL
  lore-book: ITEM_BOOK_PAGE_TURN

# Discovery & Achievement System
achievements:
  enabled: true

  discoveries:
    dragon-egg-lightning:
      title: '§e⚡ Heaven's Lighter ⚡'
      subtitle: '§7You have awakened the Dragon Egg'
      reward: false

    burning-fragment:
      title: '§6The Dragon''s Breath Awakens'
      subtitle: '§7You have claimed the Infernal power'
      reward: false

    agility-fragment:
      title: '§bThe Soaring Dragon Stirs'
      subtitle: '§7You have claimed the Skyward power'
      reward: false

    immortal-fragment:
      title: '§eThe Eternal Cycle Begins'
      subtitle: '§7You have claimed the Immortal power'
      reward: false

    corrupted-core:
      title: '§5The Darkness Awakens'
      subtitle: '§7You have claimed the Consuming power'
      reward: false

    all-fragments:
      title: '§5§lDRACONIS AETERNA'
      subtitle: '§7The ancient power is yours to command'
      reward: true
      reward-type: ADVANCEMENT
      advancement-name: 'legacy_of_dragons'
      display-particles: true
      display-sound: true

# Messages & Localization
messages:
  prefix: '§5[Elemental Dragon]§r '

  errors:
    no-permission: '§cYou lack the power to wield this fragment.'
    on-cooldown: '§cThe fragment''s power is recharging... ({time}s remaining)'
    wrong-hand: '§cThis fragment must be held in your offhand.'
    invalid-target: '§cNo valid target found.'

  success:
    ability-used: '§aYou unleash {ability}!'
    fragment-crafted: '§aYou have forged the {fragment}!'
    lore-book-received: '§aYou have received the Chronicle of the Fallen Dragons.'

  cooldown:
    ready: '§a{ability} is ready!'
    format: '§e{ability}: §c{time}s'

  info:
    plugin-reloaded: '§aElemental Dragon has been reloaded.'
    help-header: '§5§l=== Elemental Dragon Commands ==='
    help-reload: '§7/ed reload §f- Reload the plugin'
    help-give: '§7/ed give <player> <fragment> §f- Give a fragment'
    help-lore: '§7/edlore §f- Receive the lore book'

# Performance & Optimization
performance:
  async-particle-rendering: true
  cache-player-data: true
  max-tracked-projectiles: 100
  cleanup-interval: 6000 # ticks (5 minutes)

# Debug Settings
debug:
  log-ability-usage: false
  log-cooldowns: false
  log-crafting: false
  verbose-errors: false
```

---

## 2. New Feature Implementation

### 2.1 Lore Book System

**Class: `LoreBookManager.java`**

```java
package [your.package.path].lore;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;

public class LoreBookManager {

    private final ElementalDragon plugin;

    public LoreBookManager(ElementalDragon plugin) {
        this.plugin = plugin;
    }

    public ItemStack createLoreBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.setTitle("§5Chronicle of the Fallen Dragons");
        meta.setAuthor("§8The Ancient Builders");
        meta.setGeneration(BookMeta.Generation.ORIGINAL);

        // Add enchantment glow
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Page 1: Introduction
        meta.addPage(
            "§0§lThe Draconis Aeterna\n\n" +
            "§0In an age before time was measured, before the sun first touched the Overworld, there existed a realm shrouded in eternal twilight—the §5Umbral Dominion§0.\n\n" +
            "Here ruled the §5Draconis Aeterna§0, an ancient dragon race whose power transcended mortality itself."
        );

        // Page 2: The Five Powers
        meta.addPage(
            "§0§lThe Five Powers\n\n" +
            "§6The Heaven's Light\n" +
            "§0Summoning lightning from the void.\n\n" +
            "§6The Infernal Breath\n" +
            "§0Flames that could reduce mountains to ash.\n\n" +
            "§6The Skyward Dominion\n" +
            "§0Movement faster than thought itself."
        );

        // Page 3: More Powers
        meta.addPage(
            "§6The Eternal Cycle\n" +
            "§0Death held no dominion over them.\n\n" +
            "§6The Consuming Darkness\n" +
            "§0A corruption so profound it could drain life from their enemies and freeze them with terror."
        );

        // Page 4: The Sundering
        meta.addPage(
            "§0§lThe Sundering\n\n" +
            "§0The dragons' power grew until it threatened to consume all realms. The ancient builders rose against them in a war that lasted a thousand years.\n\n" +
            "The dragons could not be killed, for death meant nothing to them."
        );

        // Page 5: The Great Binding
        meta.addPage(
            "§0Using the §5Heavy Core§0—a fragment of reality's anchor—the builders performed the §5Great Binding§0.\n\n" +
            "The dragons' essences were torn from their immortal forms and scattered, their powers locked into physical vessels."
        );

        // Page 6: The Dragon Egg
        meta.addPage(
            "§0§lThe Dragon Egg\n\n" +
            "§0The Dragon Egg you find in the End is the §5last unhatched essence§0 of the Draconis Aeterna.\n\n" +
            "When held in your left hand, it resonates with the ancient Heaven's Light."
        );

        // Page 7: The Fragments
        meta.addPage(
            "§0§lThe Fragments\n\n" +
            "§6Burning Fragment\n" +
            "§0The dragon's breath, crystallized.\n\n" +
            "§bAgility Fragment\n" +
            "§0Wings of the void-born.\n\n" +
            "§eImmortal Fragment\n" +
            "§0Death holds no dominion."
        );

        // Page 8: More Fragments
        meta.addPage(
            "§5Corrupted Core\n" +
            "§0Power beyond mortal comprehension.\n\n" +
            "§0Each fragment represents one aspect of the Draconis Aeterna's power, bound within items crafted from the rarest materials."
        );

        // Page 9: The Prophecy Part 1
        meta.addPage(
            "§0§lThe Prophecy\n\n" +
            "§5When five become one in mortal hands,\n" +
            "When fire and storm and wind command,\n" +
            "When death is spurned and darkness feeds,\n" +
            "The Draconis Aeterna plants new seeds."
        );

        // Page 10: The Prophecy Part 2
        meta.addPage(
            "§5Not to rule as once before,\n" +
            "But to guard against a greater war.\n" +
            "For in the void where dragons fell,\n" +
            "Stirs something worse than any hell."
        );

        // Page 11: The Prophecy Part 3
        meta.addPage(
            "§5The worthy one who fragments claims,\n" +
            "Shall bear the weight of ancient flames,\n" +
            "And stand as guardian of the light,\n" +
            "Against the coming endless night."
        );

        // Page 12: Crafting Hints
        meta.addPage(
            "§0§lReclaiming Power\n\n" +
            "§0The builders encoded the means to reclaim these fragments into the very fabric of the world.\n\n" +
            "Seek the §5Heavy Core§0, and combine it with materials from across all dimensions."
        );

        book.setItemMeta(meta);
        return book;
    }

    public void giveLoreBook(Player player) {
        ItemStack book = createLoreBook();
        player.getInventory().addItem(book);

        // Play sound
        if (plugin.getConfig().getBoolean("sounds.enabled")) {
            player.playSound(
                player.getLocation(),
                Sound.valueOf(plugin.getConfig().getString("sounds.lore-book")),
                1.0f,
                1.0f
            );
        }

        // Send message
        String message = plugin.getConfig().getString("messages.success.lore-book-received");
        player.sendMessage(plugin.getConfig().getString("messages.prefix") + message);
    }

    public boolean canReceiveLoreBook(Player player) {
        return player.hasPermission("elementaldragon.lore");
    }
}
```

**Lore Book Recipe Registration:**

```java
package [your.package.path].crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

public class LoreBookRecipe {

    private final ElementalDragon plugin;
    private NamespacedKey key;

    public LoreBookRecipe(ElementalDragon plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "lore_book");
    }

    public void register() {
        if (!plugin.getConfig().getBoolean("lore.lore-book-craftable")) {
            return;
        }

        ItemStack result = plugin.getLoreBookManager().createLoreBook();

        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(Material.BOOK);
        recipe.addIngredient(Material.DRAGON_EGG);

        // Custom recipe that returns the dragon egg
        // This requires custom crafting listener implementation

        Bukkit.addRecipe(recipe);
    }

    public void unregister() {
        Bukkit.removeRecipe(key);
    }
}
```

### 2.2 Discovery System

**Class: `DiscoveryManager.java`**

```java
package [your.package.path].discovery;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

public class DiscoveryManager {

    private final ElementalDragon plugin;
    private final Map<UUID, Set<DiscoveryType>> discoveries;

    public enum DiscoveryType {
        DRAGON_EGG_LIGHTNING,
        BURNING_FRAGMENT,
        AGILITY_FRAGMENT,
        IMMORTAL_FRAGMENT,
        CORRUPTED_CORE,
        ALL_FRAGMENTS
    }

    public DiscoveryManager(ElementalDragon plugin) {
        this.plugin = plugin;
        this.discoveries = new HashMap<>();
    }

    public void registerDiscovery(Player player, DiscoveryType type) {
        if (!plugin.getConfig().getBoolean("lore.enable-discovery-messages")) {
            return;
        }

        UUID uuid = player.getUniqueId();
        discoveries.putIfAbsent(uuid, new HashSet<>());

        if (discoveries.get(uuid).contains(type)) {
            return; // Already discovered
        }

        discoveries.get(uuid).add(type);

        // Get discovery configuration
        ConfigurationSection config = plugin.getConfig()
            .getConfigurationSection("achievements.discoveries." + type.name().toLowerCase().replace('_', '-'));

        if (config == null) return;

        String title = config.getString("title");
        String subtitle = config.getString("subtitle");

        // Send title
        player.sendTitle(title, subtitle, 10, 70, 20);

        // Play sound
        if (plugin.getConfig().getBoolean("sounds.enabled")) {
            player.playSound(
                player.getLocation(),
                Sound.valueOf(plugin.getConfig().getString("sounds.fragment-discovery")),
                1.0f,
                1.0f
            );
        }

        // Check for all fragments achievement
        if (type != DiscoveryType.ALL_FRAGMENTS && hasAllFragments(player)) {
            registerDiscovery(player, DiscoveryType.ALL_FRAGMENTS);
            grantAllFragmentsAchievement(player);
        }
    }

    public boolean hasAllFragments(Player player) {
        UUID uuid = player.getUniqueId();
        if (!discoveries.containsKey(uuid)) return false;

        Set<DiscoveryType> playerDiscoveries = discoveries.get(uuid);
        return playerDiscoveries.contains(DiscoveryType.DRAGON_EGG_LIGHTNING) &&
               playerDiscoveries.contains(DiscoveryType.BURNING_FRAGMENT) &&
               playerDiscoveries.contains(DiscoveryType.AGILITY_FRAGMENT) &&
               playerDiscoveries.contains(DiscoveryType.IMMORTAL_FRAGMENT) &&
               playerDiscoveries.contains(DiscoveryType.CORRUPTED_CORE);
    }

    private void grantAllFragmentsAchievement(Player player) {
        ConfigurationSection config = plugin.getConfig()
            .getConfigurationSection("achievements.discoveries.all-fragments");

        if (config == null) return;

        // Display particles
        if (config.getBoolean("display-particles")) {
            ConfigurationSection particleConfig = plugin.getConfig()
                .getConfigurationSection("particles.all-fragments-achievement");

            String particleType = particleConfig.getString("type");
            int count = particleConfig.getInt("count");
            int duration = particleConfig.getInt("duration");

            // Schedule particle effect
            new ParticleCircleTask(plugin, player, particleType, count, duration).start();
        }

        // Play special sound
        if (config.getBoolean("display-sound")) {
            player.playSound(
                player.getLocation(),
                Sound.valueOf(plugin.getConfig().getString("sounds.all-fragments")),
                1.0f,
                0.8f
            );
        }

        // Grant advancement if enabled
        if (config.getBoolean("reward") &&
            config.getString("reward-type").equalsIgnoreCase("ADVANCEMENT")) {
            grantAdvancement(player, config.getString("advancement-name"));
        }
    }

    private void grantAdvancement(Player player, String advancementName) {
        // Implementation for granting custom advancement
        // This requires creating an advancement JSON file
        NamespacedKey key = new NamespacedKey(plugin, advancementName);
        Advancement advancement = Bukkit.getAdvancement(key);

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String criteria : progress.getRemainingCriteria()) {
                progress.awardCriteria(criteria);
            }
        }
    }

    public void saveData() {
        // Save discovery data to file
        // Implementation depends on your data storage preference
    }

    public void loadData() {
        // Load discovery data from file
    }
}
```

**Particle Circle Task:**

```java
package [your.package.path].tasks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleCircleTask extends BukkitRunnable {

    private final ElementalDragon plugin;
    private final Player player;
    private final Particle particle;
    private final int count;
    private final int duration;
    private int ticks = 0;

    public ParticleCircleTask(ElementalDragon plugin, Player player,
                              String particleType, int count, int duration) {
        this.plugin = plugin;
        this.player = player;
        this.particle = Particle.valueOf(particleType);
        this.count = count;
        this.duration = duration;
    }

    public void start() {
        this.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void run() {
        if (ticks >= duration || !player.isOnline()) {
            this.cancel();
            return;
        }

        Location loc = player.getLocation().clone().add(0, 1, 0);

        // Create circle of particles
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double x = Math.cos(angle) * 2;
            double z = Math.sin(angle) * 2;

            Location particleLoc = loc.clone().add(x, Math.sin(ticks * 0.1) * 0.5, z);
            player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }

        ticks++;
    }
}
```

### 2.3 Fragment Item Creation with Lore

**Class: `FragmentItems.java`**

```java
package [your.package.path].items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class FragmentItems {

    private final ElementalDragon plugin;

    public FragmentItems(ElementalDragon plugin) {
        this.plugin = plugin;
    }

    public ItemStack createBurningFragment() {
        ItemStack item = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta meta = item.getItemMeta();

        // Get lore from config
        List<String> lore = plugin.getConfig().getStringList("burning-fragment.lore");

        meta.setDisplayName(lore.get(0)); // First line is the name
        meta.setLore(lore.subList(1, lore.size())); // Rest is lore

        // Add enchantment glow
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        // Set custom model data for resource pack support (optional)
        meta.setCustomModelData(1001);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createAgilityFragment() {
        ItemStack item = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = plugin.getConfig().getStringList("agility-fragment.lore");

        meta.setDisplayName(lore.get(0));
        meta.setLore(lore.subList(1, lore.size()));

        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setCustomModelData(1002);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createImmortalFragment() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = plugin.getConfig().getStringList("immortal-fragment.lore");

        meta.setDisplayName(lore.get(0));
        meta.setLore(lore.subList(1, lore.size()));

        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setCustomModelData(1003);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createCorruptedCore() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = plugin.getConfig().getStringList("corrupted-core.lore");

        meta.setDisplayName(lore.get(0));
        meta.setLore(lore.subList(1, lore.size()));

        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setCustomModelData(1004);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isBurningFragment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() &&
               meta.getDisplayName().equals(plugin.getConfig().getStringList("burning-fragment.lore").get(0));
    }

    public boolean isAgilityFragment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() &&
               meta.getDisplayName().equals(plugin.getConfig().getStringList("agility-fragment.lore").get(0));
    }

    public boolean isImmortalFragment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() &&
               meta.getDisplayName().equals(plugin.getConfig().getStringList("immortal-fragment.lore").get(0));
    }

    public boolean isCorruptedCore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() &&
               meta.getDisplayName().equals(plugin.getConfig().getStringList("corrupted-core.lore").get(0));
    }
}
```

### 2.4 Crafting Event Listener

**Class: `CraftingListener.java`**

```java
package [your.package.path].listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {

    private final ElementalDragon plugin;

    public CraftingListener(ElementalDragon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack result = event.getRecipe().getResult();

        // Check if player is crafting a fragment
        FragmentItems items = plugin.getFragmentItems();
        DiscoveryManager discoveries = plugin.getDiscoveryManager();

        if (items.isBurningFragment(result)) {
            if (!player.hasPermission("elementaldragon.craft.burning")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.prefix") +
                                 plugin.getConfig().getString("messages.errors.no-permission"));
                return;
            }

            // Register discovery after a short delay to ensure crafting completes
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                discoveries.registerDiscovery(player, DiscoveryType.BURNING_FRAGMENT);
            }, 1L);

        } else if (items.isAgilityFragment(result)) {
            if (!player.hasPermission("elementaldragon.craft.agility")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.prefix") +
                                 plugin.getConfig().getString("messages.errors.no-permission"));
                return;
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                discoveries.registerDiscovery(player, DiscoveryType.AGILITY_FRAGMENT);
            }, 1L);

        } else if (items.isImmortalFragment(result)) {
            if (!player.hasPermission("elementaldragon.craft.immortal")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.prefix") +
                                 plugin.getConfig().getString("messages.errors.no-permission"));
                return;
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                discoveries.registerDiscovery(player, DiscoveryType.IMMORTAL_FRAGMENT);
            }, 1L);

        } else if (items.isCorruptedCore(result)) {
            if (!player.hasPermission("elementaldragon.craft.corrupted")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.prefix") +
                                 plugin.getConfig().getString("messages.errors.no-permission"));
                return;
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                discoveries.registerDiscovery(player, DiscoveryType.CORRUPTED_CORE);
            }, 1L);
        }

        // Handle lore book crafting (return dragon egg)
        if (result.getType() == Material.WRITTEN_BOOK &&
            result.getItemMeta().hasTitle() &&
            result.getItemMeta().getTitle().equals("§5Chronicle of the Fallen Dragons")) {

            // Return dragon egg to player after crafting
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                player.getInventory().addItem(new ItemStack(Material.DRAGON_EGG));
            }, 1L);
        }
    }
}
```

---

## 3. Ability Implementation Details

### 3.1 Burning Fragment Abilities

**Class: `BurningFragmentAbility.java`**

```java
package [your.package.path].abilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BurningFragmentAbility implements FragmentAbility {

    private final ElementalDragon plugin;
    private final Map<UUID, Long> dragonsWrathCooldowns;
    private final Map<UUID, Long> infernalDominionCooldowns;

    public BurningFragmentAbility(ElementalDragon plugin) {
        this.plugin = plugin;
        this.dragonsWrathCooldowns = new HashMap<>();
        this.infernalDominionCooldowns = new HashMap<>();
    }

    @Override
    public void useAbility1(Player player) { // Dragon's Wrath (Right-Click)
        if (!hasPermission(player, "elementaldragon.use.burning")) {
            sendNoPermissionMessage(player);
            return;
        }

        if (isOnCooldown(player, dragonsWrathCooldowns, "burning-fragment.dragons-wrath.cooldown")) {
            sendCooldownMessage(player, dragonsWrathCooldowns, "burning-fragment.dragons-wrath.cooldown");
            return;
        }

        // Launch fireball
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection();

        Fireball fireball = player.getWorld().spawn(
            eyeLoc.add(direction.multiply(1.5)),
            Fireball.class
        );

        fireball.setShooter(player);
        fireball.setDirection(direction);
        fireball.setYield(plugin.getConfig().getInt("burning-fragment.dragons-wrath.explosion-power"));
        fireball.setIsIncendiary(true);
        fireball.setVelocity(direction.multiply(
            plugin.getConfig().getDouble("burning-fragment.dragons-wrath.velocity")
        ));

        // Store custom data for damage handling
        fireball.getPersistentDataContainer().set(
            new NamespacedKey(plugin, "custom_damage"),
            PersistentDataType.DOUBLE,
            plugin.getConfig().getDouble("burning-fragment.dragons-wrath.damage")
        );

        fireball.getPersistentDataContainer().set(
            new NamespacedKey(plugin, "fire_duration"),
            PersistentDataType.INTEGER,
            plugin.getConfig().getInt("burning-fragment.dragons-wrath.fire-duration")
        );

        // Effects
        playSound(player, "sounds.fireball");
        spawnParticles(player.getLocation(), "particles.fireball");

        // Set cooldown
        setCooldown(player, dragonsWrathCooldowns, "burning-fragment.dragons-wrath.cooldown");

        // Send success message
        String abilityName = plugin.getConfig().getString("burning-fragment.dragons-wrath.name");
        sendAbilityUsedMessage(player, abilityName);
    }

    @Override
    public void useAbility2(Player player) { // Infernal Dominion (Shift + Right-Click)
        if (!hasPermission(player, "elementaldragon.use.burning")) {
            sendNoPermissionMessage(player);
            return;
        }

        if (isOnCooldown(player, infernalDominionCooldowns, "burning-fragment.infernal-dominion.cooldown")) {
            sendCooldownMessage(player, infernalDominionCooldowns, "burning-fragment.infernal-dominion.cooldown");
            return;
        }

        // Create area burn effect
        Location center = player.getLocation();
        double radius = plugin.getConfig().getDouble("burning-fragment.infernal-dominion.radius");
        int duration = plugin.getConfig().getInt("burning-fragment.infernal-dominion.duration");
        double damagePerTick = plugin.getConfig().getDouble("burning-fragment.infernal-dominion.damage-per-tick");
        int tickInterval = plugin.getConfig().getInt("burning-fragment.infernal-dominion.tick-interval");

        // Start burn task
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration) {
                    this.cancel();
                    return;
                }

                // Damage entities in radius
                center.getWorld().getNearbyEntities(center, radius, radius, radius).forEach(entity -> {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity living = (LivingEntity) entity;
                        living.damage(damagePerTick, player);
                        living.setFireTicks(20); // 1 second of fire
                    }
                });

                // Spawn particles
                if (ticks % plugin.getConfig().getInt("particles.area-burn.interval") == 0) {
                    spawnCircleParticles(center, radius, "particles.area-burn");
                }

                ticks += tickInterval;
            }
        }.runTaskTimer(plugin, 0L, tickInterval);

        // Effects
        playSound(player, "sounds.area-burn");

        // Set cooldown
        setCooldown(player, infernalDominionCooldowns, "burning-fragment.infernal-dominion.cooldown");

        // Send success message
        String abilityName = plugin.getConfig().getString("burning-fragment.infernal-dominion.name");
        sendAbilityUsedMessage(player, abilityName);
    }

    private void spawnCircleParticles(Location center, double radius, String configPath) {
        if (!plugin.getConfig().getBoolean("particles.enabled")) return;

        Particle particle = Particle.valueOf(plugin.getConfig().getString(configPath + ".type"));
        int count = plugin.getConfig().getInt(configPath + ".count");

        for (int i = 0; i < 360; i += 20) {
            double angle = Math.toRadians(i);
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            Location particleLoc = new Location(center.getWorld(), x, center.getY() + 0.1, z);
            center.getWorld().spawnParticle(particle, particleLoc, count / 18, 0.1, 0.1, 0.1, 0);
        }
    }

    // Helper methods (shared with other fragment classes)
    private boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    private void sendNoPermissionMessage(Player player) {
        player.sendMessage(plugin.getConfig().getString("messages.prefix") +
                         plugin.getConfig().getString("messages.errors.no-permission"));
    }

    private boolean isOnCooldown(Player player, Map<UUID, Long> cooldownMap, String configPath) {
        if (!cooldownMap.containsKey(player.getUniqueId())) return false;

        long lastUse = cooldownMap.get(player.getUniqueId());
        long cooldownSeconds = plugin.getConfig().getLong(configPath);
        long currentTime = System.currentTimeMillis();

        return (currentTime - lastUse) < (cooldownSeconds * 1000);
    }

    private void sendCooldownMessage(Player player, Map<UUID, Long> cooldownMap, String configPath) {
        long lastUse = cooldownMap.get(player.getUniqueId());
        long cooldownSeconds = plugin.getConfig().getLong(configPath);
        long currentTime = System.currentTimeMillis();
        long remaining = ((lastUse + (cooldownSeconds * 1000)) - currentTime) / 1000;

        String message = plugin.getConfig().getString("messages.errors.on-cooldown")
            .replace("{time}", String.valueOf(remaining));
        player.sendMessage(plugin.getConfig().getString("messages.prefix") + message);
    }

    private void setCooldown(Player player, Map<UUID, Long> cooldownMap, String configPath) {
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis());

        // Schedule cooldown ready message
        long cooldownSeconds = plugin.getConfig().getLong(configPath);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                playSound(player, "sounds.cooldown-ready"); // Add this to config if desired
            }
        }, cooldownSeconds * 20L);
    }

    private void playSound(Player player, String configPath) {
        if (!plugin.getConfig().getBoolean("sounds.enabled")) return;

        Sound sound = Sound.valueOf(plugin.getConfig().getString(configPath));
        float volume = (float) plugin.getConfig().getDouble("sounds.volume");
        player.playSound(player.getLocation(), sound, volume, 1.0f);
    }

    private void spawnParticles(Location location, String configPath) {
        if (!plugin.getConfig().getBoolean("particles.enabled")) return;

        Particle particle = Particle.valueOf(plugin.getConfig().getString(configPath + ".type"));
        int count = plugin.getConfig().getInt(configPath + ".count");
        location.getWorld().spawnParticle(particle, location, count, 0.5, 0.5, 0.5, 0.1);
    }

    private void sendAbilityUsedMessage(Player player, String abilityName) {
        String message = plugin.getConfig().getString("messages.success.ability-used")
            .replace("{ability}", abilityName);
        player.sendMessage(plugin.getConfig().getString("messages.prefix") + message);
    }
}
```

### 3.2 Agility Fragment Abilities

**Class: `AgilityFragmentAbility.java`**

```java
package [your.package.path].abilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AgilityFragmentAbility implements FragmentAbility {

    private final ElementalDragon plugin;
    private final Map<UUID, Long> draconicSurgeCooldowns;
    private final Map<UUID, Long> wingBurstCooldowns;

    public AgilityFragmentAbility(ElementalDragon plugin) {
        this.plugin = plugin;
        this.draconicSurgeCooldowns = new HashMap<>();
        this.wingBurstCooldowns = new HashMap<>();
    }

    @Override
    public void useAbility1(Player player) { // Draconic Surge (Dash)
        if (!hasPermission(player, "elementaldragon.use.agility")) {
            sendNoPermissionMessage(player);
            return;
        }

        if (isOnCooldown(player, draconicSurgeCooldowns, "agility-fragment.draconic-surge.cooldown")) {
            sendCooldownMessage(player, draconicSurgeCooldowns, "agility-fragment.draconic-surge.cooldown");
            return;
        }

        // Perform dash
        double distance = plugin.getConfig().getDouble("agility-fragment.draconic-surge.distance");
        Vector direction = player.getLocation().getDirection().normalize();
        Vector velocity = direction.multiply(distance);

        player.setVelocity(velocity);

        // Grant temporary invulnerability
        int invulnTicks = plugin.getConfig().getInt("agility-fragment.draconic-surge.invulnerability-duration");
        player.setNoDamageTicks(invulnTicks);

        // Particle trail
        if (plugin.getConfig().getBoolean("agility-fragment.draconic-surge.particle-trail")) {
            createDashTrail(player, distance);
        }

        // Effects
        playSound(player, "sounds.dash");

        // Set cooldown
        setCooldown(player, draconicSurgeCooldowns, "agility-fragment.draconic-surge.cooldown");

        // Send success message
        String abilityName = plugin.getConfig().getString("agility-fragment.draconic-surge.name");
        sendAbilityUsedMessage(player, abilityName);
    }

    @Override
    public void useAbility2(Player player) { // Wing Burst (Knockback)
        if (!hasPermission(player, "elementaldragon.use.agility")) {
            sendNoPermissionMessage(player);
            return;
        }

        if (isOnCooldown(player, wingBurstCooldowns, "agility-fragment.wing-burst.cooldown")) {
            sendCooldownMessage(player, wingBurstCooldowns, "agility-fragment.wing-burst.cooldown");
            return;
        }

        // Knockback surrounding entities
        double radius = plugin.getConfig().getDouble("agility-fragment.wing-burst.radius");
        double knockbackStrength = plugin.getConfig().getDouble("agility-fragment.wing-burst.knockback-strength");
        double damage = plugin.getConfig().getDouble("agility-fragment.wing-burst.damage");

        Location playerLoc = player.getLocation();

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity living = (LivingEntity) entity;

                // Calculate knockback direction
                Vector knockback = living.getLocation().toVector()
                    .subtract(playerLoc.toVector())
                    .normalize()
                    .multiply(knockbackStrength)
                    .setY(0.5); // Add upward component

                living.setVelocity(knockback);
                living.damage(damage, player);
            }
        }

        // Effects
        playSound(player, "sounds.knockback");
        spawnParticles(playerLoc, "particles.knockback");

        // Set cooldown
        setCooldown(player, wingBurstCooldowns, "agility-fragment.wing-burst.cooldown");

        // Send success message
        String abilityName = plugin.getConfig().getString("agility-fragment.wing-burst.name");
        sendAbilityUsedMessage(player, abilityName);
    }

    private void createDashTrail(Player player, double distance) {
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = (int) (distance * 2);

            @Override
            public void run() {
                if (ticks >= maxTicks || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                spawnParticles(player.getLocation(), "particles.dash");
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    // Include all helper methods from BurningFragmentAbility
    // (hasPermission, sendNoPermissionMessage, isOnCooldown, etc.)
}
```

### 3.3 Immortal Fragment Abilities

**Class: `ImmortalFragmentAbility.java`**

```java
package [your.package.path].abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ImmortalFragmentAbility implements FragmentAbility, Listener {

    private final ElementalDragon plugin;
    private final Map<UUID, Long> draconicReflexCooldowns;
    private final Map<UUID, Long> essenceRebirthCooldowns;
    private final Set<UUID> activeReflexPlayers;

    public ImmortalFragmentAbility(ElementalDragon plugin) {
        this.plugin = plugin;
        this.draconicReflexCooldowns = new HashMap<>();
        this.essenceRebirthCooldowns = new HashMap<>();
        this.activeReflexPlayers = new HashSet<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void useAbility1(Player player) { // Draconic Reflex (Dodge)
        if (!hasPermission(player, "elementaldragon.use.immortal")) {
            sendNoPermissionMessage(player);
            return;
        }

        if (isOnCooldown(player, draconicReflexCooldowns, "immortal-fragment.draconic-reflex.cooldown")) {
            sendCooldownMessage(player, draconicReflexCooldowns, "immortal-fragment.draconic-reflex.cooldown");
            return;
        }

        // Activate dodge ability
        int duration = plugin.getConfig().getInt("immortal-fragment.draconic-reflex.duration");
        activeReflexPlayers.add(player.getUniqueId());

        // Visual feedback
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.GLOWING,
            duration,
            0,
            false,
            true
        ));

        // Schedule ability end
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            activeReflexPlayers.remove(player.getUniqueId());
        }, duration);

        // Particle effect task
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration || !player.isOnline() || !activeReflexPlayers.contains(player.getUniqueId())) {
                    this.cancel();
                    return;
                }

                if (ticks % plugin.getConfig().getInt("particles.dodge.interval") == 0) {
                    spawnParticles(player.getLocation(), "particles.dodge");
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Effects
        playSound(player, "sounds.dodge");

        // Set cooldown
        setCooldown(player, draconicReflexCooldowns, "immortal-fragment.draconic-reflex.cooldown");

        // Send success message
        String abilityName = plugin.getConfig().getString("immortal-fragment.draconic-reflex.name");
        sendAbilityUsedMessage(player, abilityName);
    }

    @Override
    public void useAbility2(Player player) {
        // Passive ability - handled by death event listener
        player.sendMessage(plugin.getConfig().getString("messages.prefix") +
            "§eEssence Rebirth is a passive ability that activates upon death.");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        // Check if player has Draconic Reflex active
        if (activeReflexPlayers.contains(player.getUniqueId())) {
            double dodgeChance = plugin.getConfig().getDouble("immortal-fragment.draconic-reflex.dodge-chance");

            if (Math.random() < dodgeChance) {
                event.setCancelled(true);
                spawnParticles(player.getLocation(), "particles.dodge");
                playSound(player, "sounds.dodge");
            }
        }

        // Check for Essence Rebirth (Second Life)
        if (player.getHealth() - event.getFinalDamage() <= 0) {
            ItemStack offhand = player.getInventory().getItemInOffHand();

            if (plugin.getFragmentItems().isImmortalFragment(offhand)) {
                if (!isOnCooldown(player, essenceRebirthCooldowns, "immortal-fragment.essence-rebirth.cooldown")) {
                    event.setCancelled(true);
                    activateEssenceRebirth(player);
                }
            }
        }
    }

    private void activateEssenceRebirth(Player player) {
        // Heal player
        double healAmount = plugin.getConfig().getDouble("immortal-fragment.essence-rebirth.heal-amount");
        player.setHealth(healAmount);

        // Restore saturation
        double saturation = plugin.getConfig().getDouble("immortal-fragment.essence-rebirth.saturation");
        player.setSaturation((float) saturation);
        player.setFoodLevel(20);

        // Grant temporary effects
        int effectDuration = plugin.getConfig().getInt("immortal-fragment.essence-rebirth.effect-duration");
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, effectDuration, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, effectDuration, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, effectDuration, 0));

        // Effects
        playSound(player, "sounds.resurrection");
        spawnParticles(player.getLocation(), "particles.resurrection");

        // Set cooldown
        setCooldown(player, essenceRebirthCooldowns, "immortal-fragment.essence-rebirth.cooldown");

        // Send message
        String abilityName = plugin.getConfig().getString("immortal-fragment.essence-rebirth.name");
        player.sendMessage(plugin.getConfig().getString("messages.prefix") +
            "§6" + abilityName + " §ehas saved you from death!");
    }

    // Include all helper methods
}
```

### 3.4 Corrupted Core Abilities

**Class: `CorruptedCoreAbility.java`**

```java
package [your.package.path].abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CorruptedCoreAbility implements FragmentAbility {

    private final ElementalDragon plugin;
    private final Map<UUID, Long> dreadGazeCooldowns;
    private final Map<UUID, Long> lifeDevourer Cooldowns;

    public CorruptedCoreAbility(ElementalDragon plugin) {
        this.plugin = plugin;
        this.dreadGazeCooldowns = new HashMap<>();
        this.lifeDevourerCooldowns = new HashMap<>();
    }

    @Override
    public void useAbility1(Player player) { // Dread Gaze (Freeze)
        if (!hasPermission(player, "elementaldragon.use.corrupted")) {
            sendNoPermissionMessage(player);
            return;
        }

        if (isOnCooldown(player, dreadGazeCooldowns, "corrupted-core.dread-gaze.cooldown")) {
            sendCooldownMessage(player, dreadGazeCooldowns, "corrupted-core.dread-gaze.cooldown");
            return;
        }

        // Freeze nearby entities
        double range = plugin.getConfig().getDouble("corrupted-core.dread-gaze.range");
        int duration = plugin.getConfig().getInt("corrupted-core.dread-gaze.duration");
        int maxTargets = plugin.getConfig().getInt("corrupted-core.dread-gaze.max-targets");

        List<LivingEntity> targets = new ArrayList<>();
        Vector playerDirection = player.getLocation().getDirection();

        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity living = (LivingEntity) entity;

                // Check if entity is in player's line of sight
                Vector toEntity = living.getLocation().toVector()
                    .subtract(player.getLocation().toVector())
                    .normalize();

                double dot = playerDirection.dot(toEntity);
                if (dot > 0.8) { // ~36 degree cone
                    targets.add(living);
                }
            }
        }

        // Sort by distance and take closest maxTargets
        targets.sort(Comparator.comparingDouble(e ->
            e.getLocation().distanceSquared(player.getLocation())));

        int frozenCount = 0;
        for (LivingEntity target : targets) {
            if (frozenCount >= maxTargets) break;

            // Apply freeze effects
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 255));
            target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 250)); // Can't jump
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration / 2, 0));

            spawnParticles(target.getLocation(), "particles.freeze");
            frozenCount++;
        }

        // Effects
        playSound(player, "sounds.freeze");

        // Set cooldown
        setCooldown(player, dreadGazeCooldowns, "corrupted-core.dread-gaze.cooldown");

        // Send success message
        String abilityName = plugin.getConfig().getString("corrupted-core.dread-gaze.name");
        sendAbilityUsedMessage(player, abilityName);
    }

    @Override
    public void useAbility2(Player player) { // Life Devourer (Health Steal)
        if (!hasPermission(player, "elementaldragon.use.corrupted")) {
            sendNoPermissionMessage(player);
            return;
        }

        if (isOnCooldown(player, lifeDevourerCooldowns, "corrupted-core.life-devourer.cooldown")) {
            sendCooldownMessage(player, lifeDevourerCooldowns, "corrupted-core.life-devourer.cooldown");
            return;
        }

        // Find closest entity in range
        double range = plugin.getConfig().getDouble("corrupted-core.life-devourer.range");
        double damage = plugin.getConfig().getDouble("corrupted-core.life-devourer.damage");
        double healPercentage = plugin.getConfig().getDouble("corrupted-core.life-devourer.heal-percentage");

        LivingEntity target = null;
        double closestDistance = range;

        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (entity instanceof LivingEntity && entity != player) {
                double distance = entity.getLocation().distance(player.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    target = (LivingEntity) entity;
                }
            }
        }

        if (target == null) {
            player.sendMessage(plugin.getConfig().getString("messages.prefix") +
                plugin.getConfig().getString("messages.errors.invalid-target"));
            return;
        }

        // Damage target and heal player
        target.damage(damage, player);

        double healAmount = damage * healPercentage;
        double newHealth = Math.min(player.getHealth() + healAmount, player.getMaxHealth());
        player.setHealth(newHealth);

        // Visual effects - create particle beam from target to player
        createParticleBeam(target.getEyeLocation(), player.getEyeLocation());

        // Effects
        playSound(player, "sounds.life-steal");
        playSound(target, "sounds.life-steal");

        // Set cooldown
        setCooldown(player, lifeDevourerCooldowns, "corrupted-core.life-devourer.cooldown");

        // Send success message
        String abilityName = plugin.getConfig().getString("corrupted-core.life-devourer.name");
        sendAbilityUsedMessage(player, abilityName);
    }

    private void createParticleBeam(Location start, Location end) {
        Vector direction = end.toVector().subtract(start.toVector());
        double distance = start.distance(end);
        direction.normalize().multiply(0.2);

        Location current = start.clone();
        for (double i = 0; i < distance; i += 0.2) {
            spawnParticles(current, "particles.life-steal");
            current.add(direction);
        }
    }

    // Include all helper methods
}
```

---

## 4. Event Listeners

### 4.1 Main Ability Trigger Listener

**Class: `AbilityTriggerListener.java`**

```java
package [your.package.path].listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityTriggerListener implements Listener {

    private final ElementalDragon plugin;

    public AbilityTriggerListener(ElementalDragon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        FragmentItems items = plugin.getFragmentItems();

        // Check which fragment is in offhand
        FragmentAbility ability = null;

        if (items.isBurningFragment(offhand)) {
            ability = plugin.getBurningFragmentAbility();
        } else if (items.isAgilityFragment(offhand)) {
            ability = plugin.getAgilityFragmentAbility();
        } else if (items.isImmortalFragment(offhand)) {
            ability = plugin.getImmortalFragmentAbility();
        } else if (items.isCorruptedCore(offhand)) {
            ability = plugin.getCorruptedCoreAbility();
        } else if (offhand.getType() == Material.DRAGON_EGG) {
            // Handle Dragon Egg Lightning (original feature)
            if (event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                plugin.getDragonEggLightning().useLightning(player);
                event.setCancelled(true);
            }
            return;
        } else {
            return; // No fragment in offhand
        }

        // Trigger appropriate ability based on action
        if (event.getAction() == Action.RIGHT_CLICK_AIR ||
            event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (player.isSneaking()) {
                ability.useAbility2(player); // Shift + Right-Click
            } else {
                ability.useAbility1(player); // Right-Click
            }

            event.setCancelled(true);
        }
    }
}
```

### 4.2 Fireball Impact Listener

**Class: `FireballImpactListener.java`**

```java
package [your.package.path].listeners;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

public class FireballImpactListener implements Listener {

    private final ElementalDragon plugin;

    public FireballImpactListener(ElementalDragon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Fireball)) return;

        Fireball fireball = (Fireball) event.getEntity();

        // Check if this is a custom Dragon's Wrath fireball
        NamespacedKey damageKey = new NamespacedKey(plugin, "custom_damage");
        NamespacedKey fireKey = new NamespacedKey(plugin, "fire_duration");

        if (!fireball.getPersistentDataContainer().has(damageKey, PersistentDataType.DOUBLE)) {
            return; // Not a custom fireball
        }

        double customDamage = fireball.getPersistentDataContainer()
            .get(damageKey, PersistentDataType.DOUBLE);
        int fireDuration = fireball.getPersistentDataContainer()
            .get(fireKey, PersistentDataType.INTEGER);

        // Apply custom effects to hit entity
        if (event.getHitEntity() instanceof LivingEntity) {
            LivingEntity hit = (LivingEntity) event.getHitEntity();
            hit.setFireTicks(fireDuration);
        }
    }

    @EventHandler
    public void onFireballDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Fireball)) return;

        Fireball fireball = (Fireball) event.getDamager();
        NamespacedKey damageKey = new NamespacedKey(plugin, "custom_damage");

        if (fireball.getPersistentDataContainer().has(damageKey, PersistentDataType.DOUBLE)) {
            double customDamage = fireball.getPersistentDataContainer()
                .get(damageKey, PersistentDataType.DOUBLE);

            event.setDamage(customDamage);
        }
    }
}
```

---

## 5. Commands

### 5.1 Main Command Handler

**Class: `ElementalDragonCommand.java`**

```java
package [your.package.path].commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElementalDragonCommand implements CommandExecutor, TabCompleter {

    private final ElementalDragon plugin;

    public ElementalDragonCommand(ElementalDragon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                return handleReload(sender);

            case "give":
                return handleGive(sender, args);

            case "help":
                sendHelp(sender);
                return true;

            default:
                sender.sendMessage(plugin.getConfig().getString("messages.prefix") +
                    "§cUnknown subcommand. Use /ed help for help.");
                return true;
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("elementaldragon.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") +
                plugin.getConfig().getString("messages.errors.no-permission"));
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(plugin.getConfig().getString("messages.prefix") +
            plugin.getConfig().getString("messages.info.plugin-reloaded"));
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("elementaldragon.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") +
                plugin.getConfig().getString("messages.errors.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") +
                "§cUsage: /ed give <player> <fragment>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") +
                "§cPlayer not found.");
            return true;
        }

        ItemStack item = null;
        String fragmentName = args[2].toLowerCase();

        switch (fragmentName) {
            case "burning":
                item = plugin.getFragmentItems().createBurningFragment();
                break;
            case "agility":
                item = plugin.getFragmentItems().createAgilityFragment();
                break;
            case "immortal":
                item = plugin.getFragmentItems().createImmortalFragment();
                break;
            case "corrupted":
                item = plugin.getFragmentItems().createCorruptedCore();
                break;
            case "heavy_core":
            case "heavycore":
                item = plugin.getHeavyCoreItem();
                break;
            default:
                sender.sendMessage(plugin.getConfig().getString("messages.prefix") +
                    "§cInvalid fragment type. Available: burning, agility, immortal, corrupted, heavy_core");
                return true;
        }

        target.getInventory().addItem(item);
        sender.sendMessage(plugin.getConfig().getString("messages.prefix") +
            "§aGave " + fragmentName + " fragment to " + target.getName());
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfig().getString("messages.info.help-header"));
        sender.sendMessage(plugin.getConfig().getString("messages.info.help-reload"));
        sender.sendMessage(plugin.getConfig().getString("messages.info.help-give"));
        sender.sendMessage(plugin.getConfig().getString("messages.info.help-lore"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("reload", "give", "help"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            // Return online player names
            plugin.getServer().getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(Arrays.asList("burning", "agility", "immortal", "corrupted", "heavy_core"));
        }

        return completions;
    }
}
```

### 5.2 Lore Command

**Class: `LoreCommand.java`**

```java
package [your.package.path].commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoreCommand implements CommandExecutor {

    private final ElementalDragon plugin;

    public LoreCommand(ElementalDragon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!plugin.getLoreBookManager().canReceiveLoreBook(player)) {
            player.sendMessage(plugin.getConfig().getString("messages.prefix") +
                plugin.getConfig().getString("messages.errors.no-permission"));
            return true;
        }

        plugin.getLoreBookManager().giveLoreBook(player);
        return true;
    }
}
```

---

## 6. Advancement System

### 6.1 Custom Advancement JSON

Create file: `src/main/resources/data/elementaldragon/advancements/legacy_of_dragons.json`

```json
{
  "display": {
    "icon": {
      "item": "minecraft:nether_star"
    },
    "title": {
      "text": "Legacy of Dragons",
      "color": "dark_purple",
      "bold": true
    },
    "description": {
      "text": "Collect all five fragments of the Draconis Aeterna",
      "color": "gray"
    },
    "frame": "challenge",
    "show_toast": true,
    "announce_to_chat": true,
    "hidden": false,
    "background": "minecraft:textures/block/end_stone.png"
  },
  "parent": "minecraft:end/root",
  "criteria": {
    "has_all_fragments": {
      "trigger": "minecraft:inventory_changed",
      "conditions": {
        "items": [
          {
            "items": ["minecraft:dragon_egg"]
          },
          {
            "items": ["minecraft:fire_charge"],
            "nbt": "{display:{Name:'{\"text\":\"§6Burning Fragment\"}'}} "
          },
          {
            "items": ["minecraft:phantom_membrane"],
            "nbt": "{display:{Name:'{\"text\":\"§bAgility Fragment\"}'}}"
          },
          {
            "items": ["minecraft:totem_of_undying"],
            "nbt": "{display:{Name:'{\"text\":\"§eImmortal Fragment\"}'}}"
          },
          {
            "items": ["minecraft:nether_star"],
            "nbt": "{display:{Name:'{\"text\":\"§5Corrupted Core\"}'}}"
          }
        ]
      }
    }
  },
  "rewards": {
    "experience": 1000
  }
}
```

---

## 7. Test Cases

### 7.1 Unit Testing Framework

**Class: `FragmentAbilityTest.java`**

```java
package [your.package.path].tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class FragmentAbilityTest {

    private ElementalDragon plugin;
    private Player mockPlayer;

    @BeforeEach
    public void setup() {
        // Setup mock plugin and player
    }

    @Test
    public void testBurningFragmentCooldown() {
        // Test that cooldown is properly enforced
    }

    @Test
    public void testAgilityFragmentDashDistance() {
        // Test that dash moves player correct distance
    }

    @Test
    public void testImmortalFragmentSecondLife() {
        // Test that second life activates on fatal damage
    }

    @Test
    public void testCorruptedCoreFreeze() {
        // Test that freeze applies correct effects
    }
}
```

### 7.2 Integration Test Cases

**Test Case 1: Fragment Crafting**
```
Objective: Verify all fragments can be crafted with correct recipes
Steps:
1. Craft Heavy Core
2. Craft each fragment using Heavy Core
3. Verify correct lore is applied
4. Verify discovery message is shown
5. Verify crafting permission is checked

Expected Results:
- All fragments craftable
- Correct lore displayed
- Discovery titles shown
- Permissions respected
```

**Test Case 2: Dragon Egg Lightning**
```
Objective: Verify original functionality preserved
Steps:
1. Place Dragon Egg in offhand
2. Right-click while looking at sky
3. Verify lightning strikes
4. Verify damage is applied
5. Verify cooldown works

Expected Results:
- Lightning spawns at cursor location
- Entities take configured damage
- Cooldown prevents spam
- Particles and sounds play
```

**Test Case 3: Burning Fragment - Dragon's Wrath**
```
Objective: Test fireball ability
Steps:
1. Place Burning Fragment in offhand
2. Right-click to launch fireball
3. Verify fireball trajectory
4. Let fireball hit block/entity
5. Verify explosion and fire

Expected Results:
- Fireball launches in correct direction
- Custom damage applied on hit
- Fire duration matches config
- Explosion power correct
- Cooldown enforced
```

**Test Case 4: Burning Fragment - Infernal Dominion**
```
Objective: Test area burn ability
Steps:
1. Place Burning Fragment in offhand
2. Shift + Right-click to activate
3. Verify burn area appears
4. Place entities in area
5. Verify damage over time

Expected Results:
- Burn area has correct radius
- Damage ticks at correct interval
- Duration matches config
- Particles spawn continuously
- Cooldown enforced
```

**Test Case 5: Agility Fragment - Draconic Surge**
```
Objective: Test dash ability
Steps:
1. Place Agility Fragment in offhand
2. Right-click to dash
3. Measure distance traveled
4. Verify invulnerability during dash
5. Verify particle trail

Expected Results:
- Dash distance matches config
- Player invulnerable during dash
- Particle trail visible
- No damage taken during dash
- Cooldown enforced
```

**Test Case 6: Agility Fragment - Wing Burst**
```
Objective: Test knockback ability
Steps:
1. Place Agility Fragment in offhand
2. Surround with test entities
3. Shift + Right-click to activate
4. Verify all entities knocked back
5. Verify damage applied

Expected Results:
- All entities within radius knocked back
- Knockback strength correct
- Damage applied to all entities
- Upward component in knockback
- Cooldown enforced
```

**Test Case 7: Immortal Fragment - Draconic Reflex**
```
Objective: Test dodge ability
Steps:
1. Place Immortal Fragment in offhand
2. Right-click to activate
3. Take damage from various sources
4. Count successful dodges
5. Verify duration

Expected Results:
- Dodge chance matches config (75%)
- Duration correct (3 seconds)
- Particles display during active period
- Cooldown enforced
```

**Test Case 8: Immortal Fragment - Essence Rebirth**
```
Objective: Test resurrection ability
Steps:
1. Place Immortal Fragment in offhand
2. Take fatal damage
3. Verify resurrection occurs
4. Check health and effects
5. Verify cooldown

Expected Results:
- Player doesn't die
- Health restored to full
- Saturation and food restored
- Potion effects applied
- 5 minute cooldown active
```

**Test Case 9: Corrupted Core - Dread Gaze**
```
Objective: Test freeze ability
Steps:
1. Place Corrupted Core in offhand
2. Face group of entities
3. Right-click to activate
4. Verify freeze effects
5. Check max targets limit

Expected Results:
- Entities in cone frozen
- Slowness effect level 255
- Jump prevention active
- Max 5 targets frozen
- Cooldown enforced
```

**Test Case 10: Corrupted Core - Life Devourer**
```
Objective: Test life steal ability
Steps:
1. Place Corrupted Core in offhand
2. Reduce own health
3. Shift + Right-click near entity
4. Verify damage and healing
5. Check particle beam

Expected Results:
- Target takes damage
- Player heals for 100% of damage
- Particle beam connects entities
- Sound effects play
- Cooldown enforced
```

**Test Case 11: All Fragments Discovery**
```
Objective: Test achievement for collecting all fragments
Steps:
1. Craft all four fragments
2. Obtain Dragon Egg
3. Have all in inventory simultaneously
4. Verify achievement triggers

Expected Results:
- Title/subtitle displayed
- Dragon breath particles spawn
- Ender dragon growl sound plays
- Advancement granted
- 1000 XP rewarded
```

**Test Case 12: Lore Book**
```
Objective: Test lore book system
Steps:
1. Craft lore book (book + dragon egg)
2. Verify dragon egg returned
3. Read all 12 pages
4. Verify content matches spec
5. Test /edlore command

Expected Results:
- Book crafted successfully
- Dragon egg not consumed
- All 12 pages present
- Content formatted correctly
- Command gives book
```

**Test Case 13: Permission System**
```
Objective: Verify all permissions work
Steps:
1. Test each permission node
2. Verify denial messages
3. Test wildcard permissions
4. Test permission inheritance

Expected Results:
- All permissions functional
- Proper denial messages shown
- Wildcards grant all sub-permissions
- Inheritance works correctly
```

**Test Case 14: Config Reload**
```
Objective: Test configuration reloading
Steps:
1. Start server with default config
2. Modify config values
3. Execute /ed reload
4. Test modified values
5. Verify no errors

Expected Results:
- Config reloads without errors
- New values take effect
- No need for server restart
- Confirmation message shown
```

**Test Case 15: Cooldown Display**
```
Objective: Test cooldown feedback system
Steps:
1. Use any ability
2. Try to use again during cooldown
3. Verify cooldown message
4. Wait for cooldown to end
5. Verify ready notification

Expected Results:
- Cooldown message shows remaining time
- Ready sound plays when available
- Cooldown tracking accurate
- Message format correct
```

**Test Case 16: Particle Performance**
```
Objective: Test particle system under load
Steps:
1. Activate multiple abilities simultaneously
2. Monitor TPS
3. Test different particle density settings
4. Verify async rendering

Expected Results:
- No significant TPS drop
- Particles render smoothly
- Density settings work
- Async rendering functional
```

**Test Case 17: Multi-Player Testing**
```
Objective: Test abilities with multiple players
Steps:
1. Have 5+ players with different fragments
2. Use abilities simultaneously
3. Verify no interference
4. Check cooldown independence

Expected Results:
- Abilities don't interfere
- Each player has own cooldowns
- Effects render for all players
- No cross-contamination
```

**Test Case 18: Edge Cases**
```
Objective: Test unusual scenarios
Steps:
1. Use abilities while dead
2. Use abilities in spectator mode
3. Switch fragments during cooldown
4. Drop fragments during ability use
5. Relog during active ability

Expected Results:
- Dead players can't use abilities
- Spectators can't use abilities
- Switching fragments cancels abilities
- Abilities cancel when fragment dropped
- Abilities cancel on logout
```

**Test Case 19: Compatibility**
```
Objective: Test with other plugins
Steps:
1. Test with WorldGuard regions
2. Test with PvP toggle plugins
3. Test with economy plugins
4. Test with permissions plugins

Expected Results:
- Respects region protections
- Respects PvP settings
- No economy conflicts
- Permission integration works
```

**Test Case 20: Data Persistence**
```
Objective: Test data saving/loading
Steps:
1. Earn discoveries
2. Restart server
3. Verify discoveries retained
4. Test data corruption handling

Expected Results:
- Discoveries persist across restarts
- Data saves correctly
- Corruption handled gracefully
- No data loss
```

### 7.3 Performance Benchmarks

**Benchmark 1: Ability Execution Time**
```
Target: < 5ms per ability activation
Test: Measure time from trigger to effect
```

**Benchmark 2: Particle Rendering**
```
Target: No TPS drop below 19.5
Test: Spawn maximum particles with 20 players
```

**Benchmark 3: Cooldown Tracking**
```
Target: < 1ms per cooldown check
Test: Check 100 cooldowns simultaneously
```

**Benchmark 4: Discovery System**
```
Target: < 10ms for all-fragments check
Test: Verify 1000 players simultaneously
```

---

## 8. Plugin Architecture

### 8.1 Main Plugin Class

**Class: `ElementalDragon.java`**

```java
package [your.package.path];

import org.bukkit.plugin.java.JavaPlugin;

public class ElementalDragon extends JavaPlugin {

    // Managers
    private LoreBookManager loreBookManager;
    private DiscoveryManager discoveryManager;
    private FragmentItems fragmentItems;

    // Abilities
    private BurningFragmentAbility burningFragmentAbility;
    private AgilityFragmentAbility agilityFragmentAbility;
    private ImmortalFragmentAbility immortalFragmentAbility;
    private CorruptedCoreAbility corruptedCoreAbility;
    private DragonEggLightning dragonEggLightning;

    // Crafting
    private CraftingManager craftingManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize managers
        loreBookManager = new LoreBookManager(this);
        discoveryManager = new DiscoveryManager(this);
        fragmentItems = new FragmentItems(this);

        // Initialize abilities
        burningFragmentAbility = new BurningFragmentAbility(this);
        agilityFragmentAbility = new AgilityFragmentAbility(this);
        immortalFragmentAbility = new ImmortalFragmentAbility(this);
        corruptedCoreAbility = new CorruptedCoreAbility(this);
        dragonEggLightning = new DragonEggLightning(this);

        // Initialize crafting
        craftingManager = new CraftingManager(this);
        craftingManager.registerAllRecipes();

        // Register listeners
        registerListeners();

        // Register commands
        registerCommands();

        // Load data
        discoveryManager.loadData();

        // Startup message
        getLogger().info("Elemental Dragon has been enabled!");
        getLogger().info("The legacy of the Draconis Aeterna awakens...");
    }

    @Override
    public void onDisable() {
        // Save data
        discoveryManager.saveData();

        // Unregister recipes
        craftingManager.unregisterAllRecipes();

        getLogger().info("Elemental Dragon has been disabled.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AbilityTriggerListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);
        getServer().getPluginManager().registerEvents(new FireballImpactListener(this), this);
        // ImmortalFragmentAbility registers itself
    }

    private void registerCommands() {
        getCommand("elementaldragon").setExecutor(new ElementalDragonCommand(this));
        getCommand("elementaldragon").setTabCompleter(new ElementalDragonCommand(this));
        getCommand("edlore").setExecutor(new LoreCommand(this));
    }

    // Getters for all components
    public LoreBookManager getLoreBookManager() { return loreBookManager; }
    public DiscoveryManager getDiscoveryManager() { return discoveryManager; }
    public FragmentItems getFragmentItems() { return fragmentItems; }
    public BurningFragmentAbility getBurningFragmentAbility() { return burningFragmentAbility; }
    public AgilityFragmentAbility getAgilityFragmentAbility() { return agilityFragmentAbility; }
    public ImmortalFragmentAbility getImmortalFragmentAbility() { return immortalFragmentAbility; }
    public CorruptedCoreAbility getCorruptedCoreAbility() { return corruptedCoreAbility; }
    public DragonEggLightning getDragonEggLightning() { return dragonEggLightning; }
}
```

---

## 9. Documentation

### 9.1 README.md

```markdown
# Elemental Dragon

**Reclaim the power of the ancient Draconis Aeterna**

## Description

Elemental Dragon is a comprehensive Minecraft plugin that introduces the lore and power of an ancient dragon race. The Draconis Aeterna once ruled the darkest realms with five primordial forces: Storm, Flame, Wind, Immortality, and Corruption. Their power was shattered and bound into fragments scattered across the world. Discover their legacy, craft their relics, and become the heir to their dominion.

## Features

### Five Powerful Fragments

1. **Dragon Egg (Heaven's Light)** - Summon lightning from the heavens
2. **Burning Fragment (Flame)** - Unleash draconic fire breath
3. **Agility Fragment (Wind)** - Move with the speed of soaring dragons
4. **Immortal Fragment (Eternal Cycle)** - Defy death itself
5. **Corrupted Core (Consuming Darkness)** - Wield power beyond mortal comprehension

### Immersive Lore System

- **Chronicle of the Fallen Dragons** - A craftable lore book telling the complete story
- **Discovery System** - Special messages and achievements when crafting fragments
- **The Prophecy** - Hints at a greater purpose for those who collect all fragments
- **Custom Advancement** - "Legacy of Dragons" for collecting all fragments

### Balanced Gameplay

- Configurable cooldowns for all abilities
- Permission-based access control
- Offhand-only activation
- Fair damage values
- Proper PvP/PvE balance

## Installation

1. Download the latest release
2. Place `ElementalDragon.jar` in your server's `plugins` folder
3. Restart your server
4. Configure `plugins/ElementalDragon/config.yml` as desired
5. Use `/ed reload` to apply changes

## Commands

- `/elementaldragon` (or `/ed`) - Main plugin command
  - `/ed reload` - Reload the configuration
  - `/ed give <player> <fragment>` - Give a fragment to a player
  - `/ed help` - Show help message
- `/edlore` - Receive the Chronicle of the Fallen Dragons

## Permissions

- `elementaldragon.admin` - Access to all admin commands (default: op)
- `elementaldragon.lore` - Ability to receive the lore book (default: true)
- `elementaldragon.use.lightning` - Use Dragon Egg Lightning (default: true)
- `elementaldragon.use.burning` - Use Burning Fragment (default: true)
- `elementaldragon.use.agility` - Use Agility Fragment (default: true)
- `elementaldragon.use.immortal` - Use Immortal Fragment (default: true)
- `elementaldragon.use.corrupted` - Use Corrupted Core (default: true)
- `elementaldragon.craft.*` - Craft all fragments (default: true)

## Crafting Recipes

### Heavy Core
```
[Netherite Ingot] [Crying Obsidian] [Netherite Ingot]
[Crying Obsidian] [Netherite Block]  [Crying Obsidian]
[Netherite Ingot] [Crying Obsidian] [Netherite Ingot]
```

### Burning Fragment
```
[Fire Charge] [Blaze Rod]    [Fire Charge]
[Blaze Rod]   [Heavy Core]   [Blaze Rod]
[Fire Charge] [Blaze Rod]    [Fire Charge]
```

### Agility Fragment
```
[Phantom Membrane] [Elytra]      [Phantom Membrane]
[Elytra]           [Heavy Core]  [Elytra]
[Phantom Membrane] [Elytra]      [Phantom Membrane]
```

### Immortal Fragment
```
[Totem] [Enchanted Golden Apple] [Totem]
[Ench. Golden Apple] [Heavy Core] [Ench. Golden Apple]
[Totem] [Enchanted Golden Apple] [Totem]
```

### Corrupted Core
```
[Wither Skull] [Nether Star]  [Wither Skull]
[Nether Star]  [Heavy Core]   [Nether Star]
[Wither Skull] [Nether Star]  [Wither Skull]
```

### Chronicle of the Fallen Dragons
```
Book + Dragon Egg (egg not consumed)
```

## Configuration

The plugin is highly configurable. All abilities, cooldowns, damage values, particle effects, and messages can be customized in `config.yml`.

## Support

For issues, suggestions, or questions:
- GitHub Issues: [link]
- Discord: [link]
- Wiki: [link]

## License

[Your chosen license]

## Credits

- Original Dragon Egg Lightning concept
- Lore and expansion by [Your Name]
```

### 9.2 CHANGELOG.md

```markdown
# Changelog

## [0.2.0] - 2026-01-06

### Added
- Complete plugin rebrand to "Elemental Dragon"
- Four new powerful fragments:
  - Burning Fragment (Fire abilities)
  - Agility Fragment (Movement abilities)
  - Immortal Fragment (Survival abilities)
  - Corrupted Core (Dark abilities)
- Comprehensive lore system
  - Chronicle of the Fallen Dragons (lore book)
  - Discovery messages for each fragment
  - The Prophecy
- Achievement system
  - "Legacy of Dragons" advancement
  - All fragments discovery tracking
- Heavy Core crafting item
- Configurable recipes for all fragments
- Particle effects for all abilities
- Sound effects for all abilities
- Permission system for fine-grained control
- Commands:
  - `/ed reload` - Reload configuration
  - `/ed give` - Give fragments to players
  - `/edlore` - Receive lore book
- Complete configuration system
- Performance optimizations
  - Async particle rendering
  - Cooldown caching
  - Player data persistence

### Changed
- Plugin name from "Dragon Egg Lightning" to "Elemental Dragon"
- Dragon Egg ability now called "Heaven's Light"
- Enhanced particle and sound effects
- Improved cooldown system
- Better error messages

### Fixed
- Various bug fixes from original plugin
- Performance issues with multiple players
- Cooldown synchronization

## [1.0.0] - [Original Release Date]

### Added
- Initial Dragon Egg Lightning functionality
```

---

## 10. Deployment Checklist

### Pre-Release Testing

- [ ] All 20 test cases pass
- [ ] Performance benchmarks met
- [ ] Multi-player testing completed
- [ ] Memory leak testing completed
- [ ] Config validation working
- [ ] All permissions functional
- [ ] All commands functional
- [ ] Lore book renders correctly
- [ ] Advancements grant properly
- [ ] Data persistence working

### Documentation

- [ ] README.md complete
- [ ] CHANGELOG.md updated
- [ ] Javadoc generated
- [ ] Config comments clear
- [ ] Wiki pages written
- [ ] Video demonstration recorded

### Code Quality

- [ ] All code commented
- [ ] No TODO comments remaining
- [ ] No debug statements
- [ ] Proper error handling
- [ ] Logging appropriate
- [ ] Code formatted consistently

### Distribution

- [ ] plugin.yml correct
- [ ] config.yml has defaults
- [ ] Advancement JSON included
- [ ] Resources bundled
- [ ] Build tested
- [ ] Version numbers updated

### Server Compatibility

- [ ] Tested on Paper 1.21.8
- [ ] Tested with common plugins:
  - [ ] WorldGuard
  - [ ] LuckPerms
  - [ ] EssentialsX
  - [ ] Vault

---

## 11. Future Expansion Ideas

### Potential Features for 3.0.0

1. **Dragon Armor Set**
   - Craftable armor pieces
   - Set bonuses when wearing full set
   - Each piece grants minor fragment ability

2. **Fragment Upgrading System**
   - Enhanced fragments with stronger abilities
   - Requires rare materials
   - Visual changes to upgraded fragments

3. **Dragon Boss Fight**
   - Special boss that drops fragment materials
   - Multi-phase encounter
   - Requires all fragments to summon

4. **Fragment Synergies**
   - Bonus effects when using multiple fragments
   - Combination abilities
   - Strategic fragment choices

5. **Custom Enchantments**
   - Dragon-themed enchantments
   - Apply to fragment items
   - Unique effects

6. **Ritual System**
   - Multi-step crafting process
   - Requires specific conditions
   - Spectacular visual effects

7. **Lore Quests**
   - Multi-part quest line
   - Rewards fragments or materials
   - Teaches player about lore

8. **PvP Arena Mode**
   - Balanced PvP with fragment powers
   - Special arena with buffs
   - Leaderboards

---

## Conclusion

This implementation proposal provides a complete roadmap for transforming "Dragon Egg Lightning" into "Elemental Dragon" - a comprehensive DLC-style experience for Minecraft. The proposal maintains backward compatibility with the original plugin while adding significant new content, lore, and gameplay mechanics.

All features are designed to create a cohesive narrative experience while maintaining balanced, fun gameplay. The extensive testing suite ensures stability, and the comprehensive configuration system allows server owners to customize the experience to their needs.

The modular architecture allows for easy future expansion, and the detailed documentation ensures both players and developers can understand and work with the system effectively.