package com.parzivail.pswg.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PlayerEntityRendererWithModel extends PlayerEntityRenderer
{
	private final Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> modelSupplier;
	private Identifier overrideTexture;

	public PlayerEntityRendererWithModel(EntityRendererFactory.Context ctx, boolean slim, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> model)
	{
		super(ctx, slim);
		this.modelSupplier = model;
	}

	@Override
	public PlayerEntityModel<AbstractClientPlayerEntity> getModel()
	{
		// This works because this method is called
		// when the pose is set, before this.model is
		// used directly
		this.model = modelSupplier.get();
		return super.getModel();
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
