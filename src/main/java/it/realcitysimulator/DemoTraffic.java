package it.realcitysimulator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** In-world demo enforcement. Transient state; reset on world/server restart. */
public final class DemoTraffic {
    private static final Map<ServerWorld, DemoTraffic> DEMOS = new HashMap<>();
    private final BlockPos center;
    private final IntersectionController signals = new IntersectionController();
    private final Map<UUID, Boolean> wasInside = new HashMap<>();
    private final Map<UUID, Integer> lastFineTick = new HashMap<>();
    private int tick;
    private IntersectionController.Signal previousNS, previousEW;

    public static void register(ServerWorld world, BlockPos center) {
        DEMOS.put(world, new DemoTraffic(center));
    }
    private DemoTraffic(BlockPos center) { this.center = center.toImmutable(); }
    public static void tickAll() {
        for (var entry : DEMOS.entrySet()) entry.getValue().tick(entry.getKey());
    }
    private void tick(ServerWorld world) {
        tick++;
        signals.tick();
        var northSouth = signals.northSouth();
        var eastWest = signals.eastWest();
        if (previousNS != northSouth || previousEW != eastWest) {
            // Visual prototype, four signals at corners; axis grouping is simplified.
            setLamp(world, -5, -5, northSouth);
            setLamp(world, 5, 5, northSouth);
            setLamp(world, -5, 5, eastWest);
            setLamp(world, 5, -5, eastWest);
            previousNS = northSouth;
            previousEW = eastWest;
        }
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!(player.getVehicle() instanceof HorseEntity horse)
                || horse.getCustomName() == null
                || !horse.getCustomName().getString().equals("RCS Prototype Car")) {
                wasInside.remove(player.getUuid());
                continue;
            }
            double dx = player.getX() - center.getX();
            double dz = player.getZ() - center.getZ();
            boolean withinArea = Math.abs(dx) < 11 && Math.abs(dz) < 11;
            boolean inside = Math.abs(dx) < 3.5 && Math.abs(dz) < 3.5;
            boolean previous = wasInside.getOrDefault(player.getUuid(), false);
            wasInside.put(player.getUuid(), inside);
            if (!withinArea || !inside || previous) continue;
            // Heading roughly parallel to N/S => N/S lights apply.
            var velocity = horse.getVelocity();
            boolean northSouthTravel = Math.abs(velocity.z) >= Math.abs(velocity.x);
            var signal = northSouthTravel ? northSouth : eastWest;
            if (signal == IntersectionController.Signal.RED
                && tick - lastFineTick.getOrDefault(player.getUuid(), -10000) > 200) {
                lastFineTick.put(player.getUuid(), tick);
                player.sendMessage(Text.literal("RCS: SEMAFORO ROSSO! Multa 150 EUR. Pattuglia in arrivo!"), false);
                summonPolice(world, player);
            }
        }
    }
    private void setLamp(ServerWorld world, int dx, int dz, IntersectionController.Signal state) {
        var block = switch (state) {
            case GREEN -> net.minecraft.block.Blocks.GREEN_WOOL;
            case YELLOW -> net.minecraft.block.Blocks.YELLOW_WOOL;
            case RED -> net.minecraft.block.Blocks.RED_WOOL;
        };
        world.setBlockState(center.add(dx, 2, dz), block.getDefaultState());
    }
    private void summonPolice(ServerWorld world, ServerPlayerEntity player) {
        ZombieEntity officer = EntityType.ZOMBIE.create(world);
        if (officer == null) return;
        officer.refreshPositionAndAngles(center.getX() + 7.5, center.getY(), center.getZ() + 7.5, 0, 0);
        officer.setCustomName(Text.literal("RCS Police Prototype"));
        officer.setCustomNameVisible(true);
        officer.setPersistent();
        officer.setTarget(player);
        world.spawnEntity(officer);
    }
    private DemoTraffic() {}
}
