package dev.pswg;

import dev.pswg.container.GadgetsItems;
import dev.pswg.feature.scrapping.cutter.LaserCutterItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LaserCutterHandler
{
	public static double modifier = 1;

	public static void tick(Minecraft client)
	{
		Player player = client.player;
		if (player == null)
			return;

		ItemStack mainStack = player.getMainHandItem();
		if (mainStack.getItem() instanceof LaserCutterItem && mainStack.has(GadgetsItems.Components.MIN_POS))
		{
			double distanceMod = (player.blockInteractionRange() - player.position().distanceTo(mainStack.get(GadgetsItems.Components.CURRENT_BLOCK).getCenter())) / player.blockInteractionRange();
			modifier = (0.125f + (1 - (double)mainStack.getOrDefault(DataComponents.DAMAGE, 0) / mainStack.getOrDefault(DataComponents.MAX_DAMAGE, 1) * 0.125f)) * distanceMod;
		}
		else
			modifier = 1;
	}
}
