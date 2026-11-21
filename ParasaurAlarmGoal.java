package com.jurassicsurvival.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import java.util.EnumSet;

public class ParasaurAlarmGoal extends Goal {
    private final ParasaurEntity dino;
    private int ticks;

    public ParasaurAlarmGoal(ParasaurEntity dino) {
        this.dino = dino;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!dino.canCall())
            return false;

        LivingEntity danger = dino.level().getEntitiesOfClass(
            LivingEntity.class,
            dino.getBoundingBox().inflate(14.0D),
            e -> e.isAlive()
                && e != dino
                && (e instanceof Monster
                    || (e instanceof Player p && !p.isCreative() && !p.isSpectator()))
        ).stream().findFirst().orElse(null);

        if (danger == null)
            return false;

        dino.resetCallCooldown();
        dino.alarmHerd(danger);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return ticks < 20;
    }

    @Override
    public void start() {
        ticks = 0;
    }

    @Override
    public void tick() {
        ticks++;
    }

    @Override
    public void stop() {
        ticks = 0;
    }
}
