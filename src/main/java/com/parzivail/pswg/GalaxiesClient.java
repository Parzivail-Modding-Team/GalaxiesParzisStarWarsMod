package com.parzivail.pswg;

import com.parzivail.pswg.client.pm3d.PM3DLoader;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

public class GalaxiesClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		Lumberjack.debug("onInitializeClient");

		PM3DLoader.INSTANCE.registerDomain(Resources.MODID);

		ModelLoadingRegistry.INSTANCE.registerResourceProvider(PM3DLoader.INSTANCE);
		//		ModelLoadingRegistry.INSTANCE.registerVariantProvider(ItemPM3DLoader.INSTANCE);
	}
}
