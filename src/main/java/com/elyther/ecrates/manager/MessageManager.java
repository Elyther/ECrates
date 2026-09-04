package com.elyther.ecrates.manager;

import com.elyther.ecrates.ECrates;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class MessageManager {

    private final ECrates plugin;
    private YamlConfiguration messages;

    public MessageManager(ECrates plugin) {

        this.plugin = plugin;

        File file = new File(
                plugin.getDataFolder(),
                "messages.yml"
        );

        messages = YamlConfiguration.loadConfiguration(file);
    }

    public String get(Player player, String key) {

        String language = plugin.getConfig()
                .getString("language", "en");

        String text = messages.getString(
                language + "." + key,
                key
        );

        return ChatColor.translateAlternateColorCodes(
                '&',
                text
        );
    }

    public String get(String language, String key) {

        String text = messages.getString(
                language + "." + key,
                key
        );

        return ChatColor.translateAlternateColorCodes(
                '&',
                text
        );
    }
}
