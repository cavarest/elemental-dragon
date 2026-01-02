#!/bin/bash

set -e

# Function to generate offline-mode UUID from username
# Minecraft offline mode uses MD5 hash of "OfflinePlayer:<name>"
generate_offline_uuid() {
    local name="$1"
    # Generate MD5 hash of "OfflinePlayer:<name>" and format as UUID
    local hash=$(echo -n "OfflinePlayer:${name}" | md5sum | cut -d' ' -f1)
    # Format as UUID (8-4-4-4-12) with version 3 marker
    local uuid="${hash:0:8}-${hash:8:4}-3${hash:13:3}-${hash:16:4}-${hash:20:12}"
    echo "$uuid"
}

# Function to generate random UUID (with fallbacks) - for online mode
generate_uuid() {
    # Try uuidgen first
    if command -v uuidgen >/dev/null 2>&1; then
        uuidgen
    # Try /proc/sys/kernel/random/uuid (Linux)
    elif [ -f /proc/sys/kernel/random/uuid ]; then
        cat /proc/sys/kernel/random/uuid
    # Use openssl as fallback
    elif command -v openssl >/dev/null 2>&1; then
        openssl rand -hex 16 | sed 's/\(.\{8\}\)\(.\{4\}\)\(.\{4\}\)\(.\{4\}\)\(.\{12\}\)/\1-\2-\3-\4-\5/'
    # Final fallback: generate simple UUID-like string
    else
        # Generate a simple UUID v4 equivalent
        local timestamp=$(date +%s)
        local random=$(od -An -t x -N 8 /dev/urandom | tr -d ' ')
        echo "${timestamp:0:8}-${random:0:4}-4${random:4:3}-a${random:7:3}-${timestamp:8}${random:11}"
    fi
}

# Function to check if jq is available
check_jq() {
    if command -v jq >/dev/null 2>&1; then
        return 0
    else
        echo "⚠️  jq not found, will use manual JSON editing"
        return 1
    fi
}

echo "================================"
echo "Dragon Egg Lightning Plugin Server"
echo "Initializing..."
echo "================================"

# Ensure EULA is accepted
echo "eula=true" > /data/eula.txt
echo "✅ EULA accepted"

# Ensure plugins directory exists
mkdir -p /data/plugins

echo "📂 Checking plugins directory..."
ls -la /data/plugins/

echo "📂 Checking /opt/minecraft/plugins/ directory..."
ls -la /opt/minecraft/plugins/

# Copy fresh plugins from /opt/minecraft/plugins/ to /data/plugins/
# This merges base image plugins with volume-mounted plugins
echo "🔄 Copying fresh plugins from /opt/minecraft/plugins/ to /data/plugins/..."

# Check if the specific plugin file exists
if [ -f "/opt/minecraft/plugins/DragonEggLightning.jar" ]; then
    echo "✅ Found DragonEggLightning.jar in /opt/minecraft/plugins/"
    cp -f /opt/minecraft/plugins/DragonEggLightning.jar /data/plugins/
    echo "✅ Plugin copied to /data/plugins/"
else
    echo "⚠️  DragonEggLightning.jar NOT found in /opt/minecraft/plugins/"
    # List all files to debug
    echo "   Contents of /opt/minecraft/plugins/:"
    ls -la /opt/minecraft/plugins/ || true
fi

