package com.parzivail.pswg.client.render;

import com.parzivail.pswg.Client;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class EmptyRenderer extends EntityRenderer<Entity>
{
	public EmptyRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher);
	}

	@Override
	public void render(Entity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrix, vertexConsumers, light);

		if (!Client.minecraft.options.debugEnabled)
			return;

		Box box = entity.getBoundingBox();

		matrix.push();

		matrix.translate(0, box.getYLength() / 2, 0);

		float f = 0.025f;
		Text n = entity.getName();

		matrix.multiply(this.renderManager.getRotation());
		matrix.scale(-f, -f, f);

		Matrix4f textMatrix = matrix.peek().getModel();

		float g = Client.minecraft.options.getTextBackgroundOpacity(0.25F);
		int k = (int)(g * 255.0F) << 24;
		TextRenderer textRenderer = this.getFontRenderer();
		float h = (float)(-textRenderer.getStringWidth(n.asString()) / 2);

		textRenderer.draw(n.asString(), h, 0, 0x20ffffff, false, textMatrix, vertexConsumers, true, k, light);
		textRenderer.draw(n.asString(), h, 0, -1, false, textMatrix, vertexConsumers, false, 0, light);

		matrix.pop();

		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());

		Vec3d center = box.getCenter();

		Matrix4f boxMatrix = matrix.peek().getModel();
		VoxelShape shape = VoxelShapes.cuboid(box);
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
