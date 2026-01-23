#!/bin/bash
# Flat Survival Normal Profile - Flat survival world with hostile mobs
# Survival gameplay on flat terrain with normal difficulty.

# Profile identification
export PROFILE_NAME="flat-survival-normal"
export WORLD_NAME="world-flat-survival-normal"

# === FLAT WORLD TERRAIN ===
export LEVEL="pilaf-normal"
export LEVEL_TYPE="FLAT"
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

# === CUSTOM FLAT WORLD LAYERS ===
# 1 layer bedrock, 2 layers dirt, 1 layer grass_block
export GENERATOR_SETTINGS='{"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}'
