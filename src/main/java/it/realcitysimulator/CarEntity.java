package it.realcitysimulator;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Dedicated 3D car with prototype land-speed adjustment.
 * Steering and passenger input still use vanilla boat controls.
 */
public final class CarEntity extends BoatEntity {
    private static final double MAX_GROUND_SPEED = 0.48; // blocks per tick
    private static final double LAND_ASSIST = 1.28;

    public CarEntity(EntityType<? extends BoatEntity> type, World world) {
        super(type, world);
    }

    @Override public float getStepHeight() { return 1.0f; }

    @Override public void tick() {
        super.tick();
        if (!hasPassengers() || !isOnGround()) return;
        Vec3d v = getVelocity();
        double horizontal = Math.hypot(v.x, v.z);
        if (horizontal < 0.001) return;
        double desired = Math.min(MAX_GROUND_SPEED, horizontal * LAND_ASSIST);
        double scale = desired / horizontal;
        setVelocity(v.x * scale, v.y, v.z * scale);
    }
}
