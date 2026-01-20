#!/bin/bash

# Start Paper Minecraft server with Elemental Dragon plugin

set -e

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
    exit 1
fi

# Load environment variables
if [ -f .env ]; then
    source .env
else
    echo "❌ .env file not found!"
    exit 1
fi

# Extract version from gradle.properties using Gradle
PLUGIN_VERSION=$(grep "^project.version=" gradle.properties | cut -d'=' -f2)
export PLUGIN_VERSION
export ADMIN_USERNAME

# Parse arguments
RESET=false
CLEAN=false
BLOCKING=false
WIPE_WORLD=false

for arg in "$@"; do
    case $arg in
        -r|--rebuild)
            RESET=true
            ;;
        -c|--clean)
            CLEAN=true
            ;;
        -b|--blocking)
            BLOCKING=true
            ;;
        -w|--wipe-world)
            WIPE_WORLD=true
            ;;
        -h|--help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  -r, --rebuild    Rebuild Docker image and restart server"
            echo "  -c, --clean      Clean build (Gradle clean + fresh Docker image)"
            echo "  -w, --wipe-world Clear world data before starting (preserves config)"
            echo "  -b, --blocking   Start in blocking mode (logs shown directly)"
            echo "  -h, --help       Show this help message"
            echo ""
            echo "Modes:"
            echo "  Daemon (default)    Server runs in background, use 'docker logs -f' to view"
            echo "  Blocking (-b)        Server logs shown directly, Ctrl+C to stop"
            echo ""
            echo "Examples:"
            echo "  $0                  # Start server in daemon mode (background)"
            echo "  $0 -b               # Start server in blocking mode (see logs)"
            echo "  $0 -r               # Rebuild and start in daemon mode"
            echo "  $0 -r -b            # Rebuild and start in blocking mode"
            echo "  $0 -c               # Clean rebuild and start in daemon mode"
            echo "  $0 -c -b            # Clean rebuild and start in blocking mode"
            echo "  $0 -w               # Wipe world and start in daemon mode"
            echo "  $0 -w -b            # Wipe world and start in blocking mode"
            exit 0
            ;;
    esac
done

echo "================================"
echo "Starting Paper MC Server"
echo "================================"

# Check if server is already running and stop it
CONTAINER_NAME=${CONTAINER_NAME:-papermc-elementaldragon}
echo "🔍 Checking if server is already running..."

# Check for our container
if docker ps --format "table {{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "⚠️  Server is currently running! Stopping it first..."
    docker-compose down --remove-orphans
    echo "✅ Server stopped successfully!"
    echo ""
    sleep 2
else
    echo "✅ No running server found, proceeding to start..."
    echo ""
fi

# Also check for and stop any other containers actually using our ports
echo "🔍 Checking for containers using ports 25565/25575..."

# Find containers that are actually binding to our ports (not just matching name patterns)
PORT_CONTAINERS=$(docker ps --format "{{.Names}}\t{{.Ports}}" | grep -E ":25565->25565|:25575->25575" | awk '{print $1}' || true)

if [ -n "$PORT_CONTAINERS" ]; then
    echo "⚠️  Found containers using our ports, stopping them..."
    for container in $PORT_CONTAINERS; do
        echo "   Stopping $container..."
        docker stop "$container" 2>/dev/null || true
        docker rm "$container" 2>/dev/null || true
    done
    echo "✅ Other containers stopped!"
    echo ""
    sleep 2
else
    echo "✅ No other containers using our ports"
    echo ""
fi

# Build the JAR file only if it doesn't exist or if source code has changed
JAR_FILE="build/libs/elemental-dragon-${PLUGIN_VERSION}.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "🔧 Building plugin JAR..."
    if ! gradle clean jar -x test; then
        echo "✗ Failed to build plugin JAR"
        exit 1
    fi
else
    echo "🔧 Checking if JAR needs rebuilding due to source changes..."

    # Check if any source files are newer than the JAR
    SOURCE_NEWER=false
    for src_dir in src/main/java src/main/resources; do
        if [ -d "$src_dir" ]; then
            if [ "$(find "$src_dir" -type f -newer "$JAR_FILE" 2>/dev/null | wc -l)" -gt 0 ]; then
                SOURCE_NEWER=true
                break
            fi
        fi
    done

    if [ "$SOURCE_NEWER" = true ]; then
        echo "🔧 Source code changed, rebuilding plugin JAR..."
        if ! gradle clean jar -x test; then
            echo "✗ Failed to rebuild plugin JAR"
            exit 1
        fi
    else
        echo "🔧 Using existing plugin JAR (no source changes detected)"
    fi
