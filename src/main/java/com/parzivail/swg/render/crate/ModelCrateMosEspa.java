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
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape7 = new ModelRenderer(this, 264, 0);
		this.shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape7.addBox(-5.0F, -6.0F, -4.0F, 10, 12, 5, 0.0F);
		this.shape2 = new ModelRenderer(this, 152, 0);
		this.shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape2.addBox(-7.0F, -4.0F, -10.0F, 14, 8, 5, 0.0F);
		this.shape9 = new ModelRenderer(this, 328, 0);
		this.shape9.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape9.addBox(-5.0F, -6.0F, 8.0F, 10, 12, 5, 0.0F);
		this.shape8 = new ModelRenderer(this, 296, 0);
		this.shape8.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape8.addBox(-5.0F, -6.0F, 2.0F, 10, 12, 5, 0.0F);
		this.shape3 = new ModelRenderer(this, 152, 15);
		this.shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape3.addBox(-7.0F, -4.0F, -4.0F, 14, 8, 5, 0.0F);
		this.shape4 = new ModelRenderer(this, 192, 0);
		this.shape4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape4.addBox(-7.0F, -4.0F, 2.0F, 14, 8, 5, 0.0F);
		this.shape5 = new ModelRenderer(this, 192, 15);
		this.shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape5.addBox(-7.0F, -4.0F, 8.0F, 14, 8, 5, 0.0F);
		this.shape10 = new ModelRenderer(this, 76, 0);
		this.shape10.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape10.addBox(-5.0F, -4.0F, -12.0F, 10, 8, 27, 0.0F);
		this.shape6 = new ModelRenderer(this, 232, 0);
		this.shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape6.addBox(-5.0F, -6.0F, -10.0F, 10, 12, 5, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(0.0F, 18.0F, -1.5F);
		this.shape1.addBox(-6.0F, -5.0F, -11.0F, 12, 10, 25, 0.0F);
		this.shape1.addChild(this.shape7);
		this.shape1.addChild(this.shape2);
		this.shape1.addChild(this.shape9);
		this.shape1.addChild(this.shape8);
		this.shape1.addChild(this.shape3);
		this.shape1.addChild(this.shape4);
		this.shape1.addChild(this.shape5);
		this.shape1.addChild(this.shape10);
		this.shape1.addChild(this.shape6);
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
