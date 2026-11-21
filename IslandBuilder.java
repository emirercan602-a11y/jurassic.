package com.jurassicsurvival.world;

import com.jurassicsurvival.JurassicSurvival;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * v0.3 ada oluşturucu. Aynı arazi formülü; fark: ~1 milyon sütun tek tick'te değil,
 * tick başına ~35 ms bütçeyle yayılarak oluşturuluyor (aksi halde oyun/sunucu dakikalarca donardı).
 * Ada bitince v1.9/v1.16 dekorları (ağaç, çit, konteyner, araç, helikopter enkazı, sığınak) eklenir.
 */
@Mod.EventBusSubscriber(modid = JurassicSurvival.MOD_ID)
public final class IslandBuilder {
    private IslandBuilder() {}

    public static final int RADIUS = 500;
    public static final int SEA_Y = 62;
    public static final int FLOOR_Y = 48;
    private static final long TICK_BUDGET_NANOS = 35_000_000L;

    private static final List<Task> TASKS = new ArrayList<>();

    public static boolean isBuilding() {
        return !TASKS.isEmpty();
    }

    public static void build(ServerLevel level, BlockPos center) {
        TASKS.add(new Task(level, center));
    }

    /** Arazi yüksekliği (v0.3 formülü). dx/dz adanın merkezine göre. */
    public static int groundHeight(int dx, int dz) {
        double d = Math.sqrt((double) dx * dx + (double) dz * dz);
        double n1 = Math.sin(dx * 0.018) * 18;
        double n2 = Math.cos(dz * 0.021) * 14;
        double n3 = Math.sin((dx - dz) * 0.011) * 12;
        double n4 = Math.cos((dx + dz) * 0.037) * 5;
        double edge = Math.max(0, 1.0 - d / RADIUS);
        double mountain = Math.max(0, edge - 0.45) * 45;
        double height = SEA_Y + n1 + n2 + n3 + n4 + mountain;
        return (int) Math.max(SEA_Y - 2, Math.min(150, height));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || TASKS.isEmpty()) return;
        Iterator<Task> it = TASKS.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.step()) it.remove();
        }
    }

    private static final class Task {
        private final ServerLevel level;
        private final BlockPos center;
        private final Random random = new Random(20260817L);
        private int x = -RADIUS;
        private int z = -RADIUS;
        private int lastPercent = -1;
        private int phase = 0;      // 0 = sütunlar, 1 = göletler/düzlükler, 2 = dekor
        private int decorStep = 0;

        Task(ServerLevel level, BlockPos center) {
            this.level = level;
            this.center = center;
            tell("Ada oluşturuluyor... Bu işlem birkaç dakika sürer; oyun akıcı kalır ama sabırlı ol.");
        }

        boolean step() {
            long start = System.nanoTime();
            if (phase == 0) {
                while (System.nanoTime() - start < TICK_BUDGET_NANOS) {
                    column(x, z);
                    if (++z > RADIUS) {
                        z = -RADIUS;
                        if (++x > RADIUS) {
                            phase = 1;
                            break;
                        }
                    }
                }
                int percent = (int) (((long) (x + RADIUS)) * 100L / (2L * RADIUS + 1));
                if (percent / 10 != lastPercent / 10) {
                    lastPercent = percent;
                    tell("Ada: %" + percent);
                }
                return false;
            }
            if (phase == 1) {
                pond(center.offset(-180, 0, -150), 25, SEA_Y + 2);
                pond(center.offset(-40, 0, -220), 18, SEA_Y + 3);
                pond(center.offset(145, 0, -155), 30, SEA_Y + 2);
                pond(center.offset(220, 0, 20), 20, SEA_Y + 3);
                pond(center.offset(75, 0, 175), 27, SEA_Y + 2);
                pond(center.offset(-120, 0, 185), 22, SEA_Y + 3);
                pond(center.offset(-260, 0, 35), 16, SEA_Y + 2);
                clearArea(center.offset(0, 0, -70), 38, SEA_Y + 4);   // kamp
                clearArea(center.offset(0, 0, 360), 28, SEA_Y + 2);   // iskele
                phase = 2;
                return false;
            }
            // phase 2: dekor, birkaç öğeyi tick başına
            for (int i = 0; i < 4 && decorStep < 140; i++, decorStep++) {
                decorate(decorStep);
            }
            if (decorStep >= 140) {
                tell("Jurassic Survival adası tamamlandı!");
                return true;
            }
            return false;
        }

        private void tell(String msg) {
            for (ServerPlayer p : level.players()) {
                p.sendSystemMessage(Component.literal("[Jurassic Survival] " + msg));
            }
        }

        private void column(int dx, int dz) {
            double d = Math.sqrt((double) dx * dx + (double) dz * dz);
            if (d > RADIUS + 15) return;

            int ground = groundHeight(dx, dz);
            int wx = center.getX() + dx;
            int wz = center.getZ() + dz;

            for (int y = FLOOR_Y; y <= SEA_Y; y++) {
                set(wx, y, wz, Blocks.STONE.defaultBlockState());
            }
            if (d > RADIUS) return;

            for (int y = SEA_Y + 1; y <= ground; y++) {
                Block block;
                if (y == ground) {
                    block = (ground > 100) ? Blocks.STONE : (ground < SEA_Y + 5 ? Blocks.SAND : Blocks.GRASS_BLOCK);
                } else if (ground > 105 && y > ground - 5) {
                    block = Blocks.STONE;
                } else {
                    block = Blocks.DIRT;
                }
                set(wx, y, wz, block.defaultBlockState());
            }
            if (ground <= SEA_Y + 7) {
                for (int y = SEA_Y + 1; y <= ground; y++) {
                    set(wx, y, wz, Blocks.SAND.defaultBlockState());
                }
            }
            if (ground < SEA_Y) {
                for (int y = ground + 1; y <= SEA_Y; y++) {
                    set(wx, y, wz, Blocks.WATER.defaultBlockState());
                }
            }
            if (ground >= SEA_Y + 8 && ground < 100 && random.nextInt(180) == 0) {
                jungleTree(wx, ground + 1, wz);
            }
        }

        private void pond(BlockPos p, int radius, int waterY) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > radius * radius) continue;
                    int depth = 2 + (int) (Math.abs(Math.sin(x * 0.15) + Math.cos(z * 0.13)));
                    for (int y = waterY - depth; y <= waterY; y++) {
                        set(p.getX() + x, y, p.getZ() + z, Blocks.WATER.defaultBlockState());
                    }
                    if (x * x + z * z > (radius - 2) * (radius - 2)) {
                        set(p.getX() + x, waterY, p.getZ() + z, Blocks.SAND.defaultBlockState());
                    }
                }
            }
        }

        private void clearArea(BlockPos c, int radius, int floorY) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > radius * radius) continue;
                    set(c.getX() + x, floorY, c.getZ() + z, Blocks.GRASS_BLOCK.defaultBlockState());
                }
            }
        }

        private void jungleTree(int x, int y, int z) {
            int height = 5 + Math.abs((x * 31 + z * 17) % 4);
            BlockState leaves = Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
            for (int i = 0; i < height; i++) {
                set(x, y + i, z, Blocks.JUNGLE_LOG.defaultBlockState());
            }
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    for (int dy = height - 2; dy <= height + 1; dy++) {
                        if (Math.abs(dx) + Math.abs(dz) + (dy - (height - 2)) <= 4) {
                            set(x + dx, y + dy, z + dz, leaves);
                        }
                    }
                }
            }
        }

        /** Adanın rastgele (ama sabit tohumlu) noktalarına v1.9 / v1.16 yapılarını yerleştirir. */
        private void decorate(int i) {
            Random r = new Random(7000L + i);
            for (int attempt = 0; attempt < 30; attempt++) {
                double ang = r.nextDouble() * Math.PI * 2;
                double dist = 60 + r.nextDouble() * (RADIUS - 120);
                int dx = (int) Math.round(Math.cos(ang) * dist);
                int dz = (int) Math.round(Math.sin(ang) * dist);
                if (Math.abs(dx) < 45 && Math.abs(dz + 70) < 45) continue; // kamp alanı
                int ground = groundHeight(dx, dz);
                if (ground < SEA_Y + 8 || ground >= 100) continue;
                BlockPos base = new BlockPos(center.getX() + dx, ground + 1, center.getZ() + dz);

                if (i < 50) {
                    PrehistoricTreeBuilder.generatePrehistoricTree(level, base, 14 + r.nextInt(8));
                } else if (i < 90) {
                    IslandAtmosphereBuilder.buildBrokenFenceLine(level, base, 20 + r.nextInt(20), r.nextBoolean());
                } else if (i < 105) {
                    IslandAtmosphereBuilder.buildRustedContainer(level, base, r.nextBoolean());
                } else if (i < 115) {
                    IslandAtmosphereBuilder.buildWreckedVehicle(level, base);
                } else if (i < 130) {
                    BunkerAndWreckBuilder.buildHelicopterWreck(level, base.above());
                } else {
                    BunkerAndWreckBuilder.buildInGenBunker(level, base.below());
                }
                return;
            }
        }

        private void set(int x, int y, int z, BlockState state) {
            // flag 2: sadece istemcilere bildir (komşu güncellemesi yok) -> çok daha hızlı
            level.setBlock(new BlockPos(x, y, z), state, 2);
        }
    }
}
