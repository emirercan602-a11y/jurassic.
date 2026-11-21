package com.jurassicsurvival.entity.ai;

import com.jurassicsurvival.entity.DimorphodonEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import java.util.List;

public class DimorphodonFlockGoal extends Goal {
    private final DimorphodonEntity mob;
    private DimorphodonEntity flockLeader;
    private int timeToRecalcPath;

    public DimorphodonFlockGoal(DimorphodonEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null) {
            return false;
        }
        List<DimorphodonEntity> nearby = this.mob.level().getEntitiesOfClass(
                DimorphodonEntity.class,
                this.mob.getBoundingBox().inflate(18.0D)
        );
        if (nearby.size() <= 1) {
            return false;
        }
        // Sürüdeki en yaşlı / ilk entity'yi lider seç
        DimorphodonEntity leader = nearby.get(0);
        if (leader == this.mob) {
            return false;
        }
        this.flockLeader = leader;
        return this.mob.distanceToSqr(this.flockLeader) > 16.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return this.flockLeader != null && this.flockLeader.isAlive()
                && this.mob.getTarget() == null
                && this.mob.distanceToSqr(this.flockLeader) > 9.0D
                && this.mob.distanceToSqr(this.flockLeader) < 400.0D;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            this.mob.getNavigation().moveTo(
                    this.flockLeader.getX() + (this.mob.getRandom().nextDouble() - 0.5D) * 4.0D,
                    this.flockLeader.getY() + 2.0D + (this.mob.getRandom().nextDouble() * 2.0D),
                    this.flockLeader.getZ() + (this.mob.getRandom().nextDouble() - 0.5D) * 4.0D,
                    1.15D
            );
        }
    }
}
