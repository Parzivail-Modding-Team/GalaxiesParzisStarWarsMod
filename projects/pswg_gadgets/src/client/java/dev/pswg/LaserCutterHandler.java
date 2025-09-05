package dev.pswg;

import dev.pswg.container.GadgetsItems;
import dev.pswg.feature.scrapping.cutter.LaserCutterItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

public class LaserCutterHandler
{
	public static double modifier = 1;

	public static void tick(MinecraftClient client)
	{
		if (client.player == null)
			return;

		ItemStack mainStack = client.player.getMainHandStack();
		if (mainStack.getItem() instanceof LaserCutterItem && mainStack.contains(GadgetsItems.Components.CUTTING_PROGRESS))
			modifier = 0.25f + (1 - (float)mainStack.getOrDefault(DataComponentTypes.DAMAGE, 0) / mainStack.getOrDefault(DataComponentTypes.MAX_DAMAGE, 1) * 0.75f);
		else
			modifier = 1;
	}
}
