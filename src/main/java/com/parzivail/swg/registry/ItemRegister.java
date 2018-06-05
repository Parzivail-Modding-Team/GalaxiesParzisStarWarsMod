package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.ItemBlasterSmallGasCanister;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.weapon.ItemBlasterRifle;
import com.parzivail.swg.weapon.blastermodule.BlasterDescriptor;
import com.parzivail.util.ui.GLPalette;
import cpw.mods.fml.common.registry.GameRegistry;

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

	public static void register()
	{
		register(rifleA280 = new ItemBlasterRifle(new BlasterDescriptor("a280", 8, 1, 300, 1300, 6.7f, 25, GLPalette.ANALOG_RED)));

		register(rifleBowcaster = new ItemBlasterRifle(new BlasterDescriptor("bowcaster", 12, 1, 50, 1500, 8f, 6, GLPalette.ANALOG_RED)));

		register(rifleCycler = new ItemBlasterRifle(new BlasterDescriptor("cycler", 8, 1, 300, 900, 6f, 12, GLPalette.ANALOG_RED)));

		register(rifleDefender = new ItemBlasterRifle(new BlasterDescriptor("defender", 4, 1, 30, 350, 1f, 100, GLPalette.ANALOG_RED)));

		register(rifleDh17 = new ItemBlasterRifle(new BlasterDescriptor("dh17", 4, 1, 50, 550, 1f, 25, GLPalette.ANALOG_RED)));

		register(rifleDl18 = new ItemBlasterRifle(new BlasterDescriptor("dl18", 4, 1, 100, 500, 1f, 100, GLPalette.ANALOG_RED)));

		register(rifleDl21 = new ItemBlasterRifle(new BlasterDescriptor("dl21", 4, 1, 100, 500, 1f, 100, GLPalette.ANALOG_RED)));

		register(rifleDlt19 = new ItemBlasterRifle(new BlasterDescriptor("dlt19", 16, 1, 300, 1600, 13f, 30, GLPalette.ANALOG_RED)));

		register(rifleE11 = new ItemBlasterRifle(new BlasterDescriptor("e11", 7, 1, 300, 1000, 4.5f, 50, GLPalette.ANALOG_RED)));

		register(rifleIonization = new ItemBlasterRifle(new BlasterDescriptor("ionization", 4, 1, 12, 80, 3f, 2, GLPalette.ANALOG_RED)));

		register(rifleRt97c = new ItemBlasterRifle(new BlasterDescriptor("rt97c", 11, 1, 1500, 2000, 11f, 50, GLPalette.ANALOG_RED)));

		register(rifleScout = new ItemBlasterRifle(new BlasterDescriptor("scout", 4, 1, 60, 300, 1f, 10, GLPalette.ANALOG_RED)));

		register(rifleSe14c = new ItemBlasterRifle(new BlasterDescriptor("se14c", 4, 1, 100, 500, 1f, 20, GLPalette.ANALOG_RED)));

		register(rifleT21 = new ItemBlasterRifle(new BlasterDescriptor("t21", 8, 1, 200, 2000, 4.5f, 30, GLPalette.ANALOG_RED)));

		register(powerPackSmallGasCanister = new ItemBlasterSmallGasCanister());
	}

	private static void register(PItem item)
	{
		GameRegistry.registerItem(item, item.name, Resources.MODID);
	}
}
