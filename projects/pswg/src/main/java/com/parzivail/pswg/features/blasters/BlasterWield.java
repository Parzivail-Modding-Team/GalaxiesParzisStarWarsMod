package com.parzivail.pswg.features.blasters;

import net.minecraft.util.Hand;

public enum BlasterWield
{
	Invalid(null, false, false, false),
	None(null, false, false, false),
	SingleMain(Hand.MAIN_HAND, true, false, true),
	SingleOff(Hand.OFF_HAND, false, true, true),
	DoubleMain(Hand.MAIN_HAND, true, true, true),
	DoubleOff(Hand.OFF_HAND, true, true, true),
	Dual(Hand.MAIN_HAND, true, true, false);

	private final Hand baseHand;

	public final boolean hasBlaster;
	public final boolean mainHandOccupied;
	public final boolean offHandOccupied;
	public final boolean oneWeapon;

	BlasterWield(Hand baseHand, boolean mainHandOccupied, boolean offHandOccupied, boolean oneWeapon)
	{
		this.hasBlaster = baseHand != null;
		this.baseHand = baseHand;
		this.mainHandOccupied = mainHandOccupied;
		this.offHandOccupied = offHandOccupied;
		this.oneWeapon = oneWeapon;
	}

	public boolean isBaseHand(Hand hand)
	{
		if (hand == null)
			return false;
		return this.baseHand == hand;
	}

	public boolean areBothHandsOccupied()
	{
		return mainHandOccupied && offHandOccupied;
	}
}
