package com.parzivail.util.fx;

import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class AnimatedParticle extends EntityFX
{
	private final ResourceLocation texture;
	private final int rows;
	private final int cols;
	private final float oneOverRows;
	private final float oneOverCols;

	public AnimatedParticle(World parWorld, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ, ResourceLocation texture, int rows, int cols)
	{
		super(parWorld, parX, parY, parZ, parMotionX, parMotionY, parMotionZ);
		this.texture = texture;
		this.rows = rows;
		this.cols = cols;
		oneOverRows = 1f / this.rows;
		oneOverCols = 1f / this.cols;
		motionX = parMotionX;
		motionY = parMotionY;
		motionZ = parMotionZ;
	}

	public void renderParticle(Tessellator tessellator, float partialTicks, float rX, float rXZ, float rZ, float rYZ, float rXY)
	{
		float life = (particleAge + partialTicks) / (float)particleMaxAge;
		float opacity = getOpacity(life, partialTicks);
		float scale = getScale(life, partialTicks);

		int frame = Math.round(life * (cols - 1) * (rows - 1));

		float u = (frame % cols) * oneOverCols;
		float v = (float)(Math.floor(frame / (float)cols) * oneOverRows);

		float uMax = u + oneOverCols;
		float vMax = v + oneOverRows;

		float f11 = (float)(prevPosX + (posX - prevPosX) * (double)partialTicks - EntityFX.interpPosX);
		float f12 = (float)(prevPosY + (posY - prevPosY) * (double)partialTicks - EntityFX.interpPosY);
		float f13 = (float)(prevPosZ + (posZ - prevPosZ) * (double)partialTicks - EntityFX.interpPosZ);

		GL.PushAttrib(AttribMask.EnableBit);
		GL.PushAttrib(AttribMask.TextureBit);

		GL.Disable(EnableCap.Lighting);
		GL.Enable(EnableCap.Blend);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null)
		{
			mc.renderEngine.bindTexture(texture);
			mc.entityRenderer.disableLightmap(0);
		}

		GL.Color(1, 1, 1, opacity);

		GL.Begin(PrimitiveType.Quads);
		GL.TexCoord2((double)uMax, (double)vMax);
		GL.Vertex3((double)(f11 - rX * scale - rYZ * scale), (double)(f12 - rXZ * scale), (double)(f13 - rZ * scale - rXY * scale));

		GL.TexCoord2((double)uMax, (double)v);
		GL.Vertex3((double)(f11 - rX * scale + rYZ * scale), (double)(f12 + rXZ * scale), (double)(f13 - rZ * scale + rXY * scale));

		GL.TexCoord2((double)u, (double)v);
		GL.Vertex3((double)(f11 + rX * scale + rYZ * scale), (double)(f12 + rXZ * scale), (double)(f13 + rZ * scale + rXY * scale));

		GL.TexCoord2((double)u, (double)vMax);
		GL.Vertex3((double)(f11 + rX * scale - rYZ * scale), (double)(f12 - rXZ * scale), (double)(f13 + rZ * scale - rXY * scale));
		GL.End();

		GL.PopAttrib();
		GL.PopAttrib();
	}

	private float getScale(float life, float partialTicks)
	{
		return 2;
	}

	private float getOpacity(float life, float partialTicks)
	{
		return (float)(1 / ((1 + Math.pow(2, -(40 * life - 10))) * (1 + Math.pow(2, 40 * life - 32))));
	}
}
