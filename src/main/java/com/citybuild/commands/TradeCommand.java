package com.citybuild.commands;

import com.citybuild.features.trading.Trade;
import com.citybuild.features.trading.TradingManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /trade command
 */
public class TradeCommand implements CommandExecutor {
    
    private final TradingManager tradingManager;
    
    public TradeCommand(TradingManager tradingManager) {
        this.tradingManager = tradingManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen!");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        
        return switch (subcommand) {
            case "start" -> startTrade(player, args);
            case "offer" -> offerCoins(player, args);
            case "accept" -> acceptTrade(player);
            case "cancel" -> cancelTrade(player);
            case "status" -> showTradeStatus(player);
            default -> {
                showHelp(player);
                yield true;
            }
        };
    }
    
    private boolean startTrade(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cNutzung: /trade start <Spielername>");
            return true;
        }
        
        Trade existing = tradingManager.getPlayerTrade(player.getUniqueId());
        if (existing != null) {
            player.sendMessage("§cDu hast bereits einen offenen Handel!");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cSpieler nicht gefunden!");
            return true;
        }
        
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§cDu kannst nicht mit dir selbst handeln!");
            return true;
        }
        
        tradingManager.createTrade(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(String.format("§a✓ Handelanfrage an §e%s§a gesendet!", target.getName()));
        target.sendMessage(String.format("§e%s§7 möchte mit dir handeln! (§e/trade status§7)", player.getName()));
        
        return true;
    }
    
    private boolean offerCoins(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cNutzung: /trade offer <Summe>");
            return true;
        }
        
        Trade trade = tradingManager.getPlayerTrade(player.getUniqueId());
        if (trade == null) {
            player.sendMessage("§cDu hast keinen offenen Handel!");
            return true;
        }
        
        try {
            double amount = Double.parseDouble(args[1]);
            if (tradingManager.offerCoins(player.getUniqueId(), trade, amount)) {
                player.sendMessage(String.format("§a✓ Du hast §e$%.0f§a angeboten!", amount));
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cUngültige Summe!");
        }
        
        return false;
    }
    
    private boolean acceptTrade(Player player) {
        Trade trade = tradingManager.getPlayerTrade(player.getUniqueId());
        if (trade == null) {
            player.sendMessage("§cDu hast keinen offenen Handel!");
            return true;
        }
        
        if (tradingManager.acceptTrade(player.getUniqueId(), trade)) {
            player.sendMessage("§a✓ Du hast dem Handel zugestimmt!");
            return true;
        }
        
        player.sendMessage("§cDer Handel kann nicht abgeschlossen werden!");
        return false;
    }
    
    private boolean cancelTrade(Player player) {
        Trade trade = tradingManager.getPlayerTrade(player.getUniqueId());
        if (trade == null) {
            player.sendMessage("§cDu hast keinen offenen Handel!");
            return true;
        }
        
        tradingManager.cancelTrade(trade.getTradeId());
        player.sendMessage("§a✓ Handel abgebrochen!");
        return true;
    }
    
    private boolean showTradeStatus(Player player) {
        Trade trade = tradingManager.getPlayerTrade(player.getUniqueId());
        if (trade == null) {
            player.sendMessage("§cDu hast keinen offenen Handel!");
            return true;
        }
        
        Player p1 = Bukkit.getPlayer(trade.getPlayer1UUID());
        Player p2 = Bukkit.getPlayer(trade.getPlayer2UUID());
        
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║ Handelsdetails");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage(String.format("§e%s§7: §a$%.0f", 
            p1 != null ? p1.getName() : "Player 1", trade.getPlayer1Coins()));
        player.sendMessage(String.format("§e%s§7: §a$%.0f", 
            p2 != null ? p2.getName() : "Player 2", trade.getPlayer2Coins()));
        player.sendMessage(String.format("§7Status: §e%s", trade.getStatus()));
        player.sendMessage("§6§l╚════════════════════════════════════╝");
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║ Handels Befehle");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("§e/trade start <Spieler>      §7- Handel starten");
        player.sendMessage("§e/trade offer <Summe>        §7- Münzen anbieten");
        player.sendMessage("§e/trade accept                §7- Handel akzeptieren");
        player.sendMessage("§e/trade status                §7- Handeldetails anzeigen");
        player.sendMessage("§e/trade cancel                §7- Handel abbrechen");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
}
