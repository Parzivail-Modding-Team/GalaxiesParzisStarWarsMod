package com.parzivail.swg.register;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.item.ItemBlaster;
import com.parzivail.swg.item.ItemLightsaber;
import com.parzivail.swg.item.SwgItem;
import com.parzivail.swg.item.SwgItemFood;
import com.parzivail.swg.item.data.BlasterDescriptor;
import com.parzivail.util.item.ModularItems;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemRegister
{
	public static ItemLightsaber lightsaber;

	private static SwgItemFood joganFruit;
	private static SwgItemFood chasuka;
	private static SwgItemFood meiloorun;
	private static SwgItemFood mynockWing;
	private static SwgItemFood friedMynockWing;
	private static SwgItemFood banthaChop;
	private static SwgItemFood banthaSteak;
	private static SwgItemFood nerfChop;
	private static SwgItemFood nerfSteak;
	private static SwgItemFood gizkaChop;
	private static SwgItemFood gizkaSteak;
	private static SwgItemFood flangthTakeout;
	private static SwgItemFood flangthPlate;

	private static SwgItemFood redDeathStick;
	private static SwgItemFood yellowDeathStick;

	private static SwgItemFood blueMilk;
	private static SwgItemFood bluePuffCube;
	private static SwgItemFood blueYogurt;

	private static SwgItemFood qrikkiBread;
	private static SwgItemFood qrikkiWaffle;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(lightsaber = new ItemLightsaber());

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
