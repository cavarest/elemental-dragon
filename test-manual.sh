#!/bin/bash

#===============================================================================
# Elemental Dragon Plugin - Manual Testing Script
#===============================================================================
# Purpose: Step-by-step testing procedures for all plugin features
# Usage:   ./test-manual.sh
# Requirements: Server running, Minecraft client connected
#===============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# Checkpoint tracking
declare -A CHECKPOINTS
TOTAL_CHECKPOINTS=0
PASSED_CHECKPOINTS=0
FAILED_CHECKPOINTS=0

#-------------------------------------------------------------------------------
# Utility Functions
#-------------------------------------------------------------------------------

print_header() {
    echo -e "\n${CYAN}═══════════════════════════════════════════════════════════════════════${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════════════${NC}\n"
}

print_section() {
    echo -e "\n${BLUE}─── $1 ───${NC}\n"
}

print_step() {
    echo -e "${YELLOW}Step $1:${NC} $2"
}

print_expected() {
    echo -e "${MAGENTA}Expected:${NC} $1"
}

print_result() {
    local status=$1
    local message=$2
    if [ "$status" = "PASS" ]; then
        echo -e "${GREEN}✓ PASS:${NC} $message"
        ((PASSED_CHECKPOINTS++))
    else
        echo -e "${RED}✗ FAIL:${NC} $message"
        ((FAILED_CHECKPOINTS++))
    fi
    ((TOTAL_CHECKPOINTS++))
    CHECKPOINTS["$message"]=$status
}

print_info() {
    echo -e "${CYAN}ℹ  INFO:${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠  WARNING:${NC} $1"
}

pause_for_user() {
    echo -e "\n${YELLOW}Press Enter to continue...${NC}"
    read -r
}

yes_no_prompt() {
    local prompt="$1"
    local response
    while true; do
        echo -e "${YELLOW}$prompt (y/n):${NC}"
        read -r response
        case $response in
            [Yy]*)
                return 0
                ;;
            [Nn]*)
                return 1
                ;;
            *)
                echo "Please answer yes (y) or no (n)."
                ;;
        esac
    done
}

get_user_input() {
    local prompt="$1"
    echo -e "${YELLOW}$prompt${NC}"
    read -r response
    echo "$response"
}

#-------------------------------------------------------------------------------
# Test Summary Display
#-------------------------------------------------------------------------------

show_summary() {
    print_header "TEST SUMMARY"

    echo -e "Total Checkpoints: ${TOTAL_CHECKPOINTS}"
    echo -e "${GREEN}Passed: ${PASSED_CHECKPOINTS}${NC}"
    echo -e "${RED}Failed: ${FAILED_CHECKPOINTS}${NC}"

    local pass_rate=$((PASSED_CHECKPOINTS * 100 / TOTAL_CHECKPOINTS))
    echo -e "\nPass Rate: ${pass_rate}%"

    if [ $FAILED_CHECKPOINTS -eq 0 ]; then
        echo -e "\n${GREEN}🎉 ALL TESTS PASSED! 🎉${NC}"
    else
        echo -e "\n${YELLOW}Some tests failed. Review the output above for details.${NC}"
    fi
}

#-------------------------------------------------------------------------------
# Section 1: Server Setup & Verification
#-------------------------------------------------------------------------------

test_server_setup() {
    print_header "SECTION 1: SERVER SETUP & VERIFICATION"

    print_section "1.1 Build and Start Server"
    print_step "1.1.1" "Build plugin with production settings"
    print_expected "Plugin JAR built successfully, no errors"
    print_info "Command: ./build.sh --production"

    print_step "1.1.2" "Start server with Docker"
    print_expected "Container starts, server initializes"
    print_info "Command: ./start-server.sh"

    pause_for_user

    print_section "1.2 Plugin Verification"
    print_step "1.2.1" "Execute plugin info command"
    print_expected "Message: 'Elemental Dragon v1.1.0 - Plugin enabled!'"
    print_info "Command: /elementaldragon info or /ed info"

    print_step "1.2.2" "Verify plugin version"
    print_expected "Version displayed correctly"
    print_info "Check console for plugin load messages"

    print_step "1.2.3" "Check registered commands"
    print_expected "All commands registered: lightning, ability, elementaldragon, ed, fragment, chronicle, craft"
    print_info "Command: /help (look for plugin commands)"

    print_section "1.3 Permission Verification"
    print_step "1.3.1" "Check lightning permission"
    print_expected "Player has 'elementaldragon.lightning' permission"
    print_info "OP players have all permissions by default"

    print_step "1.3.2" "Check admin permission"
    print_expected "OP players have 'elementaldragon.admin' permission"
    print_info "Command: /op <player> (if needed)"
}

