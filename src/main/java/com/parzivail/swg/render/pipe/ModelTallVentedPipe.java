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
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.gauge = new ModelRenderer(this, 22, 6);
		this.gauge.setRotationPoint(-4.0F, 7.5F, 0.5F);
		this.gauge.addBox(0.0F, 0.0F, 0.0F, 3, 3, 2, 0.0F);
		this.stem = new ModelRenderer(this, 0, 27);
		this.stem.setRotationPoint(2.0F, 1.0F, 0.5F);
		this.stem.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		this.shroud = new ModelRenderer(this, 0, 9);
		this.shroud.setRotationPoint(-0.5F, 6.0F, -0.5F);
		this.shroud.addBox(0.0F, 0.0F, 0.0F, 3, 13, 3, 0.0F);
		this.vent = new ModelRenderer(this, 0, 0);
		this.vent.setRotationPoint(-2.0F, -1.0F, -3.0F);
		this.vent.addBox(0.0F, 0.0F, 0.0F, 6, 3, 4, 0.0F);
		this.shroud_1 = new ModelRenderer(this, 22, 0);
		this.shroud_1.setRotationPoint(-0.5F, 4.0F, -0.5F);
		this.shroud_1.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
		this.pipe = new ModelRenderer(this, 36, 0);
		this.pipe.setRotationPoint(-1.0F, 4.0F, 4.0F);
		this.pipe.addBox(0.0F, 0.0F, 0.0F, 2, 20, 2, 0.0F);
		this.pipe.addChild(this.gauge);
		this.gauge.addChild(this.stem);
		this.pipe.addChild(this.shroud);
		this.pipe.addChild(this.vent);
		this.pipe.addChild(this.shroud_1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.pipe.render(f5);
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
