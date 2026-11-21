package com.jurassicsurvival.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.*;
public class StruthiomimusHerdGoal extends Goal{
 private final StruthiomimusEntity d; private StruthiomimusEntity leader;
 public StruthiomimusHerdGoal(StruthiomimusEntity d){this.d=d;setFlags(EnumSet.of(Flag.MOVE));}
 public boolean canUse(){if(d.isBaby()||d.isDrinking()||d.getTarget()!=null)return false;leader=find();return leader!=null&&d.distanceToSqr(leader)>25;}
 public boolean canContinueToUse(){return leader!=null&&leader.isAlive()&&d.getTarget()==null&&d.distanceToSqr(leader)>16;}
 public void start(){if(leader!=null)d.getNavigation().moveTo(leader,1.05);}
 public void tick(){if(leader!=null)d.getNavigation().moveTo(leader,1.05);}
 private StruthiomimusEntity find(){var a=d.level().getEntitiesOfClass(StruthiomimusEntity.class,d.getBoundingBox().inflate(14),e->e!=d&&e.isAlive());return a.stream().min(Comparator.comparingInt(Entity::getId)).orElse(null);}
}