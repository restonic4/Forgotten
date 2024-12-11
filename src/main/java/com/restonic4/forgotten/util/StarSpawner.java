package com.restonic4.forgotten.util;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.packets.SpawnStarPacket;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.saving.StarData;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class StarSpawner {
    public static void spawn(MinecraftServer server) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            JsonDataManager dataManager = Forgotten.getDataManager();

            StarData starData = new StarData(
                    RandomUtil.randomBetween(3, 6),
                    RandomUtil.randomBetween(0, 365),
                    RandomUtil.randomBetween(-200, 200),
                    100,
                    RandomUtil.randomBetween(-200, 200)
            );

            dataManager.save("star", starData);

            SpawnStarPacket.sendToClient(
                    serverPlayer,
                    starData.getSize(),
                    starData.getRotation(),
                    starData.getX(),
                    starData.getY(),
                    starData.getZ()
            );
        }
    }

    public static void load(MinecraftServer server) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            JsonDataManager dataManager = Forgotten.getDataManager();

            if (dataManager.contains("star")) {
                StarData starData = dataManager.getStarData("star");

                SpawnStarPacket.sendToClient(
                        serverPlayer,
                        starData.getSize(),
                        starData.getRotation(),
                        starData.getX(),
                        starData.getY(),
                        starData.getZ()
                );
            }
        }
    }
}
