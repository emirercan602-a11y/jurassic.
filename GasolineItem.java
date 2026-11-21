package com.jurassicsurvival.item;

import com.jurassicsurvival.registry.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;

/**
 * v1.14 benzin maskeleme: 15 dakika, koşarsan bozulur, Scorpios Rex etkilenmez.
 * Süre artık oyun saatine göre tutuluyor (orijinal sürüm her sorguda sayacı düşürüyordu,
 * bu yüzden birkaç dinozor yakındayken 15 dakika saniyeler içinde bitiyordu).
 */
public class GasolineItem extends Item {
    private static final String UNTIL_TAG = "JurassicSurvival_GasolineUntil";
    public static final int DURATION_TICKS = 18000; // 15 dk

    public GasolineItem(Properties props) {
        super(props);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            writablePersisted(player).putLong(UNTIL_TAG, level.getGameTime() + DURATION_TICKS);
            level.playSound(null, player.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.2F, 0.8F);
            player.sendSystemMessage(Component.literal("Üzerine benzin döktün! 15 dakika boyunca normal dinozorlar kokunu alamayacak (Koşarsan görünürsün!).")
                    .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }

    public static boolean isMasked(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(Player.PERSISTED_NBT_TAG, Tag.TAG_COMPOUND)) return false;
        long until = data.getCompound(Player.PERSISTED_NBT_TAG).getLong(UNTIL_TAG);
        return player.level().getGameTime() < until;
    }

    /** Bu oyuncu bu canlıdan gizli mi? (maske aktif, koşmuyor ve canlı benzinden etkilenen türden) */
    public static boolean shouldHideFromMob(Player player, LivingEntity mob) {
        if (mob.getType().is(ModTags.IGNORES_GASOLINE)) return false;
        return isMasked(player) && !player.isSprinting();
    }

    private static CompoundTag writablePersisted(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(Player.PERSISTED_NBT_TAG, Tag.TAG_COMPOUND)) {
            data.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return data.getCompound(Player.PERSISTED_NBT_TAG);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Sağ tıklayıp üzerine dök: 15 dk boyunca normal dinozorlardan gizler (Koşmak maskeyi bozar, Scorpios Rex her türlü görür!).")
                .withStyle(ChatFormatting.GOLD));
    }
}
