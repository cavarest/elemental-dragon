/**
 * Assertion helpers for Minecraft-specific test conditions
 */

/**
 * Assert that player health equals expected value
 * @param {Object} rcon - RCON client
 * @param {string} player - Player name
 * @param {number} expectedHealth - Expected health value
 * @param {number} tolerance - Acceptable difference (default 0.5 hearts = 1.0 health)
 * @returns {Promise<boolean>} True if assertion passes
 */
export async function assertPlayerHealth(rcon, player, expectedHealth, tolerance = 1.0) {
  const command = `data get entity ${player} Health`;
  const result = await rcon.send(command);

  // Parse health from result (format: "player has 20.0 health")
  const healthMatch = result.match(/[\d.]+/);
  if (!healthMatch) {
    throw new Error(`Could not parse health from result: ${result}`);
  }

  const actualHealth = parseFloat(healthMatch[0]);
  const difference = Math.abs(actualHealth - expectedHealth);

  if (difference > tolerance) {
    throw new Error(
      `Health assertion failed: expected ${expectedHealth} (±${tolerance}), got ${actualHealth}`
    );
  }

  return true;
}

/**
 * Assert that entity with tag does not exist (died/was removed)
 * @param {Object} rcon - RCON client
 * @param {string} tag - Entity tag to check
 * @returns {Promise<boolean>} True if entity doesn't exist
 */
export async function assertEntityNotExists(rcon, tag) {
  const command = `execute unless entity @e[tag=${tag}] run say ENTITY_NOT_FOUND`;
  const result = await rcon.send(command);

  if (result.includes('ENTITY_NOT_FOUND')) {
    return true;
  }

  throw new Error(`Entity with tag ${tag} still exists`);
}

/**
 * Assert that entity with tag exists
 * @param {Object} rcon - RCON client
 * @param {string} tag - Entity tag to check
 * @returns {Promise<boolean>} True if entity exists
 */
export async function assertEntityExists(rcon, tag) {
  const command = `execute if entity @e[tag=${tag}] run say ENTITY_FOUND`;
  const result = await rcon.send(command);

  if (result.includes('ENTITY_FOUND')) {
    return true;
  }

  throw new Error(`Entity with tag ${tag} does not exist`);
}

/**
 * Assert that entity has specific potion effect
 * @param {Object} rcon - RCON client
 * @param {string} entitySelector - Entity selector
 * @param {string} effect - Effect name (e.g., 'speed', 'resistance')
 * @param {number} amplifier - Expected amplifier (default 0 = level I)
 * @returns {Promise<boolean>} True if entity has the effect
 */
export async function assertHasEffect(rcon, entitySelector, effect, amplifier = null) {
  let command;

  if (amplifier !== null) {
    command = `execute as ${entitySelector} if entity @s[has_effect={effect:${effect},amplifier:${amplifier}}] run say EFFECT_FOUND`;
  } else {
    command = `execute as ${entitySelector} if entity @s[has_effect={effect:${effect}}] run say EFFECT_FOUND`;
  }

  const result = await rcon.send(command);

  if (result.includes('EFFECT_FOUND')) {
    return true;
  }

  throw new Error(`Entity ${entitySelector} does not have effect ${effect}${amplifier !== null ? ` (amplifier ${amplifier})` : ''}`);
}

/**
 * Assert that entity does NOT have specific potion effect
 * @param {Object} rcon - RCON client
 * @param {string} entitySelector - Entity selector
 * @param {string} effect - Effect name
 * @returns {Promise<boolean>} True if entity doesn't have the effect
 */
export async function assertNotHasEffect(rcon, entitySelector, effect) {
  const command = `execute as ${entitySelector} unless entity @s[has_effect={effect:${effect}}] run say EFFECT_NOT_FOUND`;
  const result = await rcon.send(command);

  if (result.includes('EFFECT_NOT_FOUND')) {
    return true;
  }

  throw new Error(`Entity ${entitySelector} has effect ${effect} when it shouldn't`);
}

/**
 * Assert greater than condition
 * @param {number} actual - Actual value
 * @param {number} expected - Expected minimum value
 * @param {string} message - Optional error message
 */
export function assertGreaterThan(actual, expected, message = '') {
  if (actual <= expected) {
    throw new Error(message || `Expected ${actual} > ${expected}`);
  }
}

/**
 * Assert less than condition
 * @param {number} actual - Actual value
 * @param {number} expected - Expected maximum value
 * @param {string} message - Optional error message
 */
export function assertLessThan(actual, expected, message = '') {
  if (actual >= expected) {
    throw new Error(message || `Expected ${actual} < ${expected}`);
  }
}

/**
 * Assert equals condition with tolerance
 * @param {number} actual - Actual value
 * @param {number} expected - Expected value
 * @param {number} tolerance - Acceptable difference
 * @param {string} message - Optional error message
 */
export function assertEqualsWithTolerance(actual, expected, tolerance = 0, message = '') {
  const difference = Math.abs(actual - expected);
  if (difference > tolerance) {
    throw new Error(message || `Expected ${actual} ≈ ${expected} (±${tolerance}), difference was ${difference}`);
  }
}

/**
 * Parse position from data command output
 * @param {string} output - Output from "data get entity @p Pos"
 * @returns {Object} Position {x, y, z}
 */
export function parsePosition(output) {
  // Format: "player has the following entity data: [{x: 0.0d, y: 64.0d, z: -10.0d}]"
  const match = output.match(/\{x:\s*(-?\d+\.?\d*)d,\s*y:\s*(-?\d+\.?\d*)d,\s*z:\s*(-?\d+\.?\d*)d\}/);

  if (!match) {
    throw new Error(`Could not parse position from output: ${output}`);
  }

  return {
    x: parseFloat(match[1]),
    y: parseFloat(match[2]),
    z: parseFloat(match[3])
  };
}

/**
 * Calculate distance between two positions
 * @param {Object} pos1 - First position {x, y, z}
 * @param {Object} pos2 - Second position {x, y, z}
 * @returns {number} Distance in blocks
 */
export function calculateDistance(pos1, pos2) {
  const dx = pos2.x - pos1.x;
  const dy = pos2.y - pos1.y;
  const dz = pos2.z - pos1.z;
  return Math.sqrt(dx * dx + dy * dy + dz * dz);
}
