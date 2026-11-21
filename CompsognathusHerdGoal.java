package com.jurassicsurvival.entity;

import net.minecraft.world.entity.ai.goal.Goal;
import java.util.Comparator;
import java.util.EnumSet;

public class CompsognathusHerdGoal extends Goal {

    private final CompsognathusEntity dino;
    private CompsognathusEntity leader;

    public CompsognathusHerdGoal(CompsognathusEntity dino) {
        this.dino = dino;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (dino.getTarget() != null)
            return false;

        leader = dino.level()
            .getEntitiesOfClass(
                CompsognathusEntity.class,
                dino.getBoundingBox().inflate(12),
                e -> e != dino && e.isAlive())
            .stream()
            .min(Comparator.comparingInt(
                net.minecraft.world.entity.Entity::getId))
            .orElse(null);

        return leader != null && dino.distanceToSqr(leader) > 16.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return leader != null
            && leader.isAlive()
            && dino.getTarget() == null
            && dino.distanceToSqr(leader) > 10.0D;
    }

    @Override
    public void start() {
        if (leader != null)
            dino.getNavigation().moveTo(leader, 1.15D);
    }

    @Override
    public void tick() {
        if (leader != null)
            dino.getNavigation().moveTo(leader, 1.15D);
    }
}
