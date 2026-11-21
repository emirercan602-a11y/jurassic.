package com.jurassicsurvival.world;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class BunkerAndWreckBuilder {
    public static final ResourceLocation SUPPLY_LOOT = new ResourceLocation(JurassicSurvival.MOD_ID, "chests/bunker_supply");

    // 1. Düşen helikopter enkazı (yerde InGen Erişim Kartı bırakır)
    public static void buildHelicopterWreck(LevelAccessor level, BlockPos center) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) + Math.abs(z) <= 4) {
                    level.setBlock(center.offset(x, 0, z), Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
                    if (level.getRandom().nextFloat() < 0.35F) {
                        level.setBlock(center.offset(x, 1, z), Blocks.IRON_BARS.defaultBlockState(), 3);
                    }
                }
            }
        }
        level.setBlock(center.above(2), Blocks.ANVIL.defaultBlockState(), 3);
        level.setBlock(center.offset(-2, 2, 0), Blocks.IRON_TRAPDOOR.defaultBlockState(), 3);
        level.setBlock(center.offset(2, 2, 0), Blocks.IRON_TRAPDOOR.defaultBlockState(), 3);

        if (level instanceof Level lvl && !lvl.isClientSide) {
            ItemStack keycardStack = new ItemStack(ModItems.KEYCARD.get());
            ItemEntity dropped = new ItemEntity(lvl, center.getX() + 2.5D, center.getY() + 1.0D, center.getZ() + 1.5D, keycardStack);
            dropped.setUnlimitedLifetime(); // kaybolmaz
            dropped.setDeltaMovement(0, 0, 0);
            lvl.addFreshEntity(dropped);
        }
    }

    // 2. Kilitli InGen acil durum sığınağı (9x5x9)
    public static void buildInGenBunker(LevelAccessor level, BlockPos startPos) {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 9; z++) {
                    BlockPos pos = startPos.offset(x, y, z);
                    boolean isWall = (x == 0 || x == 8 || y == 0 || y == 4 || z == 0 || z == 8);
                    level.setBlock(pos, isWall ? Blocks.REINFORCED_DEEPSLATE.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        // Kilitli demir kapı (alt + üst yarı)
        BlockPos doorPos = startPos.offset(4, 1, 0);
        level.setBlock(doorPos, Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(doorPos.above(), Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);

        // Yatak: iki parçası da konmalı (orijinalde yalnızca bir yarısı konuyordu)
        BlockPos bedFoot = startPos.offset(2, 1, 6);
        BlockState foot = Blocks.WHITE_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH).setValue(BedBlock.PART, BedPart.FOOT);
        BlockState head = foot.setValue(BedBlock.PART, BedPart.HEAD);
        level.setBlock(bedFoot, foot, 3);
        level.setBlock(bedFoot.north(), head, 3);

        level.setBlock(startPos.offset(4, 3, 4), Blocks.LANTERN.defaultBlockState(), 3);

        // InGen tedarik sandığı: loot tablosuna bağlı (orijinalde boştu)
        BlockPos chestPos = startPos.offset(6, 1, 6);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        RandomizableContainerBlockEntity.setLootTable(level, level.getRandom(), chestPos, SUPPLY_LOOT);
    }
}
