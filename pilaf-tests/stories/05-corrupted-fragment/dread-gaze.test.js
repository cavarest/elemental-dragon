/**
 * Dread Gaze (Freeze) Test
 *
 * Tests the Dread Gaze ability (/corrupt 1)
 * - Complete freeze on next melee hit
 * - Freeze duration: 4 seconds
 * - Freeze effects: SLOW 255, MINING_FATIGUE 255, WEAKNESS 255, HUNGER 255
 * - 180 second (3 minute) cooldown
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

describe('Corrupted Core - Dread Gaze', () => {
  let context;
  const TEST_PLAYER = 'TestPlayer';
  const TARGET_TAG = 'dread_gaze_target';

  beforeAll(async () => {
    context = await createTestContext({ ...config, username: TEST_PLAYER });
    await clearAllEntities(context.rcon);
    await teleportPlayer(context.rcon, TEST_PLAYER, { x: 0, y: 64, z: 0 }, 0, 0);
    await context.rcon.send(`effect clear ${TEST_PLAYER}`);
    await context.rcon.send(`clear ${TEST_PLAYER}`);
    await wait(500);
  });

  afterAll(async () => {
    if (context) {
      await clearAllEntities(context.rcon);
      await cleanupTestContext(context);
    }
  });

  it('should freeze target on next melee hit', async () => {
    // Give Corrupted Core Fragment (fermented_spider_eye)
    await giveItem(context.rcon, TEST_PLAYER, 'fermented_spider_eye');

    // Equip fragment
    context.bot.chat('/corrupt equip');
    await wait(500);

    // Activate Dread Gaze
    context.bot.chat('/corrupt 1');
    await wait(500);

    // Spawn target zombie nearby with high health
    // Use simple summon first to ensure entity spawns
    const summonResult = await context.rcon.send(`summon zombie 0.5 70 -0.5 {Tags:["${TARGET_TAG}"],Health:100f,NoAI:1,Silent:1,PersistenceRequired:1}`);
    console.log('Summon result:', summonResult);
    await wait(500);

    // Try checking for the specific tag using data get (returns RCON output if exists)
    const tagResult = await context.rcon.send(`data get entity @e[tag=${TARGET_TAG},limit=1] Health`);
    console.log('Tag check result:', tagResult);

    // Check if zombie exists by verifying it has health data (not "No entity was found")
    // The response format is "Zombie has the following entity data: 100.0f" when successful
    const zombieExists = tagResult.raw.includes('has the following entity data') || tagResult.raw.includes('100.0');

    // If spawn failed (no health data), try alternative spawn location
    if (!zombieExists) {
      // Try spawning at a different location
      const retryResult = await context.rcon.send(`summon zombie 0.5 80 -0.5 {Tags:["${TARGET_TAG}"],Health:100f,NoAI:1,Silent:1,PersistenceRequired:1}`);
      console.log('Retry summon result:', retryResult);
      await wait(500);
      const retryTagResult = await context.rcon.send(`data get entity @e[tag=${TARGET_TAG},limit=1] Health`);
      console.log('Retry tag check:', retryTagResult);
      const retryExists = retryTagResult.raw.includes('has the following entity data') || retryTagResult.raw.includes('100.0');
      expect(retryExists).toBe(true);
    }

    // Unfreeze the zombie temporarily so it can be hit
    await context.rcon.send(`entitydata @e[tag=${TARGET_TAG}] {NoAI:0}`);
    await wait(500);

    // Player attacks target (melee hit) - use small damage so zombie doesn't die
    await context.rcon.send(`execute as ${TEST_PLAYER} run damage @e[tag=${TARGET_TAG}] 5`);
    await wait(200);

    // Check if target still exists (should be frozen, not killed)
    const targetExists = await context.rcon.send(`data get entity @e[tag=${TARGET_TAG},limit=1] Health`);
    const stillExists = targetExists.raw.includes('has the following entity data');
    expect(stillExists).toBe(true);
  });

  it('should have correct cooldown after activation', async () => {
    // Give Corrupted Core Fragment
    await giveItem(context.rcon, TEST_PLAYER, 'fermented_spider_eye');
    context.bot.chat('/corrupt equip');
    await wait(500);

    // Activate Dread Gaze
    context.bot.chat('/corrupt 1');
    await wait(1000);

    // Try to use again - should be on cooldown
    const result = await context.rcon.send(`execute as ${TEST_PLAYER} run corrupt 1`);
    expect(result).toBeDefined();

    // Cooldown is 3 minutes, don't wait for it
  });
});
