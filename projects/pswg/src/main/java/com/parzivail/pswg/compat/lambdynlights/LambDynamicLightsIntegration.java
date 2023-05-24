package com.parzivail.pswg.compat.lambdynlights;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.features.lightsabers.LightsaberItem;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSource;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSources;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class LambDynamicLightsIntegration implements DynamicLightsInitializer
{
	@Override
	public void onInitializeDynamicLights()
	{
		DynamicLightHandlers.registerDynamicLightHandler(SwgEntities.Misc.BlasterBolt, entity -> 8);
		DynamicLightHandlers.registerDynamicLightHandler(SwgEntities.Misc.BlasterIonBolt, entity -> 8);
		DynamicLightHandlers.registerDynamicLightHandler(SwgEntities.Misc.BlasterStunBolt, entity -> 3);

		for (var lightsaber : PswgContent.getLightsaberPresets().keySet())
		{
			var item = (LightsaberItem)Registries.ITEM.get(SwgItems.getLightsaberRegistrationId(lightsaber));
			ItemLightSources.registerItemLightSource(new ItemLightSource(Resources.id("ldl_lightsaber"), item, false)
			{
				@Override
				public int getLuminance(ItemStack stack)
				{
					return item.getLightEmission(null, stack);
				}
			});
		}
	}
}
