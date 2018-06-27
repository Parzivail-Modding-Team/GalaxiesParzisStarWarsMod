package com.parzivail.swg.render.light;

import com.parzivail.util.render.ShaderModelRenderer;
import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelAngledWallLamp - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelAngledWallLamp extends ModelBase
{
	public ModelRenderer wallPlate;
	public ModelRenderer wallAdapter;
	public ModelRenderer lamp;
	public ModelRenderer grill;
	public ModelRenderer grill_1;
	public ModelRenderer grill_2;
	public ModelRenderer grill_3;

	public ModelAngledWallLamp()
	{
		textureWidth = 64;
		textureHeight = 64;
		lamp = new ShaderModelRenderer(this, 0, 11, ShaderHelper.glowSolid);
		lamp.setRotationPoint(4.5F, -1.5F, 0.0F);
		lamp.addBox(0.0F, 0.0F, 1.0F, 5, 5, 7, 0.0F);
		setRotateAngle(lamp, -0.7853981633974483F, 3.141592653589793F, 0.0F);
		grill_2 = new ModelRenderer(this, 0, 25);
		grill_2.setRotationPoint(3.0F, -0.5F, 0.4F);
		grill_2.addBox(0.0F, 0.0F, 0.0F, 1, 6, 8, 0.0F);
		wallAdapter = new ModelRenderer(this, 42, 11);
		wallAdapter.setRotationPoint(1.0F, 1.0F, -3.0F);
		wallAdapter.addBox(0.0F, 0.0F, 0.0F, 4, 4, 3, 0.0F);
		grill = new ModelRenderer(this, 0, 0);
		grill.setRotationPoint(-0.5F, 1.0F, 0.5F);
		grill.addBox(0.0F, 0.0F, 0.0F, 6, 1, 8, 0.0F);
		wallPlate = new ModelRenderer(this, 26, 11);
		wallPlate.setRotationPoint(-3.0F, 10.0F, 7.0F);
		wallPlate.addBox(0.0F, 0.0F, 0.0F, 6, 9, 1, 0.0F);
		grill_1 = new ModelRenderer(this, 30, 0);
		grill_1.setRotationPoint(-0.5F, 3.0F, 0.5F);
		grill_1.addBox(0.0F, 0.0F, 0.0F, 6, 1, 8, 0.0F);
		grill_3 = new ModelRenderer(this, 0, 41);
		grill_3.setRotationPoint(1.0F, -0.5F, 0.4F);
		grill_3.addBox(0.0F, 0.0F, 0.0F, 1, 6, 8, 0.0F);
		wallAdapter.addChild(lamp);
		lamp.addChild(grill_2);
		wallPlate.addChild(wallAdapter);
		lamp.addChild(grill);
		lamp.addChild(grill_1);
		lamp.addChild(grill_3);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		wallPlate.render(f5);
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
