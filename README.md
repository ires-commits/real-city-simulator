# Real City Simulator — Fabric 1.21.1

Early integration branch. **Not yet the complete playable city driving mod.**

## Requirements
- Minecraft Java 1.21.1
- Fabric Loader 0.16.10+ and Fabric API for 1.21.1
- Java 21 for building

## Build
Use Gradle 8.14.3 with Java 21 and run `gradle build`. Download the generated mod JAR from the **Build Real City Simulator** action's `real-city-simulator-jar` artifact after a **successful** run. Do not install a `-sources.jar`.

## Current playable integration (prototype)
- `/rcs car` now spawns a dedicated `realcitysimulator:car` entity, rendered with a block-built 3D car body, wheels and windows. Right-click to ride. Movement is temporarily based on Minecraft boat input/physics, **not yet realistic car physics**.
- `/rcs demo` lays out a 23x23 crossroad and starts synchronized visual lamps (red, yellow, green).
- Riding the RCS prototype across the middle of the demo intersection on red triggers a 150 EUR *notification* and spawns a zombie labelled RCS Police Prototype that chases the player. This is only a demonstration: no fine economy, custom police NPCs or police vehicles yet.
- The road changes blocks in the world; **use a backup or creative test world**. The signal controller resets after server restart and must be rearmed with `/rcs demo`.

## Expanded district (October 2026)
- `/rcs district` builds a **171 x 171 block** district, with **49 intersections**, **36 accessible two-storey buildings**, interior room partitions, tables, chairs, shelves, lighting, and ladders to reach the upper floors.
- This command modifies or replaces blocks in a large area and does **not** provide an undo. **Use a NEW superflat creative test world**, far from anything worth keeping. Large block placement may briefly affect performance.
- Overhead Formula-1-inspired gantries are positioned above each incoming approach. Three horizontal, Formula-1-inspired signal lenses (red, amber, green) are surrounded by opaque black blocks and a black backing. Only one colored signal is bright; inactive signals are black. This is an interim vanilla-block model, not a fully directional textured traffic-light block.
- Each intersection has independent N/S and E/W phases; passing a red light in the prototype car triggers an infraction and a basic police-car pursuer. The pursuer still uses direct chasing, **not road-aware AI**.
- Run `/rcs district` again after reloading the world to restart demo signal controllers. Re-running also reconstructs the area.
- The old `/rcs demo` small test intersection remains available.

## Commands
- `/rcs status` — displays diagnostic traffic-light state.
- `/rcs car` — spawns the rideable demonstration vehicle.
- `/rcs district` — creates the larger district described above.
- `/rcs demo` — **creative/admin only**: constructs a small crossroad under your feet. Warning: this replaces existing ground blocks in a 23 × 23 area. Use a dedicated test world and flat terrain.

## Implemented
- Fabric entrypoints and a basic speed HUD when riding vanilla vehicles
- Standalone Java traffic lights, two-direction intersection timing, vehicle physics, traffic law, road routing, police dispatch and wanted-level rules
- A simple on-demand demo intersection made from vanilla blocks

## Not implemented yet
Realistic car physics and polished 3D car models, active NPC road traffic, registered traffic signal blocks, sustained police AI and police vehicles, realistically generated city, persistent ticket economy and multiplayer synchronization. The prototype does include basic real-world stop-line detection and a hostile vanilla NPC pursuer.

## Next major development
Add registered car entities and server-authoritative controls, road/signalling blocks, synchronized traffic agents, enforcement and police pursuit, city generation and persistence; test the whole system together before calling it version 1.0.
