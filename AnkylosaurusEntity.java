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

public class AnkylosaurusEntity extends Animal implements GrazingDino {
 private boolean grazing,drinking;
 public AnkylosaurusEntity(EntityType<? extends AnkylosaurusEntity> t,Level l){super(t,l);}
 public static AttributeSupplier.Builder createAttributes(){
  return Mob.createMobAttributes()
   .add(Attributes.MAX_HEALTH,30)
   .add(Attributes.MOVEMENT_SPEED,.18)
   .add(Attributes.ATTACK_DAMAGE,8)
   .add(Attributes.FOLLOW_RANGE,20)
   .add(Attributes.KNOCKBACK_RESISTANCE,.35);
 }
 @Override protected void registerGoals(){
  targetSelector.addGoal(1,new HurtByTargetGoal(this));
  goalSelector.addGoal(1,new MeleeAttackGoal(this,1.0,true));
  goalSelector.addGoal(2,new PanicGoal(this,1.15));
  goalSelector.addGoal(3,new BreedGoal(this,.8));
  goalSelector.addGoal(4,new TemptGoal(this,.85,Ingredient.of(Items.SWEET_BERRIES),false));
  goalSelector.addGoal(5,new AnkylosaurusDrinkGoal(this));
  goalSelector.addGoal(6,new AnkylosaurusGrazeGoal(this));
  goalSelector.addGoal(7,new WaterAvoidingRandomStrollGoal(this,.8));
  goalSelector.addGoal(8,new LookAtPlayerGoal(this,Player.class,7));
  goalSelector.addGoal(9,new RandomLookAroundGoal(this));
 }
 public boolean isFood(ItemStack s){return s.is(Items.SWEET_BERRIES);}
 @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel l,AgeableMob o){return ModEntities.ANKYLOSAURUS.get().create(l);}
 @Override public boolean canMate(Animal o){return o instanceof AnkylosaurusEntity&&super.canMate(o);}
 public boolean isGrazing(){return grazing;} public boolean isDrinking(){return drinking;}
 public void graze(boolean v){grazing=v;if(v)drinking=false;}
 public void drinkStart(){drinking=true;grazing=false;getNavigation().stop();}
 public void drinkStop(){drinking=false;}
 @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource s,float a){
  boolean r=super.hurt(s,a);
  if(r&&s.getEntity() instanceof LivingEntity x){setTarget(x);drinkStop();graze(false);}
  return r;
 }
}