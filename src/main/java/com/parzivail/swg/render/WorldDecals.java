package com.parzivail.swg.render;

import com.parzivail.util.ui.gltk.GL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldDecals
{
	private static HashMap<Integer, List<Decal>> allDecals = new HashMap<>();

	public static void create(int dim, Decal d)
	{
		if (!allDecals.containsKey(dim))
			allDecals.put(dim, new ArrayList<>());

		allDecals.get(dim).add(d);
	}

	public static void render(int dim)
	{
		if (!allDecals.containsKey(dim))
			return;

		List<Decal> decals = allDecals.get(dim);
		for (Decal d : decals)
		{
			GL.PushMatrix();
			d.render();
			GL.PopMatrix();
		}
	}
}
