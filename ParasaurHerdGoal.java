package com.jurassicsurvival.entity;

import net.minecraft.world.entity.ai.goal.Goal;
import java.util.Comparator;
import java.util.EnumSet;

public class ParasaurHerdGoal extends Goal {
    private final ParasaurEntity dino;
    private ParasaurEntity leader;

    public ParasaurHerdGoal(ParasaurEntity dino) {
        this.dino = dino;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (dino.isBaby() || dino.isDrinking())
            return false;

        leader = dino.level().getEntitiesOfClass(
            ParasaurEntity.class,
            dino.getBoundingBox().inflate(14.0D),
            e -> e != dino && e.isAlive() && !e.isBaby()
        ).stream()
            .min(Comparator.comparingInt(
                net.minecraft.world.entity.Entity::getId))
            .orElse(null);

        return leader != null && dino.distanceToSqr(leader) > 25.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return leader != null
            && leader.isAlive()
            && dino.distanceToSqr(leader) > 16.0D;
    }

    @Override
    public void start() {
        if (leader != null)
            dino.getNavigation().moveTo(leader, 1.05D);
    }

    @Override
    public void tick() {
        if (leader != null)
            dino.getNavigation().moveTo(leader, 1.05D);
    }
}
