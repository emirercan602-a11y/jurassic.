package com.jurassicsurvival.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

public class PrehistoricTreeBuilder {

    public static void generatePrehistoricTree(LevelAccessor level, BlockPos startPos, int height) {
        // 1. Kalın 3x3 Gövde (Trunk) İnşası
        for (int y = 0; y < height; y++) {
            BlockPos current = startPos.above(y);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if ((dx == 0 && dz == 0) || level.getRandom().nextFloat() > 0.25F) {
                        level.setBlock(current.offset(dx, 0, dz), Blocks.JUNGLE_LOG.defaultBlockState(), 3);
                    }
                }
            }
        }

        // 2. Devasa Tepe Tacı, Yapraklar ve Sarkan Sarmaşıklar (Canopy & Vines)
        BlockPos topPos = startPos.above(height);
        int radius = 6;
        for (int dy = -4; dy <= 5; dy++) {
            int layerRadius = (dy > 3 || dy < -3) ? radius - 3 : radius;
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    if (dx * dx + dz * dz <= layerRadius * layerRadius + 2) {
                        BlockPos leafPos = topPos.offset(dx, dy, dz);
                        if (level.isEmptyBlock(leafPos)) {
                            level.setBlock(leafPos, Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
                            
                            // Sarkan sarmaşıklar: çıplak VINE (hiç yüzü olmayan) bir sonraki güncellemede kaybolur;
                            // bu yüzden bir yaprağa bitişik yönde yüz atanır, zincir üstteki sarmaşığa tutunur.
                            if (dy == -3 && level.getRandom().nextFloat() < 0.25F) {
                                BlockPos first = leafPos.below();
                                Direction support = null;
                                for (Direction d : Direction.Plane.HORIZONTAL) {
                                    if (level.getBlockState(first.relative(d)).getBlock() instanceof LeavesBlock) {
                                        support = d;
                                        break;
                                    }
                                }
                                if (support != null) {
                                    BlockState vine = Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(support), true);
                                    int vineLen = 3 + level.getRandom().nextInt(6);
                                    for (int v = 0; v < vineLen; v++) {
                                        BlockPos vinePos = first.below(v);
                                        if (!level.isEmptyBlock(vinePos)) break;
                                        level.setBlock(vinePos, vine, 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Ağaç Altına ve Çevresine Dev Eğrelti Otları (Large Ferns) ve Yosun
        for (int rx = -4; rx <= 4; rx++) {
            for (int rz = -4; rz <= 4; rz++) {
                BlockPos groundCheck = startPos.offset(rx, 0, rz);
                if (level.getBlockState(groundCheck).is(Blocks.GRASS_BLOCK) && level.isEmptyBlock(groundCheck.above())) {
                    float rand = level.getRandom().nextFloat();
                    if (rand < 0.45F) {
                        level.setBlock(groundCheck.above(), Blocks.LARGE_FERN.defaultBlockState(), 3);
                    } else if (rand < 0.65F) {
                        level.setBlock(groundCheck.above(), Blocks.FERN.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
