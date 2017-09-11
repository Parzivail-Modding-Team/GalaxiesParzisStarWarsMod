package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.dimension.TatooineProvider;
import com.parzivail.util.world.WorldUtils;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldRegister
{
	public static void register()
	{
		WorldUtils.registerDimension(Resources.dimIdTatooine, TatooineProvider.class);
	}
}
