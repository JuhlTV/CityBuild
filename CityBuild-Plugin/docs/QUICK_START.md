# CityBuild Plot System - Quick Start for Admins

Get your plot system up and running in **5 minutes**.

---

## ✅ Prerequisites Checklist

- [ ] Minecraft Server 1.21.1+ (Paper/Spigot)
- [ ] Java 21+
- [ ] CityBuild Plugin JAR
- [ ] OP status on server

---

## 🚀 5-Minute Setup

### Step 1: Install Plugin (2 min)
```bash
# 1. Download CityBuildPlugin JAR
# 2. Place in /plugins/ directory
# 3. Restart server

# Expected log output:
# ✓ CityBuild Plugin loaded
# ✓ Loaded 0 plots from database
# ✓ Economy system initialized
```

### Step 2: Grant Yourself OP (1 min)
```bash
# In server console or in-game:
/op YourUsername

# In-game, verify:
/citybuild admin plot help
```

### Step 3: Test Plot System (2 min)

**As a regular player:**
```
/citybuild buy
→ See: "✓ Plot #1 purchased for $5000"
→ Terrain appears at Y=-60 with grass surface
→ Oak fence border visible
→ Sign on center platform with plot info
```

**As admin, verify creation:**
```
/citybuild admin plot info YourUsername
→ Shows: Plot #1, Size 16x16, Owner: [YourUUID], Area: 256m²
```

**Test selling:**
```
/citybuild sell
→ See: "✓ Plot sold for $4000"
→ Terrain completely removed
→ Area empty
```

✅ **Done!** Your plot system is working.

---

## 🎮 Basic Admin Commands (Copy-Paste)

### View Plot Info
```
/citybuild admin plot info <player>
```
Shows owner, size, location, members, premium status.

### Expand a Plot
```
/citybuild admin plot expand <player> north 5
```
Extends plot 5 blocks northward. Try: `north`, `south`, `east`, `west`

### Resize to Exact Size
```
/citybuild admin plot resize <player> 30 30
```
Sets plot to exactly 30×30 blocks (min: 10×10, max: 100×100).

### Clear Player Builds
```
/citybuild admin plot clear <player>
```
Removes player-built blocks, keeps borders and grass.

### Delete Plot Completely
```
/citybuild admin plot delete <player>
```
⚠️ **Irreversible** - removes everything and ownership.

### Teleport to Plot
```
/citybuild admin plot teleport <player>
```
Instantly TP to player's plot spawn.

### List All Plots
```
/citybuild admin plot list
```
Shows all plots on server with owners and sizes.

### Toggle Premium Status
```
/citybuild admin plot premium <player> on
```
Set to `on` or `off` for VIP perks.

---

## ⚠️ Common Mistakes to Avoid

### ❌ "Cannot find player"
- Make sure player **has already been online** (UUID must be cached)
- Try: `/citybuild admin plot list` to see available players
- **Fix:** Player must join server at least once

### ❌ "Player has no plots"
- Player hasn't purchased a plot yet
- **Fix:** Have player run `/citybuild buy` first
- Or create as admin: (not yet available - player must buy)

### ❌ Plot terrain doesn't appear
- World might be in wrong location
- Check Y=-60 is accessible
- **Fix:** Verify in config that plot world is correct
- Try: `/citybuild admin plot info <player>` to see location

### ❌ Can't run admin commands
- Not OP status
- **Fix:** `/op YourUsername` in console
- Verify: Try `/give @s diamond` (should work if OP)

### ❌ Plot protection not working
- Player can still build on others' plots
- **Fix:** Restart server after plugin install
- Check logs for errors

---

## 📝 First-Time Admin Workflow

### 1. Welcome Player
```
Player: "Hi, can I get a plot?"
You: "/citybuild admin plot help"
Show them available commands
```

### 2. Player Buys Plot
```
/citybuild buy
→ They see terrain generate
→ You verify: /citybuild admin plot info <player>
```

### 3. Help Them
```
If plot is too small:   /citybuild admin plot resize <player> 20 20
If location bad:        /citybuild admin plot expand <player> east 10
If they griefed:        /citybuild admin plot clear <player>
If they bought wrong:   /citybuild admin plot delete <player>
```

---

## 🔧 Important Configuration

Edit `plugins/CityBuild/config.yml`:

```yaml
economy:
  plot_buy_price: 5000      # Cost to purchase
  plot_sell_price: 4000     # Revenue from sale

plot:
  default_size: 16          # Starting plot size
  min_size: 10              # Minimum allowed
  max_size: 100             # Maximum allowed
```

Restart server after config changes: `/restart` or manual restart.

---

## 📂 File Locations

- **Config:** `plugins/CityBuild/config.yml`
- **Plots Data:** `plugins/CityBuild/data/plots.json`
- **Logs:** `logs/latest.log`

⚠️ **Always backup `plots.json` before major changes!**

---

## 🆘 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Plugin won't load | Check Java 21+, Paper 1.21.1+ |
| Commands don't work | Verify OP status: `/op YourUsername` |
| Plot doesn't generate | Restart server, check logs |
| Can build on others' plots | Restart server, check protection listener |
| Can't find player | Player must join once for UUID caching |

---

## 📖 Need More Help?

- 📄 Full command list: [ADMIN_COMMANDS.md](ADMIN_COMMANDS.md)
- ⚙️ Configuration guide: [CONFIGURATION.md](CONFIGURATION.md)
- 🧪 Testing before deploy: [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md)
- 🔨 Building locally: [BUILD_GUIDE.md](BUILD_GUIDE.md)

---

## ✅ You're Ready!

You now know:
- ✅ How to install the plugin
- ✅ How to test plot generation
- ✅ 8 essential admin commands
- ✅ Common mistakes to avoid
- ✅ Where to find help

**Next:** Learn [ADMIN_COMMANDS.md](ADMIN_COMMANDS.md) for all 8 subcommands with detailed examples.

---

**Questions?** Open an issue: [GitHub Issues](../../../issues)
