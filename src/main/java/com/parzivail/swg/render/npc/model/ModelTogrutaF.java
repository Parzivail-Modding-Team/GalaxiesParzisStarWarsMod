package com.parzivail.swg.render.npc.model;

import com.parzivail.swg.Resources;
import com.parzivail.swg.render.player.PModelBipedBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * ModelTogrutaF - Sindavar
 * Created using Tabula 4.1.1
 */
public class ModelTogrutaF extends PModelBipedBase
{
	private static final ResourceLocation texture = Resources.location("textures/species/togruta_f.png");

	public ModelRenderer Torso;
	public ModelRenderer Chest;
	public ModelRenderer ArmR;
	public ModelRenderer ArmL;
	public ModelRenderer LegR;
	public ModelRenderer Head;
	public ModelRenderer LegL;
	public ModelRenderer TailBaseR;
	public ModelRenderer TailBaseL;
	public ModelRenderer TailBaseB;
	public ModelRenderer HeadTailR;
	public ModelRenderer TailUpperR;
	public ModelRenderer TailLowerR;
	public ModelRenderer HeadTailChildR;
	public ModelRenderer HeadTailL;
	public ModelRenderer TaiUpperL;
	public ModelRenderer TailLowerL;
	public ModelRenderer HeadTailChildL;
	public ModelRenderer HeadTailB;
	public ModelRenderer TailLowerB;

