package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.lwjgl.Vector2f;
import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.Fx.Util;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiScreenTrailer extends GuiScreen
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

			stars.add(new Star(new Vector2f(x, y), new Vector2f(dX, dY), r.nextFloat() * 1.5f + 0.5f, Util.GetRgb(c, c, c)));
		}
	}

	private final Timeline timeline;
	private final ResourceLocation brandLogo = Resources.location("textures/brand/logo.png");
	private final ResourceLocation crawlPanBottom = Resources.location("textures/environment/openingCrawlPanBottom.png");
	private final AnimatedValue longTimeAgoOpacity = new AnimatedValue(0, 1000);
	private final AnimatedValue logoScale = new AnimatedValue(1, 8500);
	private final AnimatedValue crawlTranslate = new AnimatedValue(0, 85000);
	private final AnimatedValue crawlOpacity = new AnimatedValue(1, 2000);
	private final AnimatedValue panTranslate = new AnimatedValue(0, 7500);
	private ISound titleMusic;
	private TrailerPhase phase = TrailerPhase.LONGTIMEAGO;

	public GuiScreenTrailer()
	{
		ResourceLocation res = Resources.location("swg.music.title");
		if (titleMusic != null)
			Client.mc.getSoundHandler().stopSound(titleMusic);
		titleMusic = new PositionedSoundRecord(res, 1, 1, 0, 0, 0);

		ArrayList<TimelineEvent> events = new ArrayList<>();
		events.add(new TimelineEvent(0, (t) -> longTimeAgoOpacity.queueAnimatingTo(1)));
		events.add(new TimelineEvent(4000, (t) -> longTimeAgoOpacity.queueAnimatingTo(0)));
		events.add(new TimelineEvent(5000, (t) -> {
			phase = TrailerPhase.STARWARS;
			logoScale.queueAnimatingTo(0);
			Client.mc.getSoundHandler().playSound(titleMusic);
		}));
		events.add(new TimelineEvent(11500, (t) -> {
			phase = TrailerPhase.CRAWL;
			crawlTranslate.animateTo(500);
		}));
		events.add(new TimelineEvent(88500, (t) -> crawlOpacity.queueAnimatingTo(0)));
		events.add(new TimelineEvent(90500, (t) -> {
			phase = TrailerPhase.PAN;
			panTranslate.queueAnimatingTo(1);
		}));
		events.add(new TimelineEvent(100000, (t) -> close()));
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
			close();
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
		D2.DrawSolidRectangle(0, 0, width, height);

		GL.Color(GLPalette.WHITE);
		D2.DrawSolidRectangle(0, height - 1, width * timeline.getPosition(), 1);

		GL.Enable(EnableCap.Texture2D);

		if (phase == TrailerPhase.LONGTIMEAGO)
		{
			float lerp = longTimeAgoOpacity.getValue();
			int color = GLPalette.getLerpColor2(lerp, GLPalette.ALMOST_BLACK, 0xFF02D6F7);
			String s = "A long time ago in a galaxy far,";
			String s2 = "far away. . . .";
			GL.PushMatrix();
			int textWidth = fontRendererObj.getStringWidth(s);
			int xPos = -textWidth / 2;
			int yPos = -fontRendererObj.FONT_HEIGHT;
			GL.Translate(width / 2, height / 2, 0);
			GL.Scale(2);
			fontRendererObj.drawString(s, xPos, yPos, color);
			fontRendererObj.drawString(s2, xPos, yPos + fontRendererObj.FONT_HEIGHT, color);
			GL.PopMatrix();
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
				GL.Vertex2(s.pos.x * (float)width, wrapHeight(s.pos.y * (float)height - lerp * height / 1.5f));
				GL.End();
			}
			GL.PopAttrib();

			GL.Color(GLPalette.WHITE);
			GL.Enable(EnableCap.Blend);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			float textureSize = 425;
			float xPos = width / 2 - textureSize / 2;
			float yPos = height - textureSize;
			GL.PushMatrix();
			GL.Translate(xPos, yPos + (1 - lerp) * height / 1.5f, 0);
			Client.mc.renderEngine.bindTexture(crawlPanBottom);
			D2.DrawSolidRectangle(0, 0, textureSize, textureSize);
			GL.PopMatrix();

			if (logoScale.getValue() != 0)
			{
				Client.mc.renderEngine.bindTexture(brandLogo);
				GL.Translate(width / 2, height / 2, 0);
				GL.Scale(logoScale.getValue(Ease::inQuad));
				GL.Translate(-300, -300, 0);
				D2.DrawSolidRectangle(0, 0, 600, 600);
			}

			if (phase == TrailerPhase.CRAWL)
			{
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GLU.gluPerspective(80, width / (float)height, 0.1f, 1000);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();

				int w = 165;
				int i = 0;

				GL.Scale(1, -1, -1);

				GL.Translate(-w / 2f, 0, 120);
				GL.Rotate(-55, 1, 0, 0);

				GL.Translate(0, 85 - crawlTranslate.getValue(), 0);

				drawCenteredLine("PSWG", w, i++);
				//				drawCenteredLine("AN EMPIRE DIVIDED", w, i++);
				i++;
				drawJustifiedLine("There is great tension in", w, i++, true);
				drawJustifiedLine("the galaxy. Rumors of a new", w, i++, true);
				drawJustifiedLine("SUPERWEAPON under construction", w, i++, true);
				drawJustifiedLine("by the Empire stretch from the", w, i++, true);
				drawJustifiedLine("core planets to the Outer Rim.", w, i++, false);
				i++;
				drawJustifiedLine("The days of unity and prosperity", w, i++, true);
				drawJustifiedLine("are gone. Crime syndicates,", w, i++, true);
				drawJustifiedLine("warlords, and pirates target", w, i++, true);
				drawJustifiedLine("peaceful planets and strongarm", w, i++, true);
				drawJustifiedLine("them for dwindling resources.", w, i++, false);
				i++;
				drawJustifiedLine("But there remains hope yet.", w, i++, true);
				drawJustifiedLine("A mysterious band of SMUGGLERS,", w, i++, true);
				drawJustifiedLine("notorious in the underworld,", w, i++, true);
				drawJustifiedLine("have come across secret", w, i++, true);
				drawJustifiedLine("information that may bring", w, i++, true);
				drawJustifiedLine("harmony to the galaxy....", w, i++, false);
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
		GL.PushMatrix();
		float y = yOffset * fontRendererObj.FONT_HEIGHT * 1.25f;
		GL.Translate(0, y, 0);
		float lerp = crawlOpacity.getValue();
		int color = GLPalette.getLerpColor2(lerp, GLPalette.ALMOST_BLACK, GLPalette.SW_YELLOW);

		if (!justify)
		{
			fontRendererObj.drawString(line, 0, 0, color);
			GL.PopMatrix();
			return;
		}

		char[] characters = line.toCharArray();
		float scalar = width / (float)fontRendererObj.getStringWidth(line);

		for (char c : characters)
		{
			int charWidth = fontRendererObj.getCharWidth(c);
			fontRendererObj.drawString(String.valueOf(c), 0, 0, color);
			GL.Translate(charWidth * scalar, 0, 0);
		}
		GL.PopMatrix();
	}

	private void drawCenteredLine(String line, int blockWidth, int yOffset)
	{
		int aedWidth = fontRendererObj.getStringWidth(line);
		int x = blockWidth / 2 - aedWidth / 2;

		int y = (int)(yOffset * fontRendererObj.FONT_HEIGHT * 1.25f);
		float lerp = crawlOpacity.getValue();
		int color = GLPalette.getLerpColor2(lerp, GLPalette.ALMOST_BLACK, GLPalette.SW_YELLOW);

		fontRendererObj.drawString(line, x, y, color);
	}

	private enum TrailerPhase
	{
		LONGTIMEAGO, STARWARS, CRAWL, PAN
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
