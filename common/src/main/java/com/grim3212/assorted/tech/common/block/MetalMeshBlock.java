package com.grim3212.assorted.tech.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A grate you can walk on that items drop straight through. */
public class MetalMeshBlock extends Block {

    public static final MapCodec<MetalMeshBlock> CODEC = simpleCodec(MetalMeshBlock::new);

    public MetalMeshBlock(Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends MetalMeshBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof ItemEntity) {
            return Shapes.empty();
        }

        return super.getCollisionShape(state, level, pos, context);
    }

    /** Daylight comes through the holes; the default would dim it, the shape being a full cube. */
    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }
}
