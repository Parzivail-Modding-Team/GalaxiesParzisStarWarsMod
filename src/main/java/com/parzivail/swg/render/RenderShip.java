package com.parzivail.swg.render;

import com.parzivail.swg.entity.EntityShip;
import com.parzivail.util.math.lwjgl.Matrix4f;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.nio.FloatBuffer;

/**
 * Created by colby on 12/26/2017.
 */
public class RenderShip extends Render<EntityShip>
{
	private FloatBuffer buff = BufferUtils.createFloatBuffer(16);
	public RenderShip(RenderManager renderManagerIn)
	{
		super(renderManagerIn);
		this.shadowSize = 0.5F;
	}

	@Override
	public void doRender(EntityShip ship, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		Matrix4f rotation = ship.getRotation(partialTicks);

		//		float dYaw = MathHelper.wrapDegrees(rotation.getYaw() - prevRotation.getYaw());
		//		float dPitch = wrapAngleTo90(rotation.getPitch() - prevRotation.getPitch());
		//		float dRoll = MathHelper.wrapDegrees(rotation.getRoll() - prevRotation.getRoll());
		//		float yaw = MathHelper.wrapDegrees(prevRotation.getYaw() + dYaw * partialTicks);
		//		float pitch = wrapAngleTo90(prevRotation.getPitch() + dPitch * partialTicks);
		//		float roll = MathHelper.wrapDegrees(prevRotation.getRoll() + dRoll * partialTicks);
		//
		//		float slidDYaw = ship.slidingYaw.getOldAverage() + (ship.slidingYaw.getAverage() - ship.slidingYaw.getOldAverage()) * partialTicks;
		//		float slidDPitch = ship.slidingPitch.getOldAverage() + (ship.slidingPitch.getAverage() - ship.slidingPitch.getOldAverage()) * partialTicks;
		//
		//		roll += slidDYaw;

		float dYaw = MathHelper.wrapDegrees(ship.slidingYaw.getOldAverage() - ship.slidingYaw.getAverage());
		rotation = Matrix4f.rotate(dYaw / 180 * (float)Math.PI, new Vector3f(0, 0, 1), rotation, null);

		GL.Enable(EnableCap.Texture2D);

		// TODO: fix
		//FxMC.enableSunBasedLighting(ship, partialTicks);

		Matrix4f translation = Matrix4f.translate(new Vector3f((float)x, (float)y + 0.5f, (float)z), new Matrix4f(), null);
		Matrix4f mat = Matrix4f.mul(translation, rotation, null);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		buff.clear();
		mat.store(buff);
		buff.flip();
		GL11.glMultMatrix(buff);

		GL.Disable(EnableCap.Texture2D);
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

		GL.Color(0xFFFFFFFF);

		GL.PopAttrib();
		GL11.glPopMatrix();
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityShip entity)
	{
		return null;
	}

	public static float wrapAngleTo90(float a)
	{
		a %= 180.0F;

		if (a >= 90)
			a -= 180;

		if (a < -90)
			a += 180;

		return a;
	}
}
