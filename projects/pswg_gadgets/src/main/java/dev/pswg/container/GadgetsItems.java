package dev.pswg.container;

import com.mojang.serialization.Codec;
import dev.pswg.Gadgets;
import dev.pswg.item.PressureMineItem;
import dev.pswg.item.TripwireMineItem;
import dev.pswg.item.grenades.*;
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
		public static final TagKey<Item> MINES_TAG = TagKey.of(RegistryKeys.ITEM, Gadgets.id("mines"));
		public static final TagKey<Item> SCRAP_TAG = TagKey.of(RegistryKeys.ITEM, Gadgets.id("scrap"));

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
	public static final SmokeSignalGrenadeItem SMOKE_SIGNAL_GRENADE_ITEM = Registrar.item(Gadgets.id("smoke_grenade"), SmokeSignalGrenadeItem::new, new Item.Settings());
	public static final ImpactGrenadeItem IMPACT_GRENADE_ITEM = Registrar.item(Gadgets.id("impact_grenade"), ImpactGrenadeItem::new, new Item.Settings());
	public static final InfernoGrenadeItem INFERNO_GRENADE_ITEM = Registrar.item(Gadgets.id("inferno_grenade"), InfernoGrenadeItem::new, new Item.Settings());

	public static final PressureMineItem PRESSURE_MINE_ITEM = Registrar.item(Gadgets.id("pressure_mine"), PressureMineItem::new, new Item.Settings());
	public static final TripwireMineItem TRIPWIRE_MINE_ITEM = Registrar.item(Gadgets.id("tripwire_mine"), TripwireMineItem::new, new Item.Settings());

	public static final Item CUTTER_ITEM = Registrar.item(Gadgets.id("cutter"), Item::new, new Item.Settings());
	public static final Item SPANNER_ITEM = Registrar.item(Gadgets.id("spanner"), Item::new, new Item.Settings());
	public static final Item CALIBRATOR_ITEM = Registrar.item(Gadgets.id("calibrator"), Item::new, new Item.Settings());

	public static void register()
	{
		Tags.register();
		Components.register();

		DispenserBlock.registerProjectileBehavior(GadgetsItems.THERMAL_DETONATOR_ITEM);
		DispenserBlock.registerProjectileBehavior(GadgetsItems.FRAGMENTATION_GRENADE_ITEM);
		DispenserBlock.registerProjectileBehavior(GadgetsItems.NERVE_GAS_GRENADE_ITEM);
		DispenserBlock.registerProjectileBehavior(GadgetsItems.SMOKE_SIGNAL_GRENADE_ITEM);
		DispenserBlock.registerProjectileBehavior(GadgetsItems.IMPACT_GRENADE_ITEM);
	}
}
