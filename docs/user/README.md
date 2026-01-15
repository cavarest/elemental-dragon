# ⚡ Elemental Dragon Plugin

<div align="center">

![Dragon Egg Lightning Banner](https://img.shields.io/badge/Minecraft-Paper%201.21.8-blue?logo=minecraft&style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21+-ED8B00?logo=openjdk&logoColor=white&style=for-the-badge)
![Version](https://img.shields.io/badge/Version-1.1.0-green?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)

**Master the elements with dragon-powered abilities!**

[![Download Latest Release](https://img.shields.io/badge/Download-Latest%20Release-ff6b6b?style=for-the-badge&logo=github)](https://github.com/cavarest/elemental-dragon/releases/latest)
[![GitHub Stars](https://img.shields.io/github/stars/cavarest/elemental-dragon?style=social)](https://github.com/cavarest/elemental-dragon)

</div>

## What is Elemental Dragon?

Elemental Dragon is a PaperMC plugin that transforms your Minecraft server with **dragon-powered abilities**. Use dragon eggs and elemental fragments to unleash devastating attacks, move at supernatural speeds, or cheat death itself!

### Core Features

#### ⚡ Dragon Egg Lightning Ability
- Hold a **Dragon Egg in your offhand**
- Execute `/lightning 1` to summon **3 sequential lightning strikes**
- Each strike deals **2 hearts of armor-bypassing damage**
- **60-second cooldown** prevents spam

#### 🔥💨🛡️👁 Elemental Fragments
Four unique fragments, each with **2 powerful abilities**:

| Fragment | Emoji | Ability 1 | Ability 2 |
|----------|-------|-----------|-----------|
| **Burning** | 🔥 | Dragon's Wrath (25s) | Infernal Dominion (40s) |
| **Agility** | 💨 | Draconic Surge (45s) | Wing Burst (2min) |
| **Immortal** | 🛡️ | Draconic Reflex (2min) | Essence Rebirth (8min) |
| **Corrupted** | 👁 | Dread Gaze (3min) | Life Devourer (2min) |

#### 📊 Real-Time HUD
- See cooldown status for **all abilities at once**
- Visual progress bars for remaining cooldowns
- "Ready" indicators when abilities are available

---

## Quick Start (5 Minutes)

### For Players

```bash
# 1. Get a Dragon Egg (for lightning ability)
give @p minecraft:dragon_egg

# 2. Move to offhand (press F key)

# 3. Cast lightning!
/lightning 1

# 4. Get a fragment (ask admin or craft it)
# See /craft command for recipes
```

### For Server Admins

```bash
# Download the latest JAR from releases
# Place in your Paper server's plugins directory
cp ElementalDragon-1.1.0.jar /path/to/your/paper-server/plugins/

# Restart your Paper 1.21.8+ server
java -Xms2G -Xmx2G -jar paper-1.21.8-latest.jar nogui
```

---

## Fragment System

### Obtaining Fragments

Fragments are **crafted** using special recipes. All require a **vanilla Heavy Core** (found in Ancient City chests) as the center ingredient.

#### Crafting Limits
Each player is limited in how many they can craft:
- **Burning Fragment**: 2 max
- **Agility Fragment**: 2 max
- **Immortal Fragment**: 2 max
- **Corrupted Core**: 1 max

#### Crafting Recipes

**Burning Fragment** (2 max)
```
N S N   (N = Netherite Upgrade, S = Bolt Armor Trim)
I H I   (I = Netherite Ingot, H = Heavy Core)
N R N   (N = Netherite Upgrade, R = Rib Armor Trim)
```

**Agility Fragment** (2 max)
```
B F A   (B = Breeze Rod, F = Flow Armor Trim, A = Ancient Debris)
D H D   (D = Diamond Block, H = Heavy Core)
A F B
```

**Immortal Fragment** (2 max)
```
T A T   (T = Totem of Undying, A = Ancient Debris)
D H D   (D = Diamond Block, H = Heavy Core)
T A T
```

**Corrupted Core** (1 max)
```
R H K   (R = Wither Rose, H = Heavy Core, K = Wither Skull)
N S N   (N = Netherite Block, S = Nether Star)
K H R
```

### Craft Broadcasts

When you craft a fragment, all players see a themed message:
- 🔥 Burning: "The flames of the ancient dragon acknowledge {player} has forged the Burning Fragment!"
- 💨 Agility: "The winds of the ancient dragon acknowledge {player} has forged the Agility Fragment!"
- 🛡️ Immortal: "The earth of the ancient dragon acknowledges {player} has forged the Immortal Fragment!"
- 👁 Corrupted: "The void of the ancient dragon acknowledges {player} has forged the Corrupted Core!"

### Equipping Fragments

**Method 1: Right-Click**
- Hold fragment in main hand
- Right-click to equip to offhand

**Method 2: Command**
```
/fire equip      # Equip Burning Fragment
/agile equip     # Equip Agility Fragment
/immortal equip  # Equip Immortal Fragment
/corrupt equip   # Equip Corrupted Core
```

**Method 3: Operator Auto-Give**
```
/equip fire      # Give and equip Burning Fragment
/equip agile     # Give and equip Agility Fragment
```

### Using Abilities

Each fragment has 2 abilities:

**Burning Fragment (🔥)**
```
/fire 1   # Dragon's Wrath - Fireball with explosion AOE (25s cooldown)
/fire 2   # Infernal Dominion - Ring of fire, damages nearby enemies (40s cooldown)
```

**Agility Fragment (💨)**
```
/agile 1   # Draconic Surge - Dash 20 blocks in 1.5s, negate fall damage (45s cooldown)
/agile 2   # Wing Burst - Push enemies 20 blocks away, slow falling (2min cooldown)
```

**Immortal Fragment (🛡️)**
```
/immortal 1   # Draconic Reflex - 20% chance to dodge damage (2min cooldown)
/immortal 2   # Essence Rebirth - Second life if killed (8min cooldown)
```

**Corrupted Core (👁)**
```
/corrupt 1   # Dread Gaze - Complete freeze on next hit (3min cooldown)
/corrupt 2   # Life Devourer - 50% life steal for 20s (2min cooldown)
```

### HUD Display

When equipped, your HUD shows:
- Ability name and cooldown status for BOTH abilities simultaneously
- Visual progress bar showing cooldown remaining
- "Ready" indicator when cooldown is complete

### Withdrawing Abilities

```bash
/withdrawability
```

Removes abilities but keeps the fragment item in your inventory.

---

## Fragment Restrictions

### Container Storage
Fragments **cannot** be placed in containers (chests, furnaces, etc.). Attempting to do so shows:
```
Elemental Dragon fragments cannot be stored in containers.
```

### Item Loss
If you **drop** a fragment, abilities are automatically withdrawn.

### Fireproof
Fragments have special enchantment making them glow and fireproof.

---

## Complete Command Reference

### Player Commands

| Command | Description |
|---------|-------------|
| `/lightning 1` | Summon 3 lightning strikes (requires Dragon Egg in offhand) |
| `/fire 1` / `/fire 2` | Use Burning Fragment abilities |
| `/agile 1` / `/agile 2` | Use Agility Fragment abilities |
| `/immortal 1` / `/immortal 2` | Use Immortal Fragment abilities |
| `/corrupt 1` / `/corrupt 2` | Use Corrupted Core abilities |
| `/<type> equip` | Equip fragment to offhand |
| `/<type> status` | View fragment status and cooldowns |
| `/<type> help` | View ability information |
| `/craft <type>` | View crafting recipe |
| `/chronicle` | Open the Chronicle of the Fallen Dragons lore book |
| `/withdrawability` | Remove equipped abilities (keep item) |

### Admin Commands

| Command | Description |
|---------|-------------|
| `/ed give <player> equipment <type>` | Give a fragment to a player |
| `/ed give <player> ingredients <type>` | Give crafting materials |
| `/ed setcooldown <player> <type> <ability> <seconds>` | Set cooldown |
| `/ed clearcooldown <player> [type]` | Clear cooldowns |
| `/ed getcooldown <player>` | View cooldowns |
| `/ed info player <player>` | View player status |

---

## Crafted Count System

Each player's crafted fragments are tracked:
- Persistent across sessions
- Enforces craft limits (2 for most, 1 for Corrupted)
- Prevents duplicate crafting

View your crafted count with `/craft <type>`.

---

## Cooldown System

### How It Works
- Each ability has its own cooldown
- Cooldowns persist across logout/login
- Cooldowns are cleared on death
- Setting cooldown to 0 clears all active cooldowns for that ability

### Default Cooldowns
| Ability | Cooldown |
|---------|----------|
| Lightning | 60s |
| Fire 1 (Dragon's Wrath) | 25s |
| Fire 2 (Infernal Dominion) | 40s |
| Agility 1 (Draconic Surge) | 45s |
| Agility 2 (Wing Burst) | 120s (2min) |
| Immortal 1 (Draconic Reflex) | 120s (2min) |
| Immortal 2 (Essence Rebirth) | 480s (8min) |
| Corrupt 1 (Dread Gaze) | 180s (3min) |
| Corrupt 2 (Life Devourer) | 120s (2min) |

---

## Chronicle of the Fallen Dragons

Unlock lore as you use abilities:
- **Introduction**: Available immediately
- **IGNIS**: Unlock after using Fire abilities
- **ZEPHYR**: Unlock after using Agility abilities
- **TERRA**: Unlock after using Immortal abilities
- **UMBRA**: Unlock after using Corrupted abilities
- **RECOVERY**: Unlock after mastering all abilities
- **THE FALL**: Unlock after collecting all fragments

---

## Passive Bonuses

| Fragment | Passive Effect |
|----------|---------------|
| Burning Fragment | None |
| Agility Fragment | None |
| Immortal Fragment | None |
| Corrupted Core | Night Vision, Invisible to creepers |

---

## Lightning Ability Usage

### Setup
1. **Get a Dragon Egg**:
   ```
   /give @p minecraft:dragon_egg
   ```

2. **Equip to Offhand**:
   - Pick up the Dragon Egg
   - Press `F` key to move it to your offhand (left hand)
   - You should see it in your inventory slot 9 (hotbar far right)

3. **Verify Offhand** (optional):
   ```
   /get item @s
   ```

### Activation
```bash
/lightning 1
```

### What Happens
1. The plugin checks you have a Dragon Egg in your offhand
2. Finds the closest hostile mob within 50 blocks (ray-tracing)
3. Summons **3 sequential lightning strikes** (0.5 seconds apart)
4. Each strike deals **2 hearts of damage** (4 HP)
5. Damage **ignores all armor and protection enchantments**
6. You'll see purple lightning and hear thunder
7. 60-second cooldown begins

### Tips
- Move to get clear line-of-sight on targets
- Works on any living entity (mobs, players, animals)
- Armor-bypassing means it works equally well against heavily armored targets
- 6.0 hearts total damage can eliminate most hostile mobs

---

## Fragment Usage

### Obtaining Your First Fragment

#### Method 1: Ask an Admin
```
/ed give @p equipment fire      # Give Burning Fragment
/ed give @p equipment agile     # Give Agility Fragment
/ed give @p equipment immortal  # Give Immortal Fragment
/ed give @p equipment corrupt   # Give Corrupted Core
```

#### Method 2: Craft It
1. Find a **Heavy Core** in an Ancient City chest (vanilla Minecraft item)
2. Gather all other materials
3. Use a crafting table with the recipe
4. A themed message broadcasts to all players!

#### Method 3: Get Crafting Materials
```
/ed give @p ingredients fire      # Get Fire Fragment materials
/ed give @p ingredients agile     # Get Agility Fragment materials
/ed give @p ingredients immortal  # Get Immortal Fragment materials
/ed give @p ingredients corrupt   # Get Corrupted Core materials
```

### Equipping

#### Right-Click Method
1. Hold the fragment in your main hand
2. Right-click
3. Fragment moves to offhand
4. You hear activation sound and see particles

#### Command Method
```
/fire equip      # Equip Burning Fragment
/agile equip     # Equip Agility Fragment
/immortal equip  # Equip Immortal Fragment
/corrupt equip   # Equip Corrupted Core
```

#### Operator Auto-Give
```
/equip fire      # Give AND equip Burning Fragment
```

### Using Abilities

Once equipped, use abilities with:
```
/<type> 1   # First ability
/<type> 2   # Second ability
```

**Examples**:
```
/fire 1           # Fireball attack!
/agile 2          # Wing burst to knock back enemies
/immortal 1       # Activate damage dodge chance
/corrupt 2        # Life steal mode!
```

### Checking Status

View your cooldowns and fragment status:
```
/fire status      # See Fire Fragment cooldowns
/agile status     # See Agility Fragment cooldowns
/immortal status  # See Immortal Fragment cooldowns
/corrupt status   # See Corrupted Core cooldowns
```

### Unequipping (Keep the Item)

Want to unequip but keep the fragment for later?
```
/withdrawability
```

Your abilities are removed, but the fragment stays in your inventory.

---

## Verification & Testing

### Test Lightning Ability
```bash
# 1. Give yourself a Dragon Egg
/give @p minecraft:dragon_egg

# 2. Move to offhand (F key)
# Verify by checking your offhand slot

# 3. Find a mob (spawn one for testing)
/summon minecraft:zombie ~ ~ ~

# 4. Cast lightning
/lightning 1

# 5. Check cooldown
/ed getcooldown @s
```

### Test Fragment Ability
```bash
# 1. Get a fragment
/ed give @p equipment fire

# 2. Equip it
/fire equip

# 3. Use an ability
/fire 1

# 4. Check status
/fire status

# 5. Check HUD (should show cooldown)
```

### Admin Verification Commands
```bash
/ed info player @s          # Your status
/ed info player PlayerName  # Another player's status
/ed getcooldown @s          # Your cooldowns
```

### Server Logs
Check server logs for ability usage:
```bash
docker logs -f papermc-elementaldragon
```

Look for messages like:
- "Lightning strike 1/3!"
- "Dragon's Wrath activated!"
- "Dread Gaze has expired."

---

## System Requirements

- **Minecraft**: Java Edition 1.21.8+
- **Server Software**: Paper 1.21.8-R0.1+
- **Java**: Version 21 or higher
- **Memory**: Minimum 2GB RAM recommended

---

## Development

### Build Commands
```bash
./build.sh              # Standard build
./build.sh --clean      # Clean build
./build.sh --production # Production build
```

### Server Management
```bash
./start-server.sh       # Start server
./start-server.sh --rebuild  # Rebuild Docker image
./stop-server.sh        # Stop server
```

### Testing
```bash
./gradlew test          # Run all tests (214 tests)
./gradlew test --continuous  # Watch mode
```

---

## Support

- **GitHub Issues**: [Report bugs or request features](https://github.com/cavarest/elemental-dragon/issues)
- **Documentation**: See `/chronicle` in-game for lore and guides

---

**Version**: 1.1.0
**Author**: Augustus Tse and Octavius Tse
**License**: MIT

<div align="center">

**Master the elements. Conquer the dragon within.** 🐉

[Download Latest Release](https://github.com/cavarest/elemental-dragon/releases/latest) | [Quick Start](#quick-start-5-minutes)

</div>
