package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.structure.ScarifChunk;
import com.parzivail.pswg.structure.ScarifSection;
import com.parzivail.pswg.structure.ScarifStructure;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

public class SwgStructures
{
	public static class General
	{
		public static final ScarifStructure Region = ScarifStructure.read(new Identifier(Resources.MODID, "structures/entire_region.scrf2"));

		public static void register()
		{
			// no-op to make sure the class is loaded

			ScarifChunk chunk = Region.openChunk(new ChunkPos(-32, -32));
			chunk.init();

			ScarifSection section = chunk.readSection();
		}
	}
}
