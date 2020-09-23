package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.structure.ScarifStructure;
import net.minecraft.util.Identifier;

public class SwgStructures
{
	public static class General
	{
		public static final ScarifStructure Region = ScarifStructure.read(new Identifier(Resources.MODID, "structures/entire_region.scrf2"));

		public static void register()
		{
			// no-op to make sure the class is loaded
		}
	}
}
