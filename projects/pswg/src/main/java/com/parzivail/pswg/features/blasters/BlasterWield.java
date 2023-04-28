package com.parzivail.pswg.features.blasters;

import net.minecraft.util.Hand;

public enum BlasterWield
{
	Invalid(Hand.MAIN_HAND, false, false, false),
	None(Hand.MAIN_HAND, false, false, false),
	SingleMain(Hand.MAIN_HAND, true, false, true),
	SingleOff(Hand.OFF_HAND, false, true, true),
	DoubleMain(Hand.MAIN_HAND, true, true, true),
	DoubleOff(Hand.OFF_HAND, true, true, true),
	Dual(Hand.MAIN_HAND, true, true, false);

	public final Hand baseHand;
	public final boolean mainHandOccupied;
	public final boolean offHandOccupied;
	public final boolean oneWeapon;

	BlasterWield(Hand baseHand, boolean mainHandOccupied, boolean offHandOccupied, boolean oneWeapon)
	{
		this.baseHand = baseHand;
		this.mainHandOccupied = mainHandOccupied;
		this.offHandOccupied = offHandOccupied;
		this.oneWeapon = oneWeapon;
	}

	public boolean areBothHandsOccupied()
	{
		return mainHandOccupied && offHandOccupied;
	}
}
