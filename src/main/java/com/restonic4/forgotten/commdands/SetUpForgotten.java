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
import net.minecraft.world.phys.Vec3;

public class SetUpForgotten {
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

        generateSmallCore(serverLevel, new BlockPos(pos).offset(42, -2, 42));
        generateSmallCore(serverLevel, new BlockPos(pos).offset(42, -2, -42));
        generateSmallCore(serverLevel, new BlockPos(pos).offset(-42, -2, 42));
        generateSmallCore(serverLevel, new BlockPos(pos).offset(-42, -2, -42));

        generateBigCore(serverLevel, new BlockPos(pos).offset(0, 16, 0));

        generateChain(serverLevel, new BlockPos(pos).offset(0, 16, 0), 5, false);

        return 1;
    }

    private static void generateSmallCore(ServerLevel serverLevel, BlockPos position) {
        SmallCoreEntity entity = new SmallCoreEntity(ForgottenEntities.SMALL_CORE, serverLevel);
        serverLevel.addFreshEntity(entity);
        entity.setPos(position.getCenter());
    }

    private static void generateBigCore(ServerLevel serverLevel, BlockPos position) {
        SmallCoreEntity entity = new SmallCoreEntity(ForgottenEntities.SMALL_CORE, serverLevel);
        serverLevel.addFreshEntity(entity);
        entity.setPos(position.getCenter());
    }

    private static void generateChain(ServerLevel serverLevel, BlockPos position, int length, boolean isVertical) {
        for (int i = 0; i < length; i++) {
            generateChainPiece(serverLevel, new BlockPos(position).getCenter().add(0, 0, ((float) i/2)), isVertical, (i%2 == 0));
        }
    }

    private static void generateChainPiece(ServerLevel serverLevel, Vec3 position, boolean isVertical, boolean isAlt) {
        ChainEntity entity = new ChainEntity(ForgottenEntities.CHAIN, serverLevel);
        entity.setVertical(isVertical);
        entity.setAlt(isAlt);

        if (isAlt) {
            System.out.println("ES ALT, AAAAAAAA");
            position = position.add(0,0.25,0);
        }

        serverLevel.addFreshEntity(entity);
        entity.setPos(position);
    }
}
