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
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape1 = new ModelRenderer(this, 0, 15);
		this.shape1.setRotationPoint(0.0F, 12.0F, 0.0F);
		this.shape1.addBox(-3.5F, -12.0F, -3.5F, 7, 24, 7, 0.0F);
		this.shape2 = new ModelRenderer(this, 0, 0);
		this.shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape2.addBox(-4.5F, -8.0F, -4.5F, 9, 4, 9, 0.0F);
		this.shape6 = new ModelRenderer(this, 160, 5);
		this.shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape6.addBox(-4.0F, 0.0F, -2.5F, 8, 3, 1, 0.0F);
		this.shape8 = new ModelRenderer(this, 106, 0);
		this.shape8.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape8.addBox(-3.0F, 0.0F, -4.0F, 6, 3, 8, 0.0F);
		this.shape3 = new ModelRenderer(this, 38, 0);
		this.shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape3.addBox(-4.5F, 4.0F, -4.5F, 9, 4, 9, 0.0F);
		this.shape4 = new ModelRenderer(this, 136, 0);
		this.shape4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape4.addBox(-4.0F, -3.0F, -0.5F, 8, 6, 3, 0.0F);
		this.shape5 = new ModelRenderer(this, 160, 0);
		this.shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape5.addBox(-4.0F, -3.0F, -2.5F, 8, 2, 1, 0.0F);
		this.shape7 = new ModelRenderer(this, 76, 0);
		this.shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape7.addBox(-3.0F, -3.0F, -4.0F, 6, 2, 8, 0.0F);
		this.shape1.addChild(this.shape2);
		this.shape1.addChild(this.shape6);
		this.shape1.addChild(this.shape8);
		this.shape1.addChild(this.shape3);
		this.shape1.addChild(this.shape4);
		this.shape1.addChild(this.shape5);
		this.shape1.addChild(this.shape7);
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