#-------------------------------------------------------------------------------
# Section 2: Lightning Ability Testing
#-------------------------------------------------------------------------------

test_lightning_ability() {
    print_header "SECTION 2: LIGHTNING ABILITY TESTING"

    print_section "2.1 Preparation"
    print_step "2.1.1" "Get a Dragon Egg"
    print_expected "Player receives dragon_egg item"
    print_info "Command: /give @p dragon_egg 1"

    print_step "2.1.2" "Place Dragon Egg in offhand"
    print_expected "Dragon egg appears in offhand slot"
    print_info "Drag from inventory to helmet slot indicator (offhand)"

    print_step "2.1.3" "Verify offhand detection"
    print_expected "Action bar shows 'Lightning READY' message"
    print_info "Look at action bar above hotbar"

    print_section "2.2 Lightning Strike Test"
    print_step "2.2.1" "Spawn a test zombie"
    print_expected "Zombie spawns with custom name 'Test Zombie', 20 health"
    print_info "Command: /summon zombie ~ ~ ~ {CustomName:'\"Test Zombie\"',Health:20}"

    print_step "2.2.2" "Execute lightning command"
    print_expected "3 lightning strikes occur sequentially, zombie takes 6 hearts damage"
    print_info "Command: /lightning 1 or /ability 1"

    print_step "2.2.3" "Verify zombie death (if 3 strikes land)"
    print_expected "Zombie dies after 3 strikes (6 hearts total damage)"
    print_info "Each strike deals 2 hearts (4 damage)"

    print_step "2.2.4" "Verify visual effects"
    print_expected "Purple lightning particles, thunder sound"
    print_info "Observe particle effects during strikes"

    print_section "2.3 Cooldown Verification"
    print_step "2.3.1" "Check cooldown message"
    print_expected "Message: 'Lightning ability on cooldown: XXs'"
    print_info "Action bar should show countdown"

    print_step "2.3.2" "Wait for cooldown to expire"
    print_expected "After 60 seconds, action bar shows 'Lightning READY'"
    print_info "Use a timer to verify 60-second duration"

    print_step "2.3.3" "Test lightning again after cooldown"
    print_expected "Lightning strikes work again"
    print_info "Command: /lightning 1"

    print_section "2.4 Targeting Test"
    print_step "2.4.1" "Spawn multiple hostile mobs"
    print_expected "Multiple zombies/spiders spawn at different distances"
    print_info "Create test scenario with mobs at various distances"

    print_step "2.4.2" "Test target selection"
    print_expected "Lightning strikes closest hostile mob in player's view"
    print_info "Aim at different mobs and test targeting"

    print_step "2.4.3" "Test target switching"
    print_expected "If first target dies, lightning switches to next closest"
    print_info "Spawn weak mobs and verify target switching"

    print_section "2.5 Edge Cases"
    print_step "2.5.1" "Test without dragon egg in offhand"
    print_expected "Error message: 'Hold a dragon egg in your offhand first'"
    print_info "Remove dragon egg and try /lightning 1"

    print_step "2.5.2" "Test with no hostile mobs nearby"
    print_expected "Message: 'No hostile targets found in range'"
    print_info "Go to peaceful area and test"

    print_step "2.5.3" "Test maximum range"
    print_expected "Lightning works up to 50 blocks (default range)"
    print_info "Spawn mob at 50+ blocks and test"
}

#-------------------------------------------------------------------------------
# Section 3: Fragment Testing
#-------------------------------------------------------------------------------

