package com.elyther.ecrates.listener;

import com.elyther.ecrates.ECrates;
import com.elyther.ecrates.gui.CrateGUI;
import com.elyther.ecrates.manager.KeyManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        String crate =
                plugin.getCrateManager()
                        .getCrateAt(
                                event.getClickedBlock().getLocation()
                        );

        if (crate == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        if (!hasKey(player, crate)) {

            player.sendMessage(
                    ChatColor.RED +
                            "You don't have a key for this crate."
            );

            return;
        }

        CrateGUI.open(
                plugin,
                player,
                crate
        );
    }

    private boolean hasKey(Player player, String crate) {

        for (var item :
                player.getInventory().getContents()) {

            if (KeyManager.isKey(item, crate)) {
                return true;
            }
        }

        return false;
    }
}
