package com.parzivail.pswg.item.lightsaber.data;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class LightsaberDescriptor extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("lightsaber_model");

	public Identifier id;

	public String owner;
	public String hilt;
	public float bladeHue;

	public LightsaberDescriptor(NbtCompound tag)
	{
		super(SLUG, tag);
	}

	public LightsaberDescriptor(Identifier id, String owner, String hilt, float bladeHue)
	{
		super(SLUG);
		this.id = id;

		this.owner = owner;
		this.hilt = hilt;
		this.bladeHue = bladeHue;
	}

	/**
	 * Do not call: only used in deserialization
	 */
	@Deprecated
	public LightsaberDescriptor(Identifier id, LightsaberDescriptor other)
	{
		super(SLUG);
		this.id = id;

		this.owner = other.owner;
		this.hilt = other.hilt;
		this.bladeHue = other.bladeHue;
	}
}
