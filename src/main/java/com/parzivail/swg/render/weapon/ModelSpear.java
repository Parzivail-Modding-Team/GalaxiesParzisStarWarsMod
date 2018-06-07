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
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.Shape6 = new ModelRenderer(this, 0, 23);
		this.Shape6.setRotationPoint(-11.0F, 0.5F, 2.5F);
		this.Shape6.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(Shape6, 0.0F, -0.5235987901687622F, 0.0F);
		this.Shape11 = new ModelRenderer(this, 26, 24);
		this.Shape11.setRotationPoint(-6.5F, -0.5F, -0.5F);
		this.Shape11.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
		this.Shape8 = new ModelRenderer(this, 0, 38);
		this.Shape8.setRotationPoint(15.0F, 0.0F, 0.0F);
		this.Shape8.addBox(0.0F, 0.0F, 0.0F, 28, 2, 2, 0.0F);
		this.setRotateAngle(Shape8, -0.0015268864714515088F, -0.08725313512222008F, 0.017519947506561193F);
		this.Shape4 = new ModelRenderer(this, 10, 11);
		this.Shape4.setRotationPoint(-15.0F, 0.5F, 1.0F);
		this.Shape4.addBox(-1.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F);
		this.setRotateAngle(Shape4, 0.0F, 0.7853981852531433F, 0.0F);
		this.Shape5 = new ModelRenderer(this, 0, 18);
		this.Shape5.setRotationPoint(-11.0F, 0.5F, -0.5F);
		this.Shape5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(Shape5, 0.0F, 0.5235987901687622F, 0.0F);
		this.Shape3 = new ModelRenderer(this, 0, 8);
		this.Shape3.setRotationPoint(-14.0F, 0.5F, -0.5F);
		this.Shape3.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.setRotateAngle(Shape3, 0.0F, 0.43633231520652765F, 0.0F);
		this.Shape7 = new ModelRenderer(this, 0, 13);
		this.Shape7.setRotationPoint(-14.0F, 0.5F, 2.5F);
		this.Shape7.addBox(-1.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F);
		this.setRotateAngle(Shape7, 0.0F, -0.43633231520652765F, 0.0F);
		this.Shape1 = new ModelRenderer(this, 0, 44);
		this.Shape1.setRotationPoint(-12.0F, 0.0F, 0.0F);
		this.Shape1.addBox(0.0F, 0.0F, 0.0F, 27, 2, 2, 0.0F);
		this.Shape10 = new ModelRenderer(this, 26, 15);
		this.Shape10.setRotationPoint(-9.0F, -0.5F, 0.0F);
		this.Shape10.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
		this.setRotateAngle(Shape10, 0.0F, 0.4188790321350098F, 0.0F);
		this.Shape9 = new ModelRenderer(this, 8, 1);
		this.Shape9.setRotationPoint(9.0F, -0.5F, -0.5F);
		this.Shape9.addBox(0.0F, 0.0F, 0.0F, 7, 3, 3, 0.0F);
		this.Shape2 = new ModelRenderer(this, 0, 29);
		this.Shape2.setRotationPoint(-14.0F, 0.5F, -0.5F);
		this.Shape2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 3, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.Shape6.render(f5);
		this.Shape11.render(f5);
		this.Shape8.render(f5);
		this.Shape4.render(f5);
		this.Shape5.render(f5);
		this.Shape3.render(f5);
		this.Shape7.render(f5);
		this.Shape1.render(f5);
		this.Shape10.render(f5);
		this.Shape9.render(f5);
		this.Shape2.render(f5);
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
