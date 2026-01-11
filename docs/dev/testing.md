---
layout: default
title: Developer Testing
nav_order: 4
has_children: false
permalink: /dev/testing/
---

# Developer Testing Documentation

This document provides comprehensive testing guidance for developers working on the Elemental Dragon plugin. It covers build setup, running tests, manual testing procedures, automated testing, and troubleshooting.

## Build Setup

### Prerequisites

Before building and testing the plugin, ensure your development environment meets these requirements:

- **Java 21** (required for PaperMC 1.21.x compatibility)
- **Gradle 9.x** (wrapper is included in the project)
- **Docker & Docker Compose** (for server testing)
- **Git** (for version control and commit info in debug builds)

Verify your Java version by running `java -version`. If you have multiple Java versions installed, ensure Java 21 is set as the active version. The plugin will not compile correctly with older Java versions due to dependencies on PaperMC API 1.21.11-R0.1-SNAPSHOT.

### Building the Plugin

The project includes a build script that handles both debug and production builds. The debug build includes git commit information for development purposes, while the production build creates a clean JAR file for deployment.

```bash
# Debug build (includes git commit info for development)
./build.sh

# Production build (clean, optimized)
./build.sh --production

# Build output location
build/libs/elemental-dragon-1.0.2.jar
```

The build script performs several operations automatically. It compiles all Java source files, runs the Gradle build task, includes or excludes git metadata depending on the build type, and outputs the final JAR to the `build/libs/` directory. The production build additionally cleans previous build artifacts and applies optimization flags.

For manual Gradle builds, you can use these commands directly:

```bash
# Standard build
./gradlew build

# Build without running tests
./gradlew build -x test

# Clean and build
./gradlew clean build

# Shadow/fat JAR for deployment
./gradlew shadowJar
```

The shadow JAR task creates a single executable JAR with all dependencies bundled, which is ideal for deployment to production servers where you want to avoid dependency conflicts with other plugins.

## Running Tests

### Unit Tests

Unit tests verify the core functionality of individual components in isolation. They use JUnit 5 and Mockito to mock Bukkit API classes, allowing fast and reliable testing without running a Minecraft server.

Run all unit tests with this command:

```bash
./gradlew test
```

The test results will be available in `build/reports/tests/test/index.html`. Open this file in a web browser to view detailed test results, including passed/failed tests, code coverage information, and stack traces for failures.

Run specific test classes to focus on particular components:

```bash
# Lightning ability tests
./gradlew test --tests "*LightningAbilityTest*"

# Fragment manager tests
./gradlew test --tests "*FragmentManagerTest*"

# Fragment ability tests
./gradlew test --tests "*FragmentAbilityTest*"

# HUD manager tests
./gradlew test --tests "*HudManagerTest*"

# Ability manager tests
./gradlew test --tests "*AbilityManagerTest*"
```

Each test class focuses on specific functionality. `LightningAbilityTest` covers lightning targeting logic, damage calculation, and cooldown management. `FragmentManagerTest` verifies fragment equipping, unequipping, and state management. `FragmentAbilityTest` tests the properties and behaviors of individual fragment types. `HudManagerTest` validates message display and action bar updates. `AbilityManagerTest` covers the coordination between abilities and cooldown tracking.

### Docker Server Testing

Docker provides a consistent testing environment that mimics production. The project includes Docker Compose configurations for different testing scenarios.

Start the test server with this command:

```bash
./start-server.sh
```

This script performs several actions automatically. It first builds the plugin JAR using Gradle, then starts the PaperMC server in a Docker container with the plugin installed to `server-plugins/`. The server data persists in `server-data/` so your world and settings are preserved between restarts. The container exposes ports 25565 for Minecraft connections and 25575 for RCON access.

The default Docker Compose configuration uses `docker-compose.local.yml` which sets up a basic PaperMC server with the plugin. For CI/CD testing, use `docker-compose.ci.yml` which includes additional monitoring and logging.

