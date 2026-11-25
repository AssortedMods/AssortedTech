package com.grim3212.assorted.extruder.client.data;

import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.client.render.ExtruderItemRenderer;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** The extruder items, each drawn as the extruder itself. */
public class ExtruderItemModelProvider extends ItemModelProvider {

    public ExtruderItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return Constants.MOD_NAME + " item models";
    }

    /**
     * The extruder in 3D, drawn by {@link ExtruderItemRenderer}. Every item model is a
     * {@code builtin/entity} one through {@code item/extruder}, which carries {@code block/block}'s
     * display settings, since a special model cannot inherit them from a parent of its own. The
     * particle has to be an atlas sprite rather than the entity texture.
     */
    @Override
    protected void registerModels() {
        ItemModelBuilder base = this.getBuilder("extruder").parent(new ModelFile.UncheckedModelFile("builtin/entity")).guiLight(BlockModel.GuiLight.SIDE);
        blockDisplay(base);

        ResourceLocation particle = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/extruder_particle");
        ExtruderItems.EXTRUDERS.values().forEach(extruder ->
                this.getBuilder(BuiltInRegistries.ITEM.getKey(extruder.get()).getPath()).parent(base).texture("particle", particle));
    }

    /** {@code block/block}'s display settings, which every block item shows itself with. */
    private static void blockDisplay(ItemModelBuilder builder) {
        builder.transforms()
                .transform(ItemDisplayContext.GUI).rotation(30, 225, 0).translation(0, 0, 0).scale(0.625F).end()
                .transform(ItemDisplayContext.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25F).end()
                .transform(ItemDisplayContext.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.375F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).translation(0, 0, 0).scale(0.4F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).translation(0, 0, 0).scale(0.4F).end()
                .end();
    }
}
