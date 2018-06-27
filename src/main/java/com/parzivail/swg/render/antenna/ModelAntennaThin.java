package com.parzivail.swg.render.antenna;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelAntennaThin - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelAntennaThin extends ModelBase
{
	public ModelRenderer rod;
	public ModelRenderer vaneSupport;
	public ModelRenderer vaneSupport_1;
	public ModelRenderer vaneSupport_2;
	public ModelRenderer vaneSupport_3;
	public ModelRenderer shape3;
	public ModelRenderer shape3_1;
	public ModelRenderer shape3_2;
	public ModelRenderer shape3_3;

	public ModelAntennaThin()
	{
		textureWidth = 64;
		textureHeight = 64;
		shape3 = new ModelRenderer(this, 0, 9);
		shape3.setRotationPoint(0.0F, -6.0F, 4.5F);
		shape3.addBox(-1.0F, 0.0F, 0.0F, 2, 15, 1, 0.0F);
		shape3_1 = new ModelRenderer(this, 0, 27);
		shape3_1.setRotationPoint(0.0F, -6.0F, 4.5F);
		shape3_1.addBox(-1.0F, 0.0F, 0.0F, 2, 15, 1, 0.0F);
		rod = new ModelRenderer(this, 16, 9);
		rod.setRotationPoint(-0.5F, -16.0F, -0.5F);
		rod.addBox(0.0F, 0.0F, 0.0F, 1, 40, 1, 0.0F);
		vaneSupport_2 = new ModelRenderer(this, 24, 0);
		vaneSupport_2.setRotationPoint(0.5F, 3.0F, 0.5F);
		vaneSupport_2.addBox(-0.5F, 0.0F, 0.5F, 1, 3, 4, 0.0F);
		setRotateAngle(vaneSupport_2, 0.0F, -1.5707963267948966F, 0.0F);
		vaneSupport_1 = new ModelRenderer(this, 12, 0);
		vaneSupport_1.setRotationPoint(0.5F, 9.0F, 0.5F);
		vaneSupport_1.addBox(-0.5F, 0.0F, 0.5F, 1, 3, 4, 0.0F);
		setRotateAngle(vaneSupport_1, 0.0F, 1.5707963267948966F, 0.0F);
		vaneSupport_3 = new ModelRenderer(this, 36, 0);
		vaneSupport_3.setRotationPoint(0.5F, 6.0F, 0.5F);
		vaneSupport_3.addBox(-0.5F, 0.0F, 0.5F, 1, 3, 4, 0.0F);
		setRotateAngle(vaneSupport_3, 0.0F, 3.141592653589793F, 0.0F);
		vaneSupport = new ModelRenderer(this, 0, 0);
		vaneSupport.setRotationPoint(0.5F, 0.0F, 0.5F);
		vaneSupport.addBox(-0.5F, 0.0F, 0.5F, 1, 3, 4, 0.0F);
		shape3_2 = new ModelRenderer(this, 0, 45);
		shape3_2.setRotationPoint(0.0F, -6.0F, 4.5F);
		shape3_2.addBox(-1.0F, 0.0F, 0.0F, 2, 15, 1, 0.0F);
		shape3_3 = new ModelRenderer(this, 8, 9);
		shape3_3.setRotationPoint(0.0F, -6.0F, 4.5F);
		shape3_3.addBox(-1.0F, 0.0F, 0.0F, 2, 15, 1, 0.0F);
		vaneSupport.addChild(shape3);
		vaneSupport_1.addChild(shape3_1);
		rod.addChild(vaneSupport_2);
		rod.addChild(vaneSupport_1);
		rod.addChild(vaneSupport_3);
		rod.addChild(vaneSupport);
		vaneSupport_2.addChild(shape3_2);
		vaneSupport_3.addChild(shape3_3);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		rod.render(f5);
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