Stop the server when testing is complete:

```bash
./stop-server.sh
```

View server logs to debug issues:

```bash
# Follow logs in real-time
docker logs -f papermc-elementaldragon

# View last 100 lines
docker logs --tail 100 papermc-elementaldragon
```

Connect to the server using a Minecraft client (same version as the server, typically 1.21.x) at `localhost:25565`. For remote testing, replace `localhost` with your server's IP address.

### Manual Testing Script

The project includes a manual testing script that guides you through testing all features:

```bash
./test-manual.sh
```

This script provides an interactive checklist for testing lightning abilities, all four fragment types, crafting recipes, the chronicle/lore system, cooldown persistence, and edge cases. Follow the prompts and enter test results as you complete each step.

## Manual Testing Guide

### Quick Test (5 Minutes)

For a rapid validation that the plugin is working correctly, follow these steps:

1. Start the server: `./start-server.sh`
2. Wait for the server to fully initialize (look for "Done" in the console)
3. Connect your Minecraft client to `localhost:25565`
4. Execute these commands to verify basic functionality:
   - `/elementaldragon info` - Should display plugin version and status
   - `/lightning 1` - Should trigger lightning strikes (requires dragon egg in offhand)
   - `/fire equip` - Should equip the Burning Fragment
   - `/chronicle` - Should display the chronicle of fallen dragons

If all commands execute without errors and produce expected responses, the basic functionality is working. Move on to the full test suite for comprehensive validation.

### Full Test Suite

The complete manual testing procedure covers all plugin features systematically. Use the `test-manual.sh` script which provides step-by-step guidance through these tests:

**Lightning Ability Tests** verify that the core lightning feature works correctly. Test that lightning strikes occur when `/lightning 1` is executed, that damage is dealt correctly to hostile mobs, that the cooldown system prevents spam, and that targeting switches to new enemies when current targets are killed.

**Fragment Ability Tests** cover all four fragment types and their unique abilities. Test each fragment's two active abilities, passive effects, and cooldown management. Verify that equipping a new fragment unequips the previous one and that passive effects apply and remove correctly.

**Crafting Tests** validate that all crafting recipes produce the expected items. Test the Heavy Core recipe, fragment crafting recipes, and any other custom recipes added by the plugin.

**Chronicle/Lore Tests** verify that the lore system unlocks correctly as players use abilities. Check that pages are discovered in order and that the chronicle command displays discovered lore.

**Cooldown Persistence Tests** confirm that cooldowns survive player logout/login cycles and are properly cleared on death.

**Edge Case Tests** cover error handling, permission checks, and boundary conditions.

## Test Scenarios by Feature

### Lightning Ability Tests

The lightning ability is the core feature of the Elemental Dragon plugin. These tests verify all aspects of its functionality.

| Test Case | Command | Expected Result |
|-----------|---------|-----------------|
| Basic lightning strike | `/lightning 1` | 3 sequential lightning strikes, 6 hearts total damage |
| Dragon egg requirement | Hold egg in offhand, `/lightning 1` | Works only with dragon egg in offhand slot |
| No egg message | `/lightning 1` without egg | Error message about missing dragon egg |
| Cooldown display | After use | Action bar shows countdown timer (60 seconds) |
| Cooldown persistence | Logout and login | Cooldown maintained across sessions |
| Death clears cooldown | Die and respawn | Cooldown cancelled on death |
| Command alias | `/ability 1` | Works identically to `/lightning 1` |
| No targets message | `/lightning 1` with no hostile mobs | Message indicating no targets found |
| Target switching | Kill target mid-sequence | Lightning strikes next closest hostile |

The lightning ability uses intelligent targeting that finds the closest hostile mob within the player's view. If multiple hostile mobs are present, it prioritizes the closest one. When a target is killed, the ability automatically switches to the next closest hostile mob, allowing the remaining strikes to hit new targets.

### Fragment Tests

The fragment system adds four unique playstyles to the plugin. Each fragment has two active abilities and passive effects that apply while the fragment is equipped.

