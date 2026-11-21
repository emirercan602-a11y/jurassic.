package com.jurassicsurvival.entity;

import com.jurassicsurvival.item.GasolineItem;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TRexEntity extends Monster {
    private int roarCooldown = 0;
    private int roarTicks = 0;
    private int attackCooldown = 0;

    public TRexEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50;
        this.setMaxUpStep(1.5F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TRexRoarGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.35D, false));
        this.goalSelector.addGoal(3, new TRexHuntGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.85D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, e -> !(e instanceof Player p && GasolineItem.shouldHideFromMob(p, this))));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 20, true, false,
                e -> !(e instanceof TRexEntity)));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getSourcePosition() != null && source.getSourcePosition().y >= this.getEyeY() - 0.65D) {
            amount *= 2.2F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.attackCooldown > 0) {
            return false;
        }
        this.attackCooldown = 20;
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.8F, 0.7F);
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.roarCooldown > 0) {
            this.roarCooldown--;
        }
        if (this.roarTicks > 0) {
            this.roarTicks--;
        }
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }

    public boolean canRoar() {
        return this.roarCooldown <= 0;
    }

    public void triggerRoar(int durationTicks) {
        this.roarTicks = durationTicks;
        this.roarCooldown = 400;
    }

    public boolean isRoaring() {
        return this.roarTicks > 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDER_DRAGON_GROWL;
    }

}
