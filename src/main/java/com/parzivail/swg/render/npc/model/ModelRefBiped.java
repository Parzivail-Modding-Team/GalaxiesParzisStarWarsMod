package com.parzivail.swg.render.npc.model;

import com.parzivail.swg.render.player.PModelBipedBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * ModelBiped - Either Mojang or a mod author
 * Created using Tabula 4.1.1
 */
public class ModelRefBiped extends PModelBipedBase
{
	public ModelRenderer headgear;
	public ModelRenderer armRight;
	public ModelRenderer legLeft;
	public ModelRenderer head;
	public ModelRenderer torso;
	public ModelRenderer armLeft;
	public ModelRenderer legRight;
	public ModelRenderer ears;

	public ModelRefBiped()
	{
		textureWidth = 64;
		textureHeight = 32;
		ears = new ModelRenderer(this, 24, 0);
		ears.setRotationPoint(0.0F, 0.0F, 0.0F);
		ears.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, 0.0F);
		armLeft = new ModelRenderer(this, 40, 16);
		armLeft.mirror = true;
		armLeft.setRotationPoint(5.0F, 2.0F, 0.0F);
		armLeft.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		torso = new ModelRenderer(this, 16, 16);
		torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		legRight = new ModelRenderer(this, 0, 16);
		legRight.setRotationPoint(-1.9F, 12.0F, 0.0F);
		legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		legLeft = new ModelRenderer(this, 0, 16);
		legLeft.mirror = true;
		legLeft.setRotationPoint(1.9F, 12.0F, 0.0F);
		legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		head = new ModelRenderer(this, 0, 0);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		headgear = new ModelRenderer(this, 32, 0);
		headgear.setRotationPoint(0.0F, 0.0F, 0.0F);
		headgear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
		armRight = new ModelRenderer(this, 40, 16);
		armRight.setRotationPoint(-5.0F, 2.0F, 0.0F);
		armRight.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
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

	@Override
	public ModelRenderer getHead()
	{
		return head;
	}

	@Override
	public ModelRenderer getBody()
	{
		return torso;
	}

	@Override
	public ModelRenderer getArmLeft()
	{
		return armLeft;
	}

	@Override
	public ModelRenderer getArmRight()
	{
		return armRight;
	}

	@Override
	public ModelRenderer getLegLeft()
	{
		return legLeft;
	}

	@Override
	public ModelRenderer getLegRight()
	{
		return legRight;
	}

	@Override
	public ModelRenderer getHeadgear()
	{
		return headgear;
	}
}
