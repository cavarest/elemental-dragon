# Dragon Egg Lightning Plugin - Complete Testing Guide

## 🎉 Plugin Successfully Loaded!

The Dragon Egg Lightning plugin is now running successfully on the Paper server!

**Server Details:**
- **Server**: Paper 1.21.8
- **Plugin**: DragonEggLightning v1.0.0
- **Port**: localhost:25565

## 🔧 How to Test the Plugin

### Step 1: Join the Server
1. Open Minecraft Java Edition (1.21.8 or newer)
2. Add server: `localhost:25565`
3. Join the server

### Step 2: Get OP Permissions (Required)
**Option A: Automatic (Recommended)**
- Your user `posiflow` has been automatically granted OP permissions
- You should now have access to all commands

**Option B: Manual (if needed)**
If you still can't use `/give`, ask an administrator to run:
```
/op posiflow
```

### Step 3: Test Plugin Commands

#### 1. Get Dragon Egg
```bash
/give @s dragon_egg 1
```

#### 2. Place Dragon Egg in Offhand
1. Open inventory (E)
2. Put Dragon Egg in the **offhand slot** (bottom-right)
3. Close inventory

#### 3. Test the Lightning Ability
```bash
/ability 1
```

**Expected Behavior:**
- HUD appears in middle-left showing "Lightning ready"
- 3 purple lightning strikes target the closest entity in your facing direction
- Lightning strikes occur with 0.5-second intervals between each
- Each strike deals 1.5 hearts (3 HP) of damage
- Total potential damage: 4.5 hearts (9 HP) if all three connect
- Cooldown starts: HUD shows "59s", "58s", etc.
- After 60 seconds: HUD shows "Lightning ready" again

### Step 4: Test Edge Cases

#### Test Cooldown
1. Use `/ability 1`
2. Try to use again immediately
3. **Expected**: Ability should not work, cooldown message shown

#### Test No Dragon Egg
1. Remove Dragon Egg from offhand
2. Use `/ability 1`
3. **Expected**: Ability should not work, need Dragon Egg message shown

#### Test No Targets
1. Use `/ability 1` in an empty area
2. **Expected**: Lightning strikes should still spawn but with no targets

## 🐛 Troubleshooting

### If `/give` Command Not Working
- **Problem**: No OP permissions
- **Solution**: Contact administrator for `/op posiflow`

### If Plugin Commands Not Found
- **Problem**: Plugin not loaded
- **Solution**: Check server logs for plugin loading errors

### If Ability Does Nothing
- **Problem**: Dragon Egg not in offhand
- **Solution**: Ensure Dragon Egg is in the offhand slot (bottom-right of inventory)

### If Lightning Strikes Don't Appear
- **Problem**: No entities in range
- **Solution**: Move near other entities (players, mobs, animals)

## 🎮 Plugin Features Testing Checklist

- [ ] `/give @s dragon_egg` works (OP permissions)
- [ ] Dragon Egg can be placed in offhand
- [ ] `/ability 1` command is recognized
- [ ] HUD appears in middle-left of screen
- [ ] Lightning strikes spawn in facing direction
- [ ] Lightning strikes are purple/magenta colored
- [ ] 3 lightning strikes occur with 0.5s intervals
- [ ] Lightning deals 1.5 hearts damage per strike
- [ ] 60-second cooldown works properly
- [ ] HUD shows countdown during cooldown
- [ ] HUD shows "Lightning ready" when available
- [ ] Ability fails without Dragon Egg in offhand
- [ ] Ability fails during cooldown
- [ ] Multiple lightning strikes target closest entity

## 🔍 Server Log Verification

To verify plugin is loaded correctly, check server logs:
```bash
docker logs papermc-dragonegg | grep -i "dragon\|lightning\|plugin"
```

**Expected Output:**
```
[PluginInitializerManager] Initialized 1 plugin
[PluginInitializerManager] Bukkit plugins (1):
 - DragonEggLightning (1.0.0)
```

## 📋 Technical Details

### Plugin Architecture
- **Main Class**: `DragonEggLightningPlugin`
- **Command Handler**: `AbilityCommand`
- **Ability Manager**: `LightningAbility`
- **HUD System**: `HudManager`

### Files Generated
- `server-data/plugins/DragonEggLightning.jar` - Plugin JAR file
- Configuration files and logs in `server-data/`

### Development Setup
- **Build**: `./build.sh`
- **Start**: `./start-server.sh`
- **Stop**: `./stop-server.sh`
- **Reset**: `./start-server.sh --reset`
- **Logs**: `docker logs -f papermc-dragonegg`

## 🎯 Success Criteria

The plugin is **SUCCESSFUL** when:
1. ✅ Server shows "DragonEggLightning (1.0.0)" in plugin list
2. ✅ `/ability 1` command works
3. ✅ Lightning ability activates with Dragon Egg in offhand
4. ✅ HUD displays correctly
5. ✅ Cooldown system functions
6. ✅ Lightning deals proper damage
7. ✅ All edge cases handled properly

## 🚀 Next Steps

Once testing is complete:
1. **Document Issues**: Report any bugs or unexpected behavior
2. **Performance Check**: Monitor server performance with plugin active
3. **Feature Refinement**: Consider enhancements based on gameplay testing

---

**Plugin Status**: ✅ **LOADED AND READY FOR TESTING**

**Last Updated**: 2025-12-24 17:42:00 UTC
