# ⚡ Minecraft Dragon Egg Lightning Plugin

<div align="center">

![Dragon Egg Lightning Banner](https://img.shields.io/badge/Minecraft-Paper%201.21.8-blue?logo=minecraft&style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21+-ED8B00?logo=openjdk&logoColor=white&style=for-the-badge)
![Version](https://img.shields.io/badge/Version-1.0.0-green?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)

**Transform your server with the ultimate Dragon Egg lightning ability!**

[![Download Latest Release](https://img.shields.io/badge/Download-Latest%20Release-ff6b6b?style=for-the-badge&logo=github)](https://github.com/ronaldtse/papermc-plugin-dragon-egg/releases/latest)
[![GitHub Stars](https://img.shields.io/github/stars/ronaldtse/papermc-plugin-dragon-egg?style=social)](https://github.com/ronaldtse/papermc-plugin-dragon-egg)
[![GitHub Forks](https://img.shields.io/github/forks/ronaldtse/papermc-plugin-dragon-egg?style=social)](https://github.com/ronaldtse/papermc-plugin-dragon-egg/fork)

</div>

## 🌟 Why Choose Dragon Egg Lightning?

### **For Players**
- 🎯 **Strategic Gameplay** - Requires skill and timing to master
- ⚡ **Epic Lightning Effects** - 3 sequential purple lightning strikes with thunder sounds
- 🎮 **Intuitive Controls** - Simple `/ability 1` command with Dragon Egg in offhand
- 🏆 **Balanced Combat** - 6.0 hearts total damage (2.0 per strike) with strategic cooldown
- 🛡️ **Armor-Bypassing** - Lightning damage ignores all armor and protection enchantments
- 📊 **Real-Time Feedback** - HUD displays cooldown status and ability readiness

### **For Server Administrators**
- ✅ **Instantly Boost Server Engagement** - Give players a unique, exciting ability
- ✅ **Easy 5-Minute Installation** - Drop-in plugin with zero configuration needed
- ✅ **Zero Performance Impact** - Optimized for Paper 1.21.8 with minimal resource usage
- ✅ **Anti-Spam Protection** - Built-in 60-second cooldown prevents abuse
- ✅ **Complete Player Experience** - Includes HUD, sound effects, and visual feedback

---

## 🚀 Quick Start (5 Minutes)

### **Step 1: Download & Install**
```bash
# Download the latest JAR from releases
# Place in your Paper server's plugins directory
cp DragonEggLightning-1.0.0.jar /path/to/your/paper-server/plugins/

# Restart your Paper 1.21.8+ server
java -Xms2G -Xmx2G -jar paper-1.21.8-latest.jar nogui
```

### **Step 2: Verify Installation**
```bash
# Check server console for:
[INFO] DragonEggLightning enabled

# In-game test:
/plugins
# Should show: DragonEggLightning ✓
```

### **Step 3: Start Using Lightning!**
```bash
# Give yourself a Dragon Egg
/give @p minecraft:dragon_egg

# Move to offhand (press F key)
/ability 1

# Watch the magic happen! ⚡
```

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

## 📋 Complete Feature List

### **Core Lightning Ability**
- 🎯 **Smart Targeting**: Ray-tracing finds closest living entity in player's view
- ⚡ **Triple Strike**: 3 sequential lightning bolts with perfect timing
- 🎨 **Purple Effects**: Custom particle effects and lightning visualization
- 🔊 **Audio Experience**: Thunder sounds and impact audio
- 💥 **Armor-Bypassing Damage**: 2.0 hearts per strike (4 HP) - ignores all protection
- 🛡️ **Consistent Damage**: Same damage regardless of target's armor or enchantments

### **Player Interface**
- 📊 **HUD Display**: Real-time cooldown status in action bar
- ⏰ **Countdown Timer**: Shows remaining cooldown in seconds
- ✅ **Ready Indicator**: "⚡ Lightning ready" when ability is available
- 💬 **Progress Messages**: "Lightning strike 1/3!" during casting

### **Safety & Validation**
- 🛡️ **Item Validation**: Fails if Dragon Egg not in offhand
- 🎯 **Target Validation**: Requires line-of-sight to living entities
- ⚖️ **Cooldown Enforcement**: Prevents spam and maintains balance
- 🛑 **Smart Cancellation**: Stops if player switches items during casting

---

## 🎮 Player Usage Guide

### **Getting Started**
1. **Obtain Dragon Egg**: `/give @p minecraft:dragon_egg`
2. **Equip Offhand**: Press `F` key to move Dragon Egg to offhand
3. **Find Target**: Look at nearest entity within 50 blocks
4. **Cast Ability**: Execute `/ability 1`
5. **Watch Effects**: Enjoy 3 sequential lightning strikes!

### **Advanced Tips**
- **Range Mastery**: Learn the 50-block range for optimal targeting
- **Timing Strategy**: Use cooldown wisely - plan your next lightning strike
- **Positioning**: Move to get clear line-of-sight on targets
- **Armor-Bypassing**: Lightning damage works equally well against heavily armored targets
- **Survival Mode**: 6.0 hearts total damage can eliminate most mobs instantly

---

## 🏗️ Technical Excellence

### **Performance Optimized**
- **Efficient Ray Tracing**: Optimized entity detection within 50-block range
- **Smart Particle System**: Limited effects to prevent client lag
- **Minimal Resource Usage**: Designed for Paper 1.21.8 optimization
- **Memory Efficient**: No memory leaks or performance degradation

### **Code Quality**
- **Test-Driven Development**: 24/24 unit tests passing
- **Clean Architecture**: Modular design with clear separation of concerns
- **Future-Proof**: Extensible ability system for adding new powers
- **Production Ready**: Comprehensive error handling and edge case management

---

## 🔧 Server Configuration

### **Recommended Settings**
```properties
# server.properties
gamemode=survival
difficulty=normal
view-distance=10
simulation-distance=10
max-players=20
```

### **JVM Arguments for Best Performance**
```bash
java -Xms4G -Xmx4G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -jar paper-1.21.8-latest.jar
```

### **Monitoring Commands**
```bash
# Check plugin status
/plugins

# Monitor server performance
/tps  # Should remain ~20
/mem  # Check memory usage

# Test lightning ability
/ability 1
```

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

### **Installation for Players**
1. **Server Admin**: Install JAR in plugins directory
2. **Player**: Obtain Dragon Egg via `/give @p minecraft:dragon_egg`
3. **Equip**: Press `F` to move Dragon Egg to offhand
4. **Use**: Execute `/ability 1` command
5. **Enjoy**: Experience epic lightning strikes!

### **Developer Setup**

#### **System Prerequisites**

**Required Software:**
- **Docker Desktop** - [Download for macOS/Windows/Linux](https://www.docker.com/products/docker-desktop/)
- **Java 21+** - [OpenJDK](https://adoptium.net/) or Oracle JDK
- **Maven 3.6+** - Install via Homebrew (`brew install maven`) or [official download](https://maven.apache.org/download.cgi)
- **Git** - [Download](https://git-scm.com/downloads)

**macOS Installation (Recommended):**
```bash
# Install Homebrew (if not already installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install prerequisites
brew install openjdk@21 maven git

# Install Docker Desktop from https://www.docker.com/products/docker-desktop/
```

#### **Quick Start for Developers**

```bash
# 1. Clone repository
git clone https://github.com/ronaldtse/papermc-plugin-dragon-egg.git
cd papermc-plugin-dragon-egg

# 2. Configure environment (optional - defaults are set)
# Edit .env file to customize server settings if needed
# Most developers can skip this step

# 3. Build plugin JAR
./build.sh

# 4. Start server with plugin
./start-server.sh

# 5. Connect and test
# Server: localhost:25565
# Username: posiflow
# RCON: localhost:25575 (password: dragon123)

# 6. Stop server
./stop-server.sh
```

#### **Development Commands**

**Build Commands:**
```bash
# Standard build
./build.sh

# Clean build (removes previous builds)
./build.sh --clean
# or
./build.sh -c
```

**Server Management:**
```bash
# Start server normally
./start-server.sh

# Start with Docker image rebuild
./start-server.sh --rebuild
# or
./start-server.sh -r

# Clean build and start (removes all data)
./start-server.sh --clean
# or
./start-server.sh -c

# Combined clean rebuild
./start-server.sh --rebuild --clean
# or
./start-server.sh -r -c

# Show help
./start-server.sh --help
```

**Development Workflow:**
```bash
# Make code changes
# Edit Java files in src/main/java/

# Build and test
./build.sh --clean

# Start fresh server for testing
./start-server.sh --clean

# Monitor server logs
docker logs -f papermc-dragonegg

# When done
./stop-server.sh
```

#### **Configuration Files**

**`.env` File (Optional Customization):**
```bash
# Server Configuration
MEMORYSIZE=2G
EULA=TRUE
TYPE=PAPER
VERSION=1.21.8
MAX_PLAYERS=10
RCON_PORT=25575
RCON_PASSWORD=dragon123

# Plugin Configuration
PLUGIN_VERSION=1.0.0
ADMIN_USERNAME=posiflow

# Server Properties
SERVER_NAME="Dragon Egg Lightning Server"
MOTD="Dragon Egg Lightning Plugin Server"
```

#### **Development Tips**

**Testing the Plugin:**
```bash
# Give yourself a Dragon Egg
/give @p minecraft:dragon_egg

# Move to offhand (press F key)
/ability 1

# Check plugin status
/plugins

# View server performance
/tps
/mem
```

**Debugging:**
```bash
# View live server logs
docker logs -f papermc-dragonegg

# View recent logs
docker logs papermc-dragonegg --tail 50

# Connect to server console
docker attach papermc-dragonegg

# Exit console without stopping server
# Press Ctrl+C then Ctrl+P then Ctrl+Q
```

**Common Development Tasks:**
```bash
# Make small code changes and test
# 1. Edit Java files
# 2. Build: ./build.sh
# 3. Restart server: ./start-server.sh -r

# Major changes requiring clean environment
# 1. Edit Java files
# 2. Clean build: ./build.sh --clean
# 3. Clean server: ./start-server.sh --clean

# Test plugin loading
# Check server logs for: [DragonEggLightning] DragonEggLightning plugin enabled!
```

#### **Troubleshooting Development Issues**

**Build Errors:**
```bash
# Clean Maven cache and rebuild
./build.sh --clean

# Check Java version (must be 21+)
java -version

# Verify Maven installation
mvn --version
```

**Docker Issues:**
```bash
# Restart Docker Desktop
# Check Docker is running: docker ps

# Force rebuild everything
./start-server.sh --clean

# Remove all Docker resources
docker system prune -a
```

**Server Connection Issues:**
```bash
# Check if container is running
docker ps

# Check ports are not in use
lsof -i :25565
lsof -i :25575

# View container logs
docker logs papermc-dragonegg
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

#### **Lightning Damage Seems Low**
- **Note**: Lightning now deals 2.0 hearts per strike (6.0 hearts total)
- **Armor-Bypassing**: Damage ignores all armor and enchantments
- **Consistent**: Same damage against any target, regardless of protection

---

## 📈 Why This Plugin Will Transform Your Server

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

## 🎉 Ready to Lightning Your Server?

### **Download Now**
[![Download Latest Release](https://img.shields.io/badge/Download-Now%20🚀-ff6b6b?style=for-the-badge&logo=github)](https://github.com/ronaldtse/papermc-plugin-dragon-egg/releases/latest)

### **Installation Time: 5 Minutes**
1. Download the JAR file
2. Place in your Paper server's plugins directory
3. Restart your server
4. Start casting lightning!

### **Test It Today**
```bash
# Connect to your server
# Give yourself: /give @p minecraft:dragon_egg
# Move to offhand: Press F
# Cast lightning: /ability 1
# Experience the magic! ⚡
```

---

## 📞 Support & Community

### **Getting Help**
- **GitHub Issues**: [Report bugs or request features](https://github.com/ronaldtse/papermc-plugin-dragon-egg/issues)
- **Documentation**: Comprehensive guides and troubleshooting
- **Community**: Connect with other server administrators

### **Contributing**
- **Bug Reports**: Help us improve by reporting issues
- **Feature Requests**: Suggest new abilities or improvements
- **Code Contributions**: Submit pull requests to enhance the plugin

---

## 📄 License & Credits

**License**: MIT License - Free for personal and commercial use

**Author**: Augustus Tse and Octavius Tse
**Version**: 1.0.0
**Minecraft Version**: 1.21.8+
**Paper API**: 1.21.8-R0.1-SNAPSHOT

---

<div align="center">

**⭐ Star this project if you love Dragon Egg Lightning!** ⭐

[![GitHub Stars](https://img.shields.io/github/stars/ronaldtse/papermc-plugin-dragon-egg?style=social)](https://github.com/ronaldtse/papermc-plugin-dragon-egg)
[![GitHub Forks](https://img.shields.io/github/forks/ronaldtse/papermc-plugin-dragon-egg?style=social)](https://github.com/ronaldtse/papermc-plugin-dragon-egg/fork)

**Transform your server today with Dragon Egg Lightning!** ⚡

[Download Latest Release](https://github.com/ronaldtse/papermc-plugin-dragon-egg/releases/latest) | [Installation Guide](#-quick-start-5-minutes) | [Support](https://github.com/ronaldtse/papermc-plugin-dragon-egg/issues)

</div>
