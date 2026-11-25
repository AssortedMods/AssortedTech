package com.grim3212.assorted.bridges.client.data;

import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.api.util.BridgeType;
import com.grim3212.assorted.bridges.common.block.BridgeBlock;
import com.grim3212.assorted.bridges.common.block.BridgeControlBlock;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Block states, block models and the block items' models, the bridge loader models included. */
public class BridgesBlockstateProvider extends BlockStateProvider {

    private static final ResourceLocation MC_BLOCK = ResourceLocation.withDefaultNamespace("block/block");
    private static final ResourceLocation TINTED_CUBE = resource("block/tinted_cube");

    public BridgesBlockstateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Bridges block states";
    }

    @Override
    protected void registerStatesAndModels() {
        // The shape every bridge inherits: a full cube whose faces all read #stored and carry tint
        // index 0, which is where the bridge's block colour tints it.
        defaultPerspective(this.models().getBuilder(TINTED_CUBE.toString()).parent(unchecked(MC_BLOCK))
                .texture("particle", resource("block/bridge")).texture("stored", resource("block/bridge"))
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#stored").cullface(dir).tintindex(0)).end());

        bridge();
        bridgeControl(BridgesBlocks.BRIDGE_CONTROL_ACCEL.get());
        bridgeControl(BridgesBlocks.BRIDGE_CONTROL_LASER.get());
        bridgeControl(BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get());
        bridgeControl(BridgesBlocks.BRIDGE_CONTROL_TRICK.get());
        bridgeControl(BridgesBlocks.BRIDGE_CONTROL_DEATH.get());
    }

    // ------------------------------------------------------------------ bridge

    /**
     * One bridge loader model per fallback texture, dispatched on {@link BridgeBlock#TYPE}: {@code
     * block/bridge_gravity} for {@link BridgeType#GRAVITY}, {@code block/bridge} for the rest. The
     * baked model never sees the block state, so the choice is made in the blockstate json. The
     * light dampening a bridge carries changes nothing it draws.
     */
    private void bridge() {
        Block b = BridgesBlocks.BRIDGE.get();
        ModelFile plain = bridgeModel("block/" + name(b), resource("block/bridge"));
        ModelFile gravity = bridgeModel("block/" + name(b) + "_gravity", resource("block/bridge_gravity"));

        this.getVariantBuilder(b).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(BridgeBlock.TYPE) == BridgeType.GRAVITY ? gravity : plain).build(), BridgeBlock.LIGHT_DAMPENING);

        // The item draws its stored block through the block model's item overrides, or the plain
        // model when empty.
        this.itemModels().getBuilder(name(b)).parent(plain);
    }

    /**
     * The shape template is named twice: inside the {@code bridge} object, which the loader bakes,
     * and as the json's own {@code parent}, which is where the item takes the template's display
     * transforms from. The loader's geometry still wins over the parent's.
     */
    private ModelFile bridgeModel(String path, ResourceLocation stored) {
        BlockModelBuilder builder = this.models().getBuilder(resource(path).toString()).parent(unchecked(TINTED_CUBE)).texture("particle", stored);
        builder.customLoader(BridgeModelBuilder::begin).bridge(TINTED_CUBE).addTexture("stored", stored);
        return builder;
    }

    private void bridgeControl(Block b) {
        String name = name(b);
        ResourceLocation front = resource("block/" + name);
        ResourceLocation side = resource("block/bridge_control_side");
        ResourceLocation top = resource("block/bridge_control_top");

        ModelFile inventoryModel = this.models().orientableWithBottom(name, side, front, top, top);
        ModelFile placedModel = this.models().orientableWithBottom(name + "_vertical", side, side, top, front);

        // Every placed state uses the "_vertical" model, the horizontal ones included, turned to its
        // facing; the other model exists only for the item. Powering it changes nothing drawn.
        this.getVariantBuilder(b).forAllStatesExcept(state -> {
            Direction dir = state.getValue(BridgeControlBlock.FACING);
            return ConfiguredModel.builder().modelFile(placedModel)
                    .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot() + 180) % 360)
                    .build();
        }, BridgeControlBlock.POWERED);

        this.simpleBlockItem(b, inventoryModel);
    }

    /**
     * The block sized item perspectives of {@code tinted_cube}, which every bridge model inherits.
     */
    private static <T extends ModelBuilder<T>> T defaultPerspective(T builder) {
        return builder.transforms()
                .transform(ItemDisplayContext.GUI).rotation(30.0F, 225.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.625F).end()
                .transform(ItemDisplayContext.GROUND).rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 3.0F, 0.0F).scale(0.25F).end()
                .transform(ItemDisplayContext.FIXED).rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.5F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75.0F, 45.0F, 0.0F).translation(0.0F, 2.5F, 0.0F).scale(0.375F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0.0F, 45.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0.0F, 225.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F).end()
                .end();
    }

    // ------------------------------------------------------------------ helpers

    private static ModelFile unchecked(ResourceLocation location) {
        return new ModelFile.UncheckedModelFile(location);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
