package com.parzivail.pswg.compat.lambdynlights;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSource;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSources;
import net.minecraft.item.ItemStack;

public class LambDynamicLightsIntegration implements DynamicLightsInitializer
{
	@Override
	public void onInitializeDynamicLights()
	{
		DynamicLightHandlers.registerDynamicLightHandler(SwgEntities.Misc.BlasterBolt, entity -> 8);
		DynamicLightHandlers.registerDynamicLightHandler(SwgEntities.Misc.BlasterIonBolt, entity -> 8);
		DynamicLightHandlers.registerDynamicLightHandler(SwgEntities.Misc.BlasterStunBolt, entity -> 3);
		ItemLightSources.registerItemLightSource(new ItemLightSource(Resources.id("ldl_lightsaber"), SwgItems.Lightsaber.Lightsaber, false)
		{
			@Override
			public int getLuminance(ItemStack stack)
			{
				return SwgItems.Lightsaber.Lightsaber.getLightEmission(null, stack);
			}
		});
	}
}
