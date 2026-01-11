---
layout: default
title: Fragment User Guide
nav_order: 2
has_children: false
permalink: /user/fragments/
---

# Elemental Dragon Fragment User Guide

This guide covers the Elemental Dragon fragment system - powerful items that grant unique elemental abilities.

## Overview

Elemental Dragon fragments are powerful artifacts that grant players unique elemental abilities. There are four types:

| Fragment | Emoji | Material | Abilities |
|----------|-------|----------|-----------|
| **Burning Fragment** | 🔥 | Blaze Powder | Dragon's Wrath, Infernal Dominion |
| **Agility Fragment** | 💨 | Phantom Membrane | Draconic Surge, Wing Burst |
| **Immortal Fragment** | 🛡️ | Totem of Undying | Draconic Reflex, Essence Rebirth |
| **Corrupted Core** | 👁️ | Nether Star | Dread Gaze, Life Devourer |

---

## Obtaining Fragments

### Crafting

Fragments are crafted using special recipes. All fragments require a **vanilla Heavy Core** (found in Ancient Cities) as the center ingredient.

#### Crafting Recipes

**Burning Fragment** (2 max per player)
```
N S N   (N = Netherite Upgrade, S = Bolt Armor Trim)
I H I   (I = Netherite Ingot, H = Heavy Core)
N R N   (N = Netherite Upgrade, R = Rib Armor Trim)
```

**Agility Fragment** (2 max per player)
```
B F A   (B = Breeze Rod, F = Flow Armor Trim, A = Ancient Debris)
D H D   (D = Diamond Block, H = Heavy Core)
A F B   (A = Ancient Debris, F = Flow Armor Trim, B = Breeze Rod)
```

**Immortal Fragment** (2 max per player)
```
T A T   (T = Totem of Undying, A = Ancient Debris)
D H D   (D = Diamond Block, H = Heavy Core)
T A T   (T = Totem of Undying, A = Ancient Debris)
```

**Corrupted Core** (1 max per player)
```
R H K   (R = Wither Rose, H = Heavy Core, K = Wither Skull)
N S N   (N = Netherite Block, S = Nether Star)
K H R   (K = Wither Skull, H = Heavy Core, R = Wither Rose)
```

### Craft Limit Enforcement

Each player is limited in how many of each fragment they can craft:
- **Burning Fragment**: 2 max
- **Agility Fragment**: 2 max
- **Immortal Fragment**: 2 max
- **Corrupted Core**: 1 max

When you craft a fragment, a themed broadcast message is sent to all players:
- 🔥 Burning: "The flames of the ancient dragon acknowledge {player} has forged the Burning Fragment!"
- 💨 Agility: "The winds of the ancient dragon acknowledge {player} has forged the Agility Fragment!"
- 🛡️ Immortal: "The earth of the ancient dragon acknowledges {player} has forged the Immortal Fragment!"
- 👁️ Corrupted: "The void of the ancient dragon acknowledges {player} has forged the Corrupted Core!"

---

## Equipping Fragments

### Right-Click to Equip (while holding fragment in main hand)
1. Hold the fragment in your main hand
2. Right-click to equip it to your offhand
3. You will see activation particles and hear a sound

### Using Commands
```
/fire equip      # Equip Burning Fragment
/agile equip     # Equip Agility Fragment
/immortal equip  # Equip Immortal Fragment
/corrupt equip   # Equip Corrupted Core
```

**Note**: Only one fragment can be equipped at a time. Equipping a new fragment will unequip the previous one.

---

## Using Abilities

Each fragment has 2 abilities. Use the command format:

```
/<fragment-type> <ability-number>
```

### Ability Commands

**Burning Fragment (🔥 Fire)**
```
/fire 1   # Dragon's Wrath - Fireball that chases nearest enemy (40s cooldown)
/fire 2   # Infernal Dominion - Ring of fire that damages nearby enemies (60s cooldown)
```

**Agility Fragment (💨 Wind)**
```
/agile 1   # Draconic Surge - Speed II + Jump II for 1.5 seconds (45s cooldown)
/agile 2   # Wing Burst - Launch upward 20 blocks, push enemies away (2min cooldown)
```

**Immortal Fragment (🛡️ Earth)**
```
/immortal 1   # Draconic Reflex - 75% damage reduction for 15 seconds (2min cooldown)
/immortal 2   # Essence Rebirth - Second life with buffed respawn if killed (8min cooldown)
```

**Corrupted Core (👁️ Void)**
```
/corrupt 1   # Dread Gaze - Complete freeze on next hit (3min cooldown)
/corrupt 2   # Life Devourer - 50% life steal from all damage for 20 seconds (2min cooldown)
```

### HUD Display

