#!/bin/bash
# CityBuild Manager - Linux/Mac Launcher

echo ""
echo "======================================"
echo "  CityBuild Server Manager"
echo "======================================"
echo ""

# Check if .env exists
if [ ! -f .env ]; then
    echo "Creating .env from template..."
    cp .env.example .env
    echo ""
    echo "Please edit .env with your server settings!"
    exit 1
fi

# Install dependencies
echo "Installing dependencies..."
pip3 install -r requirements.txt

# Run manager
echo ""
echo "Starting CityBuild Manager..."
echo ""
python3 citybuild_manager.py
