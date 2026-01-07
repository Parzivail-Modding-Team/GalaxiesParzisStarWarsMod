package dev.pswg.container;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.pswg.Gadgets;
import dev.pswg.datagen.DGItemTag;
import dev.pswg.datagen.DataGenItem;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.datagen.ItemModel;
import dev.pswg.feature.scrapping.cutter.LaserCutterItem;
import dev.pswg.item.*;
import dev.pswg.item.grenades.*;
import dev.pswg.registry.Registrar;
import dev.pswg.item.NumberedItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.component.type.ConsumableComponents.food;

public class GadgetsItems
{
	public static class Tags
	{
		public static final TagKey<Item> GRENADES_TAG = TagKey.of(RegistryKeys.ITEM, Gadgets.id("grenades"));
		public static final TagKey<Item> MINES_TAG = TagKey.of(RegistryKeys.ITEM, Gadgets.id("mines"));
		public static final TagKey<Item> SCRAP_TAG = TagKey.of(RegistryKeys.ITEM, Gadgets.id("scrap"));
		public static final TagKey<Item> DRINK_CONTAINER_TAG = TagKey.of(RegistryKeys.ITEM, Gadgets.id("drink_container"));
		public static final TagKey<Item> MIXER_FOOD_TAG = TagKey.of(RegistryKeys.ITEM, Gadgets.id("mixable_food"));

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

		public static final ComponentType<Float> CUTTING_PROGRESS = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "cutting_progress"),
				ComponentType.<Float>builder().codec(Codec.FLOAT).build()
		);
		public static final ComponentType<BlockPos> CURRENT_BLOCK = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "current_block"),
				ComponentType.<BlockPos>builder().codec(BlockPos.CODEC).build()
		);
		public static final ComponentType<Vec3d> MIN_POS = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "min_pos"),
				ComponentType.<Vec3d>builder().codec(Vec3d.CODEC).build()
		);
		public static final ComponentType<Vec3d> MAX_POS = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				Identifier.of(Gadgets.MODID, "max_pos"),
				ComponentType.<Vec3d>builder().codec(Vec3d.CODEC).build()
		);


		public static void register()
		{
		}
	}

	/// GRENADE
	@DataGenItem(langOverride = "CLS-A Thermal Detonator", itemGroup = DataGenItemGroup.DemolitionsGadgets, genModel = false, itemTags = DGItemTag.Grenade)
	public static final ThermalDetonatorItem THERMAL_DETONATOR_ITEM = Registrar.item(Gadgets.id("thermal_detonator"), ThermalDetonatorItem::new, new Item.Settings());
	@DataGenItem(langOverride = "C-25 Fragmentation Grenade", itemGroup = DataGenItemGroup.DemolitionsGadgets, genModel = false, itemTags = DGItemTag.Grenade)
	public static final FragmentationGrenadeItem FRAGMENTATION_GRENADE_ITEM = Registrar.item(Gadgets.id("fragmentation_grenade"), FragmentationGrenadeItem::new, new Item.Settings());
	@DataGenItem(wiz = true, langOverride = "FEX-M3 Nerve Gas Grenade", itemGroup = DataGenItemGroup.DemolitionsGadgets, itemTags = DGItemTag.Grenade)
	public static final NerveGasGrenadeItem NERVE_GAS_GRENADE_ITEM = Registrar.item(Gadgets.id("nerve_gas_grenade"), NerveGasGrenadeItem::new, new Item.Settings());
	@DataGenItem(wiz = true, langOverride = "NACHT-5 Smoke Grenade", itemGroup = DataGenItemGroup.DemolitionsGadgets, itemTags = DGItemTag.Grenade)
	public static final SmokeSignalGrenadeItem SMOKE_SIGNAL_GRENADE_ITEM = Registrar.item(Gadgets.id("smoke_grenade"), SmokeSignalGrenadeItem::new, new Item.Settings());
	@DataGenItem(wiz = true, itemGroup = DataGenItemGroup.DemolitionsGadgets, itemTags = DGItemTag.Grenade)
	public static final ImpactGrenadeItem IMPACT_GRENADE_ITEM = Registrar.item(Gadgets.id("impact_grenade"), ImpactGrenadeItem::new, new Item.Settings());
	@DataGenItem(wiz = true, langOverride = "D-24 Inferno Grenade", itemGroup = DataGenItemGroup.DemolitionsGadgets, itemTags = DGItemTag.Grenade)
	public static final InfernoGrenadeItem INFERNO_GRENADE_ITEM = Registrar.item(Gadgets.id("inferno_grenade"), InfernoGrenadeItem::new, new Item.Settings());

	/// MINES
	@DataGenItem(wiz = true, itemGroup = DataGenItemGroup.DemolitionsGadgets, itemTags = DGItemTag.Mine)
	public static final PressureMineItem PRESSURE_MINE_ITEM = Registrar.item(Gadgets.id("pressure_mine"), PressureMineItem::new, new Item.Settings());
	@DataGenItem(wiz = true, itemGroup = DataGenItemGroup.DemolitionsGadgets, itemTags = DGItemTag.Mine)
	public static final TripwireMineItem TRIPWIRE_MINE_ITEM = Registrar.item(Gadgets.id("tripwire_mine"), TripwireMineItem::new, new Item.Settings());

	///  SCRAPPING TOOLS
	@DataGenItem(wiz = true, langOverride = "F-187 Fusioncutter")
	public static final Item CUTTER_ITEM = Registrar.item(Gadgets.id("cutter"), LaserCutterItem::new, new Item.Settings().maxDamage(100));
	@DataGenItem(wiz = true, langOverride = "FastTurn-3 Hydrospanner")
	public static final Item SPANNER_ITEM = registerSimpleItem("spanner", new Item.Settings().maxDamage(100));
	@DataGenItem(wiz = true, langOverride = "ReliaCharge Power Calibrator")
	public static final Item CALIBRATOR_ITEM = registerSimpleItem("calibrator", new Item.Settings().maxDamage(100));

	public static Item registerSimpleItem(String key)
	{
		return registerSimpleItem(key, new Item.Settings());
	}

	public static Item registerSimpleItem(String key, Item.Settings settings)
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
