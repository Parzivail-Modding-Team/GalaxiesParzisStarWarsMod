package dev.pswg.hud;

import dev.pswg.GalaxiesClient;
import dev.pswg.item.BlasterItem;
import dev.pswg.rendering.ItemHudRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Draws a weapon's heat bar and loaded ammunition below the crosshair.
 */
public class DefaultBlasterHudRenderer implements ItemHudRenderer
{
	/** Vertical distance from the crosshair to the heat bar. */
	private static final int HEAT_OFFSET = 30;

	@Override
	public void render(ItemStack stack, GuiGraphicsExtractor context, DeltaTracker tickCounter)
	{
		var client = Minecraft.getInstance();
		if (client.level == null)
		{
			return;
		}

		var stats = BlasterItem.getStats(client.level, stack);
		if (stats.isEmpty())
		{
			return;
		}

		var centerX = context.guiWidth() / 2;
		var top = context.guiHeight() / 2 + HEAT_OFFSET;
		var visual = BlasterHeatBar.sample(client.level, stack, GalaxiesClient.getTickDelta());
		if (visual.isPresent())
		{
			BlasterHeatBar.render(
					context,
					visual.orElseThrow(),
					centerX - BlasterHeatBar.SOURCE_WIDTH / 2,
					top,
					BlasterHeatBar.SOURCE_WIDTH,
					3,
					false
			);
		}

		var ammo = stats.orElseThrow().ammo();
		var capacity = BlasterItem.getAmmoCapacity(ammo);
		if (capacity > 0)
		{
			var text = Component.translatable("tooltip.pswg_blasters.ammo", BlasterItem.getLoadedAmmo(stack, ammo), capacity);
			context.text(client.font, text, centerX - client.font.width(text) / 2, top + 10, -1, true);
		}
	}
}
