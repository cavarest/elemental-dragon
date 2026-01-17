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

Elemental Dragon provides one dragon egg ability and four fragments. Each fragment grants two active abilities and one passive bonus when equipped.

| Fragment | Element | Material | Command |
|----------|---------|----------|---------|
| **Burning Fragment** | Fire | Blaze Powder | `/fire` |
| **Agility Fragment** | Wind | Phantom Membrane | `/agile` |
| **Immortal Fragment** | Earth | Diamond | `/immortal` |
| **Corrupted Core** | Void | Nether Star | `/corrupt` |

**Note**: Dragon Egg (Lightning Strike) is not a fragment. Hold dragon egg in offhand to use.

---

## Dragon Egg Ability

### Lightning Strike

**Requirement**: Dragon Egg must be held in offhand slot.

**Function**: Strikes target with three sequential purple lightning bolts.

**Specifications**
- Cooldown: 60 seconds
- Strike Count: 3
- Strike Interval: 0.5 seconds (10 ticks)
- Damage per Strike: 4.0 (2 hearts, armor-ignoring)
- Maximum Range: 50 blocks
- Targeting: Crosshair ray-trace with viewing cone fallback

**Usage**
```
/lightning 1
```

**Behavior**
- Uses ray-tracing from player's eyes in look direction
- Searches for living entities within 50 blocks
- If ray-trace finds no target, searches nearest entity in 90° viewing cone
- First strike hits initial target, then two more strikes at 0.5 second intervals
- If current target dies during sequence: finds next closest target
- Can execute up to 3 strikes on 3 different targets

**Interruption Conditions**
- Dragon egg removed from offhand: ability stops, cooldown set
- No valid targets found: ability ends early

---

## Obtaining Fragments

### Crafting

All fragments require a **vanilla Heavy Core** (found in Ancient Cities) as the center ingredient.

**Burning Fragment** (2 max per player)
- Surrounds Heavy Core with fire-related materials
- Use `/craft` to view recipe

**Agility Fragment** (2 max per player)
- Surrounds Heavy Core with wind-related materials
- Use `/craft` to view recipe

**Immortal Fragment** (2 max per player)
- Surrounds Heavy Core with earth-related materials
- Use `/craft` to view recipe

**Corrupted Core** (1 max per player)
- Surrounds Heavy Core with void-related materials
- Use `/craft` to view recipe

### Viewing Recipes

Use `/craft` to view current recipes and requirements.

### Crafting Limits

| Fragment | Maximum per Player |
|----------|-------------------|
| Burning Fragment | 2 |
| Agility Fragment | 2 |
| Immortal Fragment | 2 |
| Corrupted Core | 1 |

Limits enforced during crafting. Attempting to craft beyond limit shows current count and maximum.

---

## Burning Fragment

### Command
```
/fire [1|2|equip|status|help]
```

### Passive
- **Fire Resistance**: 100% immunity to fire damage (lava, fire blocks, fire attacks)

### Ability 1: Dragon's Wrath

**Function**: Launches a homing fireball that targets the nearest hostile entity within crosshairs.

**Specifications**
- Cooldown: 2 minutes
- Damage: 6.0 (3 hearts, armor-ignoring)
- Area of Effect: 5 blocks radius on impact
- Homing Duration: 0.5 seconds (10 ticks)
- Target Range: 50 blocks
- Fireball Velocity: 1.5 blocks/tick

**Behavior**
1. Searches for nearest hostile mob/player within 50 blocks
2. Launches fireball in direction of crosshair
3. Fireball homes toward target for 0.5 seconds
4. On impact: explodes and deals 6.0 damage to all entities in 5-block radius
5. Damage ignores armor (applies to base health)

**Usage**
```
/fire 1
```

### Ability 2: Infernal Dominion

**Function**: Creates a 10-block radius ring of fire around the player that damages entities over time.

**Specifications**
- Cooldown: 3 minutes
- Radius: 10 blocks
- Duration: 10 seconds
- Damage Rate: 1.0 damage per second (0.5 hearts per second)

**Behavior**
1. Fire ring appears at 10-block radius from player
2. All entities within ring take 1.0 damage per second
3. Fire persists for 10 seconds
4. Player is not damaged by own fire

