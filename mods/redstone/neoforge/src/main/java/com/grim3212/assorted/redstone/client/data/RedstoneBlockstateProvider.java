package com.grim3212.assorted.redstone.client.data;

import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.common.block.FlipFlopTorchBlock;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Block states and block models. The torch items are flat sprites, which {@link RedstoneItemModelProvider}
 * draws, so this models no items.
 */
public class RedstoneBlockstateProvider extends BlockStateProvider {

    public RedstoneBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Redstone block states";
    }

    @Override
    protected void registerStatesAndModels() {
        torch(RedstoneBlocks.FLIP_FLOP_TORCH.get(), RedstoneBlocks.FLIP_FLOP_WALL_TORCH.get(), FlipFlopTorchBlock.PREV_LIT);
        torch(RedstoneBlocks.GLOWSTONE_TORCH.get(), RedstoneBlocks.GLOWSTONE_WALL_TORCH.get());
    }

    /**
     * A torch pair, cut out like vanilla's torches. The wall torch turns by
     * {@code (toYRot() + 90) % 360}, as vanilla's own wall torches do. {@code PREV_LIT} on the flip
     * flop torch is not dispatched on.
     */
    private void torch(Block torch, Block wallTorch, Property<?>... ignored) {
        String name = name(torch);
        String wallName = name(wallTorch);

        ModelFile lit = torchModel(name, "block/template_torch", name);
        ModelFile unlit = torchModel(name + "_off", "block/template_torch", name + "_off");
        ModelFile litWall = torchModel(wallName, "block/template_torch_wall", name);
        ModelFile unlitWall = torchModel(wallName + "_off", "block/template_torch_wall", name + "_off");

        this.getVariantBuilder(torch).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(BlockStateProperties.LIT) ? lit : unlit).build(), ignored);

        this.getVariantBuilder(wallTorch).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(BlockStateProperties.LIT) ? litWall : unlitWall)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360).build(), ignored);
    }

    private ModelFile torchModel(String model, String parent, String texture) {
        return this.models().withExistingParent(model, mcLoc(parent)).texture("torch", resource("block/" + texture)).renderType("cutout");
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
