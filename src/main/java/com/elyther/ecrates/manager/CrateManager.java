package com.elyther.ecrates.manager;

import com.elyther.ecrates.ECrates;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CrateManager {

    private final ECrates plugin;

    private final Map<String, List<ItemStack>> rewards = new HashMap<>();
    private final Map<String, Set<String>> locations = new HashMap<>();

    private final File file;
    private YamlConfiguration data;

    public CrateManager(ECrates plugin) {

        this.plugin = plugin;

        file = new File(
                plugin.getDataFolder(),
                "data.yml"
        );

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data = YamlConfiguration.loadConfiguration(file);

        load();
    }

    public void load() {

        rewards.clear();
        locations.clear();

        var section =
                plugin.getConfig()
                        .getConfigurationSection("crates");

        if (section == null) {
            return;
        }

        for (String crate : section.getKeys(false)) {

            List<ItemStack> items = new ArrayList<>();

            List<?> raw =
                    data.getList("rewards." + crate);

            if (raw != null) {

                for (Object object : raw) {

                    if (object instanceof ItemStack item) {
                        items.add(item.clone());
                    }
                }
            }

            rewards.put(crate, items);

            Set<String> crateLocations =
                    new HashSet<>();

            crateLocations.addAll(
                    data.getStringList(
                            "locations." + crate
                    )
            );

            locations.put(
                    crate,
                    crateLocations
            );
        }
    }

    public void save() {

        for (String crate : rewards.keySet()) {

            data.set(
                    "rewards." + crate,
                    rewards.get(crate)
            );

            data.set(
                    "locations." + crate,
                    new ArrayList<>(
                            locations.getOrDefault(
                                    crate,
                                    new HashSet<>()
                            )
                    )
            );
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {

        data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public boolean exists(String crate) {

        return plugin.getConfig()
                .isConfigurationSection(
                        "crates." + crate
                );
    }

    public List<ItemStack> getRewards(String crate) {

        return rewards.computeIfAbsent(
                crate,
                k -> new ArrayList<>()
        );
    }

    public void setRewards(
            String crate,
            List<ItemStack> items
    ) {

        rewards.put(
                crate,
                new ArrayList<>(items)
        );

        save();
    }

    public void addLocation(
            String crate,
            Location location
    ) {

        locations
                .computeIfAbsent(
                        crate,
                        k -> new HashSet<>()
                )
                .add(locationKey(location));

        save();
    }

    public void removeLocation(
            Location location
    ) {

        String key = locationKey(location);

        for (Set<String> set : locations.values()) {
            set.remove(key);
        }

        save();
    }

    public String getCrateAt(
            Location location
    ) {

        String key = locationKey(location);

        for (Map.Entry<String, Set<String>> entry :
                locations.entrySet()) {

            if (entry.getValue().contains(key)) {

                Material type =
                        location.getBlock().getType();

                if (isCrateBlock(type)) {
                    return entry.getKey();
                }

                return null;
            }
        }

        return null;
    }

    public static boolean isCrateBlock(
            Material material
    ) {

        if (material == Material.CHEST) {
            return true;
        }

        if (material == Material.ENDER_CHEST) {
            return true;
        }

        return material.name().endsWith("_SHULKER_BOX");
    }

    private String locationKey(
            Location location
    ) {

        return location.getWorld().getName()
                + ":"
                + location.getBlockX()
                + ":"
                + location.getBlockY()
                + ":"
                + location.getBlockZ();
    }
}
