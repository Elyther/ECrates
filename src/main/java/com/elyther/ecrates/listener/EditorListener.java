package com.elyther.ecrates.listener;

import com.elyther.ecrates.ECrates;
import com.elyther.ecrates.gui.CrateEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EditorListener implements Listener {

    private final ECrates plugin;

    public EditorListener(ECrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        String title =
                ChatColor.stripColor(
                        event.getView().getTitle()
                );

        for (String crate :
                plugin.getConfig()
                        .getConfigurationSection("crates")
                        .getKeys(false)) {

            if (title.equalsIgnoreCase(
                    crate + " Rewards"
            )) {

                CrateEditorGUI.save(
                        plugin,
                        player,
                        crate,
                        event.getInventory()
                );

                break;
            }
        }
    }
}
