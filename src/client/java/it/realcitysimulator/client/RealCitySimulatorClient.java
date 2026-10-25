package it.realcitysimulator.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import it.realcitysimulator.RealCitySimulator;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** Prototype HUD: shows approximate horizontal vehicle speed when mounted. */
public final class RealCitySimulatorClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        EntityRendererRegistry.register(RealCitySimulator.CAR, CarRenderer::new);
        HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));
    }
    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.player.getVehicle() == null || client.options.hudHidden) return;
        var vehicle = client.player.getVehicle();
        var velocity = vehicle.getVelocity();
        int kmh = (int) Math.round(Math.hypot(velocity.x, velocity.z) * 72.0);
        String display = "RCS  " + kmh + " km/h  |  LIMIT 50";
        int x = context.getScaledWindowWidth() - client.textRenderer.getWidth(display) - 16;
        int y = context.getScaledWindowHeight() - 52;
        context.drawTextWithShadow(client.textRenderer, Text.literal(display), x, y, kmh > 50 ? 0xFF5555 : 0xFFFFFF);
    }
}
