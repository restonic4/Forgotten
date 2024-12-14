package com.restonic4.forgotten.util;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.packets.FallStarPacket;
import com.restonic4.forgotten.networking.packets.SpawnStarPacket;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import com.restonic4.forgotten.saving.SaveManager;
import com.restonic4.forgotten.saving.StarData;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class ServerShootingStarManager {
    public static void spawn(MinecraftServer server) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            StarData starData = new StarData(
                    RandomUtil.randomBetween(3, 6),
                    RandomUtil.randomBetween(0, 365),
                    RandomUtil.randomBetween(-200, 200),
                    100,
                    RandomUtil.randomBetween(-200, 200)
            );

            SaveManager.getInstance(server).save("star", starData);

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

    public static void loadStarToClient(ServerPlayer serverPlayer) {
        SaveManager saveManager = SaveManager.getInstance(serverPlayer.server);

        if (saveManager.containsKey("star")) {
            StarData starData = saveManager.get("star", StarData.class);

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

    public static void shootStar(MinecraftServer server) {
        SaveManager saveManager = SaveManager.getInstance(server);

        if (!saveManager.containsKey("star")) {
            return;
        }

        ServerPlayer chosenPlayer = RandomUtil.getRandomFromList(server.getPlayerList().getPlayers());

        if (chosenPlayer != null) {
            BlockPos startCollisionPoint = getRandomPositionAroundPlayer(chosenPlayer, 200, 300);

            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                FallStarPacket.sendToClient(serverPlayer, startCollisionPoint);
            }

            saveManager.delete("star");

            new Thread(() -> {
                try {
                    Thread.sleep(FallStarPacket.ANIMATION_TIME);
                } catch (Exception ignored) {}

                spawnStarItemInLevel(chosenPlayer.serverLevel(), startCollisionPoint);
            }).start();
        }
    }

    public static void spawnStarItemInLevel(ServerLevel serverLevel, BlockPos blockPos) {
        serverLevel.getServer().execute(() -> {
            ItemStack playerSoulItem = new ItemStack(ForgottenItems.ETHEREAL_SHARD);

            System.out.println("Ethereal shard spawned at (" + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + ")");

            ItemEntity droppedItem = new ItemEntity(
                    serverLevel,
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ(),
                    playerSoulItem
            );

            droppedItem.setDefaultPickUpDelay();

            serverLevel.addFreshEntity(droppedItem);
        });
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

        ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
        boolean wasChunkLoaded = world.isLoaded(new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ()));
        ServerChunkCache serverChunkCache = (ServerChunkCache) world.getChunkSource();

        if (!wasChunkLoaded) {
            serverChunkCache.addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, player.getId());
        }

        world.getChunk(chunkPos.x, chunkPos.z);

        int y = world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);

        if (!wasChunkLoaded) {
            serverChunkCache.removeRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, player.getId());
        }

        return new BlockPos(x, y, z);
    }

}
