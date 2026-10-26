package it.realcitysimulator;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Seven-by-seven junctions, 36 furnished two-storey buildings.
 * Run only in a fresh flat creative world: replaces terrain across 171x171 blocks.
 */
public final class DemoDistrict {
    public static final int SPACING = 26;
    public static final int RADIUS = 3;
    public static final int COUNT = 2 * RADIUS + 1;
    public static final int EXTENT = RADIUS * SPACING + 7;
    private DemoDistrict() {}

    public static int build(ServerWorld world, BlockPos origin) {
        int count = 0;
        int ground = origin.getY() - 1;
        for (int x = -EXTENT; x <= EXTENT; x++) {
            for (int z = -EXTENT; z <= EXTENT; z++) {
                int distX = nearestRoad(x), distZ = nearestRoad(z);
                boolean road = distX <= 3 || distZ <= 3;
                boolean yellow = (distX == 0 && distZ > 4) || (distZ == 0 && distX > 4);
                Block material = road
                    ? (yellow ? Blocks.YELLOW_CONCRETE : Blocks.GRAY_CONCRETE)
                    : (Math.floorMod(x,7)==0 || Math.floorMod(z,7)==0
                         ? Blocks.STONE_BRICKS : Blocks.LIGHT_GRAY_CONCRETE);
                place(world, at(origin,x,ground,z),material);
                count++;
            }
        }
        for(int ix=-RADIUS; ix<RADIUS; ix++) for(int iz=-RADIUS; iz<RADIUS; iz++) {
            int cx=ix*SPACING+SPACING/2, cz=iz*SPACING+SPACING/2;
            building(world,origin,cx,cz,ix,iz);
        }
        for(int ix=-RADIUS;ix<=RADIUS;ix++) for(int iz=-RADIUS;iz<=RADIUS;iz++) {
            signals(world,origin.add(ix*SPACING,0,iz*SPACING));
        }
        return count;
    }

    private static int nearestRoad(int value) {
        int grid=Math.round((float)value/SPACING);
        grid=Math.max(-RADIUS,Math.min(RADIUS,grid));
        return Math.abs(value-grid*SPACING);
    }
    private static BlockPos at(BlockPos o,int x,int y,int z) {
        return new BlockPos(o.getX()+x,y,o.getZ()+z);
    }
    private static void place(ServerWorld w,BlockPos p,Block b) {
        w.setBlockState(p,b.getDefaultState(),3);
    }
    private static void building(ServerWorld w,BlockPos o,int cx,int cz,int ix,int iz) {
        int floor=o.getY();
        Block facade=(Math.floorMod(ix+iz,3)==0)?Blocks.BRICKS:
            (Math.floorMod(ix+iz,3)==1)?Blocks.SMOOTH_QUARTZ:Blocks.STONE_BRICKS;
        // Two storeys: hollow rooms and a floor/ceiling at Y+5.
        for(int x=-5;x<=5;x++) for(int z=-5;z<=5;z++) for(int h=0;h<=11;h++) {
            boolean edge=Math.abs(x)==5||Math.abs(z)==5;
            boolean slab=h==5 || h==11;
            if(!edge && !slab) continue;
            Block material=(h==11)?Blocks.DEEPSLATE_TILES:
                (h==5)?Blocks.SPRUCE_PLANKS:
                (h==0)?Blocks.STONE_BRICKS:
                (h%3==2 && edge && (Math.abs(x)==5 ? Math.abs(z)<=2 : Math.abs(x)<=2))
                     ?Blocks.LIGHT_BLUE_STAINED_GLASS:facade;
            place(w,at(o,cx+x,floor+h,cz+z),material);
        }
        // Doorway and interior partition on the ground floor.
        for(int h=0;h<3;h++) place(w,at(o,cx,floor+h,cz-5),Blocks.AIR);
        for(int z=-3;z<=3;z++) for(int h=0;h<4;h++) {
            if(z==-1 && h<2) continue; // interior doorway
            place(w,at(o,cx,floor+h,cz+z),Blocks.WHITE_CONCRETE);
        }
        // Stairwell: ladder attached to rear wall and opening in upper floor.
        place(w,at(o,cx+2,floor+5,cz+4),Blocks.AIR);
        for(int h=0;h<10;h++) {
            BlockPos ladder=at(o,cx+2,floor+h,cz+4);
            w.setBlockState(ladder,Blocks.LADDER.getDefaultState()
                .with(LadderBlock.FACING,Direction.NORTH),3);
        }
        // Furniture: table, chairs, lamp and shelving on both storeys.
        for(int h:new int[]{0,6}) {
            place(w,at(o,cx-3,floor+h,cz+1),Blocks.CRAFTING_TABLE);
            place(w,at(o,cx-3,floor+h,cz+2),Blocks.BOOKSHELF);
            place(w,at(o,cx+3,floor+h,cz-2),Blocks.OAK_PLANKS);
            place(w,at(o,cx+2,floor+h,cz-2),Blocks.OAK_STAIRS);
            place(w,at(o,cx+3,floor+h+1,cz+2),Blocks.LANTERN);
        }
    }

    /** Gantry signals above each approach. Drivers face the colored side; black backs hide it. */
    public static void signals(ServerWorld w,BlockPos c) {
        // Front coordinate: incoming from N, S, W and E respectively.
        gantry(w,c,0,-5,0,-1);
        gantry(w,c,0,5,0,1);
        gantry(w,c,-5,0,-1,0);
        gantry(w,c,5,0,1,0);
        int ground=c.getY()-1;
        for(int i=-3;i<=3;i++) {
            if(i==0) continue;
            place(w,at(c,i,ground,-4),Blocks.WHITE_CONCRETE);
            place(w,at(c,i,ground,4),Blocks.WHITE_CONCRETE);
            place(w,at(c,-4,ground,i),Blocks.WHITE_CONCRETE);
            place(w,at(c,4,ground,i),Blocks.WHITE_CONCRETE);
        }
    }
    private static void gantry(ServerWorld w,BlockPos c,int x,int z,int outX,int outZ) {
        // Face is centered above lane at Y+6..8, supported by a high transverse beam.
        // N/S use X as the transverse axis; E/W use Z.
        int tx=outZ!=0?1:0, tz=outX!=0?1:0;
        BlockPos mast=c.add(x+tx*5,0,z+tz*5);
        for(int h=0;h<=10;h++) place(w,mast.up(h),Blocks.IRON_BLOCK);
        for(int t=-5;t<=5;t++) place(w,c.add(x+tx*t,10,z+tz*t),Blocks.IRON_BARS);
        for(int y=5;y<=9;y++) for(int t=-1;t<=1;t++) {
            if(y>=6&&y<=8&&t==0) continue;
            place(w,c.add(x+tx*t,y,z+tz*t),Blocks.BLACK_CONCRETE);
        }
        // Opaque rear: the three lenses can only be read from their intended approach.
        for(int y=5;y<=9;y++) for(int t=-1;t<=1;t++) {
            place(w,c.add(x-outX+tx*t,y,z-outZ+tz*t),Blocks.BLACK_CONCRETE);
        }
        for(int y=6;y<=8;y++) place(w,c.add(x,y,z),Blocks.BLACK_CONCRETE);
    }
}
