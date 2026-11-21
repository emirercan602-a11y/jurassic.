package com.jurassicsurvival.event;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.entity.EscapeBoatEntity;
import com.jurassicsurvival.entity.MosasaurusEntity;
import com.jurassicsurvival.item.GasolineItem;
import com.jurassicsurvival.item.KeycardItem;
import com.jurassicsurvival.registry.ModEntities;
import com.jurassicsurvival.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Birleşik olay yöneticisi: loot (v1.14/v1.15), 80. gün gizli buton (v1.15),
 * keycard kapıları (v1.16), 100. gün Mosasaurus pususu (v1.13), dev yırtıcı tek vuruş (v1.16).
 */
@Mod.EventBusSubscriber(modid = JurassicSurvival.MOD_ID)
public class MasterUnifiedEventHandler {
    private static final long DAY_80_TICK = 79L * 24000L;
    private static final long DAY_100_TICK = 99L * 24000L + 12000L;

    // ------------------------------------------------------------------ LOOT
    private static boolean isSupplyTable(ResourceLocation id) {
        String path = id.getPath().toLowerCase(Locale.ROOT);
        // Eski kontrol path.contains("lab") idi ve "oak_slab" gibi BLOK loot tablolarına da uyuyordu
        // (her slab kırıldığında benzin düşerdi). Artık sadece sandık/kasa tablolarına uygulanır.
        if (path.startsWith("blocks/") || path.startsWith("entities/") || path.startsWith("gameplay/")
                || path.startsWith("archaeology/")) {
            return false;
        }
        return path.startsWith("chests/") || path.contains("crate") || path.contains("armory")
                || path.contains("bunker") || path.contains("outpost");
    }

