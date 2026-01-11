# Dockerfile for Elemental Dragon Plugin
# Uses itzg/minecraft-server as the base (Java 21)

FROM itzg/minecraft-server:java21

# Set environment variables for JVM compatibility (for PaperMC servers)
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

# Copy offline ops setup script to auto-execution directory
# Scripts in /image/scripts/auto/ run automatically during startup BEFORE /start
COPY entrypoint.sh /image/scripts/auto/99-plugin-setup
RUN chmod +x /image/scripts/auto/99-plugin-setup

# Copy the pre-built plugin JAR to the image
# The PLUGINS environment variable in docker-compose.yml will sync this to /data/plugins
RUN mkdir -p /image/plugins
COPY build/libs/elemental-dragon-*.jar /image/plugins/ElementalDragon.jar

# Verify the plugin was copied successfully
RUN if [ ! -f "/image/plugins/ElementalDragon.jar" ]; then \
        echo "❌ Plugin not found in /image/plugins/"; \
        ls -la /image/plugins/ || true; \
        exit 1; \
    fi && \
    echo "✅ Plugin successfully copied to /image/plugins/"

# Expose Minecraft and RCON ports
EXPOSE 25565 25575
