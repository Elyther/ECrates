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

    private File file;
    private YamlConfiguration data;

    public CrateManager(ECrates plugin) {

        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), "data.yml");

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

        for (String crate : plugin.getConfig().getConfigurationSection("crates").getKeys(false)) {

            List<ItemStack> list = new ArrayList<>();

            if (data.contains("rewards." + crate)) {

                List<?> raw = data.getList("rewards." + crate);

                if (raw != null) {
                    for (Object obj : raw) {
                        if (obj instanceof ItemStack item) {
                            list.add(item);
                        }
                    }
                }
            }

            rewards.put(crate, list);

            Set<String> locs = new HashSet<>();

            if (data.contains("locations." + crate)) {
                locs.addAll(data.getStringList("locations." + crate));
            }

            locations.put(crate, locs);
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
                    new ArrayList<>(locations.getOrDefault(crate, new HashSet<>()))
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

    public List<ItemStack> getRewards(String crate) {
        return rewards.computeIfAbsent(crate, k -> new ArrayList<>());
    }

    public void setRewards(String crate, List<ItemStack> items) {
        rewards.put(crate, new ArrayList<>(items));
        save();
    }

    public void addLocation(String crate, Location location) {

        locations.computeIfAbsent(crate, k -> new HashSet<>())
                .add(locationKey(location));

        save();
    }

    public void removeLocation(Location location) {

        String key = locationKey(location);

        for (Set<String> set : locations.values()) {
            set.remove(key);
        }

        save();
    }

    public String getCrateAt(Location location) {

        String key = locationKey(location);

        for (Map.Entry<String, Set<String>> entry : locations.entrySet()) {

            if (entry.getValue().contains(key)) {
                return entry.getKey();
            }
        }

        return null;
    }

    private String locationKey(Location location) {

        return location.getWorld().getName()
                + ":" + location.getBlockX()
                + ":" + location.getBlockY()
                + ":" + location.getBlockZ();
    }

    public boolean exists(String crate) {
        return plugin.getConfig().isConfigurationSection("crates." + crate);
    }
}
