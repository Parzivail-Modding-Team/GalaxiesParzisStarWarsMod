package com.parzivail.pswg.client.input;

import java.util.EnumSet;

public enum ShipControls
{
	NONE(0), THROTTLE_UP(0b1), THROTTLE_DOWN(0b10), BLASTER(0b100), SPECIAL1(0b1000), SPECIAL2(0b10000);

	private final short flag;

	ShipControls(int flag)
	{
		this.flag = (short)flag;
	}

	public short getFlag()
	{
		return flag;
	}

	public static EnumSet<ShipControls> unpack(short controls)
	{
		ShipControls[] allFlags = ShipControls.values();
		EnumSet<ShipControls> flags = EnumSet.noneOf(ShipControls.class);

		for (ShipControls flag : allFlags)
			if ((controls & flag.getFlag()) != 0)
				flags.add(flag);

		return flags;
	}

	public static short pack(EnumSet<ShipControls> controls)
	{
		short packed = 0;
		for (ShipControls sc : controls)
			packed |= sc.getFlag();
		return packed;
	}
}
