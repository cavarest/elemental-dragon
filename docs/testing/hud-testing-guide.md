# HUD Testing Guide

Quick reference for testing the HUD (Heads-Up Display) system using ProtocolSidebar.

**Note**: The HUD system was migrated from boss bars to ProtocolSidebar to avoid interference with actual boss fights. See `docs/hud-protocolsidebar-migration.md` for details.

---

## 📋 Integration Test Files

All HUD integration tests are located in: `src/test/resources/integration-stories/`

| Test File | Purpose | Size |
|-----------|---------|------|
| `hud-lightning-display-test.yaml` | Lightning ability HUD sidebar display | 2.5 KB |
| `hud-fragment-display-test.yaml` | Fragment abilities HUD sidebar display | 2.6 KB |
| `hud-cooldown-updates-test.yaml` | Cooldown countdown sidebar updates | 2.6 KB |
| `hud-multiple-abilities-test.yaml` | Multiple abilities in sidebar simultaneously | 3.5 KB |

**Total**: 4 comprehensive test scenarios covering all HUD functionality

---

## 🚀 Quick Start

### **1. Start the Server**
```bash
./start-server.sh
```

Wait for server to fully initialize (~10 seconds)

### **2. Manual HUD Testing**

Connect to the server (localhost:25565) and run these commands:

#### **Test Lightning Ability HUD**
```bash
# Give dragon egg
/give @p minecraft:dragon_egg

# Move to offhand (press F key in-game)
# Sidebar should appear on right side with one line:
# ⚡ /lightning 1  █████▏ Ready

# Use ability
/lightning

# Sidebar should show cooldown with decreasing progress bar:
# ⚡ /lightning 1  ░░░░░▏ In 60s
```

#### **Test Fragment Ability HUD**
```bash
# Give and equip Burning Fragment
/ed give @p equipment fire
/fire equip

# Sidebar should appear with two lines:
# 🔥 /fire 1       █████▏ Ready
# 🔥 /fire 2       █████▏ Ready

# Use ability 1
/fire 1

# First line should show cooldown:
# 🔥 /fire 1       ░░░░░▏ In 40s
# 🔥 /fire 2       █████▏ Ready
```

#### **Test Multiple Abilities**
```bash
# Give dragon egg (for lightning)
/give @p minecraft:dragon_egg
# Move to offhand (F key)

# Give and equip Agility Fragment
/ed give @p equipment agility
/agile equip

# Sidebar should appear with three lines:
# ⚡ /lightning 1  █████▏ Ready
# 💨 /agile 1      █████▏ Ready
# 💨 /agile 2      █████▏ Ready
```

---

## 🔍 What to Verify

### **Sidebar Display**
- ✅ Sidebar appears on right side of screen (scoreboard area)
- ✅ Sidebar shows "Elemental Dragon" title in gold
- ✅ Ability lines stack vertically within sidebar
- ✅ Text-based progress bars display correctly (█████▏ vs ░░░░░▏)
- ✅ Icons display correctly at start of each line (⚡🔥💨🛡️👁️)

### **Cooldown Updates**
- ✅ Progress bar fills as cooldown decreases
- ✅ Text updates every second
- ✅ "Ready" shows in green when cooldown expires
- ✅ "In Xs" / "In Xm Ys" format for cooldowns

### **Equipment Detection**
- ✅ Lightning sidebar appears when dragon egg in offhand
- ✅ Lightning sidebar disappears when dragon egg removed
- ✅ Fragment sidebars appear when fragment equipped
- ✅ Fragment sidebars disappear when fragment unequipped

### **Multiple Abilities**
- ✅ Lightning + fragment abilities show together
- ✅ Each ability tracks independently
- ✅ Cooldowns update independently
- ✅ No interference between different ability types

---

## 🧪 Integration Test Scenarios

### **Test 1: Lightning Display**
**File**: `hud-lightning-display-test.yaml`

**Scenario**:
1. Connect player
2. Give dragon egg to offhand
3. Verify sidebar appears
4. Use lightning ability
5. Verify sidebar shows cooldown
6. Remove dragon egg
7. Verify sidebar disappears
8. Put dragon egg back
9. Verify sidebar reappears

**Expected**: Sidebar visibility matches dragon egg presence

---

### **Test 2: Fragment Display**
**File**: `hud-fragment-display-test.yaml`