| Test Case | Command | Expected Result |
|-----------|---------|----------------- |
| Equip Burning Fragment | `/fire equip` | Message "Equipped Burning Fragment" |
| Equip Agility Fragment | `/agile equip` | Message "Equipped Agility Fragment" |
| Equip Immortal Fragment | `/immortal equip` | Message "Equipped Immortal Fragment" |
| Equip Corrupted Core | `/corrupt equip` | Message "Equipped Corrupted Core" |
| Use ability 1 | `/fire 1` or `/agile 1` | First ability triggers with visual effects |
| Use ability 2 | `/fire 2` or `/agile 2` | Second ability triggers with visual effects |
| Cooldown display | After ability use | Action bar shows cooldown countdown |
| Unequip fragment | `/fire unequip` or `/agile unequip` | Fragment removed, passive effects cleared |
| Switch fragments | Equip new fragment | Old fragment unequipped, new one equipped |
| Passive effects active | Check player status | Appropriate effects applied |

#### Burning Fragment Tests

The Burning Fragment focuses on fire-based abilities that damage enemies and control terrain.

- **Dragon's Wrath** (Ability 1): Launches a fireball projectile that explodes on impact, setting ground on fire and damaging enemies in the area. Test that the projectile travels correctly, explodes at the target location, and applies fire damage to nearby entities.

- **Infernal Dominion** (Ability 2): Creates a ring of fire around the player that damages enemies who enter it and provides pushback to keep enemies at bay. Test that the ring appears correctly centered on the player, damages enemies periodically, and pushes back entities that touch it.

- **Passive Effect**: Reduces fire and lava damage by 50%. Test by having a player with the Burning Fragment equipped stand in lava and verify they take reduced damage compared to a player without the fragment.

#### Agility Fragment Tests

The Agility Fragment enhances movement and provides escape capabilities.

- **Draconic Surge** (Ability 1): Grants Speed II, Jump Boost II, and water breathing for a duration. Test that all three effects are applied and that the player can jump higher and breathe underwater.

- **Wing Burst** (Ability 2): Launches the player upward and provides slow fall to prevent fall damage. Test that the vertical launch works and that slow fall activates to cushion the landing.

- **Passive Effect**: Permanent Speed I while equipped. Test that the speed boost is always active and is removed when the fragment is unequipped.

#### Immortal Fragment Tests

The Immortal Fragment focuses on survival and defensive capabilities.

- **Draconic Reflex** (Ability 1): Provides 75% damage reduction for a short duration and reflects a portion of incoming damage back to attackers. Test that damage taken is significantly reduced and that attackers receive reflected damage.

- **Essence Rebirth** (Ability 2): Long cooldown ability that provides benefits upon respawn. Test that the ability sets a flag that activates on death, granting spawn invincibility, bonus experience, or other defined benefits.

- **Passive Effect**: +2 permanent hearts (4 health) and knockback resistance. Test that the extra hearts are added to max health and that the player is resistant to knockback from explosions and attacks.

#### Corrupted Core Tests

The Corrupted Core provides dark abilities that debuff enemies and steal life.

- **Dread Gaze** (Ability 1): Applies blindness to nearby hostile mobs, reducing their detection range and accuracy. Test that enemies within radius become blinded and behave as if affected by the blindness effect.

- **Life Devourer** (Ability 2): Drains health from nearby enemies and heals the player for a portion of the damage dealt. Test that enemies lose health and the player gains health proportional to the damage.

- **Passive Effect**: Permanent night vision and creepers do not target the player. Test that the player can see clearly in dark areas and that creepers do not approach or detonate.

### Crafting Tests

The crafting system allows players to create the Heavy Core and fragment items through Minecraft's crafting interface.

