# PILAF Framework Development Guide

## Overview

The PILAF (Paper Integration Layer for Automation Functions) framework provides a powerful, YAML-driven testing solution for Minecraft Paper plugin development. This guide covers everything developers need to know to effectively use PILAF for development and testing.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Framework Architecture](#framework-architecture)
3. [Writing YAML Test Stories](#writing-yaml-test-stories)
4. [Running Tests](#running-tests)
5. [Extending the Framework](#extending-the-framework)
6. [Troubleshooting](#troubleshooting)
7. [Best Practices](#best-practices)

## Quick Start

### Prerequisites

- Java 18+
- Maven 3.6+
- Minecraft Paper server
- Docker (for full integration testing)

### Basic Setup

1. **Framework Integration**: PILAF is already integrated as a git submodule in `lib/pilaf/`

2. **Configuration**: Test stories are located in `src/test/resources/integration-stories/`

3. **Run Tests**: Execute the integration test suite:
```bash
mvn test -Dtest=YamlIntegrationTest
```

## Framework Architecture

### Core Components

```
Plugin Repository (DragonEgg Lightning)
├── Plugin Code (src/main/java/)
├── YAML Test Stories (src/test/resources/integration-stories/)
├── Integration Test (src/test/java/)
│   └── YamlIntegrationTest.java
└── PILAF Framework (lib/pilaf/ - Git Submodule)
    ├── PilafRunner - CLI execution tool
    ├── YamlStoryParser - YAML parsing engine
    ├── TestOrchestrator - Story execution engine
    ├── Backend Factory - Backend management
    │   ├── RconBackend - Server command execution
    │   └── MineflayerBackend - Player interaction
    └── TestReporter - Comprehensive reporting
```

### Data Flow

1. **YAML Discovery**: Find test stories in resources directory
2. **Parsing**: Convert YAML to PILAF Action/Assertion models
3. **Execution**: TestOrchestrator executes actions via backend
4. **Reporting**: Generate comprehensive test reports with evidence

## Writing YAML Test Stories

### Story Structure

```yaml
name: "Test Story Name"
description: "Detailed description of what this test validates"
backend: "rcon"  # Backend type: rcon, mineflayer

setup:
  # Actions executed before main test steps
  - action: "server_command"
    command: "spawn zombie 5.0 64.0 5.0 {CustomName:\"TestZombie\"}"
    name: "Spawn test entity"

steps:
  # Main test actions
  - action: "player_command"
    player: "TestPlayer"
    command: "/ability 1"
    name: "Execute lightning ability"

cleanup:
  # Actions executed after test completion
  - action: "remove_entities"
    pattern: "TestZombie"
    name: "Clean up test entities"
```

### Supported Action Types

| Action Type | Purpose | Required Parameters |
|-------------|---------|-------------------|
| `server_command` | Execute server command | `command` |
| `player_command` | Execute player command | `player`, `command` |
| `give_item` | Give item to player | `player`, `item`, `count` |
| `equip_item` | Equip item to player | `player`, `item`, `slot` |
| `move_player` | Move player location | `player`, `destination` |
| `wait` | Pause execution | `duration` (milliseconds) |
| `spawn_entity` | Spawn game entity | `type`, `location` |
| `remove_entities` | Remove entities by pattern | `pattern` |

### Assertion Types

| Assertion Type | Purpose | Parameters |
|---------------|---------|------------|
| `entity_health` | Check entity health | `entity`, `condition`, `value` |
| `entity_exists` | Check if entity exists | `entity` |
| `player_inventory` | Check player inventory | `player`, `item`, `slot` |
| `plugin_command` | Check plugin command response | `command`, `condition` |

### Conditions

- `EQUALS` - Exact match
- `NOT_EQUALS` - Not equal to
- `LESS_THAN` - Less than value
- `GREATER_THAN` - Greater than value
- `LESS_THAN_OR_EQUALS` - Less than or equal
- `GREATER_THAN_OR_EQUALS` - Greater than or equal

### Example: Lightning Ability Test

```yaml
name: "Lightning Ability Test"
description: "Test dragon egg lightning ability on entities"

setup:
  - action: "server_command"
    command: "spawn zombie 5.0 64.0 5.0 {CustomName:\"TargetZombie\"}"
    name: "Spawn target entity"

  - action: "server_command"
    command: "op TestPlayer"
    name: "Grant operator permissions"

  - action: "give_item"
    player: "TestPlayer"
    item: "dragon_egg"
    count: 1
    name: "Provide dragon egg"

  - action: "equip_item"
    player: "TestPlayer"
    item: "dragon_egg"
    slot: "mainhand"
    name: "Equip dragon egg"

steps:
  - action: "move_player"
    player: "TestPlayer"
    destination: "relative"
    destination: "^ ^ ^3"
    name: "Position player near target"

  - action: "player_command"
    player: "TestPlayer"
    command: "/ability 1"
    name: "Execute lightning ability"

  - action: "wait"
    duration: 2000
    name: "Wait for lightning effect"

  - assertion: "entity_health"
    entity: "TargetZombie"
    condition: "LESS_THAN"
    value: 20.0
    name: "Verify entity took damage"

cleanup:
  - action: "remove_entities"
    pattern: "TargetZombie"
    name: "Remove test entity"
```

## Running Tests

### Individual Test Execution

```bash
# Run specific test story
java -cp lib/pilaf/target/pilaf-0.1.0.jar org.cavarest.pilaf.PilafRunner \
  --backend rcon \
  --rcon-host localhost \
  src/test/resources/integration-stories/lightning-ability-test.yaml
```

### Integration Test Suite

```bash
# Run all YAML integration tests
mvn test -Dtest=YamlIntegrationTest

# Run with verbose output
mvn test -Dtest=YamlIntegrationTest -Dverbose=true

# Run with custom RCON configuration
mvn test -Dtest=YamlIntegrationTest -Drcon.host=192.168.1.100
```

### Test Reports

After execution, comprehensive reports are generated in `target/pilaf-reports/`:

- **HTML Report**: Interactive web-based test results
- **JSON Report**: Machine-readable test results
- **JUnit XML**: CI/CD integration format
- **Text Report**: Human-readable summary

## Extending the Framework

### Adding New Action Types

1. **Add to Action.java**:
```java
public enum ActionType {
    // Existing types...
    NEW_ACTION_TYPE
}
```

2. **Update YamlStoryParser.java**:
```java
private Action.ActionType parseActionType(String type) {
    // Existing cases...
    case "new_action_type": return Action.ActionType.NEW_ACTION_TYPE;
}
```

3. **Implement in Backend**:
```java
// In PilafBackend interface
void executeNewAction(Action action);

// In RconBackend implementation
@Override
public void executeNewAction(Action action) {
    // Implementation logic
}
```

4. **Update TestOrchestrator**:
```java
// Add case in executeAction method
case NEW_ACTION_TYPE:
    backend.executeNewAction(action);
    break;
```

### Adding New Assertion Types

1. **Add to Assertion.java**:
```java
public enum AssertionType {
    // Existing types...
    NEW_ASSERTION_TYPE
}
```

2. **Update YamlStoryParser.java**:
```java
private Assertion.AssertionType parseAssertionType(String t) {
    // Existing cases...
    case "new_assertion": return Assertion.AssertionType.NEW_ASSERTION_TYPE;
}
```

3. **Implement Backend**:
```java
@Override
public boolean checkNewAssertion(Assertion assertion) {
    // Implementation logic
    return true; // or false based on assertion
}
```

### Custom Backend Implementation

```java
public class CustomBackend implements PilafBackend {
    @Override
    public void initialize() throws Exception {
        // Custom initialization
    }

    @Override
    public void executeAction(Action action) {
        // Custom action execution
    }

    @Override
    public boolean executeAssertion(Assertion assertion) {
        // Custom assertion logic
        return true;
    }
}
```

## Troubleshooting

### Common Issues

**1. Backend Connection Failed**
```
Error: Failed to connect to RCON server
```
- Check server is running
- Verify RCON port and password
- Ensure firewall allows connection

**2. Unknown Action Type**
```
Error: Unknown action: custom_action
```
- Check action type spelling
- Verify action is supported in current version
- May need to implement custom action

**3. Player Not Connected**
```
Error: Player TestPlayer not connected
```
- Ensure player is connected before executing commands
- Use proper player name from server

**4. Entity Not Found**
```
Error: Entity TestZombie not found
```
- Verify entity was spawned successfully
- Check entity name matches exactly
- Ensure entity hasn't been removed

### Debugging Tips

1. **Enable Verbose Logging**:
```bash
mvn test -Dtest=YamlIntegrationTest -Dverbose=true
```

2. **Check Backend Logs**: Review server logs for command execution results

3. **Step-by-Step Execution**: Run stories individually to isolate issues

4. **State Verification**: Use assertions to verify intermediate states

### Performance Optimization

1. **Parallel Execution**: Use multiple backends for independent tests
2. **Resource Cleanup**: Always include cleanup steps
3. **Wait Times**: Use appropriate wait durations for async operations
4. **Entity Management**: Clean up entities promptly to avoid server load

## Best Practices

### Test Design

1. **Single Responsibility**: Each test should validate one specific behavior
2. **Clear Naming**: Use descriptive names for tests and steps
3. **Independent Tests**: Tests should not depend on each other
4. **Complete Cleanup**: Always clean up created entities and items

### YAML Structure

1. **Consistent Format**: Follow the standard YAML story structure
2. **Descriptive Comments**: Add comments explaining complex steps
3. **Reasonable Wait Times**: Use appropriate wait durations
4. **Proper Assertions**: Validate expected outcomes

### Error Handling

1. **Graceful Degradation**: Handle missing dependencies
2. **Informative Assertions**: Provide clear failure messages
3. **Recovery Actions**: Include cleanup in error scenarios
4. **Log Everything**: Capture all relevant information

### Performance

1. **Minimize Server Load**: Clean up entities promptly
2. **Optimize Wait Times**: Balance reliability with speed
3. **Resource Management**: Avoid creating unnecessary resources
4. **Batch Operations**: Group related operations when possible

### Maintenance

1. **Version Control**: Keep YAML stories in version control
2. **Regular Updates**: Update tests when plugin functionality changes
3. **Documentation**: Document any custom extensions
4. **Testing**: Validate changes with existing tests

## Next Steps

For detailed examples and advanced usage patterns, see:

- [Testing Guide](../testing/testing-guide.md)
- [Plugin Development Guide](PLUGIN_DEVELOPMENT.md)
- [Framework Extensions](FRAMEWORK_EXTENSIONS.md)
