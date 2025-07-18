# 🏰 Copper Kingdom

A Minecraft plugin that introduces copper-based weapons with unique properties and enchantment rolls. Designed for Paper 1.21.6+, the plugin focuses on low durability, high-damage copper gear, with chances for rare lucky enhancements.

## ✨ Features

- **New Copper Weapons**: Copper Sword, Copper Axe, and Copper Pickaxe
- **Enchantment Roll System**: Random chances for special enchantments when crafting
- **Custom Crafting Recipes**: Uses copper ingots and sticks
- **Lucky Enhancements**: High durability, poison effects, and bonus damage
- **Configurable Properties**: Fully customizable via `config.yml`
- **Developer Commands**: Easy testing and debugging tools

## 🎲 Enchantment System

Each crafted copper weapon has configurable roll chances for special enhancements:

- **High Durability**: 3x base durability (default 10% chance for swords)
- **Poison on Hit**: Applies poison effect to enemies (default 5% chance for swords)
- **Bonus Damage**: Additional attack damage (default 15% chance for swords)

## 🧱 Crafting Recipes

### Copper Sword
```
[Copper Ingot]  
[Copper Ingot]  
[Stick]         
```

### Copper Axe
```
[Copper Ingot][Copper Ingot]  
[Copper Ingot][Stick]         
[        ][Stick]         
```

### Copper Pickaxe
```
[Copper Ingot][Copper Ingot][Copper Ingot]  
[        ][Stick][        ]  
[        ][Stick][        ]  
```

## 🛠️ Installation

1. Download the latest JAR from the releases
2. Place in your server's `plugins/` directory
3. Restart your server
4. Configure settings in `plugins/CopperKingdom/config.yml`

## 🏗️ Building from Source

Requirements:
- Java 21+
- Maven 3.6+
- Docker (optional, for testing)

### Quick Start
```bash
git clone <repository-url>
cd copper-kingdom
make setup    # Set up development environment
make build    # Build the plugin
make dev      # Build, install, and restart server
```

### Available Commands
```bash
make help           # Show all available commands
make build          # Build plugin JAR
make test           # Run unit tests
make start          # Start test server
make stop           # Stop test server
make restart        # Restart test server
make logs           # View server logs
make attach         # Attach to server console
make test-commands  # Show plugin test commands
```

## 🎮 Commands

### Player Commands
- `/copperkingdom give <weapon>` - Give yourself a copper weapon
- `/copperkingdom help` - Show command help

### Admin Commands
- `/copperkingdom reload` - Reload plugin configuration

### Available Weapons
- `copper_sword` - Copper sword with enchantment rolls
- `copper_axe` - Copper axe with enchantment rolls
- `copper_pickaxe` - Copper pickaxe with enchantment rolls

## ⚙️ Configuration

The plugin is fully configurable via `config.yml`:

```yaml
weapons:
  copper_sword:
    base_damage: 7.0
    base_durability: 75
    display_name: "&6Copper Sword"
    
enchantments:
  copper_sword:
    high_durability: 10    # 10% chance
    poison_on_hit: 5       # 5% chance  
    bonus_damage: 15       # 15% chance
    
enhancements:
  high_durability_multiplier: 3.0
  poison_duration: 60
  bonus_damage_amount: 3.0
```

## 🧪 Testing

### In-Game Testing
1. Start the server: `make start`
2. Connect to `localhost:25565`
3. Get materials: `/give @p minecraft:copper_ingot 64`
4. Get sticks: `/give @p minecraft:stick 64`
5. Craft weapons or use: `/copperkingdom give copper_sword`

### Automated Testing
```bash
make test           # Run unit tests
make docker-test    # Test in Docker container
make validate       # Run full validation suite
```

## 📁 Project Structure

```
src/main/java/org/xpfarm/copperkingdom/
├── CopperKingdom.java           # Main plugin class
├── commands/
│   └── CopperKingdomCommand.java # Command handler
├── events/
│   ├── CraftingListener.java    # Crafting event handler
│   └── WeaponListener.java      # Weapon attack handler
├── items/
│   └── CopperWeapons.java       # Weapon creation and management
└── recipes/
    └── CopperRecipes.java       # Recipe registration
```

## 🐳 Docker Support

Test the plugin in a containerized environment:

```bash
make docker-build   # Build Docker image with plugin
make docker-test    # Run automated Docker tests
```

The Docker setup includes:
- Paper 1.21.6 server
- Geyser + Floodgate for Bedrock compatibility
- ViaVersion for cross-version support
- Automatic plugin installation

## 🔧 Development

### Debug Commands
```bash
make debug          # Run debug script
./debug-plugin.sh status    # Check plugin status
./debug-plugin.sh logs      # Check plugin logs
./debug-plugin.sh compile   # Test compilation
```

### Code Quality
```bash
make lint           # Check shell scripts with shellcheck
make format         # Format Java code (if configured)
make validate       # Run linting and tests
```

## 📦 Dependencies

- **Paper API**: 1.21.6-R0.1-SNAPSHOT
- **Java**: 21+
- **Maven**: 3.6+

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `make validate`
5. Submit a pull request

Follow the development standards in `CONTRIBUTING-ai-plugin-dev.md`.

## 📄 License

This project follows the same license as the parent repository.

## 🏷️ Version

Current version: 1.0.0

Compatible with:
- Minecraft Java Edition 1.21+
- Paper 1.21.6+
- Java 21+

---

*Built with ⚒️ for the Minecraft community*
