package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ModelTwilek<T extends LivingEntity> extends PlayerEntityModel<T>
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

	public ModelTwilek(boolean male, float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.TailLowerL_1 = new ModelPart(this, 47, 66);
		this.TailLowerL_1.setPivot(0.5F, 11.5F, 1.7F);
		this.TailLowerL_1.addCuboid(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL_1, -0.08866272600131193F, 0.0F, 0.0F);
		this.TailMidR = new ModelPart(this, 33, 66);
		this.TailMidR.mirror = true;
		this.TailMidR.setPivot(0.4F, -0.2F, 0.9F);
		this.TailMidR.addCuboid(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailMidR, -0.08325220532012952F, 0.0F, 0.0F);
		this.FrontalR = new ModelPart(this, 2, 79);
		this.FrontalR.mirror = true;
		this.FrontalR.setPivot(-4.3F, -8.6F, -4.4F);
		this.FrontalR.addCuboid(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
		this.TailBaseR = new ModelPart(this, 14, 66);
		this.TailBaseR.mirror = true;
		this.TailBaseR.setPivot(-4.5F, -7.2F, -0.2F);
		this.TailBaseR.addCuboid(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseR, 0.18203784098300857F, 0.0F, 0.0F);
		this.SpikeR = new ModelPart(this, 4, 71);
		this.SpikeR.mirror = true;
		this.SpikeR.setPivot(-4.8F, -3.5F, -1.8F);
		this.SpikeR.addCuboid(0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(SpikeR, 0.0F, 0.0F, -0.4461061568097506F);
		this.FrontalL = new ModelPart(this, 2, 79);
		this.FrontalL.setPivot(0.3F, -8.6F, -4.4F);
		this.FrontalL.addCuboid(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
		this.TailLowerL = new ModelPart(this, 47, 66);
		this.TailLowerL.mirror = true;
		this.TailLowerL.setPivot(0.5F, 11.5F, 1.7F);
		this.TailLowerL.addCuboid(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL, -0.08866272600131193F, 0.0F, 0.0F);
		this.TailBaseL = new ModelPart(this, 14, 66);
		this.TailBaseL.setPivot(0.5F, -7.2F, -0.1F);
		this.TailBaseL.addCuboid(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
		ModelPartUtil.setRotateAngle(TailBaseL, 0.18203784098300857F, 0.0F, 0.0F);
		this.SpikeL = new ModelPart(this, 4, 71);
		this.SpikeL.setPivot(4.8F, -3.5F, -1.8F);
		this.SpikeL.addCuboid(-2.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		ModelPartUtil.setRotateAngle(SpikeL, 0.0F, 0.0F, 0.4406956361285682F);
		this.TailMidL = new ModelPart(this, 33, 66);
		this.TailMidL.setPivot(0.6F, -0.2F, 0.9F);
		this.TailMidL.addCuboid(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailMidL, -0.08325220532012952F, 0.0F, 0.0F);
		this.Chest = new ModelPart(this, 0, 65);
		this.Chest.setPivot(0.0F, -0.1F, 1.0F);
		this.Chest.addCuboid(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
		this.TailMidL.addChild(this.TailLowerL_1);
		this.TailBaseR.addChild(this.TailMidR);
		this.head.addChild(this.TailBaseR);
		this.head.addChild(this.SpikeR);
		this.TailMidR.addChild(this.TailLowerL);
		this.head.addChild(this.TailBaseL);
		this.head.addChild(this.SpikeL);
		this.TailBaseL.addChild(this.TailMidL);

		if (male)
		{
			this.head.addChild(this.FrontalR);
			this.head.addChild(this.FrontalL);
		}
		else
			this.torso.addChild(this.Chest);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
