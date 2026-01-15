---
layout: default
title: Home
nav_order: 1
description: "Elemental Dragon: PaperMC plugin for dragon egg lightning abilities and elemental fragments"
permalink: /
---

# Elemental Dragon Documentation

![Version](https://img.shields.io/badge/Version-1.1.0-green)
![Minecraft](https://img.shields.io/badge/Minecraft-Paper%201.21.8-blue?logo=minecraft)
![Java](https://img.shields.io/badge/Java-21+-ED8B00?logo=openjdk&logoColor=white)
![Tests](https://img.shields.io/badge/Tests-206%20Passing-brightgreen)

Welcome to the Elemental Dragon plugin documentation. This comprehensive PaperMC plugin adds dragon egg lightning abilities and elemental fragments to your Minecraft server.

## Quick Start

Get started in just 5 minutes:

1. **Download** the latest JAR from [releases](https://github.com/cavarest/elemental-dragon/releases/latest)
2. **Install** by placing in your Paper server's `plugins/` directory
3. **Restart** your server
4. **Test** with `/give @p minecraft:dragon_egg` and `/lightning 1`

## Features

### ⚡ Lightning Ability
- **3 Sequential Strikes**: Epic chain lightning with 0.5-second intervals
- **Smart Targeting**: Ray-tracing finds closest hostile mob in view
- **Armor-Bypassing Damage**: 2 hearts per strike, ignores all protection
- **60-Second Cooldown**: Balanced gameplay with persistent cooldowns

### 💎 Elemental Fragment System
Four unique fragments with powerful abilities:

| Fragment | Abilities | Description |
|----------|-----------|-------------|
| **Burning Fragment** 🔥 | Dragon's Wrath, Infernal Dominion | Fire-based destruction |
| **Agility Fragment** 💨 | Draconic Surge, Wing Burst | Speed and movement |
| **Immortal Fragment** 🛡️ | Draconic Reflex, Essence Rebirth | Defense and survival |
| **Corrupted Core** 👁 | Dread Gaze, Life Devourer | Dark manipulation |

### 🔨 Crafting System
- **Heavy Core**: Vanilla Minecraft item found in Ancient Cities
- **Fragment Recipes**: Combine Heavy Core with elemental materials
- **Craft Limits**: 2 max for most fragments, 1 for Corrupted Core

### 📖 Chronicle of the Fallen Dragons
- Discoverable lore book
- Progressive story unlocking
- Achievement tracking

### 🔒 Item Restrictions
- Fragments cannot be stored in containers
- Dropping a fragment withdraws abilities
- Fireproof items with enchanted glow

## Documentation

### [User Guide]({% link user/index.md %})
Complete player guide covering installation, abilities, and gameplay.

- [Installation Guide]({% link user/installation.md %}) - Server setup and configuration
- [Fragments Guide]({% link user/fragments.md %}) - All fragment abilities and how to use them

### [Administration]({% link admin/index.md %})
Server administration, deployment, and configuration.

- [Docker Deployment]({% link admin/docker.md %}) - Complete Docker setup guide
- [Commands Reference]({% link admin/commands.md %}) - Admin commands and operator setup
- [Testing Guide]({% link admin/testing.md %}) - Testing procedures

### [Development]({% link dev/index.md %})
Developer documentation and contribution guidelines.

- [Testing Guide]({% link dev/testing.md %}) - Comprehensive testing documentation

## Command Reference

### Player Commands
```bash
/lightning <1-4>    # Core lightning and fragment abilities
/ability <1-4>      # Alias for /lightning
/fire <args>        # Burning Fragment commands
/agile <args>       # Agility Fragment commands
/immortal <args>    # Immortal Fragment commands
/corrupt <args>     # Corrupted Core commands
/craft <recipe>     # View crafting recipes
/chronicle          # Access lore system
/withdrawability    # Remove equipped abilities (keep item)
```

### Admin Commands
```bash
/elementaldragon <subcommand>   # or /ed
/ed give <player> equipment <element>
/ed setcooldown <player> <element> <ability> <seconds>
/ed getcooldown <player>
/ed clearcooldown <player> [element]
/ed setglobalcooldown <element> <ability> <seconds>
```

## System Requirements

- **Minecraft**: Java Edition 1.21.8+
- **Server**: PaperMC 1.21.8-R0.1+
- **Java**: Version 21 or higher
- **Memory**: Minimum 2GB RAM

## Support

- **GitHub Issues**: [Report bugs](https://github.com/cavarest/elemental-dragon/issues)
- **Documentation**: Browse guides in navigation menu
- **Community**: Join discussions on GitHub

## License

MIT License - Free for personal and commercial use

---

**Ready to transform your server?** [Download the latest release](https://github.com/cavarest/elemental-dragon/releases/latest)