**Scenario**:
1. Connect player
2. Give Burning Fragment using `/ed give @p equipment fire`
3. Equip fragment using `/fire equip`
4. Verify sidebar shows 2 lines (abilities 1 & 2)
5. Use ability 1
6. Verify both lines still shown (1 on cooldown)
7. Use ability 2
8. Verify both lines shown (both on cooldown)
9. Unequip fragment using `/fire unequip`
10. Verify sidebar disappears

**Expected**: Both fragment abilities display and track independently

---

### **Test 3: Cooldown Updates**
**File**: `hud-cooldown-updates-test.yaml`

**Scenario**:
1. Connect player
2. Give dragon egg to offhand
3. Set short cooldown (10 seconds for testing)
4. Use lightning ability
5. Verify sidebar shows cooldown
6. Wait 5 seconds (halfway)
7. Verify sidebar still showing
8. Wait for cooldown completion
9. Verify sidebar shows "Ready"
10. Use ability again to confirm readiness

**Expected**: Sidebar updates smoothly during countdown

---

### **Test 4: Multiple Abilities**
**File**: `hud-multiple-abilities-test.yaml`

**Scenario**:
1. Connect player
2. Give dragon egg (lightning ability)
3. Verify sidebar shows 1 line (lightning)
4. Give Agility Fragment using `/ed give @p equipment agility`
5. Equip fragment using `/agile equip`
6. Verify sidebar shows 3 lines (lightning + 2 agility)
7. Use lightning ability
8. Use agility ability 1
9. Verify all 3 lines still shown
10. Remove dragon egg
11. Verify 2 lines remain (fragment only)
12. Unequip fragment using `/agile unequip`
13. Verify no sidebar

**Expected**: Independent management of different ability types

---

## 📊 Test Results Format

After running tests, verify:

```
✅ Sidebar appears when equipped
✅ Sidebar shows correct ability names
✅ Sidebar shows correct icons and colors
✅ Sidebar updates every second during cooldown
✅ Sidebar shows "Ready" when cooldown expires
✅ Sidebar disappears when equipment removed
✅ Multiple abilities display simultaneously
✅ Each ability tracks independently
```

---

## 🐛 Common Issues

### **Sidebar not appearing**
- Check dragon egg is in offhand (not main hand)
- Verify fragment is equipped using `/fire equip` or `/agile equip`
- Wait 2 seconds for HUD update

### **Sidebar not updating**
- HUD updates every 1 second (20 ticks)
- Check server console for errors
- Verify HudManager is initialized

### **Wrong cooldown displayed**
- Check global cooldown settings: `/ed getglobalcooldown`
- Verify player cooldown: `/ed getcooldown @p`
- Clear cooldown if stuck: `/ed clearcooldown @p`

---

## 🔧 Admin Commands for Testing

```bash
# Set short cooldown for faster testing
/ed setcooldown <player> lightning 1 10    # 10 second cooldown
/ed setcooldown <player> fire 1 5          # 5 second cooldown
/ed setcooldown <player> agile 1 15        # 15 second cooldown

# Clear cooldowns
/ed clearcooldown <player>                 # Clear all
/ed clearcooldown <player> fire            # Clear fire only

# Check cooldowns
/ed getcooldown <player>                   # View all cooldowns

# Give items quickly
/ed give <player> equipment fire
/ed give <player> equipment agility
/ed give <player> equipment immortal
/ed give <player> equipment corrupted
```

---

## 📈 Performance Notes

- **Update Frequency**: 1 second (20 ticks)
- **Max Sidebar Lines**: ~15-16 lines per sidebar (Minecraft limitation)
- **Memory Impact**: Minimal (one Sidebar<Component> per player)
- **CPU Impact**: Very low (updates run on Bukkit scheduler)

---

## ✅ Success Indicators

HUD is working correctly when:
- ✅ Sidebar appears within 2 seconds of equipment change
- ✅ Cooldown text updates smoothly every second
- ✅ Progress bar fills as cooldown decreases
- ✅ "Ready" status appears when cooldown expires
- ✅ Sidebar disappears when equipment removed
- ✅ Multiple abilities display without interference
- ✅ No console errors related to HudManager

---

**Quick Test**: Give dragon egg → Move to offhand → Sidebar appears → `/lightning` → Countdown starts → Wait → "Ready" appears

**Fragment Test**: Give fire equipment → `/fire equip` → Sidebar appears → `/fire 1` → Countdown starts

**Status**: If the above works, HUD is functioning correctly! ✅
