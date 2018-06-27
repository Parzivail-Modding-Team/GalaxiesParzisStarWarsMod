package com.parzivail.swg.render.pipe;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelQuadVentPipe - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelQuadVentPipe extends ModelBase
{
	public ModelRenderer pipe1;
	public ModelRenderer pipe1_1;
	public ModelRenderer pipe1_2;
	public ModelRenderer pipe1_3;
	public ModelRenderer gasket;
	public ModelRenderer top;
	public ModelRenderer bend;
	public ModelRenderer smallFlange;
	public ModelRenderer flange;
	public ModelRenderer gasket_1;
	public ModelRenderer top_1;
	public ModelRenderer bend_1;
	public ModelRenderer smallFlange_1;
	public ModelRenderer flange_1;
	public ModelRenderer gasket_2;
	public ModelRenderer top_2;
	public ModelRenderer bend_2;
	public ModelRenderer smallFlange_2;
	public ModelRenderer flange_2;
	public ModelRenderer gasket_3;
	public ModelRenderer top_3;
	public ModelRenderer bend_3;
	public ModelRenderer smallFlange_3;
	public ModelRenderer flange_3;

	public ModelQuadVentPipe()
	{
		textureWidth = 128;
		textureHeight = 96;
		smallFlange_3 = new ModelRenderer(this, 34, 0);
		smallFlange_3.setRotationPoint(4.0F, -0.5F, -3.5F);
		smallFlange_3.addBox(0.0F, 0.0F, 0.0F, 1, 7, 7, 0.0F);
		bend = new ModelRenderer(this, 0, 11);
		bend.setRotationPoint(0.0F, -6.0F, 0.0F);
		bend.addBox(-3.0F, 0.0F, -3.0F, 8, 6, 6, 0.0F);
		pipe1_2 = new ModelRenderer(this, 0, 25);
		pipe1_2.setRotationPoint(0.0F, 6.0F, 0.0F);
		pipe1_2.addBox(2.0F, 0.0F, -3.0F, 6, 18, 6, 0.0F);
		setRotateAngle(pipe1_2, 0.0F, 3.141592653589793F, 0.0F);
		gasket = new ModelRenderer(this, 0, 0);
		gasket.setRotationPoint(5.0F, -1.0F, 0.0F);
		gasket.addBox(-4.0F, 0.0F, -4.0F, 8, 1, 8, 0.0F);
		bend_3 = new ModelRenderer(this, 0, 11);
		bend_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		bend_3.addBox(-3.0F, 0.0F, -3.0F, 8, 6, 6, 0.0F);
		pipe1_3 = new ModelRenderer(this, 0, 25);
		pipe1_3.setRotationPoint(0.0F, 8.0F, 0.0F);
		pipe1_3.addBox(2.0F, 0.0F, -3.0F, 6, 16, 6, 0.0F);
		setRotateAngle(pipe1_3, 0.0F, -1.5707963267948966F, 0.0F);
		smallFlange = new ModelRenderer(this, 34, 0);
		smallFlange.setRotationPoint(4.0F, -0.5F, -3.5F);
		smallFlange.addBox(0.0F, 0.0F, 0.0F, 1, 7, 7, 0.0F);
		flange_1 = new ModelRenderer(this, 0, 65);
		flange_1.setRotationPoint(1.0F, -0.5F, -0.5F);
		flange_1.addBox(0.0F, 0.0F, 0.0F, 1, 8, 8, 0.0F);
		smallFlange_2 = new ModelRenderer(this, 34, 0);
		smallFlange_2.setRotationPoint(4.0F, -0.5F, -3.5F);
		smallFlange_2.addBox(0.0F, 0.0F, 0.0F, 1, 7, 7, 0.0F);
		pipe1_1 = new ModelRenderer(this, 0, 25);
		pipe1_1.setRotationPoint(0.0F, 12.0F, 0.0F);
		pipe1_1.addBox(2.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		setRotateAngle(pipe1_1, 0.0F, 1.5707963267948966F, 0.0F);
		gasket_1 = new ModelRenderer(this, 0, 0);
		gasket_1.setRotationPoint(5.0F, -1.0F, 0.0F);
		gasket_1.addBox(-4.0F, 0.0F, -4.0F, 8, 1, 8, 0.0F);
		flange_3 = new ModelRenderer(this, 0, 65);
		flange_3.setRotationPoint(1.0F, -0.5F, -0.5F);
		flange_3.addBox(0.0F, 0.0F, 0.0F, 1, 8, 8, 0.0F);
		gasket_2 = new ModelRenderer(this, 0, 0);
		gasket_2.setRotationPoint(5.0F, -1.0F, 0.0F);
		gasket_2.addBox(-4.0F, 0.0F, -4.0F, 8, 1, 8, 0.0F);
		top_2 = new ModelRenderer(this, 0, 53);
		top_2.setRotationPoint(0.0F, -4.0F, 0.0F);
		top_2.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6, 0.0F);
		bend_2 = new ModelRenderer(this, 0, 11);
		bend_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		bend_2.addBox(-3.0F, 0.0F, -3.0F, 8, 6, 6, 0.0F);
		pipe1 = new ModelRenderer(this, 0, 25);
		pipe1.setRotationPoint(0.0F, 4.0F, 0.0F);
		pipe1.addBox(2.0F, 0.0F, -3.0F, 6, 20, 6, 0.0F);
		top_1 = new ModelRenderer(this, 0, 53);
		top_1.setRotationPoint(0.0F, -4.0F, 0.0F);
		top_1.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6, 0.0F);
		top_3 = new ModelRenderer(this, 0, 53);
		top_3.setRotationPoint(0.0F, -4.0F, 0.0F);
		top_3.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6, 0.0F);
		bend_1 = new ModelRenderer(this, 0, 11);
		bend_1.setRotationPoint(0.0F, -6.0F, 0.0F);
		bend_1.addBox(-3.0F, 0.0F, -3.0F, 8, 6, 6, 0.0F);
		flange_2 = new ModelRenderer(this, 0, 65);
		flange_2.setRotationPoint(1.0F, -0.5F, -0.5F);
		flange_2.addBox(0.0F, 0.0F, 0.0F, 1, 8, 8, 0.0F);
		top = new ModelRenderer(this, 0, 53);
		top.setRotationPoint(0.0F, -4.0F, 0.0F);
		top.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6, 0.0F);
		flange = new ModelRenderer(this, 0, 65);
		flange.setRotationPoint(1.0F, -0.5F, -0.5F);
		flange.addBox(0.0F, 0.0F, 0.0F, 1, 8, 8, 0.0F);
		smallFlange_1 = new ModelRenderer(this, 34, 0);
		smallFlange_1.setRotationPoint(4.0F, -0.5F, -3.5F);
		smallFlange_1.addBox(0.0F, 0.0F, 0.0F, 1, 7, 7, 0.0F);
		gasket_3 = new ModelRenderer(this, 0, 0);
		gasket_3.setRotationPoint(5.0F, -1.0F, 0.0F);
		gasket_3.addBox(-4.0F, 0.0F, -4.0F, 8, 1, 8, 0.0F);
		bend_3.addChild(smallFlange_3);
		top.addChild(bend);
		pipe1.addChild(gasket);
		top_3.addChild(bend_3);
		bend.addChild(smallFlange);
		smallFlange_1.addChild(flange_1);
		bend_2.addChild(smallFlange_2);
		pipe1_1.addChild(gasket_1);
		smallFlange_3.addChild(flange_3);
		pipe1_2.addChild(gasket_2);
		gasket_2.addChild(top_2);
		top_2.addChild(bend_2);
		gasket_1.addChild(top_1);
		gasket_3.addChild(top_3);
		top_1.addChild(bend_1);
		smallFlange_2.addChild(flange_2);
		gasket.addChild(top);
		smallFlange.addChild(flange);
		bend_1.addChild(smallFlange_1);
		pipe1_3.addChild(gasket_3);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		pipe1_2.render(f5);
		pipe1_3.render(f5);
		pipe1_1.render(f5);
		pipe1.render(f5);
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
