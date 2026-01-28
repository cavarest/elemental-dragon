# Pilaf Integration Guide for ElementalDragon Developers
## Automated Minecraft Testing Framework for PaperMC Plugins

---

## 📋 Executive Summary

**Pilaf** is a pure JavaScript testing framework that enables **fully automated testing** of Minecraft PaperMC servers without manual gameplay. For ElementalDragon developers, this means you can:

- ✅ **Test custom entities** (Elemental Dragons, custom mobs) automatically
- ✅ **Verify server state** via structured RCON queries (no raw text parsing)
- ✅ **Monitor log events** with pattern matching (join/death/command execution)
- ✅ **Correlate multi-step scenarios** (quest chains, player progression)
- ✅ **Run in CI/CD pipelines** with Docker integration

### Key Benefits Over Manual Testing

| Manual Testing | Pilaf Automated Testing |
|----------------|----------------------|
| 30 minutes per test cycle | 30 seconds per test cycle |
| Human error prone | Consistent, repeatable |
| Can't run in CI/CD | Perfect for CI/CD |
| Limited coverage | Comprehensive edge case coverage |
| No debugging telemetry | Full event correlation |

---

## 🏗️ Architecture Overview

Pilaf uses a **three-tier architecture** for complete server interaction:

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Pilaf Test Suite                           │
│  (Jest Tests with Pilaf Reporter)                                  │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                ┌─────────────────┼─────────────────┐
                ▼                 ▼                 ▼
    ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
    │ Mineflayer   │   │   RCON       │   │   Docker     │
    │ Bot Layer    │   │   Layer      │   │   Logs       │
    │              │   │              │   │              │
    │ - Chat       │   │ - Queries   │   │ - Events     │
    │ - Movement   │   │ - State     │   │ - Parsing    │
    │ - Actions    │   │ - Commands  │   │ - Reconnect  │
    └──────────────┘   └──────────────┘   └──────────────┘
            │                  │                  │
            └──────────────────┼──────────────────┘
                               ▼
                    ┌──────────────────────┐
                    │  Pilaf Backend Layer │
                    │                      │
                    │ ┌──────────────────┐ │
                    │ │  QueryHelper      │ │
                    │ │  - listPlayers()  │ │
                    │ │  - getTPS()       │ │
                    │ │  - getWorldTime() │ │
                    │ └──────────────────┘ │
                    │                      │
                    │ ┌──────────────────┐ │
                    │ │  EventObserver   │ │
                    │ │  - onPlayerJoin() │ │
                    │ │  - onPlayerDeath()│ │
                    │ │  - onEvent()      │ │
                    │ └──────────────────┘ │
                    │                      │
                    │ ┌──────────────────┐ │
                    │ │  LogMonitor      │ │
                    │ │  - Correlation    │ │
                    │ │  - Buffers       │ │
                    │ └──────────────────┘ │
                    └──────────────────────┘
```

### Key Components

- **MineflayerBackend**: Real Minecraft player simulation
- **RCONBackend**: Remote console for server commands
- **DockerLogCollector**: Streams logs from Docker containers
- **MinecraftLogParser**: Parses log lines into structured events
- **QueryHelper**: Convenience methods for common RCON queries
- **EventObserver**: Clean API for event subscription
- **Correlation Strategies**: Group events by player, session, or custom tags

---

## 🚀 Quick Start (5 Minutes)

### 1. Installation

```bash
cd your-elemental-dragon-project
pnpm add @pilaf/backends
```

### 2. Basic Test Example

```javascript
// tests/dragon-spawn.test.js
const { MineflayerBackend } = require('@pilaf/backends');

