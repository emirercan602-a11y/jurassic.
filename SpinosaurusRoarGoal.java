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

public class SpinosaurusRoarGoal extends Goal {
    private final SpinosaurusEntity spino;
    private int roarDuration;

    public SpinosaurusRoarGoal(SpinosaurusEntity spino) {
        this.spino = spino;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.spino.getTarget();
        return target != null && target.isAlive() && this.spino.canRoar() && this.spino.distanceToSqr(target) < 484.0D;
    }

    @Override
    public void start() {
        this.roarDuration = 30;
        this.spino.triggerRoar(30);
        this.spino.playSound(SoundEvents.ENDER_DRAGON_GROWL, 3.0F, 0.58F);

        AABB area = this.spino.getBoundingBox().inflate(24.0D);
        List<Player> players = this.spino.level().getEntitiesOfClass(Player.class, area);
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
        if (this.spino.getTarget() != null) {
            this.spino.getLookControl().setLookAt(this.spino.getTarget(), 30.0F, 30.0F);
        }
    }
}
