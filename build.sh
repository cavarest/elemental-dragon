#!/bin/bash

# Build script for Dragon Egg Lightning plugin

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

echo "================================"
echo "Building Dragon Egg Lightning Plugin"
echo "================================"

# Parse arguments
CLEAN=false
if [[ "$1" == "--clean" ]] || [[ "$1" == "-c" ]]; then
    CLEAN=true
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

echo "Running Maven clean and package..."
mvn clean package

# Find the generated JAR file using the version from .env
JAR_FILE=$(find target/ -name "DragonEggLightning-${PLUGIN_VERSION}.jar" | head -1)

if [ -n "$JAR_FILE" ]; then
    echo "✓ Plugin JAR created successfully!"
    echo "  Location: $JAR_FILE"
    echo "  Size: $(du -h "$JAR_FILE" | cut -f1)"
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