test_burning_fragment() {
    print_header "SECTION 3: FRAGMENT TESTING - BURNING FRAGMENT"

    print_section "3.1 Crafting Heavy Core (Prerequisite)"
    print_step "3.1.1" "Get crafting ingredients"
    print_expected "1 Dragon Egg, 9 Obsidian, 1 Iron Block"
    print_info "OP permissions required for custom crafting"

    print_step "3.1.2" "Craft Heavy Core"
    print_expected "Heavy Core item created"
    print_info "Recipe: 3x3 grid with obsidian, dragon egg center, iron block bottom"

    print_section "3.2 Craft Burning Fragment"
    print_step "3.2.1" "Get fragment ingredients"
    print_expected "1 Heavy Core, Blaze Powder, Magma Block, Fire Charges"
    print_info "Ingredients needed for burning fragment recipe"

    print_step "3.2.2" "Craft Burning Fragment"
    print_expected "Burning Fragment item created"
    print_info "Use /craft heavy_core to view recipe"

    print_section "3.3 Equip Fragment"
    print_step "3.3.1" "Equip Burning Fragment"
    print_expected "Purple message: 'Equipped Burning Fragment'"
    print_info "Command: /fragment burning"

    print_step "3.3.2" "Check action bar"
    print_expected "Shows: [Fragment: Burning 🔥] [Ability 1: READY] [Ability 2: READY]"
    print_info "Look at action bar for HUD display"

    print_step "3.3.3" "Verify passive effects"
    print_expected "Fire particles around player, possible fire resistance"
    print_info "Observe visual effects"

    print_section "3.4 Dragon's Wrath (Ability 1)"
    print_step "3.4.1" "Use Dragon's Wrath"
    print_expected "Fireball launches toward cursor direction"
    print_info "Command: /fragment 1"

    print_step "3.4.2" "Verify fireball impact"
    print_expected "Fireball explodes on impact, deals 3 hearts direct damage"
    print_info "Spawn test mob and fire at it"

    print_step "3.4.3" "Verify area damage"
    print_expected "1.5 hearts splash damage to nearby entities"
    print_info "Spawn multiple mobs at different distances"

    print_step "3.4.4" "Verify fire spread"
    print_expected "Ground catches fire where fireball lands"
    print_info "Observe fire spread on blocks"

    print_section "3.5 Infernal Dominion (Ability 2)"
    print_step "3.5.1" "Use Infernal Dominion"
    print_expected "Fire ring appears around player"
    print_info "Command: /fragment 2"

    print_step "3.5.2" "Verify damage effect"
    print_expected "Enemies in ring take continuous damage"
    print_info "Spawn mob and stand near it"

    print_step "3.5.3" "Verify pushback"
    print_expected "Enemies are pushed away from fire ring"
    print_info "Observe mob movement"

    print_step "3.5.4" "Verify duration"
    print_expected "Fire ring lasts 8 seconds"
    print_info "Use timer to verify duration"

    print_section "3.6 Fire Resistance Test"
    print_step "3.6.1" "Stand in lava with fragment equipped"
    print_expected "Take half damage from lava"
    print_info "Briefly step in lava (careful!)"

    print_step "3.6.2" "Compare damage without fragment"
    print_expected "Normal lava damage without fragment"
    print_info "Unequip fragment and repeat test"
}

test_agility_fragment() {
    print_header "SECTION 4: FRAGMENT TESTING - AGILITY FRAGMENT"

    print_section "4.1 Craft Agility Fragment"
    print_step "4.1.1" "Get ingredients"
    print_expected "1 Heavy Core, Feather, Sugar, Phantom Membrane"
    print_info "Gather materials for agility fragment"

    print_step "4.1.2" "Craft Agility Fragment"
    print_expected "Agility Fragment item created"
    print_info "Use /craft command to view recipe"

    print_section "4.2 Equip and Passive Effects"
    print_step "4.2.1" "Equip Agility Fragment"
    print_expected "Purple message: 'Equipped Agility Fragment'"
    print_info "Command: /fragment agility"

    print_step "4.2.2" "Verify passive Speed I"
    print_expected "Speed I effect active (particles, faster movement)"
    print_info "Check for speed particles and movement speed"

    print_section "4.3 Draconic Surge (Ability 1)"
    print_step "4.3.1" "Use Draconic Surge"
    print_expected "Speed II + Jump Boost II activated for 10 seconds"
    print_info "Command: /fragment 1"

    print_step "4.3.2" "Verify effect intensity"
    print_expected "Much faster movement, higher jumps"
    print_info "Compare to normal speed"

    print_step "4.3.3" "Verify water walking"
    print_expected "Can walk on water surface with water breathing"
    print_info "Find a body of water and test"

    print_step "4.3.4" "Verify duration"
    print_expected "Effects last 10 seconds"
    print_info "Use timer to verify"

    print_section "4.4 Wing Burst (Ability 2)"
    print_step "4.4.1" "Use Wing Burst"
    print_expected "Player launched 15 blocks upward"
    print_info "Command: /fragment 2"

    print_step "4.4.2" "Verify slow fall"
    print_expected "Slow Fall effect for 3 seconds"
    print_info "Observe descent speed"

    print_step "4.4.3" "Verify safe landing"
    print_expected "No fall damage received"
    print_info "Check health after landing"
}

