package com.grim3212.assorted.redstone.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Extends {@link BaseTorchBlock} rather than {@code TorchBlock}, which only takes a
 * {@code SimpleParticleType}; the redstone flame is drawn here as {@code RedstoneTorchBlock} does.
 */
public class FlipFlopTorchBlock extends BaseTorchBlock {

	public static final MapCodec<FlipFlopTorchBlock> CODEC = simpleCodec(FlipFlopTorchBlock::new);
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty PREV_LIT = BooleanProperty.create("prev_lit");

	public FlipFlopTorchBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(PREV_LIT, false));
	}

	@Override
	protected MapCodec<? extends FlipFlopTorchBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(LIT, PREV_LIT);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState state2, boolean movedByPiston) {
		for (Direction direction : Direction.values()) {
			level.updateNeighborsAt(pos.relative(direction), this);
		}
	}

	/**
	 * 1.21.1 also calls this when only the state changes, which {@link #onPlace} already covers, so
	 * only a real removal updates the neighbours here.
	 */
	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!movedByPiston && !state.is(newState.getBlock())) {
			for (Direction direction : Direction.values()) {
				level.updateNeighborsAt(pos.relative(direction), this);
			}
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
		level.scheduleTick(pos, this, 2);
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction dir) {
		return state.getValue(LIT) && Direction.UP != dir ? 15 : 0;
	}

	@Override
	protected int getDirectSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction dir) {
		return dir == Direction.DOWN ? state.getSignal(getter, pos, dir) : 0;
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		boolean flag = this.hasNeighborSignal(level, pos, state);
		boolean prevFlag = state.getValue(PREV_LIT);

		if (flag != prevFlag) {
			if (flag) {
				if (state.getValue(LIT)) {
					level.setBlock(pos, state.setValue(LIT, false).setValue(PREV_LIT, flag), 3);
				} else {
					level.setBlock(pos, state.setValue(LIT, true).setValue(PREV_LIT, flag), 3);
				}
			} else {
				level.setBlock(pos, state.setValue(PREV_LIT, flag), 3);
			}
		} else {
			level.setBlock(pos, state.setValue(PREV_LIT, flag), 3);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if (state.getValue(LIT)) {
			double d0 = (double) pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double) pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d2 = (double) pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			level.addParticle(DustParticleOptions.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	protected boolean hasNeighborSignal(Level level, BlockPos pos, BlockState state) {
		return level.hasSignal(pos.below(), Direction.DOWN);
	}
}
