package com.elyther.ecrates.manager;

import com.elyther.ecrates.ECrates;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TimerManager {

    private final ECrates plugin;

    private final File file;
    private final YamlConfiguration data;

    private int taskId = -1;

    public TimerManager(ECrates plugin) {

        this.plugin = plugin;

        file = new File(
                plugin.getDataFolder(),
                "timers.yml"
        );

        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data =
                YamlConfiguration
                        .loadConfiguration(file);
    }

    public void start() {

        if (!plugin.getConfig()
                .getBoolean(
                        "timer.enabled",
                        true
                )) {
            return;
        }

        taskId =
                Bukkit.getScheduler()
                        .runTaskTimer(
                                plugin,
                                this::tick,
                                20L,
                                20L
                        )
                        .getTaskId();
    }

    private void tick() {

        long now =
                System.currentTimeMillis();

        for (Player player :
                Bukkit.getOnlinePlayers()) {

            var section =
                    plugin.getConfig()
                            .getConfigurationSection(
                                    "crates"
                            );

            if (section == null) {
                continue;
            }

            for (String crate :
                    section.getKeys(false)) {

                checkTimer(
                        player,
                        crate,
                        now
                );
            }
        }
    }

    private void checkTimer(
            Player player,
            String crate,
            long now
    ) {

        UUID uuid =
                player.getUniqueId();

        String path =
                "players."
                        + uuid
                        + "."
                        + crate;

        long end =
                data.getLong(path, -1);

        if (end == -1) {

            startTimer(
                    player,
                    crate,
                    now
            );

            return;
        }

        if (end <= now) {

            plugin.getKeyManager()
                    .addKey(
                            player,
                            crate,
                            1
                    );

            player.sendMessage(
                    plugin.getMessageManager()
                            .get(
                                    player,
                                    "key-received",
                                    "%crate%",
                                    getCrateName(crate)
                            )
            );

            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP,
                    1f,
                    1.2f
            );

            startTimer(
                    player,
                    crate,
                    now
            );
        }
    }

    public void startTimer(
            Player player,
            String crate
    ) {

        startTimer(
                player,
                crate,
                System.currentTimeMillis()
        );
    }

    private void startTimer(
            Player player,
            String crate,
            long now
    ) {

        long seconds =
                plugin.getConfig()
                        .getLong(
                                "crates."
                                        + crate
                                        + ".seconds",
                                plugin.getConfig()
                                        .getLong(
                                                "timer.default-seconds",
                                                3600
                                        )
                        );

        long end =
                now + (seconds * 1000L);

        data.set(
                "players."
                        + player.getUniqueId()
                        + "."
                        + crate,
                end
        );

        save();
    }

    public long getRemainingSeconds(
            Player player,
            String crate
    ) {

        String path =
                "players."
                        + player.getUniqueId()
                        + "."
                        + crate;

        long end =
                data.getLong(path, -1);

        if (end == -1) {

            return plugin.getConfig()
                    .getLong(
                            "crates."
                                    + crate
                                    + ".seconds",
                            3600
                    );
        }

        long seconds =
                (end - System.currentTimeMillis())
                        / 1000L;

        return Math.max(
                0,
                seconds
        );
    }

    public String getFormatted(
            Player player,
            String crate
    ) {

        long seconds =
                getRemainingSeconds(
                        player,
                        crate
                );

        long minutes =
                seconds / 60;

        long secs =
                seconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                secs
        );
    }

    private String getCrateName(
            String crate
    ) {

        return plugin.getConfig()
                .getString(
                        "crates."
                                + crate
                                + ".display-name",
                        crate
                )
                .replace("&a", "")
                .replace("&b", "")
                .replace("&c", "")
                .replace("&d", "")
                .replace("&e", "")
                .replace("&f", "");
    }

    public void save() {

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {

        if (taskId != -1) {

            Bukkit.getScheduler()
                    .cancelTask(taskId);

            taskId = -1;
        }
    }
}
