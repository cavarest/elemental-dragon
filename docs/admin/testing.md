# Real-World Plugin Testing Guide

## ⚠️ IMPORTANT: Testing Status

**Current Status**: Plugin has been built, unit tested (13/13 passing), and is ready for real-world testing.

**What HAS been verified**:
- ✅ Plugin compiles successfully
- ✅ Unit tests pass (13/13)
- ✅ JAR file created: `DragonEggLightning-1.0.0.jar`
- ✅ Docker environment configured
- ✅ All code implements requested features

**What NEEDS testing**:
- ❌ In-game functionality verification
- ❌ Paper server integration
- ❌ Visual effects rendering
- ❌ Performance under load

---

## Quick Test (15 Minutes)

### Prerequisites Check
```bash
# Verify all tools are installed
java -version  # Must show Java 21+
docker --version
docker-compose --version
mvn --version
```

### One-Command Test
```bash
# Run the complete automated test
./test-plugin.sh
```

This will:
1. Build the plugin
2. Start the Paper server
3. Wait for server to initialize
4. Display testing instructions
5. Provide server connection details

### Manual Verification Steps

If `./test-plugin.sh` doesn't work, follow these manual steps:

#### Step 1: Build Plugin
```bash
cd /Users/mulgogi/src/augustus/papermc-plugin-dragon-egg
mvn clean package
```

**Expected Output**:
```
[INFO] BUILD SUCCESS
[INFO] Building jar: /Users/mulgogi/src/augustus/papermc-plugin-dragon-egg/target/DragonEggLightning-1.0.0.jar
```

#### Step 2: Start Paper Server
```bash
# Start server with plugin
docker-compose up -d

# Wait for startup (30-60 seconds)
docker logs -f papermc-dragonegg
```

**Look for these messages**:
```
[INFO] Done (30.842s)! For help, type "help"
[INFO] DragonEggLightning enabled
```

#### Step 3: Connect to Server
1. Open Minecraft 1.21.8+
2. Multiplayer → Add Server
3. Server Address: `localhost:25565`
4. Join server

#### Step 4: Test Plugin

**In Minecraft Chat**:
```bash
# 1. Get Dragon Egg
/give @p minecraft:dragon_egg

# 2. Move to offhand (press F key)

# 3. Test command
/ability 1

# 4. Verify results
# - Should see purple lightning
# - Should get success message
# - Should see cooldown timer
```

---

## Detailed Testing Protocol

### Test Environment Setup

#### Server Specifications
- **Minecraft Version**: 1.21.8
- **Paper Version**: Latest build for 1.21.8
- **Java Version**: OpenJDK 21
- **Memory**: 2GB allocated
- **Location**: localhost:25565

#### Test Client Setup
1. **Minecraft Client**: 1.21.8 or higher
2. **Graphics**: Set particle effects to "All" for best visibility
3. **Performance**: Reduce render distance if needed for smooth gameplay

### Core Functionality Tests

#### Test 1: Plugin Loading
**Command**: `/plugins`
**Expected Result**:
- ✅ Shows "DragonEggLightning" in plugin list
- ✅ No error messages in console
- ✅ Server startup shows "[INFO] DragonEggLightning enabled"

**If Failed**:
```bash
# Check server logs
docker logs papermc-dragonegg

# Common issues:
# - Java version wrong (must be 21+)
# - Plugin JAR not in plugins/ directory
# - Server hasn't been restarted after plugin installation
```

#### Test 2: Command Recognition
**Command**: `/ability 1`
**Expected Result**:
- ✅ Command is recognized (no "Unknown command" error)
- ✅ If no Dragon Egg in offhand: "You must hold a Dragon Egg in your offhand!"
- ✅ If Dragon Egg in offhand but no target: "No valid target found!"

**If Failed**:
```bash
# Check command registration in server logs
# Should see: "Registering commands..."
```

#### Test 3: Dragon Egg Requirement
**Steps**:
1. Remove Dragon Egg from offhand
2. Run `/ability 1`
3. Put Dragon Egg back in offhand
4. Run `/ability 1` again

