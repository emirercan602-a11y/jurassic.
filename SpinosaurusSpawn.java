package com.jurassicsurvival.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpinosaurusSpawn {
    public static boolean checkSpinosaurusSpawnRules(EntityType<SpinosaurusEntity> type, ServerLevelAccessor level,
                                                     MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        Holder<Biome> biome = level.getBiome(pos);
        boolean isSwampBiome = biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP);

        BlockState below = level.getBlockState(pos.below());
        boolean validSurface = level.getFluidState(pos).is(FluidTags.WATER)
                || level.getFluidState(pos.below()).is(FluidTags.WATER)
                || below.is(Blocks.MUD)
                || below.is(Blocks.GRASS_BLOCK)
                || below.is(Blocks.CLAY);

        if (isSwampBiome) {
            return validSurface && random.nextInt(3) == 0;
        }
        // Very rare near water/mud on custom island maps outside vanilla swamp biomes
        return validSurface && level.getRawBrightness(pos, 0) > 6 && random.nextInt(12) == 0;
    }
}
