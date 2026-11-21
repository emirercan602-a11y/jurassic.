package com.jurassicsurvival.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class IslandAtmosphereBuilder {

    // Terk edilmiş Jurassic World elektrikli çit hattı (kırık ve sarmaşıklı)
    public static void buildBrokenFenceLine(LevelAccessor level, BlockPos startPos, int length, boolean alongX) {
        for (int i = 0; i < length; i++) {
            BlockPos base = alongX ? startPos.offset(i, 0, 0) : startPos.offset(0, 0, i);
            // Kırık boşluklar bırak
            if (level.getRandom().nextFloat() < 0.22F) continue;

            level.setBlock(base, Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 3);
            int fenceHeight = 3 + level.getRandom().nextInt(3);
            for (int y = 1; y <= fenceHeight; y++) {
                if (i % 4 == 0) {
                    // Ana direkler
                    level.setBlock(base.above(y), Blocks.POLISHED_ANDESITE_WALL.defaultBlockState(), 3);
                    if (y == fenceHeight && level.getRandom().nextBoolean()) {
                        level.setBlock(base.above(y + 1), Blocks.REDSTONE_TORCH.defaultBlockState(), 3);
                    }
                } else {
                    // Elektrikli teller (Demir parmaklıklar)
                    level.setBlock(base.above(y), Blocks.IRON_BARS.defaultBlockState(), 3);
                }
            }
        }
    }

    // Paslanmış InGen / Jurassic World Sevkiyat Konteyneri
    public static void buildRustedContainer(LevelAccessor level, BlockPos startPos, boolean isRed) {
        var wallBlock = isRed ? Blocks.RED_TERRACOTTA.defaultBlockState() : Blocks.CYAN_TERRACOTTA.defaultBlockState();
        var rustBlock = Blocks.OXIDIZED_COPPER.defaultBlockState();

        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 3; z++) {
                    BlockPos pos = startPos.offset(x, y, z);
                    boolean isEdge = (x == 0 || x == 6 || y == 0 || y == 3 || z == 0 || z == 2);
                    if (isEdge) {
                        // Kırık delikler ve pas efekti
                        if (level.getRandom().nextFloat() < 0.08F) continue;
                        level.setBlock(pos, level.getRandom().nextFloat() < 0.25F ? rustBlock : wallBlock, 3);
                    } else {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    // Terk Edilmiş Bozuk Arazi Aracı (Wrecked Jeep/Truck)
    public static void buildWreckedVehicle(LevelAccessor level, BlockPos startPos) {
        // Tekerlekler (Siyah Beton)
        level.setBlock(startPos.offset(0, 0, 0), Blocks.BLACK_CONCRETE.defaultBlockState(), 3);
        level.setBlock(startPos.offset(3, 0, 0), Blocks.BLACK_CONCRETE.defaultBlockState(), 3);
        level.setBlock(startPos.offset(0, 0, 2), Blocks.BLACK_CONCRETE.defaultBlockState(), 3);
        level.setBlock(startPos.offset(3, 0, 2), Blocks.BLACK_CONCRETE.defaultBlockState(), 3);

        // Gövde ve Kabin
        for (int x = 0; x <= 3; x++) {
            for (int z = 0; z <= 2; z++) {
                level.setBlock(startPos.offset(x, 1, z), Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(), 3);
            }
        }
        // Camlar ve Tavan
        for (int x = 1; x <= 2; x++) {
            for (int z = 0; z <= 2; z++) {
                level.setBlock(startPos.offset(x, 2, z), Blocks.TINTED_GLASS.defaultBlockState(), 3);
                level.setBlock(startPos.offset(x, 3, z), Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 3);
            }
        }
        // Ön Far (Kırık/Sönük veya Loş Işık)
        level.setBlock(startPos.offset(0, 1, 1), Blocks.SOUL_LANTERN.defaultBlockState(), 3);
    }
}
