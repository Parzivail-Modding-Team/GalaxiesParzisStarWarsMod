package com.parzivail.pswg.client.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.item.ThrowableExplosiveTag;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class SwgModelPredicateProviders
{
	public static final Identifier ThermalDetonatorPrimed = Resources.id("thermal_detonator_primed");
	public static final Identifier FragmentationGrenadePrimed = Resources.id("fragmentation_grenade_primed");

	public static void register()
	{
		ModelPredicateProviderRegistry.register(
				SwgItems.Explosives.ThermalDetonator,
				ThermalDetonatorPrimed,
				(stack, world, entity, seed) -> {
					var tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
					return entity != null && tag.primed ? 1.0F : 0.0F;
				}
		);
		ModelPredicateProviderRegistry.register(
				SwgItems.Explosives.FragmentationGrenade,
				FragmentationGrenadePrimed,
				(stack, world, entity, seed) -> {
					var tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
					return entity != null && tag.primed ? 1.0F : 0.0F;
				}
		);
	}
}
