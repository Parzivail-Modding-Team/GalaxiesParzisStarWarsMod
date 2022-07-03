package com.parzivail.pswg.item.lightsaber.data;

import net.minecraft.util.Identifier;

public class LightsaberDescriptor
{
	public Identifier id;

	public String owner;
	public String hilt;
	public float bladeHue;
	public float bladeSaturation;
	public float bladeValue;

	public LightsaberDescriptor(Identifier id, String owner, String hilt, float bladeHue, float bladeSaturation, float bladeValue)
	{
		this.id = id;

		this.owner = owner;
		this.hilt = hilt;
		this.bladeHue = bladeHue;
		this.bladeSaturation = bladeSaturation;
		this.bladeValue = bladeValue;
	}
}
