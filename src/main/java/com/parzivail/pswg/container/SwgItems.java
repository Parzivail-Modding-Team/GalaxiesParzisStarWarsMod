package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.container.registry.RegistryHelper;
import com.parzivail.pswg.container.registry.RegistryName;
import com.parzivail.pswg.item.DebugItem;
import com.parzivail.pswg.item.blaster.BlasterItem;
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
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.registry.Registry;

public class SwgItems
{
	public static class Axe
	{
		@RegistryName("durasteel_axe")
		public static final ToolItem Durasteel = new PAxeItem(DurasteelToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_axe")
		public static final ToolItem Titanium = new PAxeItem(TitaniumToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("beskar_axe")
		public static final ToolItem Beskar = new PAxeItem(BeskarToolMaterial.INSTANCE, 5, -3.0F, new Item.Settings().group(Galaxies.Tab));
	}

	public static class Blaster
	{
		@RegistryName("blaster")
		public static final BlasterItem Blaster = new BlasterItem(new Item.Settings().maxCount(1));
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
		@RegistryName("plasteel_rod")
		public static final Item PlasteelRod = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("durasteel_rod")
		public static final Item DurasteelRod = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("zersium_rod")
		public static final Item ZersiumRod = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("light_panel")
		public static final Item LightPanel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("display_panel")
		public static final Item DisplayPanel = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Crystal
	{
		@RegistryName("ionite_crystal")
		public static final Item Ionite = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Dust
	{
		@RegistryName("ionite_dust")
		public static final Item Ionite = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Debug
	{
		@RegistryName("debug")
		public static final DebugItem Debug = new DebugItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
	}

	public static class Food
	{
		@RegistryName("salt_pile")
		public static final Item SaltPile = new Item(new Item.Settings().group(Galaxies.Tab));

		@RegistryName("jogan_fruit")
		public static final Item JoganFruit = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		@RegistryName("chasuka")
		public static final Item Chasuka = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
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
		@RegistryName("blue_puff_cube")
		public static final Item BluePuffCube = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(1).saturationModifier(0.3F).snack().build()).group(Galaxies.Tab));
		@RegistryName("blue_yogurt")
		public static final Item BlueYogurt = new LiquidFoodItem(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));

		@RegistryName("qrikki_bread")
		public static final Item QrikkiBread = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).build()).group(Galaxies.Tab));
		@RegistryName("qrikki_waffle")
		public static final Item QrikkiWaffle = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.7F).build()).group(Galaxies.Tab));
	}

	//TODO: Make Durasteel and Titanium deal .5 less damage
	public static class Hoe
	{
		@RegistryName("durasteel_hoe")
		public static final ToolItem Durasteel = new PHoeItem(DurasteelToolMaterial.INSTANCE, -2, 0.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_hoe")
		public static final ToolItem Titanium = new PHoeItem(TitaniumToolMaterial.INSTANCE, -3, 0.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("beskar_hoe")
		public static final ToolItem Beskar = new PHoeItem(BeskarToolMaterial.INSTANCE, -5, 0.0F, new Item.Settings().group(Galaxies.Tab));
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

	public static class Lightsaber
	{
		@RegistryName("lightsaber")
		public static final LightsaberItem Lightsaber = new LightsaberItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
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
		@RegistryName("titanium_nugget")
		public static final Item Titanium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("zersium_nugget")
		public static final Item Zersium = new Item(new Item.Settings().group(Galaxies.Tab));
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

	public static class Shovel
	{
		@RegistryName("durasteel_shovel")
		public static final ToolItem Durasteel = new ShovelItem(DurasteelToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_shovel")
		public static final ToolItem Titanium = new ShovelItem(TitaniumToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings().group(Galaxies.Tab));
		@RegistryName("beskar_shovel")
		public static final ToolItem Beskar = new ShovelItem(BeskarToolMaterial.INSTANCE, 1.5F, -3.0F, new Item.Settings().group(Galaxies.Tab));
	}

	public static void register()
	{
		RegistryHelper.registerAnnotatedFields(SwgItems.class, Item.class, (instance, registryName) -> Registry.register(Registry.ITEM, registryName, instance));
	}
}
