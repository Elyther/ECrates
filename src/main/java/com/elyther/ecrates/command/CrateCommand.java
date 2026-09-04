package com.elyther.ecrates.command;

import com.elyther.ecrates.ECrates;
import com.elyther.ecrates.gui.CrateEditorGUI;
import com.elyther.ecrates.manager.KeyManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CrateCommand implements CommandExecutor {

    private final ECrates plugin;

    public CrateCommand(ECrates plugin) {
        this.plugin = plugin;
    }

    private String msg(Player p, String key) {

        String lang = plugin.getConfig()
                .getString("language", "en");

        String text = plugin.getConfig().getString("messages." + key);

        if (text == null) {
            text = key;
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Player only.");
            return true;
        }

        if (!player.hasPermission("ecrates.admin")) {
            player.sendMessage(
                    ChatColor.RED + "You don't have permission."
            );
            return true;
        }

        if (args.length == 0) {

            player.sendMessage(ChatColor.LIGHT_PURPLE +
                    "/crate create <name>");

            player.sendMessage(ChatColor.LIGHT_PURPLE +
                    "/crate edit <name>");

            player.sendMessage(ChatColor.LIGHT_PURPLE +
                    "/crate remove");

            player.sendMessage(ChatColor.LIGHT_PURPLE +
                    "/crate reload");

            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {

            if (args.length < 2) {
                player.sendMessage(
                        ChatColor.RED + "/crate create <name>"
                );
                return true;
            }

            String crate = args[1].toLowerCase();

            if (!plugin.getCrateManager().exists(crate)) {

                player.sendMessage(
                        ChatColor.RED +
                                "This crate does not exist in config."
                );

                return true;
            }

            Block block = player.getTargetBlockExact(
                    plugin.getConfig().getInt("create-distance", 6)
            );

            if (block == null) {

                player.sendMessage(
                        ChatColor.RED +
                                "You must look at a block."
                );

                return true;
            }

            plugin.getCrateManager()
                    .addLocation(crate, block.getLocation());

            player.sendMessage(
                    ChatColor.GREEN +
                            "Crate created: " + crate
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("edit")) {

            if (args.length < 2) {
                player.sendMessage(
                        ChatColor.RED + "/crate edit <name>"
                );
                return true;
            }

            String crate = args[1].toLowerCase();

            if (!plugin.getCrateManager().exists(crate)) {

                player.sendMessage(
                        ChatColor.RED +
                                "Crate not found."
                );

                return true;
            }

            new CrateEditorGUI(
                    plugin,
                    player,
                    crate
            ).open();

            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {

            Block block = player.getTargetBlockExact(6);

            if (block == null) {

                player.sendMessage(
                        ChatColor.RED +
                                "Look at a crate."
                );

                return true;
            }

            plugin.getCrateManager()
                    .removeLocation(block.getLocation());

            player.sendMessage(
                    ChatColor.GREEN +
                            "Crate removed."
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            plugin.reloadConfig();
            plugin.getCrateManager().reload();

            player.sendMessage(
                    ChatColor.GREEN +
                            "ECrates reloaded."
            );

            return true;
        }

        return true;
    }
}
