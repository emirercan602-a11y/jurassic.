package com.jurassicsurvival.client;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.entity.GyrosphereEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GyrosphereRenderer extends EntityRenderer<GyrosphereEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(JurassicSurvival.MOD_ID, "textures/entity/gyrosphere.png");
    private final GyrosphereModel model;

    public GyrosphereRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new GyrosphereModel(ctx.bakeLayer(GyrosphereModel.LAYER));
        this.shadowRadius = 0.9F;
    }

    @Override
    public void render(GyrosphereEntity entity, float entityYaw, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.translate(0.0F, -1.501F, 0.0F);
        VertexConsumer vc = buffers.getBuffer(model.renderType(TEXTURE));
        model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(GyrosphereEntity entity) {
        return TEXTURE;
    }
}
