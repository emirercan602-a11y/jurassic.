package com.jurassicsurvival.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import java.util.EnumSet;
import java.util.Random;

public class StegosaurusGrazeGoal extends Goal {
    private final StegosaurusEntity dino;
    private int ticks;
    private final Random random = new Random();

    public StegosaurusGrazeGoal(StegosaurusEntity dino) {
        this.dino = dino;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (dino.isBaby() || dino.isDrinking() || dino.getTarget() != null)
            return false;

        if (random.nextInt(100) > 8)
            return false;

        BlockPos pos = dino.blockPosition();
        return dino.level().getBlockState(pos.below()).is(Blocks.GRASS_BLOCK);
    }

    @Override
    public boolean canContinueToUse() {
        return ticks < 80 && dino.getTarget() == null;
    }

    @Override
    public void start() {
        ticks = 0;
        dino.setGrazing(true);
    }

    @Override
    public void tick() {
        dino.getNavigation().stop();
        ticks++;
    }

    @Override
    public void stop() {
        dino.setGrazing(false);
        ticks = 0;
    }
}
