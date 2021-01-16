package com.parzivail.pswg.client.model.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.parzivail.pswg.access.util.PlayerEntityModelAccessUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class ModelKaminoan<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart neck;
	public ModelPart neckoverlay;
	public ModelPart Head_1;
	public ModelPart Chest;
	public ModelPart torsolower;

	public ModelKaminoan(boolean male, float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.rightLeg = new ModelPart(this, 8, 12);
		this.rightLeg.setPivot(-0.5F, 7.9F, 1.0F);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 2, 16, 2, 0.0F);
		this.neck = new ModelPart(this, 51, 32);
		this.neck.setPivot(0.0F, -1.0F, 0.0F);
		this.neck.addCuboid(-1.0F, -13.0F, -1.0F, 2, 13, 2, 0.0F);
		this.torsolower = new ModelPart(this, 22, 25);
		this.torsolower.setPivot(0.0F, 5.4F, -0.5F);
		this.torsolower.addCuboid(-2.5F, 0.0F, -1.5F, 5, 7, 2, 0.0F);
		this.leftArm = new ModelPart(this, 29, 48);
		this.leftArm.setPivot(4.0F, -2.9F, 0.0F);
		this.leftArm.addCuboid(-1.0F, -1.5F, -1.0F, 2, 15, 2, 0.0F);
		this.neckoverlay = new ModelPart(this, 51, 14);
		this.neckoverlay.setPivot(0.0F, -1.0F, 0.0F);
		this.neckoverlay.addCuboid(-1.0F, -13.0F, -1.0F, 2, 13, 2, 0.2F);
		PlayerEntityModelAccessUtil.setJacket(this, new ModelPart(this, 21, 36));
		this.jacket.setPivot(0.0F, -4.6F, 1.0F);
		this.jacket.addCuboid(-3.0F, 0.0F, -2.0F, 6, 6, 2, 0.2F);
		this.leftLeg = new ModelPart(this, 19, 48);
		this.leftLeg.setPivot(0.5F, 7.9F, 1.0F);
		this.leftLeg.addCuboid(0.0F, 0.0F, -2.0F, 2, 16, 2, 0.0F);
		PlayerEntityModelAccessUtil.setRightSleeve(this, new ModelPart(this, 40, 30));
		this.rightSleeve.setPivot(-4.3F, -2.9F, 0.0F);
		this.rightSleeve.addCuboid(-0.7F, -1.5F, -1.0F, 2, 15, 2, 0.2F);
		this.Head_1 = new ModelPart(this, 56, 0);
		this.Head_1.setPivot(0.0F, -6.5F, -1.0F);
		this.Head_1.addCuboid(0.0F, 0.0F, 0.0F, 0, 7, 5, 0.0F);
		PlayerEntityModelAccessUtil.setLeftSleeve(this, new ModelPart(this, 40, 48));
		this.leftSleeve.setPivot(4.0F, -2.9F, 0.0F);
		this.leftSleeve.addCuboid(-1.0F, -1.5F, -1.0F, 2, 15, 2, 0.2F);
		this.torso = new ModelPart(this, 21, 15);
		this.torso.setPivot(0.0F, -4.5F, 1.0F);
		this.torso.addCuboid(-3.0F, 0.0F, -2.0F, 6, 6, 2, 0.0F);
		PlayerEntityModelAccessUtil.setRightPantLeg(this, new ModelPart(this, 9, 30));
		this.rightPantLeg.setPivot(-0.5F, 7.9F, 1.0F);
		this.rightPantLeg.addCuboid(-2.0F, 0.0F, -2.0F, 2, 16, 2, 0.2F);
		PlayerEntityModelAccessUtil.setLeftPantLeg(this, new ModelPart(this, 9, 48));
		this.leftPantLeg.setPivot(0.5F, 7.9F, 1.0F);
		this.leftPantLeg.addCuboid(0.0F, 0.0F, -2.0F, 2, 16, 2, 0.2F);
		this.head = new ModelPart(this, 0, 0);
		this.head.setPivot(0.2F, -12.4F, -0.5F);
		this.head.addCuboid(-2.5F, -5.0F, -2.5F, 5, 5, 5, 0.0F);
		this.rightArm = new ModelPart(this, 40, 12);
		this.rightArm.setPivot(-4.0F, -2.9F, 0.0F);
		this.rightArm.addCuboid(-1.0F, -1.5F, -1.0F, 2, 15, 2, 0.0F);
		this.Chest = new ModelPart(this, 0, 69);
		this.Chest.setPivot(0.5F, -1.4F, 1.0F);
		this.Chest.addCuboid(-3.0F, 2.0F, -4.0F, 5, 3, 2, 0.0F);
		this.helmet = new ModelPart(this, 32, 0);
		this.helmet.setPivot(0.0F, -12.4F, -0.5F);
		this.helmet.addCuboid(-2.5F, -5.0F, -2.5F, 5, 5, 5, 0.2F);
		this.torso.addChild(this.torsolower);
		this.head.addChild(this.Head_1);
		if (!male)
			this.torso.addChild(this.Chest);
	}

	protected Iterable<ModelPart> getBodyParts()
	{
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.neck, neckoverlay));
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
