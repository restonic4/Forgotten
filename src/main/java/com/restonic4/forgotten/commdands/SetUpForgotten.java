package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.CoreEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.registries.common.ForgottenEntities;
import com.restonic4.forgotten.saving.SaveManager;
import com.restonic4.forgotten.util.ServerCache;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class SetUpForgotten {
    private static int currentIndex = 0;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("setup_forgotten")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(SetUpForgotten::execute))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

        ServerLevel serverLevel = source.getLevel();

        SaveManager saveManager = SaveManager.getInstance(serverLevel.getServer());
        saveManager.save("center", pos);
        saveManager.save("SmallCoresDefeated", 0);
        saveManager.save("MainCoreFallAnimation", false);
        saveManager.save("Hardcore", false);

        Forgotten.resetCoreAnimation(serverLevel.getServer());

        try {
            generateSmallCore(serverLevel, new BlockPos(pos).offset(42, -2, 42));
            generateChain(serverLevel, new BlockPos(pos).offset(1, 22, 0), 56, new Vec3(1, 0, 0));

            generateSmallCore(serverLevel, new BlockPos(pos).offset(42, -2, -42));
            generateChain(serverLevel, new BlockPos(pos).offset(0, 22, -1), 56, new Vec3(0, 0, -1));

            generateSmallCore(serverLevel, new BlockPos(pos).offset(-42, -2, 42));
            generateChain(serverLevel, new BlockPos(pos).offset(-1, 22, 0), 56, new Vec3(-1, 0, 0));

            generateSmallCore(serverLevel, new BlockPos(pos).offset(-42, -2, -42));
            generateChain(serverLevel, new BlockPos(pos).offset(0, 22, 1), 56, new Vec3(0, 0, 1));

            generateBigCore(serverLevel, new BlockPos(pos).offset(0, 21, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    private static void generateSmallCore(ServerLevel serverLevel, BlockPos position) {
        SmallCoreEntity entity = new SmallCoreEntity(ForgottenEntities.SMALL_CORE, serverLevel);
        entity.setIndex(currentIndex);

        currentIndex++;

        serverLevel.addFreshEntity(entity);
        entity.setPos(position.getCenter());

        generateChain(serverLevel, position.offset(0, -4, 0), 22, new Vec3(0, 1, 0));
    }

    private static void generateBigCore(ServerLevel serverLevel, BlockPos position) {
        CoreEntity entity = new CoreEntity(ForgottenEntities.CORE, serverLevel);
        serverLevel.addFreshEntity(entity);
        entity.setPos(position.getCenter());
    }

    // This is complete garbage
    private static void generateChain(ServerLevel serverLevel, BlockPos position, int length, Vec3 direction) {
        for (int i = 0; i < length; i++) {
            if (direction.x == 1) {
                generateChainPiece(serverLevel, new BlockPos(position).getCenter().add(((float) i/1.75f), -0.5f, 0), false, (i%2 == 0), true);
            } else if (direction.x == -1) {
                generateChainPiece(serverLevel, new BlockPos(position).getCenter().add(-((float) i/1.75f), -0.5f, 0), false, (i%2 == 0), true);
            } else if (direction.y == 1) {
                generateChainPiece(serverLevel, new BlockPos(position).getCenter().add(0, ((float) i/1.75f) - 0.5f, 0), true, (i%2 == 0), false);
            } else if (direction.y == -1) {
                generateChainPiece(serverLevel, new BlockPos(position).getCenter().add(0, -((float) i/1.75f) - 0.5f, 0), true, (i%2 == 0), false);
            } else if (direction.z == 1) {
                generateChainPiece(serverLevel, new BlockPos(position).getCenter().add(0, -0.5f, ((float) i/1.75f)), false, (i%2 == 0), false);
            } else if (direction.z == -1) {
                generateChainPiece(serverLevel, new BlockPos(position).getCenter().add(0, -0.5f, -((float) i/1.75f)), false, (i%2 == 0), false);
            }
        }
    }

    private static void generateChainPiece(ServerLevel serverLevel, Vec3 position, boolean isVertical, boolean isAlt, boolean isRotated) {
        ChainEntity entity = new ChainEntity(ForgottenEntities.CHAIN, serverLevel);
        entity.setVertical(isVertical);
        entity.setAlt(isAlt);
        entity.setRotated(isRotated);
        entity.setIndex(currentIndex);

        currentIndex++;

        if (isAlt) {
            if (isVertical) {
                position = position.add(-0.1,0.2f,0);
            }

            position = position.add(0.1,-0.2f,0);
        }

        serverLevel.addFreshEntity(entity);
        entity.setPos(position);
    }

    public static void killChainRow(ServerLevel serverLevel, int coreIndex) {
        new Thread(() -> {
            List<ChainEntity> chains = ServerCache.chains.stream()
                    .filter(Objects::nonNull)
                    .toList();

            for (int i = coreIndex; i <= coreIndex + 22; i++) {
                for (int j = 0; j < chains.size(); j++) {
                    ChainEntity chainEntity = chains.get(j);

                    if (chainEntity != null && chainEntity.getIndex() == i) {
                        try {
                            Thread.sleep(300);
                            chainEntity.destroy();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            for (int i = coreIndex + 78; i >= coreIndex + 22; i--) {
                for (int j = 0; j < chains.size(); j++) {
                    ChainEntity chainEntity = chains.get(j);

                    if (chainEntity != null && chainEntity.getIndex() == i) {
                        try {
                            Thread.sleep(300);
                            chainEntity.destroy();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            SaveManager saveManager = SaveManager.getInstance(serverLevel.getServer());

            int defeated = saveManager.get("SmallCoresDefeated", Integer.class);

            saveManager.save("SmallCoresDefeated", defeated + 1);
        }).start();
    }
}
