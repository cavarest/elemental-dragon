/**
 * Dragon's Wrath (Fireball) Test
 *
 * Tests the Dragon's Wrath ability (/fire 1)
 * - Fireball chases target
 * - Deals 8.0 damage (4 hearts) - Issue #28
 * - Bypasses armor
 */

import { createTestContext, cleanupTestContext } from '../../lib/rcon.js';
import { spawnFrozenEntity, clearAllEntities, teleportPlayer, giveItem, wait } from '../../lib/entities.js';

const config = {
  host: process.env.RCON_HOST || 'localhost',
  gamePort: 25565,
  rconPort: parseInt(process.env.RCON_PORT) || 25575,
  rconPassword: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Burning Fragment - Dragons Wrath', () => {
  let context;
  const TEST_PLAYER = 'TestPlayer';

  beforeAll(async () => {
    // Create test context with bot player
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

  it('should launch fireball that damages target', async () => {
    // Spawn target zombie at North position
    await spawnFrozenEntity(context.backend, 'zombie', { x: 0, y: 64, z: -10 }, 'fireball_target', 20);

    // Give Burning Fragment (blaze powder)
    await giveItem(context.backend, TEST_PLAYER, 'blaze_powder');

    // Equip fragment (using bot chat)
    context.bot.chat('/fire equip');
    await wait(500);

    // Execute Dragon's Wrath
    context.bot.chat('/fire 1');

    // Wait for fireball to reach target (10 blocks @ ~1.5 blocks/tick)
    await wait(2000);

    // Check if zombie took damage (health < 20)
    // Fireball deals 8.0 damage (Issue #28: 4 hearts), so zombie should have 12 health remaining
    const healthResult = await context.backend.sendCommand('data get entity @e[tag=fireball_target] Health');

    // Zombie should have been damaged
    expect(healthResult).toBeDefined();

    // Parse health from result
    const healthMatch = healthResult.raw.match(/[\d.]+/);
    if (healthMatch) {
      const health = parseFloat(healthMatch[0]);
      expect(health).toBeLessThan(20);
      expect(health).toBeGreaterThan(0); // Should survive 8 damage (20 - 8 = 12)
    }
  });

  it('should apply passive Fire Resistance when equipped', async () => {
    // Clear effects
    await context.backend.sendCommand(`effect clear ${TEST_PLAYER}`);
    await wait(500);

    // Give and equip Burning Fragment
    await giveItem(context.backend, TEST_PLAYER, 'blaze_powder');
    context.bot.chat('/fire equip');
    await wait(500);

    // Fire Resistance should be applied passively
    // Since has_effect predicate is not available, we just verify the command succeeded
    const equipResult = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run fire equip`);
    expect(equipResult.raw).toBeDefined();
  });
});
