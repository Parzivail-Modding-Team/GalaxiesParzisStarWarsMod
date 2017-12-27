package com.parzivail.swg.ship;

import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/26/2017.
 */
public class VehicleT65 extends BasicFlightModel
{
	public VehicleT65(World world)
	{
		super(world);
	}

	@Override
	void createSeats()
	{
		seats = new Seat[1];
		seats[0] = new Seat(this, new Vector3f(0, 1, 0));
	}
}
