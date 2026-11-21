package com.jurassicsurvival.entity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.*;
public class TriceratopsHerdGoal extends Goal{
 private final TriceratopsEntity d;private TriceratopsEntity leader;
 public TriceratopsHerdGoal(TriceratopsEntity d){this.d=d;setFlags(EnumSet.of(Flag.MOVE));}
 public boolean canUse(){if(d.isBaby()||d.isDrinking()||d.getTarget()!=null)return false;
  leader=d.level().getEntitiesOfClass(TriceratopsEntity.class,d.getBoundingBox().inflate(14),e->e!=d&&e.isAlive()&&!e.isBaby()).stream().min(Comparator.comparingInt(net.minecraft.world.entity.Entity::getId)).orElse(null);
  return leader!=null&&d.distanceToSqr(leader)>25;}
 public boolean canContinueToUse(){return leader!=null&&leader.isAlive()&&d.getTarget()==null&&d.distanceToSqr(leader)>16;}
 public void start(){if(leader!=null)d.getNavigation().moveTo(leader,.95);}
 public void tick(){if(leader!=null)d.getNavigation().moveTo(leader,.95);}
}