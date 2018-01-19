package com.parzivail.util.binary.Cdf;

import net.minecraft.nbt.NBTTagCompound;

public class BlockInfo
{
	public final int id;
	public final int metadata;
	public final NBTTagCompound tileData;

	public BlockInfo(int id, int metadata, NBTTagCompound tileData)
	{
		this.id = id;
		this.metadata = metadata;
		this.tileData = tileData;
	}
}
