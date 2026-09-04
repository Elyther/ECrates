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

public class GUIListener implements Listener {

    private final ECrates plugin;

    public GUIListener(ECrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = ChatColor.stripColor(event.getView().getTitle());

        if (title == null) {
            return;
        }

        /*
         * CONFIRM GUI
         */
        String confirmTitle = ChatColor.stripColor(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        plugin.getConfig().getString(
                                "gui.confirm-title",
                                "&8&lConfirm Reward"
                        )
                )
        );

        if (title.equalsIgnoreCase(confirmTitle)) {

            event.setCancelled(true);

            if (event.getRawSlot() == 11) {
                confirmReward(player);
                return;
            }

            if (event.getRawSlot() == 15) {
                player.removeMetadata("ecrates_selected", plugin);
                player.closeInventory();

                player.sendMessage(
                        ChatColor.RED + "Reward selection cancelled."
                );
            }

            return;
        }

        /*
         * CRATE GUI
         *
         * We only block players from moving the GUI items.
         * Reward selection itself is handled by ConfirmListener.
         */
        if (isCrateGUI(title)) {

            event.setCancelled(true);

            if (event.getRawSlot() < 0 || event.getRawSlot() >= 45) {
                return;
            }

            if (event.getCurrentItem() == null ||
                    event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            return;
        }

        /*
         * EDITOR GUI
         *
         * The existing EditorListener handles saving.
         * We only prevent items from being moved into the player's
         * inventory when clicking outside the editor area.
         */
        if (isEditorGUI(title)) {

            int rawSlot = event.getRawSlot();

            if (rawSlot >= 45) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isCrateGUI(String title) {

        if (plugin.getConfig().getConfigurationSection("crates") == null) {
            return false;
        }

        String template = ChatColor.stripColor(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        plugin.getConfig().getString(
                                "gui.crate-title",
                                "&8&l%crate% Crate"
                        )
                )
        );

        for (String crate :
                plugin.getConfig()
                        .getConfigurationSection("crates")
                        .getKeys(false)) {

            String crateTitle = template.replace("%crate%", crate);

            if (title.equalsIgnoreCase(crateTitle)) {
                return true;
            }
        }

        return false;
    }

    private boolean isEditorGUI(String title) {

        if (plugin.getConfig().getConfigurationSection("crates") == null) {
            return false;
        }

        String template = ChatColor.stripColor(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        plugin.getConfig().getString(
                                "gui.editor-title",
                                "&8&lEdit Rewards: %crate%"
                        )
                )
        );

        for (String crate :
                plugin.getConfig()
                        .getConfigurationSection("crates")
                        .getKeys(false)) {

            String editorTitle = template.replace("%crate%", crate);

            if (title.equalsIgnoreCase(editorTitle)) {
                return true;
            }
        }

        return false;
    }

    private void confirmReward(Player player) {

        if (!player.hasMetadata("ecrates_selected")) {
            player.closeInventory();
            return;
        }

        String data = player.getMetadata("ecrates_selected")
                .get(0)
                .asString();

        String[] parts = data.split("\\|", 2);

        if (parts.length != 2) {
            player.removeMetadata("ecrates_selected", plugin);
            player.closeInventory();
            return;
        }

        String crate = parts[0];

        /*
         * Current KeyManager uses physical crate keys.
         */
        if (!KeyManager.removeKey(player, crate)) {

            player.removeMetadata("ecrates_selected", plugin);
            player.closeInventory();

            player.sendMessage(
                    ChatColor.RED +
                            "You don't have a key for this crate."
            );

            return;
        }

        /*
         * Find reward by the saved hash.
         */
        ItemStack reward = findReward(crate, parts[1]);

        if (reward == null) {

            player.removeMetadata("ecrates_selected", plugin);
            player.closeInventory();

            player.sendMessage(
                    ChatColor.RED +
                            "This reward is no longer available."
            );

            return;
        }

        if (!hasInventorySpace(player, reward)) {

            /*
             * Give the key back because the player cannot receive
             * the reward.
             */
            giveKeyBack(player, crate);

            player.removeMetadata("ecrates_selected", plugin);
            player.closeInventory();

            player.sendMessage(
                    ChatColor.RED +
                            "Your inventory is full."
            );

            return;
        }

        player.getInventory().addItem(reward.clone());

        player.removeMetadata("ecrates_selected", plugin);
        player.closeInventory();

        String itemName = reward.getType().name().toLowerCase()
                .replace("_", " ");

        player.sendMessage(
                ChatColor.GREEN +
                        "You received " +
                        reward.getAmount() +
                        "x " +
                        itemName +
                        "!"
        );
    }

    private ItemStack findReward(String crate, String hash) {

        for (ItemStack reward :
                plugin.getCrateManager().getRewards(crate)) {

            if (reward == null ||
                    reward.getType() == Material.AIR) {
                continue;
            }

            if (String.valueOf(reward.serialize().hashCode())
                    .equals(hash)) {

                return reward.clone();
            }
        }

        return null;
    }

    private boolean hasInventorySpace(Player player, ItemStack item) {

        int amount = item.getAmount();

        for (ItemStack slot :
                player.getInventory().getStorageContents()) {

            if (slot == null ||
                    slot.getType() == Material.AIR) {

                return true;
            }

            if (slot.isSimilar(item)) {

                int max = Math.min(
                        slot.getMaxStackSize(),
                        item.getMaxStackSize()
                );

                if (slot.getAmount() + amount <= max) {
                    return true;
                }
            }
        }

        return false;
    }

    private void giveKeyBack(Player player, String crate) {

        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);

        ItemMeta meta = key.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(
                    ChatColor.LIGHT_PURPLE +
                            crate +
                            " Key"
            );

            key.setItemMeta(meta);
        }

        player.getInventory().addItem(key);
    }
}
