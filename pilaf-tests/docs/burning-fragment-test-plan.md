# Burning Fragment Test Plan

## Fragment Information

**Element:** Fire
**Material:** Blaze Powder
**Permission:** `elementaldragon.fragment.burning`

## Abilities

### 1. Dragon's Wrath (`/fire 1`)

**Cooldown:** 40 seconds

**What it does:**
- Launches a fireball that dynamically chases the closest hostile entity in your crosshairs
- Explodes on impact with 6.0 damage (3 hearts, armor-ignoring)
- Explosion radius: 5 blocks
- Fireball homes in on target for up to 10 ticks (0.5 seconds)
- Target search range: 50 blocks

**Implementation Details (from source):**
```java
private static final double DRAGONS_WRATH_DAMAGE = 6.0; // 3 hearts
private static final double DRAGONS_WRATH_AOE_RADIUS = 5.0; // 5 blocks
private static final int DRAGONS_WRATH_HOMING_TICKS = 10; // 10 ticks
private static final double DRAGONS_WRATH_TARGET_RANGE = 50.0;
```

**Testing Approach:**
1. Spawn frozen zombie at known position (10 blocks North)
2. Teleport player to spawn, face North
3. Equip Burning Fragment
4. Execute `/fire 1`
5. Wait for fireball travel and impact
6. **Verify:** Zombie took damage or died
7. **Verify:** Fire blocks spawned at explosion location
8. **Verify:** Fireball tracked toward target (not straight line)

**Test Challenges:**
- Fireball tracking: Need to ensure zombie is in player's view cone
- Explosion timing: Wait long enough for impact (10 ticks = 0.5s travel + flight time)
- Damage verification: Use `/data get entity` to check health

**Pilaf Test Structure:**
```javascript
{
  steps: [
    // Clear entities
    { action: 'execute_command', command: 'kill @e[type=!player]' },

    // Spawn target at North position (in view cone when facing North)
    {
      action: 'execute_command',
      command: 'summon zombie 0 64 -10 {NoAI:1,Silent:1,Tags:["fireball_target"],Health:20}'
    },

    // Position player
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' }, // Facing North

    // Equip fragment
    { action: 'execute_command', command: 'give TestPlayer blaze_powder' },
    { action: 'execute_player_command', player: 'tester', command: '/fire equip' },

    // Execute ability
    { action: 'execute_player_command', player: 'tester', command: '/fire 1' },

    // Wait for fireball
    { action: 'wait', duration: 2000 },

    // Verify target died or took damage
    {
      action: 'execute_command',
      command: 'execute unless entity @e[tag=fireball_target] run tellraw @a {"text":"TARGET_DESTROYED","color":"green"}'
    }
  ]
}
```

---

### 2. Infernal Dominion (`/fire 2`)

**Cooldown:** 60 seconds

**What it does:**
- Creates a ring of fire around the player
- Radius: 10 blocks
- Duration: 10 seconds
- Deals 1.0 damage per tick (0.5 hearts per tick) = 5 hearts total over 10 seconds

**Implementation Details (from source):**
```java
private static final double INFERNAL_DOMINION_RADIUS = 10.0; // 10 blocks
private static final int INFERNAL_DOMINION_DURATION = 200; // 10 seconds
private static final double INFERNAL_DOMINION_DAMAGE_PER_TICK = 1.0; // 0.5 hearts/tick
```

