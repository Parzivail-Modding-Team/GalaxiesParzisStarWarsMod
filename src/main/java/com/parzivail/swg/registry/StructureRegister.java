package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.util.binary.Cdf.ChunkDiff;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class StructureRegister
{
	private static HashMap<Integer, ChunkDiff[]> structures = new HashMap<>();
	private static final ChunkDiff[] NO_STRUCTURES = new ChunkDiff[0];

	public static void register()
	{
		structures.put(Resources.dimIdTatooine, new ChunkDiff[] {
				//ChunkDiff.Load(new ResourceLocation(Resources.MODID, "structures/test.cdf"))
				ChunkDiff.Load(new ResourceLocation(Resources.MODID, "structures/diff.cdf"))
		});
	}

	public static ChunkDiff[] getStructuresForDimension(int dim)
	{
		if (!structures.containsKey(dim))
			return NO_STRUCTURES;
		return structures.get(dim);
	}
}
