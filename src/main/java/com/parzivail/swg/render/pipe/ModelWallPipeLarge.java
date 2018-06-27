package com.parzivail.swg.render.pipe;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelWallPipeLarge - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelWallPipeLarge extends ModelBase
{
	public ModelRenderer wallMount;
	public ModelRenderer fromWall;
	public ModelRenderer bend;
	public ModelRenderer bend_1;
	public ModelRenderer floorMount;

	public ModelWallPipeLarge()
	{
		textureWidth = 64;
		textureHeight = 32;
		bend_1 = new ModelRenderer(this, 0, 0);
		bend_1.setRotationPoint(0.0F, 0.0F, 8.0F);
		bend_1.addBox(0.0F, -4.0F, 0.0F, 4, 4, 9, 0.0F);
		setRotateAngle(bend_1, 0.7853981633974483F, 0.0F, 0.0F);
		fromWall = new ModelRenderer(this, 28, 0);
		fromWall.setRotationPoint(1.0F, 1.0F, -7.0F);
		fromWall.addBox(0.0F, 0.0F, 0.0F, 4, 4, 7, 0.0F);
		bend = new ModelRenderer(this, 0, 15);
		bend.setRotationPoint(0.0F, 0.0F, 0.0F);
		bend.addBox(0.0F, -4.0F, 0.0F, 4, 4, 8, 0.0F);
		setRotateAngle(bend, -2.356194490192345F, 0.0F, 0.0F);
		floorMount = new ModelRenderer(this, 28, 22);
		floorMount.setRotationPoint(-1.0F, -5.0F, 7.4F);
		floorMount.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
		wallMount = new ModelRenderer(this, 28, 13);
		wallMount.setRotationPoint(-3.0F, 9.0F, 7.0F);
		wallMount.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
		bend.addChild(bend_1);
		wallMount.addChild(fromWall);
		fromWall.addChild(bend);
		bend_1.addChild(floorMount);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		wallMount.render(f5);
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