**Usage**
```
/fire 2
```

---

## Agility Fragment

### Command
```
/agile [1|2|equip|status|help]
```

### Passive
- **Speed I**: Permanent 30% movement speed increase
- **Water Walking**: Can walk on water (permanent effect)

### Ability 1: Draconic Surge

**Function**: Grants burst of forward speed with jump enhancement and water walking.

**Specifications**
- Cooldown: 45 seconds
- Duration: 1 second (20 ticks)
- Distance: 20 blocks forward
- Velocity: 1.0 blocks/tick
- Fall Damage Protection: 10 seconds (200 ticks)

**Behavior**
1. Applies forward velocity in direction player is facing
2. Moves player 20 blocks over 1 second
3. Prevents fall damage for 10 seconds after activation
4. Allows jumping during surge

**Usage**
```
/agile 1
```

### Ability 2: Wing Burst

**Function**: Pushes all nearby entities away from player in all directions.

**Specifications**
- Cooldown: 2 minutes
- Radius: 8 blocks
- Push Distance: 20 blocks
- Push Duration: 2 seconds
- Fall Slow: 10 seconds (200 ticks)

**Behavior**
1. Detects all entities within 8-block radius
2. Applies velocity away from player
3. Pushes entities 20 blocks away over 2 seconds
4. Applies slow-fall effect to pushed entities for 10 seconds

**Usage**
```
/agile 2
```

---

## Immortal Fragment

### Command
```
/immortal [1|2|equip|status|help]
```

### Passive
- **Knockback Reduction**: 25% reduction from all knockback sources
- **Health Boost**: +2 hearts (4 HP) permanent max health increase

### Ability 1: Draconic Reflex

**Function**: Grants 20% chance to avoid damage and reflect it back to attacker.

**Specifications**
- Cooldown: 2 minutes
- Duration: 15 seconds
- Dodge Chance: 20% (1 in 5)
- Reflection Damage: 100% of original damage

**Behavior**
1. When active, any damage taken has 20% chance to trigger
2. If triggered: damage negated
3. Attacker receives original damage amount
4. Works against melee, ranged, and environmental damage
5. Duration: 15 seconds

**Usage**
```
/immortal 1
```

### Ability 2: Essence Rebirth

**Function**: Enhanced respawn with diamond armor and supplies upon death.

**Specifications**
- Cooldown: 8 minutes
- Duration: 30 seconds (auto-triggers on death if active)

**Behavior**
1. When activated, grants death protection for 30 seconds
2. If player dies while protected:
   - Respawn at death location (not bed)
   - Granted full set of diamond armor
   - Granted diamond sword with Sharpness V
   - Granted 32 golden apples
   - Granted 64 cooked beef
3. If ability expires without death, cooldown is reset (no penalty)

**Usage**
```
/immortal 2
```

**Note**: Use before entering combat. Ability must be active at moment of death.

---

## Corrupted Core

### Command
```
/corrupt [1|2|equip|status|help]
```

### Passive
- **Night Vision**: Permanent night vision effect
- **Creeper Avoidance**: Creepers will not target player (invisible to them)
- **Enderman Immunity**: Endermen will not become aggressive when looked at

### Ability 1: Dread Gaze

**Function**: Next melee attack freezes target completely for 10 seconds.

**Specifications**
- Cooldown: 3 minutes (starts when target is hit)
- Freeze Duration: 10 seconds
- Activation: Remains active until target is hit (no expiration)

**Behavior Phase 1: Activation**
1. Use `/corrupt 1` to activate
2. State: "READY TO STRIKE"
3. No cooldown is set at activation
4. No expiration timer - persists indefinitely
5. Visual: Void particles appear around player

**Behavior Phase 2: Hit**
1. When player hits any entity with melee attack:
   - Target frozen for 10 seconds
   - Target cannot move, break blocks, place blocks, interact
   - 3-minute cooldown starts now
   - "READY TO STRIKE" state consumed
   - Attacker's HUD shows "Foe Frozen" countdown (10s → 0s)

**Behavior Phase 3: Freeze Effects**
Target receives maximum-level potion effects:
- Slowness 255 (complete movement prevention)
- Mining Fatigue 255 (cannot break blocks)
- Weakness 255 (reduced attack damage)
- Hunger 255 (cannot eat)

