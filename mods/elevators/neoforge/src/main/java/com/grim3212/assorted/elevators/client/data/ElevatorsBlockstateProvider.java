package com.grim3212.assorted.elevators.client.data;

import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.elevators.common.block.InstantElevatorBlock;
import com.grim3212.assorted.lib.client.data.SpecificationBlockStateModelBuilder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.EnumMap;
import java.util.Map;

/** Block states, block models and the block items' models: every block of the mod is an item too. */
public class ElevatorsBlockstateProvider extends ModelProvider {

    public ElevatorsBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Elevators block states";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialBlock(ElevatorsBlocks.ELEVATOR.get(), TexturedModel.COLUMN);
        blockModels.createTrivialCube(ElevatorsBlocks.ELEVATOR_LANDING.get());
        camouflaged(blockModels, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), ModelTemplates.CUBE_COLUMN.create(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), TextureMapping.column(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get()), blockModels.modelOutput), "_side");
        camouflaged(blockModels, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get(), ModelTemplates.CUBE_ALL.create(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get(), TextureMapping.cube(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get()), blockModels.modelOutput), "");
        instantElevator(blockModels, itemModels);
        camouflagedInstantElevator(blockModels, itemModels);
    }

    /** A column model per dye colour, from {@code block/<name>_<colour>_side} and {@code _top}. */
    private static Map<DyeColor, Identifier> colorColumns(BlockModelGenerators blockModels, String name) {
        Map<DyeColor, Identifier> models = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            String path = "block/" + name + "_" + color.getSerializedName();
            models.put(color, ModelTemplates.CUBE_COLUMN.create(resource(path), new TextureMapping()
                    .put(TextureSlot.SIDE, texture(path + "_side"))
                    .put(TextureSlot.END, texture(path + "_top")), blockModels.modelOutput));
        }
        return models;
    }

    /** The item shows the colour it was dropped with, read from its block state component. */
    private static void colorItem(ItemModelGenerators itemModels, Block b, Map<DyeColor, Identifier> models) {
        Map<DyeColor, ItemModel.Unbaked> cases = new EnumMap<>(DyeColor.class);
        models.forEach((color, model) -> cases.put(color, ItemModelUtils.plainModel(model)));
        itemModels.itemModelOutput.accept(b.asItem(), ItemModelUtils.selectBlockItemProperty(InstantElevatorBlock.COLOR, ItemModelUtils.plainModel(models.get(InstantElevatorBlock.DEFAULT_COLOR)), cases));
    }

    private void instantElevator(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        Block b = ElevatorsBlocks.INSTANT_ELEVATOR.get();
        Map<DyeColor, Identifier> models = colorColumns(blockModels, name(b));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(InstantElevatorBlock.COLOR).generate(color -> BlockModelGenerators.plainVariant(models.get(color)))));
        colorItem(itemModels, b, models);
    }

    /**
     * Undisguised it draws its colour's plain model; the loader model around that swaps in the
     * disguise, reached through {@link SpecificationBlockStateModelBuilder}.
     */
    private void camouflagedInstantElevator(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        Block b = ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get();
        Map<DyeColor, Identifier> plain = colorColumns(blockModels, name(b));
        Map<DyeColor, Identifier> camouflage = new EnumMap<>(DyeColor.class);
        plain.forEach((color, fallback) -> {
            String path = "block/" + name(b) + "_" + color.getSerializedName();
            camouflage.put(color, camouflageModel(blockModels, path, fallback, texture(path + "_side")));
        });

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(InstantElevatorBlock.COLOR).generate(color -> SpecificationBlockStateModelBuilder.specificationVariant(camouflage.get(color)))));
        colorItem(itemModels, b, plain);
    }

    /** The camouflage loader model around {@code fallback}, written beside it with a {@code _camouflage} suffix. */
    private static Identifier camouflageModel(BlockModelGenerators blockModels, String path, Identifier fallback, Material particle) {
        ModelTemplate template = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .customLoader(CamouflageModelBuilder::begin, loader -> loader.fallback(fallback))
                .build();
        return template.create(resource(path + "_camouflage"), new TextureMapping().put(TextureSlot.PARTICLE, particle), blockModels.modelOutput);
    }

    /** A camouflaged block whose properties never change its look, drawn from {@code fallback} while undisguised. */
    private static void camouflaged(BlockModelGenerators blockModels, Block b, Identifier fallback, String particleSuffix) {
        String path = "block/" + name(b);
        Identifier camouflage = camouflageModel(blockModels, path, fallback, texture(path + particleSuffix));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b, SpecificationBlockStateModelBuilder.specificationVariant(camouflage)));
        blockModels.registerSimpleItemModel(b, fallback);
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
