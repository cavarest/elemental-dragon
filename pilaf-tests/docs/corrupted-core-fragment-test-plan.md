# Corrupted Core Fragment Test Plan

## Fragment Information

**Element:** Void
**Material:** Nether Star
**Permission:** `elementaldragon.fragment.corrupted`

## Abilities

### 1. Dread Gaze (`/corrupt 1`)

**Cooldown:** 180 seconds (3 minutes)

**What it does (from source code):**
- Activates "READY TO STRIKE" state on next melee hit
- When target is hit, applies complete freeze for 4 seconds
- Freeze effects: SLOW 255, MINING_FATIGUE 255, WEAKNESS 255, HUNGER 255
- Prevents: movement, block placement, block breaking, interactions, eating, dropping items
- Teleports victim back to freeze location every tick (anti-cheat friendly)

**Implementation Details:**
```java
private static final long DREAD_GAZE_COOLDOWN = 180000L; // 3 minutes
private static final int DREAD_GAZE_DURATION = 80; // 4 seconds (changed from 10s per Issue #20)
private static final int MAX_AMPLIFIER = 255; // Maximum effect level for complete freeze
```

**Testing Approach:**
1. Equip Corrupted Core Fragment
2. Activate Dread Gaze (`/corrupt 1`)
3. Hit a frozen zombie with melee attack
4. **Verify:** Zombie receives all freeze potion effects (SLOW 255, MINING_FATIGUE 255, WEAKNESS 255, HUNGER 255)
5. **Verify:** Zombie cannot move (position stays the same)
6. **Verify:** Zombie cannot attack
7. Wait 4 seconds
8. **Verify:** Freeze effects expire and zombie can move again

**Pilaf Test Structure:**
```javascript
{
  name: "Dread Gaze - Complete Freeze on Hit",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },

    // Spawn frozen zombie nearby (for melee hit)
    {
      action: 'execute_command',
      command: 'summon zombie 2 64 0 {NoAI:1,Silent:1,Tags:["freeze_target"],Health:20}'
    },

    // Equip fragment
    { action: 'execute_command', command: 'give TestPlayer nether_star' },
    { action: 'execute_player_command', player: 'tester', command: '/corrupt equip' },

    // Record zombie position before freeze
    {
      action: 'get_entity_location',
      entity_tag: 'freeze_target',
      store_as: 'zombie_pos_before'
    },

    // Activate Dread Gaze
    { action: 'execute_player_command', player: 'tester', command: '/corrupt 1' },

    // Wait for activation
    { action: 'wait', duration: 500 },

    // Move close to zombie for melee hit
    { action: 'execute_command', command: 'tp TestPlayer 1.5 64 0 0 0' }, // Facing zombie

    // Perform melee attack (hit the zombie)
    { action: 'execute_player_command', player: 'tester', command: 'attack nearest' },

    // Wait for freeze to apply
    { action: 'wait', duration: 500 },

    // Verify zombie has SLOW effect with amplifier 255
    {
      action: 'execute_command',
      command: 'execute as @e[tag=freeze_target] if entity @s[has_effect={effect:slowness,amplifier:255}] run tellraw @a {"text":"SLOW_255_APPLIED","color":"green"}'
    },

    // Verify zombie has MINING_FATIGUE effect with amplifier 255
    {
      action: 'execute_command',
      command: 'execute as @e[tag=freeze_target] if entity @s[has_effect={effect:mining_fatigue,amplifier:255}] run tellraw @a {"text":"MINING_FATIGUE_255_APPLIED","color":"green"}'
    },

    // Verify zombie has WEAKNESS effect with amplifier 255
    {
      action: 'execute_command',
      command: 'execute as @e[tag=freeze_target] if entity @s[has_effect={effect:weakness,amplifier:255}] run tellraw @a {"text":"WEAKNESS_255_APPLIED","color":"green"}'
    },

    // Verify zombie has HUNGER effect with amplifier 255
    {
      action: 'execute_command',
      command: 'execute as @e[tag=freeze_target] if entity @s[has_effect={effect:hunger,amplifier:255}] run tellraw @a {"text":"HUNGER_255_APPLIED","color":"green"}'
    },

    // Wait to check if zombie position changed (should be frozen)
    { action: 'wait', duration: 1000 },

    // Record zombie position during freeze
    {
      action: 'get_entity_location',
      entity_tag: 'freeze_target',
      store_as: 'zombie_pos_during'
    },

    // Verify position is the same (within small tolerance for floating point)
    {
      action: 'assert_positions_equal',
      position1: '{zombie_pos_before}',
      position2: '{zombie_pos_during}',
      tolerance: 0.1
    },

    // Wait for freeze to expire (4 seconds)
    { action: 'wait', duration: 4000 },

    // Verify zombie no longer has SLOW 255
    {
      action: 'execute_command',
      command: 'execute unless entity @e[tag=freeze_target][has_effect={effect:slowness,amplifier:255}] run tellraw @a {"text":"FREEZE_EXPIRED","color":"green"}'
    }
  ]
}
```

