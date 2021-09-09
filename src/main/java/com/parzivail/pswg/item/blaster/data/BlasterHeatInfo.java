package com.parzivail.pswg.item.blaster.data;

import net.minecraft.nbt.NbtCompound;

public class BlasterHeatInfo
{
	public int capacity;
	public byte perRound;
	public byte drainSpeed;
	public byte overheatPenalty;
	public byte overheatDrainSpeed;
	public byte passiveCooldownDelay;
	public byte overchargeBonus;

	public BlasterHeatInfo(int capacity, byte perRound, byte drainSpeed, byte overheatPenalty, byte overheatDrainSpeed, byte passiveCooldownDelay, byte overchargeBonus)
	{
		this.capacity = capacity;
		this.perRound = perRound;
		this.drainSpeed = drainSpeed;
		this.overheatPenalty = overheatPenalty;
		this.overheatDrainSpeed = overheatDrainSpeed;
		this.passiveCooldownDelay = passiveCooldownDelay;
		this.overchargeBonus = overchargeBonus;
	}

	public static BlasterHeatInfo fromTag(NbtCompound compoundTag, String s)
	{
		var tag = compoundTag.getCompound(s);

		return new BlasterHeatInfo(
				tag.getInt("capacity"),
				tag.getByte("perRound"),
				tag.getByte("drainSpeed"),
				tag.getByte("overheatPenalty"),
				tag.getByte("overheatDrainSpeed"),
				tag.getByte("passiveCooldownDelay"),
				tag.getByte("overchargeBonus")
		);
	}

	public static void toTag(NbtCompound compoundTag, String s, BlasterHeatInfo data)
	{
		var tag = new NbtCompound();

		tag.putInt("capacity", data.capacity);
		tag.putByte("perRound", data.perRound);
		tag.putByte("drainSpeed", data.perRound);
		tag.putByte("overheatPenalty", data.overheatPenalty);
		tag.putByte("overheatDrainSpeed", data.overheatPenalty);
		tag.putByte("passiveCooldownDelay", data.passiveCooldownDelay);
		tag.putByte("overchargeBonus", data.overchargeBonus);

		compoundTag.put(s, tag);
	}
}
