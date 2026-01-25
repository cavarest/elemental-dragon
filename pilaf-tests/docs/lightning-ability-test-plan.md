# Lightning Ability Test Plan

## Ability Information

**Command:** `/lightning` (also available via Dragon Egg in offhand)
**Cooldown:** 60 seconds
**Material:** Dragon Egg (held in offhand)
**Element:** Neutral (Ancient Dragon power)

## Ability Description

**What it does (from source code):**
- Strikes target with 3 purple lightning bolts
- Each strike deals 4.0 damage (2 hearts) that bypasses armor
- Strikes are 0.5 seconds apart (10 ticks between strikes)
- Auto-targets closest living entity in player's viewing cone
- Intelligent target switching: if target dies, moves to next closest target
- Max range: 50 blocks
- Requires Dragon Egg in offhand to cast

**Implementation Details:**
```java
private static final int STRIKE_COUNT = 3;
private static final long STRIKE_INTERVAL_TICKS = 10L; // 0.5 seconds
private static final double DAMAGE_PER_STRIKE = 4.0; // 2.0 hearts (bypasses armor)
private static final long COOLDOWN_MILLIS = 60000L; // 60 seconds
private static final double MAX_RANGE = 50.0;
```

---

## Test Scenarios

### 1. Basic Lightning Strike

**Testing Approach:**
1. Spawn frozen zombie at known position (10 blocks North, in view cone)
2. Give player Dragon Egg in offhand
3. Face North (yaw 0) toward zombie
4. Execute `/lightning`
5. **Verify:** Zombie takes damage from first strike
6. **Verify:** Zombie takes damage from second strike (0.5s later)
7. **Verify:** Zombie takes damage from third strike (1.0s later)
8. **Verify:** Total damage = 12.0 health (3 × 4.0 damage)
9. **Verify:** Lightning visual effects appeared (purple particles)

**Pilaf Test Structure:**
```javascript
{
  name: "Lightning - Basic Three Strike Attack",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' }, // Facing North

    // Spawn zombie in view cone (10 blocks North)
    {
      action: 'execute_command',
      command: 'summon zombie 0 64 -10 {NoAI:1,Silent:1,Tags:["lightning_target"],Health:20}'
    },

    // Give Dragon Egg in offhand
    { action: 'execute_command', command: 'give TestPlayer dragon_egg' },
    { action: 'execute_command', command: 'item replace entity TestPlayer weapon.offhand with dragon_egg' },

    // Record initial health
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=lightning_target] Health',
      store_as: 'initial_health'
    },

    // Execute lightning
    { action: 'execute_player_command', player: 'tester', command: '/lightning' },

    // Wait for all 3 strikes to complete (3 strikes × 0.5s intervals = 1 second total + buffer)
    { action: 'wait', duration: 2000 },

    // Record final health
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=lightning_target] Health',
      store_as: 'final_health'
    },

    // Verify damage dealt: 20 - 12 = 8 health remaining
    { action: 'assert', condition: 'equals', actual: '{final_health}', expected: 8 }
  ]
}
```

---

### 2. Intelligent Target Switching

**What it does:**
- If initial target dies before all 3 strikes complete
- Lightning automatically switches to next closest target
- Continues striking until all 3 strikes are delivered

**Testing Approach:**
1. Spawn weak zombie (4 health) at close range
2. Spawn strong zombie (20 health) at medium range
3. Execute lightning targeting weak zombie
4. **Verify:** First strike kills weak zombie (4 damage)
5. **Verify:** Second strike hits strong zombie (target switching message)
6. **Verify:** Third strike hits strong zombie
7. **Verify:** Strong zombie took 8 damage total (2 strikes)

**Pilaf Test Structure:**
```javascript
{
  name: "Lightning - Intelligent Target Switching",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' },

    // Spawn weak zombie (dies in 1 strike) at 10 blocks
    {
      action: 'execute_command',
      command: 'summon zombie 0 64 -10 {NoAI:1,Silent:1,Tags:["weak_target"],Health:4}'
    },

    // Spawn strong zombie at 12 blocks (further away)
    {
      action: 'execute_command',
      command: 'summon zombie 0 64 -12 {NoAI:1,Silent:1,Tags:["strong_target"],Health:20}'
    },

    // Give Dragon Egg
    { action: 'execute_command', command: 'item replace entity TestPlayer weapon.offhand with dragon_egg' },

    // Execute lightning
    { action: 'execute_player_command', player: 'tester', command: '/lightning' },

    // Wait for all strikes
    { action: 'wait', duration: 2000 },

    // Verify weak zombie died (entity no longer exists)
    {
      action: 'execute_command',
      command: 'execute unless entity @e[tag=weak_target] run tellraw @a {"text":"WEAK_TARGET_DIED","color":"green"}'
    },

    // Verify strong zombie took damage (2 strikes = 8 damage, should have 12 health)
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=strong_target] Health',
      store_as: 'strong_zombie_health'
    },
    { action: 'assert', condition: 'equals', actual: '{strong_zombie_health}', expected: 12 }
  ]
}
```

