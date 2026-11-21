package com.jurassicsurvival.item;

import com.jurassicsurvival.entity.EscapeBoatEntity;
import com.jurassicsurvival.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nullable;

import java.util.List;

/** İşaret fişeği tabancası: her atışta 1 fişek mermisi harcar (orijinal sürümde tabancanın kendisi yok oluyordu). */
public class FlareItem extends Item {
    public FlareItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);
        boolean creative = player.getAbilities().instabuild;
        ItemStack ammo = findAmmo(player);

        if (ammo.isEmpty() && !creative) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("Fişek mermin yok!").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResultHolder.fail(gun);
        }

        if (!level.isClientSide) {
            level.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.5F, 0.5F);

            AABB searchBox = new AABB(player.blockPosition()).inflate(80.0D);
            boolean recalled = false;
            for (EscapeBoatEntity boat : level.getEntitiesOfClass(EscapeBoatEntity.class, searchBox)) {
                if (boat.canRecallWithFlare(level.getGameTime())) {
                    boat.recallBoat();
                    recalled = true;
                    player.sendSystemMessage(Component.literal("ACİL DURUM SİNYALİ ALINDI: Gemi limana geri dönüyor!")
                            .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                    break;
                }
            }
            if (!recalled) {
                player.sendSystemMessage(Component.literal("Gökyüzüne kırmızı işaret fişeği fırlatıldı! Dikkat: Yakındaki dinozorlar ışığa çekiliyor.")
                        .withStyle(ChatFormatting.RED));
            }
            if (!creative) {
                ammo.shrink(1);
            }
        }
        player.getCooldowns().addCooldown(this, 40);
        return InteractionResultHolder.sidedSuccess(gun, level.isClientSide());
    }

    private static ItemStack findAmmo(Player player) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(ModItems.FLARE_AMMO.get())) return s;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Fişek mermisi gerekir. Gemi/tekne ayrıldıktan sonraki 10 dakika içinde ateşlenirse geri çağırır.")
                .withStyle(ChatFormatting.GOLD));
    }
}
