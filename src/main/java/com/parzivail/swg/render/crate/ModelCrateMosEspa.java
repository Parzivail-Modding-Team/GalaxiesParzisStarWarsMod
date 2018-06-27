package com.parzivail.swg.render.crate;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Crate Mos Espa - Weaston
 * Created using P-Tabula 4.1.1
 */
public class ModelCrateMosEspa extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;
	public ModelRenderer shape6;
	public ModelRenderer shape7;
	public ModelRenderer shape8;
	public ModelRenderer shape9;
	public ModelRenderer shape10;

	public ModelCrateMosEspa()
	{
		textureWidth = 512;
		textureHeight = 512;
		shape7 = new ModelRenderer(this, 264, 0);
		shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape7.addBox(-5.0F, -6.0F, -4.0F, 10, 12, 5, 0.0F);
		shape2 = new ModelRenderer(this, 152, 0);
		shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape2.addBox(-7.0F, -4.0F, -10.0F, 14, 8, 5, 0.0F);
		shape9 = new ModelRenderer(this, 328, 0);
		shape9.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape9.addBox(-5.0F, -6.0F, 8.0F, 10, 12, 5, 0.0F);
		shape8 = new ModelRenderer(this, 296, 0);
		shape8.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape8.addBox(-5.0F, -6.0F, 2.0F, 10, 12, 5, 0.0F);
		shape3 = new ModelRenderer(this, 152, 15);
		shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape3.addBox(-7.0F, -4.0F, -4.0F, 14, 8, 5, 0.0F);
		shape4 = new ModelRenderer(this, 192, 0);
		shape4.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape4.addBox(-7.0F, -4.0F, 2.0F, 14, 8, 5, 0.0F);
		shape5 = new ModelRenderer(this, 192, 15);
		shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape5.addBox(-7.0F, -4.0F, 8.0F, 14, 8, 5, 0.0F);
		shape10 = new ModelRenderer(this, 76, 0);
		shape10.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape10.addBox(-5.0F, -4.0F, -12.0F, 10, 8, 27, 0.0F);
		shape6 = new ModelRenderer(this, 232, 0);
		shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape6.addBox(-5.0F, -6.0F, -10.0F, 10, 12, 5, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(0.0F, 18.0F, -1.5F);
		shape1.addBox(-6.0F, -5.0F, -11.0F, 12, 10, 25, 0.0F);
		shape1.addChild(shape7);
		shape1.addChild(shape2);
		shape1.addChild(shape9);
		shape1.addChild(shape8);
		shape1.addChild(shape3);
		shape1.addChild(shape4);
		shape1.addChild(shape5);
		shape1.addChild(shape10);
		shape1.addChild(shape6);
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
