#!/bin/bash

# Stop Paper Minecraft server and all related containers

# Check if Docker daemon is running BEFORE doing anything else
if ! docker info >/dev/null 2>&1; then
    echo "================================"
    echo "❌ Docker is not running!"
    echo "================================"
    echo ""
    echo "The Docker daemon is not accessible. Please start Docker and try again."
    echo ""
    echo "On macOS:"
    echo "  1. Open Docker Desktop from Applications"
    echo "  2. Wait for the Docker whale icon to appear in your menu bar"
    echo "  3. Run this script again"
    echo ""
    echo "On Linux:"
    echo "  sudo systemctl start docker"
    echo "  sudo systemctl enable docker  # To start on boot"
    echo ""
    echo "Note: Since Docker is not running, there are no containers to stop."
    echo ""
    exit 0  # Exit with success since there's nothing to stop anyway
fi

echo "================================"
echo "Stopping Paper MC Server"
echo "================================"

# Track whether any containers were actually stopped
CONTAINERS_STOPPED=false

# Check for running containers first
RUNNING_CONTAINERS=$(docker ps --format "{{.Names}}" 2>/dev/null | grep -E "(papermc|pilaf|mineflayer)" || true)

if [ -z "$RUNNING_CONTAINERS" ]; then
    echo "No running containers found (nothing to stop)."
    echo ""
    echo "To start again: ./start-server.sh"
    exit 0
fi

echo "Found running containers:"
echo "$RUNNING_CONTAINERS"
echo ""

# Stop main server
echo "Stopping main server..."
if docker-compose down --remove-orphans 2>/dev/null; then
    CONTAINERS_STOPPED=true
fi

# Stop PILAF-related containers that might be using the network
echo "Stopping PILAF-related containers..."
if docker stop pilaf-tests mineflayer-bridge pilaf-mineflayer 2>/dev/null; then
    CONTAINERS_STOPPED=true
fi
if docker rm pilaf-tests mineflayer-bridge pilaf-mineflayer 2>/dev/null; then
    CONTAINERS_STOPPED=true
fi

# Force remove network if still in use
echo "Removing network..."
docker network disconnect -f elemental-dragon_minecraft-network pilaf-tests 2>/dev/null || true
docker network disconnect -f elemental-dragon_minecraft-network mineflayer-bridge 2>/dev/null || true
docker network disconnect -f elemental-dragon_minecraft-network papermc-elementaldragon 2>/dev/null || true

# Also stop containers from other compose files
docker-compose -f docker-compose.local.yml down --remove-orphans 2>/dev/null || true
docker-compose -f docker-compose.ci.yml down --remove-orphans 2>/dev/null || true

echo ""
echo "Server stopped successfully!"
echo ""
echo "To start again: ./start-server.sh"