describe('Elemental Dragon Spawning', () => {
  let backend;

  beforeEach(async () => {
    backend = new MineflayerBackend();
    await backend.connect({
      host: 'localhost',
      port: 25565,
      username: 'TestBot',
      auth: 'offline'
    });
  });

  afterEach(async () => {
    await backend.disconnect();
  });

  it('should spawn dragon at specified location', async () => {
    // Start observing events
    await backend.observe();

    // Spawn the dragon
    await backend.chat('/summon elemental_dragon 100 64 200');

    // Verify server response
    const tps = await backend.getTPS();
    expect(tps.tps).toBeGreaterThan(15); // Server should still be healthy

    // Clean up
    backend.unobserve();
  });
});
```

### 3. Run Tests

```bash
pilaf test tests/dragon-spawn.test.js
```

---

## 📚 Feature Deep Dives

### 1. QueryHelper - Structured Server State Queries

**Problem**: RCON returns raw text that requires manual parsing
```javascript
// Old way - painful string manipulation
const response = await rcon.send('/list');
const match = response.match(/There are (\d+) players online: (.*)/);
const count = parseInt(match[1]);
const players = match[2].split(', ').map(p => p.trim());
```

**Solution**: QueryHelper provides structured responses
```javascript
const { online, players } = await backend.listPlayers();
console.log(online);    // 2
console.log(players);  // ['Steve', 'Alex']
```

#### Available Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `listPlayers()` | `{ online, players[] }` | Get online player count and names |
| `getPlayerInfo(username)` | `{ player, position, health, ... }` | Get detailed player data |
| `getWorldTime()` | `{ time, daytime }` | Get server time (0-24000) |
| `getWeather()` | `{ weather, duration }` | Get current weather |
| `getDifficulty()` | `{ difficulty }` | Get server difficulty |
| `getGameMode()` | `{ gameMode, mode }` | Get game mode (survival, creative, etc.) |
| `getTPS()` | `{ tps }` | Get server ticks-per-second |
| `getSeed()` | `{ seed }` | Get world seed |

#### Example: Verify Player State After Command

```javascript
it('should teleport player to new location', async () => {
  await backend.chat('/tp Steve 100 64 200');

  // Verify position change
  const info = await backend.getPlayerInfo('Steve');
  expect(info.position).toEqual({ x: 100, y: 64, z: 200 });
});
```

---

### 2. EventObserver - Pattern-Based Event Subscription

**Problem**: Parsing logs manually is error-prone and inconsistent
```javascript
// Old way - fragile regex matching
const logs = await getLogs();
for (const line of logs) {
  if (line.includes('joined the game')) {
    const match = line.match(/(\w+) joined the game/);
    // Handle join...
  }
}
```

**Solution**: EventObserver with declarative subscriptions
```javascript
backend.onPlayerJoin((event) => {
  console.log(`${event.data.player} joined at ${event.data.timestamp}`);
  expect(event.data.player).toBe('TestPlayer');
});
```

#### Available Event Methods

| Method | Triggers When | Event Data |
|--------|---------------|------------|
| `onPlayerJoin(callback)` | Player joins server | `{ player, timestamp, location }` |
| `onPlayerLeave(callback)` | Player leaves server | `{ player, timestamp, reason }` |
| `onPlayerDeath(callback)` | Player/entity dies | `{ victim, killer, cause }` |
| `onPlayerChat(callback)` | Player sends message | `{ player, message }` |
| `onCommand(callback)` | Command executed | `{ command, executor, result }` |
| `onEvent(pattern, callback)` | Custom log pattern | `{ type, data, raw }` |

#### Example: Multi-Event Monitoring

```javascript
it('should track player lifecycle events', async () => {
  await backend.observe();

  const events = [];
  backend.onPlayerJoin((e) => events.push('join'));
  backend.onPlayerLeave((e) => events.push('leave'));
  backend.onPlayerDeath((e) => events.push('death'));

  // Simulate player actions
  await backend.chat('/gamemode survival');
  await backend.chat('/kill Steve');

  // Verify event sequence
  expect(events).toContain('death');

  backend.unobserve();
});
```

#### Wildcard Pattern Matching

```javascript
// Subscribe to all entity death events
backend.onEvent('entity.death.*', (event) => {
  console.log(`Death: ${event.type}`, event.data);
  // Matches: entity.death.player, entity.death.mob, etc.
});
```

---

### 3. Enhanced MineflayerBackend - All-in-One Integration

**New in Phase 5**: MineflayerBackend now integrates QueryHelper and EventObserver with **lazy initialization**.

```javascript
const backend = new MineflayerBackend();

// Connect with RCON for queries
await backend.connect({
  host: 'localhost',
  port: 25565,
  auth: 'offline',
  rconPort: 25575,
  rconPassword: 'password'
});

// Query methods delegate to QueryHelper
const players = await backend.listPlayers();
const tps = await backend.getTPS();

// Event methods delegate to EventObserver (lazily created)
backend.onPlayerJoin((event) => {
  console.log('Player joined:', event.data.player);
});

