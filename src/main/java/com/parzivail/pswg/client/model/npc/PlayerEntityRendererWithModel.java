package com.parzivail.pswg.client.model.npc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PlayerEntityRendererWithModel extends PlayerEntityRenderer
{
	private Identifier overrideTexture;

	public PlayerEntityRendererWithModel(EntityRenderDispatcher entityRenderDispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model)
	{
		super(entityRenderDispatcher);
		this.model = model;
	}

	@Override
	public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity)
	{
		if (overrideTexture != null)
			return overrideTexture;

		return super.getTexture(abstractClientPlayerEntity);
	}

	public void renderWithTexture(Identifier texture, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i)
	{
		overrideTexture = texture;
		super.render(abstractClientPlayerEntity, f, g, matrixStack, vertexConsumerProvider, i);
		overrideTexture = null;
	}
}
