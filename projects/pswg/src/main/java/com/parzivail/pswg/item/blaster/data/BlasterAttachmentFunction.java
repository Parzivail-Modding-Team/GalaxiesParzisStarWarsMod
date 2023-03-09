package com.parzivail.pswg.item.blaster.data;

public enum BlasterAttachmentFunction
{
	NONE("none"),
	DEFAULT_SCOPE("scope"),
	SNIPER_SCOPE("sniper_scope"),
	IMPROVE_COOLING("improve_cooling"),
	REDUCE_RECOIL("reduce_recoil"),
	REDUCE_SPREAD("reduce_spread"),
	INCREASE_RATE("increase_fire_rate"),
	ALLOW_AUTO("allow_auto_fire"),
	ALLOW_BURST("allow_burst_fire"),
	ION_TO_GAS_CONVERSION("ion_to_gas_conversion"),
	ION_TO_REPULSOR_CONVERSION("ion_to_repulsor_conversion"),
	INCREASE_DAMAGE("increase_damage"),
	INCREASE_RANGE("increase_range"),
	WATERPROOF_BOLTS("waterproof_bolts"),
	WATERPROOF_FIRING("waterproof_firing"),
	INCREASE_DAMAGE_RANGE("increase_damage_range"),
	;

	private final String value;

	BlasterAttachmentFunction(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
