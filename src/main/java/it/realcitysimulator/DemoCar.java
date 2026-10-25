package it.realcitysimulator;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/**
 * Playable vehicle placeholder with immediate vanilla rider controls.
 * This is deliberately a horse-based prototype, NOT a custom car model.
 */
public final class DemoCar {
    public static int spawn(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        HorseEntity horse = EntityType.HORSE.create(world);
        if (horse == null) return 0;
        var origin = source.getPosition();
        horse.refreshPositionAndAngles(origin.x, origin.y, origin.z, source.getRotation().y, 0);
        horse.setCustomName(Text.literal("RCS Prototype Car"));
        horse.setCustomNameVisible(true);
        horse.setTame(true);
        horse.setOwnerUuid(source.getPlayer() != null ? source.getPlayer().getUuid() : null);
        horse.saddle(new ItemStack(Items.SADDLE), null);
        var speed = horse.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speed != null) speed.setBaseValue(0.34);
        world.spawnEntity(horse);
        source.sendFeedback(() -> Text.literal("RCS: veicolo sperimentale creato. Clic destro per salire; WASD per guidare."), false);
        return 1;
    }
    private DemoCar() {}
}
