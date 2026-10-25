# Real City Simulator — Fabric 1.21.1

Early integration branch. **Not yet the complete playable city driving mod.**

## Requirements
- Minecraft Java 1.21.1
- Fabric Loader 0.16.10+ and Fabric API for 1.21.1
- Java 21 for building

## Build
Use Gradle 8.14.3 with Java 21 and run `gradle build`. Download the generated mod JAR from the **Build Real City Simulator** action's `real-city-simulator-jar` artifact after a **successful** run. Do not install a `-sources.jar`.

## Current playable integration (prototype)
- `/rcs car` spawns a *tamed saddled horse* labeled RCS Prototype Car; right-click to ride, use vanilla controls. This is an engineering placeholder, **not** a 3D car or actual car physics integration.
- `/rcs demo` lays out a 23x23 crossroad and starts synchronized visual lamps (red, yellow, green).
- Riding the RCS prototype across the middle of the demo intersection on red triggers a 150 EUR *notification* and spawns a zombie labelled RCS Police Prototype that chases the player. This is only a demonstration: no fine economy, custom police NPCs or police vehicles yet.
- The road changes blocks in the world; **use a backup or creative test world**. The signal controller resets after server restart and must be rearmed with `/rcs demo`.

## Commands
- `/rcs status` — displays diagnostic traffic-light state.
- `/rcs car` — spawns the rideable demonstration vehicle.
- `/rcs demo` — **creative/admin only**: constructs a small crossroad under your feet. Warning: this replaces existing ground blocks in a 23 × 23 area. Use a dedicated test world and flat terrain.

## Implemented
- Fabric entrypoints and a basic speed HUD when riding vanilla vehicles
- Standalone Java traffic lights, two-direction intersection timing, vehicle physics, traffic law, road routing, police dispatch and wanted-level rules
- A simple on-demand demo intersection made from vanilla blocks

## Not implemented yet
Custom 3D cars or real car physics controls, active NPC road traffic, registered traffic signal blocks, sustained police AI and police vehicles, realistically generated city, persistent ticket economy and multiplayer synchronization. The prototype does include basic real-world stop-line detection and a hostile vanilla NPC pursuer.

## Next major development
Add registered car entities and server-authoritative controls, road/signalling blocks, synchronized traffic agents, enforcement and police pursuit, city generation and persistence; test the whole system together before calling it version 1.0.
