package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

/**
 * PSWG_Chagrian - Sindavar
 * Created using Tabula 7.1.0
 */
public class ModelChagrian<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart HeadTailL;
	public ModelPart HeadTailR;
	public ModelPart TailUpperR;
	public ModelPart TailUpperL;
	public ModelPart TailLowerL;
	public ModelPart TailHornL;
	public ModelPart TailLowerR;
	public ModelPart TailHornR;
	public ModelPart TailUpperUpperR;
	public ModelPart TailUpperUpperL;
	public ModelPart Chest;

	public ModelChagrian(boolean male, float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.HeadTailL = new ModelPart(this, 20, 66);
		this.HeadTailL.mirror = true;
		this.HeadTailL.setPivot(-1.1F, -6.6F, 3.1F);
		this.HeadTailL.addCuboid(0.0F, 0.0F, 0.0F, 3, 7, 4, 0.0F);
		ModelPartUtil.setRotateAngle(HeadTailL, -0.136659280431156F, 3.141592653589793F, 0.18203784098300857F);
		this.Chest = new ModelPart(this, 0, 65);
		this.Chest.setPivot(0.0F, -0.1F, 1.0F);
		this.Chest.addCuboid(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
		this.HeadTailR = new ModelPart(this, 20, 66);
		this.HeadTailR.mirror = true;
		this.HeadTailR.setPivot(1.1F, -6.6F, 3.1F);
		this.HeadTailR.addCuboid(-3.0F, 0.0F, 0.0F, 3, 7, 4, 0.0F);
		ModelPartUtil.setRotateAngle(HeadTailR, -0.136659280431156F, -3.141592653589793F, -0.18203784098300857F);
		this.TailUpperUpperR = new ModelPart(this, 43, 80);
		this.TailUpperUpperR.mirror = true;
		this.TailUpperUpperR.setPivot(0.5F, -4.7F, 1.5F);
		this.TailUpperUpperR.addCuboid(0.0F, 0.0F, 0.0F, 1, 5, 1, 0.0F);
		ModelPartUtil.setRotateAngle(TailUpperUpperR, -0.18203784098300857F, 0.0F, 0.0F);
		this.TailUpperL = new ModelPart(this, 50, 80);
		this.TailUpperL.setPivot(-1.5F, -9.9F, -3.5F);
		this.TailUpperL.addCuboid(-2.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailUpperL, 0.091106186954104F, 0.0F, -0.22759093446006054F);
		this.torso = new ModelPart(this, 16, 16);
		this.torso.setPivot(0.0F, 0.0F, 0.0F);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		this.TailLowerL = new ModelPart(this, 35, 67);
		this.TailLowerL.mirror = true;
		this.TailLowerL.setPivot(0.4F, 6.7F, 2.9F);
		this.TailLowerL.addCuboid(0.0F, 0.0F, 0.0F, 3, 4, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerL, 1.3658946726107624F, 0.0F, 0.0F);
		this.TailLowerR = new ModelPart(this, 35, 67);
		this.TailLowerR.mirror = true;
		this.TailLowerR.setPivot(-0.4F, 6.7F, 2.9F);
		this.TailLowerR.addCuboid(-3.0F, 0.0F, 0.0F, 3, 4, 3, 0.0F);
		ModelPartUtil.setRotateAngle(TailLowerR, 1.3658946726107624F, 0.0F, 0.0F);
		this.TailHornR = new ModelPart(this, 48, 67);
		this.TailHornR.mirror = true;
		this.TailHornR.setPivot(-1.1F, 3.0F, 1.5F);
		this.TailHornR.addCuboid(-1.0F, 0.0F, 0.0F, 1, 7, 1, 0.0F);
		ModelPartUtil.setRotateAngle(TailHornR, -1.2292353921796064F, -0.18203784098300857F, -0.045553093477052F);
		this.helmet = new ModelPart(this, 32, 0);
		this.helmet.setPivot(0.0F, 0.0F, 0.0F);
		this.helmet.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F);
		this.TailHornL = new ModelPart(this, 48, 67);
		this.TailHornL.mirror = true;
		this.TailHornL.setPivot(1.1F, 3.0F, 1.5F);
		this.TailHornL.addCuboid(0.0F, 0.0F, 0.0F, 1, 7, 1, 0.0F);
		ModelPartUtil.setRotateAngle(TailHornL, -1.2292353921796064F, 0.18203784098300857F, 0.045553093477052F);
		this.TailUpperR = new ModelPart(this, 50, 80);
		this.TailUpperR.setPivot(1.5F, -9.9F, -3.5F);
		this.TailUpperR.addCuboid(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		ModelPartUtil.setRotateAngle(TailUpperR, 0.091106186954104F, 0.0F, 0.22759093446006054F);
		this.TailUpperUpperL = new ModelPart(this, 43, 80);
		this.TailUpperUpperL.mirror = true;
		this.TailUpperUpperL.setPivot(-0.5F, -4.7F, 1.5F);
		this.TailUpperUpperL.addCuboid(-1.0F, 0.0F, 0.0F, 1, 5, 1, 0.0F);
		ModelPartUtil.setRotateAngle(TailUpperUpperL, -0.18203784098300857F, 0.0F, 0.0F);
		this.head.addChild(this.HeadTailL);
		if (!male)
			this.torso.addChild(this.Chest);
		this.head.addChild(this.HeadTailR);
		this.TailUpperR.addChild(this.TailUpperUpperR);
		this.head.addChild(this.TailUpperL);
		this.HeadTailL.addChild(this.TailLowerL);
		this.HeadTailR.addChild(this.TailLowerR);
		this.TailLowerR.addChild(this.TailHornR);
		this.TailLowerL.addChild(this.TailHornL);
		this.head.addChild(this.TailUpperR);
		this.TailUpperL.addChild(this.TailUpperUpperL);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
