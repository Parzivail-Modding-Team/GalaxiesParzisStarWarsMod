package com.parzivail.swg.render.entity;

import com.parzivail.swg.ship.MultipartFlightModel;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 12/26/2017.
 */
public abstract class RenderStarfighter extends Render
{
	public RenderStarfighter()
	{
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float unknown, float partialTicks)
	{
		if (!(entity instanceof MultipartFlightModel))
			return;

		MultipartFlightModel ship = (MultipartFlightModel)entity;
		GL.Disable(EnableCap.CullFace);
		GL.Enable(EnableCap.Normalize);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL.PushMatrix();

		GL.Translate(x, y, z);

		GL.Translate(0, ship.verticalCenteringOffset, 0);
		float dYaw = ship.orientation.getYaw() - ship.previousOrientation.getYaw();
		float dPitch = ship.orientation.getPitch() - ship.previousOrientation.getPitch();
		float dRoll = ship.orientation.getRoll() - ship.previousOrientation.getRoll();
		GL11.glRotatef((ship.previousOrientation.getYaw() + dYaw * partialTicks), 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-(ship.previousOrientation.getPitch() + dPitch * partialTicks), 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-(ship.previousOrientation.getRoll() + dRoll * partialTicks), 0.0F, 0.0F, 1.0F);
		GL.Translate(0, -ship.verticalCenteringOffset, 0);

		GL.Translate(0, ship.verticalGroundingOffset, 0);

		doRender(ship, partialTicks);

		GL.PopMatrix();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL.Disable(EnableCap.Normalize);
		GL.Enable(EnableCap.CullFace);
	}

	public abstract void doRender(MultipartFlightModel ship, float partialTicks);

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
