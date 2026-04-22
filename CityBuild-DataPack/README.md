# CityBuild - Vanilla Data Pack

A complete City Building system for **Vanilla Minecraft** using Data Packs and Commands.

✅ **No crashes**  
✅ **Works with Vanilla servers**  
✅ **No plugins/mods needed**  
✅ **Fully functional**

## Installation

1. Download the `CityBuild-DataPack` folder
2. Upload to your server: `world/datapacks/`
3. Restart server (or `/reload`)
4. Done! Use `/function citybuild:help`

## Commands

### Plot System
```
/function citybuild:plot/buy       - Buy a plot ($5000)
/function citybuild:plot/sell      - Sell your plot ($4000)
/function citybuild:plot/info      - View your plots
/function citybuild:plot/list      - List all plots
```

### Economy
```
/function citybuild:economy/balance    - Check balance
/function citybuild:economy/add        - Add $100
/function citybuild:economy/remove     - Remove $100
```

### Admin
```
/function citybuild:admin/stats    - View statistics
/function citybuild:admin/reset    - Reset all data
```

## Features

- **Plot Management** - Buy, sell, track plots
- **Economy System** - Balance tracking via scoreboards
- **Admin Tools** - Reset, statistics
- **Vanilla Compatible** - Works on any Minecraft server

## How It Works

- Uses Minecraft **scoreboards** for data storage
- Uses **functions** for commands
- All stored in-game (no database needed)
- Persists across server restarts

## Next Steps

- Add plot coordinates
- Implement taxes
- Add NPC shops
- Create land claiming system
