package com.jurassicsurvival.client;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.entity.TRexEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TRexRenderer extends MobRenderer<TRexEntity, TRexModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(JurassicSurvival.MOD_ID, "textures/entity/trex.png");

    public TRexRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TRexModel(ctx.bakeLayer(TRexModel.LAYER)), 1.6F);
    }

    @Override
    protected void scale(TRexEntity entity, PoseStack pose, float partialTick) {
        pose.scale(1.65F, 1.65F, 1.65F);
    }

    @Override
    public ResourceLocation getTextureLocation(TRexEntity entity) {
        return TEXTURE;
    }
}
