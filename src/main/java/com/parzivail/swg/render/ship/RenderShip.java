package com.parzivail.swg.render.ship;

import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.swg.entity.ship.ShipData;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 12/26/2017.
 */
public class RenderShip extends Render
{
	private final IEntityRenderer model;

	public RenderShip(IEntityRenderer model)
	{
		this.model = model;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float unknown, float partialTicks)
	{
		if (!(entity instanceof EntityShip))
			return;

		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		EntityShip ship = (EntityShip)entity;
		float dYaw = MathHelper.wrapAngleTo180_float(ship.orientation.getYaw() - ship.previousOrientation.getYaw());
		float dPitch = wrapAngleTo90_float(ship.orientation.getPitch() - ship.previousOrientation.getPitch());
		float dRoll = MathHelper.wrapAngleTo180_float(ship.orientation.getRoll() - ship.previousOrientation.getRoll());
		float yaw = MathHelper.wrapAngleTo180_float(ship.previousOrientation.getYaw() + dYaw * partialTicks);
		float pitch = wrapAngleTo90_float(ship.previousOrientation.getPitch() + dPitch * partialTicks);
		float roll = MathHelper.wrapAngleTo180_float(ship.previousOrientation.getRoll() + dRoll * partialTicks);

		// keep camera from doing a 360 in one tick (0-1 partialTicks) when (yaw - prevYaw) ~ 360deg
		float slidDYaw = ship.slidingYaw.getOldAverage() + (ship.slidingYaw.getAverage() - ship.slidingYaw.getOldAverage()) * partialTicks;
		float slidDPitch = ship.slidingPitch.getOldAverage() + (ship.slidingPitch.getAverage() - ship.slidingPitch.getOldAverage()) * partialTicks;

		ShipData data = ship.getData();

		if (data.isAirVehicle)
			roll += slidDYaw;
		else
		{
			slidDPitch = 0;
			slidDYaw = 0;
		}

		EntityPlayer client = Client.getPlayer();
		//		if (Client.mc.renderViewEntity == ship.camera && client != null && client.ridingEntity instanceof EntitySeat && ((EntitySeat)client.ridingEntity).getParent() == ship)
		//		{
		//			float camDist = ship.camera.getCamDist(partialTicks);
		//			float shipPitch = pitch - slidDPitch;
		//			RotatedAxes ra = new RotatedAxes(yaw - slidDYaw, shipPitch, roll);
		//			Vector3f forward = ra.findLocalVectorGlobally(new Vector3f(0, 0, camDist));
		//
		//			GL.Translate(forward.x, forward.y, forward.z);
		//		}
		//		else
		GL.Translate(x, y, z);

		GL.Enable(EnableCap.Texture2D);

		// TODO: fix
		//FxMC.enableSunBasedLighting(ship, partialTicks);

		GL.Translate(0, data.verticalCenteringOffset, 0);
		GL11.glRotatef(yaw + slidDYaw / 10, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-pitch - slidDPitch, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-roll, 0.0F, 0.0F, 1.0F);
		GL.Translate(0, -data.verticalCenteringOffset, 0);

		GL.Translate(0, data.verticalGroundingOffset, 0);

		GL.Rotate(-90, 1, 0, 0);
		GL.Rotate(-90, 0, 0, 1);

		model.doRender(renderManager, entity, x, y, z, partialTicks);

		//		GL.Disable(EnableCap.Texture2D);
		//		Fx.D3.DrawSolidBox();
		//
		//		GL.Disable(EnableCap.Lighting);
		//		GL.PushMatrix();
		//		GL.Scale(0.25f);
		//		GL11.glLineWidth(2);
		//		GL.Color(1f, 0, 0);
		//		GL.Begin(PrimitiveType.LineStrip);
		//		GL.Vertex3(0.0D, 0.0D, 0.0D);
		//		GL.Vertex3((double)10, 0.0D, 0.0D);
		//		GL.End();
		//
		//		GL.Color(0, 1f, 0);
		//		GL.Begin(PrimitiveType.LineStrip);
		//		GL.Vertex3(0.0D, 0.0D, 0.0D);
		//		GL.Vertex3(0.0D, (double)10, 0.0D);
		//		GL.End();
		//
		//		GL.Color(0, 0, 1f);
		//		GL.Begin(PrimitiveType.LineStrip);
		//		GL.Vertex3(0.0D, 0.0D, 0.0D);
		//		GL.Vertex3(0.0D, 0.0D, (double)10);
		//		GL.End();
		//		GL.PopMatrix();

		GL.PopAttrib();
		GL11.glPopMatrix();
	}

	public static float wrapAngleTo90_float(float a)
	{
		a %= 180.0F;

		if (a >= 90)
			a -= 180;

		if (a < -90)
			a += 180;

		return a;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
