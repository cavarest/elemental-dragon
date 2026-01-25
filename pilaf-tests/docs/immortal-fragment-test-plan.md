# Immortal Fragment Test Plan

## Fragment Information

**Element:** Earth
**Material:** Diamond
**Permission:** `elementaldragon.fragment.immortal`

## Abilities

### 1. Draconic Reflex (`/immortal 1`)

**Cooldown:** 120 seconds (2 minutes)

**What it does (from source code):**
- Grants 20% dodge chance (1 in 5) to avoid all damage
- Duration: 15 seconds (300 ticks)
- When dodge succeeds: Damage is cancelled completely, shows gold particles, plays guardian hurt sound
- When dodge fails: Plays anvil sound (damage is taken normally)

**Implementation Details:**
```java
private static final int DRACONIC_REFLEX_DURATION = 300; // 15 seconds = 300 ticks
private static final double DRACONIC_REFLEX_DODGE_CHANCE = 0.2; // 20% (1/5 chance)
```

**Testing Approach:**
1. Test dodge success (20% chance) - needs multiple attempts or statistical testing
2. Test dodge failure (80% chance) - verify damage is taken and anvil sound plays
3. Test duration expiration after 15 seconds

**Test Challenges:**
- Dodge chance is probabilistic - need 20+ attacks to reliably see both outcomes
- Can force test by running ability multiple times and checking statistics
- Alternatively, use `/damage` command with controlled amounts

**Statistical Test Approach:**
1. Activate Draconic Reflex
2. Deal 1 heart of damage 25 times using `/damage` command
3. Record how many times damage was negated (should be ~5 out of 25)
4. Verify at least 1 dodge succeeded and at least 1 dodge failed

**Pilaf Test Structure:**
```javascript
{
  name: "Draconic Reflex - Statistical Dodge Test",
  steps: [
    // Setup
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'give TestPlayer diamond' },
    { action: 'execute_player_command', player: 'tester', command: '/immortal equip' },

    // Record initial health
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'initial_health' },

    // Activate Draconic Reflex
    { action: 'execute_player_command', player: 'tester', command: '/immortal 1' },

    // Deal 1 heart damage 25 times (20% dodge chance = ~5 dodges expected)
    // We'll track successful dodges via chat messages
    {
      action: 'repeat',
      count: 25,
      steps: [
        { action: 'execute_command', command: 'damage @p 1 generic' },
        { action: 'wait', duration: 100 }
      ]
    },

    // Check final health (should be ~20 - 20 + 5 = 5 hearts if 5 dodges occurred)
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'final_health' },

    // Verify we didn't die (health > 0)
    { action: 'assert', condition: 'greater_than', actual: '{final_health}', expected: 0 },

    // Wait for ability to expire (15 seconds)
    { action: 'wait', duration: 16000 },

    // Now damage should no longer be dodged
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'before_expiry_test' },
    { action: 'execute_command', command: 'damage @p 5 generic' },
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'after_expiry_test' },

    // Verify damage was taken (health decreased by 5)
    {
      action: 'calculate_difference',
      minuend: '{before_expiry_test}',
      subtrahend: '{after_expiry_test}',
      store_as: 'damage_taken'
    },
    { action: 'assert', condition: 'equals', actual: '{damage_taken}', expected: 5 }
  ]
}
```

---

### 2. Essence Rebirth (`/immortal 2`)

**Cooldown:** 480 seconds (8 minutes)

**What it does (from source code):**
- Grants 30-second window of death protection
- If fatal damage would occur during this window, damage is cancelled
- Player is restored to full max health
- Plays totem sound and shows totem particles
- Active ability has 30-second duration, then expires if not used

**Implementation Details:**
```java
private static final long ESSENCE_REBIRTH_COOLDOWN = 480000L; // 8 minutes
private static final int ESSENSE_REBIRTH_DURATION = 600; // 30 seconds = 600 ticks
```

