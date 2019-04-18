package com.parzivail.swg.register;

import com.parzivail.scarif.ScarifEngine;
import com.parzivail.swg.Resources;

public class StructureRegister
{
	public static ScarifEngine structureEngine;

	public static void register()
	{
		structureEngine = new ScarifEngine(Resources.MODID);
		structureEngine.registerBlockTransformer(new LegacyBlockTransformer());

		//structureEngine.load(StarWarsGalaxy.config.getDimIdTatooine(), Resources.location("structures/interdictor.scrf"));
		structureEngine.load(2, Resources.location("structures/isd_test_converted.scrf"));
	}
}
