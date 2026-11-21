package com.jurassicsurvival.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

/**
 * Parasaur, yakınındaki canavarlardan ve (yaratıcı/izleyici olmayan) oyunculardan kaçar.
 * Orijinal sürümde canUse() üst sınıfın yol bulma adımını atlıyordu; bu yüzden Parasaur
 * tehlike yakınındayken yerinde donuyordu. Artık AvoidEntityGoal'ün kendi mantığını kullanıyoruz.
 */
public class ParasaurFleeGoal extends AvoidEntityGoal<LivingEntity> {
    private final ParasaurEntity parasaur;

    public ParasaurFleeGoal(ParasaurEntity parasaur, double speed) {
        super(parasaur, LivingEntity.class, 10.0F, speed, speed * 1.10D,
                e -> e.isAlive() && (e instanceof Monster
                        || (e instanceof Player p && !p.isCreative() && !p.isSpectator())));
        this.parasaur = parasaur;
    }

    @Override
    public boolean canUse() {
        return !parasaur.isBaby() && super.canUse();
    }
}
