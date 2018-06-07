package com.parzivail.swg.render.crate;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * HothCrate1 - Weaston
 * Created using Tabula 4.1.1
 */
public class ModelHothCrate2 extends ModelBase
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

	public ModelHothCrate2()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape1_2 = new ModelRenderer(this, 0, 46);
		this.shape1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_2.addBox(1.5F, -6.5F, -6.5F, 5, 5, 5, 0.0F);
		this.shape1_8 = new ModelRenderer(this, 0, 160);
		this.shape1_8.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_8.addBox(1.5F, -6.5F, 1.5F, 5, 5, 5, 0.0F);
		this.shape1_5 = new ModelRenderer(this, 0, 100);
		this.shape1_5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_5.addBox(-6.5F, -6.5F, 1.5F, 5, 5, 5, 0.0F);
		this.shape1_3 = new ModelRenderer(this, 0, 63);
		this.shape1_3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_3.addBox(1.5F, 1.5F, -6.5F, 5, 5, 5, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(0.0F, 17.5F, 0.0F);
		this.shape1.addBox(-6.0F, -6.0F, -6.0F, 12, 12, 12, 0.0F);
		this.shape1_6 = new ModelRenderer(this, 0, 118);
		this.shape1_6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_6.addBox(-6.5F, 1.5F, 1.5F, 5, 5, 5, 0.0F);
		this.shape1_1 = new ModelRenderer(this, 0, 30);
		this.shape1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_1.addBox(-6.5F, -6.5F, -6.5F, 5, 5, 5, 0.0F);
		this.shape1_4 = new ModelRenderer(this, 0, 80);
		this.shape1_4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_4.addBox(-6.5F, 1.5F, -6.5F, 5, 5, 5, 0.0F);
		this.shape1_7 = new ModelRenderer(this, 0, 138);
		this.shape1_7.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_7.addBox(1.5F, 1.5F, 1.5F, 5, 5, 5, 0.0F);
		this.shape1.addChild(this.shape1_2);
		this.shape1.addChild(this.shape1_8);
		this.shape1.addChild(this.shape1_5);
		this.shape1.addChild(this.shape1_3);
		this.shape1.addChild(this.shape1_6);
		this.shape1.addChild(this.shape1_1);
		this.shape1.addChild(this.shape1_4);
		this.shape1.addChild(this.shape1_7);
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
