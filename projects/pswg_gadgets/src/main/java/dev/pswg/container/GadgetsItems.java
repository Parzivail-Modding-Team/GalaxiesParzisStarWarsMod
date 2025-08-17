package dev.pswg.container;

import com.mojang.serialization.Codec;
import dev.pswg.Gadgets;
import dev.pswg.datagen.DataGenItem;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.item.*;
import dev.pswg.item.grenades.*;
import dev.pswg.registry.Registrar;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
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
		public static final ComponentType<Integer> METAL_COMPONENT = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "metal_component"),
				ComponentType.<Integer>builder().codec(Codec.INT).build()
		);
		public static final ComponentType<Integer> TECH_COMPONENT = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "tech_component"),
				ComponentType.<Integer>builder().codec(Codec.INT).build()
		);
		public static final ComponentType<Integer> PLASTIC_COMPONENT = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "plastic_component"),
				ComponentType.<Integer>builder().codec(Codec.INT).build()
		);
		public static final ComponentType<Integer> ENERGY_COMPONENT = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "energy_component"),
				ComponentType.<Integer>builder().codec(Codec.INT).build()
		);
		public static final ComponentType<Boolean> SCRAP = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "scrap"),
				ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
		);



		public static void register()
		{
		}
	}

	/// GRENADE
	@DataGenItem(wiz = true, langOverride = "CLS-A Thermal Detonator", itemGroup = DataGenItemGroup.DemolitionsGadgets)
	public static final ThermalDetonatorItem THERMAL_DETONATOR_ITEM = Registrar.item(Gadgets.id("thermal_detonator"), ThermalDetonatorItem::new, new Item.Settings());
	@DataGenItem(wiz = true, langOverride = "C-25 Fragmentation Grenade", itemGroup = DataGenItemGroup.DemolitionsGadgets)
	public static final FragmentationGrenadeItem FRAGMENTATION_GRENADE_ITEM = Registrar.item(Gadgets.id("fragmentation_grenade"), FragmentationGrenadeItem::new, new Item.Settings());
	@DataGenItem(wiz = true, langOverride = "FEX-M3 Nerve Gas Grenade", itemGroup = DataGenItemGroup.DemolitionsGadgets)
	public static final NerveGasGrenadeItem NERVE_GAS_GRENADE_ITEM = Registrar.item(Gadgets.id("nerve_gas_grenade"), NerveGasGrenadeItem::new, new Item.Settings());
	@DataGenItem(wiz = true, langOverride = "NACHT-5 Smoke Grenade", itemGroup = DataGenItemGroup.DemolitionsGadgets)
	public static final SmokeSignalGrenadeItem SMOKE_SIGNAL_GRENADE_ITEM = Registrar.item(Gadgets.id("smoke_grenade"), SmokeSignalGrenadeItem::new, new Item.Settings());
	@DataGenItem(wiz = true, itemGroup = DataGenItemGroup.DemolitionsGadgets)
	public static final ImpactGrenadeItem IMPACT_GRENADE_ITEM = Registrar.item(Gadgets.id("impact_grenade"), ImpactGrenadeItem::new, new Item.Settings());
	@DataGenItem(wiz = true, langOverride = "D-24 Inferno Grenade", itemGroup = DataGenItemGroup.DemolitionsGadgets)
	public static final InfernoGrenadeItem INFERNO_GRENADE_ITEM = Registrar.item(Gadgets.id("inferno_grenade"), InfernoGrenadeItem::new, new Item.Settings());

	/// MINES
	@DataGenItem(wiz = true, itemGroup = DataGenItemGroup.DemolitionsGadgets)
	public static final PressureMineItem PRESSURE_MINE_ITEM = Registrar.item(Gadgets.id("pressure_mine"), PressureMineItem::new, new Item.Settings());
	@DataGenItem(wiz = true, itemGroup = DataGenItemGroup.DemolitionsGadgets)
	public static final TripwireMineItem TRIPWIRE_MINE_ITEM = Registrar.item(Gadgets.id("tripwire_mine"), TripwireMineItem::new, new Item.Settings());

	@DataGenItem
	public static final Item SCRAP_ITEM = registerSimpleItem("scrap", new Item.Settings().component(Components.METAL_COMPONENT, 4).component(Components.PLASTIC_COMPONENT, 3).component(Components.TECH_COMPONENT, 1).component(Components.SCRAP, true));
	@DataGenItem
	public static final Item BROKEN_SMALL_POWER_PACK_ITEM = registerSimpleItem("broken_small_power_pack", new Item.Settings().component(Components.METAL_COMPONENT, 1).component(Components.ENERGY_COMPONENT, 3).component(Components.TECH_COMPONENT, 2).component(Components.SCRAP, true));

	///  SCRAPPING TOOLS
	@DataGenItem(wiz = true)
	public static final Item CUTTER_ITEM = registerSimpleItem("cutter", new Item.Settings().maxDamage(100));
	@DataGenItem(wiz = true)
	public static final Item SPANNER_ITEM = registerSimpleItem("spanner", new Item.Settings().maxDamage(100));
	@DataGenItem(wiz = true)
	public static final Item CALIBRATOR_ITEM = registerSimpleItem("calibrator", new Item.Settings().maxDamage(100));

	/// ARMOR | TODO: ADD TRINKET ARMOR PARTS

	@DataGenItem
	public static final ArmorItems ELITE_SQUAD_TROOPER = new ArmorItems("elite_squad", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems STORM_TROOPER = new ArmorItems("stormtrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems SHOCK_TROOPER = new ArmorItems("shocktrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems PURGE_TROOPER = new ArmorItems("purgetrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems ARTILLERY_TROOPER = new ArmorItems("artillerytrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems INCINERATOR_TROOPER = new ArmorItems("incineratortrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems SANDTROOPER = new ArmorItems("sandtrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems DEATH_TROOPER = new ArmorItems("deathtrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems SCOUT_TROOPER = new ArmorItems("scouttrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems HOVERTANK_PILOT = new ArmorItems("hovertankpilot", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems SHORE_TROOPER = new ArmorItems("shoretrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItems JUMP_TROOPER = new ArmorItems("jumptrooper", ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
	@DataGenItem
	public static final ArmorItem IMPERIAL_PILOT_HELMET = registerArmorItem("imperial_pilot_helmet", ArmorMaterials.DIAMOND, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem IMPERIAL_PILOT_KIT = registerArmorItem("imperial_pilot_kit", ArmorMaterials.DIAMOND, EquipmentType.BODY);
	@DataGenItem
	public static final ArmorItem IMPERIAL_PILOT_CADET_HELMET = registerArmorItem("imperial_pilot_cadet_helmet", ArmorMaterials.DIAMOND, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem IMPERIAL_PILOT_TECHNICAL_HELMET = registerArmorItem("imperial_pilot_technical_helmet", ArmorMaterials.DIAMOND, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem IMPERIAL_PILOT_COLD_HELMET = registerArmorItem("imperial_pilot_cold_helmet", ArmorMaterials.DIAMOND, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem REBEL_PILOT_HELMET = registerArmorItem("rebel_pilot_helmet", ArmorMaterials.DIAMOND, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem REBEL_PILOT_KIT = registerArmorItem("rebel_pilot_kit", ArmorMaterials.DIAMOND, EquipmentType.CHESTPLATE);
	@DataGenItem
	public static final ArmorItem REBEL_FOREST = registerArmorItem("rebel_forest_helmet", ArmorMaterials.DIAMOND, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem REBEL_TROPICAL = registerArmorItem("rebel_tropical_helmet", ArmorMaterials.DIAMOND, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem BLACK_IMPERIAL_OFFICER = registerArmorItem("black_imperial_officer_hat", ArmorMaterials.LEATHER, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem GRAY_IMPERIAL_OFFICER = registerArmorItem("gray_imperial_officer_hat", ArmorMaterials.LEATHER, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem LIGHT_GRAY_IMPERIAL_OFFICER = registerArmorItem("light_gray_imperial_officer_hat", ArmorMaterials.LEATHER, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem KHAKI_IMPERIAL_OFFICER = registerArmorItem("khaki_imperial_officer_hat", ArmorMaterials.LEATHER, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem TAN_GOGGLES_CAP = registerArmorItem("tan_goggles_cap", ArmorMaterials.LEATHER, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem GRAY_GOGGLES_CAP = registerArmorItem("gray_goggles_cap", ArmorMaterials.LEATHER, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem BROWN_GOGGLES_CAP = registerArmorItem("brown_goggles_cap", ArmorMaterials.LEATHER, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem BEACH_INSURGENCE_HAT = registerArmorItem("beach_insurgence_hat", ArmorMaterials.LEATHER, EquipmentType.HELMET);
	@DataGenItem
	public static final ArmorItem DESERT_INSURGENCE_HAT = registerArmorItem("desert_insurgence_hat", ArmorMaterials.LEATHER, EquipmentType.HELMET);

	/// DOOR.

	@DataGenItem
	public static final DyedItems DOOR_INSERT = new DyedItems(color -> Registrar.item(Gadgets.id("door_insert" + color.name().toLowerCase()), settings -> new DoorInsertItem(color, settings), new Item.Settings()));

	// TODO: Implement wire

	/// CRAFTING COMPONENTS

	public static Item registerSimpleItem(String key)
	{
		return registerSimpleItem(key, new Item.Settings());
	}

	public static Item registerSimpleItem(String key, Item.Settings settings)
	{
		return Registrar.item(Gadgets.id(key), Item::new, settings);
	}

	public static ArmorItem registerArmorItem(String key, ArmorMaterial material, EquipmentType type)
	{
		return registerArmorItem(key, material, type, new Item.Settings().maxCount(1));
	}

	public static ArmorItem registerArmorItem(String key, ArmorMaterial material, EquipmentType type, Item.Settings itemSettings)
	{
		return Registrar.item(Gadgets.id(key), settings -> new ArmorItem(material, type, settings), itemSettings);
	}
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
