package com.grim3212.assorted.redstone.client.data;

import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.common.block.FlipFlopTorchBlock;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/**
 * Block states and block models. The torch items are flat sprites, which {@link RedstoneItemModelProvider}
 * draws, so this models no items.
 */
public class RedstoneBlockstateProvider extends ModelProvider {

    public RedstoneBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Redstone block states";
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        torch(blockModels, RedstoneBlocks.FLIP_FLOP_TORCH.get(), RedstoneBlocks.FLIP_FLOP_WALL_TORCH.get());
        torch(blockModels, RedstoneBlocks.GLOWSTONE_TORCH.get(), RedstoneBlocks.GLOWSTONE_WALL_TORCH.get());
    }

    /**
     * A torch pair. The wall torch's {@code rotationY((toYRot() + 90) % 360)} is
     * {@link BlockModelGenerators#ROTATION_TORCH}, which is how vanilla's own wall torches are
     * oriented. {@code PREV_LIT} on the flip flop torch is not dispatched on, as before.
     */
    private void torch(BlockModelGenerators blockModels, Block torch, Block wallTorch) {
        String name = name(torch);
        String wallName = name(wallTorch);

        Identifier lit = ModelTemplates.TORCH.create(resource("block/" + name), torchMapping(texture("block/" + name)), blockModels.modelOutput);
        Identifier unlit = ModelTemplates.TORCH.create(resource("block/" + name + "_off"), torchMapping(texture("block/" + name + "_off")), blockModels.modelOutput);
        Identifier litWall = ModelTemplates.WALL_TORCH.create(resource("block/" + wallName), torchMapping(texture("block/" + name)), blockModels.modelOutput);
        Identifier unlitWall = ModelTemplates.WALL_TORCH.create(resource("block/" + wallName + "_off"), torchMapping(texture("block/" + name + "_off")), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(torch)
                .with(BlockModelGenerators.createBooleanModelDispatch(FlipFlopTorchBlock.LIT, BlockModelGenerators.plainVariant(lit), BlockModelGenerators.plainVariant(unlit))));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(wallTorch)
                .with(BlockModelGenerators.createBooleanModelDispatch(FlipFlopTorchBlock.LIT, BlockModelGenerators.plainVariant(litWall), BlockModelGenerators.plainVariant(unlitWall)))
                .with(BlockModelGenerators.ROTATION_TORCH));
    }

    private static TextureMapping torchMapping(Material torch) {
        return new TextureMapping().put(TextureSlot.TORCH, torch);
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
