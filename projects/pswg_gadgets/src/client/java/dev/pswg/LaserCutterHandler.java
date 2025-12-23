package dev.pswg;

import dev.pswg.container.GadgetsItems;
import dev.pswg.feature.scrapping.cutter.LaserCutterItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class LaserCutterHandler
{
	public static double modifier = 1;

	public static void tick(MinecraftClient client)
	{
		PlayerEntity player = client.player;
		if (player == null)
			return;

		ItemStack mainStack = player.getMainHandStack();
		if (mainStack.getItem() instanceof LaserCutterItem && mainStack.contains(GadgetsItems.Components.MIN_POS))
		{
			double distanceMod = (player.getBlockInteractionRange() - player.getEntityPos().distanceTo(mainStack.get(GadgetsItems.Components.CURRENT_BLOCK).toCenterPos())) / player.getBlockInteractionRange();
			modifier = (0.125f + (1 - (double)mainStack.getOrDefault(DataComponentTypes.DAMAGE, 0) / mainStack.getOrDefault(DataComponentTypes.MAX_DAMAGE, 1) * 0.125f)) * distanceMod;
		}
		else
			modifier = 1;
	}
}
