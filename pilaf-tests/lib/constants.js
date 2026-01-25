/**
 * Cooldown values in seconds (must match plugin defaults)
 * Source: Individual Fragment classes
 */
export const COOLDOWNS = {
  LIGHTNING: 60,                     // LightningAbility.java
  FIRE_1: 40,                       // Dragon's Wrath - BurningFragment.java
  FIRE_2: 60,                       // Infernal Dominion - BurningFragment.java
  AGILE_1: 30,                      // Draconic Surge - AgilityFragment.java
  AGILE_2: 45,                      // Wing Burst - AgilityFragment.java
  IMMORTAL_1: 120,                  // Draconic Reflex - ImmortalFragment.java (2 minutes)
  IMMORTAL_2: 480,                  // Essence Rebirth - ImmortalFragment.java (8 minutes)
  CORRUPT_1: 180,                   // Dread Gaze - CorruptedCoreFragment.java (3 minutes)
  CORRUPT_2: 120                    // Life Devourer - CorruptedCoreFragment.java (2 minutes)
};

/**
 * Entity positions relative to player spawn (0, 64, 0)
 * Player faces North by default (negative Z)
 */
export const ENTITY_POSITIONS = {
  // Cardinal directions (10 blocks away)
  NORTH: { x: 0, y: 64, z: -10 },
  SOUTH: { x: 0, y: 64, z: 10 },
  EAST: { x: 10, y: 64, z: 0 },
  WEST: { x: -10, y: 64, z: 0 },

  // Diagonal directions (7 blocks away)
  NORTHEAST: { x: 7, y: 64, z: -7 },
  NORTHWEST: { x: -7, y: 64, z: -7 },
  SOUTHEAST: { x: 7, y: 64, z: 7 },
  SOUTHWEST: { x: -7, y: 64, z: 7 },

  // Ring positions (radius 8 for AoE tests)
  RING_8: [
    { x: 8, y: 64, z: 0 },   // East
    { x: -8, y: 64, z: 0 },  // West
    { x: 0, y: 64, z: 8 },   // South
    { x: 0, y: 64, z: -8 },  // North
    { x: 6, y: 64, z: 6 },   // Southeast
    { x: -6, y: 64, z: 6 },  // Southwest
    { x: 6, y: 64, z: -6 },  // Northeast
    { x: -6, y: 64, z: -6 }  // Northwest
  ],

  // Outside AoE radius (should not be affected)
  OUTSIDE_RADIUS: { x: 12, y: 64, z: 0 },

  // At exact 50 blocks (max lightning range)
  MAX_LIGHTNING_RANGE: { x: 0, y: 64, z: -50 }
};

/**
 * Entity health values
 */
export const ENTITY_HEALTH = {
  ZOMBIE: 20,       // 10 hearts
  SKELETON: 20,     // 10 hearts
  CREEPER: 20,      // 10 hearts
  SPIDER: 16,       // 8 hearts
  VILLAGER: 20      // 10 hearts
};

/**
 * Ability damage values (Issue #28)
 * Source: Individual Fragment/Ability classes
 */
export const ABILITY_DAMAGE = {
  LIGHTNING_STRIKE: 12.0,         // 3 strikes × 4 damage = 12 total (LightningAbility.java)
  DRAGONS_WRATH: 8.0,            // 4 hearts (BurningFragment.java - Issue #28)
  DRACONIC_SURGE_COLLISION: 6.0,  // 3 hearts dash collision (AgilityFragment.java - Issue #28)
  LIFE_DEVOURER_STEAL_PERCENT: 0.25, // 25% lifesteal (CorruptedCoreFragment.java - Issue #28)
};

/**
 * Effect durations (in milliseconds)
 * Source: Individual Fragment classes
 */
export const EFFECT_DURATIONS = {
  // Agility Fragment (AgilityFragment.java)
  DRACONIC_SURGE: 1000,           // 1 second (20 ticks) dash duration
  DRACONIC_SURGE_FALL_PROTECTION: 10000,  // 10 seconds fall protection

  WING_BURST_PUSH: 2000,          // 2 seconds (40 ticks) push duration
  WING_BURST_SLOW_FALLING: 10000, // 10 seconds slow falling (after push)

  // Corrupted Core Fragment (CorruptedCoreFragment.java)
  DREAD_GAZE_FREEZE: 4000,        // 4 seconds (80 ticks) complete freeze
  DREAD_GAZE_FREEZE_TICKS: 80,    // 80 ticks = 4 seconds

  LIFE_DEVOURER: 20000,           // 20 seconds (400 ticks) lifesteal active

  // Immortal Fragment (ImmortalFragment.java)
  DRAONIC_REFLEX: 15000,          // 15 seconds (300 ticks) dodge chance
  ESSENCE_REBIRTH: 30000,         // 30 seconds (600 ticks) death protection window

  // Burning Fragment (BurningFragment.java)
  INFERNAL_DOMINION: 10000,       // 10 seconds (200 ticks) fire ring
  INFERNAL_DOMINION_TICKS: 200,   // 200 ticks

  // Lightning (LightningAbility.java)
  LIGHTNING_STRIKE_INTERVAL: 500, // 0.5 seconds (10 ticks) between strikes
  LIGHTNING_STRIKE_COUNT: 3,      // 3 strikes total
};

/**
 * Entity spawning NBT tags for frozen test entities
 */
export const FROZEN_ENTITY_NBT = {
  // Prevents movement/attacks, prevents despawn, allows targeting via Tags
  DEFAULT: 'NoAI:1,Silent:1,PersistenceRequired:1'
};
