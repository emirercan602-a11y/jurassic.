package com.jurassicsurvival.registry;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.item.FlareItem;
import com.jurassicsurvival.item.GasolineItem;
import com.jurassicsurvival.item.InGenArmorItem;
import com.jurassicsurvival.item.KeycardItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JurassicSurvival.MOD_ID);

    public static final RegistryObject<Item> GASOLINE = ITEMS.register("gasoline",
            () -> new GasolineItem(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> FLARE_GUN = ITEMS.register("flare_gun",
            () -> new FlareItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FLARE_AMMO = ITEMS.register("flare_ammo",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> KEYCARD = ITEMS.register("ingen_keycard",
            () -> new KeycardItem(new Item.Properties()));
    public static final RegistryObject<Item> NIGHT_VISION_GOGGLES = ITEMS.register("night_vision_goggles",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WALKIE_TALKIE = ITEMS.register("walkie_talkie",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CAMPER_JACKET = ITEMS.register("camper_jacket",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> INGEN_HELMET = ITEMS.register("ingen_helmet",
            () -> new InGenArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> INGEN_CHESTPLATE = ITEMS.register("ingen_chestplate",
            () -> new InGenArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> INGEN_LEGGINGS = ITEMS.register("ingen_leggings",
            () -> new InGenArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> INGEN_BOOTS = ITEMS.register("ingen_boots",
            () -> new InGenArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

    // ---- Spawn yumurtaları (yaratıcı modda dinozorları çağırmak için) ----
    private static RegistryObject<Item> egg(String name, Supplier<? extends EntityType<? extends Mob>> type, int bg, int fg) {
        return ITEMS.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(type, bg, fg, new Item.Properties()));
    }

    public static final RegistryObject<Item> STRUTHIOMIMUS_EGG = egg("struthiomimus", ModEntities.STRUTHIOMIMUS, 0x9C7B52, 0xD9C08A);
    public static final RegistryObject<Item> ANKYLOSAURUS_EGG = egg("ankylosaurus", ModEntities.ANKYLOSAURUS, 0x7E6446, 0xA89C80);
    public static final RegistryObject<Item> PACHYCEPHALOSAURUS_EGG = egg("pachycephalosaurus", ModEntities.PACHYCEPHALOSAURUS, 0x96594A, 0xD2A77F);
    public static final RegistryObject<Item> TRICERATOPS_EGG = egg("triceratops", ModEntities.TRICERATOPS, 0x786E5A, 0x965A50);
    public static final RegistryObject<Item> STEGOSAURUS_EGG = egg("stegosaurus", ModEntities.STEGOSAURUS, 0x567646, 0xC87832);
    public static final RegistryObject<Item> PARASAUR_EGG = egg("parasaur", ModEntities.PARASAUR, 0x5A6E82, 0xC85A46);
    public static final RegistryObject<Item> BRACHIOSAURUS_EGG = egg("brachiosaurus", ModEntities.BRACHIOSAURUS, 0x6E826E, 0xAFC0A0);
    public static final RegistryObject<Item> COMPSOGNATHUS_EGG = egg("compsognathus", ModEntities.COMPSOGNATHUS, 0x608046, 0xBEC896);
    public static final RegistryObject<Item> VELOCIRAPTOR_EGG = egg("velociraptor", ModEntities.VELOCIRAPTOR, 0x8C5432, 0x3C2319);
    public static final RegistryObject<Item> DIMORPHODON_EGG = egg("dimorphodon", ModEntities.DIMORPHODON, 0x78504A, 0x96644F);
    public static final RegistryObject<Item> BARYONYX_EGG = egg("baryonyx", ModEntities.BARYONYX, 0x4A6860, 0x96AA96);
    public static final RegistryObject<Item> TREX_EGG = egg("trex", ModEntities.TREX, 0x4B5A32, 0xC9B36A);
    public static final RegistryObject<Item> SPINOSAURUS_EGG = egg("spinosaurus", ModEntities.SPINOSAURUS, 0x3C5A50, 0xB4503C);
    public static final RegistryObject<Item> MOSASAURUS_EGG = egg("mosasaurus", ModEntities.MOSASAURUS, 0x3C5064, 0xBEC8C8);
}
