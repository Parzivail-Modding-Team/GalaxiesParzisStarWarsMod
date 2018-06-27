package com.parzivail.scarif;

import net.minecraft.nbt.NBTTagCompound;

public class ScarifBlock
{
	public final short id;
	public final byte metadata;
	public final NBTTagCompound tileData;
	public final short pos;

	public ScarifBlock(short id, byte metadata, NBTTagCompound tileData, short pos)
	{
		this.id = id;
		this.metadata = metadata;
		this.tileData = tileData;
		this.pos = pos;
	}
}