fi

echo "Plugin version loaded: ${PLUGIN_VERSION}"
echo "Admin username loaded: ${ADMIN_USERNAME}"

# Handle clean build first
if [ "$CLEAN" = true ]; then
    echo "🧹 CLEAN MODE: Cleaning Docker image..."
    echo ""

    # Force Docker image rebuild
    RESET=true
fi

# Handle reset/rebuild functionality
if [ "$RESET" = true ]; then
    echo "🔄 REBUILD MODE: Deleting all Docker volumes and server data..."
    echo ""

    # Stop and remove container (already stopped above, but ensure cleanup)
    echo "Stopping and removing container..."
    docker-compose down 2>/dev/null || true
    docker stop ${CONTAINER_NAME:-papermc-elementaldragon} 2>/dev/null || true
    docker rm ${CONTAINER_NAME:-papermc-elementaldragon} 2>/dev/null || true

    # Remove Docker image to force rebuild
    echo "Removing Docker image..."
    docker rmi elemental-dragon:latest 2>/dev/null || true

    # Remove all volumes
    echo "Removing all Docker volumes..."
    docker volume prune -f

    # Remove server data directory
    if [ -d "server-data" ]; then
        echo "Removing server data directory..."
        rm -rf server-data/
    fi

    echo "✅ Reset complete! Starting fresh server..."
    echo ""
fi

# Handle world wiping functionality
if [ "$WIPE_WORLD" = true ]; then
    # If full reset was already done, skip world wipe (already handled)
    if [ "$RESET" = true ]; then
        echo "ℹ️  Full reset already performed (-c), world data already cleared."
        echo ""
    else
        echo "🗑️  WORLD WIPE MODE: Clearing world data only..."
        echo ""
        echo "⚠️  This will delete ALL world data (world, world_nether, world_the_end)"
        echo "   Server configs, plugins, and player data will be preserved."
        echo ""

        # Stop the container if it's running
        if docker ps --format "table {{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
            echo "Stopping server to wipe world..."
            docker-compose down 2>/dev/null || true
            docker stop ${CONTAINER_NAME:-papermc-elementaldragon} 2>/dev/null || true
            echo "✅ Server stopped"
            echo ""
        fi

        # Check if server-data directory exists
        if [ ! -d "server-data" ]; then
            echo "ℹ️  server-data directory not found (fresh server or already wiped)"
            echo ""
        else
            WORLDS_REMOVED=false

            # Remove main world
            if [ -d "server-data/world" ]; then
                echo "Removing world/ (Overworld)..."
                rm -rf server-data/world/
                WORLDS_REMOVED=true
            fi

            # Remove nether world
            if [ -d "server-data/world_nether" ]; then
                echo "Removing world_nether/ (Nether)..."
                rm -rf server-data/world_nether/
                WORLDS_REMOVED=true
            fi

            # Remove end world
            if [ -d "server-data/world_the_end" ]; then
                echo "Removing world_the_end/ (End)..."
                rm -rf server-data/world_the_end/
                WORLDS_REMOVED=true
            fi

            # Also check for region files and session.lock
            if [ -f "server-data/session.lock" ]; then
                echo "Removing session.lock..."
                rm -f server-data/session.lock
                WORLDS_REMOVED=true
            fi

            if [ "$WORLDS_REMOVED" = true ]; then
                echo "✅ World data cleared successfully!"
            else
                echo "ℹ️  No world data found to remove (fresh server or already wiped)"
            fi
        fi

        echo ""
        echo "✅ Starting server with fresh world..."
        echo ""
    fi
fi

# Always rebuild Docker image to ensure latest plugin is loaded
echo "📦 Building Docker image with latest plugin..."
echo ""
echo "Using PLUGIN_VERSION: ${PLUGIN_VERSION}"
echo "Using ADMIN_USERNAME: ${ADMIN_USERNAME}"

