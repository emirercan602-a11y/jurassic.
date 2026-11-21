package com.jurassicsurvival.entity;

import net.minecraft.world.entity.ai.goal.Goal;
import java.util.Comparator;
import java.util.EnumSet;

public class PachyHerdGoal extends Goal {
    private final PachycephalosaurusEntity dino;
    private PachycephalosaurusEntity leader;

    public PachyHerdGoal(PachycephalosaurusEntity dino) {
        this.dino = dino;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (dino.isBaby() || dino.isDrinking() || dino.getTarget() != null)
            return false;

        leader = dino.level()
            .getEntitiesOfClass(
                PachycephalosaurusEntity.class,
                dino.getBoundingBox().inflate(12),
                e -> e != dino && e.isAlive() && !e.isBaby())
            .stream()
            .min(Comparator.comparingInt(net.minecraft.world.entity.Entity::getId))
            .orElse(null);

        return leader != null && dino.distanceToSqr(leader) > 20;
    }

    @Override
    public boolean canContinueToUse() {
        return leader != null && leader.isAlive()
            && dino.getTarget() == null
            && dino.distanceToSqr(leader) > 12;
    }

    @Override
    public void start() {
        dino.getNavigation().moveTo(leader, 1.0D);
    }

    @Override
    public void tick() {
        if (leader != null)
            dino.getNavigation().moveTo(leader, 1.0D);
    }
}
