package com.parzivail.swg.render.npc.model;

import com.parzivail.swg.Resources;
import com.parzivail.swg.render.player.PModelBipedBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * ModelBothanF - Sindavar
 * Created using Tabula 4.1.1
 */
public class ModelBothanF extends PModelBipedBase
{
	private static final ResourceLocation texture = Resources.location("textures/species/bothan_f.png");

	public ModelRenderer Chest;
	public ModelRenderer Torso;
	public ModelRenderer Head;
	public ModelRenderer ArmR;
	public ModelRenderer LegL;
	public ModelRenderer ArmL;
	public ModelRenderer LegR;
	public ModelRenderer SnoutLower;
	public ModelRenderer EarR;
	public ModelRenderer EarL;
	public ModelRenderer SnoutUpper;
	public ModelRenderer BeardL;
	public ModelRenderer BeardR;
	public ModelRenderer EarTipR;
	public ModelRenderer EarTipL;

	public ModelBothanF()
	{
		textureWidth = 64;
		textureHeight = 32;
		Torso = new ModelRenderer(this, 16, 16);
		Torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		Torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		LegR = new ModelRenderer(this, 0, 16);
		LegR.setRotationPoint(-1.9F, 12.0F, 0.1F);
		LegR.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		ArmL = new ModelRenderer(this, 40, 16);
		ArmL.mirror = true;
		ArmL.setRotationPoint(5.0F, 2.0F, -0.0F);
		ArmL.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		setRotateAngle(ArmL, 0.0F, 0.0F, -0.10000736613927509F);
		ArmR = new ModelRenderer(this, 40, 16);
		ArmR.setRotationPoint(-5.0F, 2.0F, 0.0F);
		ArmR.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		setRotateAngle(ArmR, 0.0F, 0.0F, 0.10000736613927509F);
		Chest = new ModelRenderer(this, 50, 11);
		Chest.setRotationPoint(-3.0F, 2.0F, -2.9F);
		Chest.addBox(0.0F, 0.0F, 0.0F, 6, 3, 1, 0.0F);
		SnoutUpper = new ModelRenderer(this, 26, 0);
		SnoutUpper.setRotationPoint(0.0F, -3.4F, 1.0F);
		SnoutUpper.addBox(-1.5F, -1.0F, -6.0F, 3, 2, 6, 0.0F);
		setRotateAngle(SnoutUpper, 0.3975860036043083F, 0.0F, 0.0F);
		Head = new ModelRenderer(this, 0, 0);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		BeardR = new ModelRenderer(this, 50, 26);
		BeardR.mirror = true;
		BeardR.setRotationPoint(-2.4F, -0.5F, 0.8F);
		BeardR.addBox(-4.0F, 0.0F, 0.0F, 4, 0, 6, 0.0F);
		setRotateAngle(BeardR, -1.5707963267948966F, -1.7840755613886037F, 0.08848819307611251F);
		EarTipL = new ModelRenderer(this, 38, 10);
		EarTipL.setRotationPoint(0.0F, 0.0F, 1.7F);
		EarTipL.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 4, 0.0F);
		EarR = new ModelRenderer(this, 32, 8);
		EarR.mirror = true;
		EarR.setRotationPoint(-2.7F, -7.0F, 0.0F);
		EarR.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 4, 0.0F);
		setRotateAngle(EarR, 0.6724753607934152F, -0.7305948248848263F, 0.0F);
		SnoutLower = new ModelRenderer(this, 45, 0);
		SnoutLower.setRotationPoint(0.0F, -2.5F, -4.0F);
		SnoutLower.addBox(-2.0F, -2.0F, -5.0F, 4, 4, 5, 0.0F);
		EarL = new ModelRenderer(this, 32, 8);
		EarL.setRotationPoint(2.7F, -7.0F, 0.0F);
		EarL.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 4, 0.0F);
		setRotateAngle(EarL, 0.6724753607934152F, 0.7305948248848263F, 0.0F);
		LegL = new ModelRenderer(this, 0, 16);
		LegL.mirror = true;
		LegL.setRotationPoint(1.9F, 12.0F, 0.1F);
		LegL.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		BeardL = new ModelRenderer(this, 50, 26);
		BeardL.setRotationPoint(2.4F, -0.5F, 0.8F);
		BeardL.addBox(0.0F, 0.0F, 0.0F, 4, 0, 6, 0.0F);
		setRotateAngle(BeardL, -1.5707963267948966F, 1.7840755613886037F, -0.08848819307611251F);
		EarTipR = new ModelRenderer(this, 38, 10);
		EarTipR.setRotationPoint(0.0F, 0.0F, 1.7F);
		EarTipR.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 4, 0.0F);
		SnoutLower.addChild(SnoutUpper);
		SnoutLower.addChild(BeardR);
		EarL.addChild(EarTipL);
		Head.addChild(EarR);
		Head.addChild(SnoutLower);
		Head.addChild(EarL);
		SnoutLower.addChild(BeardL);
		EarR.addChild(EarTipR);
	}

	@Override
	public ModelRenderer getHead()
	{
		return Head;
	}

	@Override
	public ModelRenderer getBody()
	{
		return Torso;
	}

	@Override
	public ModelRenderer getArmLeft()
	{
		return ArmL;
	}

	@Override
	public ModelRenderer getArmRight()
	{
		return ArmR;
	}

	@Override
	public ModelRenderer getLegLeft()
	{
		return LegL;
	}

	@Override
	public ModelRenderer getLegRight()
	{
		return LegR;
	}

	@Override
	public ModelRenderer getHeadgear()
	{
		return null;
	}

	@Override
	public ResourceLocation getBaseTexture(AbstractClientPlayer player)
	{
		return texture;
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