**Testing Approach:**
1. Activate Essence Rebirth
2. Deal fatal damage (more than player's current health)
3. **Verify:** Player does NOT die
4. **Verify:** Player is restored to full health
5. **Verify:** Totem sound and particles appeared
6. **Verify:** Essence Rebirth is consumed (metadata removed)

**Pilaf Test Structure:**
```javascript
{
  name: "Essence Rebirth - Death Prevention",
  steps: [
    // Setup
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'give TestPlayer diamond' },
    { action: 'execute_player_command', player: 'tester', command: '/immortal equip' },

    // Heal to full
    { action: 'execute_command', command: 'effect give TestPlayer regeneration 2 10' },

    // Wait for full health
    { action: 'wait', duration: 3000 },

    // Record health before ability
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_before' },

    // Activate Essence Rebirth
    { action: 'execute_player_command', player: 'tester', command: '/immortal 2' },

    // Deal fatal damage (100 damage - definitely fatal)
    { action: 'execute_command', command: 'damage @p 100 generic' },

    // Wait for totem animation
    { action: 'wait', duration: 1000 },

    // Verify player is still alive (not dead)
    {
      action: 'execute_command',
      command: 'execute if entity @p run tellraw @a {"text":"SURVIVED_FATAL_DAMAGE","color":"green"}'
    },

    // Verify player is at full health (max health with passive = 24.0 for +2 hearts)
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_after' },

    // Health should be at max (24.0 with passive, or 20.0 without)
    {
      action: 'assert',
      condition: 'greater_than_or_equal',
      actual: '{health_after}',
      expected: 20 // At minimum should be at normal max health
    },

    // Verify Essence Rebirth was consumed (no longer has protection metadata)
    {
      action: 'execute_command',
      command: 'execute unless entity @p[nbt={ }] run tellraw @a {"text":"REBIRTH_CONSUMED","color":"green"}'
      // Note: Metadata check isn't directly accessible via commands
      // Alternative: Deal fatal damage again - this time should die
    },

    // Test that protection is gone - deal fatal damage again
    { action: 'execute_command', command: 'damage @p 1000 generic' },

    { action: 'wait', duration: 2000 },

    // Verify player died (no longer exists at original location)
    {
      action: 'execute_command',
      command: 'execute unless entity @p[tag=alive] run tellraw @a {"text":"DIED_AS_EXPECTED","color":"yellow"}'
    }
  ]
}
```

**Note:** The final death test will cause the test player to respawn. This may affect subsequent tests. Consider splitting this into separate test stories or using `/kill` with respawn handling.

---

## Passive Bonus: Permanent Totem of Undying

**What it does (from source code):**
- While Immortal Fragment is equipped, player has permanent totem protection
- Fatal damage is cancelled and player restored to full health
- Works even WITHOUT Essence Rebirth active (passive effect)
- +2 hearts to max health (4.0 health)
- Resistance I potion effect (minimal damage reduction)

**Implementation Details:**
```java
// From onEntityDamageForEssenceRebirth():
// Passive: Immortal Fragment acts as permanent Totem of Undying when equipped
// Max health boost: +4.0 (2 hearts)
// Resistance I: amplifier 0, Integer.MAX_VALUE duration
```

### Passive Test 1: Max Health Boost

**Testing Approach:**
1. Clear all effects and inventory
2. Check base max health (should be 20.0)
3. Equip Immortal Fragment
4. **Verify:** Max health is now 24.0 (20.0 + 4.0)
5. **Verify:** Current health increased by 4.0
6. Unequip fragment
7. **Verify:** Max health back to 20.0

**Pilaf Test Structure:**
```javascript
{
  name: "Immortal Fragment - Passive +2 Hearts",
  steps: [
    // Setup - clear everything
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'clear TestPlayer' },

    // Verify base max health is 20.0
    {
      action: 'execute_command',
      command: 'attribute @p generic.max_health base get',
      store_as: 'base_max_health'
    },
    { action: 'assert', condition: 'equals', actual: '{base_max_health}', expected: 20.0 },

    // Verify current health is 20.0
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'base_health' },
    { action: 'assert', condition: 'equals', actual: '{base_health}', expected: 20.0 },

    // Equip Immortal Fragment
    { action: 'execute_command', command: 'give TestPlayer diamond' },
    { action: 'execute_player_command', player: 'tester', command: '/immortal equip' },
    { action: 'wait', duration: 500 },

    // Verify max health is now 24.0 (20.0 + 4.0)
    {
      action: 'execute_command',
      command: 'attribute @p generic.max_health base get',
      store_as: 'boosted_max_health'
    },
    { action: 'assert', condition: 'equals', actual: '{boosted_max_health}', expected: 24.0 },

    // Verify current health increased to 24.0
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'boosted_health' },
    { action: 'assert', condition: 'equals', actual: '{boosted_health}', expected: 24.0 },

    // Unequip fragment
    { action: 'execute_command', command: 'clear TestPlayer' },
    { action: 'wait', duration: 500 },

    // Verify max health returned to 20.0
    {
      action: 'execute_command',
      command: 'attribute @p generic.max_health base get',
      store_as: 'final_max_health'
    },
    { action: 'assert', condition: 'equals', actual: '{final_max_health}', expected: 20.0 },

    // Verify current health capped at 20.0
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'final_health' },
    { action: 'assert', condition: 'equals', actual: '{final_health}', expected: 20.0 }
  ]
}
```

### Passive Test 2: Permanent Totem Protection (Without Active Ability)

**Testing Approach:**
1. Equip Immortal Fragment
2. DO NOT activate Essence Rebirth
3. Deal fatal damage
4. **Verify:** Player survives with totem effect
5. **Verify:** "Immortal Fragment saved you!" message appears

**Pilaf Test Structure:**
```javascript
{
  name: "Immortal Fragment - Passive Totem Protection",
  steps: [
    // Setup
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'give TestPlayer diamond' },

    // Equip fragment (DO NOT activate Essence Rebirth)
    { action: 'execute_player_command', player: 'tester', command: '/immortal equip' },

    // Heal to full
    { action: 'execute_command', command: 'effect give TestPlayer regeneration 2 10' },
    { action: 'wait', duration: 3000 },

    // Deal fatal damage WITHOUT activating Essence Rebirth
    { action: 'execute_command', command: 'damage @p 100 generic' },

    { action: 'wait', duration: 1000 },

    // Verify player survived (passive totem saved them)
    {
      action: 'execute_command',
      command: 'execute if entity @p run tellraw @a {"text":"PASSIVE_TOTEM_SAVED","color":"green"}'
    },

    // Verify player at full health
    { action: 'execute_command', command: 'data get entity @p Health', store_as: 'health_after_totem' },
    { action: 'assert', condition: 'greater_than_or_equal', actual: '{health_after_totem}', expected: 20 }
  ]
}
```

### Passive Test 3: Resistance I Effect

**Testing Approach:**
1. Equip Immortal Fragment
2. **Verify:** Resistance I potion effect is applied
3. Deal known damage amount
4. **Verify:** Damage is slightly reduced (Resistance I = ~5% reduction)

**Pilaf Test Structure:**
```javascript
{
  name: "Immortal Fragment - Passive Resistance I",
  steps: [
    // Setup
    { action: 'execute_command', command: 'tp TestPlayer 0 64 0' },
    { action: 'execute_command', command: 'effect clear TestPlayer' },
    { action: 'execute_command', command: 'clear TestPlayer' },

    // Verify no Resistance effect initially
    {
      action: 'execute_command',
      command: 'execute as @p unless entity @s[has_effect={effect:resistance}] run say NO_RESISTANCE'
    },

    // Equip fragment
    { action: 'execute_command', command: 'give TestPlayer diamond' },
    { action: 'execute_player_command', player: 'tester', command: '/immortal equip' },
    { action: 'wait', duration: 500 },

    // Verify Resistance I is applied (amplifier 0 = Resistance I)
    {
      action: 'execute_command',
      command: 'execute as @p if entity @s[has_effect={effect:resistance,amplifier:0}] run say RESISTANCE_I_ACTIVE'
    }
  ]
}
```

---

## Test File Structure

```
pilaf-tests/stories/04-immortal-fragment/
├── draconic-reflex.test.js       # /immortal 1 - 20% dodge chance
├── essence-rebirth.test.js       # /immortal 2 - Death protection
├── passive-max-health.test.js    # +2 hearts permanent
├── passive-totem.test.js         # Permanent totem protection
├── passive-resistance.test.js    # Resistance I effect
└── cooldown-persistence.test.js  # (shared with other fragments)
```

---

## Verification Checklist

- [ ] Draconic Reflex activates for 15 seconds
- [ ] Draconic Reflex provides ~20% dodge chance (statistical test)
- [ ] Draconic Reflex dodge negates all damage
- [ ] Draconic Reflex plays guardian hurt sound on successful dodge
- [ ] Draconic Reflex plays anvil sound on failed dodge
- [ ] Draconic Reflex expires after 15 seconds
- [ ] Draconic Reflex has 120-second cooldown
- [ ] Essence Rebirth prevents fatal damage during 30-second window
- [ ] Essence Rebirth restores to full health when triggered
- [ ] Essence Rebirth plays totem sound and particles
- [ ] Essence Rebirth is consumed after preventing death
- [ ] Essence Rebirth has 480-second cooldown
- [ ] Passive +2 hearts (4.0 max health) applied when equipped
- [ ] Passive +2 hearts removed when unequipped
- [ ] Passive totem protection works WITHOUT active ability
- [ ] Passive Resistance I applied when equipped
- [ ] Passive Resistance I removed when unequipped
