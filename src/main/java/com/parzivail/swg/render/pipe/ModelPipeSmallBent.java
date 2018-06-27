package com.parzivail.swg.render.pipe;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelPipeSmallBent - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelPipeSmallBent extends ModelBase
{
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

	public ModelPipeSmallBent()
	{
		textureWidth = 64;
		textureHeight = 32;
		flange_1 = new ModelRenderer(this, 42, 0);
		flange_1.setRotationPoint(-0.5F, -0.5F, 1.0F);
		flange_1.addBox(0.0F, 0.0F, 0.0F, 5, 5, 1, 0.0F);
		rubble_10 = new ModelRenderer(this, 26, 9);
		rubble_10.setRotationPoint(-2.0F, 23.0F, -1.0F);
		rubble_10.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_10, -0.7285004297824331F, 0.091106186954104F, 1.0927506446736497F);
		rubble_8 = new ModelRenderer(this, 26, 9);
		rubble_8.setRotationPoint(-2.0F, 23.0F, -3.0F);
		rubble_8.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_8, 0.136659280431156F, 0.40980330836826856F, 1.0927506446736497F);
		flange = new ModelRenderer(this, 26, 0);
		flange.setRotationPoint(-1.0F, -1.0F, 0.0F);
		flange.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
		rubble_11 = new ModelRenderer(this, 26, 9);
		rubble_11.setRotationPoint(2.0F, 25.8F, -3.0F);
		rubble_11.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_11, 0.136659280431156F, -1.9123572614101867F, 1.0927506446736497F);
		rubbleBase1 = new ModelRenderer(this, 0, 0);
		rubbleBase1.setRotationPoint(0.0F, 0.0F, 0.0F);
		rubbleBase1.addBox(0.0F, 14.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(rubbleBase1, 0.0F, 0.22759093446006054F, 0.0F);
		rubble_2 = new ModelRenderer(this, 26, 9);
		rubble_2.setRotationPoint(-2.0F, 23.0F, -1.0F);
		rubble_2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_2, -0.7285004297824331F, 0.091106186954104F, 1.0927506446736497F);
		rubbleBase1_1 = new ModelRenderer(this, 0, 0);
		rubbleBase1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		rubbleBase1_1.addBox(0.0F, 14.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(rubbleBase1_1, 0.0F, -1.8212510744560826F, 0.0F);
		rubble_1 = new ModelRenderer(this, 26, 9);
		rubble_1.setRotationPoint(-1.0F, 23.0F, -2.0F);
		rubble_1.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_1, 0.0F, 0.8651597102135892F, 0.6373942428283291F);
		rubble_4 = new ModelRenderer(this, 26, 9);
		rubble_4.setRotationPoint(-2.0F, 23.0F, -3.0F);
		rubble_4.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_4, 0.136659280431156F, 0.40980330836826856F, 1.0927506446736497F);
		bend = new ModelRenderer(this, 0, 0);
		bend.setRotationPoint(0.0F, -4.0F, -4.0F);
		bend.addBox(0.0F, 0.0F, 0.0F, 4, 4, 8, 0.0F);
		rubble_3 = new ModelRenderer(this, 26, 9);
		rubble_3.setRotationPoint(2.0F, 25.8F, -3.0F);
		rubble_3.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_3, 0.136659280431156F, -1.9123572614101867F, 1.0927506446736497F);
		rubble_6 = new ModelRenderer(this, 26, 9);
		rubble_6.setRotationPoint(-2.0F, 23.0F, -1.0F);
		rubble_6.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_6, -0.7285004297824331F, 0.091106186954104F, 1.0927506446736497F);
		rubble_9 = new ModelRenderer(this, 26, 9);
		rubble_9.setRotationPoint(-1.0F, 23.0F, -2.0F);
		rubble_9.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_9, 0.0F, 0.8651597102135892F, 0.6373942428283291F);
		rubble_5 = new ModelRenderer(this, 26, 9);
		rubble_5.setRotationPoint(-1.0F, 23.0F, -2.0F);
		rubble_5.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_5, 0.0F, 0.8651597102135892F, 0.6373942428283291F);
		rubble = new ModelRenderer(this, 26, 9);
		rubble.setRotationPoint(-2.0F, 23.0F, -3.0F);
		rubble.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble, 0.136659280431156F, 0.40980330836826856F, 1.0927506446736497F);
		base = new ModelRenderer(this, 0, 14);
		base.setRotationPoint(-2.0F, 16.0F, -2.0F);
		base.addBox(0.0F, 0.0F, 0.0F, 4, 8, 4, 0.0F);
		rubbleBase1_2 = new ModelRenderer(this, 0, 0);
		rubbleBase1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		rubbleBase1_2.addBox(0.0F, 14.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(rubbleBase1_2, 0.0F, 2.41309222380736F, 0.0F);
		rubble_7 = new ModelRenderer(this, 26, 9);
		rubble_7.setRotationPoint(2.0F, 25.8F, -3.0F);
		rubble_7.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(rubble_7, 0.136659280431156F, -1.9123572614101867F, 1.0927506446736497F);
		bend.addChild(flange_1);
		rubbleBase1_2.addChild(rubble_10);
		rubbleBase1_2.addChild(rubble_8);
		bend.addChild(flange);
		rubbleBase1_2.addChild(rubble_11);
		rubbleBase1.addChild(rubble_2);
		rubbleBase1.addChild(rubble_1);
		rubbleBase1_1.addChild(rubble_4);
		base.addChild(bend);
		rubbleBase1.addChild(rubble_3);
		rubbleBase1_1.addChild(rubble_6);
		rubbleBase1_2.addChild(rubble_9);
		rubbleBase1_1.addChild(rubble_5);
		rubbleBase1.addChild(rubble);
		rubbleBase1_1.addChild(rubble_7);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		rubbleBase1.render(f5);
		rubbleBase1_1.render(f5);
		base.render(f5);
		rubbleBase1_2.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
