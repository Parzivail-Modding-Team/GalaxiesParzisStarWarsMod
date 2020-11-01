package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class ModelTogrutaF<T extends LivingEntity> extends PlayerEntityModel<T>
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
	public ModelPart Chest;

	public ModelTogrutaF(float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.TailMidB = new ModelPart(this, 34, 81);
		this.TailMidB.mirror = true;
		this.TailMidB.setPivot(1.0F, -1.4F, 2.5F);
		this.TailMidB.addCuboid(0.0F, 0.0F, -0.9F, 4, 11, 3, 0.0F);
		this.TailBaseB = new ModelPart(this, 10, 85);
		this.TailBaseB.setPivot(-3.0F, -7.7F, 0.4F);
		this.TailBaseB.addCuboid(0.0F, 0.0F, 0.0F, 6, 5, 5, 0.0F);
		this.TailLowerB = new ModelPart(this, 49, 83);
		this.TailLowerB.mirror = true;
		this.TailLowerB.setPivot(1.0F, 8.8F, -0.4F);
		this.TailLowerB.addCuboid(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		this.TaiUpperL = new ModelPart(this, 41, 71);
		this.TaiUpperL.setPivot(0.2F, -3.5F, 0.3F);
		this.TaiUpperL.addCuboid(0.0F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TaiUpperL, 0.13700834628155487F, 0.0F, -0.13700834628155487F);
		this.SkullElevation = new ModelPart(this, 60, 80);
		this.SkullElevation.setPivot(-4.0F, -9.1F, -2.0F);
		this.SkullElevation.addCuboid(0.0F, 0.0F, 0.0F, 8, 2, 6, 0.0F);
		this.TailLowerL = new ModelPart(this, 32, 71);
		this.TailLowerL.setPivot(0.5F, 10.0F, 0.5F);
		this.TailLowerL.addCuboid(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL, 0.31869712141416456F, 0.0F, 0.0F);
		this.TailMidL = new ModelPart(this, 20, 71);
		this.TailMidL.setPivot(0.6F, 2.7F, 2.3F);
		this.TailMidL.addCuboid(0.0F, 0.0F, 0.0F, 3, 10, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailMidL, -0.136659280431156F, 0.136659280431156F, 0.0F);
		this.head = new ModelPart(this, 0, 0);
		this.head.setPivot(0.0F, 0.0F, 0.0F);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.TaiTipL = new ModelPart(this, 54, 72);
		this.TaiTipL.setPivot(1.4F, -2.7F, 0.5F);
		this.TaiTipL.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TaiTipL, 0.0F, 0.0F, 0.31869712141416456F);
		this.TailMidR = new ModelPart(this, 20, 71);
		this.TailMidR.mirror = true;
		this.TailMidR.setPivot(-0.6F, 2.7F, 2.3F);
		this.TailMidR.addCuboid(-3.0F, 0.0F, 0.0F, 3, 10, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailMidR, -0.136659280431156F, -0.136659280431156F, 0.0F);
		this.rightArm = new ModelPart(this, 32, 48);
		this.rightArm.mirror = true;
		this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
		this.rightArm.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
		this.TailBaseR = new ModelPart(this, 0, 71);
		this.TailBaseR.mirror = true;
		this.TailBaseR.setPivot(-1.4F, -10.1F, -2.6F);
		this.TailBaseR.addCuboid(-4.0F, 0.0F, 0.0F, 4, 7, 6, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseR, -0.22759093446006054F, 0.14398966328953217F, 0.0F);
		this.leftLeg = new ModelPart(this, 0, 16);
		this.leftLeg.mirror = true;
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.Chest = new ModelPart(this, 0, 65);
		this.Chest.setPivot(0.0F, -0.1F, 1.0F);
		this.Chest.addCuboid(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
		this.leftArm = new ModelPart(this, 40, 16);
		this.leftArm.mirror = true;
		this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
		this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
		this.TailUpperR = new ModelPart(this, 41, 71);
		this.TailUpperR.mirror = true;
		this.TailUpperR.setPivot(-0.2F, -3.5F, 0.3F);
		this.TailUpperR.addCuboid(-3.0F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailUpperR, 0.13700834628155487F, 0.0F, 0.13700834628155487F);
		this.torso = new ModelPart(this, 16, 16);
		this.torso.setPivot(0.0F, 0.0F, 0.0F);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		this.TailBaseL = new ModelPart(this, 0, 71);
		this.TailBaseL.setPivot(1.4F, -10.1F, -2.6F);
		this.TailBaseL.addCuboid(0.0F, 0.0F, 0.0F, 4, 7, 6, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseL, -0.22759093446006054F, -0.14398966328953217F, 0.0F);
		this.helmet = new ModelPart(this, 32, 0);
		this.helmet.setPivot(0.0F, 0.0F, 0.0F);
		this.helmet.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F);
		this.TailLowerR = new ModelPart(this, 32, 71);
		this.TailLowerR.mirror = true;
		this.TailLowerR.setPivot(-0.5F, 10.0F, 0.5F);
		this.TailLowerR.addCuboid(-2.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerR, 0.31869712141416456F, 0.0F, 0.0F);
		this.TailTipR = new ModelPart(this, 54, 72);
		this.TailTipR.mirror = true;
		this.TailTipR.setPivot(-1.4F, -2.7F, 0.5F);
		this.TailTipR.addCuboid(-2.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailTipR, 0.0F, 0.0F, -0.31869712141416456F);
		this.rightLeg = new ModelPart(this, 16, 48);
		this.rightLeg.mirror = true;
		this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.TailBaseB.addChild(this.TailMidB);
		this.head.addChild(this.TailBaseB);
		this.TailMidB.addChild(this.TailLowerB);
		this.TailBaseL.addChild(this.TaiUpperL);
		this.head.addChild(this.SkullElevation);
		this.TailMidL.addChild(this.TailLowerL);
		this.TailBaseL.addChild(this.TailMidL);
		this.TaiUpperL.addChild(this.TaiTipL);
		this.TailBaseR.addChild(this.TailMidR);
		this.head.addChild(this.TailBaseR);
		this.torso.addChild(this.Chest);
		this.TailBaseR.addChild(this.TailUpperR);
		this.head.addChild(this.TailBaseL);
		this.TailMidR.addChild(this.TailLowerR);
		this.TailUpperR.addChild(this.TailTipR);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
