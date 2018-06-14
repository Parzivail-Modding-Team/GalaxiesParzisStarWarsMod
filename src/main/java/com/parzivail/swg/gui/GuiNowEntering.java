package com.parzivail.swg.gui;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.Timeline;
import com.parzivail.util.ui.TimelineEvent;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

public class GuiNowEntering
{
	private static Vector3f prevPos = new Vector3f(0, 0, 0);
	private static ArrayList<Zone> zones = new ArrayList<>();
	private static Timeline textFadeOut;
	private static AnimatedValue textFadeOutValue;
	private static Zone showingZone = null;

	static
	{
		zones.add(new Zone(-63, 63, 59, -44, 75, 83, "The Mos Eisley Spaceport"));

		ArrayList<TimelineEvent> keyframes = new ArrayList<>();
		keyframes.add(new TimelineEvent(0, timelineEvent -> {
			textFadeOutValue = new AnimatedValue(0, 1000);
			textFadeOutValue.queueAnimatingTo(1);
		}));
		keyframes.add(new TimelineEvent(3000, timelineEvent -> {
			textFadeOutValue.queueAnimatingTo(0);
		}));
		keyframes.add(new TimelineEvent(4000, timelineEvent -> {
			showingZone = null;
		}));
		textFadeOut = new Timeline(keyframes);
	}

	public static void draw(ScaledResolution sr, EntityPlayer player)
	{
		Vector3f pos = new Vector3f((float)player.posX, (float)player.posY, (float)player.posZ);

		for (Zone zone : zones)
		{
			if (zone.contains(pos) && !zone.contains(prevPos))
			{
				textFadeOut.start();
				showingZone = zone;
				break;
			}
		}
		prevPos = pos;

		textFadeOut.tick();

		if (showingZone != null)
			showEnterZone(sr, showingZone);
	}

	private static void showEnterZone(ScaledResolution sr, Zone zone)
	{
		GL.PushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Enable(EnableCap.Blend);

		FontRenderer f = Client.mc.fontRendererObj;

		String str = scrambleString(String.format("Now entering\n§o%s", zone.name), textFadeOutValue.getValue());

		int x = f.FONT_HEIGHT;
		int y = sr.getScaledHeight() - f.FONT_HEIGHT * 3;

		String[] parts = str.split("\n");
		int yDiff = 0;
		for (String part : parts)
		{
			f.drawString(part, x, y + yDiff, GLPalette.WHITE);
			yDiff += f.FONT_HEIGHT;
		}

		GL.PopAttrib();
		GL.PopMatrix();
	}

	private static String scrambleString(String s, double percent)
	{
		if (percent > 1)
			percent = 1;
		if (percent < 0)
			percent = 0;

		return s.substring(0, (int)(s.length() * percent)) + (percent < 1 ? "§kM§r" : "");
	}

	private static class Zone
	{
		private final int minX;
		private final int minY;
		private final int minZ;
		private final int maxX;
		private final int maxY;
		private final int maxZ;
		private final String name;

		public Zone(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, String name)
		{
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
			this.name = name;
		}

		public boolean contains(Vector3f pos)
		{
			return pos.x >= minX && pos.x <= maxX && pos.y >= minY && pos.y <= maxY && pos.z >= minZ && pos.z <= maxZ;
		}
	}
}
