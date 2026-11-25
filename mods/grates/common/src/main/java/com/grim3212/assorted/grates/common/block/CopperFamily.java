package com.grim3212.assorted.grates.common.block;

import net.minecraft.world.level.block.WeatheringCopper.WeatherState;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * One of something for each copper block of a family: the four oxidation stages, then the same four
 * waxed. 1.21.1 has no shared copper collection, so the family is built from {@link WeatherState}'s
 * own values, which is the order vanilla lists them in.
 */
public record CopperFamily<T>(ByState<T> weathering, ByState<T> waxed) {

    /** Calls {@code factory} with each stage and its name: {@code copper_x}, {@code exposed_copper_x}, ... then {@code waxed_} in front of each. */
    public static <T> CopperFamily<T> create(String name, BiFunction<WeatherState, String, T> weathering, BiFunction<WeatherState, String, T> waxed) {
        ByState<T> unwaxed = ByState.create(state -> weathering.apply(state, stageName(state, name)));
        ByState<T> waxedFamily = ByState.create(state -> waxed.apply(state, "waxed_" + stageName(state, name)));
        return new CopperFamily<>(unwaxed, waxedFamily);
    }

    /** The stage {@code state} oxidises into; {@code OXIDIZED} stays as it is. */
    public static WeatherState next(WeatherState state) {
        WeatherState[] states = WeatherState.values();
        return states[Math.min(state.ordinal() + 1, states.length - 1)];
    }

    private static String stageName(WeatherState state, String name) {
        return state == WeatherState.UNAFFECTED ? name : state.getSerializedName() + "_" + name;
    }

    /** Each unwaxed member with the waxed one it becomes. */
    public void zipUnwaxedWaxed(BiConsumer<T, T> action) {
        for (WeatherState state : WeatherState.values()) {
            action.accept(this.weathering.pick(state), this.waxed.pick(state));
        }
    }

    /** All eight, unwaxed then waxed. */
    public List<T> asList() {
        List<T> all = new ArrayList<>(this.weathering.asList());
        all.addAll(this.waxed.asList());
        return all;
    }

    public void forEach(Consumer<T> action) {
        this.asList().forEach(action);
    }

    /** One of something per oxidation stage. */
    public record ByState<T>(Map<WeatherState, T> byState) {

        static <T> ByState<T> create(java.util.function.Function<WeatherState, T> factory) {
            Map<WeatherState, T> map = new EnumMap<>(WeatherState.class);
            for (WeatherState state : WeatherState.values()) {
                map.put(state, factory.apply(state));
            }
            return new ByState<>(map);
        }

        public T pick(WeatherState state) {
            return this.byState.get(state);
        }

        public T unaffected() {
            return this.pick(WeatherState.UNAFFECTED);
        }

        /** Each stage with the one it oxidises into. */
        public void progressMapping(BiConsumer<T, T> action) {
            WeatherState[] states = WeatherState.values();
            for (int i = 0; i + 1 < states.length; i++) {
                action.accept(this.pick(states[i]), this.pick(states[i + 1]));
            }
        }

        public void forEach(Consumer<T> action) {
            this.asList().forEach(action);
        }

        public List<T> asList() {
            List<T> all = new ArrayList<>();
            for (WeatherState state : WeatherState.values()) {
                all.add(this.pick(state));
            }
            return all;
        }
    }
}
