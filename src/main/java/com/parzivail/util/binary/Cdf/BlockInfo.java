package com.parzivail.util.binary.Cdf;

import net.minecraft.nbt.NBTTagCompound;

public class BlockInfo
{
	public final short id;
	public final byte metadata;
	public final NBTTagCompound tileData;

	public BlockInfo(short id, byte metadata, NBTTagCompound tileData)
	{
		this.id = id;
		this.metadata = metadata;
		this.tileData = tileData;
	}
}