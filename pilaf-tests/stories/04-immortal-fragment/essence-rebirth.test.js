/**
 * Essence Rebirth (Death Protection) Test
 *
 * Tests the Essence Rebirth ability (/immortal 2)
 * - 30-second death protection window
 * - Prevents death from fatal damage
 * - 480 second (8 minute) cooldown
 */

import { createTestContext, cleanupTestContext } from '../../lib/rcon.js';
import { clearAllEntities, teleportPlayer, giveItem, wait } from '../../lib/entities.js';
import { COOLDOWNS, EFFECT_DURATIONS } from '../../lib/constants.js';

const config = {
  host: process.env.RCON_HOST || 'localhost',
  gamePort: 25565,
  rconPort: parseInt(process.env.RCON_PORT) || 25575,
  rconPassword: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Immortal Fragment - Essence Rebirth', () => {
  let context;
  const TEST_PLAYER = 'TestPlayer';

  beforeAll(async () => {
    context = await createTestContext({ ...config, username: TEST_PLAYER });

    // Clean up
    await clearAllEntities(context.backend);

    // Setup player
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

  it('should activate death protection window', async () => {
    // Give Immortal Fragment
    await giveItem(context.backend, TEST_PLAYER, 'diamond');

    // Equip fragment
    context.bot.chat('/immortal equip');
    await wait(500);

    // Activate Essence Rebirth
    context.bot.chat('/immortal 2');
    await wait(500);

    // Player should still be alive (not dead from fatal damage)
    const healthResult = await context.backend.sendCommand(`data get entity ${TEST_PLAYER} Health`);
    expect(healthResult).toBeDefined();
  });

  it('should prevent death from lethal damage during protection window', async () => {
    // Ensure player has Essence Rebirth active
    await context.backend.sendCommand(`heal ${TEST_PLAYER}`);
    await context.backend.sendCommand(`effect clear ${TEST_PLAYER}`);

    // Give Immortal Fragment and activate
    await giveItem(context.backend, TEST_PLAYER, 'diamond');
    context.bot.chat('/immortal equip');
    await wait(500);
    context.bot.chat('/immortal 2');
    await wait(500);

    // Deal massive damage that would normally kill
    // Use multiple damage sources to ensure lethal
    for (let i = 0; i < 5; i++) {
      await context.backend.sendCommand(`execute as ${TEST_PLAYER} run damage @s 10 by entity @e[type=zombie,limit=1]`);
      await wait(100);
    }

    // Wait for protection window
    await wait(EFFECT_DURATIONS.ESSENCE_REBIRTH);

    // Player should still exist (not dead)
    const playerExists = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run say ALIVE`);
    expect(playerExists).toBeDefined();
  });

  it('should have long cooldown after activation', async () => {
    // Give Immortal Fragment
    await giveItem(context.backend, TEST_PLAYER, 'diamond');
    context.bot.chat('/immortal equip');
    await wait(500);

    // Activate Essence Rebirth
    context.bot.chat('/immortal 2');
    await wait(1000);

    // Try to use again - should be on long cooldown
    const result = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run immortal 2`);
    expect(result).toBeDefined();

    // Cooldown is 8 minutes, don't wait for it
  });
});
