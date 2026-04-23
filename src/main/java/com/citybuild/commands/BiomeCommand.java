package com.citybuild.commands;

import com.citybuild.features.biomes.BiomeGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /biome command
 */
public class BiomeCommand implements CommandExecutor {

    private final BiomeGenerator biomeGenerator;

    public BiomeCommand(BiomeGenerator biomeGenerator) {
        this.biomeGenerator = biomeGenerator;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("citybuild.admin")) {
            sender.sendMessage("§c❌ You don't have permission!");
            return true;
        }

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
            case "list":
                handleList(player);
                break;
            case "generate":
                handleGenerate(player, args);
                break;
            case "info":
                handleInfo(player, args);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleList(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 AVAILABLE BIOMES");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (BiomeGenerator.CustomBiome biome : biomeGenerator.getAllBiomes()) {
            player.sendMessage("§6🌲 " + biome.getDisplay());
        }

        player.sendMessage("");
    }

    private void handleGenerate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /biome generate <name> [radius]");
            return;
        }

        String biomeName = args[1];
        int radius = args.length > 2 ? Integer.parseInt(args[2]) : 50;

        BiomeGenerator.CustomBiome biome = null;
        for (BiomeGenerator.CustomBiome b : biomeGenerator.getAllBiomes()) {
            if (b.name().equalsIgnoreCase(biomeName)) {
                biome = b;
                break;
            }
        }

        if (biome == null) {
            player.sendMessage("§c❌ Biome not found!");
            return;
        }

        biomeGenerator.generateBiome(player.getLocation(), radius, biome);
        player.sendMessage("§a✓ Biome generated!");
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /biome info <name>");
            return;
        }

        String biomeName = args[1];
        BiomeGenerator.CustomBiome biome = null;

        for (BiomeGenerator.CustomBiome b : biomeGenerator.getAllBiomes()) {
            if (b.name().equalsIgnoreCase(biomeName)) {
                biome = b;
                break;
            }
        }

        if (biome == null) {
            player.sendMessage("§c❌ Biome not found!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 BIOME INFO");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(biomeGenerator.getBiomeInfo(biome));
        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 BIOME COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/biome list §6- List all biomes");
        player.sendMessage("§7/biome generate <name> [radius] §6- Generate biome");
        player.sendMessage("§7/biome info <name> §6- Biome information");
        player.sendMessage("");
    }
}
