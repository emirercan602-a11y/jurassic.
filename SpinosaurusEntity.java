package com.jurassicsurvival.entity;

import com.jurassicsurvival.item.GasolineItem;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class SpinosaurusEntity extends Monster {
    private int roarCooldown = 0;
    private int roarTicks = 0;
    private int attackCooldown = 0;

    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public SpinosaurusEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 55;
        this.setMaxUpStep(1.5F);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.moveControl = new AmphibiousMoveControl(this);
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 19.0D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85D)
                .add(Attributes.FOLLOW_RANGE, 44.0D)
                .add(ForgeMod.SWIM_SPEED.get(), 3.2D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SpinosaurusRoarGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.38D, true));
        this.goalSelector.addGoal(3, new SpinosaurusHuntGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, e -> !(e instanceof Player p && GasolineItem.shouldHideFromMob(p, this))));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 22, true, false,
                e -> !(e instanceof SpinosaurusEntity)));
    }

    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.12F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
            if (this.getTarget() != null) {
                double dy = this.getTarget().getY() - this.getY();
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, Math.max(-0.08D, Math.min(0.08D, dy * 0.04D)), 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
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
        this.attackCooldown = 18;
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.9F, 0.65F);
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
        this.roarCooldown = 360;
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


    private static class AmphibiousMoveControl extends MoveControl {
        private final SpinosaurusEntity spino;

        public AmphibiousMoveControl(SpinosaurusEntity spino) {
            super(spino);
            this.spino = spino;
        }

        @Override
        public void tick() {
            if (this.spino.isInWater()) {
                this.spino.setDeltaMovement(this.spino.getDeltaMovement().add(0.0D, 0.015D, 0.0D));
            }
            super.tick();
        }
    }
}
