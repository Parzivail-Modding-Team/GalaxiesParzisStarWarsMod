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
	protected void createSeats()
	{
		seats = new Seat[2];
		/*

		this.ship = ship;
		this.offset = offset;

		BasicFlightModel ship, Vector3f offset
		 */
		seats[0] = new Seat(this.worldObj);
		seats[0].attachToShip(this, new Vector3f(0, 0, -1), 0);

		seats[1] = new Seat(this.worldObj);
		seats[1].attachToShip(this, new Vector3f(0, 0, 1), 1);
	}
}
