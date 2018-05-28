package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.math.Ease;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.Timeline;
import com.parzivail.util.ui.TimelineEvent;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiScreenTrailer extends GuiScreen
{
	public static boolean hasBeenShown = false;
	private static List<Star> stars = new ArrayList<>();

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

			stars.add(new Star(new Vector2f(x, y), new Vector2f(dX, dY), r.nextFloat() * 1.5f + 0.5f, Fx.Util.GetRgb(c, c, c)));
		}
	}

	private Timeline timeline;
	private ISound titleMusic;
	private TrailerPhase phase = TrailerPhase.LONGTIMEAGO;

	private ResourceLocation brandLogo = Resources.location("textures/brand/logo.png");

	private AnimatedValue longTimeAgoOpacity = new AnimatedValue(0, 1000);
	private AnimatedValue logoScale = new AnimatedValue(1, 10500);
	private AnimatedValue crawlTranslate = new AnimatedValue(0, 75000);
	private AnimatedValue crawlOpacity = new AnimatedValue(1, 2000);
	private AnimatedValue panTranslate = new AnimatedValue(0, 7500);

	public GuiScreenTrailer()
	{
		ResourceLocation res = Resources.location("swg.music.title");
		if (titleMusic != null)
			Client.mc.getSoundHandler().stopSound(titleMusic);
		titleMusic = new PositionedSoundRecord(res, 1, 1, 0, 0, 0);

		ArrayList<TimelineEvent> events = new ArrayList<>();
		events.add(new TimelineEvent(0, (t) -> {
			this.longTimeAgoOpacity.queueAnimatingTo(1);
		}));
		events.add(new TimelineEvent(4000, (t) -> {
			this.longTimeAgoOpacity.queueAnimatingTo(0);
		}));
		events.add(new TimelineEvent(5000, (t) -> {
			this.phase = TrailerPhase.STARWARS;
			this.logoScale.queueAnimatingTo(0);
			Client.mc.getSoundHandler().playSound(titleMusic);
		}));
		events.add(new TimelineEvent(15500, (t) -> {
			this.phase = TrailerPhase.CRAWL;
			this.crawlTranslate.animateTo(500);
		}));
		events.add(new TimelineEvent(88500, (t) -> {
			this.crawlOpacity.queueAnimatingTo(0);
		}));
		events.add(new TimelineEvent(90500, (t) -> {
			this.phase = TrailerPhase.PAN;
			this.panTranslate.queueAnimatingTo(1);
		}));
		events.add(new TimelineEvent(98000, (t) -> {
			this.close();
		}));
		timeline = new Timeline(events);
		timeline.start();
	}

	@Override
	public void updateScreen()
	{
		timeline.tick();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode)
	{
		if (keyCode == Keyboard.KEY_ESCAPE)
		{
			close();
		}
	}

	private void close()
	{
		Client.mc.getSoundHandler().stopSound(titleMusic);
		Client.mc.displayGuiScreen(null);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GL.PushMatrix();

		GL.Disable(EnableCap.Texture2D);

		GL.Color(GLPalette.ALMOST_BLACK);
		Fx.D2.DrawSolidRectangle(0, 0, this.width, this.height);

		GL.Color(GLPalette.WHITE);
		Fx.D2.DrawSolidRectangle(0, this.height - 1, this.width * timeline.getPosition(), 1);

		GL.Enable(EnableCap.Texture2D);

		if (phase == TrailerPhase.LONGTIMEAGO)
		{
			float lerp = longTimeAgoOpacity.getValue() * 255;
			int color = Fx.Util.GetRgb((int)(lerp * 2f / 255), (int)(lerp * 214f / 255), (int)(lerp * 247f / 255));
			String s = "A long time ago in a galaxy far,";
			String s2 = "far away. . . .";
			int textWidth = this.fontRendererObj.getStringWidth(s);
			int xPos = this.width / 2 - textWidth / 2;
			int yPos = this.height / 2 - this.fontRendererObj.FONT_HEIGHT;
			this.fontRendererObj.drawString(s, xPos, yPos, color);
			this.fontRendererObj.drawString(s2, xPos, yPos + this.fontRendererObj.FONT_HEIGHT, color);
		}
		else
		{
			GL.PushAttrib(AttribMask.EnableBit);
			GL.Disable(EnableCap.Texture2D);
			GL.Enable(EnableCap.Blend);
			GL.Enable(EnableCap.PointSmooth);
			GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);

			float lerp = panTranslate.getValue(Ease::inOutQuad);
			for (Star s : stars)
			{
				GL11.glPointSize(1);
				GL.Color(s.color, 128);

				GL.Begin(PrimitiveType.Points);
				GL.Vertex2(s.pos.x * (float)width, wrapHeight(s.pos.y * (float)height - lerp * height / 2f));
				GL.End();
			}
			GL.PopAttrib();

			String s = "[PLANET OR SOMETHING]";
			int textWidth = this.fontRendererObj.getStringWidth(s);
			int xPos = this.width / 2 - textWidth / 2;
			int yPos = this.height - this.fontRendererObj.FONT_HEIGHT * 4;
			this.fontRendererObj.drawString(s, xPos, (int)(yPos + (1 - lerp) * height / 2f), GLPalette.WHITE);

			if (phase == TrailerPhase.STARWARS)
			{
				GL.Color(GLPalette.WHITE);
				GL.Enable(EnableCap.Blend);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				Client.mc.renderEngine.bindTexture(brandLogo);
				GL.Translate(this.width / 2, this.height / 2, 0);
				GL.Scale(logoScale.getValue());
				GL.Translate(-150, -150, 0);
				Fx.D2.DrawSolidRectangle(0, 0, 300, 300);
			}
			else if (phase == TrailerPhase.CRAWL)
			{
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GLU.gluPerspective(70, width / (float)height, 0.1f, 1000);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();

				int w = this.fontRendererObj.getStringWidth("spies managed to steal secret");
				int i = 0;

				GL.Scale(1, -1, -1);

				GL.Translate(-w / 2f, 0, 120);
				GL.Rotate(-55, 1, 0, 0);

				GL.Translate(0, 75 - crawlTranslate.getValue(), 0);

				drawJustifiedLine("It is a period of civil war.", w, i++, true);
				drawJustifiedLine("Rebel spaceships, striking", w, i++, true);
				drawJustifiedLine("from a hidden base, have won", w, i++, true);
				drawJustifiedLine("their first victory against", w, i++, true);
				drawJustifiedLine("the evil Galactic Empire.", w, i++, false);
				i++;
				drawJustifiedLine("During the battle, Rebel", w, i++, true);
				drawJustifiedLine("spies managed to steal secret", w, i++, true);
				drawJustifiedLine("plans to the Empire's", w, i++, true);
				drawJustifiedLine("ultimate weapon, the DEATH", w, i++, true);
				drawJustifiedLine("STAR, an armored space", w, i++, true);
				drawJustifiedLine("station with enough power to", w, i++, true);
				drawJustifiedLine("destroy an entire planet.", w, i++, false);
				i++;
				drawJustifiedLine("Pursued by the Empire's", w, i++, true);
				drawJustifiedLine("sinister agents, Princess", w, i++, true);
				drawJustifiedLine("Leia races home aboard her", w, i++, true);
				drawJustifiedLine("starship, custodian of the", w, i++, true);
				drawJustifiedLine("stolen plans that can save", w, i++, true);
				drawJustifiedLine("her people and restore", w, i++, true);
				drawJustifiedLine("freedom to the galaxy.....", w, i++, false);
			}
		}

		GL.PopMatrix();
	}

	private float wrapHeight(float v)
	{
		if (v < 0)
			return height + v;
		return v;
	}

	private void drawJustifiedLine(String line, int width, int yOffset, boolean justify)
	{
		int y = (int)(yOffset * this.fontRendererObj.FONT_HEIGHT * 1.25f);
		float lerp = crawlOpacity.getValue() * 255;
		int color = Fx.Util.GetRgb((int)(lerp * 255f / 255), (int)(lerp * 212f / 255), (int)(lerp * 0f / 255));

		if (!justify)
		{
			this.fontRendererObj.drawString(line, 0, y, color);
			return;
		}

		char[] characters = line.toCharArray();
		float scalar = width / (float)this.fontRendererObj.getStringWidth(line);

		float x = 0;
		for (char c : characters)
		{
			int charWidth = this.fontRendererObj.getCharWidth(c);
			this.fontRendererObj.drawString(String.valueOf(c), (int)x, y, color);
			x += charWidth * scalar;
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

	private enum TrailerPhase
	{
		LONGTIMEAGO, STARWARS, CRAWL, PAN
	}
}
