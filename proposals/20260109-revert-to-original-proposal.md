# Implementation Proposal: Revert to Original Design (20260103)

**Date**: January 9, 2026, 16:15 UTC+8
**Status**: APPROVED for implementation
**Version**: Phase 7 - Original Design Alignment

## Executive Summary

This proposal outlines the complete implementation plan to align the Elemental Dragon plugin with the [original proposal](20260103-elemental-dragon-original.md:1) dated January 3, 2026. All fragment abilities, materials, cooldowns, and crafting quantities must match the original specification exactly.

**Critical Requirements**:
1. ✅ Change ALL fragment materials to original specifications
2. ✅ Reimplement ALL 8 abilities to match original mechanics (6 are completely different)
3. ✅ Update ALL cooldowns to original values
4. ✅ Remove crafting quantity restrictions (allow 2 of each fragment type, 1 Corrupted Core)
5. ✅ Design and implement passive bonuses for each fragment
6. ✅ Keep fragment NAMES unchanged (architecture requirement)

---

## Part 1: Material Changes

### Current vs Original Materials

| Fragment | Current Material | Original Material | Change Required |
|----------|-----------------|-------------------|-----------------|
| Burning Fragment | `BLAZE_POWDER` | `FIRE_CHARGE` | ✅ YES |
| Agility Fragment | `FEATHER` | `WIND_CHARGE` | ✅ YES |
| Immortal Fragment | `TOTEM_OF_UNDYING` | `TOTEM_OF_UNDYING` | ❌ NO (same) |
| Corrupted Core | `NETHER_STAR` | `HEAVY_CORE` | ✅ YES |

### Implementation

Update [`BurningFragment.java:57`](../src/main/java/org/cavarest/elementaldragon/fragment/BurningFragment.java:57):
```java
// OLD: private static final Material MATERIAL = Material.BLAZE_POWDER;
private static final Material MATERIAL = Material.FIRE_CHARGE;
```

Update [`AgilityFragment.java:56`](../src/main/java/org/cavarest/elementaldragon/fragment/AgilityFragment.java:56):
```java
// OLD: private static final Material MATERIAL = Material.FEATHER;
private static final Material MATERIAL = Material.WIND_CHARGE;
```

Update [`CorruptedCoreFragment.java` (similar location)]:
```java
// OLD: private static final Material MATERIAL = Material.NETHER_STAR;
private static final Material MATERIAL = Material.HEAVY_CORE;
```

**Note**: Fragment names remain unchanged (architectural requirement). Only the visual representation (material) changes.

---

## Part 2: Ability Reimplementation

