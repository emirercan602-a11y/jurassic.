package com.jurassicsurvival.entity;

import net.minecraft.world.item.crafting.Ingredient;

import com.jurassicsurvival.registry.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class StegosaurusEntity extends Animal implements GrazingDino {
    private boolean grazing;
    private boolean drinking;
    private int tailCooldown;

    public StegosaurusEntity(EntityType<? extends StegosaurusEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 35.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.17D)
            .add(Attributes.ATTACK_DAMAGE, 8.0D)
            .add(Attributes.FOLLOW_RANGE, 22.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));

        goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.95D, true));
        goalSelector.addGoal(2, new PanicGoal(this, 1.05D));
        goalSelector.addGoal(3, new BreedGoal(this, 0.80D));
        goalSelector.addGoal(4,
            new TemptGoal(this, 0.85D, Ingredient.of(Items.WHEAT), false));
        goalSelector.addGoal(5, new StegosaurusDrinkGoal(this));
        goalSelector.addGoal(6, new StegosaurusGrazeGoal(this));
        goalSelector.addGoal(7, new StegosaurusHerdGoal(this));
        goalSelector.addGoal(8,
            new WaterAvoidingRandomStrollGoal(this, 0.75D));
        goalSelector.addGoal(9,
            new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.WHEAT);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return ModEntities.STEGOSAURUS.get().create(level);
    }

    @Override
    public boolean canMate(Animal other) {
        return other instanceof StegosaurusEntity && super.canMate(other);
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

    public boolean readyForTailAttack() {
        return tailCooldown <= 0;
    }

    private void resetTailCooldown() {
        tailCooldown = 30;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!readyForTailAttack())
            return false;

        resetTailCooldown();

        // 8 HP = 4 kalp.
        boolean hit = target.hurt(damageSources().mobAttack(this), 8.0F);

        if (hit) {
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            double len = Math.sqrt(dx * dx + dz * dz);

            if (len > 0.001D) {
                target.push(
                    dx / len * 0.85D,
                    0.18D,
                    dz / len * 0.85D
                );
            }
        }

        return hit;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source,
                        float amount) {

        // Arkadan gelen saldırıda otomatik kuyruk savunması.
        if (source.getEntity() instanceof LivingEntity attacker
            && readyForTailAttack()) {

            double dx = attacker.getX() - getX();
            double dz = attacker.getZ() - getZ();
            double len = Math.sqrt(dx * dx + dz * dz);

            if (len > 0.001D) {
                double yaw = Math.toRadians(getYRot());
                double forwardX = -Math.sin(yaw);
                double forwardZ = Math.cos(yaw);

                double dot =
                    (dx / len) * forwardX +
                    (dz / len) * forwardZ;

                // Saldırgan arkadaysa.
                if (dot < -0.35D) {
                    doHurtTarget(attacker);
                }
            }
        }

        boolean result = super.hurt(source, amount);

        if (result && source.getEntity() instanceof LivingEntity attacker) {
            setTarget(attacker);
            stopDrinking();
            setGrazing(false);
        }

        return result;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (tailCooldown > 0)
            tailCooldown--;
    }
}
