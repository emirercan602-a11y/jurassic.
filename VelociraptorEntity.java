package com.jurassicsurvival.entity;

import com.jurassicsurvival.item.GasolineItem;

import net.minecraft.world.item.crafting.Ingredient;

import com.jurassicsurvival.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class VelociraptorEntity extends Animal {
 private int cooldown;
 public VelociraptorEntity(EntityType<? extends VelociraptorEntity> t,Level l){super(t,l);}
 public static AttributeSupplier.Builder createAttributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,12).add(Attributes.MOVEMENT_SPEED,.48).add(Attributes.ATTACK_DAMAGE,4).add(Attributes.FOLLOW_RANGE,28);}
 protected void registerGoals(){
  targetSelector.addGoal(1,new HurtByTargetGoal(this));
  targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this,Player.class,true,e -> !(e instanceof Player p && GasolineItem.shouldHideFromMob(p, this))));
  targetSelector.addGoal(3,new NearestAttackableTargetGoal<>(this,Animal.class,16,true,false,e->!(e instanceof VelociraptorEntity)));
  goalSelector.addGoal(1,new MeleeAttackGoal(this,1.35,true));
  goalSelector.addGoal(2,new LeapAtTargetGoal(this,.55F));
  goalSelector.addGoal(3,new VelociraptorPackGoal(this));
  goalSelector.addGoal(4,new BreedGoal(this,1.1));
  goalSelector.addGoal(5,new TemptGoal(this,1.1,Ingredient.of(Items.CHICKEN),false));
  goalSelector.addGoal(6,new WaterAvoidingRandomStrollGoal(this,1));
  goalSelector.addGoal(7,new LookAtPlayerGoal(this,Player.class,12));
  goalSelector.addGoal(8,new RandomLookAroundGoal(this));
 }
 public boolean isFood(ItemStack s){return s.is(Items.CHICKEN)||s.is(Items.BEEF)||s.is(Items.PORKCHOP)||s.is(Items.MUTTON)||s.is(Items.RABBIT);}
 @Nullable public AgeableMob getBreedOffspring(ServerLevel l,AgeableMob o){return ModEntities.VELOCIRAPTOR.get().create(l);}
 public boolean canMate(Animal o){return o instanceof VelociraptorEntity&&super.canMate(o);}
 public boolean doHurtTarget(Entity t){if(cooldown>0)return false;cooldown=16;return t.hurt(damageSources().mobAttack(this),4);}
 public void setTarget(@Nullable LivingEntity t){super.setTarget(t);if(t!=null&&!level().isClientSide)for(VelociraptorEntity r:level().getEntitiesOfClass(VelociraptorEntity.class,getBoundingBox().inflate(14),e->e!=this&&e.isAlive()))r.setPackTarget(t);}
 private void setPackTarget(LivingEntity t){super.setTarget(t);}
 public void aiStep(){super.aiStep();if(cooldown>0)cooldown--;}
}