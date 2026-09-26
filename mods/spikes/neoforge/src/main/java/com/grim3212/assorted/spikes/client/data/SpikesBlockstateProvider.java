package com.grim3212.assorted.spikes.client.data;

import com.grim3212.assorted.spikes.Constants;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Block states, block models and item models for every spike.
 */
public class SpikesBlockstateProvider extends ModelProvider {

    public SpikesBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Spikes block states";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        SpikesBlocks.SPIKES.forEach(spike -> spike(blockModels, spike.get()));
    }

    private void spike(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Material unpowered = texture("block/spikes/" + name);
        Material powered = texture("block/spikes/" + name + "_powered");

        Identifier unpoweredModel = ModelTemplates.CROSS.create(resource("block/spikes/" + name), TextureMapping.cross(unpowered), blockModels.modelOutput);
        Identifier poweredModel = ModelTemplates.CROSS.create(resource("block/spikes/" + name + "_powered"), TextureMapping.cross(powered), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, BlockModelGenerators.plainVariant(poweredModel), BlockModelGenerators.plainVariant(unpoweredModel)))
                .with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));

        // The spike item is a flat sprite of the powered texture, not the block model.
        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name), TextureMapping.layer0(powered), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static Material texture(String path) {
        return new Material(resource(path));
    }
}
