package com.parzivail.swg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelAngledWallLamp - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelAngledWallLamp extends ModelBase {
    public ModelRenderer wallPlate;
    public ModelRenderer wallAdapter;
    public ModelRenderer lamp;
    public ModelRenderer grill;
    public ModelRenderer grill_1;
    public ModelRenderer grill_2;
    public ModelRenderer grill_3;

    public ModelAngledWallLamp() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.lamp = new ModelRenderer(this, 0, 11);
        this.lamp.setRotationPoint(4.5F, -1.5F, 0.0F);
        this.lamp.addBox(0.0F, 0.0F, 1.0F, 5, 5, 7, 0.0F);
        this.setRotateAngle(lamp, -0.7853981633974483F, 3.141592653589793F, 0.0F);
        this.grill_2 = new ModelRenderer(this, 0, 25);
        this.grill_2.setRotationPoint(3.0F, -0.5F, 0.4F);
        this.grill_2.addBox(0.0F, 0.0F, 0.0F, 1, 6, 8, 0.0F);
        this.wallAdapter = new ModelRenderer(this, 42, 11);
        this.wallAdapter.setRotationPoint(1.0F, 1.0F, -3.0F);
        this.wallAdapter.addBox(0.0F, 0.0F, 0.0F, 4, 4, 3, 0.0F);
        this.grill = new ModelRenderer(this, 0, 0);
        this.grill.setRotationPoint(-0.5F, 1.0F, 0.5F);
        this.grill.addBox(0.0F, 0.0F, 0.0F, 6, 1, 8, 0.0F);
        this.wallPlate = new ModelRenderer(this, 26, 11);
        this.wallPlate.setRotationPoint(-3.0F, 10.0F, 7.0F);
        this.wallPlate.addBox(0.0F, 0.0F, 0.0F, 6, 9, 1, 0.0F);
        this.grill_1 = new ModelRenderer(this, 30, 0);
        this.grill_1.setRotationPoint(-0.5F, 3.0F, 0.5F);
        this.grill_1.addBox(0.0F, 0.0F, 0.0F, 6, 1, 8, 0.0F);
        this.grill_3 = new ModelRenderer(this, 0, 41);
        this.grill_3.setRotationPoint(1.0F, -0.5F, 0.4F);
        this.grill_3.addBox(0.0F, 0.0F, 0.0F, 1, 6, 8, 0.0F);
        this.wallAdapter.addChild(this.lamp);
        this.lamp.addChild(this.grill_2);
        this.wallPlate.addChild(this.wallAdapter);
        this.lamp.addChild(this.grill);
        this.lamp.addChild(this.grill_1);
        this.lamp.addChild(this.grill_3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.wallPlate.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
