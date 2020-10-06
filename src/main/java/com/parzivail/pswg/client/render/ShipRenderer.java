package com.parzivail.pswg.client.render;

import com.parzivail.pswg.entity.ShipEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;

public abstract class ShipRenderer<T extends ShipEntity> extends EntityRenderer<T>
{
	public ShipRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher);
	}

	@Override
	public void render(T entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrix, vertexConsumers, light);
		matrix.push();

		matrix.translate(0, entity.getEyeHeight(null), 0);

		Quaternion r = entity.getViewRotation(tickDelta);
		matrix.multiply(r);

		renderModel(entity, yaw, tickDelta, matrix, vertexConsumers, light);

		matrix.pop();
	}

	protected abstract void renderModel(T entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light);
}