| Test Case | Action | Expected Result |
|-----------|--------|----------------- |
| Heavy Core recipe | Craft with obsidian, iron block, dragon egg | Heavy Core item created |
| Burning Fragment recipe | Craft Heavy Core with fire-related items | Burning Fragment created |
| Agility Fragment recipe | Craft Heavy Core with speed-related items | Agility Fragment created |
| Immortal Fragment recipe | Craft Heavy Core with survival items | Immortal Fragment created |
| Corrupted Core recipe | Craft Heavy Core with nether items | Corrupted Core created |

Verify that recipes use the correct shapeless or shaped format, that the output is the expected item with proper metadata or custom model data, and that the recipe appears in the recipe book when discovered.

### Chronicle/Lore Tests

The chronicle system unlocks lore pages as players use abilities, encouraging exploration of all features.

| Test Case | Action | Expected Result |
|-----------|--------|----------------- |
| View chronicle | `/chronicle` | Shows discovered pages and locked pages |
| Unlock first page | Use first ability | Page 1 unlocked with description |
| Unlock all pages | Use all abilities | All chronicle pages discovered |
| Chronicle persistence | Relog after unlocking | Progress maintained |
| Page content | Read unlocked page | Correct lore text displayed |

### Cooldown Persistence Tests

Cooldown persistence ensures fair gameplay across sessions.

| Test Case | Action | Expected Result |
|-----------|--------|----------------- |
| Cooldown starts | Use ability | Cooldown begins counting down |
| Logout during cooldown | Disconnect from server | Cooldown maintained |
| Login after cooldown | Reconnect after cooldown expires | Ability available immediately |
| Death during cooldown | Die and respawn | Cooldown completely cleared |
| Multiple abilities | Use lightning then fragment | Each has independent cooldown |

### Permission Tests

Verify that the permission system correctly controls access to features.

| Test Case | Permission | Expected Result |
|-----------|------------|----------------- |
| Lightning without permission | No `elementaldragon.lightning` | Command fails with permission message |
| Lightning with permission | `elementaldragon.lightning` | Command works normally |
| Fragment without permission | No `elementaldragon.fragment` | Command fails |
| Fragment with permission | `elementaldragon.fragment` | Command works |
| Admin commands | `elementaldragon.admin` | All admin commands available |
| Default permissions | Op or default | Basic features available |

## Automated Testing

### Unit Tests

The plugin includes comprehensive unit tests in `src/test/java/org/cavarest/elementaldragon/unit/`. These tests verify individual component behavior without requiring a running Minecraft server.

**Test Files Overview:**

- `LightningAbilityTest.java` - Tests lightning targeting logic, damage calculation, sequential strikes, and target switching. Mocks player and entity classes to test targeting behavior in various scenarios.

- `HudManagerTest.java` - Tests message display, action bar updates, and player notification systems. Verifies that cooldown messages appear at correct times and that formatting is consistent.

- `FragmentManagerTest.java` - Tests fragment equipping, unequipping, state management, and passive effect application. Covers all four fragment types and transition logic.

- `FragmentAbilityTest.java` - Tests the specific properties of each fragment type including ability cooldowns, effect durations, and passive bonuses.

- `AbilityManagerTest.java` - Tests coordination between abilities, cooldown tracking, and player state management.

- `SimpleMathTest.java` - Basic utility tests for any mathematical functions used in the plugin.

Each test class follows JUnit 5 conventions with `@Test` annotations for test methods and `@BeforeEach` for setup. Mockito is used extensively to mock Bukkit API classes, allowing tests to run without a full server environment.

**Writing New Unit Tests:**

When adding new functionality, create corresponding unit tests following these patterns:

```java
@Test
void testFragmentEquipShowsCooldowns() {
    // Setup - create mocks and prepare test data
    Player mockPlayer = mock(Player.class);
    when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());

    // Execute the method being tested
    fragmentManager.equipFragment(mockPlayer, FragmentType.BURNING);

    // Verify the expected behavior
    verify(mockPlayer).sendMessage(contains("Equipped Burning Fragment"));
    verify(mockPlayer).sendMessage(contains("60s"));
}
```

