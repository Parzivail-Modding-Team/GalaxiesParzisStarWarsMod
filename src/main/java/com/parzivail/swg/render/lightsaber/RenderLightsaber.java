package com.parzivail.swg.render.lightsaber;

import com.parzivail.swg.item.lightsaber.LightsaberData;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
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
		LightsaberData bd = new LightsaberData(item);

		int bladeColor = GLPalette.ELECTRIC_BLUE;
		int coreColor = GLPalette.WHITE;
		float length = 3 * MathHelper.clamp_float((bd.openAnimation + Client.renderPartialTicks * bd.openingState - bd.openingState) / 4f, 0, 1);

		switch (type)
		{
			case ENTITY:
				break;
			case EQUIPPED:
				GL.Rotate(13, 0, 0, 1);
				GL.Rotate(15.5f, 1, 0, 0);
				GL.Rotate(10f, 0, 1, 0);
				GL.Translate(0.425f, 0.2f, 0);
				break;
			case EQUIPPED_FIRST_PERSON:
				GL.Rotate(20, 0, 0, 1);
				//				GL.Rotate(10.5f, 1, 0, 0);
				//				GL.Rotate(10f, 0, 1, 0);
				GL.Translate(0.6f, 0.6f, 0);
				break;
			case INVENTORY:
				GL.Scale(15);
				GL.Rotate(-135, 0, 0, 1);
				GL.Translate(-0.75f, 0.5f, 0);
				break;
		}

		renderBlade(bladeColor, coreColor, length);
	}

	private static void renderBlade(int bladeColor, int coreColor, float length)
	{
		if (length == 0)
			return;

		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);

		Client.mc.entityRenderer.disableLightmap(0);

		for (int layer = 0; layer < 20; layer++)
		{
			GL.Color(bladeColor, (int)(1.275f * layer));
			Fx.D3.DrawSolidBoxSkew(0.16 - 0.0058f * layer, 0, length, 0, 0, 0, 0);
		}

		GL.Color(coreColor);
		Fx.D3.DrawSolidBoxSkew(0.035f, 0, length, 0, 0, 0, 0);

		GL11.glDepthMask(true);
		GL.PopAttrib();
		GL11.glPopMatrix();
	}
}
