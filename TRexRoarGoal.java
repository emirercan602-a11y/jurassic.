package com.jurassicsurvival.entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class TRexRoarGoal extends Goal {
    private final TRexEntity trex;
    private int roarDuration;

    public TRexRoarGoal(TRexEntity trex) {
        this.trex = trex;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.trex.getTarget();
        return target != null && target.isAlive() && this.trex.canRoar() && this.trex.distanceToSqr(target) < 400.0D;
    }

    @Override
    public void start() {
        this.roarDuration = 30;
        this.trex.triggerRoar(30);
        this.trex.playSound(SoundEvents.ENDER_DRAGON_GROWL, 3.0F, 0.65F);

        AABB area = this.trex.getBoundingBox().inflate(24.0D);
        List<Player> players = this.trex.level().getEntitiesOfClass(Player.class, area);
        for (Player player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.roarDuration > 0;
    }

    @Override
    public void tick() {
        this.roarDuration--;
        if (this.trex.getTarget() != null) {
            this.trex.getLookControl().setLookAt(this.trex.getTarget(), 30.0F, 30.0F);
        }
    }
}