**HUD Feedback**
- During READY TO STRIKE: HUD shows prepared state
- After hit: Attacker sees `👁 Foe Frozen (10s)` countdown
- Victim sees `⚠ DEBUFFS ⚠` section with freeze duration

**Cancel Conditions**
- Unequip fragment: clears READY TO STRIKE state
- Using `/corrupt 1` again: replaces previous READY TO STRIKE

**Usage**
```
/corrupt 1
# Then hit any entity with melee attack
```

### Ability 2: Life Devourer

**Function**: Grants 50% life steal on all damage dealt for 20 seconds.

**Specifications**
- Cooldown: 2 minutes
- Duration: 20 seconds
- Life Steal: 50% of damage dealt

**Behavior**
1. When active, all damage dealt by player heals player
2. Healing = Damage × 0.5
3. Works with: melee, bows, any damage source
4. Healing capped at player's max health
5. Persists through weapon switches

**Usage**
```
/corrupt 2
```

---

## Game Mechanics

### Equipping Fragments

**Method 1: Right-Click**
1. Hold fragment in main hand
2. Right-click (air or block)
3. Fragment moves to offhand automatically
4. Abilities and passive activate

**Method 2: Command**
```
/fire equip
/agile equip
/immortal equip
/corrupt equip
```

### Unequipping

**Method 1: `/withdrawability` Command**
```
/withdrawability
```
Removes fragment abilities while keeping the fragment in your inventory:
- Deactivates all passive bonuses
- Cancels active ability states (including READY TO STRIKE)
- Removes active abilities (Life Devourer, Draconic Reflex, etc.)
- Fragment item remains in your inventory
- Cooldowns are preserved (not reset)

**Method 2: Drop Fragment**
```
Press Q key (drop hotbar item)
```
Dropping the equipped fragment will:
- Unequip that fragment only
- Deactivate all passive bonuses for that fragment
- Cancel active ability states for that fragment
- Other equipped fragments remain active

**Method 3: `/clear` Command (Vanilla Minecraft)**
```
/clear
```
The vanilla Minecraft `/clear` command removes all items from your inventory:
- All fragments are unequipped (if in inventory)
- All passive bonuses are removed
- All active abilities are canceled
- All active states are cleared (including READY TO STRIKE)
- Cooldowns are preserved (not reset)

**⚠️ Important Note: Cooldown Persistence**
Cooldowns **DO NOT reset** when you:
- Drop and re-equip a fragment
- Use `/withdrawability` and re-equip
- Use `/clear` and re-craft/re-equip

This spam prevention means you cannot bypass cooldowns by quickly unequipping and re-equipping fragments.

### Dropping Fragments

When a fragment is dropped:
- Only the dropped fragment is unequipped if it was equipped
- Other fragments remain equipped
- Example: Dropping Burning Fragment while Immortal equipped → Immortal stays equipped
- Fragments are indestructible when dropped (immune to fire, lava, cactus, and all damage sources)
- **Cooldowns persist** - re-equipping will not reset your cooldowns

### Cooldown System

**Cooldown Persistence**
- ✅ Survives player logout/login
- ✅ Survives server restart
- ✅ Survives fragment unequip/equip (spam prevention)
- ✅ Survives `/clear` command and item dropping
- ✅ Cleared on player death (fair respawn mechanics)
- ✅ Persists across fragment switches

**Cooldown Display Format**
- HUD shows remaining cooldown time
- Format: `In Xm Ys` or `In Ys` (X = minutes, Y = seconds)
- Fixed-width formatting prevents UI jitter
- At 100% completion: Displays "READY" in green

---

## HUD Display

### Requirements
- Dragon egg in offhand: Shows lightning ability
- Fragment equipped: Shows fragment info + both abilities

### Format
```
FRAGMENT ICON FRAGMENT NAME
  Passive description (always shown)
  👁 Foe Frozen (10s)

⚠ DEBUFFS ⚠
[Icon] Debuff Name (seconds)

✨ ACTIVE ABILITIES ✨
[Icon] Ability Name (duration)  [Progress] ACTIVE (seconds)

⚔ AVAILABLE ABILITIES ⚔
[Icon] Ability Name (duration)  [Progress] READY
[Icon] Ability Name (duration)  [Progress] In Xm Ys
```

