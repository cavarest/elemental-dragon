#!/bin/bash

# Start Paper Minecraft server with Dragon Egg Lightning plugin

set -e

# Load environment variables
if [ -f .env ]; then
    source .env
else
    echo "❌ .env file not found!"
    exit 1
fi

# Extract version from pom.xml using Maven (single source of truth)
PLUGIN_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
export PLUGIN_VERSION
export ADMIN_USERNAME

# Parse arguments
RESET=false
CLEAN=false

for arg in "$@"; do
    case $arg in
        -r|--rebuild)
            RESET=true
            ;;
        -c|--clean)
            CLEAN=true
            ;;
        -h|--help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  -r, --rebuild    Rebuild Docker image and restart server"
            echo "  -c, --clean      Clean build (Maven clean + fresh Docker image)"
            echo "  -h, --help       Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0               # Start server normally"
            echo "  $0 -r            # Rebuild and restart"
            echo "  $0 -c            # Clean build and start"
            echo "  $0 -r -c         # Clean rebuild and restart"
            exit 0
            ;;
    esac
done

echo "================================"
echo "Starting Paper MC Server"
echo "================================"

# Build the JAR file first (always needed for Docker)
echo "🔧 Building plugin JAR..."
if ! mvn clean package -DskipTests; then
    echo "✗ Failed to build plugin JAR"
    exit 1
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

    # Stop and remove container
    echo "Stopping and removing container..."
    docker-compose down 2>/dev/null || true
    docker stop ${CONTAINER_NAME:-papermc-dragonegg} 2>/dev/null || true
    docker rm ${CONTAINER_NAME:-papermc-dragonegg} 2>/dev/null || true

    # Remove Docker image to force rebuild
    echo "Removing Docker image..."
    docker rmi dragon-egg-lightning:latest 2>/dev/null || true

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

# Always rebuild Docker image to ensure latest plugin is loaded
echo "📦 Building Docker image with latest plugin..."
echo ""
echo "Using PLUGIN_VERSION: ${PLUGIN_VERSION}"
echo "Using ADMIN_USERNAME: ${ADMIN_USERNAME}"

# Build using docker-compose with environment variables (always rebuild)
if ! docker-compose build --no-cache; then
    echo "✗ Failed to build Docker image"
    exit 1
fi

echo "✅ Docker image built successfully with plugin version ${PLUGIN_VERSION}!"
echo ""

# Check if plugin JAR exists (for reference)
JAR_FILE=$(find target/ -name "DragonEggLightning-${PLUGIN_VERSION}.jar" | head -1)
if [ -n "$JAR_FILE" ]; then
    echo "✓ Plugin JAR found: $JAR_FILE"
fi

echo "✓ Server will be configured with settings from .env file"
echo "✓ Plugin version: ${PLUGIN_VERSION}"
echo "✓ Admin username: ${ADMIN_USERNAME}"
echo ""

# Start Docker container with docker-compose
echo "Starting Docker container..."
docker-compose up -d

echo ""
echo "================================"
echo "Server Starting!"
echo "================================"
echo ""
echo "Server will be available on port 25565"
echo "RCON will be available on port 25575"
echo ""
echo "Useful commands:"
echo "  View logs:      docker logs -f ${CONTAINER_NAME:-papermc-dragonegg}"
echo "  Server console: docker attach ${CONTAINER_NAME:-papermc-dragonegg}"
echo "  Stop server:    ./stop-server.sh"
echo "  Rebuild:        ./start-server.sh -r"
echo "  Clean rebuild:  ./start-server.sh -c"
echo ""
echo "Waiting for server to start (this may take a minute)..."
sleep 10

# Show initial logs
docker logs ${CONTAINER_NAME:-papermc-dragonegg} --tail 30

echo ""
echo "✅ Server should be starting up. Check logs above for any issues."
echo "   Plugin will load automatically once the server is ready."
