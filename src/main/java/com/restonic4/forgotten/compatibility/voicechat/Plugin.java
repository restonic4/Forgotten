package com.restonic4.forgotten.compatibility.voicechat;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.DeathUtils;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.*;
import me.drex.vanish.util.VanishManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class Plugin implements VoicechatPlugin {
    @Nullable
    public static VoicechatServerApi api;

    @Override
    public String getPluginId() {
        return "forgotten";
    }

    @Override
    public void initialize(VoicechatApi api) {
        System.out.println("Plugin started");
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(JoinGroupEvent.class, this::joinGroup);
        registration.registerEvent(CreateGroupEvent.class, this::createGroup);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        api = event.getVoicechat();
    }

    private void joinGroup(JoinGroupEvent joinGroupEvent) {
        if (DeathUtils.isDeath() || (joinGroupEvent.getConnection() != null && Forgotten.isVanishLoaded() && VanishManager.isVanished((Entity) joinGroupEvent.getConnection().getPlayer().getEntity()))) {
            joinGroupEvent.cancel();
        }
    }

    private void createGroup(CreateGroupEvent createGroupEvent) {
        if (DeathUtils.isDeath() || (createGroupEvent.getConnection() != null && Forgotten.isVanishLoaded() && VanishManager.isVanished((Entity) createGroupEvent.getConnection().getPlayer().getEntity()))) {
            createGroupEvent.cancel();
        }
    }

    public static void leaveGroup(ServerPlayer serverPlayer) {
        if (api == null) {
            return;
        }

        VoicechatConnection connection = api.getConnectionOf(serverPlayer.getUUID());

        if (connection == null) {
            return;
        }

        connection.setGroup(null);
    }
}
