package it.realcitysimulator;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public final class RealCitySimulator implements ModInitializer {
    private static final TrafficLight DEMO_LIGHT = new TrafficLight();
    private static final IntersectionController DEMO_INTERSECTION = new IntersectionController();
    public static TrafficLight demoLight() { return DEMO_LIGHT; }
    @Override public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> { DEMO_LIGHT.tick(); DEMO_INTERSECTION.tick(); });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(CommandManager.literal("rcs")
                .then(CommandManager.literal("status").executes(context -> {
                    context.getSource().sendFeedback(
                        () -> Text.literal("Real City Simulator 0.1.0-dev.5 | demo light: " + DEMO_LIGHT.state() + " | N/S: " + DEMO_INTERSECTION.northSouth() + " | E/W: " + DEMO_INTERSECTION.eastWest()), false);
                    return 1;
                }))));
    }
}