When a fragment is equipped, your HUD displays:
- Ability 1 name and cooldown status
- Ability 2 name and cooldown status
- "Ready" indicator when cooldown is complete

---

## Passive Bonuses

Each fragment provides passive effects while equipped:

| Fragment | Passive Bonus |
|----------|---------------|
| Burning Fragment | Fire Resistance |
| Agility Fragment | Permanent Speed I |
| Immortal Fragment | 25% knockback reduction, +2 hearts health boost |
| Corrupted Core | Night Vision, Invisible to creepers |

---

## Withdrawing Abilities

Use the `/withdrawability` command to remove your equipped fragment's abilities:

```
/withdrawability
```

**Note**: This removes abilities but keeps the fragment item in your inventory. You can re-equip by right-clicking or using `/<type> equip`.

---

## Fragment Restrictions

### Container Restrictions

Elemental Dragon fragments **cannot** be placed in any containers:
- Chests, Barrels, Trapped Chests
- Hoppers, Droppers, Dispensers
- Furnaces, Blast Furnaces, Smokers
- Brewing Stands
- Ender Chests, Shulker Boxes
- Anvils, Smithing Tables, Grindstones
- Stonecutters, Lecterns, Cartography Tables

If you try to place a fragment in a container, the action will be blocked and you'll see:
```
Elemental Dragon fragments cannot be stored in containers.
```

### Item Loss Detection

If you **drop** a fragment by throwing it on the ground, your abilities will be automatically withdrawn:
```
Your Burning Fragment abilities have been withdrawn!
```

### Fireproof Items

Fragment items have a special enchantment that makes them appear to glow. They cannot be destroyed by lava or fire.

---

## Fragment Item Properties

Fragment items have the following properties:
- **Enchanted Glint**: Items have a visual glow effect
- **Fireproof**: Cannot be burned by lava or fire
- **Unique Lore**: Each fragment has unique description and ability information
- **Bind on Equip**: Abilities are tied to the player, not the item

---

## Troubleshooting

### "You don't have any fragment abilities equipped"
- Make sure you have a fragment in your offhand
- Right-click while holding a fragment to equip it
- Or use `/<type> equip` to equip

### "Crafting limit reached"
- You've already crafted the maximum number of this fragment type
- Check your crafted count with `/craft <type>`

### "Fragment abilities have been withdrawn"
- You dropped your fragment on the ground
- Pick up the fragment and re-equip it

### "Elemental Dragon fragments cannot be stored in containers"
- Fragments cannot be placed in chests or other containers
- Keep fragments in your inventory only

---

## Customizing Your Countdown Display

You can customize your countdown progress bar style to match your preference. Each player can set their own style, and the preference persists across server restarts.

### Setting Your Countdown Style

**Syntax**: `/<type> setcountdownsym <style> [width]`

**Available Styles**:
| Style | Description |
|-------|-------------|
| `TILES` | Unicode tile progress bar with rainbow gradient (default) |
| `MOON` | Moon phase emoji (new moon → full moon) |
| `CLOCK` | Clock emoji (12 hour positions) |
| `SHADE` | Block shading (░ → ▒ → ▓ → █) |
| `BLOCK1` - `BLOCK4` | Block variants with different character widths |
| `TRIANGLE` | Triangle fill (▹ → ▸) |

**Width Parameter**: 1-10 (optional, defaults to 1 for most styles)
- Higher values create more animation states
- Example: `MOON 2` = 10 states, `CLOCK 2` = 24 states

**Examples**:
```
/fire setcountdownsym MOON 2        # Moon phases, 2 characters wide
/agile setcountdownsym CLOCK 1       # Clock faces, 1 character wide
/immortal setcountdownsym SHADE 3    # Shading blocks, 3 characters wide
/corrupt setcountdownsym TILES       # Default tiles style
```

**Note**: Admins can use `/ed setglobalcountdownsym` to set a global default for all players who haven't set their own preference.

---

## Command Summary

| Command | Description |
|---------|-------------|
| `/fire 1` / `/fire 2` | Use Burning Fragment abilities |
| `/agile 1` / `/agile 2` | Use Agility Fragment abilities |
| `/immortal 1` / `/immortal 2` | Use Immortal Fragment abilities |
| `/corrupt 1` / `/corrupt 2` | Use Corrupted Core abilities |
| `/<type> equip` | Equip fragment to offhand |
| `/<type> status` | View fragment status and cooldowns |
| `/<type> setcountdownsym <style> [width]` | Set your personal countdown progress bar style |
| `/<type> help` | View ability information |
| `/withdrawability` | Remove equipped abilities (keep item) |
| `/craft <type>` | View crafting recipe for a fragment |
