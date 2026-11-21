package com.jurassicsurvival.entity;

import net.minecraft.world.entity.Entity;

import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.world.entity.Mob;

import com.jurassicsurvival.registry.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class PachycephalosaurusEntity extends Animal implements GrazingDino {
    private boolean grazing;
    private boolean drinking;
    private int headbuttCooldown;

    public PachycephalosaurusEntity(EntityType<? extends PachycephalosaurusEntity> type,
                                    Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 24.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.28D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.10D);
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // Kafa çarpması yerine yakın mesafeli saldırı altyapısı.
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.20D, true));
        goalSelector.addGoal(2, new PanicGoal(this, 1.30D));
        goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        goalSelector.addGoal(4,
            new TemptGoal(this, 1.0D, Ingredient.of(Items.SWEET_BERRIES), false));
        goalSelector.addGoal(5, new PachyGrazeGoal(this));
        goalSelector.addGoal(6, new PachyDrinkGoal(this));
        goalSelector.addGoal(7, new PachyHerdGoal(this));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SWEET_BERRIES);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return ModEntities.PACHYCEPHALOSAURUS.get().create(level);
    }

    @Override
    public boolean canMate(Animal other) {
        return other instanceof PachycephalosaurusEntity && super.canMate(other);
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

    public boolean readyForHeadbutt() {
        return headbuttCooldown <= 0;
    }

    public void resetHeadbuttCooldown() {
        headbuttCooldown = 30;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!(target instanceof LivingEntity living)) return false;
        if (!readyForHeadbutt()) return false;

        resetHeadbuttCooldown();

        // 6 HP = 3 kalp.
        boolean hit = target.hurt(damageSources().mobAttack(this), 6.0F);

        if (hit) {
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            double length = Math.sqrt(dx * dx + dz * dz);

            if (length > 0.001D) {
                target.push(dx / length * 0.75D, 0.18D, dz / length * 0.75D);
            }
        }

        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (headbuttCooldown > 0) headbuttCooldown--;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source,
                        float amount) {
        boolean result = super.hurt(source, amount);

        if (result && source.getEntity() instanceof LivingEntity attacker) {
            setTarget(attacker);
            stopDrinking();
            setGrazing(false);
        }

        return result;
    }
}
