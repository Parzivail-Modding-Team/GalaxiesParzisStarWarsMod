package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class ModelAqualish<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart Nose;
	public ModelPart ToothL;
	public ModelPart ToothR;
	public ModelPart Chest;

	public ModelAqualish(boolean male, float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.Nose = new ModelPart(this, 0, 73);
		this.Nose.setPivot(0.0F, -4.6F, -4.4F);
		this.Nose.addCuboid(-2.0F, 0.0F, 0.0F, 4, 4, 2, 0.0F);
		ModelPartUtil.setRotateAngle(Nose, -0.091106186954104F, 0.0F, 0.0F);
		this.ToothL = new ModelPart(this, 13, 74);
		this.ToothL.setPivot(0.1F, 3.2F, 0.1F);
		this.ToothL.addCuboid(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		ModelPartUtil.setRotateAngle(ToothL, 0.18203784098300857F, 0.0F, 0.0F);
		this.ToothR = new ModelPart(this, 13, 74);
		this.ToothR.setPivot(-0.1F, 3.2F, 0.1F);
		this.ToothR.addCuboid(-2.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		ModelPartUtil.setRotateAngle(ToothR, 0.18203784098300857F, 0.0F, 0.0F);
		this.Chest = new ModelPart(this, 0, 65);
		this.Chest.setPivot(0.0F, -0.1F, 1.0F);
		this.Chest.addCuboid(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
		if (!male)
			this.torso.addChild(this.Chest);
		this.Nose.addChild(this.ToothL);
		this.head.addChild(this.Nose);
		this.Nose.addChild(this.ToothR);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
