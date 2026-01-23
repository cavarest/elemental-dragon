# World Profiles

This directory contains pre-configured world profiles for the Elemental Dragon development server.

## What are Profiles?

Profiles are shell scripts that set environment variables to configure the Minecraft server's world generation and gameplay settings. Each profile has its own isolated world directory, so you can switch between different configurations without losing progress.

## Available Profiles

### `normal-survival-normal.sh`
**Purpose:** Standard survival gameplay with normal terrain

**Features:**
- Normal Minecraft terrain generation
- Survival mode with normal difficulty
- All mob spawning enabled (animals, monsters, NPCs)
- Nether and End dimensions enabled
- Full view distance (10 chunks)

**Use for:** Real gameplay testing, survival mechanics, mob interactions

### `flat-creative-none.sh`
**Purpose:** Deterministic flat world for quick plugin testing

**Features:**
- Flat world with custom layers (bedrock, dirt, grass)
- Creative mode with peaceful difficulty
- No mob spawning (animals, monsters, NPCs)
- Nether and End disabled
- Small world border (50 blocks)
- Minimal view distance (4 chunks) for performance
- Fixed seed (1234567890) for reproducibility

**Use for:** Quick ability testing, plugin development, debugging mechanics

### `flat-survival-peaceful.sh`
**Purpose:** Flat survival world without hostile mobs

**Features:**
- Flat world with custom layers (bedrock, dirt, grass)
- Survival mode with peaceful difficulty
- Passive mob spawning enabled (animals, NPCs)
- Hostile mob spawning disabled
- Nether and End dimensions enabled
- Normal view distance (10 chunks)

**Use for:** Survival gameplay testing on flat terrain without hostile mob threats

### `flat-survival-normal.sh`
**Purpose:** Flat survival world with hostile mobs

**Features:**
- Flat world with custom layers (bedrock, dirt, grass)
- Survival mode with normal difficulty
- All mob spawning enabled (animals, monsters, NPCs)
- Nether and End dimensions enabled
- Normal view distance (10 chunks)

**Use for:** Survival gameplay testing on flat terrain with normal difficulty

## Creating a New Profile

To create a new profile, create a `.sh` file following the naming convention: `{terrain}-{mode}-{mob-spawning}`

**Example: Creating a large-biomes survival profile**

```bash
#!/bin/bash
# Profile identification
export PROFILE_NAME="large-survival-normal"
export WORLD_NAME="world-large-survival-normal"

# === LARGE BIOMES TERRAIN ===
export LEVEL="world-large"
export LEVEL_TYPE="large_biomes"
export SEED=""
export GENERATE_STRUCTURES="true"
export MAX_WORLD_SIZE="29999984"

# === SURVIVAL MODE WITH NORMAL DIFFICULTY ===
export MODE="survival"
export DIFFICULTY="normal"
export PVP="true"
export ALLOW_NETHER="true"
export ALLOW_END="true"

# === NORMAL VIEW DISTANCE ===
export VIEW_DISTANCE="10"
export SIMULATION_DISTANCE="10"

# === ALL MOB SPAWNING ENABLED ===
export SPAWN_ANIMALS="true"
export SPAWN_MONSTERS="true"
export SPAWN_NPCS="true"

# === DEFAULT TERRAIN GENERATION ===
export GENERATOR_SETTINGS=""
```

**Naming Convention:**
- **Terrain:** `normal`, `flat`, `large`, etc.
- **Mode:** `survival`, `creative`, `adventure`, `spectator`
- **Mob spawning:** `normal` (all mobs), `peaceful` (passive only), `none` (no mobs)

## Available Environment Variables

### World Generation
- `LEVEL` - World name (default: "world")
- `LEVEL_TYPE` - World type: `default`, `flat`, `large_biomes`, etc.
- `SEED` - World seed (empty = random)
- `GENERATE_STRUCTURES` - Generate structures: `true` or `false`
- `MAX_WORLD_SIZE` - World border size (blocks)
- `GENERATOR_SETTINGS` - Custom flat world layers JSON

### Gameplay
- `MODE` - Game mode: `survival`, `creative`, `adventure`, `spectator`
- `DIFFICULTY` - Difficulty: `peaceful`, `easy`, `normal`, `hard`
- `PVP` - Enable PvP: `true` or `false`
- `ALLOW_NETHER` - Allow Nether dimension: `true` or `false`
- `ALLOW_END` - Allow End dimension: `true` or `false`

### Performance
- `VIEW_DISTANCE` - Render distance (chunks)
- `SIMULATION_DISTANCE` - Simulation distance (chunks)

### Mob Spawning
- `SPAWN_ANIMALS` - Spawn passive mobs: `true` or `false`
- `SPAWN_MONSTERS` - Spawn hostile mobs: `true` or `false`
- `SPAWN_NPCS` - Spawn villagers: `true` or `false`

## Using Profiles

Start server with a specific profile:

```bash
# Default profile (normal survival)
./start-server.sh
./start-server.sh -p normal-survival-normal

# Flat creative profile (quick testing)
./start-server.sh -p flat-creative-none

# Flat survival peaceful profile
./start-server.sh -p flat-survival-peaceful

# Flat survival normal profile
./start-server.sh -p flat-survival-normal

# With other options
./start-server.sh -p flat-creative-none -b    # flat creative, blocking mode
./start-server.sh -p flat-survival-normal -w    # flat survival, wipe world first
./start-server.sh -p flat-creative-none -r    # flat creative, rebuild image
```

## World Data Isolation

Each profile uses its own world directory, preventing conflicts:

```
server-data/
├── world/                           # normal-survival-normal profile
├── world-flat-creative-none/        # flat-creative-none profile
├── world-flat-survival-peaceful/    # flat-survival-peaceful profile
├── world-flat-survival-normal/      # flat-survival-normal profile
├── plugins/                         # shared across all profiles
├── ops.json                         # shared
└── ...
```

This means:
- **No world conflicts** - Each profile has its own world
- **Shared plugins** - All profiles use the same plugin JAR
- **Shared configs** - Server settings (except world-specific) are shared
- **Easy switching** - Change profiles without losing progress