Use descriptive test names that explain what is being tested. Test one behavior per method. Use Mockito's `verify` to confirm interactions and `when` to set up mock behaviors.

### Integration Tests (YAML Stories)

YAML-driven integration tests provide high-level scenario testing. Located in `src/test/resources/integration-stories/`, these files describe test scenarios in a human-readable format.

**Available YAML Test Files:**

- `lightning-ability-test.yaml` - Tests the complete lightning command execution flow, from command registration through ability execution and cooldown initiation.

- `cooldown-test.yaml` - Tests cooldown behavior including duration, persistence, and death clearing.

- `dragon-egg-requirement-test.yaml` - Verifies that the dragon egg offhand requirement is enforced correctly.

- `entity-removal-test.yaml` - Tests target switching when entities are removed during ability execution.

- `lightning-strike-test.yaml` - Validates the mechanics of individual lightning strikes including damage and targeting.

- `plugin-commands-test.yaml` - Tests command registration and basic command handling.

The PILAF framework (PaperMC Integration Testing Framework) parses these YAML files and executes the described scenarios against a running Minecraft server. The framework supports multiple backends including RCON, Mineflayer, and HeadlessMc.

### GitHub Actions CI/CD

Automated testing is performed on every push and pull request through GitHub Actions workflows defined in `.github/workflows/`. The workflow runs on Java 21 and Java 22 to ensure cross-version compatibility.

**Workflow Steps:**

1. Checkout repository with submodule initialization
2. Set up Java 21 and Java 22 environments
3. Run `./gradlew build` to compile the plugin
4. Run `./gradlew test` to execute unit tests
5. Upload JAR artifacts for inspection
6. Optionally run integration tests against Docker server

The workflow configuration ensures that changes are validated before merging and that the plugin remains compatible with multiple Java versions.

## Debug Commands

### In-Game Debug Commands

Use these commands to diagnose plugin behavior while playing:

```markdown
/ed info              # Plugin status, version, and configuration
/ed status            # Loaded components and their states
/ed reload            # Reload configuration (requires admin)
/ed reset <player>    # Reset a player's cooldowns (admin only)
/ed version           # Display plugin version
```

The `/ed info` command is particularly useful as it displays the plugin version, enabled features, and any configuration values that may affect behavior.

### Server Console Debug

For deeper debugging, access the server console directly:

```bash
# View real-time logs
docker logs -f papermc-elementaldragon

# Enable debug logging
# Add 'debug: true' to config.yml and reload

# Check for plugin-specific output
docker logs papermc-elementaldragon 2>&1 | grep -i elementaldragon
```

### RCON Debug

RCON allows you to send commands to the server remotely:

```bash
# Using rcon-cli (install with: npm install -g rcon-cli)
rcon-cli -p 25575 -a dragon123 "ed info"
rcon-cli -p 25575 -a dragon123 "ed status"
rcon-cli -p 25575 -a dragon123 "lightning 1"

# Using netcat
echo -e "dragon123\n/ed info" | nc localhost 25575
```

The default RCON password is `dragon123` as configured in the Docker Compose files. Change this in production environments.

### Code-Level Debugging

For debugging during development, use these approaches:

1. **Verbose Logging**: Add detailed log statements to track execution flow
   ```java
   plugin.getLogger().info("Lightning ability executed for player: " + player.getName());
   ```

2. **BukkitRunnable for Delayed Execution**: Useful for debugging timing issues
   ```java
   new BukkitRunnable() {
       @Override
       public void run() {
           // Code to debug after delay
       }
   }.runTaskLater(plugin, 20L); // 1 second delay
   ```

3. **Stack Trace Analysis**: Catch and log exceptions
   ```java
   try {
       // Code that might fail
   } catch (Exception e) {
       plugin.getLogger().severe("Error: " + e.getMessage());
       e.printStackTrace();
   }
   ```

## Troubleshooting

### Plugin Won't Load

If the plugin fails to load when the server starts, check these common issues:

