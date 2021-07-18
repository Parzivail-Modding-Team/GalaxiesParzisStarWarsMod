package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.block.BlockTatooineHomeDoor;
import com.parzivail.pswg.container.registry.RegistryHelper;
import com.parzivail.pswg.container.registry.RegistryName;
import com.parzivail.pswg.item.DebugItem;
import com.parzivail.pswg.item.SpawnEntityItem;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.BlasterPowerPackItem;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.pswg.item.material.BeskarToolMaterial;
import com.parzivail.pswg.item.material.DurasteelToolMaterial;
import com.parzivail.pswg.item.material.TitaniumToolMaterial;
import com.parzivail.util.item.LiquidFoodItem;
import com.parzivail.util.item.PAxeItem;
import com.parzivail.util.item.PHoeItem;
import com.parzivail.util.item.PPickaxeItem;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;

public class SwgItems
{
	public static class Debug
	{
		@RegistryName("debug")
		public static final DebugItem Debug = new DebugItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
	}

	public static class Door
	{
		@RegistryName("tatooine_home_door")
		public static final Item TatooineHome = new BlockTatooineHomeDoor.Item(SwgBlocks.Door.TatooineHomeController, new Item.Settings().group(Galaxies.Tab));
	}

	public static class CraftingComponents
	{
		@RegistryName("electric_motor")
		public static final Item ElectricMotor = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("turbine")
		public static final Item Turbine = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("ball_bearing")
		public static final Item BallBearing = new Item(new Item.Settings().group(Galaxies.Tab));

		@RegistryName("desh_wire")
		public static final Item DeshWire = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("desh_coil")
		public static final Item DeshCoil = new Item(new Item.Settings().group(Galaxies.Tab));

		@RegistryName("light_panel")
		public static final Item LightPanel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("display_panel")
		public static final Item DisplayPanel = new Item(new Item.Settings().group(Galaxies.Tab));

		@RegistryName("plasteel_rod")
		public static final Item PlasteelRod = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("zersium_rod")
		public static final Item ZersiumRod = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("durasteel_rod")
		public static final Item DurasteelRod = new Item(new Item.Settings().group(Galaxies.Tab));

