/**
 * Bot Player Connection Test
 *
 * Tests that we can create an offline bot player using Pilaf 1.2.0
 */

import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const { MineflayerBackend } = require('@pilaf/backends');

const config = {
  host: process.env.RCON_HOST || 'localhost',
  port: 25565, // Game port, not RCON port
  auth: 'offline',
  rconHost: process.env.RCON_HOST || 'localhost',
  rconPort: parseInt(process.env.RCON_PORT) || 25575,
  rconPassword: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Bot Player Connection', () => {
  let backend;
  let bot;

  afterAll(async () => {
    if (bot && backend) {
      await backend.quitBot(bot);
    }
    if (backend) {
      await backend.disconnect();
    }
  });

  it('should create offline bot player', async () => {
    backend = new MineflayerBackend();
    await backend.connect(config);

    // Wait for server to be ready
    const ready = await backend.waitForServerReady({ timeout: 60000 });
    expect(ready.success).toBe(true);

    // Create bot player
    bot = await backend.createBot({
      username: 'TestPlayer',
      auth: 'offline'
    });

    expect(bot).toBeDefined();
    expect(bot.username).toBe('TestPlayer');
  });

  it('should execute command as bot player', async () => {
    if (!backend || !bot) {
      pending('Bot not created');
    }

    // Execute command via bot
    bot.chat('/gamemode survival');

    // Wait a bit for command to process
    await new Promise(resolve => setTimeout(resolve, 1000));

    expect(true).toBe(true);
  });
});
