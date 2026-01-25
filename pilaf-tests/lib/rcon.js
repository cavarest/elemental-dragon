/**
 * RCON and Bot Connection Helper
 *
 * Provides shared connection functions for all tests
 * Uses Pilaf's createTestContext from @pilaf/framework
 */

import { createRequire } from 'module';
const require = createRequire(import.meta.url);

// Re-export Pilaf's createTestContext and cleanupTestContext
const { createTestContext: pilafCreateTestContext, cleanupTestContext: pilafCleanupTestContext } = require('@pilaf/framework');

// Import RconBackend for direct RCON connections
const { RconBackend } = require('@pilaf/backends');

/**
 * Create and connect to RCON server (RCON only, no bot players)
 * @param {Object} config - RCON configuration
 * @param {string} config.host - RCON host
 * @param {number} config.port - RCON port
 * @param {string} config.password - RCON password
 * @returns {Promise<RconBackend>} Connected RCON backend
 */
export async function connectRcon(config = {}) {
  const rcon = new RconBackend();
  await rcon.connect(config);
  return rcon;
}

// Re-export Pilaf's createTestContext and cleanupTestContext
export { pilafCreateTestContext as createTestContext, pilafCleanupTestContext as cleanupTestContext };
