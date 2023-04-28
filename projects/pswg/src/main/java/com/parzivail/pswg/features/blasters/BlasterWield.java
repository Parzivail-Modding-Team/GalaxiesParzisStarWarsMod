package com.parzivail.pswg.features.blasters;

public enum BlasterWield
{
	Invalid(false, false, false),
	None(false, false, false),
	SingleMain(true, false, true),
	SingleOff(false, true, true),
	DoubleMain(true, true, true),
	DoubleOff(true, true, true),
	Dual(true, true, false);

	public final boolean mainHandOccupied;
	public final boolean offHandOccupied;
	public final boolean oneWeapon;

	BlasterWield(boolean mainHandOccupied, boolean offHandOccupied, boolean oneWeapon)
	{
		this.mainHandOccupied = mainHandOccupied;
		this.offHandOccupied = offHandOccupied;
		this.oneWeapon = oneWeapon;
	}

	public boolean areBothHandsOccupied()
	{
		return mainHandOccupied && offHandOccupied;
	}
}
