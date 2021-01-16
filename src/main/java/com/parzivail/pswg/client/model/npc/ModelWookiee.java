package com.parzivail.pswg.client.model.npc;

import com.parzivail.pswg.access.util.PlayerEntityModelAccessUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class ModelWookiee<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart Hair;
	public ModelPart FootL;
	public ModelPart FootR;

	public ModelWookiee(boolean male, float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.Hair = new ModelPart(this, 11, 66);
		this.Hair.setPivot(0.0F, -1.0F, 0.0F);
		this.Hair.addCuboid(-4.0F, -8.0F, -4.0F, 8, 17, 8, 0.1F);
		this.leftArm = new ModelPart(this, 32, 49);
		this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
		this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
		this.FootR = new ModelPart(this, 47, 70);
		this.FootR.setPivot(0.0F, 8.0F, -1.5F);
		this.FootR.addCuboid(-2.0F, 0.0F, -1.5F, 4, 4, 5, 0.0F);
		this.rightLeg = new ModelPart(this, 0, 17);
		this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.torso = new ModelPart(this, 16, 17);
		this.torso.setPivot(0.0F, 0.0F, 0.0F);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		this.rightArm = new ModelPart(this, 40, 17);
		this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
		this.rightArm.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
		this.FootL = new ModelPart(this, 47, 70);
		this.FootL.setPivot(0.0F, 8.0F, -1.5F);
		this.FootL.addCuboid(-2.0F, 0.0F, -1.5F, 4, 4, 5, 0.0F);
		PlayerEntityModelAccessUtil.setRightPantLeg(this, new ModelPart(this, 0, 33));
		this.rightPantLeg.setPivot(-2.0F, 12.0F, 0.0F);
		this.rightPantLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
		this.leftLeg = new ModelPart(this, 16, 49);
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.helmet = new ModelPart(this, 32, 0);
		this.helmet.setPivot(0.0F, -1.0F, 0.0F);
		this.helmet.addCuboid(-4.0F, -8.0F, -4.0F, 8, 9, 8, 0.2F);
		this.head = new ModelPart(this, 0, 0);
		this.head.setPivot(0.0F, -1.0F, 0.0F);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 9, 8, 0.0F);
		PlayerEntityModelAccessUtil.setLeftPantLeg(this, new ModelPart(this, 0, 49));
		this.leftPantLeg.setPivot(2.0F, 12.0F, 0.0F);
		this.leftPantLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
		PlayerEntityModelAccessUtil.setRightSleeve(this, new ModelPart(this, 40, 33));
		this.rightSleeve.setPivot(-5.0F, 2.0F, 0.0F);
		this.rightSleeve.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
		PlayerEntityModelAccessUtil.setJacket(this, new ModelPart(this, 16, 33));
		this.jacket.setPivot(0.0F, 0.0F, 0.0F);
		this.jacket.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F);
		PlayerEntityModelAccessUtil.setLeftSleeve(this, new ModelPart(this, 48, 49));
		this.leftSleeve.setPivot(5.0F, 2.0F, 0.0F);
		this.leftSleeve.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
		this.rightLeg.addChild(this.FootR);
		this.leftLeg.addChild(this.FootL);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
