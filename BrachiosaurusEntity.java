package com.jurassicsurvival.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BrachiosaurusEntity extends Animal {

    public BrachiosaurusEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.5F); // Dev adımları sayesinde blokların üzerinden rahatça geçer
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)          // 100 Kalp (200 HP)
                .add(Attributes.ARMOR, 18.0D)                // Çok güçlü kalın deri (Zırh koruması)
                .add(Attributes.ARMOR_TOUGHNESS, 10.0D)      // Ağır darbelere ekstra dayanıklılık
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)  // %100 Geri savrulma direnci (Yerinden oynamaz)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)       // Ağır, heybetli ve yavaş yürüyüş
                .add(Attributes.ATTACK_DAMAGE, 12.0D)        // 6 Kalp hasar (12.0 HP)
                .add(Attributes.ATTACK_KNOCKBACK, 3.0D)      // Vurduğunda uzağa savurur
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // Sadece saldırıya uğradığında (HurtByTargetGoal tetiklendiğinde) karşı saldırıya geçer
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        // Nötr / Pasif Savunma: Kendisine saldırılmadığı sürece kimseye saldırmaz!
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(BrachiosaurusEntity.class));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // Devasa ayak sesleri
        this.playSound(SoundEvents.RAVAGER_STEP, 0.8F, 0.5F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        // Derin ve uzaklara yayılan balina/dev dinozor çağrısı
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.6F; // Sesi adanın uzak noktalarından bile duyulur
    }

    @Override
    public float getVoicePitch() {
        return 0.45F; // Çok kalın ve devasa ses tonu
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.JUNGLE_LEAVES) || stack.is(Items.OAK_LEAVES);
    }
}
