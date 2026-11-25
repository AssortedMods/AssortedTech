package com.grim3212.assorted.fan.common.block;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.fan.api.util.FanMode;
import com.grim3212.assorted.fan.common.block.blockentity.FanBlockEntity;
import com.grim3212.assorted.fan.common.network.FanOpenPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class FanBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final EnumProperty<FanMode> MODE = EnumProperty.create("mode", FanMode.class);

    public FanBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(MODE, FanMode.BLOW).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(MODE, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FanBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirrorIn) {
        return this.rotate(state, mirrorIn.getRotation(state.getValue(FACING)));
    }

    /**
     * {@code use} split into {@code useItemOn} / {@code useWithoutItem}; opening the fan screen does
     * not care what is in hand, so a held item falls through to this hand-agnostic half.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        if (!worldIn.isClientSide()) {
            Services.NETWORK.sendTo(player, new FanOpenPacket(pos));
        }
        return InteractionResult.sidedSuccess(worldIn.isClientSide());
    }

    /**
     * A fan placed already powered gets no neighbour update, so it checks its power once its block
     * entity exists, which is after this call.
     */
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.updatePower(state, level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        this.updatePower(state, level, pos);
    }

    /** Powered, the fan stops and remembers its mode; unpowered again, it goes back to that mode. */
    private void updatePower(BlockState state, Level level, BlockPos pos) {
        boolean isPowered = level.hasNeighborSignal(pos);
        BlockEntity ent = level.getBlockEntity(pos);

        if (ent instanceof FanBlockEntity fan) {
            if (isPowered && state.getValue(MODE) != FanMode.OFF) {
                if (fan.getMode() != FanMode.OFF) {
                    fan.setOldMode(fan.getMode());
                    fan.setMode(FanMode.OFF);
                }
                level.setBlock(pos, state.setValue(MODE, FanMode.OFF), 3);
            } else if (!isPowered && state.getValue(MODE) == FanMode.OFF) {
                fan.setMode(fan.getOldMode());
                level.setBlock(pos, state.setValue(MODE, fan.getOldMode()), 3);
            }
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof FanBlockEntity fan) {
                fan.tick();
            }
        };
    }
}