1. **Server Console Errors**: Review the console output for error messages during startup. The plugin logs its initialization status, and any failures will appear there.

2. **Java Version**: Verify Java 21 is installed and selected. Run `java -version` and ensure the output shows version 21.x.

3. **Plugin.yml Syntax**: Ensure `plugin.yml` is valid YAML with correct indentation. Use a YAML validator to check for syntax errors.

4. **JAR Placement**: Verify the JAR file is in `server-plugins/` and not in a subdirectory.

5. **Dependency Conflicts**: If other plugins are installed, temporarily remove them to check for conflicts.

### Lightning Not Working

When the lightning ability fails to trigger:

1. **Dragon Egg Check**: Verify the player has a dragon egg in their offhand slot (the slot to the left of the armor bar).

2. **Permission Check**: Ensure the player has the `elementaldragon.lightning` permission. Default is true for all players, but check if it was revoked.

3. **Cooldown Verification**: Confirm the 60-second cooldown has expired. Use `/ed info` to check cooldown status.

4. **Target Detection**: Ensure hostile mobs are within range and line of sight. Lightning only strikes hostile mobs the player can see.

5. **World Guard/Plot Plugins**: Check if other plugins are blocking lightning strikes or entity damage.

### Fragment Not Equipping

If fragments fail to equip:

1. **Permission Check**: Verify the `elementaldragon.fragment` permission is granted.

2. **Fragment Name**: Ensure the fragment name matches exactly (`burning`, `agility`, `immortal`, `corrupted`).

3. **Cooldown Status**: Some fragments may have cooldowns that prevent immediate use after switching.

4. **Chat Formatting**: Some chat mods may interfere with command parsing. Try in vanilla Minecraft.

### Cooldown Not Persisting

When cooldowns reset unexpectedly:

1. **Player Data Save**: Verify the server is properly saving player data. Some server configurations may not persist custom data.

2. **Event Listeners**: Check that `PlayerJoinEvent` is firing and loading cooldowns correctly. Review the `AbilityManager` implementation.

3. **HashMap Persistence**: The cooldown storage uses an in-memory HashMap. This is lost on server restart unless persisted to disk.

4. **Death Handling**: Confirm that the death event listener is clearing cooldowns correctly.

### Common Error Messages

| Error | Cause | Solution |
|-------|-------|---------- |
| "Cannot invoke method on null object" | Null reference in code | Check Java version, update to Java 21 |
| "IllegalArgumentException: Recipe given invalid items" | Invalid recipe ingredients | Verify recipe components in CraftingManager |
| "NoClassDefFoundError" | Missing dependency | Run `./gradlew build` to download dependencies |
| "Connection refused" on connect | Server not running | Ensure Docker container is running |
| "Outdated client" | Minecraft version mismatch | Use matching client version (1.21.x) |

### Performance Issues

If the plugin causes server lag:

1. **Particle Effects**: Reduce particle counts in configuration if too many particles are causing lag.

2. **Targeting Performance**: Large numbers of entities can slow targeting. Consider reducing range in dense areas.

3. **Memory Usage**: Monitor with `/ed status` to check plugin memory usage. Restart server if usage grows abnormally.

4. **Concurrent Abilities**: Multiple players using abilities simultaneously may cause temporary lag. This is normal under high load.

## Testing Checklist

### Pre-Release Testing

Before releasing a new version, verify all of the following:

- [ ] All unit tests pass (`./gradlew test` succeeds with 100% pass rate)
- [ ] Plugin builds successfully without errors (`./build.sh` completes)
- [ ] JAR file created in `build/libs/` with correct naming
- [ ] Docker server starts without errors in console
- [ ] Lightning ability works (3 strikes, correct damage, targeting)
- [ ] All four fragments equip and use abilities correctly
- [ ] Fragment cooldowns display and function properly
- [ ] Passive effects apply and remove correctly
- [ ] Crafting recipes produce expected items
- [ ] Chronicle/lore system unlocks pages correctly
- [ ] Command aliases work (`/ability` for `/lightning`, `/ed` for `/elementaldragon`)
- [ ] Permission system blocks unauthorized access
- [ ] Cooldowns persist across logout/login
- [ ] Death clears cooldowns as expected
- [ ] Edge cases handled (no targets, no dragon egg, etc.)
- [ ] No errors in server console during testing
- [ ] Documentation updated for new features

