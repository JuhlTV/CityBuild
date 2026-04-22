package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.*;
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
    private final java.util.Map<String, Long> teleportCooldowns = new java.util.HashMap<>();
    private final long TELEPORT_COOLDOWN_MS = 3000; // 3 seconds

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
            // Open main GUI menu
            plugin.getGUIManager().openMainMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "menu":
                plugin.getGUIManager().openMainMenu(player);
                return true;
            case "achievements":
                plugin.getGUIManager().openAchievementsMenu(player);
                return true;
            case "clan":
                return handleClan(player, args);
            case "warp":
                return handleWarp(player, args);
            case "setwarp":
                return handleSetWarp(player, args);
            case "delwarp":
                return handleDelWarp(player, args);
            case "warps":
                plugin.getGUIManager().openWarpsMenu(player);
                return true;
            case "quest":
            case "quests":
                plugin.getGUIManager().openQuestsMenu(player);
                return true;
            case "enchant":
                plugin.getGUIManager().openEnchantingMenu(player);
                return true;
            case "trade":
                plugin.getGUIManager().openTradingMenu(player);
                return true;
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
            case "addmember":
                return handleAddMember(player, args);
            case "removemember":
                return handleRemoveMember(player, args);
            case "members":
                return handleMembers(player);
            case "help":
                sendHelp(player);
                return true;
            case "admin":
                return handleAdmin(player, args);
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
        
        // Check cooldown
        if (isTeleportOnCooldown(player)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("⏱️ Wait before teleporting again!", NamedTextColor.YELLOW)));
            return true;
        }

        // Create/update plot frame before teleporting
        plots.createPlotFrame(uuid);
        
        Location plotLocation = plots.getFirstPlotLocation(uuid);
        player.teleport(plotLocation);
        
        // Set cooldown
        setTeleportCooldown(player);
        
        // Show title message
        player.showTitle(net.kyori.adventure.title.Title.title(
            Component.text("📍 Plot World", NamedTextColor.AQUA, net.kyori.adventure.text.format.TextDecoration.BOLD),
            Component.text("You've been teleported to your plot", NamedTextColor.GRAY)
        ));
        
        // Grant 5-second invincibility after teleport
        grantInvincibility(player, 5);
        
        return true;
    }

    private boolean handleTeleportFarm(Player player) {
        // Check cooldown
        if (isTeleportOnCooldown(player)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("⏱️ Wait before teleporting again!", NamedTextColor.YELLOW)));
            return true;
        }

        WorldManager worldManager = plugin.getWorldManager();
        Location farmSpawn = worldManager.getFarmWorld().getSpawnLocation();
        
        player.teleport(farmSpawn);
        
        // Set cooldown
        setTeleportCooldown(player);
        
        // Show title message
        player.showTitle(net.kyori.adventure.title.Title.title(
            Component.text("🌾 Farm World", NamedTextColor.GREEN, net.kyori.adventure.text.format.TextDecoration.BOLD),
            Component.text("Mine blocks and earn money!", NamedTextColor.GRAY)
        ));
        
        // Grant 5-second invincibility
        grantInvincibility(player, 5);
        
        return true;
    }

    private boolean handleTeleportPvp(Player player) {
        // Check cooldown
        if (isTeleportOnCooldown(player)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("⏱️ Wait before teleporting again!", NamedTextColor.YELLOW)));
            return true;
        }

        WorldManager worldManager = plugin.getWorldManager();
        Location pvpSpawn = worldManager.getPvpWorld().getSpawnLocation();
        
        player.teleport(pvpSpawn);
        
        // Set cooldown
        setTeleportCooldown(player);
        
        // Show title message
        player.showTitle(net.kyori.adventure.title.Title.title(
            Component.text("⚔️ PVP World", NamedTextColor.RED, net.kyori.adventure.text.format.TextDecoration.BOLD),
            Component.text("Kill monsters and earn money!", NamedTextColor.GRAY)
        ));
        
        // Grant 5-second invincibility
        grantInvincibility(player, 5);
        
        return true;
    }

    /**
     * Check if player is on teleport cooldown
     */
    private boolean isTeleportOnCooldown(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!teleportCooldowns.containsKey(uuid)) {
            return false;
        }
        long lastTeleport = teleportCooldowns.get(uuid);
        return System.currentTimeMillis() - lastTeleport < TELEPORT_COOLDOWN_MS;
    }

    /**
     * Set teleport cooldown for player
     */
    private void setTeleportCooldown(Player player) {
        teleportCooldowns.put(player.getUniqueId().toString(), System.currentTimeMillis());
    }

    /**
     * Grant temporary invincibility (fall damage protection)
     */
    private void grantInvincibility(Player player, int seconds) {
        player.setInvulnerable(true);
        org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.isOnline()) {
                player.setInvulnerable(false);
            }
        }, seconds * 20L); // Convert seconds to ticks

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
                    for (ItemStack stack : player.getInventory().getContents()) {
                        if (stack != null && stack.getType() == mat) {
                            hasAmount += stack.getAmount();
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

    private boolean handleAddMember(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /citybuild addmember <player>", NamedTextColor.RED));
            return true;
        }

        String uuid = player.getUniqueId().toString();
        if (!plots.hasPlots(uuid)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("You don't own any plots!", NamedTextColor.RED)));
            return true;
        }

        org.bukkit.OfflinePlayer target = org.bukkit.Bukkit.getOfflinePlayer(args[1]);
        if (target == null) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("Player not found!", NamedTextColor.RED)));
            return true;
        }

        int plotId = plots.getPlayerPlots(uuid).get(0);
        String targetUuid = target.getUniqueId().toString();

        if (plots.isPlotMember(targetUuid, plotId)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("This player is already a member!", NamedTextColor.YELLOW)));
            return true;
        }

        plots.addMember(plotId, targetUuid);
        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
            .append(Component.text("✓ Added " + target.getName() + " to your plot!", NamedTextColor.GREEN)));
        return true;
    }

    private boolean handleRemoveMember(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /citybuild removemember <player>", NamedTextColor.RED));
            return true;
        }

        String uuid = player.getUniqueId().toString();
        if (!plots.hasPlots(uuid)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("You don't own any plots!", NamedTextColor.RED)));
            return true;
        }

        org.bukkit.OfflinePlayer target = org.bukkit.Bukkit.getOfflinePlayer(args[1]);
        if (target == null) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("Player not found!", NamedTextColor.RED)));
            return true;
        }

        int plotId = plots.getPlayerPlots(uuid).get(0);
        String targetUuid = target.getUniqueId().toString();

        plots.removeMember(plotId, targetUuid);
        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
            .append(Component.text("✓ Removed " + target.getName() + " from your plot!", NamedTextColor.GREEN)));
        return true;
    }

    private boolean handleMembers(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!plots.hasPlots(uuid)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("You don't own any plots!", NamedTextColor.RED)));
            return true;
        }

        int plotId = plots.getPlayerPlots(uuid).get(0);
        java.util.List<String> members = plots.getPlotMembers(plotId);

        player.sendMessage(Component.text("=== Plot Members ===", NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("You (owner)", NamedTextColor.GOLD));

        if (members.isEmpty()) {
            player.sendMessage(Component.text("No members added yet", NamedTextColor.GRAY));
        } else {
            for (String memberUuid : members) {
                org.bukkit.OfflinePlayer member = org.bukkit.Bukkit.getOfflinePlayer(java.util.UUID.fromString(memberUuid));
                String name = member.getName() != null ? member.getName() : memberUuid.substring(0, 8);
                player.sendMessage(Component.text("  • " + name, NamedTextColor.GREEN));
            }
        }

        return true;
    }

    private boolean handleClan(Player player, String[] args) {
        ClanManager clanManager = plugin.getClanManager();
        String uuid = player.getUniqueId().toString();

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /citybuild clan <create|invite|leave|info|balance|transfer|level>", NamedTextColor.RED));
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "create":
                if (args.length < 3) {
                    player.sendMessage(Component.text("Usage: /citybuild clan create <name>", NamedTextColor.RED));
                    return true;
                }
                
                if (clanManager.getPlayerClan(uuid) != null) {
                    player.sendMessage(Component.text("❌ You're already in a clan!", NamedTextColor.RED));
                    return true;
                }
                
                String clanName = args[2];
                if (clanManager.createClan(clanName, uuid)) {
                    player.sendMessage(Component.text("✓ Clan '" + clanName + "' created!", NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("❌ Clan name already exists!", NamedTextColor.RED));
                }
                return true;

            case "info":
                ClanManager.Clan clan = clanManager.getPlayerClan(uuid);
                if (clan == null) {
                    player.sendMessage(Component.text("❌ You're not in a clan!", NamedTextColor.RED));
                    return true;
                }
                
                player.sendMessage(Component.text("=== Clan Info ===", NamedTextColor.GOLD));
                player.sendMessage(Component.text("Name: " + clan.name, NamedTextColor.YELLOW));
                player.sendMessage(Component.text("Members: " + clan.members.size(), NamedTextColor.YELLOW));
                player.sendMessage(Component.text("Balance: $" + clan.balance, NamedTextColor.YELLOW));
                player.sendMessage(Component.text("Level: " + clan.level, NamedTextColor.YELLOW));
                return true;

            case "leave":
                clan = clanManager.getPlayerClan(uuid);
                if (clan == null) {
                    player.sendMessage(Component.text("❌ You're not in a clan!", NamedTextColor.RED));
                    return true;
                }
                
                clanManager.removeMember(clan.name, uuid);
                player.sendMessage(Component.text("✓ You left the clan!", NamedTextColor.GREEN));
                return true;

            default:
                player.sendMessage(Component.text("Unknown subcommand!", NamedTextColor.RED));
                return true;
        }
    }

    private boolean handleSetWarp(Player player, String[] args) {
        WarpManager warpManager = plugin.getWarpManager();
        String uuid = player.getUniqueId().toString();

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /citybuild setwarp <name>", NamedTextColor.RED));
            return true;
        }

        String warpName = args[1];
        if (warpManager.createWarp(warpName, uuid, player.getLocation())) {
            player.sendMessage(Component.text("✓ Warp '" + warpName + "' created!", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("❌ Warp name already exists!", NamedTextColor.RED));
        }
        return true;
    }

    private boolean handleWarp(Player player, String[] args) {
        WarpManager warpManager = plugin.getWarpManager();

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /citybuild warp <name>", NamedTextColor.RED));
            return true;
        }

        String warpName = args[1];
        WarpManager.Warp warp = warpManager.getWarp(warpName);

        if (warp == null) {
            player.sendMessage(Component.text("❌ Warp not found!", NamedTextColor.RED));
            return true;
        }

        org.bukkit.World world = org.bukkit.Bukkit.getWorld(warp.world);
        if (world == null) {
            player.sendMessage(Component.text("❌ Warp world doesn't exist!", NamedTextColor.RED));
            return true;
        }

        // Check cooldown
        if (isTeleportOnCooldown(player)) {
            player.sendMessage(Component.text("⏱️ Wait before teleporting again!", NamedTextColor.YELLOW));
            return true;
        }

        player.teleport(warp.toLocation(world));
        setTeleportCooldown(player);
        player.sendMessage(Component.text("✓ Warped to '" + warpName + "'!", NamedTextColor.GREEN));
        grantInvincibility(player, 5);
        return true;
    }

    private boolean handleDelWarp(Player player, String[] args) {
        WarpManager warpManager = plugin.getWarpManager();
        String uuid = player.getUniqueId().toString();

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /citybuild delwarp <name>", NamedTextColor.RED));
            return true;
        }

        String warpName = args[1];
        WarpManager.Warp warp = warpManager.getWarp(warpName);

        if (warp == null) {
            player.sendMessage(Component.text("❌ Warp not found!", NamedTextColor.RED));
            return true;
        }

        if (!warp.creator.equals(uuid)) {
            player.sendMessage(Component.text("❌ You don't own this warp!", NamedTextColor.RED));
            return true;
        }

        if (warpManager.deleteWarp(warpName)) {
            player.sendMessage(Component.text("✓ Warp deleted!", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("❌ Failed to delete warp!", NamedTextColor.RED));
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("=== CityBuild v2.1.0 Commands ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("--- Economy ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild balance - Check balance", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild pay <player> <amount> - Send money", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild daily - Claim daily reward", NamedTextColor.YELLOW));
        
        player.sendMessage(Component.text("--- Plots ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild buy - Buy a plot ($" + plots.getPlotBuyPrice() + ")", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild sell - Sell a plot ($" + plots.getPlotSellPrice() + ")", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild info - View your info", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild addmember <player> - Add member to plot", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild removemember <player> - Remove member", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild members - List plot members", NamedTextColor.YELLOW));
        
        player.sendMessage(Component.text("--- Expansion Features ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild clan create <name> - Create a clan", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild setwarp <name> - Create a warp", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild warp <name> - Go to a warp", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild achievements - View achievements", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild quests - Daily quests", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild enchant - Enchanting system", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild trade - Player trading", NamedTextColor.YELLOW));
        
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
        player.sendMessage(Component.text("/citybuild menu - Open main menu", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild help - Show this message", NamedTextColor.YELLOW));
    }

    private boolean handleAdmin(Player player, String[] args) {
        AdminCommandHandler adminHandler = new AdminCommandHandler(plugin);
        return adminHandler.handleAdminCommand(player, args);
    }
}


