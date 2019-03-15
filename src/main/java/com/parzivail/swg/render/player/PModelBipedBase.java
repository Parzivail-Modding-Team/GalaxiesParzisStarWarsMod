package com.parzivail.swg.render.player;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public abstract class PModelBipedBase extends ModelBase
{
	/**
	 * Records whether the model should be rendered holding an item in the left hand, and if that item is a block.
	 */
	public int heldItemLeft;
	/**
	 * Records whether the model should be rendered holding an item in the right hand, and if that item is a block.
	 */
	public int heldItemRight;
	public boolean isSneak;
	/**
	 * Records whether the model should be rendered aiming a bow.
	 */
	public boolean aimedBow;

	public abstract ModelRenderer getHead();

	public abstract ModelRenderer getBody();

	public abstract ModelRenderer getArmLeft();

	public abstract ModelRenderer getArmRight();

	public abstract ModelRenderer getLegLeft();

	public abstract ModelRenderer getLegRight();

	public abstract ModelRenderer getHeadgear();

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
	{
		setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

		if (isChild)
		{
			float f6 = 2.0F;
			GL11.glPushMatrix();
			GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
			GL11.glTranslatef(0.0F, 16.0F * p_78088_7_, 0.0F);
			getHead().render(p_78088_7_);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
			GL11.glTranslatef(0.0F, 24.0F * p_78088_7_, 0.0F);
			getBody().render(p_78088_7_);
			getArmRight().render(p_78088_7_);
			getArmLeft().render(p_78088_7_);
			getLegRight().render(p_78088_7_);
			getLegLeft().render(p_78088_7_);
			getHeadgear().render(p_78088_7_);
			GL11.glPopMatrix();
		}
		else
		{
			getHead().render(p_78088_7_);
			getBody().render(p_78088_7_);
			getArmRight().render(p_78088_7_);
			getArmLeft().render(p_78088_7_);
			getLegRight().render(p_78088_7_);
			getLegLeft().render(p_78088_7_);
			getHeadgear().render(p_78088_7_);
		}
	}

	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
	{
		getHead().rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
		getHead().rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		getHeadgear().rotateAngleY = getHead().rotateAngleY;
		getHeadgear().rotateAngleX = getHead().rotateAngleX;
		getArmRight().rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 2.0F * p_78087_2_ * 0.5F;
		getArmLeft().rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 2.0F * p_78087_2_ * 0.5F;
		getArmRight().rotateAngleZ = 0.0F;
		getArmLeft().rotateAngleZ = 0.0F;
		getLegRight().rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
		getLegLeft().rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
		getLegRight().rotateAngleY = 0.0F;
		getLegLeft().rotateAngleY = 0.0F;

		if (isRiding)
		{
			getArmRight().rotateAngleX += -((float)Math.PI / 5F);
			getArmLeft().rotateAngleX += -((float)Math.PI / 5F);
			getLegRight().rotateAngleX = -((float)Math.PI * 2F / 5F);
			getLegLeft().rotateAngleX = -((float)Math.PI * 2F / 5F);
			getLegRight().rotateAngleY = ((float)Math.PI / 10F);
			getLegLeft().rotateAngleY = -((float)Math.PI / 10F);
		}

		if (heldItemLeft != 0)
		{
			getArmLeft().rotateAngleX = getArmLeft().rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)heldItemLeft;
		}

		if (heldItemRight != 0)
		{
			getArmRight().rotateAngleX = getArmRight().rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)heldItemRight;
		}

		getArmRight().rotateAngleY = 0.0F;
		getArmLeft().rotateAngleY = 0.0F;
		float f6;
		float f7;

		if (swingProgress > -9990.0F)
		{
			f6 = swingProgress;
			getBody().rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
			getArmRight().rotationPointZ = MathHelper.sin(getBody().rotateAngleY) * 5.0F;
			getArmRight().rotationPointX = -MathHelper.cos(getBody().rotateAngleY) * 5.0F;
			getArmLeft().rotationPointZ = -MathHelper.sin(getBody().rotateAngleY) * 5.0F;
			getArmLeft().rotationPointX = MathHelper.cos(getBody().rotateAngleY) * 5.0F;
			getArmRight().rotateAngleY += getBody().rotateAngleY;
			getArmLeft().rotateAngleY += getBody().rotateAngleY;
			getArmLeft().rotateAngleX += getBody().rotateAngleY;
			f6 = 1.0F - swingProgress;
			f6 *= f6;
			f6 *= f6;
			f6 = 1.0F - f6;
			f7 = MathHelper.sin(f6 * (float)Math.PI);
			float f8 = MathHelper.sin(swingProgress * (float)Math.PI) * -(getHead().rotateAngleX - 0.7F) * 0.75F;
			getArmRight().rotateAngleX = (float)((double)getArmRight().rotateAngleX - ((double)f7 * 1.2D + (double)f8));
			getArmRight().rotateAngleY += getBody().rotateAngleY * 2.0F;
			getArmRight().rotateAngleZ = MathHelper.sin(swingProgress * (float)Math.PI) * -0.4F;
		}

		if (isSneak)
		{
			getBody().rotateAngleX = 0.5F;
			getArmRight().rotateAngleX += 0.4F;
			getArmLeft().rotateAngleX += 0.4F;
			getLegRight().rotationPointZ = 4.0F;
			getLegLeft().rotationPointZ = 4.0F;
			getLegRight().rotationPointY = 9.0F;
			getLegLeft().rotationPointY = 9.0F;
			getHead().rotationPointY = 1.0F;
			getHeadgear().rotationPointY = 1.0F;
		}
		else
		{
			getBody().rotateAngleX = 0.0F;
			getLegRight().rotationPointZ = 0.1F;
			getLegLeft().rotationPointZ = 0.1F;
			getLegRight().rotationPointY = 12.0F;
			getLegLeft().rotationPointY = 12.0F;
			getHead().rotationPointY = 0.0F;
			getHeadgear().rotationPointY = 0.0F;
		}

		getArmRight().rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
		getArmLeft().rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
		getArmRight().rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
		getArmLeft().rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;

		if (aimedBow)
		{
			f6 = 0.0F;
			f7 = 0.0F;
			getArmRight().rotateAngleZ = 0.0F;
			getArmLeft().rotateAngleZ = 0.0F;
			getArmRight().rotateAngleY = -(0.1F - f6 * 0.6F) + getHead().rotateAngleY;
			getArmLeft().rotateAngleY = 0.1F - f6 * 0.6F + getHead().rotateAngleY + 0.4F;
			getArmRight().rotateAngleX = -((float)Math.PI / 2F) + getHead().rotateAngleX;
			getArmLeft().rotateAngleX = -((float)Math.PI / 2F) + getHead().rotateAngleX;
			getArmRight().rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
			getArmLeft().rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
			getArmRight().rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
			getArmLeft().rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
			getArmRight().rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
			getArmLeft().rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
		}
	}
}
