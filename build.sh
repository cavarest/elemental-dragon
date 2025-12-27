#!/bin/bash

# Build script for Dragon Egg Lightning plugin

set -e

# Load environment variables (optional - only used for CONTAINER_NAME display)
if [ -f .env ]; then
    source .env
fi

# Extract version from pom.xml using Maven (single source of truth)
PLUGIN_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "================================"
echo "Building Dragon Egg Lightning Plugin"
echo "================================"

# Parse arguments
CLEAN=false
DEBUG=false
PRODUCTION=false
if [[ "$1" == "--clean" ]] || [[ "$1" == "-c" ]]; then
    CLEAN=true
fi
if [[ "$1" == "--debug" ]] || [[ "$1" == "-d" ]]; then
    DEBUG=true
fi
if [[ "$1" == "--production" ]] || [[ "$1" == "-p" ]]; then
    PRODUCTION=true
    DEBUG=false
fi

# Default to debug build (with git commit) unless production is specified
if [ "$PRODUCTION" = false ]; then
    DEBUG=true
fi

if [ "$CLEAN" = true ]; then
    echo "🧹 Cleaning previous builds..."
    echo ""

    # Clean Maven build
    echo "Cleaning Maven build..."
    mvn clean

    # Remove target directory
    if [ -d "target" ]; then
        echo "Removing target directory..."
        rm -rf target/
    fi

    echo "✅ Clean complete!"
    echo ""
fi

# Build the plugin JAR
echo "🔧 Building plugin JAR..."
echo ""

echo "Running Maven clean and package (skipping tests)..."
mvn clean package -DskipTests

# Handle debug build with git commit
if [ "$DEBUG" = true ]; then
    echo "🔧 Creating debug build with git commit..."

    # Get git commit for debug build
    GIT_COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
    echo "   Git commit: ${GIT_COMMIT}"

    # Create debug JAR with git commit suffix
    if [ -f "target/DragonEggLightning-${PLUGIN_VERSION}.jar" ]; then
        cp "target/DragonEggLightning-${PLUGIN_VERSION}.jar" "target/DragonEggLightning-${PLUGIN_VERSION}-${GIT_COMMIT}.jar"
        echo "   ✅ Debug JAR created: DragonEggLightning-${PLUGIN_VERSION}-${GIT_COMMIT}.jar"
        JAR_FILE="target/DragonEggLightning-${PLUGIN_VERSION}-${GIT_COMMIT}.jar"
    else
        echo "❌ Base JAR not found for debug build"
        exit 1
    fi
else
    # Production build - use clean version
    JAR_FILE=$(find target/ -name "DragonEggLightning-${PLUGIN_VERSION}.jar" | head -1)
fi

if [ -n "$JAR_FILE" ] && [ -f "$JAR_FILE" ]; then
    echo "✓ Plugin JAR created successfully!"
    echo "  Location: $JAR_FILE"
    echo "  Size: $(du -h "$JAR_FILE" | cut -f1)"
    if [ "$DEBUG" = true ]; then
        echo "  Type: Debug build (with git commit)"
    else
        echo "  Type: Production build (clean version)"
    fi
else
    echo "✗ Failed to create plugin JAR"
    echo "Checking for alternative JAR files..."
    ls -la target/ 2>/dev/null || echo "No target directory found"
    exit 1
fi

echo ""
echo "================================"
echo "Build Complete!"
echo "================================"
echo ""
echo "Next steps:"
echo "  1. Start server: ./start-server.sh (will auto-build Docker image if needed)"
echo "  2. Stop server: ./stop-server.sh"
echo "  3. View logs: docker logs -f ${CONTAINER_NAME:-papermc-dragonegg}"
echo ""
echo "Server will be available on port 25565 when started."

# Function to populate server-plugins directory (for use by start-server.sh)
populate_server_plugins() {
    echo "📦 Populating server-plugins/ with fresh debug JAR..."

    # Clear server-plugins directory to ensure fresh plugins
    rm -rf server-plugins/*

    # Get git commit for debug build
    GIT_COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
    echo "   Git commit: ${GIT_COMMIT}"

    # Build production JAR
    mvn clean package -DskipTests -q

    # Create debug JAR with git commit suffix
    cp target/DragonEggLightning-${PLUGIN_VERSION}.jar "target/DragonEggLightning-${PLUGIN_VERSION}-${GIT_COMMIT}.jar"

    # Copy debug JAR to server-plugins directory for Docker mount
    cp "target/DragonEggLightning-${PLUGIN_VERSION}-${GIT_COMMIT}.jar" server-plugins/DragonEggLightning.jar

    echo "✅ Fresh debug JAR populated to server-plugins/ (version: ${PLUGIN_VERSION}-${GIT_COMMIT})"
}

# Export function for use by other scripts
export -f populate_server_plugins
