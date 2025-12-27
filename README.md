# ⚡ Minecraft Dragon Egg Lightning Plugin

<div align="center">

![Dragon Egg Lightning Banner](https://img.shields.io/badge/Minecraft-Paper%201.21.8-blue?logo=minecraft&style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21+-ED8B00?logo=openjdk&logoColor=white&style=for-the-badge)
![Version](https://img.shields.io/badge/Version-1.0.2-green?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)

**Transform your server with the ultimate Dragon Egg lightning ability!**

[![Download Latest Release](https://img.shields.io/badge/Download-Latest%20Release-ff6b6b?style=for-the-badge&logo=github)](https://github.com/ronaldtse/papermc-plugin-dragon-egg/releases/latest)
[![GitHub Stars](https://img.shields.io/github/stars/ronaldtse/papermc-plugin-dragon-egg?style=social)](https://github.com/ronaldtse/papermc-plugin-dragon-egg)
[![GitHub Forks](https://img.shields.io/github/forks/ronaldtse/papermc-plugin-dragon-egg?style=social)](https://github.com/ronaldtse/papermc-plugin-dragon-egg/fork)

</div>

## 🌟 Quick Start (5 Minutes)

### **Step 1: Install Plugin**
```bash
# Download the latest JAR from releases
# Place in your Paper server's plugins directory
cp DragonEggLightning-1.0.2.jar /path/to/your/paper-server/plugins/

# Restart your Paper 1.21.8+ server
java -Xms2G -Xmx2G -jar paper-1.21.8-latest.jar nogui
```

### **Step 2: Use Lightning Ability**
```bash
# Give yourself a Dragon Egg
/give @p minecraft:dragon_egg

# Move to offhand (press F key)
/ability 1

# Watch the magic happen! ⚡
```

---

## 📚 Documentation Index

This repository has been organized into logical documentation categories:

### 🎮 [User Documentation](docs/user/)
- **[README.md](docs/user/README.md)** - Complete user guide and features overview
- **[Installation Guide](docs/user/installation.md)** - Step-by-step installation instructions

### 🔧 [Administrator Documentation](docs/admin/)
- **[Testing Guide](docs/admin/testing.md)** - Real-world testing procedures
- **[Docker Architecture](docs/admin/docker.md)** - Container setup and deployment
- **[CI/CD Guide](docs/admin/cicd.md)** - Continuous integration and deployment

---

## ⚡ What Makes This Plugin Special?

### **🔮 Unique Dragon Egg Mechanic**
- **Offhand Requirement**: Players must strategically place Dragon Egg in offhand
- **Line-of-Sight Targeting**: Smart ray-tracing finds the closest entity in player's view
- **Range Limitations**: Balanced 50-block targeting range prevents long-distance abuse

### **🌩️ Epic Lightning Experience**
- **3 Sequential Strikes**: Each strike hits with 0.5-second intervals
- **Purple Visual Effects**: Stunning particle effects and lightning bolts
- **Thunder Audio**: Immersive sound effects with each lightning strike
- **Armor-Bypassing Damage**: Lightning ignores all armor, protection enchantments, and resistance effects
- **Damage Balancing**: 2.0 hearts per strike (total 6.0 hearts if all connect)

### **⏱️ Smart Cooldown System**
- **60-Second Cooldown**: Prevents spam and maintains balance
- **HUD Display**: Real-time countdown in action bar
- **Cooldown Tracking**: Individual cooldowns per player
- **Anti-Abuse Protection**: Smart validation prevents exploitation

---

## 🎯 Perfect For These Server Types

### **🎮 PvP Servers**
- Add exciting combat mechanics with consistent damage
- Reward skilled players with powerful abilities
- Create balanced risk-reward gameplay

### **🏰 RPG Servers**
- Enhance roleplay with magical dragon powers
- Create quest rewards involving Dragon Egg abilities
- Add epic boss battle mechanics

### **🏝️ Survival Servers**
- Give players special abilities for survival challenges
- Create unique mob hunting experiences
- Add strategic depth to PvE gameplay

### **🎉 Mini-Game Servers**
- Design lightning-based mini-games
- Create competitive events with abilities
- Add unique game modes and challenges

---

## 🛠️ Development & Installation

### **System Requirements**
- **Minecraft**: Java Edition 1.21.8+
- **Server Software**: Paper 1.21.8-R0.1+
- **Java**: Version 21 or higher
- **Memory**: Minimum 2GB RAM recommended

### **Developer Setup**

**Required Software:**
- **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop/)
- **Java 21+** - [OpenJDK](https://adoptium.net/) or Oracle JDK
- **Maven 3.6+** - [Maven](https://maven.apache.org/download.cgi)
- **Git** - [Git](https://git-scm.com/downloads)

**Quick Start for Developers:**

```bash
# 1. Clone repository
git clone https://github.com/ronaldtse/papermc-plugin-dragon-egg.git
cd papermc-plugin-dragon-egg

# 2. Build plugin JAR
./build.sh

# 3. Start server with plugin
./start-server.sh

# 4. Connect and test
# Server: localhost:25565
# Username: posiflow
# RCON: localhost:25575 (password: dragon123)

# 5. Stop server
./stop-server.sh
```

**Development Commands:**

```bash
# Build commands
./build.sh                    # Standard build
./build.sh --clean           # Clean build

# Server management
./start-server.sh            # Start normally
./start-server.sh --rebuild  # Rebuild Docker image
./start-server.sh --clean    # Clean server data

# Testing
./test-plugin.sh             # Run all tests
mvn test                     # Run unit tests
mvn test -Dtest=*Yaml*       # Run YAML tests
```

---

## 🐛 Troubleshooting

### **Common Issues & Solutions**

#### **Plugin Not Loading**
```bash
# Check Java version (must be 21+)
java -version

# Verify JAR location
ls plugins/DragonEggLightning*.jar

# Check server logs
tail -f logs/latest.log | grep -i dragon
```

#### **Command Not Working**
```bash
# Make player operator
op <username>

# Verify plugin loaded
/plugins

# Check command registration
/help ability
```

#### **No Lightning Effects**
```bash
# Check graphics settings
# Video Settings → Particles → All

# Monitor server performance
/tps

# Verify target validation
# Must have Dragon Egg in offhand and valid target
```

---

## 🎯 Why This Plugin Will Transform Your Server

### **🎯 Player Engagement**
- **Unique Ability**: Dragon Egg lightning is unlike any other plugin
- **Skill-Based**: Requires player positioning and timing
- **Satisfying Effects**: Epic lightning and thunder create memorable moments
- **Strategic Depth**: Cooldown management adds tactical complexity

### **📊 Server Benefits**
- **Low Maintenance**: Zero configuration required after installation
- **Performance**: Optimized for Paper 1.21.8 with minimal impact
- **Stability**: Thoroughly tested with comprehensive error handling
- **Scalability**: Works efficiently with multiple players

### **🌟 Competitive Advantage**
- **Unique Selling Point**: Lightning abilities set your server apart
- **Player Retention**: Exciting mechanics keep players engaged
- **Viral Potential**: Epic lightning effects encourage sharing
- **Content Creation**: Perfect for streamers and content creators

---

## 📈 Architecture & Testing

This plugin features a comprehensive testing framework including:

- **🧪 Unit Tests**: 24/24 passing tests covering all core functionality
- **🔧 Integration Tests**: End-to-end testing with real PaperMC servers
- **📝 YAML DSL**: Human-readable test scenarios for non-programmers
- **🎮 Mineflayer Integration**: Real Minecraft client testing automation
- **🐳 Docker Support**: Containerized development and testing environment

---

## 📞 Support & Community

### **Getting Help**
- **GitHub Issues**: [Report bugs or request features](https://github.com/ronaldtse/papermc-plugin-dragon-egg/issues)
- **Documentation**: Comprehensive guides in `/docs` directory
- **Community**: Connect with other server administrators

### **Contributing**
- **Bug Reports**: Help us improve by reporting issues
- **Feature Requests**: Suggest new abilities or improvements
- **Code Contributions**: Submit pull requests to enhance the plugin

---

## 📄 License & Credits

**License**: MIT License - Free for personal and commercial use

**Author**: Augustus Tse and Octavius Tse
**Version**: 1.0.2
**Minecraft Version**: 1.21.8+
**Paper API**: 1.21.8-R0.1-SNAPSHOT

---

<div align="center">

**⭐ Star this project if you love Dragon Egg Lightning!** ⭐

[![GitHub Stars](https://img.shields.io/github/stars/ronaldtse/papermc-plugin-dragon-egg?style=social)](https://github.com/ronaldtse/papermc-plugin-dragon-egg)
[![GitHub Forks](https://img.shields.io/github/forks/ronaldtse/papermc-plugin-dragon-egg?style=social)](https://github.com/ronaldtse/papermc-plugin-dragon-egg/fork)

**Transform your server today with Dragon Egg Lightning!** ⚡

[Download Latest Release](https://github.com/ronaldtse/papermc-plugin-dragon-egg/releases/latest) | [Installation Guide](docs/user/installation.md) | [Support](https://github.com/ronaldtse/papermc-plugin-dragon-egg/issues)

</div>

---

## 🆕 Recent Improvements (v1.0.2+)

### **🔧 Enhanced Build System**
- **DRY Principle**: `build.sh` contains all build logic, `start-server.sh` reuses it
- **Debug Builds**: Automatic git commit injection for development builds
- **Function Library**: `populate_server_plugins()` shared across scripts
- **Clean Production**: pom.xml always has clean version numbers

### **⚡ Lightning Feedback & Cooldown System**
- **Target Feedback**: Lightning strikes now show what/who was hit
  - "Lightning strike 1/3 hit Creeper!"
  - "Lightning strike 2/3 hit Steve!"
  - "Lightning strike 3/3 hit Zombie!"
- **Smart Cooldown**:
  - **Death**: Cooldown cleared automatically
  - **Respawn**: Cooldown automatically restarts (prevents spam after death)
  - **Logout/Login**: Cooldown persists (prevents cooldown abuse)

### **🎮 Better User Experience**
- **Clear Messages**: Players always know what lightning hit
- **Fair Cooldowns**: No cheating death or logout to avoid cooldowns
- **Consistent Feedback**: Real-time updates during ability use


### **🎯 Intelligent Lightning Targeting**
- **Initial Target**: Lightning starts with the closest target in view
- **Smart Switching**: If target dies, lightning automatically shifts to next closest target
- **Dynamic Feedback**:
  - "Lightning strike 1/3 hit Creeper!"
  - "Lightning shifts to Zombie!"
  - "Lightning strike 2/3 hit Zombie!"
  - "Lightning strike 3/3 hit Zombie!"
- **Range & Line-of-Sight**: Maintains 50-block range and viewing cone requirements
- **No Target Waste**: Ensures all 3 strikes are used effectively against valid targets


### **🔨 Build System Commands**

#### **Development Builds (Default)**
```bash
./build.sh                    # Debug build with git commit (DEFAULT)
./build.sh --clean           # Clean debug build
./build.sh --debug           # Explicit debug build
```

#### **Production Builds (Release)**
```bash
./build.sh --production      # Clean production build (no git commit)
./build.sh --production --clean  # Clean production build
```

#### **Server Management**
```bash
./start-server.sh            # Uses debug build by default
./start-server.sh --rebuild  # Rebuild Docker with fresh debug build
./start-server.sh --clean    # Clean start with debug build
```

**Build Behavior:**
- **Development**: Always creates debug builds with git commit suffix
- **Production**: Creates clean builds without git commit for releases
- **GitHub Workflows**: Test both debug and production builds
- **Releases**: Only production builds are released

## ⏱️ Complete Cooldown System Guide

### **How the Cooldown System Works**

The Dragon Egg Lightning plugin features a sophisticated cooldown system designed to prevent spam while providing fair gameplay mechanics.

#### **Cooldown Trigger & Duration**
- **Trigger**: Cooldown starts only when lightning ability is successfully used
- **Duration**: 60 seconds from last successful use
- **Independent**: Cooldown persists regardless of dragon egg possession

#### **Death & Respawn Behavior**
```bash
# Scenario 1: Death during cooldown
Player uses lightning → 60s cooldown starts
Player dies → Cooldown cleared immediately
Player respawns → Lightning ready to use (no cooldown)

# Scenario 2: Death after cooldown expires
Player uses lightning → 60s cooldown starts
Cooldown expires → Lightning ready again
Player dies → No effect (cooldown already expired)
```

#### **Dragon Egg Management**
```bash
# Scenario 1: Losing dragon egg
Player uses lightning → 60s cooldown starts
Player loses dragon egg → Cooldown continues running
Player picks up dragon egg → Cooldown continues running
# Cooldown expires naturally after 60 seconds

# Scenario 2: Picking up dragon egg (no active cooldown)
Player has no dragon egg → Lightning ready
Player picks up dragon egg → Lightning ready (no cooldown starts)
```

#### **Session Persistence**
```bash
# Logout/Login Behavior
Player uses lightning → 60s cooldown starts
Player logs out → Cooldown saved
Player logs back in → Cooldown continues from remaining time
# Prevents cooldown bypass through logout/login
```

### **Practical Examples**

#### **Fair Play Scenario**
1. **Use Lightning**: `/ability 1` → 60s cooldown starts
2. **Death**: Player dies → Cooldown cleared
3. **Respawn**: Player respawns → Lightning ready immediately
4. **Use Again**: Player can use lightning right away

#### **Spam Prevention Scenario**
1. **Use Lightning**: `/ability 1` → 60s cooldown starts  
2. **Keep Dragon Egg**: Player keeps dragon egg in inventory
3. **Lose Dragon Egg**: Player drops dragon egg → Cooldown continues
4. **Pick Up Dragon Egg**: Player picks up dragon egg → Cooldown continues
5. **Wait 60s**: Cooldown expires → Lightning ready again

#### **Session Abuse Prevention**
1. **Use Lightning**: `/ability 1` → 60s cooldown starts
2. **Logout**: Player logs out to avoid cooldown
3. **Login**: Player logs back in → Cooldown still active
4. **Wait**: Must wait remaining cooldown time
5. **Ready**: Lightning ready after full 60s from original use

### **Anti-Exploit Features**

- ✅ **Death clears cooldown**: Prevents death-based cooldown bypass
- ✅ **Item independence**: Cooldown doesn't reset when dragon egg is lost/gained
- ✅ **Session persistence**: Cooldown survives logout/login cycles  
- ✅ **Time-based expiration**: Cooldown only expires after full 60 seconds
- ✅ **Fair respawn**: No automatic cooldown restart after death

