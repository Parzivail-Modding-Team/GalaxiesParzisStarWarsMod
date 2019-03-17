package com.parzivail.swg.render.npc.model;

import com.parzivail.swg.Resources;
import com.parzivail.swg.render.player.PModelBipedBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * ModelTwilekM - Sindavar
 * Created using Tabula 4.1.1
 */
public class ModelTwilekM extends PModelBipedBase
{
	private static final ResourceLocation texture = Resources.location("textures/species/twilek_m.png");

	public ModelRenderer Torso;
	public ModelRenderer LegL;
	public ModelRenderer Head;
	public ModelRenderer ArmR;
	public ModelRenderer ArmL;
	public ModelRenderer LegR;
	public ModelRenderer FrontalL;
	public ModelRenderer FrontalR;
	public ModelRenderer TailBaseR;
	public ModelRenderer TailBaseL;
	public ModelRenderer HeadTailR;
	public ModelRenderer TailLowerL;
	public ModelRenderer HeadTailL;
	public ModelRenderer TailLowerL_1;

	public ModelTwilekM()
	{
		textureWidth = 64;
		textureHeight = 32;
		TailLowerL_1 = new ModelRenderer(this, 32, 8);
		TailLowerL_1.mirror = true;
		TailLowerL_1.setRotationPoint(0.5F, 11.5F, 1.7F);
		TailLowerL_1.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		setRotateAngle(TailLowerL_1, -0.08866272600131193F, 0.0F, 0.0F);
		Head = new ModelRenderer(this, 0, 0);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		HeadTailR = new ModelRenderer(this, 32, 0);
		HeadTailR.mirror = true;
		HeadTailR.setRotationPoint(0.4F, -0.2F, 0.9F);
		HeadTailR.addBox(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		setRotateAngle(HeadTailR, -0.08325220532012952F, 0.0F, 0.0F);
		FrontalR = new ModelRenderer(this, 47, 1);
		FrontalR.setRotationPoint(-4.3F, -8.6F, -4.4F);
		FrontalR.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
		ArmL = new ModelRenderer(this, 40, 16);
		ArmL.mirror = true;
		ArmL.setRotationPoint(5.0F, 2.0F, -0.0F);
		ArmL.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		FrontalL = new ModelRenderer(this, 47, 1);
		FrontalL.setRotationPoint(0.3F, -8.6F, -4.4F);
		FrontalL.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
		HeadTailL = new ModelRenderer(this, 32, 0);
		HeadTailL.mirror = true;
		HeadTailL.setRotationPoint(0.6F, -0.2F, 0.9F);
		HeadTailL.addBox(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		setRotateAngle(HeadTailL, -0.08325220532012952F, 0.0F, 0.0F);
		Torso = new ModelRenderer(this, 16, 16);
		Torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		Torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		LegL = new ModelRenderer(this, 0, 16);
		LegL.mirror = true;
		LegL.setRotationPoint(1.9F, 12.0F, 0.1F);
		LegL.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		TailBaseL = new ModelRenderer(this, 46, 0);
		TailBaseL.setRotationPoint(0.5F, -7.2F, -0.2F);
		TailBaseL.addBox(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
		setRotateAngle(TailBaseL, 0.18203784098300857F, 0.0F, 0.0F);
		TailBaseR = new ModelRenderer(this, 46, 0);
		TailBaseR.mirror = true;
		TailBaseR.setRotationPoint(-4.5F, -7.2F, -0.2F);
		TailBaseR.addBox(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
		setRotateAngle(TailBaseR, 0.18203784098300857F, 0.0F, 0.0F);
		LegR = new ModelRenderer(this, 0, 16);
		LegR.setRotationPoint(-1.9F, 12.0F, 0.1F);
		LegR.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		TailLowerL = new ModelRenderer(this, 32, 8);
		TailLowerL.mirror = true;
		TailLowerL.setRotationPoint(0.5F, 11.5F, 1.7F);
		TailLowerL.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		setRotateAngle(TailLowerL, -0.08868795731309655F, 0.0F, 0.0F);
		ArmR = new ModelRenderer(this, 40, 16);
		ArmR.setRotationPoint(-5.0F, 2.0F, 0.0F);
		ArmR.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		HeadTailL.addChild(TailLowerL_1);
		TailBaseR.addChild(HeadTailR);
		Head.addChild(FrontalR);
		Head.addChild(FrontalL);
		TailBaseL.addChild(HeadTailL);
		Head.addChild(TailBaseL);
		Head.addChild(TailBaseR);
		HeadTailR.addChild(TailLowerL);
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
