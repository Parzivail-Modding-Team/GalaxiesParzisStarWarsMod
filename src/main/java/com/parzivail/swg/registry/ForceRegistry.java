package com.parzivail.swg.registry;

import com.parzivail.swg.force.ForcePowerJump;
import com.parzivail.swg.force.ForcePowerLightning;
import com.parzivail.swg.force.IForcePower;

import java.util.ArrayList;

public class ForceRegistry
{
	public static ArrayList<IForcePower> forcePowers;
	public static IForcePower fpJump;
	public static IForcePower fpLightning;

	public static void register()
	{
		forcePowers = new ArrayList<>();

		forcePowers.add(fpJump = new ForcePowerJump());
		forcePowers.add(fpLightning = new ForcePowerLightning());
	}
}
