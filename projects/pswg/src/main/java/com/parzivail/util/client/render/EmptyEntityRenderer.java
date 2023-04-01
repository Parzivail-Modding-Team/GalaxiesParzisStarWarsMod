package com.parzivail.util.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EmptyEntityRenderer extends EntityRenderer<Entity>
{
	public EmptyEntityRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	public void render(Entity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrix, vertexConsumers, light);

		var minecraft = MinecraftClient.getInstance();
//		if (!minecraft.options.debugEnabled)
//			return;

		var box = entity.getBoundingBox();

		matrix.push();

		matrix.translate(0, box.getYLength() / 2, 0);

		var f = 0.025f;
		var n = entity.getType().getTranslationKey();

		matrix.multiply(this.dispatcher.getRotation());
		matrix.scale(-f, -f, f);

		var textMatrix = matrix.peek().getPositionMatrix();

		var g = minecraft.options.getTextBackgroundOpacity(0.25F);
		var k = (int)(g * 255.0F) << 24;
		var textRenderer = this.getTextRenderer();
		var h = (float)(-textRenderer.getWidth(n) / 2);

		textRenderer.draw(n, h, 0, 0x20ffffff, false, textMatrix, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, k, light);
		textRenderer.draw(n, h, 0, -1, false, textMatrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

		matrix.pop();

		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());

		box = box.offset(-entity.getX(), -entity.getY(), -entity.getZ());
		WorldRenderer.drawBox(matrix, vertexConsumer, box, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public Identifier getTexture(Entity entity)
	{
		return null;
	}
}
