package com.parzivail.pswg.client.zoom;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterAttachmentFunction;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import io.github.ennuil.libzoomer.api.ZoomOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;

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
			var bd = BlasterItem.getBlasterDescriptor(stack);

			blasterZoomInstance.setZoom(bt.isAimingDownSights);
			blasterZoomInstance.setZoomDivisor(b.getFovMultiplier(stack, mc.world, mc.player));

			ZoomOverlay overlay = null;

			if (mc.options.getPerspective() == Perspective.FIRST_PERSON)
			{
				for (var attachment : bd.attachmentMap.values())
				{
					if ((attachment.bit & bt.attachmentBitmask) != 0)
					{
						if (attachment.function == BlasterAttachmentFunction.DEFAULT_SCOPE)
						{
							overlay = Client.blasterZoomOverlayDefault;
							break;
						}
						else if (attachment.function == BlasterAttachmentFunction.SNIPER_SCOPE)
						{
							overlay = Client.blasterZoomOverlaySniper;
							break;
						}
					}
				}
			}

			blasterZoomInstance.setZoomOverlay(overlay);
		}
		else
			blasterZoomInstance.setZoom(false);
	}
}
