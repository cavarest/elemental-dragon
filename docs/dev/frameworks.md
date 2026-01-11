---
layout: default
title: Testing Frameworks
nav_order: 6
has_children: false
permalink: /dev/frameworks/
---

# Analysis: Using Existing Testing Frameworks vs PILAF

## Question
Should we pivot from our custom PILAF framework to use existing, established solutions?

## 🚨 **CRITICAL UPDATE**: mc-runtime-test Does NOT Support Paper!

### Key Discovery - mc-runtime-test Limitations:
- **Supported Modloaders**: Forge, Fabric, NeoForge (**NO Paper support**)
- **Focus**: Client-side mods, not server plugins
- **GitHub Action Options**: `forge`, `neoforge`, `fabric` (**NO `paper` option**)
- **Target**: "Minecraft mods" (client-side), not server plugins

### Re-examined Framework Comparison

#### 1. MC Runtime Test + HeadlessMC
- **Paper Server Support**: ❌ **NO** - Only supports Forge/Fabric/NeoForge client mods
- **Paper Plugin Testing**: ❌ **NO** - Designed for client-side mods
- **Focus**: Client-side mod testing, not server plugin testing

#### 2. PILAF Framework
- **Paper Server Support**: ✅ Custom implementation for Paper plugins
- **Plugin Testing**: ✅ Server-side plugin testing capability
- **Custom Development**: Requires ongoing maintenance but works for Paper

## **CORRECTED RECOMMENDATION**: Continue with PILAF ✅

### Why PILAF is Actually Necessary:
1. **No Viable Alternative**: mc-runtime-test doesn't support Paper plugins
2. **Server vs Client**: We need server-side testing, they provide client-side testing
3. **Paper-Specific**: PILAF designed specifically for Paper plugin testing
4. **Underserved Market**: Paper plugin testing is not well-served by existing frameworks

### Lessons Learned:
1. **Research Thoroughly**: Always verify compatibility claims
2. **Server vs Client**: Understand the difference between mod testing and plugin testing
3. **Framework Mismatch**: Client-side testing frameworks don't work for server plugins
4. **PILAF Value**: Our custom framework may be the only viable solution for Paper plugins

## Decision: **CONTINUE WITH PILAF** ✅

### Reasons to Stick with PILAF:
1. **Paper Plugin Testing**: Only viable solution for Paper server plugin testing
2. **Server-Side Focus**: Tests plugin-server interactions correctly
3. **No Alternative**: Existing frameworks don't support Paper plugins
4. **Community Need**: Paper plugin testing is underserved
5. **Existing Investment**: Significant infrastructure already built

### Next Steps:
1. **Fix PILAF compilation issues** and complete implementation
2. **Focus on Paper plugin testing** capabilities
3. **Consider open-sourcing PILAF** to help other Paper plugin developers
4. **Document Paper plugin testing challenges** and solutions

## Conclusion

The discovery that **mc-runtime-test does NOT support Paper plugins** makes PILAF not just the best option, but **the only viable option** for Paper plugin testing. The existing frameworks solve different problems (client-side mod testing) than our needs (server-side plugin testing).

**Corrected Decision**: **CONTINUE WITH PILAF** - It's not just the best option, it's the **ONLY** option that addresses Paper plugin testing needs.
