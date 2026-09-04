package com.elyther.ecrates.listener;

import com.elyther.ecrates.ECrates;
import com.elyther.ecrates.gui.CrateEditorGUI;
import com.elyther.ecrates.gui.CrateGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private final ECrates plugin;

    private final Map<UUID, Selection> selections =
            new HashMap<>();

    public GUIListener(ECrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {
            return;
        }

        Inventory inventory =
                event.getInventory();

        if (inventory.getHolder()
                instanceof CrateGUI.CrateHolder holder) {

            event.setCancelled(true);

            if (event.getRawSlot() < 0 ||
                    event.getRawSlot() >= 45) {
                return;
            }

            String crate =
                    holder.getCrate();

            int slot =
                    event.getRawSlot();

            var rewards =
                    plugin.getCrateManager()
                            .getRewards(crate);

            if (slot >= rewards.size()) {
                return;
            }

            if (plugin.getKeyManager()
                    .getKeys(player, crate) <= 0) {

                player.closeInventory();

                player.sendMessage(
                        plugin.getMessageManager()
                                .get(
                                        player,
                                        "no-key"
                                )
                );

                return;
            }

            ItemStack reward =
                    rewards.get(slot).clone();

            selections.put(
                    player.getUniqueId(),
                    new Selection(
                            crate,
                            slot
                    )
            );

            openConfirm(
                    player,
                    crate,
                    reward
            );

            return;
        }

        if (inventory.getHolder()
                instanceof CrateEditorGUI.EditorHolder holder) {

            int slot =
                    event.getRawSlot();

            if (slot == 49 ||
                    slot == 53) {

                event.setCancelled(true);

                String crate =
                        holder.getCrate();

                if (slot == 49) {

                    CrateEditorGUI.save(
                            plugin,
                            player,
                            crate,
                            inventory
                    );

                    player.closeInventory();

                } else {

                    player.closeInventory();

                    player.sendMessage(
                            ChatColor.RED
                                    + "Changes cancelled."
                    );
                }
            }
        }

        if (inventory.getHolder()
                instanceof ConfirmHolder holder) {

            event.setCancelled(true);

            int slot =
                    event.getRawSlot();

            if (slot == 11) {

                confirmReward(
                        player,
                        holder.getCrate(),
                        holder.getRewardSlot()
                );

            } else if (slot == 15) {

                selections.remove(
                        player.getUniqueId()
                );

                player.closeInventory();

                player.sendMessage(
                        plugin.getMessageManager()
                                .get(
                                        player,
                                        "reward-cancelled"
                                )
                );
            }
        }
    }

    private void openConfirm(
            Player player,
            String crate,
            ItemStack reward
    ) {

        String title =
                ChatColor.DARK_GRAY
                        + "Confirm Reward";

        Inventory inventory =
                Bukkit.createInventory(
                        new ConfirmHolder(
                                crate,
                                selections.get(
                                        player.getUniqueId()
                                ).rewardSlot
                        ),
                        27,
                        title
                );

        inventory.setItem(
                13,
                reward
        );

        ItemStack confirm =
                new ItemStack(
                        Material.LIME_WOOL
                );

        ItemMeta confirmMeta =
                confirm.getItemMeta();

        confirmMeta.setDisplayName(
                ChatColor.GREEN
                        + "CONFIRM"
        );

        confirm.setItemMeta(confirmMeta);

        inventory.setItem(
                11,
                confirm
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
                15,
                cancel
        );

        player.openInventory(inventory);
    }

    private void confirmReward(
            Player player,
            String crate,
            int rewardSlot
    ) {

        if (plugin.getKeyManager()
                .getKeys(player, crate) <= 0) {

            player.closeInventory();

            player.sendMessage(
                    plugin.getMessageManager()
                            .get(
                                    player,
                                    "no-key"
                            )
            );

            selections.remove(
                    player.getUniqueId()
            );

            return;
        }

        var rewards =
                plugin.getCrateManager()
                        .getRewards(crate);

        if (rewardSlot < 0 ||
                rewardSlot >= rewards.size()) {

            player.closeInventory();
            return;
        }

        ItemStack reward =
                rewards.get(rewardSlot).clone();

        if (!hasInventorySpace(
                player,
                reward
        )) {

            player.sendMessage(
                    plugin.getMessageManager()
                            .get(
                                    player,
                                    "inventory-full"
                            )
            );

            return;
        }

        if (!plugin.getKeyManager()
                .removeKey(
                        player,
                        crate
                )) {
            return;
        }

        player.getInventory()
                .addItem(reward);

        player.closeInventory();

        String itemName =
                reward.getType()
                        .name();

        player.sendMessage(
                plugin.getMessageManager()
                        .get(
                                player,
                                "reward-received",
                                "%item%",
                                itemName,
                                "%amount%",
                                String.valueOf(
                                        reward.getAmount()
                                )
                        )
        );

        if (plugin.getConfig()
                .getBoolean(
                        "effects.enabled",
                        true
                )) {

            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP,
                    1f,
                    1.25f
            );

            player.getWorld()
                    .spawnParticle(
                            Particle.TOTEM_OF_UNDYING,
                            player.getLocation()
                                    .add(0, 1, 0),
                            40,
                            0.5,
                            0.8,
                            0.5,
                            0.1
                    );
        }

        selections.remove(
                player.getUniqueId()
        );
    }

    private boolean hasInventorySpace(
            Player player,
            ItemStack item
    ) {

        int remaining =
                item.getAmount();

        for (ItemStack current :
                player.getInventory()
                        .getStorageContents()) {

            if (current == null ||
                    current.getType()
                            == Material.AIR) {

                remaining -= item.getMaxStackSize();

            } else if (current.isSimilar(item)) {

                remaining -=
                        current.getMaxStackSize()
                                - current.getAmount();
            }

            if (remaining <= 0) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onClose(
            InventoryCloseEvent event
    ) {

        if (!(event.getPlayer()
                instanceof Player player)) {
            return;
        }

        if (event.getInventory().getHolder()
                instanceof ConfirmHolder) {

            selections.remove(
                    player.getUniqueId()
            );
        }
    }

    private static class Selection {

        private final String crate;
        private final int rewardSlot;

        private Selection(
                String crate,
                int rewardSlot
        ) {

            this.crate = crate;
            this.rewardSlot = rewardSlot;
        }
    }

    public static class ConfirmHolder
            implements org.bukkit.inventory.InventoryHolder {

        private final String crate;
        private final int rewardSlot;

        public ConfirmHolder(
                String crate,
                int rewardSlot
        ) {

            this.crate = crate;
            this.rewardSlot = rewardSlot;
        }

        public String getCrate() {
            return crate;
        }

        public int getRewardSlot() {
            return rewardSlot;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