### Performance Testing

- [ ] No server lag during lightning strikes
- [ ] No lag during fragment ability execution
- [ ] Particle effects don't cause FPS drops
- [ ] Memory usage stays under 100MB
- [ ] Server responds normally under concurrent player load

### Compatibility Testing

- [ ] Works with Java 21
- [ ] Works with Java 22 (if supported)
- [ ] Compatible with PaperMC 1.21.8 through 1.21.11
- [ ] No conflicts with common plugins (WorldGuard, EssentialsX, etc.)

## Code Testing Guidelines

### Writing New Unit Tests

When extending the plugin with new features, write corresponding unit tests. Follow these guidelines:

1. **Test Class Location**: Place new test classes in `src/test/java/org/cavarest/elementaldragon/unit/` following the naming pattern `{Component}Test.java`.

2. **Annotations**: Use JUnit 5 annotations:
   ```java
   import org.junit.jupiter.api.Test;
   import org.junit.jupiter.api.BeforeEach;

   @Test
   void testDescription() { }

   @BeforeEach
   void setup() { }
   ```

3. **Mocking**: Use Mockito for Bukkit API classes:
   ```java
   import static org.mockito.Mockito.*;

   Player mockPlayer = mock(Player.class);
   when(mockPlayer.getName()).thenReturn("TestPlayer");
   ```

4. **Test Isolation**: Each test should be independent. Use `@BeforeEach` to set up fresh mocks for each test.

5. **Descriptive Names**: Use method names that explain what is tested:
   ```java
   @Test
   void testDragonEggRequirementPreventsAbilityUseWithoutEgg() { }

   @Test
   void testCooldownPersistsAcrossPlayerJoinEvent() { }
   ```

6. **One Behavior Per Test**: Don't test multiple behaviors in a single test method. Split into separate tests.

7. **Asserts**: Use meaningful assertions:
   ```java
   assertEquals(3, strikes, "Lightning should strike 3 times");
   assertTrue(cooldown > 0, "Cooldown should be positive");
   ```

### Writing YAML Integration Tests

Create new YAML stories in `src/test/resources/integration-stories/` following the existing format:

```yaml
name: New Feature Test
description: Tests the new feature behavior
trigger: |
  The player uses the new ability
steps:
  - name: Equip the ability
    command: /newability equip
    expect:
      - "Equipped.*"

  - name: Use the ability
    command: /newability 1
    expect:
      - "Ability activated"
      - "Effect visible"
```

Include both success and failure cases. Test edge conditions and error handling.

### Running Tests in IDE

For development in IntelliJ IDEA or VS Code:

**IntelliJ IDEA:**
1. Open the project as a Gradle project
2. Ensure Java 21 SDK is configured in Project Structure
3. Run test classes or methods directly from the editor
4. View results in the Run tool window

**VS Code:**
1. Install Java extensions (Extension Pack for Java)
2. Configure Java 21 in settings
3. Use the Java Test Runner to execute tests
4. Check the Test Results view for outcomes

Both IDEs support running tests with coverage when the appropriate plugins are installed.

## Additional Resources

For more information about testing and development:

- **PaperMC API Documentation**: https://docs.papermc.io/paper/dev
- **JUnit 5 Documentation**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **Project README**: `README.md` for overall project information
- **Implementation Guide**: `docs/implementation.md` for architecture details
- **Player Documentation**: `docs/user/README.md` for end-user guides

This testing documentation should provide comprehensive coverage for all testing needs. Update this document when adding new features or changing testing procedures.
