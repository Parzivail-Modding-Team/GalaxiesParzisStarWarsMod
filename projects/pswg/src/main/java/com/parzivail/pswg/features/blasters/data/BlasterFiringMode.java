package com.parzivail.pswg.features.blasters.data;

import com.parzivail.pswg.Resources;

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

	BlasterFiringMode(String value, byte id)
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

	public String getTranslation()
	{
		return Resources.info("blaster_firing_mode_" + value);
	}
}
