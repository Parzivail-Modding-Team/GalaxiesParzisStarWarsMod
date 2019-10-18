package com.parzivail.swg.render.item;

import com.parzivail.swg.item.data.LightsaberData;
import com.parzivail.swg.item.data.LightsaberDescriptor;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.render.util.RenderBeam;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class RenderItemLightsaber extends TileEntityItemStackRenderer
{
	public RenderItemLightsaber()
	{
	}

	@Override
	public void renderByItem(ItemStack item)
	{
		float partialTicks = Client.mc.getRenderPartialTicks();

		LightsaberData bd = new LightsaberData(item);
		LightsaberDescriptor d = bd.descriptor;

		if (d == null)
			d = LightsaberDescriptor.BLANK;

		GL.PushMatrix();

		GL.Translate(0.5f, 0.5f, 0.5f);

		float length = d.bladeLength * MathHelper.clamp((bd.openAnimation + partialTicks * bd.openingState - bd.openingState) / 4f, 0, 1);
		RenderBeam.render(0.6f, length, 0.037f, 19, 0.1f, true, d.coreColor, true, d.bladeColor, d.unstable ? 0.002f : 0, d.unstable ? 0.006f : 0, true);

		GL.PopMatrix();
	}
}
