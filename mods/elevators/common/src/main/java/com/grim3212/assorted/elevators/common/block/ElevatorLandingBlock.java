package com.grim3212.assorted.elevators.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

/**
 * Built into the side of an elevator shaft one above a floor, it makes that floor a stop. A
 * redstone pulse calls the car to it, and a comparator reads 15 from it while the car is there.
 */
public class ElevatorLandingBlock extends Block {

    public static final MapCodec<ElevatorLandingBlock> CODEC = simpleCodec(ElevatorLandingBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ElevatorLandingBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected MapCodec<? extends ElevatorLandingBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) {
            return;
        }

        level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
        if (powered) {
            ElevatorShaft.call(serverLevel, pos);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(side).below()).getBlock() instanceof ElevatorBlock) {
                return 15;
            }
        }
        return 0;
    }
}
