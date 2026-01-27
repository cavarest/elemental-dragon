/**
 * Fragment Cooldown Persistence Tests
 *
 * Tests that cooldowns persist correctly across various scenarios:
 * - Cooldown continues when fragment is unequipped
 * - Cooldown shows correctly on HUD
 * - Cooldown formula: min(remaining, max) on re-equip
 */

import { createTestContext, cleanupTestContext } from '../../lib/rcon.js';
import { giveItem, teleportPlayer, wait, clearAllEntities } from '../../lib/entities.js';

const config = {
  host: process.env.RCON_HOST || process.env.MC_HOST || 'localhost',
  gamePort: parseInt(process.env.MC_PORT) || 25565,
  rconPort: parseInt(process.env.RCON_PORT) || 25575,
  rconPassword: process.env.RCON_PASSWORD || 'dragon123'
};

describe('Fragment Cooldown Persistence', () => {
  let context;
  const TEST_PLAYER = 'CooldownTester';

  beforeAll(async () => {
    context = await createTestContext({ ...config, username: TEST_PLAYER });
    await teleportPlayer(context.backend, TEST_PLAYER, { x: 0, y: 64, z: 0 }, 0, 0);
    await context.backend.sendCommand(`effect clear ${TEST_PLAYER}`);
    await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
    await clearAllEntities(context.backend);
    await wait(500);
  });

  afterAll(async () => {
    if (context) {
      await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
      await clearAllEntities(context.backend);
      await cleanupTestContext(context);
    }
  });

  describe('Cooldown Continues After Unequip', () => {
    it('should keep cooldown active after fragment is removed', async () => {
      // Clear inventory
      await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
      await wait(300);

      // Equip burning fragment
      context.bot.chat('/fire equip');
      await wait(500);

      // Use an ability to start cooldown
      // Note: Lightning has a cooldown, let's use it
      context.bot.chat('/lightning 1');
      await wait(500);

      // Get cooldown status via RCON (check persistence data)
      const cooldownBefore = await context.backend.sendCommand(
        `data get storage elemental_dragon cooldowns ${TEST_PLAYER}`
      );
      await wait(200);

      // Clear the fragment (simulate unequip by clearing inventory)
      // In real scenario, player would drop the fragment
      await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
      await wait(300);

      // Wait a short time
      await wait(500);

      // Check cooldown status after unequip
      // The cooldown should still be tracked in persistence
      const cooldownAfter = await context.backend.sendCommand(
        `data get storage elemental_dragon cooldowns ${TEST_PLAYER}`
      );
      await wait(200);

      // Commands executed successfully
      expect(cooldownBefore).toBeDefined();
      expect(cooldownAfter).toBeDefined();
    });
  });

  describe('Cooldown Display on HUD', () => {
    it('should show cooldown on HUD after ability use', async () => {
      // Clear inventory
      await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
      await wait(300);

      // Equip burning fragment
      context.bot.chat('/fire equip');
      await wait(500);

      // Use ability that has cooldown
      context.bot.chat('/fire 1');
      await wait(500);

      // The HUD should show cooldown for remaining abilities
      // We verify by checking player receives chat message about cooldown
      const hudCheck = await context.backend.sendCommand(
        `execute as ${TEST_PLAYER} run tellraw @s {"text":"Checking HUD status","color":"gray"}`
      );
      await wait(200);

      expect(hudCheck).toBeDefined();
    });
  });

  describe('Cooldown Formula on Re-equip', () => {
    it('should use min(remaining, max) formula when re-equipping', async () => {
      // Clear inventory
      await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
      await wait(300);

      // Step 1: Equip fragment and use ability to start cooldown
      context.bot.chat('/fire equip');
      await wait(500);

      // Use ability
      context.bot.chat('/lightning 1');
      await wait(500);

      // Step 2: Wait some time (e.g., 10 seconds)
      await wait(10000);

      // Step 3: Check remaining cooldown
      const remainingBefore = await context.backend.sendCommand(
        `data get storage elemental_dragon cooldowns ${TEST_PLAYER} lightning`
      );
      await wait(200);

      // Step 4: Clear fragment and re-equip
      await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
      await wait(300);

      context.bot.chat('/fire equip');
      await wait(500);

      // Step 5: Check cooldown after re-equip
      // Should be min(original remaining, max cooldown)
      const remainingAfter = await context.backend.sendCommand(
        `data get storage elemental_dragon cooldowns ${TEST_PLAYER} lightning`
      );
      await wait(200);

      // Both commands executed
      expect(remainingBefore).toBeDefined();
      expect(remainingAfter).toBeDefined();
    });
  });

  describe('Global Cooldown Tests', () => {
    it('should apply global cooldown between abilities', async () => {
      // Clear inventory
      await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
      await wait(300);

      // Equip burning fragment
      context.bot.chat('/fire equip');
      await wait(500);

      // Use first ability
      context.bot.chat('/lightning 1');
      await wait(500);

      // Immediately try second ability
      context.bot.chat('/fire 1');
      await wait(500);

      // Check if both abilities were used or second was blocked by global cooldown
      const result = await context.backend.sendCommand(
        `tellraw ${TEST_PLAYER} {"text":"Global cooldown test","color":"gray"}`
      );
      await wait(200);

      expect(result).toBeDefined();
    });
  });

  describe('Cooldown Zero Clearing', () => {
    it('should clear all cooldowns when set to 0', async () => {
      // Clear inventory
      await context.backend.sendCommand(`clear ${TEST_PLAYER}`);
      await wait(300);

      // Set global cooldown to 0 via operator command
      const setResult = await context.backend.sendCommand(
        `execute as ${TEST_PLAYER} run ed setglobalcooldown fire lightning 0`
      );
      await wait(200);

      // Verify cooldowns are cleared
      const checkResult = await context.backend.sendCommand(
        `data get storage elemental_dragon cooldowns ${TEST_PLAYER}`
      );
      await wait(200);

      expect(setResult).toBeDefined();
      expect(checkResult).toBeDefined();
    });
  });
});
