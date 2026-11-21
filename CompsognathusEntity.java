package com.jurassicsurvival.entity;

import net.minecraft.world.item.crafting.Ingredient;

import com.jurassicsurvival.registry.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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

public class CompsognathusEntity extends Animal {

    private int biteCooldown;

    public CompsognathusEntity(EntityType<? extends CompsognathusEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 8.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.42D)
            .add(Attributes.ATTACK_DAMAGE, 2.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    @Override
    protected void registerGoals() {
        // Saldırılınca karşılık verir.
        targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // Hızlı sürü saldırısı.
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, true));
        goalSelector.addGoal(2, new PanicGoal(this, 1.45D));

        // Et ile üreme.
        goalSelector.addGoal(3, new BreedGoal(this, 1.15D));
        goalSelector.addGoal(4,
            new TemptGoal(this, 1.15D, Ingredient.of(Items.CHICKEN), false));

        // Sürü halinde kalma.
        goalSelector.addGoal(5, new CompsognathusHerdGoal(this));

        // Rastgele dolaşma.
        goalSelector.addGoal(6,
            new WaterAvoidingRandomStrollGoal(this, 1.15D));

        goalSelector.addGoal(7,
            new LookAtPlayerGoal(this, Player.class, 8.0F));

        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.CHICKEN);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return ModEntities.COMPSOGNATHUS.get().create(level);
    }

    @Override
    public boolean canMate(Animal other) {
        return other instanceof CompsognathusEntity && super.canMate(other);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (biteCooldown > 0) {
            return false;
        }

        biteCooldown = 20;

        // 2 HP = 1 kalp.
        boolean hit = target.hurt(damageSources().mobAttack(this), 2.0F);

        if (hit && target instanceof LivingEntity living) {
            // Zehir: yaklaşık 5 saniye.
            living.addEffect(new MobEffectInstance(
                MobEffects.POISON,
                100,
                0
            ));

            // Isırılan hedefe küçük bir geri itme.
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            double length = Math.sqrt(dx * dx + dz * dz);

            if (length > 0.001D) {
                target.push(
                    dx / length * 0.20D,
                    0.08D,
                    dz / length * 0.20D
                );
            }
        }

        return hit;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);

        // Bir Compsognathus hedef seçtiğinde yakındaki sürüyü de alarma geçir.
        if (target != null && !level().isClientSide) {
            for (CompsognathusEntity other :
                level().getEntitiesOfClass(
                    CompsognathusEntity.class,
                    getBoundingBox().inflate(10.0D),
                    e -> e != this && e.isAlive())) {

                if (other.getTarget() == null) {
                    other.setTargetDirect(target);
                }
            }
        }
    }

    private void setTargetDirect(LivingEntity target) {
        super.setTarget(target);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (biteCooldown > 0) {
            biteCooldown--;
        }
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source,
                        float amount) {

        boolean result = super.hurt(source, amount);

        if (result && source.getEntity() instanceof LivingEntity attacker) {
            setTarget(attacker);
        }

        return result;
    }
}
