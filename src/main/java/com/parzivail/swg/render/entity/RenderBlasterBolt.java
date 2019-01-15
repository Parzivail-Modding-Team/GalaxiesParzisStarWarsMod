package com.parzivail.swg.render.entity;

import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.render.lightsaber.RenderLightsaber;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

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

		double dx = e.getDx();
		double dy = e.getDy();
		double dz = e.getDz();
		float len = e.getLength();

		double d3 = (double)MathHelper.sqrt_double(dx * dx + dz * dz);
		float yaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)(-(Math.atan2(dy, d3) * 180.0D / Math.PI));

		GL.Rotate(90 - yaw, 0, 1, 0);
		GL.Rotate(pitch + 90, 0, 0, 1);
		GL.Scale(0.5);
		RenderLightsaber.renderBlade(len / 2, 0, e.getColor(), GLPalette.WHITE, false);

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
