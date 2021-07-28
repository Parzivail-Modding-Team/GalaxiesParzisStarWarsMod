package com.parzivail.pswg.item.blaster.data;

import java.util.EnumSet;
import java.util.HashMap;

public enum BlasterFiringMode
{
	SEMI_AUTOMATIC("semi", (byte)0),
	BURST("burst", (byte)1),
	AUTOMATIC("auto", (byte)2),
	STUN("stun", (byte)3),
	SLUGTHROWER("slug", (byte)4),
	ION("ion", (byte)5);

	public static final HashMap<String, BlasterFiringMode> VALUE_LOOKUP = new HashMap<>();
	public static final HashMap<Byte, BlasterFiringMode> ID_LOOKUP = new HashMap<>();

	static
	{
		for (var v : values())
			VALUE_LOOKUP.put(v.value, v);
		for (var v : values())
			ID_LOOKUP.put(v.id, v);
	}

	private final String value;
	private final byte id;
	private final int flag;

	BlasterFiringMode(String value, byte id)
	{
		this.value = value;
		this.id = id;
		this.flag = 1 << id;
	}

	public String getValue()
	{
		return value;
	}

	public int getFlag()
	{
		return flag;
	}

	public byte getId()
	{
		return id;
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