test_immortal_fragment() {
    print_header "SECTION 5: FRAGMENT TESTING - IMMORTAL FRAGMENT"

    print_section "5.1 Craft Immortal Fragment"
    print_step "5.1.1" "Get ingredients"
    print_expected "1 Heavy Core, Diamond Block, Totem, Gold Blocks, Apple"
    print_info "Gather materials for immortal fragment"

    print_step "5.1.2" "Craft Immortal Fragment"
    print_expected "Immortal Fragment item created"
    print_info "Use /craft command to view recipe"

    print_section "5.2 Equip and Passive Effects"
    print_step "5.2.1" "Equip Immortal Fragment"
    print_expected "Purple message: 'Equipped Immortal Fragment'"
    print_info "Command: /fragment immortal"

    print_step "5.2.2" "Verify max health increase"
    print_expected "+2 hearts max health (total 12 hearts)"
    print_info "Check health bar - should show extra hearts"

    print_section "5.3 Draconic Reflex (Ability 1)"
    print_step "5.3.1" "Use Draconic Reflex"
    print_expected "75% damage reduction for 5 seconds"
    print_info "Command: /fragment 1"

    print_step "5.3.2" "Test damage reduction"
    print_expected "Take only 25% of normal damage"
    print_info "Get hit by mob and compare damage"

    print_step "5.3.3" "Verify reflected damage"
    print_expected "Melee attackers take 25% reflected damage"
    print_info "Hit mob while ability is active"

    print_step "5.3.4" "Verify duration"
    print_expected "Ability lasts 5 seconds"
    print_info "Use timer to verify"

    print_section "5.4 Essence Rebirth (Ability 2)"
    print_step "5.4.1" "Use Essence Rebirth"
    print_expected "Cooldown set to 5 minutes, ability ready on death"
    print_info "Command: /fragment 2"

    print_step "5.4.2" "Die and respawn"
    print_expected "Respawn with diamond armor, full hunger, arrows"
    print_info "Let a mob kill you (don't heal!)"

    print_step "5.4.3" "Verify resurrection gear"
    print_expected "Diamond armor equipped, hunger bar full, arrows in inventory"
    print_info "Check inventory and status after respawn"
}

