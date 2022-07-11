package com.parzivail.pswg.client.render.player;

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
public class PlayerSpeciesModelRenderer extends PlayerEntityRenderer
{
	@FunctionalInterface
	public interface Animator
	{
		void animateModel(AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerSpeciesModelRenderer renderer, float tickDelta);
	}

	private final Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> modelSupplier;
	private final Animator animator;
	private Identifier overrideTexture;

	public PlayerSpeciesModelRenderer(EntityRendererFactory.Context ctx, boolean slim, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> model, Animator animator)
	{
		super(ctx, slim);
		this.modelSupplier = model;
		this.animator = animator;
	}

	@Override
	public PlayerEntityModel<AbstractClientPlayerEntity> getModel()
	{
		// This works because this method is called
		// when the pose is set, before this.model is
		// used directly
		if (modelSupplier != null)
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

	@Override
	public void render(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light)
	{
		if (animator != null)
			animator.animateModel(player, getModel(), this, tickDelta);
		super.render(player, yaw, tickDelta, matrices, vertexConsumerProvider, light);
	}

	public void renderWithTexture(Identifier texture, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i)
	{
		overrideTexture = texture;
		super.render(abstractClientPlayerEntity, f, g, matrixStack, vertexConsumerProvider, i);
		overrideTexture = null;
	}
}
