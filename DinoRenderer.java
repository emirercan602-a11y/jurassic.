package com.jurassicsurvival.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class DinoRenderer<T extends Mob> extends MobRenderer<T, DinoModel<T>> {
    private final DinoSpec spec;

    public DinoRenderer(EntityRendererProvider.Context ctx, DinoSpec spec) {
        super(ctx, new DinoModel<T>(ctx.bakeLayer(spec.layer())), spec.shadow());
        this.spec = spec;
    }

    @Override
    protected void scale(T entity, PoseStack pose, float partialTick) {
        float s = spec.scale() * (entity.isBaby() ? 0.5F : 1.0F);
        pose.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return spec.texture();
    }
}
