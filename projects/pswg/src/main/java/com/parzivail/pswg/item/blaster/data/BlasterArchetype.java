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

	public static final HashMap<String, BlasterArchetype> VALUE_LOOKUP = new HashMap<>();
	public static final HashMap<Byte, BlasterArchetype> ID_LOOKUP = new HashMap<>();

	static
	{
		for (var v : values())
			VALUE_LOOKUP.put(v.value, v);

		for (var v : values())
			ID_LOOKUP.put(v.id, v);
	}

	private final String value;
	private final byte id;
	private final boolean oneHanded;

	BlasterArchetype(String value, byte id, boolean oneHanded)
	{
		this.value = value;
		this.id = id;
		this.oneHanded = oneHanded;
	}

	public String getValue()
	{
		return value;
	}

	public byte getId()
	{
		return id;
	}

	public boolean isOneHanded()
	{
		return oneHanded;
	}

	public String getLangKey()
	{
		return String.format("blaster.pswg.archetype.%s", value);
	}
}
