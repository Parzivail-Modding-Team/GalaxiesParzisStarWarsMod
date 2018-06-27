package com.parzivail.swg.gui;

import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.Fx.Util;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiHyperdrive
{
	private static final List<Star> stars = new ArrayList<>();

	static
	{
		for (int i = 0; i < 5000; i++)
		{
			Random r = new Random();

			float x = r.nextFloat();
			float y = r.nextFloat();
			int c = (int)(r.nextFloat() * 255);

			float s = 1;//(float)Math.sqrt(Math.pow(x - 0.5, 2) + Math.pow(y - 0.5, 2));

			float dX = (x - 0.5f) / s;
			float dY = (y - 0.5f) / s;

			stars.add(new Star(new Vector2f(x, y), new Vector2f(dX, dY), r.nextFloat() * 1.5f + 0.5f, Util.GetRgba(c, c, c, 255)));
		}
	}

	private static float getScaleFactor(float t)
	{
		return (float)Math.pow((t + 1.5f) / 3, 10);
	}

	public static void draw(ScaledResolution sr, EntityPlayer player)
	{
		float t = (Util.GetMillis() % 3000) / 1000f;
		float l = getScaleFactor(t);
		for (Star s : stars)
		{
			GL11.glLineWidth(s.size);
			GLPalette.glColorI(s.color);

			GL.Begin(PrimitiveType.LineStrip);

			GL.Vertex2(s.pos.x * sr.getScaledWidth_double(), s.pos.y * sr.getScaledHeight_double());
			GL.Vertex2((s.pos.x + s.dir.x * l) * sr.getScaledWidth_double(), (s.pos.y + s.dir.y * l) * sr.getScaledHeight_double());

			GL.End();
		}

		if (t >= 2)
		{
			GL11.glColor4f(1, 1, 1, getScaleFactor((t - 2) * 2));
			D2.DrawSolidRectangle(0, 0, (float)sr.getScaledWidth_double(), (float)sr.getScaledHeight_double());
		}
	}

	private static class Star
	{
		public final Vector2f pos;
		public final Vector2f dir;
		public final float size;
		public final int color;

		public Star(Vector2f pos, Vector2f dir, float size, int color)
		{
			this.pos = pos;
			this.dir = dir;
			this.size = size;
			this.color = color;
		}
	}
}
