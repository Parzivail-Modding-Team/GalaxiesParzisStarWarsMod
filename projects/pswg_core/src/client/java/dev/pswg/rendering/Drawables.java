package dev.pswg.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

/**
 * Provides utilities for drawing graphical primitives inside
 * a {@link GuiGraphics}
 */
public final class Drawables
{
	@Environment(EnvType.CLIENT)
	public record ColoredFloatQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, int col1,
	                                                    int col2, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState
	{
		public ColoredFloatQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, int col1, int col2, @Nullable ScreenRectangle scissorArea)
		{
			this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
		}

		@Override
		public void buildVertices(VertexConsumer vertices)
		{
			vertices.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y0()).setColor(this.col1());
			vertices.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y1()).setColor(this.col2());
			vertices.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y1()).setColor(this.col2());
			vertices.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y0()).setColor(this.col1());
		}

		@Nullable
		private static ScreenRectangle createBounds(float x0, float y0, float x1, float y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
		{
			ScreenRectangle screenRect = (new ScreenRectangle((int)x0, (int)y0, (int)Math.ceil(x1 - x0), (int)Math.ceil(y1 - y0))).transformMaxBounds(pose);
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
	 * @param color    The color to fill the region with
	 */
	public static void fill(GuiGraphics context, RenderPipeline pipeline, float x1, float y1, float x2, float y2, int color)
	{
		context.guiRenderState.submitGuiElement(new ColoredFloatQuadGuiElementRenderState(pipeline, TextureSetup.noTexture(), new Matrix3x2f(context.pose()), x1, y1, x2, y2, color, color, context.scissorStack.peek()));
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
	public static void itemCooldown(GuiGraphics context, float value, float x, float y, float size, int color)
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
	public static void itemDurability(GuiGraphics context, float value, float x, float y, int width, int color)
	{
		float i = x + 2;
		float j = y + 13;
		fill(context, RenderPipelines.GUI, i, j, i + width, j + 2, CommonColors.BLACK);
		fill(context, RenderPipelines.GUI, i, j, i + width * value, j + 1, ARGB.opaque(color));
	}
}
