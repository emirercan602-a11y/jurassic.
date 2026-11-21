package com.jurassicsurvival.client;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.entity.SpinosaurusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SpinosaurusRenderer extends MobRenderer<SpinosaurusEntity, SpinosaurusModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(JurassicSurvival.MOD_ID, "textures/entity/spinosaurus.png");

    public SpinosaurusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpinosaurusModel(ctx.bakeLayer(SpinosaurusModel.LAYER)), 1.7F);
    }

    @Override
    protected void scale(SpinosaurusEntity entity, PoseStack pose, float partialTick) {
        pose.scale(1.75F, 1.75F, 1.75F);
    }

    @Override
    public ResourceLocation getTextureLocation(SpinosaurusEntity entity) {
        return TEXTURE;
    }
}
