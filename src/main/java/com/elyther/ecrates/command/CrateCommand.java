package com.elyther.ecrates.command;

import com.elyther.ecrates.ECrates;
import com.elyther.ecrates.gui.CrateEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrateCommand
        implements CommandExecutor, TabCompleter {

    private final ECrates plugin;

    public CrateCommand(ECrates plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "Player only."
            );

            return true;
        }

        if (!player.hasPermission(
                "ecrates.admin"
        )) {

            player.sendMessage(
                    ChatColor.RED
                            + "You don't have permission."
            );

            return true;
        }

        if (args.length == 0) {

            sendHelp(player);

            return true;
        }

        if (args[0].equalsIgnoreCase(
                "create"
        )) {

            if (args.length < 2) {

                player.sendMessage(
                        ChatColor.RED
                                + "/crate create <crate>"
                );

                return true;
            }

            String crate =
                    args[1].toLowerCase();

            if (!plugin.getCrateManager()
                    .exists(crate)) {

                player.sendMessage(
                        ChatColor.RED
                                + "Crate does not exist in config."
                );

                return true;
            }

            Block block =
                    player.getTargetBlockExact(
                            plugin.getConfig()
                                    .getInt(
                                            "create-distance",
                                            6
                                    )
                    );

            if (block == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "Look at a crate block."
                );

                return true;
            }

            if (!plugin.getCrateManager()
                    .isCrateBlock(
                            block.getType()
                    )) {

                player.sendMessage(
                        ChatColor.RED
                                + "This block cannot be a crate."
                );

                return true;
            }

            plugin.getCrateManager()
                    .addLocation(
                            crate,
                            block.getLocation()
                    );

            player.sendMessage(
                    ChatColor.GREEN
                            + "Crate created: "
                            + crate
            );

            return true;
        }

        if (args[0].equalsIgnoreCase(
                "edit"
        )) {

            if (args.length < 2) {

                player.sendMessage(
                        ChatColor.RED
                                + "/crate edit <crate>"
                );

                return true;
            }

            String crate =
                    args[1].toLowerCase();

            if (!plugin.getCrateManager()
                    .exists(crate)) {

                player.sendMessage(
                        ChatColor.RED
                                + "Crate not found."
                );

                return true;
            }

            CrateEditorGUI.open(
                    plugin,
                    player,
                    crate
            );

            return true;
        }

        if (args[0].equalsIgnoreCase(
                "remove"
        )) {

            Block block =
                    player.getTargetBlockExact(
                            6
                    );

            if (block == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "Look at the crate."
                );

                return true;
            }

            plugin.getCrateManager()
                    .removeLocation(
                            block.getLocation()
                    );

            player.sendMessage(
                    ChatColor.GREEN
                            + "Crate removed."
            );

            return true;
        }

        if (args[0].equalsIgnoreCase(
                "key"
        )) {

            if (args.length < 4) {

                player.sendMessage(
                        ChatColor.RED
                                + "/crate key <player> <crate> <amount>"
                );

                return true;
            }

            Player target =
                    plugin.getServer()
                            .getPlayerExact(
                                    args[1]
                            );

            if (target == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "Player is not online."
                );

                return true;
            }

            String crate =
                    args[2].toLowerCase();

            if (!plugin.getCrateManager()
                    .exists(crate)) {

                player.sendMessage(
                        ChatColor.RED
                                + "Crate not found."
                );

                return true;
            }

            int amount;

            try {

                amount =
                        Integer.parseInt(
                                args[3]
                        );

            } catch (NumberFormatException e) {

                player.sendMessage(
                        ChatColor.RED
                                + "Invalid amount."
                );

                return true;
            }

            if (amount <= 0) {

                player.sendMessage(
                        ChatColor.RED
                                + "Amount must be above 0."
                );

                return true;
            }

            plugin.getKeyManager()
                    .addKey(
                            target,
                            crate,
                            amount
                    );

            player.sendMessage(
                    ChatColor.GREEN
                            + "Gave "
                            + amount
                            + " virtual key(s) to "
                            + target.getName()
            );

            return true;
        }

        if (args[0].equalsIgnoreCase(
                "reload"
        )) {

            plugin.reloadConfig();
            plugin.getCrateManager()
                    .reload();

            player.sendMessage(
                    ChatColor.GREEN
                            + "ECrates reloaded."
            );

            return true;
        }

        sendHelp(player);

        return true;
    }

    private void sendHelp(
            Player player
    ) {

        player.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "========== ECrates =========="
        );

        player.sendMessage(
                ChatColor.WHITE
                        + "/crate create <crate>"
        );

        player.sendMessage(
                ChatColor.WHITE
                        + "/crate edit <crate>"
        );

        player.sendMessage(
                ChatColor.WHITE
                        + "/crate remove"
        );

        player.sendMessage(
                ChatColor.WHITE
                        + "/crate key <player> <crate> <amount>"
        );

        player.sendMessage(
                ChatColor.WHITE
                        + "/crate reload"
        );
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        List<String> result =
                new ArrayList<>();

        if (args.length == 1) {

            result.add("create");
            result.add("edit");
            result.add("remove");
            result.add("key");
            result.add("reload");

        } else if (args.length == 2 &&
                (args[0].equalsIgnoreCase(
                        "create"
                ) ||
                args[0].equalsIgnoreCase(
                        "edit"
                ))) {

            var section =
                    plugin.getConfig()
                            .getConfigurationSection(
                                    "crates"
                            );

            if (section != null) {
                result.addAll(
                        section.getKeys(false)
                );
            }
        }

        return result;
    }
}
