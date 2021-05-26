package com.parzivail.pswg.client.model.npc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ModelBith<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart HeadExtension;
	public ModelPart FrontalR;
	public ModelPart FrontalL;
	public ModelPart Chest;

	public ModelBith(boolean male, float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.HeadExtension = new ModelPart(this, 0, 74);
		this.HeadExtension.setPivot(-4.5F, -9.3F, -3.4F);
		this.HeadExtension.addCuboid(0.0F, 0.0F, 0.0F, 9, 7, 8, 0.0F);
		this.Chest = new ModelPart(this, 0, 65);
		this.Chest.setPivot(0.0F, -0.1F, 1.0F);
		this.Chest.addCuboid(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
		this.FrontalL = new ModelPart(this, 47, 80);
		this.FrontalL.setPivot(4.7F, 0.2F, -1.2F);
		this.FrontalL.addCuboid(0.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
		this.FrontalR = new ModelPart(this, 47, 80);
		this.FrontalR.setPivot(4.3F, 0.2F, -1.2F);
		this.FrontalR.addCuboid(-4.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
		this.head.addChild(this.HeadExtension);
		if (!male)
			this.torso.addChild(this.Chest);
		this.HeadExtension.addChild(this.FrontalL);
		this.HeadExtension.addChild(this.FrontalR);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}
}
