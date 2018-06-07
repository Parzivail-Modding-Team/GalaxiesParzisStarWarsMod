package com.parzivail.swg.render.ladder;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Ladder - Weaston
 * Created using Tabula 4.1.1
 */
public class ModelLadderTop extends ModelBase
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

	public ModelLadderTop()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape1_3 = new ModelRenderer(this, 0, 75);
		this.shape1_3.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_3.addBox(-14.5F, -5.0F, -1.0F, 14, 1, 2, 0.0F);
		this.shape1_2 = new ModelRenderer(this, 0, 60);
		this.shape1_2.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_2.addBox(-14.5F, 3.0F, -1.0F, 14, 1, 2, 0.0F);
		this.shape1_5 = new ModelRenderer(this, 0, 112);
		this.shape1_5.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_5.addBox(-15.5F, -8.0F, 1.0F, 16, 1, 1, 0.0F);
		this.shape1_7 = new ModelRenderer(this, 147, 0);
		this.shape1_7.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_7.addBox(-15.5F, -8.0F, 5.0F, 16, 1, 1, 0.0F);
		this.shape1_9 = new ModelRenderer(this, 42, 36);
		this.shape1_9.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_9.addBox(-15.5F, -8.0F, 0.0F, 1, 1, 8, 0.0F);
		this.shape1_10 = new ModelRenderer(this, 59, 66);
		this.shape1_10.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_10.addBox(-0.5F, -8.0F, 0.0F, 1, 1, 8, 0.0F);
		this.shape1_1 = new ModelRenderer(this, 0, 27);
		this.shape1_1.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_1.addBox(-0.5F, -8.0F, -1.0F, 1, 16, 2, 0.0F);
		this.shape1_4 = new ModelRenderer(this, 0, 94);
		this.shape1_4.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_4.addBox(-15.5F, -8.0F, -1.0F, 16, 1, 1, 0.0F);
		this.shape1_6 = new ModelRenderer(this, 45, 0);
		this.shape1_6.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_6.addBox(-15.5F, -8.0F, 3.0F, 16, 1, 1, 0.0F);
		this.shape1_8 = new ModelRenderer(this, 42, 21);
		this.shape1_8.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.shape1_8.addBox(-15.5F, -8.0F, 7.0F, 16, 1, 1, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(-7.5F, 16.0F, 0.0F);
		this.shape1.addBox(-0.5F, -8.0F, -1.0F, 1, 16, 2, 0.0F);
		this.shape1.addChild(this.shape1_3);
		this.shape1.addChild(this.shape1_2);
		this.shape1.addChild(this.shape1_5);
		this.shape1.addChild(this.shape1_7);
		this.shape1.addChild(this.shape1_9);
		this.shape1.addChild(this.shape1_10);
		this.shape1.addChild(this.shape1_1);
		this.shape1.addChild(this.shape1_4);
		this.shape1.addChild(this.shape1_6);
		this.shape1.addChild(this.shape1_8);
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
