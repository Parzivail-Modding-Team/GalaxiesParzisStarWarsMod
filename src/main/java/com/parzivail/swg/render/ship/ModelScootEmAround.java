package com.parzivail.swg.render.ship;

import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * ScootEmAround - Weaston
 * Created using Tabula 4.1.1
 */
public class ModelScootEmAround extends ModelBase
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
	public ModelRenderer Light1;
	public ModelRenderer Light2;
	public ModelRenderer shape1_10;
	public ModelRenderer shape1_11;
	public ModelRenderer shape1_12;
	public ModelRenderer shape1_13;
	public ModelRenderer shape1_14;
	public ModelRenderer shape1_15;
	public ModelRenderer shape1_16;
	public ModelRenderer shape1_17;
	public ModelRenderer shape1_18;
	public ModelRenderer shape1_19;
	public ModelRenderer shape1_20;
	public ModelRenderer shape1_21;

	public ModelScootEmAround()
	{
		textureWidth = 512;
		textureHeight = 512;
		shape1_6 = new ModelRenderer(this, 142, 0);
		shape1_6.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_6.addBox(-6.85F, 5.9F, -28.0F, 2, 4, 56, 0.0F);
		setRotateAngle(shape1_6, 0.0F, 0.0F, 1.0471975511965976F);
		Light2 = new ModelRenderer(this, 272, 0);
		Light2.setRotationPoint(0.0F, 0.0F, 0.0F);
		Light2.addBox(-10.5F, 0.8F, -28.5F, 2, 2, 57, 0.0F);
		shape1_14 = new ModelRenderer(this, 0, 100);
		shape1_14.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_14.addBox(-4.0F, -10.5F, 3.0F, 8, 4, 9, 0.0F);
		shape1_19 = new ModelRenderer(this, 35, 0);
		shape1_19.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_19.addBox(-8.5F, -8.5F, 26.8F, 3, 8, 2, 0.0F);
		shape1_13 = new ModelRenderer(this, 0, 70);
		shape1_13.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_13.addBox(-5.0F, -7.5F, 7.8F, 10, 5, 16, 0.0F);
		shape1_12 = new ModelRenderer(this, 214, 0);
		shape1_12.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_12.addBox(-5.0F, -11.5F, -14.2F, 10, 5, 22, 0.0F);
		shape1_21 = new ModelRenderer(this, 60, 156);
		shape1_21.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_21.addBox(-5.0F, -7.5F, -23.0F, 10, 8, 8, 0.0F);
		Light1 = new ModelRenderer(this, 272, 0);
		Light1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Light1.addBox(8.5F, 0.8F, -28.5F, 2, 2, 57, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(0.0F, 19.0F, 0.0F);
		shape1.addBox(-5.0F, -1.5F, -29.5F, 10, 2, 55, 0.0F);
		shape1_2 = new ModelRenderer(this, 0, 151);
		shape1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_2.addBox(-9.0F, -9.0F, -28.0F, 4, 13, 56, 0.0F);
		shape1_11 = new ModelRenderer(this, 142, 154);
		shape1_11.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_11.addBox(-11.0F, 3.7F, -27.0F, 5, 1, 54, 0.0F);
		shape1_3 = new ModelRenderer(this, 0, 230);
		shape1_3.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_3.addBox(9.0F, -1.0F, -28.0F, 3, 5, 56, 0.0F);
		shape1_8 = new ModelRenderer(this, 132, 0);
		shape1_8.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_8.addBox(5.5F, -8.5F, -29.2F, 3, 8, 2, 0.0F);
		shape1_17 = new ModelRenderer(this, 0, 34);
		shape1_17.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_17.addBox(4.0F, -15.5F, -23.2F, 1, 1, 8, 0.0F);
		shape1_1 = new ModelRenderer(this, 0, 75);
		shape1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_1.addBox(5.0F, -9.0F, -28.0F, 4, 13, 56, 0.0F);
		shape1_9 = new ModelRenderer(this, 158, 0);
		shape1_9.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_9.addBox(-8.5F, -8.5F, -29.2F, 3, 8, 2, 0.0F);
		shape1_20 = new ModelRenderer(this, 0, 0);
		shape1_20.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_20.addBox(-4.5F, -6.5F, 26.8F, 9, 8, 2, 0.0F);
		shape1_10 = new ModelRenderer(this, 140, 70);
		shape1_10.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_10.addBox(6.0F, 3.7F, -27.0F, 5, 1, 54, 0.0F);
		shape1_18 = new ModelRenderer(this, 30, 33);
		shape1_18.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_18.addBox(5.5F, -8.5F, 26.8F, 3, 8, 2, 0.0F);
		shape1_4 = new ModelRenderer(this, 0, 303);
		shape1_4.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_4.addBox(4.85F, 5.9F, -28.0F, 2, 4, 56, 0.0F);
		setRotateAngle(shape1_4, 0.0F, 0.0F, -1.0471975511965976F);
		shape1_7 = new ModelRenderer(this, 94, 0);
		shape1_7.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_7.addBox(-5.0F, -9.0F, 24.0F, 10, 13, 4, 0.0F);
		shape1_5 = new ModelRenderer(this, 0, 378);
		shape1_5.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_5.addBox(-12.0F, -1.0F, -28.0F, 3, 5, 56, 0.0F);
		shape1_16 = new ModelRenderer(this, 0, 182);
		shape1_16.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_16.addBox(-5.0F, -15.5F, -23.2F, 1, 1, 8, 0.0F);
		shape1_15 = new ModelRenderer(this, 0, 156);
		shape1_15.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_15.addBox(-5.0F, -15.5F, -15.2F, 10, 16, 1, 0.0F);
		shape1.addChild(shape1_6);
		shape1.addChild(shape1_14);
		shape1.addChild(shape1_19);
		shape1.addChild(shape1_13);
		shape1.addChild(shape1_12);
		shape1.addChild(shape1_21);
		shape1.addChild(shape1_2);
		shape1.addChild(shape1_11);
		shape1.addChild(shape1_3);
		shape1.addChild(shape1_8);
		shape1.addChild(shape1_17);
		shape1.addChild(shape1_1);
		shape1.addChild(shape1_9);
		shape1.addChild(shape1_20);
		shape1.addChild(shape1_10);
		shape1.addChild(shape1_18);
		shape1.addChild(shape1_4);
		shape1.addChild(shape1_7);
		shape1.addChild(shape1_5);
		shape1.addChild(shape1_16);
		shape1.addChild(shape1_15);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		shape1_21.isHidden = true;

		shape1.render(f5);

		if (entity != null)
		{
			GL11.glPushMatrix();
			ShaderHelper.setColor(1, 0.2f, 0.2f, 1);
			ShaderHelper.useShader(ShaderHelper.glowSolid);
			GL11.glTranslatef(0, 1.187f, 0);
			Light2.render(f5);
			Light1.render(f5);
			ShaderHelper.releaseShader();
			GL11.glPopMatrix();
		}
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
