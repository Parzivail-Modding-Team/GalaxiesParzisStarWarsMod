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
		textureWidth = 512;
		textureHeight = 512;
		shape1_3 = new ModelRenderer(this, 0, 75);
		shape1_3.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_3.addBox(-14.5F, -5.0F, -1.0F, 14, 1, 2, 0.0F);
		shape1_2 = new ModelRenderer(this, 0, 60);
		shape1_2.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_2.addBox(-14.5F, 3.0F, -1.0F, 14, 1, 2, 0.0F);
		shape1_5 = new ModelRenderer(this, 0, 112);
		shape1_5.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_5.addBox(-15.5F, -8.0F, 1.0F, 16, 1, 1, 0.0F);
		shape1_7 = new ModelRenderer(this, 147, 0);
		shape1_7.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_7.addBox(-15.5F, -8.0F, 5.0F, 16, 1, 1, 0.0F);
		shape1_9 = new ModelRenderer(this, 42, 36);
		shape1_9.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_9.addBox(-15.5F, -8.0F, 0.0F, 1, 1, 8, 0.0F);
		shape1_10 = new ModelRenderer(this, 59, 66);
		shape1_10.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_10.addBox(-0.5F, -8.0F, 0.0F, 1, 1, 8, 0.0F);
		shape1_1 = new ModelRenderer(this, 0, 27);
		shape1_1.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_1.addBox(-0.5F, -8.0F, -1.0F, 1, 16, 2, 0.0F);
		shape1_4 = new ModelRenderer(this, 0, 94);
		shape1_4.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_4.addBox(-15.5F, -8.0F, -1.0F, 16, 1, 1, 0.0F);
		shape1_6 = new ModelRenderer(this, 45, 0);
		shape1_6.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_6.addBox(-15.5F, -8.0F, 3.0F, 16, 1, 1, 0.0F);
		shape1_8 = new ModelRenderer(this, 42, 21);
		shape1_8.setRotationPoint(15.0F, 0.0F, 0.0F);
		shape1_8.addBox(-15.5F, -8.0F, 7.0F, 16, 1, 1, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-7.5F, 16.0F, 0.0F);
		shape1.addBox(-0.5F, -8.0F, -1.0F, 1, 16, 2, 0.0F);
		shape1.addChild(shape1_3);
		shape1.addChild(shape1_2);
		shape1.addChild(shape1_5);
		shape1.addChild(shape1_7);
		shape1.addChild(shape1_9);
		shape1.addChild(shape1_10);
		shape1.addChild(shape1_1);
		shape1.addChild(shape1_4);
		shape1.addChild(shape1_6);
		shape1.addChild(shape1_8);
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
