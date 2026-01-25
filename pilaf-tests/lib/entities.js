import { FROZEN_ENTITY_NBT } from './constants.js';

/**
 * Helper to send command via either RconBackend or MineflayerBackend
 * @param {Object} backend - Backend instance
 * @param {string} command - Command to send
 * @returns {Promise<Object>} Result object
 */
async function sendCommand(backend, command) {
  // RconBackend has .send(), MineflayerBackend has .sendCommand()
  if (typeof backend.send === 'function') {
    return await backend.send(command);
  } else if (typeof backend.sendCommand === 'function') {
    return await backend.sendCommand(command);
  } else {
    throw new Error('Backend must have send() or sendCommand() method');
  }
}

/**
 * Spawn a frozen entity at a specific position
 * @param {Object} backend - Backend instance (RconBackend or MineflayerBackend)
 * @param {string} type - Entity type (e.g., 'zombie', 'skeleton')
 * @param {Object} position - Position {x, y, z}
 * @param {string} tag - Entity tag for targeting
 * @param {number} health - Optional health value
 * @returns {Promise<Object>} Entity info
 */
export async function spawnFrozenEntity(backend, type, position, tag, health = 20) {
  const nbt = `${FROZEN_ENTITY_NBT.DEFAULT},Tags:["${tag}"],Health:${health}f`;
  const command = `summon ${type} ${position.x} ${position.y} ${position.z} {${nbt}}`;

  await sendCommand(backend, command);
  await wait(100); // Wait for entity to spawn

  return { type, position, tag, health };
}

/**
 * Clear all entities except players
 * @param {Object} backend - Backend instance
 */
export async function clearAllEntities(backend) {
  await sendCommand(backend, 'kill @e[type=!player]');
  await wait(200); // Wait for entities to be cleared
}

/**
 * Spawn multiple frozen entities in a ring pattern
 * @param {Object} backend - Backend instance
 * @param {number} radius - Ring radius in blocks
 * @param {string} entityType - Type of entity to spawn
 * @param {string} tagPrefix - Prefix for entity tags
 * @param {number} health - Health value for entities
 */
export async function spawnFrozenEntitiesInRing(backend, radius, entityType, tagPrefix, health = 20) {
  const positions = [
    { x: radius, y: 64, z: 0, tag: `${tagPrefix}_east` },
    { x: -radius, y: 64, z: 0, tag: `${tagPrefix}_west` },
    { x: 0, y: 64, z: radius, tag: `${tagPrefix}_south` },
    { x: 0, y: 64, z: -radius, tag: `${tagPrefix}_north` }
  ];

  for (const pos of positions) {
    await spawnFrozenEntity(backend, entityType, pos, pos.tag, health);
  }
}

/**
 * Get entity location via data command
 * @param {Object} backend - Backend instance
 * @param {string} entitySelector - Entity selector (e.g., '@e[tag=test]')
 * @returns {Promise<Object>} Position {x, y, z}
 */
export async function getEntityLocation(backend, entitySelector) {
  // For now, return a command that can be executed
  // The actual parsing would need to be done by the test framework
  return {
    getCommand: `data get entity ${entitySelector} Pos`
  };
}

/**
 * Teleport player to specific position and facing
 * @param {Object} backend - Backend instance
 * @param {string} player - Player name
 * @param {Object} position - Position {x, y, z}
 * @param {number} yaw - Yaw angle (0 = North)
 * @param {number} pitch - Pitch angle (0 = level)
 */
export async function teleportPlayer(backend, player, position, yaw = 0, pitch = 0) {
  const command = `tp ${player} ${position.x} ${position.y} ${position.z} ${yaw} ${pitch}`;
  await sendCommand(backend, command);
  await wait(100);
}

/**
 * Wait helper function
 * @param {number} ms - Milliseconds to wait
 * @returns {Promise<void>}
 */
export function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Give item to player
 * @param {Object} backend - Backend instance
 * @param {string} player - Player name
 * @param {string} item - Item ID
 * @param {number} count - Item count (default 1)
 */
export async function giveItem(backend, player, item, count = 1) {
  const command = `give ${player} ${item} ${count}`;
  await sendCommand(backend, command);
  await wait(100);
}

/**
 * Execute command as player
 * @param {Object} backend - Backend instance
 * @param {string} player - Player name
 * @param {string} command - Command to execute (without leading /)
 */
export async function executePlayerCommand(backend, player, command) {
  const fullCommand = command.startsWith('/') ? command.substring(1) : command;
  const executeCommand = `execute as ${player} run ${fullCommand}`;
  await sendCommand(backend, executeCommand);
  await wait(100);
}

/**
 * Check if entity exists
 * @param {Object} backend - Backend instance
 * @param {string} entitySelector - Entity selector
 * @returns {Promise<string>} Command that will output entity count
 */
export function checkEntityExists(backend, entitySelector) {
  return `execute unless entity ${entitySelector} run say ENTITY_NOT_FOUND`;
}

/**
 * Get entity health
 * @param {Object} backend - Backend instance
 * @param {string} entitySelector - Entity selector
 * @returns {Promise<string>} Command to get health
 */
export function getEntityHealth(backend, entitySelector) {
  return `data get entity ${entitySelector} Health`;
}

/**
 * Parse position from data command output
 * @param {string|Object} output - Output from data get entity Pos command or result object
 * @returns {Object} Position {x, y, z}
 */
export function parsePosition(output) {
  // Handle RconBackend result objects
  const text = typeof output === 'object' && output.raw ? output.raw : output;

  // Format 1: "x: 0.0d, y: 64.0d, z: 0.0d" or similar
  let match = text.match(/x:\s*(-?\d+\.?\d*)d,\s*y:\s*(-?\d+\.?\d*)d,\s*z:\s*(-?\d+\.?\d*)d/i);
  if (match) {
    return {
      x: parseFloat(match[1]),
      y: parseFloat(match[2]),
      z: parseFloat(match[3])
    };
  }

  // Format 2: {x: 0.0d, y: 64.0d, z: 0.0d}
  match = text.match(/\{x:\s*(-?\d+\.?\d*)d,\s*y:\s*(-?\d+\.?\d*)d,\s*z:\s*(-?\d+\.?\d*)d\}/);
  if (match) {
    return {
      x: parseFloat(match[1]),
      y: parseFloat(match[2]),
      z: parseFloat(match[3])
    };
  }

  // Format 3: [-8.5d, 72.0d, 1.5d] (Minecraft 1.21+ format)
  match = text.match(/\[\s*(-?\d+\.?\d*)d,\s*(-?\d+\.?\d*)d,\s*(-?\d+\.?\d*)d\s*\]/);
  if (match) {
    return {
      x: parseFloat(match[1]),
      y: parseFloat(match[2]),
      z: parseFloat(match[3])
    };
  }

  // Format 4: "TestPlayer has the following entity data: [-8.5d, 72.0d, 1.5d]"
  match = text.match(/\[\s*(-?\d+\.?\d*)d,\s*(-?\d+\.?\d*)d,\s*(-?\d+\.?\d*)d\s*\]/);
  if (match) {
    return {
      x: parseFloat(match[1]),
      y: parseFloat(match[2]),
      z: parseFloat(match[3])
    };
  }

  throw new Error(`Could not parse position from output: ${text}`);
}
