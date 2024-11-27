package com.restonic4.forgotten.compatibility.exordium;

import dev.tr7zw.exordium.ExordiumModBase;

public class Overrides {
    public static void override() {
        ExordiumModBase.instance.config.hotbarSettings.enabled = false;
        ExordiumModBase.instance.config.hotbarSettings.forceUpdates = true;

        ExordiumModBase.instance.config.healthSettings.enabled = false;
        ExordiumModBase.instance.config.healthSettings.forceUpdates = true;
    }
}
