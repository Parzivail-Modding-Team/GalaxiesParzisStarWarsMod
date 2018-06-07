package com.parzivail.swg.render.weapon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * NewProject - Undefined
 * Created using Tabula 4.1.1
 */
public class ModelGamS extends ModelBase
{
	public ModelRenderer Shape1;
	public ModelRenderer Shape2;
	public ModelRenderer Shape3;
	public ModelRenderer Shape4;
	public ModelRenderer Shape5;
	public ModelRenderer Shape6;
	public ModelRenderer Shape7;
	public ModelRenderer Shape8;
	public ModelRenderer Shape9;
	public ModelRenderer Shape10;
	public ModelRenderer Shape11;
	public ModelRenderer Shape12;
	public ModelRenderer Shape13;
	public ModelRenderer Shape14;
	public ModelRenderer Shape15;
	public ModelRenderer Shape16;

	public ModelGamS()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.Shape10 = new ModelRenderer(this, 0, 84);
		this.Shape10.setRotationPoint(10.0F, 0.0F, 9.5F);
		this.Shape10.addBox(0.0F, 0.0F, 0.0F, 1, 2, 5, 0.0F);
		this.Shape2 = new ModelRenderer(this, 0, 28);
		this.Shape2.setRotationPoint(3.0F, -0.5F, -0.5F);
		this.Shape2.addBox(0.0F, 0.0F, 0.0F, 8, 3, 3, 0.0F);
		this.Shape3 = new ModelRenderer(this, 0, 17);
		this.Shape3.setRotationPoint(4.0F, -1.0F, -1.0F);
		this.Shape3.addBox(0.0F, 0.0F, 0.0F, 6, 4, 4, 0.0F);
		this.Shape4 = new ModelRenderer(this, 0, 37);
		this.Shape4.setRotationPoint(4.0F, 0.0F, 3.0F);
		this.Shape4.addBox(0.0F, 0.0F, 0.0F, 6, 2, 11, 0.0F);
		this.Shape12 = new ModelRenderer(this, 9, 11);
		this.Shape12.setRotationPoint(11.5F, 0.0F, 13.5F);
		this.Shape12.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		this.Shape1 = new ModelRenderer(this, 0, 100);
		this.Shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Shape1.addBox(0.0F, 0.0F, 0.0F, 34, 2, 2, 0.0F);
		this.Shape15 = new ModelRenderer(this, 38, 4);
		this.Shape15.setRotationPoint(4.0F, -0.5F, 3.0F);
		this.Shape15.addBox(0.0F, 0.0F, 0.0F, 6, 1, 2, 0.0F);
		this.setRotateAngle(Shape15, -0.27925267815589905F, -0.0F, 0.0F);
		this.Shape16 = new ModelRenderer(this, 38, 11);
		this.Shape16.setRotationPoint(4.0F, 2.5F, 3.0F);
		this.Shape16.addBox(0.0F, -1.0F, 0.0F, 6, 1, 2, 0.0F);
		this.setRotateAngle(Shape16, 0.27925267815589905F, -0.0F, 0.0F);
		this.Shape6 = new ModelRenderer(this, 0, 60);
		this.Shape6.setRotationPoint(14.0F, 0.0F, 15.0F);
		this.Shape6.addBox(0.0F, 0.0F, -8.0F, 1, 2, 8, 0.0F);
		this.setRotateAngle(Shape6, 0.0F, 0.6457718014717102F, 0.0F);
		this.Shape9 = new ModelRenderer(this, 0, 5);
		this.Shape9.setRotationPoint(2.0F, 0.0F, 13.0F);
		this.Shape9.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		this.Shape8 = new ModelRenderer(this, 23, 12);
		this.Shape8.setRotationPoint(3.0F, 0.0F, 11.5F);
		this.Shape8.addBox(0.0F, 0.0F, 0.0F, 1, 2, 3, 0.0F);
		this.Shape14 = new ModelRenderer(this, 28, 21);
		this.Shape14.setRotationPoint(9.0F, 0.0F, 6.0F);
		this.Shape14.addBox(0.0F, 0.0F, 0.0F, 1, 2, 4, 0.0F);
		this.setRotateAngle(Shape14, 0.0F, 0.2617993950843811F, 0.0F);
		this.Shape11 = new ModelRenderer(this, 23, 3);
		this.Shape11.setRotationPoint(11.0F, 0.0F, 11.0F);
		this.Shape11.addBox(0.0F, 0.0F, 0.0F, 1, 2, 3, 0.0F);
		this.Shape5 = new ModelRenderer(this, 0, 54);
		this.Shape5.setRotationPoint(2.0F, 0.0F, 14.0F);
		this.Shape5.addBox(0.0F, 0.0F, 0.0F, 12, 2, 1, 0.0F);
		this.Shape7 = new ModelRenderer(this, 0, 73);
		this.Shape7.setRotationPoint(2.0F, 0.0F, 15.0F);
		this.Shape7.addBox(-1.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F);
		this.setRotateAngle(Shape7, 0.0F, -0.5235987901687622F, 0.0F);
		this.Shape13 = new ModelRenderer(this, 0, 10);
		this.Shape13.setRotationPoint(12.0F, 0.0F, 12.0F);
		this.Shape13.addBox(0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.Shape10.render(f5);
		this.Shape2.render(f5);
		this.Shape3.render(f5);
		this.Shape4.render(f5);
		this.Shape12.render(f5);
		this.Shape1.render(f5);
		this.Shape15.render(f5);
		this.Shape16.render(f5);
		this.Shape6.render(f5);
		this.Shape9.render(f5);
		this.Shape8.render(f5);
		this.Shape14.render(f5);
		this.Shape11.render(f5);
		this.Shape5.render(f5);
		this.Shape7.render(f5);
		this.Shape13.render(f5);
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
