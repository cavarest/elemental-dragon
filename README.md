# ⚡ Elemental Dragon Plugin

<div align="center">

![Dragon Egg Lightning Banner](https://img.shields.io/badge/Minecraft-Paper%201.21.8-blue?logo=minecraft&style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21+-ED8B00?logo=openjdk&logoColor=white&style=for-the-badge)
<!-- Version badge: Keep in sync with gradle.properties project.version -->
![Version](https://img.shields.io/badge/Version-1.3.6-green?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)

**Transform your server with elemental powers from ancient dragon fragments!**

[![Download Latest Release](https://img.shields.io/badge/Download-Latest%20Release-ff6b6b?style=for-the-badge&logo=github)](https://github.com/cavarest/elemental-dragon/releases/latest)
[![GitHub Stars](https://img.shields.io/github/stars/cavarest/elemental-dragon?style=social)](https://github.com/cavarest/elemental-dragon)
[![GitHub Forks](https://img.shields.io/github/forks/cavarest/elemental-dragon/fork?style=social)](https://github.com/cavarest/elemental-dragon/fork)

</div>

## 🌟 Quick Start (5 Minutes)

### **Step 1: Install Plugin**
```bash
# Download the latest JAR from releases
# Place in your Paper server's plugins directory
# Note: Version in gradle.properties is the single source of truth
cp ElementalDragon-1.3.6.jar /path/to/your/paper-server/plugins/

# Restart your Paper 1.21.8+ server
java -Xms2G -Xmx2G -jar paper-1.21.8-latest.jar nogui
```

### **Step 2: Use Lightning Ability**

```bash
# Give yourself a Dragon Egg
/give @p minecraft:dragon_egg

# Move to offhand (press F key)

# Strike with lightning!
/lightning 1

# Watch the magic happen! ⚡
```

### **Step 3: Get Your First Fragment**
```bash
# Operators can give ingredients to players to craft fragments
/ed give <player> ingredients fire

# Operators can give fragments to players
/ed give <player> equipment fire

# Or show the recipe to craft them (requires Heavy Core)
/craft heavy_core
/craft fire
```

### **Step 4: Use Fragment Abilities**
```bash
# Once you have a fragment, you can equip it by:
# a) hold on main hand and "right-click" to equip
# b) or use command:
/fire equip

# Use abilities
/fire 1    # Dragon's Wrath (homing fireball, 40s cooldown)
/fire 2    # Infernal Dominion (fire ring, 60s cooldown)

# Check status
/fire status

# Get help
/fire help
```

---

## 📖 Command Reference

### **🎮 Player Commands**

Players can use these commands to manage and activate their fragments.

#### **Fragment Commands**
```bash
# Burning Fragment (Fire Element)
/fire equip         # Equip Burning Fragment
/fire 1            # Dragon's Wrath (fireball attack, 40s cooldown)
/fire 2            # Infernal Dominion (area fire, 60s cooldown)
/fire status       # Show fragment status and cooldowns
/fire help         # Show available commands

# Agility Fragment (Wind Element)
/agile equip       # Equip Agility Fragment
/agile 1           # Draconic Surge (20-block dash, 30s cooldown)
/agile 2           # Wing Burst (push entities 8-block radius, 45s cooldown)
/agile status      # Show fragment status and cooldowns
/agile help        # Show available commands

# Immortal Fragment (Earth Element)
/immortal equip    # Equip Immortal Fragment
/immortal 1        # Draconic Reflex (20% dodge chance, 2min cooldown)
/immortal 2        # Essence Rebirth (30-sec death protection window, 8min cooldown)
/immortal status   # Show fragment status and cooldowns
/immortal help     # Show available commands

# Corrupted Core (Void Element)
/corrupt equip     # Equip Corrupted Core
/corrupt 1         # Dread Gaze (complete freeze on next hit, 3min cooldown)
/corrupt 2         # Life Devourer (50% lifesteal, 2min cooldown)
/corrupt status    # Show fragment status and cooldowns
/corrupt help      # Show available commands
```

#### **Lightning Command (Legacy)**
```bash
/lightning 1       # Core lightning ability (60s cooldown, 3-strike attack)
```

**Lightning Ability Details**:
- **Strikes**: 3 purple lightning bolts, 0.5 seconds apart
- **Damage**: 4.0 (2 hearts) per strike - bypasses armor
- **Total Damage**: 12.0 (6 hearts) if all strikes hit
- **Targeting**: Auto-targets closest entity in viewing cone (50 block range)
- **Intelligent Switching**: If target dies, moves to next closest target
- **Requirement**: Dragon Egg must be in offhand

#### **Utility Commands**
```bash
/craft <recipe>    # View crafting recipes
/chronicle <cmd>   # Access lore and achievements
/withdrawability   # Withdraw equipped fragment abilities
```

**Fragment Ability Management**:

The `/withdrawability` command removes your fragment's abilities while keeping the fragment item:
- Deactivates all passive bonuses (Fire Resistance, Speed, etc.)
- Cancels active abilities (Life Devourer, Draconic Reflex, etc.)
- Clears active states (including READY TO STRIKE for Dread Gaze)
- Fragment remains in your inventory for quick re-equip

**Using `/clear` (Vanilla Minecraft Command)**:

When you use the vanilla `/clear` command to remove all inventory items:
- All fragment abilities are unequipped
- All passive bonuses are removed
- All active abilities are canceled
- **Cooldowns are preserved** (spam prevention - see below)

**Enhanced `/craft` Command Features**:

The `/craft` command dynamically generates recipe displays from the actual crafting system (single source of truth):

```bash
# View all available recipes
/craft heavy_core          # Show Heavy Core crafting recipe
/craft fire                # Show Burning Fragment recipe
/craft agile               # Show Agility Fragment recipe
/craft immortal            # Show Immortal Fragment recipe
/craft corrupt             # Show Corrupted Core recipe
```

**Architecture Benefits**:
- ✅ **Auto-generated displays**: Recipe text generated from actual `CraftingManager` recipes
- ✅ **Always accurate**: Single source ensures displays match reality
- ✅ **DRY principle**: Zero duplicate recipe definitions
- ✅ **Easy maintenance**: Recipe changes only need one update location

### **👑 Operator Commands**

Operators have access to administrative functions for managing fragments, cooldowns, and player abilities.

#### **Give Items**
```bash
# Give crafting ingredients
/ed give <player> ingredients <element>
/ed give @p ingredients fire      # Give materials to craft Burning Fragment
/ed give @p ingredients agile     # Give materials to craft Agility Fragment

# Give crafted equipment
/ed give <player> equipment <element>
/ed give @p equipment fire        # Give Burning Fragment directly
/ed give @a equipment immortal    # Give Immortal Fragment to all players
```

**Supported Elements**: `fire`, `agile`, `immortal`, `corrupt`

#### **Player Information**
```bash
# View individual player info
/ed info player <player>
/ed info player @p              # Current player
/ed info player Steve           # Specific player

# List all players
/ed info list                   # Shows all online players with fragment status
```

#### **Cooldown Management**
```bash
# Set individual player cooldown
/ed setcooldown <player> <element> <ability> <seconds>
/ed setcooldown @p fire 1 5               # Fire ability 1: 5s cooldown
/ed setcooldown Steve agile 2 30          # Steve's agile ability 2: 30s

# Clear player cooldowns
/ed clearcooldown <player> [element]
/ed clearcooldown @p                      # Clear all cooldowns
/ed clearcooldown @p fire                 # Clear fire cooldowns only

# Get player cooldowns
/ed getcooldown <player>
/ed getcooldown @p                        # View current player's cooldowns
/ed getcooldown Steve                     # View Steve's cooldowns
```

**Elements**: `lightning`, `fire`, `agile`, `immortal`, `corrupt`
**Abilities**: `1` or `2` (each element has 2 abilities)

#### **Global Cooldown Configuration**
```bash
# Set default cooldown for an ability (affects all players)
/ed setglobalcooldown <element> <ability> <seconds>
/ed setglobalcooldown fire 1 10           # Fire ability 1: 10s default
/ed setglobalcooldown agile 2 60          # Agile ability 2: 60s default

# Disable cooldowns (clears all active cooldowns)
/ed setglobalcooldown fire 1 0            # Disables fire ability 1 cooldown
/ed setglobalcooldown lightning 1 0       # Disables lightning cooldown

# View all global cooldown settings
/ed getglobalcooldown
```

**How Global Cooldowns Work**:
1. Each ability has a default cooldown (e.g., Fire ability 1 = 40s)
2. Operators can override defaults with `/ed setglobalcooldown`
3. When players use abilities, the global cooldown is applied
4. Individual player cooldowns can be managed with `/ed setcooldown`

**Cooldown Adjustment Mechanics**:

When you change a global cooldown, the system intelligently adjusts active player cooldowns using the **MIN formula**: `min(current_remaining, new_max)`

- **Increasing cooldown**: Players with time remaining get capped at new maximum (fair to all)
  - Example: Player has 30s left, you set 20s → Player gets 20s (capped fairly)
  - Example: Player has 10s left, you set 20s → Player keeps 10s (no penalty)

- **Decreasing cooldown**: Uses same MIN formula for consistency
  - Example: Player has 60s left, you set 30s → Player gets 30s (capped)
  - Example: Player has 20s left, you set 30s → Player keeps 20s

- **Disabling cooldown (0 seconds)**: **Clears ALL active cooldowns** for that ability
  - Example: `/ed setglobalcooldown fire 1 0` → All players can use fire ability 1 immediately
  - Useful for: Events, testing, emergency cooldown resets

**Real-World Examples**:
```bash
# Speed up cooldowns during server event
/ed setglobalcooldown fire 1 10          # Faster fire attacks for everyone

# Restore normal cooldowns after event (players capped fairly)
/ed setglobalcooldown fire 1 40          # Players with < 40s keep their time

# Emergency cooldown clear for server issues
/ed setglobalcooldown lightning 1 0      # Instant clear for all players
/ed setglobalcooldown lightning 1 60     # Re-enable with normal cooldown
```

#### **Player References**
All commands support these player selectors:
- `@p` - Nearest player (usually yourself)
- `@s` - Command sender (yourself)
- `@a` - All online players
- `PlayerName` - Specific player by username

---

## 💎 Elemental Fragments System

The Elemental Dragon plugin features **4 complete elemental fragments**, each
with unique abilities, passive bonuses, and visual themes.

### **🔥 Burning Fragment (Fire Element)**
**Material**: Blaze Powder (red/orange visual)
**Theme Color**: Red
**Permission**: `elementaldragon.fragment.burning`

**Abilities**:
- **Dragon's Wrath** (`/fire 1`): Launch homing fireball at target (40s cooldown)
  - Damage: 6.0 (3 hearts) on impact - bypasses armor
  - Explosion radius: 5 blocks
  - Homing duration: 0.5 seconds (10 ticks)
  - Target range: 50 blocks
  - Fire spread: Ignites blocks at explosion location
  - Aliases: `wrath`, `dragons-wrath`

- **Infernal Dominion** (`/fire 2`): Create fire ring around player (60s cooldown)
  - Radius: 10 blocks
  - Duration: 10 seconds (200 ticks)
  - Damage: 1.0 (0.5 hearts) per tick to enemies in range
  - Total damage: ~5 hearts per enemy
  - Aliases: `dominion`, `infernal-dominion`

**Passive Bonus**: Fire Resistance when equipped

### **💨 Agility Fragment (Wind Element)**
**Material**: Phantom Membrane (light/swift visual)
**Theme Color**: Aqua
**Permission**: `elementaldragon.fragment.agility`

**Abilities**:
- **Draconic Surge** (`/agile 1`): Dash forward in facing direction (30s cooldown)
  - Distance: 20 blocks forward
  - Duration: 1 second (20 ticks)
  - Fall protection: 10 seconds after dash
  - Shows cloud particles during dash
  - Aliases: `surge`, `draconic-surge`

- **Wing Burst** (`/agile 2`): Push all entities away from player (45s cooldown)
  - Push radius: 8 blocks
  - Push distance: 20 blocks away from starting position
  - Push duration: 2 seconds (40 ticks)
  - Slow Falling: 10 seconds after push (prevents fall damage)
  - Aliases: `burst`, `wing-burst`

**Passive Bonus**: Permanent Speed I when equipped

### **🛡️ Immortal Fragment (Earth Element)**
**Material**: Diamond (golden/life visual)
**Theme Color**: Green
**Permission**: `elementaldragon.fragment.immortal`

**Abilities**:
- **Draconic Reflex** (`/immortal 1`): 20% dodge chance for 15 seconds (2min cooldown)
  - Dodge chance: 20% (1 in 5) to completely avoid damage
  - Duration: 15 seconds (300 ticks)
  - On successful dodge: Negates all damage, plays guardian hurt sound
  - On failed dodge: Plays anvil sound (damage is taken normally)
  - Aliases: `reflex`, `draconic-reflex`

- **Essence Rebirth** (`/immortal 2`): 30-second death protection window (8min cooldown)
  - Active for: 30 seconds (600 ticks) after activation
  - Effect: Prevents fatal damage and restores to full health
  - Plays totem sound and particles when triggered
  - If not consumed: Protection expires after 30 seconds
  - Aliases: `rebirth`, `essence-rebirth`

**Passive Bonus**:
- **Permanent Totem Protection**: While equipped, prevents death from fatal damage (same as totem, no cooldown)
- **+2 Hearts**: Increases max health by 4.0 (2 hearts) when equipped
- **Resistance I**: Minor damage reduction when equipped
- **Knockback Reduction**: 25% knockback reduction when equipped

### **💀 Corrupted Core (Void Element)**
**Material**: Nether Star (dark/powerful visual)
**Theme Color**: Dark Purple
**Permission**: `elementaldragon.fragment.corrupted`

**Abilities**:
- **Dread Gaze** (`/corrupt 1`): Complete freeze on next melee hit (3min cooldown)
  - Activation: "READY TO STRIKE" state until you hit a target
  - On hit: Freezes target completely for 4 seconds (80 ticks)
  - Freeze effects: SLOW 255, MINING_FATIGUE 255, WEAKNESS 255, HUNGER 255
  - Prevents: Movement, block placement/breaking, interactions, eating, dropping items
  - Target: Single entity (first one you hit after activation)
  - Aliases: `gaze`, `dread-gaze`

- **Life Devourer** (`/corrupt 2`): 50% lifesteal from all damage (2min cooldown)
  - Duration: 20 seconds (400 ticks)
  - Effect: Heal for 50% of all damage dealt
  - Works with: Melee, ranged, spells - any damage source
  - Aliases: `devourer`, `life-devourer`

**Passive Bonus**:
- **Night Vision**: Permanent night vision when equipped
- **Creeper Invisibility**: Creepers will not target you
- **Enderman Anti-Aggro**: Endermen won't attack when looked at

---

## ⚙️ Cooldown System

### **How It Works**

The Elemental Dragon plugin features a sophisticated global cooldown system that balances powerful abilities with fair gameplay.

#### **Default Cooldowns**
Each fragment ability has a carefully balanced default cooldown:

| Element | Ability 1 | Ability 2 |
|---------|-----------|-----------|
| Lightning | 60s | - |
| Fire | 40s | 60s |
| Wind (Agility) | 30s | 45s |
| Earth (Immortal) | 120s (2min) | 480s (8min) |
| Void (Corrupted) | 180s (3min) | 120s (2min) |

#### **Cooldown Persistence**
- ✅ **Survives logout/login**: Prevents bypassing cooldowns
- ✅ **Survives server restart**: Cooldowns persist across restarts
- ✅ **Survives fragment unequip/equip**: Spam prevention - cannot reset cooldowns by dropping and re-equipping
- ✅ **Survives `/clear` command**: Clearing inventory doesn't reset cooldowns
- ✅ **Cleared on death**: Fair respawn mechanics
- ✅ **Independent of items**: Dropping fragment doesn't reset cooldown
- ✅ **Global configuration**: Operators can override defaults

**⚠️ Spam Prevention**: Cooldowns intentionally persist when you unequip/equip fragments. This prevents players from bypassing cooldowns by quickly dropping and re-equipping fragments. You must wait for the full cooldown duration even if you re-equip the fragment.

#### **Cooldown Adjustment Behavior**

When operators modify global cooldowns, the plugin intelligently manages active player cooldowns to ensure fairness:

**The MIN Formula**: `min(current_remaining, new_max)`

This ensures fair cooldown management:
- **Shorter new cooldown**: Players are capped at the new maximum (prevents unfair advantage)
- **Longer new cooldown**: Players keep their current cooldown (no retroactive penalty)
- **Disabled (0 seconds)**: All active cooldowns for that ability are immediately cleared

**Example Scenarios**:

| Scenario | Player's Current | New Global | Result | Reason |
|----------|-----------------|------------|--------|--------|
| Speed up cooldowns | 60s remaining | 30s | **30s** | Capped to new max (fair) |
| Slow down cooldowns | 20s remaining | 60s | **20s** | Keep current (no penalty) |
| Disable cooldowns | 45s remaining | 0s | **0s** | Cleared immediately |
| Re-enable after disable | 0s (cleared) | 60s | **0s** | Can use immediately |

This design prevents abuse while maintaining fairness for players already on cooldown.

### **Operator Cooldown Control**

Operators can customize cooldowns server-wide or per-player:

```bash
# Change default cooldown for everyone
/ed setglobalcooldown fire 1 10          # Fire ability 1 now 10s for all

# Override specific player's cooldown
/ed setcooldown Steve fire 1 120         # Steve's fire ability 1: 120s

# View all global settings
/ed getglobalcooldown

# Clear a player's cooldowns
/ed clearcooldown Steve                  # Clear all Steve's cooldowns
/ed clearcooldown Steve fire             # Clear only Steve's fire cooldowns
```

---

## 🎯 Perfect For These Server Types

### **🎮 PvP Servers**
- Balanced combat with cooldown-based abilities
- Strategic fragment selection and timing
- Fair gameplay without spam potential

### **🏰 RPG Servers**
- Elemental magic system with 4 distinct classes
- Quest rewards involving fragment collection
- Lore system for immersive storytelling

### **🏝️ Survival Servers**
- Unique abilities enhance PvE gameplay
- Fragment crafting adds progression goals
- Strategic choices in fragment selection

### **🎉 Mini-Game Servers**
- Fragment-based game modes
- Competitive ability-focused events
- Custom challenges with unique mechanics

---

## 🛠️ Development & Installation

### **System Requirements**
- **Minecraft**: Java Edition 1.21.8+
- **Server Software**: Paper 1.21.8-R0.1+
- **Java**: Version 21 or higher
- **Memory**: Minimum 2GB RAM recommended

### **Developer Setup**

**Required Software:**
- **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop/)
- **Java 21+** - [OpenJDK](https://adoptium.net/) or Oracle JDK
- **Gradle** - [Gradle](https://gradle.org/install/) (or use included gradlew wrapper)
- **Git** - [Git](https://git-scm.com/downloads)

**Quick Start for Developers:**

```bash
# 1. Clone repository
git clone https://github.com/cavarest/elemental-dragon.git
cd elemental-dragon

# 2. Build plugin JAR
./build.sh

# 3. Start server with plugin
./start-server.sh --rebuild

# 4. Connect and test
# Server: localhost:25565
# Username: posiflow (automatically configured as operator)
# RCON: localhost:25575 (password: dragon123)

# 5. Stop server
./stop-server.sh
```

### **Running Tests**

The project includes comprehensive unit tests to ensure code quality and reliability.

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "*AchievementTest*"

# Run tests for a package
./gradlew test --tests "org.cavarest.elementaldragon.unit.fragment.*"

# Clean and run tests (force re-run)
./gradlew clean test

# Run tests in continuous mode (watches for changes)
./gradlew test --continuous
```

**View Test Results:**
- **HTML Test Report**: `build/reports/tests/test/index.html`
- **Coverage Report**: `build/reports/jacoco/test/html/index.html`

Open in browser:
```bash
open build/reports/tests/test/index.html
open build/reports/jacoco/test/html/index.html
```

**Current Test Coverage:**
- **Total Tests**: 741 (all passing ✅)
- **Overall Coverage**: 29%
- **High Coverage Areas**: command.util (100%), command.base (100%), Achievement (91%)

### **Docker Development Setup**

This project uses Docker for development and testing. The setup includes:

**Base Image**: `itzg/minecraft-server:java21` - Official PaperMC server with Java 21

**Key Docker Features**:
- **Offline-mode operator setup**: Automatic UUID generation for offline players
- **Plugin hot-reloading**: JAR copied to `/image/plugins/` for easy updates
- **Persisted server data**: `server-data/` volume for world and config files
- **RCON access**: Enabled on port 25575 for remote administration

**Offline-Mode Operator Configuration**:

The server runs in **offline mode** (`ONLINE_MODE=false`) for development. In offline mode, Minecraft clients generate UUIDs differently than online mode:

- **Online mode**: UUIDs fetched from Mojang's authentication servers
- **Offline mode**: UUIDs generated from MD5 hash of `"OfflinePlayer:username"`

**How OPS Setup Works**:

1. **Environment Variable** (`OFFLINE_OPS`): Comma-separated list of usernames who should be operators
   ```bash
   # In .env file or docker-compose.yml
   OFFLINE_OPS=posiflow,player2,admin3
   ```

2. **Entrypoint Script** (`entrypoint.sh`): Runs BEFORE the main `/start` script
   - Reads `OFFLINE_OPS` environment variable
   - Generates correct offline-mode UUIDs for each username
   - Creates `/data/ops.json` with proper format

3. **UUID Generation Algorithm**:
   ```bash
   # Matches Minecraft's offline-mode UUID (version 3)
   hash = MD5("OfflinePlayer:username")
   UUID = version3_format(hash)  # xxxxxxxx-xxxx-3xxx-yxxx-xxxxxxxxxxxx
   ```

4. **Skip mc-image-helper**: Setting `EXISTING_OPS_FILE=SKIP` prevents the base image from overwriting ops.json

**Example for "posiflow"**:
```bash
# Generated UUID: 763be461-6d24-3e4b-9e74-6ead0315f2bf
# Format: 763be461-6d24-3e4b-9e74-6ead0315f2bf
#              ^^^^^^^^ ^^^^ ^    ^^^^ ^^^^^^^^^^^^
#              part1    part2 part3 part4 part5
#                            ↑
#                            "3" = version 3 UUID (replaces first char)
```

**Docker Configuration Files**:

- **Dockerfile**: Builds image with plugin JAR and entrypoint script
- **docker-compose.yml**: Configures environment variables and volumes
- **entrypoint.sh**: Generates offline-mode UUIDs and creates ops.json
- **.env**: Local development configuration (OFFLINE_OPS, memory, etc.)

**Adding More Operators**:

```bash
# Option 1: Edit .env file
OFFLINE_OPS=posiflow,player2,admin3

# Option 2: Set in docker-compose.yml
environment:
  - OFFLINE_OPS=${OFFLINE_OPS:-posiflow,player2,admin3}

# Option 3: Pass via command line
OFFLINE_OPS="newplayer" docker-compose up
```

**Common Docker Commands**:

```bash
# Start server (rebuild image)
./start-server.sh --rebuild

# Start server (use cached image - faster)
./start-server.sh

# View server logs
docker logs -f papermc-elementaldragon

# Stop server
./stop-server.sh

# Access server console (interactive)
docker attach papermc-elementaldragon
# Press Ctrl+P, Ctrl+Q to detach without stopping

# Execute commands inside container
docker exec -it papermc-elementaldragon rcon-cli
> op list
> list
```

**Server Management Options**

The `start-server.sh` script supports several options for different development workflows:

| Option | Description |
|--------|-------------|
| `-p, --profile NAME` | Use world profile (default: 'default') |
| `-r, --rebuild` | Rebuild Docker image and restart server (preserves server data) |
| `-c, --clean` | Clean build (Gradle clean + fresh Docker image + delete all data) |
| `-w, --wipe-world` | Clear world data only (preserves configs, plugins, player data) |
| `-b, --blocking` | Start in blocking mode (logs shown directly, Ctrl+C to stop) |
| `-h, --help` | Show help message with all options |

### World Profiles

World profiles allow you to switch between different server configurations, each with its own isolated world directory. This is useful for maintaining separate environments for real gameplay testing and quick plugin testing.

**Available Profiles:**

| Profile | Description | Use Case |
|---------|-------------|----------|
| `normal-survival-normal` | Normal terrain, survival mode, all mobs | Real gameplay testing, survival mechanics |
| `flat-creative-none` | Flat terrain, creative mode, no mobs | Quick ability testing, plugin development |
| `flat-survival-peaceful` | Flat terrain, survival mode, passive mobs only | Survival without hostile threats |
| `flat-survival-normal` | Flat terrain, survival mode, all mobs | Survival with normal difficulty on flat terrain |

**Naming Convention:** `{terrain}-{mode}-{mob-spawning}`
- **Terrain:** `normal` (default) or `flat` (custom layers)
- **Mode:** `survival` or `creative`
- **Mob spawning:** `normal` (all mobs), `peaceful` (passive only), or `none` (no mobs)

**Using Profiles:**

```bash
# Default profile (normal survival)
./start-server.sh
./start-server.sh -p normal-survival-normal

# Quick testing profile (flat creative, no mobs)
./start-server.sh -p flat-creative-none
./start-server.sh -p flat-creative-none -b    # blocking mode
./start-server.sh -p flat-creative-none -w    # wipe world first

# Flat survival without hostile mobs
./start-server.sh -p flat-survival-peaceful
./start-server.sh -p flat-survival-peaceful -b    # blocking mode
./start-server.sh -p flat-survival-peaceful -w    # wipe world first

# Flat survival with hostile mobs
./start-server.sh -p flat-survival-normal
./start-server.sh -p flat-survival-normal -b    # blocking mode
./start-server.sh -p flat-survival-normal -w    # wipe world first
```

**Profile Features:**

`normal-survival-normal` profile:
- Normal Minecraft terrain generation
- Survival mode with normal difficulty
- All mob spawning enabled (animals, monsters, NPCs)
- Nether and End dimensions enabled
- Full view distance (10 chunks)

`flat-creative-none` profile:
- Flat world with custom layers (bedrock, dirt, grass)
- Creative mode with peaceful difficulty
- No mob spawning (animals, monsters, NPCs)
- Nether and End disabled
- Small world border (50 blocks)
- Minimal view distance (4 chunks) for performance
- Fixed seed (1234567890) for reproducibility

`flat-survival-peaceful` profile:
- Flat world with custom layers (bedrock, dirt, grass)
- Survival mode with peaceful difficulty
- Passive mob spawning enabled (animals, NPCs)
- Hostile mob spawning disabled
- Nether and End dimensions enabled
- Normal view distance (10 chunks)

`flat-survival-normal` profile:
- Flat world with custom layers (bedrock, dirt, grass)
- Survival mode with normal difficulty
- All mob spawning enabled (animals, monsters, NPCs)
- Nether and End dimensions enabled
- Normal view distance (10 chunks)

**World Data Isolation:**

Each profile uses its own world directory, preventing conflicts:

```
server-data/
├── world/                           # normal-survival-normal profile world
├── world-flat-creative-none/        # flat-creative-none profile world
├── world-flat-survival-peaceful/    # flat-survival-peaceful profile world
├── world-flat-survival-normal/      # flat-survival-normal profile world
├── plugins/                         # shared across all profiles
└── ...                              # shared configs
```

This means you can:
- Switch between profiles without losing progress
- Test different configurations independently
- Maintain separate worlds for different purposes

**Creating Custom Profiles:**

To create a new profile, add a `.sh` file to `world-profiles/`:

```bash
#!/bin/bash
export PROFILE_NAME="my-profile"
export WORLD_NAME="world-my-profile"
export LEVEL="my-world"
export LEVEL_TYPE="FLAT"
export MODE="creative"
# ... override any settings from .env
```

See `world-profiles/README.md` for complete documentation.

**Common Use Cases:**

```bash
# Normal development start (default profile - real gameplay)
./start-server.sh

# Quick plugin testing (flat creative profile - minimal distractions)
./start-server.sh -p flat-creative-none

# Quick restart with rebuilt image (preserves world and configs)
./start-server.sh -r

# Fresh world for testing in default profile
./start-server.sh -w

# Fresh flat world for testing
./start-server.sh -p flat-creative-none -w

# Complete reset (new world, new configs, rebuild everything)
./start-server.sh -c

# Testing with visible logs (flat creative profile)
./start-server.sh -p flat-creative-none -b

# Full reset with visible logs
./start-server.sh -c -b

# Fresh world with rebuild, logs shown
./start-server.sh -r -w -b
```

**Option Details:**

**`-p, --profile NAME`** (Profile Selection)
- Selects which world profile to use
- Each profile has its own isolated world directory
- Profile sets world generation, gameplay, and performance settings
- Default profile: `normal-survival-normal` (normal terrain, survival, all mobs)
- See "World Profiles" section above for available profiles

**`-r, --rebuild`** (Rebuild Mode)
- Rebuilds Docker image with latest plugin JAR
- Deletes all Docker volumes and server data
- Forces fresh container startup
- Use after code changes to ensure latest plugin is loaded

**`-c, --clean`** (Clean Mode)
- Runs Gradle clean build
- Deletes all Docker volumes and server data
- Forces complete Docker image rebuild
- Use when Docker cache might be stale or corrupted

**`-w, --wipe-world`** (World Wipe Mode)
- Removes only world directories for the active profile
- For `normal-survival-normal` profile: `world/`, `world_nether/`, `world_the_end/`
- For `flat-creative-none` profile: `world-flat-creative-none/`, `world-flat-creative-none_nether/`, `world-flat-creative-none_the_end/`
- Removes `session.lock` file
- **Preserves**: Server configs, plugins, player data, OP settings
- Use for quick world reset during testing without full reconfiguration

**`-b, --blocking`** (Blocking Mode)
- Starts server in foreground with logs visible
- Shows server output directly in terminal
- Press Ctrl+C to stop server
- Use for debugging or monitoring server startup

**Combining Options:**

Options can be combined for different scenarios:

```bash
# Rebuild image + fresh world (default profile)
./start-server.sh -r -w

# Fresh flat world + visible logs
./start-server.sh -p flat-creative-none -w -b

# Full reset (world already cleared by -c)
./start-server.sh -c -w    # equivalent to -c alone

# Rebuild + profile + blocking
./start-server.sh -r -p flat-survival-normal -b
```

**Port Conflict Detection:**

The script automatically detects and stops containers using ports 25565/25575. The detection checks **actual port bindings**, not container names, so it won't incorrectly stop containers with similar names that use different ports.

If you have other PaperMC servers running on different ports (e.g., ports 35115/35125), they will not be affected.

**Troubleshooting OPS Issues**:

If you cannot login as operator:

1. **Check entrypoint logs**:
   ```bash
   docker logs papermc-elementaldragon | grep "Elemental Dragon Offline Ops"
   ```

2. **Verify ops.json was created**:
   ```bash
   docker exec papermc-elementaldragon cat /data/ops.json
   ```

3. **Check your username matches OFFLINE_OPS exactly**:
   ```bash
   # Case-sensitive! "Posiflow" != "posiflow"
   ```

4. **Verify offline-mode UUID generation**:
   ```bash
   # Test UUID generation locally
   echo -n "OfflinePlayer:posiflow" | md5sum
   # Should match: 763be461-6d24-3e4b-9e74-6ead0315f2bf
   ```

**Why This Approach?**

- ✅ **Reproducible**: Same UUID every time for the same username
- ✅ **Offline-friendly**: No internet connection required
- ✅ **Development-ready**: Works with any username without Mojang API
- ✅ **Container-native**: All setup done in entrypoint, no manual steps

---

## 🏗️ Architecture Highlights

### **Phase 6: True Object-Oriented Design + Post-Completion Fixes**

The Elemental Dragon plugin demonstrates professional software architecture with continuous improvements:

#### **Single Source of Truth Pattern**
- ✅ Fragment classes own ALL their metadata
- ✅ Commands query Fragment dynamically (zero duplication)
- ✅ Items query Fragment for visual properties
- ✅ Auto-generated help, tab completion, status displays
- ✅ **NEW**: Crafting recipes auto-generate displays (DRY `/craft` command)

#### **Code Quality Metrics**
- **62% code reduction** through architectural improvements (673 lines eliminated)
- **Zero duplication** across commands, items, and recipes
- **Compiler-enforced** completeness via interfaces
- **206 tests passing** with comprehensive coverage
- **Intelligent cooldown management** with MIN formula and clear-on-disable

#### **Design Patterns Applied**
1. **Single Source of Truth**: Fragments own their data, CraftingManager owns recipes
2. **Template Method**: AbstractFragment provides structure
3. **Dependency Injection**: Commands inject Fragment instances
4. **Strategy Pattern**: Interchangeable Fragment implementations
5. **Command Pattern**: Subcommand registry for admin functions
6. **Introspection Pattern**: Runtime recipe querying for `/craft` command

#### **Recent Improvements (Phase 6 Post-Completion)**

**DRY `/craft` Command**:
- Before: Recipes hardcoded in 2 places (CraftingManager + CraftCommand)
- After: Single source of truth with runtime introspection via `RecipeData` class
- Impact: Recipe changes now require updating only 1 location

**Intelligent Cooldown Adjustment**:
- Formula: `min(current_remaining, new_max)` ensures fairness
- Prevents unfair advantages when cooldowns decrease
- No retroactive penalties when cooldowns increase
- Clear-on-disable: Setting cooldown to 0 clears all active player cooldowns

**Lightning Global Cooldown Support**:
- Lightning ability now respects global cooldown settings
- `/ed setglobalcooldown lightning 1 <seconds>` works correctly
- Integration with `AbilityManager` for unified cooldown system
- Consistent behavior across all element types

---

## 📚 Complete Documentation

### 🎮 [User Documentation](docs/user/)
- [Complete User Guide](docs/user/README.md) - Features and usage
- [Installation Guide](docs/user/installation.md) - Setup instructions

### 🔧 [Administrator Documentation](docs/admin/)
- [Commands Reference](docs/admin/commands.md) - All admin commands
- [Testing Guide](docs/admin/testing.md) - Testing procedures
- [Docker Guide](docs/admin/docker.md) - Container deployment

### 💻 [Developer Documentation](docs/dev/)
- [Architecture](docs/dev/frameworks.md) - System design
- [Testing](docs/dev/testing.md) - Test framework

---

## 🎯 Why This Plugin Will Transform Your Server

### **🎯 Player Engagement**
- **4 Unique Elements**: Fire, Wind, Earth, Void with distinct abilities
- **Skill-Based**: Requires timing, positioning, and strategy
- **Visual Spectacle**: Unique particles and effects for each element
- **Strategic Depth**: Fragment selection and cooldown management

### **📊 Server Benefits**
- **Balanced Gameplay**: Carefully tuned cooldowns prevent spam
- **Performance**: Optimized for Paper 1.21.8+ with minimal impact
- **Stability**: 197 passing tests ensure reliability
- **Extensibility**: Clean architecture makes customization easy

### **🌟 Competitive Advantage**
- **Unique System**: Elemental fragments set your server apart
- **Player Retention**: 8+ unique abilities keep gameplay fresh
- **Fair Balance**: Global cooldown system prevents abuse
- **Content Rich**: Lore, achievements, and progression systems

---

## 🐛 Troubleshooting

### **Common Issues & Solutions**

#### **Plugin Not Loading**
```bash
# Check Java version (must be 21+)
java -version

# Verify JAR location
ls plugins/ElementalDragon*.jar

# Check server logs
tail -f logs/latest.log | grep -i elementaldragon
```

#### **Command Not Working**
```bash
# Check permissions
/lp user <player> permission check elementaldragon.fragment.burning

# Verify fragment equipped
/fire status

# Check command registration
/help fire
```

#### **Cooldown Issues**
```bash
# View player cooldowns
/ed getcooldown <player>

# View global settings
/ed getglobalcooldown

# Clear stuck cooldown
/ed clearcooldown <player>
```

---

## 📈 Architecture & Testing

### **Testing Framework**
- **🧪 749 Unit Tests**: Complete coverage with JUnit and Mockito
- **🔧 Integration Tests**: End-to-end YAML-driven scenarios
- **🐳 Docker Support**: Containerized development environment
- **✅ CI/CD**: Automated testing on every commit
- **📊 29% Coverage**: Focus on testable business logic (Bukkit API limits integration testing)
- **📚 Pilaf**: JavaScript testing framework for PaperMC plugins - [https://cavarest.github.io/pilaf/](https://cavarest.github.io/pilaf/)

### **Code Quality**
- **SOLID Principles**: Applied throughout codebase
- **Design Patterns**: Professional architecture patterns
- **Zero Duplication**: DRY principle strictly enforced
- **Type Safety**: Compile-time validation

---

## 📞 Support & Community

### **Getting Help**
- **GitHub Issues**: [Report bugs or request features](https://github.com/cavarest/elemental-dragon/issues)
- **Documentation**: Comprehensive guides in `/docs` directory
- **Community**: Connect with other server administrators

### **Contributing**
- **Bug Reports**: Help us improve by reporting issues
- **Feature Requests**: Suggest new abilities or improvements
- **Code Contributions**: Submit pull requests

---

## 📄 License & Credits

**License**: MIT License - Free for personal and commercial use

**Author**: The Cavarest project
**Version**: 1.3.6 (see gradle.properties)
**Minecraft Version**: 1.21.8+
**Paper API**: 1.21.8-R0.1-SNAPSHOT

---

<div align="center">

**⭐ Star this project if you love Elemental Dragon!** ⭐

[![GitHub Stars](https://img.shields.io/github/stars/cavarest/elemental-dragon?style=social)](https://github.com/cavarest/elemental-dragon)
[![GitHub Forks](https://img.shields.io/github/forks/cavarest/elemental-dragon/fork?style=social)](https://github.com/cavarest/elemental-dragon/fork)

**Transform your server today with Elemental Dragon!** ⚡

[Download Latest Release](https://github.com/cavarest/elemental-dragon/releases/latest) | [User Guide](docs/user/README.md) | [Support](https://github.com/cavarest/elemental-dragon/issues)

</div>
