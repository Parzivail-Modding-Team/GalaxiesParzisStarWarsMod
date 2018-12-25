package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.*;
import com.parzivail.swg.item.binocular.ItemBinoculars;
import com.parzivail.swg.item.binocular.data.BinocularDescriptor;
import com.parzivail.swg.item.blaster.ItemBlasterRifle;
import com.parzivail.swg.item.blaster.data.BlasterDescriptor;
import com.parzivail.swg.item.grenade.ItemSmokeGrenade;
import com.parzivail.swg.item.grenade.ItemThermalDetonator;
import com.parzivail.swg.item.lightsaber.ItemLightsaber;
import com.parzivail.util.ui.GLPalette;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.potion.Potion;

/**
 * Created by colby on 12/26/2017.
 */
public class ItemRegister
{
	public static PItem rifleA280;
	public static PItem rifleBowcaster;
	public static PItem rifleCycler;
	public static PItem rifleDefender;
	public static PItem rifleDh17;
	public static PItem rifleDl18;
	public static PItem rifleDl21;
	public static PItem rifleDlt19;
	public static PItem rifleE11;
	public static PItem rifleIonization;
	public static PItem rifleRt97c;
	public static PItem rifleScout;
	public static PItem rifleSe14c;
	public static PItem rifleT21;

	public static PItem axeGamL;
	public static PItem axeGamM;
	public static PItem axeGamS;
	public static PItem meleeGaffi;
	public static PItem meleeSpear;
	public static PItem meleeVibro;

	public static PItem powerPackSmallGasCanister;
	public static PItem grenadeThermalDetonator;
	public static PItem grenadeSmoke;

	public static PItem saltPile;

	public static PItemFood joganFruit;
	public static PItemFood chasuka;
	public static PItemFood meiloorun;
	public static PItemFood mynockWing;
	public static PItemFood friedMynockWing;
	public static PItemFood banthaChop;
	public static PItemFood banthaSteak;
	public static PItemFood nerfChop;
	public static PItemFood nerfSteak;
	public static PItemFood gizkaChop;
	public static PItemFood gizkaSteak;
	public static PItemFood flangthPlate;
	public static PItemFood flangthTakeout;

	public static PItemFood blueMilk;
	public static PItemFood bluePuffCube;
	public static PItemFood blueYogurt;

	public static PItemFood qrikkiBread;
	public static PItemFood qrikkiWaffle;

	public static PItemFood redDeathStick;
	public static PItemFood yellowDeathStick;

	public static PItem personalDatapad;
	public static PItem binocularsMb450;

	public static PItem debugWizard;

	public static PItem lightsaber;

	public static PItem chromiumIngot;

	public static void register()
	{
		register(rifleA280 = new ItemBlasterRifle(new BlasterDescriptor("a280", 8, 1, 300, 1300, 6.7f, 25, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleBowcaster = new ItemBlasterRifle(new BlasterDescriptor("bowcaster", 12, 1, 50, 1500, 8f, 6, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleCycler = new ItemBlasterRifle(new BlasterDescriptor("cycler", 8, 1, 300, 900, 6f, 12, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleDefender = new ItemBlasterRifle(new BlasterDescriptor("defender", 4, 1, 30, 350, 1f, 100, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleDh17 = new ItemBlasterRifle(new BlasterDescriptor("dh17", 4, 1, 50, 550, 1f, 25, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleDl18 = new ItemBlasterRifle(new BlasterDescriptor("dl18", 4, 1, 100, 500, 1f, 100, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleDl21 = new ItemBlasterRifle(new BlasterDescriptor("dl21", 4, 1, 100, 500, 1f, 100, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleDlt19 = new ItemBlasterRifle(new BlasterDescriptor("dlt19", 16, 1, 300, 1600, 13f, 30, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleE11 = new ItemBlasterRifle(new BlasterDescriptor("e11", 7, 1, 300, 1000, 4.5f, 50, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleIonization = new ItemBlasterRifle(new BlasterDescriptor("ionization", 4, 1, 12, 80, 3f, 2, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleRt97c = new ItemBlasterRifle(new BlasterDescriptor("rt97c", 11, 1, 1500, 2000, 11f, 50, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleScout = new ItemBlasterRifle(new BlasterDescriptor("scout", 4, 1, 60, 300, 1f, 10, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleSe14c = new ItemBlasterRifle(new BlasterDescriptor("se14c", 4, 1, 100, 500, 1f, 20, GLPalette.ANALOG_RED, 10, 30, 4)));
		register(rifleT21 = new ItemBlasterRifle(new BlasterDescriptor("t21", 8, 1, 200, 2000, 4.5f, 30, GLPalette.ANALOG_RED, 10, 30, 4)));

		register(powerPackSmallGasCanister = new ItemBlasterSmallGasCanister());
		register(grenadeThermalDetonator = new ItemThermalDetonator());
		register(grenadeSmoke = new ItemSmokeGrenade());

		register(saltPile = new PItem("saltPile"));

		register(joganFruit = new PItemFood("joganFruit", 4, 0.3F));
		register(chasuka = new PItemFood("chasuka", 4, 0.3F));
		register(meiloorun = new PItemFood("meiloorun", 4, 0.3F));
		register(mynockWing = new PItemFood("mynockWing", 3, 0.3F));
		register(friedMynockWing = new PItemFood("friedMynockWing", 8, 0.8F));
		register(banthaChop = new PItemFood("banthaChop", 3, 0.3F));
		register(banthaSteak = new PItemFood("banthaSteak", 8, 0.8F));
		register(nerfChop = new PItemFood("nerfChop", 3, 0.3F));
		register(nerfSteak = new PItemFood("nerfSteak", 8, 0.8F));
		register(gizkaChop = new PItemFood("gizkaChop", 3, 0.3F));
		register(gizkaSteak = new PItemFood("gizkaSteak", 8, 0.8F));
		register(flangthTakeout = new PItemFood("flangthTakeout", 3, 0.3F));
		register(flangthPlate = new PItemFood("flangthPlate", 8, 0.8F));

		register(redDeathStick = (PItemFood)new PItemFood("redDeathStick", 3, 0.3F).setPotionEffect(Potion.heal.id, 10, 0, 0.8F));
		register(yellowDeathStick = (PItemFood)new PItemFood("yellowDeathStick", 3, 0.3F).setPotionEffect(Potion.moveSpeed.id, 10, 0, 0.8F));

		register(blueMilk = new PItemFood("blueMilk", 4, 0.3F));
		register(bluePuffCube = new PItemFood("bluePuffCube", 1, 0.3F));
		register(blueYogurt = new PItemFood("blueYogurt", 4, 0.3F));

		register(qrikkiBread = new PItemFood("qrikkiBread", 5, 0.6F));
		register(qrikkiWaffle = new PItemFood("qrikkiWaffle", 8, 0.7F));

		register(personalDatapad = new ItemPersonalDatapad());
		register(binocularsMb450 = new ItemBinoculars(new BinocularDescriptor("mb450", 0.1f)));

		register(debugWizard = new ItemDebugWizard());

		register(lightsaber = new ItemLightsaber());

		register(chromiumIngot = new PItem("chromiumIngot"));
	}

	private static void register(PItem item)
	{
		GameRegistry.registerItem(item, item.name, Resources.MODID);
	}

	private static void register(PItemFood item)
	{
		GameRegistry.registerItem(item, item.name, Resources.MODID);
	}
}