    private static LootPool pool(String name, float chance, Supplier<Item>... items) {
        LootPool.Builder b = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1.0F));
        if (chance < 1.0F) b.when(LootItemRandomChanceCondition.randomChance(chance));
        for (Supplier<Item> it : items) b.add(LootItem.lootTableItem(it.get()));
        return b.build();
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!isSupplyTable(event.getName())) return;

        event.getTable().addPool(pool("js_gasoline_guaranteed", 1.0F, ModItems.GASOLINE::get));   // %100 1 adet
        event.getTable().addPool(pool("js_gasoline_bonus", 0.05F, ModItems.GASOLINE::get));       // %5 2. adet
        event.getTable().addPool(pool("js_flare_gun", 0.20F, ModItems.FLARE_GUN::get));           // %20
        event.getTable().addPool(pool("js_flare_ammo", 0.65F, ModItems.FLARE_AMMO::get));         // %65 (bazen hiç)
        event.getTable().addPool(pool("js_night_vision", 0.10F, ModItems.NIGHT_VISION_GOGGLES::get)); // %10
        event.getTable().addPool(pool("js_walkie_talkie", 0.10F, ModItems.WALKIE_TALKIE::get));   // %10
        event.getTable().addPool(pool("js_camper_jacket", 0.15F, ModItems.CAMPER_JACKET::get));   // %15 kıyafet
        event.getTable().addPool(pool("js_ingen_armor", 0.15F, ModItems.INGEN_HELMET::get, ModItems.INGEN_CHESTPLATE::get,
                ModItems.INGEN_LEGGINGS::get, ModItems.INGEN_BOOTS::get));                         // %15 InGen zırhı (1 parça)
    }

    // ------------------------------------------------------- BLOK ETKİLEŞİMLERİ
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return; // olay iki el için de tetiklenir
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Player player = event.getEntity();

        // 80. günden sonra baltayla kütüğe sağ tık: gizli yardım butonu
        if (level.getDayTime() >= DAY_80_TICK && state.is(BlockTags.LOGS) && event.getItemStack().getItem() instanceof AxeItem) {
            Direction face = event.getFace() != null ? event.getFace() : Direction.NORTH;
            BlockPos btnPos = pos.relative(face);
            if (level.isEmptyBlock(btnPos)) {
                BlockState btn = Blocks.STONE_BUTTON.defaultBlockState();
                if (face == Direction.UP) {
                    btn = btn.setValue(ButtonBlock.FACE, AttachFace.FLOOR);
                } else if (face == Direction.DOWN) {
                    btn = btn.setValue(ButtonBlock.FACE, AttachFace.CEILING);
                } else {
                    btn = btn.setValue(ButtonBlock.FACE, AttachFace.WALL).setValue(ButtonBlock.FACING, face);
                }
                level.setBlock(btnPos, btn, 3);
                level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.2F, 0.8F);
                player.sendSystemMessage(Component.literal("Ağaç kabuğunun altındaki GİZLİ YARDIM ÇAĞRISI BUTONU açığa çıktı! Butona bas ve sinyali gönder!")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            }
        }

        // Bozuk jeneratöre benzin dök: elektrikli çitler aktif
        if ((state.is(Blocks.IRON_BLOCK) || state.is(Blocks.BLAST_FURNACE))
                && event.getItemStack().getItem() instanceof GasolineItem) {
            if (!player.getAbilities().instabuild) event.getItemStack().shrink(1);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.5F, 1.0F);
            player.sendSystemMessage(Component.literal("Jeneratöre benzin döküldü! Yüksek voltajlı elektrikli çitler aktif edildi!")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        }

        // Keycard ile kilitli InGen sığınak kapısı
        if (state.is(Blocks.IRON_DOOR)) {
            if (event.getItemStack().getItem() instanceof KeycardItem) {
                setDoorOpen(level, pos, state, !state.getValue(DoorBlock.OPEN));
                level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.sendSystemMessage(Component.literal("InGen Erişim Kartı onaylandı! Sığınak kapısı açıldı.")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                event.setCanceled(true);
            } else {
                player.displayClientMessage(Component.literal("KİLİTLİ KAPI: Açmak için düşen helikopter enkazından bulacağın InGen Erişim Kartı gerekli!")
                        .withStyle(ChatFormatting.RED), true);
            }
        }
    }

    /** Demir kapının alt ve üst yarısını birlikte açar/kapatır (eskiden sadece tıklanan yarı değişiyordu). */
    private static void setDoorOpen(Level level, BlockPos clicked, BlockState state, boolean open) {
        BlockPos lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? clicked : clicked.below();
        BlockState lowerState = level.getBlockState(lower);
        BlockState upperState = level.getBlockState(lower.above());
        if (lowerState.is(Blocks.IRON_DOOR)) level.setBlock(lower, lowerState.setValue(DoorBlock.OPEN, open), 10);
        if (upperState.is(Blocks.IRON_DOOR)) level.setBlock(lower.above(), upperState.setValue(DoorBlock.OPEN, open), 10);
    }

    // ------------------------------------------------- 100. GÜN MOSASAURUS PUSUSU
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()
                || event.level.dimension() != Level.OVERWORLD) return;
        ServerLevel level = (ServerLevel) event.level;
        if (level.getGameTime() % 20L != 0L || level.getDayTime() < DAY_100_TICK) return;

        EscapeData data = EscapeData.get(level);
        if (data.ambushed) return;

        for (ServerPlayer player : level.players()) {
            if (!(player.getVehicle() instanceof EscapeBoatEntity)) continue;
            BlockPos spawn = findWaterNear(level, player.blockPosition());
            if (spawn == null) continue;

            MosasaurusEntity mosa = ModEntities.MOSASAURUS.get().create(level);
            if (mosa == null) continue;
            mosa.moveTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, 0.0F, 0.0F);
            mosa.setTarget(player);
            level.addFreshEntity(mosa);
            data.ambushed = true;
            data.setDirty();
            for (ServerPlayer p : level.players()) {
                p.sendSystemMessage(Component.literal("100. GÜN - KAÇIŞ: Boş kalan Mosasaurus kafesinden sular kabardı... Suların altından devasa canavar saldırıya geçti!")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            }
            break;
        }
    }

    private static BlockPos findWaterNear(ServerLevel level, BlockPos center) {
        for (int r = 10; r <= 20; r += 5) {
            for (int i = 0; i < 8; i++) {
                double a = i * Math.PI / 4.0D;
                BlockPos p = center.offset((int) Math.round(Math.cos(a) * r), -2, (int) Math.round(Math.sin(a) * r));
                if (level.getFluidState(p).is(FluidTags.WATER) && level.getFluidState(p.above()).is(FluidTags.WATER)) {
                    return p;
                }
            }
        }
        return null;
    }

    /** Dünya başına kalıcı durum (orijinalde static bir boolean'dı: başka dünyaya da sızıyordu ve kaydedilmiyordu). */
    public static class EscapeData extends SavedData {
        public boolean ambushed = false;

        public static EscapeData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(EscapeData::load, EscapeData::new, "jurassicsurvival_day100_escape");
        }

        public static EscapeData load(CompoundTag tag) {
            EscapeData data = new EscapeData();
            data.ambushed = tag.getBoolean("Ambushed");
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putBoolean("Ambushed", this.ambushed);
            return tag;
        }
    }

    // --------------------------------------------- DEV YIRTICILAR TEK VURUŞ
    @SubscribeEvent
    public static void onApexDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player && event.getSource().getEntity() instanceof LivingEntity attacker) {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(attacker.getType());
            if (key == null) return;
            String name = key.getPath();
            if (name.equals("trex") || name.equals("spinosaurus") || name.contains("indominus")) {
                event.setAmount(999.0F); // InGen zırhı olsa bile
            }
        }
    }
}
