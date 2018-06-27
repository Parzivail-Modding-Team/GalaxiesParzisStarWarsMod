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
		textureWidth = 64;
		textureHeight = 64;
		exterior = new ModelRenderer(this, 0, 0);
		exterior.setRotationPoint(-3.0F, -2.4F, -3.0F);
		exterior.addBox(0.0F, 0.0F, 0.0F, 6, 26, 6, 0.0F);
		interior = new ModelRenderer(this, 0, 34);
		interior.setRotationPoint(-1.5F, -7.0F, -1.5F);
		interior.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
		interiorFillet = new ModelRenderer(this, 26, 0);
		interiorFillet.setRotationPoint(-2.5F, -3.0F, -2.5F);
		interiorFillet.addBox(0.0F, 0.0F, 0.0F, 5, 27, 5, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		exterior.render(f5);
		interior.render(f5);
		interiorFillet.render(f5);
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
