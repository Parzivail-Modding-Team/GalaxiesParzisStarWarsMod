package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.ZoneRegistry;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.common.TextUtils;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.Timeline;
import com.parzivail.util.ui.TimelineEvent;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.world.Zone;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import java.util.ArrayList;
import java.util.EnumSet;

public class GuiNowEntering
{
	private static final Timeline textFadeOut;
	private static Vector3f prevPos = new Vector3f(0, 0, 0);
	private static AnimatedValue textFadeOutValue;
	private static Zone showingZone;

	static
	{
		ArrayList<TimelineEvent> keyframes = new ArrayList<>();
		keyframes.add(new TimelineEvent(0, timelineEvent -> {
			textFadeOutValue = new AnimatedValue(0, 1000);
			textFadeOutValue.queueAnimatingTo(1);
		}));
		keyframes.add(new TimelineEvent(3000, timelineEvent -> textFadeOutValue.queueAnimatingTo(0)));
		keyframes.add(new TimelineEvent(4000, timelineEvent -> showingZone = null));
		textFadeOut = new Timeline(keyframes);
	}

	public static void draw(EntityPlayer player)
	{
		Vector3f pos = new Vector3f((float)player.posX, (float)player.posY, (float)player.posZ);

		for (Zone zone : ZoneRegistry.zones)
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
			showEnterZone(showingZone);
	}

	private static void showEnterZone(Zone zone)
	{
		GL.PushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Enable(EnableCap.Blend);

		FontRenderer f = Client.mc.fontRendererObj;

		String str = TextUtils.scrambleString(String.format(I18n.format(Resources.modDot("zone", "common.entering")) + "\n%s", I18n.format(zone.name)), textFadeOutValue.getValue());

		int x = f.FONT_HEIGHT;
		int y = Client.resolution.getScaledHeight() - f.FONT_HEIGHT * 3;

		String[] parts = str.split("\n");
		int yDiff = 0;
		for (String part : parts)
		{
			drawStringWithShadow(part, x, y + yDiff, GLPalette.WHITE);
			yDiff += f.FONT_HEIGHT;
		}

		GL.PopAttrib();
		GL.PopMatrix();
	}

	private static void drawStringWithShadow(String text, int x, int y, int color)
	{
		ScaledResolution sr = Client.resolution;
		float oneOverSr = 1f / sr.getScaleFactor();

		GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit));
		GL.Enable(EnableCap.Blend);
		GL.Enable(EnableCap.Texture2D);
		GL.PushMatrix();
		GL.Translate(x, y, 0);
		GL.Scale(oneOverSr);
		TextureImpl.bindNone();
		Client.latoSemibold.drawString(2, 2, text, Color.black);
		Client.latoSemibold.drawString(0, 0, text, Fx.Util.GetColor(color));
		GL.PopMatrix();
		GL.PopAttrib();
	}
}
