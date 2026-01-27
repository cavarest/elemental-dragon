# Changelog

All notable changes to the Elemental Dragon plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### [1.3.7] - 2026-01-27

## Added

- Draconic Surge toggle behavior (type `/agile 1` again to halt)
- Draconic Surge collision damage (3 hearts, ignores armor)
- Dragon's Wrath homing fireball
- Wing Burst improved velocity (20 block knockback)

## Changed

- **Lightning Ability**: Dragon egg no longer needs to be in offhand - as long as it's anywhere in your inventory, you can use `/lightning 1`
- **Dragon's Wrath**: Now deals 4 hearts damage (armor-piercing)

## Fixed

- `/equip` command allowing admins to bypass one-fragment limit
- Wing Burst velocity too low due to friction
- HUD not updating on inventory slot changes

## Technical

- Consolidated duplicate event listeners into single FragmentItemListener
- Added DRY helper methods for inventory checking (ElementalItems class)
- Added Pilaf integration test suite (17 test scenarios)
- Added world profile system for development testing

---

### [1.3.6] - 2026-01-21

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
