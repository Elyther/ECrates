package com.elyther.ecrates.gui;

import com.elyther.ecrates.ECrates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CrateEditorGUI {

    private final ECrates plugin;
    private final Player player;
    private final String crate;

    public CrateEditorGUI(
            ECrates plugin,
            Player player,
            String crate
    ) {

        this.plugin = plugin;
        this.player = player;
        this.crate = crate;
    }

    public void open() {

        String title = plugin.getConfig()
                .getString(
                        "gui.editor-title",
                        "&8%crate% Rewards"
                )
                .replace("%crate%", crate);

        title = ChatColor.translateAlternateColorCodes(
                '&',
                title
        );

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        title
                );

        List<ItemStack> rewards =
                plugin.getCrateManager()
                        .getRewards(crate);

        for (int i = 0; i < rewards.size() && i < 45; i++) {

            inv.setItem(
                    i,
                    rewards.get(i).clone()
            );
        }

        player.openInventory(inv);
    }

    public static void save(
            ECrates plugin,
            Player player,
            String crate,
            Inventory inventory
    ) {

        List<ItemStack> rewards = new ArrayList<>();

        for (int i = 0; i < 45; i++) {

            ItemStack item = inventory.getItem(i);

            if (item != null &&
                    item.getType() != org.bukkit.Material.AIR) {

                rewards.add(item.clone());
            }
        }

        plugin.getCrateManager()
                .setRewards(crate, rewards);

        player.sendMessage(
                ChatColor.GREEN +
                        "Rewards saved."
        );
    }
}
