package com.parzivail.swg.render.entity;

import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/26/2017.
 */
public class RenderRover extends Render
{
	public RenderRover()
	{
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float unknown, float partialTicks)
	{
		//if (!StarWarsGalaxy.mc.gameSettings.showDebugInfo)
		//	return;

		float dy = (float)((entity.boundingBox.maxY + entity.boundingBox.minY) / 2f);

		GL.PushMatrix();
		GL.Translate(x, y + (entity.boundingBox.maxY - entity.boundingBox.minY) / 2f, z);

		float f = 0.025f;
		String n = entity.getClass().getSimpleName();

		//		FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
		//		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		//		GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		//		GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		//		GL11.glScalef(-f, -f, f);
		//		GL11.glDisable(GL11.GL_LIGHTING);
		//		GL11.glDepthMask(false);
		//		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//		GL11.glEnable(GL11.GL_BLEND);
		//		fontrenderer.drawString(n, -fontrenderer.getStringWidth(n) / 2, 0, 0x40ffffff);
		//		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//		GL11.glDepthMask(true);
		//		fontrenderer.drawString(n, -fontrenderer.getStringWidth(n) / 2, 0, 0xffffffff);
		//		GL11.glEnable(GL11.GL_LIGHTING);
		//		GL11.glDisable(GL11.GL_BLEND);

		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Blend);
		GL.Disable(EnableCap.Texture2D);
		GL11.glDepthMask(true);

		GL11.glColor4f(0, 0, 1, 1);

		float xFrontRight = MathHelper.cos((float)((entity.rotationYaw + 22) / 180 * Math.PI)) * 2.5f;
		float xBackRight = MathHelper.cos((float)((entity.rotationYaw + 157) / 180 * Math.PI)) * 2.5f;
		float xBackLeft = MathHelper.cos((float)((entity.rotationYaw + 203) / 180 * Math.PI)) * 2.5f;
		float xFrontLeft = MathHelper.cos((float)((entity.rotationYaw + 337) / 180 * Math.PI)) * 2.5f;

		float yFrontRight = MathHelper.sin((float)((entity.rotationYaw + 22) / 180 * Math.PI)) * 2.5f;
		float yBackRight = MathHelper.sin((float)((entity.rotationYaw + 157) / 180 * Math.PI)) * 2.5f;
		float yBackLeft = MathHelper.sin((float)((entity.rotationYaw + 203) / 180 * Math.PI)) * 2.5f;
		float yFrontLeft = MathHelper.sin((float)((entity.rotationYaw + 337) / 180 * Math.PI)) * 2.5f;

		float heightFrontRight = entity.worldObj.getHeightValue((int)Math.round(xFrontRight + entity.posX), (int)Math.round(yFrontRight + entity.posZ)) + 1 / 4f;
		float heightBackRight = entity.worldObj.getHeightValue((int)Math.round(xBackRight + entity.posX), (int)Math.round(yBackRight + entity.posZ)) + 1 / 4f;
		float heightBackLeft = entity.worldObj.getHeightValue((int)Math.round(xBackLeft + entity.posX), (int)Math.round(yBackLeft + entity.posZ)) + 1 / 4f;
		float heightFrontLeft = entity.worldObj.getHeightValue((int)Math.round(xFrontLeft + entity.posX), (int)Math.round(yFrontLeft + entity.posZ)) + 1 / 4f;

		Fx.D3.DrawLine(0, 0, 0, xFrontRight, heightFrontRight - dy, yFrontRight);
		Fx.D3.DrawLine(0, 0, 0, xBackRight, heightBackRight - dy, yBackRight);
		Fx.D3.DrawLine(0, 0, 0, xBackLeft, heightBackLeft - dy, yBackLeft);
		Fx.D3.DrawLine(0, 0, 0, xFrontLeft, heightFrontLeft - dy, yFrontLeft);

		Vector3f normal1 = Vector3f.cross(Vector3f.sub(new Vector3f(xBackLeft, heightBackLeft - dy, yBackLeft), new Vector3f(xFrontRight, heightFrontRight - dy, yFrontRight), null), Vector3f.sub(new Vector3f(xBackRight, heightBackRight - dy, yBackRight), new Vector3f(xFrontRight, heightFrontRight - dy, yFrontRight), null), null);
		normal1 = normal1.normalise(null);

		Vector3f normal2 = Vector3f.cross(Vector3f.sub(new Vector3f(xBackLeft, heightBackLeft - dy, yBackLeft), new Vector3f(xFrontLeft, heightFrontLeft - dy, yFrontLeft), null), Vector3f.sub(new Vector3f(xFrontRight, heightFrontRight - dy, yFrontRight), new Vector3f(xFrontLeft, heightFrontLeft - dy, yFrontLeft), null), null);
		normal2 = normal2.normalise(null);

		normal1 = new Vector3f((normal1.x + normal2.x) / 2f, (normal1.y + normal2.y) / 2f, (normal1.z + normal2.z) / 2f);

		Fx.D3.DrawLine(0, 0, 0, normal1.x, normal1.y, normal1.z);

		GL.Enable(EnableCap.Lighting);

		GL.PushMatrix();
		GL.Translate(xFrontRight, heightFrontRight - dy, yFrontRight);
		GL.Rotate(-entity.rotationYaw, 0, 1, 0);
		Fx.D3.DrawSolidTorus(1 / 16f, 1 / 4f, 4, 6);
		GL.PopMatrix();

		GL.PushMatrix();
		GL.Translate(xBackRight, heightBackRight - dy, yBackRight);
		GL.Rotate(-entity.rotationYaw, 0, 1, 0);
		Fx.D3.DrawSolidTorus(1 / 16f, 1 / 4f, 4, 6);
		GL.PopMatrix();

		GL.PushMatrix();
		GL.Translate(xBackLeft, heightBackLeft - dy, yBackLeft);
		GL.Rotate(-entity.rotationYaw, 0, 1, 0);
		Fx.D3.DrawSolidTorus(1 / 16f, 1 / 4f, 4, 6);
		GL.PopMatrix();

		GL.PushMatrix();
		GL.Translate(xFrontLeft, heightFrontLeft - dy, yFrontLeft);
		GL.Rotate(-entity.rotationYaw, 0, 1, 0);
		Fx.D3.DrawSolidTorus(1 / 16f, 1 / 4f, 4, 6);
		GL.PopMatrix();

		GL.Enable(EnableCap.Blend);
		GL.Enable(EnableCap.Texture2D);
		GL11.glDepthMask(false);

		GL.PopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
