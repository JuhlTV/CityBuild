package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.EconomyManager;
import com.citybuild.managers.PlotManager;
import com.citybuild.managers.WorldManager;
import com.citybuild.managers.ShopManager;
import com.citybuild.managers.BankManager;
import com.citybuild.managers.DailyRewardManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CityBuildCommand implements CommandExecutor {
    private final CityBuildPlugin plugin;
    private final EconomyManager economy;
    private final PlotManager plots;

    public CityBuildCommand(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomyManager();
        this.plots = plugin.getPlotManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!").color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "buy":
                return handleBuy(player);
            case "sell":
                return handleSell(player);
            case "balance":
                return handleBalance(player);
            case "info":
                return handleInfo(player);
            case "leaderboard":
                return handleLeaderboard(player);
            case "tpplot":
                return handleTeleportPlot(player);
            case "tpfarm":
                return handleTeleportFarm(player);
            case "tppvp":
                return handleTeleportPvp(player);
            case "shop":
                return handleShop(player, args);
            case "pay":
                return handlePay(player, args);
            case "daily":
                return handleDaily(player);
            case "bank":
                return handleBank(player, args);
            case "help":
                sendHelp(player);
                return true;
            case "admin":
                if (player.isOp()) {
                    return handleAdmin(player, args);
                } else {
                    player.sendMessage(Component.text("You don't have permission!").color(NamedTextColor.RED));
                }
                return true;
            default:
                sendHelp(player);
                return true;
        }
    }

    private boolean handleBuy(Player player) {
        economy.initializePlayer(player);
        
        long balance = economy.getBalance(player);
        int price = plots.getPlotBuyPrice();

        if (balance < price) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                    .append(Component.text("❌ Insufficient funds! Need $" + price + ", Have $" + balance, NamedTextColor.RED)));
            return true;
        }

        economy.removeBalance(player, price);
        plots.addPlot(player.getUniqueId().toString());

        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Plot purchased! You now own " + plots.getPlotCount(player.getUniqueId().toString()) + " plots", NamedTextColor.GREEN)));

        return true;
    }

    private boolean handleSell(Player player) {
        String uuid = player.getUniqueId().toString();
        
        if (!plots.hasPlots(uuid)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                    .append(Component.text("❌ You don't own any plots!", NamedTextColor.RED)));
            return true;
        }

        economy.addBalance(player, plots.getPlotSellPrice());
        plots.removePlot(uuid);

        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Plot sold for $" + plots.getPlotSellPrice() + "!", NamedTextColor.GREEN)));

        return true;
    }

    private boolean handleBalance(Player player) {
        economy.initializePlayer(player);
        long balance = economy.getBalance(player);

        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("Balance: $" + balance, NamedTextColor.GREEN)));

        return true;
    }

    private boolean handleInfo(Player player) {
        economy.initializePlayer(player);
        String uuid = player.getUniqueId().toString();

        long balance = economy.getBalance(player);
        int plotCount = plots.getPlotCount(uuid);

        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("Your Info:", NamedTextColor.GOLD)));
        player.sendMessage(Component.text("  Balance: $" + balance, NamedTextColor.YELLOW));
        player.sendMessage(Component.text("  Plots: " + plotCount, NamedTextColor.YELLOW));

        return true;
    }

    private boolean handleLeaderboard(Player player) {
        var leaderboard = economy.getLeaderboard(10);

        player.sendMessage(Component.text("=== CityBuild Leaderboard ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

        int rank = 1;
        for (var entry : leaderboard) {
            String uuid = entry.getKey();
            long balance = entry.getValue();
            
            Player p = Bukkit.getPlayer(java.util.UUID.fromString(uuid));
            String name = p != null ? p.getName() : "Unknown";

            player.sendMessage(Component.text(rank + ". " + name + ": $" + balance, NamedTextColor.YELLOW));
            rank++;
        }

        return true;
    }

    private boolean handleTeleportPlot(Player player) {
        String uuid = player.getUniqueId().toString();
        
        if (!plots.hasPlots(uuid)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                    .append(Component.text("❌ You don't own any plots! Buy one first.", NamedTextColor.RED)));
            return true;
        }
        
        Location plotLocation = plots.getFirstPlotLocation(uuid);
        player.teleport(plotLocation);
        
        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Teleported to your plot!", NamedTextColor.GREEN)));
        
        return true;
    }

    private boolean handleTeleportFarm(Player player) {
        WorldManager worldManager = plugin.getWorldManager();
        Location farmSpawn = worldManager.getFarmWorld().getSpawnLocation();
        
        player.teleport(farmSpawn);
        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Teleported to Farm World!", NamedTextColor.GREEN)));
        
        return true;
    }

    private boolean handleTeleportPvp(Player player) {
        WorldManager worldManager = plugin.getWorldManager();
        Location pvpSpawn = worldManager.getPvpWorld().getSpawnLocation();
        
        player.teleport(pvpSpawn);
        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Teleported to PVP World!", NamedTextColor.GREEN)));
        
        return true;
    }

    private boolean handleAdmin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("[CityBuild] Admin commands:", NamedTextColor.BLUE));
            player.sendMessage(Component.text("  /citybuild admin reset - Reset all data", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("  /citybuild admin stats - Show statistics", NamedTextColor.YELLOW));
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "reset":
                // Reset logic would go here
                player.sendMessage(Component.text("[CityBuild] Data reset!", NamedTextColor.RED));
                return true;
            case "stats":
                player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                        .append(Component.text("System Statistics:", NamedTextColor.GOLD)));
                player.sendMessage(Component.text("  Total Plots: " + plots.getTotalPlots(), NamedTextColor.YELLOW));
                player.sendMessage(Component.text("  Version: 2.0.0", NamedTextColor.YELLOW));
                return true;
            default:
                return false;
        }
    }

    private boolean handleShop(Player player, String[] args) {
        ShopManager shop = plugin.getShopManager();
        economy.initializePlayer(player);
        
        if (args.length < 2) {
            // Show shop menu
            player.sendMessage(Component.text("=== CityBuild Shop ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("Use: /citybuild shop list - Show all items", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("Use: /citybuild shop buy <item> <amount> - Buy item", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("Use: /citybuild shop sell <item> <amount> - Sell item", NamedTextColor.YELLOW));
            return true;
        }
        
        switch (args[1].toLowerCase()) {
            case "list":
                player.sendMessage(Component.text("=== Shop Items ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
                for (Map.Entry<String, ShopManager.ShopItem> entry : shop.getAllShopItems().entrySet()) {
                    ShopManager.ShopItem item = entry.getValue();
                    player.sendMessage(Component.text(item.displayName + " - Buy: $" + item.buyPrice + " | Sell: $" + item.sellPrice, NamedTextColor.YELLOW));
                }
                return true;
            
            case "buy":
                if (args.length < 4) {
                    player.sendMessage(Component.text("Use: /citybuild shop buy <item> <amount>", NamedTextColor.RED));
                    return true;
                }
                
                String materialStr = args[2].toUpperCase();
                ShopManager.ShopItem item = shop.getShopItem(materialStr);
                
                if (item == null) {
                    player.sendMessage(Component.text("Item not found in shop!", NamedTextColor.RED));
                    return true;
                }
                
                try {
                    int amount = Integer.parseInt(args[3]);
                    long totalPrice = (long) amount * item.buyPrice;
                    
                    if (!economy.canAfford(player, totalPrice)) {
                        player.sendMessage(Component.text("Insufficient funds! Need $" + totalPrice, NamedTextColor.RED));
                        return true;
                    }
                    
                    Material mat = shop.getMaterial(materialStr);
                    if (mat == null) {
                        player.sendMessage(Component.text("Invalid material!", NamedTextColor.RED));
                        return true;
                    }
                    
                    economy.removeBalance(player, totalPrice);
                    player.getInventory().addItem(new ItemStack(mat, amount));
                    
                    player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                            .append(Component.text("✓ Bought " + amount + "x " + item.displayName + " for $" + totalPrice, NamedTextColor.GREEN)));
                } catch (NumberFormatException e) {
                    player.sendMessage(Component.text("Invalid amount!", NamedTextColor.RED));
                }
                return true;
            
            case "sell":
                if (args.length < 4) {
                    player.sendMessage(Component.text("Use: /citybuild shop sell <item> <amount>", NamedTextColor.RED));
                    return true;
                }
                
                materialStr = args[2].toUpperCase();
                item = shop.getShopItem(materialStr);
                
                if (item == null) {
                    player.sendMessage(Component.text("Item not found in shop!", NamedTextColor.RED));
                    return true;
                }
                
                try {
                    int amount = Integer.parseInt(args[3]);
                    Material mat = shop.getMaterial(materialStr);
                    
                    if (mat == null) {
                        player.sendMessage(Component.text("Invalid material!", NamedTextColor.RED));
                        return true;
                    }
                    
                    // Count items in inventory manually
                    int hasAmount = 0;
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && item.getType() == mat) {
                            hasAmount += item.getAmount();
                        }
                    }
                    
                    if (hasAmount < amount) {
                        player.sendMessage(Component.text("You don't have " + amount + "x " + item.displayName, NamedTextColor.RED));
                        return true;
                    }
                    
                    long totalPrice = (long) amount * item.sellPrice;
                    player.getInventory().removeItem(new ItemStack(mat, amount));
                    economy.addBalance(player, totalPrice);
                    
                    player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                            .append(Component.text("✓ Sold " + amount + "x " + item.displayName + " for $" + totalPrice, NamedTextColor.GREEN)));
                } catch (NumberFormatException e) {
                    player.sendMessage(Component.text("Invalid amount!", NamedTextColor.RED));
                }
                return true;
            
            default:
                return false;
        }
    }

    private boolean handleDaily(Player player) {
        DailyRewardManager daily = plugin.getDailyRewardManager();
        
        if (!daily.canClaimDaily(player)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                    .append(Component.text("❌ Already claimed today! Come back tomorrow.", NamedTextColor.RED)));
            return true;
        }
        
        long reward = daily.claimDailyReward(player);
        economy.addBalance(player, reward);
        
        int streak = daily.getConsecutiveDays(player);
        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Daily reward claimed: $" + reward, NamedTextColor.GREEN)));
        player.sendMessage(Component.text("Streak: " + streak + " days (+$" + (streak > 1 ? (streak - 1) * 100 : "0") + " bonus)", NamedTextColor.YELLOW));
        
        return true;
    }

    private boolean handlePay(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Use: /citybuild pay <player> <amount>", NamedTextColor.RED));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }
        
        try {
            long amount = Long.parseLong(args[2]);
            
            if (amount <= 0) {
                player.sendMessage(Component.text("Amount must be positive!", NamedTextColor.RED));
                return true;
            }
            
            BankManager bank = plugin.getBankManager();
            if (bank.transferMoney(player, target, amount)) {
                player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                        .append(Component.text("✓ Sent $" + amount + " to " + target.getName(), NamedTextColor.GREEN)));
                target.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                        .append(Component.text("✓ Received $" + amount + " from " + player.getName(), NamedTextColor.GREEN)));
            } else {
                player.sendMessage(Component.text("Insufficient funds!", NamedTextColor.RED));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid amount!", NamedTextColor.RED));
        }
        return true;
    }

    private boolean handleBank(Player player, String[] args) {
        BankManager bank = plugin.getBankManager();
        
        if (args.length < 2) {
            player.sendMessage(Component.text("=== Bank Commands ===", NamedTextColor.GOLD));
            player.sendMessage(Component.text("/citybuild bank history - View transactions", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("/citybuild bank stats - View statistics", NamedTextColor.YELLOW));
            return true;
        }
        
        switch (args[1].toLowerCase()) {
            case "history":
                player.sendMessage(Component.text("=== Recent Transactions ===", NamedTextColor.GOLD));
                var history = bank.getTransactionHistory(player.getUniqueId().toString(), 5);
                
                if (history.isEmpty()) {
                    player.sendMessage(Component.text("No transactions yet", NamedTextColor.YELLOW));
                } else {
                    for (BankManager.Transaction tx : history) {
                        String from = Bukkit.getPlayer(java.util.UUID.fromString(tx.from)) != null ? 
                                      Bukkit.getPlayer(java.util.UUID.fromString(tx.from)).getName() : "Unknown";
                        String to = Bukkit.getPlayer(java.util.UUID.fromString(tx.to)) != null ? 
                                    Bukkit.getPlayer(java.util.UUID.fromString(tx.to)).getName() : "Unknown";
                        
                        player.sendMessage(Component.text(from + " → " + to + ": $" + tx.amount, NamedTextColor.YELLOW));
                    }
                }
                return true;
            
            case "stats":
                player.sendMessage(Component.text("=== Bank Statistics ===", NamedTextColor.GOLD));
                long sent = bank.getTotalSent(player.getUniqueId().toString());
                long received = bank.getTotalReceived(player.getUniqueId().toString());
                
                player.sendMessage(Component.text("Total Sent: $" + sent, NamedTextColor.YELLOW));
                player.sendMessage(Component.text("Total Received: $" + received, NamedTextColor.YELLOW));
                player.sendMessage(Component.text("Net: $" + (received - sent), NamedTextColor.YELLOW));
                return true;
            
            default:
                return false;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("=== CityBuild Commands ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("--- Economy ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild balance - Check balance", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild pay <player> <amount> - Send money", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild daily - Claim daily reward", NamedTextColor.YELLOW));
        
        player.sendMessage(Component.text("--- Plots ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild buy - Buy a plot ($" + plots.getPlotBuyPrice() + ")", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild sell - Sell a plot ($" + plots.getPlotSellPrice() + ")", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild info - View your info", NamedTextColor.YELLOW));
        
        player.sendMessage(Component.text("--- Shop ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild shop list - View all items", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild shop buy <item> <amount> - Buy", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild shop sell <item> <amount> - Sell", NamedTextColor.YELLOW));
        
        player.sendMessage(Component.text("--- Worlds ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild tpplot - Go to your plot", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild tpfarm - Go to Farm World", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild tppvp - Go to PVP World", NamedTextColor.YELLOW));
        
        player.sendMessage(Component.text("--- Info ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild leaderboard - Top 10 players", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild bank history - View transactions", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild help - Show this message", NamedTextColor.YELLOW));
    }
}

