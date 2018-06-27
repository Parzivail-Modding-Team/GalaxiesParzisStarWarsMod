package com.parzivail.swg.render.crate;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Crate Villa - Weaston
 * Created using P-Tabula 4.1.1
 */
public class ModelCrateVilla extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;
	public ModelRenderer shape6;
	public ModelRenderer shape7;
	public ModelRenderer shape8;

	public ModelCrateVilla()
	{
		textureWidth = 512;
		textureHeight = 512;
		shape1 = new ModelRenderer(this, 0, 15);
		shape1.setRotationPoint(0.0F, 12.0F, 0.0F);
		shape1.addBox(-3.5F, -12.0F, -3.5F, 7, 24, 7, 0.0F);
		shape2 = new ModelRenderer(this, 0, 0);
		shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape2.addBox(-4.5F, -8.0F, -4.5F, 9, 4, 9, 0.0F);
		shape6 = new ModelRenderer(this, 160, 5);
		shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape6.addBox(-4.0F, 0.0F, -2.5F, 8, 3, 1, 0.0F);
		shape8 = new ModelRenderer(this, 106, 0);
		shape8.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape8.addBox(-3.0F, 0.0F, -4.0F, 6, 3, 8, 0.0F);
		shape3 = new ModelRenderer(this, 38, 0);
		shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape3.addBox(-4.5F, 4.0F, -4.5F, 9, 4, 9, 0.0F);
		shape4 = new ModelRenderer(this, 136, 0);
		shape4.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape4.addBox(-4.0F, -3.0F, -0.5F, 8, 6, 3, 0.0F);
		shape5 = new ModelRenderer(this, 160, 0);
		shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape5.addBox(-4.0F, -3.0F, -2.5F, 8, 2, 1, 0.0F);
		shape7 = new ModelRenderer(this, 76, 0);
		shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape7.addBox(-3.0F, -3.0F, -4.0F, 6, 2, 8, 0.0F);
		shape1.addChild(shape2);
		shape1.addChild(shape6);
		shape1.addChild(shape8);
		shape1.addChild(shape3);
		shape1.addChild(shape4);
		shape1.addChild(shape5);
		shape1.addChild(shape7);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		shape1.render(f5);
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
