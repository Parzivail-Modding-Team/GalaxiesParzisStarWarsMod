package com.parzivail.swg.entity.ship;

/**
 * Created by colby on 12/30/2017.
 */
public class ShipData
{
	public float maxThrottle = 2;
	public float acceleration = 0.1f;
	public float distanceMin = 1.5f;
	public float distanceMax = 2.5f;
	public float repulsorliftForce = 1;
	public int numSeats = 1;
	public boolean hasMinDistance;
	public boolean hasMaxDistance;
	public float verticalCenteringOffset = 1.684f;
	public float verticalGroundingOffset = 0.82f;
	public boolean isAirVehicle = true;
}
