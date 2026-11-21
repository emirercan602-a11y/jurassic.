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

public class TriceratopsEntity extends Animal implements GrazingDino {
 private boolean grazing,drinking; private int hornCooldown;
 public TriceratopsEntity(EntityType<? extends TriceratopsEntity> t,Level l){super(t,l);}
 public static AttributeSupplier.Builder createAttributes(){return Mob.createMobAttributes()
  .add(Attributes.MAX_HEALTH,40).add(Attributes.MOVEMENT_SPEED,.22)
  .add(Attributes.ATTACK_DAMAGE,10).add(Attributes.FOLLOW_RANGE,24)
  .add(Attributes.KNOCKBACK_RESISTANCE,.30);}
 @Override protected void registerGoals(){
  targetSelector.addGoal(1,new HurtByTargetGoal(this));
  goalSelector.addGoal(1,new MeleeAttackGoal(this,1.05,true));
  goalSelector.addGoal(2,new PanicGoal(this,1.2));
  goalSelector.addGoal(3,new BreedGoal(this,.85));
  goalSelector.addGoal(4,new TemptGoal(this,.9,Ingredient.of(Items.WHEAT),false));
  goalSelector.addGoal(5,new TriceratopsDrinkGoal(this));
  goalSelector.addGoal(6,new TriceratopsGrazeGoal(this));
  goalSelector.addGoal(7,new TriceratopsHerdGoal(this));
  goalSelector.addGoal(8,new WaterAvoidingRandomStrollGoal(this,.85));
  goalSelector.addGoal(9,new LookAtPlayerGoal(this,Player.class,8));
  goalSelector.addGoal(10,new RandomLookAroundGoal(this));
 }
 public boolean isFood(ItemStack s){return s.is(Items.WHEAT);}
 @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel l,AgeableMob o){return ModEntities.TRICERATOPS.get().create(l);}
 @Override public boolean canMate(Animal o){return o instanceof TriceratopsEntity&&super.canMate(o);}
 public boolean isGrazing(){return grazing;} public boolean isDrinking(){return drinking;}
 public void graze(boolean v){grazing=v;if(v)drinking=false;}
 public void drinkStart(){drinking=true;grazing=false;getNavigation().stop();}
 public void drinkStop(){drinking=false;}
 @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource s,float amount){
  if(s.getEntity()!=null){
   double dx=s.getEntity().getX()-getX(), dz=s.getEntity().getZ()-getZ();
   double yaw=Math.toRadians(getYRot());
   double fx=-Math.sin(yaw), fz=Math.cos(yaw);
   double len=Math.sqrt(dx*dx+dz*dz);
   if(len>0){
    double dot=(dx/len)*fx+(dz/len)*fz;
    if(dot>0.35) amount*=0.75f;
   }
  }
  boolean r=super.hurt(s,amount);
  if(r&&s.getEntity() instanceof LivingEntity x){setTarget(x);drinkStop();graze(false);}
  return r;
 }
 @Override public boolean doHurtTarget(Entity target){
  if(hornCooldown>0)return false;
  hornCooldown=35;
  boolean hit=target.hurt(damageSources().mobAttack(this),10.0f);
  if(hit){
   double dx=target.getX()-getX(),dz=target.getZ()-getZ(),len=Math.sqrt(dx*dx+dz*dz);
   if(len>.001)target.push(dx/len*1.0,.25,dz/len*1.0);
  }
  return hit;
 }
 @Override public void aiStep(){super.aiStep();if(hornCooldown>0)hornCooldown--;}
}