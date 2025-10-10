package dev.pswg.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

/**
 * Provides utilities for drawing graphical primitives inside
 * a {@link DrawContext}
 */
public final class Drawables
{
	@Environment(EnvType.CLIENT)
	public record ColoredFloatQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, int col1,
	                                                    int col2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState
	{
		public ColoredFloatQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, int col1, int col2, @Nullable ScreenRect scissorArea)
		{
			this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
		}

		@Override
		public void setupVertices(VertexConsumer vertices)
		{
			vertices.vertex(this.pose(), (float)this.x0(), (float)this.y0()).color(this.col1());
			vertices.vertex(this.pose(), (float)this.x0(), (float)this.y1()).color(this.col2());
			vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1()).color(this.col2());
			vertices.vertex(this.pose(), (float)this.x1(), (float)this.y0()).color(this.col1());
		}

		@Nullable
		private static ScreenRect createBounds(float x0, float y0, float x1, float y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea)
		{
			ScreenRect screenRect = (new ScreenRect((int)x0, (int)y0, (int)Math.ceil(x1 - x0), (int)Math.ceil(y1 - y0))).transformEachVertex(pose);
			return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
		}
	}

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
	 * @param z        The z-depth of the region
	 * @param color    The color to fill the region with
	 */
	public static void fill(DrawContext context, RenderPipeline pipeline, float x1, float y1, float x2, float y2, int z, int color)
	{
		context.state.addSimpleElement(new ColoredFloatQuadGuiElementRenderState(pipeline, TextureSetup.empty(), new Matrix3x2f(context.getMatrices()), x1, y1, x2, y2, color, color, context.scissorStack.peekLast()));
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
			fill(context, RenderPipelines.GUI, x, top, x + size, bottom, 200, color);
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
		fill(context, RenderPipelines.GUI, i, j, i + width, j + 2, 200, Colors.BLACK);
		fill(context, RenderPipelines.GUI, i, j, i + width * value, j + 1, 200, ColorHelper.fullAlpha(color));
	}
}
