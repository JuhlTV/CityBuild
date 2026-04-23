package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.model.PlotData;
import com.citybuild.managers.PlotManager;
import com.citybuild.utils.PlotGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Admin commands for plot management and expansion
 * /citybuild admin plot <subcommand> [args...]
 */
public class AdminPlotCommandHandler {
    private final CityBuildPlugin plugin;
    private final PlotManager plotManager;

    public AdminPlotCommandHandler(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.plotManager = plugin.getPlotManager();
    }

    public boolean handleAdminPlotCommand(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(formatError("❌ You don't have permission!"));
            return true;
        }

        if (args.length < 3) {
            showPlotAdminHelp(player);
            return true;
        }

        String subcommand = args[2].toLowerCase();

        switch (subcommand) {
            case "expand":
                return handleExpandPlot(player, args);
            case "resize":
                return handleResizePlot(player, args);
            case "clear":
                return handleClearPlot(player, args);
            case "delete":
                return handleDeletePlot(player, args);
            case "info":
                return handlePlotInfo(player, args);
            case "premium":
                return handlePremiumToggle(player, args);
            case "teleport":
                return handleTeleportToPlot(player, args);
            case "list":
                return handleListPlots(player, args);
            case "help":
            default:
                showPlotAdminHelp(player);
                return true;
        }
    }

    /**
     * /citybuild admin plot expand <player> <direction> [blocks]
     * Expand a player's first plot in a direction
     * Directions: north, south, east, west
     */
    private boolean handleExpandPlot(Player player, String[] args) {
        if (args.length < 5) {
            player.sendMessage(formatError("Usage: /citybuild admin plot expand <player> <direction> [blocks]"));
            player.sendMessage(formatInfo("Directions: north, south, east, west"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            player.sendMessage(formatError("Player not found: " + args[3]));
            return true;
        }

        String direction = args[4].toLowerCase();
        int blocks = args.length >= 6 ? Integer.parseInt(args[5]) : 5;

        PlotData plot = plotManager.getFirstPlot(target.getUniqueId().toString());
        if (plot == null) {
            player.sendMessage(formatError("Player has no plots!"));
            return true;
        }

        PlotGenerator.expandPlot(plugin.getWorldManager().getPlotWorld(), plot, direction, blocks);
        plotManager.savePlot(plot);

        player.sendMessage(formatSuccess("✓ Expanded " + target.getName() + "'s plot " + blocks + " blocks to the " + direction));
        target.sendMessage(formatSuccess("✓ Admin expanded your plot " + blocks + " blocks to the " + direction));

        return true;
    }

    /**
     * /citybuild admin plot resize <player> <width> <height>
     * Resize a player's first plot to exact dimensions
     */
    private boolean handleResizePlot(Player player, String[] args) {
        if (args.length < 6) {
            player.sendMessage(formatError("Usage: /citybuild admin plot resize <player> <width> <height>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            player.sendMessage(formatError("Player not found: " + args[3]));
            return true;
        }

        int width, height;
        try {
            width = Integer.parseInt(args[4]);
            height = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            player.sendMessage(formatError("Width and height must be numbers!"));
            return true;
        }

        if (width < 10 || height < 10) {
            player.sendMessage(formatError("Minimum plot size is 10x10!"));
            return true;
        }

        if (width > 100 || height > 100) {
            player.sendMessage(formatError("Maximum plot size is 100x100!"));
            return true;
        }

        PlotData plot = plotManager.getFirstPlot(target.getUniqueId().toString());
        if (plot == null) {
            player.sendMessage(formatError("Player has no plots!"));
            return true;
        }

        plot.setSizeX(width);
        plot.setSizeZ(height);
        PlotGenerator.generatePlot(plugin.getWorldManager().getPlotWorld(), plot);
        plotManager.savePlot(plot);

        player.sendMessage(formatSuccess("✓ Resized " + target.getName() + "'s plot to " + width + "x" + height));
        target.sendMessage(formatSuccess("✓ Admin resized your plot to " + width + "x" + height));

        return true;
    }

    /**
     * /citybuild admin plot clear <player>
     * Clear all blocks from a player's plot (keep border)
     */
    private boolean handleClearPlot(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(formatError("Usage: /citybuild admin plot clear <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            player.sendMessage(formatError("Player not found: " + args[3]));
            return true;
        }

        PlotData plot = plotManager.getFirstPlot(target.getUniqueId().toString());
        if (plot == null) {
            player.sendMessage(formatError("Player has no plots!"));
            return true;
        }

        PlotGenerator.clearPlot(plugin.getWorldManager().getPlotWorld(), plot);

        player.sendMessage(formatSuccess("✓ Cleared " + target.getName() + "'s plot"));
        target.sendMessage(formatSuccess("✓ Admin cleared your plot"));

        return true;
    }

    /**
     * /citybuild admin plot delete <player>
     * Delete a player's first plot (terrain + border)
     */
    private boolean handleDeletePlot(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(formatError("Usage: /citybuild admin plot delete <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            player.sendMessage(formatError("Player not found: " + args[3]));
            return true;
        }

        PlotData plot = plotManager.getFirstPlot(target.getUniqueId().toString());
        if (plot == null) {
            player.sendMessage(formatError("Player has no plots!"));
            return true;
        }

        int plotId = plot.getPlotId();
        PlotGenerator.deletePlot(plugin.getWorldManager().getPlotWorld(), plot);
        plotManager.removePlot(target.getUniqueId().toString(), plotId);

        player.sendMessage(formatSuccess("✓ Deleted Plot #" + plotId + " from " + target.getName()));
        target.sendMessage(formatError("⚠ Admin deleted your Plot #" + plotId));

        return true;
    }

    /**
     * /citybuild admin plot info <player>
     * Show detailed info about a player's plot
     */
    private boolean handlePlotInfo(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(formatError("Usage: /citybuild admin plot info <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            player.sendMessage(formatError("Player not found: " + args[3]));
            return true;
        }

        PlotData plot = plotManager.getFirstPlot(target.getUniqueId().toString());
        if (plot == null) {
            player.sendMessage(formatError("Player has no plots!"));
            return true;
        }

        showPlotDetails(player, plot);
        return true;
    }

    /**
     * /citybuild admin plot premium <player> [on/off]
     * Toggle premium status for a player's plot
     */
    private boolean handlePremiumToggle(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(formatError("Usage: /citybuild admin plot premium <player> [on/off]"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            player.sendMessage(formatError("Player not found: " + args[3]));
            return true;
        }

        PlotData plot = plotManager.getFirstPlot(target.getUniqueId().toString());
        if (plot == null) {
            player.sendMessage(formatError("Player has no plots!"));
            return true;
        }

        boolean premium;
        if (args.length >= 5) {
            premium = args[4].equalsIgnoreCase("on") || args[4].equalsIgnoreCase("true");
        } else {
            premium = !plot.isPremium();
        }

        plot.setPremium(premium);
        plotManager.savePlot(plot);

        String status = premium ? "✓ Premium" : "Standard";
        player.sendMessage(formatSuccess("✓ Set " + target.getName() + "'s plot to: " + status));
        target.sendMessage(formatSuccess("✓ Your plot is now: " + status));

        return true;
    }

    /**
     * /citybuild admin plot teleport <player>
     * Teleport to a player's plot
     */
    private boolean handleTeleportToPlot(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(formatError("Usage: /citybuild admin plot teleport <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            player.sendMessage(formatError("Player not found: " + args[3]));
            return true;
        }

        PlotData plot = plotManager.getFirstPlot(target.getUniqueId().toString());
        if (plot == null) {
            player.sendMessage(formatError("Player has no plots!"));
            return true;
        }

        player.teleport(PlotGenerator.getPlotSpawn(plugin.getWorldManager().getPlotWorld(), plot));
        player.sendMessage(formatSuccess("✓ Teleported to " + target.getName() + "'s plot"));

        return true;
    }

    /**
     * /citybuild admin plot list [player]
     * List all plots of a player or server stats
     */
    private boolean handleListPlots(Player player, String[] args) {
        if (args.length >= 4) {
            // Show specific player's plots
            Player target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                player.sendMessage(formatError("Player not found: " + args[3]));
                return true;
            }

            showPlayerPlots(player, target);
        } else {
            // Show server stats
            showServerPlotStats(player);
        }
        return true;
    }

    // ===== HELPER METHODS =====

    private void showPlotAdminHelp(Player player) {
        player.sendMessage(Component.empty()
            .append(Component.text("════════════════════════════════", NamedTextColor.BLUE)).append(Component.newline())
            .append(Component.text("  Admin Plot Commands", NamedTextColor.GOLD, TextDecoration.BOLD)).append(Component.newline())
            .append(Component.text("════════════════════════════════", NamedTextColor.BLUE)));

        sendCommand(player, "/citybuild admin plot expand <player> <dir> [blocks]", "Expand plot");
        sendCommand(player, "/citybuild admin plot resize <player> <w> <h>", "Resize plot");
        sendCommand(player, "/citybuild admin plot clear <player>", "Clear plot");
        sendCommand(player, "/citybuild admin plot delete <player>", "Delete plot");
        sendCommand(player, "/citybuild admin plot info <player>", "Show plot info");
        sendCommand(player, "/citybuild admin plot premium <player> [on/off]", "Toggle premium");
        sendCommand(player, "/citybuild admin plot teleport <player>", "TP to plot");
        sendCommand(player, "/citybuild admin plot list [player]", "List plots");
    }

    private void showPlotDetails(Player player, PlotData plot) {
        player.sendMessage(Component.empty()
            .append(Component.text("╔════════════════════════════════╗", NamedTextColor.AQUA)).append(Component.newline())
            .append(Component.text("║ ", NamedTextColor.AQUA))
            .append(Component.text("Plot #" + plot.getPlotId(), NamedTextColor.YELLOW, TextDecoration.BOLD))
            .append(Component.text(" ║", NamedTextColor.AQUA)).append(Component.newline())
            .append(Component.text("╚════════════════════════════════╝", NamedTextColor.AQUA)));

        player.sendMessage("  Owner: " + plot.getOwnerUuid());
        player.sendMessage("  Size: " + plot.getSizeX() + "x" + plot.getSizeZ() + " blocks");
        player.sendMessage("  Area: " + plot.getArea() + " m²");
        player.sendMessage("  Position: (" + plot.getCornerX() + ", " + plot.getCornerZ() + ")");
        player.sendMessage("  Members: " + plot.getMemberCount());
        player.sendMessage("  Premium: " + (plot.isPremium() ? "✓ Yes" : "No"));
        player.sendMessage("  Created: " + formatDate(plot.getCreatedAt()));
    }

    private void showPlayerPlots(Player player, Player target) {
        player.sendMessage(Component.empty()
            .append(Component.text("📊 ", NamedTextColor.GOLD))
            .append(Component.text(target.getName() + "'s Plots", NamedTextColor.YELLOW, TextDecoration.BOLD)));

        PlotData plot = plotManager.getFirstPlot(target.getUniqueId().toString());
        if (plot == null) {
            player.sendMessage(Component.text("  No plots", NamedTextColor.GRAY));
            return;
        }

        showPlotDetails(player, plot);
    }

    private void showServerPlotStats(Player player) {
        player.sendMessage(Component.empty()
            .append(Component.text("📊 Server Plot Statistics", NamedTextColor.GOLD, TextDecoration.BOLD)));
        
        int totalPlots = plotManager.getTotalPlots();
        player.sendMessage("  Total plots: " + totalPlots);
        player.sendMessage("  Generated terrain areas: " + (totalPlots * 256) + " blocks²");
    }

    private void sendCommand(Player player, String command, String description) {
        player.sendMessage(Component.text("  " + command, NamedTextColor.YELLOW)
            .append(Component.text(" - " + description, NamedTextColor.GRAY)));
    }

    private Component formatError(String text) {
        return Component.text(text, NamedTextColor.RED);
    }

    private Component formatSuccess(String text) {
        return Component.text(text, NamedTextColor.GREEN);
    }

    private Component formatInfo(String text) {
        return Component.text(text, NamedTextColor.GRAY);
    }

    private String formatDate(long timestamp) {
        return new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(timestamp));
    }
}
