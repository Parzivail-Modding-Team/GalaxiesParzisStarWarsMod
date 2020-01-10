package com.parzivail.swg.register;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.item.ItemBlaster;
import com.parzivail.swg.item.ItemLightsaber;
import com.parzivail.swg.item.SwgItem;
import com.parzivail.swg.item.SwgItemFood;
import com.parzivail.swg.item.data.BlasterDescriptor;
import com.parzivail.swg.item.data.LightsaberDescriptor;
import com.parzivail.util.item.ModularItems;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

public class ItemRegister
{
	public static ArrayList<ItemLightsaber> lightsabers = new ArrayList<>();

	public static SwgItemFood joganFruit;
	public static SwgItemFood chasuka;
	public static SwgItemFood meiloorun;
	public static SwgItemFood mynockWing;
	public static SwgItemFood friedMynockWing;
	public static SwgItemFood banthaChop;
	public static SwgItemFood banthaSteak;
	public static SwgItemFood nerfChop;
	public static SwgItemFood nerfSteak;
	public static SwgItemFood gizkaChop;
	public static SwgItemFood gizkaSteak;
	public static SwgItemFood flangthTakeout;
	public static SwgItemFood flangthPlate;

	public static SwgItemFood redDeathStick;
	public static SwgItemFood yellowDeathStick;

	public static SwgItemFood blueMilk;
	public static SwgItemFood bluePuffCube;
	public static SwgItemFood blueYogurt;

	public static SwgItemFood qrikkiBread;
	public static SwgItemFood qrikkiWaffle;

	public static SwgItem gasCanisterSmall;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		for (LightsaberDescriptor d : ModularItems.getLightsaberDescriptors())
		{
			ItemLightsaber saber = new ItemLightsaber(d);
			r.register(saber);
			lightsabers.add(saber);
		}

		ItemLightsaber customSaber = new ItemLightsaber(LightsaberDescriptor.DEFAULT, "custom");
		r.register(customSaber);
		lightsabers.add(customSaber);

		for (BlasterDescriptor d : ModularItems.getBlasterDescriptors())
			r.register(new ItemBlaster(d));

		registerQuick(r, joganFruit = new SwgItemFood("jogan_fruit", 4, 0.3F));
		registerQuick(r, chasuka = new SwgItemFood("chasuka", 4, 0.3F));
		registerQuick(r, meiloorun = new SwgItemFood("meiloorun", 4, 0.3F));
		registerQuick(r, mynockWing = new SwgItemFood("mynock_wing", 3, 0.3F));
		registerQuick(r, friedMynockWing = new SwgItemFood("fried_mynock_wing", 8, 0.8F));
		registerQuick(r, banthaChop = new SwgItemFood("bantha_chop", 3, 0.3F));
		registerQuick(r, banthaSteak = new SwgItemFood("bantha_steak", 8, 0.8F));
		registerQuick(r, nerfChop = new SwgItemFood("nerf_chop", 3, 0.3F));
		registerQuick(r, nerfSteak = new SwgItemFood("nerf_steak", 8, 0.8F));
		registerQuick(r, gizkaChop = new SwgItemFood("gizka_chop", 3, 0.3F));
		registerQuick(r, gizkaSteak = new SwgItemFood("gizka_steak", 8, 0.8F));
		registerQuick(r, flangthTakeout = new SwgItemFood("flangth_takeout", 3, 0.3F));
		registerQuick(r, flangthPlate = new SwgItemFood("flangth_plate", 8, 0.8F));

		registerQuick(r, redDeathStick = (SwgItemFood)new SwgItemFood("death_stick_red", 3, 0.3F).setPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0), 0.8F));
		registerQuick(r, yellowDeathStick = (SwgItemFood)new SwgItemFood("death_stick_yellow", 3, 0.3F).setPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 0), 0.8F));

		registerQuick(r, blueMilk = new SwgItemFood("blue_milk", 4, 0.3F));
		registerQuick(r, bluePuffCube = new SwgItemFood("blue_puff_cube", 1, 0.3F));
		registerQuick(r, blueYogurt = new SwgItemFood("blue_yogurt", 4, 0.3F));

		registerQuick(r, qrikkiBread = new SwgItemFood("qrikki_bread", 5, 0.6F));
		registerQuick(r, qrikkiWaffle = new SwgItemFood("qrikki_waffle", 8, 0.7F));

		registerQuick(r, gasCanisterSmall = new SwgItem("gas_canister_small"));

		StarWarsGalaxy.proxy.onRegisterItem(event);
	}

	private static void registerQuick(IForgeRegistry<Item> r, SwgItem item)
	{
		r.register(item);
		StarWarsGalaxy.proxy.registerItemRenderer(item, item.name);
	}

	private static void registerQuick(IForgeRegistry<Item> r, SwgItemFood item)
	{
		r.register(item);
		StarWarsGalaxy.proxy.registerItemRenderer(item, item.name);
	}
}
