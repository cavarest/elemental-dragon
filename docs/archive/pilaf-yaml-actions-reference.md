# PILAF YAML Actions Reference

This document describes all available actions and commands that can be used in PILAF YAML test stories to interact with Minecraft servers, players, and the plugin system.

## Table of Contents

- [Overview](#overview)
- [Action Types](#action-types)
- [RCON Commands](#rcon-commands)
- [Player Commands](#player-commands)
- [Plugin Commands](#plugin-commands)
- [Entity Management](#entity-management)
- [Environment Setup](#environment-setup)
- [Assertions](#assertions)
- [Examples](#examples)

## Overview

PILAF YAML stories use actions to interact with the Minecraft test environment. Actions are categorized by their target:

- **RCON Actions**: Direct server console commands
- **Player Actions**: Commands executed by test players
- **Plugin Actions**: Plugin-specific functionality
- **Environment Actions**: Server setup and cleanup

## Action Types

### 1. RCON Commands

RCON actions execute commands directly on the Minecraft server console with operator privileges.

```yaml
rcon:
  command: "say Hello from PILAF"
  description: "Broadcast message to all players"
```

**Available RCON Commands:**

#### Server Management
- `say <message>` - Broadcast message to all players
- `give <player> <item> [amount] [data]` - Give items to player
- `tp <player1> <player2>` - Teleport player1 to player2
- `tp <player> <x> <y> <z>` - Teleport player to coordinates
- `gamemode <player> <mode>` - Set player's gamemode (survival/creative/adventure/spectator)
- `time set <time>` - Set world time (day/night/midnight/noon)
- `weather <clear/rain/thunder>` - Set weather
- `difficulty <peaceful/easy/normal/hard>` - Set difficulty level

#### Entity Management
- `kill @e[type=<entity>]` - Kill all entities of specified type
- `summon <entity> <x> <y> <z> [data]` - Summon entities
- `execute at <player> run <command>` - Execute commands at player's location
- `fill <x1> <y1> <z1> <x2> <y2> <z2> <block>` - Fill area with blocks

#### Player Management
- `op <player>` - Give operator privileges to player
- `deop <player>` - Remove operator privileges from player
- `kick <player> [reason]` - Kick player from server
- `ban <player> [reason]` - Ban player from server

#### Dragon Egg Lightning Plugin Commands
- `dragonlightning clearcooldown <player>` - Clear lightning ability cooldown
- `dragonlightning reload` - Reload plugin configuration
- `ability version` - Show plugin version
- `ability <slot>` - Use lightning ability in specified slot (1-9)

### 2. Player Commands

Player actions execute commands as if they were typed by a Minecraft player.

```yaml
player_command:
  username: "pilaf_tester"
  command: "/say Hello from player"
  wait_for_chat: true
  chat_timeout: 2000
```

**Player Command Options:**

- `username`: Target player name (required)
- `command`: Command to execute (required, without leading `/`)
- `wait_for_chat`: Whether to wait for chat response (default: true)
- `chat_timeout`: Timeout for chat response in milliseconds (default: 2000)

**Common Player Commands:**

#### Movement and Location
- `tp <target>` - Teleport to player/coordinates
- `spawnpoint` - Set spawn point
- `home` - Go to home (if using teleport plugins)
- `back` - Return to previous location

#### Item Management
- `give <item> [amount]` - Get items
- `inventory` - Open inventory
- `hotbar <slot>` - Select hotbar slot

#### Dragon Egg Lightning Plugin Commands
- `/ability 1` - Use lightning ability in slot 1
- `/ability version` - Check plugin version
- `/ability help` - Show plugin help

### 3. Plugin Actions

Plugin-specific actions that interact with the Dragon Egg Lightning plugin.

```yaml
plugin_action:
  action: "use_lightning_ability"
  slot: 1
  player: "pilaf_tester"
```

**Plugin Action Types:**

#### Lightning Abilities
- `use_lightning_ability` - Use lightning ability
  - `slot`: Ability slot (1-9)
  - `player`: Target player username
  - `target_entity`: Optional entity name to target
  - `location`: Optional coordinates (x, y, z)

#### Cooldown Management
- `clear_cooldown` - Clear player's ability cooldowns
- `check_cooldown` - Check remaining cooldown time
- `player`: Target player username

#### Plugin Information
- `get_version` - Get plugin version information
- `reload_config` - Reload plugin configuration
- `get_settings` - Get current plugin settings

### 4. Environment Actions

Actions to set up and manage the test environment.

```yaml
environment:
  action: "connect_player"
  username: "pilaf_tester"
```

**Environment Action Types:**

#### Player Management
- `connect_player` - Connect test player to server
  - `username`: Player username
  - `wait_for_join`: Wait for player join confirmation (default: true)
  - `join_timeout`: Join timeout in milliseconds (default: 5000)

- `disconnect_player` - Disconnect test player from server
  - `username`: Player username

#### Entity Spawning
- `spawn_entity` - Spawn entities for testing
  - `type`: Entity type (zombie, skeleton, etc.)
  - `player`: Player to spawn near
  - `distance`: Distance from player (default: 5)
  - `custom_name`: Optional custom name for entity
  - `data`: Additional NBT data

#### World Setup
- `clear_entities` - Remove all test entities
- `reset_world` - Reset world to initial state
- `setup_test_area` - Create designated test area

### 5. Assertion Actions

Actions that verify expected outcomes.

```yaml
assertion:
  type: "entity_exists"
  entity_name: "ViewTarget"
  player: "pilaf_tester"
  timeout: 5000
```

**Assertion Types:**

#### Entity Assertions
- `entity_exists` - Verify entity exists
  - `entity_name`: Entity name to search for
  - `player`: Player context for search
  - `timeout`: Search timeout (default: 3000)

- `entity_not_exists` - Verify entity does not exist
  - Same parameters as `entity_exists`

#### Player State Assertions
- `player_has_permission` - Verify player has permission
  - `player`: Target player
  - `permission`: Permission node

- `player_gamemode` - Verify player gamemode
  - `player`: Target player
  - `gamemode`: Expected gamemode (survival/creative/adventure/spectator)

#### Plugin State Assertions
- `plugin_enabled` - Verify plugin is enabled
- `ability_available` - Verify ability is available for player
  - `player`: Target player
  - `slot`: Ability slot

## YAML Story Structure

```yaml
test:
  name: "Lightning Strike Test"
  description: "Test lightning ability on visible target"

setup:
  - action: "connect_player"
    username: "pilaf_tester"
  - action: "spawn_entity"
    type: "zombie"
    player: "pilaf_tester"
    distance: 5
    custom_name: "TestTarget"

steps:
  - name: "Use lightning ability"
    action:
      type: "use_lightning_ability"
      slot: 1
      player: "pilaf_tester"
    expected: "Lightning strikes the target"
    timeout: 3000

  - name: "Verify target is affected"
    assertion:
      type: "entity_exists"
      entity_name: "TestTarget"
      player: "pilaf_tester"
    expected: false  # Entity should be killed

cleanup:
  - action: "disconnect_player"
    username: "pilaf_tester"
  - action: "clear_entities"
```

## Common Patterns

### Basic Plugin Test
```yaml
test:
  name: "Plugin Commands Test"

setup:
  - action: "connect_player"
    username: "pilaf_tester"

steps:
  - name: "Check plugin version"
    action:
      rcon:
        command: "ability version"
    assertion:
      type: "command_success"
    expected: "Plugin version displayed"

cleanup:
  - action: "disconnect_player"
    username: "pilaf_tester"
```

### Entity Targeting Test
```yaml
test:
  name: "Lightning Strike Test"

setup:
  - action: "connect_player"
    username: "pilaf_tester"
  - action: "rcon"
    command: "execute at pilaf_tester run summon zombie ^ ^ ^5 {CustomName:'\"ViewTarget\"'}"

steps:
  - name: "Use ability on target"
    action:
      player_command:
        username: "pilaf_tester"
        command: "ability 1"
        wait_for_chat: true
        chat_timeout: 2000
    assertion:
      type: "entity_exists"
      entity_name: "ViewTarget"
      player: "pilaf_tester"
    expected: false

cleanup:
  - action: "rcon"
    command: "kill @e[type=zombie,CustomName='\"ViewTarget\"']"
  - action: "disconnect_player"
    username: "pilaf_tester"
```

### Cooldown Test
```yaml
test:
  name: "Cooldown Behavior Test"

setup:
  - action: "connect_player"
    username: "pilaf_tester"

steps:
  - name: "Use ability first time"
    action:
      player_command:
        username: "pilaf_tester"
        command: "ability 1"
    assertion:
      type: "ability_executed"
    expected: true

  - name: "Try to use ability immediately"
    action:
      player_command:
        username: "pilaf_tester"
        command: "ability 1"
    assertion:
      type: "cooldown_active"
    expected: true

  - name: "Clear cooldown and verify"
    action:
      rcon:
        command: "dragonlightning clearcooldown pilaf_tester"
    assertion:
      type: "command_success"
    expected: true

cleanup:
  - action: "disconnect_player"
    username: "pilaf_tester"
```

## Best Practices

1. **Always include cleanup**: Ensure test players are disconnected and entities are cleaned up
2. **Use descriptive names**: Give meaningful names to tests, steps, and entities
3. **Set appropriate timeouts**: Use timeouts to prevent tests from hanging
4. **Verify state changes**: Use assertions to verify expected outcomes
5. **Handle failures gracefully**: Design tests to handle expected failures
6. **Use player context**: When targeting entities, specify the player context

## Error Handling

PILAF automatically handles common error scenarios:

- **Connection failures**: Tests will fail gracefully if players cannot connect
- **Command timeouts**: Actions will timeout if they take too long
- **Entity not found**: Assertions will handle missing entities appropriately
- **Plugin not available**: Tests will skip if required plugin features are unavailable

## Advanced Usage

### Custom Entity Spawning
```yaml
setup:
  - action: "rcon"
    command: "execute at pilaf_tester run summon zombie ^ ^ ^8 {CustomName:'\"Target\"',Health:20}"
```

### Complex Targeting
```yaml
steps:
  - name: "Target specific entity"
    action:
      rcon:
        command: "execute as @e[type=zombie,name='Target'] at @s run tp @s ~ ~ ~1"
```

### Conditional Execution
```yaml
steps:
  - name: "Conditional test"
    action:
      player_command:
        username: "pilaf_tester"
        command: "ability 1"
    assertion:
      type: "conditional"
      condition: "plugin_enabled"
      expected: true
```
