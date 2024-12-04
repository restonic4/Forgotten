package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.common.ForgottenEntities;
import com.restonic4.forgotten.saving.JsonDataManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        JsonDataManager dataManager = Forgotten.getDataManager();
        dataManager.save("center", pos);

        try {
            generateSmallCore(serverLevel, new BlockPos(pos).offset(42, -2, 42));
            generateChain(serverLevel, new BlockPos(pos).offset(1, 22, 0), 56, new Vec3(1, 0, 0));

            generateSmallCore(serverLevel, new BlockPos(pos).offset(42, -2, -42));
            generateChain(serverLevel, new BlockPos(pos).offset(0, 22, -1), 56, new Vec3(0, 0, -1));

            generateSmallCore(serverLevel, new BlockPos(pos).offset(-42, -2, 42));
            generateChain(serverLevel, new BlockPos(pos).offset(-1, 22, 0), 56, new Vec3(-1, 0, 0));

            generateSmallCore(serverLevel, new BlockPos(pos).offset(-42, -2, -42));
            generateChain(serverLevel, new BlockPos(pos).offset(0, 22, 1), 56, new Vec3(0, 0, 1));

            generateBigCore(serverLevel, new BlockPos(pos).offset(0, 22, 0));
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
        SmallCoreEntity entity = new SmallCoreEntity(ForgottenEntities.SMALL_CORE, serverLevel);
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
        Iterable<Entity> entities = serverLevel.getAllEntities();

        List<ChainEntity> chains = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity instanceof ChainEntity chainEntity) {
                chains.add(chainEntity);
            }
        }

        chains.sort(Comparator.comparingInt(ChainEntity::getIndex));

        new Thread(() -> {
            for (ChainEntity chainEntity : chains) {
                if (chainEntity.getIndex() >= coreIndex && chainEntity.getIndex() <= coreIndex + 22) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception ignored) {}

                    chainEntity.destroy();
                }
            }

            chains.sort(Comparator.comparingInt(ChainEntity::getIndex).reversed());

            for (ChainEntity chainEntity : chains) {
                if (chainEntity.getIndex() > coreIndex + 22 && chainEntity.getIndex() <= coreIndex + 78) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception ignored) {}

                    chainEntity.destroy();
                }
            }
        }).start();
    }
}
