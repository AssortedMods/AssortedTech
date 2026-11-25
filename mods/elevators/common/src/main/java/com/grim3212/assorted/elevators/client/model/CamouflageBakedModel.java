package com.grim3212.assorted.elevators.client.model;

import com.grim3212.assorted.elevators.common.properties.ElevatorsModelProperties;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Draws the disguise's own baked model, so a disguise looks exactly like the block it copies, and
 * the fallback while there is none. The loader delegates hand it the model data of the position
 * being drawn, which carries the disguise.
 */
public class CamouflageBakedModel implements IDataAwareBakedModel {

    private final BakedModel fallback;

    public CamouflageBakedModel(BakedModel fallback) {
        this.fallback = fallback;
    }

    private static @Nullable BlockState disguise(IBlockModelData extraData) {
        if (extraData.hasProperty(ElevatorsModelProperties.BLOCK_STATE)) {
            BlockState disguise = extraData.getData(ElevatorsModelProperties.BLOCK_STATE);
            return disguise == null || disguise.isAir() ? null : disguise;
        }
        return null;
    }

    private static BakedModel modelOf(BlockState disguise) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(disguise);
    }

    // The disguise's own model is asked for its quads as its block state would be, with no data:
    // disguises are plain models. NeoForge deprecates this for a ModelData overload common cannot see.
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IBlockModelData extraData, @Nullable RenderType renderType) {
        BlockState disguise = disguise(extraData);
        return disguise == null ? this.fallback.getQuads(state, side, rand) : modelOf(disguise).getQuads(disguise, side, rand);
    }

    // Break and hit particles read the sprite, so they come off the disguise too.
    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IBlockModelData extraData) {
        BlockState disguise = disguise(extraData);
        return disguise == null ? this.fallback.getParticleIcon() : modelOf(disguise).getParticleIcon();
    }

    /** The layer the disguise draws in, asked of its own model the way the loader asks; every quad goes there. */
    @Override
    public @NotNull Collection<RenderType> getSupportedRenderTypes(BlockState state, RandomSource rand, IBlockModelData data) {
        BlockState disguise = disguise(data);
        if (disguise == null) {
            return ClientServices.MODELS.getRenderTypesFor(this.fallback, state, rand, IBlockModelData.empty());
        }

        List<RenderType> layers = RenderType.chunkBufferLayers();
        RenderType layer = RenderType.solid();
        for (RenderType candidate : ClientServices.MODELS.getRenderTypesFor(modelOf(disguise), disguise, rand, IBlockModelData.empty())) {
            // The most see-through layer the disguise draws in, which also draws the others.
            if (layers.indexOf(candidate) > layers.indexOf(layer)) {
                layer = candidate;
            }
        }
        return List.of(layer);
    }

    @Override
    public @NotNull List<RenderType> getSupportedRenderTypes(ItemStack stack, boolean fabulous) {
        return ClientServices.MODELS.getRenderTypesFor(this.fallback, stack, fabulous);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.fallback.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.fallback.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.fallback.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    // NeoForge deprecates this vanilla method for its ModelData overload, which common cannot see.
    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.fallback.getParticleIcon();
    }

    // NeoForge deprecates this vanilla method for applyTransform, which common cannot see; it is still
    // abstract on vanilla's BakedModel.
    @SuppressWarnings("deprecation")
    @Override
    public ItemTransforms getTransforms() {
        return this.fallback.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
