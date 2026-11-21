package com.jurassicsurvival.client;

import com.jurassicsurvival.entity.GrazingDino;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.NoSuchElementException;

/** DinoSpec'ten kurulan ortak model: yürüme, baş çevirme, otlama/su içme, çene, kanat/yüzgeç/kuyruk animasyonu. */
public class DinoModel<T extends LivingEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart body, neck, head, jaw, tail, tail2;
    private final ModelPart legL, legR, legFL, legFR, legBL, legBR, armL, armR, wingL, wingR;
    private final ModelPart flipFL, flipFR, flipBL, flipBR;

    public DinoModel(ModelPart root) {
        this.root = root;
        this.body = opt(root, "body");
        this.neck = opt(root, "neck", body);
        this.head = neck != null ? opt(neck, "head") : opt(root, "head");
        this.jaw = head != null ? opt(head, "jaw") : null;
        this.tail = opt(root, "tail", body);
        this.tail2 = tail != null ? opt(tail, "tail2") : null;
        this.legL = opt(root, "leg_l");
        this.legR = opt(root, "leg_r");
        this.legFL = opt(root, "leg_fl");
        this.legFR = opt(root, "leg_fr");
        this.legBL = opt(root, "leg_bl");
        this.legBR = opt(root, "leg_br");
        this.armL = opt(root, "arm_l");
        this.armR = opt(root, "arm_r");
        this.wingL = opt(root, "wing_l", body);
        this.wingR = opt(root, "wing_r", body);
        this.flipFL = opt(root, "flipper_fl", body);
        this.flipFR = opt(root, "flipper_fr", body);
        this.flipBL = opt(root, "flipper_bl", body);
        this.flipBR = opt(root, "flipper_br", body);
    }

    private static ModelPart opt(ModelPart parent, String name) {
        try {
            return parent.getChild(name);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private static ModelPart opt(ModelPart root, String name, ModelPart alt) {
        ModelPart p = opt(root, name);
        if (p == null && alt != null) p = opt(alt, name);
        return p;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float swing = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        if (legL != null) { legL.xRot += swing; legR.xRot -= swing; }
        if (legFL != null) { legFL.xRot += swing; legBR.xRot += swing; legFR.xRot -= swing; legBL.xRot -= swing; }
        if (armL != null) { armL.xRot -= swing * 0.4F; armR.xRot += swing * 0.4F; }

        if (neck != null) neck.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.4F;
        if (head != null) {
            head.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.6F;
            head.xRot += headPitch * Mth.DEG_TO_RAD;
        }
        if (entity instanceof GrazingDino g && neck != null && head != null) {
            if (g.isDrinking()) {
                neck.xRot += 0.9F;
                head.xRot += 0.6F;
            } else if (g.isGrazing()) {
                neck.xRot += 0.6F;
                head.xRot += 0.5F;
            }
        }

        if (jaw != null) {
            boolean hunting = entity instanceof Mob m && m.getTarget() != null;
            jaw.xRot += hunting ? 0.3F + Mth.abs(Mth.sin(ageInTicks * 0.3F)) * 0.2F : 0.04F;
        }

        float sway = Mth.cos(limbSwing * 0.4F) * 0.25F * limbSwingAmount + Mth.sin(ageInTicks * 0.06F) * 0.05F;
        boolean swimming = entity.isInWater() && flipFL != null;
        if (swimming) {
            sway = Mth.sin(ageInTicks * 0.3F) * 0.3F;
            if (tail2 != null) tail2.yRot += Mth.sin(ageInTicks * 0.3F - 1.0F) * 0.4F;
            float f = Mth.sin(ageInTicks * 0.25F) * 0.25F;
            flipFL.zRot += f; flipFR.zRot -= f;
            flipBL.zRot += f; flipBR.zRot -= f;
        }
        if (tail != null) tail.yRot += sway;

        if (wingL != null && wingR != null && flipFL == null) {
            if (!entity.onGround()) {
                float flap = Mth.sin(ageInTicks * 0.9F) * 0.9F;
                wingL.zRot += flap;
                wingR.zRot -= flap;
            } else {
                wingL.zRot += 1.1F;
                wingR.zRot -= 1.1F;
            }
        }
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
