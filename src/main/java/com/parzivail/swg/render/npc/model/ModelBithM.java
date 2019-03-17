package com.parzivail.swg.render.npc.model;

import com.parzivail.swg.Resources;
import com.parzivail.swg.render.player.PModelBipedBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * ModelBithM - Sindavar
 * Created using Tabula 4.1.1
 */
public class ModelBithM extends PModelBipedBase
{
	private static final ResourceLocation texture = Resources.location("textures/species/bith_m.png");

	public ModelRenderer Torso;
	public ModelRenderer Head;
	public ModelRenderer LegR;
	public ModelRenderer LegL;
	public ModelRenderer ArmR;
	public ModelRenderer ArmL;
	public ModelRenderer HeadExtension;
	public ModelRenderer FrontalR;
	public ModelRenderer FrontalL;

	public ModelBithM()
	{
		textureWidth = 64;
		textureHeight = 60;
		Head = new ModelRenderer(this, 0, 0);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		ArmL = new ModelRenderer(this, 40, 16);
		ArmL.mirror = true;
		ArmL.setRotationPoint(5.0F, 2.0F, -0.0F);
		ArmL.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		setRotateAngle(ArmL, 0.0F, 0.0F, -0.10000736613927509F);
		FrontalR = new ModelRenderer(this, 47, 1);
		FrontalR.setRotationPoint(0.3F, 0.2F, -1.1F);
		FrontalR.addBox(0.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
		LegL = new ModelRenderer(this, 0, 16);
		LegL.mirror = true;
		LegL.setRotationPoint(1.9F, 12.0F, 0.1F);
		LegL.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		ArmR = new ModelRenderer(this, 40, 16);
		ArmR.setRotationPoint(-5.0F, 2.0F, 0.0F);
		ArmR.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		setRotateAngle(ArmR, 0.0F, 0.0F, 0.10000736613927509F);
		HeadExtension = new ModelRenderer(this, 0, 34);
		HeadExtension.setRotationPoint(-4.5F, -10.4F, -3.2F);
		HeadExtension.addBox(0.0F, 0.0F, 0.0F, 9, 9, 8, 0.0F);
		LegR = new ModelRenderer(this, 0, 16);
		LegR.setRotationPoint(-1.9F, 12.0F, 0.1F);
		LegR.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		Torso = new ModelRenderer(this, 16, 16);
		Torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		Torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		FrontalL = new ModelRenderer(this, 47, 1);
		FrontalL.setRotationPoint(4.7F, 0.2F, -1.2F);
		FrontalL.addBox(0.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
		HeadExtension.addChild(FrontalR);
		Head.addChild(HeadExtension);
		HeadExtension.addChild(FrontalL);
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
