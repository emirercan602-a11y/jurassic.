package com.jurassicsurvival.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import java.util.EnumSet;

public class PachyDrinkGoal extends Goal {
 private int guard;
    private final PachycephalosaurusEntity dino;
    private BlockPos target;
    private int ticks;

    public PachyDrinkGoal(PachycephalosaurusEntity dino) {
        this.dino = dino;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (dino.isBaby() || dino.getTarget() != null)
            return false;

        if (dino.getRandom().nextInt(100) != 0) return false;

        target = findWater();
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && dino.getTarget() == null && ticks < 100 && ++guard < 400;
    }

    @Override
    public void start() { guard=0;
        ticks = 0;
        dino.getNavigation().moveTo(
            target.getX() + 0.5,
            target.getY(),
            target.getZ() + 0.5,
            1.0
        );
    }

    @Override
    public void tick() {
        if (target == null) return;

        if (dino.distanceToSqr(
                target.getX() + 0.5,
                target.getY(),
                target.getZ() + 0.5) < 5.0D) {

            dino.startDrinking();
            dino.getNavigation().stop();
            ticks++;
        } else {
            dino.getNavigation().moveTo(
                target.getX() + 0.5,
                target.getY(),
                target.getZ() + 0.5,
                1.0
            );
        }
    }

    @Override
    public void stop() {
        dino.stopDrinking();
        target = null;
        ticks = 0;
    }

    private BlockPos findWater() {
        BlockPos center = dino.blockPosition();

        for (int x = -16; x <= 16; x += 2)
            for (int z = -16; z <= 16; z += 2)
                for (int y = -3; y <= 3; y++) {
                    BlockPos pos = center.offset(x, y, z);

                    if (dino.level().getBlockState(pos).is(Blocks.WATER)
                        && dino.level().getBlockState(pos.above()).isAir()) {
                        return pos;
                    }
                }

        return null;
    }
}
