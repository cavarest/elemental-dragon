# Dockerfile for Dragon Egg Lightning Plugin
# Version is sourced from pom.xml (single source of truth)

# PaperMC Server with pre-built plugin
FROM marctv/minecraft-papermc-server:1.21.8

# Install required tools for Ubuntu-based PaperMC image
RUN apt-get update && apt-get install -y jq uuid-runtime && apt-get clean

# Copy the pre-built plugin JAR to locations:
# - /data/plugins/ for default use (may be shadowed by volume mount)
# - /opt/minecraft/plugins/ as backup outside /data (won't be shadowed)
RUN mkdir -p /data/plugins /opt/minecraft/plugins
COPY target/DragonEggLightning-*.jar /data/plugins/DragonEggLightning.jar
COPY target/DragonEggLightning-*.jar /opt/minecraft/plugins/DragonEggLightning.jar

# Verify the plugin was copied successfully - FAIL BUILD IF NOT FOUND
RUN if [ ! -f "/opt/minecraft/plugins/DragonEggLightning.jar" ]; then \
        echo "❌ Plugin not found in /opt/minecraft/plugins/DragonEggLightning.jar"; \
        ls -la /opt/minecraft/plugins/ || true; \
        exit 1; \
    fi && \
    echo "✅ Plugin successfully copied to /opt/minecraft/plugins/DragonEggLightning.jar"

# Copy the entrypoint script
COPY entrypoint.sh /entrypoint.sh

# Set environment variables for JVM compatibility
ENV PAPERMC_FLAGS="--add-modules=jdk.unsupported \
  --add-exports=java.base/sun.nio.ch=ALL-UNNAMED \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  --add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
  --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens=java.base/java.io=ALL-UNNAMED \
  --add-opens=java.base/java.net=ALL-UNNAMED \
  --add-opens=java.base/java.nio=ALL-UNNAMED \
  --add-opens=java.base/java.util=ALL-UNNAMED \
  --add-opens=java.base/java.util.concurrent=ALL-UNNAMED \
  --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED \
  --add-opens=java.base/jdk.internal.ref=ALL-UNNAMED \
  --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
  --add-opens=java.base/sun.security.action=ALL-UNNAMED"

# Make entrypoint script executable
RUN chmod +x /entrypoint.sh

# Set the entrypoint
ENTRYPOINT ["/entrypoint.sh"]

# Expose Minecraft and RCON ports
EXPOSE 25565 25575

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD rcon-cli "list" > /dev/null 2>&1 || exit 1