await backend.observe(); // Starts log monitoring automatically
backend.unobserve(); // Stop observing
```

#### Lazy Initialization Benefits

- **Zero overhead** if you don't use events
- **Automatic setup** when you call `observe()`
- **Shared RCON connection** between queries and events

---

### 4. Correlation Strategies - Link Related Events

**Problem**: Tracking multi-step scenarios (e.g., quest chains) is difficult
```javascript
// Challenge: Link these events
// 1. Player accepts quest
// 2. Player kills dragon
// 3. Player turns in quest
// How do you know they're the same quest session?
```

**Solution**: Correlation strategies automatically group related events

#### UsernameCorrelationStrategy

Groups events by player username:

```javascript
const { LogMonitor } = require('@pilaf/backends');
const { UsernameCorrelationStrategy } = require('@pilaf/backends');

const monitor = new LogMonitor({
  correlation: new UsernameCorrelationStrategy()
});

monitor.on('correlation', (session) => {
  console.log(`Player ${session.username} session:`);
  console.log(`  Events: ${session.events.length}`);
  console.log(`  Active: ${session.isActive}`);

  // session.events contains all events for this player
  session.events.forEach(event => {
    console.log(`    - ${event.type}: ${JSON.stringify(event.data)}`);
  });
});
```

#### TagCorrelationStrategy

Groups events by custom tag (e.g., quest ID):

```javascript
const { TagCorrelationStrategy } = require('@pilaf/backends');

const questCorrelation = new TagCorrelationStrategy({
  tagExtractor: (event) => event.data.questId,
  timeout: 300000 // 5 minutes
});

// All events with same questId are grouped
// Automatically expires after timeout
```

---

## 🎯 Real-World Testing Scenarios

### Scenario 1: Testing Custom Entity (Elemental Dragon)

**Goal**: Verify custom entity spawns correctly and behaves as expected

```javascript
const { MineflayerBackend } = require('@pilaf/backends');

