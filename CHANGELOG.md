# Changelog

All notable changes to the Elemental Dragon plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### [1.3.6] - 2026-01-21
# Release v1.3.6

## New Features

### Automated Release Process
- Fully automated release workflow with CHANGELOG.md updates
- Post-release cleanup now automatic (no manual steps required)
- RELEASE_NOTES.md template system for structured release notes

### Improved Distribution
- Release notes now properly published to Hangar
- Changelog extracted from RELEASE_NOTES.md instead of regex parsing
- More reliable and maintainable release process

## Technical Changes

### Release Workflow Improvements
- Fixed workflow permissions (`actions: write` for triggering workflows)
- Fixed Modrinth/Hangar changelog display
- Separated template (`RELEASE_NOTES.md.in`) from actual release notes
- Automated CHANGELOG.md updates after release
- Automated cleanup of RELEASE_NOTES.md after release

### Documentation
- Added comprehensive release process documentation (`docs/dev/releases.md`)
- Clear step-by-step instructions for developers
- Troubleshooting guide for common release issues

## For Players

No gameplay changes in this release. Focus is on improving the release and distribution process.

## For Server Operators

No configuration changes required. Upgrade instructions remain the same.

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


---


### [1.3.0] - 2026-01-20

#### For Players (Gameplay Changes)

##### ⚠️ Important: One-Fragment Limit
- **You can now only carry ONE fragment type at a time!**
  - If you have a Burning Fragment, you cannot pick up or equip any other fragment (Agility, Immortal, Corrupted Core)
  - To switch fragments, you must DROP your current fragment first
  - Use `/withdrawability` to unequip your current fragment without dropping it
  - This applies to ALL methods of acquiring fragments: crafting, pickup, equip commands

##### 🔥 Fire Fragment Fixes
- **Dragon's Wrath** (`/fire 1`) now deals consistent damage
  - Always deals exactly 3 hearts of damage
  - Damage ignores armor (as originally intended)
  - No longer variable damage based on armor

##### 👁 Corrupted Core Fixes
- **Dread Gaze** (`/corrupt 1`) duration changed to **4 seconds** (was 10 seconds)
- Frozen targets now properly cannot:
  - Move or walk
  - Eat food
  - Open/move items in their inventory
- Improved freeze mechanics to avoid triggering anti-cheat warnings

#### For Server Operators (Technical Changes)

##### 🛡️ One-Fragment Limit Implementation
- **FragmentItemListener** (new in `listener` package):
  - Added comprehensive fragment checking across inventory, equipped, and cursor
  - Players can no longer bypass limit by:
    - Picking up fragments while holding another on cursor
    - Clicking equipped fragment while standing on another
    - Using equip commands with fragments in containers
- **FragmentManager**:
  - Added `hasAnyFragmentInInventory()` method
  - Equip commands now check entire inventory before allowing equip
  - Admin bypass still available with `elementaldragon.fragment.admin` permission

##### 🔧 Bug Fixes
- **BurningFragment**:
  - Simplified Dragon's Wrath damage calculation
  - Now uses `EntityDamageByEntityEvent` with `setDamage(6.0)` for consistent armor-ignoring damage
  - Reduced from 93 lines to 11 lines in damage handler
- **CorruptedCoreFragment**:
  - Added debuff metadata keys for tracking frozen players
  - Implemented persistent freeze using Bukkit teleportation (anti-cheat compatible)
  - Added freeze location tracking to prevent escaping
  - Added saturation tracking for proper freeze mechanics
  - Fixed freeze duration to 4 seconds (80 ticks)
  - Added event handlers for block break/place prevention while frozen
  - Added event handler for inventory interaction prevention while frozen

##### 🧪 Testing
- Added `FragmentManagerOneFragmentLimitTest` with 5 comprehensive test cases
- Updated `FragmentItemListenerTest` for new API usage
- Fixed import path: `fragment.FragmentItemListener` → `listener.FragmentItemListener`

##### 📚 Documentation
- Updated user documentation (`docs/user/fragments.md`):
  - Clarified one-fragment limit rules
  - Removed references to non-existent `/unequip` command
  - Updated to use `/withdrawability` command
- Updated admin documentation (`docs/admin/commands.md`):
  - Removed hallucinated `/unequip` command references

##### 🐳 Integration Tests
- Fixed Docker volume permissions
- Improved caching strategy for integration tests
- Added retry logic for flaky tests

---

### [1.2.5] - Previous Release

#### For Players
- Initial fragment system implementation
- Fire, Agility, Immortal, and Corrupted Core fragments
- Active abilities and passive bonuses
- HUD display for abilities and cooldowns

#### For Server Operators
- Basic fragment management
- Cooldown system
- Ability execution framework

---

## Version History

| Version | Date | Description |
|---------|------|-------------|
| 1.3.0 | 2026-01-20 | One-fragment limit, ability fixes |
| 1.2.5 | Previous | Initial release |
