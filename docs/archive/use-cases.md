# Dragon Egg Lightning Use Case Testing Plan

**Date**: December 27, 2025
**Framework**: PILAF (Paper Integration Lightning Automation Framework)
**Use Case**: Dragon Egg Lightning Plugin Testing

---

## 🎯 Use Case Overview

**Objective**: Verify PILAF framework can comprehensively test the Dragon Egg Lightning plugin use case

**Plugin Functionality**:
- Players use Dragon Eggs in offhand to cast lightning abilities
- 3 sequential lightning strikes with 2 hearts damage each (6 hearts total)
- Armor-bypassing damage (consistent regardless of protection)
- 60-second cooldown system

---

## 📋 Use Case Testing Plan

### Phase 1: MockBukkit Backend Testing
- [ ] **Verify MockBukkit Backend**: Test framework initialization
- [ ] **Player Setup Simulation**: Create test player with Dragon Eggs
- [ ] **Entity Spawning**: Spawn armored and unarmored zombies
- [ ] **Lightning Execution**: Test lightning command execution
- [ ] **Damage Verification**: Confirm 6 hearts total damage (2 per strike × 3)
- [ ] **Cooldown Testing**: Verify 60-second cooldown enforcement
- [ ] **HUD Integration**: Test real-time cooldown display

### Phase 2: Real Server Backend Testing
- [ ] **Server Integration**: Connect to PaperMC server via RCON
- [ ] **Plugin Loading**: Verify DragonEggLightning plugin loads correctly
- [ ] **Live Command Testing**: Execute real `/ability 1` commands
- [ ] **Entity Interaction**: Test lightning on live server entities
- [ ] **Performance Monitoring**: Verify server performance during testing
- [ ] **Error Handling**: Test plugin behavior with invalid inputs

### Phase 3: End-to-End Validation
- [ ] **Complete Workflow**: Full player → target → lightning → damage cycle
- [ ] **Cross-Backend Verification**: Ensure consistent results between MockBukkit and Real Server
- [ ] **Edge Case Testing**: Invalid targets, missing items, cooldowns
- [ ] **Performance Benchmarking**: Compare MockBukkit vs Real Server execution times
- [ ] **Documentation Validation**: Verify all documented commands work correctly

---

## 🧪 Test Scenarios

### Scenario 1: Basic Lightning Use Case
```
Setup:
- Spawn test player
- Give 3 Dragon Eggs to player
- Move Dragon Eggs to offhand
- Spawn unarmored zombie (50 blocks away)

Execution:
- Player executes /ability 1
- Lightning strikes zombie 3 times

Expected Results:
- Zombie takes 6 hearts total damage (2 per strike)
- Player sees cooldown countdown
- Lightning effects visible
- Thunder sounds played
```

### Scenario 2: Armored Target Testing
```
Setup:
- Spawn test player with Dragon Eggs
- Spawn zombie with full diamond armor
- Verify zombie has Protection IV enchantments

Execution:
- Player executes /ability 1 on armored zombie

Expected Results:
- Zombie still takes 6 hearts total damage
- Armor provides no protection (armor-bypassing)
- Same damage as unarmored target
```

### Scenario 3: Cooldown Enforcement
```
Setup:
- Player has Dragon Eggs in offhand
- Lightning ability is ready

Execution:
- First: /ability 1 (should work)
- Immediate second: /ability 1 (should be blocked)
- Wait 59 seconds: /ability 1 (should be blocked)
- Wait 61 seconds: /ability 1 (should work)

Expected Results:
- First command succeeds
- Second command blocked with cooldown message
- Cooldown countdown visible in HUD
- Ability works again after 60+ seconds
```

### Scenario 4: Invalid Target Testing
```
Setup:
- Player has Dragon Eggs in offhand
- No valid targets in range

Execution:
- Player executes /ability 1

Expected Results:
- Clear error message displayed
- No lightning strikes
- No cooldown consumed
- HUD shows "No valid targets"
```

### Scenario 5: Item Validation
```
Setup:
- Player has no Dragon Eggs
- OR Dragon Eggs not in offhand

Execution:
- Player executes /ability 1

Expected Results:
- Clear error message about missing Dragon Egg in offhand
- No cooldown consumed
- Helpful guidance message
```

---

## 🔧 Testing Commands

### MockBukkit Testing:
```bash
# Run specific use case test
mvn test -Dtest=DragonEggLightningUseCaseTest#testDragonEggLightningMockBackend

# Run all MockBukkit tests
mvn test -Dtest.groups=mock

# Run unit tests with MockBukkit
mvn test -Dtest.groups=unit
```

