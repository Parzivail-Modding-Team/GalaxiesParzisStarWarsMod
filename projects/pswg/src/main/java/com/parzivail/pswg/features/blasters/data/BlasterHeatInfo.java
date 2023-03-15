package com.parzivail.pswg.features.blasters.data;

public class BlasterHeatInfo
{
	public int capacity;
	public int perRound;
	public int drainSpeed;
	public int overheatPenalty;
	public int overheatDrainSpeed;
	public int passiveCooldownDelay;
	public int overchargeBonus;

	public BlasterHeatInfo(int capacity, int perRound, int drainSpeed, int overheatPenalty, int overheatDrainSpeed, int passiveCooldownDelay, int overchargeBonus)
	{
		this.capacity = capacity;
		this.perRound = perRound;
		this.drainSpeed = drainSpeed;
		this.overheatPenalty = overheatPenalty;
		this.overheatDrainSpeed = overheatDrainSpeed;
		this.passiveCooldownDelay = passiveCooldownDelay;
		this.overchargeBonus = overchargeBonus;
	}
}
