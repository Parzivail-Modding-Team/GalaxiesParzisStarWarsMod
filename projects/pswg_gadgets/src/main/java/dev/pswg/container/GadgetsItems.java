package dev.pswg.container;

import com.mojang.serialization.Codec;
import dev.pswg.Gadgets;
import dev.pswg.item.*;
import dev.pswg.registry.Registrar;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class GadgetsItems
{
	public static class Tags
	{
		public static final TagKey<Item> GRENADES_TAG = TagKey.of(RegistryKeys.ITEM, Gadgets.id("grenades"));

		public static void register()
		{
		}
	}

	public static class Components
	{
		public static final ComponentType<Long> PRIMING_TIME = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "priming_time"),
				ComponentType.<Long>builder().codec(Codec.LONG).build()
		);

		public static void register()
		{
		}
	}

	public static final ThermalDetonatorItem THERMAL_DETONATOR_ITEM = Registrar.item(Gadgets.id("thermal_detonator"), ThermalDetonatorItem::new, new Item.Settings());
	public static final FragmentationGrenadeItem FRAGMENTATION_GRENADE_ITEM = Registrar.item(Gadgets.id("fragmentation_grenade"), FragmentationGrenadeItem::new, new Item.Settings());
	public static final NerveGasGrenadeItem NERVE_GAS_GRENADE_ITEM = Registrar.item(Gadgets.id("nerve_gas_grenade"), NerveGasGrenadeItem::new, new Item.Settings());
	public static final SmokeGasGrenadeItem SMOKE_GRENADE_ITEM = Registrar.item(Gadgets.id("smoke_grenade"), SmokeGasGrenadeItem::new, new Item.Settings());
	public static final ImpactGrenadeItem IMPACT_GRENADE_ITEM = Registrar.item(Gadgets.id("impact_grenade"), ImpactGrenadeItem::new, new Item.Settings());

	public static void register()
	{
		Tags.register();
		Components.register();

		DispenserBlock.registerProjectileBehavior(GadgetsItems.THERMAL_DETONATOR_ITEM);
		DispenserBlock.registerProjectileBehavior(GadgetsItems.FRAGMENTATION_GRENADE_ITEM);
		DispenserBlock.registerProjectileBehavior(GadgetsItems.NERVE_GAS_GRENADE_ITEM);
	}
}
