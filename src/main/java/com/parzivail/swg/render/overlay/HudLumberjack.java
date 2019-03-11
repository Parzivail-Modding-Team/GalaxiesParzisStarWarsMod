package com.parzivail.swg.render.overlay;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Iterator;

public class HudLumberjack
{
	private final HashMap<String, HudDebugEntry> debugEntries = new HashMap<>();

	public void debug(String category, String text)
	{
		HudDebugEntry newEntry = new HudDebugEntry(System.currentTimeMillis(), 2000, text);

		if (debugEntries.containsKey(category))
			debugEntries.replace(category, newEntry);
		else
			debugEntries.put(category, newEntry);
	}

	private void tick()
	{
		long now = System.currentTimeMillis();

		Iterator<HashMap.Entry<String, HudDebugEntry>> iterator = debugEntries.entrySet().iterator();
		while (iterator.hasNext())
		{
			HashMap.Entry<String, HudDebugEntry> entry = iterator.next();
			HudDebugEntry debugEntry = entry.getValue();

			if (debugEntry.timeCreated + debugEntry.life <= now)
				iterator.remove();
		}
	}

	public void render()
	{
		tick();

		GL.PushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Enable(EnableCap.Texture2D);
		GL.Enable(EnableCap.Blend);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		long now = System.currentTimeMillis();

		int n = 0;
		for (HashMap.Entry<String, HudDebugEntry> entry : debugEntries.entrySet())
		{
			HudDebugEntry debugEntry = entry.getValue();
			int c = GLPalette.WHITE;
			c &= 0x00FFFFFF;
			c |= (int)((1 - (now - debugEntry.timeCreated) / (float)debugEntry.life) * 247 + 8) << 24;
			Client.mc.fontRendererObj.drawString(String.format("%s: %s", entry.getKey(), entry.getValue().text), 5, 30 + n * Client.mc.fontRendererObj.FONT_HEIGHT, c);
			n++;
		}

		GLPalette.glColorI(GLPalette.WHITE);
		GL.PopAttrib();
		GL.PopMatrix();
	}

	private class HudDebugEntry
	{
		long timeCreated;
		int life;
		String text;

		HudDebugEntry(long timeCreated, int life, String text)
		{
			this.timeCreated = timeCreated;
			this.life = life;
			this.text = text;
		}
	}
}
