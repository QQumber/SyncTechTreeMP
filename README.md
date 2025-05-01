# Shared Tech Tree Mod for Mindustry

This mod enables collaborative research and blueprint sharing in Mindustry multiplayer campaign mode. By default, Mindustry doesn't properly sync tech tree progress between players in multiplayer campaigns, which can lead to frustrating gameplay where only the host has access to certain technologies.

## Features

- Automatically synchronizes research progress between all players in multiplayer
- Shares unlocked blueprints/schematics with everyone in the game
- Works with Steam multiplayer campaign mode
- Compatible with vanilla Mindustry content

## Installation

### Method 1: Import GitHub Repository
1. In Mindustry, go to `Mods`
2. Click `Import GitHub Mod`
3. Enter: `QQumber/shared-tech-tree`
4. Click `OK`

### Method 2: Manual Installation
1. Download the mod files
2. Place the mod folder or .jar file in your Mindustry mods directory:
   - Windows: `%AppData%/Mindustry/mods/`
   - Linux: `~/.local/share/Mindustry/mods/`
   - MacOS: `~/Library/Application Support/Mindustry/mods/`
3. Restart Mindustry

## Usage

The mod works automatically once installed. Just host or join a multiplayer campaign and the tech tree and blueprints will be synchronized automatically.

### Server Commands

Server administrators have access to the following commands:
- `/synctechall` - Manually force synchronization of the tech tree to all players
- `/techmoddebug` - Toggle debug mode for more detailed logging (useful for troubleshooting)

## Troubleshooting

If players aren't seeing the same tech tree:
1. Make sure the mod is installed on the server
2. Try having the server administrator run the `/synctechall` command
3. Ensure all players are running the same version of the mod and Mindustry

## Limitations

- The mod needs to be installed on the server for full functionality
- In rare cases, you might need to restart the server to ensure proper synchronization
- Client-side only features may still require players to have the mod installed

## Compatibility

- Tested with Mindustry v136+
- Should be compatible with most other mods, but conflicts may occur with mods that modify the same systems

## License

This mod is released under the MIT License. See the LICENSE file for details.
