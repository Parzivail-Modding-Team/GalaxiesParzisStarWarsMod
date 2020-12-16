package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.item.BlasterItem;
import com.parzivail.pswg.item.DebugItem;
import com.parzivail.pswg.item.LightsaberItem;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

public class SwgItems
{
	public static class Blaster
	{
		public static final BlasterItem A280 = new BlasterItem(new BlasterItem.Settings().maxCount(1).damage(8).sound(SwgSounds.Blaster.FIRE_A280).group(Galaxies.Tab));
		public static final BlasterItem DH17 = new BlasterItem(new BlasterItem.Settings().maxCount(1).damage(8).sound(SwgSounds.Blaster.FIRE_DH17).group(Galaxies.Tab));
		public static final BlasterItem E11 = new BlasterItem(new BlasterItem.Settings().maxCount(1).damage(8).sound(SwgSounds.Blaster.FIRE_E11).group(Galaxies.Tab));
		public static final BlasterItem EE3 = new BlasterItem(new BlasterItem.Settings().maxCount(1).damage(8).sound(SwgSounds.Blaster.FIRE_DH17).group(Galaxies.Tab));
	}

	public static class Debug
	{
		public static final DebugItem Debug = new DebugItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
	}

	public static class Ingot
	{
		public static final Item BeskarIngot = new Item(new Item.Settings().group(Galaxies.Tab));
		public static final Item ChromiumIngot = new Item(new Item.Settings().group(Galaxies.Tab));
		public static final Item DurasteelIngot = new Item(new Item.Settings().group(Galaxies.Tab));
		public static final Item PlasteelIngot = new Item(new Item.Settings().group(Galaxies.Tab));
	}

	public static class Lightsaber
	{
		public static final LightsaberItem Lightsaber = new LightsaberItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
	}

	public static class Food
	{
		public static final Item SaltPile = new Item(new Item.Settings().group(Galaxies.Tab));

		public static final Item JoganFruit = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		public static final Item Chasuka = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		public static final Item Meiloorun = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		public static final Item MynockWing = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		public static final Item FriedMynockWing = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));
		public static final Item BanthaChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		public static final Item BanthaSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));
		public static final Item NerfChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		public static final Item NerfSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));
		public static final Item GizkaChop = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		public static final Item GizkaSteak = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));

		// TODO: are these meat?
		public static final Item FlangthTakeout = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build()).group(Galaxies.Tab));
		public static final Item FlangthPlate = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build()).group(Galaxies.Tab));

		// TODO: negative effects?
		public static final Item DeathStickRed = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 200), 0.8F).build()).group(Galaxies.Tab));
		public static final Item DeathStickYellow = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200), 0.8F).build()).group(Galaxies.Tab));

		// TODO: consider turning this into a Fluid
		public static final Item BlueMilk = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		public static final Item BluePuffCube = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(1).saturationModifier(0.3F).build()).group(Galaxies.Tab));
		public static final Item BlueYogurt = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build()).group(Galaxies.Tab));

		public static final Item QrikkiBread = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).build()).group(Galaxies.Tab));
		public static final Item QrikkiWaffle = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.7F).build()).group(Galaxies.Tab));
	}
}
