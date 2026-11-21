package com.jurassicsurvival.entity;

import com.jurassicsurvival.item.GasolineItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

/** Kırık Jirosfer: Cam + Benzin ile tamir edilir, sonra sürülür (WASD, Shift ile iner). */
public class GyrosphereEntity extends Entity {
    private boolean glassRepaired = false;
    private boolean fueled = false;

    public GyrosphereEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.glassRepaired = tag.getBoolean("GlassRepaired");
        this.fueled = tag.getBoolean("Fueled");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("GlassRepaired", this.glassRepaired);
        tag.putBoolean("Fueled", this.fueled);
    }

    // Orijinalde yoktu: bunlar olmadan oyuncu Jirosfer'i hiç seçemiyor ve süremiyordu.
    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity first = this.getFirstPassenger();
        return first instanceof LivingEntity living ? living : null;
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity rider = this.getControllingPassenger();
        if (rider != null && this.isControlledByLocalInstance()) {
            this.setYRot(rider.getYRot());
            this.moveRelative(0.06F, new Vec3(rider.xxa * 0.6D, 0.0D, rider.zza));
            Vec3 m = this.getDeltaMovement();
            this.setDeltaMovement(m.x * 0.9D, this.onGround() ? 0.0D : m.y - 0.04D, m.z * 0.9D);
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else if (!this.level().isClientSide && rider == null && !this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!this.level().isClientSide) {
            if (!glassRepaired && (held.is(Items.GLASS) || held.is(Items.GLASS_PANE))) {
                glassRepaired = true;
                if (!player.getAbilities().instabuild) held.shrink(1);
                this.level().playSound(null, this.blockPosition(), SoundEvents.GLASS_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                player.sendSystemMessage(Component.literal("Jirosferin kırık cam kubbesi onarıldı! Şimdi benzin doldurmalısın.")
                        .withStyle(ChatFormatting.AQUA));
                return InteractionResult.SUCCESS;
            }
            if (!fueled && held.getItem() instanceof GasolineItem) {
                fueled = true;
                if (!player.getAbilities().instabuild) held.shrink(1);
                this.level().playSound(null, this.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 1.0F, 0.9F);
                player.sendSystemMessage(Component.literal("Jirosferin deposuna benzin dolduruldu!")
                        .withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS;
            }
            if (glassRepaired && fueled) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
            player.sendSystemMessage(Component.literal("Bu Jirosfer bozuk! Tamir için Cam ve Benzin gerekiyor.")
                    .withStyle(ChatFormatting.RED));
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }
}
