package com.restonic4.forgotten.block;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.networking.packets.BeamPacket;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import com.restonic4.forgotten.util.helpers.SimpleEffectHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AltarBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final VoxelShape SHAPE_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    public static final VoxelShape SHAPE_POST = Block.box(4.0, 2.0, 4.0, 13.0, 10.0, 13.0);
    public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0, 14.0, 0.0, 16.0, 14.0, 16.0);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST, SHAPE_TOP_PLATE);
    public static final VoxelShape SHAPE_COLLISION = Shapes.or(SHAPE_COMMON);

    public AltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE_COLLISION;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE_COMMON;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return SHAPE_COMMON;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState blockState) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return (BlockState)blockState.setValue(FACING, rotation.rotate((Direction)blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation((Direction)blockState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!level.isClientSide) {
            AltarBlockEntity blockEntity = (AltarBlockEntity) level.getBlockEntity(blockPos);
            if (blockEntity != null) {
                ItemStack heldItem = player.getItemInHand(interactionHand);
                ItemStack storedItem = blockEntity.getStoredItem();

                boolean isRitualCondition =
                        (storedItem.is(ForgottenItems.PLAYER_SOUL) && heldItem.is(ForgottenItems.ETHEREAL_SHARD)) ||
                        (storedItem.is(ForgottenItems.ETHEREAL_SHARD) && heldItem.is(ForgottenItems.PLAYER_SOUL));

                if (isRitualCondition) {
                    CompoundTag tag = storedItem.getTag();

                    String playerName = null;

                    if (tag != null && tag.contains(PlayerSoul.MAIN_TAG, 8)) {
                        playerName = tag.getString(PlayerSoul.MAIN_TAG);
                    } else if (tag != null && tag.contains(PlayerSoul.MAIN_TAG, 10)) {
                        CompoundTag compoundTag2 = tag.getCompound(PlayerSoul.MAIN_TAG);
                        if (compoundTag2.contains("Name", 8)) {
                            playerName = compoundTag2.getString("Name");
                        }
                    }

                    System.out.println("Player name: " + playerName);
                    if (tag != null) {
                        System.out.println(tag.contains(PlayerSoul.MAIN_TAG));
                    }

                    if (playerName != null) {
                        ServerPlayer targetPlayer = level.getServer().getPlayerList().getPlayerByName(playerName);

                        if (targetPlayer != null && Forgotten.canExecuteRevivalRitual()) {
                            blockEntity.setStoredItem(ItemStack.EMPTY);
                            player.setItemInHand(interactionHand, ItemStack.EMPTY);

                            syncWithClients(level, blockEntity, blockState, blockPos);
                            executeRitual(targetPlayer);
                        } else {
                            SimpleEffectHelper.invalidHeadPlacement((ServerLevel) player.level(), blockPos.offset(0, 1, 0));
                        }
                    } else {
                        SimpleEffectHelper.invalidHeadPlacement((ServerLevel) player.level(), blockPos.offset(0, 1, 0));
                    }

                    return InteractionResult.SUCCESS;
                }

                if (storedItem.isEmpty() && !heldItem.isEmpty()) {
                    blockEntity.setStoredItem(heldItem.copy());
                    player.setItemInHand(interactionHand, ItemStack.EMPTY);
                } else if (!storedItem.isEmpty()) {
                    blockEntity.setStoredItem(heldItem.copy());
                    player.setItemInHand(interactionHand, storedItem);
                }

                syncWithClients(level, blockEntity, blockState, blockPos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    private void executeRitual(ServerPlayer playerToBeSaved) {
        Forgotten.setRevivalRitualTarget(playerToBeSaved);

        for (ServerPlayer serverPlayer : playerToBeSaved.getServer().getPlayerList().getPlayers()) {
            BeamPacket.sendToClient(serverPlayer);
        }
    }

    private void syncWithClients(Level level, BlockEntity blockEntity, BlockState blockState, BlockPos blockPos) {
        blockEntity.setChanged();
        level.sendBlockUpdated(blockPos, blockState, blockState, 3);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AltarBlockEntity(blockPos, blockState);
    }
}
