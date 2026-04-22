"""
CityBuild API Extensions
Additional features like webhooks, APIs, and integrations
"""

from citybuild_manager import CityBuildManager
from datetime import datetime
import json

class CityBuildAPI(CityBuildManager):
    """Extended API for CityBuild Manager"""
    
    def get_player_data(self, player_name: str) -> dict:
        """Get complete player data as JSON"""
        self._init_player(player_name)
        return self.players_data.get(player_name, {})
    
    def get_all_players(self) -> dict:
        """Get all players data"""
        return self.players_data
    
    def get_leaderboard(self, limit: int = 10) -> list:
        """Get richest players"""
        sorted_players = sorted(
            self.players_data.items(),
            key=lambda x: x[1]['balance'],
            reverse=True
        )
        return [(name, data['balance']) for name, data in sorted_players[:limit]]
    
    def get_plot_leaderboard(self, limit: int = 10) -> list:
        """Get players with most plots"""
        sorted_players = sorted(
            self.players_data.items(),
            key=lambda x: len(x[1]['plots']),
            reverse=True
        )
        return [(name, len(data['plots'])) for name, data in sorted_players[:limit]]
    
    def get_system_stats(self) -> dict:
        """Get system-wide statistics"""
        total_players = len(self.players_data)
        total_balance = sum(p['balance'] for p in self.players_data.values())
        total_plots = sum(len(p['plots']) for p in self.players_data.values())
        
        return {
            'total_players': total_players,
            'total_balance': total_balance,
            'total_plots': total_plots,
            'timestamp': datetime.now().isoformat()
        }
    
    def transfer_money(self, from_player: str, to_player: str, amount: int) -> bool:
        """Transfer money between players"""
        self._init_player(from_player)
        self._init_player(to_player)
        
        if self.get_balance(from_player) < amount:
            self._log(f"Transfer failed: {from_player} insufficient funds")
            return False
        
        self.remove_money(from_player, amount)
        self.add_money(to_player, amount)
        
        # Send messages
        self.send_command(f'tellraw {from_player} {{"text":"✓ Sent ${amount} to {to_player}","color":"green"}}')
        self.send_command(f'tellraw {to_player} {{"text":"✓ Received ${amount} from {from_player}","color":"green"}}')
        
        self._log(f"Transferred ${amount} from {from_player} to {to_player}")
        return True
    
    def apply_tax(self, percentage: float = 0.05):
        """Apply tax to all players (percentage of balance)"""
        tax_collected = 0
        
        for player_name, data in self.players_data.items():
            balance = data['balance']
            tax = int(balance * percentage)
            
            if tax > 0:
                self.remove_money(player_name, tax)
                tax_collected += tax
                self.send_command(f'tellraw {player_name} {{"text":"⚠ Tax: -${tax}","color":"yellow"}}')
        
        self.broadcast(f"Tax collected: ${tax_collected}", "gold")
        self._log(f"Tax collected: ${tax_collected}")
        return tax_collected
    
    def export_data(self, filename: str = None) -> str:
        """Export all data to JSON file"""
        if not filename:
            filename = f"export_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        
        with open(filename, 'w') as f:
            json.dump(self.players_data, f, indent=2)
        
        self._log(f"Data exported to {filename}")
        print(f"Data exported to {filename}")
        return filename
    
    def import_data(self, filename: str) -> bool:
        """Import player data from JSON file"""
        try:
            with open(filename, 'r') as f:
                self.players_data = json.load(f)
            
            self._save_data()
            self._log(f"Data imported from {filename}")
            return True
        except Exception as e:
            print(f"Import failed: {e}")
            return False


if __name__ == '__main__':
    api = CityBuildAPI()
    
    if api.connect_rcon():
        # Example: Get leaderboard
        print("\nTop 5 Richest Players:")
        for rank, (player, balance) in enumerate(api.get_leaderboard(5), 1):
            print(f"{rank}. {player}: ${balance}")
        
        # Example: Get system stats
        stats = api.get_system_stats()
        print(f"\nSystem Stats:")
        print(f"Players: {stats['total_players']}")
        print(f"Total Balance: ${stats['total_balance']}")
        print(f"Total Plots: {stats['total_plots']}")
