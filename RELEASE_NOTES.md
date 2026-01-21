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
