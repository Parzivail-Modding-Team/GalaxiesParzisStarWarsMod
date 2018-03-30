package com.parzivail.swg.ship;

import net.minecraft.item.ItemStack;
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
		setPivots(1.684f, 0.82f);
	}

	@Override
	public ItemStack[] getInventory()
	{
		return new ItemStack[0];
	}

	@Override
	protected void createSeats()
	{
		seats = new Seat[2];
		seatOffsets = new Vector3f[2];
		/*

		this.ship = ship;
		this.offset = offset;

		BasicFlightModel ship, Vector3f offset
		 */
		seats[0] = new Seat(this.worldObj);
		seats[0].attachToShip(this, 0);
		seatOffsets[0] = new Vector3f(0, 0, -1);

		seats[1] = new Seat(this.worldObj);
		seats[1].attachToShip(this, 1);
		seatOffsets[1] = new Vector3f(0, 0, 1);
	}
}
