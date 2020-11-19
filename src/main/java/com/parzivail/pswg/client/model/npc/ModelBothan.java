package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class ModelBothan<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart EarL;
	public ModelPart SnoutLower;
	public ModelPart EarR;
	public ModelPart EarTipL;
	public ModelPart SnoutUpper;
	public ModelPart SnoutLower_1;
	public ModelPart EarTipR;
	public ModelPart Chest;

	public ModelBothan(boolean male, float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.SnoutLower_1 = new ModelPart(this, 63, 71);
		this.SnoutLower_1.setPivot(-0.6F, 1.7F, -0.1F);
		this.SnoutLower_1.addCuboid(0.0F, 0.0F, 0.0F, 5, 3, 4, 0.0F);
		this.Chest = new ModelPart(this, 0, 65);
		this.Chest.setPivot(0.0F, -0.1F, 1.0F);
		this.Chest.addCuboid(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
		this.SnoutUpper = new ModelPart(this, 66, 81);
		this.SnoutUpper.setPivot(0.5F, 0.0F, 0.0F);
		this.SnoutUpper.addCuboid(0.0F, 0.0F, 0.0F, 3, 2, 6, 0.0F);
		ModelPartUtil.setRotateAngle(SnoutUpper, 0.38275070496235647F, 0.0F, 0.0F);
		this.EarL = new ModelPart(this, 46, 69);
		this.EarL.setPivot(3.0F, -4.9F, -2.1F);
		this.EarL.addCuboid(0.0F, 0.0F, 0.0F, 1, 2, 4, 0.0F);
		ModelPartUtil.setRotateAngle(EarL, 0.6373942428283291F, 0.36425021489121656F, 0.18203784098300857F);
		this.SnoutLower = new ModelPart(this, 45, 80);
		this.SnoutLower.setPivot(-2.0F, -1.1F, -7.0F);
		this.SnoutLower.addCuboid(0.0F, 0.0F, 0.0F, 4, 4, 5, 0.0F);
		ModelPartUtil.setRotateAngle(SnoutLower, 0.6373942428283291F, 0.0F, 0.0F);
		this.EarR = new ModelPart(this, 46, 69);
		this.EarR.setPivot(-3.0F, -4.9F, -2.1F);
		this.EarR.addCuboid(-1.0F, 0.0F, 0.0F, 1, 2, 4, 0.0F);
		ModelPartUtil.setRotateAngle(EarR, 0.6373942428283291F, -0.36425021489121656F, -0.18203784098300857F);
		this.EarTipR = new ModelPart(this, 55, 68);
		this.EarTipR.setPivot(0.0F, 0.0F, 4.0F);
		this.EarTipR.addCuboid(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.EarTipL = new ModelPart(this, 55, 68);
		this.EarTipL.setPivot(0.0F, 0.0F, 4.0F);
		this.EarTipL.addCuboid(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.SnoutLower.addChild(this.SnoutLower_1);
		if (!male)
			this.torso.addChild(this.Chest);
		this.EarR.addChild(this.EarTipR);
		this.EarL.addChild(this.EarTipL);
		this.SnoutLower.addChild(this.SnoutUpper);
		this.head.addChild(this.EarL);
		this.head.addChild(this.SnoutLower);
		this.head.addChild(this.EarR);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
