package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.ZoneRegistry;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.TextUtil;
import com.parzivail.util.ui.Timeline;
import com.parzivail.util.ui.TimelineEvent;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.world.Zone;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

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

		String str = TextUtil.scrambleString(String.format(I18n.format(Resources.modDot("zone", "common.entering")) + "\n%s", I18n.format(zone.name)), textFadeOutValue.getValue());

		int x = f.FONT_HEIGHT;
		int y = Client.resolution.getScaledHeight() - f.FONT_HEIGHT * 3;

		String[] parts = str.split("\n");
		int yDiff = 0;
		for (String part : parts)
		{
			Client.mc.fontRendererObj.drawString(part, x + 1, y + yDiff + 1, GLPalette.DARK_GREY);
			Client.mc.fontRendererObj.drawString(part, x, y + yDiff, GLPalette.WHITE);
			yDiff += f.FONT_HEIGHT;
		}

		GL.PopAttrib();
		GL.PopMatrix();
	}
}
