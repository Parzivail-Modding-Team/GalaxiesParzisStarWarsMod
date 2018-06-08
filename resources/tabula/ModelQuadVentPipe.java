package com.parzivail.swg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelQuadVentPipe - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelQuadVentPipe extends ModelBase {
    public ModelRenderer pipe1;
    public ModelRenderer pipe1_1;
    public ModelRenderer pipe1_2;
    public ModelRenderer pipe1_3;
    public ModelRenderer gasket;
    public ModelRenderer top;
    public ModelRenderer bend;
    public ModelRenderer smallFlange;
    public ModelRenderer flange;
    public ModelRenderer gasket_1;
    public ModelRenderer top_1;
    public ModelRenderer bend_1;
    public ModelRenderer smallFlange_1;
    public ModelRenderer flange_1;
    public ModelRenderer gasket_2;
    public ModelRenderer top_2;
    public ModelRenderer bend_2;
    public ModelRenderer smallFlange_2;
    public ModelRenderer flange_2;
    public ModelRenderer gasket_3;
    public ModelRenderer top_3;
    public ModelRenderer bend_3;
    public ModelRenderer smallFlange_3;
    public ModelRenderer flange_3;

    public ModelQuadVentPipe() {
        this.textureWidth = 128;
        this.textureHeight = 96;
        this.smallFlange_3 = new ModelRenderer(this, 34, 0);
        this.smallFlange_3.setRotationPoint(4.0F, -0.5F, -3.5F);
        this.smallFlange_3.addBox(0.0F, 0.0F, 0.0F, 1, 7, 7, 0.0F);
        this.bend = new ModelRenderer(this, 0, 11);
        this.bend.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.bend.addBox(-3.0F, 0.0F, -3.0F, 8, 6, 6, 0.0F);
        this.pipe1_2 = new ModelRenderer(this, 0, 25);
        this.pipe1_2.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.pipe1_2.addBox(2.0F, 0.0F, -3.0F, 6, 18, 6, 0.0F);
        this.setRotateAngle(pipe1_2, 0.0F, 3.141592653589793F, 0.0F);
        this.gasket = new ModelRenderer(this, 0, 0);
        this.gasket.setRotationPoint(5.0F, -1.0F, 0.0F);
        this.gasket.addBox(-4.0F, 0.0F, -4.0F, 8, 1, 8, 0.0F);
        this.bend_3 = new ModelRenderer(this, 0, 11);
        this.bend_3.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.bend_3.addBox(-3.0F, 0.0F, -3.0F, 8, 6, 6, 0.0F);
        this.pipe1_3 = new ModelRenderer(this, 0, 25);
        this.pipe1_3.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.pipe1_3.addBox(2.0F, 0.0F, -3.0F, 6, 16, 6, 0.0F);
        this.setRotateAngle(pipe1_3, 0.0F, -1.5707963267948966F, 0.0F);
        this.smallFlange = new ModelRenderer(this, 34, 0);
        this.smallFlange.setRotationPoint(4.0F, -0.5F, -3.5F);
        this.smallFlange.addBox(0.0F, 0.0F, 0.0F, 1, 7, 7, 0.0F);
        this.flange_1 = new ModelRenderer(this, 0, 65);
        this.flange_1.setRotationPoint(1.0F, -0.5F, -0.5F);
        this.flange_1.addBox(0.0F, 0.0F, 0.0F, 1, 8, 8, 0.0F);
        this.smallFlange_2 = new ModelRenderer(this, 34, 0);
        this.smallFlange_2.setRotationPoint(4.0F, -0.5F, -3.5F);
        this.smallFlange_2.addBox(0.0F, 0.0F, 0.0F, 1, 7, 7, 0.0F);
        this.pipe1_1 = new ModelRenderer(this, 0, 25);
        this.pipe1_1.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.pipe1_1.addBox(2.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
        this.setRotateAngle(pipe1_1, 0.0F, 1.5707963267948966F, 0.0F);
        this.gasket_1 = new ModelRenderer(this, 0, 0);
        this.gasket_1.setRotationPoint(5.0F, -1.0F, 0.0F);
        this.gasket_1.addBox(-4.0F, 0.0F, -4.0F, 8, 1, 8, 0.0F);
        this.flange_3 = new ModelRenderer(this, 0, 65);
        this.flange_3.setRotationPoint(1.0F, -0.5F, -0.5F);
        this.flange_3.addBox(0.0F, 0.0F, 0.0F, 1, 8, 8, 0.0F);
        this.gasket_2 = new ModelRenderer(this, 0, 0);
        this.gasket_2.setRotationPoint(5.0F, -1.0F, 0.0F);
        this.gasket_2.addBox(-4.0F, 0.0F, -4.0F, 8, 1, 8, 0.0F);
        this.top_2 = new ModelRenderer(this, 0, 53);
        this.top_2.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.top_2.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6, 0.0F);
        this.bend_2 = new ModelRenderer(this, 0, 11);
        this.bend_2.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.bend_2.addBox(-3.0F, 0.0F, -3.0F, 8, 6, 6, 0.0F);
        this.pipe1 = new ModelRenderer(this, 0, 25);
        this.pipe1.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.pipe1.addBox(2.0F, 0.0F, -3.0F, 6, 20, 6, 0.0F);
        this.top_1 = new ModelRenderer(this, 0, 53);
        this.top_1.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.top_1.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6, 0.0F);
        this.top_3 = new ModelRenderer(this, 0, 53);
        this.top_3.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.top_3.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6, 0.0F);
        this.bend_1 = new ModelRenderer(this, 0, 11);
        this.bend_1.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.bend_1.addBox(-3.0F, 0.0F, -3.0F, 8, 6, 6, 0.0F);
        this.flange_2 = new ModelRenderer(this, 0, 65);
        this.flange_2.setRotationPoint(1.0F, -0.5F, -0.5F);
        this.flange_2.addBox(0.0F, 0.0F, 0.0F, 1, 8, 8, 0.0F);
        this.top = new ModelRenderer(this, 0, 53);
        this.top.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.top.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6, 0.0F);
        this.flange = new ModelRenderer(this, 0, 65);
        this.flange.setRotationPoint(1.0F, -0.5F, -0.5F);
        this.flange.addBox(0.0F, 0.0F, 0.0F, 1, 8, 8, 0.0F);
        this.smallFlange_1 = new ModelRenderer(this, 34, 0);
        this.smallFlange_1.setRotationPoint(4.0F, -0.5F, -3.5F);
        this.smallFlange_1.addBox(0.0F, 0.0F, 0.0F, 1, 7, 7, 0.0F);
        this.gasket_3 = new ModelRenderer(this, 0, 0);
        this.gasket_3.setRotationPoint(5.0F, -1.0F, 0.0F);
        this.gasket_3.addBox(-4.0F, 0.0F, -4.0F, 8, 1, 8, 0.0F);
        this.bend_3.addChild(this.smallFlange_3);
        this.top.addChild(this.bend);
        this.pipe1.addChild(this.gasket);
        this.top_3.addChild(this.bend_3);
        this.bend.addChild(this.smallFlange);
        this.smallFlange_1.addChild(this.flange_1);
        this.bend_2.addChild(this.smallFlange_2);
        this.pipe1_1.addChild(this.gasket_1);
        this.smallFlange_3.addChild(this.flange_3);
        this.pipe1_2.addChild(this.gasket_2);
        this.gasket_2.addChild(this.top_2);
        this.top_2.addChild(this.bend_2);
        this.gasket_1.addChild(this.top_1);
        this.gasket_3.addChild(this.top_3);
        this.top_1.addChild(this.bend_1);
        this.smallFlange_2.addChild(this.flange_2);
        this.gasket.addChild(this.top);
        this.smallFlange.addChild(this.flange);
        this.bend_1.addChild(this.smallFlange_1);
        this.pipe1_3.addChild(this.gasket_3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.pipe1_2.render(f5);
        this.pipe1_3.render(f5);
        this.pipe1_1.render(f5);
        this.pipe1.render(f5);
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
