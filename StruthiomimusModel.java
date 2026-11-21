package com.jurassicsurvival.client;

import com.jurassicsurvival.entity.StruthiomimusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class StruthiomimusModel extends HierarchicalModel<StruthiomimusEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation("jurassicsurvival","struthiomimus"), "main");

    private final ModelPart root, body, neck, head, beak, leftLeg, rightLeg, leftArm, rightArm, tail;

    public StruthiomimusModel(ModelPart root) {
        this.root=root;
        body=root.getChild("body"); neck=root.getChild("neck"); head=root.getChild("head");
        beak=head.getChild("beak"); leftLeg=root.getChild("left_leg"); rightLeg=root.getChild("right_leg");
        leftArm=root.getChild("left_arm"); rightArm=root.getChild("right_arm"); tail=root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh=new MeshDefinition();
        PartDefinition r=mesh.getRoot();
        r.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0,0)
                .addBox(-4,-3,-6,8,7,12), PartPose.offset(0,13,0));
        r.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0,20)
                .addBox(-2,-8,-2,4,10,4), PartPose.offset(0,7,-4));
        PartDefinition h=r.addOrReplaceChild("head", CubeListBuilder.create().texOffs(16,20)
                .addBox(-2,-3,-4,4,5,7), PartPose.offset(0,0,-2));
        h.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(38,20)
                .addBox(-1.5F,-1.5F,-4,3,3,4), PartPose.offset(0,0,-4));
        r.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0,32)
                .addBox(-2,-2,0,4,4,13), PartPose.offset(0,12,6));
        r.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(30,32)
                .addBox(-1.5F,0,-1.5F,3,11,3), PartPose.offset(-2,16,0));
        r.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(42,32)
                .addBox(-1.5F,0,-1.5F,3,11,3), PartPose.offset(2,16,0));
        r.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(52,0)
                .addBox(-1,-1,-1,2,7,2), PartPose.offset(-3,10,-2));
        r.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(52,10)
                .addBox(-1,-1,-1,2,7,2), PartPose.offset(3,10,-2));
        return LayerDefinition.create(mesh,64,64);
    }

    @Override public void setupAnim(StruthiomimusEntity e,float limbSwing,float limbSwingAmount,
                                    float ageInTicks,float netHeadYaw,float headPitch) {
        root().getAllParts().forEach(p -> p.resetPose());
        head.yRot=netHeadYaw*Mth.DEG_TO_RAD;
        head.xRot=headPitch*Mth.DEG_TO_RAD;
        leftLeg.xRot=Mth.cos(limbSwing*0.75F)*1.1F*limbSwingAmount;
        rightLeg.xRot=Mth.cos(limbSwing*0.75F+Mth.PI)*1.1F*limbSwingAmount;
        leftArm.xRot=-leftLeg.xRot*0.35F;
        rightArm.xRot=-rightLeg.xRot*0.35F;
        tail.xRot=0.12F;
        if(e.isGrazing()) head.xRot=0.75F;
        if(e.isDrinking()) head.xRot=1.0F;
    }

    @Override public ModelPart root(){ return root; }
}
