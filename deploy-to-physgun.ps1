# CityBuild Plugin Deployment Script for Physgun
# Author: CityBuild Team
# Purpose: Deploy plugin JAR to Physgun Server via SFTP

param(
    [string]$JarPath = "C:\Users\julia\OneDrive\Dokumente\Minecraft\CityBuild-Plugin\target\CityBuildPlugin-2.0.0.jar"
)

# Physgun Credentials
$SFTPHost = "chi-s-game-21.physgun.com"
$SFTPPort = 2022
$SFTPUser = "julianmallon435@gmail.com.bca24f9c"
$SFTPPass = "Juhl44istcool!"
$RemotePluginsPath = "/home/container/plugins"

Write-Host "======================================"
Write-Host "CityBuild v2.0.0 Deployment Script"
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Check if JAR exists
if (-not (Test-Path $JarPath)) {
    Write-Host "ERROR: JAR file not found at $JarPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "You need to build the JAR first!"
    Write-Host "Options:"
    Write-Host "1. Download pre-built JAR from GitHub: https://github.com/JuhlTV/CityBuild"
    Write-Host "2. Install Maven locally: choco install maven"
    Write-Host "3. Use GitHub Actions to auto-build"
    exit 1
}

Write-Host "JAR File: $JarPath" -ForegroundColor Green
Write-Host "JAR Size: $((Get-Item $JarPath).Length / 1MB) MB"
Write-Host ""

# SFTP Connection Info
Write-Host "SFTP Details:" -ForegroundColor Yellow
Write-Host "  Host: $SFTPHost"
Write-Host "  Port: $SFTPPort"
Write-Host "  User: $SFTPUser"
Write-Host "  Remote Path: $RemotePluginsPath"
Write-Host ""

# Instructions for WinSCP or PuTTY
Write-Host "======================================"
Write-Host "DEPLOYMENT METHOD" -ForegroundColor Cyan
Write-Host "======================================"
Write-Host ""
Write-Host "Option 1: Manual Upload via WinSCP (GUI)"
Write-Host "  1. Download WinSCP: https://winscp.net"
Write-Host "  2. Create new SFTP session:"
Write-Host "     - Host: $SFTPHost"
Write-Host "     - Port: $SFTPPort"
Write-Host "     - Username: $SFTPUser"
Write-Host "     - Password: (your password)"
Write-Host "  3. Navigate to: $RemotePluginsPath"
Write-Host "  4. Upload: CityBuildPlugin-2.0.0.jar"
Write-Host "  5. Delete old CityBuildPlugin JAR files"
Write-Host "  6. Restart server via Physgun Dashboard"
Write-Host ""

Write-Host "Option 2: Command Line (Requires OpenSSH/PuTTY)"
Write-Host "  Run: plink -ssh -P 2022 -l $SFTPUser -pw '****' $SFTPHost"
Write-Host ""

Write-Host "Option 3: Direct Download from GitHub"
Write-Host "  1. Go to: https://github.com/JuhlTV/CityBuild"
Write-Host "  2. Download latest JAR release"
Write-Host "  3. Upload via Physgun File Manager"
Write-Host ""

Write-Host "======================================"
Write-Host "NEXT STEPS" -ForegroundColor Cyan
Write-Host "======================================"
Write-Host "1. Use WinSCP to upload the JAR"
Write-Host "2. Restart server via Physgun Dashboard"
Write-Host "3. Test commands in-game:"
Write-Host "   /citybuild help"
Write-Host "   /citybuild daily"
Write-Host "   /citybuild shop list"
Write-Host ""
Write-Host "For issues, check:"
Write-Host "  /home/container/logs/latest.log"
Write-Host ""
