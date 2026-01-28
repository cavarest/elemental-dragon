# Pilaf Integration Testing Plan for Elemental Dragon Plugin

**Created:** 2025-01-23
**Status:** Planning Phase
**Author:** Claude Code (ULTRATHINK Mode)

---

## Executive Summary

This plan implements comprehensive integration testing for the Elemental Dragon Minecraft plugin using [Pilaf](https://github.com/cavarest/pilaf) - a JavaScript-based testing framework for PaperMC servers.

**Objectives:**
1. Test all 4 elemental fragments and their 8 abilities
2. Verify passive bonuses and cooldown systems
3. Ensure entity targeting works with predictable positioning
4. Integrate tests into GitHub Actions CI/CD pipeline

**Testing Coverage:**
- 1 Lightning ability
- 4 Elemental Fragments (Fire, Wind, Earth, Void)
- 8 Active abilities (2 per fragment)
- 4 Passive bonuses
- 6 Cooldown persistence behaviors

---

## Architecture Overview

### Pilaf Integration Model

```
┌─────────────────────────────────────────────────────────────┐
│                    GitHub Actions Workflow                   │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │ Build Plugin│  │ Start Server │  │ Run Pilaf Tests  │  │
│  │ (Gradle)    │  │ (Docker)     │  │ (Jest/Node.js)   │  │
│  └─────────────┘  └──────────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    PaperMC Server (Docker)                   │
│  Port 25565: Game, Port 25575: RCON                         │
│  - Plugin JAR mounted from build/libs/                      │
│  - flat-survival-test world profile                        │
│  - Fixed seed (42), flat terrain, no structure spawning    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Pilaf Test Runner                       │
│  ┌──────────────┐  ┌───────────────┐  ┌─────────────────┐  │
│  │ RCON Backend │  │ Mineflayer    │  │ Story Runner    │  │
│  │ (Server cmd) │  │ (Player sim)  │  │ (Test execution)│  │
│  └──────────────┘  └───────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Test Stories (JavaScript)                  │
│  - Lightning Tests          - Fire Fragment Tests           │
│  - Agility Fragment Tests   - Immortal Fragment Tests      │
│  - Corrupted Fragment Tests - Cooldown System Tests         │
└─────────────────────────────────────────────────────────────┘
```

### Directory Structure

```
papermc-plugin-dragon-egg/
├── pilaf-tests/                          # NEW: Integration test directory
│   ├── package.json                      # Node.js dependencies
│   ├── jest.config.js                    # Jest configuration
│   ├── pilaf.config.js                   # Pilaf configuration
│   ├── .github/workflows/
│   │   └── pilaf-integration.yml        # GitHub Actions workflow
│   ├── lib/
│   │   ├── entities.js                   # Entity spawning utilities
│   │   ├── assertions.js                 # Custom Minecraft assertions
│   │   ├── constants.js                  # Test constants (cooldowns, positions)
│   │   └── global-setup.js               # Global test setup
│   └── stories/
│       ├── 00-setup/
│       │   ├── connection.test.js       # Server connection test
│       │   └── entity-spawn.test.js     # Entity spawn verification
│       ├── 01-lightning/
│       │   └── lightning-strike.test.js # Lightning strike ability
│       ├── 02-burning-fragment/
│       │   ├── dragons-wrath.test.js   # /fire 1 - Fireball
│       │   ├── infernal-dominion.test.js # /fire 2 - Fire ring
│       │   ├── passive-fire-resistance.test.js
│       │   └── cooldown-persistence.test.js
│       ├── 03-agility-fragment/
│       │   ├── draconic-surge.test.js  # /agile 1 - 20-block dash
│       │   ├── wing-burst.test.js      # /agile 2 - Entity push (8-block radius)
│       │   └── passive-speed.test.js
│       ├── 04-immortal-fragment/
│       │   ├── draconic-reflex.test.js # /immortal 1 - 20% dodge chance
│       │   ├── essence-rebirth.test.js # /immortal 2 - Death protection window
│       │   └── passive-ancient-heart.test.js
│       ├── 05-corrupted-fragment/
│       │   ├── dread-gaze-freeze.test.js      # /corrupt 1 - Complete freeze on hit
│       │   ├── life-devourer-clever.test.js   # /corrupt 2 - 50% lifesteal
│       │   └── passive-dark-blessing.test.js
│       └── 06-cooldown-system/
│           ├── persistence-relog.test.js
│           ├── persistence-restart.test.js
│           ├── persistence-unequip.test.js
│           ├── persistence-clear.test.js
│           ├── reset-on-death.test.js
│           └── global-adjustment-min.test.js
└── world-profiles/
    └── flat-survival-test.sh           # NEW: Test-optimized world profile
```

---

## Detailed Fragment Test Plans

**IMPORTANT:** The following detailed test plans contain accurate ability information based on actual source code analysis. These plans are the single source of truth for test implementation.

- **[Burning Fragment Test Plan](pilaf-tests/docs/burning-fragment-test-plan.md)** - Dragon's Wrath (fireball), Infernal Dominion (fire ring), passive Fire Resistance
- **[Agility Fragment Test Plan](pilaf-tests/docs/agility-fragment-test-plan.md)** - Draconic Surge (20-block dash), Wing Burst (entity push), passive Speed I
- **[Immortal Fragment Test Plan](pilaf-tests/docs/immortal-fragment-test-plan.md)** - Draconic Reflex (20% dodge), Essence Rebirth (death protection), passive +2 hearts & totem
- **[Corrupted Core Fragment Test Plan](pilaf-tests/docs/corrupted-core-fragment-test-plan.md)** - Dread Gaze (complete freeze), Life Devourer (50% lifesteal), passive Night Vision, creeper invisibility, and enderman anti-aggro
- **[Lightning Ability Test Plan](pilaf-tests/docs/lightning-ability-test-plan.md)** - 3-strike lightning attack, intelligent target switching, armor bypassing

**Note:** The matrix below is for quick reference. See individual test plans for implementation details.

## Test Coverage Matrix

| Fragment | Ability | Command | Test File | Priority |
|----------|---------|---------|-----------|----------|
| Lightning | Strike (3× bolts) | `/lightning` | lightning-strike.test.js | P0 |
| Fire | Dragon's Wrath (fireball) | `/fire 1` | dragons-wrath.test.js | P0 |
| Fire | Infernal Dominion (fire ring) | `/fire 2` | infernal-dominion.test.js | P0 |
| Fire | Fire Resistance | Passive | passive-fire-resistance.test.js | P1 |
| Agility | Draconic Surge (20-block dash) | `/agile 1` | draconic-surge.test.js | P0 |
| Agility | Wing Burst (push 8-block radius) | `/agile 2` | wing-burst.test.js | P1 |
| Agility | Speed I | Passive | passive-speed.test.js | P1 |
| Immortal | Draconic Reflex (20% dodge) | `/immortal 1` | draconic-reflex.test.js | P1 |
| Immortal | Essence Rebirth (totem protection) | `/immortal 2` | essence-rebirth.test.js | P2 |
| Immortal | +2 Hearts & Resistance I | Passive | passive-ancient-heart.test.js | P1 |
| Corrupted | Dread Gaze (complete freeze) | `/corrupt 1` | dread-gaze-freeze.test.js | P1 |
| Corrupted | Life Devourer (50% lifesteal) | `/corrupt 2` | life-devourer-clever.test.js | P2 |
| Corrupted | Night Vision, creeper ghost, enderman anti-aggro | Passive | passive-dark-blessing.test.js | P1 |

**Priority Legend:**
- P0: Critical (blocks release if broken)
- P1: High (important features)
- P2: Medium (nice to have)

**Note:** See individual fragment test plans for accurate ability descriptions. Some documentation (README.md) contains outdated ability descriptions that don't match source code implementation.

---

## Entity Positioning Strategy

### The Problem

Targeted abilities like `/fire 1` (Dragon's Wrath - fireball) require:
1. Player to be looking at the target entity
2. Entity to be at a known, predictable position
3. Fireball tracking to work (fireballs home in on targets within view cone)

### The Solution: Spawn and Freeze

```javascript
// Spawn frozen zombie at exact position (10 blocks North)
summon zombie 0 64 -10 {
  NoAI:1,           // Prevents movement/attacks
  Silent:1,         // No ambient sounds
  PersistenceRequired:1,  // Won't despawn
  Tags:["target_zombie"],
  Age:-2147483648   // Prevent baby zombie growth
}
```

**Why This Works:**
- `NoAI:1` freezes entity in place (no movement, no attacks)
- `Silent:1` prevents noise interference
- `Tags:[]` allows precise entity targeting for verification
- Fixed coordinates ensure reproducible tests
- Flat world at y=64 provides predictable terrain

### Position Reference Frame

```
        North (-Z)
            ↑
            │
West (-X) ←─┼─→ East (+X)
            │
            ↓
        South (+Z)

Player spawn: 0, 64, 0
Test positions (relative to player):
- North target:    (0, 64, -10)
- South target:    (0, 64, +10)
- East target:     (+10, 64, 0)
- West target:     (-10, 64, 0)
- Ring radius 8:   (±8, 64, 0), (0, 64, ±8)
```

### Utility Implementation

```javascript
// lib/entities.js
async function spawnFrozenEntity(context, type, position, tag) {
  const nbt = `NoAI:1,Silent:1,PersistenceRequired:1,Tags:["${tag}"]`;
  const command = `summon ${type} ${position.x} ${position.y} ${position.z} {${nbt}}`;
  await context.rcon.send(command);
  await context.wait(100);
  return { type, position, tag };
}

async function clearAllEntities(context) {
  await context.rcon.send('kill @e[type=!player]');
  await context.wait(200);
}
```

---

## Test Implementation Patterns

### Pattern 1: Targeted Ability Test

**Test:** `/fire 1` - Dragon's Wrath (Homing Fireball)

**Ability Details**:
- Damage: 6.0 (3 hearts) - bypasses armor
- Explosion radius: 5 blocks
- Homing duration: 0.5 seconds (10 ticks)
- Target range: 50 blocks

```javascript
const { describe, it, expect } = require('@jest/globals');
const { StoryRunner } = require('@pilaf/framework');
const { spawnFrozenEntity, clearAllEntities } = require('../../lib/entities');
const { ENTITY_POSITIONS, COOLDOWNS } = require('../../lib/constants');

describe('Burning Fragment - Dragon\'s Wrath', () => {
  it('should launch fireball and hit target entity', async () => {
    const runner = new StoryRunner();

    const result = await runner.execute({
      name: 'Dragon\'s Wrath hits target',
      setup: {
        server: {
          type: 'paper',
          version: '1.21.8',
          rcon: {
            host: process.env.RCON_HOST || 'localhost',
            port: parseInt(process.env.RCON_PORT) || 25575,
            password: process.env.RCON_PASSWORD || 'dragon123'
          }
        },
        players: [
          {
            name: 'tester',
            username: 'PilafTestPlayer',
            auth: 'offline'
          }
        ]
      },
      steps: [
        // SETUP: Clear entities, spawn target
        {
          name: 'Clear all entities',
          action: 'execute_command',
          command: 'kill @e[type=!player]'
        },
        {
          name: 'Spawn target zombie at North position',
          action: 'execute_command',
          command: `summon zombie ${ENTITY_POSITIONS.NORTH.x} ${ENTITY_POSITIONS.NORTH.y} ${ENTITY_POSITIONS.NORTH.z} {NoAI:1,Silent:1,Tags:["fireball_target"]}`
        },
        {
          name: 'Teleport player to spawn facing North',
          action: 'execute_command',
          command: 'tp PilafTestPlayer 0 64 0 0 0' // x, y, z, yaw, pitch (0,0 = facing North)
        },

        // EQUIP: Give burning fragment
        {
          name: 'Give blaze powder (Burning Fragment)',
          action: 'execute_command',
          command: 'give PilafTestPlayer blaze_powder'
        },
        {
          name: 'Equip Burning Fragment',
          action: 'execute_player_command',
          player: 'tester',
          command: '/fire equip'
        },

        // EXECUTE: Fire ability
        {
          name: 'Execute Dragon\'s Wrath',
          action: 'execute_player_command',
          player: 'tester',
          command: '/fire 1'
        },

        // WAIT: For fireball travel (10 blocks @ ~1.5 blocks/tick)
        {
          name: 'Wait for fireball to reach target',
          action: 'wait',
          duration: 1000 // 1 second
        },

        // VERIFY: Target took damage or died
        {
          name: 'Get remaining entities with fireball_target tag',
          action: 'execute_command',
          command: 'execute as @e[tag=fireball_target] if entity @s[scores={health=..}] run say TARGET_ALIVE',
          store_as: 'target_check'
        },

        // ALTERNATIVE VERIFICATION: Check if entity still exists
        {
          name: 'Count tagged entities',
          action: 'execute_command',
          command: 'execute unless entity @e[tag=fireball_target] run tellraw @a {"text":"TARGET_DESTROYED"}',
          store_as: 'verify_destruction'
        }
      ],
      teardown: {
        stop_server: false,
        disconnect_players: true
      }
    });

    expect(result.success).toBe(true);
  });
});
```

### Pattern 2: AoE Ability Test

**Test:** `/fire 2` - Infernal Dominion (Fire Ring)

**Ability Details**:
- Radius: 10 blocks (entities inside take damage)
- Duration: 10 seconds (200 ticks)
- Damage: 1.0 (0.5 hearts) per tick = ~5 hearts total

```javascript
describe('Burning Fragment - Infernal Dominion', () => {
  it('should damage all entities within fire ring radius', async () => {
    const result = await runner.execute({
      name: 'Infernal Dominion damages ring entities',
      setup: { /* same as above */ },
      steps: [
        // SETUP: Spawn entities in ring pattern
        { action: 'execute_command', command: 'kill @e[type=!player]' },

        // Spawn zombies at radius 8 (inside 10-block radius)
        { action: 'execute_command', command: 'summon zombie 8 64 0 {NoAI:1,Tags:["ring_east"]}' },
        { action: 'execute_command', command: 'summon zombie -8 64 0 {NoAI:1,Tags:["ring_west"]}' },
        { action: 'execute_command', command: 'summon zombie 0 64 8 {NoAI:1,Tags:["ring_south"]}' },
        { action: 'execute_command', command: 'summon zombie 0 64 -8 {NoAI:1,Tags:["ring_north"]}' },

        // Spawn entity OUTSIDE radius (should NOT take damage)
        { action: 'execute_command', command: 'summon zombie 12 64 0 {NoAI:1,Tags:["outside"]}' },

        // EQUIP and PROTECT player (from fire)
        { action: 'execute_command', command: 'effect give PilafTestPlayer fire_resistance 30 1' },
        { action: 'execute_command', command: 'give PilafTestPlayer blaze_powder' },
        { action: 'execute_player_command', player: 'tester', command: '/fire equip' },

        // EXECUTE: Infernal Dominion
        { action: 'execute_player_command', player: 'tester', command: '/fire 2' },

        // WAIT: For damage ticks (3 seconds = ~1.5 hearts damage)
        { action: 'wait', duration: 3000 },

        // VERIFY: Entities inside ring took damage (outside should not have)
        {
          name: 'Check ring entities took damage',
          action: 'execute_command',
          command: 'execute if entity @e[tag=ring_east,health=..19] run say RING_DAMAGED' // Started at 20, damaged = <20
        },
        {
          name: 'Verify outside entity undamaged',
          action: 'execute_command',
          command: 'execute if entity @e[tag=outside,health=20] run say OUTSIDE_SAFE'
        }
      ],
      teardown: { stop_server: false }
    });

    expect(result.success).toBe(true);
  });
});
```

### Pattern 3: Movement Ability Test

**Test:** `/agile 1` - Draconic Surge (20-block dash)

```javascript
describe('Agility Fragment - Draconic Surge', () => {
  it('should dash player 20 blocks in facing direction', async () => {
    const result = await runner.execute({
      name: 'Draconic Surge dashes player forward',
      steps: [
        // SETUP
        { action: 'execute_command', command: 'tp PilafTestPlayer 0 64 0 0 0' }, // Facing North
        { action: 'execute_command', command: 'give PilafTestPlayer phantom_membrane' },
        { action: 'execute_player_command', player: 'tester', command: '/agile equip' },

        // Record start position
        {
          action: 'execute_command',
          command: 'data get entity @p Pos',
          store_as: 'start_pos'
        },

        // EXECUTE dash
        { action: 'execute_player_command', player: 'tester', command: '/agile 1' },

        // Wait for dash to complete (1 second + buffer)
        { action: 'wait', duration: 1500 },

        // Record end position
        {
          action: 'execute_command',
          command: 'data get entity @p Pos',
          store_as: 'end_pos'
        },

        // VERIFY: Player moved ~20 blocks North (Z decreased by ~20)
        // Use tolerance for floating point precision
        {
          action: 'assert_position_delta',
          axis: 'z',
          expected_delta: -20, // North is negative Z
          tolerance: 2 // Allow ±2 blocks for client-server desync
        }
      ]
    });

    expect(result.success).toBe(true);
  });
});
```

### Pattern 4: Cooldown Persistence Test

**Test:** Cooldown survives player relog

```javascript
describe('Cooldown System', () => {
  it('should persist cooldown across player disconnect/reconnect', async () => {
    const result = await runner.execute({
      name: 'Cooldown persists across relog',
      steps: [
        // SETUP: Equip fragment
        { action: 'execute_command', command: 'give PilafTestPlayer blaze_powder' },
        { action: 'execute_player_command', player: 'tester', command: '/fire equip' },

        // EXECUTE: Use ability (starts 40s cooldown)
        { action: 'execute_player_command', player: 'tester', command: '/fire 1' },

        // VERIFY: Cooldown started
        {
          name: 'Check cooldown started',
          action: 'execute_player_command',
          player: 'tester',
          command: '/fire status',
          expect_output: /cooldown.*\d+s/i
        },

        // DISCONNECT
        { action: 'disconnect_player', player: 'tester' },
        { action: 'wait', duration: 3000 }, // Wait 3 seconds

        // RECONNECT
        { action: 'connect_player', player: 'tester' },
        { action: 'wait', duration: 1000 }, // Wait for login

        // VERIFY: Cooldown persisted (should be ~37s remaining)
        {
          name: 'Check cooldown persisted',
          action: 'execute_player_command',
          player: 'tester',
          command: '/fire status',
          expect_output: /3[5-9]\s+s/i  // 35-39 seconds remaining
        }
      ]
    });

    expect(result.success).toBe(true);
  });
});
```

---

## World Profile Configuration

**File:** `world-profiles/flat-survival-test.sh`

```bash
#!/bin/bash
# Test Profile - Optimized for Pilaf integration testing
# Provides deterministic, predictable test environment

export PROFILE_NAME="flat-survival-test"
export WORLD_NAME="world-test"

# === FLAT WORLD FOR PREDICTABLE TESTING ===
export LEVEL="world-test"
export LEVEL_TYPE="FLAT"
export SEED="42"  # FIXED SEED FOR REPRODUCIBILITY
export GENERATE_STRUCTURES="false"
export MAX_WORLD_SIZE="29999984"

# === SURVIVAL MODE (MOST ABILITIES DESIGNED FOR SURVIVAL) ===
export MODE="survival"
export DIFFICULTY="normal"
export PVP="false"
export ALLOW_NETHER="false"  # DISABLE FOR FASTER SERVER STARTUP
export ALLOW_END="false"

# === REDUCED VIEW DISTANCE FOR PERFORMANCE ===
export VIEW_DISTANCE="6"
export SIMULATION_DISTANCE="6"

# === WE CONTROL ENTITY SPAWNING ===
export SPAWN_ANIMALS="false"
export SPAWN_MONSTERS="false"
export SPAWN_NPCS="false"

# === FLAT WORLD LAYERS ===
export GENERATOR_SETTINGS='{"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}'
```

**Rationale for Each Setting:**

| Setting | Value | Reason |
|---------|-------|--------|
| SEED | 42 | Fixed seed ensures same terrain every run |
| FLAT | true | Predictable terrain for entity positioning |
| STRUCTURES | false | No villages/ruins to interfere |
| ALLOW_NETHER | false | Faster server startup, not needed for tests |
| ALLOW_END | false | Faster server startup, not needed for tests |
| VIEW_DISTANCE | 6 | Reduced server load, faster tests |
| SPAWN_MONSTERS | false | We manually spawn test entities |

---

## GitHub Actions Implementation

**File:** `.github/workflows/pilaf-integration.yml`

```yaml
name: Pilaf Integration Tests

on:
  push:
    branches: [main, rt-refactoring]
    paths:
      - 'src/main/java/**'
      - 'pilaf-tests/**'
      - '.github/workflows/pilaf-integration.yml'
  pull_request:
    branches: [main]
  workflow_dispatch:

jobs:
  pilaf-tests:
    runs-on: ubuntu-latest
    timeout-minutes: 45

    services:
      minecraft:
        image: itzg/minecraft-server:java21
        ports:
          - 25565:25565
          - 25575:25575
        env:
          # Server configuration
          EULA: 'TRUE'
          TYPE: 'PAPER'
          VERSION: '1.21.8'
          RCON_PASSWORD: 'pilaf_test_password'
          ENABLE_RCON: 'true'
          RCON_PORT: 25575

          # World configuration (matches flat-survival-test.sh)
          LEVEL: 'world-test'
          LEVEL_TYPE: 'FLAT'
          SEED: '42'
          GENERATE_STRUCTURES: 'false'
          SPAWN_ANIMALS: 'false'
          SPAWN_MONSTERS: 'false'
          SPAWN_NPCS: 'false'
          VIEW_DISTANCE: '6'
          SIMULATION_DISTANCE: '6'
          ALLOW_NETHER: 'false'
          ALLOW_END: 'false'

          # Plugin configuration
          PLUGINS: /plugins/*.jar
          ONLINE_MODE: 'false'

          # Auto-op test players
          OPS: 'PilafTestPlayer,PilafTestRunner'

        options: >-
          --name pilaf-test-server
          --health-cmd "mcstatus localhost:25565 ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 30
          --health-start-period 90s
          --volume /opt/server/data:/data

        volumes:
          - ./build/libs:/plugins

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Cache Gradle packages
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
          restore-keys: ${{ runner.os }}-gradle-

      - name: Build plugin JAR
        run: |
          ./gradlew clean build -x test
          ls -la build/libs/
          echo "Plugin JAR built successfully"

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'

      - name: Install pnpm
        uses: pnpm/action-setup@v4
        with:
          version: 8

      - name: Cache pnpm dependencies
        uses: actions/cache@v4
        with:
          path: |
            ~/.pnpm-store
            pilaf-tests/node_modules
          key: ${{ runner.os }}-pnpm-${{ hashFiles('pilaf-tests/pnpm-lock.yaml') }}
          restore-keys: ${{ runner.os }}-pnpm-

      - name: Install Pilaf dependencies
        working-directory: ./pilaf-tests
        run: |
          pnpm install

      - name: Wait for Minecraft server to be ready
        run: |
          echo "Waiting for PaperMC server to start..."
          for i in {1..120}; do
            if docker logs pilaf-test-server 2>&1 | grep -q "Done"; then
              echo "Server is ready!"
              docker logs pilaf-test-server --tail 20
              break
            fi
            echo "Waiting... ($i/120)"
            sleep 1
          done

      - name: Verify server is running
        run: |
          docker ps
          mcstatus localhost:25565 ping || echo "mcstatus not available, using docker logs"

      - name: Run Pilaf integration tests
        working-directory: ./pilaf-tests
        env:
          RCON_HOST: localhost
          RCON_PORT: 25575
          RCON_PASSWORD: pilaf_test_password
        run: |
          pnpm test --ci --coverage --maxWorkers=2

      - name: Upload Pilaf HTML reports
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: pilaf-test-reports-${{ github.run_number }}
          path: pilaf-tests/target/pilaf-reports/
          retention-days: 30

      - name: Upload server logs on failure
        uses: actions/upload-artifact@v4
        if: failure()
        with:
          name: server-logs-${{ github.run_number }}
          path: /var/lib/docker/containers/pilaf-test-server*/pilaf-test-server*-json.log
          retention-days: 7

      - name: Upload coverage reports
        uses: codecov/codecov-action@v4
        if: always()
        with:
          files: ./pilaf-tests/coverage/lcov.info
          flags: pilaf-integration
          name: pilaf-coverage
```

---

## Configuration Files

### package.json

```json
{
  "name": "elemental-dragon-pilaf-tests",
  "version": "1.0.0",
  "description": "Pilaf integration tests for Elemental Dragon plugin",
  "private": true,
  "scripts": {
    "test": "jest",
    "test:watch": "jest --watch",
    "test:ci": "jest --ci --coverage --maxWorkers=2",
    "test:verbose": "jest --verbose"
  },
  "dependencies": {
    "@jest/globals": "^29.7.0",
    "@pilaf/framework": "file:../../pilaf/packages/framework",
    "@pilaf/backends": "file:../../pilaf/packages/backends"
  },
  "devDependencies": {
    "jest": "^29.7.0",
    "jest-environment-node": "^29.7.0"
  },
  "jest": {
    "preset": "./jest.config.js"
  }
}
```

### jest.config.js

```javascript
module.exports = {
  preset: '@pilaf/framework',
  testEnvironment: 'node',
  testMatch: ['**/stories/**/*.test.js'],
  testTimeout: 120000, // 2 minutes per test

  // Global setup/teardown
  globalSetup: '<rootDir>/lib/global-setup.js',
  globalTeardown: '<rootDir>/lib/global-teardown.js',

  // Reporting
  reporters: [
    'default',
    ['@pilaf/reporting', {
      outputDir: 'target/pilaf-reports',
      fileName: 'pilaf-report.html',
      includeStackTrace: true
    }]
  ],

  // Coverage
  collectCoverageFrom: [
    'stories/**/*.js',
    '!stories/**/*.test.js'
  ],

  // Verbose output in CI
  verbose: process.env.CI === 'true'
};
```

### pilaf.config.js

```javascript
module.exports = {
  // Default server configuration (can be overridden by environment variables)
  server: {
    type: 'paper',
    version: '1.21.8',
    rcon: {
      host: process.env.RCON_HOST || 'localhost',
      port: parseInt(process.env.RCON_PORT) || 25575,
      password: process.env.RCON_PASSWORD || 'dragon123'
    }
  },

  // Default player configuration
  players: {
    default: {
      auth: 'offline',
      spawnPoint: { x: 0, y: 64, z: 0 },
      spawnYaw: 0, // Facing North
      spawnPitch: 0
    }
  },

  // Test timeouts
  timeouts: {
    connection: 30000,    // 30s to establish RCON connection
    command: 10000,       // 10s for command to execute
    effect: 5000,         // 5s for effect to apply
    entitySpawn: 2000,    // 2s for entity to spawn
    damage: 3000          // 3s for damage to occur
  },

  // Retry configuration for flaky operations
  retry: {
    maxAttempts: 3,
    delay: 1000
  }
};
```

### lib/constants.js

```javascript
/**
 * Cooldown values in seconds (must match plugin defaults)
 * Source: Individual Fragment classes
 */
const COOLDOWNS = {
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
const ENTITY_POSITIONS = {
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
  OUTSIDE_RADIUS: { x: 10, y: 64, z: 0 }
};

/**
 * Entity health values
 */
const ENTITY_HEALTH = {
  ZOMBIE: 20,       // 10 hearts
  SKELETON: 20,     // 10 hearts
  CREEPER: 20,      // 10 hearts
  SPIDER: 16,       // 8 hearts
  VILLAGER: 20      // 10 hearts
};

/**
 * Effect durations (in milliseconds)
 * Source: Individual Fragment classes
 */
const EFFECT_DURATIONS = {
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

module.exports = {
  COOLDOWNS,
  ENTITY_POSITIONS,
  ENTITY_HEALTH,
  EFFECT_DURATIONS
};
```

---

## Implementation Phases

### Phase 1: Foundation (Week 1)

**Deliverables:**
1. Create `pilaf-tests/` directory structure
2. Create `package.json`, `jest.config.js`, `pilaf.config.js`
3. Create `world-profiles/flat-survival-test.sh`
4. Implement `lib/entities.js`, `lib/assertions.js`, `lib/constants.js`
5. Write `stories/00-setup/connection.test.js`
6. Create `.github/workflows/pilaf-integration.yml`
7. Verify workflow can start server and run connection test

**Acceptance Criteria:**
- GitHub Actions workflow completes successfully
- Server starts in <90 seconds
- Connection test passes
- HTML report generated

### Phase 2: Core Ability Tests (Week 2)

**Deliverables:**
1. `stories/01-lightning/lightning-strike.test.js`
2. `stories/02-burning-fragment/dragons-wrath.test.js`
3. `stories/02-burning-fragment/infernal-dominion.test.js`
4. `stories/03-agility-fragment/draconic-surge.test.js`
5. Basic cooldown test (command executes, cooldown enforced)

**Acceptance Criteria:**
- Lightning strike test passes (lightning spawns at target)
- Fireball test passes (entity takes 6.0 damage)
- Fire ring test passes (all ring entities damaged)
- Dash movement test passes (player moved ~20 blocks)
- Cooldown test passes (ability blocked during cooldown)

### Phase 3: Advanced Tests (Week 3)

**Deliverables:**
1. `stories/03-agility-fragment/wing-burst.test.js`
2. `stories/04-immortal-fragment/draconic-reflex.test.js`
3. `stories/05-corrupted-fragment/dread-gaze-freeze.test.js`
4. Passive bonus tests (fire resistance, speed, night vision, creeper invisibility, enderman anti-aggro)
5. Cooldown persistence tests (relog, unequip/equip)

**Acceptance Criteria:**
- Wing Burst push test passes (entities pushed 20 blocks away)
- Draconic Reflex dodge test passes (20% dodge chance verified statistically)
- Dread Gaze freeze test passes (target completely frozen for 4 seconds)
- Passive bonuses verified (all passives work correctly)
- Cooldowns verified across relog

### Phase 4: Edge Cases (Week 4)

**Deliverables:**
1. `stories/04-immortal-fragment/essence-rebirth.test.js`
2. `stories/05-corrupted-fragment/life-devourer-clever.test.js`
3. `stories/06-cooldown-system/persistence-restart.test.js`
4. `stories/06-cooldown-system/global-adjustment-min.test.js`
5. Stress tests (rapid ability usage)

**Acceptance Criteria:**
- Death protection test passes (totem protection verified)
- Life Devourer lifesteal test passes (50% healing from damage)
- Server restart cooldown test passes
- MIN formula test passes
- All edge cases covered

---

## Risk Assessment and Mitigation

### High Risks

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Entity positioning flaky | High | Medium | Use NoAI:1, close range, multiple attempts |
| Server startup timeout | High | Low | Increase healthcheck to 90s, add pre-warmed image option |
| CI flakiness | Medium | Medium | Retry logic, generous timeouts, good diagnostics |

### Medium Risks

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Movement test imprecision | Medium | High | Test effect presence, use tolerance (±2 blocks) |
| Dodge probability testing | Medium | Medium | Use 20+ attacks for statistical significance |
| Freeze detection timing | Low | Low | Use generous wait times for tick-based effects |
| Death/respawn testing complex | Medium | Low | Simplify test, use /kill command |
| Fire resistance test (lava) | Medium | Low | Use fire blocks instead of lava |
| Cooldown test timing issues | Medium | Medium | Use tolerance windows, wait for sync |

### Low Risks

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Command execution tests | Low | Low | Straightforward, well-tested |
| Effect presence tests | Low | Low | Use Minecraft data commands |

---

## Success Criteria

### Test Coverage

- [ ] All 4 fragments tested
- [ ] All 8 abilities tested
- [ ] All 4 passive bonuses tested
- [ ] All 6 cooldown behaviors tested
- [ ] Minimum 80% of happy path scenarios covered

### CI/CD Integration

- [ ] GitHub Actions workflow runs on every PR
- [ ] Tests complete in <45 minutes
- [ ] HTML reports uploaded as artifacts
- [ ] Failed tests block merge

### Test Quality

- [ ] Tests are deterministic (same result every run)
- [ ] Tests are independent (can run in any order)
- [ ] Tests are fast (<2 minutes each)
- [ ] Tests have clear failure messages

---

## Next Steps

1. **Review and approve this plan** - Confirm approach and priorities
2. **Set up Pilaf development environment** - Install Pilaf locally for testing
3. **Begin Phase 1 implementation** - Create foundation files
4. **Validate foundation** - Run first integration test
5. **Iterate through phases** - Implement tests per phase
6. **Monitor and refine** - Fix flaky tests, optimize performance

---

**Document Version:** 1.0
**Last Updated:** 2025-01-23
