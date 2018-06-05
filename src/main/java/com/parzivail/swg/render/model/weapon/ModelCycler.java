package com.parzivail.swg.render.model.weapon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * NewProject - Undefined
 * Created using Tabula 4.1.1
 */
public class ModelCycler extends ModelBase
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
	public ModelRenderer Shape17;
	public ModelRenderer Shape18;
	public ModelRenderer Shape19;

	public ModelCycler()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.Shape16 = new ModelRenderer(this, 0, 60);
		this.Shape16.setRotationPoint(-1.0F, -3.5F, 0.5F);
		this.Shape16.addBox(0.0F, 0.0F, 0.0F, 13, 2, 2, 0.0F);
		this.Shape3 = new ModelRenderer(this, 0, 102);
		this.Shape3.setRotationPoint(-17.5F, 2.0F, 0.0F);
		this.Shape3.addBox(-6.0F, 0.0F, 0.0F, 7, 3, 3, 0.0F);
		this.Shape14 = new ModelRenderer(this, 14, 5);
		this.Shape14.setRotationPoint(41.0F, 1.0F, 1.0F);
		this.Shape14.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		this.Shape17 = new ModelRenderer(this, 0, 10);
		this.Shape17.setRotationPoint(12.0F, -4.0F, 0.0F);
		this.Shape17.addBox(0.0F, 0.0F, 0.0F, 5, 3, 3, 0.0F);
		this.Shape15 = new ModelRenderer(this, 3, 6);
		this.Shape15.setRotationPoint(42.0F, -1.5F, 1.0F);
		this.Shape15.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.Shape19 = new ModelRenderer(this, 26, 12);
		this.Shape19.setRotationPoint(-6.0F, 2.5F, 0.0F);
		this.Shape19.addBox(-1.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.Shape1 = new ModelRenderer(this, 0, 119);
		this.Shape1.setRotationPoint(-11.0F, 0.0F, 0.0F);
		this.Shape1.addBox(0.0F, 0.0F, 0.0F, 24, 3, 3, 0.0F);
		this.Shape6 = new ModelRenderer(this, 0, 94);
		this.Shape6.setRotationPoint(-9.0F, -0.5F, 0.5F);
		this.Shape6.addBox(0.0F, 0.0F, 0.0F, 57, 2, 2, 0.0F);
		this.Shape5 = new ModelRenderer(this, 0, 48);
		this.Shape5.setRotationPoint(-22.5F, 3.5F, 0.0F);
		this.Shape5.addBox(-5.0F, 0.0F, 0.0F, 5, 5, 3, 0.0F);
		this.setRotateAngle(Shape5, 0.0F, -0.0F, 0.2094395160675048F);
		this.Shape18 = new ModelRenderer(this, 1, 30);
		this.Shape18.setRotationPoint(5.0F, -4.5F, 1.0F);
		this.Shape18.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.Shape13 = new ModelRenderer(this, 27, 44);
		this.Shape13.setRotationPoint(38.0F, -1.0F, 0.0F);
		this.Shape13.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
		this.Shape12 = new ModelRenderer(this, 27, 34);
		this.Shape12.setRotationPoint(18.0F, -1.0F, 0.0F);
		this.Shape12.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
		this.Shape7 = new ModelRenderer(this, 0, 24);
		this.Shape7.setRotationPoint(-6.0F, 6.0F, 1.0F);
		this.Shape7.addBox(0.0F, -1.0F, 0.0F, 6, 1, 1, 0.0F);
		this.setRotateAngle(Shape7, 0.0F, -0.0F, -0.5410520434379578F);
		this.Shape8 = new ModelRenderer(this, 0, 19);
		this.Shape8.setRotationPoint(-6.0F, 6.0F, 1.0F);
		this.Shape8.addBox(-7.0F, -1.0F, 0.0F, 7, 1, 1, 0.0F);
		this.setRotateAngle(Shape8, 0.0F, -0.0F, 0.296705961227417F);
		this.Shape10 = new ModelRenderer(this, 0, 70);
		this.Shape10.setRotationPoint(2.0F, -4.0F, 0.0F);
		this.Shape10.addBox(0.0F, 0.0F, 0.0F, 1, 4, 3, 0.0F);
		this.Shape4 = new ModelRenderer(this, 0, 36);
		this.Shape4.setRotationPoint(-22.5F, 3.5F, 0.0F);
		this.Shape4.addBox(-5.0F, -5.0F, 0.0F, 5, 5, 3, 0.0F);
		this.setRotateAngle(Shape4, 0.0F, -0.0F, -0.15707963705062866F);
		this.Shape9 = new ModelRenderer(this, 24, 19);
		this.Shape9.setRotationPoint(-4.0F, -1.0F, 0.0F);
		this.Shape9.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
		this.Shape2 = new ModelRenderer(this, 0, 111);
		this.Shape2.setRotationPoint(-16.5F, 5.0F, 0.0F);
		this.Shape2.addBox(0.0F, -3.0F, 0.0F, 7, 3, 3, 0.0F);
		this.setRotateAngle(Shape2, 0.0F, -0.0F, -0.296705961227417F);
		this.Shape11 = new ModelRenderer(this, 14, 70);
		this.Shape11.setRotationPoint(8.0F, -4.0F, 0.0F);
		this.Shape11.addBox(0.0F, 0.0F, 0.0F, 1, 4, 3, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.Shape16.render(f5);
		this.Shape3.render(f5);
		this.Shape14.render(f5);
		this.Shape17.render(f5);
		this.Shape15.render(f5);
		this.Shape19.render(f5);
		this.Shape1.render(f5);
		this.Shape6.render(f5);
		this.Shape5.render(f5);
		this.Shape18.render(f5);
		this.Shape13.render(f5);
		this.Shape12.render(f5);
		this.Shape7.render(f5);
		this.Shape8.render(f5);
		this.Shape10.render(f5);
		this.Shape4.render(f5);
		this.Shape9.render(f5);
		this.Shape2.render(f5);
		this.Shape11.render(f5);
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
