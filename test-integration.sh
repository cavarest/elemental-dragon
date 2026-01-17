#!/bin/bash

# Integration test script for Elemental Dragon plugin using Pilaf
# This script runs the full integration test suite locally

set -e

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PILAF_DIR="${PILAF_DIR:-../pilaf}"
PLUGIN_VERSION=$(grep "^project.version=" gradle.properties | cut -d'=' -f2)
CONTAINER_NAME="papermc-elementaldragon"

echo -e "${BLUE}================================"
echo "Elemental Dragon Integration Tests"
echo "Pilaf JavaScript Testing Framework"
echo -e "================================${NC}"
echo ""

# Check prerequisites
echo -e "${BLUE}Checking prerequisites...${NC}"

# Check if Docker is running
if ! docker ps > /dev/null 2>&1; then
    echo -e "${RED}✗ Docker is not running!${NC}"
    echo "  Please start Docker and try again."
    exit 1
fi
echo -e "${GREEN}✓ Docker is running${NC}"

# Check if Gradle is available
if ! command -v gradle &> /dev/null; then
    echo -e "${RED}✗ Gradle is not found!${NC}"
    echo "  Please install Gradle or use the Gradle wrapper."
    exit 1
fi
echo -e "${GREEN}✓ Gradle is available${NC}"

# Check if Pilaf directory exists
if [ ! -d "$PILAF_DIR" ]; then
    echo -e "${YELLOW}⚠ Pilaf directory not found at: $PILAF_DIR${NC}"
    echo "  Cloning Pilaf framework..."
    git clone https://github.com/cavarest/pilaf.git "$PILAF_DIR"
fi
echo -e "${GREEN}✓ Pilaf directory found${NC}"

# Check if pnpm is available in Pilaf
if ! command -v pnpm &> /dev/null; then
    echo -e "${YELLOW}⚠ pnpm is not installed${NC}"
    echo "  Installing pnpm..."
    npm install -g pnpm
fi
echo -e "${GREEN}✓ pnpm is available${NC}"

echo ""

# Build the plugin
echo -e "${BLUE}Building Elemental Dragon plugin...${NC}"
chmod +x build.sh
./build.sh --production
echo -e "${GREEN}✓ Plugin built successfully${NC}"
echo ""

# Install Pilaf dependencies
echo -e "${BLUE}Installing Pilaf dependencies...${NC}"
cd "$PILAF_DIR"
pnpm install --frozen-lockfile
cd - > /dev/null
echo -e "${GREEN}✓ Pilaf dependencies installed${NC}"
echo ""

# Check if server is already running
if docker ps | grep -q "$CONTAINER_NAME"; then
    echo -e "${YELLOW}⚠ Server is already running${NC}"
    echo "  Stopping existing server..."
    chmod +x stop-server.sh
    ./stop-server.sh
    sleep 5
fi

# Start the server
echo -e "${BLUE}Starting Minecraft server...${NC}"
chmod +x start-server.sh
./start-server.sh

# Wait for server to be ready
echo -e "${YELLOW}Waiting for server to initialize...${NC}"
for i in {1..60}; do
    if docker exec "$CONTAINER_NAME" rcon-cli version > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Server is ready!${NC}"
        break
    fi
    if [ $i -eq 60 ]; then
        echo -e "${RED}✗ Server failed to start within timeout${NC}"
        ./stop-server.sh
        exit 1
    fi
    echo -n "."
    sleep 2
done
echo ""

# Verify plugin is loaded
echo -e "${BLUE}Verifying Elemental Dragon plugin is loaded...${NC}"
if docker exec "$CONTAINER_NAME" rcon-cli plugins 2>&1 | grep -qi "dragon\|elemental"; then
    echo -e "${GREEN}✓ Elemental Dragon plugin is loaded${NC}"
else
    echo -e "${YELLOW}⚠ Could not verify plugin is loaded${NC}"
fi
echo ""

# Run the tests
echo -e "${BLUE}================================"
echo "Running Integration Tests"
echo -e "================================${NC}"
echo ""

cd "$PILAF_DIR"

# Set environment variables for tests
export RCON_HOST="localhost"
export RCON_PORT="25575"
export RCON_PASSWORD="cavarest"
export MC_HOST="localhost"
export MC_PORT="25565"
export SKIP_DISCONNECT_RECONNECT="false"

# Run the tests
echo -e "${BLUE}Executing: pnpm test tests/dragon-egg-integration.pilaf.test.js${NC}"
echo ""

if pnpm test tests/dragon-egg-integration.pilaf.test.js; then
    echo ""
    echo -e "${GREEN}================================"
    echo "✓ All Integration Tests Passed!"
    echo -e "================================${NC}"
    TEST_RESULT=0
else
    echo ""
    echo -e "${RED}================================"
    echo "✗ Some Integration Tests Failed"
    echo -e "================================${NC}"
    TEST_RESULT=1
fi

cd - > /dev/null

# Ask about cleanup
echo ""
echo -e "${BLUE}================================"
echo "Test Complete"
echo -e "================================${NC}"
echo ""
echo "HTML reports are available at: $PILAF_DIR/target/pilaf-reports/"
echo ""

if [ "$TEST_RESULT" -eq 0 ]; then
    echo -e "${GREEN}✓ Tests passed successfully!${NC}"
else
    echo -e "${RED}✗ Some tests failed. Check the output above for details.${NC}"
fi

# Ask if user wants to stop the server
echo ""
read -p "Stop the Minecraft server? (y/N): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${BLUE}Stopping server...${NC}"
    ./stop-server.sh
    echo -e "${GREEN}✓ Server stopped${NC}"
else
    echo -e "${YELLOW}Server is still running${NC}"
    echo "  Stop it later with: ./stop-server.sh"
    echo "  View logs with: docker logs -f $CONTAINER_NAME"
fi

echo ""
echo "Done!"
exit $TEST_RESULT
