package com.parzivail.pswg.client.zoom;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import net.minecraft.client.MinecraftClient;

public class ZoomHandler
{
	public static void tick(MinecraftClient mc)
	{
		if (mc.player == null)
			return;

		var stack = mc.player.getMainHandStack();

		var blasterZoomInstance = Client.blasterZoomInstance;

		if (stack.isOf(SwgItems.Blaster.Blaster))
		{
			var b = (BlasterItem)stack.getItem();
			var bt = new BlasterTag(stack.getOrCreateNbt());

			blasterZoomInstance.setZoom(bt.isAimingDownSights);
			blasterZoomInstance.setZoomDivisor(b.getFovMultiplier(stack, mc.world, mc.player));
		}
		else
			blasterZoomInstance.setZoom(false);
	}
}