	public ModelTogrutaF()
	{
		textureWidth = 64;
		textureHeight = 60;
		TailLowerB = new ModelRenderer(this, 36, 52);
		TailLowerB.mirror = true;
		TailLowerB.setRotationPoint(0.5F, 4.7F, -0.4F);
		TailLowerB.addBox(0.0F, 0.0F, 0.0F, 3, 6, 2, 0.0F);
		Chest = new ModelRenderer(this, 50, 10);
		Chest.setRotationPoint(-3.0F, 2.0F, -2.9F);
		Chest.addBox(0.0F, 0.0F, 0.0F, 6, 3, 1, 0.0F);
		ArmL = new ModelRenderer(this, 40, 16);
		ArmL.mirror = true;
		ArmL.setRotationPoint(5.0F, 2.0F, -0.0F);
		ArmL.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		setRotateAngle(ArmL, 0.0F, 0.0F, -0.10000736613927509F);
		LegL = new ModelRenderer(this, 0, 16);
		LegL.mirror = true;
		LegL.setRotationPoint(1.9F, 12.0F, 0.1F);
		LegL.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		TailBaseR = new ModelRenderer(this, 0, 36);
		TailBaseR.mirror = true;
		TailBaseR.setRotationPoint(-5.4F, -7.9F, 0.2F);
		TailBaseR.addBox(0.0F, -1.3F, -0.8F, 4, 6, 6, 0.0F);
		setRotateAngle(TailBaseR, -0.45378560551852565F, 0.13962634015954636F, 0.0F);
		TailLowerR = new ModelRenderer(this, 32, 40);
		TailLowerR.mirror = true;
		TailLowerR.setRotationPoint(1.5F, 11.5F, 2.7F);
		TailLowerR.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F);
		setRotateAngle(TailLowerR, 0.45378560551852565F, 0.0F, 0.0F);
		HeadTailChildL = new ModelRenderer(this, 40, 40);
		HeadTailChildL.mirror = true;
		HeadTailChildL.setRotationPoint(0.0F, 3.5F, 0.0F);
		HeadTailChildL.addBox(0.0F, 0.0F, 0.0F, 3, 6, 1, 0.0F);
		TaiUpperL = new ModelRenderer(this, 50, 39);
		TaiUpperL.mirror = true;
		TaiUpperL.setRotationPoint(0.9F, -5.3F, -0.1F);
		TaiUpperL.addBox(0.0F, 0.0F, 0.0F, 2, 6, 3, 0.0F);
		setRotateAngle(TaiUpperL, 0.13962634015954636F, 0.0F, -0.13962634015954636F);
		TailLowerL = new ModelRenderer(this, 32, 40);
		TailLowerL.mirror = true;
		TailLowerL.setRotationPoint(1.5F, 11.5F, 2.7F);
		TailLowerL.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F);
		setRotateAngle(TailLowerL, 0.45378560551852565F, 0.0F, 0.0F);
		ArmR = new ModelRenderer(this, 40, 16);
		ArmR.setRotationPoint(-5.0F, 2.0F, 0.0F);
		ArmR.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		setRotateAngle(ArmR, 0.0F, 0.0F, 0.10000736613927509F);
		Torso = new ModelRenderer(this, 16, 16);
		Torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		Torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		TailBaseB = new ModelRenderer(this, 0, 50);
		TailBaseB.setRotationPoint(-3.0F, -6.8F, 0.4F);
		TailBaseB.addBox(0.0F, 0.0F, 0.0F, 6, 5, 5, 0.0F);
		TailBaseL = new ModelRenderer(this, 0, 36);
		TailBaseL.setRotationPoint(1.4F, -7.9F, -0.2F);
		TailBaseL.addBox(0.0F, -1.3F, -0.8F, 4, 6, 6, 0.0F);
		setRotateAngle(TailBaseL, -0.45378560551852565F, -0.13962634015954636F, 0.0F);
		HeadTailChildR = new ModelRenderer(this, 40, 40);
		HeadTailChildR.mirror = true;
		HeadTailChildR.setRotationPoint(0.0F, 3.5F, 0.0F);
		HeadTailChildR.addBox(0.0F, 0.0F, 0.0F, 3, 6, 1, 0.0F);
		HeadTailR = new ModelRenderer(this, 20, 35);
		HeadTailR.mirror = true;
		HeadTailR.setRotationPoint(0.4F, -0.2F, 0.9F);
		HeadTailR.addBox(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		setRotateAngle(HeadTailR, -0.06981317007977318F, 0.0F, 0.0F);
		TailUpperR = new ModelRenderer(this, 50, 39);
		TailUpperR.mirror = true;
		TailUpperR.setRotationPoint(0.9F, -5.6F, 0.1F);
		TailUpperR.addBox(0.0F, 0.0F, 0.0F, 2, 6, 3, 0.0F);
		setRotateAngle(TailUpperR, 0.13962634015954636F, 0.0F, 0.13962634015954636F);
		Head = new ModelRenderer(this, 0, 0);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		LegR = new ModelRenderer(this, 0, 16);
		LegR.setRotationPoint(-1.9F, 12.0F, 0.1F);
		LegR.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		HeadTailL = new ModelRenderer(this, 20, 35);
		HeadTailL.mirror = true;
		HeadTailL.setRotationPoint(0.6F, -0.2F, 0.9F);
		HeadTailL.addBox(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		setRotateAngle(HeadTailL, -0.08726646259971647F, 0.0F, 0.0F);
		HeadTailB = new ModelRenderer(this, 22, 50);
		HeadTailB.mirror = true;
		HeadTailB.setRotationPoint(1.0F, 1.6F, 2.6F);
		HeadTailB.addBox(0.0F, 0.0F, -0.9F, 4, 7, 3, 0.0F);
		HeadTailB.addChild(TailLowerB);
		Head.addChild(TailBaseR);
		HeadTailR.addChild(TailLowerR);
		HeadTailL.addChild(HeadTailChildL);
		TailBaseL.addChild(TaiUpperL);
		HeadTailL.addChild(TailLowerL);
		Head.addChild(TailBaseB);
		Head.addChild(TailBaseL);
		HeadTailR.addChild(HeadTailChildR);
		TailBaseR.addChild(HeadTailR);
		TailBaseR.addChild(TailUpperR);
		TailBaseL.addChild(HeadTailL);
		TailBaseB.addChild(HeadTailB);
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
