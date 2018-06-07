package com.parzivail.swg.render.ladder;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Ladder - Weaston
 * Created using Tabula 4.1.1
 */
public class ModelLadder extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer shape1_1;
	public ModelRenderer shape1_2;
	public ModelRenderer shape1_3;

	public ModelLadder()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape1_3 = new ModelRenderer(this, 0, 100);
		this.shape1_3.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_3.addBox(-14.5F, -5.0F, -1.0F, 14, 1, 2, 0.0F);
		this.shape1_1 = new ModelRenderer(this, 0, 44);
		this.shape1_1.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_1.addBox(-0.5F, -8.0F, -1.0F, 1, 16, 2, 0.0F);
		this.shape1_2 = new ModelRenderer(this, 49, 0);
		this.shape1_2.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_2.addBox(-14.5F, 3.0F, -1.0F, 14, 1, 2, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(-7.5F, 16.0F, 0.0F);
		this.shape1.addBox(-0.5F, -8.0F, -1.0F, 1, 16, 2, 0.0F);
		this.shape1.addChild(this.shape1_3);
		this.shape1.addChild(this.shape1_1);
		this.shape1.addChild(this.shape1_2);
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
