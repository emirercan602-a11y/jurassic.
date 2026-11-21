package com.jurassicsurvival.entity;

import com.jurassicsurvival.item.GasolineItem;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MosasaurusEntity extends WaterAnimal {
    private int scentLostTimer = 0;

    public MosasaurusEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
        // Orijinalde yüzme kontrolü yoktu; Mosasaurus suda ilerleyemiyordu (Yunus mantığı eklendi).
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.05F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)       // 50 Kalp
                .add(Attributes.MOVEMENT_SPEED, 0.45D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, true));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.0D, 40));
        // Benzinle maskelenmiş oyuncuyu hedef almaz
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
                e -> !(e instanceof Player p && GasolineItem.shouldHideFromMob(p, this))));
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    /** Kaçış teknesindeki oyuncuya saldırısı tekneye vurur (10 vuruşta batar). */
    @Override
    public boolean doHurtTarget(Entity target) {
        Entity vehicle = target.getVehicle();
        if (vehicle instanceof EscapeBoatEntity boat) {
            return boat.hurt(this.damageSources().mobAttack(this), 1.0F);
        }
        return super.doHurtTarget(target);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            if (target instanceof Player player && GasolineItem.shouldHideFromMob(player, this)) {
                scentLostTimer++;
                if (scentLostTimer > 60) {
                    this.setTarget(null);
                    scentLostTimer = 0;
                    player.sendSystemMessage(Component.literal("Mosasaurus üzerindeki keskin benzin kokusu yüzünden yönünü kaybetti ve uzaklaştı!"));
                }
            } else {
                scentLostTimer = 0;
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDER_DRAGON_DEATH;
    }
}
