package com.parzivail.swg.render.entity;

import com.parzivail.swg.entity.EntityShipParentTest;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 12/26/2017.
 */
public class RenderShipParentTest extends Render
{
	public RenderShipParentTest()
	{
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float unknown, float partialTicks)
	{
		if (!(entity instanceof EntityShipParentTest))
			return;

		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);

		EntityShipParentTest ship = (EntityShipParentTest)entity;

		if (ship.riddenByEntity == Client.getPlayer() || (ship.seats[0] != null && ship.seats[0].riddenByEntity == Client.getPlayer()))
			GL.Translate(0, -1.75f, 0);
		else
			GL.Translate(x, y, z);
		GL.Translate(0, 0.5f, 0);
		GL.Disable(EnableCap.Texture2D);

		float dYaw = MathHelper.wrapAngleTo180_float(ship.orientation.getYaw() - ship.previousOrientation.getYaw());
		float dPitch = MathHelper.wrapAngleTo180_float(ship.orientation.getPitch() - ship.previousOrientation.getPitch());
		float dRoll = MathHelper.wrapAngleTo180_float(ship.orientation.getRoll() - ship.previousOrientation.getRoll());
		GL11.glRotatef((ship.previousOrientation.getYaw() + dYaw * partialTicks), 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-(ship.previousOrientation.getPitch() + dPitch * partialTicks), 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-(ship.previousOrientation.getRoll() + dRoll * partialTicks), 0.0F, 0.0F, 1.0F);

		Fx.D3.DrawSolidBox();

		GL.Disable(EnableCap.Lighting);
		GL.PushMatrix();
		GL.Scale(0.25f);
		GL11.glLineWidth(2);
		GL.Color(1f, 0, 0);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3((double)10, 0.0D, 0.0D);
		GL.End();

		GL.Color(0, 1f, 0);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3(0.0D, (double)10, 0.0D);
		GL.End();

		GL.Color(0, 0, 1f);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3(0.0D, 0.0D, (double)10);
		GL.End();
		GL.PopMatrix();

		GL.PopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
