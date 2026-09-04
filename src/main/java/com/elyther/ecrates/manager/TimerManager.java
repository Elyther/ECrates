package com.elyther.ecrates.manager;

import com.elyther.ecrates.ECrates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerManager {

    private final ECrates plugin;

    private final Map<UUID, Map<String, Long>> timers = new HashMap<>();

    private int task;

    public TimerManager(ECrates plugin) {
        this.plugin = plugin;
    }

    public void start() {

        task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {

                    for (Player player : Bukkit.getOnlinePlayers()) {

                        for (String crate :
                                plugin.getConfig()
                                        .getConfigurationSection("crates")
                                        .getKeys(false)) {

                            tick(player, crate);
                        }
                    }

                },
                20L,
                20L
        ).getTaskId();
    }

    private void tick(Player player, String crate) {

        long now = System.currentTimeMillis();

        Map<String, Long> playerTimers =
                timers.computeIfAbsent(
                        player.getUniqueId(),
                        k -> new HashMap<>()
                );

        long end;

        if (!playerTimers.containsKey(crate)) {

            long seconds = plugin.getConfig()
                    .getLong(
                            "crates." + crate + ".seconds",
                            plugin.getConfig().getLong("timer.default-seconds", 3600)
                    );

            end = now + (seconds * 1000L);

            playerTimers.put(crate, end);

            return;
        }

        end = playerTimers.get(crate);

        if (now >= end) {

            giveKey(player, crate);

            if (plugin.getConfig().getBoolean("timer.restart-after-key", true)) {

                long seconds = plugin.getConfig()
                        .getLong(
                                "crates." + crate + ".seconds",
                                plugin.getConfig().getLong("timer.default-seconds", 3600)
                        );

                playerTimers.put(
                        crate,
                        now + seconds * 1000L
                );

            } else {

                playerTimers.remove(crate);
            }
        }
    }

    public long getRemainingSeconds(Player player, String crate) {

        Map<String, Long> map = timers.get(player.getUniqueId());

        if (map == null || !map.containsKey(crate)) {

            return plugin.getConfig()
                    .getLong(
                            "crates." + crate + ".seconds",
                            3600
                    );
        }

        long remaining =
                (map.get(crate) - System.currentTimeMillis()) / 1000L;

        return Math.max(0, remaining);
    }

    public String getFormatted(Player player, String crate) {

        long seconds = getRemainingSeconds(player, crate);

        long minutes = seconds / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d", minutes, secs);
    }

    private void giveKey(Player player, String crate) {

        String keyName = plugin.getConfig()
                .getString(
                        "crates." + crate + ".key-name",
                        "&f" + crate + " Key"
                );

        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);

        var meta = key.getItemMeta();

        meta.setDisplayName(
                org.bukkit.ChatColor.translateAlternateColorCodes(
                        '&',
                        keyName
                )
        );

        meta.getPersistentDataContainer().set(
                KeyManager.KEY,
                org.bukkit.persistence.PersistentDataType.STRING,
                crate
        );

        key.setItemMeta(meta);

        player.getInventory().addItem(key);

        player.playSound(
                player.getLocation(),
                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                1f,
                1.2f
        );
    }

    public void save() {

        // Timerlər runtime-da saxlanılır.
        // Server restart üçün növbəti versiyada persistent timer də əlavə edə bilərik.
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(task);
    }
}
