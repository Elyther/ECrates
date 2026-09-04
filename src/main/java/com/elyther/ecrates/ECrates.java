package com.elyther.ecrates;

import com.elyther.ecrates.command.CrateCommand;
import com.elyther.ecrates.listener.CrateListener;
import com.elyther.ecrates.listener.GUIListener;
import com.elyther.ecrates.manager.CrateManager;
import com.elyther.ecrates.manager.KeyManager;
import com.elyther.ecrates.manager.MessageManager;
import com.elyther.ecrates.manager.TimerManager;
import com.elyther.ecrates.placeholder.CratePlaceholder;
import org.bukkit.plugin.java.JavaPlugin;

public final class ECrates extends JavaPlugin {

    private static ECrates instance;

    private CrateManager crateManager;
    private KeyManager keyManager;
    private TimerManager timerManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);

        messageManager = new MessageManager(this);
        keyManager = new KeyManager(this);
        crateManager = new CrateManager(this);
        timerManager = new TimerManager(this);

        getCommand("crate").setExecutor(new CrateCommand(this));
        getCommand("crate").setTabCompleter(new CrateCommand(this));

        getServer().getPluginManager().registerEvents(
                new CrateListener(this), this
        );

        getServer().getPluginManager().registerEvents(
                new GUIListener(this), this
        );

        if (getServer().getPluginManager()
                .getPlugin("PlaceholderAPI") != null) {

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

        if (keyManager != null) {
            keyManager.save();
        }

        if (crateManager != null) {
            crateManager.save();
        }
    }

    public static ECrates getInstance() {
        return instance;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}
