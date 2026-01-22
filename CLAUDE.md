# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Building
```bash
./build.sh              # Build plugin JAR (bundles dependencies)
./gradlew build         # Direct Gradle build
./gradlew slimJar       # Build slim JAR for Hangar (no bundled dependencies)
```

### Testing
```bash
./gradlew test                          # Run all unit tests
./gradlew test --tests "*FragmentTest*" # Run specific test class
./gradlew test --continuous             # Watch mode
./test-integration.sh                   # Run YAML-driven integration tests
./test-plugin.sh                        # Manual plugin testing
```

### Docker Development Server
```bash
./start-server.sh         # Start server (cached image)
./start-server.sh -r      # Rebuild image with latest plugin
./start-server.sh -w      # Fresh world (preserves configs)
./start-server.sh -c      # Full clean reset
./start-server.sh -b      # Show logs in terminal
./stop-server.sh          # Stop server
```

The server runs on ports 25565 (game) and 25575 (RCON). Default user: `posiflow` (auto-op).

## High-Level Architecture

### Single Source of Truth Pattern

The core architecture principle: **Fragment classes own ALL their metadata**. Commands, items, and recipes query Fragments dynamically. This eliminates duplication.

**Fragment Interface** (`fragment/Fragment.java`):
- Defines contract for all fragments
- Provides visual properties (Material, color)
- Provides ability definitions (AbilityDefinition[])
- Provides command metadata (command name, permission node, element name)
- Subclasses: `BurningFragment`, `AgilityFragment`, `ImmortalFragment`, `CorruptedCoreFragment`

**FragmentRegistry** (`fragment/FragmentRegistry.java`):
- Central registration point for all fragment types
- Maps `FragmentType` enum to canonical names used by `CooldownManager`
- Auto-registers fragment event listeners

**AbstractFragment** (`fragment/AbstractFragment.java`):
- Base class implementing Fragment interface
- Provides common functionality: particles, sounds, potion effects
- Subclasses only define fragment-specific behavior

### Command Architecture

**AbstractFragmentCommand** (`command/AbstractFragmentCommand.java`):
- Template Method pattern for all fragment commands
- 100% auto-generated help from Fragment metadata
- Subclasses only pass Fragment instance to constructor

**Fragment Commands** (`command/FireCommand.java`, etc.):
- Each command is ~15 lines (constructor-only)
- Uses `AbstractFragmentCommand` for all behavior
- Queries Fragment for tab completion, aliases, descriptions

### Manager System

The plugin uses multiple managers, each with a specific responsibility:

- **CooldownManager** (`cooldown/CooldownManager.java`): Global cooldown system with persistence
- **FragmentManager** (`fragment/FragmentManager.java`): Equips/unequips fragments, manages ability use
- **AbilityManager** (`ability/AbilityManager.java`): Legacy lightning ability management
- **CraftingManager** (`crafting/CraftingManager.java`): Recipe registration + introspection for `/craft` command
- **HudManager** (`hud/HudManager.java`): ProtocolSidebar-based HUD for cooldowns/fragment status
- **ChronicleManager** (`lore/ChronicleManager.java`): Lore and achievement tracking
- **AchievementManager** (`achievement/AchievementManager.java`): Player achievement system

### Initialization Order (Critical!)

`ElementalDragon.onEnable()` initializes managers in dependency order:
1. CooldownManager (others depend on it)
2. AbilityManager, ChronicleManager, AchievementManager
3. FragmentManager (depends on CooldownManager)
4. HudManager (depends on AbilityManager, FragmentManager, CooldownManager)
5. CraftingManager, CraftedCountManager
6. PlayerPreferenceManager

### Cooldown System

- Cooldowns persist across logout, server restarts, fragment unequip (spam prevention)
- Global cooldowns set via `/ed setglobalcooldown <element> <ability> <seconds>`
- Adjustment formula: `min(current_remaining, new_max)` ensures fairness
- Setting to 0 clears all active player cooldowns for that ability

### Crafting System

**CraftingManager** uses `RecipeData` class for runtime introspection:
- Single source of truth for recipes (no duplication with `/craft` command)
- All fragments require vanilla `HEAVY_CORE` as center ingredient
- `/craft` command displays recipes dynamically from `get*Recipe()` methods

## Release Process

### Creating a Release

1. Create `RELEASE_NOTES.md` from template:
   ```bash
   cp RELEASE_NOTES.md.in RELEASE_NOTES.md
   vim RELEASE_NOTES.md  # Add release notes
   ```

2. Commit and push release notes:
   ```bash
   git add RELEASE_NOTES.md
   git commit -m "chore: add release notes for v1.3.6"
   git push
   ```

3. Trigger release workflow:
   ```bash
   gh workflow run release.yml -f next_version=skip -f release_title="Elemental Dragon v1.3.6"
   ```

### Automated Post-Release

The workflow automatically:
- Builds plugin and creates GitHub Release
- Publishes to Modrinth and Hangar
- Updates CHANGELOG.md with release notes
- Removes RELEASE_NOTES.md
- Commits and pushes changes

### Distribution Workflows

- `distribution.yml`: Publishes to Modrinth and Hangar (triggered by release.yml)
- `build_deploy.yml`: Build and deployment
- `unit-tests.yml`: Unit test runner
- `integration-tests.yml`: YAML-driven integration tests

## Adding a New Fragment

When adding a new fragment, modify these locations:

1. **FragmentType enum** (`fragment/FragmentType.java`): Add enum value
2. **FragmentRegistry** (`fragment/FragmentRegistry.java`): Add `register()` call
3. **CraftingManager** (`crafting/CraftingManager.java`): Add recipe registration + introspection method
4. **ElementalItems** (`item/ElementalItems.java`): Add item creation method
5. **ElementalDragon** (`ElementalDragon.java`): Register command
6. **plugin.yml**: Add permission node

The command class itself is trivial (e.g., `FireCommand.java` is only ~20 lines).

## Project Configuration

- **Version**: Defined in `gradle.properties` (`project.version=1.3.6`)
- **Paper API**: 1.21.11-R0.1-SNAPSHOT
- **Java**: 21
- **Dependencies**: ProtocolSidebar, FoliaLib, Adventure MiniMessage

## Testing

- **Unit tests**: JUnit 5 + Mockito, 741 tests passing
- **Integration tests**: YAML-driven scenarios in `src/test/resources/stories/`
- **Coverage**: 29% (Bukkit API limits integration test coverage)
- **Reports**: `build/reports/tests/test/index.html`, `build/reports/jacoco/test/html/index.html`
