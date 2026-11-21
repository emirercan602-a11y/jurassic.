package com.jurassicsurvival.spawn;

import com.jurassicsurvival.entity.*;
import com.jurassicsurvival.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

/**
 * Doğal spawn kuralları. (Eski sürümlerdeki SpecialSpawn olayı 1.20.1'de yok; yerine bunlar geldi.)
 * Hangi biyomda ve kaç adet spawn olacağı data/jurassicsurvival/forge/biome_modifier/*.json içindedir.
 */
public final class ModSpawns {
    private ModSpawns() {}

    private static final Heightmap.Types HM = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
    private static final SpawnPlacementRegisterEvent.Operation REPLACE = SpawnPlacementRegisterEvent.Operation.REPLACE;

    /** Otçul kuralı: çimen üstü, yeterli ışık, yakında en fazla `max` aynı tür. */
    private static <T extends Mob> SpawnPlacements.SpawnPredicate<T> grassland(Class<T> cls, int max, double radius) {
        return (type, level, reason, pos, random) ->
                level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)
                        && level.getRawBrightness(pos, 0) >= 9
                        && level.getEntitiesOfClass(cls, new AABB(pos).inflate(radius)).size() <= max;
    }

    /** Küçük/orta avcılar için: katı zemin, ışık ve seyrek spawn. */
    private static <T extends Mob> SpawnPlacements.SpawnPredicate<T> predator(Class<T> cls, int oneIn, int max, double radius) {
        return (type, level, reason, pos, random) ->
                level.getBlockState(pos.below()).isSolidRender(level, pos.below())
                        && level.getRawBrightness(pos, 0) > 6
                        && random.nextInt(oneIn) == 0
                        && level.getEntitiesOfClass(cls, new AABB(pos).inflate(radius)).size() <= max;
    }

    private static boolean waterNearby(net.minecraft.world.level.LevelReader level, BlockPos pos, int r) {
        for (int x = -r; x <= r; x += 2) {
            for (int z = -r; z <= r; z += 2) {
                for (int y = -2; y <= 0; y++) {
                    if (level.getFluidState(pos.offset(x, y, z)).is(FluidTags.WATER)) return true;
                }
            }
        }
        return false;
    }

    public static void registerPlacements(SpawnPlacementRegisterEvent e) {
        e.register(ModEntities.STRUTHIOMIMUS.get(), SpawnPlacements.Type.ON_GROUND, HM, grassland(StruthiomimusEntity.class, 8, 18), REPLACE);
        e.register(ModEntities.ANKYLOSAURUS.get(), SpawnPlacements.Type.ON_GROUND, HM, grassland(AnkylosaurusEntity.class, 5, 22), REPLACE);
        e.register(ModEntities.PACHYCEPHALOSAURUS.get(), SpawnPlacements.Type.ON_GROUND, HM, grassland(PachycephalosaurusEntity.class, 5, 20), REPLACE);
        e.register(ModEntities.TRICERATOPS.get(), SpawnPlacements.Type.ON_GROUND, HM, grassland(TriceratopsEntity.class, 4, 24), REPLACE);
        e.register(ModEntities.STEGOSAURUS.get(), SpawnPlacements.Type.ON_GROUND, HM, grassland(StegosaurusEntity.class, 4, 24), REPLACE);
        e.register(ModEntities.PARASAUR.get(), SpawnPlacements.Type.ON_GROUND, HM, grassland(ParasaurEntity.class, 6, 24), REPLACE);
        e.register(ModEntities.COMPSOGNATHUS.get(), SpawnPlacements.Type.ON_GROUND, HM, grassland(CompsognathusEntity.class, 12, 24), REPLACE);
        e.register(ModEntities.BRACHIOSAURUS.get(), SpawnPlacements.Type.ON_GROUND, HM, grassland(BrachiosaurusEntity.class, 3, 48), REPLACE);

        e.register(ModEntities.VELOCIRAPTOR.get(), SpawnPlacements.Type.ON_GROUND, HM, predator(VelociraptorEntity.class, 3, 5, 32), REPLACE);
        e.register(ModEntities.BARYONYX.get(), SpawnPlacements.Type.ON_GROUND, HM,
                (type, level, reason, pos, random) -> random.nextInt(3) == 0 && waterNearby(level, pos, 8)
                        && level.getEntitiesOfClass(BaryonyxEntity.class, new AABB(pos).inflate(48)).size() <= 2, REPLACE);
        e.register(ModEntities.DIMORPHODON.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING,
                (type, level, reason, pos, random) -> level.getRawBrightness(pos, 0) > 8
                        && level.getEntitiesOfClass(DimorphodonEntity.class, new AABB(pos).inflate(32)).size() <= 8, REPLACE);

        // v1.6 / v1.7'deki kendi spawn kurallarınız aynen kullanılıyor.
        e.register(ModEntities.TREX.get(), SpawnPlacements.Type.ON_GROUND, HM, TRexSpawn::checkTRexSpawnRules, REPLACE);
        e.register(ModEntities.SPINOSAURUS.get(), SpawnPlacements.Type.NO_RESTRICTIONS, HM, SpinosaurusSpawn::checkSpinosaurusSpawnRules, REPLACE);
    }
}
