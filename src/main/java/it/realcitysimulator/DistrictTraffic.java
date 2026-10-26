package it.realcitysimulator;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Forty-nine-intersection district: directional signal groups and simple patrol response.
 * Transient demo state; use /rcs district again after restarting the world.
 */
public final class DistrictTraffic {
    private static final Map<ServerWorld, DistrictTraffic> ACTIVE = new HashMap<>();
    private final BlockPos origin;
    private final IntersectionController[] controllers = new IntersectionController[DemoDistrict.COUNT * DemoDistrict.COUNT];
    private final Map<UUID, String> lastIntersection = new HashMap<>();
    private final Map<UUID, Integer> lastViolation = new HashMap<>();
    private final Map<UUID, CarEntity> patrols = new HashMap<>();
    private int tick;
    private final IntersectionController.Signal[] lastNorthSouth = new IntersectionController.Signal[DemoDistrict.COUNT * DemoDistrict.COUNT];
    private final IntersectionController.Signal[] lastEastWest = new IntersectionController.Signal[9];

    private DistrictTraffic(BlockPos origin) {
        this.origin = origin.toImmutable();
        for(int i=0;i<controllers.length;i++) controllers[i]=new IntersectionController();
    }
    public static void register(ServerWorld world,BlockPos origin) { ACTIVE.put(world,new DistrictTraffic(origin)); }
    public static void tickAll() {
        for(var entry: ACTIVE.entrySet()) entry.getValue().tick(entry.getKey());
    }
    private BlockPos center(int ix,int iz) {
        return origin.add((ix-DemoDistrict.RADIUS)*DemoDistrict.SPACING,0,(iz-DemoDistrict.RADIUS)*DemoDistrict.SPACING);
    }
    private void tick(ServerWorld world) {
        tick++;
        for(int ix=0;ix<DemoDistrict.COUNT;ix++) for(int iz=0;iz<DemoDistrict.COUNT;iz++) {
            int index=ix*DemoDistrict.COUNT+iz;
            controllers[index].tick();
            var ns=controllers[index].northSouth();
            var ew=controllers[index].eastWest();
            if(ns!=lastNorthSouth[index] || ew!=lastEastWest[index]) {
                BlockPos c=center(ix,iz);
                show(world,c,2,-6,ns);
                show(world,c,-2,6,ns);
                show(world,c,-6,-2,ew);
                show(world,c,6,2,ew);
                lastNorthSouth[index]=ns;
                lastEastWest[index]=ew;
            }
        }
        for(ServerPlayerEntity player: world.getPlayers()) {
            if(!(player.getVehicle() instanceof CarEntity car)) {
                lastIntersection.remove(player.getUuid());
                continue;
            }
            BlockPos near=nearestIntersection(player.getPos());
            double dx=player.getX()-near.getX(), dz=player.getZ()-near.getZ();
            boolean inside=Math.abs(dx)<3.5 && Math.abs(dz)<3.5;
            String key=near.getX()+":"+near.getZ();
            String old=lastIntersection.get(player.getUuid());
            if(!inside) lastIntersection.remove(player.getUuid());
            else if(!key.equals(old)) {
                lastIntersection.put(player.getUuid(),key);
                int ix=Math.round((near.getX()-origin.getX())/(float)DemoDistrict.SPACING)+DemoDistrict.RADIUS;
                int iz=Math.round((near.getZ()-origin.getZ())/(float)DemoDistrict.SPACING)+DemoDistrict.RADIUS;
                if(ix<0||ix>=DemoDistrict.COUNT||iz<0||iz>=DemoDistrict.COUNT) continue;
                Vec3d v=car.getVelocity();
                boolean ns=Math.abs(v.z)>=Math.abs(v.x);
                var phase=ns?controllers[ix*3+iz].northSouth():controllers[ix*3+iz].eastWest();
                if(phase==IntersectionController.Signal.RED
                    && tick-lastViolation.getOrDefault(player.getUuid(),-100000)>200) {
                    lastViolation.put(player.getUuid(),tick);
                    player.sendMessage(Text.literal("RCS | SEMAFORO ROSSO: multa 150 EUR. Pattuglia in arrivo!"),false);
                    dispatch(world,player,near);
                }
            }
        }
        updatePatrols(world);
    }
    private BlockPos nearestIntersection(Vec3d pos) {
        int ix=Math.max(-DemoDistrict.RADIUS,Math.min(DemoDistrict.RADIUS,(int)Math.round((pos.x-origin.getX())/DemoDistrict.SPACING)));
        int iz=Math.max(-1,Math.min(1,(int)Math.round((pos.z-origin.getZ())/DemoDistrict.SPACING)));
        return origin.add(ix*DemoDistrict.SPACING,0,iz*DemoDistrict.SPACING);
    }
    private void show(ServerWorld w,BlockPos c,int dx,int dz,IntersectionController.Signal signal) {
        // Only the driver-facing front is colored. Side walls and opaque back stay black.
        BlockPos face=c.add(dx,0,dz);
        w.setBlockState(face.up(8),
            (signal==IntersectionController.Signal.RED?Blocks.REDSTONE_BLOCK:Blocks.BLACK_CONCRETE).getDefaultState(),3);
        w.setBlockState(face.up(7),
            (signal==IntersectionController.Signal.YELLOW?Blocks.GOLD_BLOCK:Blocks.BLACK_CONCRETE).getDefaultState(),3);
        w.setBlockState(face.up(6),
            (signal==IntersectionController.Signal.GREEN?Blocks.EMERALD_BLOCK:Blocks.BLACK_CONCRETE).getDefaultState(),3);
    }
    private void dispatch(ServerWorld world,ServerPlayerEntity target,BlockPos intersection) {
        CarEntity patrol=patrols.get(target.getUuid());
        if(patrol!=null && !patrol.isRemoved()) return;
        patrol=RealCitySimulator.CAR.create(world);
        if(patrol==null) return;
        patrol.refreshPositionAndAngles(intersection.getX()+9,intersection.getY()+1,intersection.getZ()+9,0,0);
        patrol.setCustomName(Text.literal("RCS POLIZIA"));
        patrol.setCustomNameVisible(true);
        world.spawnEntity(patrol);
        patrols.put(target.getUuid(),patrol);
    }
    private void updatePatrols(ServerWorld world) {
        Iterator<Map.Entry<UUID,CarEntity>> iterator=patrols.entrySet().iterator();
        while(iterator.hasNext()) {
            var entry=iterator.next();
            CarEntity patrol=entry.getValue();
            ServerPlayerEntity target=world.getServer().getPlayerManager().getPlayer(entry.getKey());
            if(target==null || target.getWorld()!=world || patrol.isRemoved()) {
                if(!patrol.isRemoved()) patrol.discard();
                iterator.remove(); continue;
            }
            double dx=target.getX()-patrol.getX(), dz=target.getZ()-patrol.getZ();
            double distance=Math.hypot(dx,dz);
            if(distance<3.0) {
                if(tick%40==0) target.sendMessage(Text.literal("RCS POLIZIA | Fermati! Infrazione rilevata."),true);
                patrol.setVelocity(0,patrol.getVelocity().y,0);
            } else {
                double speed=Math.min(.30, .09+distance*.002);
                patrol.setVelocity(dx/distance*speed,patrol.getVelocity().y,dz/distance*speed);
                patrol.setYaw((float)(Math.toDegrees(Math.atan2(-dx,dz))));
                patrol.velocityModified=true;
            }
        }
    }
}
