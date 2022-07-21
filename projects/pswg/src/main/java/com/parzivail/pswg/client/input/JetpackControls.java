package com.parzivail.pswg.client.input;

import java.util.EnumSet;

public enum JetpackControls
{
	NONE(0), FORWARD(0b1), BACKWARD(0b10), LEFT(0b100), RIGHT(0b1000), ASCEND(0b10000), DESCEND(0b100000), MODE(0b1000000);

	private final short flag;

	JetpackControls(int flag)
	{
		this.flag = (short)flag;
	}

	public short getFlag()
	{
		return flag;
	}

	public static EnumSet<JetpackControls> unpack(short controls)
	{
		var allFlags = values();
		var flags = EnumSet.noneOf(JetpackControls.class);

		for (var flag : allFlags)
			if ((controls & flag.getFlag()) != 0)
				flags.add(flag);

		return flags;
	}

	public static short pack(EnumSet<JetpackControls> controls)
	{
		short packed = 0;
		for (var sc : controls)
			packed |= sc.getFlag();
		return packed;
	}
}
