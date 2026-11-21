package com.jurassicsurvival.registry;

import com.jurassicsurvival.JurassicSurvival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ModTags {
    private ModTags() {}

    /** Benzin kokusundan etkilenmeyen canlılar (ör. Scorpios Rex). Veri paketiyle genişletilebilir. */
    public static final TagKey<EntityType<?>> IGNORES_GASOLINE =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JurassicSurvival.MOD_ID, "ignores_gasoline"));
}
