package com.jurassicsurvival.entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import java.util.*;
public class AnkylosaurusDrinkGoal extends Goal{
 private int guard;
 private final AnkylosaurusEntity d;private BlockPos p;private int t;
 public AnkylosaurusDrinkGoal(AnkylosaurusEntity d){this.d=d;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
 public boolean canUse(){if(d.isBaby()||d.getTarget()!=null)return false;if(d.getRandom().nextInt(100)!=0)return false;p=find();return p!=null;}
 public boolean canContinueToUse(){return p!=null&&d.getTarget()==null&&t < 100 && ++guard < 400;}
 public void start(){ guard=0;t=0;d.getNavigation().moveTo(p.getX()+.5,p.getY(),p.getZ()+.5,1);}
 public void tick(){if(p==null)return;if(d.distanceToSqr(p.getX()+.5,p.getY(),p.getZ()+.5)<5){d.drinkStart();d.getNavigation().stop();t++;}else d.getNavigation().moveTo(p.getX()+.5,p.getY(),p.getZ()+.5,1);}
 public void stop(){d.drinkStop();p=null;t=0;}
 private BlockPos find(){BlockPos c=d.blockPosition();for(int x=-14;x<=14;x+=2)for(int z=-14;z<=14;z+=2)for(int y=-3;y<=3;y++){BlockPos q=c.offset(x,y,z);if(d.level().getBlockState(q).is(Blocks.WATER)&&d.level().getBlockState(q.above()).isAir())return q;}return null;}
}