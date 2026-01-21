---
title: Release Process
parent: Development
nav_order: 10
---

# Release Process

This document describes the complete release process for the Elemental Dragon plugin.

**Post-release tasks are fully automated** - the workflow handles CHANGELOG updates and cleanup automatically.

## Overview

The release process consists of three main workflows:

1. **Release Workflow** (`release.yml`) - Creates GitHub release, bumps version, updates CHANGELOG
2. **Distribution Workflow** (`distribution.yml`) - Publishes to Modrinth and Hangar

Post-release tasks (CHANGELOG update, cleanup) are **fully automated**.

## Recent Release: v1.3.6

The v1.3.6 release (2026-01-21) was the first to use the fully automated release process:

**Workflow Run**: https://github.com/cavarest/elemental-dragon/actions/runs/21204524436

**What was automated**:
- Created GitHub Release with JAR artifacts
- Published to Modrinth successfully
- Updated CHANGELOG.md with release notes
- Removed RELEASE_NOTES.md
- Committed and pushed all changes

This release validated the entire automated release workflow, confirming that developers can now create releases with a single command and no manual post-release steps.

## Prerequisites

Before creating a release, ensure:

- [ ] All tests pass: `./gradlew test`
- [ ] Code compiles successfully: `./build.sh`
- [ ] Version is ready to bump in `gradle.properties`
- [ ] `RELEASE_NOTES.md` is created from template with release notes

## Release Checklist

### 1. Update Release Notes

**About the Template System:**

The project uses a template-based release notes system following Unix conventions:

- `RELEASE_NOTES.md.in` - Template file (tracked in git)
- `RELEASE_NOTES.md` - Current release notes (created from template, removed after release)

This ensures:
1. Developers always have a structured template to follow
2. Release notes are consistent across releases
3. The workflow can read the notes file directly (no regex parsing)

Create `RELEASE_NOTES.md` from the template:

```bash
# Copy template
cp RELEASE_NOTES.md.in RELEASE_NOTES.md

# Edit with your release notes
vim RELEASE_NOTES.md
```

Update `RELEASE_NOTES.md` with notes for the upcoming release:

```markdown
# Release v1.3.6

## New Features
- Feature 1 description
- Feature 2 description

## Bug Fixes
- Bug fix 1 description
- Bug fix 2 description

## Technical Changes
- Technical change 1
- Technical change 2
```

**Important**: `RELEASE_NOTES.md` should contain **only** the notes for the current release being created. The workflow reads this file directly and publishes it to Modrinth and Hangar.

### 2. Verify Version

Check `gradle.properties` for the current version:

```properties
project.version=1.3.6
```

### 3. Trigger Release Workflow

Go to GitHub Actions → **release** workflow → **Run workflow**

**Input Parameters**:

| Parameter | Description | Example |
|-----------|-------------|---------|
| `next_version` | Version to release | `1.3.6` (or `major`, `minor`, `patch`, `skip`) |
| `release_title` | Title for release (optional) | `Elemental Dragon v1.3.6` |
| `release_notes` | Release notes (optional) | Leave empty - uses `RELEASE_NOTES.md` |

**Version Options**:
- `x.y.z` - Specific version (e.g., `1.3.6`)
- `major` - Bump major version (1.3.6 → 2.0.0)
- `minor` - Bump minor version (1.3.6 → 1.4.0)
- `patch` - Bump patch version (1.3.6 → 1.3.7)
- `skip` - Use current version without bumping

### 4. Workflow Execution

The release workflow will:

1. **Bump version** in `gradle.properties` and commit it
2. **Create git tag** for the version
3. **Build plugin** using `./build.sh --production`
4. **Create GitHub Release** with the JAR
5. **Trigger distribution workflow** automatically

### 5. Distribution Workflow

The distribution workflow runs automatically and:

1. **Reads `RELEASE_NOTES.md`** for release notes
2. **Builds the plugin** (in case of reusable workflow)
3. **Publishes to Modrinth** with version and changelog
4. **Publishes to Hangar** with version and changelog

### 6. Verify Release

After workflows complete:

1. **Check GitHub Release**: https://github.com/cavarest/elemental-dragon/releases
   - Verify version number
   - Verify release notes
   - Verify JAR is attached

2. **Check Modrinth**: https://modrinth.com/plugin/elemental-dragon
   - Verify version is published
   - Verify changelog is displayed

3. **Check Hangar**: https://hangar.papermc.io/ElementalDragon
   - Verify version is published
   - Verify changelog is displayed

### 7. Post-Release (Automated)

The release workflow **automatically** handles post-release tasks:

1. **Updates CHANGELOG.md** - Appends release notes from `RELEASE_NOTES.md` in the correct format
2. **Removes RELEASE_NOTES.md** - Deletes the file (template remains for next release)
3. **Commits changes** - Creates a commit with the changelog update

**No manual action required** - the workflow does everything after the release is created.

For the next release, simply recreate `RELEASE_NOTES.md` from the template:

```bash
cp RELEASE_NOTES.md.in RELEASE_NOTES.md
vim RELEASE_NOTES.md
```

## Manual Distribution (Alternative)

If automatic distribution fails, you can manually trigger the **distribution** workflow:

1. Go to GitHub Actions → **distribution** workflow
2. Click **Run workflow**
3. Enter the `tag` (e.g., `v1.3.6`)
4. Optionally enter `release_title`

## Troubleshooting

### Distribution workflow fails

**Symptom**: Distribution workflow shows "Resource not accessible by integration"

**Solution**: The workflow needs `actions: write` permission. Verify this is set in the workflow file.

### Changelog not showing on Modrinth/Hangar

**Symptom**: Modrinth or Hangar shows generic or no release notes

**Solution**: Ensure `RELEASE_NOTES.md` exists and contains the release notes. Create it from the template:
```bash
cp RELEASE_NOTES.md.in RELEASE_NOTES.md
# Then edit with your release notes
```

### Version bump commit fails

**Symptom**: "Commit version bump" step fails with detached HEAD error

**Solution**: The workflow should detect the branch automatically. If it fails, check that the branch name is correctly detected.

### Hangar publish fails

**Symptom**: Gradle Hangar publish fails with authentication error

**Solution**: Verify `HANGER_PAPERMC_API_KEY` secret is set in GitHub repository settings.

## Workflow Files

- `.github/workflows/release.yml` - Main release workflow
- `.github/workflows/distribution.yml` - Modrinth/Hangar distribution
- `RELEASE_NOTES.md.in` - Template for release notes (copy to RELEASE_NOTES.md before releasing)
- `RELEASE_NOTES.md` - Current release notes (created from template, removed after release)
- `CHANGELOG.md` - Complete version history (update after releasing)

## Version History

See `CHANGELOG.md` for complete version history.
