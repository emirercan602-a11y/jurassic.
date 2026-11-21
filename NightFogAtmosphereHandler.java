package com.jurassicsurvival.client;

import com.jurassicsurvival.JurassicSurvival;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** v1.9: gece ve yağmurda yoğun, soğuk mavi-gri sis. Yalnızca Overworld'de ve su/lav dışında uygulanır. */
@Mod.EventBusSubscriber(modid = JurassicSurvival.MOD_ID, value = Dist.CLIENT)
public class NightFogAtmosphereHandler {

    private static boolean isNight(Level level) {
        long dayTime = level.getDayTime() % 24000L;
        return dayTime >= 12500L && dayTime <= 23500L;
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Level level = Minecraft.getInstance().level;
        if (level == null || level.dimension() != Level.OVERWORLD) return;
        if (event.getCamera().getFluidInCamera() != FogType.NONE) return; // su altı / lav sisini bozma

        boolean night = isNight(level);
        if (night || level.isRaining()) {
            event.setNearPlaneDistance(night ? 4.0F : 12.0F);
            event.setFarPlaneDistance(night ? 36.0F : 58.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Level level = Minecraft.getInstance().level;
        if (level == null || level.dimension() != Level.OVERWORLD) return;
        if (isNight(level)) {
            event.setRed(0.05F);
            event.setGreen(0.07F);
            event.setBlue(0.11F);
        }
    }
}