describe('Elemental Dragon Entity', () => {
  let backend;

  beforeEach(async () => {
    backend = new MineflayerBackend();
    await backend.connect({
      host: 'localhost',
      port: 25565,
      username: 'TestBot',
      auth: 'offline',
      rconPort: 25575,
      rconPassword: 'test'
    });
    await backend.observe();
  });

  afterEach(async () => {
    backend.unobserve();
    await backend.disconnect();
  });

  it('should spawn dragon at specified coordinates', async () => {
    // Spawn dragon
    await backend.chat('/summon elemental_dragon 100 70 200');

    // Wait for spawn event
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Verify TPS remains healthy (no lag)
    const { tps } = await backend.getTPS();
    expect(tps).toBeGreaterThan(10);
  });

  it('should attack nearby players', async () => {
    // Spawn dragon
    await backend.chat('/summon elemental_dragon 100 70 200');

    // Spawn test player
    await backend.chat('/execute as @p[tag=dragon_target] in minecraft:summon illusion ~ ~ ~ {EntityTag:{id:"dragon_target"}}');

    // Monitor for damage events
    const damageEvents = [];
    backend.onEvent('entity.damage.player', (event) => {
      damageEvents.push(event);
    });

    // Wait for attack
    await new Promise(resolve => setTimeout(resolve, 5000));

    // Verify dragon attacked player
    expect(damageEvents.length).toBeGreaterThan(0);
  });

  it('should be removed with kill command', async () => {
    // Spawn dragon
    await backend.chat('/summon elemental_dragon 100 70 200');

    // Kill dragon
    await backend.chat('/kill @e[type=elemental_dragon]');

    // Verify death event
    await new Promise(resolve => setTimeout(resolve, 500));

    const { tps } = await backend.getTPS();
    expect(tps).toBeGreaterThan(15); // Server should recover
  });
});
```

---

### Scenario 2: Player Progression Testing

**Goal**: Test XP earning, leveling, and stat changes

```javascript
describe('Player Progression', () => {
  let backend;

  beforeEach(async () => {
    backend = new MineflayerBackend();
    await backend.connect({
      host: 'localhost',
      port: 25565,
      username: 'TestBot',
      auth: 'offline',
      rconPort: 25575,
      rconPassword: 'test'
    });
    await backend.observe();
  });

  afterEach(async () => {
    backend.unobserve();
    await backend.disconnect();
  });

  it('should award XP on entity kill', async () => {
    // Get starting XP
    const { player: before } = await backend.getPlayerInfo('TestBot');

    // Give player XP
    await backend.chat('/xp add 100 TestBot');

    // Wait for update
    await new Promise(resolve => setTimeout(resolve, 500));

    // Verify XP increased
    const { player: after } = await backend.getPlayerInfo('TestBot');
    expect(after.xp - before.xp).toBeGreaterThanOrEqual(100);
  });

  it('should level up at XP threshold', async () => {
    // Set to level 29 with near-max XP
    await backend.chat('/xp set level 29 TestBot');
    await backend.chat('/xp set -10 points TestBot');

    // Get level before
    const { player: before } = await backend.getPlayerInfo('TestBot');

    // Add enough XP to level up
    await backend.chat('/xp add 50 TestBot');

    // Wait for level up event
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Verify level increased
    const { player: after } = await backend.getPlayerInfo('TestBot');
    expect(after.level).toBeGreaterThan(before.level);
  });
});
```

---

### Scenario 3: Command Testing

**Goal**: Test custom ElementalDragon commands

```javascript
describe('Custom Commands', () => {
  let backend;

  beforeEach(async () => {
    backend = new MineflayerBackend();
    await backend.connect({
      host: 'localhost',
      port: 25565,
      username: 'TestBot',
      auth: 'offline',
      rconPort: 25575,
      rconPassword: 'test'
    });
  });

  afterEach(async () => {
    await backend.disconnect();
  });

  it('should respond to /dragon command with info', async () => {
    // Register command listener
    const commandResults = [];
    backend.onCommand((event) => {
      if (event.data.command === '/dragon') {
        commandResults.push(event.data.result);
      }
    });

    // Execute command
    await backend.chat('/dragon');

    // Verify response
    await new Promise(resolve => setTimeout(resolve, 500));
    expect(commandResults.length).toBeGreaterThan(0);
  });

  it('should show error for invalid command usage', async () => {
    // Execute invalid command
    await backend.chat('/dragon invalid args');

    // Wait for error response
    await new Promise(resolve => setTimeout(resolve, 500));

    // Verify server still responsive
    const { tps } = await backend.getTPS();
    expect(tps).toBeGreaterThan(0);
  });
});
```

---

### Scenario 4: Performance Testing

**Goal**: Ensure server remains stable under load

```javascript
describe('Performance Tests', () => {
  let backend;

  beforeEach(async () => {
    backend = new MineflayerBackend();
    await backend.connect({
      host: 'localhost',
      port: 25565,
      username: 'TestBot',
      auth: 'offline',
      rconPort: 25575,
      rconPassword: 'test'
    });
  });

  afterEach(async () => {
    await backend.disconnect();
  });

  it('should maintain TPS > 15 when spawning 100 entities', async () => {
    const beforeTPS = await backend.getTPS();

    // Spawn many entities
    for (let i = 0; i < 100; i++) {
      await backend.chat(`/summon zombie ~${i} ~ ~`);
    }

    // Wait for server to process
    await new Promise(resolve => setTimeout(resolve, 5000));

    // Check TPS
    const afterTPS = await backend.getTPS();
    expect(afterTPS.tps).toBeGreaterThan(15);

    // Cleanup
    await backend.chat('/kill @e[type=zombie]');
  });

  it('should handle rapid RCON queries without rate limiting', async () => {
    const queries = [];

    // Send many rapid queries
    for (let i = 0; i < 50; i++) {
      queries.push(backend.getTPS());
    }

    // All should complete
    const results = await Promise.all(queries);
    results.forEach(({ tps }) => {
      expect(tps).toBeGreaterThan(0);
    });
  });
});
```

---

### Scenario 5: Docker CI/CD Integration

**Goal**: Run tests in isolated Docker environment

```javascript
const { DockerLogCollector, MinecraftLogParser, LogMonitor, UsernameCorrelationStrategy } = require('@pilaf/backends');

