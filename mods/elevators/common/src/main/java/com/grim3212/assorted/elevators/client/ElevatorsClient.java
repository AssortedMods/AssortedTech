package com.grim3212.assorted.elevators.client;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.client.color.CamouflageBlockColor;
import com.grim3212.assorted.elevators.client.model.CamouflageModelLoader;
import com.grim3212.assorted.elevators.client.model.CamouflageUnbakedModel;
import com.grim3212.assorted.elevators.client.render.ElevatorCarRenderer;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.elevators.common.block.InstantElevatorBlock;
import com.grim3212.assorted.elevators.common.entity.ElevatorsEntities;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;

public class ElevatorsClient {

    /**
     * The colour an instant elevator item carries in its block state component, which its item
     * model's overrides pick a model by. Its value is {@link #colorPropertyValue}.
     */
    public static final ResourceLocation COLOR_PROPERTY = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "color");

    private static final ClampedItemPropertyFunction COLOR = (stack, level, entity, seed) -> colorPropertyValue(colorOf(stack));

    public static void init() {
        ClientServices.CLIENT.registerEntityRenderer(() -> ElevatorsEntities.ELEVATOR_CAR.get(), ElevatorCarRenderer::new);

        ClientServices.CLIENT.registerModelLoader(CamouflageUnbakedModel.LOADER_NAME, CamouflageModelLoader.INSTANCE);
        ClientServices.CLIENT.registerBlockColor(new CamouflageBlockColor(), () -> ImmutableList.of(ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get()));

        ClientServices.CLIENT.registerItemProperty(() -> ElevatorsBlocks.INSTANT_ELEVATOR.get().asItem(), COLOR_PROPERTY, COLOR);
        ClientServices.CLIENT.registerItemProperty(() -> ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get().asItem(), COLOR_PROPERTY, COLOR);
    }

    /** Each colour's own step, so an override that asks for at least this matches it and every later colour. */
    public static float colorPropertyValue(DyeColor color) {
        return color.getId() / 16.0F;
    }

    private static DyeColor colorOf(ItemStack stack) {
        DyeColor color = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).get(InstantElevatorBlock.COLOR);
        return color != null ? color : InstantElevatorBlock.DEFAULT_COLOR;
    }
}
