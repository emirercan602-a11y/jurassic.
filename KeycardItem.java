package com.jurassicsurvival.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;

public class KeycardItem extends Item {
    public KeycardItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("InGen Erişim Kartı: Düşen helikopter enkazı çevresinde bulundu. Kilitli InGen sığınak kapılarını açar.")
                .withStyle(ChatFormatting.AQUA));
    }
}
