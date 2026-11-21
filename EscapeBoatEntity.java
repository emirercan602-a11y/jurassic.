package com.jurassicsurvival.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EscapeBoatEntity extends Boat {
    public static final int MAX_HITS = 10;                    // Mosasaurus tekneyi 10 vuruşta batırır
    private static final long RECALL_WINDOW_TICKS = 12000L;   // 10 dakika

    private int mosasaurusHits = 0;
    private BlockPos homePos = null;
    private long lastOccupiedTick = -1L;

    public EscapeBoatEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (homePos == null) {
                homePos = this.blockPosition();
            }
            if (this.isVehicle()) {
                lastOccupiedTick = this.level().getGameTime();
            }
        }
    }

    /** Tekne son 10 dakika içinde kullanıldıysa ve limandan uzaktaysa fişekle geri çağrılabilir. */
    public boolean canRecallWithFlare(long gameTime) {
        return homePos != null && lastOccupiedTick >= 0
                && gameTime - lastOccupiedTick <= RECALL_WINDOW_TICKS
                && this.blockPosition().distSqr(homePos) > 36;
    }

    public void recallBoat() {
        if (homePos == null) return;
        this.setDeltaMovement(Vec3.ZERO);
        this.moveTo(homePos.getX() + 0.5D, homePos.getY(), homePos.getZ() + 0.5D, this.getYRot(), 0.0F);
        lastOccupiedTick = -1L;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof MosasaurusEntity) {
            mosasaurusHits++;
            if (!this.level().isClientSide && this.level().getServer() != null) {
                for (ServerPlayer player : this.level().getServer().getPlayerList().getPlayers()) {
                    player.sendSystemMessage(Component.literal("TEKNE HASAR ALDI! (" + mosasaurusHits + "/" + MAX_HITS + ") Mosasaurus tekneyi parçalıyor!")
                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                }
                if (mosasaurusHits >= MAX_HITS) {
                    for (ServerPlayer player : this.level().getServer().getPlayerList().getPlayers()) {
                        player.sendSystemMessage(Component.literal("TEKNE BATTI! Mosasaurus adadan kaçışınızı engelledi!")
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
                    }
                }
            }
            if (mosasaurusHits >= MAX_HITS && !this.level().isClientSide) {
                this.discard();
            }
            return true;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MosasaurusHits", mosasaurusHits);
        tag.putLong("LastOccupied", lastOccupiedTick);
        if (homePos != null) tag.putLong("Home", homePos.asLong());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        mosasaurusHits = tag.getInt("MosasaurusHits");
        lastOccupiedTick = tag.contains("LastOccupied") ? tag.getLong("LastOccupied") : -1L;
        if (tag.contains("Home")) homePos = BlockPos.of(tag.getLong("Home"));
    }
}
