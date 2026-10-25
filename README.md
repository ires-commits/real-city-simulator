# Real City Simulator — Fabric 1.21.1

Early integration branch. **Not yet the complete playable city driving mod.**

## Requirements
- Minecraft Java 1.21.1
- Fabric Loader 0.16.10+ and Fabric API for 1.21.1
- Java 21 for building

## Build
Use Gradle 8.14.3 with Java 21 and run `gradle build`. Download the generated mod JAR from the **Build Real City Simulator** action's `real-city-simulator-jar` artifact after a **successful** run. Do not install a `-sources.jar`.

## Commands
- `/rcs status` — displays diagnostic traffic-light state.
- `/rcs demo` — **creative/admin only**: constructs a small crossroad under your feet. Warning: this replaces existing ground blocks in a 23 × 23 area. Use a dedicated test world and flat terrain.

## Implemented
- Fabric entrypoints and a basic speed HUD when riding vanilla vehicles
- Standalone Java traffic lights, two-direction intersection timing, vehicle physics, traffic law, road routing, police dispatch and wanted-level rules
- A simple on-demand demo intersection made from vanilla blocks

## Not implemented yet
Custom 3D cars or player car controls, active NPC road traffic, functional traffic signal blocks, traffic violation detection in the Minecraft world, patrolling/chasing police entities, realistically generated city, persistence and multiplayer sync. The engine classes are foundations, not live gameplay.

## Next major development
Add registered car entities and server-authoritative controls, road/signalling blocks, synchronized traffic agents, enforcement and police pursuit, city generation and persistence; test the whole system together before calling it version 1.0.
