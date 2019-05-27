package com.parzivail.swg.animation;

import com.parzivail.swg.proxy.SwgClientProxy;
import com.parzivail.util.animation.Animatable;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.util.Random;

public class HyperspaceEnter extends Animatable
{
	private Vector3f[] stars;

	public HyperspaceEnter()
	{
		super("hyperspaceEnter", 2000);
		stars = new Vector3f[2000];
	}

	@Override
	public void reset()
	{
		Random r = new Random(0);

		for (int i = 0; i < stars.length; i++)
		{
			float x = r.nextFloat() * 2 - 1;
			float y = r.nextFloat() * 2 - 1;
			float c = r.nextFloat() * 0.1f + 0.9f;

			stars[i] = new Vector3f(x, y, c);
		}
	}

	@Override
	public void tick(float t)
	{
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1);
		GlStateManager.disableAlpha();
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();

		GlStateManager.pushMatrix();

		ScaledResolution scaledRes = new ScaledResolution(SwgClientProxy.mc);
		double w = scaledRes.getScaledWidth();
		double h = scaledRes.getScaledHeight();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(60, (float)(w / h), 0.1f, 1000);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL.Scale(1, -1, -1);

		//		GlStateManager.translate(w / 2, h / 2, 0);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder b = tessellator.getBuffer();

		b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		double start = 1.5 * t * t * t + -0.75 * t * t + 0.25 * t + 0.005;
		double end = Math.pow(t, 20);

		GL11.glLineWidth(2);

		float c = 2.5f;

		for (Vector3f star : stars)
		{
			double zEnd = (1 - end) * (900 + star.z * 99);
			double zStart = (1 - start) * (900 + star.z * 99);

			b.pos(star.x * w * c, star.y * h * c + 1, zEnd).color(star.z, star.z, star.z, 1).endVertex();
			b.pos(star.x * w * c, star.y * h * c + 1, zStart).color(star.z, star.z, star.z, 1).endVertex();
			b.pos(star.x * w * c, star.y * h * c - 1, zStart).color(star.z, star.z, star.z, 1).endVertex();
			b.pos(star.x * w * c, star.y * h * c - 1, zEnd).color(star.z, star.z, star.z, 1).endVertex();

			b.pos(star.x * w * c + 1, star.y * h * c, zEnd).color(star.z, star.z, star.z, 1).endVertex();
			b.pos(star.x * w * c + 1, star.y * h * c, zStart).color(star.z, star.z, star.z, 1).endVertex();
			b.pos(star.x * w * c - 1, star.y * h * c, zStart).color(star.z, star.z, star.z, 1).endVertex();
			b.pos(star.x * w * c - 1, star.y * h * c, zEnd).color(star.z, star.z, star.z, 1).endVertex();
		}

		tessellator.draw();

		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
