module.exports = {
  // Default server configuration
  server: {
    type: 'paper',
    version: '1.21.8',
    rcon: {
      host: process.env.RCON_HOST || 'localhost',
      port: parseInt(process.env.RCON_PORT) || 25575,
      password: process.env.RCON_PASSWORD || 'dragon123'
    }
  },

  // Default player configuration
  players: {
    default: {
      auth: 'offline',
      spawnPoint: { x: 0, y: 64, z: 0 },
      spawnYaw: 0, // Facing North
      spawnPitch: 0
    }
  },

  // Test timeouts (in milliseconds)
  timeouts: {
    connection: 30000,    // 30s to establish RCON connection
    command: 10000,       // 10s for command to execute
    effect: 5000,         // 5s for effect to apply
    entitySpawn: 2000,    // 2s for entity to spawn
    damage: 3000          // 3s for damage to occur
  },

  // Retry configuration for flaky operations
  retry: {
    maxAttempts: 3,
    delay: 1000
  }
};
