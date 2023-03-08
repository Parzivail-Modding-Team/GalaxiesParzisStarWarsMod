package com.parzivail.util.client.render;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class EmptyBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T>
{
	public EmptyBlockEntityRenderer(BlockEntityRendererFactory.Context ctx)
	{
	}

	@Override
	public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		var minecraft = MinecraftClient.getInstance();
		if (!minecraft.options.debugEnabled)
			return;

		var world = blockEntity.getWorld();
		var pos = blockEntity.getPos();
		var shape = world.getBlockState(pos).getOutlineShape(world, pos);

		matrices.push();

		matrices.translate(0.5f, 0.5f, 0.5f);

		var f = 0.025f;
		var n = blockEntity.getClass().getSimpleName();

		matrices.multiply(minecraft.getEntityRenderDispatcher().getRotation());
		matrices.scale(-f, -f, f);

		var textMatrix = matrices.peek().getPositionMatrix();

		var g = minecraft.options.getTextBackgroundOpacity(0.25F);
		var k = (int)(g * 255.0F) << 24;
		var textRenderer = minecraft.textRenderer;
		var h = (float)(-textRenderer.getWidth(n) / 2);

		textRenderer.draw(n, h, 0, 0x20ffffff, false, textMatrix, vertexConsumers, true, k, light);
		textRenderer.draw(n, h, 0, -1, false, textMatrix, vertexConsumers, false, 0, light);

		matrices.pop();

		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());

		for (var box : shape.getBoundingBoxes())
			WorldRenderer.drawBox(matrices, vertexConsumer, box, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
