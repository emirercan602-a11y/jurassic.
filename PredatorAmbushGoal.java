package com.jurassicsurvival.entity.ai;

import com.jurassicsurvival.item.GasolineItem;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;

public class PredatorAmbushGoal extends Goal {
    private final PathfinderMob predator;
    private Player targetPlayer;
    private int crouchTicks = 0;

    public PredatorAmbushGoal(PathfinderMob predator) {
        this.predator = predator;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.predator.getTarget() != null) return false;

        List<Player> players = this.predator.level().getEntitiesOfClass(
                Player.class,
                this.predator.getBoundingBox().inflate(26.0D)
        );

        for (Player p : players) {
            if (p.isCreative() || p.isSpectator()) continue;
            // Benzin maskesi aktifse ve oyuncu sessizce (Shift/eğilerek veya yürüyerek) gidiyorsa pusu tetiklenmez
            if (GasolineItem.shouldHideFromMob(p, this.predator) && !p.isSprinting()) {
                continue;
            }
            this.targetPlayer = p;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.crouchTicks = 40; // 2 saniye boyunca çalıların arasında pusu kurar ve sessizce izler
        this.predator.setShiftKeyDown(true);
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null || !this.targetPlayer.isAlive()) return;

        this.predator.getLookControl().setLookAt(this.targetPlayer, 30.0F, 30.0F);

        if (this.crouchTicks > 0) {
            this.crouchTicks--;
            // Sessizce hedefin arkasına/yanına süzülme
            this.predator.getNavigation().moveTo(this.targetPlayer, 0.75D);
        } else {
            // Pusu anı! Ani depar ile saldırıya geçer!
            this.predator.setShiftKeyDown(false);
            this.predator.setTarget(this.targetPlayer);
            this.predator.getNavigation().moveTo(this.targetPlayer, 1.55D);
        }
    }

    @Override
    public void stop() {
        this.predator.setShiftKeyDown(false);
        this.targetPlayer = null;
    }
}
