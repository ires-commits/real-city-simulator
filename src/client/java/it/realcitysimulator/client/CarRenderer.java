package it.realcitysimulator.client;

import it.realcitysimulator.CarEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/** Model made of transformed Minecraft block meshes; no horse rendering. */
public final class CarRenderer extends EntityRenderer<CarEntity> {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/block/red_concrete.png");
    private final net.minecraft.client.render.block.BlockRenderManager blocks;

    public CarRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.blocks = context.getBlockRenderManager();
        this.shadowRadius = 0.9f;
    }

    @Override public Identifier getTexture(CarEntity entity) { return TEXTURE; }

    @Override
    public void render(CarEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider consumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw())));
        boolean police = entity.getCustomName() != null && entity.getCustomName().getString().contains("POLIZIA");
        part(matrices, consumers, light, (police ? Blocks.BLUE_CONCRETE : Blocks.RED_CONCRETE).getDefaultState(), 0, 0.45f, 0, 1.55f, 0.40f, 2.65f);
        if (police) {
            part(matrices, consumers, light, Blocks.REDSTONE_BLOCK.getDefaultState(), -0.29f, 1.20f, -0.12f, 0.43f, 0.15f, 0.25f);
            part(matrices, consumers, light, Blocks.LAPIS_BLOCK.getDefaultState(), 0.29f, 1.20f, -0.12f, 0.43f, 0.15f, 0.25f);
        }
        part(matrices, consumers, light, Blocks.BLACK_STAINED_GLASS.getDefaultState(), 0, 0.88f, -0.12f, 1.19f, 0.55f, 1.28f);
        part(matrices, consumers, light, Blocks.GRAY_CONCRETE.getDefaultState(), 0, 0.34f, -1.30f, 1.45f, 0.20f, 0.20f);
        for (float x : new float[]{-0.79f, 0.79f}) {
            for (float z : new float[]{-0.85f, 0.85f}) {
                part(matrices, consumers, light, Blocks.BLACK_CONCRETE.getDefaultState(), x, 0.24f, z, 0.32f, 0.47f, 0.53f);
            }
        }
        for (float x : new float[]{-0.52f, 0.52f}) {
            part(matrices, consumers, light, Blocks.SEA_LANTERN.getDefaultState(), x, 0.55f, -1.34f, 0.32f, 0.19f, 0.11f);
        }
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, consumers, light);
    }

    private void part(MatrixStack matrices, VertexConsumerProvider consumers, int light,
                      net.minecraft.block.BlockState block, float x, float y, float z,
                      float width, float height, float length) {
        matrices.push();
        matrices.translate(x - width/2f, y - height/2f, z - length/2f);
        matrices.scale(width, height, length);
        blocks.renderBlockAsEntity(block, matrices, consumers, light, net.minecraft.client.render.OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