describe('Docker Integration Tests', () => {
  let monitor;
  let collector;

  beforeEach(async () => {
    // Connect to Docker container
    collector = new DockerLogCollector({
      container: 'minecraft-server',
      follow: true
    });

    const parser = new MinecraftLogParser();

    monitor = new LogMonitor({
      collector,
      parser,
      correlation: new UsernameCorrelationStrategy(),
      bufferSize: 1000
    });

    await monitor.start();
  });

  afterEach(async () => {
    await monitor.stop();
    await collector.disconnect();
  });

  it('should capture events from Docker container logs', async () => {
    const events = [];

    monitor.on('event', (event) => {
      events.push(event);
    });

    // Run test commands via RCON
    const backend = new RconBackend();
    await backend.connect({ host: 'localhost', port: 25575, password: 'test' });
    await backend.sendCommand('/list');

    // Wait for log processing
    await new Promise(resolve => setTimeout(resolve, 2000));

    // Verify events captured
    expect(events.length).toBeGreaterThan(0);
  });
});
```

---

## 🎓 Best Practices

### 1. Test Isolation

```javascript
describe('My Test Suite', () => {
  let backend;

  beforeEach(async () => {
    // Use unique usernames to avoid conflicts
    backend = new MineflayerBackend();
    await backend.connect({
      username: `TestBot_${Date.now()}`, // Unique username
      auth: 'offline'
    });
  });

  afterEach(async () => {
    // Always disconnect to prevent lingering connections
    await backend.disconnect();
  });
});
```

### 2. Event Observation Lifecycle

```javascript
it('should observe events during test', async () => {
  // Start observing BEFORE triggering events
  await backend.observe();

  // Do test actions
  await backend.chat('/test command');

  // Give events time to process
  await new Promise(resolve => setTimeout(resolve, 500));

  // Stop observing
  backend.unobserve();
});
```

### 3. Use Correlation for Multi-Step Tests

```javascript
it('should track quest completion flow', async () => {
  const questEvents = [];

  monitor.on('correlation', (session) => {
    if (session.questId === 'dragon_slayer_001') {
      questEvents.push(...session.events);
    }
  });

  // Trigger quest flow
  await backend.chat('/quest start dragon_slayer_001');
  await backend.chat('/kill @e[type=dragon]');
  await backend.chat('/quest turnin dragon_slayer_001');

  await new Promise(resolve => setTimeout(resolve, 2000));

  // Verify all quest events occurred
  expect(questEvents).toHaveLength(3);
});
```

### 4. Handle Async Timing

```javascript
it('should handle async event timing', async () => {
  let eventReceived = false;

  backend.onPlayerJoin(() => {
    eventReceived = true;
  });

  // Trigger event
  await backend.chat('/test trigger join');

  // Wait with timeout
  await waitFor(() => eventReceived, 5000);
  expect(eventReceived).toBe(true);
});

function waitFor(condition, timeout) {
  return new Promise((resolve) => {
    const startTime = Date.now();
    const check = () => {
      if (condition() || Date.now() - startTime > timeout) {
        resolve(condition());
      } else {
        setTimeout(check, 100);
      }
    };
    check();
  });
}
```

### 5. Docker for CI/CD

```yaml
# docker-compose.test.yml
version: '3'
services:
  minecraft-server:
    image: pilaf/minecraft-test-server
    ports:
      - "25565:25565"  # Minecraft
      - "25575:25575"  # RCON
    environment:
      - RCON_PASSWORD=test
      - OPS=TestBot
    volumes:
      - ./plugins:/plugins
```

---

## 🔧 Troubleshooting

### Issue: "Docker is not a constructor"

**Cause**: Dockerode mock isn't properly set up in tests

**Solution**: Ensure tests use mocked Dockerode:
```javascript
jest.mock('dockerode', () => jest.fn().mockImplementation(() => ({
  getContainer: jest.fn().mockReturnValue({
    logs: jest.fn().mockResolvedValue(mockStream)
  })
})));
```

### Issue: Events not being captured

**Cause**: Not observing before triggering events

**Solution**:
```javascript
// WRONG
await backend.chat('/test');
await backend.observe();
backend.onPlayerJoin(handler);

// CORRECT
await backend.observe();
backend.onPlayerJoin(handler);
await backend.chat('/test');
```

### Issue: Tests timing out

**Cause**: Not waiting for async operations

**Solution**: Use proper async/await and timeouts:
```javascript
it('should handle async operations', async () => {
  await backend.chat('/command');

  // Wait for server response
  await new Promise(resolve => setTimeout(resolve, 1000));

  // Then verify
  const result = await backend.getPlayerInfo('TestBot');
  expect(result).toBeDefined();
}, 10000); // Increase test timeout if needed
```

### Issue: Reconnection tests failing

**Cause**: Fake timers don't work well with async reconnection

**Solution**: Use real timers for reconnection tests:
```javascript
beforeEach(() => {
  jest.useRealTimers();
});

