package dev.pswg.container;

import com.mojang.serialization.Codec;
import dev.pswg.Galaxies;
import dev.pswg.datagen.DGItemTag;
import dev.pswg.datagen.DataGenItem;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.datagen.ItemModel;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DoorInsertItem;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import dev.pswg.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

import static net.minecraft.world.item.component.Consumables.defaultFood;

public class GalaxiesItems
{
	public static class Tags
	{
		public static final TagKey<Item> BESKAR_TOOL_MATERIALS_TAG = TagKey.create(Registries.ITEM, Galaxies.id("beskar_tool_materials"));
		public static final TagKey<Item> DURASTEEL_TOOL_MATERIALS_TAG = TagKey.create(Registries.ITEM, Galaxies.id("durasteel_tool_materials"));
		public static final TagKey<Item> TITANIUM_TOOL_MATERIALS_TAG = TagKey.create(Registries.ITEM, Galaxies.id("titanium_tool_materials"));

		public static void register()
		{
		}
	}
	public static class Components
	{
		public static final DataComponentType<Boolean> SCRAP = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				ResourceLocation.fromNamespaceAndPath(Galaxies.MODID, "scrap"),
				DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build()
		);

		public static final DataComponentType<Integer> METAL_COMPONENT = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				ResourceLocation.fromNamespaceAndPath(Galaxies.MODID, "metal_component"),
				DataComponentType.<Integer>builder().persistent(Codec.INT).build()
		);
		public static final DataComponentType<Integer> TECH_COMPONENT = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				ResourceLocation.fromNamespaceAndPath(Galaxies.MODID, "tech_component"),
				DataComponentType.<Integer>builder().persistent(Codec.INT).build()
		);
		public static final DataComponentType<Integer> PLASTIC_COMPONENT = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				ResourceLocation.fromNamespaceAndPath(Galaxies.MODID, "plastic_component"),
				DataComponentType.<Integer>builder().persistent(Codec.INT).build()
		);
		public static final DataComponentType<Integer> ENERGY_COMPONENT = Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				ResourceLocation.fromNamespaceAndPath(Galaxies.MODID, "energy_component"),
				DataComponentType.<Integer>builder().persistent(Codec.INT).build()
		);

		public static final Consumable DEATH_STICK_RED = defaultFood()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1), 0.99F))
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 200, 1), 1F))
				.build();
		public static final Consumable DEATH_STICK_YELLOW = defaultFood()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 1), 0.99F))
				.build();
		public static final Consumable MYSTERIOUS_SMOOTHIE = defaultFood()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 1), 0.5F))
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 200, 1), 1F))
				.build();
		public static final Consumable KREETLE_JUICE = defaultFood()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 1), 1F))
				.build();
		public static final Consumable ABSYNTHESIZED_MALT = defaultFood()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 1), 0.5F))
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.LUCK, 200, 1), 1F))
				.build();
		public static final Consumable CORONET_COCKTAIL = defaultFood()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 1), 0.5F))
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 1), 1F))
				.build();
		public static final Consumable SODA = defaultFood()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 200, 1), 1F))
				.build();
		public static final Consumable WATER = defaultFood()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SATURATION, 200, 1), 1F))
				.build();


		public static void register()
		{
		}
	}

	@DataGenItem
	public static final Item BROKEN_SMALL_POWER_PACK_ITEM = registerSimpleItem("broken_small_power_pack", new Item.Properties().component(Components.METAL_COMPONENT, 1).component(Components.ENERGY_COMPONENT, 3).component(Components.TECH_COMPONENT, 2).component(Components.SCRAP, true));

	/// ARMOR | TODO: ADD TRINKET ARMOR PARTS

	@DataGenItem
	public static final ArmorItems ELITE_SQUAD_TROOPER = new ArmorItems("elite_squad", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems STORM_TROOPER = new ArmorItems("stormtrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems SHOCK_TROOPER = new ArmorItems("shocktrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems PURGE_TROOPER = new ArmorItems("purgetrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems ARTILLERY_TROOPER = new ArmorItems("artillerytrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems INCINERATOR_TROOPER = new ArmorItems("incineratortrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems SANDTROOPER = new ArmorItems("sandtrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems DEATH_TROOPER = new ArmorItems("deathtrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems SCOUT_TROOPER = new ArmorItems("scouttrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems HOVERTANK_PILOT = new ArmorItems("hovertankpilot", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems SHORE_TROOPER = new ArmorItems("shoretrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final ArmorItems JUMP_TROOPER = new ArmorItems("jumptrooper", ArmorMaterials.DIAMOND, new Item.Properties().stacksTo(1));
	@DataGenItem
	public static final Item IMPERIAL_PILOT_HELMET = registerArmorItem("imperial_pilot_helmet", ArmorMaterials.DIAMOND, ArmorType.HELMET);
	@DataGenItem
	public static final Item IMPERIAL_PILOT_KIT = registerArmorItem("imperial_pilot_kit", ArmorMaterials.DIAMOND, ArmorType.BODY);
	@DataGenItem
	public static final Item IMPERIAL_PILOT_CADET_HELMET = registerArmorItem("imperial_pilot_cadet_helmet", ArmorMaterials.DIAMOND, ArmorType.HELMET);
	@DataGenItem
	public static final Item IMPERIAL_PILOT_TECHNICAL_HELMET = registerArmorItem("imperial_pilot_technical_helmet", ArmorMaterials.DIAMOND, ArmorType.HELMET);
	@DataGenItem
	public static final Item IMPERIAL_PILOT_COLD_HELMET = registerArmorItem("imperial_pilot_cold_helmet", ArmorMaterials.DIAMOND, ArmorType.HELMET);
	@DataGenItem
	public static final Item REBEL_PILOT_HELMET = registerArmorItem("rebel_pilot_helmet", ArmorMaterials.DIAMOND, ArmorType.HELMET);
	@DataGenItem
	public static final Item REBEL_PILOT_KIT = registerArmorItem("rebel_pilot_kit", ArmorMaterials.DIAMOND, ArmorType.CHESTPLATE);
	@DataGenItem
	public static final Item REBEL_FOREST = registerArmorItem("rebel_forest_helmet", ArmorMaterials.DIAMOND, ArmorType.HELMET);
	@DataGenItem
	public static final Item REBEL_TROPICAL = registerArmorItem("rebel_tropical_helmet", ArmorMaterials.DIAMOND, ArmorType.HELMET);
	@DataGenItem
	public static final Item BLACK_IMPERIAL_OFFICER = registerArmorItem("black_imperial_officer_hat", ArmorMaterials.LEATHER, ArmorType.HELMET);
	@DataGenItem
	public static final Item GRAY_IMPERIAL_OFFICER = registerArmorItem("gray_imperial_officer_hat", ArmorMaterials.LEATHER, ArmorType.HELMET);
	@DataGenItem
	public static final Item LIGHT_GRAY_IMPERIAL_OFFICER = registerArmorItem("light_gray_imperial_officer_hat", ArmorMaterials.LEATHER, ArmorType.HELMET);
	@DataGenItem
	public static final Item KHAKI_IMPERIAL_OFFICER = registerArmorItem("khaki_imperial_officer_hat", ArmorMaterials.LEATHER, ArmorType.HELMET);
	@DataGenItem
	public static final Item TAN_GOGGLES_CAP = registerArmorItem("tan_goggles_cap", ArmorMaterials.LEATHER, ArmorType.HELMET);
	@DataGenItem
	public static final Item GRAY_GOGGLES_CAP = registerArmorItem("gray_goggles_cap", ArmorMaterials.LEATHER, ArmorType.HELMET);
	@DataGenItem
	public static final Item BROWN_GOGGLES_CAP = registerArmorItem("brown_goggles_cap", ArmorMaterials.LEATHER, ArmorType.HELMET);
	@DataGenItem
	public static final Item BEACH_INSURGENCE_HAT = registerArmorItem("beach_insurgence_hat", ArmorMaterials.LEATHER, ArmorType.HELMET);
	@DataGenItem
	public static final Item DESERT_INSURGENCE_HAT = registerArmorItem("desert_insurgence_hat", ArmorMaterials.LEATHER, ArmorType.HELMET);

	/// DOOR.

	@DataGenItem
	public static final DyedItems DOOR_INSERT = new DyedItems(color -> Registrar.item(Galaxies.id(color.name().toLowerCase() + "_door_insert"), settings -> new DoorInsertItem(color, settings), new Item.Properties()));

	/// CRAFTING COMPONENTS

	@DataGenItem
	public static final Item ELECTRIC_MOTOR = registerSimpleItem("electric_motor", new Item.Properties().component(Components.METAL_COMPONENT, 1).component(Components.TECH_COMPONENT, 2).component(Components.ENERGY_COMPONENT, 3).component(Components.SCRAP, false));
	@DataGenItem
	public static final Item TURBINE = registerSimpleItem("turbine", new Item.Properties().component(Components.METAL_COMPONENT, 1).component(Components.TECH_COMPONENT, 2));
	@DataGenItem
	public static final Item BALL_BEARING = registerSimpleItem("ball_bearing", new Item.Properties().component(Components.METAL_COMPONENT, 2));
	@DataGenItem
	public static final Item DESH_WIRE = registerSimpleItem("desh_wire", new Item.Properties().component(Components.METAL_COMPONENT, 1).component(Components.PLASTIC_COMPONENT, 1));
	@DataGenItem
	public static final Item DESH_COIL = registerSimpleItem("desh_coil", new Item.Properties().component(Components.METAL_COMPONENT, 2).component(Components.PLASTIC_COMPONENT, 2).component(Components.ENERGY_COMPONENT, 2).component(Components.SCRAP, false));
	@DataGenItem
	public static final Item LIGHT_PANEL = registerSimpleItem("light_panel", new Item.Properties().component(Components.PLASTIC_COMPONENT, 2).component(Components.TECH_COMPONENT, 2).component(Components.SCRAP, false));
	@DataGenItem
	public static final Item DISPLAY_PANEL = registerSimpleItem("display_panel", new Item.Properties().component(Components.PLASTIC_COMPONENT, 1).component(Components.TECH_COMPONENT, 2).component(Components.ENERGY_COMPONENT, 2).component(Components.SCRAP, false));
	@DataGenItem
	public static final Item PLASTEEL_ROD = registerSimpleItem("plasteel_rod", new Item.Properties().component(Components.PLASTIC_COMPONENT, 1));
	@DataGenItem
	public static final Item DURASTEEL_ROD = registerSimpleItem("durasteel_rod", new Item.Properties().component(Components.METAL_COMPONENT, 1));

	// TODO: Implement wire

	/// MATERIAL

	@DataGenItem
	public static final Item BESKAR_RAW = registerSimpleItem("raw_beskar");
	@DataGenItem
	public static final Item BESKAR_INGOT = registerSimpleItem("beskar_ingot");
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final ShovelItem BESKAR_SHOVEL = Registrar.item(Galaxies.id("beskar_shovel"), settings -> new ShovelItem(GalaxiesToolMaterials.BESKAR, 1.5F, -3.0F, settings), new Item.Properties());
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final Item BESKAR_PICKAXE = Registrar.item(Galaxies.id("beskar_pickaxe"), Item::new, new Item.Properties().pickaxe(GalaxiesToolMaterials.BESKAR, 1, -2.8F));
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final AxeItem BESKAR_AXE = Registrar.item(Galaxies.id("beskar_axe"), settings -> new AxeItem(GalaxiesToolMaterials.BESKAR, 5, -3.0F, settings), new Item.Properties());
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final HoeItem BESKAR_HOE = Registrar.item(Galaxies.id("beskar_hoe"), settings -> new HoeItem(GalaxiesToolMaterials.BESKAR, 0, 0.0F, settings), new Item.Properties());

	@DataGenItem
	public static final Item CHROMIUM_RAW = registerSimpleItem("raw_chromium");
	@DataGenItem
	public static final Item CHROMIUM_INGOT = registerSimpleItem("chromium_ingot");
	@DataGenItem
	public static final Item CHROMIUM_NUGGET = registerSimpleItem("chromium_nugget");

	@DataGenItem
	public static final Item CORTOSIS_RAW = registerSimpleItem("raw_cortosis");
	@DataGenItem
	public static final Item CORTOSIS_INGOT = registerSimpleItem("cortosis_ingot");

	@DataGenItem
	public static final Item DESH_RAW = registerSimpleItem("raw_desh");
	@DataGenItem
	public static final Item DESH_INGOT = registerSimpleItem("desh_ingot");
	@DataGenItem
	public static final Item DESH_NUGGET = registerSimpleItem("desh_nugget");

	@DataGenItem
	public static final Item DIATIUM_RAW = registerSimpleItem("raw_diatium");
	@DataGenItem
	public static final Item DIATIUM_INGOT = registerSimpleItem("diatium_ingot");
	@DataGenItem
	public static final Item DIATIUM_NUGGET = registerSimpleItem("diatium_nugget");

	@DataGenItem
	public static final Item DURASTEEL_INGOT = registerSimpleItem("durasteel_ingot");
	@DataGenItem
	public static final Item DURASTEEL_NUGGET = registerSimpleItem("durasteel_nugget");
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final ShovelItem DURASTEEL_SHOVEL = Registrar.item(Galaxies.id("durasteel_shovel"), settings -> new ShovelItem(GalaxiesToolMaterials.DURASTEEL, 1.5F, -3.0F, settings), new Item.Properties());
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final AxeItem DURASTEEL_AXE = Registrar.item(Galaxies.id("durasteel_axe"), settings -> new AxeItem(GalaxiesToolMaterials.DURASTEEL, 5, -3.0F, settings), new Item.Properties());
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final HoeItem DURASTEEL_HOE = Registrar.item(Galaxies.id("durasteel_hoe"), settings -> new HoeItem(GalaxiesToolMaterials.DURASTEEL, 0, 0.0F, settings), new Item.Properties());
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final Item DURASTEEL_PICKAXE = Registrar.item(Galaxies.id("durasteel_pickaxe"), Item::new, new Item.Properties().pickaxe(GalaxiesToolMaterials.DURASTEEL, 1, -2.8F));

	@DataGenItem
	public static final Item EXONIUM_CRYSTAL = registerSimpleItem("exonium");

	@DataGenItem
	public static final Item HELICITE_CRYSTAL = registerSimpleItem("helicite_crystal");
	@DataGenItem
	public static final Item HELICITE_DUST = registerSimpleItem("helicite_dust");

	@DataGenItem
	public static final Item IONITE_RAW = registerSimpleItem("raw_ionite");
	@DataGenItem
	public static final Item IONITE_INGOT = registerSimpleItem("ionite_ingot");
	@DataGenItem
	public static final Item IONITE_NUGGET = registerSimpleItem("ionite_nugget");

	@DataGenItem
	public static final Item KELERIUM_RAW = registerSimpleItem("raw_kelerium");
	@DataGenItem
	public static final Item KELERIUM_INGOT = registerSimpleItem("kelerium_ingot");

	@DataGenItem
	public static final Item LOMMITE_CRYSTAL = registerSimpleItem("lommite_crystal");
	@DataGenItem
	public static final Item LOMMITE_DUST = registerSimpleItem("lommite_dust");

	@DataGenItem
	public static final Item PLASTEEL_INGOT = registerSimpleItem("plasteel_ingot");
	@DataGenItem
	public static final Item PLASTEEL_NUGGET = registerSimpleItem("plasteel_nugget");

	@DataGenItem
	public static final Item RUBINDUM_RAW = registerSimpleItem("raw_rubindum");
	@DataGenItem
	public static final Item RUBINDUM_SHARD = registerSimpleItem("rubindum_shard");
	@DataGenItem
	public static final Item THORILIDE_CRYSTAL = registerSimpleItem("thorilide_crystal");
	@DataGenItem
	public static final Item THORILIDE_DUST = registerSimpleItem("thorilide_dust");

	@DataGenItem
	public static final Item TITANIUM_RAW = registerSimpleItem("raw_titanium");
	@DataGenItem
	public static final Item TITANIUM_INGOT = registerSimpleItem("titanium_ingot");
	@DataGenItem
	public static final Item TITANIUM_NUGGET = registerSimpleItem("titanium_nugget");
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final ShovelItem TITANIUM_SHOVEL = Registrar.item(Galaxies.id("titanium_shovel"), settings -> new ShovelItem(GalaxiesToolMaterials.TITANIUM, 1.5F, -3.0F, settings), new Item.Properties());
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final Item TITANIUM_PICKAXE = Registrar.item(Galaxies.id("titanium_pickaxe"), Item::new, new Item.Properties().pickaxe(GalaxiesToolMaterials.TITANIUM, 1, -2.8F));
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final AxeItem TITANIUM_AXE = Registrar.item(Galaxies.id("titanium_axe"), settings -> new AxeItem(GalaxiesToolMaterials.TITANIUM, 5, -3.0F, settings), new Item.Properties());
	@DataGenItem(model = ItemModel.HANDHELD)
	public static final HoeItem TITANIUM_HOE = Registrar.item(Galaxies.id("titanium_hoe"), settings -> new HoeItem(GalaxiesToolMaterials.TITANIUM, 0, 0.0F, settings), new Item.Properties());

	@DataGenItem
	public static final Item TRANSPARISTEEL_INGOT = registerSimpleItem("transparisteel_ingot");

	@DataGenItem
	public static final Item ZERSIUM_CRYSTAL = registerSimpleItem("zersium_crystal");
	@DataGenItem
	public static final Item ZERSIUM_DUST = registerSimpleItem("zersium_dust");

	/// NATURAL
	@DataGenItem
	public static final Item STRIPPED_JAPOR_BRANCH = registerSimpleItem("stripped_japor_branch");
	@DataGenItem
	public static final Item MOLO_FLOWER = registerSimpleItem("molo_flower");
	@DataGenItem
	public static final Item SALT_PILE = registerSimpleItem("salt_pile");

	/// SEEDS
	@DataGenItem(model = ItemModel.NONE)
	public static final Item CHASUKA_SEEDS = registerBlockItem("chasuka_seeds", GalaxiesBlocks.CHASUKA, new Item.Properties().useItemDescriptionPrefix());

	/// FOOD PREP

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.DRINK_CONTAINER)
	public static final Item DURASTEEL_CUP = registerSimpleItem("durasteel_cup", new Item.Properties().component(Components.METAL_COMPONENT, 1));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.DRINK_CONTAINER)
	public static final Item DESH_CUP = registerSimpleItem("desh_cup", new Item.Properties().component(Components.METAL_COMPONENT, 1));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, model = ItemModel.DRINK, overlayTextureOverride = "cup_overlay", invertLayer = true)
	public static final Item FILLED_DURASTEEL_CUP = registerDefaultPotionItem("durasteel_cup_filled", new Item.Properties().usingConvertsTo(DURASTEEL_CUP));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, model = ItemModel.DRINK, overlayTextureOverride = "cup_overlay", invertLayer = true)
	public static final Item FILLED_DESH_CUP = registerDefaultPotionItem("desh_cup_filled", new Item.Properties().usingConvertsTo(DESH_CUP));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.DRINK_CONTAINER)
	public static final DyedItems CUPS = new DyedItems(color -> registerSimpleItem(color.name().toLowerCase() + "_cup"));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, model = ItemModel.DRINK, overlayTextureOverride = "cup_overlay", invertLayer = true)
	public static final DyedItems FILLED_CUPS = new DyedItems(color -> registerDefaultPotionItem(color.name().toLowerCase() + "_cup_filled", new Item.Properties().usingConvertsTo(CUPS.get(color))));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.DRINK_CONTAINER, langOverride = "Glass")
	public static final NumberedItems GLASSES = new NumberedItems(10, i -> registerSimpleItem("glass_" + i));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, langOverride = "Glass", model = ItemModel.DRINK)
	public static final NumberedItems FILLED_GLASSES = new NumberedItems(10, i -> registerDefaultPotionItem("glass_" + i + "_filled", new Item.Properties().usingConvertsTo(GLASSES.get(i - 1))));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.DRINK_CONTAINER, langOverride = "Glass Bottle")
	public static final NumberedItems GLASS_BOTTLES = new NumberedItems(3, i -> registerSimpleItem("glass_bottle_" + i));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, langOverride = "Glass Bottle", model = ItemModel.DRINK)
	public static final NumberedItems FILLED_GLASS_BOTTLES = new NumberedItems(3, i -> registerDefaultPotionItem("glass_bottle_" + i + "_filled", new Item.Properties().usingConvertsTo(GLASS_BOTTLES.get(i - 1))));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.DRINK_CONTAINER, langOverride = "Plastic Bottle")
	public static final NumberedItems PLASTIC_BOTTLES = new NumberedItems(2, i -> registerSimpleItem("plastic_bottle_" + i));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, langOverride = "Plastic Bottle", model = ItemModel.DRINK)
	public static final NumberedItems FILLED_PLASTIC_BOTTLES = new NumberedItems(2, i -> registerDefaultPotionItem("plastic_bottle_" + i + "_filled", new Item.Properties().usingConvertsTo(PLASTIC_BOTTLES.get(i - 1))));
	///  FOOD

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.MIXABLE_FOOD)
	public static final Item JOGAN_FRUIT = registerSimpleItem("jogan_fruit", new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item CHASUKA_LEAF = registerSimpleItem("chasuka_leaf", new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.MIXABLE_FOOD)
	public static final Item MEILOORUN = registerSimpleItem("meiloorun", new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item MYNOCK_WING = registerSimpleItem("mynock_wing", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item FRIED_MYNOCK_WING = registerSimpleItem("cooked_mynock_wing", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item BANTHA_CHOP = registerSimpleItem("bantha_chop", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item BANTHA_STEAK = registerSimpleItem("cooked_bantha_chop", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item NERF_CHOP = registerSimpleItem("nerf_chop", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item NERF_STEAK = registerSimpleItem("cooked_nerf_chop", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item GIZKA_CHOP = registerSimpleItem("gizka_chop", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item GIZKA_STEAK = registerSimpleItem("cooked_gizka_chop", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build()));

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item FLANGTH_TAKEOUT = registerSimpleItem("flangth_takeout", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item FLANGTH_PLATE = registerSimpleItem("flangth_plate", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item DEATH_STICK_RED = registerSimpleItem("death_stick_red", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.DEATH_STICK_RED).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item DEATH_STICK_YELLOW = registerSimpleItem("death_stick_yellow", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.DEATH_STICK_YELLOW).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item MYSTERIOUS_SMOOTHIE = registerSimpleItem("mysterious_smoothie", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.MYSTERIOUS_SMOOTHIE).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item KREETLE_JUICE = registerSimpleItem("kreetlejuice", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.KREETLE_JUICE).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item ABSYNTHESIZED_MALT = registerSimpleItem("absynthesized_malt", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.ABSYNTHESIZED_MALT).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item CORONET_COCKTAIL = registerSimpleItem("coronet_cocktail", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.CORONET_COCKTAIL).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item CLASSIC_SODA = registerSimpleItem("classic_soda", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.SODA).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item DIET_SODA = registerSimpleItem("diet_soda", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.SODA).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item CITRUS_SODA = registerSimpleItem("citrus_soda", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.SODA).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item BOTTLED_WATER = registerSimpleItem("bottled_water", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).component(DataComponents.CONSUMABLE, Components.WATER).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build()));

	// TODO: consider turning this into a Fluid
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.MIXABLE_FOOD)
	public static final Item BLUE_MILK = registerSimpleItem("blue_milk", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item BLUE_MILK_GLASS = registerSimpleItem("blue_milk_glass", new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item BLUE_YOGURT = registerSimpleItem("blue_yogurt", new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item BANTHA_COOKIE = registerSimpleItem("bantha_cookie", new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build()));

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item QRIKKI_BREAD = registerSimpleItem("qrikki_bread", new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item QRIKKI_WAFFLE = registerSimpleItem("qrikki_waffle", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.7F).build()));

	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item AHRISA_BOWL = registerSimpleItem("ahrisa_bowl", new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.MIXABLE_FOOD)
	public static final Item BLACK_MELON = registerSimpleItem("black_melon", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.MIXABLE_FOOD)
	public static final Item DESERT_PLUMS = registerSimpleItem("desert_plums", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item DRIED_POONTEN_GRASS = registerSimpleItem("dried_poonten_grass_bushel", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item HAROUN_BREAD = registerSimpleItem("haroun_bread", new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item HKAK_BEAN = registerSimpleItem("hkak_bean", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.MIXABLE_FOOD)
	public static final Item PALLIE_FRUIT = registerSimpleItem("pallie_fruit", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.MIXABLE_FOOD)
	public static final Item PIKA_FRUIT = registerSimpleItem("pika_fruit", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item TUBER = registerSimpleItem("tuber", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item COOKED_EOPIE_LOIN = registerSimpleItem("cooked_eopie_loin", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item CRISPY_GORG = registerSimpleItem("crispy_gorg", new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item DEWBACK_EGG = registerSimpleItem("dewback_egg", new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item DEWBACK_OMELETTE = registerSimpleItem("dewback_omelette", new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item JERBA_RACK = registerSimpleItem("jerba_rack", new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item JERBA_RIB = registerSimpleItem("jerba_rib", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item KRAYT_MEAT = registerSimpleItem("krayt_meat", new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item RAW_SKETTO_NUGGET = registerSimpleItem("raw_sketto_nugget", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item ROAST_KRAYT = registerSimpleItem("roast_krayt", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item RONTO_CHUCK = registerSimpleItem("ronto_chuck", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item TUBER_MASH = registerSimpleItem("tuber_mash", new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item VAPORATOR_MUSHROOM = registerSimpleItem("vaporator_mushroom", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item WORRT_EGG = registerSimpleItem("worrt_egg", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD, itemTags = DGItemTag.MIXABLE_FOOD)
	public static final Item DEB_DEB = registerSimpleItem("deb_deb", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item EOPIE_LOIN = registerSimpleItem("eopie_loin", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build()));
	@DataGenItem(itemGroup = DataGenItemGroup.FOOD)
	public static final Item HUBBA_GOURD = registerSimpleItem("hubba_gourd", new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));

	/// MOB DROPS
	// TODO: FAA AND LAA BUCKETS WHEN THEY'RE IMPLEMENTED
	@DataGenItem
	public static final Item CORPSE_OF_GORG = registerSimpleItem("corpse_of_gorg");
	@DataGenItem
	public static final Item BANTHA_HORN = registerSimpleItem("bantha_horn");
	@DataGenItem
	public static final Item DEWBACK_BONE = registerSimpleItem("dewback_bone");
	@DataGenItem
	public static final Item DEWBACK_BONE_SHARD = registerSimpleItem("dewback_bone_shard");
	@DataGenItem
	public static final Item EYE_OF_SKETTO = registerSimpleItem("eye_of_sketto");
	@DataGenItem
	public static final Item HIDE = registerSimpleItem("hide");
	@DataGenItem
	public static final Item KRAYT_PEARL = registerSimpleItem("krayt_pearl");
	@DataGenItem
	public static final Item KRAYT_TOOTH = registerSimpleItem("krayt_tooth");
	@DataGenItem
	public static final Item KREETLE_HUSK = registerSimpleItem("kreetle_husk");
	@DataGenItem
	public static final Item LIZARD_GIZZARD = registerSimpleItem("lizard_gizzard");
	@DataGenItem
	public static final Item SQUILL_LIVER = registerSimpleItem("squill_liver");
	@DataGenItem
	public static final Item TONGUE_OF_WORRT = registerSimpleItem("tongue_of_worrt");
	@DataGenItem
	public static final Item TOUGH_HIDE = registerSimpleItem("tough_hide");

	public static Item registerSimpleItem(String key)
	{
		return registerSimpleItem(key, new Item.Properties());
	}

	public static Item registerSimpleItem(String key, Item.Properties settings)
	{
		return Registrar.item(Galaxies.id(key), Item::new, settings);
	}

	public static BlockItem registerBlockItem(String key, Block block, Item.Properties settings)
	{
		return Registrar.item(Galaxies.id(key), itemSettings -> new BlockItem(block, itemSettings), settings);
	}

	public static Item registerDefaultPotionItem(String key, Item.Properties settings)
	{
		return registerSimpleItem(key, settings.component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK));
	}

	public static Item registerArmorItem(String key, ArmorMaterial material, ArmorType type)
	{
		return registerArmorItem(key, material, type, new Item.Properties().stacksTo(1));
	}

	public static Item registerArmorItem(String key, ArmorMaterial material, ArmorType type, Item.Properties itemSettings)
	{
		return Registrar.item(Galaxies.id(key), Item::new, itemSettings.humanoidArmor(material, type));
	}

	public static void register()
	{
		Tags.register();
		Components.register();
	}
}