# Check if plugin JAR exists
JAR_FILE="build/libs/elemental-dragon-${PLUGIN_VERSION}.jar"
if [ -n "$JAR_FILE" ] && [ -f "$JAR_FILE" ]; then
    echo "✓ Plugin JAR found: $JAR_FILE"
    JAR_MODIFIED=$(stat -f %m "$JAR_FILE" 2>/dev/null || stat -c %Y "$JAR_FILE" 2>/dev/null)
    echo "📅 JAR file last modified: $(date -r $JAR_MODIFIED '+%Y-%m-%d %H:%M:%S' 2>/dev/null || date -d @$JAR_MODIFIED '+%Y-%m-%d %H:%M:%S')"
else
    echo "✗ Plugin JAR not found: $JAR_FILE"
    exit 1
fi

echo "✅ Docker image built successfully with plugin version ${PLUGIN_VERSION}!"

echo ""
echo "✓ Server will be configured with settings from .env file"
echo "✓ Plugin version: ${PLUGIN_VERSION}"
echo "✓ Admin username: ${ADMIN_USERNAME}"
echo ""

# Start Docker container with docker-compose
echo "Starting Docker container..."
if [ "$BLOCKING" = true ]; then
    echo "Mode: BLOCKING (logs will be shown directly, Ctrl+C to stop)"
    echo ""
    docker-compose up --remove-orphans
else
    echo "Mode: DAEMON (background, use 'docker logs -f ${CONTAINER_NAME}' to view logs)"
    docker-compose up -d --remove-orphans
fi

echo ""
echo "================================"
echo "Server Starting!"
echo "================================"
echo ""
echo "Server will be available on port 25565"
echo "RCON will be available on port 25575"
echo ""

# Show mode-specific information
if [ "$BLOCKING" = true ]; then
    echo "📺 Running in BLOCKING mode - logs will be shown below"
    echo "   Press Ctrl+C to stop the server"
    echo ""
else
    echo "Useful commands:"
    echo "  View logs:       docker logs -f ${CONTAINER_NAME:-papermc-elementaldragon}"
    echo "  Server console:  docker attach ${CONTAINER_NAME:-papermc-elementaldragon}"
    echo "  Stop server:     ./stop-server.sh"
    echo "  Rebuild:         ./start-server.sh -r"
    echo "  Clean rebuild:   ./start-server.sh -c"
    echo "  Wipe world:      ./start-server.sh -w"
    echo "  Blocking mode:   ./start-server.sh -b"
    echo ""
fi


# Wait for server to be ready with proper detection (skip in blocking mode)
if [ "$BLOCKING" = false ]; then
    echo "Waiting for server to start..."
    echo "(This may take 30-60 seconds on first run)"
    echo ""

    MAX_WAIT=120
    WAIT_COUNT=0
    SERVER_READY=false

    while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
        # Check if container is running
        if ! docker ps --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
            echo "✗ Container stopped unexpectedly!"
            echo "Container logs:"
            docker logs ${CONTAINER_NAME:-papermc-elementaldragon} --tail 50
            exit 1
        fi

        # Check logs for server ready indicators
        LOGS=$(docker logs ${CONTAINER_NAME:-papermc-elementaldragon} 2>&1)

        if echo "$LOGS" | grep -qE "Done \([0-9.]+s\)!"; then
            SERVER_READY=true
            break
        elif echo "$LOGS" | grep -qE "ThreadedAnvilChunkStorage.*Loading"; then
            # Server is loading chunks, getting close
            echo "   Server is loading world data..."
        elif echo "$LOGS" | grep -qE "ElementalDragon.*enabled"; then
            # Plugin loaded
            echo "   Plugin loaded!"
        fi

        sleep 2
        WAIT_COUNT=$((WAIT_COUNT + 2))
        echo -ne "   Still starting... ${WAIT_COUNT}s elapsed\r"
    done

    echo ""


    if [ "$SERVER_READY" = true ]; then
        echo "✅ Server is ready for manual testing!"
    else
        echo "⚠️  Server may still be starting, checking logs..."
    fi

    echo ""
    echo "Recent server logs:"
    echo "==================="
    docker logs ${CONTAINER_NAME:-papermc-elementaldragon} --tail 20
    echo "==================="
    echo ""


    if [ "$SERVER_READY" = true ]; then
        echo "✅ Server is ready! You can now connect with your Minecraft client."
        echo ""
        echo "Connection details:"
        echo "  Address: localhost (or your server IP)"
        echo "  Port: 25565"
        echo "  Version: 1.21.x (PaperMC)"
    else
        echo "⚠️  Server might still be loading. Check logs above for status."
        echo "   If you see 'Done' in the logs, the server is ready."
    fi
fi
