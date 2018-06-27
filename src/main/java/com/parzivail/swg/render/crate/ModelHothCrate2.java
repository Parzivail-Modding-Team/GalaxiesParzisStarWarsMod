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
		textureWidth = 512;
		textureHeight = 512;
		shape1_2 = new ModelRenderer(this, 0, 46);
		shape1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_2.addBox(1.5F, -6.5F, -6.5F, 5, 5, 5, 0.0F);
		shape1_8 = new ModelRenderer(this, 0, 160);
		shape1_8.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_8.addBox(1.5F, -6.5F, 1.5F, 5, 5, 5, 0.0F);
		shape1_5 = new ModelRenderer(this, 0, 100);
		shape1_5.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_5.addBox(-6.5F, -6.5F, 1.5F, 5, 5, 5, 0.0F);
		shape1_3 = new ModelRenderer(this, 0, 63);
		shape1_3.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_3.addBox(1.5F, 1.5F, -6.5F, 5, 5, 5, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(0.0F, 17.5F, 0.0F);
		shape1.addBox(-6.0F, -6.0F, -6.0F, 12, 12, 12, 0.0F);
		shape1_6 = new ModelRenderer(this, 0, 118);
		shape1_6.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_6.addBox(-6.5F, 1.5F, 1.5F, 5, 5, 5, 0.0F);
		shape1_1 = new ModelRenderer(this, 0, 30);
		shape1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_1.addBox(-6.5F, -6.5F, -6.5F, 5, 5, 5, 0.0F);
		shape1_4 = new ModelRenderer(this, 0, 80);
		shape1_4.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_4.addBox(-6.5F, 1.5F, -6.5F, 5, 5, 5, 0.0F);
		shape1_7 = new ModelRenderer(this, 0, 138);
		shape1_7.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_7.addBox(1.5F, 1.5F, 1.5F, 5, 5, 5, 0.0F);
		shape1.addChild(shape1_2);
		shape1.addChild(shape1_8);
		shape1.addChild(shape1_5);
		shape1.addChild(shape1_3);
		shape1.addChild(shape1_6);
		shape1.addChild(shape1_1);
		shape1.addChild(shape1_4);
		shape1.addChild(shape1_7);
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
