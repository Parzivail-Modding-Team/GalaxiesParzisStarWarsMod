package com.parzivail.swg.ship;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by colby on 12/26/2017.
 */
public class VehicleT65 extends BasicFlightModel
{
	public VehicleT65(World world)
	{
		super(world);
		setPivots(1.684f, 0.82f);
	}

	@Override
	public ItemStack[] getInventory()
	{
		return new ItemStack[0];
	}
}
