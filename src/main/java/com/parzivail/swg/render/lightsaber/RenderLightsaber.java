package com.parzivail.swg.render.lightsaber;

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
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL.Translate(0, 1, 0);

		for (int layer = 0; layer < 10; layer++)
		{
			GL.Color(0, 0, 1, 0.005 * layer);

			GL.PushMatrix();
			GL.Scale(1 - 0.05f * layer);
			Fx.D3.DrawSolidBox();
			GL.PopMatrix();
		}

		GL.Color(1, 1, 1, 0.95);

		GL.PushMatrix();
		GL.Scale(1 - 0.05f * 10);
		Fx.D3.DrawSolidBox();
		GL.PopMatrix();

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL.PopAttrib();
		GL11.glPopMatrix();
	}
}
