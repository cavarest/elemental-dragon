#!/bin/bash
# Flat Creative None Profile - Deterministic flat world for testing
# Minimal world for quick plugin testing without survival distractions.

# Profile identification
export PROFILE_NAME="flat-creative-none"
export WORLD_NAME="world-flat-creative-none"

# === DETERMINISTIC FLAT WORLD FOR TESTING ===
export LEVEL="pilaf-test"
export LEVEL_TYPE="FLAT"
export SEED="1234567890"
export GENERATE_STRUCTURES="false"
export MAX_WORLD_SIZE="50"

# === CREATIVE MODE FOR TESTING ===
export MODE="creative"
export DIFFICULTY="peaceful"
export PVP="false"
export ALLOW_NETHER="false"

# === PERFORMANCE FOR TESTING ===
export VIEW_DISTANCE="4"
export SIMULATION_DISTANCE="4"

# === DISABLE ENTITY SPAWNING ===
export SPAWN_ANIMALS="false"
export SPAWN_MONSTERS="false"
export SPAWN_NPCS="false"

# === CUSTOM FLAT WORLD LAYERS ===
# 1 layer bedrock, 2 layers dirt, 1 layer grass_block
export GENERATOR_SETTINGS='{"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}'

# Disable End dimension for flat world testing
export ALLOW_END="false"
