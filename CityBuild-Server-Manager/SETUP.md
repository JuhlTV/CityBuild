# Setup Instructions for CityBuild Server Manager

## Quick Start

### Windows
```bash
run.bat
```

### Linux/Mac
```bash
chmod +x run.sh
./run.sh
```

### Manual
```bash
pip install -r requirements.txt
python citybuild_manager.py
```

---

## Configuration

### 1. Edit `.env`
```
RCON_HOST=your-server-ip
RCON_PORT=25575
RCON_PASSWORD=your_rcon_password
```

### 2. Enable RCON on Server

**Edit `server.properties`:**
```properties
enable-rcon=true
rcon.port=25575
rcon.password=your_rcon_password
```

### 3. Restart Server
```
/reload
```

### 4. Run Manager
```
python citybuild_manager.py
```

---

## Verify Installation

### Check RCON Connection
```
In Manager, it should show:
✓ Connected to server: [your-ip]:25575
```

### Test Command
```
Command: balance
Player name: YourName
Should return: balance amount
```

---

## First Time Setup

1. **Start manager** with `run.bat` or `python citybuild_manager.py`
2. **Type "stats"** to see system statistics
3. **Type "list"** to see registered players
4. **Create player** by typing "balance" then entering name

---

## Common Issues

### RCON connection fails
- Check server has `enable-rcon=true` in server.properties
- Check password is correct
- Check IP address is accessible
- Check firewall allows port 25575

### Players not showing in list
- Players are added automatically when balance is checked
- Or use "/function citybuild:init" on server to initialize

### Data not persisting
- Check `data/citybuild.json` exists
- Check write permissions on data folder
- Check logs/citybuild.log for errors

---

## File Structure

```
CityBuild-Server-Manager/
├── citybuild_manager.py       ← Main script
├── requirements.txt           ← Python dependencies
├── .env.example              ← Configuration template
├── .env                      ← Your configuration (create from .env.example)
├── run.py                    ← Python launcher
├── run.bat                   ← Windows launcher
├── run.sh                    ← Linux/Mac launcher
├── SETUP.md                  ← This file
├── README.md                 ← Full documentation
├── data/                     ← Player data storage
│   └── citybuild.json        ← Player data (auto-created)
└── logs/                     ← Server logs
    └── citybuild.log        ← Manager logs (auto-created)
```

---

## Next Steps

After setup works:

1. **Create custom commands** in citybuild_manager.py
2. **Add Discord bot** integration
3. **Build web dashboard**
4. **Add advanced economy features**
   - Taxes
   - Rent
   - Trading
   - Auctions

---

**Happy City Building!** 🎮🏗️
