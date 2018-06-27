package com.parzivail.scarif;

import net.minecraft.nbt.NBTTagCompound;

public class ScarifBlock
{
	public final short pos;
	public final short id;
	public final byte metadata;
	public final NBTTagCompound tileData;

	public ScarifBlock(short pos, short id, byte metadata, NBTTagCompound tileData)
	{
		this.pos = pos;
		this.id = id;
		this.metadata = metadata;
		this.tileData = tileData;
	}
}
