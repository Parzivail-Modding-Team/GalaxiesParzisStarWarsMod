package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.pswg.features.blasters.BlasterPowerPackItem;
import com.parzivail.pswg.features.lightsabers.LightsaberItem;
import com.parzivail.pswg.item.CableItem;
import com.parzivail.pswg.item.DebugItem;
import com.parzivail.pswg.item.DoorInsertItem;
import com.parzivail.pswg.item.jetpack.JetpackItem;
import com.parzivail.pswg.item.material.BeskarToolMaterial;
import com.parzivail.pswg.item.material.DurasteelToolMaterial;
import com.parzivail.pswg.item.material.TitaniumToolMaterial;
import com.parzivail.tarkin.api.TarkinItem;
import com.parzivail.tarkin.api.TrItemTag;
import com.parzivail.tarkin.api.TrModel;
import com.parzivail.util.item.*;
import com.parzivail.util.registry.*;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class SwgItems
{
	@RegistryOrder(0)
	public static class Debug
	{
		@RegistryName("debug")
		@TabIgnore
		@TarkinItem
		public static final DebugItem Debug = new DebugItem(new Item.Settings().maxCount(1));
	}

	@RegistryOrder(1)
	public static class Armor
	{
		@RegistryName("stormtrooper")
		public static final ArmorItems Stormtrooper = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
		@RegistryName("shocktrooper")
		public static final ArmorItems Shocktrooper = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
		@RegistryName("purgetrooper")
		public static final ArmorItems Purgetrooper = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
		@RegistryName("sandtrooper")
		public static final ArmorItems Sandtrooper = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
		@RegistryName("sandtrooper_backpack")
		@TarkinItem(tags = { TrItemTag.TrinketsChestBack })
		public static final TrinketItem SandtrooperBackpack = new TrinketItem(new Item.Settings().maxCount(1));
		@RegistryName("deathtrooper")
		public static final ArmorItems Deathtrooper = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
		@RegistryName("scouttrooper")
		public static final ArmorItems Scouttrooper = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
		@RegistryName("shoretrooper")
		public static final ArmorItems Shoretrooper = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
		@RegistryName("jumptrooper")
		public static final ArmorItems Jumptrooper = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
		@RegistryName("jumptrooper_jetpack")
		@TarkinItem(tags = { TrItemTag.TrinketsChestBack })
		public static final TrinketItem JumptrooperJetpack = new JetpackItem(new Item.Settings().maxCount(1), new JetpackItem.Stats());
		@RegistryName("imperial_pilot_helmet")
		@TarkinItem
		public static final ArmorItem ImperialPilotHelmet = new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
		@RegistryName("imperial_pilot_kit")
		@TarkinItem
		public static final ArmorItem ImperialPilotKit = new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxCount(1));
		@RegistryName("rebel_pilot_helmet")
		@TarkinItem
		public static final ArmorItem RebelPilotHelmet = new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
		@RegistryName("rebel_pilot_kit")
		@TarkinItem
		public static final ArmorItem RebelPilotKit = new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxCount(1));
		@RegistryName("rebel_forest_helmet")
		@TarkinItem
		public static final ArmorItem RebelForest = new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
		@RegistryName("rebel_tropical_helmet")
		@TarkinItem
		public static final ArmorItem RebelTropical = new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
		@RegistryName("black_imperial_officer_hat")
		@TarkinItem
		public static final ArmorItem BlackImperialOfficer = new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
		@RegistryName("gray_imperial_officer_hat")
		@TarkinItem
		public static final ArmorItem GrayImperialOfficer = new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
		@RegistryName("light_gray_imperial_officer_hat")
		@TarkinItem
		public static final ArmorItem LightGrayImperialOfficer = new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
		@RegistryName("khaki_imperial_officer_hat")
		@TarkinItem
		public static final ArmorItem KhakiImperialOfficer = new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
	}

	@RegistryOrder(2)
	public static class Door
	{
		@RegistryName("door_insert")
		public static final DyedItems DoorInsert = new DyedItems(color -> new DoorInsertItem(color, new Item.Settings()));
	}

	@RegistryOrder(3)
	public static class Cable
	{
		@RegistryName("insulated_desh_cable")
		@TarkinItem
		public static final Item Power = new CableItem(new Item.Settings());
	}

	@RegistryOrder(4)
	public static class CraftingComponents
	{
		@RegistryName("electric_motor")
		@TarkinItem
		public static final Item ElectricMotor = new Item(new Item.Settings());
		@RegistryName("turbine")
		@TarkinItem
		public static final Item Turbine = new Item(new Item.Settings());
		@RegistryName("ball_bearing")
		@TarkinItem
		public static final Item BallBearing = new Item(new Item.Settings());

		@RegistryName("desh_wire")
		@TarkinItem
		public static final Item DeshWire = new Item(new Item.Settings());
		@RegistryName("desh_coil")
		@TarkinItem
		public static final Item DeshCoil = new Item(new Item.Settings());

		@RegistryName("light_panel")
		@TarkinItem
		public static final Item LightPanel = new Item(new Item.Settings());
		@RegistryName("display_panel")
		@TarkinItem
		public static final Item DisplayPanel = new Item(new Item.Settings());

		@RegistryName("plasteel_rod")
		@TarkinItem
		public static final Item PlasteelRod = new Item(new Item.Settings());
		@RegistryName("durasteel_rod")
		@TarkinItem
		public static final Item DurasteelRod = new Item(new Item.Settings());
	}

	@RegistryOrder(5)
	public static class Material
	{
		@RegistryName("raw_beskar")
		@TarkinItem
		public static final Item BeskarRaw = new Item(new Item.Settings());
		@RegistryName("beskar_ingot")
		@TarkinItem
		public static final Item BeskarIngot = new Item(new Item.Settings());
		@RegistryName("beskar_shovel")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem BeskarShovel = new ShovelItem(BeskarToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings());
		@RegistryName("beskar_pickaxe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem BeskarPickaxe = new PPickaxeItem(BeskarToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings());
		@RegistryName("beskar_axe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem BeskarAxe = new PAxeItem(BeskarToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings());
		@RegistryName("beskar_hoe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem BeskarHoe = new PHoeItem(BeskarToolMaterial.INSTANCE, 0, 0.0F, new Item.Settings());

		@RegistryName("raw_chromium")
		@TarkinItem
		public static final Item ChromiumRaw = new Item(new Item.Settings());
		@RegistryName("chromium_ingot")
		@TarkinItem
		public static final Item ChromiumIngot = new Item(new Item.Settings());
		@RegistryName("chromium_nugget")
		@TarkinItem
		public static final Item ChromiumNugget = new Item(new Item.Settings());

		@RegistryName("raw_cortosis")
		@TarkinItem
		public static final Item CortosisRaw = new Item(new Item.Settings());
		@RegistryName("cortosis_ingot")
		@TarkinItem
		public static final Item CortosisIngot = new Item(new Item.Settings());

		@RegistryName("raw_desh")
		@TarkinItem
		public static final Item DeshRaw = new Item(new Item.Settings());
		@RegistryName("desh_ingot")
		@TarkinItem
		public static final Item DeshIngot = new Item(new Item.Settings());
		@RegistryName("desh_nugget")
		@TarkinItem
		public static final Item DeshNugget = new Item(new Item.Settings());

		@RegistryName("raw_diatium")
		@TarkinItem
		public static final Item DiatiumRaw = new Item(new Item.Settings());
		@RegistryName("diatium_ingot")
		@TarkinItem
		public static final Item DiatiumIngot = new Item(new Item.Settings());
		@RegistryName("diatium_nugget")
		@TarkinItem
		public static final Item DiatiumNugget = new Item(new Item.Settings());

		@RegistryName("durasteel_ingot")
		@TarkinItem
		public static final Item DurasteelIngot = new Item(new Item.Settings());
		@RegistryName("durasteel_nugget")
		@TarkinItem
		public static final Item DurasteelNugget = new Item(new Item.Settings());
		@RegistryName("durasteel_shovel")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem DurasteelShovel = new ShovelItem(DurasteelToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings());
		@RegistryName("durasteel_axe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem DurasteelAxe = new PAxeItem(DurasteelToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings());
		@RegistryName("durasteel_hoe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem DurasteelHoe = new PHoeItem(DurasteelToolMaterial.INSTANCE, 0, 0.0F, new Item.Settings());
		@RegistryName("durasteel_pickaxe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem DurasteelPickaxe = new PPickaxeItem(DurasteelToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings());

		@RegistryName("exonium")
		@TarkinItem
		public static final Item ExoniumCrystal = new Item(new Item.Settings());

		@RegistryName("helicite_crystal")
		@TarkinItem
		public static final Item HeliciteCrystal = new Item(new Item.Settings());
		@RegistryName("helicite_dust")
		@TarkinItem
		public static final Item HeliciteDust = new Item(new Item.Settings());

		@RegistryName("raw_ionite")
		@TarkinItem
		public static final Item IoniteRaw = new Item(new Item.Settings());
		@RegistryName("ionite_ingot")
		@TarkinItem
		public static final Item IoniteIngot = new Item(new Item.Settings());
		@RegistryName("ionite_nugget")
		@TarkinItem
		public static final Item IoniteNugget = new Item(new Item.Settings());

		@RegistryName("raw_kelerium")
		@TarkinItem
		public static final Item KeleriumRaw = new Item(new Item.Settings());
		@RegistryName("kelerium_ingot")
		@TarkinItem
		public static final Item KeleriumIngot = new Item(new Item.Settings());

		@RegistryName("lommite_crystal")
		@TarkinItem
		public static final Item LommiteCrystal = new Item(new Item.Settings());
		@RegistryName("lommite_dust")
		@TarkinItem
		public static final Item LommiteDust = new Item(new Item.Settings());

		@RegistryName("plasteel_ingot")
		@TarkinItem
		public static final Item PlasteelIngot = new Item(new Item.Settings());
		@RegistryName("plasteel_nugget")
		@TarkinItem
		public static final Item PlasteelNugget = new Item(new Item.Settings());

		@RegistryName("raw_rubindum")
		@TarkinItem
		public static final Item RubindumRaw = new Item(new Item.Settings());
		@RegistryName("rubindum_shard")
		@TarkinItem
		public static final Item RubindumShard = new Item(new Item.Settings());

		@RegistryName("thorilide_crystal")
		@TarkinItem
		public static final Item ThorilideCrystal = new Item(new Item.Settings());
		@RegistryName("thorilide_dust")
		@TarkinItem
		public static final Item ThorilideDust = new Item(new Item.Settings());

		@RegistryName("raw_titanium")
		@TarkinItem
		public static final Item TitaniumRaw = new Item(new Item.Settings());
		@RegistryName("titanium_ingot")
		@TarkinItem
		public static final Item TitaniumIngot = new Item(new Item.Settings());
		@RegistryName("titanium_nugget")
		@TarkinItem
		public static final Item TitaniumNugget = new Item(new Item.Settings());
		@RegistryName("titanium_shovel")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem TitaniumShovel = new ShovelItem(TitaniumToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings());
		@RegistryName("titanium_pickaxe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem TitaniumPickaxe = new PPickaxeItem(TitaniumToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings());
		@RegistryName("titanium_axe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem TitaniumAxe = new PAxeItem(TitaniumToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings());
		@RegistryName("titanium_hoe")
		@TarkinItem(model = TrModel.HandheldItem)
		public static final ToolItem TitaniumHoe = new PHoeItem(TitaniumToolMaterial.INSTANCE, 0, 0.0F, new Item.Settings());

		@RegistryName("transparisteel_ingot")
		@TarkinItem
		public static final Item TransparisteelIngot = new Item(new Item.Settings());

		@RegistryName("zersium_crystal")
		@TarkinItem
		public static final Item ZersiumCrystal = new Item(new Item.Settings());
		@RegistryName("zersium_dust")
		@TarkinItem
		public static final Item ZersiumDust = new Item(new Item.Settings());
	}

	@RegistryOrder(6)
	public static class Natural
	{
		@RegistryName("stripped_japor_branch")
		@TarkinItem
		public static final Item StrippedJaporBranch = new Item(new Item.Settings());
		@RegistryName("molo_flower")
		@TarkinItem
		public static final Item MoloFlower = new Item(new Item.Settings());
		@RegistryName("salt_pile")
		@TarkinItem
		public static final Item SaltPile = new Item(new Item.Settings());
	}

	@RegistryOrder(7)
	public static class Seeds
	{
		@RegistryName("chasuka_seeds")
		@TarkinItem
		public static final Item ChasukaSeeds = new AliasedBlockItem(SwgBlocks.Plant.Chasuka, new Item.Settings());
	}

	@RegistryOrder(8)
	public static class FoodPrep
	{
		@RegistryName("durasteel_cup")
		@TarkinItem
		public static final Item DurasteelCup = new Item(new Item.Settings());
		@RegistryName("desh_cup")
		@TarkinItem
		public static final Item DeshCup = new Item(new Item.Settings());
		@RegistryName("cup")
		public static final DyedItems Cups = new DyedItems(color -> new Item(new Item.Settings()));
		@RegistryName("glass")
		public static final NumberedItems Glasses = new NumberedItems(10, i -> new Item(new Item.Settings()));
		@RegistryName("glass_bottle")
		public static final NumberedItems GlassBottles = new NumberedItems(3, i -> new Item(new Item.Settings()));
		@RegistryName("plastic_bottle")
		public static final NumberedItems PlasticBottles = new NumberedItems(2, i -> new Item(new Item.Settings()));
	}

	@RegistryOrder(9)
	public static class Food
	{
		@RegistryName("jogan_fruit")
		@TarkinItem
		public static final Item JoganFruit = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()));
		@RegistryName("chasuka_leaf")
		@TarkinItem
		public static final Item ChasukaLeaf = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()));
		@RegistryName("meiloorun")
		@TarkinItem
		public static final Item Meiloorun = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()));

		@RegistryName("mynock_wing")
		@TarkinItem
		public static final Item MynockWing = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()));
		@RegistryName("cooked_mynock_wing")
		@TarkinItem
		public static final Item FriedMynockWing = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()));
		@RegistryName("bantha_chop")
		@TarkinItem
		public static final Item BanthaChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()));
		@RegistryName("cooked_bantha_chop")
		@TarkinItem
		public static final Item BanthaSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()));
		@RegistryName("nerf_chop")
		@TarkinItem
		public static final Item NerfChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()));
		@RegistryName("cooked_nerf_chop")
		@TarkinItem
		public static final Item NerfSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()));
		@RegistryName("gizka_chop")
		@TarkinItem
		public static final Item GizkaChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()));
		@RegistryName("cooked_gizka_chop")
		@TarkinItem
		public static final Item GizkaSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()));

		// TODO: are these meat?
		@RegistryName("flangth_takeout")
		@TarkinItem
		public static final Item FlangthTakeout = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()));
		@RegistryName("flangth_plate")
		@TarkinItem
		public static final Item FlangthPlate = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()));

		@RegistryName("death_stick_red")
		@TarkinItem
		public static final Item DeathStickRed = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 200), 1).statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 1).build()));
		@RegistryName("death_stick_yellow")
		@TarkinItem
		public static final Item DeathStickYellow = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200), 1).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200), 1).build()));

		@RegistryName("mysterious_smoothie")
		@TarkinItem
		public static final Item MysteriousSmoothie = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 200), 1).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200), 0.5f).build()));
		@RegistryName("kreetlejuice")
		@TarkinItem
		public static final Item Kreetlejuice = new LoreLiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 200), 1).build()));

		@RegistryName("absynthesized_malt")
		@TarkinItem
		public static final Item AbsynthesizedMalt = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 200), 1).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200), 0.5f).build()));
		@RegistryName("coronet_cocktail")
		@TarkinItem
		public static final Item CoronetCocktail = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200), 1).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200), 0.5f).build()));

		@RegistryName("classic_soda")
		@TarkinItem
		public static final Item ClassicSoda = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200), 1).build()));
		@RegistryName("diet_soda")
		@TarkinItem
		public static final Item DietSoda = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200), 1).build()));
		@RegistryName("citrus_soda")
		@TarkinItem
		public static final Item CitrusSoda = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200), 1).build()));

		@RegistryName("bottled_water")
		@TarkinItem
		public static final Item BottledWater = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 200), 1).build()));

		// TODO: consider turning this into a Fluid
		@RegistryName("blue_milk")
		@TarkinItem
		public static final Item BlueMilk = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(4).snack().saturationModifier(0.3F).build()));
		@RegistryName("blue_milk_glass")
		@TarkinItem
		public static final Item BlueMilkGlass = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(4).snack().saturationModifier(0.3F).build()));
		@RegistryName("blue_yogurt")
		@TarkinItem
		public static final Item BlueYogurt = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()));
		@RegistryName("bantha_cookie")
		@TarkinItem
		public static final Item BanthaCookie = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).build()));

		@RegistryName("qrikki_bread")
		@TarkinItem
		public static final Item QrikkiBread = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).build()));
		@RegistryName("qrikki_waffle")
		@TarkinItem
		public static final Item QrikkiWaffle = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.7F).build()));

		@RegistryName("ahrisa_bowl")
		@TarkinItem
		public static final Item AhrisaBowl = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()));
		@RegistryName("black_melon")
		@TarkinItem
		public static final Item BlackMelon = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("desert_plums")
		@TarkinItem
		public static final Item DesertPlums = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("dried_poonten_grass_bushel")
		@TarkinItem
		public static final Item DriedPoontenGrass = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("haroun_bread")
		@TarkinItem
		public static final Item HarounBread = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.3F).build()));
		@RegistryName("hkak_bean")
		@TarkinItem
		public static final Item HkakBean = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("pallie_fruit")
		@TarkinItem
		public static final Item PallieFruit = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("pika_fruit")
		@TarkinItem
		public static final Item PikaFruit = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("tuber")
		@TarkinItem
		public static final Item Tuber = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("cooked_eopie_loin")
		@TarkinItem
		public static final Item CookedEopieLoin = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build()));
		@RegistryName("crispy_gorg")
		@TarkinItem
		public static final Item CrispyGorg = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.3F).build()));
		@RegistryName("dewback_egg")
		@TarkinItem
		public static final Item DewbackEgg = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()));
		@RegistryName("dewback_omelette")
		@TarkinItem
		public static final Item DewbackOmelette = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.3F).build()));
		@RegistryName("jerba_rack")
		@TarkinItem
		public static final Item JerbaRack = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()));
		@RegistryName("jerba_rib")
		@TarkinItem
		public static final Item JerbaRib = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build()));
		@RegistryName("krayt_meat")
		@TarkinItem
		public static final Item KraytMeat = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.3F).build()));
		@RegistryName("raw_sketto_nugget")
		@TarkinItem
		public static final Item RawSkettoNugget = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("roast_krayt")
		@TarkinItem
		public static final Item RoastKrayt = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build()));
		@RegistryName("ronto_chuck")
		@TarkinItem
		public static final Item RontoChuck = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).build()));
		@RegistryName("tuber_mash")
		@TarkinItem
		public static final Item TuberMash = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()));
		@RegistryName("vaporator_mushroom")
		@TarkinItem
		public static final Item VaporatorMushroom = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("worrt_egg")
		@TarkinItem
		public static final Item WorrtEgg = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("deb_deb")
		@TarkinItem
		public static final Item DebDeb = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
		@RegistryName("eopie_loin")
		@TarkinItem
		public static final Item EopieLoin = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).build()));
		@RegistryName("hubba_gourd")
		@TarkinItem
		public static final Item HubbaGourd = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()));
	}

	@RegistryOrder(10)
	public static class MobDrops
	{
		@RegistryName("faa_bucket")
		@TarkinItem
		public static final Item FaaBucket = new EntityBucketItem(SwgEntities.Fish.Faa, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, (new Item.Settings()).maxCount(1));
		@RegistryName("laa_bucket")
		@TarkinItem
		public static final Item LaaBucket = new EntityBucketItem(SwgEntities.Fish.Laa, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, (new Item.Settings()).maxCount(1));
		@RegistryName("corpse_of_gorg")
		@TarkinItem
		public static final Item CorpseOfGorg = new Item(new Item.Settings());
		@RegistryName("bantha_horn")
		@TarkinItem
		public static final Item BanthaHorn = new Item(new Item.Settings());
		@RegistryName("dewback_bone")
		@TarkinItem
		public static final Item DewbackBone = new Item(new Item.Settings());
		@RegistryName("dewback_bone_shard")
		@TarkinItem
		public static final Item DewbackBoneShard = new Item(new Item.Settings());
		@RegistryName("eye_of_sketto")
		@TarkinItem
		public static final Item EyeOfSketto = new Item(new Item.Settings());
		@RegistryName("hide")
		@TarkinItem
		public static final Item Hide = new Item(new Item.Settings());
		@RegistryName("krayt_pearl")
		@TarkinItem
		public static final Item KraytPearl = new Item(new Item.Settings());
		@RegistryName("krayt_tooth")
		@TarkinItem
		public static final Item KraytTooth = new Item(new Item.Settings());
		@RegistryName("kreetle_husk")
		@TarkinItem
		public static final Item KreetleHusk = new Item(new Item.Settings());
		@RegistryName("lizard_gizzard")
		@TarkinItem
		public static final Item LizardGizzard = new Item(new Item.Settings());
		@RegistryName("squill_liver")
		@TarkinItem
		public static final Item SquillLiver = new Item(new Item.Settings());
		@RegistryName("tongue_of_worrt")
		@TarkinItem
		public static final Item TongueOfWorrt = new Item(new Item.Settings());
		@RegistryName("tough_hide")
		@TarkinItem
		public static final Item ToughHide = new Item(new Item.Settings());
	}

	@RegistryOrder(11)
	public static class Blaster
	{
		@RegistryName("small_power_pack")
		@TabInclude("pswg:blasters")
		@TarkinItem
		public static final BlasterPowerPackItem SmallPowerPack = new BlasterPowerPackItem(75, new Item.Settings());
	}

	@RegistryOrder(12)
	public static class Spawners
	{
		@RegistryName("spawn_xwing_t65b")
		@TarkinItem
		public static final Item XwingT65b = new SpawnEntityItem(SwgEntities.Ship.T65bXwing, new Item.Settings(), 3);

		@RegistryName("spawn_landspeeder_x34")
		@TarkinItem
		public static final Item LandspeederX34 = new SpawnEntityItem(SwgEntities.Speeder.X34, new Item.Settings(), 0);

		@RegistryName("spawn_zephyr_j")
		@TarkinItem
		public static final Item ZephyrJ = new SpawnEntityItem(SwgEntities.Speeder.ZephyrJ, new Item.Settings(), 0);

		@RegistryName("spawn_faa")
		@TarkinItem(model = TrModel.SpawnEgg)
		public static final Item Faa = new SpawnEggItem(SwgEntities.Fish.Faa, 0xE9933E, 0x9471EB, new Item.Settings());

		@RegistryName("spawn_laa")
		@TarkinItem(model = TrModel.SpawnEgg)
		public static final Item Laa = new SpawnEggItem(SwgEntities.Fish.Laa, 0x2F5747, 0xD5BC92, new Item.Settings());

		@RegistryName("spawn_worrt")
		@TarkinItem(model = TrModel.SpawnEgg)
		public static final Item Worrt = new SpawnEggItem(SwgEntities.Amphibian.Worrt, 0x5B482C, 0x635735, new Item.Settings());

		@RegistryName("spawn_bantha")
		@TarkinItem(model = TrModel.SpawnEgg)
		public static final Item Bantha = new SpawnEggItem(SwgEntities.Mammal.Bantha, 0x362318, 0xD1B693, new Item.Settings());

		@RegistryName("spawn_sand_skitter")
		@TarkinItem(model = TrModel.SpawnEgg)
		public static final Item SandSkitter = new SpawnEggItem(SwgEntities.Rodent.SandSkitter, 0x4F3733, 0xE2946E, new Item.Settings());
	}

	@RegistryOrder(13)
	public static class Lightsaber
	{
		@RegistryName("lightsaber")
		@TabInclude("pswg:lightsabers")
		@TarkinItem(model = TrModel.Empty)
		public static final LightsaberItem Lightsaber = new LightsaberItem(new Item.Settings().maxCount(1));
	}

	static HashMap<Identifier, ArrayList<ItemConvertible>> ITEM_GROUPS = new HashMap<>();

	public static void register()
	{
		RegistryHelper.registerAutoId(Resources.MODID, SwgItems.class, Object.class, SwgItems::tryRegisterItem);

		for (var entry : ITEM_GROUPS.entrySet())
		{
			var group = entry.getKey();
			var items = entry.getValue();

			ItemGroupEvents.modifyEntriesEvent(group).register(entries -> {
				for (var item : items)
					if (item instanceof ITabStackProvider customStacks)
						customStacks.appendStacks(entries);
					else
						entries.add(item);
			});
		}
	}

	public static void tryRegisterItem(Object o, Identifier identifier, boolean ignoreTab, String tabOverride)
	{
		if (o instanceof Item item)
			registerWithTab(identifier, item, ignoreTab, tabOverride);
		else if (o instanceof ArmorItems armorItems)
		{
			registerWithTab(new Identifier(identifier.getNamespace(), identifier.getPath() + "_helmet"), armorItems.helmet, ignoreTab, tabOverride);
			registerWithTab(new Identifier(identifier.getNamespace(), identifier.getPath() + "_chestplate"), armorItems.chestplate, ignoreTab, tabOverride);
			registerWithTab(new Identifier(identifier.getNamespace(), identifier.getPath() + "_leggings"), armorItems.leggings, ignoreTab, tabOverride);
			registerWithTab(new Identifier(identifier.getNamespace(), identifier.getPath() + "_boots"), armorItems.boots, ignoreTab, tabOverride);
		}
		else if (o instanceof DyedItems items)
			for (var entry : items.entrySet())
				registerWithTab(new Identifier(identifier.getNamespace(), entry.getKey().getName() + "_" + identifier.getPath()), entry.getValue(), ignoreTab, tabOverride);
		else if (o instanceof NumberedItems items)
			for (var i = 0; i < items.size(); i++)
				registerWithTab(new Identifier(identifier.getNamespace(), identifier.getPath() + "_" + (i + 1)), items.get(i), ignoreTab, tabOverride);
	}

	private static void registerWithTab(Identifier identifier, Item item, boolean ignoreTab, String tabOverride)
	{
		if (!ignoreTab)
		{
			var tab = tabOverride == null ? Galaxies.TabItems.getId() : new Identifier(tabOverride);
			if (!ITEM_GROUPS.containsKey(tab))
				ITEM_GROUPS.put(tab, new ArrayList<>());

			ITEM_GROUPS.get(tab).add(item);
		}

		Registry.register(Registries.ITEM, identifier, item);
	}

	public static void registerAddons()
	{
		for (var blaster : PswgContent.getBlasterPresets().entrySet())
		{
			var id = blaster.getKey();
			registerWithTab(
					getBlasterRegistrationId(id),
					new BlasterItem(new Item.Settings().maxCount(1), id, blaster.getValue()),
					false,
					Galaxies.TabBlasters.getId().toString()
			);
		}
	}

	@NotNull
	public static Identifier getBlasterRegistrationId(Identifier id)
	{
		return new Identifier(id.getNamespace(), "blaster_" + id.getPath());
	}
}
