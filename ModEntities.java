package com.jurassicsurvival.registry;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JurassicSurvival.MOD_ID);

    // ---- Otçullar ----
    public static final RegistryObject<EntityType<StruthiomimusEntity>> STRUTHIOMIMUS = ENTITIES.register("struthiomimus",
            () -> EntityType.Builder.of(StruthiomimusEntity::new, MobCategory.CREATURE).sized(0.75F, 1.35F).clientTrackingRange(10).build("struthiomimus"));
    public static final RegistryObject<EntityType<AnkylosaurusEntity>> ANKYLOSAURUS = ENTITIES.register("ankylosaurus",
            () -> EntityType.Builder.of(AnkylosaurusEntity::new, MobCategory.CREATURE).sized(1.35F, 1.25F).clientTrackingRange(10).build("ankylosaurus"));
    public static final RegistryObject<EntityType<PachycephalosaurusEntity>> PACHYCEPHALOSAURUS = ENTITIES.register("pachycephalosaurus",
            () -> EntityType.Builder.of(PachycephalosaurusEntity::new, MobCategory.CREATURE).sized(0.85F, 1.45F).clientTrackingRange(10).build("pachycephalosaurus"));
    public static final RegistryObject<EntityType<TriceratopsEntity>> TRICERATOPS = ENTITIES.register("triceratops",
            () -> EntityType.Builder.of(TriceratopsEntity::new, MobCategory.CREATURE).sized(1.45F, 1.55F).clientTrackingRange(10).build("triceratops"));
    public static final RegistryObject<EntityType<StegosaurusEntity>> STEGOSAURUS = ENTITIES.register("stegosaurus",
            () -> EntityType.Builder.of(StegosaurusEntity::new, MobCategory.CREATURE).sized(1.45F, 1.35F).clientTrackingRange(10).build("stegosaurus"));
    public static final RegistryObject<EntityType<ParasaurEntity>> PARASAUR = ENTITIES.register("parasaur",
            () -> EntityType.Builder.of(ParasaurEntity::new, MobCategory.CREATURE).sized(1.0F, 1.45F).clientTrackingRange(10).build("parasaur"));
    public static final RegistryObject<EntityType<BrachiosaurusEntity>> BRACHIOSAURUS = ENTITIES.register("brachiosaurus",
            () -> EntityType.Builder.of(BrachiosaurusEntity::new, MobCategory.CREATURE).sized(3.5F, 8.5F).clientTrackingRange(16).build("brachiosaurus"));

    // ---- Küçük / orta etçiller ----
    public static final RegistryObject<EntityType<CompsognathusEntity>> COMPSOGNATHUS = ENTITIES.register("compsognathus",
            () -> EntityType.Builder.of(CompsognathusEntity::new, MobCategory.CREATURE).sized(0.45F, 0.55F).clientTrackingRange(10).build("compsognathus"));
    public static final RegistryObject<EntityType<VelociraptorEntity>> VELOCIRAPTOR = ENTITIES.register("velociraptor",
            () -> EntityType.Builder.of(VelociraptorEntity::new, MobCategory.CREATURE).sized(0.7F, 1.05F).clientTrackingRange(10).build("velociraptor"));
    public static final RegistryObject<EntityType<DimorphodonEntity>> DIMORPHODON = ENTITIES.register("dimorphodon",
            () -> EntityType.Builder.of(DimorphodonEntity::new, MobCategory.CREATURE).sized(0.7F, 0.6F).clientTrackingRange(10).build("dimorphodon"));
    public static final RegistryObject<EntityType<BaryonyxEntity>> BARYONYX = ENTITIES.register("baryonyx",
            () -> EntityType.Builder.of(BaryonyxEntity::new, MobCategory.CREATURE).sized(1.6F, 2.2F).clientTrackingRange(10).build("baryonyx"));

    // ---- Dev yırtıcılar ----
    public static final RegistryObject<EntityType<TRexEntity>> TREX = ENTITIES.register("trex",
            () -> EntityType.Builder.of(TRexEntity::new, MobCategory.MONSTER).sized(2.4F, 3.8F).clientTrackingRange(12).build("trex"));
    public static final RegistryObject<EntityType<SpinosaurusEntity>> SPINOSAURUS = ENTITIES.register("spinosaurus",
            () -> EntityType.Builder.of(SpinosaurusEntity::new, MobCategory.MONSTER).sized(2.5F, 4.1F).clientTrackingRange(12).build("spinosaurus"));
    public static final RegistryObject<EntityType<MosasaurusEntity>> MOSASAURUS = ENTITIES.register("mosasaurus",
            () -> EntityType.Builder.of(MosasaurusEntity::new, MobCategory.WATER_CREATURE).sized(4.5F, 2.5F).clientTrackingRange(16).build("mosasaurus"));

    // ---- Araçlar ----
    public static final RegistryObject<EntityType<EscapeBoatEntity>> ESCAPE_BOAT = ENTITIES.register("escape_boat",
            () -> EntityType.Builder.of(EscapeBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("escape_boat"));
    public static final RegistryObject<EntityType<GyrosphereEntity>> GYROSPHERE = ENTITIES.register("gyrosphere",
            () -> EntityType.Builder.of(GyrosphereEntity::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(10).build("gyrosphere"));

    public static void registerAttributes(EntityAttributeCreationEvent e) {
        e.put(STRUTHIOMIMUS.get(), StruthiomimusEntity.createAttributes().build());
        e.put(ANKYLOSAURUS.get(), AnkylosaurusEntity.createAttributes().build());
        e.put(PACHYCEPHALOSAURUS.get(), PachycephalosaurusEntity.createAttributes().build());
        e.put(TRICERATOPS.get(), TriceratopsEntity.createAttributes().build());
        e.put(STEGOSAURUS.get(), StegosaurusEntity.createAttributes().build());
        e.put(PARASAUR.get(), ParasaurEntity.createAttributes().build());
        e.put(BRACHIOSAURUS.get(), BrachiosaurusEntity.createAttributes().build());
        e.put(COMPSOGNATHUS.get(), CompsognathusEntity.createAttributes().build());
        e.put(VELOCIRAPTOR.get(), VelociraptorEntity.createAttributes().build());
        e.put(DIMORPHODON.get(), DimorphodonEntity.createAttributes().build());
        e.put(BARYONYX.get(), BaryonyxEntity.createAttributes().build());
        e.put(TREX.get(), TRexEntity.createAttributes().build());
        e.put(SPINOSAURUS.get(), SpinosaurusEntity.createAttributes().build());
        e.put(MOSASAURUS.get(), MosasaurusEntity.createAttributes().build());
    }
}