**Update Frequency**: 1 second

### HUD Features

#### Passive Display (Always Shown)
When a fragment is equipped, the HUD always shows its passive benefit:

**Burning Fragment**
- `100% Fire Immunity` - Fire Resistance is always active

**Agility Fragment**
- `30% Speed Boost` - Speed effect is always active

**Immortal Fragment**
- `Resists Death` - Resistance and health boost are always active

**Corrupted Core**
- `See in Darkness` - Night Vision is always active

#### Foe Frozen Countdown
When using Dread Gaze (`/corrupt 1`), after hitting a target:
- Attacker's HUD shows `👁 Foe Frozen (10s)` countdown
- Counts down from 10 to 0 seconds
- Shows how much longer the target remains frozen
- Automatically disappears when freeze expires

#### Active Abilities Section
Shows abilities that are currently active with countdown timers:

**Life Devourer** (Corrupt 2)
- When active, shows `👁 /corrupt 2 (20s)  [███████████████████████████████] ACTIVE (18s)`
- Counts down from 20 to 0 seconds
- Shows how much longer life steal effect remains

#### Debuffs Section
When a player is affected by debuffs (like being frozen by Dread Gaze):
- `⚠ DEBUFFS ⚠` section appears
- Shows active debuff with countdown timer
- Example: `👁 Dread Gaze Freeze (10s)` with reverse progress bar
- Updates in real-time as debuff duration decreases

### Progress Bar Variants

The plugin supports multiple progress bar styles for visual feedback.

#### TILES (Default)
Rainbow gradient Unicode tile progress bar.

**Symbols**: ▱ (empty), ▰ (filled)
**States**: 7 tiles
**Animation**: Smooth transitions between states
**Colors**: Rainbow gradient (red → yellow → green → cyan → blue → magenta)

Example: `▱▱▰▰▰▰▰` (4/7 filled)

#### SHADE
Block shading progression.

**Symbols**: ░ → ▒ → ▓ → █
**States**: 4 × width (default width 1 = 4 states)
**Animation**: Linear fill left-to-right

Example progression (width 4):
```
░░░░
▒░░░
▓░░░
█░░░
█▒░░
█▓░░
█▓▒░
██░░
██▒░
██▓░
███░
███▒
███▓
████
```

#### BLOCK
Binary block characters with configurable width.

**Variants**:
- BLOCK1: ▏ (quarter block)
- BLOCK2: ▍ (half block)
- BLOCK3: ▋ (three-quarter block)
- BLOCK4: ▉ (full block)

**States**: width + 1 (each char is space or symbol)
**Animation**: Binary fill (each position fills completely)

Example progression (BLOCK1, width 6):
```
▏
▏▏
▏▏▏
▏▏▏▏
▏▏▏▏▏
▏▏▏▏▏▏
```

#### TRIANGLE
Triangle fill progression.

**Symbols**: ▹ (empty), ▸ (filled)
**States**: width + 1 (default width 6 = 7 states)
**Animation**: Binary fill left-to-right

Example progression (width 6):
```
▹▹▹▹▹▹
▸▹▹▹▹▹
▸▸▹▹▹▹
▸▸▸▹▹▹
▸▸▸▸▹▹
▸▸▸▸▸▹
▸▸▸▸▸▸
```

#### MOON
Moon phase emoji progression.

**Symbols**: 🌕 → 🌔 → 🌓 → 🌒 → 🌑
**States**: 5 × width (default width 1 = 5 states)
**Animation**: Full moon to new moon (reversed)

Example progression (width 1):
```
🌕
🌔
🌓
🌒
🌑
```

#### CLOCK
Clock face progression.

**Symbols**: 🕐 🕑 🕒 🕓 🕔 🕕 🕖 🕗 🕘 🕙 🕚 🕛
**States**: 12 × width (default width 1 = 12 states)
**Animation**: 12 hour positions

Example progression (width 1):
```
🕐
🕑
🕒
🕓
🕔
🕕
🕖
🕗
🕘
🕙
🕚
🕛
```

### Progress Bar Behavior

**At 100% Completion**: Displays "READY" in green text (no progress bar shown)

