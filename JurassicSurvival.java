package com.jurassicsurvival;

import com.jurassicsurvival.registry.ModCreativeTabs;
import com.jurassicsurvival.registry.ModEntities;
import com.jurassicsurvival.registry.ModItems;
import com.jurassicsurvival.spawn.ModSpawns;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(JurassicSurvival.MOD_ID)
public class JurassicSurvival {
    public static final String MOD_ID = "jurassicsurvival";

    public JurassicSurvival() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        modBus.addListener(ModEntities::registerAttributes);
        modBus.addListener(ModSpawns::registerPlacements);
    }
}
