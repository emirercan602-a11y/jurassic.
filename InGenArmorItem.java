package com.jurassicsurvival.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;

public class InGenArmorItem extends ArmorItem {

    public static final ArmorMaterial INGEN_TACTICAL_MATERIAL = new ArmorMaterial() {
        private static final int[] DURABILITY = new int[]{220, 320, 340, 260};
        private static final int[] PROTECTION = new int[]{2, 5, 6, 2}; // Toplam 15 Zırh (Demir zırh denginde, çok aşırı güçlü değil)

        @Override
        public int getDurabilityForType(Type type) {
            return DURABILITY[type.getSlot().getIndex()];
        }

        @Override
        public int getDefenseForType(Type type) {
            return PROTECTION[type.getSlot().getIndex()];
        }

        @Override
        public int getEnchantmentValue() {
            return 12;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public String getName() {
            return "jurassicsurvival:ingen_tactical";
        }

        @Override
        public float getToughness() {
            return 0.5F; // Düşük sertlik: Küçük saldırılara dayanır ama T-Rex, Indominus ve Spinosaurus hala tek atar!
        }

        @Override
        public float getKnockbackResistance() {
            return 0.05F;
        }
    };

    public InGenArmorItem(Type type, Properties props) {
        super(INGEN_TACTICAL_MATERIAL, type, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("InGen Güvenlik Zırhı: Küçük dinozorlara karşı korur ancak T-Rex, Spinosaurus ve Indominus Rex darbesine dayanamaz.")
                .withStyle(ChatFormatting.GRAY));
    }
}