test_corrupted_core() {
    print_header "SECTION 6: FRAGMENT TESTING - CORRUPTED CORE"

    print_section "6.1 Craft Corrupted Core"
    print_step "6.1.1" "Get ingredients"
    print_expected "1 Heavy Core, Nether Star, Obsidian, Purple Glass, Ender Pearl"
    print_info "Gather materials for corrupted core"

    print_step "6.1.2" "Craft Corrupted Core"
    print_expected "Corrupted Core item created"
    print_info "Use /craft command to view recipe"

    print_section "6.2 Equip and Passive Effects"
    print_step "6.2.1" "Equip Corrupted Core"
    print_expected "Purple message: 'Equipped Corrupted Core'"
    print_info "Command: /fragment corrupted"

    print_step "6.2.2" "Verify Night Vision"
    print_expected "Night Vision effect active (darker areas visible)"
    print_info "Go underground or at night to verify"

    print_section "6.3 Dread Gaze (Ability 1)"
    print_step "6.3.1" "Use Dread Gaze"
    print_expected "10 block radius cone, enemies get Blindness II for 5 seconds"
    print_info "Command: /fragment 1"

    print_step "6.3.2" "Verify blindness effect"
    print_expected "Enemies wander randomly, can't target player"
    print_info "Spawn zombie and use ability on it"

    print_step "6.3.3" "Verify radius"
    print_expected "Effect only within 10 blocks in front of player"
    print_info "Test at different distances and angles"

    print_section "6.4 Life Devourer (Ability 2)"
    print_step "6.4.1" "Use Life Devourer"
    print_expected "Health drain effect active for 8 seconds"
    print_info "Command: /fragment 2"

    print_step "6.4.2" "Verify health drain"
    print_expected "Nearby enemies lose health over time"
    print_info "Spawn mob and observe its health"

    print_step "6.4.3" "Verify health transfer"
    print_expected "50% of drained health given to player"
    print_info "Monitor player health bar"

    print_step "6.4.4" "Verify duration"
    print_expected "Effect lasts 8 seconds"
    print_info "Use timer to verify"

    print_section "6.5 Creeper Invisibility"
    print_step "6.5.1" "Approach creeper with Corrupted Core"
    print_expected "Creeper does not target/fuse"
    print_info "Walk toward creeper and observe behavior"

    print_step "6.5.2" "Unequip and test again"
    print_expected "Creeper targets normally without fragment"
    print_info "Unequip and repeat test"
}

#-------------------------------------------------------------------------------
# Section 4: Command Testing
#-------------------------------------------------------------------------------

test_commands() {
    print_header "SECTION 7: COMMAND TESTING"

    print_section "7.1 Lightning Commands"
    print_step "7.1.1" "Test /lightning 1"
    print_expected "Lightning ability executes"
    print_info "Must have dragon egg in offhand"

    print_step "7.1.2" "Test /ability alias"
    print_expected "Same behavior as /lightning 1"
    print_info "Command: /ability 1"

    print_section "7.2 Admin Commands"
    print_step "7.2.1" "Test /elementaldragon info"
    print_expected "Plugin information displayed"
    print_info "Shows version, status, enabled features"

    print_step "7.2.2" "Test /ed info (short alias)"
    print_expected "Same output as /elementaldragon info"
    print_info "Admin command alias"

    print_step "7.2.3" "Test /elementaldragon reload"
    print_expected "Plugin configuration reloaded"
    print_info "Command: /elementaldragon reload (requires admin)"

    print_step "7.2.4" "Test /elementaldragon reset"
    print_expected "Cooldowns reset for player"
    print_info "Command: /elementaldragon reset <player> (admin only)"

    print_step "7.2.5" "Test /elementaldragon status"
    print_expected "Shows plugin status and statistics"
    print_info "Command: /elementaldragon status"

    print_section "7.3 Fragment Commands"
    print_step "7.3.1" "Test /fragment status"
    print_expected "Shows current fragment and abilities"
    print_info "Displays equipped fragment info"

    print_step "7.3.2" "Test /fragment unequip"
    print_expected "Current fragment unequipped"
    print_info "Removes active fragment from player"

    print_step "7.3.3" "Test /fragment <type>"
    print_expected "Equips specified fragment type"
    print_info "Types: burning, agility, immortal, corrupted"

    print_step "7.3.4" "Test fragment abilities"
    print_expected "Respective abilities execute"
    print_info "Commands: /fragment 1, /fragment 2"

    print_section "7.4 Craft Command"
    print_step "7.4.1" "Test /craft heavy_core"
    print_expected "Heavy Core recipe displayed"
    print_info "Shows crafting grid and ingredients"

    print_step "7.4.2" "Test /craft <fragment>"
    print_expected "Specified fragment recipe displayed"
    print_info "Shows crafting recipe for fragments"

    print_section "7.5 Chronicle Commands"
    print_step "7.5.1" "Test /chronicle"
    print_expected "Lore book opens"
    print_info "Displays Chronicle of the Fallen Dragons"

    print_step "7.5.2" "Test /chronicle status"
    print_expected "Shows discovery progress"
    print_info "Displays which lore pages unlocked"
}

#-------------------------------------------------------------------------------
# Section 5: Cooldown & Persistence Testing
#-------------------------------------------------------------------------------

