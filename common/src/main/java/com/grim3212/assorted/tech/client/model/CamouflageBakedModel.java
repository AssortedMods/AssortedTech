package com.grim3212.assorted.tech.client.model;

import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.tech.common.properties.TechModelProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Draws the disguise's own baked model, so a disguise looks exactly like the block it copies, and
 * the fallback while there is none. Disguises are opaque full blocks, so the fallback's flags hold.
 */
public class CamouflageBakedModel implements IDataAwareBakedModel {

    private final BlockStateModel fallback;

    public CamouflageBakedModel(BlockStateModel fallback) {
        this.fallback = fallback;
    }

    private BlockStateModel modelFor(IBlockModelData extraData) {
        BlockState disguise = disguise(extraData);
        return disguise == null ? this.fallback : Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(disguise);
    }

    private static @Nullable BlockState disguise(IBlockModelData extraData) {
        if (extraData.hasProperty(TechModelProperties.BLOCK_STATE)) {
            BlockState disguise = extraData.getData(TechModelProperties.BLOCK_STATE);
            return disguise == null || disguise.isAir() ? null : disguise;
        }
        return null;
    }

    // Deprecated by NeoForge for a level/pos aware overload only its patched jar has; vanilla models still answer this one.
    @SuppressWarnings("deprecation")
    @Override
    public void collectParts(@NotNull RandomSource random, @NotNull IBlockModelData extraData, @NotNull List<BlockStateModelPart> output) {
        this.modelFor(extraData).collectParts(random, output);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Material.Baked particleMaterial(@NotNull IBlockModelData extraData) {
        return this.modelFor(extraData).particleMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Material.Baked particleMaterial() {
        return this.fallback.particleMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return this.fallback.materialFlags();
    }
}
