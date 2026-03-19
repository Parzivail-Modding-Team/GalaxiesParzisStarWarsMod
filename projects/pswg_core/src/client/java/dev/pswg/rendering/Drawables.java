package dev.pswg.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;

/**
 * Provides utilities for drawing graphical primitives inside
 * a {@link GuiGraphicsExtractor}
 */
public final class Drawables
{
	/**
	 * Fills a given region with the specified color, without aligning the boundaries
	 * to a pixel grid
	 *
	 * @param context  The context into which it will be drawn
	 * @param pipeline The {@link RenderPipeline} into which the region will be drawn
	 * @param x1       The first corner's x-coordinate
	 * @param y1       The first corner's y-coordinate
	 * @param x2       The second corner's x-coordinate
	 * @param y2       The second corner's y-coordinate
	 * @param color    The color to fill the region with
	 */
	public static void fill(GuiGraphicsExtractor context, RenderPipeline pipeline, float x1, float y1, float x2, float y2, int color)
	{
		context.fill(pipeline, (int)x1, (int)y1, (int)x2, (int)y2, color);
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
	public static void itemCooldown(GuiGraphicsExtractor context, float value, float x, float y, float size, int color)
	{
		if (value > 0.0F)
		{
			float top = y + size * (1 - value);
			float bottom = top + size * value;
			fill(context, RenderPipelines.GUI, x, top, x + size, bottom, color);
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
	public static void itemDurability(GuiGraphicsExtractor context, float value, float x, float y, int width, int color)
	{
		float i = x + 2;
		float j = y + 13;
		fill(context, RenderPipelines.GUI, i, j, i + width, j + 2, CommonColors.BLACK);
		fill(context, RenderPipelines.GUI, i, j, i + width * value, j + 1, ARGB.opaque(color));
	}
}
