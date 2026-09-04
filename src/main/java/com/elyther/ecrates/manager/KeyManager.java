package com.elyther.ecrates.manager;

import com.elyther.ecrates.ECrates;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class KeyManager {

    public static NamespacedKey KEY;

    public static void init(ECrates plugin) {
        KEY = new NamespacedKey(plugin, "crate_key");
    }

    public static boolean isKey(ItemStack item, String crate) {

        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        String value = item.getItemMeta()
                .getPersistentDataContainer()
                .get(KEY, PersistentDataType.STRING);

        return crate.equalsIgnoreCase(value);
    }

    public static boolean removeKey(Player player, String crate) {

        for (ItemStack item : player.getInventory().getContents()) {

            if (isKey(item, crate)) {

                if (item.getAmount() <= 1) {

                    player.getInventory().remove(item);

                } else {

                    item.setAmount(item.getAmount() - 1);
                }

                return true;
            }
        }

        return false;
    }
}
