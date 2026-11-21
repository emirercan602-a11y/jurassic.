package com.jurassicsurvival.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SpinosaurusHuntGoal extends Goal {
    private final SpinosaurusEntity spino;
    private double targetX;
    private double targetY;
    private double targetZ;

    public SpinosaurusHuntGoal(SpinosaurusEntity spino) {
        this.spino = spino;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.spino.getTarget() != null || this.spino.getRandom().nextInt(60) != 0) {
            return false;
        }
        // Prefer patrolling towards nearby swamp water or shorelines
        BlockPos waterPos = findNearbyWater();
        if (waterPos != null && this.spino.getRandom().nextBoolean()) {
            this.targetX = waterPos.getX() + 0.5D;
            this.targetY = waterPos.getY();
            this.targetZ = waterPos.getZ() + 0.5D;
            return true;
        }
        Vec3 pos = DefaultRandomPos.getPos(this.spino, 26, 7);
        if (pos == null) {
            return false;
        }
        this.targetX = pos.x;
        this.targetY = pos.y;
        this.targetZ = pos.z;
        return true;
    }

    private BlockPos findNearbyWater() {
        RandomSource rand = this.spino.getRandom();
        BlockPos origin = this.spino.blockPosition();
        for (int i = 0; i < 12; i++) {
            BlockPos check = origin.offset(rand.nextInt(25) - 12, rand.nextInt(5) - 2, rand.nextInt(25) - 12);
            if (this.spino.level().getFluidState(check).is(FluidTags.WATER)) {
                return check;
            }
        }
        return null;
    }

    @Override
    public void start() {
        double speed = this.spino.isInWater() ? 1.45D : 1.05D;
        this.spino.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, speed);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.spino.getNavigation().isDone() && this.spino.getTarget() == null;
    }
}
