package com.parzivail.swg.render.npc.model;

import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelPlayer extends ModelBiped
{
	private final ModelRenderer field_178729_w;
	private final ModelRenderer field_178736_x;
	private final boolean field_178735_y;
	public ModelRenderer bipedLeftArmwear;
	public ModelRenderer bipedRightArmwear;
	public ModelRenderer bipedLeftLegwear;
	public ModelRenderer bipedRightLegwear;
	public ModelRenderer bipedBodyWear;

	public ModelPlayer(float scaleFactor, boolean smallArms)
	{
		super(scaleFactor, 0.0F, 64, 64);
		field_178735_y = smallArms;
		field_178736_x = new ModelRenderer(this, 24, 0);
		field_178736_x.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, scaleFactor);
		field_178729_w = new ModelRenderer(this, 0, 0);
		field_178729_w.setTextureSize(64, 32);
		field_178729_w.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, scaleFactor);

		if (smallArms)
		{
			bipedLeftArm = new ModelRenderer(this, 32, 48);
			bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scaleFactor);
			bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
			bipedRightArm = new ModelRenderer(this, 40, 16);
			bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scaleFactor);
			bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			bipedLeftArmwear = new ModelRenderer(this, 48, 48);
			bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scaleFactor + 0.25F);
			bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
			bipedRightArmwear = new ModelRenderer(this, 40, 32);
			bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scaleFactor + 0.25F);
			bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
		}
		else
		{
			bipedLeftArm = new ModelRenderer(this, 32, 48);
			bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scaleFactor);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedLeftArmwear = new ModelRenderer(this, 48, 48);
			bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scaleFactor + 0.25F);
			bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedRightArmwear = new ModelRenderer(this, 40, 32);
			bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scaleFactor + 0.25F);
			bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
		}

		bipedLeftLeg = new ModelRenderer(this, 16, 48);
		bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scaleFactor);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLegwear = new ModelRenderer(this, 0, 48);
		bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scaleFactor + 0.25F);
		bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedRightLegwear = new ModelRenderer(this, 0, 32);
		bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scaleFactor + 0.25F);
		bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedBodyWear = new ModelRenderer(this, 16, 32);
		bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scaleFactor + 0.25F);
		bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
	{
		super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
		GL.PushMatrix();

		if (isChild)
		{
			float f6 = 2.0F;
			GL.Scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
			GL.Translate(0.0F, 24.0F * p_78088_7_, 0.0F);
			bipedLeftLegwear.render(p_78088_7_);
			bipedRightLegwear.render(p_78088_7_);
			bipedLeftArmwear.render(p_78088_7_);
			bipedRightArmwear.render(p_78088_7_);
			bipedBodyWear.render(p_78088_7_);
		}
		else
		{
			if (p_78088_1_.isSneaking())
			{
				GL.Translate(0.0F, 0.2F, 0.0F);
			}

			bipedLeftLegwear.render(p_78088_7_);
			bipedRightLegwear.render(p_78088_7_);
			bipedLeftArmwear.render(p_78088_7_);
			bipedRightArmwear.render(p_78088_7_);
			bipedBodyWear.render(p_78088_7_);
		}

		GL.PopMatrix();
	}

	public void func_178727_b(float p_178727_1_)
	{
		copyModelAngles(bipedHead, field_178736_x);
		field_178736_x.rotationPointX = 0.0F;
		field_178736_x.rotationPointY = 0.0F;
		field_178736_x.render(p_178727_1_);
	}

	public void func_178728_c(float p_178728_1_)
	{
		field_178729_w.render(p_178728_1_);
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	 * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	 * "far" arms and legs can swing at most.
	 */
	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
	{
		super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
		copyModelAngles(bipedLeftLeg, bipedLeftLegwear);
		copyModelAngles(bipedRightLeg, bipedRightLegwear);
		copyModelAngles(bipedLeftArm, bipedLeftArmwear);
		copyModelAngles(bipedRightArm, bipedRightArmwear);
		copyModelAngles(bipedBody, bipedBodyWear);

		if (p_78087_7_.isSneaking())
		{
			field_178729_w.rotationPointY = 2.0F;
		}
		else
		{
			field_178729_w.rotationPointY = 0.0F;
		}
	}

	public void func_178725_a()
	{
		bipedRightArm.render(0.0625F);
		bipedRightArmwear.render(0.0625F);
	}

	public void func_178726_b()
	{
		bipedLeftArm.render(0.0625F);
		bipedLeftArmwear.render(0.0625F);
	}

	public void postRenderArm(float p_178718_1_)
	{
		if (field_178735_y)
		{
			++bipedRightArm.rotationPointX;
			bipedRightArm.postRender(p_178718_1_);
			--bipedRightArm.rotationPointX;
		}
		else
		{
			bipedRightArm.postRender(p_178718_1_);
		}
	}

	void copyModelAngles(ModelRenderer source, ModelRenderer dest)
	{
		dest.rotateAngleX = source.rotateAngleX;
		dest.rotateAngleY = source.rotateAngleY;
		dest.rotateAngleZ = source.rotateAngleZ;
		dest.rotationPointX = source.rotationPointX;
		dest.rotationPointY = source.rotationPointY;
		dest.rotationPointZ = source.rotationPointZ;
	}
}