**IMPORTANT**: All abilities already have excellent draconic names (Dragon's Wrath, Infernal Dominion, Draconic Surge, Wing Burst, Draconic Reflex, Essence Rebirth, Dread Gaze, Life Devourer). These names are KEPT unchanged. Only the **mechanics** need to change to match the original proposal specifications.

### 2.1 Burning Fragment Abilities

#### **Ability 1: Dragon's Wrath (Keep Name, Change Mechanics to Targeted Fireball)**

**Original Specification** ([Line 40-43](20260103-elemental-dragon-original.md:40)):
- Summons fireball targeting closest entity **in 10 ticks**
- Deals **3 hearts damage** (6.0 damage)
- Negates armor
- Affects all players in **5 block radius**
- **Cooldown: 2 minutes (120 seconds)**

**Current Implementation** ([`BurningFragment.java:222-263`](../src/main/java/org/cavarest/elementaldragon/fragment/BurningFragment.java:222)):
- **Name**: "Dragon's Wrath" ✅ (KEEP THIS)
- Launches fireball in direction player faces
- 4 hearts damage (8.0)
- 2.0 explosion radius
- **Cooldown: 40 seconds**

**Changes Required**:
1. **KEEP name**: "Dragon's Wrath"
2. Change targeting system: Find closest hostile entity within range
3. Fireball must home/track target for 10 ticks (0.5 seconds)
4. Change damage from 8.0 → 6.0 (3 hearts)
5. Change explosion radius from 2.0 → None (direct hit + 5 block radius effect)
6. Update cooldown constant from 40000L → 120000L
7. Make damage ignore armor (use custom damage calculation)
8. Apply damage to all players within 5 blocks of impact point

#### **Ability 2: Infernal Dominion (Keep Name, Change Mechanics to Player-Only AOE)**

**Original Specification** ([Line 46-49](20260103-elemental-dragon-original.md:46)):
- Sets all **players (except wielder)** in 10 block radius on fire
- Deals **1 heart per second** for up to **10 hearts** total
- **Orange circle** visible to all players (visual marker on ground)
- **Cooldown: 3 minutes (180 seconds)**

**Current Implementation** ([`BurningFragment.java:270-323`](../src/main/java/org/cavarest/elementaldragon/fragment/BurningFragment.java:270)):
- **Name**: "Infernal Dominion" ✅ (KEEP THIS)
- Damages all entities (not just players) in 8 block radius
- Deals 2 hearts per tick
- Duration 100 ticks (5 seconds)
- **Cooldown: 60 seconds**

**Changes Required**:
1. **KEEP name**: "Infernal Dominion"
2. Change target selection: **Players only** (exclude wielder, exclude mobs)
3. Change radius from 8.0 → 10.0 blocks
4. Change damage: Apply fire ticks + direct damage totaling ~10 hearts over duration
5. Add orange particle circle on ground (visible marker at radius edge)
6. Update cooldown constant from 60000L → 180000L
7. Duration calculation: 1 heart/sec for 10 seconds = 200 ticks minimum

### 2.2 Agility Fragment Abilities

#### **Ability 1: Draconic Surge (Keep Name, Change Mechanics to Dash)**

**Original Specification** ([Line 98-101](20260103-elemental-dragon-original.md:98)):
- Dashes player **20 blocks** in direction they are looking
- Completes within **1.5 seconds**
- **Negates damage** while flying and during landing
- **Cooldown: 45 seconds**

**Current Implementation** ([`AgilityFragment.java:221-269`](../src/main/java/org/cavarest/elementaldragon/fragment/AgilityFragment.java:221)):
- **Name**: "Draconic Surge" ✅ (KEEP THIS)
- Applies Speed II + Jump Boost II for 10 seconds
- No dash mechanic
- No damage negation
- **Cooldown: 30 seconds**

**Changes Required**:
1. **KEEP name**: "Draconic Surge"
2. **Complete rewrite**: Remove potion effects entirely
3. Calculate velocity vector for 20 block movement in 1.5 seconds (30 ticks)
4. Apply continuous velocity over 30 ticks using BukkitRunnable
5. Make player invulnerable during dash (EntityDamageEvent cancellation)
6. Extend invulnerability for 1 second after dash completes (landing protection)
7. Update cooldown constant from 30000L → 45000L
8. Add wind trail particles during dash

#### **Ability 2: Wing Burst (Keep Name, Change Mechanics to Knockback AOE)**

**Original Specification** ([Line 103-106](20260103-elemental-dragon-original.md:103)):
- Pushes **all players** in 8 block radius **20 blocks away** from wielder
- Push completes within **2 seconds**
- Applies **slow falling for 10 seconds**
- **Cooldown: 2 minutes (120 seconds)**

**Current Implementation** ([`AgilityFragment.java:276-359`](../src/main/java/org/cavarest/elementaldragon/fragment/AgilityFragment.java:276)):
- **Name**: "Wing Burst" ✅ (KEEP THIS)
- Vertical launch (levitation)
- Forward momentum
- Slow falling for 3 seconds
- **Cooldown: 45 seconds**

**Changes Required**:
1. **KEEP name**: "Wing Burst"
2. **Complete rewrite**: Remove vertical launch mechanics
3. Target selection: **All players** in 8 block radius (except wielder)
4. Calculate knockback vector: Away from wielder, horizontal only
5. Apply velocity over 40 ticks (2 seconds) for smooth 20 block push
6. Apply SLOW_FALLING effect for 200 ticks (10 seconds) to all affected players
7. Update cooldown constant from 45000L → 120000L
8. Add wind burst particle effect at origin

### 2.3 Immortal Fragment Abilities

#### **Ability 1: Draconic Reflex (Keep Name, Change Mechanics to Dodge Chance)**

**Original Specification** ([Line 152-155](20260103-elemental-dragon-original.md:152)):
- Provides **1/5 chance (20%) to avoid damage** for 15 seconds after activation
- Plays **anvil sound on miss** (when dodge fails)
- **Cooldown: 2 minutes (120 seconds)**

**Current Implementation** ([`ImmortalFragment.java:231-301`](../src/main/java/org/cavarest/elementaldragon/fragment/ImmortalFragment.java:231)):
- **Name**: "Draconic Reflex" ✅ (KEEP THIS)
- 75% damage reduction (Resistance II)
- 25% melee damage reflection
- Duration: 5 seconds
- **Cooldown: 90 seconds**

**Changes Required**:
1. **KEEP name**: "Draconic Reflex"
2. **Complete rewrite**: Remove Resistance potion effect
3. Remove damage reflection mechanic entirely
4. Implement random dodge: 20% chance (Random.nextDouble() < 0.2)
5. On EntityDamageEvent: Roll dice, cancel if success, play sound if fail
6. Duration: 300 ticks (15 seconds)
7. Sound on miss: `Sound.BLOCK_ANVIL_LAND`
8. Update cooldown constant from 90000L → 120000L

#### **Ability 2: Essence Rebirth (Keep Name, Change Mechanics to Second Life)**

**Original Specification** ([Line 157-160](20260103-elemental-dragon-original.md:157)):
- Grants wielder **second life if reduced to 0 hearts**
- Active for **30 seconds post-activation**
- **Retains previous effects** (e.g., fire resistance, speed)
- **Cooldown: 8 minutes (480 seconds / 480000ms)**

**Current Implementation** ([`ImmortalFragment.java:308-569`](../src/main/java/org/cavarest/elementaldragon/fragment/ImmortalFragment.java:308)):
- **Name**: "Essence Rebirth" ✅ (KEEP THIS)
- Enhanced respawn with diamond armor, hunger, arrows
- Triggers on respawn event
- **Cooldown: 5 minutes (300 seconds)**

**Changes Required**:
1. **KEEP name**: "Essence Rebirth"
2. **Partial rewrite**: Change trigger from respawn to fatal damage prevention
3. On EntityDamageEvent where player.getHealth() - damage <= 0:
   - Cancel event if within 30 second window and ability active
   - Set player health to full (20.0)
   - Keep all active potion effects
   - Play totem animation/sound
4. Remove diamond armor / hunger / arrow mechanics entirely
5. Update cooldown constant from 300000L → 480000L
6. Duration window: 600 ticks (30 seconds) after activation

### 2.4 Corrupted Core Abilities

#### **Ability 1: Dread Gaze (Keep Name, Change Mechanics to Complete Freeze)**

**Original Specification** ([Line 206-209](20260103-elemental-dragon-original.md:206)):
- Prevents victim from **acting** (moving, eating, attacking, etc.) upon being hit
- **Cooldown: 3 minutes (180 seconds)**

**Current Implementation** (CorruptedCoreFragment equivalent):
- **Name**: "Dread Gaze" ✅ (KEEP THIS)
- Applies Blindness II for 5 seconds
- 10 block radius AOE
- **Cooldown: 60 seconds**

**Changes Required**:
1. **KEEP name**: "Dread Gaze"
2. **Complete rewrite**: Remove Blindness effect
3. Change to single-target on-hit effect (requires melee attack event)
4. On EntityDamageByEntityEvent where attacker has Corrupted Core equipped:
   - Apply effect preventing ALL actions:
     - SLOW at max level (complete movement freeze)
     - MINING_FATIGUE at max level (no block breaking)
     - WEAKNESS at max level (no damage dealing)
     - HUNGER at max level (no eating)
   - Duration: TBD (not specified in original, suggest 5-10 seconds)
5. Update cooldown constant from 60000L → 180000L
6. Visual: Dark particles around victim

#### **Ability 2: Life Devourer (Keep Name, Adjust Mechanics for Extended Duration)**

**Original Specification** ([Line 210-213](20260103-elemental-dragon-original.md:210)):
- Applies **"life steal"**: healing wielder **half the damage dealt** to any entity
- Active for **20 seconds**
- **Cooldown: 2 minutes (120 seconds)**

**Current Implementation** (CorruptedCoreFragment equivalent):
- **Name**: "Life Devourer" ✅ (KEEP THIS)
- Health steal 50% of damage dealt
- 8 block range
- 8 seconds duration
- **Cooldown: 90 seconds**

**Changes Required**:
1. **KEEP name**: "Life Devourer"
2. Keep 50% heal mechanic (already correct)
3. Change duration from 8 seconds → 20 seconds (400 ticks)
4. Make effect apply to ANY entity damage (not just nearby)
5. On EntityDamageByEntityEvent: If attacker has life steal active, heal 50% of damage
6. Update cooldown constant from 90000L → 120000L

---

## Part 3: Cooldown Changes Summary

| Fragment | Ability | Current | Original | Change |
|----------|---------|---------|----------|--------|
| Burning | Ability 1 | 40s | 120s | +80s |
| Burning | Ability 2 | 60s | 180s | +120s |
| Agility | Ability 1 | 30s | 45s | +15s |
| Agility | Ability 2 | 45s | 120s | +75s |
| Immortal | Ability 1 | 90s | 120s | +30s |
| Immortal | Ability 2 | 300s | 480s | +180s |
| Corrupted | Ability 1 | 60s | 180s | +120s |
| Corrupted | Ability 2 | 90s | 120s | +30s |

Update constants in each fragment class.

---

## Part 4: Passive Bonus System

**NEW REQUIREMENT**: Each fragment must grant a powerful passive bonus while equipped.

### Design Principles

1. **Always active** while fragment is equipped in offhand
2. **Automatically applied** on equip, removed on unequip
3. **Visible to player** through status effects or attribute changes
4. **Thematically appropriate** to fragment's element
5. **Balanced but noticeable** in gameplay

### Proposed Passive Bonuses

#### 🔥 Burning Fragment Passive
**Name**: "Dragon's Scales"
**Effect**: Fire Resistance + Flame damage on attackers
**Implementation**:
- Permanent Fire Resistance effect (already implemented)
- Add: Thorns-like fire damage (1 heart) to melee attackers
- Visual: Faint fire particles around player

#### 💨 Agility Fragment Passive
**Name**: "Wind Walker"
**Effect**: Permanent Speed I + Reduced fall damage
**Implementation**:
- Permanent Speed I effect (already partially implemented)
- Add: 50% fall damage reduction
- Add: No slow-down in soul sand/honey blocks
- Visual: Cloud particles trail behind player

#### 🛡️ Immortal Fragment Passive
**Name**: "Stone Skin"
**Effect**: +2 hearts max health + Minor knockback resistance
**Implementation**:
- Permanent +4.0 max health attribute (already implemented)
- Add: 25% knockback resistance attribute modifier
- Add: Slight regeneration (Regeneration 0 with very low duration, refresh frequently)
- Visual: Brown/green particles around player

#### 💀 Corrupted Core Passive
**Name**: "Void Shroud"
**Effect**: Night Vision + Weakened enemy detection
**Implementation**:
- Permanent Night Vision effect
- Add: Invisible to Endermen (prevent aggro)
- Add: Creepers have reduced detection range (80% normal range)
- Visual: Dark purple particles swirling around player

### Implementation Location

Add to each Fragment class:
1. New constants for passive bonus values
2. `applyPassiveEffects(Player player)` - Called on equip
3. `removePassiveEffects(Player player)` - Called on unequip
4. Event listeners for passive mechanics (thorns, fall damage, etc.)

---

## Part 5: Crafting Quantity System

### Current Restriction

The plugin currently **prevents crafting multiple fragments** due to Single Source inventory restriction logic.

**Location**: [`CraftingListener.java`] or [`FragmentManager.java`] (TBD - needs investigation)

### Original Specification

**From proposal** ([Line 17-18, 76, 131, 186](20260103-elemental-dragon-original.md:17)):
- **Burning Fragment**: Craftable Quantity = **2**
- **Agility Fragment**: Craftable Quantity = **2**
- **Immortal Fragment**: Craftable Quantity = **2**
- **Corrupted Core**: Craftable Quantity = **1**

### Required Changes

1. **Remove single-instance enforcement** from crafting validation
2. **Allow players to craft up to N copies** based on fragment type:
   ```java
   public int getMaxCraftableQuantity(FragmentType type) {
       return type == FragmentType.CORRUPTED ? 1 : 2;
   }
   ```
3. **Track crafted count per player** (persistent data):
   ```java
   // Store in player metadata or data file
   Map<UUID, Map<FragmentType, Integer>> craftedCounts;
   ```
4. **Block crafting when limit reached**:
   ```java
   if (getCraftedCount(player, fragmentType) >= getMaxCraftableQuantity(fragmentType)) {
       event.setCancelled(true);
       player.sendMessage("You have already crafted the maximum amount of this fragment!");
   }
   ```
5. **Display crafted quantity** in `/craft` command and fragment tooltips

### HUD Display Changes

**Original Specification** ([Lines 51-59, 109-117, 163-171, 216-224](20260103-elemental-dragon-original.md:51)):
- **Location**: Middle-left of the screen
- **During Cooldown**: Display remaining time in minutes (only once below 1 minute) and seconds
  - Format: "7 minutes, 59 seconds" when > 1 minute
  - Format: "59 seconds" when ≤ 1 minute
- **When Ready**: Display custom message per ability:
  - Burning 1: "Fireball fully charged"
  - Burning 2: "Furnace ready"
  - Agility 1: "Dash ready"
  - Agility 2: "Continental explosion ready"
  - Immortal 1: "Instinctive weave ready"
  - Immortal 2: "2nd life ready"
  - Corrupted 1: "Corrupted freeze ready"
  - Corrupted 2: "Heart stealer ready"

**Current Implementation**: [`FragmentHudManager.java`] and [`HudManager.java`]

**Changes Required**:
1. Move HUD position to middle-left (currently uses action bar)
2. Format cooldown display with minute/second split
3. Add custom ready messages per ability
4. Show both ability cooldowns simultaneously (2 lines)
5. Ensure messages don't overlap and are centered

---

## Part 6: Testing Requirements

### Unit Tests to Update

1. **Ability Tests**: All 8 ability tests need complete rewrites
   - Test targeting mechanics
   - Test damage values
   - Test duration/timing
   - Test cooldown enforcement
   - Test edge cases (no targets, out of range, etc.)

2. **Material Tests**: Verify fragment material changes
3. **Crafting Tests**: Verify quantity restrictions
4. **Passive Tests**: Test passive bonus application/removal

### Integration Tests to Create

1. **Ability Execution Stories**: YAML stories for each ability
2. **Cooldown Stories**: Verify original cooldown timings
3. **Quantity Stories**: Verify crafting restrictions
4. **Passive Stories**: Verify passive effects persist

### Manual Testing Checklist

- [ ] All 4 fragment materials display correctly
- [ ] Burning Fragment abilities match original spec
- [ ] Agility Fragment abilities match original spec
- [ ] Immortal Fragment abilities match original spec
- [ ] Corrupted Core abilities match original spec
- [ ] All cooldowns match original timings
- [ ] HUD displays show correct format and position
- [ ] Passive bonuses apply and remove correctly
- [ ] Crafting quantity restrictions work
- [ ] Can craft 2 of Burning/Agility/Immortal
- [ ] Can only craft 1 Corrupted Core
- [ ] All visual effects (particles, sounds) appropriate

---

## Part 7: Implementation Plan

### Phase 7.1: Material Changes (1 day)
- Day 1: Update fragment material constants
- Day 1: Update [`ElementalItems.java`] generation
- Day 1: Test visual appearance in-game
- Day 1: Update tooltip text if needed

### Phase 7.2: Ability Reimplementation (5 days)
- Day 2: Burning Fragment Ability 1 (targeted fireball)
- Day 3: Burning Fragment Ability 2 (player AOE fire)
- Day 4: Agility Fragment Ability 1 (20-block dash)
- Day 5: Agility Fragment Ability 2 (knockback AOE)
- Day 6: Immortal Fragment Ability 1 (dodge chance)

### Phase 7.3: Ability Completion (3 days)
- Day 7: Immortal Fragment Ability 2 (second life)
- Day 8: Corrupted Core Ability 1 (complete freeze)
- Day 9: Corrupted Core Ability 2 (extended life steal)

### Phase 7.4: Passive System (2 days)
- Day 10: Design and implement passive bonus framework
- Day 11: Implement all 4 passive bonuses

### Phase 7.5: Cooldown & HUD (2 days)
- Day 12: Update all cooldown constants
- Day 13: Rewrite HUD system for middle-left display

### Phase 7.6: Crafting System (2 days)
- Day 14: Implement quantity tracking system
- Day 15: Add crafting restrictions and displays

### Phase 7.7: Testing & Polish (3 days)
- Day 16: Unit test updates
- Day 17: Integration test creation
- Day 18: Manual testing and bug fixes

**Total Estimated Time**: 18 working days (~3.5 weeks)

---

## Part 8: Risk Assessment

### High Risk Items

1. **Dash Mechanic**: 20-block movement in 1.5 seconds requires precise velocity calculations
   - **Mitigation**: Test extensively, use smooth teleportation as fallback

2. **Targeted Fireball**: Homing projectile in 10 ticks is complex
   - **Mitigation**: Use BukkitRunnable to adjust fireball direction each tick

3. **Second Life Mechanic**: Preventing death while keeping effects is tricky
   - **Mitigation**: Study Totem of Undying implementation

4. **Complete Freeze**: Preventing all player actions requires multiple effect stacking
   - **Mitigation**: Test each prevention type individually

### Medium Risk Items

1. **HUD Position**: Middle-left display may conflict with other UI elements
2. **Passive Persistence**: Effects must survive logout/login
3. **Quantity Tracking**: Must be saved persistently per player

### Low Risk Items

1. Material changes (straightforward)
2. Cooldown updates (simple constants)
3. Particle effects (cosmetic)

---

## Part 9: Backward Compatibility

### Breaking Changes

⚠️ **This update breaks existing player data**:
1. Players with current fragments will have different abilities
2. Passive bonuses will change
3. Cooldowns will be longer
4. Some abilities completely change mechanics

### Migration Strategy

**Option 1: Hard Reset** (Recommended)
- Announce update with full changelog
- Warn players abilities are changing
- Consider refunding resources for existing fragments
- Reset all fragment-related player data

**Option 2: Grandfather Existing Fragments**
- Allow existing fragments to keep old mechanics
- New crafts use new system
- More complex to implement and maintain

**Recommendation**: Choose Option 1 (Hard Reset) for clean implementation

---

## Part 10: Success Criteria

### Functional Requirements
- [ ] All 4 fragment visuals match original materials
- [ ] All 8 abilities match original mechanics exactly
- [ ] All 8 cooldowns match original timings exactly
- [ ] All 4 passive bonuses functional
- [ ] Crafting quantities enforced (2/2/2/1)
- [ ] HUD displays in correct position with correct format

### Quality Requirements
- [ ] Zero regression bugs in existing features
- [ ] All unit tests passing (197+ tests)
- [ ] All integration tests passing
- [ ] Performance impact < 5% (same as before)
- [ ] Manual testing checklist 100% complete

### Documentation Requirements
- [ ] Update README.md with new abilities
- [ ] Update all command help text
- [ ] Update fragment tooltips
- [ ] Create migration guide for players
- [ ] Update memory bank files

---

## Conclusion

This proposal provides a complete roadmap to align the Elemental Dragon plugin with the original January 3, 2026 design specification. The implementation requires significant code changes but will result in a plugin that exactly matches the approved design.

**Estimated Effort**: 18 working days
**Risk Level**: Medium-High (complex mechanics)
**Impact**: Breaking change requiring player communication

**Next Steps**:
1. Obtain final approval from project team
2. Create GitHub issue for tracking
3. Begin Phase 7.1 (Material Changes)
4. Update memory bank after each phase completion

---

## Appendix A: Code File Locations

### Files Requiring Major Changes
- [`BurningFragment.java`](../src/main/java/org/cavarest/elementaldragon/fragment/BurningFragment.java)
- [`AgilityFragment.java`](../src/main/java/org/cavarest/elementaldragon/fragment/AgilityFragment.java)
- [`ImmortalFragment.java`](../src/main/java/org/cavarest/elementaldragon/fragment/ImmortalFragment.java)
- [`CorruptedCoreFragment.java`]
- [`ElementalItems.java`](../src/main/java/org/cavarest/elementaldragon/item/ElementalItems.java)
- [`FragmentHudManager.java`]
- [`CraftingManager.java`](../src/main/java/org/cavarest/elementaldragon/crafting/CraftingManager.java)
- [`CraftingListener.java`]

### Files Requiring Minor Changes
- [`AbstractFragment.java`] - Add passive bonus framework
- [`FragmentManager.java`] - Add quantity tracking
- All fragment command classes - Update help text

### New Files Required
- `QuantityTracker.java` - Track crafted quantities per player
- `PassiveEffectManager.java` - Manage passive bonus lifecycle