package com.grim3212.assorted.gravity.client.data;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.block.GravityBlock;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.block.GravityDirectionalBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and every block item, and
 * {@link GravityItemModelProvider} the boots, so the two never write the same file.
 */
public class GravityBlockstateProvider extends ModelProvider {

    public GravityBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Gravity block states";
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        genericGravity(blockModels, GravityBlocks.ATTRACTOR.get());
        genericGravity(blockModels, GravityBlocks.REPULSOR.get());
        genericGravity(blockModels, GravityBlocks.GRAVITOR.get());

        gravityDirectional(blockModels, GravityBlocks.ATTRACTOR_DIRECTIONAL.get(), texture("block/attractor_on"), texture("block/attractor_off"));
        gravityDirectional(blockModels, GravityBlocks.REPULSOR_DIRECTIONAL.get(), texture("block/repulsor_on"), texture("block/repulsor_off"));
        gravityDirectional(blockModels, GravityBlocks.GRAVITOR_DIRECTIONAL.get(), texture("block/gravitor_on"), texture("block/gravitor_off"));
    }

    private void genericGravity(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Identifier on = ModelTemplates.CUBE_ALL.create(resource("block/" + name), TextureMapping.cube(texture("block/" + name + "_on")), blockModels.modelOutput);
        Identifier off = ModelTemplates.CUBE_ALL.create(resource("block/" + name + "_off"), TextureMapping.cube(texture("block/" + name + "_off")), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(GravityBlock.POWERED, BlockModelGenerators.plainVariant(on), BlockModelGenerators.plainVariant(off))));

        blockModels.registerSimpleItemModel(b, on);
    }

    private void gravityDirectional(BlockModelGenerators blockModels, GravityDirectionalBlock b, Material on, Material off) {
        String name = name(b);
        Material side = texture("block/gravity_side");

        Identifier onModel = orientable(blockModels, "block/" + name + "_on", on, side);
        Identifier offModel = orientable(blockModels, "block/" + name + "_off", off, side);
        Identifier onVertical = orientableVertical(blockModels, "block/" + name + "_on_vertical", on, side);
        Identifier offVertical = orientableVertical(blockModels, "block/" + name + "_off_vertical", off, side);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(GravityDirectionalBlock.FACING, GravityDirectionalBlock.POWERED)
                        .generate((dir, powered) -> orientVertical(powered ? onModel : offModel, powered ? onVertical : offVertical, dir))));

        blockModels.registerSimpleItemModel(b, onModel);
    }

    private Identifier orientable(BlockModelGenerators blockModels, String path, Material front, Material side) {
        return ModelTemplates.CUBE_ORIENTABLE.create(resource(path), new TextureMapping()
                .put(TextureSlot.FRONT, front).put(TextureSlot.SIDE, side).put(TextureSlot.TOP, side), blockModels.modelOutput);
    }

    /**
     * {@code orientable_vertical} takes only {@code front} and {@code side}. The 1.20.1 builder also
     * wrote a {@code top} entry into these models; the template does not, because nothing reads it.
     */
    private Identifier orientableVertical(BlockModelGenerators blockModels, String path, Material front, Material side) {
        return ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create(resource(path), new TextureMapping()
                .put(TextureSlot.FRONT, front).put(TextureSlot.SIDE, side), blockModels.modelOutput);
    }

    /**
     * A vertical facing draws the {@code _vertical} model (flipped for {@code DOWN}) and a horizontal one the
     * plain model turned to face the player, which is {@link BlockModelGenerators#ROTATION_HORIZONTAL_FACING}.
     */
    private static MultiVariant orientVertical(Identifier horizontal, Identifier vertical, Direction dir) {
        if (dir == Direction.DOWN) {
            return BlockModelGenerators.plainVariant(vertical).with(BlockModelGenerators.X_ROT_180);
        }

        if (dir == Direction.UP) {
            return BlockModelGenerators.plainVariant(vertical);
        }

        return BlockModelGenerators.plainVariant(horizontal).with(horizontalRotation(dir));
    }

    private static VariantMutator horizontalRotation(Direction dir) {
        return switch (dir) {
            case EAST -> BlockModelGenerators.Y_ROT_90;
            case SOUTH -> BlockModelGenerators.Y_ROT_180;
            case WEST -> BlockModelGenerators.Y_ROT_270;
            default -> BlockModelGenerators.NOP;
        };
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
