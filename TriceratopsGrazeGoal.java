package com.jurassicsurvival.entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import java.util.*;
public class TriceratopsGrazeGoal extends Goal{
 private final TriceratopsEntity d;private int t;private final Random r=new Random();
 public TriceratopsGrazeGoal(TriceratopsEntity d){this.d=d;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
 public boolean canUse(){if(d.isBaby()||d.isDrinking()||d.getTarget()!=null||r.nextInt(100)>6)return false;return d.level().getBlockState(d.blockPosition().below()).is(Blocks.GRASS_BLOCK);}
 public boolean canContinueToUse(){return t<80&&d.getTarget()==null;}
 public void start(){t=0;d.graze(true);} public void tick(){d.getNavigation().stop();t++;} public void stop(){d.graze(false);}
}