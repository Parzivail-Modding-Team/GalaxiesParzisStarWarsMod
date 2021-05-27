package com.parzivail.pswg.client.render;

import com.parzivail.pswg.Client;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShapes;

public class EmptyRenderer extends EntityRenderer<Entity>
{
	public EmptyRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	public void render(Entity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrix, vertexConsumers, light);

		if (!Client.minecraft.options.debugEnabled)
			return;

		var box = entity.getBoundingBox();

		matrix.push();

		matrix.translate(0, box.getYLength() / 2, 0);

		var f = 0.025f;
		var n = entity.getClass().getSimpleName();

		matrix.multiply(this.dispatcher.getRotation());
		matrix.scale(-f, -f, f);

		var textMatrix = matrix.peek().getModel();

		var g = Client.minecraft.options.getTextBackgroundOpacity(0.25F);
		var k = (int)(g * 255.0F) << 24;
		var textRenderer = this.getFontRenderer();
		var h = (float)(-textRenderer.getWidth(n) / 2);

		textRenderer.draw(n, h, 0, 0x20ffffff, false, textMatrix, vertexConsumers, true, k, light);
		textRenderer.draw(n, h, 0, -1, false, textMatrix, vertexConsumers, false, 0, light);

		matrix.pop();

		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());

		var center = box.getCenter();

		var boxMatrix = matrix.peek().getModel();
		var shape = VoxelShapes.cuboid(box);
		shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
			vertexConsumer.vertex(boxMatrix, (float)(x1 - center.x), (float)(y1 - center.y), (float)(z1 - center.z)).color(1f, 1f, 1f, 1f).next();
			vertexConsumer.vertex(boxMatrix, (float)(x2 - center.x), (float)(y2 - center.y), (float)(z2 - center.z)).color(1f, 1f, 1f, 1f).next();
		});
	}

	@Override
	public Identifier getTexture(Entity entity)
	{
		return null;
	}
}
