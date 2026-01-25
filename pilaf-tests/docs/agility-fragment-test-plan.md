# Agility Fragment Test Plan

## Fragment Information

**Element:** Wind
**Material:** Phantom Membrane
**Permission:** `elementaldragon.fragment.agility`

## Abilities

### 1. Draconic Surge (`/agile 1`)

**Cooldown:** 30 seconds

**What it does (from source code):**
- Dashes player 20 blocks in the direction they are facing
- Dash completes in 1 second (20 ticks)
- Provides fall damage protection for 10 seconds after dash
- Shows cloud particles during dash

**Implementation Details:**
```java
private static final double DRACONIC_SURGE_DISTANCE = 20.0; // 20 blocks
private static final int DRACONIC_SURGE_DURATION = 20; // 1 second = 20 ticks
private static final double DRACONIC_SURGE_VELOCITY = 1.0; // blocks/tick
private static final int DRACONIC_SURGE_FALL_PROTECTION = 200; // 10 seconds
```

**Testing Approach:**
1. Record player starting position (0, 64, 0)
2. Face North (yaw 0)
3. Equip Agility Fragment
4. Execute `/agile 1`
5. Wait for dash to complete (1.5 seconds)
6. **Verify:** Player moved ~20 blocks North (new position ~0, 64, -20)
7. **Verify:** Cloud particles appeared during dash

**Test Challenges:**
- Movement measurement may be imprecise due to client-server desync
- Use tolerance (±2 blocks acceptable)
- Position verification via `/data get entity @p Pos`

**Pilaf Test Structure:**
```javascript
{
  steps: [
    // Setup
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' }, // x,y,z,yaw,pitch - facing North
    { action: 'execute_command', command: 'give TestPlayer phantom_membrane' },
    { action: 'execute_player_command', player: 'tester', command: '/agile equip' },

    // Record start position
    { action: 'get_player_location', player: 'tester', store_as: 'start_pos' },

    // Execute dash
    { action: 'execute_player_command', player: 'tester', command: '/agile 1' },

    // Wait for dash
    { action: 'wait', duration: 1500 },

    // Record end position
    { action: 'get_player_location', player: 'tester', store_as: 'end_pos' },

    // Calculate distance traveled
    {
      action: 'calculate_distance',
      from: '{start_pos}',
      to: '{end_pos}',
      store_as: 'distance'
    },

    // Verify dash distance (~20 blocks North, so Z should be -20)
    {
      action: 'assert',
      condition: 'greater_than',
      actual: '{distance}',
      expected: 18 // Allow 2 block tolerance
    }
  ]
}
```

---

### 2. Wing Burst (`/agile 2`)

**Cooldown:** 45 seconds

**What it does (from source code):**
- Pushes all living entities within 8 block radius away from player
- Pushes them 20 blocks away from their starting position
- Push completes in 2 seconds (40 ticks)
- Applies slow falling for 10 seconds after push

**Implementation Details:**
```java
private static final double WING_BURST_RADIUS = 8.0; // 8 blocks
private static final double WING_BURST_DISTANCE = 20.0; // 20 blocks push
private static final int WING_BURST_PUSH_DURATION = 40; // 2 seconds
private static final int WING_BURST_FALL_SLOW_DURATION = 200; // 10 seconds slow falling
```

**Testing Approach:**
1. Spawn frozen zombies around player (radius 5, inside push range)
2. Spawn zombie outside range (radius 12, should NOT be affected)
3. Record initial positions
4. Execute `/agile 2`
5. Wait for push (3 seconds)
6. **Verify:** Zombies within radius 8 pushed ~20 blocks away
7. **Verify:** Zombie outside radius NOT pushed
8. **Verify:** Player has slow falling effect

