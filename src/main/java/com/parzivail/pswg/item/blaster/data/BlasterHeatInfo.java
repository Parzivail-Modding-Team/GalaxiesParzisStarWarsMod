package com.parzivail.pswg.item.blaster.data;

import net.minecraft.nbt.CompoundTag;

public class BlasterHeatInfo
{
	public int capacity;
	public int perRound;
	public int overheatPenalty;
	public int passiveCooldownDelay;

	public BlasterHeatInfo(int capacity, int perRound, int overheatPenalty, int passiveCooldownDelay)
	{
		this.capacity = capacity;
		this.perRound = perRound;
		this.overheatPenalty = overheatPenalty;
		this.passiveCooldownDelay = passiveCooldownDelay;
	}

	public static BlasterHeatInfo fromTag(CompoundTag compoundTag, String s)
	{
		CompoundTag tag = compoundTag.getCompound(s);

		return new BlasterHeatInfo(
				tag.getInt("capacity"),
				tag.getInt("perRound"),
				tag.getInt("overheatPenalty"),
				tag.getInt("passiveCooldownDelay")
		);
	}

	public static void toTag(CompoundTag compoundTag, String s, BlasterHeatInfo data)
	{
		CompoundTag tag = new CompoundTag();

		tag.putInt("capacity", data.capacity);
		tag.putInt("perRound", data.perRound);
		tag.putInt("overheatPenalty", data.overheatPenalty);
		tag.putInt("passiveCooldownDelay", data.passiveCooldownDelay);

		compoundTag.put(s, tag);
	}
}
