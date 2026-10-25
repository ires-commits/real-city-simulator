package it.realcitysimulator;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/** Spawns a dedicated 3D car entity instead of a horse. */
public final class DemoCar {
    public static int spawn(ServerCommandSource source) {
        CarEntity car = RealCitySimulator.CAR.create(source.getWorld());
        if (car == null) return 0;
        var pos = source.getPosition();
        car.refreshPositionAndAngles(pos.x, pos.y, pos.z, source.getRotation().y, 0);
        car.setCustomName(Text.literal("RCS City Car"));
        source.getWorld().spawnEntity(car);
        source.sendFeedback(() -> Text.literal("RCS: automobile 3D creata. Clic destro per salire; controlli direzionali vanilla."), false);
        return 1;
    }
    private DemoCar() {}
}