test_cooldown_persistence() {
    print_header "SECTION 8: COOLDOWN & PERSISTENCE TESTING"

    print_section "8.1 Lightning Cooldown Persistence"
    print_step "8.1.1" "Use lightning ability"
    print_expected "Cooldown timer starts (60 seconds)"
    print_info "Command: /lightning 1"

    print_step "8.1.2" "Disconnect from server"
    print_expected "Client disconnects normally"
    print_info "Exit Minecraft and close game"

    print_step "8.1.3" "Reconnect to server"
    print_expected "Player rejoins, cooldown still active"
    print_info "Reconnect within 60 seconds"

    print_step "8.1.4" "Check cooldown on reconnect"
    print_expected "Same remaining cooldown time"
    print_info "Action bar should show remaining time"

    print_section "8.2 Death Cooldown Clearing"
    print_step "8.2.1" "Use lightning (start cooldown)"
    print_expected "Cooldown timer starts"
    print_info "Command: /lightning 1"

    print_step "8.2.2" "Die while on cooldown"
    print_expected "Player dies and respawns"
    print_info "Let mob kill you or /kill"

    print_step "8.2.3" "Check cooldown after respawn"
    print_expected "Cooldown completely cleared"
    print_info "Lightning ability should be READY"

    print_section "8.3 Fragment Cooldown Persistence"
    print_step "8.3.1" "Use fragment ability"
    print_expected "Ability cooldown starts"
    print_info "Command: /fragment 1 or /fragment 2"

    print_step "8.3.2" "Disconnect and reconnect"
    print_expected "Player rejoins, cooldown persists"
    print_info "Reconnect before cooldown expires"

    print_step "8.3.3" "Verify fragment equipped"
    print_expected "Same fragment still equipped"
    print_info "Action bar should show fragment status"

    print_step "8.3.4" "Verify ability state"
    print_expected "Same cooldown remaining on reconnect"
    print_info "Check ability status in HUD"

    print_section "8.4 Heavy Core Drop on Death"
    print_step "8.4.1" "Equip fragment with Heavy Core"
    print_expected "Fragment equipped, Heavy Core in inventory"
    print_info "Craft and equip any fragment"

    print_step "8.4.2" "Die while equipped"
    print_expected "Player dies and respawns"
    print_info "Let mob kill you"

    print_step "8.4.3" "Check Heavy Core after death"
    print_expected "Heavy Core dropped on death ground"
    print_info "Find Heavy Core where you died"
}

#-------------------------------------------------------------------------------
# Section 6: Edge Cases & Error Handling
#-------------------------------------------------------------------------------

test_edge_cases() {
    print_header "SECTION 9: EDGE CASES & ERROR HANDLING"

    print_section "9.1 Fragment Edge Cases"
    print_step "9.1.1" "Use fragment ability without fragment equipped"
    print_expected "Error message: 'Equip a fragment first'"
    print_info "Command: /fragment 1 (no fragment equipped)"

    print_step "9.1.2" "Equip second fragment while one equipped"
    print_expected "Old fragment unequipped, new one equipped"
    print_info "Command: /fragment burning then /fragment agility"

    print_step "9.1.3" "Test invalid fragment type"
    print_expected "Error message or help text"
    print_info "Command: /fragment invalid_type"

    print_section "9.2 Dragon Egg Edge Cases"
    print_step "9.2.1" "Drop dragon egg while on cooldown"
    print_expected "Cooldown persists in memory"
    print_info "Drop egg and pick it up again"

    print_step "9.2.2" "Use /lightning with full inventory"
    print_expected "Lightning works regardless of inventory"
    print_info "Fill inventory and test"

    print_step "9.2.3" "Test with dragon egg in main hand only"
    print_expected "Error message - must be in offhand"
    print_info "Put egg in main hand, try /lightning 1"

    print_section "9.3 Action Bar Updates"
    print_step "9.3.1" "Verify cooldown countdown"
    print_expected "Action bar shows decreasing seconds"
    print_info "Watch action bar during cooldown"

    print_step "9.3.2" "Verify ready state display"
    print_expected "Action bar shows 'Lightning READY'"
    print_info "After cooldown expires"

    print_step "9.3.3" "Verify fragment status updates"
    print_expected "Action bar updates with ability states"
    print_info "Use abilities and watch HUD"

    print_section "9.4 Particle Effects"
    print_step "9.4.1" "Verify lightning particles"
    print_expected "Purple lightning particles during strikes"
    print_info "Watch /lightning 1 execution"

    print_step "9.4.2" "Verify burning fragment particles"
    print_expected "Fire/ember particles around player"
    print_info "Equip Burning Fragment and observe"

    print_step "9.4.3" "Verify agility fragment particles"
    print_expected "Wind/swirl particles around player"
    print_info "Equip Agility Fragment and observe"

    print_step "9.4.4" "Verify immortal fragment particles"
    print_expected "Sparkle/shine particles around player"
    print_info "Equip Immortal Fragment and observe"

    print_step "9.4.5" "Verify corrupted core particles"
    print_expected "Purple/dark particles around player"
    print_info "Equip Corrupted Core and observe"

    print_section "9.5 Sound Effects"
    print_step "9.5.1" "Verify thunder sound"
    print_expected "Thunder sound during lightning strikes"
    print_info "Listen during /lightning 1"

    print_step "9.5.2" "Verify fireball launch sound"
    print_expected "Whoosh sound when fireball launches"
    print_info "Listen during Dragon's Wrath"

    print_step "9.5.3" "Verify ability activation sounds"
    print_expected "Unique sound for each ability activation"
    print_info "Listen during all fragment abilities"
}

