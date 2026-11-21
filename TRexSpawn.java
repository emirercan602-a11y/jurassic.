package com.jurassicsurvival.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;

public class TRexSpawn {
    public static boolean checkTRexSpawnRules(EntityType<TRexEntity> type, ServerLevelAccessor level,
                                              MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getRawBrightness(pos, 0) > 6 && random.nextInt(5) == 0;
    }
}
