# PILAF Command Vocabulary Reference

## Overview

This document defines the complete MECE (Mutually Exclusive, Collectively Exhaustive) command vocabulary for PILAF YAML stories. Commands are designed to be intuitive to Minecraft players and follow familiar patterns.

## Command Categories

### 1. Player Management Commands

#### `connect_player`
Connect a test player to the Minecraft server.
```yaml
- action: "connect_player"
  player: "pilaf_tester"
```

#### `disconnect_player`
Disconnect a test player from the Minecraft server.
```yaml
- action: "disconnect_player"
  player: "pilaf_tester"
```

#### `make_operator`
Grant operator privileges to a player.
```yaml
- action: "make_operator"
  player: "pilaf_tester"
```

#### `get_player_inventory`
Get player's inventory contents as JSON.
```yaml
- action: "get_player_inventory"
  player: "pilaf_tester"
  store_as: "inventory_state"
```

#### `get_player_position`
Get player's current position.
```yaml
- action: "get_player_position"
  player: "pilaf_tester"
  store_as: "player_position"
```

#### `get_player_health`
Get player's current health.
```yaml
- action: "get_player_health"
  player: "pilaf_tester"
  store_as: "player_health"
```

#### `send_chat_message`
Send a chat message as a player.
```yaml
- action: "send_chat_message"
  player: "pilaf_tester"
  message: "Testing lightning ability"
```

### 2. Entity Management Commands

#### `spawn_entity`
Spawn an entity at specified location with optional custom name.
```yaml
- action: "spawn_entity"
  entity_type: "zombie"
  custom_name: "TestZombie"
  command: "execute at pilaf_tester run summon zombie ~ ~5 ~ {CustomName:'\"TestZombie\"'}"
  store_as: "spawn_result"
```

#### `get_entities`
Get all entities near a player as JSON.
```yaml
- action: "get_entities"
  player: "pilaf_tester"
  store_as: "entities_before"
```

#### `get_entities_in_view`
Get entities in player's view range.
```yaml
- action: "get_entities_in_view"
  player: "pilaf_tester"
  store_as: "entities_in_view"
```

#### `get_entity_by_name`
Find entity by custom name.
```yaml
- action: "get_entity_by_name"
  entity_name: "TestZombie"
  player: "pilaf_tester"
  store_as: "target_entity"
```

#### `get_entity_distance`
Get distance from player to entity.
```yaml
- action: "get_entity_distance"
  entity_name: "TestZombie"
  player: "pilaf_tester"
  store_as: "entity_distance"
```

#### `get_entity_health`
Get entity's health.
```yaml
- action: "get_entity_health"
  entity_name: "TestZombie"
  player: "pilaf_tester"
  store_as: "entity_health"
```

### 3. Command Execution Commands

#### `execute_player_command`
Execute a command as a player.
```yaml
- action: "execute_player_command"
  player: "pilaf_tester"
  command: "/ability 1"
```

#### `execute_rcon_command`
Execute a command via RCON.
```yaml
- action: "execute_rcon_command"
  command: "op pilaf_tester"
  store_as: "rcon_result"
```

#### `execute_rcon_with_capture`
Execute RCON command and capture full response.
```yaml
- action: "execute_rcon_with_capture"
  command: "dragonlightning version"
  store_as: "plugin_version"
```

### 4. Inventory Management Commands

#### `give_item`
Give item to player.
```yaml
- action: "give_item"
  player: "pilaf_tester"
  item: "dragon_egg"
  count: 1
```

#### `remove_item`
Remove item from player.
```yaml
- action: "remove_item"
  player: "pilaf_tester"
  item: "dragon_egg"
  count: 1
```

#### `get_player_equipment`
Get player's equipment.
```yaml
- action: "get_player_equipment"
  player: "pilaf_tester"
  store_as: "equipment_state"
```

#### `equip_item`
Equip item to specific slot.
```yaml
- action: "equip_item"
  player: "pilaf_tester"
  item: "dragon_egg"
  slot: "offhand"
```

### 5. World & Environment Commands

#### `get_block_at_position`
Get block information at position.
```yaml
- action: "get_block_at_position"
  position: ["~", "~", "~"]
  store_as: "block_info"
```

#### `get_world_time`
Get current world time.
```yaml
- action: "get_world_time"
  store_as: "world_time"
```

