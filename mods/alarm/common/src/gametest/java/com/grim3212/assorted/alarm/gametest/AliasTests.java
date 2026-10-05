package com.grim3212.assorted.alarm.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.alarm.common.block.blockentity.AlarmBlockEntityTypes;
import com.grim3212.assorted.alarm.common.sounds.AlarmSounds;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A world saved when this was all one mod, Assorted Tech, still has its alarms in it, each with its sound. */
final class AliasTests {

    private AliasTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assortedtech_ids_still_load", AliasTests::assortedtechIdsStillLoad);
    }

    private static void assortedtechIdsStillLoad(GameTestHelper helper) {
        for (IRegistryObject<Item> item : AlarmBlocks.ITEMS.getEntries()) {
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + old(item.getId()) + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(item.get()), "a stack saved as " + old(item.getId()) + " reads back as " + stack);
        }

        for (IRegistryObject<Block> block : AlarmBlocks.BLOCKS.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.BLOCK.getValue(old(block.getId())), block.get(), "the block saved as " + old(block.getId()));
        }

        for (IRegistryObject<BlockEntityType<?>> type : AlarmBlockEntityTypes.BLOCK_ENTITIES.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(old(type.getId())), type.get(), "the block entity saved as " + old(type.getId()));
        }

        for (IRegistryObject<SoundEvent> sound : AlarmSounds.SOUNDS.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.SOUND_EVENT.getValue(old(sound.getId())), sound.get(), "the sound saved as " + old(sound.getId()));
        }
        helper.succeed();
    }

    private static Identifier old(Identifier id) {
        return Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, id.getPath());
    }
}
