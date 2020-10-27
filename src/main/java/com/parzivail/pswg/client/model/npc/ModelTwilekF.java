package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class ModelTwilekF<T extends LivingEntity> extends PlayerEntityModel<T>
{
	public ModelPart Chest;
	public ModelPart SpikeL;
	public ModelPart SpikeR;
	public ModelPart TailBaseR;
	public ModelPart TailBaseL;
	public ModelPart HeadTailR;
	public ModelPart TailLowerL;
	public ModelPart HeadTailL;
	public ModelPart TailLowerL_1;

	public ModelTwilekF(float scale)
	{
		super(scale, false);

		textureWidth = 64;
		textureHeight = 32;
		Chest = new ModelPart(this, 50, 10);
		Chest.setPivot(-3.0F, 2.0F, -2.9F);
		Chest.addCuboid(0.0F, 0.0F, 0.0F, 6, 3, 1, 0.0F);
		rightArm = new ModelPart(this, 40, 16);
		rightArm.setPivot(-5.0F, 2.0F, 0.0F);
		rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		ModelPartUtil.setRotateAngle(rightArm, 0.0F, 0.0F, 0.10000736613927509F);
		SpikeR = new ModelPart(this, 24, 0);
		SpikeR.mirror = true;
		SpikeR.setPivot(-4.8F, -4.8F, -1.8F);
		SpikeR.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(SpikeR, 0.0F, 0.0F, -0.4461061568097506F);
		rightLeg = new ModelPart(this, 0, 16);
		rightLeg.setPivot(-1.9F, 12.0F, 0.1F);
		rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		TailBaseL = new ModelPart(this, 46, 0);
		TailBaseL.setPivot(0.5F, -7.2F, -0.2F);
		TailBaseL.addCuboid(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseL, 0.18203784098300857F, 0.0F, 0.0F);
		HeadTailL = new ModelPart(this, 32, 0);
		HeadTailL.setPivot(0.6F, -0.2F, 0.9F);
		HeadTailL.addCuboid(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		ModelPartUtil.setRotateAngle(HeadTailL, -0.08325220532012952F, 0.0F, 0.0F);
		leftLeg = new ModelPart(this, 0, 16);
		leftLeg.mirror = true;
		leftLeg.setPivot(1.9F, 12.0F, 0.1F);
		leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		HeadTailR = new ModelPart(this, 33, 0);
		HeadTailR.mirror = true;
		HeadTailR.setPivot(0.4F, -0.2F, 0.9F);
		HeadTailR.addCuboid(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		ModelPartUtil.setRotateAngle(HeadTailR, -0.08325220532012952F, 0.0F, 0.0F);
		head = new ModelPart(this, 0, 0);
		head.setPivot(0.0F, 0.0F, 0.0F);
		head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		leftArm = new ModelPart(this, 40, 16);
		leftArm.mirror = true;
		leftArm.setPivot(5.0F, 2.0F, -0.0F);
		leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		ModelPartUtil.setRotateAngle(leftArm, 0.0F, 0.0F, -0.10000736613927509F);
		TailBaseR = new ModelPart(this, 46, 0);
		TailBaseR.mirror = true;
		TailBaseR.setPivot(-4.5F, -7.2F, -0.2F);
		TailBaseR.addCuboid(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseR, 0.18203784098300857F, 0.0F, 0.0F);
		TailLowerL_1 = new ModelPart(this, 35, 4);
		TailLowerL_1.mirror = true;
		TailLowerL_1.setPivot(0.5F, 11.5F, 1.7F);
		TailLowerL_1.addCuboid(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL_1, -0.08866272600131193F, 0.0F, 0.0F);
		torso = new ModelPart(this, 16, 16);
		torso.setPivot(0.0F, 0.0F, 0.0F);
		torso.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		TailLowerL = new ModelPart(this, 35, 4);
		TailLowerL.setPivot(0.5F, 11.5F, 1.7F);
		TailLowerL.addCuboid(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL, -0.08866272600131193F, 0.0F, 0.0F);
		SpikeL = new ModelPart(this, 24, 0);
		SpikeL.mirror = true;
		SpikeL.setPivot(3.0F, -5.8F, -1.8F);
		SpikeL.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(SpikeL, 0.0F, 0.0F, 0.4461061568097506F);
		head.addChild(SpikeR);
		head.addChild(TailBaseL);
		TailBaseL.addChild(HeadTailL);
		TailBaseR.addChild(HeadTailR);
		head.addChild(TailBaseR);
		HeadTailL.addChild(TailLowerL_1);
		HeadTailR.addChild(TailLowerL);
		head.addChild(SpikeL);
		torso.addChild(Chest);
	}
}
