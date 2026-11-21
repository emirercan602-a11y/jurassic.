package com.jurassicsurvival.command;

import com.jurassicsurvival.JurassicSurvival;
import com.jurassicsurvival.registry.ModEntities;
import com.jurassicsurvival.world.BunkerAndWreckBuilder;
import com.jurassicsurvival.world.IslandAtmosphereBuilder;
import com.jurassicsurvival.world.IslandBuilder;
import com.jurassicsurvival.world.PrehistoricTreeBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * /js_island  (v0.3'ten, aynen korunuyor)
 * /jurassic island|bunker|wreck|container|vehicle|boat|gyrosphere
 * /jurassic fence <uzunluk>   /jurassic tree [yükseklik]
 * Hepsi op (seviye 2) ister; yapılar komutu yazanın bulunduğu yere kurulur.
 */
@Mod.EventBusSubscriber(modid = JurassicSurvival.MOD_ID)
public class JurassicCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("js_island")
                .requires(s -> s.hasPermission(2))
                .executes(JurassicCommands::island));

        event.getDispatcher().register(Commands.literal("jurassic")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("island").executes(JurassicCommands::island))
                .then(Commands.literal("bunker").executes(ctx -> {
                    BunkerAndWreckBuilder.buildInGenBunker(level(ctx), here(ctx).offset(4, -1, 4));
                    return ok(ctx, "InGen sığınağı kuruldu (kapıyı Keycard ile aç).");
                }))
                .then(Commands.literal("wreck").executes(ctx -> {
                    BunkerAndWreckBuilder.buildHelicopterWreck(level(ctx), here(ctx).offset(4, 0, 4));
                    return ok(ctx, "Helikopter enkazı kuruldu; Keycard yerde duruyor.");
                }))
                .then(Commands.literal("container").executes(ctx -> {
                    IslandAtmosphereBuilder.buildRustedContainer(level(ctx), here(ctx).offset(3, 0, 3), level(ctx).getRandom().nextBoolean());
                    return ok(ctx, "Paslı konteyner kuruldu.");
                }))
                .then(Commands.literal("vehicle").executes(ctx -> {
                    IslandAtmosphereBuilder.buildWreckedVehicle(level(ctx), here(ctx).offset(3, 0, 3));
                    return ok(ctx, "Enkaz araç kuruldu.");
                }))
                .then(Commands.literal("fence")
                        .then(Commands.argument("length", IntegerArgumentType.integer(4, 100)).executes(ctx -> {
                            IslandAtmosphereBuilder.buildBrokenFenceLine(level(ctx), here(ctx).offset(2, 0, 2),
                                    IntegerArgumentType.getInteger(ctx, "length"), true);
                            return ok(ctx, "Kırık elektrikli çit kuruldu.");
                        })))
                .then(Commands.literal("tree")
                        .executes(ctx -> tree(ctx, 16))
                        .then(Commands.argument("height", IntegerArgumentType.integer(8, 40))
                                .executes(ctx -> tree(ctx, IntegerArgumentType.getInteger(ctx, "height")))))
                .then(Commands.literal("boat").executes(ctx -> {
                    ServerLevel lvl = level(ctx);
                    Entity boat = ModEntities.ESCAPE_BOAT.get().create(lvl);
                    if (boat == null) return 0;
                    BlockPos p = here(ctx);
                    boat.moveTo(p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D, 0.0F, 0.0F);
                    lvl.addFreshEntity(boat);
                    return ok(ctx, "Kaçış teknesi oluşturuldu (100. gün olayı için üzerine bin).");
                }))
                .then(Commands.literal("gyrosphere").executes(ctx -> {
                    ServerLevel lvl = level(ctx);
                    Entity gyro = ModEntities.GYROSPHERE.get().create(lvl);
                    if (gyro == null) return 0;
                    BlockPos p = here(ctx);
                    gyro.moveTo(p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D, 0.0F, 0.0F);
                    lvl.addFreshEntity(gyro);
                    return ok(ctx, "Kırık Jirosfer oluşturuldu: Cam + Benzin ile tamir et.");
                })));
    }

    private static int island(CommandContext<CommandSourceStack> ctx) {
        if (IslandBuilder.isBuilding()) {
            ctx.getSource().sendFailure(Component.literal("Zaten bir ada oluşturuluyor, bitmesini bekle."));
            return 0;
        }
        IslandBuilder.build(level(ctx), here(ctx));
        return ok(ctx, "Jurassic Survival adası oluşturulmaya başladı (500 blok yarıçap).");
    }

    private static int tree(CommandContext<CommandSourceStack> ctx, int height) {
        PrehistoricTreeBuilder.generatePrehistoricTree(level(ctx), here(ctx).offset(4, 0, 4), height);
        return ok(ctx, "Dev prehistorik ağaç dikildi.");
    }

    private static ServerLevel level(CommandContext<CommandSourceStack> ctx) {
        return ctx.getSource().getLevel();
    }

    private static BlockPos here(CommandContext<CommandSourceStack> ctx) {
        return BlockPos.containing(ctx.getSource().getPosition());
    }

    private static int ok(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(() -> Component.literal(msg), true);
        return 1;
    }
}
