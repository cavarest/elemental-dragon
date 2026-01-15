---
title: Development
parent: Development
nav_order: 1
has_children: true
---

# Development Guide

This section provides comprehensive documentation for contributors and developers working on the Elemental Dragon plugin.

## Project Overview

Elemental Dragon is a **PaperMC plugin** (Java 21) that demonstrates professional software architecture:

- **206 unit tests** with comprehensive coverage
- **SOLID principles** and design patterns throughout
- **Template Method pattern** for commands (63% code reduction)
- **Command pattern** for admin subcommands (76% code reduction)
- **Single source of truth** pattern for fragment metadata

## Quick Start for Developers

```bash
# Clone repository
git clone https://github.com/cavarest/elemental-dragon.git
cd elemental-dragon

# Build plugin
./build.sh

# Run tests
./gradlew test

# Start development server
./start-server.sh --rebuild
```

## Development Workflow

1. **Create feature branch**: `git checkout -b feature/my-feature`
2. **Make changes**: Edit code and add tests
3. **Run tests**: `./gradlew test` to ensure all 206 tests pass
4. **Build JAR**: `./build.sh` to create plugin JAR
5. **Test manually**: `./start-server.sh` to test with real Minecraft client
6. **Commit**: Use semantic commit messages (feat:, fix:, docs:, etc.)
7. **Push**: Push to GitHub and create pull request

## Contents

### Getting Started

- [Testing](testing/) - Unit testing and manual testing procedures
- [Testing Frameworks](frameworks/) - PILAF and other testing frameworks
- [HeadlessMC Testing](headlessmc/) - Automated testing without GUI

### Architecture Overview

Key architectural patterns and systems:

- **Single Source of Truth**: Fragment classes own all metadata
- **Template Method Pattern**: `AbstractFragmentCommand` provides structure
- **Command Pattern**: `ElementalDragonCommand` uses subcommands
- **Dependency Injection**: Managers injected via constructors

For detailed architecture, see the project README.md.

## Key Architectural Patterns

### Single Source of Truth

Fragment classes own ALL their metadata. Commands, items, and recipes query fragments dynamically:

```java
// Fragment owns metadata
public interface Fragment {
    String getName();
    Material getMaterial();
    NamedTextColor getThemeColor();
    AbilityDefinition getAbility1();
    // ...
}

// Command queries fragment dynamically
public class FireCommand extends AbstractFragmentCommand {
    @Override
    protected String getAbility1Name() {
        return getFragment().getAbility1().getName();  // Query at runtime
    }
}
```

**Benefits**:
- Zero duplication across commands, items, and recipes
- Changes to Fragment automatically propagate everywhere
- Easier to add new fragments (~100 lines per command)

### Template Method Pattern

`AbstractFragmentCommand` provides structure, concrete commands provide metadata:

```java
public abstract class AbstractFragmentCommand extends AbstractCommand {
    // Template methods (final)
    public final boolean onCommand(...) { /* 400 lines of structure */ }

    // Abstract methods for subclasses
    protected abstract String getCommandName();
    protected abstract FragmentType getFragmentType();
    protected abstract String getAbility1Name();
    // ...
}
```

**Benefits**:
- Consistent command behavior across all fragment commands
- 63% code reduction (400 lines in base, ~100 per command)
- Easy to add new fragment commands

### Command Pattern

`ElementalDragonCommand` uses subcommands for admin functionality:

```java
public class ElementalDragonCommand extends AbstractCommand {
    private final Map<String, Subcommand> subcommands = new HashMap<>();

    @Override
    protected void registerSubcommands() {
        registerSubcommand("give", new GiveSubcommand(...));
        registerSubcommand("info", new InfoSubcommand(...));
        registerSubcommand("cooldown", new CooldownSubcommand(...));
        // ...
    }
}
```

**Benefits**:
- 76% code reduction in main admin command (893 → 211 lines)
- Easy to add new subcommands
- Clear separation of concerns

### Dependency Injection

Managers are injected via constructors:

```java
public FireCommand(ElementalDragon plugin, FragmentManager fragmentManager) {
    super(plugin, fragmentManager);
}
```

**Benefits**:
- Testable code (can mock dependencies)
- Clear dependencies
- Easier to maintain

## Code Organization

### Package Structure

```
org.cavarest.elementaldragon/
├── ability/          # Core lightning ability system
├── fragment/         # Fragment system (4 elements)
├── command/          # Command layer
│   ├── base/         # Command pattern infrastructure
│   ├── subcommands/  # Admin subcommands
│   ├── display/      # Formatters for output
│   └── util/         # Player resolution utilities
├── cooldown/         # Unified cooldown system
├── crafting/         # Recipe definitions
├── item/             # Item creation
├── hud/              # Player feedback
├── visual/           # Particle effects
├── audio/            # Sound effects
└── lore/             # Chronicle system
```

### Adding New Features

#### Add a New Fragment

1. Create Fragment class extending `AbstractFragment` (~150 lines)
2. Register in `FragmentManager` (~5 lines)
3. Create Command class extending `AbstractFragmentCommand` (~100 lines)
4. Register command in `ElementalDragon.java` (~5 lines)
5. Add to `plugin.yml` (~5 lines)

**Total**: ~265 lines for complete new fragment with 2 abilities

#### Add an Admin Subcommand

1. Create class in `command/subcommands/` implementing `Subcommand` (~50-100 lines)
2. Register in `ElementalDragonCommand.registerSubcommands()` (~3 lines)

## Testing Philosophy

### Unit Tests (206 tests)

- Located in `src/test/java/org/cavarest/elementaldragon/unit/`
- Use Mockito for dependency mocking
- Focus on business logic, not Bukkit API calls
- Run with: `./gradlew test`

### Manual Testing (Docker-based)

```bash
# Start Paper server in Docker
./start-server.sh

# Connect Minecraft client to localhost:25565
# Test commands interactively
```

### Integration Tests (PILAF framework)

- YAML-driven test scenarios in `src/test/resources/integration-stories/`
- Currently disabled (framework WIP)
- Will provide end-to-end testing without real Minecraft client

## Performance Considerations

- **HUD updates**: 1/second (not 20/second) for performance
- **Cooldown lookups**: O(1) HashMap
- **Fragment queries**: Dependency injection (not lookup on every call)
- **Docker caching**: Use cached layers unless `--rebuild` specified

## Coding Standards

- **Java 21** features (records, pattern matching, etc.)
- **SOLID principles** throughout
- **Design patterns** where appropriate
- **Comprehensive tests** for all business logic
- **Clear documentation** in code comments

## Common Tasks

### Modify Ability Cooldowns

```java
// In Fragment class
@Override
public AbilityDefinition getAbility1() {
    return new AbilityDefinition(
        "Dragon's Wrath",
        "Launch a devastating fireball",
        30  // cooldown in seconds
    );
}
```

### Add New Particle Effect

```java
// In ParticleFX class
public static void spawnCustomEffect(Location location, Player player) {
    player.spawnParticle(Particle.FLAME, location, 10, 0.5, 0.5, 0.5, 0.02);
}
```

### Debug Server Issues

```bash
# View server logs
docker logs -f papermc-elementaldragon

# Access RCON
docker exec -it papermc-elementaldragon rcon-cli

# Check plugin loaded
docker exec papermc-elementaldragon rcon-cli plugins
```

## Resources

- **PaperMC API**: [https://papermc.io/javadocs/](https://papermc.io/javadocs/)
- **Bukkit Wiki**: [https://bukkit.fandom.com/wiki/](https://bukkit.fandom.com/wiki/)
- **Minecraft Wiki**: [https://minecraft.fandom.com/](https://minecraft.fandom.com/)
- **Project Repository**: [github.com/cavarest/elemental-dragon](https://github.com/cavarest/elemental-dragon)

---

> [!NOTE]
> New contributors should review the README.md and Architecture sections for comprehensive project documentation.
