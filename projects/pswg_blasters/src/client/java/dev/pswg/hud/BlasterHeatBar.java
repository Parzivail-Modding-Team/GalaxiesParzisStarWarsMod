package dev.pswg.hud;

import dev.pswg.Blasters;
import dev.pswg.item.BlasterItem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Draws heat and cooling with the same texture at HUD and item-bar sizes.
 */
public final class BlasterHeatBar
{
	/**
	 * Stores the values needed to draw one stack's heat bar.
	 */
	public record Visual(
			float fraction,
			BlasterItem.CoolingMode mode,
			boolean overcharged,
			Optional<BlasterItem.Cooling> cooling
	)
	{
	}

	/**
	 * Width of the original HUD texture strip.
	 */
	public static final int SOURCE_WIDTH = 61;

	/**
	 * Texture shared by full and compact heat bars.
	 */
	private static final Identifier TEXTURE = Blasters.id("textures/gui/hud_elements.png");

	/**
	 * Samples the stack. Weapons without heat do not need a bar.
	 */
	public static Optional<Visual> sample(Level level, ItemStack stack, float partialTick)
	{
		var stats = BlasterItem.getStats(level, stack);
		if (stats.isEmpty() || stats.orElseThrow().heat().capacity() <= 0)
		{
			return Optional.empty();
		}

		var definition = stats.orElseThrow();

		var overcharge = BlasterItem.getOverchargeTimeRemaining(level, stack, partialTick);
		if (overcharge.isPresent())
		{
			return Optional.of(new Visual(
					overcharge.orElseThrow(),
					BlasterItem.CoolingMode.PASSIVE,
					true,
					definition.cooling()
			));
		}

		var status = BlasterItem.getCoolingStatus(level, stack, partialTick);
		var capacity = status.coolingMode() == BlasterItem.CoolingMode.PASSIVE
		               ? definition.heat().capacity() : BlasterItem.getState(stack).lastVentingHeat();
		var fraction = capacity > 0 ? Math.clamp(status.totalHeat() / capacity, 0, 1) : 0;

		return Optional.of(new Visual(fraction, status.coolingMode(), false, definition.cooling()));
	}

	/**
	 * Draws a heat bar. Compact bars keep the cursor inside the bar.
	 */
	public static void render(GuiGraphicsExtractor context, Visual visual, int x, int y, int width, int height, boolean compact)
	{
		strip(context, x, y, width, height, 0, 0, SOURCE_WIDTH);

		if (visual.overcharged())
		{
			strip(context, x, y, width, height, 0, 12, SOURCE_WIDTH);
			drawCursor(context, visual.fraction(), x, y, width, height, compact);
		}
		else if (visual.mode() == BlasterItem.CoolingMode.PASSIVE)
		{
			var fill = Math.clamp(Math.round(width * visual.fraction()), 0, width);
			if (fill > 0)
			{
				strip(context, x, y, fill, height, 0, 4, Math.max(1, Math.round(SOURCE_WIDTH * visual.fraction())));
			}
		}
		else
		{
			strip(context, x, y, width, height, 0, 16, SOURCE_WIDTH);
			drawWindows(context, visual, x, y, width, height);
			drawCursor(context, visual.fraction(), x, y, width, height, compact);
		}

		if (!compact)
		{
			strip(context, x, y, width, height, 0, 20, SOURCE_WIDTH);
		}
	}

	/**
	 * Draws the primary and secondary cooling windows.
	 */
	private static void drawWindows(GuiGraphicsExtractor context, Visual visual, int x, int y, int width, int height)
	{
		if (!visual.mode().canBypass() || visual.cooling().isEmpty())
		{
			return;
		}

		var cooling = visual.cooling().orElseThrow();
		drawWindow(context, x, y, width, height, cooling.primaryBypassTime(), cooling.primaryBypassTolerance(), 8);
		drawWindow(context, x, y, width, height, cooling.secondaryBypassTime(), cooling.secondaryBypassTolerance(), 12);
	}

	/**
	 * Maps a cooling window to pixels. A nonempty window gets at least one pixel.
	 */
	public static int windowStart(float center, float tolerance, int width)
	{
		return Math.clamp((int)((center - tolerance) * width), 0, width - 1);
	}

	/**
	 * Keeps a cooling window within the bar.
	 */
	public static int windowWidth(float center, float tolerance, int width)
	{
		var start = windowStart(center, tolerance, width);
		var end = Math.clamp((int)Math.ceil((center + tolerance) * width), start + 1, width);
		return end - start;
	}

	/**
	 * Draws a window from the original HUD texture.
	 */
	private static void drawWindow(GuiGraphicsExtractor context, int x, int y, int width, int height, float center, float tolerance, int row)
	{
		var start = windowStart(center, tolerance, width);
		var pixels = windowWidth(center, tolerance, width);
		var sourceStart = windowStart(center, tolerance, SOURCE_WIDTH);
		var sourceWidth = windowWidth(center, tolerance, SOURCE_WIDTH);

		strip(context, x + start, y, pixels, height, sourceStart, row, sourceWidth);
	}

	/**
	 * Draws the remaining cooling or overcharge marker.
	 */
	private static void drawCursor(GuiGraphicsExtractor context, float fraction, int x, int y, int width, int height, boolean compact)
	{
		var cursorWidth = compact ? 1 : 3;
		var cursorHeight = compact ? height : 7;
		var cursorX = x + Math.round(Math.clamp(fraction, 0, 1) * (width - cursorWidth));
		var cursorY = compact ? y : y - 2;

		context.blit(
				RenderPipelines.GUI_TEXTURED,
				TEXTURE,
				cursorX,
				cursorY,
				0,
				24,
				cursorWidth,
				cursorHeight,
				3,
				7,
				256,
				256,
				-1
		);
	}

	/**
	 * Scales one texture strip to the requested bar size.
	 */
	private static void strip(GuiGraphicsExtractor context, int x, int y, int width, int height, int sourceX, int sourceY, int sourceWidth)
	{
		context.blit(
				RenderPipelines.GUI_TEXTURED,
				TEXTURE,
				x,
				y,
				sourceX,
				sourceY,
				width,
				height,
				sourceWidth,
				3,
				256,
				256,
				-1
		);
	}

	/**
	 * Utility class.
	 */
	private BlasterHeatBar()
	{
	}
}
