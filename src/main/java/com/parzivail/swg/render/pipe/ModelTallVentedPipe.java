package com.parzivail.swg.render.pipe;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelTallVentedPipe - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelTallVentedPipe extends ModelBase
{
	public ModelRenderer pipe;
	public ModelRenderer vent;
	public ModelRenderer shroud;
	public ModelRenderer shroud_1;
	public ModelRenderer gauge;
	public ModelRenderer stem;

	public ModelTallVentedPipe()
	{
		textureWidth = 64;
		textureHeight = 32;
		gauge = new ModelRenderer(this, 22, 6);
		gauge.setRotationPoint(-4.0F, 7.5F, 0.5F);
		gauge.addBox(0.0F, 0.0F, 0.0F, 3, 3, 2, 0.0F);
		stem = new ModelRenderer(this, 0, 27);
		stem.setRotationPoint(2.0F, 1.0F, 0.5F);
		stem.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		shroud = new ModelRenderer(this, 0, 9);
		shroud.setRotationPoint(-0.5F, 6.0F, -0.5F);
		shroud.addBox(0.0F, 0.0F, 0.0F, 3, 13, 3, 0.0F);
		vent = new ModelRenderer(this, 0, 0);
		vent.setRotationPoint(-2.0F, -1.0F, -3.0F);
		vent.addBox(0.0F, 0.0F, 0.0F, 6, 3, 4, 0.0F);
		shroud_1 = new ModelRenderer(this, 22, 0);
		shroud_1.setRotationPoint(-0.5F, 4.0F, -0.5F);
		shroud_1.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
		pipe = new ModelRenderer(this, 36, 0);
		pipe.setRotationPoint(-1.0F, 4.0F, 4.0F);
		pipe.addBox(0.0F, 0.0F, 0.0F, 2, 20, 2, 0.0F);
		pipe.addChild(gauge);
		gauge.addChild(stem);
		pipe.addChild(shroud);
		pipe.addChild(vent);
		pipe.addChild(shroud_1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		pipe.render(f5);
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
