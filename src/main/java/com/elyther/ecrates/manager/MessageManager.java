package com.elyther.ecrates.manager;

import com.elyther.ecrates.ECrates;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {

    private final ECrates plugin;

    private YamlConfiguration messages;

    public MessageManager(ECrates plugin) {

        this.plugin = plugin;

        File file =
                new File(
                        plugin.getDataFolder(),
                        "messages.yml"
                );

        messages =
                YamlConfiguration
                        .loadConfiguration(file);
    }

    public String get(
            Player player,
            String key
    ) {

        String language =
                plugin.getConfig()
                        .getString(
                                "language",
                                "en"
                        );

        String message =
                messages.getString(
                        language + "." + key,
                        key
                );

        return color(message);
    }

    public String get(
            Player player,
            String key,
            String... replacements
    ) {

        String message =
                get(player, key);

        for (int i = 0;
             i + 1 < replacements.length;
             i += 2) {

            message =
                    message.replace(
                            replacements[i],
                            replacements[i + 1]
                    );
        }

        return message;
    }

    private String color(String text) {

        if (text == null) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes(
                '&',
                text
        );
    }
}
