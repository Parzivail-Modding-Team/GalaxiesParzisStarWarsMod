package com.parzivail.pswg.item.blaster.data;

import java.util.HashMap;

public enum BlasterAttachmentFunction
{
	NONE("none", (byte)0),
	DEFAULT_SCOPE("scope", (byte)1),
	SNIPER_SCOPE("sniper_scope", (byte)2),
	IMPROVE_COOLING("improve_cooling", (byte)3),
	REDUCE_RECOIL("reduce_recoil", (byte)4),
	REDUCE_SPREAD("reduce_spread", (byte)5),
	INCREASE_RATE("increase_fire_rate", (byte)6),
	ALLOW_AUTO("allow_auto_fire", (byte)7),
	ALLOW_BURST("allow_burst_fire", (byte)8),
	ION_TO_GAS_CONVERSION("ion_to_gas_conversion", (byte)9),
	ION_TO_REPULSOR_CONVERSION("ion_to_repulsor_conversion", (byte)10),
	INCREASE_DAMAGE("increase_damage", (byte)11),
	INCREASE_RANGE("increase_range", (byte)12);

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
