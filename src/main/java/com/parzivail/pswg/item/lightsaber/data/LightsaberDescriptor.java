package com.parzivail.pswg.item.lightsaber.data;

import net.minecraft.util.Identifier;

public class LightsaberDescriptor
{
	public Identifier id;

	public String owner;
	public String hilt;
	public float bladeHue;

	public LightsaberDescriptor(Identifier id, String owner, String hilt, float bladeHue)
	{
		this.id = id;

		this.owner = owner;
		this.hilt = hilt;
		this.bladeHue = bladeHue;
	}
}
