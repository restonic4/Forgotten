package com.restonic4.forgotten.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

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

                if (storedItem.isEmpty() && !heldItem.isEmpty()) {
                    blockEntity.setStoredItem(heldItem.copy());
                    player.setItemInHand(interactionHand, ItemStack.EMPTY);
                } else if (!storedItem.isEmpty()) {
                    blockEntity.setStoredItem(heldItem.copy());
                    player.setItemInHand(interactionHand, storedItem);
                }

                // Sync with the client
                blockEntity.setChanged();
                level.sendBlockUpdated(blockPos, blockState, blockState, 3);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
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