**Expected Result**:
- ✅ Without Dragon Egg: Error message about missing Dragon Egg
- ✅ With Dragon Egg: Ability attempts to execute
- ✅ Clear error messaging

#### Test 4: Lightning Ability Execution
**Steps**:
1. Get Dragon Egg: `/give @p minecraft:dragon_egg`
2. Move to offhand: Press `F` key
3. Find a target entity (zombie, cow, villager, etc.)
4. Look at the entity
5. Execute: `/ability 1`

**Expected Results**:
- ✅ **Visual**: Purple lightning strikes appear
- ✅ **Timing**: 3 strikes with 0.5-second intervals
- ✅ **Damage**: Target takes 1.5 hearts per strike
- ✅ **Message**: "Lightning ability activated!"
- ✅ **Sound**: Thunder sound for each strike

**Visual Effects Checklist**:
- [ ] Purple/magenta lightning beam from sky to target
- [ ] Electric spark particles along the beam
- [ ] Explosion effect at impact point
- [ ] Flash effect for dramatic impact
- [ ] Higher pitched thunder sound than normal

#### Test 5: Cooldown System
**Steps**:
1. Execute `/ability 1` successfully
2. Immediately try `/ability 1` again
3. Wait 60 seconds
4. Try `/ability 1` again

**Expected Results**:
- ✅ **First attempt**: Works normally
- ✅ **Second attempt**: Fails with cooldown message
- ✅ **HUD Display**: Shows countdown (59s, 58s, etc.)
- ✅ **After 60s**: Ability works again
- ✅ **Ready State**: Shows "⚡ Lightning ready"

#### Test 6: Target Detection
**Test Scenarios**:
1. **Valid Target**: Look at entity within 50 blocks
2. **No Target**: Look at empty area
3. **Behind Wall**: Look at entity blocked by blocks
4. **Too Far**: Look at entity >50 blocks away

**Expected Results**:
- ✅ **Valid**: Lightning targets closest entity in line of sight
- ✅ **Empty**: "No valid target found!" message
- ✅ **Blocked**: "No valid target found!" message
- ✅ **Far**: "No valid target found!" message

#### Test 7: Edge Cases
**Test Scenarios**:
1. **Item Removal During Cast**: Start ability, remove Dragon Egg mid-execution
2. **Target Death During Cast**: Kill target while lightning is striking
3. **Multiple Players**: Multiple players use ability simultaneously

**Expected Results**:
- ✅ **Item Removal**: "Ability cancelled! Dragon Egg removed from offhand."
- ✅ **Target Death**: Remaining strikes stop gracefully
- ✅ **Multiple Players**: No interference between players
- ✅ **No Crashes**: Server remains stable

### Performance Testing

#### Test 8: Server Performance
**Monitoring Commands**:
```bash
# In server console or with paper plugins:
/tps  # Should be around 20
/mem  # Check memory usage

# Monitor during lightning use:
# - TPS should not drop significantly
# - Memory should not spike
# - No console errors
```

**Expected Performance**:
- ✅ **TPS Impact**: <1% reduction during lightning strikes
- ✅ **Memory**: No continuous growth
- ✅ **Stability**: No server crashes or lag spikes

#### Test 9: Visual Performance
**Client-Side Testing**:
1. **Low-End Machine**: Test with reduced graphics settings
2. **High-End Machine**: Test with maximum particle effects
3. **Multiple Lightning**: Cast ability rapidly (within cooldown)

**Expected Results**:
- ✅ **Low-End**: Lightning still visible with reduced particles
- ✅ **High-End**: Full visual effects render smoothly
- ✅ **Rapid Use**: Cooldown prevents excessive lightning

---

## Troubleshooting Common Issues

### Issue: Plugin Not Loading
**Symptoms**:
- `/plugins` doesn't show DragonEggLightning
- Server console has errors
- `/ability 1` shows "Unknown command"

