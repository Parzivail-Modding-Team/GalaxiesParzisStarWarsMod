package com.parzivail.swg.registry;

import com.parzivail.scarif.ScarifEngine;
import com.parzivail.swg.Resources;

public class StructureRegister
{
	public static ScarifEngine structureEngine;

	public static void register()
	{
		structureEngine = new ScarifEngine(Resources.MODID);

		structureEngine.load(Resources.dimIdTatooine, Resources.location("structures/interdictor.scrf"));
	}
}