### Dread Gaze - Freeze Prevention Tests

Test that frozen entities cannot:
- Place blocks
- Break blocks
- Interact (right-click)
- Drop items
- Eat food

**Pilaf Test Structure:**
```javascript
{
  name: "Dread Gaze - Freeze Prevention Actions",
  steps: [
    // Use second player as victim to test player-specific actions
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'tp TestVictim 0 64 5' },

    // Equip fragment on attacker
    { action: 'execute_command', command: 'give TestPlayer nether_star' },
    { action: 'execute_player_command', player: 'tester', command: '/corrupt equip' },

    // Activate Dread Gaze
    { action: 'execute_player_command', player: 'tester', command: '/corrupt 1' },

    // Wait for activation
    { action: 'wait', duration: 500 },

    // Teleport attacker to hit victim
    { action: 'execute_command', command: 'tp TestPlayer 0 64 4.5' }, // Right next to victim

    // Hit victim with melee
    { action: 'execute_player_command', player: 'tester', command: 'attack nearest' },

    { action: 'wait', duration: 500 },

    // Try to place block while frozen (should fail)
    {
      action: 'execute_player_command',
      player: 'victim',
      command: 'setblock ~ ~ ~ stone',
      expect_failure: true
    },

    // Try to break block while frozen (should fail)
    {
      action: 'execute_player_command',
      player: 'victim',
      command: 'setblock ~ ~-1 ~ air',
      expect_failure: true
    },

    // Try to drop item while frozen (should fail)
    {
      action: 'execute_player_command',
      player: 'victim',
      command: 'drop diamond',
      expect_failure: true
    },

    // Wait for freeze to expire
    { action: 'wait', duration: 4500 }
  ]
}
```

---

### 2. Life Devourer (`/corrupt 2`)

**Cooldown:** 120 seconds (2 minutes)

**What it does (from source code):**
- Grants 50% lifesteal for 20 seconds
- Player heals for 50% of all damage dealt
- Works with ANY damage source (melee, ranged, spells)
- Duration: 20 seconds (400 ticks)

**Implementation Details:**
```java
private static final long LIFE_DEVOURER_COOLDOWN = 120000L; // 2 minutes
private static final int LIFE_DEVOURER_DURATION = 400; // 20 seconds
private static final double LIFE_DEVOURER_STEAL_PERCENT = 0.5; // 50% health steal
```

