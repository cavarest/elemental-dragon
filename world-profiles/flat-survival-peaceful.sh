#!/bin/bash
# Flat Survival Peaceful Profile - Flat survival world without hostile mobs
# Survival gameplay on flat terrain without hostile mob threats.

# Profile identification
export PROFILE_NAME="flat-survival-peaceful"
export WORLD_NAME="world-flat-survival-peaceful"

# === FLAT WORLD TERRAIN ===
export LEVEL="pilaf-peaceful"
export LEVEL_TYPE="FLAT"
export SEED=""
export GENERATE_STRUCTURES="true"
export MAX_WORLD_SIZE="29999984"

# === SURVIVAL MODE WITH PEACEFUL DIFFICULTY ===
export MODE="survival"
export DIFFICULTY="peaceful"
export PVP="true"
export ALLOW_NETHER="true"
export ALLOW_END="true"

# === NORMAL VIEW DISTANCE ===
export VIEW_DISTANCE="10"
export SIMULATION_DISTANCE="10"

# === PASSIVE MOBS ONLY ===
export SPAWN_ANIMALS="true"
export SPAWN_MONSTERS="false"
export SPAWN_NPCS="true"

# === CUSTOM FLAT WORLD LAYERS ===
# 1 layer bedrock, 2 layers dirt, 1 layer grass_block
export GENERATOR_SETTINGS='{"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}'
