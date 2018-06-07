package com.parzivail.swg.render.crate;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * CrateMass1 - Weaston
 * Created using Tabula 4.1.1
 */
public class ModelCrateMass1 extends ModelBase
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

	public ModelCrateMass1()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape1_5 = new ModelRenderer(this, 79, 0);
		this.shape1_5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_5.addBox(-7.0F, 2.0F, -7.0F, 14, 12, 14, 0.0F);
		this.shape1_8 = new ModelRenderer(this, 63, 44);
		this.shape1_8.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_8.addBox(4.0F, 3.0F, -8.0F, 2, 3, 16, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(0.0F, 10.0F, 0.0F);
		this.shape1.addBox(-6.0F, -14.0F, -6.0F, 12, 28, 12, 0.0F);
		this.shape1_7 = new ModelRenderer(this, 211, 0);
		this.shape1_7.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_7.addBox(1.0F, 3.0F, -8.0F, 2, 3, 16, 0.0F);
		this.shape1_10 = new ModelRenderer(this, 33, 78);
		this.shape1_10.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_10.addBox(1.0F, 10.0F, -8.0F, 5, 3, 16, 0.0F);
		this.shape1_11 = new ModelRenderer(this, 94, 88);
		this.shape1_11.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_11.addBox(-8.0F, 4.0F, -6.0F, 16, 8, 12, 0.0F);
		this.shape1_1 = new ModelRenderer(this, 0, 153);
		this.shape1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_1.addBox(-5.0F, -13.0F, -7.0F, 10, 14, 1, 0.0F);
		this.shape1_4 = new ModelRenderer(this, 0, 49);
		this.shape1_4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_4.addBox(6.0F, -13.0F, -5.0F, 1, 14, 10, 0.0F);
		this.shape1_9 = new ModelRenderer(this, 120, 45);
		this.shape1_9.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_9.addBox(1.0F, 7.0F, -8.0F, 5, 2, 16, 0.0F);
		this.shape1_2 = new ModelRenderer(this, 0, 125);
		this.shape1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_2.addBox(-5.0F, -13.0F, 6.0F, 10, 14, 1, 0.0F);
		this.shape1_3 = new ModelRenderer(this, 0, 88);
		this.shape1_3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_3.addBox(-7.0F, -13.0F, -5.0F, 1, 14, 10, 0.0F);
		this.shape1_6 = new ModelRenderer(this, 152, 0);
		this.shape1_6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_6.addBox(-6.0F, 3.0F, -8.0F, 6, 10, 16, 0.0F);
		this.shape1.addChild(this.shape1_5);
		this.shape1.addChild(this.shape1_8);
		this.shape1.addChild(this.shape1_7);
		this.shape1.addChild(this.shape1_10);
		this.shape1.addChild(this.shape1_11);
		this.shape1.addChild(this.shape1_1);
		this.shape1.addChild(this.shape1_4);
		this.shape1.addChild(this.shape1_9);
		this.shape1.addChild(this.shape1_2);
		this.shape1.addChild(this.shape1_3);
		this.shape1.addChild(this.shape1_6);
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
