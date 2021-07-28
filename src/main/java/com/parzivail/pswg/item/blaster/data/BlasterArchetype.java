package com.parzivail.pswg.item.blaster.data;

import java.util.HashMap;

public enum BlasterArchetype
{
	PISTOL("pistol", (byte)0, true),
	RIFLE("rifle", (byte)1, false),
	SNIPER("sniper", (byte)2, false),
	HEAVY("heavy", (byte)3, false),
	SLUGTHROWER("slug", (byte)4, false),
	ION("ion", (byte)5, true);

	public static final HashMap<String, BlasterArchetype> ID_LOOKUP = new HashMap<>();
	public static final HashMap<Byte, BlasterArchetype> ORDINAL_LOOKUP = new HashMap<>();

	static
	{
		for (var v : values())
			ID_LOOKUP.put(v.id, v);

		for (var v : values())
			ORDINAL_LOOKUP.put(v.ordinal, v);
	}

	private final String id;
	private final byte ordinal;
	private final boolean oneHanded;

	BlasterArchetype(String id, byte ordinal, boolean oneHanded)
	{
		this.id = id;
		this.ordinal = ordinal;
		this.oneHanded = oneHanded;
	}

	public String getId()
	{
		return id;
	}

	public byte getOrdinal()
	{
		return ordinal;
	}

	public boolean isOneHanded()
	{
		return oneHanded;
	}
}
