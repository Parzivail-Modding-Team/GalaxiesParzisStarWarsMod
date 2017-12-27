package com.parzivail.swg.ship;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/27/2017.
 */
public class Seat extends EntityBase
{
	private final BasicFlightModel ship;
	private final Vector3f offset;

	public Seat(BasicFlightModel ship, Vector3f offset)
	{
		super(ship.worldObj);
		this.ship = ship;
		this.offset = offset;
	}

	@Override
	public void onUpdate()
	{
		if (this.posY < -64.0D)
			this.setDead();

		this.moveEntity(ship.motionX, ship.motionY, ship.motionZ);

		// if we're over 3 blocks away from where we should be, snap back
		Vector3f nextPosition = ship.orientation.findLocalVectorGlobally(offset);
		if (Math.pow(nextPosition.x - posX, 2) + Math.pow(nextPosition.x - posX, 2) + Math.pow(nextPosition.x - posX, 2) > 9)
		{
			this.posX = nextPosition.x;
			this.posY = nextPosition.y;
			this.posZ = nextPosition.z;
		}

		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				this.riddenByEntity = null;
		}
	}

	void setLocationAndAngles(BasicFlightModel ship)
	{
		this.setLocationAndAngles(ship.posX + offset.x, ship.posY + offset.y, ship.posZ + offset.z, 0, 0);
	}
}
