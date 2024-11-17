package dev.pswg.rendering;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * Represents a texture that can be drawn onto the screen.
 */
public record BlittableTexture(Identifier identifier, Function<Identifier, RenderLayer> renderLayers, int width, int height)
{
	/**
	 * A region of the {@link BlittableTexture} that has a pre-defined
	 * texture UV and size.
	 */
	public final class Patch
	{
		private final int u;
		private final int v;
		private final int width;
		private final int height;

		public Patch(int texU, int texV, int width, int height)
		{
			this.u = texU;
			this.v = texV;
			this.width = width;
			this.height = height;
		}

		/**
		 * Draws a section of the texture onto the screen at given screen coordinates.
		 *
		 * @param context the drawing context used to render the texture
		 * @param screenX the x-coordinate on the screen where the texture will be drawn
		 * @param screenY the y-coordinate on the screen where the texture will be drawn
		 * @param color   the tint to be applied to the texture while drawing
		 */
		public void blit(DrawContext context, int screenX, int screenY, int color)
		{
			BlittableTexture.this.blit(context, screenX, screenY, u, v, width, height, color);
		}

		/**
		 * Draws a section of the texture onto the screen at given screen coordinates with a custom size.
		 *
		 * @param context     the drawing context used to render the texture
		 * @param screenX     the x-coordinate on the screen where the texture will be drawn
		 * @param screenY     the y-coordinate on the screen where the texture will be drawn
		 * @param patchWidth  the width of the texture portion to be drawn
		 * @param patchHeight the height of the texture portion to be drawn
		 * @param color       the tint to be applied to the texture while drawing
		 */
		public void blit(DrawContext context, int screenX, int screenY, int patchWidth, int patchHeight, int color)
		{
			BlittableTexture.this.blit(context, screenX, screenY, u, v, patchWidth, patchHeight, color);
		}
	}

	/**
	 * Creates a new {@link Patch} with specified texture coordinates and size.
	 *
	 * @param texU        The x-coordinate within the texture to start the patch from.
	 * @param texV        The y-coordinate within the texture to start the patch from.
	 * @param patchWidth  The width of the patch.
	 * @param patchHeight The height of the patch.
	 *
	 * @return A new {@link Patch} that derives from this texture.
	 */
	public Patch createPatch(int texU, int texV, int patchWidth, int patchHeight)
	{
		return new Patch(texU, texV, patchWidth, patchHeight);
	}

	/**
	 * Draws a portion of the texture onto the screen at the specified location.
	 *
	 * @param context     the drawing context used to render the texture
	 * @param screenX     the x-coordinate on the screen where the texture will be drawn
	 * @param screenY     the y-coordinate on the screen where the texture will be drawn
	 * @param texU        the x-coordinate within the texture to start drawing from
	 * @param texV        the y-coordinate within the texture to start drawing from
	 * @param patchWidth  the width of the texture portion to be drawn
	 * @param patchHeight the height of the texture portion to be drawn
	 * @param color       the tint to be applied to the texture while drawing
	 */
	public void blit(DrawContext context, int screenX, int screenY, int texU, int texV, int patchWidth, int patchHeight, int color)
	{
		context.drawTexture(
				renderLayers,
				identifier,
				screenX, screenY,
				texU, texV,
				patchWidth, patchHeight,
				width, height,
				color
		);
	}
}