---

### 3. Armor Bypassing Damage

**What it does:**
- Lightning damage bypasses ALL armor and enchantments
- Deals direct damage to health using `setHealth()`

**Testing Approach:**
1. Spawn zombie with full diamond armor (high protection)
2. Spawn zombie without armor
3. Execute lightning on armored zombie
4. Execute lightning on unarmored zombie
5. **Verify:** Both zombies take same damage (armor doesn't matter)

**Pilaf Test Structure:**
```javascript
{
  name: "Lightning - Armor Bypassing",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' },

    // Spawn armored zombie with full diamond armor
    {
      action: 'execute_command',
      command: 'summon zombie 0 64 -10 {NoAI:1,Silent:1,Tags:["armored"],Health:20,ArmorItems:[{},{},{},{}],HandItems:[{id:"diamond_sword",Count:1},{}]}'
    },

    // Spawn unarmored zombie
    {
      action: 'execute_command',
      command: 'summon zombie 2 64 -10 {NoAI:1,Silent:1,Tags:["unarmored"],Health:20}'
    },

    // Give Dragon Egg
    { action: 'execute_command', command: 'item replace entity TestPlayer weapon.offhand with dragon_egg' },

    // Face armored zombie
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' },

    // Strike armored zombie
    { action: 'execute_player_command', player: 'tester', command: '/lightning' },
    { action: 'wait', duration: 2000 },

    // Check armored zombie health (should be 8 = 20 - 12)
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=armored] Health',
      store_as: 'armored_health'
    },

    // Face unarmored zombie
    { action: 'execute_command', command: 'tp TestPlayer 2 64 0 0 0' },

    // Strike unarmored zombie
    { action: 'execute_player_command', player: 'tester', command: '/lightning' },
    { action: 'wait', duration: 2000 },

    // Check unarmored zombie health (should be 8 = 20 - 12)
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=unarmored] Health',
      store_as: 'unarmored_health'
    },

    // Verify both have same health (armor didn't reduce damage)
    { action: 'assert', condition: 'equals', actual: '{armored_health}', expected: 8 },
    { action: 'assert', condition: 'equals', actual: '{unarmored_health}', expected: 8 }
  ]
}
```

---

### 4. No Target Error

**Testing Approach:**
1. Clear all entities
2. Execute `/lightning`
3. **Verify:** Error message "no foe in your sights"

**Pilaf Test Structure:**
```javascript
{
  name: "Lightning - No Target Error",
  steps: [
    // Setup - no entities
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },

    // Give Dragon Egg
    { action: 'execute_command', command: 'item replace entity TestPlayer weapon.offhand with dragon_egg' },

    // Try to execute lightning with no target
    {
      action: 'execute_player_command',
      player: 'tester',
      command: '/lightning',
      expect_failure: true,
      expect_output: /no foe in your sights/i
    }
  ]
}
```

---

### 5. Dragon Egg Requirement

**What it does:**
- Lightning ability requires Dragon Egg in offhand
- If Dragon Egg is removed during casting, ability stops
- Error message if Dragon Egg not in offhand

**Testing Approach:**
1. Execute `/lightning` without Dragon Egg
2. **Verify:** Error message "dragon's power fades"
3. Give Dragon Egg, execute ability
4. Remove Dragon Egg during casting
5. **Verify:** Ability stops mid-cast

**Pilaf Test Structure:**
```javascript
{
  name: "Lightning - Dragon Egg Requirement",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' },
    { action: 'execute_command', command: 'clear TestPlayer' },

    // Spawn target
    {
      action: 'execute_command',
      command: 'summon zombie 0 64 -10 {NoAI:1,Silent:1,Tags:["egg_test"],Health:20}'
    },

    // Try without Dragon Egg
    {
      action: 'execute_player_command',
      player: 'tester',
      command: '/lightning',
      expect_failure: true,
      expect_output: /power fades|Dragon Egg/i
    },

    // Give Dragon Egg
    { action: 'execute_command', command: 'item replace entity TestPlayer weapon.offhand with dragon_egg' },

    // Execute lightning successfully
    { action: 'execute_player_command', player: 'tester', command: '/lightning' },
    { action: 'wait', duration: 1000 },

    // Verify damage dealt
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=egg_test] Health',
      store_as: 'health_with_egg'
    },
    { action: 'assert', condition: 'less_than', actual: '{health_with_egg}', expected: 20 }
  ]
}
```

---

### 6. Cooldown Test

**Testing Approach:**
1. Execute `/lightning`
2. Immediately try to execute again
3. **Verify:** Cooldown message or ability fails
4. Wait 60 seconds
5. **Verify:** Ability can be used again

**Pilaf Test Structure:**
```javascript
{
  name: "Lightning - Cooldown Enforcement",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' },

    // Spawn target
    {
      action: 'execute_command',
      command: 'summon zombie 0 64 -10 {NoAI:1,Silent:1,Tags:["cooldown_test"],Health:20}'
    },

    // Give Dragon Egg
    { action: 'execute_command', command: 'item replace entity TestPlayer weapon.offhand with dragon_egg' },

    // Execute lightning
    { action: 'execute_player_command', player: 'tester', command: '/lightning' },
    { action: 'wait', duration: 500 },

    // Try to execute again immediately (should fail)
    {
      action: 'execute_player_command',
      player: 'tester',
      command: '/lightning',
      expect_failure: true,
      expect_output: /cooldown|on cooldown/i
    }
  ]
}
```

---

### 7. Max Range Test

**Testing Approach:**
1. Spawn zombie at exactly 50 blocks (max range)
2. Spawn zombie at 51 blocks (outside range)
3. Face toward both zombies
4. Execute `/lightning`
5. **Verify:** Zombie at 50 blocks is targeted
6. **Verify:** Zombie at 51 blocks is NOT targeted

**Pilaf Test Structure:**
```javascript
{
  name: "Lightning - Max Range (50 blocks)",
  steps: [
    // Setup
    { action: 'execute_command', command: 'kill @e[type=!player]' },
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0 0 0' }, // Facing North

    // Spawn zombie at exactly 50 blocks North (max range)
    {
      action: 'execute_command',
      command: 'summon zombie 0 64 -50 {NoAI:1,Silent:1,Tags:["max_range"],Health:20}'
    },

    // Give Dragon Egg
    { action: 'execute_command', command: 'item replace entity TestPlayer weapon.offhand with dragon_egg' },

    // Execute lightning
    { action: 'execute_player_command', player: 'tester', command: '/lightning' },
    { action: 'wait', duration: 2000 },

    // Verify zombie at 50 blocks took damage
    {
      action: 'execute_command',
      command: 'data get entity @e[tag=max_range] Health',
      store_as: 'max_range_health'
    },
    { action: 'assert', condition: 'less_than', actual: '{max_range_health}', expected: 20 }
  ]
}
```

---

## Test File Structure

```
pilaf-tests/stories/06-lightning-ability/
├── basic-strike.test.js         # 3-strike basic attack
├── target-switching.test.js     # Intelligent target switching
├── armor-bypass.test.js         # Verify damage bypasses armor
├── no-target-error.test.js      # Error when no target
├── dragon-egg-requirement.test.js  # Dragon Egg in offhand required
├── cooldown.test.js             # 60-second cooldown
└── max-range.test.js            # 50 block max range
```

---

## Verification Checklist

- [ ] Lightning strikes 3 times
- [ ] Each strike deals 4.0 damage (2 hearts)
- [ ] Total damage = 12.0 health
- [ ] Strikes are 0.5 seconds apart
- [ ] Visual effects appear (purple lightning)
- [ ] Thunder sound plays
- [ ] Targets closest entity in viewing cone
- [ ] Intelligent target switching when target dies
- [ ] Damage bypasses armor completely
- [ ] Requires Dragon Egg in offhand
- [ ] Stops casting if Dragon Egg removed mid-cast
- [ ] Shows error when no target found
- [ ] Has 60-second cooldown
- [ ] Max range is 50 blocks
- [ ] Does not target entities behind player (viewing cone)

---

## Special Testing Considerations

### Viewing Cone Detection
The ability uses `EntityTargeter.findInViewingCone()` with a threshold of 0.9. This means:
- Entity must be roughly in front of player (within view cone)
- Entities behind player are NOT targeted
- Test positioning must account for player's facing direction

### Direct Health Damage
Lightning uses `dealDirectDamage()` which:
- Bypasses ALL armor calculations
- Directly modifies entity health via `setHealth()`
- Cannot be reduced by armor enchantments

### Target Switching Logic
- Only switches if current target is null, dead, or invalid
- Searches for next closest entity in viewing cone
- Excludes the dead/invalid target from search
- Continues until 3 strikes are delivered or no targets remain

### Mid-Cast Interruption
- Checks Dragon Egg requirement before EACH strike
- If Dragon Egg removed, ability stops immediately
- Shows cancellation message to player
- Remaining strikes are NOT delivered
