package com.jurassicsurvival.client;

import com.jurassicsurvival.entity.SpinosaurusEntity;
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

public class SpinosaurusModel extends HierarchicalModel<SpinosaurusEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation("jurassicsurvival", "spinosaurus"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart sail;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tail;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public SpinosaurusModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.sail = this.body.getChild("sail");
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

        // Long muscular body
        PartDefinition bodyPart = r.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-7.5F, -6.5F, -14.0F, 15, 13, 28), PartPose.offset(0.0F, 7.0F, 0.0F));

        // Iconic large neural-spine dorsal sail
        bodyPart.addOrReplaceChild("sail", CubeListBuilder.create().texOffs(58, 0)
                .addBox(-1.0F, -15.0F, -11.0F, 2, 15, 22), PartPose.offset(0.0F, -6.5F, 0.0F));

        // Long S-curved neck
        PartDefinition neckPart = r.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 41)
                .addBox(-4.5F, -9.0F, -8.0F, 9, 10, 11), PartPose.offset(0.0F, 3.0F, -13.0F));

        // Elongated crocodilian skull + nasal crest
        PartDefinition headPart = neckPart.addOrReplaceChild("head", CubeListBuilder.create().texOffs(40, 41)
                .addBox(-4.5F, -5.0F, -18.0F, 9, 6, 18)
                .texOffs(40, 65).addBox(-1.5F, -7.0F, -13.0F, 3, 2, 6), PartPose.offset(0.0F, -6.0F, -4.0F));

        // Long lower jaw
        headPart.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(40, 74)
                .addBox(-3.5F, 0.0F, -17.0F, 7, 3, 17), PartPose.offset(0.0F, 1.0F, 0.0F));

        // Paddle-like swimming tail
        r.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(62, 37)
                .addBox(-3.5F, -4.5F, 0.0F, 7, 9, 28), PartPose.offset(0.0F, 5.0F, 14.0F));

        // Hind legs
        r.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 62)
                .addBox(-3.0F, 0.0F, -3.5F, 6, 17, 7), PartPose.offset(-5.5F, 7.0F, 3.0F));

        r.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 86)
                .addBox(-3.0F, 0.0F, -3.5F, 6, 17, 7), PartPose.offset(5.5F, 7.0F, 3.0F));

        // Strong clawed forelimbs (larger than T-Rex)
        r.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(26, 62)
                .addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4), PartPose.offset(-7.5F, 9.0F, -10.0F));

        r.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(26, 76)
                .addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4), PartPose.offset(7.5F, 9.0F, -10.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(SpinosaurusEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.neck.yRot = netHeadYaw * 0.5F * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * 0.5F * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        if (entity.isInWater()) {
            // Streamlined fast swimming posture & strong tail propulsion
            this.body.xRot = 0.08F;
            this.neck.xRot = -0.05F;
            this.leftLeg.xRot = 0.65F + Mth.cos(ageInTicks * 0.45F) * 0.35F;
            this.rightLeg.xRot = 0.65F + Mth.cos(ageInTicks * 0.45F + Mth.PI) * 0.35F;
            this.leftArm.xRot = 0.45F;
            this.rightArm.xRot = 0.45F;
            this.tail.yRot = Mth.cos(ageInTicks * 0.55F) * 0.45F;
        } else {
            this.leftLeg.xRot = Mth.cos(limbSwing * 0.55F) * 0.9F * limbSwingAmount;
            this.rightLeg.xRot = Mth.cos(limbSwing * 0.55F + Mth.PI) * 0.9F * limbSwingAmount;
            this.leftArm.xRot = -0.25F + Mth.cos(limbSwing * 0.55F + Mth.PI) * 0.3F * limbSwingAmount;
            this.rightArm.xRot = -0.25F + Mth.cos(limbSwing * 0.55F) * 0.3F * limbSwingAmount;
            this.tail.yRot = Mth.cos(limbSwing * 0.4F) * 0.28F * limbSwingAmount + Mth.cos(ageInTicks * 0.08F) * 0.07F;
        }

        if (entity.isRoaring()) {
            this.neck.xRot = -0.28F;
            this.head.xRot = -0.35F;
            this.jaw.xRot = 0.55F;
        } else if (entity.getTarget() != null) {
            this.jaw.xRot = 0.24F + Mth.abs(Mth.sin(ageInTicks * 0.25F)) * 0.16F;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
