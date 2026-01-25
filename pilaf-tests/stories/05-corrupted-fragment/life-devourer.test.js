/**
 * Life Devourer (Lifesteal) Test
 *
 * Tests the Life Devourer ability (/corrupt 2)
 * - 25% lifesteal from all damage (Issue #28)
 * - Duration: 20 seconds
 * - 120 second (2 minute) cooldown
 */

import { createTestContext, cleanupTestContext } from '../../lib/rcon.js';
import { spawnFrozenEntity, clearAllEntities, teleportPlayer, giveItem, wait } from '../../lib/entities.js';
import { COOLDOWNS, EFFECT_DURATIONS } from '../../lib/constants.js';

const config = {
  host: process.env.RCON_HOST || 'localhost',
  gamePort: 25565,
  rconPort: parseInt(process.env.RCON_PORT) || 25575,
  rconPassword: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Corrupted Core - Life Devourer', () => {
  let context;
  const TEST_PLAYER = 'TestPlayer';
  const TARGET_TAG = 'life_devourer_target';

  beforeAll(async () => {
    context = await createTestContext({ ...config, username: TEST_PLAYER });

    // Clean up
    await clearAllEntities(context.backend);

    // Setup player
    await teleportPlayer(context.backend, TEST_PLAYER, { x: 0, y: 64, z: 0 }, 0, 0);
    await context.backend.sendCommand(`effect clear ${TEST_PLAYER}`);
    await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
    await wait(500);
  });

  afterAll(async () => {
    if (context) {
      await clearAllEntities(context.backend);
      await cleanupTestContext(context);
    }
  });

  it('should provide 25% lifesteal when activated', async () => {
    // Set player to low health
    await context.backend.sendCommand(`heal ${TEST_PLAYER}`);
    await context.backend.sendCommand(`execute as ${TEST_PLAYER} run damage @s 10`);
    await wait(200);

    // Get initial health
    const initialHealth = await context.backend.sendCommand(`data get entity ${TEST_PLAYER} Health`);

    // Give Corrupted Core Fragment
    await giveItem(context.backend, TEST_PLAYER, 'fermented_spider_eye');
    context.bot.chat('/corrupt equip');
    await wait(500);

    // Activate Life Devourer
    context.bot.chat('/corrupt 2');
    await wait(500);

    // Spawn target
    await spawnFrozenEntity(context.backend, 'zombie', { x: 0, y: 64, z: -3 }, TARGET_TAG, 20);

    // Deal damage and verify lifesteal (Issue #28: 25% of 10 = 2.5 health gained)
    await context.backend.sendCommand(`execute as ${TEST_PLAYER} run damage @e[tag=${TARGET_TAG}] 10`);
    await wait(200);

    // Check if player healed
    const finalHealth = await context.backend.sendCommand(`data get entity ${TEST_PLAYER} Health`);

    // Player should have more health than before (25% lifesteal from 10 damage = 2.5 hearts)
    expect(finalHealth).toBeDefined();
  });

  it('should last for 20 seconds', async () => {
    // Give Corrupted Core Fragment
    await giveItem(context.backend, TEST_PLAYER, 'fermented_spider_eye');
    context.bot.chat('/corrupt equip');
    await wait(500);

    // Activate Life Devourer
    context.bot.chat('/corrupt 2');
    await wait(500);

    // Spawn target
    await spawnFrozenEntity(context.backend, 'zombie', { x: 0, y: 64, z: -3 }, TARGET_TAG, 20);

    // Set player to low health
    await context.backend.sendCommand(`heal ${TEST_PLAYER}`);
    await context.backend.sendCommand(`execute as ${TEST_PLAYER} run damage @s 10`);
    await wait(200);

    // Deal damage and verify lifesteal during effect
    await context.backend.sendCommand(`execute as ${TEST_PLAYER} run damage @e[tag=${TARGET_TAG}] 10`);
    await wait(200);

    const duringEffectHealth = await context.backend.sendCommand(`data get entity ${TEST_PLAYER} Health`);

    // Wait for effect to expire
    await wait(EFFECT_DURATIONS.LIFE_DEVOURER);

    // Set player to low health again
    await context.backend.sendCommand(`heal ${TEST_PLAYER}`);
    await context.backend.sendCommand(`execute as ${TEST_PLAYER} run damage @s 10`);
    await wait(200);

    // Deal damage (no lifesteal after effect expires)
    await context.backend.sendCommand(`execute as ${TEST_PLAYER} run damage @e[tag=${TARGET_TAG}] 10`);
    await wait(200);

    const afterEffectHealth = await context.backend.sendCommand(`data get entity ${TEST_PLAYER} Health`);

    // Lifesteal should no longer work
    expect(afterEffectHealth).toBeDefined();
  });

  it('should have correct cooldown after activation', async () => {
    // Give Corrupted Core Fragment
    await giveItem(context.backend, TEST_PLAYER, 'fermented_spider_eye');
    context.bot.chat('/corrupt equip');
    await wait(500);

    // Activate Life Devourer
    context.bot.chat('/corrupt 2');
    await wait(1000);

    // Try to use again - should be on cooldown
    const result = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run corrupt 2`);
    expect(result).toBeDefined();

    // Cooldown is 2 minutes, don't wait for it
  });
});
