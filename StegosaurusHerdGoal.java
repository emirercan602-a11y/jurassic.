package com.jurassicsurvival.entity;

import net.minecraft.world.entity.ai.goal.Goal;
import java.util.Comparator;
import java.util.EnumSet;

public class StegosaurusHerdGoal extends Goal {
    private final StegosaurusEntity dino;
    private StegosaurusEntity leader;

    public StegosaurusHerdGoal(StegosaurusEntity dino) {
        this.dino = dino;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (dino.isBaby() || dino.isDrinking() || dino.getTarget() != null)
            return false;

        leader = dino.level()
            .getEntitiesOfClass(
                StegosaurusEntity.class,
                dino.getBoundingBox().inflate(14),
                e -> e != dino && e.isAlive() && !e.isBaby())
            .stream()
            .min(Comparator.comparingInt(
                net.minecraft.world.entity.Entity::getId))
            .orElse(null);

        return leader != null && dino.distanceToSqr(leader) > 25.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return leader != null
            && leader.isAlive()
            && dino.getTarget() == null
            && dino.distanceToSqr(leader) > 16.0D;
    }

    @Override
    public void start() {
        if (leader != null)
            dino.getNavigation().moveTo(leader, 0.85D);
    }

    @Override
    public void tick() {
        if (leader != null)
            dino.getNavigation().moveTo(leader, 0.85D);
    }
}
