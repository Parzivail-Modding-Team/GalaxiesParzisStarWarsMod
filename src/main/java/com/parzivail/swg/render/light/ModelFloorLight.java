package com.parzivail.swg.render.light;

import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * FloorLight - Weaston
 * Created using Tabula 4.1.1
 */
public class ModelFloorLight extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer shape1_1;
	public ModelRenderer shape1_2;
	public ModelRenderer shape1_3;
	public ModelRenderer shape1_4;
	public ModelRenderer shape1_5;
	public ModelRenderer shape1_6;
	public ModelRenderer shape1_7;
	public ModelRenderer shape1_8;
	public ModelRenderer shape1_9;
	public ModelRenderer shape1_10;
	public ModelRenderer Light;

	public ModelFloorLight()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(0.0F, 23.5F, 0.0F);
		this.shape1.addBox(-3.5F, -0.5F, -5.15F, 7, 1, 10, 0.0F);
		this.shape1_8 = new ModelRenderer(this, 39, 22);
		this.shape1_8.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_8.addBox(-3.0F, -4.3F, 2.0F, 6, 1, 1, 0.0F);
		this.setRotateAngle(shape1_8, 1.0471975511965976F, 0.0F, 0.0F);
		this.Light = new ModelRenderer(this, 35, 48);
		this.Light.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Light.addBox(-2.0F, -4.2F, -1.0F, 4, 1, 3, 0.0F);
		this.setRotateAngle(Light, 1.0471975511965976F, 0.0F, 0.0F);
		this.shape1_1 = new ModelRenderer(this, 0, 22);
		this.shape1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_1.addBox(-3.0F, -4.3F, -2.0F, 6, 1, 1, 0.0F);
		this.setRotateAngle(shape1_1, 1.0471975511965976F, 0.0F, 0.0F);
		this.shape1_5 = new ModelRenderer(this, 91, 0);
		this.shape1_5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_5.addBox(-3.0F, -2.5F, -3.02F, 6, 1, 4, 0.0F);
		this.shape1_2 = new ModelRenderer(this, 0, 45);
		this.shape1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_2.addBox(-3.0F, -4.74F, -2.22F, 6, 1, 1, 0.0F);
		this.shape1_9 = new ModelRenderer(this, 66, 22);
		this.shape1_9.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_9.addBox(-3.0F, -4.3F, -2.0F, 1, 1, 5, 0.0F);
		this.setRotateAngle(shape1_9, 1.0471975511965976F, 0.0F, 0.0F);
		this.shape1_10 = new ModelRenderer(this, 96, 22);
		this.shape1_10.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_10.addBox(2.0F, -4.3F, -2.0F, 1, 1, 5, 0.0F);
		this.setRotateAngle(shape1_10, 1.0471975511965976F, 0.0F, 0.0F);
		this.shape1_7 = new ModelRenderer(this, 0, 88);
		this.shape1_7.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_7.addBox(-3.0F, -2.47F, -4.4F, 6, 1, 7, 0.0F);
		this.setRotateAngle(shape1_7, -0.6632251157578453F, 0.0F, 0.0F);
		this.shape1_3 = new ModelRenderer(this, 0, 67);
		this.shape1_3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_3.addBox(-3.0F, -2.98F, -3.9F, 6, 1, 7, 0.0F);
		this.setRotateAngle(shape1_3, -0.6632251157578453F, 0.0F, 0.0F);
		this.shape1_4 = new ModelRenderer(this, 49, 0);
		this.shape1_4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_4.addBox(-3.0F, -1.5F, -3.72F, 6, 1, 6, 0.0F);
		this.shape1_6 = new ModelRenderer(this, 135, 0);
		this.shape1_6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1_6.addBox(-3.0F, -3.5F, -2.52F, 6, 1, 2, 0.0F);
		this.shape1.addChild(this.shape1_8);
		this.shape1.addChild(this.Light);
		this.shape1.addChild(this.shape1_1);
		this.shape1.addChild(this.shape1_5);
		this.shape1.addChild(this.shape1_2);
		this.shape1.addChild(this.shape1_9);
		this.shape1.addChild(this.shape1_10);
		this.shape1.addChild(this.shape1_7);
		this.shape1.addChild(this.shape1_3);
		this.shape1.addChild(this.shape1_4);
		this.shape1.addChild(this.shape1_6);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.shape1.render(f5);

		GL11.glTranslatef(0, 1.172f, -0.005f);
		ShaderHelper.setColor(1, 1, 1, 1);
		ShaderHelper.useShader(ShaderHelper.glowSolid);
		this.Light.render(f5);
		ShaderHelper.releaseShader();
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
