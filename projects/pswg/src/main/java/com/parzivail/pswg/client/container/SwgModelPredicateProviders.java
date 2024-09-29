package com.parzivail.pswg.client.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgComponents;
import com.parzivail.pswg.container.SwgItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class SwgModelPredicateProviders
{
	public static final Identifier ThermalDetonatorPrimed = Resources.id("thermal_detonator_primed");

	public static void register()
	{
		ModelPredicateProviderRegistry.register(
				SwgItems.Explosives.ThermalDetonator,
				ThermalDetonatorPrimed,
				(stack, world, entity, seed) -> {
					return entity != null && stack.get(SwgComponents.ThermalDetonator).primed() ? 1.0F : 0.0F;
				}
		);
	}
}
