package com.parzivail.swg.render.weapon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * NewProject - Undefined
 * Created using Tabula 4.1.1
 */
public class ModelSpear extends ModelBase
{
	public ModelRenderer Shape1;
	public ModelRenderer Shape2;
	public ModelRenderer Shape4;
	public ModelRenderer Shape5;
	public ModelRenderer Shape6;
	public ModelRenderer Shape3;
	public ModelRenderer Shape7;
	public ModelRenderer Shape8;
	public ModelRenderer Shape9;
	public ModelRenderer Shape10;
	public ModelRenderer Shape11;

	public ModelSpear()
	{
		textureWidth = 512;
		textureHeight = 512;
		Shape6 = new ModelRenderer(this, 0, 23);
		Shape6.setRotationPoint(-11.0F, 0.5F, 2.5F);
		Shape6.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
		setRotateAngle(Shape6, 0.0F, -0.5235987901687622F, 0.0F);
		Shape11 = new ModelRenderer(this, 26, 24);
		Shape11.setRotationPoint(-6.5F, -0.5F, -0.5F);
		Shape11.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
		Shape8 = new ModelRenderer(this, 0, 38);
		Shape8.setRotationPoint(15.0F, 0.0F, 0.0F);
		Shape8.addBox(0.0F, 0.0F, 0.0F, 28, 2, 2, 0.0F);
		setRotateAngle(Shape8, -0.0015268864714515088F, -0.08725313512222008F, 0.017519947506561193F);
		Shape4 = new ModelRenderer(this, 10, 11);
		Shape4.setRotationPoint(-15.0F, 0.5F, 1.0F);
		Shape4.addBox(-1.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F);
		setRotateAngle(Shape4, 0.0F, 0.7853981852531433F, 0.0F);
		Shape5 = new ModelRenderer(this, 0, 18);
		Shape5.setRotationPoint(-11.0F, 0.5F, -0.5F);
		Shape5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		setRotateAngle(Shape5, 0.0F, 0.5235987901687622F, 0.0F);
		Shape3 = new ModelRenderer(this, 0, 8);
		Shape3.setRotationPoint(-14.0F, 0.5F, -0.5F);
		Shape3.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(Shape3, 0.0F, 0.43633231520652765F, 0.0F);
		Shape7 = new ModelRenderer(this, 0, 13);
		Shape7.setRotationPoint(-14.0F, 0.5F, 2.5F);
		Shape7.addBox(-1.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F);
		setRotateAngle(Shape7, 0.0F, -0.43633231520652765F, 0.0F);
		Shape1 = new ModelRenderer(this, 0, 44);
		Shape1.setRotationPoint(-12.0F, 0.0F, 0.0F);
		Shape1.addBox(0.0F, 0.0F, 0.0F, 27, 2, 2, 0.0F);
		Shape10 = new ModelRenderer(this, 26, 15);
		Shape10.setRotationPoint(-9.0F, -0.5F, 0.0F);
		Shape10.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
		setRotateAngle(Shape10, 0.0F, 0.4188790321350098F, 0.0F);
		Shape9 = new ModelRenderer(this, 8, 1);
		Shape9.setRotationPoint(9.0F, -0.5F, -0.5F);
		Shape9.addBox(0.0F, 0.0F, 0.0F, 7, 3, 3, 0.0F);
		Shape2 = new ModelRenderer(this, 0, 29);
		Shape2.setRotationPoint(-14.0F, 0.5F, -0.5F);
		Shape2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 3, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		Shape6.render(f5);
		Shape11.render(f5);
		Shape8.render(f5);
		Shape4.render(f5);
		Shape5.render(f5);
		Shape3.render(f5);
		Shape7.render(f5);
		Shape1.render(f5);
		Shape10.render(f5);
		Shape9.render(f5);
		Shape2.render(f5);
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
