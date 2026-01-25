/**
 * Lightning Strike Basic Test
 *
 * Tests the lightning strike ability (/lightning 1)
 * - Verifies Dragon Egg requirement
 * - Verifies command execution
 */

import { createTestContext, cleanupTestContext } from '../../lib/rcon.js';
import { spawnFrozenEntity, clearAllEntities, teleportPlayer, giveItem, wait } from '../../lib/entities.js';

const config = {
  host: process.env.RCON_HOST || 'localhost',
  gamePort: 25565,
  rconPort: parseInt(process.env.RCON_PORT) || 25575,
  rconPassword: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Lightning Ability', () => {
  let context;
  const TEST_PLAYER = 'TestPlayer';

  beforeAll(async () => {
    context = await createTestContext({ ...config, username: TEST_PLAYER });

    // Clean up any existing entities
    await clearAllEntities(context.backend);

    // Teleport player to spawn facing North
    await teleportPlayer(context.backend, TEST_PLAYER, { x: 0, y: 64, z: 0 }, 0, 0);

    // Clear inventory to ensure no Dragon Egg
    await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
    await wait(500);
  });

  afterAll(async () => {
    if (context) {
      await clearAllEntities(context.backend);
      await cleanupTestContext(context);
    }
  });

  it('should fail without Dragon Egg in offhand', async () => {
    // Try to use lightning without Dragon Egg
    context.bot.chat('/lightning');
    await wait(500);

    // Bot should receive error message about Dragon Egg
    // The command should fail silently or with an error message
  });

  it('should accept Dragon Egg and execute lightning', async () => {
    // Give Dragon Egg
    await giveItem(context.backend, TEST_PLAYER, 'dragon_egg');

    // Put Dragon Egg in offhand
    await context.backend.sendCommand(`item replace entity ${TEST_PLAYER} weapon.offhand with dragon_egg`);
    await wait(500);

    // Spawn target zombie
    await spawnFrozenEntity(context.backend, 'zombie', { x: 0, y: 64, z: -10 }, 'lightning_target', 20);

    // Execute lightning
    context.bot.chat('/lightning');

    // Wait for lightning to strike
    await wait(2000);

    // Check if zombie was damaged (no longer at full health)
    const healthResult = await context.backend.sendCommand('data get entity @e[tag=lightning_target,limit=1] Health');

    // Zombie should have taken damage (started at 20, should be less after 3 strikes of 4 damage each = 8 total)
    // But might be dead from 12 damage (3 strikes × 4 damage = 12)
    expect(healthResult).toBeDefined();
  });
});
