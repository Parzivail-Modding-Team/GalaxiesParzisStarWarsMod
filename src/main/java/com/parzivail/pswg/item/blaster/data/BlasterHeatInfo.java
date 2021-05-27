package com.parzivail.pswg.item.blaster.data;

import net.minecraft.nbt.NbtCompound;

public class BlasterHeatInfo
{
	public int capacity;
	public int perRound;
	public int drainSpeed;
	public int overheatPenalty;
	public int overheatDrainSpeed;
	public int passiveCooldownDelay;

	public BlasterHeatInfo(int capacity, int perRound, int drainSpeed, int overheatPenalty, int overheatDrainSpeed, int passiveCooldownDelay)
	{
		this.capacity = capacity;
		this.perRound = perRound;
		this.drainSpeed = drainSpeed;
		this.overheatPenalty = overheatPenalty;
		this.overheatDrainSpeed = overheatDrainSpeed;
		this.passiveCooldownDelay = passiveCooldownDelay;
	}

	public static BlasterHeatInfo fromTag(NbtCompound compoundTag, String s)
	{
		NbtCompound tag = compoundTag.getCompound(s);

		return new BlasterHeatInfo(
				tag.getInt("capacity"),
				tag.getInt("perRound"),
				tag.getInt("drainSpeed"),
				tag.getInt("overheatPenalty"),
				tag.getInt("overheatDrainSpeed"),
				tag.getInt("passiveCooldownDelay")
		);
	}

	public static void toTag(NbtCompound compoundTag, String s, BlasterHeatInfo data)
	{
		NbtCompound tag = new NbtCompound();

		tag.putInt("capacity", data.capacity);
		tag.putInt("perRound", data.perRound);
		tag.putInt("drainSpeed", data.perRound);
		tag.putInt("overheatPenalty", data.overheatPenalty);
		tag.putInt("overheatDrainSpeed", data.overheatPenalty);
		tag.putInt("passiveCooldownDelay", data.passiveCooldownDelay);

		compoundTag.put(s, tag);
	}
}
