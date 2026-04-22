# CityBuild Setup - Physgun Server

## ⚠️ IMPORTANT: Setup Steps

### 1. Upload Data Pack to Physgun Server

**Via File Manager:**
```
1. Login to Physgun Dashboard
2. Go to: Files → File Manager
3. Navigate: /home/container/world/datapacks/
4. Upload: CityBuild-DataPack folder (or ZIP)
5. Wait for upload complete
```

### 2. Restart Server

```
1. Physgun Dashboard → Server
2. Click: Restart
3. Wait for server to start (30-60 seconds)
4. Check server logs for "[CityBuild]" messages
```

### 3. Initialize Data Pack (In-Game as OP)

**Connect to your server and run:**
```
/reload
/function citybuild:init
```

**Expected output:**
```
[CityBuild] System initialized! Use /function citybuild:help for commands
[CityBuild] City Building System Loaded!
[CityBuild] Type /function citybuild:help for commands
```

### 4. Test All Commands

```
/function citybuild:help           → Shows command list ✓
/function citybuild:economy/balance → Shows your money ($10000) ✓
/function citybuild:plot/info      → Shows your plots (0) ✓
/function citybuild:plot/buy       → Buy a plot ($5000) ✓
/function citybuild:economy/balance → Check new balance ($5000) ✓
```

---

## 🚨 Troubleshooting

### Data Pack Not Loading?
- ✓ Check file is in `/home/container/world/datapacks/`
- ✓ Folder name must be exactly: `CityBuild-DataPack`
- ✓ Restart server after uploading
- ✓ Run `/reload` command

### Commands Show Errors?
- ✓ Run `/function citybuild:init` first
- ✓ Make sure you're in `game_mode: creative` or OP
- ✓ Check server logs for errors
- ✓ Restart server completely

### Scores Not Saving?
- ✓ Scoreboards are created by `/function citybuild:init`
- ✓ Data saves automatically every 6000 ticks (5 min)
- ✓ Check with: `/scoreboard players list @s`

### Can't Use Commands?
- ✓ You need OP status: `/op [name]`
- ✓ Or use `game_mode: creative`
- ✓ Economy/Plot commands work for all players

---

## 📊 Data Storage

All player data is stored in Minecraft **Scoreboards**:
- `cb_balance` - Player money (starts at $10000)
- `cb_plots` - Number of plots owned (starts at 0)
- `cb_admin` - Admin flags (system use)

**Data persists across:**
- Server restarts ✓
- Plugin reloads ✓
- Player logouts ✓

---

## 🎮 Command Reference

### Plot Commands
```
/function citybuild:plot/buy      Buy plot ($5000)
/function citybuild:plot/sell     Sell plot (+$4000)
/function citybuild:plot/info     View your plots
/function citybuild:plot/list     All plots list
```

### Economy Commands
```
/function citybuild:economy/balance    Check balance
/function citybuild:economy/add        Add $100 (test)
/function citybuild:economy/remove     Remove $100 (test)
```

### Admin Commands (OP Only)
```
/function citybuild:admin/stats    System stats
/function citybuild:admin/reset    Reset all data
```

---

## ✅ Success Checklist

- [ ] Data Pack uploaded to `/home/container/world/datapacks/CityBuild-DataPack/`
- [ ] Server restarted
- [ ] `/function citybuild:init` executed
- [ ] `/function citybuild:help` shows colored commands
- [ ] `/function citybuild:economy/balance` shows $10000
- [ ] `/function citybuild:plot/buy` deducts $5000 successfully
- [ ] `/function citybuild:economy/balance` shows $5000 after purchase
- [ ] Data persists after server restart

---

**If all steps pass, your CityBuild system is 100% ready!** 🚀
