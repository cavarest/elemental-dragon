#!/bin/sh
# Elemental Dragon Plugin Setup Script
# This script runs automatically during container startup BEFORE the main /start script
# It generates offline-mode UUIDs from usernames and directly creates ops.json

set -e

echo "================================"
echo "Elemental Dragon Offline Ops Setup"
echo "================================"

# Function to generate offline-mode UUID from username
# Matches Minecraft's offline-mode UUID algorithm (version 3, variant 2)
generate_offline_uuid() {
    name="$1"
    hash=$(echo -n "OfflinePlayer:${name}" | md5sum | cut -d' ' -f1)

    # Break hash into parts for UUID version 3 format
    part1=$(echo "$hash" | cut -c1-8)
    part2=$(echo "$hash" | cut -c9-12)
    part3_raw=$(echo "$hash" | cut -c13-16)
    part4=$(echo "$hash" | cut -c17-20)
    part5=$(echo "$hash" | cut -c21-32)

    # For version 3 UUID: replace first char of part3 with "3"
    part3="3$(echo "$part3_raw" | cut -c2-4)"

    echo "${part1}-${part2}-${part3}-${part4}-${part5}"
}

# Process OFFLINE_OPS environment variable (comma-separated usernames)
if [ -n "${OFFLINE_OPS}" ]; then
    echo "👤 Processing offline operators: ${OFFLINE_OPS}"

    # Create a temporary file to build the JSON
    tmpfile=/tmp/ops-build.json
    echo "[" > "$tmpfile"

    # Process each username (comma-separated)
    comma=""
    echo "${OFFLINE_OPS}" | tr ',' '\n' | while read -r username; do
        # Skip empty lines
        [ -z "$username" ] && continue

        # Generate offline-mode UUID
        uuid=$(generate_offline_uuid "$username")

        # Append entry to JSON file
        echo "${comma}{\"name\":\"${username}\",\"uuid\":\"${uuid}\",\"level\":4,\"bypassesPlayerLimit\":false}" >> "$tmpfile"
        comma=","
        echo "   ✅ $username -> $uuid"
    done

    # Close JSON array
    echo "]" >> "$tmpfile"

    # Copy to final location
    cat "$tmpfile" > /data/ops.json
    rm -f "$tmpfile"

    echo "✅ ops.json created"
else
    echo "⚠️  No OFFLINE_OPS configured, skipping"
fi

echo "✅ Setup complete!"
echo "================================"
