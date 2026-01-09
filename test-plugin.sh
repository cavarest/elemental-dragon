#!/bin/bash

# Test script for Elemental Dragon plugin
# This script automates basic functionality testing

set -e

echo "================================"
echo "Testing Elemental Dragon Plugin"
echo "================================"

# Check if server is running
if ! docker ps | grep -q papermc-elementaldragon; then
    echo "✗ Server is not running!"
    echo "  Please run ./start-server.sh first"
    exit 1
fi

echo "✓ Server is running"

# Wait for server to be fully started
echo "Waiting for server to be ready..."
sleep 10

# Run unit tests
echo "Running unit tests..."
mvn test

if [ $? -eq 0 ]; then
    echo "✓ Unit tests passed!"
else
    echo "✗ Unit tests failed!"
    exit 1
fi

echo ""
echo "================================"
echo "Manual Testing Instructions"
echo "================================"
echo ""
echo "1. Connect to the Minecraft server (localhost:25565)"
echo ""
echo "2. Get a Dragon Egg:"
echo "   /give @p minecraft:dragon_egg"
echo ""
echo "3. Hold the Dragon Egg in your offhand (F key)"
echo ""
echo "4. Test the ability:"
echo "   /lightning 1"
echo "   (or /ability 1 as alias)"
echo ""
echo "5. Verify the following:"
echo "   ✓ Purple lightning strikes appear"
echo "   ✓ Lightning deals 2 hearts (4 HP) per strike"
echo "   ✓ Three strikes occur with 0.5s intervals"
echo "   ✓ HUD shows cooldown (60s, 59s, etc.)"
echo "   ✓ HUD shows 'Lightning ready' when ready"
echo "   ✓ Ability fails without Dragon Egg in offhand"
echo "   ✓ Ability fails on cooldown"
echo ""
echo "6. Test admin commands:"
echo "   /elementaldragon info"
echo "   /elementaldragon getcooldown <player>"
echo "   (or /ed as alias)"
echo ""
echo "7. Test edge cases:"
echo "   ✓ No targets in range - should show error message"
echo "   ✓ Switch items during casting - should cancel"
echo "   ✓ Dead targets - should cancel strikes"
echo ""
echo "8. Stop the server when done:"
echo "   ./stop-server.sh"
echo ""
echo "================================"
echo "Testing Complete!"
echo "================================"
