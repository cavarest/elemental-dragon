---
layout: default
title: Admin Commands
nav_order: 3
has_children: false
permalink: /admin/commands/
---

# Admin Commands Reference

This document provides a comprehensive reference for all administrative commands in the Elemental Dragon plugin.

## Table of Contents

- [Operator Setup](#operator-setup)
- [Command Summary](#command-summary)
- [Give Commands](#give-commands)
- [Info Commands](#info-commands)
- [Cooldown Management](#cooldown-management)
- [Player Selectors](#player-selectors)
- [Permissions](#permissions)
- [Element Names](#element-names)
- [Player Commands](#player-commands-reference)

---

## Operator Setup

### Becoming an Operator

Before you can use admin commands, you must be configured as an operator.

#### Docker Development Environment

If using the provided Docker setup, operators are configured automatically:

1. **Default Operator**: The username `posiflow` is automatically configured as an operator (defined in `.env`)

2. **Add More Operators**: Edit the `.env` file:
   ```bash
   OFFLINE_OPS=posiflow,player2,admin3
   ```

3. **Restart Server**:
   ```bash
   ./stop-server.sh
   ./start-server.sh
   ```

#### Production Server (Non-Docker)

For production servers, use the standard Minecraft operator commands:

1. **In-Game**: `/op <username>`

2. **Server Console**:
   ```bash
   # Stop the server
   # Edit ops.json directly
   {
     "uuid": "player-uuid",
     "name": "username",
     "level": 4,
     "bypassesPlayerLimit": false
   }
   ```

3. **Using itzg/minecraft-server** (non-offline mode):
   ```bash
   # Set OPS environment variable
   OPS=username1,username2
   ```

### Verify Operator Status

Check if you're an operator:

```bash
# In-game
/op list  # Shows all operators

# RCON
docker exec -it papermc-elementaldragon rcon-cli
> op list

# Check ops.json
docker exec papermc-elementaldragon cat /data/ops.json
```

### Troubleshooting Operator Issues

If you cannot run admin commands:

1. **Verify you're listed as operator**: `/op list`
2. **Check username case-sensitive**: `Posiflow` ≠ `posiflow`
3. **Restart server after adding operator**: `docker-compose restart`
4. **Check ops.json exists**: See [Docker Deployment Guide](/admin/docker/) for details

---

## Command Summary

| Command | Description | Permission |
|---------|-------------|------------|
| `/ed give <player-ref> <ingredients\|equipment> <element>` | Give items to players | `elementaldragon.admin` |
| `/ed setfragment <player-ref> <element>` | Equip fragment for player | `elementaldragon.admin` |
| `/ed info <element>` | Show fragment information | `elementaldragon.admin` |
| `/ed status <player-ref>` | Show player's fragment status | `elementaldragon.admin` |
| `/ed setcooldown <player> <element> <ability> <seconds>` | Set player cooldown | `elementaldragon.admin` |
| `/ed clearcooldown <player> [element]` | Clear cooldowns | `elementaldragon.admin` |
| `/ed setglobalcooldown <element> <ability> <seconds>` | Configure default cooldowns | `elementaldragon.admin` |

---

## Give Commands

### Give Items
**Syntax**: `/ed give <player-ref> <type> <element-kind>`

**Parameters**:
- `<player-ref>`: Player selector (@p, @s, @a) or player name
- `<type>`: `ingredients` or `equipment`
- `<element-kind>`: `fire`, `agile`, `immortal`, or `corrupt`

**Examples**:
```
/ed give @p equipment fire          # Give Burning Fragment to nearest player
/ed give PlayerName ingredients agile # Give Agility Fragment crafting materials
/ed give @a equipment immortal      # Give Immortal Fragment to all players
```

**What Gets Given**:

**Equipment**:
- The specified fragment item

**Ingredients**:
- Heavy Core (1x) - now a **vanilla Minecraft item** found in Ancient Cities
- All materials needed to craft the fragment
- Complete ingredient set for immediate crafting

**Note**: Heavy Core is a vanilla Minecraft item found in Ancient City chests. Players no longer need to craft Heavy Core - they can find it directly in the world or receive it from admins.

---

## Set Fragment Command

### Equip Player Fragment
**Syntax**: `/ed setfragment <player-ref> <element>`

Equip a fragment for a player (replaces currently equipped fragment).

**Parameters**:
- `<player-ref>`: Player selector or name
- `<element>`: `fire`, `agile`, `immortal`, or `corrupt`

**Examples**:
```
/ed setfragment @p immortal          # Equip Immortal Fragment for nearest player
/ed setfragment PlayerName agile    # Equip Agility Fragment for PlayerName
/ed setfragment @a corrupt          # Equip Corrupted Core for all players
```

**Use Case**: Quickly equip a fragment for a player without giving them the item.

---

## Info Commands

### Fragment Info
**Syntax**: `/ed info <element>`

Displays detailed information about a fragment including abilities, cooldowns, and passive bonuses.

**Parameters**:
- `<element>`: `fire`, `agile`, `immortal`, `corrupt`, or `lightning`

**Examples**:
```
/ed info fire                         # Show Burning Fragment info
/ed info corrupt                      # Show Corrupted Core info
/ed info lightning                    # Show Lightning Strike info
```

### Player Status
**Syntax**: `/ed status <player-ref>`

Shows player's equipped fragment and cooldown states.

**Parameters**:
- `<player-ref>`: Player selector or name

**Examples**:
```
/ed status @p                         # Show nearest player's status
/ed status PlayerName                 # Show PlayerName's status
```

---

## Cooldown Management

### Set Player Cooldown
**Syntax**: `/ed setcooldown <player-ref> <element> <ability-num> <seconds>`

Set cooldown for a specific ability of an element.

**Parameters**:
- `<player-ref>`: Player selector or name
- `<element>`: `lightning`, `fire`, `agile`, `immortal`, or `corrupt`
- `<ability-num>`: `1`, `2`, or `all` (for both abilities)
- `<seconds>`: Cooldown duration in seconds (use 0 to clear cooldown)

**Examples**:
```
/ed setcooldown @p fire 1 60       # Set Fire ability 1 to 60s cooldown
/ed setcooldown PlayerName agile 2 120  # Set Agility ability 2 to 2 minutes
/ed setcooldown @s immortal all 90     # Set both Immortal abilities to 90s
/ed setcooldown @a corrupt 1 0       # Clear Dread Gaze cooldown for all players
```

**Ability Reference**:
- ⚡ Lightning: Lightning Strike (60s cooldown)
- 🔥 Fire: 1 = Dragon's Wrath (2m), 2 = Infernal Dominion (3m)
- 💨 Agility: 1 = Draconic Surge (45s), 2 = Wing Burst (2m)
- 🛡️ Immortal: 1 = Draconic Reflex (2m), 2 = Essence Rebirth (8m)
- 👁 Corrupted: 1 = Dread Gaze (3m), 2 = Life Devourer (2m)

### Clear Player Cooldown
**Syntax**: `/ed clearcooldown <player-ref> [element]`

Clear cooldowns for a player.

**Parameters**:
- `<player-ref>`: Player selector or name
- `[element]`: Optional - specific element to clear (omit to clear all)
  - Valid values: `lightning`, `fire`, `agile`, `immortal`, `corrupt`

**Examples**:
```
/ed clearcooldown @p                # Clear all cooldowns
/ed clearcooldown PlayerName fire   # Clear only fire cooldowns
/ed clearcooldown @s lightning      # Clear lightning cooldown
```

### Get Player Cooldowns
**Syntax**: `/ed getcooldown <player-ref>`

Display all active cooldowns for a player with status for each element.

**Example**:
```
/ed getcooldown @s
```

### Set Global Cooldown
**Syntax**: `/ed setglobalcooldown <element> <ability-num> <seconds>`

Configure the default cooldown duration for an element's ability.

**Parameters**:
- `<element>`: `lightning`, `fire`, `agile`, `immortal`, or `corrupt`
- `<ability-num>`: `1` or `2`
- `<seconds>`: Default cooldown duration in seconds (use 0 to disable cooldown)

**Examples**:
```
/ed setglobalcooldown fire 1 60       # Set Dragon's Wrath default to 60s
/ed setglobalcooldown immortal 2 480  # Set Essence Rebirth default to 8 minutes
/ed setglobalcooldown corrupt 1 0       # Disable Dread Gaze cooldown entirely
```

**Current Defaults**:
- ⚡ Lightning (Lightning Strike): 60s (1 minute)
- 🔥 Fire (Dragon's Wrath): 2 minutes, (Infernal Dominion): 3 minutes
- 💨 Agility (Draconic Surge): 45 seconds, (Wing Burst): 2 minutes
- 🛡️ Immortal (Draconic Reflex): 2 minutes, (Essence Rebirth): 8 minutes
- 👁 Corrupted (Dread Gaze): 3 minutes, (Life Devourer): 2 minutes

### View Global Cooldowns
**Syntax**: `/ed getglobalcooldown`

Display all global cooldown configurations in a formatted table with emojis.

**Example Output**:
```
═══════════════════════════════════════
    ⚙️  GLOBAL COOLDOWN CONFIGURATION  ⚙️
═══════════════════════════════════════

  ⚡ Lightning:
    Ability 1 (Lightning Strike): 60s

  🔥 Fire Fragment:
    Ability 1 (Dragon's Wrath): 2m 0s
    Ability 2 (Infernal Dominion): 3m 0s

  💨 Agility Fragment:
    Ability 1 (Draconic Surge): 45s
    Ability 2 (Wing Burst): 2m 0s

  🛡️ Immortal Fragment:
    Ability 1 (Draconic Reflex): 2m 0s
    Ability 2 (Essence Rebirth): 8m 0s

  👁 Corrupted Core:
    Ability 1 (Dread Gaze): 3m 0s
    Ability 2 (Life Devourer): 2m 0s
```

---

## Player Selectors

All commands support Minecraft-style player selectors:

- `@p` - Nearest player (you, if executed by player)
- `@s` - Self (command executor)
- `@a` - All online players
- Or use exact player name

---

## Permissions

All admin commands require the `elementaldragon.admin` permission (default: op).

---

## Element Names

Valid element names (canonical only):
- `lightning` - Dragon Egg Lightning Strike
- `fire` - Burning Fragment
- `agile` - Agility Fragment
- `immortal` - Immortal Fragment
- `corrupt` - Corrupted Core

**Note**: Other aliases (burning, agility, corrupted) are NOT accepted in admin commands for consistency.

---

## Player Commands Reference

These commands are available to all players (permission: `elementaldragon.withdrawability`, default: true):

### Withdraw Abilities
**Syntax**: `/withdrawability`

Withdraws the currently equipped fragment's abilities. The fragment item remains in your inventory.

**Example**:
```
/withdrawability
```

**Output**:
```
Your fragment abilities have been withdrawn.
The fragment item remains in your inventory.
```

**Use Case**: Unequip your fragment without dropping the item.

---