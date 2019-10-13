package com.parzivail.swg.render.item;

import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.render.util.RenderBeam;
import com.parzivail.util.ui.gltk.GL;
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

		GL.PushMatrix();

		GL.Translate(0.5f, 0.5f, 0.5f);

		RenderBeam.render(0.6f, 1.5f, 0.037f, 19, 1.275f, true, 0xFFFFFFFF, true, 0xFF0000FF, 0, 0, true);

		GL.PopMatrix();
	}
}
