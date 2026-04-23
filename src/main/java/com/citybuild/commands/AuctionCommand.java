package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.auctions.AuctionHouseManager;
import com.citybuild.features.auctions.AuctionItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Command handler for /auction command
 * Manages auction house operations
 */
public class AuctionCommand implements CommandExecutor {

    private final CityBuildPlugin plugin;
    private final AuctionHouseManager auctionHouseManager;

    public AuctionCommand(CityBuildPlugin plugin, AuctionHouseManager auctionHouseManager) {
        this.plugin = plugin;
        this.auctionHouseManager = auctionHouseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "create":
            case "sell":
                handleCreate(player, args);
                break;
            case "bid":
                handleBid(player, args);
                break;
            case "list":
            case "browse":
                handleList(player);
                break;
            case "my":
            case "myauctions":
                handleMyAuctions(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "cancel":
                handleCancel(player, args);
                break;
            case "stats":
                handleStats(player);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /auction create <price>");
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getAmount() == 0) {
            player.sendMessage("§c❌ You must hold an item!");
            return;
        }

        try {
            double price = Double.parseDouble(args[1]);
            if (price <= 0) {
                player.sendMessage("§c❌ Price must be positive!");
                return;
            }

            String auctionId = auctionHouseManager.createAuction(
                player.getUniqueId(), 
                itemInHand.clone(), 
                price
            );

            if (auctionId != null) {
                player.getInventory().setItemInMainHand(null);
                player.sendMessage("§a✓ Auction created! §6ID: §e" + auctionId);
                player.sendMessage("§7Item: §e" + itemInHand.getType().name() + " x" + itemInHand.getAmount());
                player.sendMessage("§7Starting price: §6$" + String.format("%.2f", price));
                player.sendMessage("§7Duration: §e24 hours");
            } else {
                player.sendMessage("§c❌ Failed to create auction!");
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§c❌ Invalid price!");
        }
    }

    private void handleBid(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cUsage: /auction bid <id> <amount>");
            return;
        }

        String auctionId = args[1];
        try {
            double bidAmount = Double.parseDouble(args[2]);
            if (bidAmount <= 0) {
                player.sendMessage("§c❌ Bid must be positive!");
                return;
            }

            AuctionItem auction = auctionHouseManager.getAuction(auctionId);
            if (auction == null) {
                player.sendMessage("§c❌ Auction not found!");
                return;
            }

            if (!auctionHouseManager.placeBid(player.getUniqueId(), auctionId, bidAmount)) {
                player.sendMessage("§c❌ Bid failed! Ensure you have enough coins and bid is higher than current.");
                return;
            }

            player.sendMessage("§a✓ Bid placed!");
            player.sendMessage("§6Auction: §e" + auctionId);
            player.sendMessage("§6Your bid: §e$" + String.format("%.2f", bidAmount));

        } catch (NumberFormatException e) {
            player.sendMessage("§c❌ Invalid bid amount!");
        }
    }

    private void handleList(Player player) {
        List<AuctionItem> auctions = auctionHouseManager.getActiveAuctions();
        
        if (auctions.isEmpty()) {
            player.sendMessage("§c❌ No active auctions!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 AUCTION HOUSE - ACTIVE AUCTIONS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (int i = 0; i < Math.min(10, auctions.size()); i++) {
            AuctionItem auction = auctions.get(i);
            player.sendMessage("§6ID: §e" + auction.getAuctionId());
            player.sendMessage("  §6Item: §e" + auction.getItemStack().getType().name() + 
                " x" + auction.getItemStack().getAmount());
            player.sendMessage("  §6Current Bid: §e$" + String.format("%.2f", auction.getCurrentBid()) +
                " §6| Time: §e" + auction.getTimeRemainingMinutes() + "m");
            player.sendMessage("");
        }

        if (auctions.size() > 10) {
            player.sendMessage("§7... and " + (auctions.size() - 10) + " more");
        }
        player.sendMessage("");
    }

    private void handleMyAuctions(Player player) {
        List<AuctionItem> myAuctions = auctionHouseManager.getPlayerAuctions(player.getUniqueId());
        
        if (myAuctions.isEmpty()) {
            player.sendMessage("§c❌ You have no auctions!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 YOUR AUCTIONS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (AuctionItem auction : myAuctions) {
            player.sendMessage("§6ID: §e" + auction.getAuctionId());
            player.sendMessage("  §6Status: " + auction.getStatus().getDisplay());
            player.sendMessage("  §6Current Bid: §e$" + String.format("%.2f", auction.getCurrentBid()));
            if (auction.getStatus() == AuctionItem.AuctionStatus.ACTIVE) {
                player.sendMessage("  §6Time Remaining: §e" + auction.getTimeRemainingMinutes() + " minutes");
            }
            player.sendMessage("");
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /auction info <id>");
            return;
        }

        AuctionItem auction = auctionHouseManager.getAuction(args[1]);
        if (auction == null) {
            player.sendMessage("§c❌ Auction not found!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 AUCTION INFO");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(auction.getFormattedInfo());
        player.sendMessage("");
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /auction cancel <id>");
            return;
        }

        if (auctionHouseManager.cancelAuction(args[1], player.getUniqueId())) {
            player.sendMessage("§a✓ Auction cancelled!");
        } else {
            player.sendMessage("§c❌ Failed to cancel auction!");
        }
    }

    private void handleStats(Player player) {
        var stats = auctionHouseManager.getStatistics();
        
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 AUCTION HOUSE STATISTICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§6Active Auctions: §e" + stats.get("active_auctions"));
        player.sendMessage("§6Total Auctions: §e" + stats.get("total_auctions"));
        player.sendMessage("§6Active Sellers: §e" + stats.get("sellers"));
        player.sendMessage("§6Total Value: §e$" + String.format("%.2f", (double) stats.get("total_value")));
        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 AUCTION HOUSE HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/auction create <price> §6- List item in hand");
        player.sendMessage("§7/auction list §6- Browse all auctions");
        player.sendMessage("§7/auction bid <id> <amount> §6- Place a bid");
        player.sendMessage("§7/auction my §6- View your auctions");
        player.sendMessage("§7/auction info <id> §6- View auction details");
        player.sendMessage("§7/auction cancel <id> §6- Cancel your auction");
        player.sendMessage("§7/auction stats §6- View statistics");
        player.sendMessage("");
    }
}
