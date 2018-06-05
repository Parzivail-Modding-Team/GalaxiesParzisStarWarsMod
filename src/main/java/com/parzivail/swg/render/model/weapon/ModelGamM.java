package com.parzivail.swg.render.model.weapon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * NewProject - Undefined
 * Created using Tabula 4.1.1
 */
public class ModelGamM extends ModelBase
{
	public ModelRenderer Shape1;
	public ModelRenderer Shape2;
	public ModelRenderer Shape3;
	public ModelRenderer Shape4;
	public ModelRenderer Shape10;
	public ModelRenderer Shape15;
	public ModelRenderer Shape16;
	public ModelRenderer Shape5;
	public ModelRenderer Shape6;
	public ModelRenderer Shape7;
	public ModelRenderer Shape8;
	public ModelRenderer Shape9;
	public ModelRenderer Shape11;
	public ModelRenderer Shape12;

	public ModelGamM()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.Shape12 = new ModelRenderer(this, 42, 33);
		this.Shape12.setRotationPoint(23.0F, 2.5F, 3.0F);
		this.Shape12.addBox(0.0F, -1.0F, 0.0F, 6, 1, 4, 0.0F);
		this.setRotateAngle(Shape12, 0.13962633907794952F, -0.0F, 0.0F);
		this.Shape6 = new ModelRenderer(this, 0, 87);
		this.Shape6.setRotationPoint(23.0F, -1.0F, -1.0F);
		this.Shape6.addBox(0.0F, 0.0F, 0.0F, 6, 4, 4, 0.0F);
		this.Shape9 = new ModelRenderer(this, 0, 26);
		this.Shape9.setRotationPoint(21.0F, 0.0F, 16.0F);
		this.Shape9.addBox(0.0F, 0.0F, -6.0F, 11, 2, 6, 0.0F);
		this.setRotateAngle(Shape9, 0.0F, 0.40142571926116943F, 0.0F);
		this.Shape7 = new ModelRenderer(this, 0, 64);
		this.Shape7.setRotationPoint(23.0F, 0.0F, 3.0F);
		this.Shape7.addBox(0.0F, 0.0F, 0.0F, 6, 2, 8, 0.0F);
		this.Shape1 = new ModelRenderer(this, 0, 121);
		this.Shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Shape1.addBox(0.0F, 0.0F, 0.0F, 44, 2, 2, 0.0F);
		this.Shape11 = new ModelRenderer(this, 42, 15);
		this.Shape11.setRotationPoint(23.0F, -0.5F, 3.0F);
		this.Shape11.addBox(0.0F, 0.0F, 0.0F, 6, 1, 4, 0.0F);
		this.setRotateAngle(Shape11, -0.13962633907794952F, -0.0F, 0.0F);
		this.Shape15 = new ModelRenderer(this, 42, 6);
		this.Shape15.setRotationPoint(4.0F, -0.5F, 3.0F);
		this.Shape15.addBox(0.0F, 0.0F, 0.0F, 6, 1, 4, 0.0F);
		this.setRotateAngle(Shape15, -0.13962633907794952F, -0.0F, 0.0F);
		this.Shape8 = new ModelRenderer(this, 0, 38);
		this.Shape8.setRotationPoint(10.0F, 0.0F, 16.0F);
		this.Shape8.addBox(-7.0F, 0.0F, -5.0F, 7, 2, 5, 0.0F);
		this.setRotateAngle(Shape8, 0.0F, -0.33161255717277527F, 0.0F);
		this.Shape10 = new ModelRenderer(this, 0, 15);
		this.Shape10.setRotationPoint(10.0F, 0.0F, 10.0F);
		this.Shape10.addBox(0.0F, 0.0F, 0.0F, 11, 2, 6, 0.0F);
		this.Shape16 = new ModelRenderer(this, 42, 24);
		this.Shape16.setRotationPoint(4.0F, 2.5F, 3.0F);
		this.Shape16.addBox(0.0F, -1.0F, 0.0F, 6, 1, 4, 0.0F);
		this.setRotateAngle(Shape16, 0.13962633907794952F, -0.0F, 0.0F);
		this.Shape4 = new ModelRenderer(this, 0, 49);
		this.Shape4.setRotationPoint(4.0F, 0.0F, 3.0F);
		this.Shape4.addBox(0.0F, 0.0F, 0.0F, 6, 2, 9, 0.0F);
		this.Shape3 = new ModelRenderer(this, 0, 107);
		this.Shape3.setRotationPoint(4.0F, -1.0F, -1.0F);
		this.Shape3.addBox(0.0F, 0.0F, 0.0F, 6, 4, 4, 0.0F);
		this.Shape2 = new ModelRenderer(this, 0, 98);
		this.Shape2.setRotationPoint(3.0F, -0.5F, -0.5F);
		this.Shape2.addBox(0.0F, 0.0F, 0.0F, 8, 3, 3, 0.0F);
		this.Shape5 = new ModelRenderer(this, 0, 78);
		this.Shape5.setRotationPoint(22.0F, -0.5F, -0.5F);
		this.Shape5.addBox(0.0F, 0.0F, 0.0F, 8, 3, 3, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.Shape12.render(f5);
		this.Shape6.render(f5);
		this.Shape9.render(f5);
		this.Shape7.render(f5);
		this.Shape1.render(f5);
		this.Shape11.render(f5);
		this.Shape15.render(f5);
		this.Shape8.render(f5);
		this.Shape10.render(f5);
		this.Shape16.render(f5);
		this.Shape4.render(f5);
		this.Shape3.render(f5);
		this.Shape2.render(f5);
		this.Shape5.render(f5);
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
