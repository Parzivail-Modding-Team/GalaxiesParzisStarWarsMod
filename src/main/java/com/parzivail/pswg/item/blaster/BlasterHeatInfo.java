package com.parzivail.pswg.item.blaster;

import net.minecraft.nbt.CompoundTag;

public class BlasterHeatInfo
{
	public int capacity;
	public int perRound;

	public BlasterHeatInfo(int capacity, int perRound)
	{
		this.capacity = capacity;
		this.perRound = perRound;
	}

	public static BlasterHeatInfo fromTag(CompoundTag compoundTag, String s)
	{
		CompoundTag tag = compoundTag.getCompound(s);

		return new BlasterHeatInfo(
				tag.getInt("capacity"),
				tag.getInt("perRound")
		);
	}

	public static void toTag(CompoundTag compoundTag, String s, BlasterHeatInfo data)
	{
		CompoundTag tag = new CompoundTag();

		tag.putInt("capacity", data.capacity);
		tag.putInt("perRound", data.perRound);

		compoundTag.put(s, tag);
	}
}
