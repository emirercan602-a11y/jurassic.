package com.jurassicsurvival.client;

import com.jurassicsurvival.JurassicSurvival;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Veri tabanlı dinozor modeli tanımı (parça listesi + ölçek + doku). */
public final class DinoSpec {
    public static final class Part {
        final String name, parent;
        final int u, v, w, h, d;
        final float x, y, z, px, py, pz, rx, ry, rz;

        Part(String name, String parent, int u, int v, float x, float y, float z, int w, int h, int d,
             float px, float py, float pz, float rx, float ry, float rz) {
            this.name = name; this.parent = parent; this.u = u; this.v = v;
            this.x = x; this.y = y; this.z = z; this.w = w; this.h = h; this.d = d;
            this.px = px; this.py = py; this.pz = pz; this.rx = rx; this.ry = ry; this.rz = rz;
        }
    }

    private final String id;
    private final int texW, texH;
    private final float scale, shadow;
    private final List<Part> parts;

    public DinoSpec(String id, int texW, int texH, float scale, float shadow, List<Part> parts) {
        this.id = id; this.texW = texW; this.texH = texH; this.scale = scale; this.shadow = shadow; this.parts = parts;
    }

    public float scale() { return scale; }
    public float shadow() { return shadow; }

    public ModelLayerLocation layer() {
        return new ModelLayerLocation(new ResourceLocation(JurassicSurvival.MOD_ID, id), "main");
    }

    public ResourceLocation texture() {
        return new ResourceLocation(JurassicSurvival.MOD_ID, "textures/entity/" + id + ".png");
    }

    public LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Map<String, PartDefinition> defs = new HashMap<>();
        for (Part p : parts) {
            PartDefinition parent = p.parent == null ? root : defs.get(p.parent);
            PartDefinition def = parent.addOrReplaceChild(p.name,
                    CubeListBuilder.create().texOffs(p.u, p.v).addBox(p.x, p.y, p.z, p.w, p.h, p.d),
                    PartPose.offsetAndRotation(p.px, p.py, p.pz, p.rx, p.ry, p.rz));
            defs.put(p.name, def);
        }
        return LayerDefinition.create(mesh, texW, texH);
    }
}
