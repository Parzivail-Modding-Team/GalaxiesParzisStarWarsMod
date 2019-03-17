package com.parzivail.swg.render.npc.model;

import com.parzivail.swg.Resources;
import com.parzivail.swg.render.player.PModelBipedBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * ModelChagrianF - Sindavar
 * Created using Tabula 4.1.1
 */
public class ModelChagrianF extends PModelBipedBase
{
	private static final ResourceLocation texture = Resources.location("textures/species/chagrian_f.png");

	public ModelRenderer Torso;
	public ModelRenderer ArmR;
	public ModelRenderer LegL;
	public ModelRenderer Head;
	public ModelRenderer ArmL;
	public ModelRenderer LegR;
	public ModelRenderer Chest;
	public ModelRenderer TailBaseR;
	public ModelRenderer TailBaseL;
	public ModelRenderer HeadTailR;
	public ModelRenderer TailUpperR;
	public ModelRenderer TailLowerR;
	public ModelRenderer TailLowerLowerR;
	public ModelRenderer HeadTailL;
	public ModelRenderer TaiUpperL;
	public ModelRenderer TailLowerL;
	public ModelRenderer TailLowerLowerL;

	public ModelChagrianF()
	{
		textureWidth = 64;
		textureHeight = 60;
		Head = new ModelRenderer(this, 0, 0);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		Torso = new ModelRenderer(this, 16, 16);
		Torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		Torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		TailUpperR = new ModelRenderer(this, 50, 39);
		TailUpperR.setRotationPoint(1.2F, -2.7F, -4.4F);
		TailUpperR.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		setRotateAngle(TailUpperR, 0.8651597102135892F, 0.024085543677521744F, -0.0645771823237902F);
		TailLowerR = new ModelRenderer(this, 32, 40);
		TailLowerR.mirror = true;
		TailLowerR.setRotationPoint(-1.5F, 9.2F, 2.4F);
		TailLowerR.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
		setRotateAngle(TailLowerR, 0.45378560551852565F, 0.0F, 0.0F);
		TailBaseL = new ModelRenderer(this, 0, 36);
		TailBaseL.setRotationPoint(2.5F, -7.9F, -1.0F);
		TailBaseL.addBox(-1.5F, 0.3F, -0.2F, 4, 5, 6, 0.0F);
		setRotateAngle(TailBaseL, -0.45378560551852565F, -0.13962634015954636F, 0.0F);
		HeadTailR = new ModelRenderer(this, 20, 35);
		HeadTailR.mirror = true;
		HeadTailR.setRotationPoint(0.2F, 0.8F, 0.9F);
		HeadTailR.addBox(-3.0F, 1.9F, 1.1F, 3, 8, 3, 0.0F);
		setRotateAngle(HeadTailR, -0.08726646259971647F, 0.0F, 0.0F);
		ArmL = new ModelRenderer(this, 40, 16);
		ArmL.mirror = true;
		ArmL.setRotationPoint(5.0F, 2.0F, -0.0F);
		ArmL.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		setRotateAngle(ArmL, 0.0F, 0.0F, -0.10000736613927509F);
		TaiUpperL = new ModelRenderer(this, 50, 39);
		TaiUpperL.mirror = true;
		TaiUpperL.setRotationPoint(0.8F, -2.6F, -5.0F);
		TaiUpperL.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		setRotateAngle(TaiUpperL, 0.8651597102135892F, -0.024085543677521744F, 0.0645771823237902F);
		TailBaseR = new ModelRenderer(this, 0, 36);
		TailBaseR.mirror = true;
		TailBaseR.setRotationPoint(-2.5F, -7.9F, -1.0F);
		TailBaseR.addBox(-2.5F, 0.3F, -0.2F, 4, 5, 6, 0.0F);
		setRotateAngle(TailBaseR, -0.45378560551852565F, 0.13962634015954636F, 0.0F);
		ArmR = new ModelRenderer(this, 40, 16);
		ArmR.setRotationPoint(-5.0F, 2.0F, 0.0F);
		ArmR.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		setRotateAngle(ArmR, 0.0F, 0.0F, 0.10000736613927509F);
		TailLowerL = new ModelRenderer(this, 32, 40);
		TailLowerL.mirror = true;
		TailLowerL.setRotationPoint(1.5F, 9.2F, 2.4F);
		TailLowerL.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
		setRotateAngle(TailLowerL, 0.45378560551852565F, 0.0F, 0.0F);
		TailLowerLowerR = new ModelRenderer(this, 47, 50);
		TailLowerLowerR.mirror = true;
		TailLowerLowerR.setRotationPoint(0.0F, 1.0F, 0.0F);
		TailLowerLowerR.addBox(-0.5F, 0.0F, -0.5F, 1, 9, 1, 0.0F);
		TailLowerLowerL = new ModelRenderer(this, 47, 50);
		TailLowerLowerL.mirror = true;
		TailLowerLowerL.setRotationPoint(0.0F, 1.0F, 0.0F);
		TailLowerLowerL.addBox(-0.5F, 0.0F, -0.5F, 1, 9, 1, 0.0F);
		Chest = new ModelRenderer(this, 50, 10);
		Chest.setRotationPoint(-3.0F, 2.0F, -2.9F);
		Chest.addBox(0.0F, 0.0F, 0.0F, 6, 3, 1, 0.0F);
		LegR = new ModelRenderer(this, 0, 16);
		LegR.setRotationPoint(-1.9F, 12.0F, 0.1F);
		LegR.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		LegL = new ModelRenderer(this, 0, 16);
		LegL.mirror = true;
		LegL.setRotationPoint(1.9F, 12.0F, 0.1F);
		LegL.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		HeadTailL = new ModelRenderer(this, 20, 35);
		HeadTailL.mirror = true;
		HeadTailL.setRotationPoint(-0.2F, 0.8F, 0.9F);
		HeadTailL.addBox(0.0F, 1.9F, 1.1F, 3, 8, 3, 0.0F);
		setRotateAngle(HeadTailL, -0.08726646259971647F, 0.0F, 0.0F);
		TailBaseR.addChild(TailUpperR);
		HeadTailR.addChild(TailLowerR);
		Head.addChild(TailBaseL);
		TailBaseR.addChild(HeadTailR);
		TailBaseL.addChild(TaiUpperL);
		Head.addChild(TailBaseR);
		HeadTailL.addChild(TailLowerL);
		TailLowerR.addChild(TailLowerLowerR);
		TailLowerL.addChild(TailLowerLowerL);
		TailBaseL.addChild(HeadTailL);
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
