package com.grim3212.assorted.elevators.client.data;

import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.client.ElevatorsClient;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.elevators.common.block.InstantElevatorBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.EnumMap;
import java.util.Map;

/** Block states, block models and the block items' models: every block of the mod is an item too. */
public class ElevatorsBlockstateProvider extends BlockStateProvider {

    public ElevatorsBlockstateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Elevators block states";
    }

    @Override
    protected void registerStatesAndModels() {
        Block elevator = ElevatorsBlocks.ELEVATOR.get();
        ModelFile elevatorModel = column(name(elevator));
        this.simpleBlock(elevator, elevatorModel);
        this.simpleBlockItem(elevator, elevatorModel);

        Block landing = ElevatorsBlocks.ELEVATOR_LANDING.get();
        ModelFile landingModel = this.models().cubeAll(name(landing), resource("block/" + name(landing)));
        this.simpleBlock(landing, landingModel);
        this.simpleBlockItem(landing, landingModel);

        Block camouflaged = ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get();
        camouflaged(camouflaged, column(name(camouflaged)), resource("block/" + name(camouflaged) + "_side"));

        Block camouflagedLanding = ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get();
        camouflaged(camouflagedLanding, this.models().cubeAll(name(camouflagedLanding), resource("block/" + name(camouflagedLanding))), resource("block/" + name(camouflagedLanding)));

        instantElevator();
        camouflagedInstantElevator();
    }

    /** A column from {@code block/<name>_side} and {@code block/<name>_top}. */
    private ModelFile column(String name) {
        return this.models().cubeColumn(name, resource("block/" + name + "_side"), resource("block/" + name + "_top"));
    }

    /** A column model per dye colour, from {@code block/<name>_<colour>_side} and {@code _top}. */
    private Map<DyeColor, ModelFile> colorColumns(String name) {
        Map<DyeColor, ModelFile> models = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            models.put(color, column(name + "_" + color.getSerializedName()));
        }
        return models;
    }

    /**
     * The item shows the colour it was dropped with: {@link ElevatorsClient} reads it from the
     * stack's block state component as a property, one override per colour, in the order of their
     * ids so each matches only its own.
     */
    private void colorItem(Block b, Map<DyeColor, ModelFile> models) {
        ItemModelBuilder item = this.itemModels().getBuilder(name(b)).parent(models.get(InstantElevatorBlock.DEFAULT_COLOR));
        for (DyeColor color : DyeColor.values()) {
            item.override().predicate(ElevatorsClient.COLOR_PROPERTY, ElevatorsClient.colorPropertyValue(color)).model(models.get(color)).end();
        }
    }

    private void instantElevator() {
        Block b = ElevatorsBlocks.INSTANT_ELEVATOR.get();
        Map<DyeColor, ModelFile> models = colorColumns(name(b));
        this.getVariantBuilder(b).forAllStates(state -> ConfiguredModel.builder().modelFile(models.get(state.getValue(InstantElevatorBlock.COLOR))).build());
        colorItem(b, models);
    }

    /**
     * Undisguised it draws its colour's plain model; the loader model around that swaps in the
     * disguise.
     */
    private void camouflagedInstantElevator() {
        Block b = ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get();
        Map<DyeColor, ModelFile> plain = colorColumns(name(b));
        Map<DyeColor, ModelFile> camouflage = new EnumMap<>(DyeColor.class);
        plain.forEach((color, fallback) -> {
            String path = name(b) + "_" + color.getSerializedName();
            camouflage.put(color, camouflageModel(path, fallback, resource("block/" + path + "_side")));
        });

        this.getVariantBuilder(b).forAllStates(state -> ConfiguredModel.builder().modelFile(camouflage.get(state.getValue(InstantElevatorBlock.COLOR))).build());
        colorItem(b, plain);
    }

    /** The camouflage loader model around {@code fallback}, written beside it with a {@code _camouflage} suffix. */
    private ModelFile camouflageModel(String name, ModelFile fallback, ResourceLocation particle) {
        BlockModelBuilder builder = this.models().getBuilder(resource("block/" + name + "_camouflage").toString()).texture("particle", particle);
        builder.customLoader(CamouflageModelBuilder::begin).fallback(fallback.getLocation());
        return builder;
    }

    /** A camouflaged block whose properties never change its look, drawn from {@code fallback} while undisguised. */
    private void camouflaged(Block b, ModelFile fallback, ResourceLocation particle) {
        this.simpleBlock(b, camouflageModel(name(b), fallback, particle));
        this.simpleBlockItem(b, fallback);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
