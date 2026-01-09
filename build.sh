#!/bin/bash

# Build script for Elemental Dragon plugin

set -e

# Load environment variables (optional - only used for CONTAINER_NAME display)
if [ -f .env ]; then
    source .env
fi

# Extract version from gradle.properties using Gradle
PLUGIN_VERSION=$(grep "^project.version=" gradle.properties | cut -d'=' -f2)

echo "================================"
echo "Building Elemental Dragon Plugin"
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

    # Clean Gradle build
    echo "Cleaning Gradle build..."
    gradle clean

    # Remove build directory
    if [ -d "build" ]; then
        echo "Removing build directory..."
        rm -rf build/
    fi

    echo "✅ Clean complete!"
    echo ""
fi

# Build the plugin JAR
echo "🔧 Building plugin JAR..."
echo ""

echo "Running Gradle clean and build (skipping tests)..."
gradle clean build -x test

# Handle debug build with git commit
if [ "$DEBUG" = true ]; then
    echo "🔧 Creating debug build with git commit..."

    # Get git commit for debug build
    GIT_COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
    echo "   Git commit: ${GIT_COMMIT}"

    # Create debug JAR with git commit suffix
    JAR_FILE="build/libs/elemental-dragon-${PLUGIN_VERSION}.jar"
    if [ -f "$JAR_FILE" ]; then
        cp "$JAR_FILE" "build/libs/elemental-dragon-${PLUGIN_VERSION}-${GIT_COMMIT}.jar"
        echo "   ✅ Debug JAR created: elemental-dragon-${PLUGIN_VERSION}-${GIT_COMMIT}.jar"
        DEBUG_JAR_FILE="build/libs/elemental-dragon-${PLUGIN_VERSION}-${GIT_COMMIT}.jar"
    else
        echo "❌ Base JAR not found for debug build"
        exit 1
    fi
else
    # Production build - use clean version
    JAR_FILE=$(find build/libs/ -name "elemental-dragon-${PLUGIN_VERSION}.jar" | head -1)
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
    ls -la build/libs/ 2>/dev/null || echo "No build/libs directory found"
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
echo "  3. View logs: docker logs -f ${CONTAINER_NAME:-papermc-elementaldragon}"
echo ""
echo "Server will be available on port 25565 when started."

# Function to populate server-plugins directory (for use by start-server.sh)
populate_server_plugins() {
    echo "📦 Populating server-plugins/ with debug JAR..."

    # Get git commit for debug build
    GIT_COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
    echo "   Git commit: ${GIT_COMMIT}"

    # Get the base JAR file path
    BASE_JAR_FILE="build/libs/elemental-dragon-${PLUGIN_VERSION}.jar"

    # Only build if JAR doesn't exist (avoid duplicate builds)
    if [ ! -f "$BASE_JAR_FILE" ]; then
        echo "   Building JAR (not found)..."
        gradle jar -x test -q
    else
        echo "   Using existing JAR (run 'gradle clean' to force rebuild)"
    fi

    # Ensure server-plugins directory exists
    mkdir -p server-plugins

    # Create debug JAR with git commit suffix if not exists
    DEBUG_JAR_FILE="build/libs/elemental-dragon-${PLUGIN_VERSION}-${GIT_COMMIT}.jar"
    if [ ! -f "$DEBUG_JAR_FILE" ]; then
        cp "$BASE_JAR_FILE" "$DEBUG_JAR_FILE"
    fi

    # Copy debug JAR to server-plugins directory for Docker mount
    cp "$DEBUG_JAR_FILE" server-plugins/ElementalDragon.jar

    echo "✅ Debug JAR populated to server-plugins/ (version: ${PLUGIN_VERSION}-${GIT_COMMIT})"
}

# Export function for use by other scripts
export -f populate_server_plugins
