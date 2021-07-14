package com.parzivail.pswg.item.blaster.data;

import net.minecraft.nbt.NbtCompound;

public class BlasterSpreadInfo
{
	public float horizontal;
	public float vertical;

	public BlasterSpreadInfo(float horizontal, float vertical)
	{
		this.horizontal = horizontal;
		this.vertical = vertical;
	}

	public static BlasterSpreadInfo fromTag(NbtCompound compoundTag, String s)
	{
		var tag = compoundTag.getCompound(s);

		return new BlasterSpreadInfo(
				tag.getFloat("horizontal"),
				tag.getFloat("vertical")
		);
	}

	public static void toTag(NbtCompound compoundTag, String s, BlasterSpreadInfo data)
	{
		var tag = new NbtCompound();

		tag.putFloat("horizontal", data.horizontal);
		tag.putFloat("vertical", data.vertical);

		compoundTag.put(s, tag);
	}
}
