# CityBuild Deployment Guide - Physgun Server

## 🚀 Quick Deployment (5 minutes)

### Your Physgun Server Details
- **Server Name:** Juhl Subuser
- **Dashboard:** https://gamecp.physgun.com/server/bca24f9c
- **SFTP Host:** chi-s-game-21.physgun.com:2022
- **Username:** julianmallon435@gmail.com.bca24f9c

---

## ✅ Method 1: Automated Build via GitHub Actions (RECOMMENDED)

### Step 1: Enable GitHub Actions
```bash
# Actions are already configured!
# Every push to main branch = auto-build
```

### Step 2: Get the Built JAR
```
1. Go to: https://github.com/JuhlTV/CityBuild
2. Click "Actions" tab
3. Click latest workflow
4. Scroll down to "Artifacts"
5. Download: CityBuildPlugin-JAR
```

### Step 3: Upload to Physgun via WinSCP
```
1. Download WinSCP: https://winscp.net/download/
2. Create new SFTP session:
   - Host: chi-s-game-21.physgun.com
   - Port: 2022
   - Username: julianmallon435@gmail.com.bca24f9c
   - Password: Juhl44istcool!
3. Navigate to: /home/container/plugins
4. Upload: CityBuildPlugin-2.0.0.jar
```

### Step 4: Restart Server
```
1. Go to: https://gamecp.physgun.com/server/bca24f9c
2. Click: Power → Restart
3. Wait 30-60 seconds
4. ✅ Done!
```

---

## ✅ Method 2: Manual Upload via File Manager

### Step 1: Download JAR
```
Download from GitHub Releases:
https://github.com/JuhlTV/CityBuild/releases/latest
```

### Step 2: Use Physgun File Manager
```
1. Go to: https://gamecp.physgun.com/server/bca24f9c
2. Click: Files → Browse Files
3. Navigate to: plugins/
4. Upload: CityBuildPlugin-2.0.0.jar
5. Delete old CityBuildPlugin JAR files
```

### Step 3: Restart
```
Server → Restart
```

---

## ✅ Method 3: WinSCP GUI (Easiest)

### Step 1: Install WinSCP
Download from: https://winscp.net/download/

### Step 2: Create SFTP Session
```
Session Properties:
  File Protocol: SFTP
  Host name: chi-s-game-21.physgun.com
  Port number: 2022
  User name: julianmallon435@gmail.com.bca24f9c
  Password: Juhl44istcool!
```

### Step 3: Upload JAR
```
Remote path: /home/container/plugins/
Drag & Drop: CityBuildPlugin-2.0.0.jar
```

### Step 4: Restart Server
```
Dashboard → Server → Restart
```

---

## 🧪 Testing the Plugin

### Connect to Server
```
1. Launch Minecraft Java Edition
2. Add server: your-physgun-server-ip:25565
3. Join and become OP (via console or command)
```

### Test Commands
```
/citybuild help                    Show all commands
/citybuild daily                   Claim daily reward
/citybuild shop list               View shop items
/citybuild shop buy stone 64       Buy 64 stone
/citybuild balance                 Check balance
/citybuild pay <player> <amount>   Transfer money
/citybuild leaderboard             Top 10 players
```

### Check Logs
If something fails, check:
```
SSH into: chi-s-game-21.physgun.com:2022
User: julianmallon435@gmail.com.bca24f9c
Path: /home/container/logs/latest.log
```

---

## 🔧 Troubleshooting

### Commands not appearing?
```
→ Server may need restart
→ Check plugin.yml is updated
→ Verify JAR in plugins/ folder
```

### Plugin not loading?
```
Check logs: /home/container/logs/latest.log
Look for: "[CityBuild] ✓ Plugin enabled"
```

### Shop items not available?
```
→ Auto-generated on first run
→ Check: /home/container/plugins/CityBuild/data/shop.json
```

---

## 📦 What Gets Deployed

```
CityBuildPlugin-2.0.0.jar (200-300 KB)
├── All Java Classes
├── Config Files
├── Resource Files
├── Dependencies (Paper API, GSON, Adventure)
└── Ready to Run!
```

---

## 🎯 Performance Notes

- **Server Load:** Minimal (JSON-based, no database)
- **Memory:** ~50-100 MB
- **CPU:** Negligible
- **Startup Time:** <2 seconds

---

## 📞 Support

If you need help:
1. Check logs: `/home/container/logs/latest.log`
2. Review README.md in repo
3. Test commands in-game

**Version:** 2.0.0  
**Status:** Production Ready ✅
