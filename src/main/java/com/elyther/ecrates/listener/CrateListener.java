package com.elyther.ecrates.listener;

import com.elyther.ecrates.ECrates;
import com.elyther.ecrates.gui.CrateGUI;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CrateListener implements Listener {

    private final ECrates plugin;

    public CrateListener(ECrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(
            PlayerInteractEvent event
    ) {

        if (event.getAction()
                != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        String crate =
                plugin.getCrateManager()
                        .getCrateAt(
                                event.getClickedBlock()
                                        .getLocation()
                        );

        if (crate == null) {
            return;
        }

        event.setCancelled(true);

        if (plugin.getKeyManager()
                .getKeys(
                        event.getPlayer(),
                        crate
                ) <= 0) {

            event.getPlayer().sendMessage(
                    plugin.getMessageManager()
                            .get(
                                    event.getPlayer(),
                                    "no-key"
                            )
            );

            return;
        }

        CrateGUI.open(
                plugin,
                event.getPlayer(),
                crate
        );
    }
}
