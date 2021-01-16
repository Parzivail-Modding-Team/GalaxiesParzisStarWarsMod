package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.container.registry.RegistryHelper;
import com.parzivail.pswg.container.registry.RegistryName;
import com.parzivail.pswg.item.DebugItem;
import com.parzivail.pswg.item.LiquidFoodItem;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class SwgItems
{
	public static class Blaster
	{
		@RegistryName("blaster")
		public static final BlasterItem Blaster = new BlasterItem(new Item.Settings().maxCount(1));
	}

	public static class Debug
	{
		@RegistryName("debug")
		public static final DebugItem Debug = new DebugItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
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
		@RegistryName("durasteel_ingot")
		public static final Item Durasteel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("plasteel_ingot")
		public static final Item Plasteel = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("titanium_ingot")
		public static final Item Titanium = new Item(new Item.Settings().group(Galaxies.Tab));
		@RegistryName("transparisteel_ingot")
		public static final Item Transparisteel = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Lightsaber
	{
		@RegistryName("lightsaber")
		public static final LightsaberItem Lightsaber = new LightsaberItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
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

	public static void register()
	{
		RegistryHelper.registerAnnotatedFields(SwgItems.class, Item.class, (instance, registryName) -> Registry.register(Registry.ITEM, registryName, instance));
	}
}
