#!/usr/bin/env python3
"""
Quick start script for CityBuild Manager
"""

import os
import sys
import subprocess

def main():
    print("\n" + "="*50)
    print(" CityBuild Server Manager - Setup")
    print("="*50 + "\n")
    
    # Check Python version
    if sys.version_info < (3, 8):
        print("❌ Python 3.8+ required!")
        sys.exit(1)
    
    # Install requirements
    print("📦 Installing dependencies...")
    os.system('pip install -r requirements.txt')
    
    # Check .env
    if not os.path.exists('.env'):
        print("\n⚠️  .env not found!")
        print("Creating from .env.example...")
        os.system('copy .env.example .env' if os.name == 'nt' else 'cp .env.example .env')
        print("\n📝 Edit .env with your server settings!")
        print("   - RCON_HOST: Your server IP")
        print("   - RCON_PASSWORD: Your RCON password")
    
    # Run manager
    print("\n🚀 Starting CityBuild Manager...")
    os.system('python citybuild_manager.py')

if __name__ == '__main__':
    main()
