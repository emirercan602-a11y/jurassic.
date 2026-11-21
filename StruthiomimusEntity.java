package com.jurassicsurvival.entity;

import net.minecraft.world.item.crafting.Ingredient;

import com.jurassicsurvival.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class StruthiomimusEntity extends Animal implements GrazingDino {
 private boolean drinking,grazing;
 public StruthiomimusEntity(EntityType<? extends StruthiomimusEntity> t,Level l){super(t,l);}
 public static AttributeSupplier.Builder createAttributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,16).add(Attributes.MOVEMENT_SPEED,.34).add(Attributes.ATTACK_DAMAGE,2).add(Attributes.FOLLOW_RANGE,28);}
 @Override protected void registerGoals(){
  targetSelector.addGoal(1,new HurtByTargetGoal(this));
  goalSelector.addGoal(1,new MeleeAttackGoal(this,1.15,true));
  goalSelector.addGoal(2,new PanicGoal(this,1.35));
  goalSelector.addGoal(3,new BreedGoal(this,1.05));
  goalSelector.addGoal(4,new TemptGoal(this,1.1,Ingredient.of(Items.WHEAT_SEEDS),false));
  goalSelector.addGoal(5,new StruthiomimusDrinkGoal(this));
  goalSelector.addGoal(6,new StruthiomimusGrazeGoal(this));
  goalSelector.addGoal(7,new StruthiomimusHerdGoal(this));
  goalSelector.addGoal(8,new WaterAvoidingRandomStrollGoal(this,1));
  goalSelector.addGoal(9,new LookAtPlayerGoal(this,Player.class,8));
  goalSelector.addGoal(10,new RandomLookAroundGoal(this));
 }
 public boolean isFood(ItemStack s){return s.is(Items.WHEAT_SEEDS);}
 @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel l,AgeableMob o){return ModEntities.STRUTHIOMIMUS.get().create(l);}
 @Override public boolean canMate(Animal o){return o instanceof StruthiomimusEntity&&super.canMate(o);}
 public boolean isDrinking(){return drinking;} public boolean isGrazing(){return grazing;}
 public void drinkStart(){drinking=true;grazing=false;getNavigation().stop();}
 public void drinkStop(){drinking=false;}
 public void graze(boolean v){grazing=v;if(v){drinking=false;getNavigation().stop();}}
 @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource s,float a){boolean r=super.hurt(s,a);if(r&&s.getEntity() instanceof LivingEntity x){setTarget(x);drinkStop();graze(false);}return r;}
}