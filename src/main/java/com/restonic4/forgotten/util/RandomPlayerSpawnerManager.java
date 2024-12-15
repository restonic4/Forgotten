package com.restonic4.forgotten.util;

import com.restonic4.forgotten.commdands.RandomTP;
import com.restonic4.forgotten.saving.SaveManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.border.WorldBorder;

import java.util.Random;
import java.util.UUID;

public class RandomPlayerSpawnerManager {
    public static final int INVALID_HEIGHT = -100;
    public static final int MAX_TRIES = 100;


    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RandomTP.register(dispatcher);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            spawnPlayer(newPlayer);
        });
    }

    public static void spawnPlayer(ServerPlayer player) {
        if (player.getRespawnPosition() != null) {
            System.out.println(player.getDisplayName() + " has a vanilla respawn point");
            return;
        }

        SaveManager saveManager = SaveManager.getInstance(player.getServer());
        UUID uuid = player.getGameProfile().getId();
        String uuidString = uuid.toString();

        if (saveManager.containsKey(uuidString)) {
            BlockPos spawnPoint = saveManager.get(uuidString, BlockPos.class);
            player.teleportTo(player.getServer().overworld(), spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), player.getYRot(), player.getXRot());
        } else {
            forceSpawnRandomly(player);
        }
    }

    public static void forceSpawnRandomly(ServerPlayer player) {
        boolean couldSpawn = false;
        int currentTry = 0;

        while (!couldSpawn && currentTry < MAX_TRIES) {
            currentTry++;
            couldSpawn = spawnRandomly(player);
        }

        if (currentTry >= MAX_TRIES) {
            System.out.println("Max tries reached for " + player.getName());
        }
    }

    public static boolean spawnRandomly(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        WorldBorder worldBorder = level.getWorldBorder();
        Random random = new Random();

        double borderSize = worldBorder.getSize() / 2.0;
        BlockPos center = new BlockPos(((int) worldBorder.getCenterX()), 0, ((int) worldBorder.getCenterZ()));

        int margin = (int) Math.max(0, (borderSize - 1000) / 1000) * 100;

        double min = margin - borderSize;
        double max = borderSize - margin;

        if (min > max) {
            min = -borderSize;
            max = borderSize;
        }

        int x = (int) (center.getX() + random.nextDouble() * (max - min) + min);
        int z = (int) (center.getZ() + random.nextDouble() * (max - min) + min);

        int y = findValidY(level, x, z);

        if (y != INVALID_HEIGHT) {
            player.teleportTo(level, x + 0.5, y, z + 0.5, player.getYRot(), player.getXRot());

            SaveManager saveManager = SaveManager.getInstance(player.getServer());
            UUID uuid = player.getGameProfile().getId();

            saveManager.save(uuid.toString(), new BlockPos(x, y, z));

            return true;
        }

        System.out.println("Could not spawn at " + x + ", ?, " + z);

        return false;
    }

    private static int findValidY(Level level, int x, int z) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, level.getMaxBuildHeight(), z);

        while (mutablePos.getY() > level.getMinBuildHeight()) {
            mutablePos.move(0, -1, 0);

            if (isEmpty(level, mutablePos) && isEmpty(level, mutablePos.above()) &&
                    level.getBlockState(mutablePos.below()).isSolidRender(level, mutablePos.below())) {
                return mutablePos.getY() >= 60 ? mutablePos.getY() : INVALID_HEIGHT;
            }
        }
        return INVALID_HEIGHT;
    }

    private static boolean isEmpty(Level level, BlockPos blockPos) {
        return (level.getBlockState(blockPos).isAir() || !level.getBlockState(blockPos).isSolidRender(level, blockPos)) && !(level.getBlockState(blockPos).getBlock() instanceof LiquidBlock);
    }
}
