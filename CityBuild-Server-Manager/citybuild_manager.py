"""
CityBuild Server Manager - Vanilla Minecraft Server Manager
Manages plots, economy, and admin commands via RCON and local database
"""

import json
import os
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional
from colorama import Fore, Back, Style, init
from mcrcon import MCRcon
from dotenv import load_dotenv

init(autoreset=True)

class CityBuildManager:
    def __init__(self):
        load_dotenv()
        
        self.rcon_host = os.getenv('RCON_HOST', 'localhost')
        self.rcon_port = int(os.getenv('RCON_PORT', 25575))
        self.rcon_pass = os.getenv('RCON_PASSWORD', 'password')
        
        self.starting_balance = int(os.getenv('STARTING_BALANCE', 10000))
        self.plot_buy_price = int(os.getenv('PLOT_BUY_PRICE', 5000))
        self.plot_sell_price = int(os.getenv('PLOT_SELL_PRICE', 4000))
        
        self.db_file = os.getenv('DB_FILE', 'data/citybuild.json')
        
        # Create data directory
        Path('data').mkdir(exist_ok=True)
        Path('logs').mkdir(exist_ok=True)
        
        self.rcon = None
        self.players_data = self._load_data()
    
    def _load_data(self) -> Dict:
        """Load player data from JSON database"""
        if os.path.exists(self.db_file):
            try:
                with open(self.db_file, 'r') as f:
                    return json.load(f)
            except Exception as e:
                print(f"{Fore.RED}Error loading database: {e}")
                return {}
        return {}
    
    def _save_data(self):
        """Save player data to JSON database"""
        try:
            with open(self.db_file, 'w') as f:
                json.dump(self.players_data, f, indent=2)
            self._log(f"Database saved: {len(self.players_data)} players")
        except Exception as e:
            print(f"{Fore.RED}Error saving database: {e}")
    
    def connect_rcon(self) -> bool:
        """Connect to Minecraft server via RCON"""
        try:
            self.rcon = MCRcon(self.rcon_host, self.rcon_pass, port=self.rcon_port)
            self.rcon.connect()
            print(f"{Fore.GREEN}✓ Connected to server: {self.rcon_host}:{self.rcon_port}")
            self._log("RCON connection established")
            return True
        except Exception as e:
            print(f"{Fore.RED}✗ Failed to connect RCON: {e}")
            self._log(f"RCON connection failed: {e}")
            return False
    
    def send_command(self, command: str) -> str:
        """Send command to Minecraft server"""
        try:
            if not self.rcon:
                print(f"{Fore.RED}RCON not connected!")
                return ""
            response = self.rcon.command(command)
            return response
        except Exception as e:
            print(f"{Fore.RED}Command failed: {e}")
            return ""
    
    def broadcast(self, message: str, color: str = "white"):
        """Broadcast message to all players"""
        command = f'tellraw @a {{"text":"[CityBuild] {message}","color":"{color}"}}'
        self.send_command(command)
    
    def _init_player(self, player_name: str):
        """Initialize new player data"""
        if player_name not in self.players_data:
            self.players_data[player_name] = {
                'uuid': None,
                'balance': self.starting_balance,
                'plots': [],
                'joined': datetime.now().isoformat(),
                'last_login': datetime.now().isoformat()
            }
            self._save_data()
            self._log(f"New player initialized: {player_name}")
    
    def get_balance(self, player_name: str) -> int:
        """Get player balance"""
        self._init_player(player_name)
        return self.players_data[player_name]['balance']
    
    def set_balance(self, player_name: str, amount: int):
        """Set player balance"""
        self._init_player(player_name)
        self.players_data[player_name]['balance'] = amount
        self._save_data()
        
        # Sync with server scoreboard
        self.send_command(f'scoreboard players set {player_name} cb_balance {amount}')
    
    def add_money(self, player_name: str, amount: int) -> bool:
        """Add money to player"""
        self._init_player(player_name)
        self.players_data[player_name]['balance'] += amount
        self._save_data()
        self.send_command(f'scoreboard players add {player_name} cb_balance {amount}')
        self._log(f"{player_name} gained ${amount}")
        return True
    
    def remove_money(self, player_name: str, amount: int) -> bool:
        """Remove money from player"""
        self._init_player(player_name)
        balance = self.players_data[player_name]['balance']
        
        if balance < amount:
            self._log(f"{player_name} insufficient funds")
            return False
        
        self.players_data[player_name]['balance'] -= amount
        self._save_data()
        self.send_command(f'scoreboard players remove {player_name} cb_balance {amount}')
        self._log(f"{player_name} spent ${amount}")
        return True
    
    def buy_plot(self, player_name: str, plot_id: str) -> bool:
        """Buy a plot"""
        self._init_player(player_name)
        
        # Check balance
        if self.get_balance(player_name) < self.plot_buy_price:
            msg = f"❌ Insufficient funds! Need ${self.plot_buy_price}, have ${self.get_balance(player_name)}"
            self.send_command(f'tellraw {player_name} {{"text":"[CityBuild] {msg}","color":"red"}}')
            self._log(f"{player_name} failed to buy plot - insufficient funds")
            return False
        
        # Deduct money
        if not self.remove_money(player_name, self.plot_buy_price):
            return False
        
        # Add plot
        if plot_id not in self.players_data[player_name]['plots']:
            self.players_data[player_name]['plots'].append(plot_id)
        
        self._save_data()
        
        # Update scoreboard
        plot_count = len(self.players_data[player_name]['plots'])
        self.send_command(f'scoreboard players set {player_name} cb_plots {plot_count}')
        
        # Feedback
        msg = f"✓ Plot purchased! Plot #{plot_count}"
        self.send_command(f'tellraw {player_name} {{"text":"[CityBuild] {msg}","color":"green"}}')
        self._log(f"{player_name} bought plot {plot_id}")
        
        return True
    
    def sell_plot(self, player_name: str, plot_index: int = 0) -> bool:
        """Sell a plot"""
        self._init_player(player_name)
        
        plots = self.players_data[player_name]['plots']
        
        if not plots or plot_index >= len(plots):
            msg = "❌ You don't own any plots!"
            self.send_command(f'tellraw {player_name} {{"text":"[CityBuild] {msg}","color":"red"}}')
            self._log(f"{player_name} failed to sell plot - no plots owned")
            return False
        
        # Remove plot
        plot_id = plots.pop(plot_index)
        
        # Add money
        self.add_money(player_name, self.plot_sell_price)
        
        self._save_data()
        
        # Update scoreboard
        plot_count = len(plots)
        self.send_command(f'scoreboard players set {player_name} cb_plots {plot_count}')
        
        # Feedback
        msg = f"✓ Plot sold for ${self.plot_sell_price}!"
        self.send_command(f'tellraw {player_name} {{"text":"[CityBuild] {msg}","color":"green"}}')
        self._log(f"{player_name} sold plot {plot_id}")
        
        return True
    
    def reset_all(self):
        """Reset all player data"""
        self.players_data = {}
        self._save_data()
        self.send_command('scoreboard players reset @a cb_balance')
        self.send_command('scoreboard players reset @a cb_plots')
        self.broadcast("All data has been reset!", "red")
        self._log("All data reset")
    
    def show_stats(self, player_name: str = None):
        """Show player or global statistics"""
        if player_name:
            self._init_player(player_name)
            data = self.players_data[player_name]
            balance = data['balance']
            plots = len(data['plots'])
            msg = f"Balance: ${balance} | Plots: {plots}"
            self.send_command(f'tellraw {player_name} {{"text":"[CityBuild] {msg}","color":"gold"}}')
        else:
            # Global stats
            total_players = len(self.players_data)
            total_balance = sum(p['balance'] for p in self.players_data.values())
            total_plots = sum(len(p['plots']) for p in self.players_data.values())
            
            print(f"\n{Fore.CYAN}=== CityBuild System Statistics ===")
            print(f"Total Players: {total_players}")
            print(f"Total Balance: ${total_balance}")
            print(f"Total Plots: {total_plots}")
            print(f"Database File: {self.db_file}")
    
    def list_players(self):
        """List all players with their data"""
        if not self.players_data:
            print(f"{Fore.YELLOW}No player data found")
            return
        
        print(f"\n{Fore.CYAN}=== Registered Players ===")
        for name, data in self.players_data.items():
            print(f"{Fore.WHITE}{name}: ${data['balance']} | {len(data['plots'])} plots")
    
    def _log(self, message: str):
        """Log message to file"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        log_message = f"[{timestamp}] {message}\n"
        
        with open('logs/citybuild.log', 'a') as f:
            f.write(log_message)
    
    def shutdown(self):
        """Graceful shutdown"""
        if self.rcon:
            self.rcon.close()
        self._save_data()
        print(f"{Fore.GREEN}✓ CityBuild Manager shutdown")


def main():
    """Main interface"""
    print(f"\n{Back.CYAN}{Fore.BLACK} CityBuild Server Manager v1.0 {Style.RESET_ALL}\n")
    
    manager = CityBuildManager()
    
    if not manager.connect_rcon():
        print(f"{Fore.YELLOW}Warning: RCON not available. Operating in offline mode.")
    
    while True:
        print(f"\n{Fore.CYAN}Commands: balance, buy, sell, reset, stats, list, exit")
        cmd = input(f"{Fore.GREEN}> {Style.RESET_ALL}").strip().lower()
        
        if cmd == 'exit':
            manager.shutdown()
            break
        
        elif cmd == 'balance':
            player = input("Player name: ")
            balance = manager.get_balance(player)
            print(f"{Fore.GREEN}${balance}")
        
        elif cmd == 'buy':
            player = input("Player name: ")
            manager.buy_plot(player, f"plot_{len(manager.players_data[player]['plots'])}")
        
        elif cmd == 'sell':
            player = input("Player name: ")
            manager.sell_plot(player)
        
        elif cmd == 'reset':
            confirm = input(f"{Fore.RED}Reset ALL data? (yes/no): {Style.RESET_ALL}")
            if confirm.lower() == 'yes':
                manager.reset_all()
        
        elif cmd == 'stats':
            manager.show_stats()
        
        elif cmd == 'list':
            manager.list_players()
        
        else:
            print(f"{Fore.YELLOW}Unknown command")


if __name__ == '__main__':
    main()
