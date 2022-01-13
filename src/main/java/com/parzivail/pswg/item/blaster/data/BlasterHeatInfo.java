package com.parzivail.pswg.item.blaster.data;

import net.minecraft.nbt.NbtCompound;

public class BlasterHeatInfo
{
	public int capacity;
	public short perRound;
	public short drainSpeed;
	public short overheatPenalty;
	public short overheatDrainSpeed;
	public short passiveCooldownDelay;
	public short overchargeBonus;

	public BlasterHeatInfo(int capacity, short perRound, short drainSpeed, short overheatPenalty, short overheatDrainSpeed, short passiveCooldownDelay, short overchargeBonus)
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
				tag.getShort("perRound"),
				tag.getShort("drainSpeed"),
				tag.getShort("overheatPenalty"),
				tag.getShort("overheatDrainSpeed"),
				tag.getShort("passiveCooldownDelay"),
				tag.getShort("overchargeBonus")
		);
	}

	public static void toTag(NbtCompound compoundTag, String s, BlasterHeatInfo data)
	{
		var tag = new NbtCompound();

		tag.putInt("capacity", data.capacity);
		tag.putShort("perRound", data.perRound);
		tag.putShort("drainSpeed", data.perRound);
		tag.putShort("overheatPenalty", data.overheatPenalty);
		tag.putShort("overheatDrainSpeed", data.overheatPenalty);
		tag.putShort("passiveCooldownDelay", data.passiveCooldownDelay);
		tag.putShort("overchargeBonus", data.overchargeBonus);

		compoundTag.put(s, tag);
	}
}
