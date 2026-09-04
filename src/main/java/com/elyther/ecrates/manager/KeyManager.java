package com.elyther.ecrates.manager;

import com.elyther.ecrates.ECrates;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class KeyManager {

    private final ECrates plugin;

    private final File file;
    private final YamlConfiguration data;

    public KeyManager(ECrates plugin) {

        this.plugin = plugin;

        file = new File(
                plugin.getDataFolder(),
                "keys.yml"
        );

        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data = YamlConfiguration.loadConfiguration(file);
    }

    public int getKeys(
            UUID uuid,
            String crate
    ) {

        return data.getInt(
                "players."
                        + uuid
                        + "."
                        + crate,
                0
        );
    }

    public int getKeys(
            Player player,
            String crate
    ) {

        return getKeys(
                player.getUniqueId(),
                crate
        );
    }

    public void addKey(
            Player player,
            String crate,
            int amount
    ) {

        if (amount <= 0) {
            return;
        }

        int current =
                getKeys(player, crate);

        data.set(
                "players."
                        + player.getUniqueId()
                        + "."
                        + crate,
                current + amount
        );

        save();
    }

    public boolean removeKey(
            Player player,
            String crate
    ) {

        int current =
                getKeys(player, crate);

        if (current <= 0) {
            return false;
        }

        data.set(
                "players."
                        + player.getUniqueId()
                        + "."
                        + crate,
                current - 1
        );

        save();

        return true;
    }

    public void save() {

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
