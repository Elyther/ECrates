package com.elyther.ecrates.gui;

import com.elyther.ecrates.ECrates;
import com.elyther.ecrates.manager.KeyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CrateGUI {

    public static void open(
            ECrates plugin,
            Player player,
            String crate
    ) {

        String title = plugin.getConfig()
                .getString(
                        "gui.crate-title",
                        "&8%crate%"
                )
                .replace("%crate%", crate);

        title = ChatColor.translateAlternateColorCodes(
                '&',
                title
        );

        Inventory inv = Bukkit.createInventory(
                null,
                54,
                title
        );

        List<ItemStack> rewards =
                plugin.getCrateManager()
                        .getRewards(crate);

        int slot = 0;

        for (ItemStack item : rewards) {

            if (slot >= 45) {
                break;
            }

            inv.setItem(
                    slot++,
                    item.clone()
            );
        }

        ItemStack keyInfo =
                new ItemStack(Material.TRIPWIRE_HOOK);

        ItemMeta meta = keyInfo.getItemMeta();

        meta.setDisplayName(
                ChatColor.LIGHT_PURPLE +
                        "Your Keys"
        );

        keyInfo.setItemMeta(meta);

        inv.setItem(49, keyInfo);

        player.openInventory(inv);
    }
}
