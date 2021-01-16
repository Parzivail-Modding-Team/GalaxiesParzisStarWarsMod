package com.parzivail.pswg.item.blaster;

import net.minecraft.nbt.CompoundTag;

public class BlasterSpreadInfo
{
	public float horizontal;
	public float vertical;

	public BlasterSpreadInfo(float horizontal, float vertical)
	{
		this.horizontal = horizontal;
		this.vertical = vertical;
	}

	public static BlasterSpreadInfo fromTag(CompoundTag compoundTag, String s)
	{
		CompoundTag tag = compoundTag.getCompound(s);

		return new BlasterSpreadInfo(
				tag.getFloat("horizontal"),
				tag.getFloat("vertical")
		);
	}

	public static void toTag(CompoundTag compoundTag, String s, BlasterSpreadInfo data)
	{
		CompoundTag tag = new CompoundTag();

		tag.putFloat("horizontal", data.horizontal);
		tag.putFloat("vertical", data.vertical);

		compoundTag.put(s, tag);
	}
}
