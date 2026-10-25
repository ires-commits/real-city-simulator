package it.realcitysimulator;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** A compact three-by-three grid of city blocks with nine intersections. */
public final class DemoDistrict {
    public static final int SPACING = 30;
    public static final int EXTENT = 47;
    private DemoDistrict() {}

    public static int build(ServerWorld world, BlockPos origin) {
        int count = 0;
        int y = origin.getY() - 1;
        for (int x = -EXTENT; x <= EXTENT; x++) {
            for (int z = -EXTENT; z <= EXTENT; z++) {
                int distX = nearestRoad(x), distZ = nearestRoad(z);
                boolean road = distX <= 3 || distZ <= 3;
                Block material;
                if (road) {
                    boolean marking = distX == 0 && distZ > 4 || distZ == 0 && distX > 4;
                    material = marking ? Blocks.YELLOW_CONCRETE : Blocks.GRAY_CONCRETE;
                } else {
                    material = Blocks.LIGHT_GRAY_CONCRETE;
                }
                world.setBlockState(at(origin,x,y,z), material.getDefaultState(), 3);
                count++;
            }
        }
        // Four compact buildings per pair of inner streets; each has a doorway and windows.
        for (int cx : new int[]{-15,15}) {
            for (int cz : new int[]{-15,15}) {
                building(world,origin,cx,cz,6 + Math.floorMod(cx*3+cz,4)*2);
            }
        }
        // Some additional buildings at perimeter plots.
        for (int cx : new int[]{-44,44}) {
            for (int cz : new int[]{-15,15}) building(world,origin,cx,cz,5);
        }
        for (int cz : new int[]{-44,44}) {
            for (int cx : new int[]{-15,15}) building(world,origin,cx,cz,5);
        }
        for (int gx = -1; gx <= 1; gx++) {
            for (int gz = -1; gz <= 1; gz++) {
                BlockPos center = origin.add(gx*SPACING,0,gz*SPACING);
                signals(world, center);
            }
        }
        return count;
    }

    private static int nearestRoad(int offset) {
        return Math.min(Math.abs(offset),Math.min(Math.abs(offset-SPACING),Math.abs(offset+SPACING)));
    }
    private static BlockPos at(BlockPos o,int dx,int y,int dz) {
        return new BlockPos(o.getX()+dx,y,o.getZ()+dz);
    }
    private static void building(ServerWorld w,BlockPos o,int cx,int cz,int height) {
        int floor = o.getY();
        for(int x=-5;x<=5;x++) for(int z=-5;z<=5;z++) {
            for(int h=0;h<=height;h++) {
                boolean shell = Math.abs(x)==5 || Math.abs(z)==5;
                if(!shell && h!=height) continue;
                Block block = h==height ? Blocks.DEEPSLATE_TILES
                    : h==0 ? Blocks.STONE_BRICKS
                    : h%3==1 && shell && (x%3==0 || z%3==0) ? Blocks.LIGHT_BLUE_STAINED_GLASS
                    : Blocks.SMOOTH_QUARTZ;
                w.setBlockState(at(o,cx+x,floor+h,cz+z),block.getDefaultState(),3);
            }
        }
        // Two-block-tall accessible doorway.
        for(int h=0;h<2;h++) w.setBlockState(at(o,cx,floor+h,cz-5),Blocks.AIR.getDefaultState(),3);
    }
    public static void signals(ServerWorld world, BlockPos c) {
        // Distinct signals for approaches: NORTH/SOUTH travel along Z, EAST/WEST along X.
        signalPole(world,c,2,-6);
        signalPole(world,c,-2,6);
        signalPole(world,c,-6,-2);
        signalPole(world,c,6,2);
        // White stopping bars: each is immediately before the central crossing.
        int y=c.getY()-1;
        for(int i=-3;i<=3;i++) {
            if(i!=0) {
                world.setBlockState(at(c,i,y,-4),Blocks.WHITE_CONCRETE.getDefaultState(),3);
                world.setBlockState(at(c,i,y,4),Blocks.WHITE_CONCRETE.getDefaultState(),3);
                world.setBlockState(at(c,-4,y,i),Blocks.WHITE_CONCRETE.getDefaultState(),3);
                world.setBlockState(at(c,4,y,i),Blocks.WHITE_CONCRETE.getDefaultState(),3);
            }
        }
    }
    public static void signalPole(ServerWorld world,BlockPos c,int dx,int dz) {
        BlockPos foot=c.add(dx,0,dz);
        for(int h=0;h<4;h++) world.setBlockState(foot.up(h),Blocks.IRON_BARS.getDefaultState(),3);
        for(int h=4;h<=6;h++) world.setBlockState(foot.up(h),Blocks.BLACK_CONCRETE.getDefaultState(),3);
    }
}
