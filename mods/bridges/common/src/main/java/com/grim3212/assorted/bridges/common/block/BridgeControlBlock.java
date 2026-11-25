package com.grim3212.assorted.bridges.common.block;

import com.grim3212.assorted.bridges.BridgesCommonMod;
import com.grim3212.assorted.bridges.api.util.BridgeType;
import com.grim3212.assorted.bridges.common.block.blockentity.BridgeControlBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

public class BridgeControlBlock extends Block implements EntityBlock {

    private final BridgeType type;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BridgeControlBlock(BridgeType type, Properties props) {
        super(props.pushReaction(PushReaction.BLOCK).isValidSpawn((s, g, p, e) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
        this.type = type;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BridgeControlBlockEntity(pos, state);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            boolean flag = state.getValue(POWERED);
            if (flag != level.hasNeighborSignal(pos)) {
                if (flag) {
                    level.scheduleTick(pos, this, 4);
                } else {
                    level.setBlock(pos, state.cycle(POWERED), 2);
                }
            }

        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (state.getValue(POWERED) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(POWERED), 2);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    /**
     * Tears down the projected bridge when the controller is broken, walking the segments from the
     * controller's position. Only when the block itself goes: powering it is a state change of the
     * same block, and the block entity takes the bridge down itself then.
     */
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            this.removeSegments(state, level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private void removeSegments(BlockState state, Level level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        int maxLength = BridgesCommonMod.COMMON_CONFIG.bridgeMaxLength.get();

        for (int i = 1; i <= maxLength; i++) {
            BlockPos segment = pos.relative(facing, i);
            BlockState segmentState = level.getBlockState(segment);

            if (!segmentState.is(BridgesBlocks.BRIDGE.get()) || segmentState.getValue(BridgeBlock.TYPE) != this.type) {
                break;
            }

            level.removeBlock(segment, false);
        }
    }

    public BridgeType getType() {
        return type;
    }

    // Server only, like the vanilla hopper: the tick places and removes bridge blocks, and the
    // server sends clients the result. A client placing its own copies left ghost segments.
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof BridgeControlBlockEntity bridge) {
                bridge.tick();
            }
        };
    }

    /**
     * Setting the projected block needs the held stack, so it is in {@code useItemOn}; clearing it
     * with a shift-click is handled in both, as {@code PASS_TO_DEFAULT_BLOCK_INTERACTION} falls
     * through to {@code useWithoutItem}.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof BridgeControlBlockEntity bridgeControl) {
            if (player.isShiftKeyDown()) {
                this.clearStoredState(bridgeControl, state, level, pos);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }

            if (!heldItem.isEmpty() && heldItem.getItem() instanceof BlockItem block && block.getBlock() != null) {
                BlockState currentState = bridgeControl.getStoredBlockState();
                BlockState tryToSetState = block.getBlock().defaultBlockState();

                // A projected segment always renders as a full cube, so only a full block can be one.
                if (currentState != tryToSetState && tryToSetState.isCollisionShapeFullBlock(level, pos)) {
                    bridgeControl.setStoredBlockState(tryToSetState);

                    // Toggle to update blocks
                    level.setBlockAndUpdate(pos, state.setValue(POWERED, false));

                    SoundType sound = soundOf(tryToSetState);
                    level.playSound(player, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof BridgeControlBlockEntity bridgeControl) {
            this.clearStoredState(bridgeControl, state, level, pos);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    /**
     * The sound of a block that is not placed anywhere yet. NeoForge deprecates this for its
     * level-aware overload, which only exists in its patched jar, and there is no position to ask.
     */
    @SuppressWarnings("deprecation")
    private static SoundType soundOf(BlockState state) {
        return state.getSoundType();
    }

    private void clearStoredState(BridgeControlBlockEntity bridgeControl, BlockState state, Level level, BlockPos pos) {
        bridgeControl.setStoredBlockState(Blocks.AIR.defaultBlockState());

        // Toggle to update blocks
        level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
    }
}
