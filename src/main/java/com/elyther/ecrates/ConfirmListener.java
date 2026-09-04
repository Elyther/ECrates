package com.elyther.ecrates.listener;

import com.elyther.ecrates.ECrates;
import com.elyther.ecrates.manager.KeyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmListener implements Listener {

    private final ECrates plugin;

    public ConfirmListener(ECrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title =
                ChatColor.stripColor(
                        event.getView().getTitle()
                );

        if (!title.contains("Crate")) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null) {
            return;
        }

        String crate = null;

        for (String id :
                plugin.getConfig()
                        .getConfigurationSection("crates")
                        .getKeys(false)) {

            if (title.equalsIgnoreCase(id + " Crate")) {
                crate = id;
                break;
            }
        }

        if (crate == null) {
            return;
        }

        int slot = event.getRawSlot();

        if (slot < 0 || slot >= 45) {
            return;
        }

        var rewards =
                plugin.getCrateManager()
                        .getRewards(crate);

        if (slot >= rewards.size()) {
            return;
        }

        ItemStack reward =
                rewards.get(slot).clone();

        openConfirm(
                player,
                crate,
                reward
        );
    }

    private void openConfirm(
            Player player,
            String crate,
            ItemStack reward
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        27,
                        ChatColor.DARK_GRAY +
                                "Confirm Reward"
                );

        inv.setItem(
                13,
                reward
        );

        ItemStack confirm =
                new ItemStack(Material.LIME_WOOL);

        ItemMeta confirmMeta =
                confirm.getItemMeta();

        confirmMeta.setDisplayName(
                ChatColor.GREEN +
                        "CONFIRM"
        );

        confirm.setItemMeta(confirmMeta);

        ItemStack cancel =
                new ItemStack(Material.RED_WOOL);

        ItemMeta cancelMeta =
                cancel.getItemMeta();

        cancelMeta.setDisplayName(
                ChatColor.RED +
                        "CANCEL"
        );

        cancel.setItemMeta(cancelMeta);

        inv.setItem(11, confirm);
        inv.setItem(15, cancel);

        player.openInventory(inv);

        player.setMetadata(
                "ecrates_selected",
                new org.bukkit.metadata.FixedMetadataValue(
                        plugin,
                        crate + "|" +
                                reward.serialize().hashCode()
                )
        );
    }
}
