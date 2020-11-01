package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class ModelTwilekCombined<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart FrontalL;
	public ModelPart FrontalR;
	public ModelPart SpikeL;
	public ModelPart TailBaseR;
	public ModelPart TailBaseL;
	public ModelPart SpikeR;
	public ModelPart TailMidR;
	public ModelPart TailLowerL;
	public ModelPart TailMidL;
	public ModelPart TailLowerL_1;
	public ModelPart Chest;

	public ModelTwilekCombined(boolean male, float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		TailLowerL_1 = new ModelPart(this, 47, 66);
		TailLowerL_1.setPivot(0.5F, 11.5F, 1.7F);
		TailLowerL_1.addCuboid(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL_1, -0.08866272600131193F, 0.0F, 0.0F);
		TailBaseR = new ModelPart(this, 14, 66);
		TailBaseR.mirror = true;
		TailBaseR.setPivot(-4.5F, -7.2F, -0.2F);
		TailBaseR.addCuboid(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseR, 0.18203784098300857F, 0.0F, 0.0F);
		Chest = new ModelPart(this, 0, 65);
		Chest.setPivot(0.0F, -0.1F, 1.0F);
		Chest.addCuboid(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
		SpikeL = new ModelPart(this, 4, 71);
		SpikeL.setPivot(4.8F, -3.5F, -1.8F);
		SpikeL.addCuboid(-2.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(SpikeL, 0.0F, 0.0F, 0.4406956361285682F);
		TailMidL = new ModelPart(this, 33, 66);
		TailMidL.setPivot(0.6F, -0.2F, 0.9F);
		TailMidL.addCuboid(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailMidL, -0.08325220532012952F, 0.0F, 0.0F);
		FrontalR = new ModelPart(this, 2, 79);
		FrontalR.mirror = true;
		FrontalR.setPivot(-4.3F, -8.6F, -4.4F);
		FrontalR.addCuboid(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
		FrontalL = new ModelPart(this, 2, 79);
		FrontalL.setPivot(0.3F, -8.6F, -4.4F);
		FrontalL.addCuboid(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
		TailMidR = new ModelPart(this, 33, 66);
		TailMidR.mirror = true;
		TailMidR.setPivot(0.4F, -0.2F, 0.9F);
		TailMidR.addCuboid(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailMidR, -0.08325220532012952F, 0.0F, 0.0F);
		TailLowerL = new ModelPart(this, 47, 66);
		TailLowerL.mirror = true;
		TailLowerL.setPivot(0.5F, 11.5F, 1.7F);
		TailLowerL.addCuboid(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL, -0.08866272600131193F, 0.0F, 0.0F);
		SpikeR = new ModelPart(this, 4, 71);
		SpikeR.mirror = true;
		SpikeR.setPivot(-4.8F, -3.5F, -1.8F);
		SpikeR.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(SpikeR, 0.0F, 0.0F, -0.4461061568097506F);
		TailBaseL = new ModelPart(this, 14, 66);
		TailBaseL.setPivot(0.5F, -7.2F, -0.1F);
		TailBaseL.addCuboid(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseL, 0.18203784098300857F, 0.0F, 0.0F);
		TailMidL.addChild(TailLowerL_1);
		head.addChild(TailBaseR);
		if (!male)
			torso.addChild(Chest);
		head.addChild(SpikeL);
		TailBaseL.addChild(TailMidL);
		head.addChild(FrontalR);
		head.addChild(FrontalL);
		TailBaseR.addChild(TailMidR);
		TailMidR.addChild(TailLowerL);
		head.addChild(SpikeR);
		head.addChild(TailBaseL);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
