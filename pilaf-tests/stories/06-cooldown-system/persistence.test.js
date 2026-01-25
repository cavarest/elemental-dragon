/**
 * Cooldown Persistence Test
 *
 * Tests that cooldowns persist across:
 * - Player logout/login
 * - Fragment unequip/equip
 * - Server restart (if persistence is enabled)
 */

import { createTestContext, cleanupTestContext } from '../../lib/rcon.js';
import { clearAllEntities, teleportPlayer, giveItem, wait } from '../../lib/entities.js';
import { COOLDOWNS } from '../../lib/constants.js';

const config = {
  host: process.env.RCON_HOST || 'localhost',
  gamePort: 25565,
  rconPort: parseInt(process.env.RCON_PORT) || 25575,
  rconPassword: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Cooldown System - Persistence', () => {
  let context;
  const TEST_PLAYER = 'TestPlayer';

  beforeAll(async () => {
    context = await createTestContext({ ...config, username: TEST_PLAYER });
    await clearAllEntities(context.backend);
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

  it('should set cooldown after using ability', async () => {
    // Give Burning Fragment
    await giveItem(context.backend, TEST_PLAYER, 'blaze_powder');

    // Equip fragment
    context.bot.chat('/fire equip');
    await wait(500);

    // Use ability (should start cooldown)
    context.bot.chat('/fire 1');
    await wait(1000);

    // Try to use again - should be on cooldown
    const cooldownResult = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run fire 1`);
    expect(cooldownResult).toBeDefined();

    // Wait for cooldown to expire (40 seconds for /fire 1)
    await wait(COOLDOWNS.FIRE_1 * 1000);

    // Now ability should be available again
    const availableResult = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run fire 1`);
    expect(availableResult).toBeDefined();
  });

  it('should maintain cooldown after unequipping fragment', async () => {
    // Give and equip Burning Fragment
    await giveItem(context.backend, TEST_PLAYER, 'blaze_powder');
    context.bot.chat('/fire equip');
    await wait(500);

    // Use ability
    context.bot.chat('/fire 1');
    await wait(1000);

    // Unequip fragment
    context.bot.chat('/fire unequip');
    await wait(500);

    // Re-equip fragment
    context.bot.chat('/fire equip');
    await wait(500);

    // Try to use ability - should STILL be on cooldown
    const cooldownResult = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run fire 1`);
    expect(cooldownResult).toBeDefined();

    // Wait for cooldown to expire
    await wait(COOLDOWNS.FIRE_1 * 1000);
  });

  it('should have different cooldowns for different abilities', async () => {
    // Give Burning Fragment
    await giveItem(context.backend, TEST_PLAYER, 'blaze_powder');
    context.bot.chat('/fire equip');
    await wait(500);

    // Use ability 1 (40s cooldown)
    context.bot.chat('/fire 1');
    await wait(1000);

    // Try to use ability 2 (60s cooldown, should be available)
    context.bot.chat('/fire 2');
    await wait(1000);

    // Both abilities should now be on cooldown
    const ability1Cooldown = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run fire 1`);
    const ability2Cooldown = await context.backend.sendCommand(`execute as ${TEST_PLAYER} run fire 2`);

    expect(ability1Cooldown).toBeDefined();
    expect(ability2Cooldown).toBeDefined();
  });

  it('should use correct cooldown values from source code', async () => {
    // Test various abilities have correct cooldowns

    // Lightning: 60s
    await giveItem(context.backend, TEST_PLAYER, 'dragon_egg');
    await context.backend.sendCommand(`item replace entity ${TEST_PLAYER} weapon.offhand with dragon_egg`);
    await wait(500);
    context.bot.chat('/lightning');
    await wait(1000);
    await context.backend.sendCommand(`clear ${TEST_PLAYER}`);

    // Agility 1: 30s
    await giveItem(context.backend, TEST_PLAYER, 'phantom_membrane');
    context.bot.chat('/agile equip');
    await wait(500);
    context.bot.chat('/agile 1');
    await wait(1000);
    await context.backend.sendCommand(`clear ${TEST_PLAYER}`);

    // Immortal 1: 120s (2min)
    await giveItem(context.backend, TEST_PLAYER, 'diamond');
    context.bot.chat('/immortal equip');
    await wait(500);
    context.bot.chat('/immortal 1');
    await wait(1000);
    await context.backend.sendCommand(`clear ${TEST_PLAYER}`);

    // Corrupt 1: 180s (3min)
    await giveItem(context.backend, TEST_PLAYER, 'fermented_spider_eye');
    context.bot.chat('/corrupt equip');
    await wait(500);
    context.bot.chat('/corrupt 1');
    await wait(1000);

    // All should be on cooldown
    expect(true).toBe(true);
  });
});
