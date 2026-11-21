package com.jurassicsurvival.entity;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TRexHuntGoal extends Goal {
    private final TRexEntity trex;
    private double targetX;
    private double targetY;
    private double targetZ;

    public TRexHuntGoal(TRexEntity trex) {
        this.trex = trex;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.trex.getTarget() != null || this.trex.getRandom().nextInt(80) != 0) {
            return false;
        }
        Vec3 pos = DefaultRandomPos.getPos(this.trex, 24, 7);
        if (pos == null) {
            return false;
        }
        this.targetX = pos.x;
        this.targetY = pos.y;
        this.targetZ = pos.z;
        return true;
    }

    @Override
    public void start() {
        this.trex.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, 1.05D);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.trex.getNavigation().isDone() && this.trex.getTarget() == null;
    }
}
