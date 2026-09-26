package com.grim3212.assorted.alarm.client.data;

import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.common.block.AlarmBlock;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

/**
 * The alarm's block states, block models and item model.
 */
public class AlarmBlockstateProvider extends ModelProvider {

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");

    public AlarmBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Alarm block states";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        alarm(blockModels);
    }

    /**
     * The alarm box: two hand built element models, a floor one and a wall one, chosen by whether the
     * block faces vertically. {@code WATERLOGGED} and {@code POWERED} are not dispatched on, exactly
     * as the 1.20.1 {@code forAllStatesExcept} did.
     */
    private void alarm(BlockModelGenerators blockModels) {
        Block b = AlarmBlocks.ALARM.get();
        Material side = texture("block/alarm_side");
        Material top = texture("block/alarm_top");

        ModelTemplate floorTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(MC_BLOCK)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.SIDE)
                .requiredTextureSlot(TextureSlot.TOP)
                .element(e -> e.from(3, 0, 3).to(13, 4, 13).allFaces((dir, face) -> {
                    switch (dir) {
                        case EAST, NORTH, SOUTH, WEST -> face.texture(TextureSlot.SIDE).uvs(2, 11, 14, 16);
                        case DOWN -> face.texture(TextureSlot.TOP).uvs(3, 3, 13, 13).cullface(Direction.DOWN);
                        case UP -> face.texture(TextureSlot.TOP).uvs(3, 3, 13, 13);
                    }
                }))
                .build();

        ModelTemplate wallTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(MC_BLOCK)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.SIDE)
                .requiredTextureSlot(TextureSlot.TOP)
                .element(e -> e.from(3, 3, 12).to(13, 13, 16).allFaces((dir, face) -> {
                    switch (dir) {
                        case EAST -> face.texture(TextureSlot.SIDE).uvs(2, 11, 14, 16).rotation(Quadrant.R90);
                        case NORTH -> face.texture(TextureSlot.TOP).uvs(3, 3, 13, 13);
                        case SOUTH -> face.texture(TextureSlot.TOP).uvs(3, 3, 13, 13).cullface(Direction.SOUTH);
                        case WEST -> face.texture(TextureSlot.SIDE).uvs(2, 11, 14, 16).rotation(Quadrant.R270);
                        case DOWN -> face.texture(TextureSlot.SIDE).uvs(2, 11, 14, 16).rotation(Quadrant.R180);
                        case UP -> face.texture(TextureSlot.SIDE).uvs(2, 11, 14, 16);
                    }
                }))
                .build();

        TextureMapping textures = new TextureMapping().put(TextureSlot.PARTICLE, side).put(TextureSlot.SIDE, side).put(TextureSlot.TOP, top);
        Identifier floorModel = floorTemplate.create(resource("block/alarm"), textures, blockModels.modelOutput);
        Identifier wallModel = wallTemplate.create(resource("block/alarm_wall"), textures, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(AlarmBlock.FACING).generate(dir -> orientVertical(wallModel, floorModel, dir))));

        blockModels.registerSimpleItemModel(b, wallModel);
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

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static Material texture(String path) {
        return new Material(resource(path));
    }
}
