package com.jurassicsurvival.entity;

import net.minecraft.world.item.crafting.Ingredient;

import com.jurassicsurvival.registry.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class ParasaurEntity extends Animal implements GrazingDino {
    private boolean grazing;
    private boolean drinking;
    private int callCooldown;

    public ParasaurEntity(EntityType<? extends ParasaurEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.31D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.05D);
    }

    @Override
    protected void registerGoals() {
        // Vurulursa hedefi hatırlar ama normalde saldırmaz.
        targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // Tehlikeden kaçış en yüksek davranışlardan biri.
        goalSelector.addGoal(1, new ParasaurFleeGoal(this, 1.35D));
        goalSelector.addGoal(2, new PanicGoal(this, 1.35D));

        goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        goalSelector.addGoal(4,
            new TemptGoal(this, 1.0D, Ingredient.of(Items.WHEAT), false));

        goalSelector.addGoal(5, new ParasaurAlarmGoal(this));
        goalSelector.addGoal(6, new ParasaurDrinkGoal(this));
        goalSelector.addGoal(7, new ParasaurGrazeGoal(this));
        goalSelector.addGoal(8, new ParasaurHerdGoal(this));

        goalSelector.addGoal(9,
            new WaterAvoidingRandomStrollGoal(this, 1.05D));
        goalSelector.addGoal(10,
            new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.WHEAT);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return ModEntities.PARASAUR.get().create(level);
    }

    @Override
    public boolean canMate(Animal other) {
        return other instanceof ParasaurEntity && super.canMate(other);
    }

    public boolean isGrazing() {
        return grazing;
    }

    public boolean isDrinking() {
        return drinking;
    }

    public void setGrazing(boolean value) {
        grazing = value;
        if (value) drinking = false;
    }

    public void startDrinking() {
        drinking = true;
        grazing = false;
        getNavigation().stop();
    }

    public void stopDrinking() {
        drinking = false;
    }

    public boolean canCall() {
        return callCooldown <= 0;
    }

    public void resetCallCooldown() {
        callCooldown = 120;
    }

    public void alarmHerd(@Nullable LivingEntity danger) {
        if (level().isClientSide) return;

        for (ParasaurEntity other :
            level().getEntitiesOfClass(
                ParasaurEntity.class,
                getBoundingBox().inflate(18.0D),
                e -> e != this && e.isAlive())) {

            other.setAlarmedBy(danger);
        }
    }

    private void setAlarmedBy(@Nullable LivingEntity danger) {
        if (danger != null) {
            setTarget(null);
        }
        if (canCall()) {
            resetCallCooldown();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (callCooldown > 0) callCooldown--;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source,
                        float amount) {
        boolean result = super.hurt(source, amount);

        if (result && source.getEntity() instanceof LivingEntity attacker) {
            // Parasaur saldırmaz; alarm verip kaçmaya çalışır.
            setTarget(null);
            alarmHerd(attacker);
        }

        return result;
    }
}