**Solutions**:
```bash
# 1. Check Java version
java -version  # Must be 21+

# 2. Verify JAR file location
ls -la plugins/DragonEggLightning*.jar

# 3. Check server logs
docker logs papermc-dragonegg | grep -i dragon

# 4. Restart server
docker-compose restart
```

### Issue: Command Not Working
**Symptoms**:
- "Unknown command" message
- Permission denied errors

**Solutions**:
```bash
# 1. Check permissions
op <username>  # Make player operator

# 2. Verify plugin loaded
/plugins

# 3. Check server console for errors
```

### Issue: Lightning Not Appearing
**Symptoms**:
- Command works but no visual effects
- Target doesn't take damage
- No sound effects

**Solutions**:
```bash
# 1. Check graphics settings
# In Minecraft: Options → Video Settings → Particles → All

# 2. Check for conflicts
# Disable other combat/particle plugins temporarily

# 3. Monitor performance
# Low FPS can cause particle lag
```

### Issue: Server Lag
**Symptoms**:
- TPS drops during lightning
- Server becomes unresponsive
- High memory usage

**Solutions**:
```bash
# 1. Increase JVM memory
# Edit docker-compose.yml, change MEMORYSIZE=4G

# 2. Monitor TPS
/tps  # Should be 20

# 3. Check for memory leaks
# Memory should not grow continuously
```

---

## Testing Checklist

### ✅ Basic Functionality
- [ ] Plugin loads without errors
- [ ] `/ability 1` command recognized
- [ ] Dragon Egg requirement enforced
- [ ] Lightning strikes appear visually
- [ ] Lightning deals correct damage
- [ ] Three strikes with correct timing

### ✅ Advanced Features
- [ ] Cooldown system works
- [ ] HUD displays cooldown timer
- [ ] Target detection accurate
- [ ] Edge cases handled gracefully
- [ ] Performance impact minimal

### ✅ Server Integration
- [ ] No server crashes
- [ ] TPS remains stable
- [ ] Memory usage stable
- [ ] Compatible with Paper 1.21.8
- [ ] Works with multiple players

### ✅ User Experience
- [ ] Clear error messages
- [ ] Intuitive gameplay
- [ ] Satisfying visual effects
- [ ] Balanced gameplay
- [ ] Easy to learn

---

## Reporting Test Results

### If All Tests Pass
Congratulations! The plugin is working as designed.

**Next Steps**:
1. Document any minor issues found
2. Test with additional players
3. Test in different game modes (survival, creative)
4. Test with other plugins installed

### If Tests Fail
Please document the following:

```bash
# Environment Information
- Operating System:
- Java Version:
- Minecraft Version:
- Paper Version:

# Test Results
- Which tests failed:
- Error messages received:
- Console output:
- Server logs:

# Steps to Reproduce
1.
2.
3.

# Expected vs Actual
Expected:
Actual:
```

### Issue Severity
- **Critical**: Server crashes, data loss, unplayable
- **Major**: Core features don't work, poor performance
- **Minor**: Cosmetic issues, confusing messages
- **Enhancement**: Feature requests, optimizations

---

## Contact for Support

**Before Reporting Issues**:
1. ✅ Check this troubleshooting guide
2. ✅ Verify all prerequisites are met
3. ✅ Test with minimal plugins
4. ✅ Check server console logs
5. ✅ Try restarting the server

**When Reporting**:
- Include complete error messages
- Provide steps to reproduce
- List your system specifications
- Mention any other plugins installed

**Documentation**:
- Check `README.md` for technical details
- Check `TESTING_GUIDE.md` for comprehensive testing
- Check `INSTALLATION_GUIDE.md` for setup help

---

**Ready to Test?** Run `./test-plugin.sh` or follow the manual steps above. The plugin is built and ready - now we just need to verify it works in the real Minecraft environment!

**Plugin Version**: 1.0.0
**Last Updated**: December 24, 2025
**Status**: Ready for real-world testing
