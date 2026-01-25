/**
 * Server Connection Test
 *
 * Verifies that we can connect to the PaperMC server via RCON
 * and execute basic commands.
 */

import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const { RconBackend } = require('/Users/mulgogi/src/cavarest/pilaf/packages/backends/lib/rcon-backend.js');

const config = {
  host: process.env.RCON_HOST || 'localhost',
  port: parseInt(process.env.RCON_PORT) || 25575,
  password: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Server Connection', () => {
  let rcon;

  beforeAll(async () => {
    // Connect to RCON server
    rcon = new RconBackend();
    await rcon.connect(config);
  });

  afterAll(async () => {
    if (rcon) {
      await rcon.disconnect();
    }
  });

  it('should connect to server', async () => {
    expect(rcon).toBeDefined();
    expect(rcon.connected).toBe(true);
  });

  it('should execute basic command', async () => {
    const result = await rcon.send('seed');
    expect(result).toBeDefined();
    expect(result.raw).toContain('Seed');
  });

  it('should get list of online players', async () => {
    const result = await rcon.send('list');
    expect(result).toBeDefined();
    // Should return something like "There are 0 out of 1 max players online"
    expect(result.raw).toContain('players');
  });
});
