package it.realcitysimulator;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;

/**
 * Dedicated Fabric entity for the first car. Reuses the vanilla boat
 * rider input/packet synchronization temporarily, not its appearance.
 */
public final class CarEntity extends BoatEntity {
    public CarEntity(EntityType<? extends BoatEntity> type, World world) {
        super(type, world);
    }

    @Override
    public float getStepHeight() {
        return 1.0f;
    }
}
