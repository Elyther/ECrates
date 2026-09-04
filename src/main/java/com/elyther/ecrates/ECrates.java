package com.elyther.ecrates;

import com.elyther.ecrates.command.CrateCommand;
import com.elyther.ecrates.listener.CrateListener;
import com.elyther.ecrates.listener.EditorListener;
import com.elyther.ecrates.listener.ConfirmListener;
import com.elyther.ecrates.placeholder.CratePlaceholder;
import com.elyther.ecrates.manager.CrateManager;
import com.elyther.ecrates.manager.TimerManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ECrates extends JavaPlugin {

    private static ECrates instance;

    private CrateManager crateManager;
    private TimerManager timerManager;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        saveResource("messages.yml", false);

        crateManager = new CrateManager(this);
        timerManager = new TimerManager(this);

        getCommand("crate").setExecutor(new CrateCommand(this));

        getServer().getPluginManager().registerEvents(
                new CrateListener(this), this
        );

        getServer().getPluginManager().registerEvents(
                new EditorListener(this), this
        );

        getServer().getPluginManager().registerEvents(
                new ConfirmListener(this), this
        );

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CratePlaceholder(this).register();
            getLogger().info("PlaceholderAPI hooked.");
        }

        timerManager.start();

        getLogger().info("ECrates enabled.");
    }

    @Override
    public void onDisable() {

        if (timerManager != null) {
            timerManager.save();
        }

        if (crateManager != null) {
            crateManager.save();
        }

        getLogger().info("ECrates disabled.");
    }

    public static ECrates getInstance() {
        return instance;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }
}
