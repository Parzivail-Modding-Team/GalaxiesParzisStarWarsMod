package com.parzivail.swg.npc;

import com.google.gson.Gson;
import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.binary.PIO;
import net.minecraft.util.ResourceLocation;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class NpcLoader
{
	private static final HashMap<String, NpcEntry> loadedNpcs = new HashMap<>();

	public NpcEntry getNpc(String id)
	{
		if (loadedNpcs.containsKey(id))
			return loadedNpcs.get(id);

		ResourceLocation resourceLocation = Resources.location(String.format("npcs/%s.json", id));
		InputStreamReader stream = new InputStreamReader(PIO.getResource(StarWarsGalaxy.class, resourceLocation), StandardCharsets.UTF_8);
		Gson gson = new Gson();
		NpcEntry e = gson.fromJson(stream, NpcEntry.class);
		loadedNpcs.put(id, e);
		return e;
	}
}