		@RegistryName("stripped_japor_branch")
		public static final Item StrippedJaporBranch = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Hoe
	{
		@RegistryName("durasteel_hoe")
		public static final ToolItem Durasteel = new PHoeItem(DurasteelToolMaterial.INSTANCE, 0, 0.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_hoe")
		public static final ToolItem Titanium = new PHoeItem(TitaniumToolMaterial.INSTANCE, 0, 0.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("beskar_hoe")
		public static final ToolItem Beskar = new PHoeItem(BeskarToolMaterial.INSTANCE, 0, 0.0F, new Item.Settings().group(Galaxies.Tab));
	}

	public static class Axe
	{
		@RegistryName("durasteel_axe")
		public static final ToolItem Durasteel = new PAxeItem(DurasteelToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_axe")
		public static final ToolItem Titanium = new PAxeItem(TitaniumToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("beskar_axe")
		public static final ToolItem Beskar = new PAxeItem(BeskarToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings().group(Galaxies.Tab));
	}

	public static class Shovel
	{
		@RegistryName("durasteel_shovel")
		public static final ToolItem Durasteel = new ShovelItem(DurasteelToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_shovel")
		public static final ToolItem Titanium = new ShovelItem(TitaniumToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("beskar_shovel")
		public static final ToolItem Beskar = new ShovelItem(BeskarToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings().group(Galaxies.Tab));
	}

	public static class Pickaxe
	{
		@RegistryName("durasteel_pickaxe")
		public static final ToolItem Durasteel = new PPickaxeItem(DurasteelToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_pickaxe")
		public static final ToolItem Titanium = new PPickaxeItem(TitaniumToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("beskar_pickaxe")
		public static final ToolItem Beskar = new PPickaxeItem(BeskarToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings().group(Galaxies.Tab));
	}

	public static class Dust
	{
		@RegistryName("ionite_dust")
		public static final Item Ionite = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Crystal
	{
		@RegistryName("exonium")
		public static final Item Exonium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("helicite_crystal")
		public static final Item Helicite = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("ionite_crystal")
		public static final Item Ionite = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("lommite_crystal")
		public static final Item Lommite = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("thorilide_crystal")
		public static final Item Thorilide = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Nugget
	{
		@RegistryName("chromium_nugget")
		public static final Item Chromium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("desh_nugget")
		public static final Item Desh = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("diatium_nugget")
		public static final Item Diatium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("durasteel_nugget")
		public static final Item Durasteel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("lommite_nugget")
		public static final Item Lommite = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("plasteel_nugget")
		public static final Item Plasteel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_nugget")
		public static final Item Titanium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("zersium_nugget")
		public static final Item Zersium = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Ingot
	{
		@RegistryName("beskar_ingot")
		public static final Item Beskar = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("chromium_ingot")
		public static final Item Chromium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("cortosis_ingot")
		public static final Item Cortosis = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("desh_ingot")
		public static final Item Desh = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("diatium_ingot")
		public static final Item Diatium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("durasteel_ingot")
		public static final Item Durasteel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("lommite_ingot")
		public static final Item Lommite = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("plasteel_ingot")
		public static final Item Plasteel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_ingot")
		public static final Item Titanium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("transparisteel_ingot")
		public static final Item Transparisteel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("zersium_ingot")
		public static final Item Zersium = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	// TODO: some of these might belong into Crystal, or somewhere else.
	//  Also, check if the names are consistent with vanilla 1.17.
	public static class RawOre
	{
		@RegistryName("raw_beskar")
		public static final Item Beskar = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("raw_chromium")
		public static final Item Chromium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("raw_cortosis")
		public static final Item Cortosis = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("raw_desh")
		public static final Item Desh = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("raw_diatium")
		public static final Item Diatium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("raw_kelerium")
		public static final Item Kelerium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("raw_rubindum")
		public static final Item Rubindum = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("raw_titanium")
		public static final Item Titanium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("raw_zersium")
		public static final Item Zersium = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Food
	{
		@RegistryName("salt_pile")
		public static final Item SaltPile = new Item(new Item.Settings().group(Galaxies.Tab));

		@RegistryName("jogan_fruit")
		public static final Item JoganFruit = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("chasuka_leaf")
		public static final Item ChasukaLeaf = new AliasedBlockItem(SwgBlocks.Plant.Chasuka, new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("meiloorun")
		public static final Item Meiloorun = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));

		@RegistryName("mynock_wing")
		public static final Item MynockWing = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		@RegistryName("cooked_mynock_wing")
		public static final Item FriedMynockWing = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));
		@RegistryName("bantha_chop")
		public static final Item BanthaChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		@RegistryName("cooked_bantha_chop")
		public static final Item BanthaSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));
		@RegistryName("nerf_chop")
		public static final Item NerfChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		@RegistryName("cooked_nerf_chop")
		public static final Item NerfSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));
		@RegistryName("gizka_chop")
		public static final Item GizkaChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		@RegistryName("cooked_gizka_chop")
		public static final Item GizkaSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));

		// TODO: are these meat?
		@RegistryName("flangth_takeout")
		public static final Item FlangthTakeout = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		@RegistryName("flangth_plate")
		public static final Item FlangthPlate = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));

		@RegistryName("death_stick_red")
		public static final Item DeathStickRed = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 200), 0.8F).statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 1).build()).group(Galaxies.Tab));
		@RegistryName("death_stick_yellow")
		public static final Item DeathStickYellow = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).snack().alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200), 0.8F).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200), 1).build()).group(Galaxies.Tab));

		// TODO: consider turning this into a Fluid
		@RegistryName("blue_milk")
		public static final Item BlueMilk = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("blue_yogurt")
		public static final Item BlueYogurt = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));

		@RegistryName("qrikki_bread")
		public static final Item QrikkiBread = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).build()).group(Galaxies.Tab));
		@RegistryName("qrikki_waffle")
		public static final Item QrikkiWaffle = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.7F).build()).group(Galaxies.Tab));

		@RegistryName("ahrisa_bowl")
		public static final Item AhrisaBowl = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("black_melon")
		public static final Item BlackMelon = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("chasuka_seeds")
		public static final Item ChasukaSeeds = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("desert_plums")
		public static final Item DesertPlums = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("dried_poonten_grass_bushel")
		public static final Item DriedPoontenGrass = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("haroun_bread")
		public static final Item HarounBread = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("hkak_bean")
		public static final Item HkakBean = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("pallie_fruit")
		public static final Item PallieFruit = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("pika_fruit")
		public static final Item PikaFruit = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("tuber")
		public static final Item Tuber = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("cooked_eopie_loin")
		public static final Item CookedEopieLoin = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("crispy_gorg")
		public static final Item CrispyGorg = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("dewback_egg")
		public static final Item DewbackEgg = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("dewback_omlette")
		public static final Item DewbackOmlette = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("jerba_rack")
		public static final Item JerbaRack = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("jerba_rib")
		public static final Item JerbaRib = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("krayt_meat")
		public static final Item KraytMeat = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("raw_sketto_nugget")
		public static final Item RawSkettoNugget = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("roast_krayt")
		public static final Item RoastKrayt = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("ronto_chuck")
		public static final Item RontoChuck = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("tuber_mash")
		public static final Item TuberMash = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("vaporator_mushroom")
		public static final Item VaporatorMushroom = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("worrt_egg")
		public static final Item WorrtEgg = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("deb_deb")
		public static final Item DebDeb = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("eopie_loin")
		public static final Item EopieLoin = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("hubba_gourd")
		public static final Item HubbaGourd = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build()).group(Galaxies.Tab));
	}

	public static class MobDrops
	{
		@RegistryName("faa_bucket")
		public static final Item FaaBucket = new EntityBucketItem(SwgEntities.Fish.Faa, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, (new Item.Settings()).maxCount(1).group(Galaxies.Tab));
		@RegistryName("laa_bucket")
		public static final Item LaaBucket = new EntityBucketItem(SwgEntities.Fish.Laa, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, (new Item.Settings()).maxCount(1).group(Galaxies.Tab));
		@RegistryName("corpse_of_gorg")
		public static final Item CorpseOfGorg = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("bantha_horn")
		public static final Item BanthaHorn = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("dewback_bone")
		public static final Item DewbackBone = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("dewback_bone_shard")
		public static final Item DewbackBoneShard = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("eye_of_sketto")
		public static final Item EyeOfSketto = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("hide")
		public static final Item Hide = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("krayt_pearl")
		public static final Item KraytPearl = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("krayt_tooth")
		public static final Item KraytTooth = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("kreetle_husk")
		public static final Item KreetleHusk = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("lizard_gizzard")
		public static final Item LizardGizzard = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("squill_liver")
		public static final Item SquillLiver = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("tongue_of_worrt")
		public static final Item TongueOfWorrt = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("tough_hide")
		public static final Item ToughHide = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Blaster
	{
		@RegistryName("blaster")
		public static final BlasterItem Blaster = new BlasterItem(new Item.Settings().maxCount(1));
		@RegistryName("small_power_pack")
		public static final BlasterPowerPackItem SmallPowerPack = new BlasterPowerPackItem(75, new Item.Settings().group(Galaxies.Tab));
	}

	public static class Spawners
	{
		@RegistryName("spawn_xwing_t65b")
		public static final Item XwingT65b = new SpawnEntityItem(SwgEntities.Ship.T65bXwing, new Item.Settings().group(Galaxies.Tab));

		@RegistryName("spawn_faa")
		public static final Item Faa = new SpawnEggItem(SwgEntities.Fish.Faa, 0xE9933E, 0x9471EB, new Item.Settings().group(Galaxies.Tab));

		@RegistryName("spawn_laa")
		public static final Item Laa = new SpawnEggItem(SwgEntities.Fish.Laa, 0x2F5747, 0xD5BC92, new Item.Settings().group(Galaxies.Tab));

		@RegistryName("spawn_worrt")
		public static final Item Worrt = new SpawnEggItem(SwgEntities.Amphibian.Worrt, 0x5B482C, 0x635735, new Item.Settings().group(Galaxies.Tab));
	}

	public static class Lightsaber
	{
		@RegistryName("lightsaber")
		public static final LightsaberItem Lightsaber = new LightsaberItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
	}

	public static void register()
	{
		RegistryHelper.registerAnnotatedFields(SwgItems.class, Item.class, (instance, registryName, ignoreTab) -> Registry.register(Registry.ITEM, registryName, instance));
	}
}
