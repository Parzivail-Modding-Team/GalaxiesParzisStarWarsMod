package com.parzivail.swg.render.entity;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 12/26/2017.
 */
public class RenderNothing extends Render
{
	public RenderNothing()
	{
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float unknown, float partialTicks)
	{
		if (!Client.mc.gameSettings.showDebugInfo)
			return;

		GL.PushMatrix();
		GL.Translate(x, y + (entity.boundingBox.maxY - entity.boundingBox.minY) / 2f, z);

		float f = 0.025f;
		String n = entity.getClass().getSimpleName();

		FontRenderer fontrenderer = getFontRendererFromRenderManager();
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f, -f, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		fontrenderer.drawString(n, -fontrenderer.getStringWidth(n) / 2, 0, 0x40ffffff);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		fontrenderer.drawString(n, -fontrenderer.getStringWidth(n) / 2, 0, 0xffffffff);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);

		GL.PopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
