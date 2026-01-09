# ⚡ Elemental Dragon Plugin

<div align="center">

![Dragon Egg Lightning Banner](https://img.shields.io/badge/Minecraft-Paper%201.21.8-blue?logo=minecraft&style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21+-ED8B00?logo=openjdk&logoColor=white&style=for-the-badge)
![Version](https://img.shields.io/badge/Version-1.1.0-green?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)

**Transform your server with elemental powers from ancient dragon fragments!**

[![Download Latest Release](https://img.shields.io/badge/Download-Latest%20Release-ff6b6b?style=for-the-badge&logo=github)](https://github.com/cavarest/papermc-plugin-dragon-egg/releases/latest)
[![GitHub Stars](https://img.shields.io/github/stars/cavarest/papermc-plugin-dragon-egg?style=social)](https://github.com/cavarest/papermc-plugin-dragon-egg)
[![GitHub Forks](https://img.shields.io/github/forks/cavarest/papermc-plugin-dragon-egg/fork?style=social)](https://github.com/cavarest/papermc-plugin-dragon-egg/fork)

</div>

## 🌟 Quick Start (5 Minutes)

### **Step 1: Install Plugin**
```bash
# Download the latest JAR from releases
# Place in your Paper server's plugins directory
cp ElementalDragon-1.1.0.jar /path/to/your/paper-server/plugins/

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
/fire 1    # Dragon's Wrath (fireball attack)
/fire 2    # Infernal Dominion (fire ring)

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
/agile 1           # Draconic Surge (speed boost, 30s cooldown)
/agile 2           # Wing Burst (levitation jump, 45s cooldown)
/agile status      # Show fragment status and cooldowns
/agile help        # Show available commands

# Immortal Fragment (Earth Element)
/immortal equip    # Equip Immortal Fragment
/immortal 1        # Draconic Reflex (damage reduction, 90s cooldown)
/immortal 2        # Essence Rebirth (death protection, 5min cooldown)
/immortal status   # Show fragment status and cooldowns
/immortal help     # Show available commands

# Corrupted Core (Void Element)
/corrupt equip     # Equip Corrupted Core
/corrupt 1         # Dread Gaze (blindness & slow, 60s cooldown)
/corrupt 2         # Life Devourer (health steal, 90s cooldown)
/corrupt status    # Show fragment status and cooldowns
/corrupt help      # Show available commands
```

#### **Lightning Command (Legacy)**
```bash
/lightning 1       # Core lightning ability (60s cooldown)
```

#### **Utility Commands**
```bash
/craft <recipe>    # View crafting recipes
/chronicle <cmd>   # Access lore and achievements
```

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
- **Dragon's Wrath** (`/fire 1`): Launch explosive fireball (40s cooldown)
  - Damage: 4 hearts on impact
  - Explosion power: 2.0 blocks radius
  - Fire spread: Ignites targets
  - Aliases: `wrath`, `dragons-wrath`

- **Infernal Dominion** (`/fire 2`): Create fire ring around player (60s cooldown)
  - Radius: 8 blocks
  - Duration: 5 seconds
  - Damage: 1 heart per tick to enemies in range
  - Aliases: `dominion`, `infernal-dominion`

**Passive Bonus**: Fire Resistance when equipped

### **💨 Agility Fragment (Wind Element)**
**Material**: Feather (light/swift visual)
**Theme Color**: Aqua
**Permission**: `elementaldragon.fragment.agility`

**Abilities**:
- **Draconic Surge** (`/agile 1`): Speed II + Jump II boost (30s cooldown)
  - Speed: Level II for 10 seconds
  - Jump Boost: Level II for 10 seconds
  - Aliases: `surge`, `draconic-surge`

- **Wing Burst** (`/agile 2`): Vertical launch with fall protection (45s cooldown)
  - Vertical force: 1.5 blocks/tick
  - Levitation: 2 seconds
  - Slow Falling: 3 seconds (prevents fall damage)
  - Aliases: `burst`, `wing-burst`

**Passive Bonus**: Permanent Speed I when equipped

### **🛡️ Immortal Fragment (Earth Element)**
**Material**: Totem of Undying (golden/life visual)
**Theme Color**: Green
**Permission**: `elementaldragon.fragment.immortal`

**Abilities**:
- **Draconic Reflex** (`/immortal 1`): 75% damage reduction + reflection (90s cooldown)
  - Damage reduction: 75% for 5 seconds
  - Reflection: 25% melee damage reflected
  - Aliases: `reflex`, `draconic-reflex`

- **Essence Rebirth** (`/immortal 2`): Enhanced respawn benefits (5min cooldown)
  - Diamond armor piece: Random piece on respawn
  - Full hunger: 20 food level
  - Arrows: 32 arrows if bow in inventory
  - Aliases: `rebirth`, `essence-rebirth`

**Passive Bonus**: 25% knockback reduction + 2 hearts when equipped

### **💀 Corrupted Core (Void Element)**
**Material**: Nether Star (dark/powerful visual)
**Theme Color**: Dark Purple
**Permission**: `elementaldragon.fragment.corrupted`

**Abilities**:
- **Dread Gaze** (`/corrupt 1`): Blind nearby enemies (60s cooldown)
  - Radius: 10 blocks
  - Effect: Blindness II for 5 seconds
  - Targets: All hostile mobs in range
  - Aliases: `gaze`, `dread-gaze`

- **Life Devourer** (`/corrupt 2`): Drain health from enemies (90s cooldown)
  - Range: 8 blocks
  - Duration: 8 seconds
  - Health steal: 50% of damage dealt
  - Drain rate: 0.5 hearts per tick
  - Aliases: `devourer`, `life-devourer`

**Passive Bonus**: Night Vision + invisible to creepers

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
| Earth (Immortal) | 90s | 300s (5min) |
| Void (Corrupted) | 60s | 90s |

#### **Cooldown Persistence**
- ✅ **Survives logout/login**: Prevents bypassing cooldowns
- ✅ **Cleared on death**: Fair respawn mechanics
- ✅ **Independent of items**: Dropping fragment doesn't reset cooldown
- ✅ **Global configuration**: Operators can override defaults

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
git clone https://github.com/cavarest/papermc-plugin-dragon-egg.git
cd papermc-plugin-dragon-egg

# 2. Build plugin JAR
./build.sh

# 3. Start server with plugin
./start-server.sh --rebuild

# 4. Connect and test
# Server: localhost:25565
# Username: posiflow
# RCON: localhost:25575 (password: dragon123)

# 5. Stop server
./stop-server.sh
```

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
- **🧪 206 Unit Tests**: Complete coverage with JUnit and Mockito
- **🔧 Integration Tests**: End-to-end YAML-driven scenarios
- **🐳 Docker Support**: Containerized development environment
- **✅ CI/CD**: Automated testing on every commit

### **Code Quality**
- **SOLID Principles**: Applied throughout codebase
- **Design Patterns**: Professional architecture patterns
- **Zero Duplication**: DRY principle strictly enforced
- **Type Safety**: Compile-time validation

---

## 📞 Support & Community

### **Getting Help**
- **GitHub Issues**: [Report bugs or request features](https://github.com/cavarest/papermc-plugin-dragon-egg/issues)
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
**Version**: 1.1.0
**Minecraft Version**: 1.21.8+
**Paper API**: 1.21.8-R0.1-SNAPSHOT

---

<div align="center">

**⭐ Star this project if you love Elemental Dragon!** ⭐

[![GitHub Stars](https://img.shields.io/github/stars/cavarest/papermc-plugin-dragon-egg?style=social)](https://github.com/cavarest/papermc-plugin-dragon-egg)
[![GitHub Forks](https://img.shields.io/github/forks/cavarest/papermc-plugin-dragon-egg/fork?style=social)](https://github.com/cavarest/papermc-plugin-dragon-egg/fork)

**Transform your server today with Elemental Dragon!** ⚡

[Download Latest Release](https://github.com/cavarest/papermc-plugin-dragon-egg/releases/latest) | [User Guide](docs/user/README.md) | [Support](https://github.com/cavarest/papermc-plugin-dragon-egg/issues)

</div>
