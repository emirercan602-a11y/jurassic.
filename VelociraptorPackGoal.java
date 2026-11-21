package com.jurassicsurvival.entity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
public class VelociraptorPackGoal extends Goal{
 private final VelociraptorEntity r;
 public VelociraptorPackGoal(VelociraptorEntity r){this.r=r;setFlags(EnumSet.of(Flag.MOVE));}
 public boolean canUse(){return r.getTarget()!=null&&r.getTarget().isAlive()&&r.level().getEntitiesOfClass(VelociraptorEntity.class,r.getBoundingBox().inflate(14),e->e!=r&&e.isAlive()).size()>0;}
 public void tick(){if(r.getTarget()!=null)r.getNavigation().moveTo(r.getTarget(),1.4);}
}