**Testing Approach (User's Suggested Clever Test):**
1. Damage player to low health (e.g., 5 hearts)
2. Spawn villager nearby (benign entity, won't fight back)
3. Activate Life Devourer
4. Attack villager with melee
5. **Verify:** Player health increased by 50% of damage dealt

**Pilaf Test Structure:**
```javascript
{
  name: "Life Devourer - Clever Lifesteal Test",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },

    // Damage player to low health (15 damage = 7.5 hearts damage)
    { action: 'execute_command', command: 'damage TestPlayer 15 generic' },

    // Record health after damage
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_before_lifesteal' },

    // Verify player is at low health (should be ~5 hearts)
    { action: 'assert', condition: 'less_than', actual: '{health_before_lifesteal}', expected: 10 },

    // Spawn villager (benign entity, won't fight back)
    {
      action: 'execute_command',
      command: 'summon villager 2 64 0 {NoAI:1,Silent:1,Tags:["lifesteal_target"],Health:20}'
    },

    // Equip Corrupted Core fragment
    { action: 'execute_command', command: 'give TestPlayer nether_star' },
    { action: 'execute_player_command', player: 'tester', command: '/corrupt equip' },

    // Activate Life Devourer
    { action: 'execute_player_command', player: 'tester', command: '/corrupt 2' },

    // Move close to villager
    { action: 'execute_command', command: 'tp TestPlayer 1.5 64 0 0 0' },

    // Wait for activation
    { action: 'wait', duration: 500 },

    // Attack villager with melee (assume dealing ~4 damage per hit with fist)
    // Villager has 20 health, player will deal multiple hits
    { action: 'execute_player_command', player: 'tester', command: 'attack nearest' },

    // Wait for lifesteal to apply
    { action: 'wait', duration: 500 },

    // Record health after attack
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_after_lifesteal' },

    // Verify health increased
    {
      action: 'assert',
      condition: 'greater_than',
      actual: '{health_after_lifesteal}',
      expected: '{health_before_lifesteal}'
    },

    // Calculate healing received
    {
      action: 'calculate_difference',
      minuend: '{health_after_lifesteal}',
      subtrahend: '{health_before_lifesteal}',
      store_as: 'healing_received'
    },

    // Verify healing is reasonable (at least 1 heart, at most 50% of damage dealt)
    { action: 'assert', condition: 'greater_than', actual: '{healing_received}', expected: 2 },

    // Verify villager took damage (health decreased)
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=lifesteal_target] Health',
      store_as: 'villager_health'
    },
    { action: 'assert', condition: 'less_than', actual: '{villager_health}', expected: 20 }
  ]
}
```

**Life Devourer - Detailed Damage Test:**
For more precise testing, we can use `/damage` command with known damage amounts:

```javascript
{
  name: "Life Devourer - Precise Lifesteal Calculation",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },

    // Set player to exactly 10 health (5 hearts)
    { action: 'execute_command', command: 'attribute @p generic.max_health base set 20' },
    { action: 'execute_command', command: 'effect give TestPlayer instant_damage 1 2' }, // Damage to reduce health

    // Heal to specific value (10 health = 5 hearts)
    { action: 'execute_command', command: 'effect give TestPlayer regeneration 1 10' },
    { action: 'wait', duration: 3000 },

    // Force health to exactly 10
    { action: 'execute_command', command: 'data merge entity @p {Health:10f}' },

    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_before' },

    // Spawn zombie with known health
    {
      action: 'execute_command',
      command: 'summon zombie 2 64 0 {NoAI:1,Silent:1,Tags:["precise_target"],Health:20}'
    },

    // Equip fragment
    { action: 'execute_command', command: 'give TestPlayer nether_star' },
    { action: 'execute_player_command', player: 'tester', command: '/corrupt equip' },

    // Activate Life Devourer
    { action: 'execute_player_command', player: 'tester', command: '/corrupt 2' },
    { action: 'wait', duration: 500 },

    // Deal exactly 8 damage to zombie using /damage command
    { action: 'execute_command', command: 'damage @e[tag=precise_target] 8 generic' },

    // Wait for lifesteal
    { action: 'wait', duration: 500 },

    // Player should have healed 50% of 8 = 4 health
    // Starting at 10 health, should now be at 14 health
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_after' },

    // Verify healing (allowing small tolerance for floating point)
    { action: 'assert', condition: 'equals', actual: '{health_after}', expected: 14 }
  ]
}
```

---

## Passive Bonus: Night Vision and Creeper Invisibility

### Passive 1: Night Vision

**What it does:**
- Applies permanent Night Vision potion effect while fragment is equipped
- No particles shown
- No icon shown
- Removed when fragment is unequipped

**Testing Approach:**
1. Clear all effects and inventory
2. Verify no Night Vision effect
3. Equip Corrupted Core Fragment
4. **Verify:** Night Vision effect applied (amplifier 0)
5. Unequip fragment
6. **Verify:** Night Vision effect removed

**Pilaf Test Structure:**
```javascript
{
  name: "Corrupted Core - Passive Night Vision",
  steps: [
    // Setup - clear all effects
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'clear TestPlayer' },

    // Verify no Night Vision effect initially
    {
      action: 'execute_command',
      command: 'execute as @p unless entity @s[has_effect={effect:night_vision}] run say NO_NIGHT_VISION'
    },

    // Equip fragment
    { action: 'execute_command', command: 'give TestPlayer nether_star' },
    { action: 'execute_player_command', player: 'tester', command: '/corrupt equip' },
    { action: 'wait', duration: 500 },

    // Verify Night Vision is applied (amplifier 0 = Night Vision I)
    {
      action: 'execute_command',
      command: 'execute as @p if entity @s[has_effect={effect:night_vision,amplifier:0}] run say NIGHT_VISION_ACTIVE'
    },

    // Unequip fragment
    { action: 'execute_command', command: 'clear TestPlayer' },
    { action: 'wait', duration: 500 },

    // Verify Night Vision removed
    {
      action: 'execute_command',
      command: 'execute as @p unless entity @s[has_effect={effect:night_vision}] run say NIGHT_VISION_REMOVED'
    }
  ]
}
```

### Passive 2: Creeper Invisibility

**What it does:**
- Creepers will NOT target player with Corrupted Core equipped
- Endermen will NOT aggro when looked at
- EntityTargetEvent is cancelled for creepers and endermen targeting player

**Testing Approach:**
1. Spawn creeper near player without fragment
2. **Verify WITHOUT fragment:** Creeper targets player
3. Equip Corrupted Core Fragment
4. Spawn another creeper
5. **Verify WITH fragment:** Creeper does NOT target player
6. Repeat with enderman

**Pilaf Test Structure:**
```javascript
{
  name: "Corrupted Core - Passive Creeper Invisibility",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'clear TestPlayer' },

    // Spawn creeper WITHOUT fragment equipped
    {
      action: 'execute_command',
      command: 'summon creeper 10 64 0 {PersistenceRequired:1,Tags:["creeper_test_1"]}'
    },

    // Wait for creeper to acquire target
    { action: 'wait', duration: 2000 },

    // Check if creeper is targeting player (should be targeting without fragment)
    {
      action: 'execute_command',
      command: 'execute as @e[tag=creeper_test_1] if entity @s[nbt={Invulnerable:0b}] run data get entity @s Target',
      store_as: 'target_without_fragment',
      expect_failure: false // May or may not have target yet
    },

    // Equip fragment
    { action: 'execute_command', command: 'give TestPlayer nether_star' },
    { action: 'execute_player_command', player: 'tester', command: '/corrupt equip' },

    // Spawn second creeper WITH fragment equipped
    {
      action: 'execute_command',
      command: 'summon creeper 10 64 5 {PersistenceRequired:1,Tags:["creeper_test_2"]}'
    },

    // Wait for creeper to potentially target
    { action: 'wait', duration: 2000 },

    // Verify creeper does NOT target player
    // This is tricky to test via commands - alternative is to check distance
    // If creeper was targeting, it would move toward player
    {
      action: 'get_entity_location',
      entity_tag: 'creeper_test_2',
      store_as: 'creeper_pos_after'
    },

    // Creeper should still be at spawn location (not moved toward player)
    {
      action: 'assert',
      condition: 'equals',
      actual: '{creeper_pos_after.x}',
      expected: 10 // Should still be at X=10
    }
  ]
}
```

**Alternative Creeper Test (Direct Target Check):**
Since targeting is hard to verify directly, we can use behavior observation:

```javascript
{
  name: "Corrupted Core - Creeper Target Prevention via Movement",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=creeper]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'clear TestPlayer' },

    // Equip fragment FIRST
    { action: 'execute_command', command: 'give TestPlayer nether_star' },
    { action: 'execute_player_command', player: 'tester', command: '/corrupt equip' },

    // Spawn creeper at distance 15
    {
      action: 'execute_command',
      command: 'summon creeper 15 64 0 {NoAI:0,PersistenceRequired:1,Tags:["creeper_behavior"],Health:20}'
    },

    // Record initial creeper position
    {
      action: 'get_entity_location',
      entity_tag: 'creeper_behavior',
      store_as: 'creeper_initial'
    },

    // Wait 5 seconds for creeper AI to run
    { action: 'wait', duration: 5000 },

    // Record creeper position after AI
    {
      action: 'get_entity_location',
      entity_tag: 'creeper_behavior',
      store_as: 'creeper_final'
    },

    // Calculate distance moved
    {
      action: 'calculate_distance',
      from: '{creeper_initial}',
      to: '{creeper_final}',
      store_as: 'distance_moved'
    },

    // Creeper should NOT have moved significantly (less than 2 blocks)
    // If it was targeting player, it would have moved toward X=0
    { action: 'assert', condition: 'less_than', actual: '{distance_moved}', expected: 2 }
  ]
}
```

---

## Test File Structure

```
pilaf-tests/stories/05-corrupted-core-fragment/
├── dread-gaze-freeze.test.js      # /corrupt 1 - Complete freeze on hit
├── dread-gaze-prevention.test.js  # Block actions while frozen
├── life-devourer-clever.test.js   # /corrupt 2 - Clever lifesteal test (user suggested)
├── life-devourer-precise.test.js  # /corrupt 2 - Precise damage/healing calculation
├── passive-night-vision.test.js   # Night Vision passive
├── passive-creeper-ghost.test.js  # Creeper invisibility passive
└── cooldown-persistence.test.js   # (shared with other fragments)
```

---

## Verification Checklist

- [ ] Dread Gaze activates "READY TO STRIKE" state
- [ ] Dread Gaze applies complete freeze on melee hit
- [ ] Dread Gaze freeze includes SLOW 255
- [ ] Dread Gaze freeze includes MINING_FATIGUE 255
- [ ] Dread Gaze freeze includes WEAKNESS 255
- [ ] Dread Gaze freeze includes HUNGER 255
- [ ] Dread Gaze prevents movement (position locked)
- [ ] Dread Gaze prevents block placement
- [ ] Dread Gaze prevents block breaking
- [ ] Dread Gaze prevents interactions
- [ ] Dread Gaze freeze expires after 4 seconds
- [ ] Dread Gaze has 180-second cooldown
- [ ] Life Devourer activates for 20 seconds
- [ ] Life Devourer heals 50% of damage dealt
- [ ] Life Devourer works with melee damage
- [ ] Life Devourer works with ranged damage
- [ ] Life Devourer expires after 20 seconds
- [ ] Life Devourer has 120-second cooldown
- [ ] Passive Night Vision applied when equipped
- [ ] Passive Night Vision removed when unequipped
- [ ] Passive creeper invisibility prevents targeting
- [ ] Passive enderman anti-aggro works

---

## Special Testing Considerations

### Dread Gaze Persistence (Issue #20)
The freeze debuff persists across player rejoins using PersistentDataContainer. Tests should verify:
1. Freeze victim disconnects and rejoins
2. Freeze effects are restored
3. Remaining duration is preserved

### Life Devourer Edge Cases
- Lifesteal when player is at full health (should overheal to cap)
- Lifesteal with damage value of 0 (should not trigger)
- Lifesteal when event is cancelled by other plugins (still heals per line 832)

### Freeze Prevention Events
The following events are cancelled for frozen players:
- `BlockPlaceEvent` - Blocks placement
- `BlockBreakEvent` - Block breaking
- `PlayerInteractEvent` - All interactions (left/right click, air/block)
- Additional handlers for drop item and consume food may exist
