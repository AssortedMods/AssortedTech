package com.grim3212.assorted.fan.client.data;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.fan.common.block.FanBlock;
import com.grim3212.assorted.fan.common.block.FanBlocks;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/**
 * The fan's block states, block models and item model.
 */
public class FanBlockstateProvider extends ModelProvider {

    public FanBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Fan block states";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        fan(blockModels);
    }

    private void fan(BlockModelGenerators blockModels) {
        Block b = FanBlocks.FAN.get();
        String name = name(b);
        Material noteBlock = new Material(Identifier.withDefaultNamespace("block/note_block"));

        Identifier blow = orientable(blockModels, "block/" + name + "_blow", texture("block/" + name), noteBlock);
        Identifier suck = orientable(blockModels, "block/" + name + "_suck", texture("block/" + name + "_suck"), noteBlock);
        Identifier stopped = orientable(blockModels, "block/" + name + "_stopped", texture("block/" + name + "_stopped"), noteBlock);

        Identifier blowVertical = orientableVertical(blockModels, "block/" + name + "_blow_vertical", texture("block/" + name), noteBlock);
        Identifier suckVertical = orientableVertical(blockModels, "block/" + name + "_suck_vertical", texture("block/" + name + "_suck"), noteBlock);
        Identifier stoppedVertical = orientableVertical(blockModels, "block/" + name + "_stopped_vertical", texture("block/" + name + "_stopped"), noteBlock);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(FanBlock.FACING, FanBlock.MODE).generate((dir, mode) -> switch (mode) {
                    case BLOW -> orientVertical(blow, blowVertical, dir);
                    case SUCK -> orientVertical(suck, suckVertical, dir);
                    case OFF -> orientVertical(stopped, stoppedVertical, dir);
                })));

        blockModels.registerSimpleItemModel(b, stopped);
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
     * A vertical facing draws {@code vertical} (flipped for {@code DOWN}) and a horizontal facing draws {@code horizontal}
     * turned to face the player, which is {@link BlockModelGenerators#ROTATION_HORIZONTAL_FACING}.
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
