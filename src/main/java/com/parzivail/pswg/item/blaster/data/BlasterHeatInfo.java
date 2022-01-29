package com.parzivail.pswg.item.blaster.data;

import net.minecraft.network.PacketByteBuf;

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

	public static BlasterHeatInfo read(PacketByteBuf buf)
	{
		var capacity = buf.readInt();
		var perRound = buf.readShort();
		var drainSpeed = buf.readShort();
		var overheatPenalty = buf.readShort();
		var overheatDrainSpeed = buf.readShort();
		var passiveCooldownDelay = buf.readShort();
		var overchargeBonus = buf.readShort();
		return new BlasterHeatInfo(capacity, perRound, drainSpeed, overheatPenalty, overheatDrainSpeed, passiveCooldownDelay, overchargeBonus);
	}

	public void write(PacketByteBuf buf)
	{
		buf.writeInt(capacity);
		buf.writeShort(perRound);
		buf.writeShort(drainSpeed);
		buf.writeShort(overheatPenalty);
		buf.writeShort(overheatDrainSpeed);
		buf.writeShort(passiveCooldownDelay);
		buf.writeShort(overchargeBonus);
	}
}
