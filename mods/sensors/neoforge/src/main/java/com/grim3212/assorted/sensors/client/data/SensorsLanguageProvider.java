package com.grim3212.assorted.sensors.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.sensors.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block or item whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class SensorsLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public SensorsLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("tooltip.sensor.detects.wood", "Detects all entities");
        this.add("tooltip.sensor.detects.stone", "Detects only living mobs");
        this.add("tooltip.sensor.detects.iron", "Detects players");
        this.add("tooltip.sensor.detects.mossy_cobblestone", "Detects only monsters");
        this.add("tooltip.sensor.detects.prismarine", "Detects only water mobs");
        this.add("tooltip.sensor.detects.gold", "Detects only item entities");
        this.add("tooltip.sensor.detects.emerald", "Detects only villager mobs");
        this.add("tooltip.sensor.detects.netherrack", "Detects only nether mobs");
        this.add("tooltip.sensor.detects.cobweb", "Detects only arthropods");
        this.add("tooltip.sensor.detects.end_stone", "Detects only end mobs");
        this.add("tooltip.sensor.detects.hay_bale", "Detects only tameable mobs");
        this.add("tooltip.sensor.detects.feather", "Detects only flying mobs");

        this.add("message.sensor.range", "Sensor range update to: %s");

        this.add("item.assortedsensors.gps", "GPS");
        this.add("block.assortedsensors.gps_sensor", "GPS Sensor");
        this.add("block.assortedsensors.upgraded_gps_sensor", "Upgraded GPS Sensor");

        this.add("tooltip.gps.usage", "Right click a block to store the space in front of it");
        this.add("tooltip.gps.stored", "Stored: %s, %s, %s");
        this.add("message.gps.stored", "GPS stored %s, %s, %s");
        this.add("message.gps.cleared", "GPS position cleared");

        this.add("gps_sensor.screen.mode", "Detects: %s");
        this.add("gps_sensor.screen.mode.players", "Players");
        this.add("gps_sensor.screen.mode.mobs", "Mobs");
        this.add("gps_sensor.screen.mode.items", "Items");
        this.add("gps_sensor.screen.filter", "Filter");
        this.add("gps_sensor.screen.any_players", "Any player, or type a name");
        this.add("gps_sensor.screen.any_mobs", "Any mob, or an id like zombie");
        this.add("gps_sensor.screen.add_players", "Player name to add");
        this.add("gps_sensor.screen.add_mobs", "Mob id or #tag to add");
        this.add("gps_sensor.screen.add_items", "Item id or #tag");
        this.add("gps_sensor.screen.help.players", "Press + or Enter to add the name to the list. The sensor watches for anyone on it.");
        this.add("gps_sensor.screen.help.mobs", "Press + or Enter to add it to the list. The sensor watches for anything on it. Start with # for an entity tag, like #minecraft:undead.");
        this.add("gps_sensor.screen.help.items", "Press + or Enter to add it to the list, or click an item on the slot instead. The sensor watches for anything on the list. Start with # for an item tag, like #minecraft:logs.");
        this.add("gps_sensor.screen.entries", "Filter: %s/%s");
        this.add("gps_sensor.screen.entries.empty.players", "Nothing added, so any player counts.");
        this.add("gps_sensor.screen.entries.empty.mobs", "Nothing added, so any mob counts.");
        this.add("gps_sensor.screen.entries.empty.items", "Nothing added, so any item counts.");
        this.add("gps_sensor.screen.problem.tag", "Only an upgraded sensor takes #tags");
        this.add("gps_sensor.screen.problem.player_tag", "Players have no tags");
        this.add("gps_sensor.screen.problem.unknown_tag", "No such tag: %s");
        this.add("gps_sensor.screen.problem.unknown", "Nothing is called %s");
        this.add("gps_sensor.screen.problem.duplicate", "Already on the list");
        this.add("gps_sensor.screen.problem.full", "The list is full");
        this.add("gps_sensor.screen.any_item", "Any Item");
        this.add("gps_sensor.screen.item", "Item: %s");
        this.add("gps_sensor.screen.item_hint", "Click an item on the slot");
        this.add("gps_sensor.screen.item_change_hint", "Click another item to swap it, or click empty handed to clear it");
        this.add("gps_sensor.screen.item_add_hint", "Click an item on the slot to add it to the list");
        this.add("gps_sensor.screen.radius", "Radius: %s");
        this.add("gps_sensor.screen.show.on", "Show Watched Area: On");
        this.add("gps_sensor.screen.show.off", "Show Watched Area: Off");
        this.add("gps_sensor.screen.status.none", "No position.");
        this.add("gps_sensor.screen.status.other_dimension", "The GPS's position is in another dimension");
        this.add("gps_sensor.screen.gps_hint", "Put a GPS here to watch the position it stored");
        this.add("gps_sensor.screen.status.good", "Watching %s, %s, %s");
        this.add("gps_sensor.screen.status.blocked", "%s, %s, %s is solid, nothing can be there");
        this.add("gps_sensor.screen.status.out_of_range", "The position is more than %s blocks away");

        this.add("tag.item.assortedsensors.sensors", "Sensors");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.addSensorsChapter();
    }

    private void addSensorsChapter() {
        this.add("manual.assortedtech.chapter.sensors", "Sensors");

        this.add("manual.assortedtech.chapter.sensors.sensors.title", "Sensors");
        this.add("manual.assortedtech.chapter.sensors.sensors",
                "A sensor watches the space in front of it and gives out redstone while something it cares "
                        + "about is there. A light on the face shows when it has seen one." + BREAK
                        + "Which things it cares about is decided by what it is made of, so the recipe is the "
                        + "setting. The next page lists them.");

        this.add("manual.assortedtech.chapter.sensors.gps.title", "GPS");
        this.add("manual.assortedtech.chapter.sensors.gps",
                "Right click a block with a GPS to store the space in front of it. Sneak and use it in the air to forget it.");

        this.add("manual.assortedtech.chapter.sensors.gps_sensor.title", "GPS Sensor");
        this.add("manual.assortedtech.chapter.sensors.gps_sensor",
                "A GPS sensor watches the position a GPS stored rather than the space in front of it, so it "
                        + "can sit behind a wall or under the floor. Right click it and put the GPS in the slot at "
                        + "the top right." + BREAK
                        + "You can open the GUI and choose between players, mobs and items, and "
                        + "narrow it to one player, one kind of mob or one item.");

        this.add("manual.assortedtech.chapter.sensors.upgraded_gps_sensor.title", "Upgraded GPS Sensor");
        this.add("manual.assortedtech.chapter.sensors.upgraded_gps_sensor",
                "The upgraded GPS sensor can be farther away and supports an adjusteable range." + BREAK
                        + "The upgraded GPS sensor supports a list of 6 entries each as well as supporting entity and item tags now." + BREAK 
                        + "Start an entry with # to use a tag, like #minecraft:undead for "
                        + "every undead mob or #minecraft:logs for every log.");

        this.add("manual.assortedtech.chapter.sensors.triggers.title", "What Each One Watches");
        this.add("manual.assortedtech.chapter.sensors.triggers",
                "Wood sees everything, including dropped items and arrows. Stone sees anything alive. Iron "
                        + "sees only players." + BREAK
                        + "Mossy cobblestone sees hostile mobs, cobweb sees spiders and their kin, netherrack "
                        + "sees what belongs in the Nether, and end stone sees what belongs in the End." + BREAK
                        + "Gold sees dropped items, emerald sees villagers and wandering traders, prismarine "
                        + "sees what swims, feather sees what flies, and a hay bale sees tamed pets.");
    }
}