afterEach(() => {
  jest.useFakeTimers();
});
```

---

## 📖 Migration Guide

### From Manual Testing to Pilaf

**Before (Manual Testing):**
1. Start server manually
2. Join game manually
3. Type commands manually
4. Visually verify results
5. Document findings

**After (Pilaf Automated):**
```javascript
// All steps automated and repeatable
it('should verify dragon spawning', async () => {
  await backend.connect({ /* ... */ });
  await backend.observe();

  await backend.chat('/summon elemental_dragon 100 64 100');

  await new Promise(resolve => setTimeout(resolve, 1000));

  const { tps } = await backend.getTPS();
  expect(tps.tps).toBeGreaterThan(15);

  backend.unobserve();
  await backend.disconnect();
});
```

### From Raw RCON to QueryHelper

**Before:**
```javascript
const response = await rcon.send('/time');
const time = parseInt(response.match(/time is (\d+)/)[1]);
```

**After:**
```javascript
const { time } = await backend.getWorldTime();
```

### From Log Parsing to EventObserver

**Before:**
```javascript
const logs = await getLogs();
for (const line of logs) {
  if (line.includes('joined')) {
    const player = line.match(/(\w+) joined/)[1];
    // ...
  }
}
```

**After:**
```javascript
backend.onPlayerJoin((event) => {
  const { player, timestamp } = event.data;
  // Handle join event
});
```

---

## 📋 Checklist for Adopting Pilaf

### Setup Phase
- [ ] Install `@pilaf/backends` package
- [ ] Configure test environment (Jest, Pilaf reporter)
- [ ] Set up test server (local or Docker)
- [ ] Configure RCON credentials

### Implementation Phase
- [ ] Write first test (start with simple query)
- [ ] Add event observation for relevant events
- [ ] Implement correlation if needed
- [ ] Set up test isolation (beforeEach/afterEach)

### Integration Phase
- [ ] Run tests locally to verify
- [ ] Add to CI/CD pipeline
- [ ] Configure Docker test environment
- [ ] Set up test reporting

### Best Practices Phase
- [ ] Establish test naming conventions
- [ ] Create reusable test utilities
- [ ] Document test patterns
- [ ] Train team on Pilaf usage

---

## 🚀 Getting Help

- **Documentation**: See README.md in `@pilaf/backends` package
- **Examples**: Check `lib/helpers/*.pilaf.test.js` for reference implementations
- **Issues**: Report bugs to Pilaf GitHub repository
- **Architecture**: See `docs/plans/2025-01-16-pilaf-js-design.md` for architecture details

---

## 📊 API Reference Summary

### MineflayerBackend Methods

```javascript
const backend = new MineflayerBackend();

// Connection
await backend.connect(options);
await backend.disconnect();

// Query methods (delegates to QueryHelper)
await backend.listPlayers();
await backend.getPlayerInfo(username);
await backend.getWorldTime();
await backend.getWeather();
await backend.getDifficulty();
await backend.getGameMode();
await backend.getTPS();
await backend.getSeed();

// Event methods (delegates to EventObserver)
await backend.observe();
backend.unobserve();
backend.onPlayerJoin(callback);
backend.onPlayerLeave(callback);
backend.onPlayerDeath(callback);
backend.onPlayerChat(callback);
backend.onCommand(callback);
backend.onEvent(pattern, callback);
```

### QueryHelper Methods

```javascript
const helper = new QueryHelper(rconBackend);

await helper.listPlayers();        // { online, players[] }
await helper.getPlayerInfo(name);    // { player, position, ... }
await helper.getWorldTime();        // { time, daytime }
await helper.getWeather();           // { weather, duration }
await helper.getDifficulty();       // { difficulty }
await helper.getGameMode();          // { gameMode, mode }
await helper.getTPS();               // { tps }
await helper.getSeed();               // { seed }
```

### EventObserver Methods

```javascript
const observer = new EventObserver({ logMonitor, parser });

await observer.start();
observer.stop();

observer.onEvent(pattern, callback);        // Returns unsubscribe function
observer.onPlayerJoin(callback);            // Convenience method
observer.onPlayerLeave(callback);            // Convenience method
observer.onPlayerDeath(callback);            // Convenience method
observer.onPlayerChat(callback);             // Convenience method
observer.onCommand(callback);                 // Convenience method
```

---

**Ready to transform your testing workflow?** Start with the Quick Start guide above, and refer to the Real-World Testing Scenarios for patterns specific to ElementalDragon's needs.
