package com.jurassicsurvival.client;

import com.jurassicsurvival.entity.TRexEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TRexModel extends HierarchicalModel<TRexEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation("jurassicsurvival", "trex"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tail;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public TRexModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.neck = root.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.tail = root.getChild("tail");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();

        r.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-8.0F, -7.0F, -12.0F, 16, 14, 24), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition neckPart = r.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 38)
                .addBox(-5.0F, -10.0F, -6.0F, 10, 12, 10), PartPose.offset(0.0F, 1.0F, -11.0F));

        PartDefinition headPart = neckPart.addOrReplaceChild("head", CubeListBuilder.create().texOffs(40, 38)
                .addBox(-5.5F, -6.0F, -14.0F, 11, 8, 14), PartPose.offset(0.0F, -8.0F, -2.0F));

        headPart.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(40, 60)
                .addBox(-4.5F, 0.0F, -13.0F, 9, 4, 13), PartPose.offset(0.0F, 2.0F, 0.0F));

        r.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(56, 0)
                .addBox(-4.0F, -4.0F, 0.0F, 8, 8, 26), PartPose.offset(0.0F, 3.0F, 12.0F));

        r.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 60)
                .addBox(-3.5F, 0.0F, -3.5F, 7, 18, 7), PartPose.offset(-6.0F, 6.0F, 2.0F));

        r.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 85)
                .addBox(-3.5F, 0.0F, -3.5F, 7, 18, 7), PartPose.offset(6.0F, 6.0F, 2.0F));

        r.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(28, 60)
                .addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3), PartPose.offset(-7.5F, 8.0F, -8.0F));

        r.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(28, 70)
                .addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3), PartPose.offset(7.5F, 8.0F, -8.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(TRexEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.neck.yRot = netHeadYaw * 0.5F * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * 0.5F * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        this.leftLeg.xRot = Mth.cos(limbSwing * 0.55F) * 0.9F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.55F + Mth.PI) * 0.9F * limbSwingAmount;

        this.leftArm.xRot = -0.35F + Mth.cos(ageInTicks * 0.1F) * 0.08F;
        this.rightArm.xRot = -0.35F - Mth.cos(ageInTicks * 0.1F) * 0.08F;

        this.tail.yRot = Mth.cos(limbSwing * 0.4F) * 0.25F * limbSwingAmount + Mth.cos(ageInTicks * 0.08F) * 0.06F;

        if (entity.isRoaring()) {
            this.neck.xRot = -0.25F;
            this.head.xRot = -0.35F;
            this.jaw.xRot = 0.55F;
        } else if (entity.getTarget() != null) {
            this.jaw.xRot = 0.22F + Mth.abs(Mth.sin(ageInTicks * 0.25F)) * 0.15F;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
