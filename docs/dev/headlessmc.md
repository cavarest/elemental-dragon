---
layout: default
title: HeadlessMC Testing
nav_order: 7
has_children: false
permalink: /dev/headlessmc/
---

# HeadlessMC Integration Plan for Paper Plugin Testing

## 🚨 **GAME-CHANGING DISCOVERY**: HeadlessMC Supports Paper Server Testing!

Based on the user research, HeadlessMC **DOES support Paper server testing**, which fundamentally changes our testing strategy.

## HeadlessMC Capabilities for Paper Plugins

### Server Support
- **Paper Servers**: ✅ Native support for Paper 1.21.5
- **Server Management**: Install, launch, and manage Paper servers
- **Command Testing**: Built-in command testing framework
- **Process Control**: Send commands and check output

### Testing Framework
- **JSON Test Format**: Structured test specification
- **Command Testing**: Built-in framework for command testing
- **CI Integration**: Already used in mc-runtime-test
- **Process Monitoring**: Can check server output and responses

### Example HeadlessMC Test for Paper Server:
```json
{
  "name": "Paper Plugin Test",
  "steps": [
    {
      "type": "ENDS_WITH",
      "message": "For help, type \"help\""
    },
    {
      "type": "SEND",
      "message": "op testuser"
    },
    {
      "type": "SEND",
      "message": "give testuser dragon_egg 1"
    },
    {
      "type": "SEND",
      "message": "ability 1"
    },
    {
      "type": "ENDS_WITH",
      "message": "Lightning ability activated"
    },
    {
      "type": "SEND",
      "message": "stop",
      "timeout": 120
    }
  ]
}
```

## New Strategy: HeadlessMC + mc-runtime-test

### Advantages
1. **Mature Solution**: Already battle-tested in production
2. **Paper Native**: Direct Paper server support
3. **Command Testing**: Built-in framework
4. **JSON Format**: Structured test specification
5. **CI Ready**: GitHub Actions workflows already exist
6. **Less Maintenance**: Leverage existing codebase
7. **Community Support**: Large user base

### Migration from PILAF to HeadlessMC

#### Phase 1: HeadlessMC Setup
- [ ] Install and configure HeadlessMC
- [ ] Set up Paper 1.21.5 server
- [ ] Test basic server launching
- [ ] Configure plugin loading

#### Phase 2: Test Conversion
- [ ] Convert PILAF YAML tests to HeadlessMC JSON format
- [ ] Map PILAF actions to HeadlessMC commands
- [ ] Test Paper plugin integration
- [ ] Validate command testing framework

#### Phase 3: CI Integration
- [ ] Set up mc-runtime-test in GitHub Actions
- [ ] Configure Paper server versions matrix
- [ ] Add plugin artifact deployment
- [ ] Test CI workflow

#### Phase 4: Documentation
- [ ] Document HeadlessMC setup process
- [ ] Create Paper plugin testing guide
- [ ] Provide migration examples
- [ ] Troubleshooting guide

## Implementation Commands

### Local Development
```bash
# Install HeadlessMC
# Follow: https://github.com/3arthqu4ke/headlessmc

# Add Paper server
server add paper 1.21.5

# Accept EULA
server eula paper-1.21.5-54 -accept

# Launch server
server launch paper-1.21.5-54 --jvm "-Xmx2G"

# Run tests
hmc test run --file tests/paper-plugin-test.json
```

### CI Integration
```yaml
# GitHub Actions using mc-runtime-test
- name: Test Paper Plugin
  uses: headlesshq/mc-runtime-test@4.1.0
  with:
    mc: 1.21.5
    modloader: paper
    mc-runtime-test: paper
    java: 21
```

## Comparison: PILAF vs HeadlessMC

| Feature | PILAF | HeadlessMC + mc-runtime-test |
|---------|-------|-------------------------------|
| Paper Support | Custom Implementation | ✅ Native |
| Server Management | Custom Docker Setup | ✅ Built-in |
| Command Testing | Custom Framework | ✅ Built-in |
| Test Format | YAML | JSON |
| CI Integration | Custom GitHub Actions | ✅ Pre-built |
| Maturity | New/Unproven | ✅ Mature/Proven |
| Maintenance | High | Low |
| Community | Limited | Large |

## Recommendation: **PIVOT TO HEADLESSMC** ✅

### Reasons
1. **Paper Server Support**: Native Paper server testing
2. **Proven Solution**: Already used in production
3. **Less Development**: Leverage existing codebase
4. **Better Testing**: Built-in command testing framework
5. **Future-Proof**: Active development and support

### Next Steps Priority
1. **High**: Set up HeadlessMC and test Paper server launching
2. **High**: Convert existing PILAF tests to HeadlessMC JSON format
3. **Medium**: Set up mc-runtime-test for CI integration
4. **Medium**: Create documentation and migration guide
5. **Low**: Consider contributing improvements back to HeadlessMC

## Conclusion

The discovery that **HeadlessMC supports Paper server testing** makes it the clear choice over our custom PILAF framework. We should **pivot to HeadlessMC + mc-runtime-test** for a mature, proven, and well-supported Paper plugin testing solution.

**Decision**: **MIGRATE TO HEADLESSMC** for Paper plugin testing.