### Real Server Testing:
```bash
# Automated integration testing
./run-pilaf-integration-tests.sh

# Manual approach
./start-server.sh && sleep 60
mvn test -Dtest=DragonEggLightningUseCaseTest#testDragonEggLightningEndToEnd
./stop-server.sh
```

### Performance Testing:
```bash
# Benchmark MockBukkit performance
time mvn test -Dtest=DragonEggLightningUseCaseTest#testDragonEggLightningMockBackend

# Compare execution times
echo "MockBukkit: $(time mvn test -Dtest=DragonEggLightningUseCaseTest#testDragonEggLightningMockBackend)"
echo "Real Server: $(time ./run-pilaf-integration-tests.sh)"
```

---

## ✅ Success Criteria

### Functional Requirements:
- [ ] **Lightning Damage**: Confirmed 6 hearts total (2 per strike × 3)
- [ ] **Armor Bypassing**: Same damage regardless of target armor/protection
- [ ] **Cooldown System**: 60-second cooldown properly enforced
- [ ] **HUD Integration**: Real-time cooldown display working
- [ ] **Error Handling**: Clear messages for invalid scenarios
- [ ] **Item Validation**: Dragon Egg must be in offhand

### Technical Requirements:
- [ ] **MockBukkit Backend**: All tests pass with simulated environment
- [ ] **Real Server Backend**: Integration tests pass with live server
- [ ] **Cross-Backend Consistency**: Same results from both backends
- [ ] **Performance**: MockBukkit tests complete in <5 seconds
- [ ] **Documentation**: All documented commands verified working

### Quality Assurance:
- [ ] **No Test Failures**: All tests pass (0 failures, 0 errors)
- [ ] **Clear Output**: Test results are informative and actionable
- [ ] **Reproducible**: Tests can be run multiple times with consistent results
- [ ] **Error Recovery**: Graceful handling of unexpected conditions

---

## 📊 Expected Test Output

### MockBukkit Test Success:
```
🧪 Testing Dragon Egg Lightning with MockBukkit Backend
========================================================
🔧 Initializing MockBukkit backend...
✅ MockBukkit backend initialized

📝 Simulating player setup...
🎁 MockBukkit: Giving 3 dragon_egg to test_player
🎮 MockBukkit: Equipping dragon_egg to offhand for test_player

🧟 Simulating zombie spawns...
🌟 MockBukkit: Spawning entity zombie_unarmored (ZOMBIE) at [10.0, 64.0, 10.0]
🌟 MockBukkit: Spawning entity zombie_armored (ZOMBIE) at [15.0, 64.0, 10.0]

⚡ Simulating lightning strikes...
🎮 MockBukkit: test_player executes command: ability test_player lightning zombie_unarmored
🎮 MockBukkit: test_player executes command: ability test_player lightning zombie_armored

🔌 MockBukkit: Checking if plugin DragonEggLightning received command from test_player
📊 MockBukkit: Lightning damage verification - 6 hearts total (2 per strike × 3)
⏰ MockBukkit: Cooldown verification - 60 seconds enforced
🎯 MockBukkit: Armor-bypassing damage confirmed

✅ MockBukkit backend simulation completed successfully
✅ All use case requirements verified
```

### Real Server Test Success:
```
🖥️ Testing Dragon Egg Lightning with Real Server Backend
========================================================
🔌 Initializing RCON connection...
✅ RCON connection established

📡 Server verification...
✅ PaperMC server running
✅ DragonEggLightning plugin loaded
✅ RCON port accessible

🎮 Live server testing...
🎯 test_player executes: /ability 1
⚡ Lightning strikes executed: 3 sequential strikes
💥 Damage verification: 6 hearts total confirmed
⏰ Cooldown verification: 60-second cooldown active
🎨 HUD integration: Cooldown display working

✅ Real server backend integration completed successfully
✅ All use case requirements verified
```

---

## 🚀 Next Steps

### Immediate Actions:
1. **Run MockBukkit Tests**: Execute use case tests with corrected naming
2. **Verify Framework**: Confirm PILAF framework handles the use case correctly
3. **Performance Check**: Benchmark execution times and resource usage
4. **Documentation Update**: Update any remaining documentation references

### Follow-up Actions:
1. **Real Server Testing**: If MockBukkit tests pass, proceed with real server testing
2. **Edge Case Coverage**: Test additional scenarios and error conditions
3. **Performance Optimization**: Identify any bottlenecks or improvements
4. **Framework Enhancement**: Consider PILAF framework improvements based on use case results

---

**Status**: 🔄 **READY TO EXECUTE USE CASE TESTING**
**Priority**: 🎯 **HIGH - Core use case verification**
**Expected Outcome**: ✅ **PILAF framework successfully handles Dragon Egg Lightning plugin testing**
