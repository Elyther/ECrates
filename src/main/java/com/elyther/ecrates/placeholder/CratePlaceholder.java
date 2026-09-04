package com.elyther.ecrates.placeholder;

import com.elyther.ecrates.ECrates;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class CratePlaceholder extends PlaceholderExpansion {

    private final ECrates plugin;

    public CratePlaceholder(ECrates plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "ecrates";
    }

    @Override
    public String getAuthor() {
        return "Elyther";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(
            Player player,
            String params
    ) {

        if (params.startsWith("time_")) {

            String crate =
                    params.substring(5);

            if (!plugin.getCrateManager()
                    .exists(crate)) {

                return "00:00";
            }

            return plugin.getTimerManager()
                    .getFormatted(
                            player,
                            crate
                    );
        }

        if (params.startsWith("seconds_")) {

            String crate =
                    params.substring(8);

            return String.valueOf(
                    plugin.getTimerManager()
                            .getRemainingSeconds(
                                    player,
                                    crate
                            )
            );
        }

        return null;
    }
}