#-------------------------------------------------------------------------------
# Section 7: Chronicle/Lore System Testing
#-------------------------------------------------------------------------------

test_chronicle_system() {
    print_header "SECTION 10: CHRONICLE/LORE SYSTEM TESTING"

    print_section "10.1 Chronicle Access"
    print_step "10.1.1" "Open chronicle book"
    print_expected "Book GUI opens with lore content"
    print_info "Command: /chronicle"

    print_step "10.1.2" "Navigate pages"
    print_expected "Can browse multiple lore pages"
    print_info "Use book controls to navigate"

    print_section "10.2 Discovery Progress"
    print_step "10.2.1" "Check discovery status"
    print_expected "Shows which lore pages discovered"
    print_info "Command: /chronicle status"

    print_step "10.2.2" "Unlock lore by using fragments"
    print_expected "New pages unlock as fragments used"
    print_info "Use different fragment abilities"

    print_step "10.2.3" "Verify page content"
    print_expected "Lore pages contain relevant story"
    print_info "Read unlocked pages"
}

#-------------------------------------------------------------------------------
# Main Execution
#-------------------------------------------------------------------------------

main() {
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║     Elemental Dragon Plugin - Manual Testing Script                   ║${NC}"
    echo -e "${CYAN}║                                                                       ║${NC}"
    echo -e "${CYAN}║  This script provides step-by-step testing procedures for all         ║${NC}"
    echo -e "${CYAN}  plugin features. Follow each section carefully and record results.    ║${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════════════════════╝${NC}"

    echo -e "\n${YELLOW}Before starting, ensure:${NC}"
    echo "  1. Server is running with plugin loaded"
    echo "  2. Minecraft client is connected"
    echo "  3. You have OP permissions"
    echo "  4. You can access the server console/logs"

    if yes_no_prompt "Are you ready to begin testing?"; then
        echo -e "\n${GREEN}Starting tests...${NC}"
    else
        echo -e "\n${YELLOW}Please prepare your environment and run this script again.${NC}"
        exit 0
    fi

    # Run all test sections
    test_server_setup
    pause_for_user

    test_lightning_ability
    pause_for_user

    test_burning_fragment
    pause_for_user

    test_agility_fragment
    pause_for_user

    test_immortal_fragment
    pause_for_user

    test_corrupted_core
    pause_for_user

    test_commands
    pause_for_user

    test_cooldown_persistence
    pause_for_user

    test_edge_cases
    pause_for_user

    test_chronicle_system
    pause_for_user

    # Show final summary
    show_summary

    echo -e "\n${CYAN}Testing complete!${NC}"
    echo -e "${YELLOW}Tip: Screenshot or save your test results for documentation.${NC}"
}

# Run main function
main "$@"
