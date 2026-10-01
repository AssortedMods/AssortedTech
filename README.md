# Assorted Tech

Contains an assorted group of additions based around tech, machines and logic. Each group is also its own mod if you only want some of them.

- [Assorted Tech](mods/tech) has all of them in one download
- [Assorted Elevators](mods/elevators) adds elevators, instant elevators and their camouflaged versions
- [Assorted Bridges](mods/bridges) adds the bridge controls
- [Assorted Gravity](mods/gravity) adds attractors, repulsors, gravitors and gravity boots
- [Assorted Grates](mods/grates) adds item grates that dropped items fall through, including the old metal mesh
- [Assorted Extruder](mods/extruder) adds the extruder
- [Assorted Sensors](mods/sensors) adds sensors, the GPS and GPS sensors
- [Assorted Redstone](mods/redstone) adds flip flop torches and glowstone torches
- [Assorted Alarm](mods/alarm) adds the alarm
- [Assorted Fan](mods/fan) adds the fan
- [Assorted Spikes](mods/spikes) adds spikes

Worlds made with Assorted Tech 9.x work with any of these.

Requires [Assorted Lib](https://github.com/AssortedMods/AssortedLib). Branches are per Minecraft version and `26.2` is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* NeoForge version, or Fabric Loader and Fabric API versions
* Which of these mods you have and their versions
* Assorted Lib version
* The full `latest.log`, and the crash report if the game crashed

## Building

You need JDK 25. Each mod is its own folder under `mods`. The build setup comes from
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild) and `assortedbuild_version` in `gradle.properties`
picks the version.

To build against a local copy of Assorted Lib, publish it first.

```bash
cd ../AssortedLib && ./gradlew publishToMavenLocal
```

Some useful commands

```bash
./gradlew build                                  # build every mod
./gradlew :elevators:neoforge:runClient          # run one mod
./gradlew :all:neoforge:runClient                # run every mod together
./gradlew runGameTestServer runGameTest          # gametests on NeoForge and Fabric
./gradlew runClientData runServerData            # datagen
```

Generated resources are committed. The NeoForge datagen writes them for both loaders.

## License

[GPL-3.0-only](LICENSE).
