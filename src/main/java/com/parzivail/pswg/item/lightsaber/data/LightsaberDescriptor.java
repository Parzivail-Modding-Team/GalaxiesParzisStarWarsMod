package com.parzivail.pswg.item.lightsaber.data;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class LightsaberDescriptor extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("lightsaber_model");

	public Identifier id;

	public LightsaberDescriptor(NbtCompound tag)
	{
		super(SLUG, tag);
	}

	public LightsaberDescriptor(Identifier id)
	{
		super(SLUG);
		this.id = id;
	}

	/**
	 * Do not call: only used in deserialization
	 */
	@Deprecated
	public LightsaberDescriptor(Identifier id, LightsaberDescriptor other)
	{
		super(SLUG);
		this.id = id;
	}
}
