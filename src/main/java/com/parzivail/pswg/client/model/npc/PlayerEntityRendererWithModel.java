package com.parzivail.pswg.client.model.npc;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class PlayerEntityRendererWithModel extends PlayerEntityRenderer
{
	public PlayerEntityRendererWithModel(EntityRenderDispatcher entityRenderDispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model)
	{
		super(entityRenderDispatcher);
		this.model = model;
	}
}
