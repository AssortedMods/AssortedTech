package com.grim3212.assorted.bridges.client.model;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.bridges.common.properties.BridgesModelProperties;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.item.DataAwareItemOverrides;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A bridge takes its geometry from a fixed shape and its texture from the block state its block
 * entity holds, so one baked bridge caches a model per stored state. The loader delegates hand it
 * the model data of the position being drawn; an item reaches the same cache through
 * {@link DataAwareItemOverrides}, which reads the stack's {@code stored_state}. The baker and sprite
 * getter are held past baking because stored states are only known while rendering.
 */
public abstract class BridgeBaseBakedModel implements IDataAwareBakedModel {

    protected final BridgeUnbakedModel.Bridge model;
    protected final ModelBaker bakery;
    protected final Function<Material, TextureAtlasSprite> spriteGetter;
    protected final ModelState transform;
    protected final ResourceLocation name;
    protected final IModelBakingContext context;

    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;

    public BridgeBaseBakedModel(IModelBakingContext context, BridgeUnbakedModel.Bridge model, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation name) {
        this.context = context;
        this.model = model;
        this.bakery = bakery;
        this.spriteGetter = spriteGetter;
        this.transform = transform;
        this.name = name;
        this.particle = spriteGetter.apply(context.getMaterial("particle").orElseGet(() -> blockMaterial(model.textures().getOrDefault("stored", "minecraft:missingno"))));
        this.overrides = new DataAwareItemOverrides(bakery, this, BridgeItemModel::modelData, BridgeItemModel::storedState);
    }

    /**
     * Concurrent because it is filled during rendering, not during baking: the stored states are only
     * known once chunks are being built, and section compilation runs on several threads at once.
     */
    protected final Map<BlockState, BakedModel> cache = new ConcurrentHashMap<>();
    protected final Map<BlockState, TextureAtlasSprite> particleCache = new ConcurrentHashMap<>();
    protected volatile BakedModel EMPTY;

    public BakedModel getCachedModel(BlockState blockState) {
        if (isEmpty(blockState)) {
            BakedModel empty = EMPTY;
            if (empty == null) {
                // No overrides: an empty bridge is whatever the model json's own stored slot names,
                // which is how one loader model per BridgeType fallback texture stands in for a
                // type check here.
                EMPTY = empty = generateModel(ImmutableMap.of());
            }
            return empty;
        }

        return this.cache.computeIfAbsent(blockState, state -> generateModel(textures(storedTexture(state))));
    }

    // Resolved from the stored texture rather than read off the cached model, so it costs no bake for
    // a state that was only ever broken and never drawn.
    public TextureAtlasSprite getCachedParticle(BlockState blockState) {
        if (isEmpty(blockState)) {
            return this.particle;
        }

        return this.particleCache.computeIfAbsent(blockState, state -> this.spriteGetter.apply(blockMaterial(storedTexture(state))));
    }

    protected static Material blockMaterial(String texture) {
        return new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.parse(texture));
    }

    private static boolean isEmpty(@Nullable BlockState blockState) {
        return blockState == null || blockState.isAir();
    }

    // Grass, podzol and mycelium are special cased: their particle sprite is the side texture, not the
    // top one that reads as the block's colour.
    // NeoForge deprecates BakedModel#getParticleIcon() for its ModelData overload, which common cannot
    // see; the stored block's own model is asked with no data, as its particle needs none.
    @SuppressWarnings("deprecation")
    private static String storedTexture(BlockState state) {
        if (state.getBlock() == Blocks.GRASS_BLOCK) {
            return "minecraft:block/grass_block_top";
        } else if (state.getBlock() == Blocks.PODZOL) {
            return "minecraft:block/dirt_podzol_top";
        } else if (state.getBlock() == Blocks.MYCELIUM) {
            return "minecraft:block/mycelium_top";
        }

        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state).getParticleIcon().contents().name().toString();
    }

    /**
     * Texture overrides putting {@code texture} in the {@code stored} slot. Not {@code #stored}: a
     * leading {@code #} marks a reference, so it would never be found.
     */
    private static ImmutableMap<String, String> textures(String texture) {
        return ImmutableMap.of("particle", texture, "stored", texture);
    }

    protected abstract BakedModel generateModel(ImmutableMap<String, String> textures);

    private static BlockState storedState(IBlockModelData extraData) {
        if (extraData.hasProperty(BridgesModelProperties.BLOCK_STATE)) {
            BlockState stored = extraData.getData(BridgesModelProperties.BLOCK_STATE);
            if (stored != null) {
                return stored;
            }
        }

        return Blocks.AIR.defaultBlockState();
    }

    // The cached models are plain vanilla models baked from the shape template, so the data-less
    // getQuads is the one to ask; NeoForge deprecates it for a ModelData overload common cannot see.
    // Every quad goes in the one layer getSupportedRenderTypes answers.
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IBlockModelData extraData, @Nullable RenderType renderType) {
        return this.getCachedModel(storedState(extraData)).getQuads(state, side, rand);
    }

    // Break and hit particles read the sprite, not the geometry; without this they show the model
    // json's own particle slot - the fallback bridge texture - whatever the block entity has stored.
    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IBlockModelData extraData) {
        return this.getCachedParticle(storedState(extraData));
    }

    /**
     * The chunk layer of the stored block, asked of that block's own model the way the loader asks.
     * An empty bridge is a laser with see-through pixels, so it never goes below cutout.
     */
    @Override
    public @NotNull Collection<RenderType> getSupportedRenderTypes(BlockState state, RandomSource rand, IBlockModelData data) {
        BlockState stored = storedState(data);
        if (stored.isAir()) {
            return List.of(RenderType.cutout());
        }

        BakedModel storedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(stored);
        List<RenderType> layers = RenderType.chunkBufferLayers();
        RenderType layer = RenderType.solid();
        for (RenderType candidate : ClientServices.MODELS.getRenderTypesFor(storedModel, stored, rand, IBlockModelData.empty())) {
            // The most see-through layer the stored block draws in, which also draws the others.
            if (layers.indexOf(candidate) > layers.indexOf(layer)) {
                layer = candidate;
            }
        }
        return List.of(layer);
    }

    /** Items take the translucent sheet, which also draws solid and cut out stored blocks. */
    @Override
    public @NotNull List<RenderType> getSupportedRenderTypes(ItemStack stack, boolean fabulous) {
        return List.of(fabulous ? Sheets.translucentItemSheet() : Sheets.translucentCullBlockSheet());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.context.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.context.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.context.useBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    // NeoForge deprecates this vanilla method for its ModelData overload, which common cannot see.
    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.particle;
    }

    // NeoForge deprecates this vanilla method for applyTransform, which common cannot see; it is still
    // abstract on vanilla's BakedModel.
    @SuppressWarnings("deprecation")
    @Override
    public ItemTransforms getTransforms() {
        return this.context.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.overrides;
    }
}
