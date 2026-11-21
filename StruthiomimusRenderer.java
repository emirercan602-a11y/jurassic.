package com.jurassicsurvival.client;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.entity.StruthiomimusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StruthiomimusRenderer extends MobRenderer<StruthiomimusEntity, StruthiomimusModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(JurassicSurvival.MOD_ID, "textures/entity/struthiomimus.png");

    public StruthiomimusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new StruthiomimusModel(ctx.bakeLayer(StruthiomimusModel.LAYER)), 0.55F);
    }

    @Override protected void scale(StruthiomimusEntity entity, PoseStack pose, float partialTick) {
        if(entity.isBaby()) pose.scale(0.55F,0.55F,0.55F);
    }

    @Override public ResourceLocation getTextureLocation(StruthiomimusEntity entity) {
        return TEXTURE;
    }
}
