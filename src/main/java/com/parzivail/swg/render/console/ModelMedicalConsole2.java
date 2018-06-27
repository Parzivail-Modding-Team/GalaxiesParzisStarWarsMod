package com.parzivail.swg.render.console;

import com.parzivail.swg.tile.console.TileMedicalConsole2;
import com.parzivail.util.entity.EntityTilePassthrough;
import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * MedicalConsole2 - Weaston
 * Created using Tabula 4.1.1
 */
public class ModelMedicalConsole2 extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer Light;
	public ModelRenderer Light_1;
	public ModelRenderer Light_2;
	public ModelRenderer Light_3;
	public ModelRenderer Light_4;
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
	public ModelRenderer shape1_11;
	public ModelRenderer shape1_12;
	public ModelRenderer shape1_13;
	public ModelRenderer shape1_14;
	public ModelRenderer shape1_15;
	public ModelRenderer shape1_16;
	public ModelRenderer shape1_17;

	public ModelMedicalConsole2()
	{
		textureWidth = 512;
		textureHeight = 512;
		Light_3 = new ModelRenderer(this, 127, 42);
		Light_3.setRotationPoint(0.0F, -5.8F, 0.0F);
		Light_3.addBox(2.5F, -1.5F, 0.2F, 1, 1, 2, 0.0F);
		setRotateAngle(Light_3, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_3 = new ModelRenderer(this, 0, 138);
		shape1_3.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_3.addBox(-8.0F, -26.9F, 4.5F, 16, 39, 2, 0.0F);
		shape1_2 = new ModelRenderer(this, 0, 112);
		shape1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_2.addBox(-8.0F, -0.81F, -1.51F, 16, 13, 2, 0.0F);
		Light_2 = new ModelRenderer(this, 110, 46);
		Light_2.setRotationPoint(0.0F, -5.5F, 0.0F);
		Light_2.addBox(-5.5F, -14.5F, 0.2F, 1, 1, 2, 0.0F);
		setRotateAngle(Light_2, -0.20943951023931953F, 0.0F, 0.0F);
		Light_1 = new ModelRenderer(this, 80, 46);
		Light_1.setRotationPoint(0.0F, -5.8F, 0.0F);
		Light_1.addBox(-1.0F, -7.5F, 0.2F, 1, 1, 2, 0.0F);
		setRotateAngle(Light_1, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_8 = new ModelRenderer(this, 97, 62);
		shape1_8.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_8.addBox(-7.0F, 7.5F, 1.6F, 9, 5, 2, 0.0F);
		setRotateAngle(shape1_8, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_1 = new ModelRenderer(this, 0, 62);
		shape1_1.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_1.addBox(-8.0F, -18.86F, 2.1F, 16, 36, 2, 0.0F);
		setRotateAngle(shape1_1, -0.20943951023931953F, 0.0F, 0.0F);
		Light_4 = new ModelRenderer(this, 155, 41);
		Light_4.setRotationPoint(0.0F, -5.8F, 0.0F);
		Light_4.addBox(4.5F, -1.5F, 0.2F, 1, 1, 2, 0.0F);
		setRotateAngle(Light_4, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_15 = new ModelRenderer(this, 80, 108);
		shape1_15.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_15.addBox(-6.0F, -2.5F, 1.4F, 2, 4, 2, 0.0F);
		setRotateAngle(shape1_15, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_13 = new ModelRenderer(this, 115, 81);
		shape1_13.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_13.addBox(-6.0F, -15.0F, 1.4F, 2, 3, 2, 0.0F);
		setRotateAngle(shape1_13, -0.20943951023931953F, 0.0F, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(0.0F, 12.0F, -1.0F);
		shape1.addBox(-8.0F, -36.0F, 6.0F, 16, 48, 3, 0.0F);
		shape1_4 = new ModelRenderer(this, 0, 198);
		shape1_4.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_4.addBox(-8.0F, -7.0F, 0.4F, 16, 19, 4, 0.0F);
		shape1_16 = new ModelRenderer(this, 100, 105);
		shape1_16.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_16.addBox(-3.0F, -2.5F, 1.4F, 4, 4, 2, 0.0F);
		setRotateAngle(shape1_16, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_14 = new ModelRenderer(this, 50, 105);
		shape1_14.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_14.addBox(-4.0F, -9.5F, 1.4F, 8, 4, 2, 0.0F);
		setRotateAngle(shape1_14, -0.20943951023931953F, 0.0F, 0.0F);
		Light = new ModelRenderer(this, 60, 45);
		Light.setRotationPoint(0.0F, -5.8F, 0.0F);
		Light.addBox(-3.0F, -7.5F, 0.2F, 1, 1, 2, 0.0F);
		setRotateAngle(Light, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_7 = new ModelRenderer(this, 52, 62);
		shape1_7.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_7.addBox(-7.0F, 14.0F, 1.6F, 14, 2, 2, 0.0F);
		setRotateAngle(shape1_7, -0.20943951023931953F, 0.006283185307179587F, 0.0F);
		shape1_9 = new ModelRenderer(this, 128, 62);
		shape1_9.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_9.addBox(-7.0F, -17.5F, 1.9F, 14, 6, 2, 0.0F);
		setRotateAngle(shape1_9, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_10 = new ModelRenderer(this, 170, 62);
		shape1_10.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_10.addBox(-7.0F, -10.5F, 1.9F, 14, 6, 2, 0.0F);
		setRotateAngle(shape1_10, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_5 = new ModelRenderer(this, 48, 0);
		shape1_5.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_5.addBox(-8.0F, -20.0F, 3.5F, 16, 32, 2, 0.0F);
		shape1_17 = new ModelRenderer(this, 122, 106);
		shape1_17.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_17.addBox(2.0F, -2.5F, 1.4F, 4, 3, 2, 0.0F);
		setRotateAngle(shape1_17, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_6 = new ModelRenderer(this, 98, 0);
		shape1_6.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1_6.addBox(-8.0F, -15.3F, 1.7F, 16, 10, 3, 0.0F);
		shape1_11 = new ModelRenderer(this, 47, 81);
		shape1_11.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_11.addBox(-7.0F, -3.5F, 1.9F, 14, 6, 2, 0.0F);
		setRotateAngle(shape1_11, -0.20943951023931953F, 0.0F, 0.0F);
		shape1_12 = new ModelRenderer(this, 90, 81);
		shape1_12.setRotationPoint(0.0F, -18.0F, 0.0F);
		shape1_12.addBox(-2.0F, -17.0F, 1.4F, 4, 5, 2, 0.0F);
		setRotateAngle(shape1_12, -0.20943951023931953F, 0.0F, 0.0F);
		shape1.addChild(shape1_3);
		shape1.addChild(shape1_2);
		shape1.addChild(shape1_8);
		shape1.addChild(shape1_1);
		shape1.addChild(shape1_15);
		shape1.addChild(shape1_13);
		shape1.addChild(shape1_4);
		shape1.addChild(shape1_16);
		shape1.addChild(shape1_14);
		shape1.addChild(shape1_7);
		shape1.addChild(shape1_9);
		shape1.addChild(shape1_10);
		shape1.addChild(shape1_5);
		shape1.addChild(shape1_17);
		shape1.addChild(shape1_6);
		shape1.addChild(shape1_11);
		shape1.addChild(shape1_12);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		GL11.glPushMatrix();
		if (entity instanceof EntityTilePassthrough && ((EntityTilePassthrough)entity).tileEntity instanceof TileMedicalConsole2)
		{
			TileMedicalConsole2 console = (TileMedicalConsole2)((EntityTilePassthrough)entity).tileEntity;

			ShaderHelper.setColor(console.color1);
			ShaderHelper.useShader(ShaderHelper.glowSolid);
			Light.render(f5);
			ShaderHelper.releaseShader();

			ShaderHelper.setColor(console.color2);
			ShaderHelper.useShader(ShaderHelper.glowSolid);
			Light_1.render(f5);
			ShaderHelper.releaseShader();

			ShaderHelper.setColor(console.color3);
			ShaderHelper.useShader(ShaderHelper.glowSolid);
			Light_2.render(f5);
			ShaderHelper.releaseShader();

			ShaderHelper.setColor(console.color4);
			ShaderHelper.useShader(ShaderHelper.glowSolid);
			Light_3.render(f5);
			ShaderHelper.releaseShader();

			ShaderHelper.setColor(console.color5);
			ShaderHelper.useShader(ShaderHelper.glowSolid);
			Light_4.render(f5);
			ShaderHelper.releaseShader();
		}
		GL11.glPopMatrix();

		shape1.render(f5);
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
