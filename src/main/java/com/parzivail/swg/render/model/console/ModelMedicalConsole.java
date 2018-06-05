package com.parzivail.swg.render.model.console;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * MedicalPanel - Weston
 * Created using Tabula 4.1.1
 */
public class ModelMedicalConsole extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer shape1_1;
	public ModelRenderer shape1_2;
	public ModelRenderer shape1_3;
	public ModelRenderer shape1_4;
	public ModelRenderer shape1_5;
	public ModelRenderer shape1_6;
	public ModelRenderer shape1_7;
	public ModelRenderer shape1_8;
	public ModelRenderer shape1_9;
	public ModelRenderer shape1_10;
	public ModelRenderer shape1_11;
	public ModelRenderer shape1_12;
	public ModelRenderer shape1_13;

	public ModelMedicalConsole()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape1_11 = new ModelRenderer(this, 108, 0);
		this.shape1_11.setRotationPoint(0.0F, -0.2F, 0.4F);
		this.shape1_11.addBox(-5.0F, -17.8F, 2.0F, 6, 1, 5, 0.0F);
		this.setRotateAngle(shape1_11, 0.3141592653589793F, 0.0F, 0.0F);
		this.shape1_2 = new ModelRenderer(this, 0, 37);
		this.shape1_2.setRotationPoint(0.0F, -0.2F, 0.4F);
		this.shape1_2.addBox(-6.0F, -17.5F, 0.0F, 12, 3, 9, 0.0F);
		this.setRotateAngle(shape1_2, 0.3141592653589793F, 0.0F, 0.0F);
		this.shape1_6 = new ModelRenderer(this, 0, 104);
		this.shape1_6.setRotationPoint(0.0F, -0.2F, 0.4F);
		this.shape1_6.addBox(2.0F, -18.5F, 7.0F, 1, 2, 1, 0.0F);
		this.setRotateAngle(shape1_6, 0.3141592653589793F, 0.0F, 0.0F);
		this.shape1_3 = new ModelRenderer(this, 0, 58);
		this.shape1_3.setRotationPoint(0.0F, -0.2F, 0.4F);
		this.shape1_3.addBox(-8.0F, -17.0F, 2.0F, 3, 2, 5, 0.0F);
		this.setRotateAngle(shape1_3, 0.3141592653589793F, 0.0F, 0.0F);
		this.shape1_9 = new ModelRenderer(this, 74, 0);
		this.shape1_9.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_9.addBox(-3.5F, -6.0F, -0.5F, 2, 3, 1, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(0.0F, 23.5F, 0.0F);
		this.shape1.addBox(-5.0F, -0.5F, -5.0F, 10, 1, 10, 0.0F);
		this.shape1_1 = new ModelRenderer(this, 0, 16);
		this.shape1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_1.addBox(-2.5F, -16.5F, -2.5F, 5, 6, 5, 0.0F);
		this.shape1_10 = new ModelRenderer(this, 92, 0);
		this.shape1_10.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_10.addBox(-5.0F, -6.7F, -1.0F, 2, 5, 2, 0.0F);
		this.shape1_12 = new ModelRenderer(this, 138, 0);
		this.shape1_12.setRotationPoint(0.0F, -0.2F, 0.4F);
		this.shape1_12.addBox(2.5F, -17.8F, 2.0F, 2, 1, 4, 0.0F);
		this.setRotateAngle(shape1_12, 0.3141592653589793F, 0.0F, 0.0F);
		this.shape1_5 = new ModelRenderer(this, 0, 94);
		this.shape1_5.setRotationPoint(0.0F, -0.2F, 0.4F);
		this.shape1_5.addBox(4.0F, -18.5F, 7.0F, 1, 2, 1, 0.0F);
		this.setRotateAngle(shape1_5, 0.3141592653589793F, 0.0F, 0.0F);
		this.shape1_8 = new ModelRenderer(this, 47, 0);
		this.shape1_8.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_8.addBox(-2.0F, -10.5F, -2.0F, 4, 4, 4, 0.0F);
		this.shape1_7 = new ModelRenderer(this, 0, 118);
		this.shape1_7.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_7.addBox(-2.5F, -6.5F, -2.5F, 5, 6, 5, 0.0F);
		this.shape1_4 = new ModelRenderer(this, 0, 78);
		this.shape1_4.setRotationPoint(0.0F, -0.2F, 0.4F);
		this.shape1_4.addBox(5.0F, -17.0F, 2.0F, 3, 2, 5, 0.0F);
		this.setRotateAngle(shape1_4, 0.3141592653589793F, 0.0F, 0.0F);
		this.shape1_13 = new ModelRenderer(this, 70, 24);
		this.shape1_13.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_13.addBox(-4.5F, -7.7F, -0.5F, 1, 2, 1, 0.0F);
		this.shape1.addChild(this.shape1_11);
		this.shape1.addChild(this.shape1_2);
		this.shape1.addChild(this.shape1_6);
		this.shape1.addChild(this.shape1_3);
		this.shape1.addChild(this.shape1_9);
		this.shape1.addChild(this.shape1_1);
		this.shape1.addChild(this.shape1_10);
		this.shape1.addChild(this.shape1_12);
		this.shape1.addChild(this.shape1_5);
		this.shape1.addChild(this.shape1_8);
		this.shape1.addChild(this.shape1_7);
		this.shape1.addChild(this.shape1_4);
		this.shape1.addChild(this.shape1_13);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.shape1.render(f5);
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