#### `get_weather`
Get current weather.
```yaml
- action: "get_weather"
  store_as: "weather_state"
```

### 6. State Management Commands

#### `store_state`
Store command result in variable.
```yaml
- action: "store_state"
  from_command_result: "entities_before"
  variable_name: "zombie_state_before"
```

#### `print_stored_state`
Print stored state to report.
```yaml
- action: "print_stored_state"
  variable_name: "zombie_state_before"
  format: "json"  # or "pretty", "summary"
```

#### `compare_states`
Compare two stored states.
```yaml
- action: "compare_states"
  state1: "entities_before"
  state2: "entities_after"
  store_as: "entity_comparison"
```

#### `print_state_comparison`
Print state comparison to report.
```yaml
- action: "print_state_comparison"
  variable_name: "entity_comparison"
```

### 7. Data Extraction Commands

#### `extract_with_jsonpath`
Extract data using JSONPath.
```yaml
- action: "extract_with_jsonpath"
  source_variable: "entities_before"
  json_path: "$.entities[?(@.CustomName=='TestZombie')]"
  store_as: "target_zombie"
```

#### `filter_entities`
Filter entities by criteria.
```yaml
- action: "filter_entities"
  source_variable: "entities_before"
  filter_type: "by_name"
  filter_value: "TestZombie"
  store_as: "filtered_zombies"
```

### 8. Assertion Commands

#### `assert_entity_missing`
Assert entity is no longer present.
```yaml
- action: "assert_entity_missing"
  from_comparison: "entity_comparison"
  json_path: "$.entities[?(@.CustomName=='TestZombie')]"
```

#### `assert_entity_exists`
Assert entity exists.
```yaml
- action: "assert_entity_exists"
  entity_name: "TestZombie"
  player: "pilaf_tester"
```

#### `assert_player_has_item`
Assert player has specific item.
```yaml
- action: "assert_player_has_item"
  player: "pilaf_tester"
  item: "dragon_egg"
```

#### `assert_response_contains`
Assert command response contains/excludes text.
```yaml
- action: "assert_response_contains"
  source: "plugin_version"
  contains: "Dragon Egg Lightning"
  negated: false
```

#### `assert_json_equals`
Assert two JSON states are equal.
```yaml
- action: "assert_json_equals"
  state1: "expected_entities"
  state2: "actual_entities"
  ignore_fields: ["timestamp", "id"]
```

### 9. Plugin & Server Commands

#### `get_plugin_status`
Get plugin status information.
```yaml
- action: "get_plugin_status"
  plugin: "DragonEggLightning"
  store_as: "plugin_status"
```

#### `execute_plugin_command`
Execute plugin-specific command.
```yaml
- action: "execute_plugin_command"
  plugin: "DragonEggLightning"
  command: "version"
  store_as: "plugin_version"
```

#### `get_server_info`
Get server information.
```yaml
- action: "get_server_info"
  store_as: "server_info"
```

### 10. Utility Commands

#### `wait`
Wait for specified duration.
```yaml
- action: "wait"
  duration: 5000  # milliseconds
```

#### `wait_for_entity_spawn`
Wait for entity to spawn.
```yaml
- action: "wait_for_entity_spawn"
  entity_type: "zombie"
  player: "pilaf_tester"
  timeout: 10000
  store_as: "spawn_event"
```

#### `wait_for_chat_message`
Wait for chat message containing specific text.
```yaml
- action: "wait_for_chat_message"
  player: "pilaf_tester"
  contains: "lightning strike"
  timeout: 5000
  store_as: "chat_response"
```

#### `clear_entities`
Clear all entities (cleanup utility).
```yaml
- action: "clear_entities"
  entity_type: "zombie"
```

#### `damage_entity`
Damage an entity.
```yaml
- action: "damage_entity"
  entity_name: "TestZombie"
  damage: 10
```

#### `heal_player`
Heal a player to full health.
```yaml
- action: "heal_player"
  player: "pilaf_tester"
```

## Variable Scope

Variables are scoped to the current test story and do not persist across different stories.

## Error Handling

All commands include automatic error handling with descriptive error messages in reports.

## Report Integration

State management commands automatically integrate with PILAF's reporting system to show before/after comparisons and assertion results.
