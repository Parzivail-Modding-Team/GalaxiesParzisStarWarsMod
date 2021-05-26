package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ModelTogrutaM<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart TailBaseL;
	public ModelPart TailBaseR;
	public ModelPart TailBaseB;
	public ModelPart SkullElevation;
	public ModelPart TailMidL;
	public ModelPart TaiUpperL;
	public ModelPart TailLowerL;
	public ModelPart TaiTipL;
	public ModelPart TailMidR;
	public ModelPart TailUpperR;
	public ModelPart TailLowerR;
	public ModelPart TailTipR;
	public ModelPart TailMidB;
	public ModelPart TailLowerB;

	public ModelTogrutaM(float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.TailLowerR = new ModelPart(this, 32, 71);
		this.TailLowerR.mirror = true;
		this.TailLowerR.setPivot(-0.9F, 6.4F, 0.5F);
		this.TailLowerR.addCuboid(-2.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerR, 0.31869712141416456F, 0.0F, -0.27314402793711257F);
		this.TailBaseL = new ModelPart(this, 0, 71);
		this.TailBaseL.setPivot(1.4F, -10.1F, -2.6F);
		this.TailBaseL.addCuboid(0.0F, 0.0F, 0.0F, 4, 7, 6, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseL, -0.22759093446006054F, -0.14398966328953217F, 0.0F);
		this.TailMidB = new ModelPart(this, 34, 81);
		this.TailMidB.mirror = true;
		this.TailMidB.setPivot(1.0F, -1.4F, 2.5F);
		this.TailMidB.addCuboid(0.0F, 0.0F, -0.9F, 4, 11, 3, 0.0F);
		this.TailLowerB = new ModelPart(this, 49, 83);
		this.TailLowerB.mirror = true;
		this.TailLowerB.setPivot(1.0F, 10.0F, -0.4F);
		this.TailLowerB.addCuboid(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		this.TailBaseR = new ModelPart(this, 0, 71);
		this.TailBaseR.mirror = true;
		this.TailBaseR.setPivot(-1.4F, -10.1F, -2.6F);
		this.TailBaseR.addCuboid(-4.0F, 0.0F, 0.0F, 4, 7, 6, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseR, -0.22759093446006054F, 0.14398966328953217F, 0.0F);
		this.TailBaseB = new ModelPart(this, 10, 85);
		this.TailBaseB.setPivot(-3.0F, -7.7F, 0.4F);
		this.TailBaseB.addCuboid(0.0F, 0.0F, 0.0F, 6, 5, 5, 0.0F);
		this.TaiTipL = new ModelPart(this, 54, 72);
		this.TaiTipL.setPivot(0.0F, -2.1F, 0.5F);
		this.TaiTipL.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TaiTipL, 0.0F, 0.0F, -0.27314402793711257F);
		this.TailLowerL = new ModelPart(this, 32, 71);
		this.TailLowerL.setPivot(0.9F, 6.4F, 0.5F);
		this.TailLowerL.addCuboid(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL, 0.31869712141416456F, 0.0F, 0.27314402793711257F);
		this.head = new ModelPart(this, 0, 0);
		this.head.setPivot(0.0F, 0.0F, 0.0F);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.SkullElevation = new ModelPart(this, 60, 80);
		this.SkullElevation.setPivot(-4.0F, -9.1F, -2.0F);
		this.SkullElevation.addCuboid(0.0F, 0.0F, 0.0F, 8, 2, 6, 0.0F);
		this.TaiUpperL = new ModelPart(this, 41, 71);
		this.TaiUpperL.setPivot(1.2F, -1.0F, -1.3F);
		this.TaiUpperL.addCuboid(0.0F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TaiUpperL, 0.8196066167365371F, 0.0F, 0.27314402793711257F);
		this.TailTipR = new ModelPart(this, 54, 72);
		this.TailTipR.mirror = true;
		this.TailTipR.setPivot(0.0F, -2.1F, 0.5F);
		this.TailTipR.addCuboid(-2.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailTipR, 0.0F, 0.0F, 0.27314402793711257F);
		this.TailUpperR = new ModelPart(this, 41, 71);
		this.TailUpperR.mirror = true;
		this.TailUpperR.setPivot(-1.2F, -1.0F, -1.3F);
		this.TailUpperR.addCuboid(-3.0F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailUpperR, 0.8196066167365371F, 0.0F, -0.27314402793711257F);
		this.TailMidR = new ModelPart(this, 20, 71);
		this.TailMidR.mirror = true;
		this.TailMidR.setPivot(-1.0F, 2.7F, 2.3F);
		this.TailMidR.addCuboid(-3.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailMidR, -0.22759093446006054F, -0.136659280431156F, 0.091106186954104F);
		this.TailMidL = new ModelPart(this, 20, 71);
		this.TailMidL.setPivot(1.0F, 2.7F, 2.3F);
		this.TailMidL.addCuboid(0.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailMidL, -0.22759093446006054F, 0.136659280431156F, -0.091106186954104F);
		this.TailMidR.addChild(this.TailLowerR);
		this.head.addChild(this.TailBaseL);
		this.TailBaseB.addChild(this.TailMidB);
		this.TailMidB.addChild(this.TailLowerB);
		this.head.addChild(this.TailBaseR);
		this.head.addChild(this.TailBaseB);
		this.TaiUpperL.addChild(this.TaiTipL);
		this.TailMidL.addChild(this.TailLowerL);
		this.head.addChild(this.SkullElevation);
		this.TailBaseL.addChild(this.TaiUpperL);
		this.TailUpperR.addChild(this.TailTipR);
		this.TailBaseR.addChild(this.TailUpperR);
		this.TailBaseR.addChild(this.TailMidR);
		this.TailBaseL.addChild(this.TailMidL);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
