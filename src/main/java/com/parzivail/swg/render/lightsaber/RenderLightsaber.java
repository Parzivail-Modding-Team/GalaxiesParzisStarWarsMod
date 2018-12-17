package com.parzivail.swg.render.lightsaber;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderLightsaber implements IItemRenderer
{
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return type == ItemRenderType.ENTITY;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);

		Client.mc.entityRenderer.disableLightmap(0);

		float r = 0.3f;
		float g = 0.3f;
		float b = 1f;

		float length = 2;

		GL.Color(r, g, b, 1f / 20);
		for (int layer = 0; layer < 20; layer++)
		{
			GL.Color(r, g, b, 0.1f / 20 * layer);
			Fx.D3.DrawSolidBoxSkew(0.18 - (0.1f / 20) * layer, 0, length, 0, 0, 0, 0);
		}

		GL.Color(1, 1, 1, 1);
		Fx.D3.DrawSolidBoxSkew(0.06f, 0, length, 0, 0, 0, 0);

		GL11.glDepthMask(true);
		GL.PopAttrib();
		GL11.glPopMatrix();
	}
}