**Testing Approach:**
1. Spawn zombies in ring pattern (radius 8, cardinal directions)
2. Spawn zombie OUTSIDE radius as control (at 12 blocks)
3. Give player fire resistance (so we don't die testing)
4. Execute `/fire 2`
5. Wait 3 seconds (should drain ~1.5 hearts)
6. **Verify:** Zombies inside ring took damage
7. **Verify:** Zombie outside ring did NOT take damage
8. **Verify:** Fire blocks appear in ring pattern

**Pilaf Test Structure:**
```javascript
{
  steps: [
    // Clear entities
    { action: 'execute_command', command: 'kill @e[type=!player]' },

    // Spawn zombies at radius 8 (should be affected)
    { action: 'execute_command', command: 'summon zombie 8 64 0 {NoAI:1,Tags:["ring_east"],Health:20}' },
    { action: 'execute_command', command: 'summon zombie -8 64 0 {NoAI:1,Tags:["ring_west"],Health:20}' },
    { action: 'execute_command', command: 'summon zombie 0 64 8 {NoAI:1,Tags:["ring_south"],Health:20}' },
    { action: 'execute_command', command: 'summon zombie 0 64 -8 {NoAI:1,Tags:["ring_north"],Health:20}' },

    // Spawn zombie OUTSIDE radius (should NOT be affected)
    { action: 'execute_command', command: 'summon zombie 12 64 0 {NoAI:1,Tags:["outside"],Health:20}' },

    // Protect player from fire
    { action: 'execute_command', command: 'effect give TestPlayer fire_resistance 30 1' },

    // Equip fragment
    { action: 'execute_command', command: 'give TestPlayer blaze_powder' },
    { action: 'execute_player_command', player: 'tester', command: '/fire equip' },

    // Record initial health
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=ring_east] Health',
      store_as: 'initial_health'
    },

    // Execute Infernal Dominion
    { action: 'execute_player_command', player: 'tester', command: '/fire 2' },

    // Wait for damage ticks
    { action: 'wait', duration: 3000 }, // 3 seconds

    // Verify damage dealt
    {
      action: 'execute_command',
      command: 'execute as @e[tag=ring_east] if entity @s[health=..19] run tellraw @a {"text":"RING_DAMAGED","color":"green"}'
    },

    // Verify outside entity undamaged
    {
      action: 'execute_command',
      command: 'execute as @e[tag=outside] if entity @s[health=20] run tellraw @a {"text":"OUTSIDE_SAFE","color":"green"}'
    }
  ]
}
```

---

## Passive Bonus: Fire Resistance

**What it does:**
- Player takes no damage from fire, lava, or blaze attacks when fragment is equipped
- Applied as permanent potion effect while equipped

**Testing Approach:**
1. Teleport player to spawn
2. Unequip all fragments
3. Place fire/lava source near player
4. Record player health
5. Walk into fire/lava
6. **Verify WITHOUT fragment:** Player takes damage
7. Equip Burning Fragment
8. Heal player to full
9. Walk into fire/lava again
10. **Verify WITH fragment:** Player takes NO damage

**Pilaf Test Structure:**
```javascript
{
  steps: [
    // Setup
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'clear TestPlayer' }, // Unequip all

    // Place fire under player
    { action: 'execute_command', command: 'setblock ~ ~ ~ fire' },

    // Get baseline health
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_before' },

    // Walk into fire (without protection - expect damage)
    { action: 'execute_command', command: 'tp TestPlayer ~ ~ ~0.1' }, // Slight movement down

    { action: 'wait', duration: 500 },
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_after_fire_no_protect' },

    // Verify damage taken
    {
      action: 'assert',
      condition: 'less_than',
      actual: '{health_after_fire_no_protect}',
      expected: '{health_before}'
    },

    // Now equip Burning Fragment and test again
    { action: 'execute_command', command: 'effect give TestPlayer regeneration 5 1' }, // Heal up
    { action: 'execute_command', command: 'give TestPlayer blaze_powder' },
    { action: 'execute_player_command', player: 'tester', command: '/fire equip' },

    { action: 'wait', duration: 1000 }, // Wait for fire resistance to apply

    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_before_protected' },

    // Walk into fire again
    { action: 'execute_command', command: 'tp TestPlayer ~ ~ ~0.1' },

    { action: 'wait', duration: 500 },
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_after_fire_protected' },

    // Verify NO damage taken
    {
      action: 'assert',
      condition: 'equals',
      actual: '{health_after_fire_protected}',
      expected: '{health_before_protected}'
    }
  ]
}
```

**Alternative:** Use `/damage` command with fire damage type to simulate fire damage more precisely.

---

## Cooldown Tests

### Cooldown Enforcement

**Test:** Using ability during cooldown should fail with message

```javascript
{
  steps: [
    { action: 'execute_command', command: 'give TestPlayer blaze_powder' },
    { action: 'execute_player_command', player: 'tester', command: '/fire equip' },

    // Use ability
    { action: 'execute_player_command', player: 'tester', command: '/fire 1' },

    // Try to use again immediately (should fail)
    {
      action: 'execute_player_command',
      player: 'tester',
      command: '/fire 1',
      expect_failure: true,
      expect_output: /cooldown|on cooldown/i
    }
  ]
}
```

### Cooldown Persistence

**Test:** Cooldown should persist across relog, unequip/equip, `/clear`

See `cooldown-system-test-plan.md` for detailed persistence tests.

---

## Test File Structure

```
pilaf-tests/stories/02-burning-fragment/
├── dragons-wrath.test.js        # /fire 1 - Fireball attack
├── infernal-dominion.test.js     # /fire 2 - Fire ring
├── passive-fire-resistance.test.js
└── cooldown-persistence.test.js  # (shared with other fragments)
```

---

## Verification Checklist

- [ ] Dragon's Wrath fireball spawns and travels toward target
- [ ] Dragon's Wrath fireball explodes and damages target
- [ ] Dragon's Wrath fireball ignites blocks (fire blocks created)
- [ ] Dragon's Wrath has 40-second cooldown
- [ ] Infernal Dominion creates fire ring at radius 10
- [ ] Infernal Dominion damages entities inside ring
- [ ] Infernal Dominion does NOT damage entities outside ring
- [ ] Infernal Dominion lasts 10 seconds
- [ ] Infernal Dominion deals ~1 heart/second damage rate
- [ ] Infernal Dominion has 60-second cooldown
- [ ] Passive Fire Resistance prevents fire damage
- [ ] Passive Fire Resistance is applied when equipped
- [ ] Passive Fire Resistance is removed when unequipped
