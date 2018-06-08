package com.parzivail.swg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelPipeSmallBent - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelPipeSmallBent extends ModelBase {
    public ModelRenderer base;
    public ModelRenderer rubbleBase1;
    public ModelRenderer rubbleBase1_1;
    public ModelRenderer rubbleBase1_2;
    public ModelRenderer bend;
    public ModelRenderer flange;
    public ModelRenderer flange_1;
    public ModelRenderer rubble;
    public ModelRenderer rubble_1;
    public ModelRenderer rubble_2;
    public ModelRenderer rubble_3;
    public ModelRenderer rubble_4;
    public ModelRenderer rubble_5;
    public ModelRenderer rubble_6;
    public ModelRenderer rubble_7;
    public ModelRenderer rubble_8;
    public ModelRenderer rubble_9;
    public ModelRenderer rubble_10;
    public ModelRenderer rubble_11;

    public ModelPipeSmallBent() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.flange_1 = new ModelRenderer(this, 42, 0);
        this.flange_1.setRotationPoint(-0.5F, -0.5F, 1.0F);
        this.flange_1.addBox(0.0F, 0.0F, 0.0F, 5, 5, 1, 0.0F);
        this.rubble_10 = new ModelRenderer(this, 26, 9);
        this.rubble_10.setRotationPoint(-2.0F, 23.0F, -1.0F);
        this.rubble_10.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_10, -0.7285004297824331F, 0.091106186954104F, 1.0927506446736497F);
        this.rubble_8 = new ModelRenderer(this, 26, 9);
        this.rubble_8.setRotationPoint(-2.0F, 23.0F, -3.0F);
        this.rubble_8.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_8, 0.136659280431156F, 0.40980330836826856F, 1.0927506446736497F);
        this.flange = new ModelRenderer(this, 26, 0);
        this.flange.setRotationPoint(-1.0F, -1.0F, 0.0F);
        this.flange.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
        this.rubble_11 = new ModelRenderer(this, 26, 9);
        this.rubble_11.setRotationPoint(2.0F, 25.8F, -3.0F);
        this.rubble_11.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_11, 0.136659280431156F, -1.9123572614101867F, 1.0927506446736497F);
        this.rubbleBase1 = new ModelRenderer(this, 0, 0);
        this.rubbleBase1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rubbleBase1.addBox(0.0F, 14.0F, 0.0F, 1, 1, 1, 0.0F);
        this.setRotateAngle(rubbleBase1, 0.0F, 0.22759093446006054F, 0.0F);
        this.rubble_2 = new ModelRenderer(this, 26, 9);
        this.rubble_2.setRotationPoint(-2.0F, 23.0F, -1.0F);
        this.rubble_2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_2, -0.7285004297824331F, 0.091106186954104F, 1.0927506446736497F);
        this.rubbleBase1_1 = new ModelRenderer(this, 0, 0);
        this.rubbleBase1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rubbleBase1_1.addBox(0.0F, 14.0F, 0.0F, 1, 1, 1, 0.0F);
        this.setRotateAngle(rubbleBase1_1, 0.0F, -1.8212510744560826F, 0.0F);
        this.rubble_1 = new ModelRenderer(this, 26, 9);
        this.rubble_1.setRotationPoint(-1.0F, 23.0F, -2.0F);
        this.rubble_1.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_1, 0.0F, 0.8651597102135892F, 0.6373942428283291F);
        this.rubble_4 = new ModelRenderer(this, 26, 9);
        this.rubble_4.setRotationPoint(-2.0F, 23.0F, -3.0F);
        this.rubble_4.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_4, 0.136659280431156F, 0.40980330836826856F, 1.0927506446736497F);
        this.bend = new ModelRenderer(this, 0, 0);
        this.bend.setRotationPoint(0.0F, -4.0F, -4.0F);
        this.bend.addBox(0.0F, 0.0F, 0.0F, 4, 4, 8, 0.0F);
        this.rubble_3 = new ModelRenderer(this, 26, 9);
        this.rubble_3.setRotationPoint(2.0F, 25.8F, -3.0F);
        this.rubble_3.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_3, 0.136659280431156F, -1.9123572614101867F, 1.0927506446736497F);
        this.rubble_6 = new ModelRenderer(this, 26, 9);
        this.rubble_6.setRotationPoint(-2.0F, 23.0F, -1.0F);
        this.rubble_6.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_6, -0.7285004297824331F, 0.091106186954104F, 1.0927506446736497F);
        this.rubble_9 = new ModelRenderer(this, 26, 9);
        this.rubble_9.setRotationPoint(-1.0F, 23.0F, -2.0F);
        this.rubble_9.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_9, 0.0F, 0.8651597102135892F, 0.6373942428283291F);
        this.rubble_5 = new ModelRenderer(this, 26, 9);
        this.rubble_5.setRotationPoint(-1.0F, 23.0F, -2.0F);
        this.rubble_5.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_5, 0.0F, 0.8651597102135892F, 0.6373942428283291F);
        this.rubble = new ModelRenderer(this, 26, 9);
        this.rubble.setRotationPoint(-2.0F, 23.0F, -3.0F);
        this.rubble.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble, 0.136659280431156F, 0.40980330836826856F, 1.0927506446736497F);
        this.base = new ModelRenderer(this, 0, 14);
        this.base.setRotationPoint(-2.0F, 16.0F, -2.0F);
        this.base.addBox(0.0F, 0.0F, 0.0F, 4, 8, 4, 0.0F);
        this.rubbleBase1_2 = new ModelRenderer(this, 0, 0);
        this.rubbleBase1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rubbleBase1_2.addBox(0.0F, 14.0F, 0.0F, 1, 1, 1, 0.0F);
        this.setRotateAngle(rubbleBase1_2, 0.0F, 2.41309222380736F, 0.0F);
        this.rubble_7 = new ModelRenderer(this, 26, 9);
        this.rubble_7.setRotationPoint(2.0F, 25.8F, -3.0F);
        this.rubble_7.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(rubble_7, 0.136659280431156F, -1.9123572614101867F, 1.0927506446736497F);
        this.bend.addChild(this.flange_1);
        this.rubbleBase1_2.addChild(this.rubble_10);
        this.rubbleBase1_2.addChild(this.rubble_8);
        this.bend.addChild(this.flange);
        this.rubbleBase1_2.addChild(this.rubble_11);
        this.rubbleBase1.addChild(this.rubble_2);
        this.rubbleBase1.addChild(this.rubble_1);
        this.rubbleBase1_1.addChild(this.rubble_4);
        this.base.addChild(this.bend);
        this.rubbleBase1.addChild(this.rubble_3);
        this.rubbleBase1_1.addChild(this.rubble_6);
        this.rubbleBase1_2.addChild(this.rubble_9);
        this.rubbleBase1_1.addChild(this.rubble_5);
        this.rubbleBase1.addChild(this.rubble);
        this.rubbleBase1_1.addChild(this.rubble_7);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.rubbleBase1.render(f5);
        this.rubbleBase1_1.render(f5);
        this.base.render(f5);
        this.rubbleBase1_2.render(f5);
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
