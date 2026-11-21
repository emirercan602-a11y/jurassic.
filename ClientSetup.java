package com.jurassicsurvival.client;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.registry.ModEntities;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Tüm varlıkların model katmanı + renderer kaydı (eski sürümlerde sadece 3 tanesi vardı; kalanlar istemciyi çökertirdi). */
@Mod.EventBusSubscriber(modid = JurassicSurvival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions e) {
        // elle yazılmış modelleriniz
        e.registerLayerDefinition(TRexModel.LAYER, TRexModel::createBodyLayer);
        e.registerLayerDefinition(SpinosaurusModel.LAYER, SpinosaurusModel::createBodyLayer);
        e.registerLayerDefinition(StruthiomimusModel.LAYER, StruthiomimusModel::createBodyLayer);
        e.registerLayerDefinition(GyrosphereModel.LAYER, GyrosphereModel::createBodyLayer);
        // veri tabanlı modeller
        for (DinoSpec spec : DinoSpecs.ALL) {
            e.registerLayerDefinition(spec.layer(), spec::createLayer);
        }
    }

    private static <T extends Mob> void dino(EntityRenderersEvent.RegisterRenderers e, EntityType<T> type, DinoSpec spec) {
        e.registerEntityRenderer(type, ctx -> new DinoRenderer<T>(ctx, spec));
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModEntities.TREX.get(), TRexRenderer::new);
        e.registerEntityRenderer(ModEntities.SPINOSAURUS.get(), SpinosaurusRenderer::new);
        e.registerEntityRenderer(ModEntities.STRUTHIOMIMUS.get(), StruthiomimusRenderer::new);
        e.registerEntityRenderer(ModEntities.GYROSPHERE.get(), GyrosphereRenderer::new);
        e.<Boat>registerEntityRenderer(ModEntities.ESCAPE_BOAT.get(), ctx -> new BoatRenderer(ctx, false));

        dino(e, ModEntities.ANKYLOSAURUS.get(), DinoSpecs.ANKYLOSAURUS);
        dino(e, ModEntities.PACHYCEPHALOSAURUS.get(), DinoSpecs.PACHYCEPHALOSAURUS);
        dino(e, ModEntities.TRICERATOPS.get(), DinoSpecs.TRICERATOPS);
        dino(e, ModEntities.STEGOSAURUS.get(), DinoSpecs.STEGOSAURUS);
        dino(e, ModEntities.PARASAUR.get(), DinoSpecs.PARASAUR);
        dino(e, ModEntities.BRACHIOSAURUS.get(), DinoSpecs.BRACHIOSAURUS);
        dino(e, ModEntities.COMPSOGNATHUS.get(), DinoSpecs.COMPSOGNATHUS);
        dino(e, ModEntities.VELOCIRAPTOR.get(), DinoSpecs.VELOCIRAPTOR);
        dino(e, ModEntities.DIMORPHODON.get(), DinoSpecs.DIMORPHODON);
        dino(e, ModEntities.BARYONYX.get(), DinoSpecs.BARYONYX);
        dino(e, ModEntities.MOSASAURUS.get(), DinoSpecs.MOSASAURUS);
    }
}
