package com.restonic4.forgotten.util;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.common.ForgottenBlocks;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import com.restonic4.forgotten.util.helpers.SimpleEffectHelper;
import io.github.fabricators_of_create.porting_lib.event.common.ExplosionEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GriefingPrevention {
    public static final float MAX_DISTANCE_FROM_EFFECT = 100;

    public static Map<SafeBlockPos, BlockState> originalBlocks = new HashMap<>();

    public static void register() {

    }

    public static boolean isInProtectedArea(BlockPos blockPos) {
        return isInMainTempleRange(blockPos);
    }

    public static boolean isInMainTempleRange(BlockPos blockPos) {
        JsonDataManager dataManager = Forgotten.getDataManager();
        BlockPos pos = dataManager.getBlockPos("center");

        if (pos != null) {
            Vec3 blockPosVec = blockPos.getCenter();
            double distance = blockPosVec.distanceTo(pos.getCenter());

            return distance <= 120;
        }

        return false;
    }

    public static BlockState getOriginalBlockAndRegister(BlockPos blockPos, BlockState blockStateFallBack) {
        if (originalBlocks.containsKey(new SafeBlockPos(blockPos))) {
            return originalBlocks.get(new SafeBlockPos(blockPos));
        } else {
            originalBlocks.put(new SafeBlockPos(blockPos), blockStateFallBack);
            return blockStateFallBack;
        }
    }

    public static void onBlockModifiedInMainTemple(Level level, BlockState newBlockstate, BlockState originalBlockState, BlockPos blockPos) {
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;

            if (originalBlockState.getBlock() == Blocks.AIR && newBlockstate.getBlock() == ForgottenBlocks.ALTAR) {
                return;
            }

            new Thread(() -> {
                try {
                    int randomMilliseconds;

                    if (newBlockstate.getBlock() instanceof FireBlock) {
                        randomMilliseconds = RandomUtil.randomBetween(100, 250);
                    } else if (originalBlockState.getBlock() instanceof VineBlock || originalBlockState.getBlock() instanceof BushBlock) {
                        randomMilliseconds = RandomUtil.randomBetween(250, 1000);
                    } else {
                        randomMilliseconds = RandomUtil.randomBetween(2000, 8000);
                    }

                    sendEffectsToNearbyPlayers(serverLevel, randomMilliseconds, blockPos);

                    Thread.sleep(randomMilliseconds);
                } catch (Exception ignored) {}

                serverLevel.getServer().execute(() -> {
                    if (shouldDropBlock(originalBlockState)) {
                        //dropResources(newBlockstate, serverLevel, blockPos);
                    }

                    SimpleEffectHelper.invalidHeadPlacement(serverLevel, blockPos);
                    serverLevel.setBlockAndUpdate(blockPos, originalBlockState);
                });
            }).start();
        }
    }

    public static void sendEffectsToNearbyPlayers(ServerLevel serverLevel, int timeToRecover, BlockPos blockPos) {
        for (ServerPlayer serverPlayer : serverLevel.players()) {
            if (serverPlayer.position().distanceTo(blockPos.getCenter()) <= MAX_DISTANCE_FROM_EFFECT) {
                FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                friendlyByteBuf.writeBlockPos(blockPos);
                friendlyByteBuf.writeInt(timeToRecover);
                ServerPlayNetworking.send(serverPlayer, PacketManager.BLOCK_CANCELLATION_EFFECTS, friendlyByteBuf);
            }
        }
    }

    public static boolean shouldDropBlock(BlockState originalBlockState) {
        return originalBlockState.getBlock() == Blocks.AIR || originalBlockState.getBlock() == Blocks.CAVE_AIR;
    }

    public static void dropResources(BlockState blockState, Level level, BlockPos blockPos) {
        if (level instanceof ServerLevel) {
            Block.getDrops(blockState, (ServerLevel)level, blockPos, null).forEach(itemStack -> popResource(level, blockPos, itemStack));
            blockState.spawnAfterBreak((ServerLevel)level, blockPos, ItemStack.EMPTY, true);
        }
    }

    public static void popResource(Level level, BlockPos blockPos, ItemStack itemStack) {
        double d = (double) EntityType.ITEM.getHeight() / 2.0;
        double e = (double)blockPos.getX() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
        double f = (double)blockPos.getY() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25) - d;
        double g = (double)blockPos.getZ() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
        popResource(level, () -> new ItemEntity(level, e, f, g, itemStack), itemStack);
    }

    private static void popResource(Level level, Supplier<ItemEntity> supplier, ItemStack itemStack) {
        if (!level.isClientSide && !itemStack.isEmpty() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ItemEntity itemEntity = (ItemEntity)supplier.get();
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }
}
