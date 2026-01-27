## New Features

### Combat Improvements (Issue #28 Balance Changes)

**Agility Fragment - Draconic Surge (`/agile 1`)**
- **Toggle Behavior**: Type `/agile 1` again while dashing to halt immediately
- **Collision Damage**: Deals 3 hearts damage to entities you hit during the dash
  - Ignores armor - pure damage
  - 2-block collision radius
  - Each entity can only be hit once per dash

**Burning Fragment - Dragon's Wrath (`/fire 1`)**
- **Homing Fireball**: Fireball now seeks targets for 0.5 seconds
  - Better hit rate against moving targets
  - 50-block targeting range
  - Still bypasses armor

**Lightning Ability (`/lightning 1`)**
- **Instant Cast**: Lightning strikes immediately (was 0.5s delay)

**Agility Fragment - Wing Burst (`/agile 2`)**
- **Improved Velocity**: Knockback increased to 20 blocks (more reliable)

## Bug Fixes

- Fixed `/equip` command allowing admins to bypass the one-fragment limit
- Fixed Wing Burst velocity too low due to friction
- Fixed HUD not updating on inventory slot changes

## For Players

**One-Fragment Limit Enforcement**
- You can now only carry ONE fragment at a time (inventory + equipped)
- Clear error messages tell you which fragment to drop first

**Improved Combat Feel**
- Draconic Surge can now be stopped mid-dash by recasting
- Dragon's Wrath fireballs track targets better
- Lightning strikes instantly
- Wing Burst pushes enemies further

## For Server Operators

**Internal Improvements**
- Consolidated duplicate event listeners
- Modernized Bukkit/Paper API usage
- Added DRY helper methods for inventory checking
- Enhanced HUD system with better event handling

**Testing Framework**
- Added Pilaf integration test suite (17 test scenarios)
- Added world profile system for development testing

**Test Coverage**
- Unit Tests: 783 tests passing
- Integration Tests: 17 Pilaf scenarios passing

---

## Installation

1. Download the plugin JAR from the release below
2. Place it in your Paper/Folia server's `plugins/` directory
3. **Install required dependencies**:
   - [ProtocolSidebar](https://jitpack.io/com/github/CatCoderr/ProtocolSidebar/master-SNAPSHOT/ProtocolSidebar-master-SNAPSHOT.jar)
   - [FoliaLib](https://repo.tcoded.com/releases/com/tcoded/FoliaLib/0.5.1/FoliaLib-0.5.1.jar)
4. Restart your server

## Requirements

- Paper 1.21.x or Folia
- Java 21 or higher
- Minecraft 1.21.x

## Upgrading

From previous versions:
- No config changes required
- Existing fragments remain equipped
- No data migration needed
