package com.parzivail.swg.render.item;

import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.render.util.RenderBeam;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class RenderItemLightsaber extends TileEntityItemStackRenderer
{
	public RenderItemLightsaber()
	{
	}

	@Override
	public void renderByItem(ItemStack itemStackIn)
	{
		float partialTicks = Client.partialTicks;

		RenderBeam.render(1, 0.037f, 19, 1.275f, true, 0xFFFFFF, true, 0x0000FF, 0, 0);
	}
}
