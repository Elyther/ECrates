package com.elyther.ecrates.gui;

import com.elyther.ecrates.ECrates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CrateEditorGUI {

    public static void open(
            ECrates plugin,
            Player player,
            String crate
    ) {

        String title =
                ChatColor.DARK_GRAY
                        + "Edit Rewards: "
                        + crate;

        Inventory inventory =
                Bukkit.createInventory(
                        new EditorHolder(crate),
                        54,
                        title
                );

        List<ItemStack> rewards =
                plugin.getCrateManager()
                        .getRewards(crate);

        for (int i = 0;
             i < rewards.size() && i < 45;
             i++) {

            inventory.setItem(
                    i,
                    rewards.get(i).clone()
            );
        }

        ItemStack save =
                new ItemStack(
                        Material.LIME_WOOL
                );

        ItemMeta saveMeta =
                save.getItemMeta();

        saveMeta.setDisplayName(
                ChatColor.GREEN
                        + "SAVE"
        );

        save.setItemMeta(saveMeta);

        inventory.setItem(
                49,
                save
        );

        ItemStack cancel =
                new ItemStack(
                        Material.RED_WOOL
                );

        ItemMeta cancelMeta =
                cancel.getItemMeta();

        cancelMeta.setDisplayName(
                ChatColor.RED
                        + "CANCEL"
        );

        cancel.setItemMeta(cancelMeta);

        inventory.setItem(
                53,
                cancel
        );

        player.openInventory(inventory);
    }

    public static void save(
            ECrates plugin,
            Player player,
            String crate,
            Inventory inventory
    ) {

        List<ItemStack> rewards =
                new ArrayList<>();

        for (int i = 0; i < 45; i++) {

            ItemStack item =
                    inventory.getItem(i);

            if (item != null &&
                    item.getType()
                            != Material.AIR) {

                rewards.add(
                        item.clone()
                );
            }
        }

        plugin.getCrateManager()
                .setRewards(
                        crate,
                        rewards
                );

        player.sendMessage(
                ChatColor.GREEN
                        + "Rewards saved!"
        );
    }

    public static class EditorHolder
            implements org.bukkit.inventory.InventoryHolder {

        private final String crate;

        public EditorHolder(String crate) {
            this.crate = crate;
        }

        public String getCrate() {
            return crate;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
