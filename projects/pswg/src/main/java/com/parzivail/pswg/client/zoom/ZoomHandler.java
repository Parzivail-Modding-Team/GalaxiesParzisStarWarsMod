package com.parzivail.pswg.client.zoom;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterAttachmentFunction;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import io.github.ennuil.libzoomer.api.ZoomOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.Util;

import java.util.HashMap;

public class ZoomHandler
{
	private static final HashMap<BlasterAttachmentFunction, ZoomOverlay> ZOOM_OVERLAYS = Util.make(new HashMap<>(), (h) -> {
		h.put(BlasterAttachmentFunction.DEFAULT_SCOPE, Client.blasterZoomOverlayDefault);
		h.put(BlasterAttachmentFunction.SNIPER_SCOPE, Client.blasterZoomOverlaySniper);
	});

	public static void tick(MinecraftClient mc)
	{
		if (mc.player == null)
			return;

		var stack = mc.player.getMainHandStack();

		var blasterZoomInstance = Client.blasterZoomInstance;

		if (stack.getItem() instanceof BlasterItem b)
		{
			var bt = new BlasterTag(stack.getOrCreateNbt());
			var bd = BlasterItem.getBlasterDescriptor(stack);

			blasterZoomInstance.setZoom(bt.isAimingDownSights);
			blasterZoomInstance.setZoomDivisor(b.getFovMultiplier(stack, mc.world, mc.player));

			ZoomOverlay overlay = null;

			if (mc.options.getPerspective() == Perspective.FIRST_PERSON)
				overlay = bt.mapWithAttachment(bd, ZOOM_OVERLAYS).orElse(null);

			blasterZoomInstance.setZoomOverlay(overlay);
		}
		else
			blasterZoomInstance.setZoom(false);
	}
}
