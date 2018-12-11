package com.parzivail.swg.render.overlay;

import java.util.HashMap;
import java.util.Iterator;

public class HudLumberjack
{
	private final HashMap<String, HudDebugEntry> debugEntries = new HashMap<>();

	public void debug(String category, String text)
	{
		HudDebugEntry newEntry = new HudDebugEntry(System.currentTimeMillis(), 2000, category, text);

		if (debugEntries.containsKey(category))
			debugEntries.replace(category, newEntry);
		else
			debugEntries.put(category, newEntry);
	}

	public void tick()
	{
		long now = System.currentTimeMillis();

		Iterator<HashMap.Entry<String, HudDebugEntry>> iterator = debugEntries.entrySet().iterator();
		while (iterator.hasNext())
		{
			HashMap.Entry<String, HudDebugEntry> entry = iterator.next();
			HudDebugEntry debugEntry = entry.getValue();

			if (debugEntry.timeCreated + debugEntry.life < now)
				iterator.remove();
		}
	}

	private class HudDebugEntry
	{
		long timeCreated;
		int life;
		String category;
		String text;

		HudDebugEntry(long timeCreated, int life, String category, String text)
		{
			this.timeCreated = timeCreated;
			this.life = life;
			this.category = category;
			this.text = text;
		}
	}
}
