package com.grim3212.assorted.bridges.client.data;

import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.api.util.BridgeType;
import com.grim3212.assorted.bridges.client.color.BridgeItemTintSource;
import com.grim3212.assorted.bridges.client.model.BridgeItemModel;
import com.grim3212.assorted.bridges.common.block.BridgeBlock;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.lib.client.data.SpecificationBlockStateModelBuilder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.List;

/** Block states, block models and the block items' models. */
public class BridgesBlockstateProvider extends ModelProvider {

    /**
     * The slot the bridge's shape reads its face texture from. {@link TextureSlot} has no
     * {@code equals}, so it has to be created exactly once and shared.
     */
    private static final TextureSlot STORED = TextureSlot.create("stored");

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");
    private static final Identifier TINTED_CUBE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/tinted_cube");

    /**
     * The shape every bridge inherits: a full cube whose faces all read {@code #stored} and all carry
     * tint index 0, which is where the bridge's {@code BlockTintSource} colours it.
     */
    private static final ModelTemplate TINTED_CUBE_TEMPLATE = defaultPerspective(ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(STORED)
            .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(STORED).cullface(dir).tintindex(0))))
            .build();

    public BridgesBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Bridges block states";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        TINTED_CUBE_TEMPLATE.create(TINTED_CUBE, new TextureMapping()
                .put(TextureSlot.PARTICLE, texture("block/bridge"))
                .put(STORED, texture("block/bridge")), blockModels.modelOutput);

        bridge(blockModels);
        bridgeControl(blockModels, BridgesBlocks.BRIDGE_CONTROL_ACCEL.get());
        bridgeControl(blockModels, BridgesBlocks.BRIDGE_CONTROL_LASER.get());
        bridgeControl(blockModels, BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get());
        bridgeControl(blockModels, BridgesBlocks.BRIDGE_CONTROL_TRICK.get());
        bridgeControl(blockModels, BridgesBlocks.BRIDGE_CONTROL_DEATH.get());
    }

    // ------------------------------------------------------------------ bridge

    /**
     * One bridge loader model per fallback texture, dispatched on {@link BridgeBlock#TYPE}: {@code
     * block/bridge_gravity} for {@link BridgeType#GRAVITY}, {@code block/bridge} for the rest.
     * {@code collectParts} never sees the block state, so the choice is made in the blockstate
     * json.
     */
    private void bridge(BlockModelGenerators blockModels) {
        Block b = BridgesBlocks.BRIDGE.get();
        Identifier plain = bridgeModel(blockModels, "block/" + name(b), texture("block/bridge"));
        Identifier gravity = bridgeModel(blockModels, "block/" + name(b) + "_gravity", texture("block/bridge_gravity"));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(BridgeBlock.TYPE)
                        .generate(type -> bridgeVariant(type == BridgeType.GRAVITY ? gravity : plain))));

        // The item draws its stored block through BridgeItemModel, or the plain model when empty.
        // The tint source sits at index 0, the tint index tinted_cube puts on every face.
        blockModels.itemModelOutput.accept(b.asItem(), new BridgeItemModel.Unbaked(plain, List.of(new BridgeItemTintSource())));
    }

    /**
     * A bridge's {@link MultiVariant}, through {@link SpecificationBlockStateModelBuilder} so the
     * bridge's own model reaches the blockstate layer, the only one that sees the level and
     * position. A plain variant is baked once with no block entity, and every bridge draws its
     * fallback.
     */
    private static MultiVariant bridgeVariant(Identifier model) {
        return SpecificationBlockStateModelBuilder.specificationVariant(model);
    }

    private Identifier bridgeModel(BlockModelGenerators blockModels, String path, Material stored) {
        ModelTemplate template = defaultPerspective(ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .customLoader(BridgeModelBuilder::begin, loader -> loader
                        .bridge(TINTED_CUBE)
                        .addTexture("stored", stored.sprite())))
                .build();

        return template.create(resource(path), new TextureMapping().put(TextureSlot.PARTICLE, stored), blockModels.modelOutput);
    }

    /**
     * The block-sized item perspectives the 1.20.1 provider stamped on the bridge models and on
     * {@code tinted_cube}. They were an inline {@code transforms()} block on the old builder and are
     * template transforms now; the values are copied across unchanged.
     */
    private static ExtendedModelTemplateBuilder defaultPerspective(ExtendedModelTemplateBuilder builder) {
        return builder
                .transform(ItemDisplayContext.GUI, t -> t.rotation(30.0F, 225.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.625F))
                .transform(ItemDisplayContext.GROUND, t -> t.rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 3.0F, 0.0F).scale(0.25F))
                .transform(ItemDisplayContext.FIXED, t -> t.rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.5F))
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, t -> t.rotation(75.0F, 45.0F, 0.0F).translation(0.0F, 2.5F, 0.0F).scale(0.375F))
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, t -> t.rotation(0.0F, 45.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F))
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, t -> t.rotation(0.0F, 225.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F));
    }

    private void bridgeControl(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Material front = texture("block/" + name);
        Material side = texture("block/bridge_control_side");
        Material top = texture("block/bridge_control_top");

        Identifier inventoryModel = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(resource("block/" + name), new TextureMapping()
                .put(TextureSlot.TOP, top).put(TextureSlot.BOTTOM, top).put(TextureSlot.SIDE, side).put(TextureSlot.FRONT, front), blockModels.modelOutput);
        Identifier placedModel = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(resource("block/" + name + "_vertical"), new TextureMapping()
                .put(TextureSlot.TOP, front).put(TextureSlot.BOTTOM, top).put(TextureSlot.SIDE, side).put(TextureSlot.FRONT, side), blockModels.modelOutput);

        // Every placed state used the "_vertical" model in 1.20.1, including the horizontal ones; the
        // other model exists only for the item. Preserved verbatim.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b, BlockModelGenerators.plainVariant(placedModel))
                .with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));

        blockModels.registerSimpleItemModel(b, inventoryModel);
    }

    // ------------------------------------------------------------------ helpers

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
