---
title: Administration
parent: Administration
nav_order: 1
has_children: true
---

# Administration Guide

This section provides comprehensive documentation for server administrators running the Elemental Dragon plugin.

## Overview

Elemental Dragon is designed for easy deployment with minimal configuration required. The plugin supports:

- **Docker deployment** for quick setup and development
- **Manual installation** on existing PaperMC servers
- **Configuration via config.yml** for all settings
- **Operator commands** for server management
- **CI/CD integration** for automated testing and deployment

## Quick Reference

| Task | Guide |
|------|-------|
| Install plugin | [Installation](cicd/) |
| Configure operators | [Commands Reference](commands/) |
| Docker deployment | [Docker Guide](docker/) |
| Testing | [Testing Guide](testing/) |
| CI/CD setup | [CI/CD Guide](cicd/) |

## Contents

### Deployment

- [Docker Deployment](docker/) - Complete Docker setup guide with offline-mode operator configuration
- [CI/CD Pipeline](cicd/) - Automated testing and deployment workflows

### Configuration

- [Commands Reference](commands/) - Complete admin command reference including operator setup
- [Testing Guide](testing/) - Unit testing and manual testing procedures

## Key Concepts

### Offline Mode Operators

The Docker setup uses **offline mode** for development convenience. This requires special UUID handling:

- Operators configured via `OFFLINE_OPS` environment variable
- Custom entrypoint generates correct offline-mode UUIDs
- See [Docker Guide](docker/#offline-mode-operator-setup) for details

### Plugin Configuration

All plugin settings are in `config.yml`, auto-generated on first run:

```yaml
# Cooldown settings (seconds)
lightning-cooldown: 10
fire-ability1-cooldown: 15

# Ability damage values
lightning-damage: 5
fireball-damage: 3

# HUD settings
enable-hud: true
hud-update-interval: 20  # ticks
```

### Administrative Commands

Key admin commands (require `elementaldragon.admin` permission):

```bash
/ed give <player-ref> <ingredients|equipment> <element>  # Give items to players
/ed info player <player-ref>  # Show player's elemental status
/ed info list  # List all players' status
/ed setcooldown <player> <element> <ability> <seconds>  # Set player cooldowns
/ed clearcooldown <player> [element]  # Clear cooldowns
/ed getcooldown <player>  # Get player's cooldowns
/ed setglobalcooldown <element> <ability> <seconds>  # Set global cooldowns
/ed getglobalcooldown  # View global cooldown configuration
/ed setglobalcountdownsym <style> [width]  # Set HUD countdown style
```

See [Commands Reference](commands/) for complete documentation.

## Docker Development Workflow

```bash
# Clone and build
git clone https://github.com/cavarest/elemental-dragon.git
cd elemental-dragon
./build.sh

# Start development server
./start-server.sh --rebuild

# View logs
docker logs -f papermc-elementaldragon

# Stop server
./stop-server.sh
```

See [Docker Deployment Guide](docker/) for comprehensive documentation.

## Production Deployment

For production servers:

1. **Enable online mode** for authentication: `ONLINE_MODE=true`
2. **Use strong RCON password**: Set via `RCON_PASSWORD` environment variable
3. **Configure operators**: Use `/op` command instead of `OFFLINE_OPS`
4. **Memory allocation**: Increase to `4G` for larger servers
5. **Backup strategy**: Implement regular world data backups

---

> [!IMPORTANT]
> Production servers should enable online mode (`ONLINE_MODE=true`) for proper authentication. See [Docker Guide](docker/#production-deployment) for details.
