package it.realcitysimulator;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Builds a small test intersection, not a complete generated city.
 * Call only from an explicit admin command: it replaces a 23x23 surface. */
public final class DemoIntersection {
    private DemoIntersection() {}
    public static int build(ServerWorld world, BlockPos center) {
        int count = 0;
        int y = center.getY() - 1;
        for (int dx = -11; dx <= 11; dx++) {
            for (int dz = -11; dz <= 11; dz++) {
                boolean road = Math.abs(dx) <= 3 || Math.abs(dz) <= 3;
                if (!road) continue;
                var block = (dx == 0 || dz == 0) && !(Math.abs(dx) <= 3 && Math.abs(dz) <= 3)
                    ? Blocks.YELLOW_CONCRETE : Blocks.GRAY_CONCRETE;
                world.setBlockState(new BlockPos(center.getX() + dx, y, center.getZ() + dz),
                                    block.getDefaultState());
                count++;
            }
        }
        // Signal poles / lamps are visual placeholders; phase control is independent for now.
        for (int[] p : new int[][] {{-5,-5},{-5,5},{5,-5},{5,5}}) {
            BlockPos base = new BlockPos(center.getX()+p[0],y+1,center.getZ()+p[1]);
            for (int h=0;h<3;h++) world.setBlockState(base.up(h), Blocks.IRON_BARS.getDefaultState());
            world.setBlockState(base.up(3), Blocks.RED_WOOL.getDefaultState());
        }
        return count;
    }
}
