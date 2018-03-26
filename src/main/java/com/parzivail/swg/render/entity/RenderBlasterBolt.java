package com.parzivail.swg.render.entity;

import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 12/26/2017.
 */
public class RenderBlasterBolt extends Render
{
	public RenderBlasterBolt()
	{
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float unknown, float partialTicks)
	{
		if (!(entity instanceof EntityBlasterBolt))
			return;

		EntityBlasterBolt e = ((EntityBlasterBolt)entity);

		GL.PushMatrix();

		GL.Translate(x, y, z);

		GL.PushAttrib(AttribMask.EnableBit);
		GL.PushAttrib(AttribMask.LineBit);

		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Texture2D);
		GL.Disable(EnableCap.Blend);
		Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

		GL11.glLineWidth(5);

		GLPalette.glColorI(e.getColor(), 255);

		float dx = e.getDx();
		float dy = e.getDy();
		float dz = e.getDz();
		float len = e.getLength();

		Fx.D3.DrawLine(0, 0, 0, dx * len, dy * len, dz * len);

		GL.PopAttrib();
		GL.PopAttrib();
		GL.PopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
