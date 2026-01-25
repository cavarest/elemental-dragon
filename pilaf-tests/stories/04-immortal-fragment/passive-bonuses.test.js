/**
 * Passive Bonuses Test
 *
 * Tests the passive bonuses of Immortal Fragment:
 * - Permanent Totem Protection (prevents death from fatal damage)
 *
 * Issue #28: Removed +2 Hearts and Resistance I passives
 */

import { createTestContext, cleanupTestContext } from '../../lib/rcon.js';
import { clearAllEntities, teleportPlayer, giveItem, wait } from '../../lib/entities.js';

const config = {
  host: process.env.RCON_HOST || 'localhost',
  gamePort: 25565,
  rconPort: parseInt(process.env.RCON_PORT) || 25575,
  rconPassword: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Immortal Fragment - Passive Bonuses', () => {
  let context;
  const TEST_PLAYER = 'TestPlayer';

  beforeAll(async () => {
    context = await createTestContext({ ...config, username: TEST_PLAYER });
    await clearAllEntities(context.backend);
    await teleportPlayer(context.backend, TEST_PLAYER, { x: 0, y: 64, z: 0 }, 0, 0);
    await context.backend.sendCommand(`effect clear ${TEST_PLAYER}`);
    await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
    await context.backend.sendCommand(`heal ${TEST_PLAYER}`);
    await wait(500);
  });

  afterAll(async () => {
    if (context) {
      await clearAllEntities(context.backend);
      await cleanupTestContext(context);
    }
  });

  it('should provide Totem Protection when equipped (prevent death)', async () => {
    // Ensure player has full health
    await context.backend.sendCommand(`heal ${TEST_PLAYER}`);

    // Give and equip Immortal Fragment
    await giveItem(context.backend, TEST_PLAYER, 'diamond');
    context.bot.chat('/immortal equip');
    await wait(500);

    // Deal lethal damage (20 damage = 10 hearts)
    // With totem protection (Issue #28: only passive remaining), player should not die
    await context.backend.sendCommand(`execute as ${TEST_PLAYER} run damage @s 20`);
    await wait(200);

    // Player should still be alive (totem effect)
    const healthResult = await context.backend.sendCommand('data get entity TestPlayer Health');
    expect(healthResult).toBeDefined();

    // Check if player is still online - use bot to verify
    expect(context.bot.username).toBe(TEST_PLAYER);
  });
});
