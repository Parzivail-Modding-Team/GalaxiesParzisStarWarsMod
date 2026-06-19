package dev.pswg.container;

import com.mojang.serialization.Codec;
import dev.pswg.Gadgets;
import dev.pswg.datagen.DataGenItem;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.datagen.ItemModel;
import dev.pswg.feature.scrapping.cutter.LaserCutterItem;
import dev.pswg.item.*;
import dev.pswg.item.drill.DrillExtractionInstance;
import dev.pswg.item.drill.DrillProperties;
import dev.pswg.item.drill.DrillItem;
import dev.pswg.item.grenades.*;
import dev.pswg.registry.Registrar;
import dev.pswg.util.gen.DataGenGadgetsItemTag;
import dev.pswg.util.gen.GadgetsItemTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class GadgetsItems
{
	public static class Tags
	{
		public static final TagKey<Item> GRENADES_TAG = TagKey.create(Registries.ITEM, Gadgets.id("grenades"));
		public static final TagKey<Item> MINES_TAG = TagKey.create(Registries.ITEM, Gadgets.id("mines"));
		public static final TagKey<Item> MIXER_FOOD_TAG = TagKey.create(Registries.ITEM, Gadgets.id("mixable_food"));

		public static void register()
		{
		}
	}

	public static class Components
	{
		public static final DataComponentType<Long> PRIMING_TIME = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				Identifier.fromNamespaceAndPath(Gadgets.MODID, "priming_time"),
				DataComponentType.<Long>builder().persistent(Codec.LONG).build()
		);

		public static final DataComponentType<Float> CUTTING_PROGRESS = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				Identifier.fromNamespaceAndPath(Gadgets.MODID, "cutting_progress"),
				DataComponentType.<Float>builder().persistent(Codec.FLOAT).build()
		);
		public static final DataComponentType<BlockPos> CURRENT_BLOCK = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				Identifier.fromNamespaceAndPath(Gadgets.MODID, "current_block"),
				DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).build()
		);
		public static final DataComponentType<Vec3> MIN_POS = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				Identifier.fromNamespaceAndPath(Gadgets.MODID, "min_pos"),
				DataComponentType.<Vec3>builder().persistent(Vec3.CODEC).build()
		);
		public static final DataComponentType<Vec3> MAX_POS = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				Identifier.fromNamespaceAndPath(Gadgets.MODID, "max_pos"),
				DataComponentType.<Vec3>builder().persistent(Vec3.CODEC).build()
		);
		public static final DataComponentType<DrillProperties> DRILL_EXTRACTOR_PROPERTIES = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				Identifier.fromNamespaceAndPath(Gadgets.MODID, "drill_extractor_properties"),
				DataComponentType.<DrillProperties>builder().persistent(DrillProperties.CODEC).build()
		);
		public static final DataComponentType<DrillExtractionInstance> DRILL_EXTRACTION_INSTANCE = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				Identifier.fromNamespaceAndPath(Gadgets.MODID, "drill_extraction_instance"),
				DataComponentType.<DrillExtractionInstance>builder().persistent(DrillExtractionInstance.CODEC).build()
		);


		public static void register()
		{
		}
	}

	/// GRENADE
	@GadgetsItemTag(itemTags = DataGenGadgetsItemTag.GRENADE)
	@DataGenItem(langOverride = "CLS-A Thermal Detonator", itemGroup = DataGenItemGroup.DEMOLITIONS_GADGETS, model = ItemModel.NONE)
	public static final ThermalDetonatorItem THERMAL_DETONATOR_ITEM = Registrar.item(Gadgets.id("thermal_detonator"), ThermalDetonatorItem::new, new Item.Properties());
	@GadgetsItemTag(itemTags = DataGenGadgetsItemTag.GRENADE)
	@DataGenItem(langOverride = "C-25 Fragmentation Grenade", itemGroup = DataGenItemGroup.DEMOLITIONS_GADGETS, model = ItemModel.NONE)
	public static final FragmentationGrenadeItem FRAGMENTATION_GRENADE_ITEM = Registrar.item(Gadgets.id("fragmentation_grenade"), FragmentationGrenadeItem::new, new Item.Properties());
	@GadgetsItemTag(itemTags = DataGenGadgetsItemTag.GRENADE)
	@DataGenItem(wiz = true, langOverride = "FEX-M3 Nerve Gas Grenade", itemGroup = DataGenItemGroup.DEMOLITIONS_GADGETS, model = ItemModel.NONE)
	public static final NerveGasGrenadeItem NERVE_GAS_GRENADE_ITEM = Registrar.item(Gadgets.id("nerve_gas_grenade"), NerveGasGrenadeItem::new, new Item.Properties());
	@GadgetsItemTag(itemTags = DataGenGadgetsItemTag.GRENADE)
	@DataGenItem(wiz = true, langOverride = "NACHT-5 Smoke Grenade", itemGroup = DataGenItemGroup.DEMOLITIONS_GADGETS, model = ItemModel.NONE)
	public static final SmokeSignalGrenadeItem SMOKE_SIGNAL_GRENADE_ITEM = Registrar.item(Gadgets.id("smoke_signal_grenade"), SmokeSignalGrenadeItem::new, new Item.Properties());
	@GadgetsItemTag(itemTags = DataGenGadgetsItemTag.GRENADE)
	@DataGenItem(wiz = true, langOverride = "DTA-9 Impact Grenade", itemGroup = DataGenItemGroup.DEMOLITIONS_GADGETS, model = ItemModel.NONE)
	public static final ImpactGrenadeItem IMPACT_GRENADE_ITEM = Registrar.item(Gadgets.id("impact_grenade"), ImpactGrenadeItem::new, new Item.Properties());
	@GadgetsItemTag(itemTags = DataGenGadgetsItemTag.GRENADE)
	@DataGenItem(wiz = true, langOverride = "D-24 Inferno Grenade", itemGroup = DataGenItemGroup.DEMOLITIONS_GADGETS, model = ItemModel.NONE)
	public static final InfernoGrenadeItem INFERNO_GRENADE_ITEM = Registrar.item(Gadgets.id("inferno_grenade"), InfernoGrenadeItem::new, new Item.Properties());

	/// MINES
	@GadgetsItemTag(itemTags = DataGenGadgetsItemTag.MINE)
	@DataGenItem(wiz = true, itemGroup = DataGenItemGroup.DEMOLITIONS_GADGETS)
	public static final PressureMineItem PRESSURE_MINE_ITEM = Registrar.item(Gadgets.id("pressure_mine"), PressureMineItem::new, new Item.Properties());
	@GadgetsItemTag(itemTags = DataGenGadgetsItemTag.MINE)
	@DataGenItem(wiz = true, itemGroup = DataGenItemGroup.DEMOLITIONS_GADGETS)
	public static final TripwireMineItem TRIPWIRE_MINE_ITEM = Registrar.item(Gadgets.id("tripwire_mine"), TripwireMineItem::new, new Item.Properties());

	///  SCRAPPING TOOLS
	@DataGenItem(wiz = true, langOverride = "F-187 Fusioncutter")
	public static final Item CUTTER_ITEM = Registrar.item(Gadgets.id("cutter"), LaserCutterItem::new, new Item.Properties().durability(100));
	@DataGenItem(wiz = true, langOverride = "FastTurn-3 Hydrospanner")
	public static final Item SPANNER_ITEM = registerSimpleItem("spanner", new Item.Properties().durability(100));
	@DataGenItem(wiz = true, langOverride = "ReliaCharge Power Calibrator")
	public static final Item CALIBRATOR_ITEM = registerSimpleItem("calibrator", new Item.Properties().durability(100));
	@DataGenItem(langOverride = "Extractor", model = ItemModel.NONE)
	public static final Item DRILL_ITEM = Registrar.item(Gadgets.id("extraction_drill"), DrillItem::new, new Item.Properties().durability(100)
	                                                                                                                          .component(Components.DRILL_EXTRACTOR_PROPERTIES, new DrillProperties("baseExtractor", "baseDrill", "baseCapsule"))
	                                                                                                                          .pickaxe(GalaxiesToolMaterials.TITANIUM, 1.0F, -2.8F));

	public static Item registerSimpleItem(String key)
	{
		return registerSimpleItem(key, new Item.Properties());
	}

	public static Item registerSimpleItem(String key, Item.Properties settings)
	{
		return Registrar.item(Gadgets.id(key), Item::new, settings);
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
