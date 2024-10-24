package dev.pswg.rendering;

import dev.pswg.mixin.client.Accessor$DrawContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

/**
 * Provides utilities for drawing garphical primitives inside
 * a {@link DrawContext}
 */
public final class Drawables
{
	/**
	 * Gets the vertex consumer provider that the given context uses to draw primitives
	 *
	 * @param context The context from which to extract the vertex consumer provider
	 *
	 * @return The context's vertex consumer provider
	 */
	public static VertexConsumerProvider.Immediate getVertexConsumers(DrawContext context)
	{
		return ((Accessor$DrawContext)context).getVertexConsumers();
	}

	/**
	 * Fills a given region with the specified color, without aligning the boundaries
	 * to a pixel grid
	 *
	 * @param context The context into which it will be drawn
	 * @param layer   The {@link RenderLayer} into which the region will be drawn
	 * @param x1      The first corner's x-coordinate
	 * @param y1      The first corner's y-coordinate
	 * @param x2      The second corner's x-coordinate
	 * @param y2      The second corner's y-coordinate
	 * @param z       The z-depth of the region
	 * @param color   The color to fill the region with
	 */
	public static void fill(DrawContext context, RenderLayer layer, float x1, float y1, float x2, float y2, int z, int color)
	{
		Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

		float i;

		if (x1 < x2)
		{
			i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2)
		{
			i = y1;
			y1 = y2;
			y2 = i;
		}

		VertexConsumer vertexConsumer = getVertexConsumers(context).getBuffer(layer);
		vertexConsumer.vertex(matrix4f, x1, y1, (float)z).color(color);
		vertexConsumer.vertex(matrix4f, x1, y2, (float)z).color(color);
		vertexConsumer.vertex(matrix4f, x2, y2, (float)z).color(color);
		vertexConsumer.vertex(matrix4f, x2, y1, (float)z).color(color);
	}

	/**
	 * Draws an item cooldown box
	 *
	 * @param context The context into which it will be drawn
	 * @param value   The value [0,1]=[empty,full] of the cooldown box
	 * @param x       The x position of the left of the box
	 * @param y       The y position of the top of the full box
	 * @param size    The width and height dimensions of the box
	 * @param color   The color of the box
	 */
	public static void itemCooldown(DrawContext context, float value, float x, float y, float size, int color)
	{
		if (value > 0.0F)
		{
			float top = y + size * (1 - value);
			float bottom = top + size * value;
			fill(context, RenderLayer.getGui(), x, top, x + size, bottom, 200, color);
		}
	}

	/**
	 * Draws an item durability bar
	 *
	 * @param context The context into which it will be drawn
	 * @param value   The value [0,1]=[empty,full] of the durability bar
	 * @param x       The x position of the left of the box
	 * @param y       The y position of the top of the box
	 * @param width   The width of the full box
	 * @param color   The color of the foreground bar
	 */
	public static void itemDurability(DrawContext context, float value, float x, float y, int width, int color)
	{
		float i = x + 2;
		float j = y + 13;
		fill(context, RenderLayer.getGui(), i, j, i + width, j + 2, 200, Colors.BLACK);
		fill(context, RenderLayer.getGui(), i, j, i + width * value, j + 1, 200, ColorHelper.fullAlpha(color));
	}
}
