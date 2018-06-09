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
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.bend_1 = new ModelRenderer(this, 0, 0);
		this.bend_1.setRotationPoint(0.0F, 0.0F, 8.0F);
		this.bend_1.addBox(0.0F, -4.0F, 0.0F, 4, 4, 9, 0.0F);
		this.setRotateAngle(bend_1, 0.7853981633974483F, 0.0F, 0.0F);
		this.fromWall = new ModelRenderer(this, 28, 0);
		this.fromWall.setRotationPoint(1.0F, 1.0F, -7.0F);
		this.fromWall.addBox(0.0F, 0.0F, 0.0F, 4, 4, 7, 0.0F);
		this.bend = new ModelRenderer(this, 0, 15);
		this.bend.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bend.addBox(0.0F, -4.0F, 0.0F, 4, 4, 8, 0.0F);
		this.setRotateAngle(bend, -2.356194490192345F, 0.0F, 0.0F);
		this.floorMount = new ModelRenderer(this, 28, 22);
		this.floorMount.setRotationPoint(-1.0F, -5.0F, 7.4F);
		this.floorMount.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
		this.wallMount = new ModelRenderer(this, 28, 13);
		this.wallMount.setRotationPoint(-3.0F, 9.0F, 7.0F);
		this.wallMount.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
		this.bend.addChild(this.bend_1);
		this.wallMount.addChild(this.fromWall);
		this.fromWall.addChild(this.bend);
		this.bend_1.addChild(this.floorMount);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.wallMount.render(f5);
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