**Pilaf Test Structure:**
```javascript
{
  steps: [
    // Clear and spawn entities
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },

    // Spawn zombies within push range (5 blocks)
    { action: 'execute_command', command: 'summon zombie 5 64 0 {NoAI:1,Tags:["close_east"],Health:20}' },
    { action: 'execute_command', command: 'summon zombie -5 64 0 {NoAI:1,Tags:["close_west"],Health:20}' },
    { action: 'execute_command', command: 'summon zombie 0 64 5 {NoAI:1,Tags:["close_south"],Health:20}' },

    // Spawn zombie outside push range (12 blocks)
    { action: 'execute_command', command: 'summon zombie 12 64 0 {NoAI:1,Tags:["outside"],Health:20}' },

    // Record initial positions
    { action: 'get_entity_location', entity_tag: 'close_east', store_as: 'close_east_start' },
    { action: 'get_entity_location', entity_tag: 'outside', store_as: 'outside_start' },

    // Equip and execute
    { action: 'execute_command', command: 'give TestPlayer phantom_membrane' },
    { action: 'execute_player_command', player: 'tester', command: '/agile equip' },
    { action: 'execute_player_command', player: 'tester', command: '/agile 2' },

    // Wait for push
    { action: 'wait', duration: 3000 },

    // Record new positions
    { action: 'get_entity_location', entity_tag: 'close_east', store_as: 'close_east_end' },
    { action: 'get_entity_location', entity_tag: 'outside', store_as: 'outside_end' },

    // Verify close zombie pushed (~20 blocks)
    {
      action: 'calculate_distance',
      from: '{close_east_start}',
      to: '{close_east_end}',
      store_as: 'push_distance_close'
    },
    { action: 'assert', condition: 'greater_than', actual: '{push_distance_close}', expected: 15 },

    // Verify outside zombie NOT pushed
    {
      action: 'calculate_distance',
      from: '{outside_start}',
      to: '{outside_end}',
      store_as: 'push_distance_outside'
    },
    { action: 'assert', condition: 'less_than', actual: '{push_distance_outside}', expected: 3 }
  ]
}
```

---

## Passive Bonus: Permanent Speed I

**What it does:**
- Applies Speed I (amplifier 0) permanently while fragment is equipped
- Removed when fragment is unequipped

**Testing Approach:**
1. Clear all effects and inventory
2. Check for no speed effect
3. Equip Agility Fragment
4. **Verify:** Speed I applied (check potion effect)
5. Unequip fragment
6. **Verify:** Speed I removed

**Pilaf Test Structure:**
```javascript
{
  steps: [
    // Setup - clear effects
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'clear TestPlayer' },

    // Verify no speed effect
    {
      action: 'execute_command',
      command: 'execute as @p unless entity @s[has_effect={effect:speed}] run say NO_SPEED',
      store_as: 'no_speed_check'
    },

    // Equip fragment
    { action: 'execute_command', command: 'give TestPlayer phantom_membrane' },
    { action: 'execute_player_command', player: 'tester', command: '/agile equip' },
    { action: 'wait', duration: 500 },

    // Verify Speed I applied (amplifier 0 = Speed I)
    {
      action: 'execute_command',
      command: 'execute as @p if entity @s[has_effect={effect:speed,amplifier:0}] run say SPEED_I_ACTIVE'
    },

    // Unequip fragment
    { action: 'execute_command', command: 'clear TestPlayer' },

    // Verify Speed I removed
    {
      action: 'execute_command',
      command: 'execute as @p unless entity @s[has_effect={effect:speed}] run say SPEED_REMOVED'
    }
  ]
}
```

---

## Test File Structure

```
pilaf-tests/stories/03-agility-fragment/
├── draconic-surge.test.js         # /agile 1 - Dash ability
├── wing-burst.test.js             # /agile 2 - Push ability
├── passive-speed.test.js           # Permanent Speed I
└── cooldown-persistence.test.js   # (shared with other fragments)
```

---

## Verification Checklist

- [ ] Draconic Surge dashes player ~20 blocks in facing direction
- [ ] Draconic Surge completes in ~1 second
- [ ] Draconic Surge provides fall damage protection for 10 seconds
- [ ] Draconic Surge has 30-second cooldown
- [ ] Wing Burst pushes entities within 8 blocks radius
- [ ] Wing Burst pushes entities ~20 blocks away
- [ ] Wing Burst does NOT affect entities outside radius
- [ ] Wing Burst applies slow falling for 10 seconds
- [ ] Wing Burst has 45-second cooldown
- [ ] Passive Speed I is applied when equipped
- [ ] Passive Speed I is removed when unequipped
