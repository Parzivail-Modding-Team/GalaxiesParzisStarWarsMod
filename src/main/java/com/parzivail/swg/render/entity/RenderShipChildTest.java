package com.parzivail.swg.render.entity;

import com.parzivail.swg.entity.ship.EntitySeat;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 12/26/2017.
 */
public class RenderShipChildTest extends Render
{
	public RenderShipChildTest()
	{
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float unknown, float partialTicks)
	{
		if (!(entity instanceof EntitySeat))
			return;

		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);

		GL.Translate(x, y, z);
		GL.Translate(0, 0.5f, 0);
		GL.Disable(EnableCap.Texture2D);

		Fx.D3.DrawSolidBox();

		GL.PopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