**Below 100%**: Shows animated progress bar based on selected variant

**Animation**: Bars animate between states for smooth visual feedback

**Color Coding**:
- Cooldown hot (0-33%): Red
- Cooldown warm (33-66%): Gold
- Cooldown cool (66-99%): Green
- Active ability: Magenta
- Ready: Green

---

## Operator Commands

### Cooldown Management

**Global Cooldown Override**
```
/ed setglobalcooldown <element> <ability> <seconds>
```
Sets cooldown for all players. Use 0 to disable cooldown entirely.

Elements: `lightning`, `fire`, `agile`, `immortal`, `corrupt`
Abilities: `1` or `2`
Seconds: Numeric value (0 to disable)

**Player-Specific Cooldown**
```
/ed setcooldown <player> <element> <ability> <seconds>
```
Sets cooldown for a specific player. Use 0 to clear their cooldown.

**Clear All Cooldowns**
```
/ed clearcooldown <player> [element]
```
Clears all cooldowns (or specific element) for a player.

### Fragment Management

**Give Fragment to Player**
```
/ed give <player> equipment <fragment>
```
Gives a fragment to a player's inventory.

Fragments: `fire`, `agile`, `immortal`, `corrupt`

**Set Player's Fragment**
```
/ed setfragment <player> <fragment>
```
Equips a fragment for a player (replaces currently equipped fragment).

### Information Commands

**Fragment Info**
```
/ed info <fragment>
```
Displays detailed information about a fragment.

**Player Status**
```
/ed status <player>
```
Shows player's equipped fragment and cooldown states.

---

## Command Summary

### Fragment Commands
```
/fire [1|2|equip|status|help]
/agile [1|2|equip|status|help]
/immortal [1|2|equip|status|help]
/corrupt [1|2|equip|status|help]
/lightning 1
/withdrawability    # Withdraw equipped fragment abilities
/craft
```

**Fragment Ability Management**:
- `/withdrawability` - Removes abilities while keeping fragment equipped
  - Deactivates passive bonuses (Fire Resistance, Speed, etc.)
  - Cancels active abilities (Life Devourer, Draconic Reflex, etc.)
  - Clears active states (including READY TO STRIKE)
  - Fragment item remains in inventory
  - Cooldowns are preserved (not reset)

**Using `/clear` (Vanilla Command)**:
- Removes all inventory items including fragments
- All abilities are unequipped and passives removed
- **Cooldowns persist** (spam prevention)

**Important Notes**:
- ⚠️ Cooldowns survive `/withdrawability`, `/clear`, and fragment dropping
- ⚠️ You cannot bypass cooldowns by unequipping and re-equipping
- ✅ Only death clears cooldowns (fair respawn mechanics)

### Operator Commands
```
/ed setglobalcooldown <element> <ability> <seconds>
/ed setcooldown <player> <element> <ability> <seconds>
/ed clearcooldown <player> [element]
/ed give <player> equipment <fragment>
/ed setfragment <player> <fragment>
/ed info <fragment>
/ed status <player>
```

---

## Troubleshooting

### "You don't have any fragment abilities equipped"
- Make sure you have a fragment in your offhand
- Right-click while holding a fragment to equip it
- Or use `/<type> equip` to equip
- If you used `/clear`, you need to re-craft or re-equip your fragment

### "Crafting limit reached"
- You've already crafted the maximum number of this fragment type
- Check your crafted count with `/craft`

### "Fragment abilities have been withdrawn"
- You used `/withdrawability` to remove abilities
- Or you dropped your equipped fragment
- Re-equip the fragment to restore abilities
- Note: Cooldowns are preserved (not reset)

### "Why is my cooldown still active after re-equipping?"
- This is intentional spam prevention
- Cooldowns persist when you:
  - Drop and re-equip a fragment
  - Use `/withdrawability` and re-equip
  - Use `/clear` and re-craft/re-equip
- Only death clears cooldowns (fair respawn mechanics)
- This prevents bypassing cooldowns by quickly unequipping and re-equipping

### Dread Gaze "READY TO STRIKE" won't expire
- This is intentional - Dread Gaze persists until you hit a target
- Unequip fragment or hit a target to clear the state