# Also copy any other JAR files
if [ -n "$(ls /opt/minecraft/plugins/*.jar 2>/dev/null)" ]; then
    echo "🔄 Copying all JAR files from /opt/minecraft/plugins/ to /data/plugins/..."
    cp -f /opt/minecraft/plugins/*.jar /data/plugins/ 2>/dev/null || true
    echo "✅ All JAR files copied"
fi

echo "📋 Plugins in /data/plugins/:"
ls -la /data/plugins/

echo "✅ Plugin directory ready."

# Ensure ops.json exists and is valid
if [ ! -f /data/ops.json ]; then
    echo "Creating fresh ops.json..."
    echo "[]" > /data/ops.json
fi

# Check if ops.json is valid JSON
if ! jq empty /data/ops.json 2>/dev/null; then
    echo "⚠️  ops.json is corrupted, recreating..."
    echo "[]" > /data/ops.json
fi

# Set up admin user only if not already present
if [ -n "${ADMIN_USERNAME}" ]; then
    echo "Setting up admin user: ${ADMIN_USERNAME}"

    # Generate offline-mode UUID (matches what Minecraft generates for this username)
    ADMIN_UUID=$(generate_offline_uuid "${ADMIN_USERNAME}")
    echo "   Offline UUID: ${ADMIN_UUID}"

    if check_jq; then
        # Always update ops.json with correct offline UUID
        jq --arg name "${ADMIN_USERNAME}" --arg uuid "${ADMIN_UUID}" \
           'if any(.[]; .name == $name) then map(if .name == $name then .uuid = $uuid else . end) else . + [{"name": $name, "uuid": $uuid, "level": 4, "bypassesPlayerLimit": false}] end' \
           /data/ops.json > /tmp/ops.json.tmp
        cat /tmp/ops.json.tmp > /data/ops.json
        rm -f /tmp/ops.json.tmp
        echo "✅ Admin user '${ADMIN_USERNAME}' configured in ops.json"
    else
        # Manual JSON editing without jq
        echo '[{"name":"'"${ADMIN_USERNAME}"'","uuid":"'"${ADMIN_UUID}"'","level":4,"bypassesPlayerLimit":false}]' > /data/ops.json
        echo "✅ Admin user '${ADMIN_USERNAME}' added to ops.json (manual method)"
    fi
elif [ ! -s /data/ops.json ] || [ "$(jq length /data/ops.json 2>/dev/null || echo 0)" = "0" ]; then
    # Set up default admin if ops.json is empty
    echo "Setting up default admin user: admin"

    ADMIN_UUID=$(generate_uuid)
    echo '[{"name":"admin","uuid":"'"${ADMIN_UUID}"'","level":4,"bypassesPlayerLimit":false}]' > /data/ops.json
    echo "✅ Default admin user 'admin' added to ops.json"
fi

# Ensure server.properties exists
if [ ! -f /data/server.properties ]; then
    echo "Creating server.properties..."

    cat > /data/server.properties << EOF
server-name=${SERVER_NAME:-Dragon Egg Lightning Server}
gamemode=survival
difficulty=normal
max-players=10
online-mode=false
white-list=false
pvp=true
spawn-protection=16
motd=${MOTD:-Dragon Egg Lightning Plugin Server}
view-distance=10
simulation-distance=10
enable-command-block=false
enable-rcon=true
rcon.port=${RCON_PORT:-25575}
rcon.password=${RCON_PASSWORD:-dragon123}
EOF
    echo "✅ server.properties created"
else
    echo "✅ server.properties already exists"
fi

# Check memory
MEMORY=${MEMORYSIZE:-2G}
echo "✅ Memory allocation: $MEMORY"

# Final plugin check
echo "🔍 Final check for Dragon Egg Lightning plugin..."
PLUGIN_FOUND=false

if [ -f /data/plugins/DragonEggLightning.jar ]; then
    echo "✅ Dragon Egg Lightning plugin found: DragonEggLightning.jar"
    echo "   Size: $(du -h /data/plugins/DragonEggLightning.jar | cut -f1)"
    echo "   Last modified: $(stat -f "%Sm" /data/plugins/DragonEggLightning.jar 2>/dev/null || stat -c "%y" /data/plugins/DragonEggLightning.jar)"
    PLUGIN_FOUND=true
fi

if [ "$PLUGIN_FOUND" = false ]; then
    echo "❌ Dragon Egg Lightning plugin NOT FOUND!"
    echo "Available files in /data/plugins/:"
    ls -la /data/plugins/
    echo "❌ Server cannot start without plugin!"
    exit 1
fi

echo ""
echo "================================"
echo "Starting PaperMC Server"
echo "Admin: ${ADMIN_USERNAME:-admin}"
echo "Memory: $MEMORY"
echo "================================"
echo ""

# Start server
exec java -Xms${MEMORY} -Xmx${MEMORY} ${PAPERMC_FLAGS} -jar /opt/minecraft/paperspigot.jar nogui
