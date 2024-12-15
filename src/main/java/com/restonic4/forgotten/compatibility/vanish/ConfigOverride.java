package com.restonic4.forgotten.compatibility.vanish;

import me.drex.vanish.config.ConfigManager;
import me.drex.vanish.config.VanishConfig;

public class ConfigOverride {
    public static void override() {
        VanishConfig vanishConfig = ConfigManager.vanish();
        vanishConfig.sendJoinDisconnectMessage = false;
        vanishConfig.invulnerable = true;
        vanishConfig.actionBar = false;
    }
}
