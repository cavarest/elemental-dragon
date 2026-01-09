#!/bin/bash

# Stop Paper Minecraft server and all related containers

echo "================================"
echo "Stopping Paper MC Server"
echo "================================"

# Stop main server
echo "Stopping main server..."
docker-compose down --remove-orphans 2>/dev/null || true

# Stop PILAF-related containers that might be using the network
echo "Stopping PILAF-related containers..."
docker stop pilaf-tests mineflayer-bridge pilaf-mineflayer 2>/dev/null || true
docker rm pilaf-tests mineflayer-bridge pilaf-mineflayer 2>/dev/null || true

# Force remove network if still in use
echo "Removing network..."
docker network disconnect -f papermc-plugin-dragon-egg_minecraft-network pilaf-tests 2>/dev/null || true
docker network disconnect -f papermc-plugin-dragon-egg_minecraft-network mineflayer-bridge 2>/dev/null || true
docker network disconnect -f papermc-plugin-dragon-egg_minecraft-network papermc-elementaldragon 2>/dev/null || true

# Also stop containers from other compose files
docker-compose -f docker-compose.local.yml down --remove-orphans 2>/dev/null || true
docker-compose -f docker-compose.ci.yml down --remove-orphans 2>/dev/null || true

echo ""
echo "Server stopped successfully!"
echo ""
echo "To start again: ./start-server.sh"
