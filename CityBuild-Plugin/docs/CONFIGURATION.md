# CityBuild Plot System - Configuration Guide

Complete guide to configuring the plot system via `plugins/CityBuild/config.yml`.

---

## 📄 Configuration File Location

```
plugins/CityBuild/
└── config.yml
```

Edit with any text editor. **Restart server** after changes to apply.

---

## ⚙️ Configuration Options

### Economy Settings

```yaml
economy:
  plot_buy_price: 5000          # Cost in $ to purchase a plot
  plot_sell_price: 4000         # Revenue in $ from selling a plot
```

**Example:** To make plots more expensive:
```yaml
economy:
  plot_buy_price: 10000
  plot_sell_price: 8000
```

---

### Plot Sizing

```yaml
plot:
  default_size: 16              # Default width/height when created (blocks)
  min_size: 10                  # Minimum allowed size (blocks)
  max_size: 100                 # Maximum allowed size (blocks)
  spacing: 2                    # Gap between plots in grid (blocks)
```

**Example:** To allow larger plots by default:
```yaml
plot:
  default_size: 32              # New players start with 32x32
  min_size: 10
  max_size: 150                 # Allow up to 150x150
  spacing: 2
```

⚠️ **Note:** Changing `default_size` only affects **newly created** plots. Existing plots keep their size.

---

### Terrain Settings

```yaml
terrain:
  generation_height: -60        # Y-level for plot surface (grass level)
  border_height: -59            # Y-level for fence borders
  material_grass: GRASS_BLOCK   # Surface material
  material_dirt: DIRT           # Underlay material (Y-63 to -61)
  material_border: OAK_FENCE    # Border fence material
  material_spawn: OAK_PLANKS    # Spawn platform material
```

**Example:** To change to stone/dark oak:
```yaml
terrain:
  generation_height: -60
  border_height: -59
  material_grass: STONE         # Change grass to stone
  material_border: DARK_OAK_FENCE
  material_spawn: DARK_OAK_PLANKS
```

**Valid Materials:**
- Ground: `GRASS_BLOCK`, `STONE`, `DIRT`, `SAND`, `GRAVEL`
- Border: `OAK_FENCE`, `DARK_OAK_FENCE`, `SPRUCE_FENCE`, `BIRCH_FENCE`
- Spawn Platform: `OAK_PLANKS`, `DARK_OAK_PLANKS`, `SPRUCE_PLANKS`, `BIRCH_PLANKS`, `POLISHED_BLACKSTONE_BRICKS`

---

### Grid & Layout

```yaml
plot:
  spacing: 2                    # Blocks between plots
  plots_per_row: 10             # How many plots fit horizontally
```

**Grid Layout Example (with spacing: 2):**
```
Plot 1:  X=0-15,  Z=0-15
Plot 2:  X=18-33, Z=0-15   (gap of 2 blocks)
Plot 3:  X=36-51, Z=0-15   (gap of 2 blocks)
```

---

## 🔄 Default config.yml

Here's a complete default configuration:

```yaml
# CityBuild Plot System Configuration

economy:
  # Cost to purchase a plot ($)
  plot_buy_price: 5000
  # Revenue from selling a plot ($)
  plot_sell_price: 4000

plot:
  # Default size for new plots (width × height blocks)
  default_size: 16
  # Minimum plot size that admins can resize to
  min_size: 10
  # Maximum plot size that admins can resize to
  max_size: 100
  # Spacing between plots in grid (blocks)
  spacing: 2
  # Plots per row in grid layout
  plots_per_row: 10

terrain:
  # Y-level for plot surface (where players spawn)
  generation_height: -60
  # Y-level for borders (fence blocks)
  border_height: -59
  # Material for grass/surface
  material_grass: GRASS_BLOCK
  # Material for dirt underlay
  material_dirt: DIRT
  # Material for fence borders
  material_border: OAK_FENCE
  # Material for spawn platform
  material_spawn: OAK_PLANKS

logging:
  # Enable detailed logging of plot operations
  debug_mode: false
  # Log plot purchases/sales
  log_economy: true
  # Log plot protection events
  log_protection: true
```

---

## 🎨 Common Configuration Presets

### "Luxury Server" - Expensive Plots
```yaml
economy:
  plot_buy_price: 50000
  plot_sell_price: 40000

plot:
  default_size: 32
  max_size: 200

terrain:
  material_grass: POLISHED_BLACKSTONE
  material_border: DARK_OAK_FENCE
  material_spawn: DARK_OAK_PLANKS
```

### "Budget Server" - Cheap, Small Plots
```yaml
economy:
  plot_buy_price: 1000
  plot_sell_price: 800

plot:
  default_size: 10
  max_size: 32
  spacing: 1

terrain:
  material_grass: STONE
  material_border: STONE_BRICKS
```

