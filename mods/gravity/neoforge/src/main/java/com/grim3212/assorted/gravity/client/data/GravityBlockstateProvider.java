package com.grim3212.assorted.gravity.client.data;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.block.GravityBlock;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.block.GravityDirectionalBlock;
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
 * Block states, block models and the block items' models. {@link GravityItemModelProvider} models the boots, so the
 * two never write the same file.
 */
public class GravityBlockstateProvider extends BlockStateProvider {

    public GravityBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Gravity block states";
    }

    @Override
    protected void registerStatesAndModels() {
        genericGravity(GravityBlocks.ATTRACTOR.get());
        genericGravity(GravityBlocks.REPULSOR.get());
        genericGravity(GravityBlocks.GRAVITOR.get());

        gravityDirectional(GravityBlocks.ATTRACTOR_DIRECTIONAL.get(), resource("block/attractor_on"), resource("block/attractor_off"));
        gravityDirectional(GravityBlocks.REPULSOR_DIRECTIONAL.get(), resource("block/repulsor_on"), resource("block/repulsor_off"));
        gravityDirectional(GravityBlocks.GRAVITOR_DIRECTIONAL.get(), resource("block/gravitor_on"), resource("block/gravitor_off"));
    }

    private void genericGravity(Block b) {
        String name = name(b);
        ModelFile on = this.models().cubeAll(name, resource("block/" + name + "_on"));
        ModelFile off = this.models().cubeAll(name + "_off", resource("block/" + name + "_off"));

        this.getVariantBuilder(b).forAllStates(state -> ConfiguredModel.builder().modelFile(state.getValue(GravityBlock.POWERED) ? on : off).build());
        this.simpleBlockItem(b, on);
    }

    private void gravityDirectional(GravityDirectionalBlock b, ResourceLocation on, ResourceLocation off) {
        String name = name(b);
        ResourceLocation side = resource("block/gravity_side");

        ModelFile onModel = this.models().orientable(name + "_on", side, on, side);
        ModelFile offModel = this.models().orientable(name + "_off", side, off, side);
        // orientable_vertical takes only front and side; nothing there reads a top texture.
        ModelFile onVertical = this.models().orientableVertical(name + "_on_vertical", side, on);
        ModelFile offVertical = this.models().orientableVertical(name + "_off_vertical", side, off);

        this.getVariantBuilder(b).forAllStates(state -> {
            boolean powered = state.getValue(GravityDirectionalBlock.POWERED);
            Direction dir = state.getValue(GravityDirectionalBlock.FACING);
            ModelFile horizontal = powered ? onModel : offModel;
            ModelFile vertical = powered ? onVertical : offVertical;

            if (dir == Direction.DOWN) {
                return ConfiguredModel.builder().modelFile(vertical).rotationX(180).build();
            }
            if (dir == Direction.UP) {
                return ConfiguredModel.builder().modelFile(vertical).build();
            }
            // A horizontal facing draws the plain model turned to face the player.
            return ConfiguredModel.builder().modelFile(horizontal).rotationY(((int) dir.toYRot() + 180) % 360).build();
        });

        this.simpleBlockItem(b, onModel);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
