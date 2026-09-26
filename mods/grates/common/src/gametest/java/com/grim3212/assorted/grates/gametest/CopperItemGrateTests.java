package com.grim3212.assorted.grates.gametest;

import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.grates.common.block.ItemGrateBlock;
import com.grim3212.assorted.grates.common.block.WeatheringItemGrateBlock;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.grates.gametest.GratesTestSupport.name;

/**
 * The copper grates weathering on their own, and scraping and waxing the way any other copper does.
 */
final class CopperItemGrateTests {

    private CopperItemGrateTests() {
    }

    private static final BlockPos GRATE = new BlockPos(4, 1, 4);

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("copper_item_grates_oxidise", CopperItemGrateTests::copperItemGratesOxidise);
        out.accept("copper_item_grates_scrape_and_wax", CopperItemGrateTests::copperItemGratesScrapeAndWax);
    }

    /** Each stage but oxidized is randomly ticked and steps to the next; a waxed grate never ticks. */
    private static void copperItemGratesOxidise(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();

        WeatheringCopperCollection.zipApply(WeatheringCopperCollection.STATES, GratesBlocks.COPPER_ITEM_GRATES.weathering(), (age, grate) -> {
            ItemGrateBlock block = grate.get();
            boolean shouldOxidise = age != WeatherState.OXIDIZED;
            helper.setBlock(GRATE, block);

            // The baked property that schedules the change; the loaders' oxidation registries load too late to ask.
            if (helper.getBlockState(GRATE).isRandomlyTicking() != shouldOxidise) {
                wrong.add(name(block) + (shouldOxidise ? " is not randomly ticked, so it never oxidises" : " is randomly ticked but has nowhere to go"));
                return;
            }
            if (!shouldOxidise) {
                return;
            }

            Optional<BlockState> next = ((WeatheringItemGrateBlock) block).getNext(helper.getBlockState(GRATE));
            Block expected = GratesBlocks.COPPER_ITEM_GRATES.weathering().pick(age.next()).get();
            if (next.filter(state -> state.is(expected)).isEmpty()) {
                wrong.add(name(block) + " oxidises into " + next.map(state -> name(state.getBlock())).orElse("nothing") + ", not " + name(expected));
            }
        });

        GratesBlocks.COPPER_ITEM_GRATES.waxed().forEach(grate -> {
            helper.setBlock(GRATE, grate.get());
            if (helper.getBlockState(GRATE).isRandomlyTicking()) {
                wrong.add(name(grate.get()) + " is waxed but still randomly ticked");
            }
        });

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " oxidation problem(s): " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Asked the way the axe and honeycomb ask, because NeoForge ignores the vanilla
     * {@code WAXABLES} / {@code NEXT_BY_BLOCK} fields. Miss a registry and it fails silently.
     */
    private static void copperItemGratesScrapeAndWax(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();

        GratesBlocks.COPPER_ITEM_GRATES.weathering().progressMapping((from, to) -> {
            Optional<BlockState> scraped = WeatheringCopper.getPrevious(to.get().defaultBlockState());
            if (scraped.filter(state -> state.is(from.get())).isEmpty()) {
                wrong.add(name(to.get()) + " scrapes back to " + scraped.map(state -> name(state.getBlock())).orElse("nothing") + ", not " + name(from.get()));
            }
        });

        GratesBlocks.COPPER_ITEM_GRATES.zipUnwaxedWaxed((unwaxed, waxed) -> {
            Optional<BlockState> waxedState = HoneycombItem.getWaxed(unwaxed.get().defaultBlockState());
            if (waxedState.filter(state -> state.is(waxed.get())).isEmpty()) {
                wrong.add(name(unwaxed.get()) + " waxes into " + waxedState.map(state -> name(state.getBlock())).orElse("nothing") + ", not " + name(waxed.get()));
            }
        });

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " scrape/wax problem(s) on " + Services.PLATFORM.getPlatformName() + ": " + String.join("; ", wrong));
        helper.succeed();
    }
}
