package com.restonic4.forgotten.util;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.packets.FallStarPacket;
import com.restonic4.forgotten.networking.packets.SpawnStarPacket;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.saving.StarData;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class ServerShootingStarManager {
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

    public static void shootStar(MinecraftServer server) {
        JsonDataManager dataManager = Forgotten.getDataManager();

        if (!dataManager.contains("star")) {
            return;
        }

        ServerPlayer chosenPlayer = RandomUtil.getRandomFromList(server.getPlayerList().getPlayers());

        if (chosenPlayer != null) {
            BlockPos startCollisionPoint = getRandomPositionAroundPlayer(chosenPlayer, 200, 300);

            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                FallStarPacket.sendToClient(serverPlayer, startCollisionPoint);
            }
        }
    }

    public static BlockPos getRandomPositionAroundPlayer(ServerPlayer player, int minRadius, int maxRadius) {
        BlockPos playerPos = player.blockPosition();
        Level world = player.level();

        if (minRadius < 0 || maxRadius <= minRadius) {
            throw new IllegalArgumentException("Error.");
        }

        double angle = RandomUtil.getRandom().nextDouble() * Math.PI * 2;

        double distance = minRadius + (RandomUtil.getRandom().nextDouble() * (maxRadius - minRadius));

        int xOffset = (int) Math.round(Math.cos(angle) * distance);
        int zOffset = (int) Math.round(Math.sin(angle) * distance);

        int x = playerPos.getX() + xOffset;
        int z = playerPos.getZ() + zOffset;

        int y = world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);

        return new BlockPos(x, y, z);
    }

}
