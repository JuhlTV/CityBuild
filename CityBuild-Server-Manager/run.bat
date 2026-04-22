@echo off
REM CityBuild Manager - Windows Launcher

echo.
echo ======================================
echo  CityBuild Server Manager
echo ======================================
echo.

REM Check if .env exists
if not exist .env (
    echo Creating .env from template...
    copy .env.example .env
    echo.
    echo Please edit .env with your server settings!
    pause
)

REM Install dependencies
echo Installing dependencies...
pip install -r requirements.txt

REM Run manager
echo.
echo Starting CityBuild Manager...
echo.
python citybuild_manager.py

pause
