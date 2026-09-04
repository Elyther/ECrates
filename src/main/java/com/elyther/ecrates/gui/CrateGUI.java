package com.elyther.ecrates.gui;

import com.elyther.ecrates.ECrates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CrateGUI {

    public static final String TYPE =
            "ECrates-CRATE";

    public static void open(
            ECrates plugin,
            Player player,
            String crate
    ) {

        String name =
                plugin.getConfig()
                        .getString(
                                "crates."
                                        + crate
                                        + ".display-name",
                                crate
                        );

        name =
                ChatColor.translateAlternateColorCodes(
                        '&',
                        name
                );

        String title =
                plugin.getConfig()
                        .getString(
                                "gui.crate-title",
                                "&8%crate% Crate"
                        )
                        .replace(
                                "%crate%",
                                name
                        );

        title =
                ChatColor.translateAlternateColorCodes(
                        '&',
                        title
                );

        Inventory inventory =
                Bukkit.createInventory(
                        new CrateHolder(crate),
                        54,
                        title
                );

        List<ItemStack> rewards =
                plugin.getCrateManager()
                        .getRewards(crate);

        for (int i = 0;
             i < rewards.size() && i < 45;
             i++) {

            ItemStack item =
                    rewards.get(i).clone();

            ItemMeta meta =
                    item.getItemMeta();

            if (meta != null) {

                var lore =
                        meta.getLore();

                if (lore == null) {
                    lore =
                            new java.util.ArrayList<>();
                } else {
                    lore =
                            new java.util.ArrayList<>(
                                    lore
                            );
                }

                lore.add("");
                lore.add(
                        ChatColor.YELLOW
                                + "Click to select"
                );

                meta.setLore(lore);

                item.setItemMeta(meta);
            }

            inventory.setItem(
                    i,
                    item
            );
        }

        ItemStack keys =
                new ItemStack(
                        Material.TRIPWIRE_HOOK
                );

        ItemMeta keyMeta =
                keys.getItemMeta();

        keyMeta.setDisplayName(
                ChatColor.GOLD
                        + "Virtual Keys: "
                        + ChatColor.WHITE
                        + plugin.getKeyManager()
                        .getKeys(
                                player,
                                crate
                        )
        );

        keys.setItemMeta(keyMeta);

        inventory.setItem(
                49,
                keys
        );

        player.openInventory(inventory);
    }

    public static class CrateHolder
            implements org.bukkit.inventory.InventoryHolder {

        private final String crate;

        public CrateHolder(String crate) {
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
