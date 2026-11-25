package com.grim3212.assorted.spikes.client.data;

import com.grim3212.assorted.spikes.Constants;
import com.grim3212.assorted.spikes.common.block.SpikeBlock;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Every spike's block states, block models and item model.
 */
public class SpikesBlockstateProvider extends BlockStateProvider {

    public SpikesBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Spikes block states";
    }

    @Override
    protected void registerStatesAndModels() {
        SpikesBlocks.SPIKES.forEach(spike -> spike(spike.get()));
    }

    /**
     * A cut out cross, retracted or deployed by {@code POWERED} and turned to its {@code FACING} as
     * a column is. {@code WATERLOGGED} is not dispatched on.
     */
    private void spike(Block b) {
        String name = name(b);
        ResourceLocation unpowered = texture("block/spikes/" + name);
        ResourceLocation powered = texture("block/spikes/" + name + "_powered");

        ModelFile unpoweredModel = this.models().cross("block/spikes/" + name, unpowered).renderType("cutout");
        ModelFile poweredModel = this.models().cross("block/spikes/" + name + "_powered", powered).renderType("cutout");

        this.getVariantBuilder(b).forAllStatesExcept(state -> {
            Direction dir = state.getValue(SpikeBlock.FACING);
            return ConfiguredModel.builder().modelFile(state.getValue(SpikeBlock.POWERED) ? poweredModel : unpoweredModel)
                    .rotationX(dir == Direction.DOWN ? 180 : dir == Direction.UP ? 0 : 90)
                    .rotationY(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot() + 180) % 360).build();
        }, SpikeBlock.WATERLOGGED);

        // The spike item is a flat sprite of the powered texture, not the block model.
        this.itemModels().withExistingParent(name, mcLoc("item/generated")).texture("layer0", powered);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static ResourceLocation texture(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
