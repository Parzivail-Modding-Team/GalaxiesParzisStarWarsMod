package com.parzivail.pswg.item.blaster.data;

import java.util.HashMap;

public enum BlasterAttachmentFunction
{
	NONE("none", (byte)0),
	DEFAULT_SCOPE("scope", (byte)1),
	SNIPER_SCOPE("sniper_scope", (byte)2);

	public static final HashMap<String, BlasterAttachmentFunction> VALUE_LOOKUP = new HashMap<>();
	public static final HashMap<Byte, BlasterAttachmentFunction> ID_LOOKUP = new HashMap<>();

	static
	{
		for (var v : values())
			VALUE_LOOKUP.put(v.value, v);
		for (var v : values())
			ID_LOOKUP.put(v.id, v);
	}

	private final String value;
	private final byte id;

	BlasterAttachmentFunction(String value, byte id)
	{
		this.value = value;
		this.id = id;
	}

	public String getValue()
	{
		return value;
	}

	public byte getId()
	{
		return id;
	}
}
