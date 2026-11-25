package com.grim3212.assorted.alarm.client.data;

import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.common.block.AlarmBlock;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder.FaceRotation;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * The alarm's block states, block models and item model.
 */
public class AlarmBlockstateProvider extends BlockStateProvider {

    public AlarmBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Alarm block states";
    }

    @Override
    protected void registerStatesAndModels() {
        alarm();
    }

    /**
     * The alarm box: two hand built element models, a floor one and a wall one, chosen by whether the
     * block faces vertically. {@code WATERLOGGED} and {@code POWERED} are not dispatched on. The side
     * texture has see-through pixels, so both are cut out.
     */
    private void alarm() {
        Block b = AlarmBlocks.ALARM.get();

        BlockModelBuilder floorModel = base("alarm");
        floorModel.element().from(3, 0, 3).to(13, 4, 13).allFaces((dir, face) -> {
            switch (dir) {
                case EAST, NORTH, SOUTH, WEST -> face.texture("#side").uvs(2, 11, 14, 16);
                case DOWN -> face.texture("#top").uvs(3, 3, 13, 13).cullface(Direction.DOWN);
                case UP -> face.texture("#top").uvs(3, 3, 13, 13);
            }
        }).end();

        BlockModelBuilder wallModel = base("alarm_wall");
        wallModel.element().from(3, 3, 12).to(13, 13, 16).allFaces((dir, face) -> {
            switch (dir) {
                case EAST -> face.texture("#side").uvs(2, 11, 14, 16).rotation(FaceRotation.CLOCKWISE_90);
                case NORTH -> face.texture("#top").uvs(3, 3, 13, 13);
                case SOUTH -> face.texture("#top").uvs(3, 3, 13, 13).cullface(Direction.SOUTH);
                case WEST -> face.texture("#side").uvs(2, 11, 14, 16).rotation(FaceRotation.COUNTERCLOCKWISE_90);
                case DOWN -> face.texture("#side").uvs(2, 11, 14, 16).rotation(FaceRotation.UPSIDE_DOWN);
                case UP -> face.texture("#side").uvs(2, 11, 14, 16);
            }
        }).end();

        this.getVariantBuilder(b).forAllStatesExcept(state -> {
            Direction dir = state.getValue(AlarmBlock.FACING);
            ModelFile model = dir.getAxis().isVertical() ? floorModel : wallModel;
            return ConfiguredModel.builder().modelFile(model)
                    .rotationX(dir == Direction.DOWN ? 180 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot() + 180) % 360).build();
        }, AlarmBlock.WATERLOGGED, AlarmBlock.POWERED);

        this.simpleBlockItem(b, wallModel);
    }

    private BlockModelBuilder base(String name) {
        return this.models().withExistingParent(name, mcLoc("block/block"))
                .texture("particle", resource("block/alarm_side"))
                .texture("side", resource("block/alarm_side"))
                .texture("top", resource("block/alarm_top"))
                .renderType("cutout");
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
