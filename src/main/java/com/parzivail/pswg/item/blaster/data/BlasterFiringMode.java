package com.parzivail.pswg.item.blaster.data;

import java.util.EnumSet;
import java.util.HashMap;

public enum BlasterFiringMode
{
	SEMI_AUTOMATIC("semi", 0b1),
	BURST("burst", 0b10),
	AUTOMATIC("auto", 0b100),
	STUN("stun", 0b1000),
	SLUGTHROWER("slug", 0b10000),
	ION("ion", 0b100000);

	public static final HashMap<String, BlasterFiringMode> REVERSE_LOOKUP = new HashMap<>();

	static
	{
		for (var v : values())
			REVERSE_LOOKUP.put(v.id, v);
	}

	private final String id;
	private final int flag;

	BlasterFiringMode(String id, int flag)
	{
		this.id = id;
		this.flag = flag;
	}

	public String getId()
	{
		return id;
	}

	public int getFlag()
	{
		return flag;
	}

	public static EnumSet<BlasterFiringMode> unpack(short value)
	{
		var allFlags = values();
		var flags = EnumSet.noneOf(BlasterFiringMode.class);

		for (var flag : allFlags)
			if ((value & flag.getFlag()) != 0)
				flags.add(flag);

		return flags;
	}

	public static short pack(EnumSet<BlasterFiringMode> controls)
	{
		short packed = 0;
		for (var sc : controls)
			packed |= sc.getFlag();
		return packed;
	}
}