### "Sandbox Server" - Large Creative Plots
```yaml
economy:
  plot_buy_price: 0         # Free!
  plot_sell_price: 0

plot:
  default_size: 64
  min_size: 32
  max_size: 256
  spacing: 3

terrain:
  material_grass: GRASS_BLOCK
  material_border: GLOWSTONE
  material_spawn: GOLD_BLOCK
```

---

## 📋 Configuration Checklist

Before deploying to production:

- [ ] Plot prices match server economy
- [ ] Default plot size is appropriate
- [ ] Terrain materials look good (test in creative)
- [ ] Height (-60) is accessible on your world
- [ ] Border material is distinctive (not same as terrain)

---

## 🔄 Hot Reloading

**Not all settings reload without restart.**

| Setting | Reload? | Notes |
|---------|---------|-------|
| `plot_buy_price` | ✅ Yes | Applies immediately |
| `default_size` | ✅ Yes | Only for NEW plots |
| `material_grass` | ❌ No | Requires restart |
| `generation_height` | ❌ No | Requires restart |

**To apply all changes safely:** Restart server
```bash
# In-game as OP:
/restart
```

---

## ⚠️ Configuration Warnings

### ⚠️ Don't set height outside these ranges:
```
Y = -64 (bedrock)
Y = 320 (sky limit 1.18+)

Recommended: -64 to 0 for underground bases
          :  0 to 256 for above-ground
```

### ⚠️ Don't use invalid materials:
```
❌ INVALID:     material_grass: WOODEN_DOOR
✅ VALID:       material_grass: GRASS_BLOCK

Material must be a solid block, not entity or door
```

### ⚠️ Spacing too small creates overlap:
```
default_size: 16
spacing: 0        ❌ Plots touch! No room for borders

default_size: 16
spacing: 2        ✅ Good - 2 block gap between plots
```

---

## 🔍 Verify Configuration

After editing, verify syntax is correct:

1. **Check file format** - YAML is whitespace-sensitive
   - Use **spaces** (not tabs)
   - Indent consistently

2. **Check values** - Make sure:
   - Numbers are valid (no quotes for numbers)
   - Materials exist (check server version)
   - Heights make sense (-64 to 320)

3. **Test in-game**:
   ```
   /citybuild buy        # Should cost configured price
   /citybuild admin plot info <player>  # Check plot size
   ```

---

## 📊 Configuration Impact on Performance

| Setting | Performance Impact | Notes |
|---------|-------------------|-------|
| Larger plots | ⬆️ Higher | 100×100 = 10,000 blocks to generate |
| More plots | ⬆️ Higher | Each plot in JSON persists |
| Different materials | ➡️ Neutral | No impact |
| Height level | ➡️ Neutral | No impact |

**Optimization Tip:** Default size `16×16` = 256 blocks to generate = fast  
Larger sizes (50×50+) = more time, consider `max_size: 75` as limit

---

## 🆘 Troubleshooting Configuration

### Problem: "Unknown material GRASS_BLOCK"
**Cause:** Server version doesn't recognize material name  
**Fix:** Check Minecraft version (need 1.21.1+)  
**Workaround:** Use `GRASS` instead of `GRASS_BLOCK` (older versions)

### Problem: Config won't apply
**Cause:** YAML syntax error (usually indentation)  
**Fix:** Check spacing - each level should indent by 2 spaces:
```yaml
✅ Correct:
economy:
  plot_buy_price: 5000

❌ Wrong (tab instead of space):
economy:
→plot_buy_price: 5000
```

### Problem: Plots generate at wrong height
**Cause:** `generation_height` setting incorrect  
**Fix:** Check `/citybuild admin plot info` - shows actual Y level  
**Solution:** Edit config, set correct Y, restart

---

## 📖 Advanced Configuration

### Custom Spawn Platform Size
Currently: 3×3 oak planks
To change: Edit `PlotGenerator.java` (requires rebuild)
```java
// Line ~120 in PlotGenerator.java
for (int x = cornerX - 1; x <= cornerX + 1; x++) {  // 3x3
    for (int z = cornerZ - 1; z <= cornerZ + 1; z++) {
```

### Custom Terrain Generation
Edit `PlotGenerator.generatePlot()` method for custom terrain patterns

### Custom Border Sizes
Currently: 1 block wide  
To change: Edit border loop in `PlotGenerator.generateBorder()`

👉 See [BUILD_GUIDE.md](BUILD_GUIDE.md) for code modification guide

---

## 📞 Need Help?

- **Config won't load:** Check [YAML syntax](https://yaml.org/)
- **Plugin won't start:** Check `logs/latest.log`
- **Unknown materials:** Check Minecraft wiki for version
- **Height issues:** Use `/data get block ~ ~ ~` to find Y

**Questions?** Open an issue: [GitHub Issues](../../../issues)

---

**Configuration Applied:** After save + server restart  
**Version:** Config v3.2.0 (matches plugin v3.2.0)  
**Last Updated:** April 23, 2026
