package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.util.binary.ChunkDiff;
import net.minecraft.util.ResourceLocation;

public class StructureRegister
{
	public static ChunkDiff test;
	public static ChunkDiff testNbt;

	public static void register()
	{
		test = ChunkDiff.Load(new ResourceLocation(Resources.MODID, "structures/test.cdf"));
	}
}
