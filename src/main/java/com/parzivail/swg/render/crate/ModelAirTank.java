package com.parzivail.swg.render.crate;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelAirTank - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelAirTank extends ModelBase
{
	public ModelRenderer interior;
	public ModelRenderer interiorFillet;
	public ModelRenderer exterior;

	public ModelAirTank()
	{
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.exterior = new ModelRenderer(this, 0, 0);
		this.exterior.setRotationPoint(-3.0F, -2.4F, -3.0F);
		this.exterior.addBox(0.0F, 0.0F, 0.0F, 6, 26, 6, 0.0F);
		this.interior = new ModelRenderer(this, 0, 34);
		this.interior.setRotationPoint(-1.5F, -7.0F, -1.5F);
		this.interior.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
		this.interiorFillet = new ModelRenderer(this, 26, 0);
		this.interiorFillet.setRotationPoint(-2.5F, -3.0F, -2.5F);
		this.interiorFillet.addBox(0.0F, 0.0F, 0.0F, 5, 27, 5, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.exterior.render(f5);
		this.interior.render(f5);
		this.interiorFillet.render(f5);
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
