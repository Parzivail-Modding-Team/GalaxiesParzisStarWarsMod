package com.parzivail.pswg.client.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

//@Environment(EnvType.CLIENT)
public class EmptyPlayerModel<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final ModelPart EMPTY_PLAYER_MODEL = TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, false), 64, 64).createModel();

	public EmptyPlayerModel()
	{
		super(EMPTY_PLAYER_MODEL, false);
	}
}
